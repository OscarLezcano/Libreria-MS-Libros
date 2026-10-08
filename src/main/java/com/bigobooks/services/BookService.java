package com.bigobooks.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.bigobooks.dto.BookDto;
import com.bigobooks.dto.BookRequestDto;
import com.bigobooks.entities.book.Author;
import com.bigobooks.entities.book.Book;
import com.bigobooks.entities.book.Genre;
import com.bigobooks.mappers.BookMapper;
import com.bigobooks.repositories.AuthorRepository;
import com.bigobooks.repositories.BookRepository;
import com.bigobooks.repositories.GenreRepository;

@Service
public class BookService extends AbstractCrudService<Book, BookDto, BookRepository> {

    private final GenreRepository genreRepository;
    private final AuthorRepository authorRepository;
    private final BookMapper bookMapper;

    public BookService(BookRepository bookRepository, GenreRepository genreRepository,
            AuthorRepository authorRepository, BookMapper bookMapper) {
        super(bookRepository);
        this.genreRepository = genreRepository;
        this.authorRepository = authorRepository;
        this.bookMapper = bookMapper;
    }

    @Override
    protected Function<Book, BookDto> toDtoMapper() {
        return bookMapper::toDto;
    }

    @Transactional(readOnly = true)
    public Optional<BookDto> findByTitle(String title) {
        return repository.findByTitle(title).map(bookMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<BookDto> search(String title, Pageable pageable) {
        if (!StringUtils.hasText(title)) {
            return getAll(pageable);
        }
        return repository.searchByName(title.trim(), pageable).map(bookMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<BookDto> getByGenre(Long genreId, Pageable pageable) {
        return repository.findByGenres_Id(genreId, pageable).map(bookMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<BookDto> getByAuthor(Long authorId, Pageable pageable) {
        return repository.findByAuthors_Id(authorId, pageable).map(bookMapper::toDto);
    }

    @Transactional
    public BookDto create(BookRequestDto dto) {
        Book book = new Book();
        bookMapper.update(dto, book);
        book.setGenres(resolveGenres(dto.getGenreIds()));
        book.setAuthors(resolveAuthors(dto.getAuthorIds()));
        return bookMapper.toDto(repository.save(book));
    }

    @Transactional
    public Optional<BookDto> update(Long id, BookRequestDto dto) {
        return repository.findById(id).map(book -> {
            bookMapper.update(dto, book);
            if (dto.getGenreIds() != null) {
                book.setGenres(resolveGenres(dto.getGenreIds()));
            }
            if (dto.getAuthorIds() != null) {
                book.setAuthors(resolveAuthors(dto.getAuthorIds()));
            }
            return bookMapper.toDto(repository.save(book));
        });
    }

    private List<Genre> resolveGenres(List<Long> genreIds) {
        if (genreIds == null || genreIds.isEmpty()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(genreRepository.findAllById(genreIds));
    }

    private List<Author> resolveAuthors(List<Long> authorIds) {
        if (authorIds == null || authorIds.isEmpty()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(authorRepository.findAllById(authorIds));
    }
}
