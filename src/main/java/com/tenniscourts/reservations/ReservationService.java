package com.tenniscourts.reservations;

import com.tenniscourts.exceptions.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@AllArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;

    private final ReservationMapper reservationMapper;

    public ReservationDTO bookReservation(CreateReservationRequestDTO createReservationRequestDTO) {
        throw new UnsupportedOperationException();
    }

    @SneakyThrows
    public ReservationDTO findReservation(Long reservationId)  {
        return reservationRepository.findById(reservationId).map(reservationMapper::map).orElseThrow(() -> {
            throw new EntityNotFoundException("Reservation not found.");
        });
    }

    public ReservationDTO cancelReservation(Long reservationId) {
        return reservationMapper.map(this.cancel(reservationId));
    }

    @SneakyThrows
    private Reservation cancel(Long reservationId){
        return reservationRepository.findById(reservationId).map(reservation -> {

            this.validateCancellation(reservation);

            BigDecimal refundValue = getRefundValue(reservation);
            return this.updateReservation(reservation, refundValue, ReservationStatus.CANCELLED);

        }).orElseThrow(() -> {
            throw new EntityNotFoundException("Reservation not found.");
        });
    }

    private Reservation updateReservation(Reservation reservation, BigDecimal refundValue, ReservationStatus status) {
        reservation.setReservationStatus(status);
        reservation.setValue(reservation.getValue().subtract(refundValue));
        reservation.setRefundValue(refundValue);

        return reservationRepository.save(reservation);
    }

    private void validateCancellation(Reservation reservation) {
        if (!ReservationStatus.READY_TO_PLAY.equals(reservation.getReservationStatus())) {
            throw new IllegalArgumentException("Cannot cancel/reschedule because it's not in ready to play status.");
        }

        if (reservation.getSchedule().getStartDateTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Can cancel/reschedule only future dates.");
        }
    }

    public BigDecimal getRefundValue(Reservation reservation) {

        LocalDateTime reservationSchedule =reservation.getSchedule().getStartDateTime();
        BigDecimal reservationValue = reservation.getValue();

        return refundValueCalculated(reservationSchedule, reservationValue);
    }


    /*TODO: This method actually not fully working, find a way to fix the issue when it's throwing the error:
            "Cannot reschedule to the same slot.*/
    /* What I've done:
    *   First set a new Reservation object using the cancel method.
    *   After the if statement you need to cancel the previous reservation and change its status to RESCHEDULED
    *   Later you must create a new reservation using data from previous reservation and the new schedule id.
    * */
    @Transactional
    public ReservationDTO rescheduleReservation(Long previousReservationId, Long scheduleId) {

        Reservation previousReservation = cancel(previousReservationId);

        if (scheduleId.equals(previousReservation.getSchedule().getId())) {
            throw new IllegalArgumentException("Cannot reschedule to the same slot.");
        }

        previousReservation.setReservationStatus(ReservationStatus.RESCHEDULED);
        reservationRepository.save(previousReservation);

        ReservationDTO newReservation = bookReservation(CreateReservationRequestDTO.builder()
                .guestId(previousReservation.getGuest().getId())
                .scheduleId(scheduleId)
                .build());
        newReservation.setPreviousReservation(reservationMapper.map(previousReservation));
        return newReservation;
    }


    private BigDecimal refundValueCalculated(LocalDateTime reservationSchedule, BigDecimal reservationValue){

        long hours = ChronoUnit.HOURS.between(LocalDateTime.now(), reservationSchedule);
        long minutes = ChronoUnit.MINUTES.between(LocalDateTime.now(), reservationSchedule);

        if (hours >= 24L) {
            return reservationValue;
        } else if (hours >= 12L) {
            return reservationValue.multiply(new BigDecimal(.75));
        } else if (hours >= 2L) {
            return reservationValue.multiply(new BigDecimal(.50));
        } else if (minutes >= 1L) {
            return reservationValue.multiply(new BigDecimal(.25));
        } else {
            return BigDecimal.ZERO;
        }
    }
}
