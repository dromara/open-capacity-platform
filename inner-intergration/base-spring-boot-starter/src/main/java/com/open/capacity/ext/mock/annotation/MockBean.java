/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.ext.mock.annotation;

import com.open.capacity.ext.mock.DefaultMockChecker;
import com.open.capacity.ext.mock.MockChecker;

import java.lang.annotation.*;


@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MockBean {
    /**
     * 该mock bean 是否需要开启的mock的配置参数名，配置参数的值类型为boolean，默认为false不开启mock
     * @author: hillchen
     * @data: 2022/5/26 9:14
     * @param:
     * @return:
     */
    String mockKey() default "hill4j.mock";
    /**
     * 该mock bean 进行mock的目标对象类型，如果mock bean是通过实现目标bean的相同接口，怎么可以不需要指定，否则必须要指定
     * @author: hillchen
     * @data: 2022/5/26 9:17
     * @param:
     * @return:
     */
    Class targetClazz() default void.class ;
    /**
     * 当开启了mock功能后，可以通过自定义MockChecker，来控制有的场景下需要mock,有的场景不需要mock，来实现在测试环境上，可以按测试需求来决定是否需要mock
     * @author: hillchen
     * @data: 2022/5/26 9:19
     * @param:
     * @return:
     */
    Class<? extends MockChecker> checkerClazz() default DefaultMockChecker.class ;
}
