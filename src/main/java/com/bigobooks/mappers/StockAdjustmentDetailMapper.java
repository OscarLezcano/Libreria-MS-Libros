package com.bigobooks.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.bigobooks.dto.StockAdjustmentDetailDto;
import com.bigobooks.entities.stock.StockAdjustmentDetail;

@Mapper(componentModel = "spring")
public interface StockAdjustmentDetailMapper {

    @Mapping(source = "stockAdjustment.id", target = "stockAdjustmentId")
    StockAdjustmentDetailDto toDto(StockAdjustmentDetail detail);

    List<StockAdjustmentDetailDto> toDtoList(List<StockAdjustmentDetail> details);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "stockAdjustment", ignore = true)
    void update(StockAdjustmentDetailDto dto, @MappingTarget StockAdjustmentDetail detail);
}