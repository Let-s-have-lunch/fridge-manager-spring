# 🧊 Fridge Manager Service (Backend)

스마트한 유통기한 관리로 식재료 폐기는 줄이고 소비 효율을 높여주는 냉장고 관리 서비스의 백엔드(Spring Boot) API입니다.

---

## 🛠 Tech Stack

*   **Framework:** Spring Boot (Web, Data JPA, Security, Validation)
*   **Language:** Java 21
*   **Security:** Spring Security, JWT (JSON Web Token)
*   **Database:** MariaDB, Spring Data JPA / Hibernate
*   **Architecture:** Layered Architecture (Controller - Service - Repository)

---

## ✨ Key Features

1.  **스마트 냉장고 및 식재료 관리 (Fridges & Products)**
    *   유저당 다중 냉장고(Fridge) 생성 및 관리.
    *   커스텀 식재료 카테고리 추가 및 관리.
    *   식재료(Product) 보관 방식(냉장/냉동/실온), 유통기한, 수량, 단위, 상태(보관/소비/폐기) 정밀 추적.

2.  **월간 소비 및 폐기 통계 (Statistics)**
    *   이번 달 소비/폐기/보관 중인 식재료 비율 및 총액 자동 계산.
    *   유통기한 임박 및 만료 식재료 리스트 추출.
    *   전월 폐기 금액과 비교한 '절약 효과(Saving Effect)' 계산 및 최다 소비 TOP 3 식재료 랭킹 제공.

3.  **장보기 캘린더 (Shopping List)**
    *   날짜(LocalDate) 기반 장보기 메모 등록 및 조회.
    *   체크박스(Todo) 토글 기능 및 유저 탈퇴 시 연관 데이터 Cascade 자동 삭제 처리.

4.  **회원 및 보안 (Users & Security)**
    *   회원가입 및 JWT 기반 Stateless 인증/인가 (`JwtAuthenticationFilter`).
    *   BCrypt 암호화 및 닉네임/이메일 중복 방지.
    *   전역 Soft Delete (`@SQLDelete`, `@SQLRestriction`) 적용으로 실수로 인한 데이터 유실 방지 및 무결성 유지.

5.  **고객지원 및 관리자 시스템 (Admin & CS)**
    *   공지사항(Notice) 및 1:1 문의(Inquiry) 게시판 기능.
    *   Spring Security `@PreAuthorize`를 활용한 권한(USER/ADMIN)별 API 접근 제어.
    *   관리자의 사용자 권한 변경 및 강제 탈퇴, 문의 답변 기능 구현.

---

## 📂 Project Structure

```text
├── config/          # Spring Security, JWT, CORS 설정
├── controller/      # REST API 엔드포인트
├── domain/          # JPA 엔티티 및 Enum (BaseTimeEntity 기반 Soft Delete 적용)
├── dto/             # Request / Response DTO 및 Pagination 객체
├── repository/      # Spring Data JPA Repository (Query 메서드 포함)
├── service/         # 핵심 비즈니스 로직 및 트랜잭션 관리
└── utils/           # JWT 토큰 생성 및 검증 유틸리티 클래스
```

---

## 🚀 Getting Started

### 1. Prerequisites
- Java 21
- Gradle (또는 IDE 내장 Gradle Wrapper)
- MariaDB (포트: 8012 기본 설정)

### 2. Environment Variables
데이터베이스 정보 및 JWT 시크릿 키 설정이 필요합니다. `application.yml` 또는 `application-dev.yml`에 아래 정보를 설정하세요.

```yaml
spring:
  datasource:
    url: jdbc:mariadb://localhost:8012/fridge_manager
    username: your_db_username
    password: your_db_password

jwt:
  secret: your_jwt_secret_key_string_here_must_be_long_enough

cors:
  allowed-origins: http://localhost:8081, http://localhost:3000
```

### 3. Run Development Server
터미널에서 아래 명령어를 실행하여 서버를 구동합니다. (기본 포트: 5000)

```bash
# Windows
gradlew bootRun

# macOS / Linux
./gradlew bootRun
```