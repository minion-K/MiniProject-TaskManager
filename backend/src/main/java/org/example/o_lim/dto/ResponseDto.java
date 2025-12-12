package org.example.o_lim.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "set")
public class ResponseDto<T> {
    private boolean success;

    private String message;

    private T data;

    public static <T> ResponseDto<T> setSuccess(String message, T data) {
        return ResponseDto.set(true, message, data);
    }
    public static <T> ResponseDto<T> setFailed(String message) {
        return ResponseDto.set(false, message, null);
    }
    public static <T> ResponseDto<T> setFailed(String message, T data){
        return ResponseDto.set(false, message, data);
    }
}
