package com.bigobooks.controller;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.bigobooks.dto.StockDto;
import com.bigobooks.dto.StockListResponse;
import com.bigobooks.dto.StockResponse;
import com.bigobooks.dto.StockQuantityRequestDto;
import com.bigobooks.services.StockService;
import com.bigobooks.util.Envelopes;

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
    public ResponseEntity<StockListResponse> getStocksByWarehouse(Long warehouseId, Integer page, Integer size) {
        Page<StockDto> result = stockService.getByWarehouse(warehouseId, pageable(page, size));
        return ResponseEntity.ok(new StockListResponse()
                .data(result.getContent())
                .pagination(Envelopes.pagination(result)));
    }

    @Override
    public ResponseEntity<StockListResponse> getStocksByBook(Long bookId, Integer page, Integer size) {
        Page<StockDto> result = stockService.getByBook(bookId, pageable(page, size));
        return ResponseEntity.ok(new StockListResponse()
                .data(result.getContent())
                .pagination(Envelopes.pagination(result)));
    }

    @Override
    public ResponseEntity<StockResponse> getStock(Long warehouseId, Long bookId) {
        return stockService.getStock(warehouseId, bookId)
                .map(stock -> ResponseEntity.ok(new StockResponse().data(stock)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<StockResponse> setStockQuantity(Long warehouseId, Long bookId,
            StockQuantityRequestDto stockQuantityRequestDto) {
        return stockService.changeQuantity(warehouseId, bookId, stockQuantityRequestDto.getQuantity())
                .map(stock -> ResponseEntity.ok(new StockResponse().data(stock)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private Pageable pageable(Integer page, Integer size) {
        int pageNo = page == null ? 0 : page;
        int pageSize = Math.min(size == null ? defaultPageSize : size, maxPageSize);
        return PageRequest.of(pageNo, pageSize);
    }
}
