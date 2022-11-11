package org.boozsoft.rest.stock;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.stock.StockJiesuan;
import org.boozsoft.domain.entity.stock.StockJiesuans;
import org.boozsoft.domain.vo.stock.StockJiesuanVo;
import org.boozsoft.repo.stock.StockJiesuanRepository;
import org.boozsoft.repo.stock.StockJiesuansRepository;
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
@RequestMapping("/stockJiesuan")
public class StockJiesuanController {
    @Autowired
    StockJiesuanRepository stockJiesuanRepository;
    @Autowired
    StockJiesuansRepository stockJiesuansRepository;
    @Autowired
    DatabaseClient client;

    @PostMapping("saveJiesuanPojo")
    @Transactional
    public Mono<R> saveJiesuanPojo(@RequestBody StockJiesuan pojo){
        return stockJiesuanRepository.save(pojo).map(R::ok);
    }
    @PostMapping("saveJiesuansPojo")
    @Transactional
    public Mono<R> saveJiesuansPojo(@RequestBody List<StockJiesuans> pojo){
        return stockJiesuansRepository.saveAll(pojo).collectList().map(R::ok);
    }
    @GetMapping("getNewStockJiesuanNum")
    public Mono<R> getNewStockJiesuanNum(){
        return stockJiesuanRepository.findAll().collectList()
        .flatMap(list->{
           int num= list.size()+1;
            String format = String.format("%04d", num);
           return Mono.just(format);
        }).map(R::ok);
    }

    @PostMapping("delJiesuansByCcodeRuku")
    @Transactional
    public Mono<R> delJiesuansByCcodeRuku(String ccodeRuku){
        Mono<Void> sj = stockJiesuanRepository.deleteByCcodeRuku(ccodeRuku).then();
        Mono<Void> sjs = stockJiesuansRepository.deleteByCcodeRuku(ccodeRuku).then();
        return Mono.zip(sj,sjs).thenReturn(R.ok());
    }
    @PostMapping("deleteByCcodeDaohuo")
    @Transactional
    public Mono<R> deleteByCcodeDaohuo(String ccodeRuku){
        Mono<Void> sj = stockJiesuanRepository.deleteByCcodeRuku(ccodeRuku).then();
        Mono<Void> sjs = stockJiesuansRepository.deleteByCcodeDaohuo(ccodeRuku).then();
        return Mono.zip(sj,sjs).thenReturn(R.ok());
    }

    @PostMapping("delJiesuansByCcode")
    @Transactional
    public Mono<R> delJiesuansByCcode(String ccode){
        Mono<Void> sj = stockJiesuanRepository.deleteByCcode(ccode).then();
        Mono<Void> sjs = stockJiesuansRepository.deleteByCcode(ccode).then();
        return Mono.zip(sj,sjs).thenReturn(R.ok());
    }

    @PostMapping("findBillByCondition")
    public Mono<R> findBillByCondition(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        String type = map.get("type").toString();
        String iyear = map.get("iyear").toString();
        String action = map.get("action").toString();
        String currPdId = map.containsKey("curr") ? map.get("curr").toString() : "";
        String bdocum = map.containsKey("bdocum") ? map.get("bdocum").toString() : "";
        // 单据编码
        String ccode = map.get("ccode")==null?"":map.get("ccode").toString();
        return stockJiesuanRepository.findAllByIyear(iyear)
        .filter(it->bdocum.equals("")?true:it.getBdocumStyle().equals(bdocum))
        .collectList().cache()
        .flatMap(list -> {
            if (list.size() == 0) {
                return Mono.just(R.ok());
            } else {
                StockJiesuan master=null;
                switch (action) {
                    case "curr":
                        master = list.get((list.stream().map(e -> e.getCcode()).collect(Collectors.toList())).indexOf(currPdId));
                        break;
                    case "tail":
                        List<StockJiesuan> collect = list.stream().filter(a -> a.getCcode().equals(currPdId)).collect(Collectors.toList());
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
                StockJiesuan finalMaster = master;
                return stockJiesuansRepository.findAllByCcode(master.getCcode())
                .collectList()
                .map(enlist -> {
                    if (enlist.size() > 0) finalMaster.setEntryList(JSON.toJSONString(enlist));
                    return R.ok(finalMaster);
                });
            }
        });
    }

    @PostMapping("findAllStockJiesuanList")
    public Mono<R> findAllStockJiesuanList(@RequestBody Map map) {
        // 1期间\2日期
        String dateType=map.get("dateType").toString();
        String start=map.get("strDate").toString();
        String end=map.get("endDate").toString();
        String sup=map.get("sup").toString();
        String cinvode=map.get("cinvode").toString();
        Map<String, String> pageSearch = ((HashMap<String,  String>) map.get("pageSearch"));

        StringBuffer sb=new StringBuffer("where");
        if(StrUtil.isNotBlank(start)){
            String str=start;
            String str2=end;
            if(dateType.equals("1")){
                str=start.substring(0,4)+"-"+start.substring(4)+"-01";
                str2=end.substring(0,4)+"-"+end.substring(4)+"-31";
            }
            sb.append(" sj.ddate between '"+str+"' and '"+str2+"' ");
        }
        if(StrUtil.isNotBlank(sup)){
            sb.append(StrUtil.isNotBlank(start)?" and ":"");
            sb.append(" sj.cvencode_js='"+sup+"' ");
        }
        if(StrUtil.isNotBlank(cinvode)){
            sb.append(StrUtil.isNotBlank(sup)?" and ":"");
            sb.append(" sj.cinvode='"+cinvode+"' ");
        }
        if(StrUtil.isBlank(start)&&StrUtil.isBlank(sup)&&StrUtil.isBlank(cinvode)){
            sb.setLength(0);
        }

        String sql="select sup.cust_abbname as cust_name,sup.cust_code,sj.*,\n" +
                "       sum(cast(sjs.quantity_ruku as float))   quantity_ruku,\n" +
                "       sum(cast(sjs.quantity_daohuo as float)) quantity_daohuo,\n" +
                "       sum(cast(sjs.price_zg as float))        price_zg,\n" +
                "       sum(cast(sjs.icost_zg as float))        icost_zg,\n" +
                "       sum(cast(sjs.price_js as float))        price_js,\n" +
                "       sum(cast(sjs.icost_js as float))        icost_js\n" +
                "from stock_jiesuan sj\n" +
                "        left join supplier sup on sup.id=sj.cvencode left join stock_jiesuans sjs on sjs.ccode = sj.ccode "+sb+" \n" +
                "group by sj.id, sj.tenant_id, sj.iyear, sj.ccode, cmaker, sj.ddate, bdocum_style, cvencode, sj.cvencode_js, cdepcode,\n" +
                "         cpersoncode, sj.citemcode, sj.ccode_ruku,sup.cust_abbname,sup.cust_code";
        return client.sql(sql).fetch().all().collectList()
                .flatMap(list->{
                    List<StockJiesuanVo> listVo = JSON.parseArray(JSON.toJSONString(list), StockJiesuanVo.class);

                    if(pageSearch!=null&&StrUtil.isNotBlank(pageSearch.get("selectValue"))){
                        if(pageSearch.get("selectType").equals("ccode")){
                            listVo=listVo.stream().filter(a->a.getCcode().contains(pageSearch.get("selectValue"))).collect(Collectors.toList());
                        }else if(pageSearch.get("selectType").equals("supName")){
                            listVo=listVo.stream().filter(a->a.getCustName().contains(pageSearch.get("selectValue"))).collect(Collectors.toList());
                        }
                    }
                    return Mono.just(listVo);
                })
                .map(R::ok);
    }

    @PostMapping("findByCcodeStockJiesuanList")
    public Mono<R> findByCcodeStockJiesuanList(String ccode){
        return stockJiesuansRepository.findAllByCcode(ccode).collectList().map(R::ok);
    }

    /**
     * @description: 到货单、退货单、采购发票、订单=查询采购结算单
     * @author: miao
     * @date: 2022/10/8 11:54
     * @param: [map]
     * @return: Mono<R>
     **/
    @PostMapping("findJieSuanByCaigouDaoHuo")
    public Mono<R> findJieSuanByCaigouDaoHuo(@RequestBody Map map){
        String ccodeDaohuo=map.get("syccode").toString();
        return stockJiesuansRepository.findAllByCcodeDaoHuo(ccodeDaohuo).collectList().map(R::ok);
    }

}
