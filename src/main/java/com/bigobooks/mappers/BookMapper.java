package com.bigobooks.mappers;

import java.util.ArrayList;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.bigobooks.dto.AuthorDto;
import com.bigobooks.dto.BookDto;
import com.bigobooks.dto.BookRequestDto;
import com.bigobooks.dto.GenreDto;
import com.bigobooks.entities.book.Book;
import com.bigobooks.entities.book.BookAuthor;
import com.bigobooks.entities.book.BookGenre;

@Mapper(componentModel = "spring", uses = { GenreMapper.class, AuthorMapper.class })
public interface BookMapper {

    @Mapping(source = "genres", target = "genreIds")
    @Mapping(source = "authors", target = "authorIds")
    BookDto toDto(Book book);

    List<BookDto> toDtoList(List<Book> books);

    @Mapping(source = "genre.id", target = "id")
    @Mapping(source = "genre.name", target = "name")
    // @Mapping(source = "genre.createdAt", target = "createdAt")
    GenreDto toDto(BookGenre bookGenre);

    @Mapping(source = "author.id", target = "id")
    @Mapping(source = "author.name", target = "name")
    // @Mapping(source = "author.createdAt", target = "createdAt")
    AuthorDto toDto(BookAuthor bookAuthor);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "genres", ignore = true)
    @Mapping(target = "authors", ignore = true)
    @Mapping(target = "wishlistedBy", ignore = true)
    void update(BookRequestDto dto, @MappingTarget Book book);

    default List<Long> toGenreIds(List<BookGenre> genres) {
        return genres == null ? new ArrayList<>() : genres.stream().map(link -> link.getGenre().getId()).toList();
    }

    default List<Long> toAuthorIds(List<BookAuthor> authors) {
        return authors == null ? new ArrayList<>() : authors.stream().map(link -> link.getAuthor().getId()).toList();
    }
}