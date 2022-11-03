package org.springbooz.datasource.r2dbc.config;

import lombok.Data;

@Data
public class ConnectionConfig {

    private String url;
    private String username;
    private String password;

    private ConnectionPoolConfig pool;
}
