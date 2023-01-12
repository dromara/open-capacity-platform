package com.open.capacity.user.controller;

import com.open.capacity.common.dto.ResponseEntity;
import com.open.capacity.common.signatureValid.annotation.SignatureValidation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lzw
 * @description
 * @date 2023/2/24 15:28
 */
@Slf4j
@RestController
@RequestMapping("/demo")
public class DemoController {

    @GetMapping("/signatureValidation")
    @SignatureValidation
    public ResponseEntity signatureValidation() {
        return ResponseEntity.succeed("操作成功");
    }
}
