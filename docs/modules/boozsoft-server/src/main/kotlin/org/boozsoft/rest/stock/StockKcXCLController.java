package org.boozsoft.rest.stock;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.vo.stock.StockCurrentLackVo;
import org.boozsoft.repo.stock.StockCurrentstockRepository;
import org.boozsoft.service.stock.StockXCLService;
import org.boozsoft.util.BigDecimalUtils;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 账表-------存货现存量查询
 */
@Slf4j
@RestController
@RequestMapping("/stockKcXCL")
public class StockKcXCLController {

    @Autowired
    StockCurrentstockRepository currentstockRepository;
    @Autowired
    StockXCLService stockXCLService;
    @Autowired
    DatabaseClient client;

    // 账表现存量和批次现存量查询
    @PostMapping("findByStockXCL")
    public Mono<R> findByStockXCL(@RequestBody Map map) {
        StringBuffer sb1 = new StringBuffer("where 1=1");
        StringBuffer sb2 = new StringBuffer("where 1=1");
        StringBuffer sb3 = new StringBuffer("");
        StringBuffer sb4 = new StringBuffer("");
        StringBuffer cwhcodeColumn = new StringBuffer("");
        StringBuffer cwhcodeColumn1 = new StringBuffer("");
        String cinvode = "";
        String cinvodeClass = "";

        // ******************* 账表查询通用参数 ****************
        String queryType = map.get("queryType").toString();   // xcl \ pcxcl
        String cwhcode = map.get("ckId").toString();
        String iyear = map.get("iyear").toString();
        Map<String, String> searchMap = ((HashMap<String,  String>) map.get("searchMap"));
        // 参数设置：入库保存状态就是现存量 标志 1是查询所有 0 查询已审核
        String rkBcheck = map.get("rkBcheck") == null ? "0" : map.get("rkBcheck").toString();
        String ckBcheck = map.get("ckBcheck") == null ? "0" : map.get("ckBcheck").toString();
        List<String> stockId = (List<String>) map.get("stockId");
        List<String> stockClassId = (List<String>) map.get("stockClassId");
        // ***************现存量查询参数****************
        String endDate = map.get("endDate") == null ? "" : map.get("endDate").toString();
        // ***************批次现存量查询参数****************
        String batchId = map.get("batchId") == null ? "" : map.get("batchId").toString();
        String dpdate = map.get("dpdate") == null ? "" : map.get("dpdate").toString();
        String dvdate = map.get("dvdate") == null ? "" : map.get("dvdate").toString();

        if (queryType.equals("pcxcl")) {
            sb3.append(" and temp.batch_id is not null ");
        }
        if (queryType.equals("xcl")&&StrUtil.isNotBlank(cwhcode)) {
            cwhcodeColumn.append(",sbb.stock_cangku_id as cwhcode");
            cwhcodeColumn1.append(",sws.cwhcode");
        }
        if (StrUtil.isNotBlank(batchId)) {
            sb3.append("and temp.batch_id='"+batchId+"'");
        }
        if (StrUtil.isNotBlank(dpdate)) {
            sb3.append(" and temp.dpdate between '" + dpdate.split(",")[0] + "' and '" + dpdate.split(",")[1] + "' ");
        }
        if (StrUtil.isNotBlank(dvdate)) {
            sb3.append(" and temp.dvdate between '" + dpdate.split(",")[0] + "' and '" + dpdate.split(",")[1] + "' ");
        }
        if (StrUtil.isNotBlank(endDate)) {
            sb1.append(" and sbb.ddate between '" + endDate.substring(0,4)+"-01-01" + "' and '"+endDate+"' ");
            sb2.append(" and sws.ddate between '" + endDate.substring(0,4)+"-01-01" + "' and '"+endDate+"' ");
        }
        if (stockId != null && stockId.size() > 0) {
            for (int i = 0; i < stockId.size(); i++) {
                cinvode += "'" + stockId.get(i) + "',";
            }
            if (StrUtil.isNotBlank(cinvode)) {
                sb1.append(" and sbb.stock_id in (" + cinvode.substring(0, cinvode.length() - 1) + ") ");
                sb2.append(" and sws.cinvode in (" + cinvode.substring(0, cinvode.length() - 1) + ") ");
            }
            sb4.append(" and temp.cinvode in (" + cinvode.substring(0, cinvode.length() - 1) + ") ");
        }
        if (stockClassId != null && stockClassId.size() > 0) {
            for (int i = 0; i < stockClassId.size(); i++) {
                cinvodeClass += "'" + stockClassId.get(i) + "',";
            }
            if (StrUtil.isNotBlank(cinvodeClass)) {
                sb3.append(" and ck.stock_class in (" + cinvodeClass.substring(0, cinvodeClass.length() - 1) + ") ");
            }
        }
        if (StrUtil.isNotBlank(cwhcode)) {
            sb1.append(" and sbb.stock_cangku_id='" + cwhcode + "' ");
            sb2.append(" and sws.cwhcode='" + cwhcode + "' ");
        }

        // 做过单据的存货---------批次现存量
        String pcxclsql = "select distinct temp.*, " +
                "                ck.stock_num,\n" +
                "                ck.stock_name,\n" +
                "                ck.stock_ggxh,\n" +
                "                ck.stock_unit_id,\n" +
                "                ck.stock_unit_id1,\n" +
                "                ck.stock_unit_id2,\n" +
                "                ck.stock_unit_name,\n" +
                "                ck.stock_unit_name1,\n" +
                "                ck.stock_unit_name2," +
                "(select mx.conversion_rate from sys_unit_of_mea_list mx where mx.id=ck.stock_unit_id) rate,\n" +
                "(select mx.conversion_rate from sys_unit_of_mea_list mx where mx.id=ck.stock_unit_id1) rate1,\n" +
                "coalesce((select mx.conversion_rate from sys_unit_of_mea_list mx where mx.id=ck.stock_unit_id2),'0') rate2 " +
                " from (select sbb.stock_id as cinvode, sbb.stock_cangku_id as cwhcode, sbb.batch_number as batch_id, sbb.dpdate, sbb.dvdate\n" +
                "               from stock_begin_balance sbb " + sb1 + " union all select distinct sws.cinvode, sws.cwhcode, sws.batch_id, sws.dpdate, sws.dvdate from stock_warehousings sws where 1 = 1 and sws.bill_style = 'QC' union all \n" +
                "               select distinct sws.cinvode, sws.cwhcode, sws.batch_id, sws.dpdate, sws.dvdate\n" +
                "               from stock_warehousings sws " + sb2 + " and sws.bill_style!='CGDD' union all\n" +
                "               select distinct sws.cinvode, sws.cwhcode, sws.batch_id, sws.dpdate, sws.dvdate\n" +
                "               from stock_saleousings sws " + sb2 + ") as temp left join stock ck on ck.stock_num = temp.cinvode where temp.cinvode is not null and temp.cwhcode is not null " + sb3 + " "+sb4+" ";

        // 做过单据的存货---------现存量
        String xclsql = "select distinct temp.*, " +
                "                ck.stock_num,\n" +
                "                ck.stock_name,\n" +
                "                ck.stock_ggxh,\n" +
                "                ck.stock_unit_id,\n" +
                "                ck.stock_unit_id1,\n" +
                "                ck.stock_unit_id2,\n" +
                "                ck.stock_unit_name,\n" +
                "                ck.stock_unit_name1,\n" +
                "                ck.stock_unit_name2," +
                "(select mx.conversion_rate from sys_unit_of_mea_list mx where mx.id=ck.stock_unit_id) rate,\n" +
                "(select mx.conversion_rate from sys_unit_of_mea_list mx where mx.id=ck.stock_unit_id1) rate1,\n" +
                "coalesce((select mx.conversion_rate from sys_unit_of_mea_list mx where mx.id=ck.stock_unit_id2),'0') rate2 " +
                " from (select sbb.stock_id as cinvode "+cwhcodeColumn+" \n" +
                "               from stock_begin_balance sbb " + sb1 + " union all select distinct sws.cinvode from stock_warehousings sws where 1 = 1 and sws.bill_style = 'QC' union all \n" +
                "               select distinct sws.cinvode "+cwhcodeColumn1+"\n" +
                "               from stock_warehousings sws " + sb2 + " and sws.bill_style!='CGDD' union all\n" +
                "               select distinct sws.cinvode "+cwhcodeColumn1+"\n" +
                "               from stock_saleousings sws " + sb2 + " and sws.bill_style != 'XSDD') as temp left join stock ck on ck.stock_num = temp.cinvode where temp.cinvode is not null " + sb3 + " "+sb4+" ";
        return client.sql(queryType.equals("xcl")?xclsql:pcxclsql).fetch().all().collectList()
                .flatMapMany(skList -> Flux.fromIterable(JSON.parseArray(JSON.toJSONString(skList), StockCurrentLackVo.class)))
                .doOnNext(p -> p.setBaseQuantity(BigDecimal.ZERO).setXcl(BigDecimal.ZERO))
                .index((i, it) -> {
                    if (queryType.equals("xcl")) {
                        it.setBatchId(null).setDpdate(null).setDvdate(null);
                    }
                    List<StockCurrentLackVo> templist = new ArrayList<>();
                    templist.add(it);
                    return stockXCLService.verifyStockXCLList(endDate,queryType, rkBcheck, ckBcheck, "0", iyear, templist)
                            .map(tt -> {
                                List<StockCurrentLackVo> monolist = (List<StockCurrentLackVo>) tt.getResult();
                                BigDecimal reduce = monolist.stream().map(StockCurrentLackVo::getXcl).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                                it.setXcl(reduce);
                                log.info("仓库下存货批号列表-现存量：" + it);
                                return it;
                            });
                })
                .flatMap(it -> it)
                .collectList()
                .flatMap(list -> {
                    list=list.stream().filter(a->!a.getXcl().equals(BigDecimal.ZERO)||!a.getKeyong().equals(BigDecimal.ZERO)||!a.getMidWayDh().equals(BigDecimal.ZERO)||!a.getMidWayRk().equals(BigDecimal.ZERO)||!a.getMidWayXh().equals(BigDecimal.ZERO)||!a.getMidWayCk().equals(BigDecimal.ZERO)).collect(Collectors.toList());
                    if (queryType.equals("pcxcl")) {
                        list=list.stream().filter(xcl -> StrUtil.isNotBlank(xcl.getCinvode())&&StrUtil.isNotBlank(xcl.getBatchId())).sorted(Comparator.comparing(StockCurrentLackVo::getCinvode).thenComparing(StockCurrentLackVo::getBatchId)).distinct().collect(Collectors.toList());
                    }else{
                        list=list.stream().filter(xcl -> StrUtil.isNotBlank(xcl.getCinvode())).sorted(Comparator.comparing(StockCurrentLackVo::getCinvode)).distinct().collect(Collectors.toList());
                    }
                    if(StrUtil.isNotBlank(searchMap.get("selectVal"))){
                        list=list.stream().filter(a->searchMap.get("selectType").equals("stockNum")?a.getStockNum().contains(searchMap.get("selectVal")):a.getStockName().contains(searchMap.get("selectVal"))).collect(Collectors.toList());
                    }
                    return Mono.just(list);
                })
                .map(R::ok);
    }

    // 获取现存两大于0的存货
    @PostMapping("findXclByZero")
    public Mono<R> findXclByZero(@RequestBody Map map) {
        return currentstockRepository.findByStockXCLThanZero(map.get("year").toString()).collectList().map(R::ok);

    }

    @PostMapping("findXclListByYear")
    public Mono<R> findXclListByYear(@RequestBody Map map) {
        return currentstockRepository.findAllByIyearAndBatchIdIsNotNullOrderByInvCode(map.get("year").toString()).collectList().map(list -> CollUtil.sort(list, (o1, o2) -> Integer.valueOf(o1.getInvCode()).compareTo(Integer.valueOf(o2.getInvCode())))).map(R::ok);
    }
}
