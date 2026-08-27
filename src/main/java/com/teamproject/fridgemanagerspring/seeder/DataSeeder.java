package com.teamproject.fridgemanagerspring.seeder;

import com.teamproject.fridgemanagerspring.domain.category.Category;
import com.teamproject.fridgemanagerspring.domain.enums.ProductStatus;
import com.teamproject.fridgemanagerspring.domain.enums.StorageType;
import com.teamproject.fridgemanagerspring.domain.enums.Unit;
import com.teamproject.fridgemanagerspring.domain.fridge.Fridge;
import com.teamproject.fridgemanagerspring.domain.product.Product;
import com.teamproject.fridgemanagerspring.domain.user.User;
import com.teamproject.fridgemanagerspring.repository.CategoryRepository;
import com.teamproject.fridgemanagerspring.repository.FridgeRepository;
import com.teamproject.fridgemanagerspring.repository.ProductRepository;
import com.teamproject.fridgemanagerspring.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final FridgeRepository fridgeRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public DataSeeder(UserRepository userRepository, FridgeRepository fridgeRepository,
                      CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.userRepository = userRepository;
        this.fridgeRepository = fridgeRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 1. 기본 카테고리가 없을 때만 최초 1회 생성
        if (categoryRepository.count() == 0) {
            System.out.println("🌱 기본 카테고리가 없어 15개를 생성합니다...");
            List<Category> initialCategories = List.of(
                    Category.builder().name("채소류").icon("vegetable").isDefault(true).build(),
                    Category.builder().name("과일류").icon("fruit").isDefault(true).build(),
                    Category.builder().name("육류/해산물").icon("meat").isDefault(true).build(),
                    Category.builder().name("유제품").icon("milk").isDefault(true).build(),
                    Category.builder().name("가공식품").icon("processed_food").isDefault(true).build(),
                    Category.builder().name("양념/소스류").icon("sauce").isDefault(true).build(),
                    Category.builder().name("조리된 음식").icon("dish").isDefault(true).build(),
                    Category.builder().name("음료").icon("juice").isDefault(true).build(),
                    Category.builder().name("빵/베이커리류").icon("bread").isDefault(true).build(),
                    Category.builder().name("간식/과자류").icon("snack").isDefault(true).build(),
                    Category.builder().name("건강/다이어트식").icon("salad").isDefault(true).build(),
                    Category.builder().name("약품/영양제").icon("pill").isDefault(true).build(),
                    Category.builder().name("화장품").icon("cosmetics").isDefault(true).build(),
                    Category.builder().name("유아/이유식").icon("baby_food").isDefault(true).build(),
                    Category.builder().name("기타").icon("tag").isDefault(true).build()
            );
            categoryRepository.saveAll(initialCategories);
            System.out.println("✅ 카테고리 생성 완료!");
        }

        // 2. 제품 데이터가 이미 있으면 시딩 중단
        if (productRepository.count() > 0) {
            return;
        }

        // 3. 유저 확인
        Optional<User> optionalUser = userRepository.findAll().stream().findFirst();
        if (optionalUser.isEmpty()) {
            System.out.println("ℹ️ 유저가 없어 제품 시딩을 대기합니다.");
            return;
        }
        User user = optionalUser.get();

        // 4. 유저 냉장고 조회 (없으면 생성)
        Fridge fridge = fridgeRepository.findAllByUserIdAndDeletedAtIsNullOrderByIdAsc(user.getId()).stream()
                .findFirst()
                .orElseGet(() -> fridgeRepository.save(
                        Fridge.builder().name("내 냉장고").user(user).build()
                ));

        // 5. 카테고리 조회
        Category vegCategory = categoryRepository.findByName("채소류").orElseThrow();
        Category dairyCategory = categoryRepository.findByName("유제품").orElseThrow();
        Category meatCategory = categoryRepository.findByName("육류/해산물").orElseThrow();

        // 6. 날짜 세팅
        LocalDate today = LocalDate.now();
        LocalDate threeDaysLater = today.plusDays(2);
        LocalDate pastDate = today.minusDays(3);
        LocalDate safeDate = today.plusDays(10);
        LocalDateTime lastMonthTime = LocalDateTime.now().minusMonths(1);

        // 7. 제품 생성 및 저장
        System.out.println("🌱 최초 테스트용 제품 데이터를 생성합니다...");
        List<Product> products = List.of(
                Product.builder().name("유통기한 임박 우유").category(dairyCategory).fridge(fridge)
                        .storageType(StorageType.REFRIGERATED).quantity(1.0).unit(Unit.L)
                        .price(3000).expirationDate(threeDaysLater).status(ProductStatus.STORED).build(),

                Product.builder().name("상해버린 돼지고기").category(meatCategory).fridge(fridge)
                        .storageType(StorageType.FROZEN).quantity(600.0).unit(Unit.G)
                        .price(15000).expirationDate(pastDate).status(ProductStatus.STORED).build(),

                Product.builder().name("싱싱한 대파").category(vegCategory).fridge(fridge)
                        .storageType(StorageType.REFRIGERATED).quantity(1.0).unit(Unit.EA)
                        .price(2000).expirationDate(safeDate).status(ProductStatus.STORED).build(),

                Product.builder().name("계란").category(dairyCategory).fridge(fridge)
                        .storageType(StorageType.REFRIGERATED).quantity(30.0).unit(Unit.EA)
                        .price(8000).expirationDate(safeDate).status(ProductStatus.CONSUMED).build(),

                Product.builder().name("계란").category(dairyCategory).fridge(fridge)
                        .storageType(StorageType.REFRIGERATED).quantity(15.0).unit(Unit.EA)
                        .price(4000).expirationDate(safeDate).status(ProductStatus.CONSUMED).build(),

                Product.builder().name("한우 등심").category(meatCategory).fridge(fridge)
                        .storageType(StorageType.REFRIGERATED).quantity(400.0).unit(Unit.G)
                        .price(45000).expirationDate(safeDate).status(ProductStatus.CONSUMED).build(),

                Product.builder().name("양배추").category(vegCategory).fridge(fridge)
                        .storageType(StorageType.REFRIGERATED).quantity(1.0).unit(Unit.EA)
                        .price(5000).expirationDate(pastDate).status(ProductStatus.DISCARDED).build(),

                Product.builder().name("두부").category(vegCategory).fridge(fridge)
                        .storageType(StorageType.REFRIGERATED).quantity(1.0).unit(Unit.EA)
                        .price(1500).expirationDate(pastDate).status(ProductStatus.DISCARDED).build()
        );

        productRepository.saveAll(products);

        // 8. 통계 테스트용 시간 강제 업데이트
        try {
            productRepository.updateUpdatedAtForSeed("양배추", ProductStatus.DISCARDED, lastMonthTime);
        } catch (Exception e) {
            System.out.println("⚠️ 강제 업데이트 쿼리 실행 실패");
        }

        System.out.println("✅ 최초 제품 데이터 시딩 완료!");
    }
}