package com.qovf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * QOVF 外汇交易平台后端启动类。
 * 分层目录：controller / service / mapper / entity / dto / config / common
 */
@SpringBootApplication
public class QovfApplication {

    public static void main(String[] args) {
        SpringApplication.run(QovfApplication.class, args);
    }
}
