package com.teamproject.fridgemanagerspring.service;

import com.teamproject.fridgemanagerspring.domain.category.Category;
import com.teamproject.fridgemanagerspring.domain.enums.ProductStatus;
import com.teamproject.fridgemanagerspring.domain.fridge.Fridge;
import com.teamproject.fridgemanagerspring.domain.product.Product;
import com.teamproject.fridgemanagerspring.dto.statistics.response.StatisticsResponse;
import com.teamproject.fridgemanagerspring.repository.CategoryRepository;
import com.teamproject.fridgemanagerspring.repository.FridgeRepository;
import com.teamproject.fridgemanagerspring.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final ProductRepository productRepository;
    private final FridgeRepository fridgeRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public StatisticsResponse getUserStatistics(Long userId, int year, int month) {

        // 1. 공통 날짜 세팅
        YearMonth targetMonth = YearMonth.of(year, month);
        LocalDateTime thisMonthStart = targetMonth.atDay(1).atStartOfDay();
        LocalDateTime thisMonthEnd = targetMonth.plusMonths(1).atDay(1).atStartOfDay();

        YearMonth prevMonth = targetMonth.minusMonths(1);
        LocalDateTime lastMonthStart = prevMonth.atDay(1).atStartOfDay();
        LocalDateTime lastMonthEnd = targetMonth.atDay(1).atStartOfDay();

        LocalDate today = LocalDate.now();
        LocalDate threeDaysLater = today.plusDays(3);

        // 2. 내 냉장고 ID 목록 추출
        List<Long> fridgeIds = fridgeRepository.findAllByUserIdAndDeletedAtIsNullOrderByIdAsc(userId).stream()
                .map(Fridge::getId)
                .collect(Collectors.toList());

        if (fridgeIds.isEmpty()) {
            return StatisticsResponse.builder().targetMonth(year + "-" + month).build();
        }

        // -------------------------------------------------------------
        // 대시보드 데이터 조립
        // -------------------------------------------------------------

        // 2-1. 상태별 비율 계산
        List<Object[]> statusStats = productRepository.getStatusStatsByFridgeIds(fridgeIds, thisMonthStart, thisMonthEnd);

        long totalItems = 0;
        int consumedCount = 0, discardedCount = 0, storedCount = 0;
        int totalConsumedPrice = 0;

        for (Object[] stat : statusStats) {
            ProductStatus status = (ProductStatus) stat[0];
            long count = (Long) stat[1];
            long sumPrice = stat[2] != null ? (Long) stat[2] : 0L;

            totalItems += count;

            if (status == ProductStatus.CONSUMED) {
                consumedCount = (int) count;
                totalConsumedPrice = (int) sumPrice;
            } else if (status == ProductStatus.DISCARDED) {
                discardedCount = (int) count;
            } else if (status == ProductStatus.STORED) {
                storedCount = (int) count;
            }
        }

        final long finalTotalItems = totalItems;

        // 2-2. 만료 임박 / 지남 목록 (엔티티 필터링)
        List<Product> storedProductsThisMonth = productRepository.findAll().stream()
                .filter(p -> fridgeIds.contains(p.getFridge().getId())
                        && p.getStatus() == ProductStatus.STORED
                        && !p.getExpirationDate().isBefore(thisMonthStart.toLocalDate())
                        && p.getExpirationDate().isBefore(thisMonthEnd.toLocalDate()))
                .collect(Collectors.toList());

        List<StatisticsResponse.ExpirationProduct> expiredList = storedProductsThisMonth.stream()
                .filter(p -> p.getExpirationDate().isBefore(today))
                .map(p -> StatisticsResponse.ExpirationProduct.builder()
                        .id(p.getId())
                        .name(p.getName())
                        .expirationDate(p.getExpirationDate())
                        .icon(p.getCategory() != null ? p.getCategory().getIcon() : "tag") // 👈 실제 카테고리 icon 꺼내기
                        .build())
                .collect(Collectors.toList());

        List<StatisticsResponse.ExpirationProduct> expiringSoonList = storedProductsThisMonth.stream()
                .filter(p -> !p.getExpirationDate().isBefore(today) && !p.getExpirationDate().isAfter(threeDaysLater))
                .map(p -> StatisticsResponse.ExpirationProduct.builder()
                        .id(p.getId())
                        .name(p.getName())
                        .expirationDate(p.getExpirationDate())
                        .icon(p.getCategory() != null ? p.getCategory().getIcon() : "tag") // 👈 실제 카테고리 icon 꺼내기
                        .build())
                .collect(Collectors.toList());

        // 2-3. TOP 3 (Limit 처리를 위해 PageRequest 사용)
        List<Object[]> top3Query = productRepository.getTopConsumedProducts(fridgeIds, thisMonthStart, thisMonthEnd, PageRequest.of(0, 3));
        List<StatisticsResponse.Top3Product> top3Products = new ArrayList<>();

        for (Object[] row : top3Query) {
            String name = (String) row[0];
            Long count = (Long) row[1];
            Long sumPrice = (Long) row[2];

            // 💡 해당 상품명의 카테고리 아이콘 조회 (없으면 기본 "tag")
            String productIcon = productRepository.findAll().stream()
                    .filter(p -> p.getName().equals(name) && p.getCategory() != null)
                    .map(p -> p.getCategory().getIcon())
                    .findFirst()
                    .orElse("tag");

            top3Products.add(StatisticsResponse.Top3Product.builder()
                    .name(name)
                    .useCount(count)
                    .totalPrice(sumPrice.intValue())
                    .icon(productIcon) // 👈 조회한 아이콘 주입
                    .build());
        }

        StatisticsResponse.DashboardData dashboardData = StatisticsResponse.DashboardData.builder()
                .totalConsumedPrice(totalConsumedPrice)
                .statusRates(StatisticsResponse.StatusRates.builder()
                        .consumed(getPercent(consumedCount, finalTotalItems))
                        .discarded(getPercent(discardedCount, finalTotalItems))
                        .others(getPercent(storedCount, finalTotalItems))
                        .build())
                .expirationCards(StatisticsResponse.ExpirationCards.builder()
                        .expired(expiredList.size())
                        .expiringSoon(expiringSoonList.size())
                        .expiredList(expiredList)
                        .expiringSoonList(expiringSoonList)
                        .build())
                .top3Products(top3Products)
                .build();


        // -------------------------------------------------------------
        // 모달 데이터 조립
        // -------------------------------------------------------------

        // 3-1. 카테고리별 소비 금액
        List<Object[]> categoryStats = productRepository.getCategoryStats(fridgeIds, thisMonthStart, thisMonthEnd);
        List<StatisticsResponse.CategoryChartData> top3Categories = new ArrayList<>();
        int othersPrice = 0;

        for (int i = 0; i < categoryStats.size(); i++) {
            Object[] row = categoryStats.get(i);
            Long catId = (Long) row[0];
            Long sumPrice = (Long) row[1];

            if (i < 3) {
                Optional<Category> catOpt = categoryRepository.findById(catId);
                String catName = catOpt.isPresent() ? catOpt.get().getName() : "알 수 없음";
                top3Categories.add(StatisticsResponse.CategoryChartData.builder()
                        .name(catName)
                        .price(sumPrice.intValue())
                        .build());
            } else {
                othersPrice += sumPrice.intValue();
            }
        }
        if (othersPrice > 0) {
            top3Categories.add(StatisticsResponse.CategoryChartData.builder().name("기타").price(othersPrice).build());
        }

        // 3-2. 절약 효과
        Integer thisMonthWasteRaw = productRepository.getDiscardedTotalPrice(fridgeIds, thisMonthStart, thisMonthEnd);
        int thisMonthWaste = thisMonthWasteRaw != null ? thisMonthWasteRaw : 0;

        Integer lastMonthWasteRaw = productRepository.getDiscardedTotalPrice(fridgeIds, lastMonthStart, lastMonthEnd);
        int lastMonthWaste = lastMonthWasteRaw != null ? lastMonthWasteRaw : 0;

        int savingAmount = 0;
        int savingPercentage = 0;

        if (lastMonthWaste > 0) {
            savingAmount = lastMonthWaste - thisMonthWaste;
            savingPercentage = Math.round(((float) savingAmount / lastMonthWaste) * 100);
        } else if (lastMonthWaste == 0 && thisMonthWaste > 0) {
            savingAmount = -thisMonthWaste;
            savingPercentage = -100;
        }

        StatisticsResponse.ModalData modalData = StatisticsResponse.ModalData.builder()
                .totalConsumedPrice(totalConsumedPrice)
                .categoryChartData(top3Categories)
                .savingEffect(StatisticsResponse.SavingEffect.builder()
                        .amount(Math.abs(savingAmount))
                        .isPositive(savingAmount >= 0)
                        .percentage(savingPercentage > 0 ? "+" + savingPercentage + "%" : savingPercentage + "%")
                        .build())
                .build();


        return StatisticsResponse.builder()
                .targetMonth(year + "-" + month)
                .dashboardData(dashboardData)
                .modalData(modalData)
                .build();
    }

    private int getPercent(int count, long total) {
        return total > 0 ? Math.round(((float) count / total) * 100) : 0;
    }
}
