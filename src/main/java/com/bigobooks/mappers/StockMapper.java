package com.bigobooks.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.bigobooks.dto.StockDto;
import com.bigobooks.entities.stock.Stock;

@Mapper(componentModel = "spring")
public interface StockMapper {

    @Mapping(source = "warehouse.id", target = "warehouseId")
    StockDto toDto(Stock stock);

    List<StockDto> toDtoList(List<Stock> stocks);
}