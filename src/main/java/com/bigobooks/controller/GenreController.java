package com.bigobooks.controller;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.bigobooks.dto.Envelope;
import com.bigobooks.dto.GenreDto;
import com.bigobooks.services.GenreService;
import com.bigobooks.util.Envelopes;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class GenreController implements GenresApi {

    private final GenreService genreService;

    @Value("${app.pagination.page-size:25}")
    private int defaultPageSize;

    @Value("${app.pagination.max-page-size:100}")
    private int maxPageSize;

    @Override
    public ResponseEntity<Envelope> getGenres(String name, Integer page, Integer size) {
        Page<GenreDto> result = genreService.search(name, pageable(page, size));
        return ResponseEntity.ok(Envelopes.page(result, result.getContent()));
    }

    @Override
    public ResponseEntity<Envelope> getGenreById(Long id) {
        return genreService.getById(id)
                .map(genre -> ResponseEntity.ok(Envelopes.single(genre)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Envelope> createGenre(GenreDto genreDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Envelopes.single(genreService.create(genreDto)));
    }

    @Override
    public ResponseEntity<Envelope> updateGenre(Long id, GenreDto genreDto) {
        return genreService.update(id, genreDto)
                .map(genre -> ResponseEntity.ok(Envelopes.single(genre)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Void> deleteGenre(Long id) {
        genreService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private Pageable pageable(Integer page, Integer size) {
        int pageNo = page == null ? 0 : page;
        int pageSize = Math.min(size == null ? defaultPageSize : size, maxPageSize);
        return PageRequest.of(pageNo, pageSize);
    }
}
