package com.bigobooks.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.bigobooks.dto.BookDto;
import com.bigobooks.entities.book.Author;
import com.bigobooks.entities.book.Book;
import com.bigobooks.entities.book.Genre;
import com.bigobooks.mappers.BookMapper;
import com.bigobooks.repositories.AuthorRepository;
import com.bigobooks.repositories.BookRepository;
import com.bigobooks.repositories.GenreRepository;
import com.bigobooks.service.BaseService;

@Service
public class BookService extends BaseService<Book, BookRepository> {

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

    @Transactional(readOnly = true)
    public List<BookDto> getAll() {
        return bookMapper.toDtoList(findAll());
    }

    @Transactional(readOnly = true)
    public Optional<BookDto> getById(Long id) {
        return findById(id).map(bookMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<BookDto> findByTitle(String title) {
        return getRepository().findByTitle(title).map(bookMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<BookDto> search(String title) {
        if (!StringUtils.hasText(title)) {
            return getAll();
        }
        return bookMapper.toDtoList(getRepository().searchByTitle(title.trim()));
    }

    @Transactional(readOnly = true)
    public List<BookDto> getByGenre(Long genreId) {
        return bookMapper.toDtoList(getRepository().findByGenres_Id(genreId));
    }

    @Transactional(readOnly = true)
    public List<BookDto> getByAuthor(Long authorId) {
        return bookMapper.toDtoList(getRepository().findByAuthors_Id(authorId));
    }

    @Transactional
    public BookDto create(BookDto dto) {
        Book book = new Book();
        bookMapper.update(dto, book);
        book.setGenres(resolveGenres(dto.getGenreIds()));
        book.setAuthors(resolveAuthors(dto.getAuthorIds()));
        return bookMapper.toDto(save(book));
    }

    @Transactional
    public Optional<BookDto> update(Long id, BookDto dto) {
        return findById(id).map(book -> {
            bookMapper.update(dto, book);
            if (dto.getGenreIds() != null) {
                book.setGenres(resolveGenres(dto.getGenreIds()));
            }
            if (dto.getAuthorIds() != null) {
                book.setAuthors(resolveAuthors(dto.getAuthorIds()));
            }
            return bookMapper.toDto(save(book));
        });
    }

    @Transactional
    public void delete(Long id) {
        findById(id).ifPresent(book -> deleteById(book.getId()));
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