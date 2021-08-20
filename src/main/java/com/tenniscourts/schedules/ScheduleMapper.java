package com.tenniscourts.schedules;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ScheduleMapper {

    ScheduleDTO map(Schedule source);

    @InheritInverseConfiguration
    Schedule map(ScheduleDTO source);

    List<ScheduleDTO> map(List<Schedule> source);

}
