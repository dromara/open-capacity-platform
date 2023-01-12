package com.open.capacity.common.agora.media;

/**
 * Created by Li on 10/1/2016.
 * @author: <a href="https://github.com/hiwepy">hiwepy</a>
 */
public interface Packable {
    ByteBuf marshal(ByteBuf out);
}
