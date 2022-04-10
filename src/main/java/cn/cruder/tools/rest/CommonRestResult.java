package cn.cruder.tools.rest;

import java.io.Serializable;

/**
 * @Author: cruder
 * @Date: 2022/04/05/14:17
 */
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


    /**
     * 成功
     *
     * @return
     */
    public static <T> CommonRestResult<T> ok() {
        return genRestResult(null, UniversalCodeEnum.SUCCESS.code(), UniversalCodeEnum.SUCCESS.msg());
    }

    /**
     * 成功
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> CommonRestResult<T> ok(T data) {
        return genRestResult(data, UniversalCodeEnum.SUCCESS.code(), UniversalCodeEnum.SUCCESS.msg());
    }

    /**
     * @param data 数据
     * @param msg  提示信息
     * @param <T>  泛型
     * @return RestResult
     */
    public static <T> CommonRestResult<T> ok(T data, String msg) {
        return genRestResult(data, UniversalCodeEnum.SUCCESS.code(), msg);
    }

    /**
     * 失败
     *
     * @param <T> 泛型
     * @return RestResult
     */
    public static <T> CommonRestResult<T> fail() {
        return genRestResult(null, UniversalCodeEnum.FAIL.code(), UniversalCodeEnum.FAIL.msg());
    }

    /**
     * @param msg
     * @param <T>
     * @return
     */
    public static <T> CommonRestResult<T> fail(String msg) {
        return genRestResult(null, UniversalCodeEnum.FAIL.code(), msg);
    }

    /**
     * @param code
     * @param msg
     * @param <T>
     * @return
     */
    public static <T> CommonRestResult<T> fail(Integer code, String msg) {
        return genRestResult(null, code, msg);
    }

    /**
     * @param data
     * @param <T>
     * @return
     */
    public static <T> CommonRestResult<T> fail(T data) {
        return genRestResult(data, UniversalCodeEnum.FAIL.code(), UniversalCodeEnum.FAIL.msg());
    }

    /**
     * @param data
     * @param msg
     * @param <T>
     * @return
     */
    public static <T> CommonRestResult<T> fail(T data, String msg) {
        return genRestResult(data, UniversalCodeEnum.FAIL.code(), msg);
    }

    /**
     * @param businessCode
     * @param msg
     * @param <T>
     * @return
     */
    public static <T> CommonRestResult<T> fail(int businessCode, String msg) {
        return genRestResult(null, businessCode, msg);
    }

    /**
     * 构建返回只
     *
     * @param data         data
     * @param businessCode 业务状态码
     * @param msg          提示信息
     * @param <T>          数据泛型
     * @return RestResult
     */
    public static <T> CommonRestResult<T> genRestResult(T data, int businessCode, String msg) {
        CommonRestResult<T> apiResult = new CommonRestResult<>();
        apiResult.setBusinessCode(businessCode);
        apiResult.setData(data);
        apiResult.setMsg(msg);
        return apiResult;
    }

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
}