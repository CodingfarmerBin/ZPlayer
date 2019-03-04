package com.zqb.baselibrary.request.bean;

/**
 * Created by Allen on 2017/10/23.
 *
 * @author Allen
 *         <p>
 *         返回数据基类
 */

public class BaseBean {
    /**
     * 错误码
     */
    private int code;
    /**
     * 错误描述
     */
    private String msg;


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
