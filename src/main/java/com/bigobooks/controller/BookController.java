package com.bigobooks.controller;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.bigobooks.dto.BookDto;
import com.bigobooks.dto.Envelope;
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
    public ResponseEntity<Envelope> getBooks(String title, Integer page, Integer size) {
        Page<BookDto> result = bookService.search(title, pageable(page, size));
        return ResponseEntity.ok(Envelopes.page(result, result.getContent()));
    }

    @Override
    public ResponseEntity<Envelope> getBookById(Long id) {
        return bookService.getById(id)
                .map(book -> ResponseEntity.ok(Envelopes.single(book)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Envelope> getBooksByGenre(Long genreId, Integer page, Integer size) {
        Page<BookDto> result = bookService.getByGenre(genreId, pageable(page, size));
        return ResponseEntity.ok(Envelopes.page(result, result.getContent()));
    }

    @Override
    public ResponseEntity<Envelope> getBooksByAuthor(Long authorId, Integer page, Integer size) {
        Page<BookDto> result = bookService.getByAuthor(authorId, pageable(page, size));
        return ResponseEntity.ok(Envelopes.page(result, result.getContent()));
    }

    @Override
    public ResponseEntity<Envelope> createBook(BookDto bookDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Envelopes.single(bookService.create(bookDto)));
    }

    @Override
    public ResponseEntity<Envelope> updateBook(Long id, BookDto bookDto) {
        return bookService.update(id, bookDto)
                .map(book -> ResponseEntity.ok(Envelopes.single(book)))
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
