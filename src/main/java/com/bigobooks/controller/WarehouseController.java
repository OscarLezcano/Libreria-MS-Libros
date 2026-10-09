package com.bigobooks.controller;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.bigobooks.dto.WarehouseDto;
import com.bigobooks.dto.WarehouseListResponse;
import com.bigobooks.dto.WarehouseRequestDto;
import com.bigobooks.dto.WarehouseResponse;
import com.bigobooks.services.WarehouseService;
import com.bigobooks.util.Envelopes;
import com.bigobooks.util.Pageables;

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
    public ResponseEntity<WarehouseListResponse> getWarehouses(String city, Integer page, Integer size) {
        Page<WarehouseDto> result = warehouseService.findByCity(city, Pageables.of(page, size, defaultPageSize, maxPageSize));
        return ResponseEntity.ok(new WarehouseListResponse()
                .data(result.getContent())
                .pagination(Envelopes.pagination(result)));
    }

    @Override
    public ResponseEntity<WarehouseResponse> getWarehouseById(Long id) {
        return warehouseService.getById(id)
                .map(warehouse -> ResponseEntity.ok(new WarehouseResponse().data(warehouse)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<WarehouseResponse> createWarehouse(WarehouseRequestDto warehouseRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new WarehouseResponse().data(warehouseService.create(warehouseRequestDto)));
    }

    @Override
    public ResponseEntity<WarehouseResponse> updateWarehouse(Long id, WarehouseRequestDto warehouseRequestDto) {
        return warehouseService.update(id, warehouseRequestDto)
                .map(warehouse -> ResponseEntity.ok(new WarehouseResponse().data(warehouse)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Void> deleteWarehouse(Long id) {
        warehouseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
