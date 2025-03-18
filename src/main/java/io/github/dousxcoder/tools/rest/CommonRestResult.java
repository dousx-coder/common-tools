package io.github.dousxcoder.tools.rest;

import io.github.dousxcoder.tools.rest.enums.RestCodeAbsEnumClass;
import io.github.dousxcoder.tools.rest.enums.UniversalCodeEnum;

import java.io.Serializable;


public class CommonRestResult<T> implements Serializable {

    /**
     * 业务状态码
     */
    private int businessCode;

    /**
     * 提示信息
     */
    private String msg;

    /**
     * 数据
     */
    private T data;

    public int getBusinessCode() {
        return businessCode;
    }

    public void setBusinessCode(int businessCode) {
        this.businessCode = businessCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    /**
     * 成功
     *
     * @return {@link CommonRestResult}
     */
    public static <T> CommonRestResult<T> ok() {
        return genRestResult(null, UniversalCodeEnum.SUCCESS.code(), UniversalCodeEnum.SUCCESS.msg());
    }

    /**
     * 成功
     *
     * @param data data
     * @param <T>  t
     * @return {@link CommonRestResult}
     */
    public static <T> CommonRestResult<T> ok(T data) {
        return genRestResult(data, UniversalCodeEnum.SUCCESS.code(), UniversalCodeEnum.SUCCESS.msg());
    }

    /**
     * @param data 数据
     * @param msg  提示信息
     * @param <T>  泛型
     * @return {@link CommonRestResult}
     */
    public static <T> CommonRestResult<T> ok(T data, String msg) {
        return genRestResult(data, UniversalCodeEnum.SUCCESS.code(), msg);
    }

    /**
     * 失败
     *
     * @param <T> 泛型
     * @return {@link CommonRestResult}
     */
    public static <T> CommonRestResult<T> fail() {
        return genRestResult(null, UniversalCodeEnum.FAIL.code(), UniversalCodeEnum.FAIL.msg());
    }

    /**
     * @param msg msg
     * @param <T> t
     * @return {@link CommonRestResult}
     */
    public static <T> CommonRestResult<T> fail(String msg) {
        return genRestResult(null, UniversalCodeEnum.FAIL.code(), msg);
    }

    /**
     * @param code code
     * @param msg  msg
     * @param <T>  t
     * @return {@link CommonRestResult}
     */
    public static <T> CommonRestResult<T> fail(Integer code, String msg) {
        return genRestResult(null, code, msg);
    }

    /**
     * @param data data
     * @param <T>  t
     * @return {@link CommonRestResult}
     */
    public static <T> CommonRestResult<T> fail(T data) {
        return genRestResult(data, UniversalCodeEnum.FAIL.code(), UniversalCodeEnum.FAIL.msg());
    }

    /**
     * @param data data
     * @param msg  msg
     * @param <T>  t
     * @return {@link CommonRestResult}
     */
    public static <T> CommonRestResult<T> fail(T data, String msg) {
        return genRestResult(data, UniversalCodeEnum.FAIL.code(), msg);
    }

    /**
     * @param businessCode buz
     * @param msg          msg
     * @param <T>          t
     * @return {@link CommonRestResult}
     */
    public static <T> CommonRestResult<T> fail(int businessCode, String msg) {
        return genRestResult(null, businessCode, msg);
    }

    /**
     * 构建返回结果
     *
     * @param data         data
     * @param businessCode 业务状态码
     * @param msg          提示信息
     * @param <T>          数据泛型
     * @return {@link CommonRestResult}
     */
    public static <T> CommonRestResult<T> genRestResult(T data, int businessCode, String msg) {
        CommonRestResult<T> apiResult = new CommonRestResult<>();
        apiResult.setBusinessCode(businessCode);
        apiResult.setData(data);
        apiResult.setMsg(msg);
        return apiResult;
    }


    /**
     * 构建返回结果
     *
     * @param data     data
     * @param codeEnum {@link RestCodeAbsEnumClass}
     * @param <T>      数据泛型
     * @return {@link CommonRestResult}
     */
    public static <T> CommonRestResult<T> genRestResult(T data, RestCodeAbsEnumClass codeEnum) {
        CommonRestResult<T> apiResult = new CommonRestResult<>();
        apiResult.setBusinessCode(codeEnum.code());
        apiResult.setData(data);
        apiResult.setMsg(codeEnum.msg());
        return apiResult;
    }


    /**
     * 构建返回结果
     *
     * @param codeEnum {@link RestCodeAbsEnumClass}
     * @param <T>      数据泛型
     * @return {@link CommonRestResult}
     */
    public static <T> CommonRestResult<T> genRestResult(RestCodeAbsEnumClass codeEnum) {
        CommonRestResult<T> apiResult = new CommonRestResult<>();
        apiResult.setBusinessCode(codeEnum.code());
        apiResult.setMsg(codeEnum.msg());
        apiResult.setData(null);
        return apiResult;
    }


    /**
     * 如果这里用无参的对象方法，返回的Json会多一个字段(需要在文档中特意说明)感觉不太合适用
     *
     * @param restResult 是否等于{@link UniversalCodeEnum#SUCCESS}
     * @return 是否等于{@link UniversalCodeEnum#SUCCESS}
     */
    public static boolean isUniversalCodeSuccess(@SuppressWarnings("all") CommonRestResult restResult) {
        if (restResult == null) {
            return false;
        }
        return restResult.eqCode(UniversalCodeEnum.SUCCESS);
    }


    /**
     * @param codeEnum 判断当前对象{@link CommonRestResult#businessCode}是否等于{@link RestCodeAbsEnumClass#code()}
     * @return true or false
     */
    public boolean eqCode(RestCodeAbsEnumClass codeEnum) {
        if (codeEnum == null) {
            return false;
        }
        return eqCode(codeEnum.code());
    }

    /**
     * @param code 判断当前对象{@link CommonRestResult#businessCode}是否等于指定code
     * @return true or false
     */
    public boolean eqCode(int code) {
        return this.businessCode == code;
    }


}