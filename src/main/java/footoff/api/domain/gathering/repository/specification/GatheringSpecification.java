package footoff.api.domain.gathering.repository.specification;

import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.Specification;

import footoff.api.domain.gathering.entity.Gathering;
import footoff.api.domain.gathering.entity.GatheringLocation;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

/**
 * 모임 검색을 위한 JPA Specification 정의
 */
public class GatheringSpecification {
    
    /**
     * 제목 또는 설명에 키워드가 포함된 모임 조회 Specification
     * 
     * @param keyword 검색 키워드
     * @return 검색 조건 Specification
     */
    public static Specification<Gathering> titleOrDescriptionContains(String keyword) {
        return (root, query, criteriaBuilder) -> {
            String pattern = "%" + keyword.toLowerCase() + "%";
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), pattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), pattern)
            );
        };
    }
    
    /**
     * 특정 날짜 이후의 모임 조회 Specification
     * 
     * @param date 기준 날짜
     * @return 검색 조건 Specification
     */
    public static Specification<Gathering> gatheringDateAfter(LocalDateTime date) {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.greaterThanOrEqualTo(root.get("gatheringDate"), date);
    }
    
    /**
     * 날짜 범위 내의 모임 조회 Specification
     * 
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return 검색 조건 Specification
     */
    public static Specification<Gathering> gatheringDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.between(root.get("gatheringDate"), startDate, endDate);
    }
    
    /**
     * 특정 위치 반경 내의 모임 조회 Specification
     * 
     * @param latitude 위도
     * @param longitude 경도
     * @param radiusInKm 반경 (km)
     * @return 검색 조건 Specification
     */
    public static Specification<Gathering> locationWithinRadius(Double latitude, Double longitude, Double radiusInKm) {
        return (root, query, criteriaBuilder) -> {
            if (latitude == null || longitude == null) {
                return criteriaBuilder.conjunction();
            }

            // GatheringLocation과 조인
            Join<Gathering, GatheringLocation> locationJoin = root.join("location");
            
            // Haversine 공식을 사용하여 거리 계산 (MySQL 기준)
            // 6371 = 지구 반지름 (km)
            String distanceFormula = 
                    "6371 * acos(cos(radians(" + latitude + ")) * cos(radians(latitude)) * " + 
                    "cos(radians(longitude) - radians(" + longitude + ")) + " + 
                    "sin(radians(" + latitude + ")) * sin(radians(latitude)))";
            
            return criteriaBuilder.lessThanOrEqualTo(
                    criteriaBuilder.function("", Double.class, 
                            criteriaBuilder.literal(distanceFormula)),
                    radiusInKm
            );
        };
    }
    
    /**
     * 승인된 참가자 수가 최소 인원 이상인 모임 조회 Specification
     * 
     * @param minUsers 최소 참가자 수
     * @return 검색 조건 Specification
     */
    public static Specification<Gathering> approvedUsersCountGreaterThan(int minUsers) {
        return (root, query, criteriaBuilder) -> {
            // 중복 카운트를 방지하기 위해 distinct 설정
            query.distinct(true);
            
            // 승인된 참가자 수를 카운트하는 서브쿼리
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<Gathering> subRoot = subquery.from(Gathering.class);
            subquery.select(criteriaBuilder.count(subRoot.get("users")));
            subquery.where(
                    criteriaBuilder.equal(subRoot.get("id"), root.get("id")),
                    criteriaBuilder.equal(subRoot.join("users").get("status"), "APPROVED")
            );
            
            return criteriaBuilder.greaterThanOrEqualTo(subquery, (long) minUsers);
        };
    }
    
    /**
     * 남은 자리가 있는 모임 조회 Specification
     * 
     * @return 검색 조건 Specification
     */
    public static Specification<Gathering> hasAvailableSpots() {
        return (root, query, criteriaBuilder) -> {
            // 중복 카운트를 방지하기 위해 distinct 설정
            query.distinct(true);
            
            // 승인된 참가자 수를 카운트하는 서브쿼리
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<Gathering> subRoot = subquery.from(Gathering.class);
            subquery.select(criteriaBuilder.count(subRoot.get("users")));
            subquery.where(
                    criteriaBuilder.equal(subRoot.get("id"), root.get("id")),
                    criteriaBuilder.equal(subRoot.join("users").get("status"), "APPROVED")
            );
            
            return criteriaBuilder.lessThan(subquery, root.get("maxUsers"));
        };
    }
    
    /**
     * 특정 사용자가 참가하지 않은 모임 조회 Specification
     * 
     * @param userId 사용자 ID
     * @return 검색 조건 Specification
     */
    public static Specification<Gathering> userNotJoined(String userId) {
        return (root, query, criteriaBuilder) -> {
            // 중복 카운트를 방지하기 위해 distinct 설정
            query.distinct(true);
            
            // 사용자가 참가한 모임 ID를 조회하는 서브쿼리
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<Gathering> subRoot = subquery.from(Gathering.class);
            subquery.select(subRoot.get("id"));
            subquery.where(
                    criteriaBuilder.equal(
                            subRoot.join("users").join("user").get("id").as(String.class), 
                            userId
                    )
            );
            
            return criteriaBuilder.not(root.get("id").in(subquery));
        };
    }
} 