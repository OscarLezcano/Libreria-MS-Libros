package com.bigobooks.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.bigobooks.dto.WarehouseDto;
import com.bigobooks.entities.stock.Warehouse;

@Mapper(componentModel = "spring")
public interface WarehouseMapper {

    WarehouseDto toDto(Warehouse warehouse);

    List<WarehouseDto> toDtoList(List<Warehouse> warehouses);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    void update(WarehouseDto dto, @MappingTarget Warehouse warehouse);
}