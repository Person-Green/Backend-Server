package bssm.plantshuman.peopleandgreen.shared.response;

import lombok.Getter;

@Getter
public class CommonResponse {
    String code;
    String message;

    public CommonResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
