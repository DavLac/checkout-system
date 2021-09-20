package io.davlac.checkoutsystem.productdeal.service.mapper;

import io.davlac.checkoutsystem.product.model.Product;
import io.davlac.checkoutsystem.productdeal.model.Bundle;
import io.davlac.checkoutsystem.productdeal.model.ProductDeal;
import io.davlac.checkoutsystem.productdeal.service.dto.request.BundleRequest;
import io.davlac.checkoutsystem.productdeal.service.dto.request.CreateProductDealRequest;
import io.davlac.checkoutsystem.productdeal.service.dto.response.BundleResponse;
import io.davlac.checkoutsystem.productdeal.service.dto.response.ProductDealResponse;
import org.mapstruct.AfterMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.util.CollectionUtils;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface ProductDealMapper {

    @Mapping(target = "product", source = "dto.productId", qualifiedByName = "productIdToProduct")
    ProductDeal toEntity(CreateProductDealRequest dto);

    @Mapping(target = "product", source = "dto.productId", qualifiedByName = "productIdToProduct")
    Bundle toEntity(BundleRequest dto);

    @Mapping(target = "productId", source = "entity.product", qualifiedByName = "productToProductId")
    ProductDealResponse toDto(ProductDeal entity);

    @Mapping(target = "productId", source = "entity.product", qualifiedByName = "productToProductId")
    BundleResponse toDto(Bundle entity);

    @Named("productIdToProduct")
    static Product productIdToProduct(Long productId) {
        return new Product(productId);
    }

    @Named("productToProductId")
    static Long productToProductId(Product product) {
        return product.getId();
    }

    @AfterMapping
    static void setBundleProductDeal(@MappingTarget ProductDeal productDeal) {
        if (!CollectionUtils.isEmpty(productDeal.getBundles())) {
            productDeal.getBundles().forEach(bundle -> bundle.setProductDeal(productDeal));
        }
    }

}
