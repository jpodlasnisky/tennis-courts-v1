package com.tenniscourts.guests;

import com.tenniscourts.exceptions.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class GuestService {

    private final GuestRepository guestRepository;

    private final GuestMapper guestMapper;

    public GuestDTO addGuest(GuestDTO guestDTO){
        return guestMapper.map(guestRepository
                .saveAndFlush(guestMapper.map(guestDTO)));
    }

    @SneakyThrows
    public GuestDTO findGuestById(Long id) {
        return guestMapper.map(guestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Guest not found.")));
    }

    public List<GuestDTO> listAllGuests(){
        List<Guest> guests = guestRepository.findAll();
        List<GuestDTO> guestDTOList = new ArrayList<>();

        guests
                .stream()
                .forEach(guest -> guestDTOList
                        .add(GuestDTO
                                .builder()
                                .id(guest.getId())
                                .name(guest.getName())
                                .build()));
        return guestDTOList;
    }

    @SneakyThrows
    public void deleteGuest(Long guestId) {

        guestRepository.delete(guestRepository.findById(guestId)
                .orElseThrow(() -> {
                    throw new EntityNotFoundException("Guest not found.");
        }));
    }

    @SneakyThrows
    public Guest findGuestByName(String name){
        try {
            return guestRepository.findByName(name);
        } catch (EntityNotFoundException exception){
            throw new EntityNotFoundException("Guest not found.");
        }
    }

    @SneakyThrows
    public GuestDTO updateGuest(GuestDTO guestUpdate){
        GuestDTO guestDTO = guestRepository.findById(guestUpdate.getId())
                .map(guestMapper::map)
                .orElseThrow(() -> {
                    throw new EntityNotFoundException("Guest not found.");
                });
        guestDTO.setName(guestUpdate.getName());

        return guestMapper.map(guestRepository.save(guestMapper.map(guestDTO)));
    }
}
