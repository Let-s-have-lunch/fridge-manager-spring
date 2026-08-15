package com.teamproject.fridgemanagerspring.dto.statistics.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class StatisticsResponse {
    private String targetMonth;
    private DashboardData dashboardData;
    private ModalData modalData;

    @Getter
    @Builder
    public static class DashboardData {
        private Integer totalConsumedPrice;
        private StatusRates statusRates;
        private ExpirationCards expirationCards;
        private List<Top3Product> top3Products;
    }

    @Getter
    @Builder
    public static class StatusRates {
        private Integer consumed;
        private Integer discarded;
        private Integer others;
    }

    @Getter
    @Builder
    public static class ExpirationCards {
        private Integer expired;
        private Integer expiringSoon;
        private List<ExpirationProduct> expiredList;
        private List<ExpirationProduct> expiringSoonList;
    }

    @Getter
    @Builder
    public static class ExpirationProduct {
        private Long id;
        private String name;
        private LocalDate expirationDate;
        private String icon;
    }

    @Getter
    @Builder
    public static class Top3Product {
        private String name;
        private Long useCount;
        private Integer totalPrice;
        private String icon;
    }

    @Getter
    @Builder
    public static class ModalData {
        private Integer totalConsumedPrice;
        private List<CategoryChartData> categoryChartData;
        private SavingEffect savingEffect;
    }

    @Getter
    @Builder
    public static class CategoryChartData {
        private String name;
        private Integer price;
    }

    @Getter
    @Builder
    public static class SavingEffect {
        private Integer amount;
        private Boolean isPositive;
        private String percentage;
    }
}
