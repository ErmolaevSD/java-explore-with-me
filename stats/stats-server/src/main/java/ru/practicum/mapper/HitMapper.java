package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.EndpointHit;
import ru.practicum.model.Hit;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface HitMapper {

    @Mapping(target = "timestamp", source = "createdAt")
    EndpointHit toDto(Hit hit);

    @Mapping(target = "createdAt", source = "timestamp")
    Hit toEntity(EndpointHit dto);

}
