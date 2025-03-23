package footoff.api.global.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BaseResponse<T> {
    private final boolean isSuccess;
    private final String message;
    private final T result;

    public static <T> BaseResponse<T> onSuccess(T result) {
        return new BaseResponse<>(true, "요청에 성공하였습니다.", result);
    }

    public static <T> BaseResponse<T> onFailure(String message) {
        return new BaseResponse<>(false, message, null);
    }
} 