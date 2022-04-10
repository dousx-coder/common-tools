package cn.cruder.tools.rest;

/**
 * 通用业务状态码枚举
 * <li/>成功 默认用10000
 * <li/>失败 默认用20000
 * <br/>
 * 有其他状态码,在各自服务内新建枚举
 *
 * @Author: cruder
 * @Date: 2022/04/05/14:19
 */
public enum UniversalCodeEnum {
    /**
     * 成功
     */
    SUCCESS(10000, "SUCCESS"),

    /**
     * 失败
     */
    FAIL(20000, "FAIL"),
    ;


    /**
     * 业务状态码
     */
    private final Integer code;

    /**
     * 提示信息
     */
    private final String msg;

    UniversalCodeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 业务状态码
     *
     * @return 业务状态码
     */
    public Integer code() {
        return this.code;
    }

    /**
     * 提示信息
     *
     * @return 提示信息
     */
    public String msg() {
        return this.msg;
    }
}
