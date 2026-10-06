package com.bigobooks.controller;

import org.springframework.beans.factory.annotation.Value;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.bigobooks.dto.GenreDto;
import com.bigobooks.services.GenreService;

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
    public ResponseEntity<List<GenreDto>> getGenres(String name, Integer page, Integer size) {
        return ResponseEntity.ok(genreService.search(name, pageable(page, size)).getContent());
    }

    @Override
    public ResponseEntity<GenreDto> getGenreById(Long id) {
        return genreService.getById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<GenreDto> createGenre(GenreDto genreDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(genreService.create(genreDto));
    }

    @Override
    public ResponseEntity<GenreDto> updateGenre(Long id, GenreDto genreDto) {
        return genreService.update(id, genreDto)
                .map(ResponseEntity::ok)
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
