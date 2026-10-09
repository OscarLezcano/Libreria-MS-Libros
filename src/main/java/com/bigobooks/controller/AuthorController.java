package com.bigobooks.controller;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.bigobooks.dto.AuthorDto;
import com.bigobooks.dto.AuthorListResponse;
import com.bigobooks.dto.AuthorRequestDto;
import com.bigobooks.dto.AuthorResponse;
import com.bigobooks.services.AuthorService;
import com.bigobooks.util.Envelopes;
import com.bigobooks.util.Pageables;

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
    public ResponseEntity<AuthorListResponse> getAuthors(String name, Integer page, Integer size) {
        Page<AuthorDto> result = authorService.search(name, Pageables.of(page, size, defaultPageSize, maxPageSize));
        return ResponseEntity.ok(new AuthorListResponse()
                .data(result.getContent())
                .pagination(Envelopes.pagination(result)));
    }

    @Override
    public ResponseEntity<AuthorResponse> getAuthorById(Long id) {
        return authorService.getById(id)
                .map(author -> ResponseEntity.ok(new AuthorResponse().data(author)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<AuthorResponse> createAuthor(AuthorRequestDto authorRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthorResponse().data(authorService.create(authorRequestDto)));
    }

    @Override
    public ResponseEntity<AuthorResponse> updateAuthor(Long id, AuthorRequestDto authorRequestDto) {
        return authorService.update(id, authorRequestDto)
                .map(author -> ResponseEntity.ok(new AuthorResponse().data(author)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Void> deleteAuthor(Long id) {
        authorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
