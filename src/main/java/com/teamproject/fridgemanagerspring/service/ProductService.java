package com.teamproject.fridgemanagerspring.service;

import com.teamproject.fridgemanagerspring.domain.category.Category;
import com.teamproject.fridgemanagerspring.domain.enums.ProductStatus;
import com.teamproject.fridgemanagerspring.domain.fridge.Fridge;
import com.teamproject.fridgemanagerspring.domain.product.Product;
import com.teamproject.fridgemanagerspring.dto.product.request.ProductRequest;
import com.teamproject.fridgemanagerspring.repository.CategoryRepository;
import com.teamproject.fridgemanagerspring.repository.FridgeRepository;
import com.teamproject.fridgemanagerspring.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final FridgeRepository fridgeRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<Product> getProductList(Long userId, Long fridgeId) {
        // 프론트엔드 필터링을 위해 STORED 상태인 전체 제품을 반환합니다.
        return productRepository.findProductsByFridgeIdAndStatus(fridgeId, userId, ProductStatus.STORED);
    }

    @Transactional(readOnly = true)
    public Product getProductById(Long userId, Long productId) {
        return productRepository.findByIdAndUserId(productId, userId)
                .orElseThrow(() -> new RuntimeException("PRODUCT_NOT_FOUND"));
    }

    @Transactional
    public Product createProduct(Long userId, Long fridgeId, ProductRequest request) {
        Fridge fridge = fridgeRepository.findByIdAndUserIdAndDeletedAtIsNull(fridgeId, userId)
                .orElseThrow(() -> new RuntimeException("UNAUTHORIZED_ACCESS"));

        Category category = categoryRepository.findByIdAndDeletedAtIsNull(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("CATEGORY_NOT_FOUND"));

        Product product = Product.builder()
                .name(request.getName())
                .memo(request.getMemo())
                .storageType(request.getStorageType())
                .quantity(request.getQuantity())
                .unit(request.getUnit())
                .price(request.getPrice())
                .expirationDate(request.getExpirationDate())
                .status(request.getStatus()) // null일 경우 엔티티 내에서 STORED 처리됨
                .fridge(fridge)
                .category(category)
                .build();

        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(Long userId, Long productId, ProductRequest request) {
        Product product = getProductById(userId, productId);

        Category category = categoryRepository.findByIdAndDeletedAtIsNull(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("CATEGORY_NOT_FOUND"));

        // 거대한 하나의 update 대신 개별 비즈니스 메서드를 호출하여 변경 의도를 명확히 합니다.
        product.updateName(request.getName());
        product.updateMemo(request.getMemo());
        product.updateStorageType(request.getStorageType());
        product.updateQuantity(request.getQuantity());
        product.updateUnit(request.getUnit());
        product.updatePrice(request.getPrice());
        product.updateExpirationDate(request.getExpirationDate());

        if (request.getStatus() != null) {
            product.updateStatus(request.getStatus());
        }
        product.changeCategory(category);

        return product;
    }

    @Transactional
    public void deleteProduct(Long userId, Long productId) {
        Product product = getProductById(userId, productId);
        productRepository.delete(product); // 하드 삭제 처리
    }
}