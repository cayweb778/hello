package org.boozsoft.service.dataBase;

import org.boozsoft.repo.SysAccountPeriodRepository;
import org.springbooz.datasource.r2dbc.domain.vo.R2dbcRouterBuilder;
import org.springbooz.datasource.r2dbc.service.R2dbcRouterLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.util.Map;

/**
 * 数据库业务
 */
@Service
public class DataBaseService{
    @Autowired
    R2dbcRouterLoader r2dbcRouterLoader;
    @Autowired
    R2dbcEntityTemplate entityTemplate;
    @Autowired
    DatabaseClient client;
    @Autowired
    SysAccountPeriodRepository sysAccountPeriodRepository;

    /**
     * 判断表是否存在
     * @param baseName 表名
     * @param m         包含sql语句
     * @return
     */
    public Mono<Long> findByDataBaseName(String baseName,Map m){
       return r2dbcRouterLoader.bind(() -> client.sql("select * from pg_tables where tablename='"+baseName+"'").fetch().all().collectList().flatMap(item->{ return item.size()>0?Mono.just(1L):createDataBase(baseName,m.get("sql").toString()); }), R2dbcRouterBuilder.ofDefault());
    }

    /**
     * 执行创建表以及赋予权限
     * @param baseName  表名
     * @param sql       语句
     * @return
     */
    public Mono<Long> createDataBase(String baseName,String sql){
        System.out.println("执行创建表以及赋予权限");
        return client.sql(sql).fetch().rowsUpdated().flatMap(tx->{
        // 获取区间中的模式名称
        return sysAccountPeriodRepository.findAll().collectList()
            .flatMap(periodlist->{
                    StringBuffer sb1=new StringBuffer();
                    StringBuffer sb2=new StringBuffer();
                    StringBuffer sb3=new StringBuffer();
                    StringBuffer sb4=new StringBuffer();
                    periodlist.stream().forEach(item->{
                        // 删除行安全策略
                        sb1.append("DROP policy IF EXISTS \"" + item.getAccountMode() + "-table-" + baseName + "\" on \"" + baseName + "\";");
                        // 数据库权限
                        sb2.append("GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO \""+item.getAccountMode()+"\";\n");
                        // 行级权限
                        sb3.append("create policy \""+item.getAccountMode()+"-"+baseName+"\" on "+baseName+"  for all to \""+item.getAccountMode()+"\"  using (tenant_id  = '"+item.getAccountMode()+"');\n");
                    });
                    // 租户间数据出现交叉,需执行
                    sb4.append("ALTER TABLE "+baseName+" ENABLE ROW LEVEL SECURITY;\n");
                   return DatabaseClient.create(client.getConnectionFactory()).sql(sb1.toString()).fetch().rowsUpdated()
                       .flatMap(a->{
                          return DatabaseClient.create(client.getConnectionFactory()).sql(sb2.toString()).fetch().rowsUpdated()
                               .flatMap(b->{
                                   return DatabaseClient.create(client.getConnectionFactory()).sql(sb3.toString()).fetch().rowsUpdated()
                                       .flatMap(c->{
                                           return DatabaseClient.create(client.getConnectionFactory()).sql(sb4.toString()).fetch().rowsUpdated()
                                               .map(d -> Tuples.of(a,b,c,d));
                                       });
                               });
                       });
                });
        }).map(a->1L);
    }
}
