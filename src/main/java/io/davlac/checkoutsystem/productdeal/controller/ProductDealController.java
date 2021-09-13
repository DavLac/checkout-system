package io.davlac.checkoutsystem.productdeal.controller;

import io.davlac.checkoutsystem.productdeal.service.dto.request.CreateProductDealRequest;
import io.davlac.checkoutsystem.productdeal.service.dto.response.DiscountResponse;
import io.davlac.checkoutsystem.productdeal.service.dto.response.ProductDealResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;
import java.time.Instant;

@RestController
@RequestMapping(value = "/product-deals")
@Validated
public class ProductDealController {

    @PostMapping
    @Operation(description = "Create a product deal")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product deal created"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ProductDealResponse> create(
            @RequestBody @Valid CreateProductDealRequest request
    ) {
        ProductDealResponse response = new ProductDealResponse();
        response.setId(123L);
        response.setLastModifiedDate(Instant.now());
        response.setProductId(request.getProductId());
        DiscountResponse discountResponse = new DiscountResponse();
        discountResponse.setId(456L);
        discountResponse.setLastModifiedDate(Instant.now());
        discountResponse.setDiscountPercentage(request.getDiscount().getDiscountPercentage());
        discountResponse.setTotalFullPriceItems(request.getDiscount().getTotalFullPriceItems());
        discountResponse.setTotalDiscountedItems(request.getDiscount().getTotalDiscountedItems());
        response.setDiscount(discountResponse);
        return ResponseEntity
                .created(URI.create("/product-deals/" + response.getId()))
                .body(response);
    }

}
