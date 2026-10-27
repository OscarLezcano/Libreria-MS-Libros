package com.bigobooks.specifications;

import org.springframework.stereotype.Component;

import com.bigobooks.dto.StockAdjustmentDetailRequestDto;
import com.bigobooks.dto.StockAdjustmentRequestDto;

@Component
public class StockAdjustmentSpecifications extends Specification<StockAdjustmentRequestDto> {

    private final StockAdjustmentDetailSpecifications detailSpecifications;

    public StockAdjustmentSpecifications(StockAdjustmentDetailSpecifications detailSpecifications) {
        this.detailSpecifications = detailSpecifications;
    }

    @Override
    public void validateCreate(StockAdjustmentRequestDto dto) {
        requireText(dto.getNote(), "note");
        requireNotNull(dto.getDetails(), "details");
        if (dto.getDetails().isEmpty()) {
            throw new IllegalArgumentException("details debe contener al menos un detalle");
        }
        for (StockAdjustmentDetailRequestDto detail : dto.getDetails()) {
            requireNotNull(detail, "details[]");
            detailSpecifications.validateCreate(detail);
        }
    }

    @Override
    public void validateUpdate(StockAdjustmentRequestDto dto) {
        validateCreate(dto);
    }
}
