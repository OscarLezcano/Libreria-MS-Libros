package com.bigobooks.controller;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.bigobooks.dto.AuthorDto;
import com.bigobooks.dto.Envelope;
import com.bigobooks.services.AuthorService;
import com.bigobooks.util.Envelopes;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuthorController implements AuthorsApi {

    private final AuthorService authorService;

    @Value("${app.pagination.page-size:25}")
    private int defaultPageSize;

    @Value("${app.pagination.max-page-size:100}")
    private int maxPageSize;

    @Override
    public ResponseEntity<Envelope> getAuthors(String name, Integer page, Integer size) {
        Page<AuthorDto> result = authorService.search(name, pageable(page, size));
        return ResponseEntity.ok(Envelopes.page(result, result.getContent()));
    }

    @Override
    public ResponseEntity<Envelope> getAuthorById(Long id) {
        return authorService.getById(id)
                .map(author -> ResponseEntity.ok(Envelopes.single(author)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Envelope> createAuthor(AuthorDto authorDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Envelopes.single(authorService.create(authorDto)));
    }

    @Override
    public ResponseEntity<Envelope> updateAuthor(Long id, AuthorDto authorDto) {
        return authorService.update(id, authorDto)
                .map(author -> ResponseEntity.ok(Envelopes.single(author)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Void> deleteAuthor(Long id) {
        authorService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private Pageable pageable(Integer page, Integer size) {
        int pageNo = page == null ? 0 : page;
        int pageSize = Math.min(size == null ? defaultPageSize : size, maxPageSize);
        return PageRequest.of(pageNo, pageSize);
    }
}
