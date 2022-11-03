package org.boozsoft.config;

import com.alibaba.fastjson.JSON;
import org.boozsoft.utils.CollectOfUtils;
import org.springbooz.datasource.r2dbc.properties.BoozR2dbcProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author 木子桉易洋
 * @version 1.0
 * @company 财税达软件科技
 * @date 2021/3/22 9:44 上午
 */
@Service
public class BoozR2dbcPropertiesImpl implements BoozR2dbcProperties {

    /**
     * Database name. Set if no name is specified in the url. Default to "testdb" when
     * using an embedded database.
     */
    @Value("${spring.booz.r2dbc.mode}")
    private String mode;

    @Value("${spring.booz.r2dbc.port:}")
    private String port;

    @Value("${spring.booz.r2dbc.name}")
    private String name;

    /**
     * Whether to generate a random database name. Ignore any configured name when
     * enabled.
     */

    @Value("${spring.booz.r2dbc.generateUniqueName:false}")
    private boolean generateUniqueName;

    /**
     * R2DBC URL of the database. database name, username, password and pooling options
     * specified in the url take precedence over individual options.
     */

    @Value("${spring.booz.r2dbc.url}")
    private String url;

    /**
     * Login username of the database. Set if no username is specified in the url.
     */

    @Value("${spring.booz.r2dbc.username}")
    private String username;

    /**
     * Login password of the database. Set if no password is specified in the url.
     */

    @Value("${spring.booz.r2dbc.password}")
    private String password;

    /**
     * Additional R2DBC options.
     */
    @Value("${spring.booz.r2dbc.properties.search_path:}")  // 默认值是{"k1":"v1","k2":"v2"}
    private String searchPath;
    private Map properites;

    private R2dbcProperties.Pool pool = new R2dbcProperties.Pool();

    private String uniqueName;

    @Override
    public void setMode(String mode) {
        this.mode=mode;
    }

    @Override
    public void setPort(String port) {
        this.port=port;
    }

    @Override
    public String getMode() {
        return this.mode;
    }

    @Override
    public String getPort() {
        return this.port;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name=name;
    }

    @Override
    public boolean isGenerateUniqueName() {
        return this.generateUniqueName;
    }

    @Override
    public void setGenerateUniqueName(boolean generateUniqueName) {
        this.generateUniqueName=generateUniqueName;
    }

    @Override
    public String getUrl() {
        return this.url;
    }

    @Override
    public void setUrl(String url) {
        this.url=url;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public void setUsername(String username) {
        this.username=username;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public void setPassword(String password) {
        this.password=password;
    }

    @Override
    public Map<String, String> getProperties() {
        if(this.searchPath!=null){
            this.properites= CollectOfUtils.mapof("search_path",this.searchPath);
        }else{
            if(this.properites!=null){
                this.properites=CollectOfUtils.mapof("search_path",this.properites.get("search_path"));
            }
        }
        return this.properites;
    }


    public void setProperites(Map properites) {
        this.properites = properites;
    }

    @Override
    public R2dbcProperties.Pool getPool() {
        return this.pool;
    }

    public void setPool(R2dbcProperties.Pool pool) {
        this.pool = pool;
    }

    @Override
    public BoozR2dbcProperties copy() {
        BoozR2dbcPropertiesImpl boozR2dbcProperties = new BoozR2dbcPropertiesImpl();
        boozR2dbcProperties.setGenerateUniqueName(false);
        boozR2dbcProperties.setMode(this.mode);
        boozR2dbcProperties.setName(this.name);
        boozR2dbcProperties.setPassword(this.password);
        boozR2dbcProperties.setPort(this.port);
        boozR2dbcProperties.setPool(this.pool);
        boozR2dbcProperties.setProperites(this.properites);
        boozR2dbcProperties.setUrl(this.url);
        boozR2dbcProperties.setUsername(this.username);
        return boozR2dbcProperties;
    }
}
