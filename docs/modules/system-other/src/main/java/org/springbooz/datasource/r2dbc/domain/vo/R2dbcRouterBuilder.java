package org.springbooz.datasource.r2dbc.domain.vo;

import lombok.Data;

/**
 * @author 木子桉易洋
 * @version 1.0
 * @company 财税达软件科技
 * @date 2021/1/29 2:16 下午
 */
@Data
public class R2dbcRouterBuilder {
    String host;
    String port;
    String databaseName;
    String schemaName;
    String user;
    public static R2dbcRouterBuilder r2dbcRouterBuider(){
        return new R2dbcRouterBuilder();
    }

    public R2dbcRouterBuilder database(String databaseName){
        this.databaseName=databaseName;
        return this;
    }
    public R2dbcRouterBuilder schema(String schemaName){
        this.schemaName=schemaName;
        return this;
    }
    public R2dbcRouterBuilder user(String user){
        this.user=user;
        return this;
    }
    public R2dbcRouterBuilder host(String host){
        this.host=host;
        return this;
    }
    public R2dbcRouterBuilder port(String port){
        this.port=port;
        return this;
    }
    public R2dbcRouterBuilder build(){
        return this;
    }
    public String getIdentity(){
        String host=this.getHost();
        String port=this.getPort();
        String database=this.getDatabaseName();
        String user=this.getUser();
        String schema=this.getSchemaName();
        host=host==null?"empty":host;
        port=port==null?"empty":port;
        database=database==null?"empty":database;
        schema=schema==null?"empty":schema;
        user=user==null?"empty":user;
        String identity=host+"-"+port+"-"+database+"-"+schema+"-"+user;
        return identity;
    }
    public static R2dbcRouterBuilder of(String databaseName, String schemaName,String user){
        R2dbcRouterBuilder r2dbcRouterBuilder = new R2dbcRouterBuilder();
        r2dbcRouterBuilder.databaseName=databaseName;
        r2dbcRouterBuilder.schemaName=schemaName;
        r2dbcRouterBuilder.user=user;
        return r2dbcRouterBuilder;
    }

    public static R2dbcRouterBuilder ofDefault() {
        R2dbcRouterBuilder r2dbcRouterBuilder = new R2dbcRouterBuilder();
        r2dbcRouterBuilder.databaseName = "boozsoft-nc";
        r2dbcRouterBuilder.schemaName = "public";
        r2dbcRouterBuilder.user = "postgres";
        return r2dbcRouterBuilder;
    }


//
//    public static void main(String[] args) {
//        r2dbcRouter()
//                .host("81.30.248.105")
//                .database("")
//                .schema("")
//                .build();
//    }
}
