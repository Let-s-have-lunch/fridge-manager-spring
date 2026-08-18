package com.teamproject.fridgemanagerspring.domain.product;

import com.teamproject.fridgemanagerspring.domain.category.Category;
import com.teamproject.fridgemanagerspring.domain.common.BaseTimeEntity;
import com.teamproject.fridgemanagerspring.domain.enums.ProductStatus;
import com.teamproject.fridgemanagerspring.domain.enums.StorageType;
import com.teamproject.fridgemanagerspring.domain.enums.Unit;
import com.teamproject.fridgemanagerspring.domain.fridge.Fridge;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "products")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String memo;   // String? 은 @Column 생략 가능

    @Enumerated(EnumType.STRING)
    @Column(name = "storage_type", nullable = false)
    private StorageType storageType;

    @Column(nullable = false)
    private Double quantity; // Prisma의 Float은 Java의 Double로 매핑하는 것이 안전합니다.

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Unit unit;

    private Integer price;   // Int? 매핑

    @Column(name = "expiration_date", nullable = false)
    private LocalDate expirationDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status = ProductStatus.STORED;

    @ManyToOne(fetch = FetchType.LAZY)  // 지연 로딩
    @JoinColumn(name = "refrigerator_id", nullable = false) // FK 컬럼명
    private Fridge fridge;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Builder
    public Product(
            String name,
            String memo,
            StorageType storageType,
            Double quantity,
            Unit unit,
            Integer price,
            LocalDate expirationDate,
            ProductStatus status,
            Fridge fridge,
            Category category
    ) {
        this.name = name;
        this.memo = memo;
        this.storageType = storageType;
        this.quantity = quantity;
        this.unit = unit;
        this.price = price;
        this.expirationDate = expirationDate;
        // status가 null로 들어올 경우 기본값(STORED) 유지
        this.status = (status != null) ? status : ProductStatus.STORED;
        this.fridge = fridge;
        this.category = category;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateMemo(String memo) {
        this.memo = memo;
    }

    public void updateStorageType(StorageType storageType) {
        this.storageType = storageType;
    }

    public void updateQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public void updateUnit(Unit unit) {
        this.unit = unit;
    }

    public void updatePrice(Integer price) {
        this.price = price;
    }

    public void updateExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public void updateStatus(ProductStatus status) {
        this.status = status;
    }

    public void changeCategory(Category category) {
        this.category = category;
    }
}