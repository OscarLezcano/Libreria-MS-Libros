package com.bigobooks.services;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bigobooks.dto.StockDto;
import com.bigobooks.entities.stock.Stock;
import com.bigobooks.entities.stock.Warehouse;
import com.bigobooks.mappers.StockMapper;
import com.bigobooks.repositories.StockRepository;
import com.bigobooks.repositories.WarehouseRepository;
import com.bigobooks.service.BaseService;

@Service
public class StockService extends BaseService<Stock, StockRepository> {

    private final WarehouseRepository warehouseRepository;
    private final StockMapper stockMapper;

    public StockService(StockRepository stockRepository, WarehouseRepository warehouseRepository,
            StockMapper stockMapper) {
        super(stockRepository);
        this.warehouseRepository = warehouseRepository;
        this.stockMapper = stockMapper;
    }

    @Transactional(readOnly = true)
    public Page<StockDto> getByWarehouse(Long warehouseId, Pageable pageable) {
        return getRepository().findByWarehouse_Id(warehouseId, pageable).map(stockMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<StockDto> getByBook(Long bookId, Pageable pageable) {
        return getRepository().findByBookId(bookId, pageable).map(stockMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<StockDto> getById(Long id) {
        return findById(id).map(stockMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<StockDto> getStock(Long warehouseId, Long bookId) {
        return getRepository().findByBookIdAndWarehouse_Id(bookId, warehouseId).map(stockMapper::toDto);
    }

    @Transactional(readOnly = true)
    public int totalQuantityByBook(Long bookId) {
        return getRepository().sumQuantityByBookId(bookId);
    }

    @Transactional(readOnly = true)
    public int totalQuantity(Long warehouseId, Long bookId) {
        return getRepository().sumQuantityByBookIdAndWarehouseId(bookId, warehouseId);
    }

    @Transactional
    public Optional<StockDto> setQuantity(Long warehouseId, Long bookId, int quantity) {
        Optional<Warehouse> warehouse = warehouseRepository.findById(warehouseId);
        if (warehouse.isEmpty()) {
            return Optional.empty();
        }
        Stock stock = getRepository().findForUpdateByBookIdAndWarehouseId(bookId, warehouseId).orElseGet(Stock::new);
        if (stock.getId() == null) {
            stock.setBookId(bookId);
            stock.setWarehouse(warehouse.get());
        }
        stock.setQuantity(quantity);
        return Optional.of(stockMapper.toDto(save(stock)));
    }

    @Transactional
    public Optional<StockDto> changeQuantity(Long warehouseId, Long bookId, int delta) {
        Optional<Warehouse> warehouse = warehouseRepository.findById(warehouseId);
        if (warehouse.isEmpty()) {
            return Optional.empty();
        }
        Stock stock = getRepository().findForUpdateByBookIdAndWarehouseId(bookId, warehouseId).orElseGet(Stock::new);
        if (stock.getId() == null) {
            stock.setBookId(bookId);
            stock.setWarehouse(warehouse.get());
            stock.setQuantity(delta);
        } else {
            stock.setQuantity(stock.getQuantity() + delta);
        }
        return Optional.of(stockMapper.toDto(save(stock)));
    }

    @Transactional
    public Optional<StockDto> increase(Long warehouseId, Long bookId, int quantity) {
        return getRepository().findForUpdateByBookIdAndWarehouseId(bookId, warehouseId).map(stock -> {
            stock.setQuantity(stock.getQuantity() + quantity);
            return stockMapper.toDto(save(stock));
        });
    }

    @Transactional
    public Optional<StockDto> decrease(Long warehouseId, Long bookId, int quantity) {
        return getRepository().findForUpdateByBookIdAndWarehouseId(bookId, warehouseId).map(stock -> {
            stock.setQuantity(stock.getQuantity() - quantity);
            return stockMapper.toDto(save(stock));
        });
    }
}
