package org.springbooz.datasource.r2dbc.mode.router.builder;

import lombok.Data;
import lombok.experimental.Accessors;
import org.boozsoft.utils.CollectOfUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Accessors(chain = true)
public class R2dbcUrlUtil {
    private String url;

    private String baseUrl;
    String databaseName;

    public R2dbcUrlUtil(String url) {
        this.url = url;
        getFirstUri(url);
        getUrlParms(url);
    }

    Map<String, String> parms = new HashMap();

    public Map<String, String> getUrlParms(String url) {
        int index = url.lastIndexOf("/");
        String uri = url.substring(index + 1, url.length());
        this.baseUrl=url.substring(0,index);
        if (uri.contains("?")) {
            String parmsStr = uri.substring(uri.lastIndexOf("?") + 1, uri.length());
            CollectOfUtils.listof(parmsStr.split("&")).forEach(str -> {
                String[] kv = str.split("=");
                parms.put(kv[0], kv[1]);

            });
        }
        return parms;
    }

    public String getFirstUri(String url) {
        int index = url.lastIndexOf("/");
        String uri = url.substring(index + 1, url.length());
        if (uri.contains("?")) {
            String[] arr = uri.split("\\?");
            this.databaseName = arr[0];
            return this.databaseName;
        }
        return null;
    }

    public String make() {
//        String parms="currentSchema=public&adsad=public&asdasd=public&abc=public";
        StringBuffer parmsStr = new StringBuffer();

        parms.forEach((k, v) -> {
            parmsStr.append(  k + "=" + v + "&");
        });
        return baseUrl + "/" + databaseName +"?" +parmsStr;
    }

    public R2dbcUrlUtil setSchemaName(String schemaName) {
        parms.put("currentSchema", schemaName);
        return this;
    }

}
