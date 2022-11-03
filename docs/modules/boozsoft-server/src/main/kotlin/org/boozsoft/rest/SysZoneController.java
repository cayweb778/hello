package org.boozsoft.rest;

import org.boozsoft.domain.entity.SysZone;
import org.boozsoft.repo.SysZoneRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 行政区划表
 */
@RestController
@RequestMapping("/sys/zone")
public class SysZoneController {

    @Autowired
    DatabaseClient client;
    @Autowired
    R2dbcEntityTemplate entityTemplate;
    @Autowired
    SysZoneRepository sysZoneRepository;


    @GetMapping("findAllProvince")
    public Mono<R> findAllProvince(){
        return sysZoneRepository.findAll().collectList()
                .map(item->{
                    List<Map<String, Object>> list2 = null;
                    try {
                        list2 = getChild(100000, item);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return list2;
                }).map(item->R.ok().setResult(item));
    }

    public List<Map<String, Object>> getChild(Integer pid, List<SysZone> allList) throws Exception {
        List<Map<String, Object>> childList = new ArrayList<>();//用于保存子节点的list;
        for (SysZone ms : allList) {
            if (ms.getPid().equals(pid)) {//判断传入的父id是否等于自身的，如果是，就说明自己是子节点
                Map<String, Object> map = new HashMap<>();
                map.put("value", String.valueOf(ms.getId()));
                map.put("label", ms.getZoneName());
                map.put("children", new Object[]{});
                childList.add(map); //加入子节点
            }
        }
        for (Map<String, Object> map : childList) {//遍历子节点，继续递归判断每个子节点是否还含有子节点
            List<Map<String, Object>> tList = getChild(Integer.valueOf(map.get("value").toString()), allList);
            map.put("children", tList);
        }
        return childList;
    }

    /**
     * 获取市信息
     * @return
     */
    @PostMapping("findByCity")
    public Mono<R> findByCity(String province){
        return client.sql("SELECT city from sys_zone where province='"+province+"' GROUP BY city").fetch().all().collectList().map(item->R.ok().setResult(item));
    }
    /**
     * 获取 区/县 信息
     * @return
     */
    @PostMapping("findByDistrict")
    public Mono<R> findByDistrict(String province,String city){
        return client.sql("SELECT district from sys_zone where province='"+province+"' and city='"+city+"' GROUP BY district").fetch().all().collectList().map(item->R.ok().setResult(item));
    }

    @PostMapping("findByZoneId")
    public Mono<R> findByZoneId(String id){
        return client.sql("SELECT * from sys_zone where id='"+id+"'").fetch().all().collectList().map(item->R.ok().setResult(item));
    }
}
