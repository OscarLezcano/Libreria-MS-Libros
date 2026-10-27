package com.bigobooks.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bigobooks.dto.StockAdjustmentDetailDto;
import com.bigobooks.dto.StockAdjustmentDetailRequestDto;
import com.bigobooks.dto.StockAdjustmentDto;
import com.bigobooks.dto.StockAdjustmentRequestDto;
import com.bigobooks.entities.book.Book;
import com.bigobooks.entities.stock.StockAdjustment;
import com.bigobooks.entities.stock.StockAdjustmentDetail;
import com.bigobooks.entities.stock.Warehouse;
import com.bigobooks.mappers.StockAdjustmentDetailMapper;
import com.bigobooks.mappers.StockAdjustmentMapper;
import com.bigobooks.repositories.BookRepository;
import com.bigobooks.repositories.StockAdjustmentDetailRepository;
import com.bigobooks.repositories.StockAdjustmentRepository;
import com.bigobooks.repositories.WarehouseRepository;
import com.bigobooks.specifications.StockAdjustmentSpecifications;

@Service
public class StockAdjustmentService
        extends AbstractCrudService<StockAdjustment, StockAdjustmentDto, StockAdjustmentRepository> {

    private final StockAdjustmentDetailRepository stockAdjustmentDetailRepository;
    private final StockService stockService;
    private final WarehouseRepository warehouseRepository;
    private final BookRepository bookRepository;
    private final StockAdjustmentMapper stockAdjustmentMapper;
    private final StockAdjustmentDetailMapper stockAdjustmentDetailMapper;
    private final StockAdjustmentSpecifications stockAdjustmentSpecifications;

    public StockAdjustmentService(StockAdjustmentRepository stockAdjustmentRepository,
            StockAdjustmentDetailRepository stockAdjustmentDetailRepository, StockService stockService,
            WarehouseRepository warehouseRepository, BookRepository bookRepository,
            StockAdjustmentMapper stockAdjustmentMapper,
            StockAdjustmentDetailMapper stockAdjustmentDetailMapper,
            StockAdjustmentSpecifications stockAdjustmentSpecifications) {
        super(stockAdjustmentRepository);
        this.stockAdjustmentDetailRepository = stockAdjustmentDetailRepository;
        this.stockService = stockService;
        this.warehouseRepository = warehouseRepository;
        this.bookRepository = bookRepository;
        this.stockAdjustmentMapper = stockAdjustmentMapper;
        this.stockAdjustmentDetailMapper = stockAdjustmentDetailMapper;
        this.stockAdjustmentSpecifications = stockAdjustmentSpecifications;
    }

    @Override
    protected Function<StockAdjustment, StockAdjustmentDto> toDtoMapper() {
        return stockAdjustmentMapper::toDto;
    }

    @Transactional(readOnly = true)
    public Page<StockAdjustmentDto> getByWarehouse(Long warehouseId, Pageable pageable) {
        return repository.findByWarehouseId(warehouseId, pageable).map(stockAdjustmentMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<StockAdjustmentDto> getByBook(Long bookId, Pageable pageable) {
        return repository.findByBookId(bookId, pageable).map(stockAdjustmentMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<List<StockAdjustmentDetailDto>> getDetails(Long id) {
        if (!repository.existsById(id)) {
            return Optional.empty();
        }
        return Optional.of(stockAdjustmentDetailMapper
                .toDtoList(stockAdjustmentDetailRepository.findByStockAdjustmentId(id)));
    }

    @Transactional
    public Optional<StockAdjustmentDto> create(Long warehouseId, StockAdjustmentRequestDto dto) {
        stockAdjustmentSpecifications.validateCreate(dto);
        Optional<Warehouse> warehouse = warehouseRepository.findById(warehouseId);
        if (warehouse.isEmpty()) {
            return Optional.empty();
        }

        StockAdjustment adjustment = new StockAdjustment();
        adjustment.setNote(dto.getNote());
        adjustment.setWarehouse(warehouse.get());
        adjustment.setDetails(toDetails(dto.getDetails()));

        StockAdjustment saved = repository.save(adjustment);
        saved.getDetails().forEach(detail -> {
            prepareDetail(detail, saved, warehouse.get().getId());
            stockAdjustmentDetailRepository.save(detail);
        });
        applyPhysicalQuantities(saved, warehouse.get().getId());

        return Optional.of(stockAdjustmentMapper.toDto(saved));
    }

    private List<StockAdjustmentDetail> toDetails(List<StockAdjustmentDetailRequestDto> details) {
        List<StockAdjustmentDetail> entities = new ArrayList<>();
        for (StockAdjustmentDetailRequestDto detail : details) {
            StockAdjustmentDetail entity = new StockAdjustmentDetail();
            entity.setBook(bookRepository.getReferenceById(detail.getBookId()));
            entity.setPhysicalQuantity(detail.getPhysicalQuantity());
            entities.add(entity);
        }
        return entities;
    }

    @SuppressWarnings("null")
    private void prepareDetail(StockAdjustmentDetail detail, StockAdjustment adjustment, Long warehouseId) {
        detail.setStockAdjustment(adjustment);
        Book book = detail.getBook();
        if (book == null) {
            throw new IllegalArgumentException("El libro no puede ser nulo");
        }
        detail.setBookName(book == null ? null : book.getTitle());
        detail.setSystemQuantity(stockService.totalQuantity(warehouseId, book.getId()));
    }

    private void applyPhysicalQuantities(StockAdjustment adjustment, Long warehouseId) {
        adjustment.getDetails()
                .forEach(detail -> stockService.setQuantity(warehouseId, detail.getBook().getId(),
                        detail.getPhysicalQuantity()));
    }
}
