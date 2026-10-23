package com.bigobooks.services;

import java.util.Optional;
import java.util.function.Function;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
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
import com.bigobooks.specifications.WarehouseSpecifications;

@Service
public class WarehouseService extends AbstractCrudService<Warehouse, WarehouseDto, WarehouseRepository> {

    private final WarehouseMapper warehouseMapper;
    private final WarehouseSpecifications warehouseSpecifications;
    private final CacheManager cacheManager;

    public WarehouseService(WarehouseRepository warehouseRepository, WarehouseMapper warehouseMapper,
            WarehouseSpecifications warehouseSpecifications, CacheManager cacheManager) {
        super(warehouseRepository);
        this.warehouseMapper = warehouseMapper;
        this.warehouseSpecifications = warehouseSpecifications;
        this.cacheManager = cacheManager;
    }

    @Override
    protected Function<Warehouse, WarehouseDto> toDtoMapper() {
        return warehouseMapper::toDto;
    }

    @Transactional(readOnly = true)
    public Page<WarehouseDto> findByCity(String city, Pageable pageable) {
        if (!StringUtils.hasText(city)) {
            return getAll(pageable);
        }
        return repository.findByCity(city.trim(), pageable).map(warehouseMapper::toDto);
    }

    @Transactional
    @CachePut(value = "warehouse", key = "'warehouse_' + #result.id")
    public WarehouseDto create(WarehouseRequestDto dto) {
        warehouseSpecifications.validateCreate(dto);
        Warehouse warehouse = new Warehouse();
        warehouseMapper.update(dto, warehouse);
        Warehouse saved = repository.save(warehouse);
        return warehouseMapper.toDto(saved);
    }

    @Transactional
    @CachePut(value = "warehouse", key = "'warehouse_' + #id", condition = "#result != null && #result.isPresent()")
    public Optional<WarehouseDto> update(Long id, WarehouseRequestDto dto) {
        warehouseSpecifications.validateUpdate(dto);
        return repository.findById(id).map(warehouse -> {
            warehouseMapper.update(dto, warehouse);
            Warehouse saved = repository.save(warehouse);
            return warehouseMapper.toDto(saved);
        });
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "warehouse", key = "'warehouse_' + #id")
    public Optional<WarehouseDto> getById(Long id) {
        return super.getById(id);
    }
}
