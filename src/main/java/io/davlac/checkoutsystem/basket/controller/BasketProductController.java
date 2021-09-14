package io.davlac.checkoutsystem.basket.controller;

import io.davlac.checkoutsystem.basket.service.dto.AddBasketProductRequest;
import io.davlac.checkoutsystem.basket.service.dto.BasketProductResponse;
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
import java.time.Instant;

@RestController
@RequestMapping(value = "/basket-products")
@Validated
public class BasketProductController {

    @PostMapping("add")
    @Operation(description = "Add product to basket")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<BasketProductResponse> addProduct(
            @RequestBody @Valid AddBasketProductRequest request
    ) {
        BasketProductResponse response = new BasketProductResponse();
        response.setProductId(request.getProductId());
        response.setQuantity(request.getQuantity());
        response.setLastModifiedDate(Instant.now());
        return ResponseEntity.ok(response);
    }

}
