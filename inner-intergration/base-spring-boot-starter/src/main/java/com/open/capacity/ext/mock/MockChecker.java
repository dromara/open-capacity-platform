/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.ext.mock;

public interface MockChecker {
    /**
     * 通过实现该方法根据上下文来做二次确认是否需要进行mock，返回true时表示需要进行mock
     * @author: hillchen
     * @data: 2022/5/26 10:30
     * @param:
     * @return:
     */
    boolean needMock(MockHandler mockHandler);
}
