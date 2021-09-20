package com.pwc.wiki.req;

/**
 * Created by xuhaocheng on 20/09/2021.
 */
public class UserQueryReq extends PageReq {
    private String loginName;

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    @Override
    public String toString() {
        return "UserQueryReq{" +
                "loginName='" + loginName + '\'' +
                "} " + super.toString();
    }
}
