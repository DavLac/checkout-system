package io.davlac.checkoutsystem.product.controller;

import io.davlac.checkoutsystem.product.model.CreateProductRequest;
import io.davlac.checkoutsystem.product.model.CreateProductResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping(value = "/products")
public class ProductController {

    @PostMapping
    @Operation(description = "Create a product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<CreateProductResponse> create(@RequestBody CreateProductRequest createProductRequest) {
        return ResponseEntity
                .created(URI.create("/products/" + 1L))
                .body(new CreateProductResponse(1L, createProductRequest.getName()));
    }

    @DeleteMapping("{id}")
    @Operation(description = "Delete a product by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product deleted"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteById(@PathVariable long id) {
        return ResponseEntity.noContent().build();
    }

}
