package com.open.capacity.db;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author 作者 owen E-mail: 624191343@qq.com
 */
@SpringBootApplication
public class DbCenterApp {
    public static void main(String[] args) {
        SpringApplication.run(DbCenterApp.class, args);
    }
}
