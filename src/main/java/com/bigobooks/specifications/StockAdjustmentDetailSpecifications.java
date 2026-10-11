package com.bigobooks.specifications;

import org.springframework.stereotype.Component;

import com.bigobooks.dto.StockAdjustmentDetailRequestDto;

@Component
public class StockAdjustmentDetailSpecifications extends Specification<StockAdjustmentDetailRequestDto> {

    @Override
    public void validateCreate(StockAdjustmentDetailRequestDto dto) {
        requireId(dto.getBook(), "book");
        requireNotNull(dto.getPhysicalQuantity(), "physicalQuantity");
        inRange(0, Integer.MAX_VALUE, dto.getPhysicalQuantity());
    }

    @Override
    public void validateUpdate(StockAdjustmentDetailRequestDto dto) {
        validateCreate(dto);
    }
}
