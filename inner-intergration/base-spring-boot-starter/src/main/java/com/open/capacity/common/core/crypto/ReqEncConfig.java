/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.common.core.crypto;

public class ReqEncConfig {
    private String signDataKey;
    private String encKey;
    private boolean pub;

    public String getSignDataKey() {
        return signDataKey;
    }

    public void setSignDataKey(String signDataKey) {
        this.signDataKey = signDataKey;
    }

    public String getEncKey() {
        return encKey;
    }

    public void setEncKey(String encKey) {
        this.encKey = encKey;
    }

    public boolean isPub() {
        return pub;
    }

    public void setPub(boolean pub) {
        this.pub = pub;
    }
}
