//package footoff.api.domain.gathering.controller;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//import org.springframework.format.annotation.DateTimeFormat;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import footoff.api.domain.gathering.dto.GatheringDto;
//import footoff.api.domain.gathering.service.GatheringSearchService;
//import footoff.api.global.common.BaseResponse;
//import lombok.RequiredArgsConstructor;
//
///**
// * 모임 검색 관련 HTTP 요청을 처리하는 컨트롤러
// */
//@RestController
//@RequestMapping("/api/gatherings/search")
//@RequiredArgsConstructor
//public class GatheringSearchController {
//
//    private final GatheringSearchService gatheringSearchService;
//
//    /**
//     * 키워드로 모임을 검색하는 엔드포인트
//     *
//     * @param keyword 검색 키워드 (제목, 설명에서 검색)
//     * @param page 페이지 번호 (0부터 시작)
//     * @param size 페이지 크기
//     * @return 검색된 모임 목록
//     */
//    @GetMapping("/keyword")
//    public ResponseEntity<BaseResponse<List<GatheringDto>>> searchByKeyword(
//            @RequestParam String keyword,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size) {
//        List<GatheringDto> gatherings = gatheringSearchService.searchByKeyword(keyword, page, size);
//        return ResponseEntity.ok(BaseResponse.onSuccess(gatherings));
//    }
//
//    /**
//     * 위치 기반으로 모임을 검색하는 엔드포인트
//     *
//     * @param latitude 위도
//     * @param longitude 경도
//     * @param radius 검색 반경(km)
//     * @param page 페이지 번호 (0부터 시작)
//     * @param size 페이지 크기
//     * @return 검색된 모임 목록
//     */
//    @GetMapping("/location")
//    public ResponseEntity<BaseResponse<List<GatheringDto>>> searchByLocation(
//            @RequestParam Double latitude,
//            @RequestParam Double longitude,
//            @RequestParam(defaultValue = "5.0") Double radius,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size) {
//        List<GatheringDto> gatherings = gatheringSearchService.searchByLocation(latitude, longitude, radius, page, size);
//        return ResponseEntity.ok(BaseResponse.onSuccess(gatherings));
//    }
//
//    /**
//     * 날짜 범위로 모임을 검색하는 엔드포인트
//     *
//     * @param startDate 시작 날짜 (ISO 형식: yyyy-MM-dd'T'HH:mm:ss)
//     * @param endDate 종료 날짜 (ISO 형식: yyyy-MM-dd'T'HH:mm:ss)
//     * @param page 페이지 번호 (0부터 시작)
//     * @param size 페이지 크기
//     * @return 검색된 모임 목록
//     */
//    @GetMapping("/date")
//    public ResponseEntity<BaseResponse<List<GatheringDto>>> searchByDateRange(
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size) {
//        List<GatheringDto> gatherings = gatheringSearchService.searchByDateRange(startDate, endDate, page, size);
//        return ResponseEntity.ok(BaseResponse.onSuccess(gatherings));
//    }
//
//    /**
//     * 통합 검색 엔드포인트 (키워드, 위치, 날짜 범위 조합)
//     *
//     * @param keyword 검색 키워드 (선택적)
//     * @param latitude 위도 (선택적)
//     * @param longitude 경도 (선택적)
//     * @param radius 검색 반경(km) (선택적, 기본값 5.0)
//     * @param startDate 시작 날짜 (선택적, ISO 형식: yyyy-MM-dd'T'HH:mm:ss)
//     * @param endDate 종료 날짜 (선택적, ISO 형식: yyyy-MM-dd'T'HH:mm:ss)
//     * @param page 페이지 번호 (0부터 시작)
//     * @param size 페이지 크기
//     * @return 검색된 모임 목록
//     */
//    @GetMapping
//    public ResponseEntity<BaseResponse<List<GatheringDto>>> search(
//            @RequestParam(required = false) String keyword,
//            @RequestParam(required = false) Double latitude,
//            @RequestParam(required = false) Double longitude,
//            @RequestParam(required = false, defaultValue = "5.0") Double radius,
//            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
//            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size) {
//        List<GatheringDto> gatherings = gatheringSearchService.search(
//                keyword, latitude, longitude, radius, startDate, endDate, page, size);
//        return ResponseEntity.ok(BaseResponse.onSuccess(gatherings));
//    }
//}