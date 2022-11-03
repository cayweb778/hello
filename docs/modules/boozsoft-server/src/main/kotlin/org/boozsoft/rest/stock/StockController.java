package org.boozsoft.rest.stock;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.FileEncodingRules;
import org.boozsoft.domain.entity.Supplier;
import org.boozsoft.domain.entity.SysUnitOfMea;
import org.boozsoft.domain.entity.SysUnitOfMeaMany;
import org.boozsoft.domain.entity.stock.Stock;
import org.boozsoft.domain.entity.stock.StockCangku;
import org.boozsoft.domain.entity.stock.StockCangkuLevelRecord;
import org.boozsoft.domain.entity.stock.StockClass;
import org.boozsoft.domain.vo.CustomerMaxNumVo;
import org.boozsoft.domain.vo.stock.StockVo;
import org.boozsoft.repo.SupplierRepository;
import org.boozsoft.repo.SysUnitOfMeaListRepository;
import org.boozsoft.repo.SysUnitOfMeaManyRepository;
import org.boozsoft.repo.SysUnitOfMeaRepository;
import org.boozsoft.repo.stock.StockCangkuLevelRecordRepository;
import org.boozsoft.repo.stock.StockCangkuRepository;
import org.boozsoft.repo.stock.StockClassRepository;
import org.boozsoft.repo.stock.StockRepository;
import org.boozsoft.service.stock.StockService;
import org.boozsoft.util.NewStringUtil;
import org.boozsoft.util.XlsUtils3;
import org.springbooz.core.tool.result.R;
import org.springbooz.core.tool.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/stock")
public class StockController {

    @Autowired
    private StockRepository stockRepository;
    @Autowired  // 单计量
    SysUnitOfMeaRepository sysUnitOfMeaRepository;
    @Autowired  // 多计量
    SysUnitOfMeaManyRepository sysUnitOfMeaManyRepository;
    @Autowired  // 多计量明细
    SysUnitOfMeaListRepository sysUnitOfMeaListRepository;
    @Autowired
    StockClassRepository stockClassRepository;
    @Autowired
    SupplierRepository supplierRepository;
    @Autowired
    StockCangkuRepository cangkuRepository;
    @Autowired
    StockCangkuLevelRecordRepository cangkuLevelRecordRepository;
    @Autowired
    StockService stockService;
    @Autowired
    DatabaseClient client;



    @PostMapping("findByStockIdToBalanceAndSwsAndSss")
    public Mono<R> findByStockIdToBalanceAndSwsAndSss(String id){
        return stockRepository.findByStockIdToBalanceAndSwsAndSss(id).map(a->R.ok().setResult(a)).defaultIfEmpty(R.ok().setResult(""));
    }

    @PostMapping("findStockById")
    public Mono<R> findBylimit(String id){
        return stockRepository.findById(id)
                .map(a->R.ok().setResult(a)).defaultIfEmpty(R.ok().setResult("不存在"));
    }

    @PostMapping("save")
    public Mono<R> save(@RequestBody Stock object) {
        object.setStockNum(object.getStockNum().trim())
        .setStockName(object.getStockName().trim())
        .setStockGgxh(StrUtil.isBlank(object.getStockGgxh())?"":object.getStockGgxh().trim())
        .setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()))
        .setStockBarcode(StrUtil.isBlank(object.getStockBarcode())?"":object.getStockBarcode());

        return stockRepository.save(object).map(a->R.ok().setResult(a));
    }
    @PostMapping("saveList")
    public Mono<R> saveList(@RequestBody List<Stock> object) {
        object.forEach(a->{
            a.setStockName(a.getStockName().trim())
            .setStockBarcode(a.getStockBarcode().trim())
            .setStockGgxh(a.getStockGgxh().trim())
            .setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()))
            .setStockBarcode(StrUtil.isBlank(a.getStockBarcode())?"":a.getStockBarcode());
        });
        return stockRepository.saveAll(object).collectList().map(a->R.ok().setResult(a));
    }

    /**
     * 单计量
     * @return
     */
    @PostMapping("singleUnitOfMea")
    public Mono<R> singleUnitOfMea(){
       return sysUnitOfMeaRepository.findAllByFlag().collectList().map(a->R.ok().setResult(a));
    }

    /**
     * 多计量
     * @return
     */
    @PostMapping("multiUnitOfMea")
    public Mono<R> multiUnitOfMea(){
        return sysUnitOfMeaManyRepository.findAll().collectList().map(a->R.ok().setResult(a));
    }

    /**
     * 多计量明细
     * @return
     */
    @PostMapping("multiUnitOfMeaMX")
    public Mono<R> multiUnitOfMeaMX(String manyCode){
        return sysUnitOfMeaListRepository.findAllByManyCode(manyCode).collectList().map(a->R.ok().setResult(a));
    }

    /**
     * 编码是否重复
     * @param num
     * @return
     */
    @PostMapping("findByStockNum")
    public Mono<R> findByStockNum(String num){
        return stockRepository.countByStockNum(num).map(a->R.ok().setResult(a));
    }

    @PostMapping("findByStockNum2")
    public Mono<R> findByStockNum2(String num){
        return stockRepository.findByStockNum(num).map(a->R.ok().setResult(a));
    }

    /**
     * 名称加规格型号 是否重复
     * @return
     */
    @PostMapping("findByStockNameAndGgxh")
    public Mono<R> findByStockNameAndGgxh(@RequestBody Map map){
        String name=map.get("name").toString().trim();
        String ggxh=map.get("ggxh").toString().trim();
        String ggxhType=map.get("ggxhType").toString().trim();
        return stockRepository.findAll().collectList()
            .flatMap(list->{
                if(StrUtil.isNotBlank(name)){
                    list=list.stream().filter(a->StrUtil.isNotBlank(a.getStockName())&&a.getStockName().equals(name)).collect(Collectors.toList());
                }
                if(ggxhType.equals("0")&&StrUtil.isNotBlank(ggxh)){
                    list=list.stream().filter(a->StrUtil.isNotBlank(a.getStockGgxh())&&a.getStockGgxh().equals(ggxh)).collect(Collectors.toList());
                }else if(ggxhType.equals("1")&&StrUtil.isNotBlank(ggxh)){
                    list=list.stream().filter(a->StrUtil.isNotBlank(a.getStockUnitGgxh1())&&a.getStockGgxh().equals(ggxh)).collect(Collectors.toList());
                }else if(ggxhType.equals("2")&&StrUtil.isNotBlank(ggxh)){
                    list=list.stream().filter(a->StrUtil.isNotBlank(a.getStockUnitGgxh2())&&a.getStockGgxh().equals(ggxh)).collect(Collectors.toList());
                }
                return Mono.just(list.size());
            })
            .map(a->R.ok().setResult(a));
    }

    @PostMapping("countByStockUnitBarcode")
    public Mono<R> countByStockUnitBarcode(String stockBarcode){
        return stockRepository.countByStockUnitBarcode(stockBarcode).map(a->R.ok().setResult(a));
    }

    @PostMapping("delFindById")
    public Mono<R> delFindById(String id){
        return stockRepository.deleteById(id).then(Mono.just(R.ok()));
    }

    @PostMapping("delAll")
    public Mono<R> delAll(@RequestBody List<Stock> list){
        return stockRepository.deleteAll(list).then(Mono.just(R.ok()));
    }

    @GetMapping("countStock")
    public Mono<R> countStock(){
        return stockRepository.countStock().map(a->R.ok().setResult(a));
    }

    // 查询存货档案
    @PostMapping("findAll")
    public Mono<R> findAll(@RequestBody Map map){
        String stockFlag=map.get("stockFlag").toString();
        String stockClass=map.get("stockClass").toString();
        Map<String, String> searchMap = ((HashMap<String,  String>) map.get("searchConditon"));
        return stockRepository.findAlls().collectList()
                .flatMap(list->{
                    if(StrUtil.isNotBlank(stockClass) && !stockClass.equals("0")){
                        list=list.stream().filter(f->f.getStockClass().equals(stockClass)).collect(Collectors.toList());
                    }else if(StrUtil.isNotBlank(stockFlag)){
                        list=list.stream().filter(f->f.getStockFlag().equals(stockFlag)).collect(Collectors.toList());
                    }
                    if(StrUtil.isNotBlank(searchMap.get("value"))){
                        if(searchMap.get("requirement").equals("stockNum")){
                            list=list.stream().filter(a->a.getStockNum().contains(searchMap.get("value"))).collect(Collectors.toList());
                        }
                        if(searchMap.get("requirement").equals("stockName")){
                            list=list.stream().filter(a->a.getStockName().contains(searchMap.get("value"))).collect(Collectors.toList());
                        }
                        if(searchMap.get("requirement").equals("stockBarcode")){
                            list=list.stream().filter(a->a.getStockBarcode().contains(searchMap.get("value"))).collect(Collectors.toList());
                        }
                        if(searchMap.get("requirement").equals("stockClass")){
                            list=list.stream().filter(a->a.getStockCclassName().contains(searchMap.get("value"))).collect(Collectors.toList());
                        }
                        if(searchMap.get("requirement").equals("stockGgxh")){
                            list=list.stream().filter(a->a.getStockGgxh().contains(searchMap.get("value"))).collect(Collectors.toList());
                        }
                        if(searchMap.get("requirement").equals("stockJiliang")){
                            list=list.stream().filter(a->a.getStockMeasurementName().contains(searchMap.get("value"))).collect(Collectors.toList());
                        }
                        if(searchMap.get("requirement").equals("stockBrand")){
                            list=list.stream().filter(a->a.getStockBrand().contains(searchMap.get("value"))).collect(Collectors.toList());
                        }
                    }
                    return Mono.just(list);
                })
                .map(a->R.ok().setResult(a));
    }
    // 查询存货档案
    @PostMapping("findAllBySearch")
    public Mono<R> findAllBySearch(String search){
        return stockRepository.findAlls().collectList()
        .flatMap(list->{
            List<Map> listmap=new ArrayList<>();
            if(StrUtil.isNotBlank(search) ){
                list=list.stream().filter(f->f.getStockNum().contains(search) || f.getStockName().contains(search) ).collect(Collectors.toList());
                list.forEach(a->{
                    Map map = new HashMap();
                    map.put("label",a.getStockNum()+"-"+a.getStockName());
                    map.put("key",a);
                    listmap.add(map);
                });
            }
           return Mono.just(listmap);
        })
        .map(a->R.ok().setResult(a));
    }

    @PostMapping("getMaxStockNumByCodingFlag")
    public Mono<R> getMaxCusCodeByCodingFlag(@RequestBody FileEncodingRules obj){
        return stockRepository.findAll().collectList()
        .flatMap(list->{
            String newcode="";
            if(obj.getId()!=null){
                String showRules=StrUtil.isBlank(obj.getShowRules())?"":obj.getShowRules();
                // 判断表中前缀开头的编码 是否与编码规则一致
                List<String> collect = list.stream().filter(a ->StrUtil.isNotBlank(obj.getShowRules()) &&a.getStockNum().startsWith(obj.getShowRules())).map(Stock::getStockNum).collect(Collectors.toList());
                List<Integer> listInt=new ArrayList<>();
                if(collect.size()>0){
                    collect.forEach(a->{
                        // 不包含特殊符号【是否编码规则设置】
                        if(!NewStringUtil.isSpecialChar(a)){
                            listInt.add(Integer.valueOf(a.substring(obj.getShowRules().length())));
                        }else{
                            listInt.add(Integer.valueOf(a.split(obj.getShowRules())[1]));
                        }
                    });
                    List<Integer> list2 = new ArrayList<>();
                    for (int i = 0; i < 10000; i++) {
                        list2.add(i + 1);
                    }
                    // 查找科目编码 中间 不连续，得出最小值;等于0 说明到头了【最大99】
                    int minCode = list2.stream().filter(item -> !listInt.contains(item)).mapToInt(Integer::intValue).min().orElse(0);
                    newcode=showRules+String.format("%0"+obj.getSerialNumLength()+"d",minCode);
                }else{
                    List<CustomerMaxNumVo> listMaxNumVo=new ArrayList<>();
                    list.forEach(a->{
                        if(StrUtil.isNotBlank(a.getStockNum())&&NewStringUtil.isNumeric4(a.getStockNum())){
                            CustomerMaxNumVo maxNumVo=new CustomerMaxNumVo();
                            listMaxNumVo.add(maxNumVo.setCustCode(Integer.valueOf(a.getStockNum())));
                        }
                    });
                    if(listMaxNumVo.size()==0 || list.size()==0){
                        newcode="0001";
                    }else{
                        CustomerMaxNumVo maxNumVo = listMaxNumVo.stream().max(Comparator.comparing(CustomerMaxNumVo::getCustCode)).get();
                        newcode="0"+String.format("%03d",maxNumVo.getCustCode()+1);
                    }
                }
            }
            else{
                List<CustomerMaxNumVo> listMaxNumVo=new ArrayList<>();
                list.forEach(a->{
                    if(StrUtil.isNotBlank(a.getStockNum())&&NewStringUtil.isNumeric4(a.getStockNum())){
                        CustomerMaxNumVo maxNumVo=new CustomerMaxNumVo();
                        listMaxNumVo.add(maxNumVo.setCustCode(Integer.valueOf(a.getStockNum())));
                    }
                });
                if(listMaxNumVo.size()==0 || list.size()==0){
                    newcode="0001";
                }else{
                    CustomerMaxNumVo maxNumVo = listMaxNumVo.stream().max(Comparator.comparing(CustomerMaxNumVo::getCustCode)).get();
                    newcode="0"+String.format("%03d",maxNumVo.getCustCode()+1);
                }
            }
            return Mono.just(newcode);
        }) .map(a->R.ok().setResult(a));
    }

    @PostMapping("countByStockClass")
    public Mono<R> countByStockClass(String stockClass){return stockRepository.countByStockClass(stockClass).map(a->R.ok().setResult(a));}

    @PostMapping("editStockFlag")
    public Mono<R> editStockFlag(@RequestBody List<Stock> list){
        list.forEach(a->{
            if(a.getStockFlag().equals("1")){
                a.setStockFlag("0");
            }else{
                a.setStockFlag("1");
            }
        });
        return stockRepository.saveAll(list).collectList().map(a->R.ok().setResult(a));
    }

    @PostMapping("/importStockColumn")
    public Mono<R> importStockColumn(@RequestBody List<Stock> list) throws Exception {
        return stockRepository.saveAll(list).collectList().map(R::ok);
    }

    @PostMapping("findAllByXcl")
    public Mono<R> findAllByXcl(@RequestBody Map map){
        String stockFlag=map.get("stockFlag").toString();
        String stockClass=map.get("stockClass").toString();
        String cangku=map.get("cangku").toString();
        //仓库下的现存量 
        Map<String, String> searchMap = ((HashMap<String,  String>) map.get("searchConditon"));
        return stockRepository.findAllByXcl().collectList()
                .flatMap(list->{
                    if(StrUtil.isNotBlank(stockClass) && !stockClass.equals("0")){
                        list=list.stream().filter(f->f.getStockClass().equals(stockClass)).collect(Collectors.toList());
                    }else if(StrUtil.isNotBlank(stockFlag)){
                        list=list.stream().filter(f->f.getStockFlag().equals(stockFlag)).collect(Collectors.toList());
                    }
                    if(StrUtil.isNotBlank(searchMap.get("value"))){
                        if(searchMap.get("requirement").equals("stockNum")){
                            list=list.stream().filter(a->a.getStockNum().equals(searchMap.get("value"))).collect(Collectors.toList());
                        }
                        if(searchMap.get("requirement").equals("stockName")){
                            list=list.stream().filter(a->a.getStockName().equals(searchMap.get("value"))).collect(Collectors.toList());
                        }
                        if(searchMap.get("requirement").equals("stockBarcode")){
                            list=list.stream().filter(a->a.getStockBarcode().equals(searchMap.get("value"))).collect(Collectors.toList());
                        }
                        if(searchMap.get("requirement").equals("stockClass")){
                            list=list.stream().filter(a->a.getStockCclassName().equals(searchMap.get("value"))).collect(Collectors.toList());
                        }
                        if(searchMap.get("requirement").equals("stockGgxh")){
                            list=list.stream().filter(a->a.getStockGgxh().equals(searchMap.get("value"))).collect(Collectors.toList());
                        }
                        if(searchMap.get("requirement").equals("stockJiliang")){
                            list=list.stream().filter(a->a.getStockMeasurementName().equals(searchMap.get("value"))).collect(Collectors.toList());
                        }
                    }
                    return Mono.just(list);
                })
                .map(a->R.ok().setResult(a));
    }

    @PostMapping("findCunHuoAllByXcl")
    public Mono<R> findCunHuoAllByXcl(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        Flux<Stock> stockMasterFlux = stockRepository.findCunHuoAllByXcl();// stockRepository.findAllByCreateTimeLessThanEqualOrderByStockNum(date);
        return stockMasterFlux.collectList().map(o -> R.ok(o));
    }

    @PostMapping("findAllByStockClass")
    public Mono<R> findAllByStockClass(String stockClass) {
        return stockRepository.findAllByStockClass(stockClass).collectList().map(o -> R.ok(o));
    }

    @PostMapping("verifyStockIsData")
    public Mono<R> verifyStockIsData(String stockNum) {
        return stockRepository.verifyStockIsData(stockNum).map(o ->R.ok().setResult(o));
    }
    @PostMapping("batchStockData")
    public Mono<R> batchStockData(@RequestBody Map map){
        String stockClass=map.get("stockClass").toString();
        String stockCangku=map.get("stockCangku").toString();
        String stockCangkuDuli=map.get("stockCangkuDuli").toString();
        String stockSup=map.get("stockSup").toString();
        String pinpai=map.get("pinpai").toString();
        String shichang=map.get("shichang").toString();
        String yujing=map.get("yujing").toString();
        String pici=map.get("pici").toString();
        String yslw=map.get("yslw").toString();
        List<String>idlist=Arrays.asList(map.get("idlist").toString().split(","));

        StringBuffer sb=new StringBuffer("set");
        if(StrUtil.isNotBlank(stockClass)){
            sb.append(" stock_class='"+stockClass+"' ,");
        }else if(StrUtil.isNotBlank(stockCangku)){
            sb.append(" stock_cangku='"+stockCangku+"' ,");
            sb.append(" stock_cangku_duli='"+stockCangkuDuli+"' ,");
        }else if(StrUtil.isNotBlank(stockSup)){
            sb.append(" stock_supplier='"+stockSup+"' ,");
        }else if(StrUtil.isNotBlank(pinpai)){
            sb.append(" stock_brand='"+pinpai+"' ,");
        }else if(StrUtil.isNotBlank(shichang)){
            sb.append(" stock_indate_manage='1' ,");
            sb.append(" stock_indate_unit='天' ,");
            sb.append(" stock_indate_duration='"+shichang+"' ,");
        }else if(StrUtil.isNotBlank(yujing)){
            sb.append(" stock_indate_warning_day='"+yujing+"' ,");
        }else if(StrUtil.isNotBlank(pici)){
            sb.append(" stock_property_batch='"+pici+"' ,");
        }else if(StrUtil.isNotBlank(pici)){
            if(yslw.equals("1")){
                sb.append(" stock_property_batch='0' ,stock_property_market='0' ,stock_property_purchase='0' ,stock_property_production='0' ,stock_property_production='0' ,");
                sb.append(" stock_property_entrust='0' ,stock_property_series_number='0' ,stock_indate_manage='0' ,stock_indate_unit='' ,stock_indate_duration='' ,stock_indate_warning_day='' ,");
            }
            sb.append(" stock_property_yslwfy='"+yslw+"' ,");
        }

        String str=sb.substring(0,sb.length()-1);
        String idStr="";
        for (int i = 0; i <idlist.size(); i++) {
            idStr+="'"+idlist.get(i)+"',";
        }
        idStr=idStr.substring(0,idStr.length()-1);
        String sql="update stock "+str+" where id in ("+idStr+")";
        return client.sql(sql).fetch().rowsUpdated().then(Mono.just(R.ok()));
    }


    @PostMapping("findByStockSupplierPrice")
    public Mono<R> findByStockSupplierPrice(String stockNum) {
        return stockRepository.findByStockSupplierPrice(stockNum).collectList().map(o -> R.ok(o));
    }
    @PostMapping("countByStockUnitBarcode1NotInStockNum")
    public Mono<R> countByStockUnitBarcode1NotInStockNum(String stockNum,String stockUnitBarcode1) {
        return stockRepository.countByStockUnitBarcode1NotInStockNum(stockNum,stockUnitBarcode1).map(o -> R.ok(o));
    }
    @PostMapping("countByStockUnitBarcode2NotInStockNum")
    public Mono<R> countByStockUnitBarcode2NotInStockNum(String stockNum,String stockUnitBarcode2) {
        return stockRepository.countByStockUnitBarcode2NotInStockNum(stockNum,stockUnitBarcode2).map(o -> R.ok(o));
    }
}
