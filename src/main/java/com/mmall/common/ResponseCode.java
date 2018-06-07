package com.mmall.common;

/**
 * Created by xyzzg on 2018/6/4.
 */
public enum  ResponseCode {
    SUCCESS(0,"SUCCESS"),
    ERROR(1,"ERROR"),
    NEED_LOGIN(10,"NEED_LOGIN"),
    ILLEGAL_ARGUMENT(2,"ILLEGAL_ARGUMENT");

    //如果打算自定义自己的方法，那么必须在enum实例序列的最后添加一个分号。
    // 而且 Java 要求必须先定义 enum 实例

    // 成员变量
    private final int codel;
    private final String desc;

    // 构造方法
    ResponseCode(int codel,String desc){
        this.codel = codel;
        this.desc = desc;
    }
    // get set 方法
    public int getCodel(){
        return codel;
    }
    public String getDesc(){
        return desc;
    }
}
