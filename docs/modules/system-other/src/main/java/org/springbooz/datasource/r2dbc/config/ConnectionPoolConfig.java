package org.springbooz.datasource.r2dbc.config;

import lombok.Data;

@Data
public class ConnectionPoolConfig {
    // 初始数据库
    private int initialSize;
    // 最大连接
    private int maxSize;
    private int maxAcquireTime;
    // 最长使用寿命
    private int maxLifeTime;
    // 最大创建连接时间
    private int maxCreateConnectionTime;
    // 最大Idel时间
    private int idelTimeout;

    private String validationQuery = "select 1";
}
