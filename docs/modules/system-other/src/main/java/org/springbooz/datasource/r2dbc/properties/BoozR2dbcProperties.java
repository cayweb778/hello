package org.springbooz.datasource.r2dbc.properties;

import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties;

import java.util.Map;

/**
 * @author 木子桉易洋
 * @version 1.0
 * @company 财税达软件科技
 * @date 2021/1/29 1:28 下午
 */
public interface BoozR2dbcProperties {
    // 模式: router(主要维护)、masterSlave、cluster

    public void setMode(String mode) ;

    public void setPort(String port) ;

    public String getMode() ;

    public String getPort() ;
    public String getName();

    public void setName(String name);
    public boolean isGenerateUniqueName();
    public void setGenerateUniqueName(boolean generateUniqueName);
    public String getUrl();

    public void setUrl(String url);

    public String getUsername();

    public void setUsername(String username);

    public String getPassword();

    public void setPassword(String password);

    public Map<String, String> getProperties();

    public void setProperites(Map properites);

    public R2dbcProperties.Pool getPool();
    public BoozR2dbcProperties copy();


}
