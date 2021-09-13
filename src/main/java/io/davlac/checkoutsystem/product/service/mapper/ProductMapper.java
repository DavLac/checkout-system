package io.davlac.checkoutsystem.product.service.mapper;

import io.davlac.checkoutsystem.product.model.Product;
import io.davlac.checkoutsystem.product.service.dto.CreateProductRequest;
import io.davlac.checkoutsystem.product.service.dto.ProductResponse;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface ProductMapper {

    Product toEntity(CreateProductRequest dto);

    ProductResponse toDto(Product entity);

}
