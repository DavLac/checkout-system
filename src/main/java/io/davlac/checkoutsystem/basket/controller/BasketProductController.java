package io.davlac.checkoutsystem.basket.controller;

import io.davlac.checkoutsystem.basket.service.BasketProductService;
import io.davlac.checkoutsystem.basket.service.CalculationBasketProductService;
import io.davlac.checkoutsystem.basket.service.dto.AddBasketProductRequest;
import io.davlac.checkoutsystem.basket.service.dto.BasketProductResponse;
import io.davlac.checkoutsystem.basket.service.dto.TotalBasketProductResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping(value = "/basket-products")
@Validated
@RequiredArgsConstructor
public class BasketProductController {

    private final BasketProductService basketProductService;
    private final CalculationBasketProductService calculationBasketProductService;

    @PostMapping("add")
    @Operation(description = "Add product to basket")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<BasketProductResponse> addProduct(
            @RequestBody @Valid AddBasketProductRequest request
    ) {
        return ResponseEntity.ok(basketProductService.addProduct(request));
    }

    @PatchMapping("products/{productId}")
    @Operation(description = "Amend basket product by product ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Product or product basket not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<BasketProductResponse> patchByProductId(
            @PathVariable long productId,
            @RequestParam @Positive int quantity
    ) {
        return ResponseEntity.ok(basketProductService.patchByProductId(productId, quantity));
    }

    @DeleteMapping("products/{productId}")
    @Operation(description = "Delete basket product by product ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product basket deleted"),
            @ApiResponse(responseCode = "404", description = "Product or product basket not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteByProductId(@PathVariable long productId) {
        basketProductService.deleteByProductId(productId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("calculate-total")
    @Operation(description = "Calculate total price and products taking into account all discounts and bundles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product basket deleted"),
            @ApiResponse(responseCode = "404", description = "Product or product basket not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<TotalBasketProductResponse> calculateTotal() {
        return ResponseEntity.ok(calculationBasketProductService.calculateTotalPrice());
    }

}
