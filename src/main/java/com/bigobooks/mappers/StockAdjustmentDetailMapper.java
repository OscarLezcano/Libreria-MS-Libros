package com.bigobooks.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.bigobooks.dto.StockAdjustmentDetailDto;
import com.bigobooks.dto.StockAdjustmentDetailRequestDto;
import com.bigobooks.entities.stock.StockAdjustmentDetail;

@Mapper(componentModel = "spring")
public interface StockAdjustmentDetailMapper {

    @Mapping(source = "book.id", target = "bookId")
    StockAdjustmentDetailDto toDto(StockAdjustmentDetail detail);

    List<StockAdjustmentDetailDto> toDtoList(List<StockAdjustmentDetail> details);

    @Mapping(target = "book", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "stockAdjustment", ignore = true)
    @Mapping(target = "bookName", ignore = true)
    @Mapping(target = "systemQuantity", ignore = true)
    void update(StockAdjustmentDetailRequestDto dto, @MappingTarget StockAdjustmentDetail detail);
}