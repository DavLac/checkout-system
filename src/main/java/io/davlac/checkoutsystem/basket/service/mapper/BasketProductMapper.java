package io.davlac.checkoutsystem.basket.service.mapper;

import io.davlac.checkoutsystem.basket.model.BasketProduct;
import io.davlac.checkoutsystem.basket.service.dto.BasketProductResponse;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface BasketProductMapper {

    BasketProductResponse toDto(BasketProduct entity);

}
