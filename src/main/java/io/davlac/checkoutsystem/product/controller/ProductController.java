package io.davlac.checkoutsystem.product.controller;

import io.davlac.checkoutsystem.product.service.ProductService;
import io.davlac.checkoutsystem.product.service.dto.CreateProductRequest;
import io.davlac.checkoutsystem.product.service.dto.PatchProductRequest;
import io.davlac.checkoutsystem.product.service.dto.ProductResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping(value = "/products")
@Validated
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @Operation(description = "Create product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ProductResponse> create(
            @RequestBody @Valid CreateProductRequest createProductRequest
    ) {
        ProductResponse response = productService.create(createProductRequest);
        return ResponseEntity
                .created(URI.create("/products/" + response.getId()))
                .body(response);
    }

    @GetMapping("{id}")
    @Operation(description = "Get product by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ProductResponse> getById(@PathVariable long id) {
        return ResponseEntity.ok(productService.getById(id));
    }

    @DeleteMapping("{id}")
    @Operation(description = "Delete product by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product deleted"),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteById(@PathVariable long id) {
        productService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("{id}")
    @Operation(description = "Update product by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ProductResponse> patchById(
            @PathVariable long id,
            @RequestBody @Valid PatchProductRequest patchProductRequest) {
        return ResponseEntity.ok(productService.patchById(id, patchProductRequest));
    }

}
