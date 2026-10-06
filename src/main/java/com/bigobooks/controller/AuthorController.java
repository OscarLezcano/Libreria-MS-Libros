package com.bigobooks.controller;

import org.springframework.beans.factory.annotation.Value;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.bigobooks.dto.AuthorDto;
import com.bigobooks.services.AuthorService;

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
    public ResponseEntity<List<AuthorDto>> getAuthors(String name, Integer page, Integer size) {
        return ResponseEntity.ok(authorService.search(name, pageable(page, size)).getContent());
    }

    @Override
    public ResponseEntity<AuthorDto> getAuthorById(Long id) {
        return authorService.getById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<AuthorDto> createAuthor(AuthorDto authorDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authorService.create(authorDto));
    }

    @Override
    public ResponseEntity<AuthorDto> updateAuthor(Long id, AuthorDto authorDto) {
        return authorService.update(id, authorDto)
                .map(ResponseEntity::ok)
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
