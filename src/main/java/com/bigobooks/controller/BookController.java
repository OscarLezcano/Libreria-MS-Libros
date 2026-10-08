package com.bigobooks.controller;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.bigobooks.dto.BookDto;
import com.bigobooks.dto.BookListResponse;
import com.bigobooks.dto.BookRequestDto;
import com.bigobooks.dto.BookResponse;
import com.bigobooks.services.BookService;
import com.bigobooks.util.Envelopes;

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
    public ResponseEntity<BookListResponse> getBooks(String title, Integer page, Integer size) {
        Page<BookDto> result = bookService.search(title, pageable(page, size));
        return ResponseEntity.ok(new BookListResponse()
                .data(result.getContent())
                .pagination(Envelopes.pagination(result)));
    }

    @Override
    public ResponseEntity<BookResponse> getBookById(Long id) {
        return bookService.getById(id)
                .map(book -> ResponseEntity.ok(new BookResponse().data(book)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<BookListResponse> getBooksByGenre(Long genreId, Integer page, Integer size) {
        Page<BookDto> result = bookService.getByGenre(genreId, pageable(page, size));
        return ResponseEntity.ok(new BookListResponse()
                .data(result.getContent())
                .pagination(Envelopes.pagination(result)));
    }

    @Override
    public ResponseEntity<BookListResponse> getBooksByAuthor(Long authorId, Integer page, Integer size) {
        Page<BookDto> result = bookService.getByAuthor(authorId, pageable(page, size));
        return ResponseEntity.ok(new BookListResponse()
                .data(result.getContent())
                .pagination(Envelopes.pagination(result)));
    }

    @Override
    public ResponseEntity<BookResponse> createBook(BookRequestDto bookRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new BookResponse().data(bookService.create(bookRequestDto)));
    }

    @Override
    public ResponseEntity<BookResponse> updateBook(Long id, BookRequestDto bookRequestDto) {
        return bookService.update(id, bookRequestDto)
                .map(book -> ResponseEntity.ok(new BookResponse().data(book)))
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
