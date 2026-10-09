package com.bigobooks.controller;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.bigobooks.dto.StockAdjustmentDto;
import com.bigobooks.dto.StockAdjustmentListResponse;
import com.bigobooks.dto.StockAdjustmentRequestDto;
import com.bigobooks.dto.StockAdjustmentResponse;
import com.bigobooks.services.StockAdjustmentService;
import com.bigobooks.util.Envelopes;
import com.bigobooks.util.Pageables;

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
    public ResponseEntity<StockAdjustmentResponse> getStockAdjustmentById(Long id) {
        return stockAdjustmentService.getById(id)
                .map(adjustment -> ResponseEntity.ok(new StockAdjustmentResponse().data(adjustment)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<StockAdjustmentListResponse> getStockAdjustmentsByWarehouse(Long warehouseId, Integer page,
            Integer size) {
        Page<StockAdjustmentDto> result = stockAdjustmentService.getByWarehouse(warehouseId, Pageables.of(page, size, defaultPageSize, maxPageSize));
        return ResponseEntity.ok(new StockAdjustmentListResponse()
                .data(result.getContent())
                .pagination(Envelopes.pagination(result)));
    }

    @Override
    public ResponseEntity<StockAdjustmentResponse> createStockAdjustment(Long warehouseId,
            StockAdjustmentRequestDto stockAdjustmentRequestDto) {
        return stockAdjustmentService.create(warehouseId, stockAdjustmentRequestDto)
                .map(adjustment -> ResponseEntity.status(HttpStatus.CREATED).body(new StockAdjustmentResponse().data(adjustment)))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }
}
