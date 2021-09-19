package io.davlac.checkoutsystem.basket.service;

import io.davlac.checkoutsystem.basket.service.dto.BasketProductDetailsResponse;
import io.davlac.checkoutsystem.productdeal.service.dto.response.BundleResponse;
import io.davlac.checkoutsystem.productdeal.service.dto.response.ProductDealResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class BundleCalculationBasketProductService extends AbstractCalculationBasketProductService {

    public static void applyBundles(List<BasketProductDetailsResponse> productDetails) {
        initializeBundleTotalPrices(productDetails);

        productDetails.forEach(productDetail -> {
            if (CollectionUtils.isEmpty(productDetail.getProductDeals())) {
                return;
            }

            Optional<Set<BundleResponse>> bundlesOpt = getBundleFromProductDetail(productDetail);

            if (bundlesOpt.isEmpty()) {
                return;
            }

            Set<BundleResponse> bundles = bundlesOpt.get();

            // check bundle trigger
            int nbBundleToApply = calculateNumberBundleToApply(productDetails, productDetail, bundles);

            if (nbBundleToApply > 0) {
                // apply bundles on each product
                calculateBundleTotalPriceForEachProduct(productDetails, bundles, nbBundleToApply);
            }
        });
    }

    private static int calculateNumberBundleToApply(List<BasketProductDetailsResponse> allProductDetails,
                                                    BasketProductDetailsResponse currentProductDetail,
                                                    Set<BundleResponse> bundles) {
        int nbBundleToApply = currentProductDetail.getQuantity();

        for (BundleResponse bundle : bundles) {
            int productQuantity = allProductDetails.stream()
                    .filter(det -> det.getProductId() == bundle.getProductId())
                    .map(BasketProductDetailsResponse::getQuantity)
                    .findFirst()
                    .orElse(0);

            if (productQuantity < nbBundleToApply) {
                nbBundleToApply = productQuantity;
            }
        }

        return nbBundleToApply;
    }

    private static void calculateBundleTotalPriceForEachProduct(List<BasketProductDetailsResponse> allProductDetails,
                                                                Set<BundleResponse> bundles,
                                                                int nbBundleToApply) {
        for (BundleResponse bundle : bundles) {
            for (BasketProductDetailsResponse productDetail : allProductDetails) {
                if (productDetail.getProductId() == bundle.getProductId()) {
                    int nbProductsFullPrice = productDetail.getQuantity() - nbBundleToApply;
                    productDetail.setProductTotalPriceAfterBundle(
                            calculateDealsTotalPrice(productDetail.getProductPrice(),
                                    nbProductsFullPrice, nbBundleToApply, bundle.getDiscountPercentage())
                    );
                }
            }
        }
    }

    private static Optional<Set<BundleResponse>> getBundleFromProductDetail(BasketProductDetailsResponse productDetail) {
        return productDetail.getProductDeals().stream()
                .map(ProductDealResponse::getBundles)
                .filter(bundles -> !CollectionUtils.isEmpty(bundles))
                .findFirst();
    }

    private static void initializeBundleTotalPrices(List<BasketProductDetailsResponse> productDetails) {
        // init bundle : set full price for all products
        productDetails.forEach(det ->
                det.setProductTotalPriceAfterBundle(calculateProductPrice(det.getProductPrice(), det.getQuantity())));
    }
}
