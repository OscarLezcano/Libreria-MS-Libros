package com.bigobooks.controller;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.bigobooks.dto.Envelope;
import com.bigobooks.dto.WarehouseDto;
import com.bigobooks.services.WarehouseService;
import com.bigobooks.util.Envelopes;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class WarehouseController implements WarehousesApi {

    private final WarehouseService warehouseService;

    @Value("${app.pagination.page-size:25}")
    private int defaultPageSize;

    @Value("${app.pagination.max-page-size:100}")
    private int maxPageSize;

    @Override
    public ResponseEntity<Envelope> getWarehouses(String city, Integer page, Integer size) {
        Page<WarehouseDto> result = warehouseService.findByCity(city, pageable(page, size));
        return ResponseEntity.ok(Envelopes.page(result, result.getContent()));
    }

    @Override
    public ResponseEntity<Envelope> getWarehouseById(Long id) {
        return warehouseService.getById(id)
                .map(warehouse -> ResponseEntity.ok(Envelopes.single(warehouse)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Envelope> createWarehouse(WarehouseDto warehouseDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Envelopes.single(warehouseService.create(warehouseDto)));
    }

    @Override
    public ResponseEntity<Envelope> updateWarehouse(Long id, WarehouseDto warehouseDto) {
        return warehouseService.update(id, warehouseDto)
                .map(warehouse -> ResponseEntity.ok(Envelopes.single(warehouse)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Void> deleteWarehouse(Long id) {
        warehouseService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private Pageable pageable(Integer page, Integer size) {
        int pageNo = page == null ? 0 : page;
        int pageSize = Math.min(size == null ? defaultPageSize : size, maxPageSize);
        return PageRequest.of(pageNo, pageSize);
    }
}
