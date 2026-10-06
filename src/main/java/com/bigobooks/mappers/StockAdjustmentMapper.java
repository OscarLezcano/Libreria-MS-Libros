package com.bigobooks.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.bigobooks.dto.StockAdjustmentDto;
import com.bigobooks.entities.stock.StockAdjustment;

@Mapper(componentModel = "spring", uses = StockAdjustmentDetailMapper.class)
public interface StockAdjustmentMapper {

    StockAdjustmentDto toDto(StockAdjustment stockAdjustment);

    List<StockAdjustmentDto> toDtoList(List<StockAdjustment> stockAdjustments);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "warehouse", ignore = true)
    @Mapping(target = "details", ignore = true)
    void update(StockAdjustmentDto dto, @MappingTarget StockAdjustment stockAdjustment);
}