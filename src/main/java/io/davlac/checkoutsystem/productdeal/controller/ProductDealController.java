package io.davlac.checkoutsystem.productdeal.controller;

import io.davlac.checkoutsystem.productdeal.service.ProductDealService;
import io.davlac.checkoutsystem.productdeal.service.dto.request.CreateProductDealRequest;
import io.davlac.checkoutsystem.productdeal.service.dto.response.ProductDealResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping(value = "/product-deals")
@Validated
public class ProductDealController {

    private final ProductDealService productDealService;

    public ProductDealController(ProductDealService productDealService) {
        this.productDealService = productDealService;
    }

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
        ProductDealResponse response = productDealService.create(request);
        return ResponseEntity
                .created(URI.create("/product-deals/" + response.getId()))
                .body(response);
    }

    @DeleteMapping("{id}")
    @Operation(description = "Delete product deal by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product deal deleted"),
            @ApiResponse(responseCode = "404", description = "Product deal not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteById(@PathVariable long id) {
        productDealService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
