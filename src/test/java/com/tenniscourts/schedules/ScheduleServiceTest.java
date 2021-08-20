package com.tenniscourts.schedules;

import com.tenniscourts.exceptions.EntityNotFoundException;
import com.tenniscourts.tenniscourts.TennisCourt;
import com.tenniscourts.tenniscourts.TennisCourtDTO;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(classes = ScheduleService.class)
public class ScheduleServiceTest {

    @Mock
    ScheduleRepository scheduleRepository;

    @Mock
    ScheduleMapper scheduleMapper;

    @InjectMocks
    ScheduleService scheduleService;

    @BeforeAll
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldAddANewSchedule() {
        when(scheduleService.addSchedule(mockCreateScheduleRequestDTO())).thenReturn(mockScheduleDTO());

        ScheduleDTO scheduleDTO = scheduleService.addSchedule(mockCreateScheduleRequestDTO());

        Assert.assertTrue(scheduleDTO.getId().equals(mockScheduleDTO().getId()));
    }

    @Test
    public void shouldReturnAListOfSchedules() {
        when(scheduleService.findSchedulesByDates(Mockito.any(), Mockito.any())).thenReturn(mockListScheduleDTO());

        List<ScheduleDTO> scheduleDTOList = scheduleService.findSchedulesByDates(Mockito.any(), Mockito.any());

        Assert.assertTrue(scheduleDTOList.size() > BigInteger.ZERO.intValue());
    }

    @Test
    public void shouldFindScheduleById() {
        when(scheduleRepository.findById(mockScheduleDTO().getId())).thenReturn(Optional.of(mockSchedule()));
        when(scheduleService.findSchedule(mockScheduleDTO().getId())).thenReturn(mockScheduleDTO());

        ScheduleDTO scheduleDTO = scheduleService.findSchedule(mockScheduleDTO().getId());
        Assert.assertEquals(mockScheduleDTO().getId(), scheduleDTO.getId());
    }

    @Test(expected = EntityNotFoundException.class)
    public void shouldThrowEntityNotFoundExceptionWhenFindScheduleById() {
        when(scheduleRepository.findById(anyLong())).thenReturn(Optional.empty());

        scheduleService.findSchedule(anyLong());
    }

    @Test
    public void shouldFindSchedulesByTennisCourtId() {
        when(scheduleService.findSchedulesByTennisCourtId(anyLong())).thenReturn(mockListScheduleDTO());

        List<ScheduleDTO> scheduleDTOList = scheduleService.findSchedulesByTennisCourtId(anyLong());
        Assert.assertTrue(scheduleDTOList.size() > 0);
    }

    private List<Schedule> mockListSchedule(){
        List<Schedule> scheduleList = new ArrayList<>();
        scheduleList.add(mockSchedule());
        scheduleList.add(mockSchedule());
        return scheduleList;
    }

    private List<ScheduleDTO> mockListScheduleDTO(){
        List<ScheduleDTO> scheduleList = new ArrayList<>();
        scheduleList.add(mockScheduleDTO());
        scheduleList.add(mockScheduleDTO());
        return scheduleList;
    }

    private Schedule mockSchedule() {
        return Schedule
                .builder()
                .tennisCourt(mockTennisCourt())
                .startDateTime(LocalDateTime.now())
                .endDateTime(LocalDateTime.now().plusHours(1))
                .build();
    }

    private ScheduleDTO mockScheduleDTO(){
        return ScheduleDTO
                .builder()
                .id(1L)
                .tennisCourt(mockTennisCourtDTO())
                .tennisCourtId(mockTennisCourtDTO().getId())
                .startDateTime(LocalDateTime.now())
                .build();
    }

    private TennisCourtDTO mockTennisCourtDTO(){
        return TennisCourtDTO
                .builder()
                .id(3L)
                .name("Aberto do Rio de Janeiro")
                .build();
    }

    private TennisCourt mockTennisCourt() {
        return TennisCourt.builder().name("Aberto do Rio de Janeiro").build();
    }

    private CreateScheduleRequestDTO mockCreateScheduleRequestDTO(){
        return CreateScheduleRequestDTO
                .builder()
                .startDateTime(LocalDateTime.now())
                .tennisCourtId(3L).build();
    }
}
