package vn.clmart.manager_service.untils;

import org.springframework.http.HttpStatus;

public class ResponseAPI {
    private int status;
    private Object body;


    public ResponseAPI() {
    }

    public ResponseAPI(int status, Object body) {
        this.status = status;
        this.body = body;
    }


    public static ResponseAPI handlerSuccess(Object body){
        return new ResponseAPI(HttpStatus.OK.value(), body);
    }

    public static ResponseAPI handlerException(Object body){
        return new ResponseAPI(HttpStatus.EXPECTATION_FAILED.value(), body);
    }

    public static ResponseAPI handlerError(Object body, int status){
        return new ResponseAPI(status, body);
    }

    @Override
    public String toString() {
        return "ResponseAPI [status=" + status + ", body=" + body + "]";
    }
}
