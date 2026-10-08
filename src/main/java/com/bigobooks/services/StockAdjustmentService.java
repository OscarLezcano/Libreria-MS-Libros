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
import com.bigobooks.dto.StockAdjustmentDto;
import com.bigobooks.entities.book.Book;
import com.bigobooks.entities.stock.StockAdjustment;
import com.bigobooks.entities.stock.StockAdjustmentDetail;
import com.bigobooks.entities.stock.Warehouse;
import com.bigobooks.mappers.StockAdjustmentMapper;
import com.bigobooks.repositories.BookRepository;
import com.bigobooks.repositories.StockAdjustmentDetailRepository;
import com.bigobooks.repositories.StockAdjustmentRepository;
import com.bigobooks.repositories.WarehouseRepository;

@Service
public class StockAdjustmentService
        extends AbstractCrudService<StockAdjustment, StockAdjustmentDto, StockAdjustmentRepository> {

    private final StockAdjustmentDetailRepository stockAdjustmentDetailRepository;
    private final StockService stockService;
    private final WarehouseRepository warehouseRepository;
    private final BookRepository bookRepository;
    private final StockAdjustmentMapper stockAdjustmentMapper;

    public StockAdjustmentService(StockAdjustmentRepository stockAdjustmentRepository,
            StockAdjustmentDetailRepository stockAdjustmentDetailRepository, StockService stockService,
            WarehouseRepository warehouseRepository, BookRepository bookRepository,
            StockAdjustmentMapper stockAdjustmentMapper) {
        super(stockAdjustmentRepository);
        this.stockAdjustmentDetailRepository = stockAdjustmentDetailRepository;
        this.stockService = stockService;
        this.warehouseRepository = warehouseRepository;
        this.bookRepository = bookRepository;
        this.stockAdjustmentMapper = stockAdjustmentMapper;
    }

    @Override
    protected Function<StockAdjustment, StockAdjustmentDto> toDtoMapper() {
        return stockAdjustmentMapper::toDto;
    }

    @Transactional(readOnly = true)
    public Page<StockAdjustmentDto> getByWarehouse(Long warehouseId, Pageable pageable) {
        return repository.findByWarehouse_Id(warehouseId, pageable).map(stockAdjustmentMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<StockAdjustmentDto> getByBook(Long bookId, Pageable pageable) {
        return repository.findByDetails_Book(bookId, pageable).map(stockAdjustmentMapper::toDto);
    }

    @Transactional
    public Optional<StockAdjustmentDto> create(Long warehouseId, StockAdjustmentDto dto) {
        Optional<Warehouse> warehouse = warehouseRepository.findById(warehouseId);
        if (warehouse.isEmpty() || dto.getDetails() == null || dto.getDetails().isEmpty()) {
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

    private List<StockAdjustmentDetail> toDetails(List<StockAdjustmentDetailDto> details) {
        List<StockAdjustmentDetail> entities = new ArrayList<>();
        for (StockAdjustmentDetailDto detail : details) {
            StockAdjustmentDetail entity = new StockAdjustmentDetail();
            entity.setBook(detail.getBook());
            entity.setPhysicalQuantity(detail.getPhysicalQuantity());
            entities.add(entity);
        }
        return entities;
    }

    private void prepareDetail(StockAdjustmentDetail detail, StockAdjustment adjustment, Long warehouseId) {
        detail.setStockAdjustment(adjustment);
        Book book = bookRepository.findById(detail.getBook()).orElse(null);
        detail.setBookName(book == null ? null : book.getTitle());
        detail.setSystemQuantity(stockService.totalQuantity(warehouseId, detail.getBook()));
    }

    private void applyPhysicalQuantities(StockAdjustment adjustment, Long warehouseId) {
        adjustment.getDetails().forEach(detail -> stockService
                .setQuantity(warehouseId, detail.getBook(), detail.getPhysicalQuantity()));
    }
}
