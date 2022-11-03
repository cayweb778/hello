package org.springbooz.datasource.r2dbc.annotation;

public enum SCHEMA_TYPE{
    /**
     * session或redis获取
     */
    OWNER_SCHEMA,
    /**
     * 使用本地获取
     */
    DEFAULT
}
