package org.boozsoft.rest.stock;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.stock.StockAd;
import org.boozsoft.domain.entity.stock.StockAds;
import org.boozsoft.domain.vo.stock.StockAdVo;
import org.boozsoft.domain.vo.stock.StockAdsVo;
import org.boozsoft.repo.stock.StockAdRepository;
import org.boozsoft.repo.stock.StockAdsRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/stock_ad")
public class StockAdController {

    @Autowired
    StockAdRepository adRepository;
    @Autowired
    StockAdsRepository adsRepository;
    @Autowired
    DatabaseClient client;


    @PostMapping("findAllStockAd")
    public Mono<R> findAllStockAd(@RequestBody Map map){
        if (map.keySet().size() == 0) return Mono.just(R.error());
        // 单据编码
        String ccode = map.get("ccode")==null?"":map.get("ccode").toString();
        String iyear = map.get("iyear").toString();
        String action = map.get("action").toString();
        String currPdId = map.containsKey("curr") ? map.get("curr").toString() : "";
        return adRepository.findAllByIyearOrderByDdateAscCcodeAsc(iyear)
            .collectList().cache()
            .flatMap(list -> {
                if (list.size() == 0) {
                    return Mono.just(R.ok());
                } else {
                    StockAd master = null;
                    switch (action) {
                        case "curr":
                            long count = list.stream().filter(a -> a.getCcode().equals(currPdId)).count();
                            if(count>0){
                                master = list.get((list.stream().map(e -> e.getCcode()).collect(Collectors.toList())).indexOf(currPdId));
                                break;
                            }
                        case "tail":
                            List<StockAd> collect = list.stream().filter(a -> a.getCcode().equals(currPdId)).collect(Collectors.toList());

                            if(StrUtil.isNotBlank(ccode)){
                                list=list.stream().filter(a->a.getCcode().equals(ccode)).collect(Collectors.toList());
                                if(list.size()>0){
                                    master = list.get(0);
                                }else{
                                    master = collect.get(0);
                                }
                            }else{
                                master = list.get(list.size() - 1);
                            }
                            break;
                        case "prev":
                            if (StrUtil.isBlank(currPdId)) {
                                master = list.get(0);
                            } else {
                                int index = (list.stream().map(e -> e.getCcode()).collect(Collectors.toList())).indexOf(currPdId);
                                index = index == 0 ? 0 : index - 1;
                                master = list.get(index);
                            }
                            break;
                        case "next":
                            if (StrUtil.isBlank(currPdId)) {
                                master = list.get(0);
                            } else {
                                int index = (list.stream().map(e -> e.getCcode()).collect(Collectors.toList())).indexOf(currPdId);
                                index = index >= list.size() - 1 ? list.size() - 1 : index + 1;
                                master = list.get(index);
                            }
                            break;
                        default:
                            master = list.get(0);
                            break;
                    }
                    StockAd finalMaster = master;
                    return adsRepository.findAllByAdStyleAndCcodeOrderByDdateAscCcodeAscLineIdAsc(master.getAdStyle(), master.getCcode()).collectList().map(enlist -> {
                        if (enlist.size() > 0) {
                            finalMaster.setEntryList(JSON.toJSONString(enlist));
                        }
                        return R.ok(finalMaster);
                    });
                }
            });
    }

    @GetMapping("getNewStockAdNum")
    public Mono<R> getNewStockAdNum(){
        return adRepository.findAll().collectList()
        .flatMap(list->{
            int num= list.size()+1;
            String format = String.format("%04d", num);
            return Mono.just(format);
        }).map(R::ok);
    }

    @PostMapping("saveStockAd")
    @Transactional
    public Mono<R> saveStockAd(@RequestBody StockAd obj){
       return adRepository.save(obj).map(R::ok);
    }
    @PostMapping("saveStockAds")
    @Transactional
    public Mono<R> saveStockAds(@RequestBody List<StockAds> obj){
        return adsRepository.saveAll(obj).collectList().map(R::ok);
    }

    @PostMapping("delStockAd")
    @Transactional
    public Mono<R> delStockAd(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        return adRepository.findById(map.get("id").toString()).flatMap(db ->
                adsRepository.findAllByAdStyleAndCcodeOrderByDdateAscCcodeAscLineIdAsc(db.getAdStyle(), db.getCcode()).collectList()
                        .flatMap(list -> adsRepository.deleteAll(list).thenReturn("").flatMap(i -> adRepository.delete(db).thenReturn(""))) //修改
        ).map(o -> R.ok());
    }

    @PostMapping("delStockAdZip")
    @Transactional
    public Mono<R> delStockAdZip(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        Mono<Void> a=adRepository.deleteByCcode(map.get("ccode").toString()).then();
        Mono<Void> b=adsRepository.deleteByCcode(map.get("ccode").toString()).then();
        return Mono.zip(a,b).then(Mono.just(R.ok()));
    }


    /**
     * @description: 获取下游表【其他入库、其他出库】
     * @author: miao
     * @date: 2022/11/1 15:45
     * @param: [map]
     * @return: Mono<R>
     **/
    @PostMapping("getXyQTRKD_And_QTCKD")
    public Mono<R> getXyQTRKD_And_QTCKD(@RequestBody Map map) {
        String ccode=map.get("ccode").toString();
        return adRepository.getXyRkCk(ccode).map(R::ok).defaultIfEmpty(R.ok().setResult(""));
    }

    @PostMapping("getUnitRate")
    public Mono<R> getUnitRate(@RequestBody Map map){
        String cgUnitId=map.get("cgUnitId").toString();
        String ccode=map.get("ccode").toString();
        return adsRepository.getUnitRate(cgUnitId,ccode).map(a->R.ok().setResult(a)).defaultIfEmpty(R.ok().setResult(""));
    }

    @PostMapping("verifyDataState")
    public Mono<R> verifyDataState(@RequestBody Map map){
        // 操作类型【rowEdit（列表点击行跳转）,edit：修改,del：删除,audit：审核】
        String operation=map.get("operation").toString();
        // 单号>>>审核状态
        List<String> list= (List<String>) map.get("list");
        return adRepository.findAll().collectList()
        .flatMap(datalist->{
            String txt="1";
            for (int i = 0; i < list.size(); i++) {
                String ccode=list.get(i).split(">>>")[0];
                String bcheck=list.get(i).split(">>>")[1];
                long count = datalist.stream().filter(t -> operation.equals("rowEdit")?t.getCcode().equals(ccode):t.getCcode().equals(ccode)&&t.getBcheck().equals(bcheck)).count();
                if (count==0){txt="";break;}
            }
            return Mono.just(txt);
        }).map(R::ok);
    }
    //==================================== 列表 =====================================
    @PostMapping("findByStockAdTableList")
    public Mono<R> findByStockAdTableList(@RequestBody Map map) {
        String strDate=map.get("strDate").toString();
        String endDate=map.get("endDate").toString();
        String strNum=map.get("strNum").toString();
        String endNum=map.get("endNum").toString();
        String bomCode=map.get("bomCode").toString();
        Map<String, String> pageSearch = ((HashMap<String,  String>) map.get("pageSearch"));
        StringBuffer sb=new StringBuffer("where 1=1");
        if(StrUtil.isNotBlank(strNum)&&StrUtil.isNotBlank(endNum)){
            sb.append("and (right(ad.ccode,4) like '%"+strNum+"%'  or right(ad.ccode,4) like '%"+endNum+"%')");
        }
        if(StrUtil.isNotBlank(strDate)&&StrUtil.isNotBlank(endDate)){
            sb.append("and ad.ddate between '"+strDate+"' and '"+endDate+"'");
        }
        if(StrUtil.isNotBlank(bomCode)){
            sb.append("and ad.bom_code='"+bomCode+"'");
        }

        String sql="select ad.bcheck,\n" +
                "       ad.ddate,\n" +
                "       ad.ccode,\n" +
                "       case when ad.ad_style = 'ZZ' then '组装' else '拆卸' end as ad_style,\n" +
                "       bom.bom_name,\n" +
                "       ad.bom_ver,\n" +
                "       ad.bom_ver_name,\n" +
                "       (case\n" +
                "            when bom.unit_id = s.stock_unit_id then s.stock_unit_name\n" +
                "            else\n" +
                "                ((case\n" +
                "                      when bom.unit_id = s.stock_unit_id1 then s.stock_unit_name1\n" +
                "                      else\n" +
                "                          (case\n" +
                "                               when bom.unit_id = s.stock_unit_id2 then s.stock_unit_name2\n" +
                "                               else '' end) end)) end)         unit_name,\n" +
                "    ad.cnumber,\n" +
                "    ad.bom_level,\n" +
                "    ad.feiyong_je,\n" +
                "    dept.dept_name,\n" +
                "    psn.real_name as cmaker_name,\n" +
                "    psn2.real_name as bcheck_name\n" +
                "from stock_ad ad\n" +
                "         left join stock_bom bom on bom.bom_unique_id = ad.bom_code\n" +
                "         left join stock s on s.stock_num = bom.bom_id\n" +
                "         left join sys_department dept on dept.unique_code = ad.cdepcode\n" +
                "         left join _app_group_sys_user psn on psn.id = ad.cmaker\n" +
                "         left join _app_group_sys_user psn2 on psn2.id = ad.bcheck_user "+sb+" \n" +
                " order by ad.ccode";

        return client.sql(sql).fetch().all().collectList()
        .flatMap(list->{
            List<StockAdVo> listVo = JSON.parseArray(JSON.toJSONString(list), StockAdVo.class);
            if(StrUtil.isNotBlank(pageSearch.get("selectValue"))){
                if(pageSearch.get("selectType").equals("ccode")){
                    listVo=listVo.stream().filter(a->a.getCcode().contains(pageSearch.get("selectValue"))).collect(Collectors.toList());
                }else if(pageSearch.get("selectType").equals("bomName")){
                    listVo=listVo.stream().filter(a->a.getBomName().contains(pageSearch.get("selectValue"))).collect(Collectors.toList());
                }else if(pageSearch.get("selectType").equals("bomVer")){
                    listVo=listVo.stream().filter(a->a.getBomVer().contains(pageSearch.get("selectValue"))).collect(Collectors.toList());
                }
            }
            return Mono.just(listVo);
        }).map(R::ok);
    }

    @PostMapping("findByStockAdMXTableList")
    public Mono<R> findByStockAdMXTableList(@RequestBody Map map) {
        String strDate=map.get("strDate").toString();
        String endDate=map.get("endDate").toString();
        String strNum=map.get("strNum").toString();
        String endNum=map.get("endNum").toString();
        String bomCode=map.get("bomCode").toString();
        Map<String, String> pageSearch = ((HashMap<String,  String>) map.get("pageSearch"));
        StringBuffer sb=new StringBuffer("where 1=1");
        if(StrUtil.isNotBlank(strNum)&&StrUtil.isNotBlank(endNum)){
            sb.append("and (right(ad.ccode,4) like '%"+strNum+"%'  or right(ad.ccode,4) like '%"+endNum+"%')");
        }
        if(StrUtil.isNotBlank(strDate)&&StrUtil.isNotBlank(endDate)){
            sb.append("and ad.ddate between '"+strDate+"' and '"+endDate+"'");
        }
        if(StrUtil.isNotBlank(bomCode)){
            sb.append("and ad.bom_code='"+bomCode+"'");
        }

        String sql="select ad.bcheck,\n" +
                "       ad.ddate,\n" +
                "       ad.ccode,\n" +
                "       case when ad.ad_style = 'ZZ' then '组装' else '拆卸' end as ad_style,\n" +
                "       bom.bom_name,\n" +
                "       ad.bom_ver,\n" +
                "       ad.bom_ver_name,\n" +
                "       (CASE\n" +
                "            WHEN s.stock_measurement_type = '单计量' THEN\n" +
                "                ( SELECT sm.unit_name FROM sys_unit_of_mea sm WHERE sm.ID = ad.cunitid )\n" +
                "            ELSE ( SELECT sm.unit_name FROM sys_unit_of_mea_list sm WHERE sm.ID = ad.cunitid )\n" +
                "           END\n" +
                "           ) AS unit_name,"+
                "       ad.bom_level,\n" +
                "       ad.feiyong_je,\n" +
                "       dept.dept_name,\n" +
                "       psn.real_name                                        as cmaker_name,\n" +
                "       psn2.real_name                                       as bcheck_name,\n" +
                "       ads.cinvode,\n" +
                "       s.stock_name,\n" +
                "       s.stock_ggxh,\n" +
                "       s.stock_barcode,\n" +
                "       (case\n" +
                "            when ads.cg_unit_id = s.stock_unit_id then s.stock_unit_name\n" +
                "            else\n" +
                "                ((case\n" +
                "                      when ads.cg_unit_id = s.stock_unit_id1 then s.stock_unit_name1\n" +
                "                      else\n" +
                "                          (case\n" +
                "                               when ads.cg_unit_id = s.stock_unit_id2 then s.stock_unit_name2\n" +
                "                               else '' end) end)) end)         unit_name2,\n" +
                "    ads.cnumber,\n" +
                "    s.stock_unit_name,\n" +
                "    ads.base_quantity,\n" +
                "    ads.price,\n" +
                "    ads.icost,ads.fyft_isum,ads.icost_total,\n" +
                "    ads.ruku_code,\n" +
                "    ads.sjsh_money,\n" +
                "    ads.desh_money, case when ads.fc_style = 'Z' then '子件' else '父件' end as fc_style,\n" +
                "    pro.project_name,ads.cmemo\n" +
                "from stock_ads ads\n" +
                "         left join stock_ad ad on ad.ccode = ads.ccode\n" +
                "         left join stock_bom bom on bom.bom_unique_id = ad.bom_code\n" +
                "         left join stock s on s.stock_num = ads.cinvode\n" +
                "         left join sys_department dept on dept.unique_code = ad.cdepcode\n" +
                "         left join _app_group_sys_user psn on psn.id = ad.cmaker\n" +
                "         left join _app_group_sys_user psn2 on psn2.id = ad.bcheck_user\n" +
                "         left join project pro on pro.unique_code = ads.citem\n" +
                "where 1=1\n" +
                "order by ads.ccode,ads.fc_style";

        return client.sql(sql).fetch().all().collectList()
                .flatMap(list->{
                    List<StockAdsVo> listVo = JSON.parseArray(JSON.toJSONString(list), StockAdsVo.class);
                    if(StrUtil.isNotBlank(pageSearch.get("selectValue"))){
                        if(pageSearch.get("selectType").equals("ccode")){
                            listVo=listVo.stream().filter(a->a.getCcode().contains(pageSearch.get("selectValue"))).collect(Collectors.toList());
                        }else if(pageSearch.get("selectType").equals("bomName")){
                            listVo=listVo.stream().filter(a->a.getBomName().contains(pageSearch.get("selectValue"))).collect(Collectors.toList());
                        }else if(pageSearch.get("selectType").equals("bomVer")){
                            listVo=listVo.stream().filter(a->a.getBomVer().contains(pageSearch.get("selectValue"))).collect(Collectors.toList());
                        }
                    }
                    return Mono.just(listVo);
                }).map(R::ok);
    }
}