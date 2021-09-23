package com.pwc.wiki.exception;

/**
 * Created by xuhaocheng on 21/09/2021.
 */
public enum BusinessExceptionCode {
    USER_LOGIN_NAME_EXIST("登录名已存在"),
    LOGIN_USER_ERROR("用户名不存在或密码错误"),//模糊信息
    VOTE_REPEAT("您已点赞过"),
    ;

    private String desc;

    BusinessExceptionCode(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

//    public static void main(String[] args) {
//        for(BusinessExceptionCode code: BusinessExceptionCode.values()){
//            System.out.println(code.name()+" " + code.getDesc());
//        }
//    }

}
