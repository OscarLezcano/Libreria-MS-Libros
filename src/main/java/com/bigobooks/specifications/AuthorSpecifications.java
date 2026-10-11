package com.bigobooks.specifications;

import org.springframework.stereotype.Component;

import com.bigobooks.dto.AuthorRequestDto;

@Component
public class AuthorSpecifications extends Specification<AuthorRequestDto> {

    @Override
    public void validateCreate(AuthorRequestDto dto) {
        requireText(dto.getName(), "name");
    }

    @Override
    public void validateUpdate(AuthorRequestDto dto) {
        validateCreate(dto);
    }
}
