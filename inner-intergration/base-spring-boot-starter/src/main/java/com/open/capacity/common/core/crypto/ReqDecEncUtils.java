/**
* 相关源码实现原理和使用手册请查看:https://gitee.com/hilltool/hill-spring-ext
*/
package com.open.capacity.common.core.crypto;

import com.open.capacity.common.core.crypto.exception.CryptoCheckSignErrorException;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class ReqDecEncUtils {
    public static <T extends ReqEncVo> T encReq(ReqSourceVo sourceVo,
                                         Supplier<T> encVoBuilder,
                                         Function<String,String> dataKeyEncHandler,
                                         BiFunction<String,String,String> dataEncHandler,
                                         Function<ReqSourceVo,String> signHandler){
        T encVo = encVoBuilder.get();
        encVo.putReqTime(sourceVo.findReqTime());
        encVo.putReqSource(sourceVo.findReqSource());
        encVo.putReqId(sourceVo.findReqId());
        encVo.putEncDataKey(dataKeyEncHandler.apply(sourceVo.findDataKey()));
        encVo.putEncReqData(dataEncHandler.apply(sourceVo.findDataKey(),sourceVo.findReqData()));
        encVo.putReqSign(signHandler.apply(sourceVo));
        return encVo;
    }
    public static  <T extends ReqSourceVo> T decReq(ReqEncVo encVo,
                                         Supplier<T> sourceVoBuilder,
                                         Function<String,String> dataKeyDecHandler,
                                         BiFunction<String,String,String> dataDecHandler,
                                         Function<ReqSourceVo,String> signHandler){
        T sourceVo = sourceVoBuilder.get();
        sourceVo.putReqTime(encVo.findReqTime());
        sourceVo.putReqSource(encVo.findReqSource());
        sourceVo.putReqId(encVo.findReqId());
        sourceVo.putDataKey(dataKeyDecHandler.apply(encVo.findEncDataKey()));
        sourceVo.putReqData(dataDecHandler.apply(sourceVo.findDataKey(),encVo.findEncReqData()));
        String sign = signHandler.apply(sourceVo);
        if (!Objects.equals(sign,encVo.findReqSign())){
            throw new CryptoCheckSignErrorException("请求参数验签失败");
        }
        return sourceVo;
    }
}
