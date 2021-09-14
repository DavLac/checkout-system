package io.davlac.checkoutsystem.productdeal.service.mapper;

import io.davlac.checkoutsystem.product.model.Product;
import io.davlac.checkoutsystem.product.service.ProductService;
import io.davlac.checkoutsystem.productdeal.model.ProductDeal;
import io.davlac.checkoutsystem.productdeal.service.dto.request.CreateProductDealRequest;
import io.davlac.checkoutsystem.productdeal.service.dto.response.ProductDealResponse;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public abstract class ProductDealMapper {

    @Autowired
    ProductService productService;

    @Mapping(target = "product", source = "dto.productId", qualifiedByName = "productIdToProduct")
    public abstract ProductDeal toEntity(CreateProductDealRequest dto);

    public abstract ProductDealResponse toDto(ProductDeal entity);

    @Named("productIdToProduct")
    public Product productIdToProduct(Long productId) {
        return productService.getEntityById(productId);
    }

}
