package io.davlac.checkoutsystem.basket.service;

import io.davlac.checkoutsystem.basket.model.BasketProduct;
import io.davlac.checkoutsystem.basket.repository.BasketProductRepository;
import io.davlac.checkoutsystem.basket.service.dto.AddBasketProductRequest;
import io.davlac.checkoutsystem.basket.service.dto.BasketProductResponse;
import io.davlac.checkoutsystem.basket.service.mapper.BasketProductMapper;
import io.davlac.checkoutsystem.product.model.Product;
import io.davlac.checkoutsystem.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BasketProductService {

    private final ProductService productService;
    private final BasketProductRepository basketProductRepository;
    private final BasketProductMapper basketProductMapper;

    @Transactional
    public BasketProductResponse addProduct(@NotNull final AddBasketProductRequest request) {
        Product product = productService.getEntityById(request.getProductId());
        Optional<BasketProduct> basketProductOpt = basketProductRepository.findByProduct(product);
        BasketProduct basketProductToSave = createOrUpdateBasketProduct(basketProductOpt, product, request.getQuantity());
        BasketProduct basketProductSaved = basketProductRepository.save(basketProductToSave);
        return basketProductMapper.toDto(basketProductSaved);
    }

    @Transactional
    public BasketProductResponse patchByProductId(final long productId, final int quantity) {
        BasketProductResponse response = new BasketProductResponse();
        response.setProductId(productId);
        response.setQuantity(quantity);
        return response;
    }

    private static BasketProduct createOrUpdateBasketProduct(Optional<BasketProduct> basketProductOpt,
                                                             Product product,
                                                             int quantity) {
        if (basketProductOpt.isEmpty()) {
            return new BasketProduct(product, quantity);
        }

        basketProductOpt.get().setQuantity(basketProductOpt.get().getQuantity() + quantity);
        return basketProductOpt.get();
    }

}
