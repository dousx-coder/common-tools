package cn.cruder.tools.ex;

import cn.cruder.tools.rest.enums.RestCodeAbsEnumClass;
import cn.cruder.tools.rest.enums.UniversalCodeEnum;

import java.io.Serializable;

public class CommonException extends RuntimeException implements Serializable {
    /**
     * 错误码
     * <br/>
     * 业务码各服务自定义枚举管理起来
     */
    private Integer code;
    /**
     * 错误提示
     */
    private String message;

    public void setCode(Integer code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public CommonException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }


    public CommonException(String message, Throwable cause, Integer code) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }

    public CommonException(Throwable cause) {
        this(cause, cause.getMessage());
    }
    public CommonException(Throwable cause,String msg) {
        super(cause);
        this.code = UniversalCodeEnum.FAIL.code();
        this.message = msg;
    }
    public CommonException(Throwable cause, Integer code, String message) {
        super(cause);
        this.code = code;
        this.message = message;
    }

    public CommonException(Integer code, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.code = code;
    }

    public Integer code() {
        return code;
    }

    public String msg() {
        return message;
    }

    /**
     * 未知错误
     */
    public CommonException() {
        this(UniversalCodeEnum.FAIL);
    }

    public CommonException(RestCodeAbsEnumClass businessEnum) {
        super(businessEnum.msg());
        this.code = businessEnum.code();
        this.message = businessEnum.msg();
    }

    public CommonException(String msg) {
        this(UniversalCodeEnum.FAIL, msg);
    }

    public CommonException(RestCodeAbsEnumClass businessEnum, String msg) {
        super(msg);
        this.code = businessEnum.code();
        this.message = msg;
    }

    @Override
    public String getMessage() {
        return String.format("code:%s message:%s", code, super.getMessage());
    }

    @Override
    public String getLocalizedMessage() {
        return String.format("code:%s message:%s", code, super.getLocalizedMessage());
    }
}
