package com.tenniscourts.schedules;

import com.tenniscourts.exceptions.EntityNotFoundException;
import com.tenniscourts.tenniscourts.TennisCourtDTO;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    private final ScheduleMapper scheduleMapper;


    public ScheduleDTO addSchedule(CreateScheduleRequestDTO createScheduleRequestDTO) {

        ScheduleDTO scheduleDTO = new ScheduleDTO();

        scheduleDTO.setTennisCourtId(createScheduleRequestDTO.getTennisCourtId());
        scheduleDTO.setStartDateTime(createScheduleRequestDTO.getStartDateTime());
        scheduleDTO.setEndDateTime(createScheduleRequestDTO.getStartDateTime().plusHours(1));
        scheduleDTO.setTennisCourt(TennisCourtDTO.builder().id(createScheduleRequestDTO.getTennisCourtId()).build());
        return scheduleMapper.map(scheduleRepository.saveAndFlush(scheduleMapper.map(scheduleDTO)));
    }

    public List<ScheduleDTO> findSchedulesByDates(LocalDateTime startDate, LocalDateTime endDate) {
        return scheduleMapper.map(scheduleRepository.findSchedules(startDate, endDate));
    }

    @SneakyThrows
    public ScheduleDTO findSchedule(Long scheduleId) {
        return scheduleMapper.map(scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> {
                    throw new EntityNotFoundException("Schedule not found.");
                }));
    }

    public List<ScheduleDTO> findSchedulesByTennisCourtId(Long tennisCourtId) {
        return scheduleMapper.map(scheduleRepository.findByTennisCourt_IdOrderByStartDateTime(tennisCourtId));
    }
}
