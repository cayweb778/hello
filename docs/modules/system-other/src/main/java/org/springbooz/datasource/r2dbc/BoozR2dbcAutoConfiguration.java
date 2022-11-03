package org.springbooz.datasource.r2dbc;


import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.r2dbc.R2dbcDataAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;


@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, R2dbcDataAutoConfiguration.class, R2dbcAutoConfiguration.class})
@ComponentScan("org.springbooz.datasource.r2dbc")
public class BoozR2dbcAutoConfiguration  {}
