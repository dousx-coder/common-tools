package cn.cruder.dousx.tools.rest.enums;

/**
 * 枚举实现该接口
 * <br/>
 * 通用业务状态码枚举{@link UniversalCodeEnum}
 *
 * @Author: cruder
 * @Date: 2022/04/05/14:19
 */
public interface RestCodeAbsEnumClass {
    /**
     * 状态码
     *
     * @return 状态码
     */
    Integer code();

    /**
     * 提示信息
     *
     * @return 提示信息
     */
    String msg();

}
