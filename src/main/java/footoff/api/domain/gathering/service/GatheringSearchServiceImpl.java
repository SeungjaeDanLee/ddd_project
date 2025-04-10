//package footoff.api.domain.gathering.service;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.domain.Specification;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import footoff.api.domain.gathering.dto.GatheringDto;
//import footoff.api.domain.gathering.entity.Gathering;
//import footoff.api.domain.gathering.entity.GatheringLocation;
//import footoff.api.domain.gathering.repository.GatheringRepository;
//import footoff.api.domain.gathering.repository.specification.GatheringSpecification;
//import lombok.RequiredArgsConstructor;
//
//@Service
//@RequiredArgsConstructor
//public class GatheringSearchServiceImpl implements GatheringSearchService {
//
//    private final GatheringRepository gatheringRepository;
//
//    @Override
//    @Transactional(readOnly = true)
//    public List<GatheringDto> searchByKeyword(String keyword, int page, int size) {
//        Pageable pageable = PageRequest.of(page, size);
//
//        Specification<Gathering> spec = Specification.where(null);
//
//        // 키워드 검색 조건 추가
//        if (keyword != null && !keyword.trim().isEmpty()) {
//            spec = spec.and(GatheringSpecification.titleOrDescriptionContains(keyword));
//        }
//
//        // 현재 시간 이후의 모임만 검색
//        spec = spec.and(GatheringSpecification.gatheringDateAfter(LocalDateTime.now()));
//
//        return gatheringRepository.findAll(spec, pageable).stream()
//                .map(GatheringDto::fromEntity)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public List<GatheringDto> searchByLocation(Double latitude, Double longitude, Double radius, int page, int size) {
//        if (latitude == null || longitude == null) {
//            return new ArrayList<>();
//        }
//
//        Pageable pageable = PageRequest.of(page, size);
//
//        Specification<Gathering> spec = Specification.where(null);
//
//        // 위치 기반 검색 조건 추가
//        spec = spec.and(GatheringSpecification.locationWithinRadius(latitude, longitude, radius));
//
//        // 현재 시간 이후의 모임만 검색
//        spec = spec.and(GatheringSpecification.gatheringDateAfter(LocalDateTime.now()));
//
//        return gatheringRepository.findAll(spec, pageable).stream()
//                .map(GatheringDto::fromEntity)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public List<GatheringDto> searchByDateRange(LocalDateTime startDate, LocalDateTime endDate, int page, int size) {
//        if (startDate == null || endDate == null) {
//            return new ArrayList<>();
//        }
//
//        Pageable pageable = PageRequest.of(page, size);
//
//        Specification<Gathering> spec = Specification.where(null);
//
//        // 날짜 범위 검색 조건 추가
//        spec = spec.and(GatheringSpecification.gatheringDateBetween(startDate, endDate));
//
//        return gatheringRepository.findAll(spec, pageable).stream()
//                .map(GatheringDto::fromEntity)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public List<GatheringDto> search(String keyword, Double latitude, Double longitude, Double radius,
//                                    LocalDateTime startDate, LocalDateTime endDate, int page, int size) {
//        Pageable pageable = PageRequest.of(page, size);
//
//        Specification<Gathering> spec = Specification.where(null);
//
//        // 키워드 검색 조건 추가
//        if (keyword != null && !keyword.trim().isEmpty()) {
//            spec = spec.and(GatheringSpecification.titleOrDescriptionContains(keyword));
//        }
//
//        // 위치 기반 검색 조건 추가
//        if (latitude != null && longitude != null && radius != null) {
//            spec = spec.and(GatheringSpecification.locationWithinRadius(latitude, longitude, radius));
//        }
//
//        // 날짜 범위 검색 조건 추가
//        if (startDate != null && endDate != null) {
//            spec = spec.and(GatheringSpecification.gatheringDateBetween(startDate, endDate));
//        } else {
//            // 시작 날짜/종료 날짜가 지정되지 않은 경우, 현재 시간 이후의 모임만 검색
//            spec = spec.and(GatheringSpecification.gatheringDateAfter(LocalDateTime.now()));
//        }
//
//        return gatheringRepository.findAll(spec, pageable).stream()
//                .map(GatheringDto::fromEntity)
//                .collect(Collectors.toList());
//    }
//}