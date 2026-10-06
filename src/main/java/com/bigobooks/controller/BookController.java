package com.bigobooks.controller;

import org.springframework.beans.factory.annotation.Value;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.bigobooks.dto.BookDto;
import com.bigobooks.services.BookService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class BookController implements BooksApi {

    private final BookService bookService;

    @Value("${app.pagination.page-size:25}")
    private int defaultPageSize;

    @Value("${app.pagination.max-page-size:100}")
    private int maxPageSize;

    @Override
    public ResponseEntity<List<BookDto>> getBooks(String title, Integer page, Integer size) {
        return ResponseEntity.ok(bookService.search(title, pageable(page, size)).getContent());
    }

    @Override
    public ResponseEntity<BookDto> getBookById(Long id) {
        return bookService.getById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<List<BookDto>> getBooksByGenre(Long genreId, Integer page, Integer size) {
        return ResponseEntity.ok(bookService.getByGenre(genreId, pageable(page, size)).getContent());
    }

    @Override
    public ResponseEntity<List<BookDto>> getBooksByAuthor(Long authorId, Integer page, Integer size) {
        return ResponseEntity.ok(bookService.getByAuthor(authorId, pageable(page, size)).getContent());
    }

    @Override
    public ResponseEntity<BookDto> createBook(BookDto bookDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.create(bookDto));
    }

    @Override
    public ResponseEntity<BookDto> updateBook(Long id, BookDto bookDto) {
        return bookService.update(id, bookDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Void> deleteBook(Long id) {
        bookService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private Pageable pageable(Integer page, Integer size) {
        int pageNo = page == null ? 0 : page;
        int pageSize = Math.min(size == null ? defaultPageSize : size, maxPageSize);
        return PageRequest.of(pageNo, pageSize);
    }
}
