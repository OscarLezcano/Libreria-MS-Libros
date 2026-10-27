package com.bigobooks.specifications;

import org.springframework.stereotype.Component;

import com.bigobooks.dto.StockQuantityRequestDto;

@Component
public class StockSpecifications extends Specification<StockQuantityRequestDto> {

    @Override
    public void validateCreate(StockQuantityRequestDto dto) {
        requireNotNull(dto.getQuantity(), "quantity");
    }

    @Override
    public void validateUpdate(StockQuantityRequestDto dto) {
        validateCreate(dto);
    }

    public void validateQuantity(int quantity) {
        inRange(0, Integer.MAX_VALUE, quantity);
    }

    public void validateResult(int currentQuantity, int delta) {
        if (currentQuantity + delta < 0) {
            throw new IllegalArgumentException(
                    "El stock resultante no puede ser negativo (actual " + currentQuantity
                            + ", delta " + delta + ").");
        }
    }
}
