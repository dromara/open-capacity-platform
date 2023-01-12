/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.ext.dispatcher;

import java.util.Objects;

/**
 * TODO Description
 *
 * @author: hillchen
 * @data: 2023-02-17 14:57
 */
public class DispatchRoute {
    private String groupName;
    private String bizName;

    public DispatchRoute(String groupName, String bizName) {
        this.groupName = groupName;
        this.bizName = bizName;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getBizName() {
        return bizName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DispatchRoute that = (DispatchRoute) o;
        return Objects.equals(groupName, that.groupName) && Objects.equals(bizName, that.bizName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupName, bizName);
    }
}
