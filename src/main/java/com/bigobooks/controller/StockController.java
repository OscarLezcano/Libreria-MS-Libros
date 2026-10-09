package com.bigobooks.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.bigobooks.dto.StockDto;
import com.bigobooks.dto.StockListResponse;
import com.bigobooks.dto.StockResponse;
import com.bigobooks.dto.StockQuantityRequestDto;
import com.bigobooks.services.StockService;
import com.bigobooks.util.Envelopes;
import com.bigobooks.util.Pageables;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class StockController implements StocksApi {

    private final StockService stockService;
    private final Pageables pageables;

    @Override
    public ResponseEntity<StockListResponse> getStocksByWarehouse(Long warehouseId, Integer page, Integer size) {
        Page<StockDto> result = stockService.getByWarehouse(warehouseId, pageables.of(page, size));
        return ResponseEntity.ok(new StockListResponse()
                .data(result.getContent())
                .pagination(Envelopes.pagination(result)));
    }

    @Override
    public ResponseEntity<StockListResponse> getStocksByBook(Long bookId, Integer page, Integer size) {
        Page<StockDto> result = stockService.getByBook(bookId, pageables.of(page, size));
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
}
