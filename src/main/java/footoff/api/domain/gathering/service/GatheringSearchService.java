//package footoff.api.domain.gathering.service;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//import footoff.api.domain.gathering.dto.GatheringDto;
//
///**
// * 모임 검색 관련 비즈니스 로직을 처리하는 서비스 인터페이스
// */
//public interface GatheringSearchService {
//
//    /**
//     * 키워드로 모임을 검색하는 메소드
//     *
//     * @param keyword 검색 키워드 (제목, 설명에서 검색)
//     * @param page 페이지 번호 (0부터 시작)
//     * @param size 페이지 크기
//     * @return 검색된 모임 목록
//     */
//    List<GatheringDto> searchByKeyword(String keyword, int page, int size);
//
//    /**
//     * 위치 기반으로 모임을 검색하는 메소드
//     *
//     * @param latitude 위도
//     * @param longitude 경도
//     * @param radius 검색 반경(km)
//     * @param page 페이지 번호 (0부터 시작)
//     * @param size 페이지 크기
//     * @return 검색된 모임 목록
//     */
//    List<GatheringDto> searchByLocation(Double latitude, Double longitude, Double radius, int page, int size);
//
//    /**
//     * 날짜 범위로 모임을 검색하는 메소드
//     *
//     * @param startDate 시작 날짜
//     * @param endDate 종료 날짜
//     * @param page 페이지 번호 (0부터 시작)
//     * @param size 페이지 크기
//     * @return 검색된 모임 목록
//     */
//    List<GatheringDto> searchByDateRange(LocalDateTime startDate, LocalDateTime endDate, int page, int size);
//
//    /**
//     * 통합 검색 메소드 (키워드, 위치, 날짜 범위 조합)
//     *
//     * @param keyword 검색 키워드 (선택적)
//     * @param latitude 위도 (선택적)
//     * @param longitude 경도 (선택적)
//     * @param radius 검색 반경(km) (선택적)
//     * @param startDate 시작 날짜 (선택적)
//     * @param endDate 종료 날짜 (선택적)
//     * @param page 페이지 번호 (0부터 시작)
//     * @param size 페이지 크기
//     * @return 검색된 모임 목록
//     */
//    List<GatheringDto> search(String keyword, Double latitude, Double longitude, Double radius,
//                             LocalDateTime startDate, LocalDateTime endDate, int page, int size);
//}