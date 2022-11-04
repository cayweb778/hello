package org.boozsoft.rest.stock;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.stock.StockWarehousings;
import org.boozsoft.domain.vo.stock.*;
import org.boozsoft.repo.stock.StockCangkuRepository;
import org.boozsoft.repo.stock.StockRepository;
import org.boozsoft.repo.stock.StockWarehousingRepository;
import org.boozsoft.repo.stock.StockWarehousingsRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/stockWarehList")
public class StockWareListController {

    @Autowired
    private StockWarehousingRepository warehousingRepository;
    @Autowired
    private StockWarehousingsRepository warehousingsRepository;
    @Autowired
    private StockCangkuRepository stockCangkuRepository;
    @Autowired
    private StockRepository stockRepository;
    @Autowired
    DatabaseClient client;


    @GetMapping("findByTypeList")
    public Mono<R> findByTypeList(String type,String year,String stockCangkuId) {
        return warehousingRepository.findAllByBillStyleAndIyearOrderByBcheckAscDdateAscCcodeAsc(type,year)
                .collectList()
                .flatMap(list->{
                    if(StrUtil.isNotBlank(stockCangkuId)&&!stockCangkuId.equals("undefined")){
                        list=list.stream().filter(a->a.getCwhcode().equals(stockCangkuId)).collect(Collectors.toList());
                    }
                    return Mono.just(list);
                })
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("findByQCDHDList")
    public Mono<R> findByQCDHDList(@RequestBody Map map) {
        String type=map.get("type").toString();
        String stockyear=map.get("stockyear").toString();
        String stockCangku=map.get("stockCangku").toString();
        String stockBcheck=map.get("stockBcheck").toString();
        String stockSup=map.get("stockSup").toString();
        String dept=map.get("dept").toString();
        String person=map.get("person").toString();
        Map<String, String> pageSearch = ((HashMap<String,  String>) map.get("pageSearch"));
        return warehousingRepository.findAllByBillStyleAndIyearOrderByBcheckAscDdateAscCcodeAsc(type,stockyear)
        .collectList()
        .flatMap(list->{
            if(StrUtil.isNotBlank(stockCangku)){
                list=list.stream().filter(a->a.getCwhcode().equals(stockCangku)).collect(Collectors.toList());
            }
            if(StrUtil.isNotBlank(stockBcheck)){
                list=list.stream().filter(a->StrUtil.isNotBlank(a.getBcheck())&&a.getBcheck().equals(stockBcheck)).collect(Collectors.toList());
            }
            if(StrUtil.isNotBlank(stockSup)){
                list=list.stream().filter(a->StrUtil.isNotBlank(a.getCvencode())&&a.getCvencode().equals(stockSup)).collect(Collectors.toList());
            }
            if(StrUtil.isNotBlank(dept)){
                list=list.stream().filter(a->StrUtil.isNotBlank(a.getCdepcode())&&a.getCdepcode().equals(dept)).collect(Collectors.toList());
            }
            if(StrUtil.isNotBlank(person)){
                list=list.stream().filter(a->StrUtil.isNotBlank(a.getCmaker())&&a.getCmaker().equals(person)).collect(Collectors.toList());
            }
            if(pageSearch!=null&&StrUtil.isNotBlank(pageSearch.get("selectValue"))){
                if(pageSearch.get("selectType").equals("ccode")){
                    list=list.stream().filter(a->a.getCcode().contains(pageSearch.get("selectValue"))).collect(Collectors.toList());
                }else if(pageSearch.get("selectType").equals("supName")){
                    list=list.stream().filter(a->a.getCustName().contains(pageSearch.get("selectValue"))).collect(Collectors.toList());
                }
            }
            return Mono.just(list);
        })
        .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByTypeLists")
    public Mono<R> findByTypeLists(String type,String year,String stockCangkuId) {
        return warehousingsRepository.findAllByBillStyleAndIyearOrderByBcheckDescDdateAscCcodeAscLineIdAsc(type,year)
                .collectList()
                .flatMap(list->{
                    if(StrUtil.isNotBlank(stockCangkuId)&&!stockCangkuId.equals("undefined")){
                        list=list.stream().filter(a->a.getCwhcode().equals(stockCangkuId)).collect(Collectors.toList());
                    }
                    return Mono.just(list);
                })
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("findAllMainsList")
    public Mono<R> findAllMainsList(@RequestBody Map map) {
        String type=map.get("type").toString();
        Integer strDate=Integer.valueOf(map.get("strDate").toString().replaceAll("-",""));
        Integer endDate=Integer.valueOf(map.get("endDate").toString().replaceAll("-",""));
        String strNum=map.get("strNum").toString();
        String endNum=map.get("endNum").toString();
        String sup=map.get("sup").toString();
        String dataType=map.get("dataType").toString();
        String cangku=map.get("cangku").toString();
        String stock=map.get("stock").toString();
        String ggxh=map.get("ggxh").toString();
        String stockClass=map.get("stockClass").toString();
        String bdocumStyle=map.get("bdocumStyle")==null?"":map.get("bdocumStyle").toString();

        return warehousingsRepository.findAllMainsList(type).collectList()
        .flatMap(list->{
            // 到货单
            if(bdocumStyle.equals("0")){
                list=list.stream().filter(a-> Double.valueOf(a.getBaseQuantity())>0).collect(Collectors.toList());
            }else if(bdocumStyle.equals("1")){
                list=list.stream().filter(a-> Double.valueOf(a.getBaseQuantity())<0).collect(Collectors.toList());
            }
            if(strDate!=null){
                list=list.stream().filter(a->Integer.valueOf(a.getDdate().replaceAll("-","").substring(0,map.get("strDate").toString().replaceAll("-","").length()))>=strDate&&Integer.valueOf(a.getDdate().replaceAll("-","").substring(0,map.get("endDate").toString().replaceAll("-","").length()))<=endDate).collect(Collectors.toList());
            }
            if(StrUtil.isNotBlank(strNum)){
                list=list.stream().filter(a->a.getCcode().contains(strNum)&&a.getCcode().contains(endNum)).collect(Collectors.toList());
            }
            if(StrUtil.isNotBlank(dataType)){
                list=list.stream().filter(a->a.getBcheck().equals(dataType)).collect(Collectors.toList());
            }
            if(StrUtil.isNotBlank(cangku)){
                list=list.stream().filter(a->a.getCwhcode().equals(cangku)).collect(Collectors.toList());
            }
            if(StrUtil.isNotBlank(stock)){
                list=list.stream().filter(a->a.getCinvode().equals(stock)).collect(Collectors.toList());
            }
            if(StrUtil.isNotBlank(ggxh)){
                list=list.stream().filter(a->a.getStockGgxh().equals(ggxh)).collect(Collectors.toList());
            }
            if(StrUtil.isNotBlank(stockClass)){
                list=list.stream().filter(a->a.getStockClass().equals(stockClass)).collect(Collectors.toList());
            }
            if(StrUtil.isNotBlank(sup)){
                list=list.stream().filter(a->a.getCvencode().equals(sup)).collect(Collectors.toList());
            }
            return Mono.just(list);
        }).map(a->R.ok().setResult(a));
    }

    @PostMapping("findByQCDHDLists")
    public Mono<R> findByQCDHDLists(@RequestBody Map map) {
        String type=map.get("type").toString();
        String stockyear=map.get("stockyear").toString();
        String stockCangku=map.get("stockCangku").toString();
        String stockBcheck=map.get("stockBcheck").toString();
        String stockSup=map.get("stockSup").toString();
        String stockInfo=map.get("stockInfo").toString();
        String dept=map.get("dept").toString();
        String person=map.get("person").toString();
        Map<String, String> searchMap = ((HashMap<String,  String>) map.get("searchConditon"));

        return warehousingsRepository.findAllByBillStyleAndIyear(type,stockyear)
                .collectList()
                .flatMap(list->{
                    if(StrUtil.isNotBlank(stockCangku)&&!stockCangku.equals("undefined")){
                        list=list.stream().filter(a->a.getCwhcode().equals(stockCangku)).collect(Collectors.toList());
                    }
                    if(StrUtil.isNotBlank(stockBcheck)){
                        list=list.stream().filter(a->StrUtil.isNotBlank(a.getBcheck())&&a.getBcheck().equals(stockBcheck)).collect(Collectors.toList());
                    }
                    if(StrUtil.isNotBlank(stockSup)){
                        list=list.stream().filter(a->StrUtil.isNotBlank(a.getCvencode())&&a.getCvencode().equals(stockSup)).collect(Collectors.toList());
                    }
                    if(StrUtil.isNotBlank(stockInfo)){
                        list=list.stream().filter(a->StrUtil.isNotBlank(a.getCinvode())&&a.getCinvode().equals(stockInfo)).collect(Collectors.toList());
                    }
                    if(StrUtil.isNotBlank(dept)){
                        list=list.stream().filter(a->StrUtil.isNotBlank(a.getCdepcode())&&a.getCdepcode().equals(dept)).collect(Collectors.toList());
                    }
                    if(StrUtil.isNotBlank(person)){
                        list=list.stream().filter(a->StrUtil.isNotBlank(a.getCmaker())&&a.getCmaker().equals(person)).collect(Collectors.toList());
                    }
                    list.forEach(temp->{
                        String str="";
                        if(StrUtil.isNotBlank(temp.getCangkuDuli())&&temp.getCangkuDuli().equals("0")){
                            if(StrUtil.isNotBlank(temp.getCwhcode1())){
                                str=temp.getCwhcode1();
                            }
                            if(StrUtil.isNotBlank(temp.getCwhcode2())){
                                str+="\\"+temp.getCwhcode2();
                            }
                            if(StrUtil.isNotBlank(temp.getCwhcode3())){
                                str+="\\"+temp.getCwhcode3();
                            }
                            if(StrUtil.isNotBlank(temp.getCwhcode4())){
                                str+="\\"+temp.getCwhcode4();
                            }
                            if(StrUtil.isNotBlank(temp.getCwhcode5())){
                                str+="\\"+temp.getCwhcode5();
                            }
                            if(StrUtil.isNotBlank(temp.getCwhcode6())){
                                str+="\\"+temp.getCwhcode6();
                            }
                        }else{
                            str=temp.getCwhcode1();
                        }
                        temp.setCwhcodeNameJoin(str);
                    });

                    if(StrUtil.isNotBlank(searchMap.get("value"))){
                        if(searchMap.get("requirement").equals("ccode")){
                            list=list.stream().filter(a->StrUtil.isNotBlank(a.getCcode())&&a.getCcode().contains(searchMap.get("value"))).collect(Collectors.toList());
                        }else if(searchMap.get("requirement").equals("sup")){
                            list=list.stream().filter(a->StrUtil.isNotBlank(a.getCustName())&&a.getCustName().contains(searchMap.get("value"))).collect(Collectors.toList());
                        }else if(searchMap.get("requirement").equals("cangku")){
                            list=list.stream().filter(a->StrUtil.isNotBlank(a.getCkName())&&a.getCkName().contains(searchMap.get("value"))).collect(Collectors.toList());
                        }else if(searchMap.get("requirement").equals("stock")){
                            list=list.stream().filter(a->StrUtil.isNotBlank(a.getStockName())&&a.getStockName().contains(searchMap.get("value"))).collect(Collectors.toList());
                        }else if(searchMap.get("requirement").equals("cmemo")){
                            list=list.stream().filter(a->StrUtil.isNotBlank(a.getCmemo())&&a.getCmemo().contains(searchMap.get("value"))).collect(Collectors.toList());
                        }else if(searchMap.get("requirement").equals("cmaker")){
                            list=list.stream().filter(a->StrUtil.isNotBlank(a.getCmakerName())&&a.getCmakerName().contains(searchMap.get("value"))).collect(Collectors.toList());
                        }else if(searchMap.get("requirement").equals("bcheckUser")){
                            list=list.stream().filter(a->StrUtil.isNotBlank(a.getBuname())&&a.getBuname().contains(searchMap.get("value"))).collect(Collectors.toList());
                        }
                    }
                    return Mono.just(list);
                })
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findAllByBillStyleAndCcode")
    public Mono<R> findAllByBillStyleAndCcode(String type,String year) {
        return warehousingsRepository.findAllByBillStyleAndCcodeOrderByDdateAscCcodeAscLineIdAsc(type,year)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findCangkuAllList")
    public Mono<R> findCangkuAllList(){
        return stockCangkuRepository.findAllOrderByCreatTime()
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findStockAllList")
    public Mono<R> findStockAllList(){
        return stockRepository.findAll()
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("findAllByCcodeAndBillStyle")
    public Mono<R> findAllByCcodeAndBillStyle(String ccode,String type) {
        return warehousingsRepository.findAllByCcodeAndBillStyle(ccode,type)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("save")
    @Transactional
    public Mono<R> save(@RequestBody List<StockWarehousings> list) {
        list=list.stream().map(tx->tx.setIsGive(tx.getIsGive().equals("true") || tx.getIsGive().equals("1")?"1":"0")).collect(Collectors.toList());
        return warehousingsRepository.saveAll(list)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("findByStockWarehId")
    public Mono<R> findByStockWarehId(String id) {
        return warehousingsRepository.findById(id)
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("findByStockWarehLinecode")
    public Mono<R> findByStockWarehLinecode(String linecode) {
        return warehousingsRepository.findByLineCode(linecode)
                .map(o -> R.ok().setResult(o)).defaultIfEmpty(R.ok().setResult(""));
    }


    @PostMapping("findAllByListCcode")
    public Mono<R> findAllByListCcode(@RequestBody List<String> lists){
        return warehousingsRepository.findAllByListCcode(lists).collectList().map(o -> R.ok().setResult(o));
    }

    /**
     * 存货未审核的盘点入库单
     * @return
     */
    @PostMapping("getPYRKDAndNoBcheck1")
    public Mono<R> getPYRKDAndNoBcheck1(String iyear){
        return warehousingsRepository.getPYRKDAndNoBcheck1(iyear).map(o -> R.ok().setResult(o));
    }

    @PostMapping("findAllByTypeList")
    public Mono<R> findAllByTypeList(@RequestBody Map map) {
        String type=map.get("type").toString();
        String strDate=map.get("strDate").toString();
        String endDate=map.get("endDate").toString();
        String cangku= map.containsKey("cangku") ? map.get("cangku").toString() : "";
        String stock= map.containsKey("cangku") ? map.get("stock").toString() : "";
        String ccode= map.containsKey("ccode") ? map.get("ccode").toString() : "";
        return warehousingRepository.findAllByTypeList(type, strDate, endDate).collectList()
                .flatMap(list->{
                    if(StrUtil.isNotBlank(cangku)){
                        list=list.stream().filter(a->a.getCwhcode().equals(cangku)).collect(Collectors.toList());
                    }
                    if(StrUtil.isNotBlank(ccode)){
                        list=list.stream().filter(a->a.getCcode().equals(ccode)).collect(Collectors.toList());
                    }
                    return Mono.just(list);
                }).map(a->R.ok().setResult(a));
    }


    // 统计入库单子表
    @PostMapping("findAllStockWarehTongji_CGDHD")
    public Mono<R> findAllStockWarehTongji_CGDHD(@RequestBody Map map){
        StringBuffer sb=new StringBuffer(" where sws.bill_style='CGDHD' www");

        // 状态
        String bcheck=map.get("bcheck").toString();
        if(StrUtil.isNotBlank(bcheck)){
            sb.append(" sws.bcheck='"+bcheck+"' ");
            sb.append("www");
        }
        // 编码Max 
        String ccodeMax=map.get("ccodeMax").toString();
        String ccodeMin=map.get("ccodeMin").toString();
        if(StrUtil.isNotBlank(ccodeMax)){
            sb.append(" sws.ccode between '"+ccodeMin+"' and '"+ccodeMax+"' ");
            sb.append("www");
        }
        // 存货
        String cinvode=map.get("cinvode2").toString();
        if(StrUtil.isNotBlank(cinvode)){
            sb.append(" sws.cinvode= '"+cinvode+"' ");
            sb.append("www");
        }

        // 查询页面-input值
        String searchInput=map.get("searchInput").toString();
        String searchVal=map.get("searchVal").toString();
        if(StrUtil.isNotBlank(searchVal)){
            if(searchInput.equals("stockNum")){
                sb.append(" sws.cinvode like '%"+searchVal+"%' ");
                sb.append("www");
            }else if(searchInput.equals("stockName")){
                sb.append(" sk.stock_name like '%"+searchVal+"%' ");
                sb.append("www");
            }
        }
        // 存货分类
        String cinvodeClass=map.get("cinvodeClass").toString();
        if(StrUtil.isNotBlank(cinvodeClass)){
            sb.append(" sk.stock_class= '"+cinvodeClass+"' ");
            sb.append("www");
        }

        // 供应商
        String cvencode=map.get("cvencode").toString();
        if(StrUtil.isNotBlank(cvencode)){
            sb.append(" sws.cvencode= '"+cvencode+"' ");
            sb.append("www");
        }
        // 仓库
        String cwhcode=map.get("cwhcode").toString();
        if(StrUtil.isNotBlank(cwhcode)){
            sb.append(" sws.cwhcode= '"+cwhcode+"' ");
            sb.append("www");
        }

        // 期间
        String strTime=map.get("strTime").toString();
        String endTime=map.get("endTime").toString();
        if(StrUtil.isNotBlank(strTime)){
            sb.append(" sws.ddate between '"+strTime+"' and '"+endTime+"' ");
            sb.append("www");
        }
        // 规格型号
        String stockGgxh=map.get("stockGgxh").toString();
        if(StrUtil.isNotBlank(stockGgxh)){
            sb.append(" sk.stock_ggxh= '"+stockGgxh+"' ");
        }

        String temp=sb.substring(sb.length()-3);
        String sql2=temp.equals("www")?sb.substring(0,sb.length()-3).replaceAll("www","and"):sb.toString().replaceAll("www","and");
        String sql="select temp.*,cast(temp.base_quantity as float)/cast(temp.rate as float) as cnumber from (select sws.cinvode,sk.stock_name,sk.stock_purchase_unit,sk.stock_unit_name as main_unit_name,\n" +
                "       (case when sk.stock_purchase_unit=sk.stock_unit_id then sk.stock_barcode else\n" +
                "   ((case when sk.stock_purchase_unit=sk.stock_unit_id1 then sk.stock_unit_barcode1 else\n" +
                "       (case when sk.stock_purchase_unit=sk.stock_unit_id2 then sk.stock_unit_barcode2 else '' end ) end )) end ) stock_barcode,\n" +
                "       (case when sk.stock_purchase_unit=sk.stock_unit_id then sk.stock_ggxh else\n" +
                "   ((case when sk.stock_purchase_unit=sk.stock_unit_id1 then sk.stock_unit_ggxh1 else\n" +
                "       (case when sk.stock_purchase_unit=sk.stock_unit_id2 then sk.stock_unit_ggxh2 else '' end ) end )) end ) stock_ggxh,\n" +
                "(case\n" +
                "    when sk.stock_measurement_type = '单计量' then (select sm.unit_name\n" +
                "                                                 from sys_unit_of_mea sm\n" +
                "                                                 where sm.id = sk.stock_purchase_unit)\n" +
                "    else (select li.unit_name from sys_unit_of_mea_list li where li.id = sk.stock_purchase_unit) end) as unit_name,\n" +
                "       (case\n" +
                "            when sk.stock_measurement_type = '单计量' then '1'\n" +
                "            else (select li.conversion_rate from sys_unit_of_mea_list li where li.id = sk.stock_purchase_unit) end) as rate,\n" +
                "       sum(cast(sws.base_quantity as decimal))    as      base_quantity,\n" +
                "       sum(cast(sws.isum as decimal)) as isum,\n" +
                "        sum(cast(sws.itaxprice as decimal)) as itaxprice,\n" +
                "        sum(cast(sws.icost as decimal)) as icost,\n" +
                "        coalesce(sum(cast(sws.isum_ruku as decimal)),'0')     as      isum_ruku,\n" +
                "        coalesce(sum(cast(sws.isum_chuku as decimal)),'0')    as      isum_tuihuo,\n" +
                "        coalesce(sum(cast(sws.isum_fapiao as decimal)),'0')    as      isum_fapiao,\n" +
                "        coalesce(sum(cast(sws.hx_isum as decimal)),'0')    as      isum_jiekuan\n" +
                "from stock_warehousings sws left join stock sk on sk.stock_num=sws.cinvode "+sql2+" \n" +
                "group by sws.cinvode,sk.stock_name,sk.stock_purchase_unit,sk.stock_unit_id,sk.stock_unit_id1,sk.stock_unit_id2,sk.stock_barcode,\n" +
                "         sk.stock_unit_barcode1,sk.stock_unit_barcode2,sk.stock_ggxh,sk.stock_unit_ggxh1,sk.stock_unit_ggxh2,sk.stock_measurement_type,sk.stock_unit_name) as temp";
        return client.sql(sql).fetch().all().collectList()
        .flatMap(list->{
            List<StockWarehousings2Vo> a=JSON.parseArray(JSON.toJSONString(list),StockWarehousings2Vo.class)
                    .stream().map(tx->{

                BigDecimal rate=new BigDecimal(tx.getRate());
                BigDecimal isumRuku=new BigDecimal(tx.getIsumRuku());
                BigDecimal isumTuihuo=new BigDecimal(tx.getIsumTuihuo());
                BigDecimal isumFapiao=new BigDecimal(tx.getIsumFapiao());

                tx.setIsumRuku(String.valueOf(isumRuku.divide(rate)));
                tx.setIsumTuihuo(String.valueOf(isumTuihuo.divide(rate)));
                tx.setIsumFapiao(String.valueOf(isumFapiao.divide(rate)));
                return tx;
            }).collect(Collectors.toList());
            // 排行榜-排序方式
            String paihang=map.get("selectClass")==null?"":map.get("selectClass").toString();
            if(StrUtil.isNotBlank(paihang)&& paihang.equals("baseQuantity")){
               a.sort(Comparator.comparing(StockWarehousings2Vo::getBaseQuantity));
            }else if(StrUtil.isNotBlank(paihang)&& paihang.equals("isum")){
                a.sort(Comparator.comparing(StockWarehousings2Vo::getIsum));
            }
            return Mono.just(a);
        })
        .map(item->R.ok().setResult(item));
    }

    // 统计明细入库单子表
    @PostMapping("findAllStockWarehTongjiMX_CGDHD")
    public Mono<R> findAllStockWarehTongjiMX_CGDHD(@RequestBody Map map){
        String type=map.get("type").toString();
        Map<String, String> pageSearch = ((HashMap<String,  String>) map.get("pageSearch"));

        StringBuffer sb=new StringBuffer(" where sws.bill_style='"+type+"' www");

        // 状态
        String bcheck=map.get("bcheck")==null?"":map.get("bcheck").toString();
        if(StrUtil.isNotBlank(bcheck)){
            sb.append(" sws.bcheck='"+bcheck+"' ");
            sb.append("www");
        }
        // 编码Max
        String ccodeMax=map.get("ccodeMax")==null?"":map.get("ccodeMax").toString();
        String ccodeMin=map.get("ccodeMin")==null?"":map.get("ccodeMin").toString();
        if(StrUtil.isNotBlank(ccodeMax)){
            sb.append(" sws.ccode between '"+ccodeMin+"' and '"+ccodeMax+"' ");
            sb.append("www");
        }
        // 存货
        String cinvode=map.get("cinvode2")==null?"":map.get("cinvode2").toString();
        if(StrUtil.isNotBlank(cinvode)){
            sb.append(" sws.cinvode= '"+cinvode+"' ");
            sb.append("www");
        }

        // 查询页面-input值
        String searchInput=map.get("searchInput")==null?"":map.get("searchInput").toString();
        String searchVal=map.get("searchVal")==null?"":map.get("searchVal").toString();
        if(StrUtil.isNotBlank(searchVal)){
            if(searchInput.equals("stockNum")){
                sb.append(" sws.cinvode like '%"+searchVal+"%' ");
                sb.append("www");
            }else if(searchInput.equals("stockName")){
                sb.append(" sk.stock_name like '%"+searchVal+"%' ");
                sb.append("www");
            }
        }
        // 存货分类
        String cinvodeClass=map.get("cinvodeClass")==null?"":map.get("cinvodeClass").toString();
        if(StrUtil.isNotBlank(cinvodeClass)){
            sb.append(" sk.stock_class= '"+cinvodeClass+"' ");
            sb.append("www");
        }

        // 供应商
        String cvencode=map.get("cvencode")==null?"":map.get("cvencode").toString();
        if(StrUtil.isNotBlank(cvencode)){
            sb.append(" sws.cvencode= '"+cvencode+"' ");
            sb.append("www");
        }
        // 仓库
        String cwhcode=map.get("cwhcode")==null?"":map.get("cwhcode").toString();
        if(StrUtil.isNotBlank(cwhcode)){
            sb.append(" sws.cwhcode= '"+cwhcode+"' ");
            sb.append("www");
        }
        // 期间
        String strDate=map.get("strDate")==null?"":map.get("strDate").toString();
        String endDate=map.get("endDate")==null?"":map.get("endDate").toString();

        if(StrUtil.isNotBlank(strDate)){
            sb.append(" sws.ddate between '"+strDate+"' and '"+endDate+"' ");
            sb.append("www");
        }
        // 规格型号
        String stockGgxh=map.get("stockGgxh")==null?"":map.get("stockGgxh").toString();
        if(StrUtil.isNotBlank(stockGgxh)){
            sb.append(" sk.stock_ggxh= '"+stockGgxh+"' ");
        }

        String temp=sb.substring(sb.length()-3);
        String sql2=temp.equals("www")?sb.substring(0,sb.length()-3).replaceAll("www","and"):sb.toString().replaceAll("www","and");
        String sql="select (case\n" +
                "            when sw.unit_type = 'etc' then sw.unit_value\n" +
                "            when sw.unit_type = 'cust' then (select cus.cust_name from customer cus where cus.id = sw.unit_value)\n" +
                "            when sw.unit_type = 'supplier' then (select sup.cust_name from supplier sup where sup.id = sw.unit_value)\n" +
                "            when sw.unit_type = 'user' then (select psn.psn_name from sys_psn psn where psn.id = sw.unit_value)\n" +
                "            when sw.unit_type = 'project' then (select pro.project_name from project pro where pro.id = sw.unit_value)\n" +
                "            else '' end\n" +
                "           )                                                                                           as unit_value_name,\n" +
                "       (case\n" +
                "            when sw.unit_type = 'etc' then '其他'\n" +
                "            when sw.unit_type = 'cust' then '客户'\n" +
                "            when sw.unit_type = 'supplier' then '供应商'\n" +
                "            when sw.unit_type = 'user' then '员工'\n" +
                "            when sw.unit_type = 'project' then '项目'\n" +
                "            else '' end\n" +
                "           )                                                                                           as unit_trans_name,\n" +
                "sw.invoice_style,sw.bill_code,sw.bill_number,sw.bill_date,sw.bstyle, sws.bcheck,sws.ddate,sws.ccode,psn.psn_name,dept.dept_name,sk.stock_num,sk.stock_name,\n" +
                "       sws.base_quantity,\n" +
                "       sws.sub_quantity1,\n" +
                "       sws.sub_quantity2," +
                "       sws.cwhcode," +
                "       sws.batch_id," +
                "       sws.dpdate," +
                "       sws.dvdate," +
                "       sws.price," +
                "       sws.taxprice," +
                "       sws.cmemo," +
                "       sws.itaxrate," +
                "       sws.arrival_date," +
                "       sws.cnumber,sk.stock_unit_name,sup.cust_code as cvencode,sup.cust_abbname as cvencode_name,sup2.cust_code as cvencode_js,sup2.cust_abbname as cvencode_jsname,\n" +
                "(case when sws.cg_unit_id=sk.stock_unit_id then sk.stock_barcode else\n" +
                "           ((case when sws.cg_unit_id=sk.stock_unit_id1 then sk.stock_unit_barcode1 else\n" +
                "               (case when sws.cg_unit_id=sk.stock_unit_id2 then sk.stock_unit_barcode2 else '' end ) end )) end ) stock_barcode,\n" +
                "       (case when sws.cg_unit_id=sk.stock_unit_id then sk.stock_ggxh else\n" +
                "           ((case when sws.cg_unit_id=sk.stock_unit_id1 then sk.stock_unit_ggxh1 else\n" +
                "               (case when sws.cg_unit_id=sk.stock_unit_id2 then sk.stock_unit_ggxh2 else '' end ) end )) end ) stock_ggxh,\n" +
                "       (case when sws.cg_unit_id=sk.stock_unit_id then sk.stock_unit_name else\n" +
                "           ((case when sws.cg_unit_id=sk.stock_unit_id1 then sk.stock_unit_name1 else\n" +
                "               (case when sws.cg_unit_id=sk.stock_unit_id2 then sk.stock_unit_name2 else '' end ) end )) end ) unit_name,"+
                "       sws.icost,sws.itaxprice,sws.isum, (case when sws.is_give ='1' then '是' else '否' end) as is_give,psn2.real_name as cmaker_name\n" +
                "from stock_warehousings sws\n" +
                "         left join stock_warehousing sw on sw.ccode=sws.ccode " +
                "         left join stock sk on sk.stock_num = sws.cinvode\n" +
                "         left join sys_psn psn on psn.id = sw.cpersoncode left join supplier sup on sup.id=sw.cvencode left join supplier sup2 on sup2.id=sw.cvencode_js \n" +
                "         left join sys_department dept on dept.unique_code = sw.cdepcode left join _app_group_sys_user psn2 on psn2.id = sw.cmaker "+sql2+"  order by sws.ccode";
        return client.sql(sql).fetch().all().collectList()
                .flatMap(list->{
                    List<StockWarehTongjiMXVo> newlist=JSON.parseArray(JSON.toJSONString(list),StockWarehTongjiMXVo.class);
                    if(pageSearch!=null&&StrUtil.isNotBlank(pageSearch.get("selectValue"))){
                        if(pageSearch.get("selectType").equals("ccode")){
                            newlist=newlist.stream().filter(a->a.getCcode().contains(pageSearch.get("selectValue"))).collect(Collectors.toList());
                        }else if(pageSearch.get("selectType").equals("cvencode")){
                            newlist=newlist.stream().filter(a->a.getCvencode().contains(pageSearch.get("selectValue"))).collect(Collectors.toList());
                        }else if(pageSearch.get("selectType").equals("cvencodeName")){
                            newlist=newlist.stream().filter(a->a.getCvencodeName().contains(pageSearch.get("selectValue"))).collect(Collectors.toList());
                        }else if(pageSearch.get("selectType").equals("deptName")){
                            newlist=newlist.stream().filter(a->a.getDeptName().contains(pageSearch.get("selectValue"))).collect(Collectors.toList());
                        }else if(pageSearch.get("selectType").equals("psnName")){
                            newlist=newlist.stream().filter(a->a.getPsnName().contains(pageSearch.get("selectValue"))).collect(Collectors.toList());
                        }else if(pageSearch.get("selectType").equals("cmakerName")){
                            newlist=newlist.stream().filter(a->a.getCmakerName().contains(pageSearch.get("selectValue"))).collect(Collectors.toList());
                        }else if(pageSearch.get("selectType").equals("stockNum")){
                            newlist=newlist.stream().filter(a->a.getStockNum().contains(pageSearch.get("selectValue"))).collect(Collectors.toList());
                        }else if(pageSearch.get("selectType").equals("stockName")){
                            newlist=newlist.stream().filter(a->a.getStockName().contains(pageSearch.get("selectValue"))).collect(Collectors.toList());
                        }else if(pageSearch.get("selectType").equals("batchId")){
                            newlist=newlist.stream().filter(a->a.getBatchId().contains(pageSearch.get("selectValue"))).collect(Collectors.toList());
                        }
                    }
                    return Mono.just(newlist);
                })
                .map(item->R.ok().setResult(item));
    }


    @PostMapping("findAllStockWarehTongjiSUP_CGDHD")
    public Mono<R> findAllStockWarehTongjiSUP_CGDHD(@RequestBody Map map){
        StringBuffer sb=new StringBuffer(" where sws.bill_style='CGDHD' www");

        // 状态
        String bcheck=map.get("bcheck").toString();
        if(StrUtil.isNotBlank(bcheck)){
            sb.append(" sws.bcheck='"+bcheck+"' ");
            sb.append("www");
        }
        // 编码Max
        String ccodeMax=map.get("ccodeMax").toString();
        String ccodeMin=map.get("ccodeMin").toString();
        if(StrUtil.isNotBlank(ccodeMax)){
            sb.append(" sws.ccode between '"+ccodeMin+"' and '"+ccodeMax+"' ");
            sb.append("www");
        }

        // 查询页面-input值
        String searchInput=map.get("searchInput").toString();
        String searchVal=map.get("searchVal").toString();
        if(StrUtil.isNotBlank(searchVal)){
            if(searchInput.equals("custCode")){
                sb.append(" sup.cust_code like '%"+searchVal+"%' ");
                sb.append("www");
            }else if(searchInput.equals("custName")){
                sb.append(" sup.cust_name like '%"+searchVal+"%' ");
                sb.append("www");
            }
        }

        // 供应商
        String cvencode=map.get("cvencode").toString();
        if(StrUtil.isNotBlank(cvencode)){
            sb.append(" sws.cvencode= '"+cvencode+"' ");
            sb.append("www");
        }
        // 仓库
        String cwhcode=map.get("cwhcode").toString();
        if(StrUtil.isNotBlank(cwhcode)){
            sb.append(" sws.cwhcode= '"+cwhcode+"' ");
            sb.append("www");
        }
        // 日期
        String dateStart=map.get("dateStart").toString();
        String dateEnd=map.get("dateEnd").toString();
        // 期间
        String periodStart=map.get("periodStart").toString();
        String periodEnd=map.get("periodEnd").toString();
        // 1期间 or 2日期
        String radiovalue=map.get("radiovalue").toString();
        if(radiovalue.equals("1")){
            sb.append(" sws.ddate between '"+periodStart.substring(0,4)+"-"+periodStart.substring(4,6)+"-01' and '"+periodEnd.substring(0,4)+"-"+periodEnd.substring(4,6)+"-31' ");
            sb.append("www");
        }

        String temp=sb.substring(sb.length()-3);

        String sql2=temp.equals("www")?sb.substring(0,sb.length()-3).replaceAll("www","and"):sb.toString().replaceAll("www","and");
        String sql="select temp.*,cast(temp.isum as float)-cast(temp.isum_jie_kuan as float) as no_jie_kuan from ( select sup.id as cust_id,sup.cust_code,\n" +
                "       sup.cust_name,\n" +
                "       jssup.cust_code                         as jscust_code,\n" +
                "       jssup.cust_name                         as jscust_name,\n" +
                "       sum(cast(sws.base_quantity as decimal)) as base_quantity,\n" +
                "       sum(cast(sws.isum as decimal))          as isum,\n" +
                "       sum(cast(sws.itaxprice as decimal))     as itaxprice,\n" +
                "       sum(cast(sws.icost as decimal))         as icost,\n" +
                "       sum(cast(sws.cnumber as decimal))        as cnumber,\n" +
                "       sum(cast(sws.isum_ruku as decimal))        as isum_ruku,\n" +
                "       sum(cast(sws.isum_chuku as decimal))        as isum_tuihuo,\n" +
                "       sum(cast(sws.isum_fapiao as decimal))        as isum_fapiao,\n" +
                "       sum(cast(sws.hx_isum as decimal))        as isum_jie_kuan \n" +
                "from stock_warehousings sws\n" +
                "         left join supplier sup on sup.id = sws.cvencode\n" +
                "         left join stock_warehousing sw on sw.ccode = sws.ccode\n" +
                "         left join supplier jssup on jssup.id = sw.cvencode "+sql2+" " +
                "group by sup.cust_code, sup.cust_name, jssup.cust_code,\n" +
                "         jssup.cust_name,sup.id) as temp ";
        return client.sql(sql).fetch().all().collectList()
        .flatMap(list->{
            List<CaiGouTongJiSupVo> a=JSON.parseArray(JSON.toJSONString(list),CaiGouTongJiSupVo.class);

            // 排行榜-排序方式
            String paihang=map.get("selectClass")==null?"":map.get("selectClass").toString();
            if(StrUtil.isNotBlank(paihang)&& paihang.equals("baseQuantity")){
                a.sort(Comparator.comparing(CaiGouTongJiSupVo::getBaseQuantity));
            }else if(StrUtil.isNotBlank(paihang)&& paihang.equals("isum")){
                a.sort(Comparator.comparing(CaiGouTongJiSupVo::getIsum));
            }
            return Mono.just(a);
        })
        .map(item->R.ok().setResult(item));
    }

    @PostMapping("deleteByStockWarehLineCode")
    @Transactional
    public Mono<R> deleteByStockWarehLineCode(String linecode){
        return warehousingsRepository.deleteByLineCode(linecode).then(Mono.just(R.ok()));
    }

    @PostMapping("getUnitRate")
    public Mono<R> getUnitRate(@RequestBody Map map){
        String cgUnitId=map.get("cgUnitId").toString();
        String ccode=map.get("ccode").toString();
        return warehousingsRepository.getUnitRate(cgUnitId,ccode).map(a->R.ok().setResult(a)).defaultIfEmpty(R.ok().setResult(""));
    }

    // 导出前查询子表
    @PostMapping("exportFindStockWaresByCcode")
    public Mono<R> exportFindStockWaresByCcode(String ccode){
        String sql="select sk.stock_name,\n" +
                "       (case when sws.cg_unit_id=sk.stock_unit_id then sk.stock_barcode else\n" +
                "           ((case when sws.cg_unit_id=sk.stock_unit_id1 then sk.stock_unit_barcode1 else\n" +
                "               (case when sws.cg_unit_id=sk.stock_unit_id2 then sk.stock_unit_barcode2 else '' end ) end )) end ) stock_barcode,\n" +
                "       (case when sws.cg_unit_id=sk.stock_unit_id then sk.stock_ggxh else\n" +
                "           ((case when sws.cg_unit_id=sk.stock_unit_id1 then sk.stock_unit_ggxh1 else\n" +
                "               (case when sws.cg_unit_id=sk.stock_unit_id2 then sk.stock_unit_ggxh2 else '' end ) end )) end ) stock_ggxh,\n" +
                "       (case when sws.cg_unit_id=sk.stock_unit_id then sk.stock_unit_name else\n" +
                "           ((case when sws.cg_unit_id=sk.stock_unit_id1 then sk.stock_unit_name1 else\n" +
                "               (case when sws.cg_unit_id=sk.stock_unit_id2 then sk.stock_unit_name2 else '' end ) end )) end ) unit_name,\n" +
                "              (case\n" +
                "                   when sk.stock_measurement_type = '单计量' then (select sm.unit_name\n" +
                "                     from sys_unit_of_mea sm\n" +
                "                       where sm.id = sk.stock_measurement_unit)\n" +
                "         else (select li.unit_name from sys_unit_of_mea_many many left join sys_unit_of_mea_list li on li.many_code=many.unit_code and li.is_main='true'\n" +
                "                                   where many.id = sk.stock_measurement_unit) end) as main_unit_name,\n" +
                "       sws.cinvode,sws.batch_id,sws.cnumber,sws.itaxrate,sws.taxprice,sws.price,sws.icost,sws.isum,sws.dpdate,sws.dvdate,sws.is_give,sws.cmemo,sws.base_quantity,sws.cwhcode,sws.arrival_date \n" +
                "from stock_warehousings sws\n" +
                "         left join stock sk on sk.stock_num = sws.cinvode\n" +
                "where sws.ccode = '"+ccode+"'";
        return client.sql(sql).fetch().all().collectList()
        .flatMap(list->{
            List<StockWarehousings3Vo> a=JSON.parseArray(JSON.toJSONString(list),StockWarehousings3Vo.class);
            return Mono.just(a);
        })
        .map(item->R.ok().setResult(item));
    }

    @PostMapping("findByStockWaresCcodeAndLineCode")
    public Mono<R> findByStockWaresCcodeAndLineCode(String ccode,String lineCode){
        return warehousingsRepository.findByCcodeAndLineCode(ccode,lineCode).map(R::ok).defaultIfEmpty(R.ok().setResult(""));
    }

    /** 1、主数量减累计结算数量不等于0。2、期初到货、暂估入库单
     * @description: 手动结算获取 到货单、入库单
     * @author: miao 
     * @date: 2022/10/11 15:59
     * @param: [map]
     * @return: Mono<R>
     **/
    @PostMapping("findAllByBillStyleAndDdateAndCvencode")
    public Mono<R> findAllByBillStyleAndDdateAndCvencode(@RequestBody Map map){
        String cvencode=map.get("cvencode").toString();
        String startDate=map.get("ddate").toString().split("-")[0]+"-01-31";
        String endDate=map.get("ddate").toString();
        return warehousingsRepository.findAllByBillStyleAndDdateAndCvencode(cvencode, Arrays.asList(map.get("billStyle").toString().split(",")),startDate,endDate).collectList().map(R::ok);
    }


    /**
     * @description: 根据单据类型查询存货执行情况表
     * @author: miao
     * @date: 2022/10/17 11:09
     * @param: [map]
     * @return: Mono<R>
     **/
    @PostMapping("findAllByBillStyleDetailed")
    public Mono<R> findAllByBillStyleDetailed(@RequestBody Map map){
        // 是否包含期初到货单
        Boolean inQCDHD= (Boolean) map.get("inQCDHD");
        // 单据类型
        String billStyle=map.get("billStyle").toString();
        // 年度
        String iyear=map.get("iyear").toString();
        // 期间
        String startDate=map.get("strDate").toString();
        String endDate=map.get("endDate").toString();
        // 供应商
        String sup=map.get("sup").toString();
        String jssup=map.get("jssup").toString();
        // 存货
        String cinvode=map.get("cinvode").toString();
        // 仓库
        String cwhcode=map.get("cwhcode").toString();
        Map<String, String> searchMap = ((HashMap<String,  String>) map.get("pageSearch"));

        StringBuffer sb=new StringBuffer();
        if(StrUtil.isNotBlank(sup)){
            sb.append("and sws.cvencode='"+sup+"' ");
        }
        if(StrUtil.isNotBlank(jssup)){
            sb.append("and sws.cvencode_js='"+jssup+"' ");
        }
        if(StrUtil.isNotBlank(cinvode)){
            sb.append("and sws.cinvode='"+cinvode+"' ");
        }
        if(StrUtil.isNotBlank(cwhcode)){
            sb.append("and sws.cwhcode='"+cwhcode+"' ");
        }

        // 与期初一块查出，在区分
        String sql="select temp.* from ((select sws.ddate,\n" +
            "       sws.ccode,\n" +
            "       case when sws.bcheck = '1' then '已审核' else '' end as              bcheck,\n" +
            "       sup.cust_code as cvencode,\n" +
            "       sup.cust_abbname                                     as              cvencode_name,\n" +
            "       sup2.cust_code as cvencode_js,\n" +
            "       sup2.cust_abbname as cvencode_js_name,\n" +
            "       sws.cwhcode,\n" +
            "       sws.batch_id,\n" +
            "       sws.cinvode,\n" +
            "       sk.stock_name                                     as              cinvode_name,\n" +
            "       sk.stock_ggxh                                     as              ggxh,\n" +
            "       (case\n" +
            "            when sk.stock_measurement_type = '单计量' then\n" +
            "                (select sm.unit_name as cunitid_name from sys_unit_of_mea sm where sm.id = sk.stock_measurement_unit)\n" +
            "            else\n" +
            "                (select sml.unit_name as cunitid_name\n" +
            "                 from sys_unit_of_mea_many smm\n" +
            "                          LEFT JOIN sys_unit_of_mea_list sml ON smm.unit_code = sml.many_code AND sml.is_main = 'true'\n" +
            "                 where smm.id = sk.stock_measurement_unit)\n" +
            "           end)                                          as              unit_name,\n" +
            "       sws.base_quantity,\n" +
            "       sws.isum,\n" +
            "       case when sws.is_give = '1' then '是' else '' end  as              is_give,\n" +
            "       sws.isum_tui_huo,\n" +
            "       sws.isum_ruku,\n" +
            "       cast(sws.base_quantity as float) - cast(sws.isum_ruku as float)   no_ruku,\n" +
            "       sws.hx_isum,\n" +
            "       cast(sws.isum as float) - cast(sws.hx_isum as float)              no_hx_isum,\n" +
            "       sws.isum_fapiao,sws.isum_fapiao_money,\n" +
            "       cast(sws.base_quantity as float) - cast(sws.isum_fapiao as float) no_fapiao,\n" +
            "       sws.cmemo,dept.dept_name,psn.psn_name,sh.real_name sh_name,zd.real_name zd_name,fh.real_name fh_name,sk.stock_barcode,sws.bill_style \n" +
            "from stock_warehousings sws\n" +
            "         left join supplier sup on sup.id = sws.cvencode\n" +
            "         left join stock sk on sk.stock_num = sws.cinvode\n" +
            "         left join stock_warehousing sw on sw.ccode = sws.ccode\n" +
            "         left join supplier sup2 on sup2.id = sw.cvencode\n" +
            "         left join sys_department dept on dept.id = sw.cdepcode\n" +
            "         left join sys_psn psn on psn.id = sw.cpersoncode\n" +
            "         left join _app_group_sys_user sh on sh.id = sws.bcheck_user\n" +
            "         left join _app_group_sys_user zd on zd.id = sws.cmaker\n" +
            "         left join _app_group_sys_user fh on fh.id = sw.bworkable_user\n" +
            "where sws.bill_style in ('"+billStyle+"') and sws.iyear='"+iyear+"' and sws.ddate between '"+startDate+"' and '"+endDate+"' "+sb+" )" +
                " union all (select sws.ddate,\n" +
                "        sws.ccode,\n" +
                "        case when sws.bcheck = '1' then '已审核' else '' end as              bcheck,\n" +
                "        sup.cust_code                                     as              cvencode,\n" +
                "        sup.cust_abbname                                  as              cvencode_name,\n" +
                "        sup2.cust_code                                    as              cvencode_js,\n" +
                "        sup2.cust_abbname                                 as              cvencode_js_name,\n" +
                "        sws.cwhcode,\n" +
                "        sws.batch_id,\n" +
                "        sws.cinvode,\n" +
                "        sk.stock_name                                     as              cinvode_name,\n" +
                "        sk.stock_ggxh                                     as              ggxh,\n" +
                "        (case\n" +
                "             when sk.stock_measurement_type = '单计量' then\n" +
                "                 (select sm.unit_name as cunitid_name from sys_unit_of_mea sm where sm.id = sk.stock_measurement_unit)\n" +
                "             else\n" +
                "                 (select sml.unit_name as cunitid_name\n" +
                "                  from sys_unit_of_mea_many smm\n" +
                "                           LEFT JOIN sys_unit_of_mea_list sml ON smm.unit_code = sml.many_code AND sml.is_main = 'true'\n" +
                "                  where smm.id = sk.stock_measurement_unit)\n" +
                "            end)                                          as              unit_name,\n" +
                "        sws.base_quantity,\n" +
                "        sws.isum,\n" +
                "        case when sws.is_give = '1' then '是' else '' end  as              is_give,\n" +
                "        sws.isum_tui_huo,\n" +
                "        sws.isum_ruku,\n" +
                "        cast(sws.base_quantity as float) - cast(sws.isum_ruku as float)   no_ruku,\n" +
                "        sws.hx_isum,\n" +
                "        cast(sws.isum as float) - cast(sws.hx_isum as float)              no_hx_isum,\n" +
                "        sws.isum_fapiao,sws.isum_fapiao_money,\n" +
                "        cast(sws.base_quantity as float) - cast(sws.isum_fapiao as float) no_fapiao,\n" +
                "        sws.cmemo,\n" +
                "        dept.dept_name,\n" +
                "        psn.psn_name,\n" +
                "        sh.real_name                                                      sh_name,\n" +
                "        zd.real_name                                                      zd_name,\n" +
                "        fh.real_name                                                      fh_name,\n" +
                "        sk.stock_barcode,sws.bill_style\n" +
                " from stock_warehousings sws\n" +
                "          left join supplier sup on sup.id = sws.cvencode\n" +
                "          left join stock sk on sk.stock_num = sws.cinvode\n" +
                "          left join stock_warehousing sw on sw.ccode = sws.ccode\n" +
                "          left join supplier sup2 on sup2.id = sw.cvencode\n" +
                "          left join sys_department dept on dept.id = sw.cdepcode\n" +
                "          left join sys_psn psn on psn.id = sw.cpersoncode\n" +
                "          left join _app_group_sys_user sh on sh.id = sws.bcheck_user\n" +
                "          left join _app_group_sys_user zd on zd.id = sws.cmaker\n" +
                "          left join _app_group_sys_user fh on fh.id = sw.bworkable_user\n" +
                " where sws.bill_style in ('QC') )) as temp order by temp.bill_style desc,temp.ddate,temp.ccode asc";
        return client.sql(sql).fetch().all().collectList()
        .flatMap(list->{
            List<StockDetailedVo> newlist=JSON.parseArray(JSON.toJSONString(list),StockDetailedVo.class);
            if(!inQCDHD){
                newlist=newlist.stream().filter(a->a.getBillStyle().equals(billStyle)).collect(Collectors.toList());
            }
            if(searchMap!=null){
                if(StrUtil.isNotBlank(searchMap.get("selectValue"))){
                    switch (searchMap.get("selectType")){
                        case "ccode":
                            newlist=newlist.stream().filter(a->a.getCcode().contains(searchMap.get("selectValue"))).collect(Collectors.toList());
                            break;
                        case "cvencode":
                            newlist=newlist.stream().filter(a->a.getCvencode().contains(searchMap.get("selectValue"))).collect(Collectors.toList());
                            break;
                        case "cvencodeName":
                            newlist=newlist.stream().filter(a->a.getCvencodeName().contains(searchMap.get("selectValue"))).collect(Collectors.toList());
                            break;
                        case "cinvode":
                            newlist=newlist.stream().filter(a->a.getCinvode().contains(searchMap.get("selectValue"))).collect(Collectors.toList());
                            break;
                        case "cinvodeName":
                            newlist=newlist.stream().filter(a->a.getCinvodeName().contains(searchMap.get("selectValue"))).collect(Collectors.toList());
                            break;
                        case "deptName":
                            newlist=newlist.stream().filter(a->a.getDeptName().contains(searchMap.get("selectValue"))).collect(Collectors.toList());
                            break;
                        case "psnName":
                            newlist=newlist.stream().filter(a->a.getPsnName().contains(searchMap.get("selectValue"))).collect(Collectors.toList());
                            break;
                        case "stockBarcode":
                            newlist=newlist.stream().filter(a->a.getStockBarcode().contains(searchMap.get("selectValue"))).collect(Collectors.toList());
                            break;
                    }
                }
            }
            return Mono.just(newlist);
        }).map(R::ok);
    }
}
