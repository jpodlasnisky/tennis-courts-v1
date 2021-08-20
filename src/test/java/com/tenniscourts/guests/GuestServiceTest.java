package com.tenniscourts.guests;

import com.tenniscourts.exceptions.EntityNotFoundException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(classes = GuestService.class)
public class GuestServiceTest {

    @Mock
    GuestRepository guestRepository;

    @Mock
    GuestMapper guestMapper;

    @InjectMocks
    GuestService guestService;

    @BeforeAll
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        when(guestRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(mockGuest()));
        when(guestService.findGuestById(anyLong())).thenReturn(mockGuestDTO());

    }

    @Test
    public void shouldAddANewGuest() {
        when(guestService.addGuest(mockGuestDTO())).thenReturn(mockGuestDTO());

        GuestDTO guestDTO = guestService.addGuest(mockGuestDTO());

        Assert.assertTrue(guestDTO.getId().equals(mockGuestDTO().getId()));
        Assert.assertTrue(guestDTO.getName().equals(mockGuestDTO().getName()));
    }

    @Test
    public void shouldFindGuestById() {

        when(guestRepository.findById(mockGuestDTO().getId())).thenReturn(Optional.of(mockGuest()));
        when(guestService.findGuestById(mockGuestDTO().getId())).thenReturn(mockGuestDTO());

        GuestDTO guestDTO = guestService.findGuestById(mockGuestDTO().getId());
        Assert.assertEquals(mockGuestDTO().getId(), guestDTO.getId());
    }

    @Test(expected = EntityNotFoundException.class)
    public void shouldThrowEntityNotFoundExceptionWhenFindGuestById() {
        when(guestRepository.findById(anyLong())).thenReturn(Optional.empty());

        guestService.findGuestById(anyLong());
    }

    @Test
    public void shouldFindAllGuests() {
        when(guestRepository.findAll()).thenReturn(mockGuestList());

        List<GuestDTO> guestList = guestService.listAllGuests();

        Assert.assertTrue(guestList.size() > BigInteger.ZERO.intValue());
    }

    @Test
    public void shouldDeleteAGuest() {
        when(guestRepository.findById(mockGuest().getId())).thenReturn(Optional.of(mockGuest()));

        guestService.deleteGuest(mockGuest().getId());

        verify(guestRepository, times(1)).delete(mockGuest());
    }

    @Test
    public void shouldFindGuestByName() {

        when(guestRepository.findByName(mockGuest().getName())).thenReturn(mockGuest());
        when(guestService.findGuestByName(mockGuest().getName())).thenReturn(mockGuest());

        Guest guest = guestService.findGuestByName(mockGuest().getName());
        Assert.assertEquals(mockGuest().getName(), guest.getName());
    }


    private GuestDTO mockGuestDTO() {
        return GuestDTO.builder().id(10L).name("João Pedro").build();
    }

    private GuestDTO mockGuestDTOUpdate() {
        return GuestDTO.builder().id(10L).name("Aristídes").build();
    }

        private Guest mockGuest() {
        Guest guest = new Guest();
        guest.setName("João Paulo");
        guest.setId(15L);
        return guest;
    }

    private List<Guest> mockGuestList() {
        List<Guest> guestList = new ArrayList<>();
        guestList.add(mockGuest());
        guestList.add(mockGuest());
        guestList.add(mockGuest());
        return guestList;
    }
}
