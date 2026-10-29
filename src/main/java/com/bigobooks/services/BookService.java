package com.bigobooks.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

import com.bigobooks.dto.BookDto;
import com.bigobooks.dto.BookRequestDto;
import com.bigobooks.entities.book.Author;
import com.bigobooks.entities.book.Book;
import com.bigobooks.entities.book.BookAuthor;
import com.bigobooks.entities.book.BookGenre;
import com.bigobooks.entities.book.Genre;
import com.bigobooks.mappers.BookMapper;
import com.bigobooks.repositories.AuthorRepository;
import com.bigobooks.repositories.BookRepository;
import com.bigobooks.repositories.GenreRepository;
import com.bigobooks.specifications.BookSpecifications;

@Service
@Slf4j
public class BookService extends AbstractCrudService<Book, BookDto, BookRepository> {

    private final GenreRepository genreRepository;
    private final AuthorRepository authorRepository;
    private final BookMapper bookMapper;
    private final BookSpecifications bookSpecifications;
    private final Cache cache;

    public BookService(BookRepository bookRepository, GenreRepository genreRepository,
            AuthorRepository authorRepository, BookMapper bookMapper, BookSpecifications bookSpecifications,
            CacheManager cacheManager) {
        super(bookRepository);
        this.genreRepository = genreRepository;
        this.authorRepository = authorRepository;
        this.bookMapper = bookMapper;
        this.bookSpecifications = bookSpecifications;
        this.cache = cacheManager.getCache("book");
    }

    @Override
    protected Function<Book, BookDto> toDtoMapper() {
        return bookMapper::toDto;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookDto> getAll(Pageable pageable) {
        Page<Long> idPage = repository.findAllIds(pageable);
        ArrayList<BookDto> books = new ArrayList<>(idPage.getNumberOfElements());

        for (Long id : idPage.getContent()) {
            String cacheKey = "book_" + id;
            BookDto cached = cache.get(cacheKey, BookDto.class);
            log.debug("Cache GET cache=book key={} result={}", cacheKey, cached == null ? "MISS" : "HIT");

            if (cached != null) {
                books.add(cached);
                continue;
            }

            getById(id).ifPresent(book -> {
                cache.put(cacheKey, book);
                log.debug("Cache PUT cache=book key={}", cacheKey);
                books.add(book);
            });
        }

        return new PageImpl<>(books, pageable, idPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "book", key = "'book_' + #id")
    public Optional<BookDto> getById(Long id) {
        return super.getById(id);
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
        return repository.findByGenreId(genreId, pageable).map(bookMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<BookDto> getByAuthor(Long authorId, Pageable pageable) {
        return repository.findByAuthorId(authorId, pageable).map(bookMapper::toDto);
    }

    @Transactional
    @CachePut(value = "book", key = "'book_' + #result.id", condition = "#result.id != null")
    public BookDto create(BookRequestDto dto) {
        bookSpecifications.validateCreate(dto);
        Book book = new Book();
        bookMapper.update(dto, book);
        book.setGenres(resolveGenres(book, dto.getGenreIds()));
        book.setAuthors(resolveAuthors(book, dto.getAuthorIds()));
        return bookMapper.toDto(repository.save(book));
    }

    @Transactional
    @CachePut(value = "book", key = "'book_' + #id", condition = "#result != null")
    public Optional<BookDto> update(Long id, BookRequestDto dto) {
        bookSpecifications.validateUpdate(dto);
        return repository.findById(id).map(book -> {
            List<BookGenre> requestedGenres = dto.getGenreIds() == null
                    ? null
                    : resolveGenres(book, dto.getGenreIds());
            List<BookAuthor> requestedAuthors = dto.getAuthorIds() == null
                    ? null
                    : resolveAuthors(book, dto.getAuthorIds());

            bookMapper.update(dto, book);
            if (requestedGenres != null) {
                reconcileGenres(book, requestedGenres);
            }
            if (requestedAuthors != null) {
                reconcileAuthors(book, requestedAuthors);
            }
            return bookMapper.toDto(repository.save(book));
        });
    }

    @Override
    @Transactional
    @CacheEvict(value = "book", key = "'book_' + #id")
    public void delete(Long id) {
        super.delete(id);
    }

    private void reconcileGenres(Book book, List<BookGenre> requested) {
        List<BookGenre> current = book.getGenres();
        if (current == null) {
            current = new ArrayList<>();
            book.setGenres(current);
        }

        Set<Long> requestedIds = new HashSet<>();
        for (BookGenre link : requested) {
            requestedIds.add(link.getGenre().getId());
        }

        current.removeIf(link -> {
            boolean removed = !requestedIds.contains(link.getGenre().getId());
            if (removed) {
                link.setDeleted(true);
            }
            return removed;
        });

        Set<Long> currentIds = new HashSet<>();
        for (BookGenre link : current) {
            currentIds.add(link.getGenre().getId());
        }

        for (BookGenre link : requested) {
            if (currentIds.add(link.getGenre().getId())) {
                current.add(link);
            }
        }
    }

    private void reconcileAuthors(Book book, List<BookAuthor> requested) {
        List<BookAuthor> current = book.getAuthors();
        if (current == null) {
            current = new ArrayList<>();
            book.setAuthors(current);
        }

        Set<Long> requestedIds = new HashSet<>();
        for (BookAuthor link : requested) {
            requestedIds.add(link.getAuthor().getId());
        }

        current.removeIf(link -> {
            boolean removed = !requestedIds.contains(link.getAuthor().getId());
            if (removed) {
                link.setDeleted(true);
            }
            return removed;
        });

        Set<Long> currentIds = new HashSet<>();
        for (BookAuthor link : current) {
            currentIds.add(link.getAuthor().getId());
        }

        for (BookAuthor link : requested) {
            if (currentIds.add(link.getAuthor().getId())) {
                current.add(link);
            }
        }
    }

    private List<BookGenre> resolveGenres(Book book, List<Long> genreIds) {
        List<BookGenre> links = new ArrayList<>();
        if (genreIds == null || genreIds.isEmpty()) {
            return links;
        }
        for (Genre genre : genreRepository.findAllById(genreIds)) {
            BookGenre link = new BookGenre();
            link.setBook(book);
            link.setGenre(genre);
            links.add(link);
        }
        return links;
    }

    private List<BookAuthor> resolveAuthors(Book book, List<Long> authorIds) {
        List<BookAuthor> links = new ArrayList<>();
        if (authorIds == null || authorIds.isEmpty()) {
            return links;
        }
        for (Author author : authorRepository.findAllById(authorIds)) {
            BookAuthor link = new BookAuthor();
            link.setBook(book);
            link.setAuthor(author);
            links.add(link);
        }
        return links;
    }
}
