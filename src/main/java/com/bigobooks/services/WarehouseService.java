package com.bigobooks.services;

import java.util.Optional;
import java.util.function.Function;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.bigobooks.dto.WarehouseDto;
import com.bigobooks.dto.WarehouseRequestDto;
import com.bigobooks.entities.stock.Warehouse;
import com.bigobooks.mappers.WarehouseMapper;
import com.bigobooks.repositories.WarehouseRepository;

@Service
public class WarehouseService extends AbstractCrudService<Warehouse, WarehouseDto, WarehouseRepository> {

    private final WarehouseMapper warehouseMapper;

    public WarehouseService(WarehouseRepository warehouseRepository, WarehouseMapper warehouseMapper) {
        super(warehouseRepository);
        this.warehouseMapper = warehouseMapper;
    }

    @Override
    protected Function<Warehouse, WarehouseDto> toDtoMapper() {
        return warehouseMapper::toDto;
    }

    @Transactional(readOnly = true)
    public Optional<WarehouseDto> findByName(String name) {
        return repository.findByName(name).map(warehouseMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<WarehouseDto> findByCity(String city, Pageable pageable) {
        if (!StringUtils.hasText(city)) {
            return getAll(pageable);
        }
        return repository.findByCity(city.trim(), pageable).map(warehouseMapper::toDto);
    }

    @Transactional
    public WarehouseDto create(WarehouseRequestDto dto) {
        Warehouse warehouse = new Warehouse();
        warehouseMapper.update(dto, warehouse);
        return warehouseMapper.toDto(repository.save(warehouse));
    }

    @Transactional
    public Optional<WarehouseDto> update(Long id, WarehouseRequestDto dto) {
        return repository.findById(id).map(warehouse -> {
            warehouseMapper.update(dto, warehouse);
            return warehouseMapper.toDto(repository.save(warehouse));
        });
    }
}
