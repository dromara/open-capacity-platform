package com.open.capacity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PacketModel {
    /**
     * 设备 eui
     */
    private String devEui;

    /**
     * 设备类型
     */
    private String type;
}
