package vn.clmart.manager_service.config.exceptions;

public class BusinessException extends RuntimeException {
    private String code;
    private String message;
    private Object[] args;

    public BusinessException() {
    }

    public BusinessException(String message) {
        super(message);
        this.message = message;
        this.code = "500";
    }

    public BusinessException(String code, String message, Object... args) {
        super(message);
        this.message = message;
        this.args = args;
        this.code = code;
    }

    public BusinessException(Integer code, String message, Throwable throwable) {
        super(message, throwable);
        this.message = message;
        this.code = code.toString();
    }

    public BusinessException(String code, String message) {
        super(message);
        this.message = message;
        this.code = code;
    }

    public BusinessException(String code, String message, Throwable throwable) {
        super(throwable);
        this.message = message;
        this.code = code;
    }

    public BusinessException(Integer code, Throwable throwable) {
        super(throwable);
        this.code = code.toString();
    }

    public BusinessException(String code, Throwable throwable) {
        super(throwable);
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public Object[] getArgs() {
        return this.args;
    }
}
