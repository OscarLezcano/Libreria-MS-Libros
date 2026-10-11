package com.bigobooks.specifications;

import org.springframework.stereotype.Component;

import com.bigobooks.dto.BookRequestDto;

@Component
public class BookSpecifications extends Specification<BookRequestDto> {

    @Override
    public void validateCreate(BookRequestDto dto) {
        requireText(dto.getTitle(), "title");
        requireText(dto.getDescription(), "description");
        inRange(1L, null, dto.getPrice());
    }

    @Override
    public void validateUpdate(BookRequestDto dto) {
        validateCreate(dto);
    }
}
