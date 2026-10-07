package com.bigobooks.controller;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.bigobooks.dto.Envelope;
import com.bigobooks.dto.StockAdjustmentDto;
import com.bigobooks.services.StockAdjustmentService;
import com.bigobooks.util.Envelopes;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class StockAdjustmentController implements StockAdjustmentsApi {

    private final StockAdjustmentService stockAdjustmentService;

    @Value("${app.pagination.page-size:25}")
    private int defaultPageSize;

    @Value("${app.pagination.max-page-size:100}")
    private int maxPageSize;

    @Override
    public ResponseEntity<Envelope> getStockAdjustmentById(Long id) {
        return stockAdjustmentService.getById(id)
                .map(adjustment -> ResponseEntity.ok(Envelopes.single(adjustment)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Envelope> getStockAdjustmentsByWarehouse(Long warehouseId, Integer page,
            Integer size) {
        Page<StockAdjustmentDto> result = stockAdjustmentService.getByWarehouse(warehouseId, pageable(page, size));
        return ResponseEntity.ok(Envelopes.page(result, result.getContent()));
    }

    @Override
    public ResponseEntity<Envelope> createStockAdjustment(Long warehouseId, StockAdjustmentDto stockAdjustmentDto) {
        return stockAdjustmentService.create(warehouseId, stockAdjustmentDto)
                .map(adjustment -> ResponseEntity.status(HttpStatus.CREATED).body(Envelopes.single(adjustment)))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    private Pageable pageable(Integer page, Integer size) {
        int pageNo = page == null ? 0 : page;
        int pageSize = Math.min(size == null ? defaultPageSize : size, maxPageSize);
        return PageRequest.of(pageNo, pageSize);
    }
}
