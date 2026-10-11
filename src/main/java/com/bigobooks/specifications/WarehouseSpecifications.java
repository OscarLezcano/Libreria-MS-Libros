package com.bigobooks.specifications;

import org.springframework.stereotype.Component;

import com.bigobooks.dto.WarehouseRequestDto;

@Component
public class WarehouseSpecifications extends Specification<WarehouseRequestDto> {

    @Override
    public void validateCreate(WarehouseRequestDto dto) {
        requireText(dto.getName(), "name");
        requireText(dto.getAddress(), "address");
        requireText(dto.getCity(), "city");
        requireTextIfPresent(dto.getPhone(), "phone");
    }

    @Override
    public void validateUpdate(WarehouseRequestDto dto) {
        validateCreate(dto);
    }
}
