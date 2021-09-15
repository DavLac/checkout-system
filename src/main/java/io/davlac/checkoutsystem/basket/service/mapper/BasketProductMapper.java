package io.davlac.checkoutsystem.basket.service.mapper;

import io.davlac.checkoutsystem.basket.model.BasketProduct;
import io.davlac.checkoutsystem.basket.service.dto.BasketProductResponse;
import io.davlac.checkoutsystem.product.model.Product;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface BasketProductMapper {

    BasketProductResponse toDto(BasketProduct entity);

    @Mapping(target = "lastModifiedDate", ignore = true)
    void updateEntity(Product product, int quantity, @MappingTarget BasketProduct entity);

}
