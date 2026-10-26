package com.bigobooks.mappers;

import java.util.ArrayList;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.bigobooks.dto.StockAdjustmentDto;
import com.bigobooks.dto.StockAdjustmentRequestDto;
import com.bigobooks.entities.stock.StockAdjustment;
import com.bigobooks.entities.stock.StockAdjustmentDetail;

@Mapper(componentModel = "spring")
public interface StockAdjustmentMapper {

    @Mapping(source = "details", target = "detailIds")
    StockAdjustmentDto toDto(StockAdjustment stockAdjustment);

    List<StockAdjustmentDto> toDtoList(List<StockAdjustment> stockAdjustments);

    @SuppressWarnings("null")
    default List<Long> toDetailIds(List<StockAdjustmentDetail> details) {
        return details == null ? new ArrayList<>()
                : details.stream().map(StockAdjustmentDetail::getId).toList();
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "warehouse", ignore = true)
    @Mapping(target = "details", ignore = true)
    void update(StockAdjustmentRequestDto dto, @MappingTarget StockAdjustment stockAdjustment);
}