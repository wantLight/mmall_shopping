package com.mmall.common;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;

/**
 * Created by xyzzg on 2018/6/4.
 */
//保证序列化Json的时候，如果是null的对象，key也会消失
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
//此注解用于属性或者getter方法上，用于在序列化时嵌入我们自定义的代码
public class ServerResponse<T> implements Serializable {

    private int status;
    private String msg;
    private T data;

    private ServerResponse(int status){
        this.status = status;
    }
    private ServerResponse(int status,T data){
        this.status = status;
        this.data = data;
    }
    private ServerResponse(int status,String msg,T data){
        this.status = status;
        this.msg = msg;
        this.data = data;
    }
    private ServerResponse(int status,String msg){
        this.status = status;
        this.msg = msg;
    }

    @JsonIgnore
    //不在json实例化中
    public boolean isSuccess(){
        return this.status == ResponseCode.SUCCESS.getCodel();
    }

    public int getStatus(){
        return  status;
    }

    public T getData(){
        return data;
    }

    public String getMsg(){
        return msg;
    }

    public static <T> ServerResponse<T> createBySuccess(){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCodel());
    }

    public static <T> ServerResponse<T> createBySuccessMessage(String msg){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCodel(),msg);
    }

    public static <T> ServerResponse<T> createBySuccess(T data){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCodel(),data);
    }

    public static <T> ServerResponse<T> createBySuccess(String msg,T data){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCodel(),msg,data);
    }

    public static <T> ServerResponse<T> createByError(){
        return new ServerResponse<T>(ResponseCode.ERROR.getCodel(),ResponseCode.ERROR.getDesc());
    }

    public static <T> ServerResponse<T> createByErrorMessage(String errorMsg){
        return new ServerResponse<T>(ResponseCode.ERROR.getCodel(),errorMsg);
    }

    public static <T> ServerResponse<T> createByErrorCodeMessage(int errorCode,String errorMessage){
        return new ServerResponse<T>(errorCode,errorMessage);
    }

}
