package com.bigobooks.controller;

import org.springframework.beans.factory.annotation.Value;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.bigobooks.dto.StockDto;
import com.bigobooks.dto.StockQuantityRequestDto;
import com.bigobooks.services.StockService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class StockController implements StocksApi {

    private final StockService stockService;

    @Value("${app.pagination.page-size:25}")
    private int defaultPageSize;

    @Value("${app.pagination.max-page-size:100}")
    private int maxPageSize;

    @Override
    public ResponseEntity<List<StockDto>> getStocksByWarehouse(Long warehouseId, Integer page, Integer size) {
        return ResponseEntity.ok(stockService.getByWarehouse(warehouseId, pageable(page, size)).getContent());
    }

    @Override
    public ResponseEntity<List<StockDto>> getStocksByBook(Long bookId, Integer page, Integer size) {
        return ResponseEntity.ok(stockService.getByBook(bookId, pageable(page, size)).getContent());
    }

    @Override
    public ResponseEntity<StockDto> getStock(Long warehouseId, Long bookId) {
        return stockService.getStock(warehouseId, bookId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<StockDto> setStockQuantity(Long warehouseId, Long bookId,
            StockQuantityRequestDto stockQuantityRequestDto) {
        return stockService.changeQuantity(warehouseId, bookId, stockQuantityRequestDto.getQuantity())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private Pageable pageable(Integer page, Integer size) {
        int pageNo = page == null ? 0 : page;
        int pageSize = Math.min(size == null ? defaultPageSize : size, maxPageSize);
        return PageRequest.of(pageNo, pageSize);
    }
}
