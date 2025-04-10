package footoff.api.global.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonInclude;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse<T> {
    // API 응답 성공 여부
    private boolean success;
    
    // 응답 데이터
    private T data;
    
    // 에러 코드
    private String code;
    
    // 에러 메시지
    private String message;
    
    // 성공 응답 생성
    public static <T> BaseResponse<T> onSuccess(T data) {
        return new BaseResponse<>(true, data, null, null);
    }
    
    // 기본 실패 응답 생성
    public static <T> BaseResponse<T> onFailure(String code, String message) {
        return new BaseResponse<>(false, null, code, message);
    }
    
    // 데이터가 포함된 실패 응답 생성
    public static <T> BaseResponse<T> onFailure(String code, String message, T data) {
        return new BaseResponse<>(false, data, code, message);
    }
} 