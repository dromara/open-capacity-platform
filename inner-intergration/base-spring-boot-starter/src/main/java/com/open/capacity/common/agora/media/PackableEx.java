package com.open.capacity.common.agora.media;

/**
 * @author: <a href="https://github.com/hiwepy">hiwepy</a>
 *
 */
public interface PackableEx extends Packable {
    void unmarshal(ByteBuf in);
}
