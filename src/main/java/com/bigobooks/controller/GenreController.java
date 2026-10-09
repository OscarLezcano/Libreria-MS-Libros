package com.bigobooks.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.bigobooks.dto.GenreDto;
import com.bigobooks.dto.GenreListResponse;
import com.bigobooks.dto.GenreRequestDto;
import com.bigobooks.dto.GenreResponse;
import com.bigobooks.services.GenreService;
import com.bigobooks.util.Envelopes;
import com.bigobooks.util.Pageables;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class GenreController implements GenresApi {

    private final GenreService genreService;
    private final Pageables pageables;

    @Override
    public ResponseEntity<GenreListResponse> getGenres(String name, Integer page, Integer size) {
        Page<GenreDto> result = genreService.search(name, pageables.of(page, size));
        return ResponseEntity.ok(new GenreListResponse()
                .data(result.getContent())
                .pagination(Envelopes.pagination(result)));
    }

    @Override
    public ResponseEntity<GenreResponse> getGenreById(Long id) {
        return genreService.getById(id)
                .map(genre -> ResponseEntity.ok(new GenreResponse().data(genre)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<GenreResponse> createGenre(GenreRequestDto genreRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new GenreResponse().data(genreService.create(genreRequestDto)));
    }

    @Override
    public ResponseEntity<GenreResponse> updateGenre(Long id, GenreRequestDto genreRequestDto) {
        return genreService.update(id, genreRequestDto)
                .map(genre -> ResponseEntity.ok(new GenreResponse().data(genre)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Void> deleteGenre(Long id) {
        genreService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
