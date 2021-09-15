package io.davlac.checkoutsystem.basket.controller;

import io.davlac.checkoutsystem.basket.service.BasketProductService;
import io.davlac.checkoutsystem.basket.service.dto.AddBasketProductRequest;
import io.davlac.checkoutsystem.basket.service.dto.BasketProductResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/basket-products")
@Validated
@RequiredArgsConstructor
public class BasketProductController {

    private final BasketProductService basketProductService;

    @PostMapping("add")
    @Operation(description = "Add product to basket")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<BasketProductResponse> addProduct(
            @RequestBody @Valid AddBasketProductRequest request
    ) {
        return ResponseEntity.ok(basketProductService.addProduct(request));
    }

}
