package io.davlac.checkoutsystem.basket.service.mapper;

import io.davlac.checkoutsystem.basket.model.BasketProduct;
import io.davlac.checkoutsystem.basket.service.dto.BasketProductDetailsResponse;
import io.davlac.checkoutsystem.basket.service.dto.BasketProductResponse;
import io.davlac.checkoutsystem.product.model.Product;
import io.davlac.checkoutsystem.productdeal.service.dto.response.ProductDealResponse;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public abstract class BasketProductMapper {

    public abstract BasketProductResponse toDto(BasketProduct entity);

    @Mapping(target = "lastModifiedDate", ignore = true)
    public abstract void updateEntity(Product product, int quantity, @MappingTarget BasketProduct entity);

    @Mapping(target = "productId", source = "basketProduct.productId")
    @Mapping(target = "quantity", source = "basketProduct.quantity")
    public abstract BasketProductDetailsResponse toDetailsResponse(BasketProduct basketProduct,
                                                                   List<ProductDealResponse> productDeals);
}
