package io.davlac.checkoutsystem.productdeal.controller.validator;

import io.davlac.checkoutsystem.product.repository.ProductRepository;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ProductExistsValidator implements ConstraintValidator<ProductExists, Long> {

    private final ProductRepository productRepository;

    public ProductExistsValidator(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public boolean isValid(final Long productId, ConstraintValidatorContext context) {
        if(productId == null) {
            return false;
        }

        return productRepository.findById(productId).isPresent();
    }
}
