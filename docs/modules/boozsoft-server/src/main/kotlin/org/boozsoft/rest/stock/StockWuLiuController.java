package org.boozsoft.rest.stock;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.ReportEncodingRules;
import org.boozsoft.domain.entity.stock.StockJiesuan;
import org.boozsoft.domain.entity.stock.StockWuliu;
import org.boozsoft.domain.entity.stock.StockWulius;
import org.boozsoft.domain.vo.stock.StockWuliuVo;
import org.boozsoft.repo.ReportEncodingRulesRepository;
import org.boozsoft.repo.stock.StockSaleousingRepository;
import org.boozsoft.repo.stock.StockWuLiuRepository;
import org.boozsoft.repo.stock.StockWuLiusRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/stock/wuliu")
public class StockWuLiuController {

    @Autowired
    private StockWuLiuRepository wuLiuRepository;
    @Autowired
    private StockWuLiusRepository wuLiusRepository;
    @Autowired
    private StockSaleousingRepository stockSaleousingRepository;
    @Autowired
    private ReportEncodingRulesRepository encodingRulesRepository;
    @Autowired
    DatabaseClient client;


    @PostMapping("findWuliuCode")
    public Mono<R> findWuliuCode(String ccode) {
        return wuLiuRepository.findByCcode(ccode).map(a->R.ok().setResult(a));
    }

    @PostMapping("findBillByCondition")
    public Mono<R> findBillByCondition(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        // 单据编码
        String ccode = map.get("ccode")==null?"":map.get("ccode").toString();
        String type = map.get("type").toString();
        String iyear = map.get("iyear").toString();
        String action = map.get("action").toString();
        String currPdId = map.containsKey("curr") ? map.get("curr").toString() : "";
        return wuLiuRepository.findAllByIyearOrderByDdateDescCcodeDesc(iyear)
                .collectList().cache().flatMap(list -> {
                    if (list.size() == 0) {
                        return Mono.just(R.ok());
                    } else {
                        StockWuliu master = null;
                        switch (action) {
                            case "curr":
                                master = list.get((list.stream().map(e -> e.getCcode()).collect(Collectors.toList())).indexOf(currPdId));
                                break;
                            case "tail":
                                List<StockWuliu> collect = list.stream().filter(a -> a.getCcode().equals(currPdId)).collect(Collectors.toList());
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
                            default:
                                master = list.get(0);
                                break;
                        }
                        StockWuliu finalMaster = master;
                        return wuLiusRepository.findAllByCcodeOrderByCcodeAsc(master.getCcode()).collectList().map(enlist -> {
                            finalMaster.setEntryList(JSON.toJSONString(enlist));
                            return R.ok(finalMaster);
                        });
                    }
                });
    }


    @PostMapping("wuliuSave")
    @Transactional
    public Mono<R> wuliuSave(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        Map<String, Object> obj = assemblyCorrespondingRulesData(map);
        StockWuliu master = (StockWuliu) obj.get("master");
        List<StockWulius> sub = (List<StockWulius>) obj.get("sub");
        return wuLiuRepository.save(master).flatMap(db -> {
            return wuLiusRepository.saveAll(sub).collectList();
        }).map(o -> R.ok());
    }

    @PostMapping("wuliuDel")
    @Transactional
    public Mono<R> wuliuDel(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        return wuLiuRepository.findById(map.get("id").toString()).flatMap(db ->
                wuLiusRepository.findAllByCcodeOrderByCcodeAsc(db.getCcode()).collectList()
                        .flatMap(list -> wuLiusRepository.deleteAll(list).thenReturn("").flatMap(i -> wuLiuRepository.delete(db).thenReturn("")))
        ).map(o -> R.ok());
    }

    // 修改统一出库单主表-物流单、日期、备注
    @PostMapping("editStockSaleousingDelivery")
    @Transactional
    public Mono<R> editStockSaleousingDelivery(@RequestBody Map map) {
        String deliveryDate=map.get("deliveryDate").toString();
        String deliveryId=map.get("deliveryId").toString();
        String deliveryExplain=map.get("deliveryExplain").toString();
        return stockSaleousingRepository.editStockSaleousingDelivery(deliveryDate,deliveryId,deliveryExplain,Arrays.asList(map.get("ccode").toString().split(","))).then(Mono.just(R.ok()));
    }

    @PostMapping("code")
    public Mono<R> lastCode(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        String date = map.get("date").toString();
        String type = map.get("type").toString();
        String key = map.get("key").toString();
        return wuLiuRepository.findAllByIyearOrderByDdateDescCcodeDesc(date.substring(0,4)).collectList().flatMap(list->{
            return encodingRulesRepository.findByFileType(key).flatMap(tips->{
                ReportEncodingRules obj = tips;
//                StockWarehousing it = tips.getT2();
                String customize = map.containsKey("prefix")?map.get("prefix").toString():"";
                StringBuilder pre = new StringBuilder("");
                int l = 4;
                if (obj.getId() == null){
                    pre.append(customize);
                }else {
                    l = Integer.parseInt(obj.getSerialNumLength());
                    String separation = obj.getDelimiter().equals("3")?"-":obj.getDelimiter().equals("2")?".":"";
                    if (obj.getPrefixOneIs().equals("true"))
                        pre.append((StrUtil.isBlank(customize)?obj.getPrefixOneCustomize():customize)+separation);
                    if (obj.getPrefixTwoIs().equals("true"))
                        pre.append((date.substring(0,7).replace("-",""))+separation);
                }
                if(list.size()>0){
                    List<StockWuliu> collect = list.stream().filter(a -> a.getCcode().contains(pre.toString())).collect(Collectors.toList());
                    if(collect.size()>0){
                        collect.sort(Comparator.comparing(StockWuliu::getCcode).reversed());
                        int t = Integer.parseInt(collect.get(0).getCcode().replace(pre.toString(),""))+1;
                        return Mono.just(pre.toString()+String.format("%0"+l+"d",t));
                    }
                    return Mono.just(pre.toString()+"0001");
                }else{
                    return Mono.just(pre.toString()+"0001");
                }
            });
        }).map(R::ok);
    }

    private Map<String, Object> assemblyCorrespondingRulesData(Map map) {
        HashMap<String, Object> resut = new HashMap<>();
        StockWuliu warehousing = new StockWuliu();
        warehousing.setId(map.containsKey("id") ? map.get("id").toString() : null).setCcode(map.containsKey("ccode") ? map.get("ccode").toString() : null) //单号
                .setDdate(map.containsKey("ddate") ? map.get("ddate").toString().substring(0, 10) : null) // 日期
                .setIyear(map.containsKey("ddate") ? map.get("ddate").toString().substring(0, 4) : null)
                .setCmaker(map.containsKey("cmaker") ? map.get("cmaker").toString() : null) // 制单人
                .setCvencode(map.containsKey("cvencode") ? map.get("cvencode").toString() : null) // 客户
                .setCdepcode(map.containsKey("cdepcode") ? map.get("cdepcode")==null?null:map.get("cdepcode").toString() : null) // 部门
                .setCpersoncode(map.containsKey("cpersoncode") ? map.get("cpersoncode")==null?null:map.get("cpersoncode").toString() : null) // 业务员
                .setCuserTel(map.containsKey("cuserTel") ? map.get("cuserTel")==null?null:map.get("cuserTel").toString() : null) // 联系人&电话
                .setCAddress(map.containsKey("caddress") ? map.get("caddress")==null?null:map.get("caddress").toString() : null) // 送货地址

                .setWuliuSup(map.containsKey("wuliuSup") ? map.get("wuliuSup")==null?null:map.get("wuliuSup").toString() : null) // 供应商
                .setWuliuId(map.containsKey("wuliuId") ? map.get("wuliuId")==null?null:map.get("wuliuId").toString() : null) // 物流单号
                .setWuliuTel(map.containsKey("wuliuTel") ? map.get("wuliuTel")==null?null:map.get("wuliuTel").toString() : null) // 物流电话
                .setStockSaleousingCode(map.containsKey("stockSaleousingCode") ? map.get("stockSaleousingCode")==null?null:map.get("stockSaleousingCode").toString() : null)
                .setCmemo(map.containsKey("cmemo") ? map.get("cmemo")==null?null: map.get("cmemo").toString(): null)
                .setCmakeDate(new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(new Date()));

        String entryList = map.containsKey("entryList") ? map.get("entryList").toString() : ""; // 表体
        List<StockWulius> entrys = JSON.parseArray(entryList, StockWulius.class);
        BigDecimal squantitySum = new BigDecimal(0);
        BigDecimal squantity1Sum = new BigDecimal(0);
        BigDecimal squantity2Sum = new BigDecimal(0);
        for (StockWulius entry : entrys) {
            entry.setIyear(warehousing.getIyear())
                    .setSourceDate(entry.getDdate()).setDdate(warehousing.getDdate())
                    .setSourceCode(entry.getCcode()).setSourceType(entry.getBillStyle())
                    .setCcode(warehousing.getCcode()).setSourceCode(entry.getLineCode());


            squantitySum = squantitySum.add(new BigDecimal(entry.getBaseQuantity() == null ? "0" : entry.getBaseQuantity()));
            squantity1Sum = squantity1Sum.add(new BigDecimal(entry.getSubQuantity1() == null ? "0" : entry.getSubQuantity1()));
            squantity2Sum = squantity2Sum.add(new BigDecimal(entry.getSubQuantity2() == null ? "0" : entry.getSubQuantity2()));
        }
        warehousing.setSquantity(keepDecimals(squantitySum, 10).toString()).setSquantity1(keepDecimals(squantity1Sum, 10)).setSquantity2(keepDecimals(squantity2Sum, 10));
        resut.put("master", warehousing);
        resut.put("sub", entrys);
        return resut;
    }
    private String keepDecimals(BigDecimal b, int len) {
        StringBuilder s = new StringBuilder("");
        for (int i = 0; i < len; i++) s.append("0");
        DecimalFormat decimalFormat = new DecimalFormat("0." + s + "#");
        BigDecimal value = b.setScale(len, BigDecimal.ROUND_HALF_UP);
        return decimalFormat.format(value);
    }


    @PostMapping("findAllWuliusList")
    public Mono<R> findAllWuliusList(@RequestBody Map map) {
        String dataType=map.get("dataType").toString();
        String cus=map.get("cus").toString();

        StringBuffer sb=new StringBuffer("");
        if(StrUtil.isNotBlank(dataType)){
            sb.append(" and wl.bcheck='"+dataType+"' ");
        }
        if(StrUtil.isNotBlank(cus)){
            sb.append(" and wl.wuliu_sup='"+cus+"' ");
        }
        String sql="select wl.bcheck,\n" +
                "       wl.cmemo,\n" +
                "       wl.ddate,\n" +
                "       wl.ccode,\n" +
                "       wl.cvencode,\n" +
                "       cus.cust_name                    as cven_name,\n" +
                "       (select string_agg(psn.psn_name, ',')\n" +
                "        from stock_wuliu_songhuo sh\n" +
                "                 left join sys_psn psn on psn.id = sh.wuliu_user\n" +
                "        where sh.wuliu_code = wl.ccode) as psn_name,\n" +
                "       dept.dept_name,\n" +
                "       psn.psn_name                     as cperson_name,\n" +
                "       wl.cuser_tel,\n" +
                "       wl.c_address,\n" +
                "       sup.cust_name,\n" +
                "       wl.wuliu_id,\n" +
                "       wl.wuliu_tel,\n" +
                "        (select sum(cast(sws.base_quantity as float)) from stock_wulius sws where sws.ccode=wl.ccode) cnumber\n" +
                "from stock_wuliu wl\n" +
                "         left join customer cus on cus.id = wl.cvencode\n" +
                "         left join sys_department dept on dept.unique_code = wl.cdepcode\n" +
                "         left join sys_psn psn on psn.id = wl.cpersoncode\n" +
                "         left join supplier sup on sup.id = wl.wuliu_sup where 1=1 "+sb+" ";
        return client.sql(sql).fetch().all().collectList()
        .flatMap(list->{
            List<StockWuliuVo> a=JSON.parseArray(JSON.toJSONString(list),StockWuliuVo.class);
            return Mono.just(a);
        }).map(R::ok);
    }
}
