package com.bigobooks.specifications;

import org.springframework.stereotype.Component;

import com.bigobooks.dto.GenreRequestDto;

@Component
public class GenreSpecifications extends Specification<GenreRequestDto> {

    @Override
    public void validateCreate(GenreRequestDto dto) {
        requireText(dto.getName(), "name");
    }

    @Override
    public void validateUpdate(GenreRequestDto dto) {
        validateCreate(dto);
    }
}
