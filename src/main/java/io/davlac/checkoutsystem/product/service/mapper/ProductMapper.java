package io.davlac.checkoutsystem.product.service.mapper;

import io.davlac.checkoutsystem.product.model.Product;
import io.davlac.checkoutsystem.product.service.dto.CreateProductRequest;
import io.davlac.checkoutsystem.product.service.dto.PatchProductRequest;
import io.davlac.checkoutsystem.product.service.dto.ProductResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface ProductMapper {

    Product toEntity(CreateProductRequest dto);

    ProductResponse toDto(Product entity);

    @BeanMapping(
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
    )
    @Mapping(source = "request.price", target = "price")
    @Mapping(source = "request.description", target = "description")
    void updateEntity(PatchProductRequest request, @MappingTarget Product entity);

}
