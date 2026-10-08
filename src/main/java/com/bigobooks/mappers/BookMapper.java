package com.bigobooks.mappers;

import java.util.ArrayList;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.bigobooks.dto.BookDto;
import com.bigobooks.dto.BookRequestDto;
import com.bigobooks.entities.book.Author;
import com.bigobooks.entities.book.Book;
import com.bigobooks.entities.book.Genre;

@Mapper(componentModel = "spring")
public interface BookMapper {

    @Mapping(source = "genres", target = "genreIds")
    @Mapping(source = "authors", target = "authorIds")
    BookDto toDto(Book book);

    List<BookDto> toDtoList(List<Book> books);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "genres", ignore = true)
    @Mapping(target = "authors", ignore = true)
    @Mapping(target = "wishlistedBy", ignore = true)
    void update(BookRequestDto dto, @MappingTarget Book book);

    default List<Long> toGenreIds(List<Genre> genres) {
        return genres == null ? new ArrayList<>() : genres.stream().map(Genre::getId).toList();
    }

    default List<Long> toAuthorIds(List<Author> authors) {
        return authors == null ? new ArrayList<>() : authors.stream().map(Author::getId).toList();
    }
}