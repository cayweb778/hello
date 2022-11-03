package org.boozsoft.rest.stock;//package org.boozsoft.rest;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.SysUnitOfMea;
import org.boozsoft.domain.entity.SysUnitOfMeaList;
import org.boozsoft.domain.entity.SysUnitOfMeaMany;
import org.boozsoft.domain.entity.Task;
import org.boozsoft.domain.entity.stock.StockBeginBalance;
import org.boozsoft.domain.entity.stock.StockCangku;
import org.boozsoft.domain.entity.stock.StockCangkuLevelRecord;
import org.boozsoft.domain.entity.stock.StockCurrentstock;
import org.boozsoft.domain.vo.stock.StockBalanceLackVo;
import org.boozsoft.domain.vo.stock.StockBeginBalanceVo;
import org.boozsoft.domain.vo.stock.StockCurrentstockVo;
import org.boozsoft.domain.vo.stock.StockVo;
import org.boozsoft.repo.SysUnitOfMeaListRepository;
import org.boozsoft.repo.SysUnitOfMeaManyRepository;
import org.boozsoft.repo.SysUnitOfMeaRepository;
import org.boozsoft.repo.TaskRepository;
import org.boozsoft.repo.project.base.ProjectRepositoryBase;
import org.boozsoft.repo.stock.*;
import org.boozsoft.util.NewStringUtil;
import org.boozsoft.util.XlsUtils3;
import org.springbooz.core.tool.result.R;
import org.springbooz.core.tool.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 存货期初余额表
 */
@Slf4j
@RestController
@RequestMapping("/stock_balance")
@Api(value = "存存期初余额表", tags = "存存期初余额表")
public class StockBeginBalanceController {
    @Autowired
    StockBeginBalanceRepository stockBeginBalanceRepository;
    @Autowired
    TaskRepository taskRepository;
    @Autowired
    StockCurrentstockRepository stockCurrentstockRepository;
    @Autowired
    StockWarehousingsRepository stockWarehousingsRepository;
    @Autowired
    StockRepository stockRepository;
    @Autowired
    StockCangkuRepository stockCangkuRepository;
    @Autowired  // 单计量
    SysUnitOfMeaRepository sysUnitOfMeaRepository;
    @Autowired  // 多计量
    SysUnitOfMeaManyRepository sysUnitOfMeaManyRepository;
    @Autowired  // 多计量明细
    SysUnitOfMeaListRepository sysUnitOfMeaListRepository;
    @Autowired
    StockCangkuLevelRecordRepository stockCangkuLevelRecordRepository;
    @Autowired
    ProjectRepositoryBase projectRepositoryBase;


    private String getFieldValueByFieldName(String fieldName, Object object) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = object.getClass().getMethod(getter, new Class[]{});
            Object value = method.invoke(object, new Object[]{});
            return value.toString();
        } catch (Exception e) {
            return null;
        }
    }

    // 计算失效日期\生效日期
    private static String DateTestUtil(String str,String type,int number) throws ParseException {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat sdf2=new SimpleDateFormat("yyyy-MM-dd");
        Date dt=sdf.parse(str);
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(dt);

        rightNow.add(type.equals("年")?Calendar.YEAR:type.equals("月")?Calendar.MONTH:Calendar.DAY_OF_YEAR,number);//日期加
        if(type.equals("天")){
            rightNow.add(Calendar.DAY_OF_YEAR,-1);//日期减
        }
        Date dt1=rightNow.getTime();
        String reStr = sdf2.format(dt1);
        return reStr;
    }


    @PostMapping("stockBalanceEdit")
    public Mono<R> stockBalanceEdit(@RequestBody Map map){
        String id=map.get("id").toString();
        String stockRukuNumber=map.get("stockRukuNumber")==null?"":map.get("stockRukuNumber").toString();
        String stockRukuDate=map.get("stockRukuDate")==null?"":map.get("stockRukuDate").toString();
        String stockCangkuRecordId=map.get("stockCangkuRecordId").toString();
        String stockCangkuId=map.get("stockCangkuId").toString();
        String cangkuDuli=map.get("cangkuDuli").toString();
        String cunitidType=map.get("cunitidType").toString();
        String stockClass=map.get("stockClass").toString();
        String cmaker=map.get("cmaker").toString();
        String iyear=map.get("iyear").toString();
        String cangkuName=map.get("cangkuName").toString();
        String stockId=map.get("stockId").toString();
        String cunitid=map.get("cunitid").toString();
        BigDecimal baseQuantity=new BigDecimal(map.get("primaryNumber").toString());
        BigDecimal price=new BigDecimal(map.get("price").toString());
        BigDecimal icost=new BigDecimal(map.get("money").toString());
        String dpdate=map.get("dpdate")==null?"":map.get("dpdate").toString().split(" ")[0];
        String dvdate=map.get("dvdate")==null?"":map.get("dvdate").toString().split(" ")[0];
        String batchNumber=map.get("batchNumber").toString();
        Integer iineId= Integer.valueOf(map.get("iineId").toString());
        String cunitidF1=map.get("cunitidF1")==null?"":map.get("cunitidF1").toString();
        String cunitidF2=map.get("cunitidF2")==null?"":map.get("cunitidF2").toString();
        BigDecimal subQuantity1=map.get("primaryNumber1")==null?new BigDecimal(0):new BigDecimal(map.get("primaryNumber1").toString());
        BigDecimal subQuantity2=map.get("primaryNumber2")==null?new BigDecimal(0):new BigDecimal(map.get("primaryNumber2").toString());

        StockBeginBalance b=new StockBeginBalance();
//        for (int i = 0; i < cangkuName.split("\\\\").length; i++) {
//            if(i==0){
//                b.setCwhcode1(cangkuName.split("\\\\")[i].contains("默认")?"仓库":cangkuName.split("\\\\")[i]);
//            }else if(i==1){
//                b.setCwhcode2(cangkuName.split("\\\\")[i]);
//            }else if(i==2){
//                b.setCwhcode3(cangkuName.split("\\\\")[i]);
//            }else if(i==3){
//                b.setCwhcode4(cangkuName.split("\\\\")[i]);
//            }else if(i==4){
//                b.setCwhcode5(cangkuName.split("\\\\")[i]);
//            }else if(i==5){
//                b.setCwhcode6(cangkuName.split("\\\\")[i]);
//            }
//        }
        b.setDdate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        b.setId(id)
                .setStockId(stockId)
                .setBcheck("0")
                .setStockRukuNumber(stockRukuNumber)
                .setStockRukuDate(stockRukuDate)
                .setStockCangkuRecordId(stockCangkuRecordId)
                .setStockCangkuId(stockCangkuId)
                .setCwhcode1(stockCangkuId)
                .setCangkuDuli(cangkuDuli)
                .setCunitidType(cunitidType)
                .setIyear(iyear)
                .setStockClass(stockClass)
                .setCmaker(cmaker)
                .setCunitid(cunitid)
                .setBaseQuantity(baseQuantity)
                .setPrice(price)
                .setDvdate(dvdate)
                .setDpdate(dpdate)
                .setIcost(icost)
                .setBatchNumber(batchNumber)
                .setIineId(iineId.longValue())
                .setCunitidF1(cunitidF1)
                .setCunitidF2(cunitidF2)
                .setSubQuantity1(subQuantity1)
                .setSubQuantity2(subQuantity2);
        return stockBeginBalanceRepository.save(b).map(a->R.ok().setResult(a));
    }

    @PostMapping("stockBalanceSave")
    public Mono<R> stockBalanceSave(@RequestBody Map map){
        String citemCode=map.get("citemCode")==null?"":map.get("citemCode").toString();
        String stockRukuNumber=map.get("stockRukuNumber")==null?"":map.get("stockRukuNumber").toString();
        String stockRukuDate=map.get("stockRukuDate")==null?"":map.get("stockRukuDate").toString();
        String stockCangkuRecordId=map.get("stockCangkuRecordId").toString();
        String stockCangkuId=map.get("stockCangkuId").toString();
        String cangkuDuli=map.get("cangkuDuli").toString();
        String cunitidType=map.get("cunitidType").toString();
        String stockClass=map.get("stockClass").toString();
        String cmaker=map.get("cmaker").toString();
        String iyear=map.get("iyear").toString();
        String cangkuName=map.get("cangkuName").toString();
        String stockId=map.get("stockId").toString();
        String cunitid=map.get("cunitid").toString();
        BigDecimal baseQuantity=new BigDecimal(map.get("primaryNumber").toString());
        BigDecimal price=new BigDecimal(map.get("price").toString());
        BigDecimal icost=new BigDecimal(map.get("money").toString());
        String batchNumber=map.get("batchNumber")==null?"":map.get("batchNumber").toString();
        String dpdate=map.get("dpdate")==null?"":map.get("dpdate").toString().split(" ")[0];
        String dvdate=map.get("dvdate")==null?"":map.get("dvdate").toString().split(" ")[0];
        Integer iineId= Integer.valueOf(map.get("iineId").toString());
        String cunitidF1=map.get("cunitidF1")==null?"":map.get("cunitidF1").toString();
        String cunitidF2=map.get("cunitidF2")==null?"":map.get("cunitidF2").toString();
        BigDecimal subQuantity1=map.get("primaryNumber1")==null?new BigDecimal(0):new BigDecimal(map.get("primaryNumber1").toString());
        BigDecimal subQuantity2=map.get("primaryNumber2")==null?new BigDecimal(0):new BigDecimal(map.get("primaryNumber2").toString());

        StockBeginBalance b=new StockBeginBalance();
        for (int i = 0; i < cangkuName.split("\\\\").length; i++) {
            if(i==0){
                b.setCwhcode1(cangkuName.split("\\\\")[i]);
            }else if(i==1){
                b.setCwhcode2(cangkuName.split("\\\\")[i]);
            }else if(i==2){
                b.setCwhcode3(cangkuName.split("\\\\")[i]);
            }else if(i==3){
                b.setCwhcode4(cangkuName.split("\\\\")[i]);
            }else if(i==4){
                b.setCwhcode5(cangkuName.split("\\\\")[i]);
            }else if(i==5){
                b.setCwhcode6(cangkuName.split("\\\\")[i]);
            }
        }
        b.setDdate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        b.setStockId(stockId)
                .setCitemCode(citemCode)
                .setBcheck("0")
                .setStockRukuNumber(stockRukuNumber)
                .setStockRukuDate(stockRukuDate)
                .setStockCangkuRecordId(stockCangkuRecordId)
                .setStockCangkuId(stockCangkuId)
                .setCangkuDuli(cangkuDuli)
                .setCunitidType(cunitidType)
                .setIyear(iyear)
                .setStockClass(stockClass)
                .setCmaker(cmaker)
                .setCunitid(cunitid)
                .setBaseQuantity(baseQuantity)
                .setPrice(price)
                .setIcost(icost)
                .setDvdate(dvdate)
                .setDpdate(dpdate)
                .setBatchNumber(batchNumber)
                .setIineId(iineId.longValue())
                .setCunitidF1(cunitidF1)
                .setCunitidF2(cunitidF2)
                .setSubQuantity1(subQuantity1)
                .setSubQuantity2(subQuantity2);
        return stockBeginBalanceRepository.save(b).map(a->R.ok().setResult(a));
    }

    @GetMapping("getStockBalanceMaxIineId")
    public Mono<R> getStockBalanceMaxIineId(){
        return stockBeginBalanceRepository.getStockBalanceMaxIineId().map(a->R.ok().setResult(a));
    }
    @PostMapping("getStockBalanceColumns")
    public Mono<R> getStockBalanceColumns(String iyear){
        return stockBeginBalanceRepository.getStockBalanceColumns(iyear).map(a->R.ok().setResult(a));
    }

    @PostMapping("findAllStockBalance")
    public Mono<R> findAllStockBalance(@RequestBody Map map, Pageable pageable){
        AtomicReference<Long> totalAR = new AtomicReference(0);
        // 当前页
        int page = map.get("page") == null ? 1 : Integer.parseInt(map.get("page").toString());
        // 页多少条
        int size = map.get("size") == null ? 10000 : Integer.parseInt(map.get("size").toString());
        String iyear=map.get("iyear").toString();
        String stockClass=map.get("stockClass").toString();
        String stockCangkuId=map.get("stockCangkuId").toString();
        Map<String, String> searchMap = ((HashMap<String, String>) map.get("searchConditon"));

        return stockBeginBalanceRepository.findAllStockBalance(iyear).collectList()
                .flatMap(list->{
                    if(StrUtil.isNotBlank(stockCangkuId)){
                        list=list.stream().filter(a->a.getStockCangkuId().equals(stockCangkuId)).collect(Collectors.toList());
                    }
                    if(StrUtil.isNotBlank(stockClass)){
                        list=list.stream().filter(a->a.getStockClass().equals(stockClass)).collect(Collectors.toList());
                    }
                    list.forEach(a->{

                    });
                    long total = list.size();
                    totalAR.set(total);
                    list=list.stream().skip((page - 1) * size).limit(size).collect(Collectors.toList());
                    return Mono.just(list.stream().filter(item -> {
                        // 按条件过滤
                        if (StringUtils.isNotBlank(searchMap.get("requirement")) && StringUtils.isNotBlank(searchMap.get("value"))) {
                            String value = searchMap.get("value");
                            String dbValue = getFieldValueByFieldName(searchMap.get("requirement").trim(), item);
                            if (Objects.nonNull(dbValue) && !dbValue.contains(value)) {
                                return false;
                            }
                        }
                        return true;
                    }));
                })
                .map(a -> R.page(a.collect(Collectors.toList()), pageable, (totalAR.get())));
    }

    // 审核\弃审 库存期初余额
    @PostMapping("auditStockBalance")
    public Mono<R> getStockBalanceColumns(@RequestBody Map map){
        String bcheck=map.get("bcheck").toString();
        String bcheckUser=map.get("bcheckUser").toString();
        String bcheckTime=map.get("bcheckTime").toString();
        List<String> id= (List<String>) map.get("id");
        return stockBeginBalanceRepository.editAuditStockBalance(bcheck,bcheckUser,bcheckTime,id).then(Mono.just(R.ok()));
    }

    @PostMapping("delStockBalance")
    public Mono<R> delStockBalance(@RequestBody Map map){
        List<String> id= (List<String>) map.get("id");
        return stockBeginBalanceRepository.delStockBalance(id).then(Mono.just(R.ok()));
    }

    /**
     * 带批次的期初 是否在 出库单子表中存在
     * @return
     */
    @PostMapping("findByStockWarehousingsCount")
    public Mono<R> findByStockWarehousingsCount(@RequestBody List<StockBeginBalanceVo> list){
        return stockWarehousingsRepository.findAll().collectList()
        .flatMap(stockWarehousingsList->{
            List<StockBeginBalanceVo> collect = list.stream().filter(a -> StrUtil.isNotBlank(a.getBatchNumber())).collect(Collectors.toList());
            String str="";
            for (int i = 0; i < collect.size(); i++) {
                int finalI = i;
                if(StrUtil.isNotBlank(collect.get(i).getDvdate())){
                    long count = stockWarehousingsList.stream().filter(sw -> sw.getCinvode().equals(collect.get(finalI).getStockNum()) && sw.getBatchId().equals(collect.get(finalI).getBatchNumber()) && sw.getDvdate().equals(collect.get(finalI).getDvdate())).count();
                    if(count>0){
                        str="【"+collect.get(finalI).getStockName()+"】已出库,不能弃审";
                        break;
                    }
                }
                else{
                    long count = stockWarehousingsList.stream().filter(sw -> sw.getCinvode().equals(collect.get(finalI).getStockNum()) && sw.getBatchId().equals(collect.get(finalI).getBatchNumber())).count();
                    if(count>0){
                        str="【"+collect.get(finalI).getStockName()+"】已出库,不能弃审";
                        break;
                    }
                }
            }
           return Mono.just(str);
        }).map(a->R.ok().setResult(a));
    }

    @PostMapping("importStockBalanceDuli")
    public Mono<R> importStockBalance(@RequestPart("file") FilePart filePartParm,@RequestPart("iyear") String iyear,@RequestPart("userId") String userId) throws Exception {
        // 获取表头个数
        AtomicInteger column= new AtomicInteger();
        Path tempFilePath = Files.createTempFile("", new String(filePartParm.filename().getBytes("ISO-8859-1"), "UTF-8"));
        return Mono.just(filePartParm)
        // 上传到临时目录
        .flatMap(filePart -> {
            try {
                return DataBufferUtils
                        .write(filePart.content(), AsynchronousFileChannel.open(tempFilePath, StandardOpenOption.WRITE), 0)
                        .doOnComplete(() -> log.info("上传成功"))
                        .collectList()
                        .map(item -> tempFilePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return Mono.just("");
        })
        .flatMap(objArr -> {
            List<Object[]> list = null;
            XlsUtils3 excelReader = new XlsUtils3(tempFilePath.toString());
            String[] str = new String[]{"仓库名称","存货编码","存货名称","规格型号","计量单位","数量","金额","批号","生产日期","失效日期","入库单号","入库日期",""};
            list = excelReader.getExcelObj(tempFilePath.toString(), str, 0);
            Arrays.stream(list.get(0)).forEach(v->{
                if(v!=null){
                    column.addAndGet(1);
                }
            });
            assert tempFilePath != null;
            try {
                Files.delete(tempFilePath);
            } catch (IOException e) {
            }
            return Mono.just(list);
        })
        .flatMap(item->{
            Map mapArr = new HashMap();
            mapArr.put("excellist", item);   // excel中内容
            mapArr.put("code", "200");
            mapArr.put("error", "");
            return stockRepository.findAlls().collectList()
            .flatMap(stockList->{
                mapArr.put("stockList", stockList);
                return stockCangkuRepository.findByCkIsDuli("1").collectList()
                .flatMap(stockCangKuList->{
                    mapArr.put("stockCangKuList", stockCangKuList);
                    // 单计量
                    return sysUnitOfMeaRepository.findAllByFlag().collectList()
                    .flatMap(danJl->{
                        mapArr.put("danJl", danJl);
                        // 多计量
                        return sysUnitOfMeaManyRepository.findAll().collectList()
                        .flatMap(duoJl->{
                            mapArr.put("duoJl", duoJl);
                            // 多计量明细
                            return sysUnitOfMeaListRepository.findAll().collectList()
                            .flatMap(duoJlMX->{
                                mapArr.put("duoJlMX", duoJlMX);
                                return stockBeginBalanceRepository.getStockBalanceMaxIineId()
                                .flatMap(maxIineid->{
                                    mapArr.put("maxIineid", maxIineid);
                                    return Mono.just(mapArr);
                                });
                            });
                        });
                    });
                });
            });
        })
        .flatMap(mapArr -> {
            List<Object[]> excellist = (List<Object[]>) mapArr.get("excellist");
            List<StockVo> stockList = (List<StockVo>) mapArr.get("stockList");
            List<StockCangku> stockCangKuList = (List<StockCangku>) mapArr.get("stockCangKuList");
            List<SysUnitOfMea> danJl = (List<SysUnitOfMea>) mapArr.get("danJl");
            List<SysUnitOfMeaMany> duoJl = (List<SysUnitOfMeaMany>) mapArr.get("duoJl");
            List<SysUnitOfMeaList> duoJlMX = (List<SysUnitOfMeaList>) mapArr.get("duoJlMX");
            Long maxIineid = (Long) mapArr.get("maxIineid");

            List<StockBeginBalance> newList = new ArrayList<>();
            // 如果小于5 说明表头不对
            if (column.get()<12) {
                mapArr.put("error", "导入模板表头不正确，请使用正确的模板进行导入");
                mapArr.put("code", "401");
                return Mono.just(mapArr);
            }
            // 判断是否重复
            for (int i = 1; i < excellist.size(); i++) {
                int a=column.get();
                Object[] obj=excellist.get(i);
                int finalI = i;
                List<String>errorText=new ArrayList<>();
                // 仓库
                if(StringUtils.isBlank(excellist.get(finalI)[0].toString().trim())) {
                    errorText.add("仓库不能为空");
                    obj[a]=errorText.toString();
                }else{
                    long count = stockCangKuList.stream().filter(t -> t.getCkName().equals(excellist.get(finalI)[0].toString().trim()) || t.getCkNum().equals(excellist.get(finalI)[0].toString().trim())&&t.getCkIsDuli().equals("1")).count();
                    if(count==0){
                        errorText.add("仓库在档案中不存在或者不是独立仓库");
                        obj[a]=errorText.toString();
                    }
                }
                // 存货编码
                if(StringUtils.isBlank(excellist.get(finalI)[1].toString().trim())) {
                    errorText.add("存货编码不能为空");
                    obj[a]=errorText.toString();
                }else{
                    List<StockVo> collect = stockList.stream().filter(t -> t.getStockNum().equals(excellist.get(finalI)[1].toString().trim())).collect(Collectors.toList());
                    if(collect.size()==0){
                        errorText.add("存货在档案中不存在");
                        obj[a]=errorText.toString();
                    }else{
                        // 打开批次管理
                        if(collect.get(0).getStockPropertyBatch().equals("1")){
                            if(StringUtils.isBlank(excellist.get(finalI)[7].toString().trim())) {
                                errorText.add("批号不能为空");
                                obj[a]=errorText.toString();
                            }
                        }
                        // 打开有效期管理
                        if(collect.get(0).getStockIndateManage().equals("1")){
                            if(StringUtils.isBlank(excellist.get(finalI)[8].toString().trim()) && StringUtils.isBlank(excellist.get(finalI)[9].toString().trim())) {
                                errorText.add("生产日期和失效日期不能同时为空");
                                obj[a]=errorText.toString();
                            }
                        }
                    }
                }

                StockVo stockInfo = stockList.stream().filter(t -> t.getStockNum().equals(excellist.get(finalI)[1].toString().trim())).collect(Collectors.toList()).get(0);

                // 计量单位
                if(StringUtils.isBlank(excellist.get(finalI)[4].toString().trim())) {
                    errorText.add("计量单位不能为空");
                    obj[a]=errorText.toString();
                }else{
                    if(stockInfo.getStockMeasurementType().equals("单计量")){
                        List<SysUnitOfMea> dan = danJl.stream().filter(d -> d.getUnitName().equals(excellist.get(finalI)[4].toString().trim())).collect(Collectors.toList());
                        if(dan.size()==0 ){
                            errorText.add("计量单位在系统中不存在");
                            obj[a]=errorText.toString();
                        }
                    }else{
                        SysUnitOfMeaMany sysUnitOfMeaMany = duoJl.stream().filter(d -> d.getId().equals(stockInfo.getStockMeasurementUnit())).collect(Collectors.toList()).get(0);
                        List<SysUnitOfMeaList> duomx = duoJlMX.stream().filter(mx->mx.getManyCode().equals(sysUnitOfMeaMany.getUnitCode()) && mx.getUnitName().equals(excellist.get(finalI)[4].toString().trim())).collect(Collectors.toList());
                        if(duomx.size()==0 ){
                            errorText.add("计量单位在系统中不存在");
                            obj[a]=errorText.toString();
                        }
                    }
                }
                // 数量
                if(StringUtils.isBlank(excellist.get(finalI)[5].toString().trim())) {
                    errorText.add("数量不能为空");
                    obj[a]=errorText.toString();
                }else{
                    if(Double.valueOf(excellist.get(finalI)[5].toString().trim())==0){
                        errorText.add("数量必须是大于0的整数");
                        obj[a]=errorText.toString();
                    }else{
                        boolean numeric4 = NewStringUtil.isNumeric4(excellist.get(finalI)[5].toString().trim());
                        if(!numeric4){
                            errorText.add("数量必须是大于0的整数");
                            obj[a]=errorText.toString();
                        }
                    }
                }

                // 金额
                if(StringUtils.isBlank(excellist.get(finalI)[6].toString().trim())) {
                    errorText.add("金额不能为空");
                    obj[a]=errorText.toString();
                }else{
                    if(Double.valueOf(excellist.get(finalI)[6].toString().trim())==0){
                        errorText.add("金额必须是大于0的整数");
                        obj[a]=errorText.toString();
                    }else{
                        boolean numeric4 = NewStringUtil.isNumeric4(excellist.get(finalI)[6].toString().trim());
                        if(!numeric4){
                            errorText.add("金额必须是大于0的整数");
                            obj[a]=errorText.toString();
                        }
                    }
                }

                if(errorText.size()==0){
                    StockVo stock = stockList.stream().filter(t -> t.getStockNum().equals(excellist.get(finalI)[1].toString().trim())).collect(Collectors.toList()).get(0);
                    BigDecimal number=new BigDecimal(excellist.get(finalI)[5].toString().trim());
                    BigDecimal money=new BigDecimal(excellist.get(finalI)[6].toString().trim());

                    StockBeginBalance b=new StockBeginBalance();
                    b.setIyear(iyear);
                    b.setDdate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                    b.setStockId(stock.getStockNum());
                    b.setStockClass(stock.getStockClass());
                    b.setCmaker(userId);
                    b.setCunitidType(stock.getStockMeasurementType());
                    b.setCunitid(stock.getStockPurchaseUnit());
                    b.setBaseQuantity(number);

                    if(stock.getStockMeasurementType().equals("多计量")){
                        SysUnitOfMeaMany duo = duoJl.stream().filter(dan -> dan.getId().equals(stock.getStockMeasurementUnit())).collect(Collectors.toList()).get(0);
                        SysUnitOfMeaList duomx = duoJlMX.stream().filter(mx -> mx.getManyCode().equals(duo.getUnitCode()) && mx.getUnitName().equals(excellist.get(finalI)[4].toString().trim())).collect(Collectors.toList()).get(0);
                        List<SysUnitOfMeaList> duomxNoMain = duoJlMX.stream().filter(mx -> mx.getManyCode().equals(duo.getUnitCode()) && mx.getIsMain().equals("false")).collect(Collectors.toList());

                        if(duomx.getIsMain().equals("true")){
                            b.setCunitid(duomx.getId());
                            b.setBaseQuantity(number);
                            if(duomxNoMain.size()==1){
                                b.setCunitidF1(duomxNoMain.get(0).getId());
                                b.setSubQuantity1(number.divide(new BigDecimal(duomxNoMain.get(0).getConversionRate()),2,BigDecimal.ROUND_HALF_UP));
                            }else if(duomxNoMain.size()==2){
                                b.setCunitidF1(duomxNoMain.get(0).getId());
                                b.setSubQuantity1(number.divide(new BigDecimal(duomxNoMain.get(0).getConversionRate()),2,BigDecimal.ROUND_HALF_UP));
                                b.setCunitidF2(duomxNoMain.get(1).getId());
                                b.setSubQuantity2(number.divide(new BigDecimal(duomxNoMain.get(1).getConversionRate()),2,BigDecimal.ROUND_HALF_UP));
                            }
                        }
                        else {
                            if (duomxNoMain.size() == 1) {
                                b.setBaseQuantity(number.multiply(new BigDecimal(duomxNoMain.get(0).getConversionRate()).setScale(2, BigDecimal.ROUND_HALF_UP)));
                                b.setCunitidF1(duomxNoMain.get(0).getId());
                                b.setSubQuantity1(number);
                            } else if (duomxNoMain.size() == 2) {
                                b.setCunitidF1(duomxNoMain.get(0).getId());
                                b.setCunitidF2(duomxNoMain.get(1).getId());

                                // 所在list 索引
                                int index = duomxNoMain.stream().map(aa->aa.getUnitName()).collect(Collectors.toList()).indexOf(excellist.get(finalI)[4].toString().trim());
                                // 计量1
                                if (index == 0) {
                                    b.setSubQuantity1(number);
                                    b.setBaseQuantity(number.multiply(new BigDecimal(duomxNoMain.get(0).getConversionRate())).setScale(2, BigDecimal.ROUND_HALF_UP));
                                    b.setSubQuantity2(b.getBaseQuantity().divide(new BigDecimal(duomxNoMain.get(1).getConversionRate()), 2, BigDecimal.ROUND_HALF_UP));
                                }else if(index==1){
                                    b.setSubQuantity2(number);
                                    b.setBaseQuantity(number.multiply(new BigDecimal(duomxNoMain.get(1).getConversionRate())).setScale(2, BigDecimal.ROUND_HALF_UP));
                                    b.setSubQuantity1(b.getBaseQuantity().divide(new BigDecimal(duomxNoMain.get(0).getConversionRate()), 2, BigDecimal.ROUND_HALF_UP));
                                }
                            }
                        }
                    }

                    b.setPrice(money.divide(number,10,BigDecimal.ROUND_HALF_UP));
                    b.setIcost(money);
                    b.setBcheck("0");
                    b.setCangkuDuli("1");
                    b.setIineId(maxIineid);

                    List<StockCangku> stockCangkus = stockCangKuList.stream().filter(t -> t.getCkName().equals(excellist.get(finalI)[0].toString().trim()) || t.getCkNum().equals(excellist.get(finalI)[0].toString().trim())).collect(Collectors.toList());
                    b.setStockCangkuId(stockCangkus.get(0).getId());

                    // 开启有效期管理
                    if(stock.getStockIndateManage().equals("1")){
                        if(StrUtil.isNotBlank(excellist.get(finalI)[9].toString().trim()) && StrUtil.isNotBlank(excellist.get(finalI)[8].toString().trim())){
                            // 优先已失效日期 计算出 生效日期
                            String dpdate = null;
                            try {
                                dpdate = DateTestUtil(NewStringUtil.regExpStr(excellist.get(finalI)[9].toString().trim()), stock.getStockIndateUnit(), Integer.valueOf(stock.getStockIndateDuration())*-1);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            b.setDpdate(dpdate);
                            b.setDvdate(excellist.get(finalI)[9].toString().trim());
                        }else{
                            // 失效日期
                            if(StrUtil.isNotBlank(excellist.get(finalI)[9].toString().trim())){
                                try {
                                    String dpdate = DateTestUtil(NewStringUtil.regExpStr(excellist.get(finalI)[9].toString().trim()), stock.getStockIndateUnit(), Integer.valueOf(stock.getStockIndateDuration())*-1);
                                    b.setDpdate(dpdate);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                            // 生效日期
                            else if(StrUtil.isNotBlank(excellist.get(finalI)[8].toString().trim())){
                                try {
                                    String dvdate = DateTestUtil(NewStringUtil.regExpStr(excellist.get(finalI)[8].toString().trim()), stock.getStockIndateUnit(), Integer.valueOf(stock.getStockIndateDuration()));
                                    b.setDvdate(dvdate);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    b.setBatchNumber(excellist.get(finalI)[7].toString().trim());
                    b.setStockRukuNumber(excellist.get(finalI)[10].toString().trim());
                    b.setStockRukuDate(excellist.get(finalI)[11].toString().trim());
                    newList.add(b);
                    maxIineid++;
                }
                else{ mapArr.put("code", "200");mapArr.put("error", "error");}
            }
            mapArr.put("list", newList);
            mapArr.put("column", column.get());
            return Mono.just(mapArr);
        })
        .flatMapMany(mapArr -> {
            List<StockBeginBalance> list = (List<StockBeginBalance>) mapArr.get("list");
            return mapArr.get("code").equals("401") ? Mono.just(mapArr) :list.size()==0?Mono.just(mapArr): stockBeginBalanceRepository.saveAll(list).map(a->mapArr);
        })
        .collectList()
        .map(a->R.ok().setResult(a));
    }
    @PostMapping("importStockBalanceJiBie")
    public Mono<R> importStockBalanceJiBie(@RequestPart("file") FilePart filePartParm,@RequestPart("iyear") String iyear,@RequestPart("userId") String userId) throws Exception {
        // 获取表头个数
        AtomicInteger column= new AtomicInteger();
        Path tempFilePath = Files.createTempFile("", new String(filePartParm.filename().getBytes("ISO-8859-1"), "UTF-8"));
        return Mono.just(filePartParm)
                // 上传到临时目录
                .flatMap(filePart -> {
                    try {
                        return DataBufferUtils
                                .write(filePart.content(), AsynchronousFileChannel.open(tempFilePath, StandardOpenOption.WRITE), 0)
                                .doOnComplete(() -> log.info("上传成功"))
                                .collectList()
                                .map(item -> tempFilePath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return Mono.just("");
                })
                .flatMap(objArr -> {
                    List<Object[]> list = null;
                    XlsUtils3 excelReader = new XlsUtils3(tempFilePath.toString());
                    String[] str = new String[]{"*仓库级别1*","仓库级别2","仓库级别3","仓库级别4","仓库级别5","仓库级别6","*存货编码*","存货名称","规格型号","*主数量*","单价","*金额*","批号","生产日期","失效日期","入库单号","入库日期",""};
                    list = excelReader.getExcelObj(tempFilePath.toString(), str, 0);

                    Arrays.stream(list.get(0)).forEach(v->{
                        if(v!=null){
                            column.addAndGet(1);
                        }
                    });

                    assert tempFilePath != null;
                    try {
                        Files.delete(tempFilePath);
                    } catch (IOException e) {
                    }
                    return Mono.just(list);
                })
                .flatMap(item->{
                    Map mapArr = new HashMap();
                    mapArr.put("excellist", item);   // excel中内容
                    mapArr.put("code", "200");
                    mapArr.put("error", "");
                    return stockRepository.findAlls().collectList()
                            .flatMap(stockList->{
                                mapArr.put("stockList", stockList);
                                return stockCangkuRepository.findAll().collectList()
                                        .flatMap(stockCangKuList->{
                                            mapArr.put("stockCangKuList", stockCangKuList);
                                            // 单计量
                                            return sysUnitOfMeaRepository.findAllByFlag().collectList()
                                                    .flatMap(danJl->{
                                                        mapArr.put("danJl", danJl);
                                                        // 多计量
                                                        return sysUnitOfMeaManyRepository.findAll().collectList()
                                                                .flatMap(duoJl->{
                                                                    mapArr.put("duoJl", duoJl);
                                                                    // 多计量明细
                                                                    return sysUnitOfMeaListRepository.findAll().collectList()
                                                                            .flatMap(duoJlMX->{
                                                                                mapArr.put("duoJlMX", duoJlMX);
                                                                                return stockBeginBalanceRepository.getStockBalanceMaxIineId()
                                                                                        .flatMap(maxIineid->{
                                                                                            mapArr.put("maxIineid", maxIineid);
                                                                                            return stockCangkuLevelRecordRepository.findAll().collectList()
                                                                                                    .flatMap(cangkuRecordList->{
                                                                                                        mapArr.put("cangkuRecordList", cangkuRecordList);
                                                                                                        return Mono.just(mapArr);
                                                                                                    });
                                                                                        });
                                                                            });
                                                                });
                                                    });
                                        });
                            });
                })
                .flatMap(mapArr -> {
                    List<Object[]> excellist = (List<Object[]>) mapArr.get("excellist");
                    List<StockVo> stockList = (List<StockVo>) mapArr.get("stockList");
                    List<StockCangku> stockCangKuList = (List<StockCangku>) mapArr.get("stockCangKuList");
                    List<SysUnitOfMea> danJl = (List<SysUnitOfMea>) mapArr.get("danJl");
                    List<SysUnitOfMeaMany> duoJl = (List<SysUnitOfMeaMany>) mapArr.get("duoJl");
                    List<SysUnitOfMeaList> duoJlMX = (List<SysUnitOfMeaList>) mapArr.get("duoJl");
                    List<StockCangkuLevelRecord> cangkuRecordList = (List<StockCangkuLevelRecord>) mapArr.get("cangkuRecordList");
                    Long maxIineid = (Long) mapArr.get("maxIineid");

                    List<StockBeginBalance> newList = new ArrayList<>();
                    // 如果小于5 说明表头不对
                    if (column.get()<12) {
                        mapArr.put("error", "导入模板表头不正确，请使用正确的模板进行导入");
                        mapArr.put("code", "401");
                        return Mono.just(mapArr);
                    }
                    // 判断是否重复

                    for (int i = 1; i < excellist.size(); i++) {
                        int a=column.get();
                        Object[] obj=excellist.get(i);
                        int finalI = i;
                        List<String>errorText=new ArrayList<>();
                        int jibie=1;
                        // 1仓库 【仓库档案：标志是级别仓库】
                        if(StringUtils.isBlank(excellist.get(finalI)[0].toString().trim())) {
                            errorText.add("级别仓库1不能为空");
                            obj[a]=errorText.toString();
                        }else{
                            long count = stockCangKuList.stream().filter(t -> t.getCkName().equals(excellist.get(finalI)[0].toString().trim())).count();
                            if(count==0){
                                errorText.add("级别仓库1在档案中不存在");
                                obj[a]=errorText.toString();
                            }
                        }
                        // 2仓库
                        if(StringUtils.isNotBlank(excellist.get(finalI)[1].toString().trim())) {
                            List<StockCangku> jibieCangKu = stockCangKuList.stream().filter(t -> t.getCkName().equals(excellist.get(finalI)[0].toString().trim())).collect(Collectors.toList());
                            // 级别仓库档案
                            List<StockCangkuLevelRecord> collect = cangkuRecordList.stream().filter(t -> t.getCangkuId().equals(jibieCangKu.get(0).getId()) && t.getRecordName().equals(excellist.get(finalI)[1].toString().trim())).collect(Collectors.toList());
                            if(collect.size()==0){
                                errorText.add("级别仓库2在档案中不存在");
                                obj[a]=errorText.toString();
                            }else{
                                jibie=2;
                            }
                        }
                        // 3仓库
                        if(StringUtils.isNotBlank(excellist.get(finalI)[2].toString().trim())) {
                            List<StockCangku> jibieCangKu = stockCangKuList.stream().filter(t -> t.getCkName().equals(excellist.get(finalI)[0].toString().trim())).collect(Collectors.toList());
                            // 级别仓库档案
                            List<StockCangkuLevelRecord> collect = cangkuRecordList.stream().filter(t -> t.getCangkuId().equals(jibieCangKu.get(0).getId()) && t.getRecordName().equals(excellist.get(finalI)[2].toString().trim())).collect(Collectors.toList());
                            if(collect.size()==0){
                                errorText.add("级别仓库3在档案中不存在");
                                obj[a]=errorText.toString();
                            }else{
                                jibie=3;
                            }
                        }
                        // 4仓库
                        if(StringUtils.isNotBlank(excellist.get(finalI)[3].toString().trim())) {
                            List<StockCangku> jibieCangKu = stockCangKuList.stream().filter(t -> t.getCkName().equals(excellist.get(finalI)[0].toString().trim())).collect(Collectors.toList());
                            // 级别仓库档案
                            List<StockCangkuLevelRecord> collect = cangkuRecordList.stream().filter(t -> t.getCangkuId().equals(jibieCangKu.get(0).getId()) && t.getRecordName().equals(excellist.get(finalI)[3].toString().trim())).collect(Collectors.toList());
                            if(collect.size()==0){
                                errorText.add("级别仓库4在档案中不存在");
                                obj[a]=errorText.toString();
                            }else{
                                jibie=4;
                            }
                        }
                        // 5仓库
                        if(StringUtils.isNotBlank(excellist.get(finalI)[4].toString().trim())) {
                            List<StockCangku> jibieCangKu = stockCangKuList.stream().filter(t -> t.getCkName().equals(excellist.get(finalI)[0].toString().trim())).collect(Collectors.toList());
                            // 级别仓库档案
                            List<StockCangkuLevelRecord> collect = cangkuRecordList.stream().filter(t -> t.getCangkuId().equals(jibieCangKu.get(0).getId()) && t.getRecordName().equals(excellist.get(finalI)[4].toString().trim())).collect(Collectors.toList());
                            if(collect.size()==0){
                                errorText.add("级别仓库5在档案中不存在");
                                obj[a]=errorText.toString();
                            }else{
                                jibie=5;
                            }
                        }
                        // 6仓库
                        if(StringUtils.isNotBlank(excellist.get(finalI)[5].toString().trim())) {
                            List<StockCangku> jibieCangKu = stockCangKuList.stream().filter(t -> t.getCkName().equals(excellist.get(finalI)[0].toString().trim())).collect(Collectors.toList());
                            // 级别仓库档案
                            List<StockCangkuLevelRecord> collect = cangkuRecordList.stream().filter(t -> t.getCangkuId().equals(jibieCangKu.get(0).getId()) && t.getRecordName().equals(excellist.get(finalI)[5].toString().trim())).collect(Collectors.toList());
                            if(collect.size()==0){
                                errorText.add("级别仓库6在档案中不存在");
                                obj[a]=errorText.toString();
                            }else{
                                jibie=6;
                            }
                        }

                        // 判断仓库之间有空值
                        String cangkuNull=excellist.get(finalI)[0].toString().trim()+","+excellist.get(finalI)[1].toString().trim()+","+
                                            excellist.get(finalI)[2].toString().trim()+","+excellist.get(finalI)[3].toString().trim()+","+
                                            excellist.get(finalI)[4].toString().trim()+","+excellist.get(finalI)[5].toString().trim();
                        for (int i1 = 1; i1 < cangkuNull.split(",").length; i1++) {
                            if(cangkuNull.split(",")[i1].equals("")){
                                errorText.add("级别仓库不正确");
                                obj[a]=errorText.toString();
                            }
                        }

                        // 存货编码
                        if(StringUtils.isBlank(excellist.get(finalI)[6].toString().trim())) {
                            errorText.add("存货编码不能为空");
                            obj[a]=errorText.toString();
                        }else{
                            List<StockVo> collect = stockList.stream().filter(t -> t.getStockNum().equals(excellist.get(finalI)[6].toString().trim())).collect(Collectors.toList());
                            if(collect.size()==0){
                                errorText.add("存货在档案中不存在");
                                obj[a]=errorText.toString();
                            }else{
                                // 打开批次管理
                                if(collect.get(0).getStockPropertyBatch().equals("1")){
                                    if(StringUtils.isBlank(excellist.get(finalI)[12].toString().trim())) {
                                        errorText.add("批号不能为空");
                                        obj[a]=errorText.toString();
                                    }
                                }
                                // 打开有效期管理
                                if(collect.get(0).getStockIndateManage().equals("1")){
                                    if(StringUtils.isBlank(excellist.get(finalI)[13].toString().trim()) && StringUtils.isBlank(excellist.get(finalI)[14].toString().trim())) {
                                        errorText.add("生产日期和失效日期不能同时为空");
                                        obj[a]=errorText.toString();
                                    }
                                }
                            }
                        }
                        // 主数量
                        if(StringUtils.isBlank(excellist.get(finalI)[9].toString().trim())) {
                            errorText.add("主数量不能为空");
                            obj[a]=errorText.toString();
                        }else{
                            if(Double.valueOf(excellist.get(finalI)[9].toString().trim())==0){
                                errorText.add("主数量必须是大于0的整数");
                                obj[a]=errorText.toString();
                            }else{
                                boolean numeric4 = NewStringUtil.isNumeric4(excellist.get(finalI)[9].toString().trim());
                                if(!numeric4){
                                    errorText.add("主数量必须是大于0的整数");
                                    obj[a]=errorText.toString();
                                }
                            }
                        }
                        // 单价&金额
                        if(StringUtils.isBlank(excellist.get(finalI)[10].toString().trim()) && StringUtils.isBlank(excellist.get(finalI)[11].toString().trim())) {
                            errorText.add("单价和金额不能同时为空");
                            obj[a]=errorText.toString();
                        }

                        if(errorText.size()==0){
                            StockVo stock = stockList.stream().filter(t -> t.getStockNum().equals(excellist.get(finalI)[6].toString().trim())).collect(Collectors.toList()).get(0);
                            StockBeginBalance b=new StockBeginBalance();
                            b.setIyear(iyear);
                            b.setDdate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                            b.setStockId(stock.getId());
                            b.setStockClass(stock.getStockClass());
                            b.setCmaker(userId);
                            b.setCunitidType(stock.getStockMeasurementType());

                            if(stock.getStockMeasurementType().equals("单计量")){
                                List<SysUnitOfMea> collect = danJl.stream().filter(dan -> dan.getId().equals(stock.getStockMeasurementUnit())).collect(Collectors.toList());
                                if(collect.size()>0){
                                    b.setCunitid(collect.get(0).getId());
                                }
                            }
                            else{
                                List<SysUnitOfMeaMany> collect = duoJl.stream().filter(dan -> dan.getId().equals(stock.getStockMeasurementUnit())).collect(Collectors.toList());
                                if(collect.size()>0){
                                    List<SysUnitOfMeaList> collect1 = duoJlMX.stream().filter(mx -> mx.getUnitCode().equals(collect.get(0).getUnitCode())).collect(Collectors.toList());
                                    if(collect1.size()==1){
                                        b.setCunitid(collect1.get(0).getId());
                                    }
                                    else if(collect1.size()==2){
                                        collect1.forEach(mx->{
                                            if(mx.equals("true")){
                                                b.setCunitid(mx.getId());
                                            }else{
                                                b.setCunitidF1(mx.getId());
                                            }
                                        });
                                    }
                                    else if(collect1.size()==3){
                                        collect1.forEach(mx->{
                                            if(mx.equals("true")){
                                                b.setCunitid(mx.getId());
                                            }
                                        });
                                        List<SysUnitOfMeaList> aFalse = collect1.stream().filter(mx -> mx.getIsMain().equals("false")).collect(Collectors.toList());
                                        b.setCunitidF1(aFalse.get(0).getId());
                                        b.setCunitidF2(aFalse.get(0).getId());
                                    }
                                }
                            }

                            BigDecimal number=new BigDecimal(excellist.get(finalI)[9].toString().trim());
                            BigDecimal price=StringUtils.isBlank(excellist.get(finalI)[10].toString().trim())?new BigDecimal(0):new BigDecimal(NewStringUtil.regExpStr(excellist.get(finalI)[10].toString().trim()));
                            BigDecimal money=StringUtils.isBlank(excellist.get(finalI)[11].toString().trim())?new BigDecimal(0):new BigDecimal(NewStringUtil.regExpStr(excellist.get(finalI)[11].toString().trim()));

                            if(stock.getStockMeasurementType().equals("单计量")){
                                b.setBaseQuantity(number);
                                if(price.compareTo(BigDecimal.ZERO) == 0){
                                    b.setIcost(money.divide(number));
                                }else if(money.compareTo(BigDecimal.ZERO) == 0){
                                    b.setPrice(number.multiply(price));
                                }
                            }
                            else{
                                List<SysUnitOfMeaMany> collect = duoJl.stream().filter(dan -> dan.getId().equals(stock.getStockMeasurementUnit())).collect(Collectors.toList());
                                if(collect.size()>0){
                                    List<SysUnitOfMeaList> collect1 = duoJlMX.stream().filter(mx -> mx.getUnitCode().equals(collect.get(0).getUnitCode())).collect(Collectors.toList());
                                    if(collect1.size()==1){
                                        b.setBaseQuantity(number);
                                    }else if(collect1.size()==2){
                                        // 辅计量换算率
                                        List<SysUnitOfMeaList> aFalse = collect1.stream().filter(mx -> mx.getIsMain().equals("false")).collect(Collectors.toList());
                                        BigDecimal conversionRate=new BigDecimal(aFalse.get(0).getConversionRate());

                                        b.setSubQuantity1(number.divide(conversionRate));
                                    }else if(collect1.size()==3){
                                        // 辅计量换算率
                                        List<SysUnitOfMeaList> aFalse = collect1.stream().filter(mx -> mx.getIsMain().equals("false")).collect(Collectors.toList());
                                        BigDecimal conversionRate1=new BigDecimal(aFalse.get(0).getConversionRate());
                                        BigDecimal conversionRate2=new BigDecimal(aFalse.get(1).getConversionRate());

                                        b.setSubQuantity1(number.divide(conversionRate1));
                                        b.setSubQuantity2(number.divide(conversionRate2));
                                    }
                                }
                            }

                            b.setPrice(price);
                            b.setIcost(money);
                            b.setBcheck("0");
                            b.setIineId(maxIineid);

                            List<StockCangku> stockCangkus = stockCangKuList.stream().filter(t -> t.getCkName().equals(excellist.get(finalI)[0].toString().trim())).collect(Collectors.toList());
                            b.setStockCangkuId(stockCangkus.get(0).getId());
                            b.setCwhcode1(stockCangkus.get(0).getCkName());
                            b.setCangkuDuli(stockCangkus.get(0).getCkIsDuli());

                            if(jibie==2){
                                List<StockCangkuLevelRecord> collect = cangkuRecordList.stream().filter(r -> r.getCangkuId().equals(stockCangkus.get(0).getId()) && r.getRecordName().equals(excellist.get(finalI)[1].toString().trim())).collect(Collectors.toList());
                                b.setCwhcode2(collect.get(0).getRecordName());
                                b.setStockCangkuRecordId(collect.get(0).getId());
                            }else if(jibie==3){
                                b.setCwhcode2(excellist.get(finalI)[1].toString().trim());

                                List<StockCangkuLevelRecord> collect = cangkuRecordList.stream().filter(r -> r.getCangkuId().equals(stockCangkus.get(0).getId()) && r.getRecordName().equals(excellist.get(finalI)[2].toString().trim())).collect(Collectors.toList());
                                b.setCwhcode3(collect.get(0).getRecordName());
                                b.setStockCangkuRecordId(collect.get(0).getId());
                            }else if(jibie==4){
                                b.setCwhcode2(excellist.get(finalI)[1].toString().trim());
                                b.setCwhcode3(excellist.get(finalI)[2].toString().trim());

                                List<StockCangkuLevelRecord> collect = cangkuRecordList.stream().filter(r -> r.getCangkuId().equals(stockCangkus.get(0).getId()) && r.getRecordName().equals(excellist.get(finalI)[3].toString().trim())).collect(Collectors.toList());
                                b.setCwhcode4(collect.get(0).getRecordName());
                                b.setStockCangkuRecordId(collect.get(0).getId());
                            }else if(jibie==5){
                                b.setCwhcode2(excellist.get(finalI)[1].toString().trim());
                                b.setCwhcode3(excellist.get(finalI)[2].toString().trim());
                                b.setCwhcode4(excellist.get(finalI)[3].toString().trim());

                                List<StockCangkuLevelRecord> collect = cangkuRecordList.stream().filter(r -> r.getCangkuId().equals(stockCangkus.get(0).getId()) && r.getRecordName().equals(excellist.get(finalI)[4].toString().trim())).collect(Collectors.toList());
                                b.setCwhcode5(collect.get(0).getRecordName());
                                b.setStockCangkuRecordId(collect.get(0).getId());
                            }else if(jibie==6){
                                b.setCwhcode2(excellist.get(finalI)[1].toString().trim());
                                b.setCwhcode3(excellist.get(finalI)[2].toString().trim());
                                b.setCwhcode4(excellist.get(finalI)[3].toString().trim());
                                b.setCwhcode5(excellist.get(finalI)[4].toString().trim());

                                List<StockCangkuLevelRecord> collect = cangkuRecordList.stream().filter(r -> r.getCangkuId().equals(stockCangkus.get(0).getId()) && r.getRecordName().equals(excellist.get(finalI)[5].toString().trim())).collect(Collectors.toList());
                                b.setCwhcode6(collect.get(0).getRecordName());
                                b.setStockCangkuRecordId(collect.get(0).getId());
                            }

                            // 开启有效期管理
                            if(stock.getStockIndateManage().equals("1")){
                                if(StrUtil.isNotBlank(excellist.get(finalI)[13].toString().trim()) && StrUtil.isNotBlank(excellist.get(finalI)[14].toString().trim())){
                                    // 优先已失效日期 计算出 生效日期
                                    String dpdate = null;
                                    try {
                                        dpdate = DateTestUtil(NewStringUtil.regExpStr(excellist.get(finalI)[14].toString().trim()), stock.getStockIndateUnit(), Integer.valueOf(stock.getStockIndateDuration())*-1);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    b.setDpdate(dpdate);
                                    b.setDvdate(excellist.get(finalI)[14].toString().trim());
                                }else{
                                    // 失效日期
                                    if(StrUtil.isNotBlank(excellist.get(finalI)[14].toString().trim())){
                                        try {
                                            String dpdate = DateTestUtil(NewStringUtil.regExpStr(excellist.get(finalI)[14].toString().trim()), stock.getStockIndateUnit(), Integer.valueOf(stock.getStockIndateDuration())*-1);
                                            b.setDpdate(dpdate);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    // 生效日期
                                    else if(StrUtil.isNotBlank(excellist.get(finalI)[13].toString().trim())){
                                        try {
                                            String dvdate = DateTestUtil(NewStringUtil.regExpStr(excellist.get(finalI)[13].toString().trim()), stock.getStockIndateUnit(), Integer.valueOf(stock.getStockIndateDuration()));
                                            b.setDvdate(dvdate);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }else{
                                b.setDpdate(excellist.get(finalI)[13].toString().trim());
                                b.setDvdate(excellist.get(finalI)[14].toString().trim());
                            }
                            b.setBatchNumber(excellist.get(finalI)[12].toString().trim());
                            b.setStockRukuNumber(excellist.get(finalI)[15].toString().trim());
                            b.setStockRukuDate(excellist.get(finalI)[16].toString().trim());
                            newList.add(b);
                            maxIineid++;
                        }
                        else{ mapArr.put("code", "200");mapArr.put("error", "error");}
                    }
                    mapArr.put("list", newList);
                    mapArr.put("column", column.get());
                    return Mono.just(mapArr);
                })
                .flatMapMany(mapArr -> {
                    List<StockBeginBalance> list = (List<StockBeginBalance>) mapArr.get("list");
                    return mapArr.get("code").equals("401") ? Mono.just(mapArr) :list.size()==0?Mono.just(mapArr): stockBeginBalanceRepository.saveAll(list).map(a->mapArr);
                })
                .collectList()
                .map(a->R.ok().setResult(a));
    }

    @PostMapping("findByStock_CKAndTH")
    public Mono<R> findByStock_CKAndTH(String stockNum,String iyear) {
        return stockBeginBalanceRepository.findByStock_CKAndTH(stockNum,iyear).map(a->R.ok().setResult(a));
    }

    @GetMapping("getAllProject")
    public Mono<R> getAllProject(){
        return projectRepositoryBase.findAll().collectList().map(a->R.ok().setResult(a));
    }

    /************************************** 查询任务 ********************************************/
    @PostMapping("getByStockBalanceTask")
    public Mono<R> getByStockBalanceTask(@RequestBody Map map){
        String name=map.get("name").toString();
        String iyear=map.get("iyear").toString();
        List<String> method=Arrays.asList(map.get("method").toString().split(","));
        String recordNum=map.get("recordNum")==null?"":map.get("recordNum").toString();

        Mono<R> empty=taskRepository.findAllByFunctionModule2(name,iyear,method).collectList().map(a->R.ok().setResult(a)).defaultIfEmpty(R.ok().setResult(""));
        Mono<R> noEmpty=taskRepository.findAllByFunctionModule3(name,iyear,method,recordNum).collectList().map(a->R.ok().setResult(a)).defaultIfEmpty(R.ok().setResult(""));
       return StrUtil.isNotBlank(recordNum)?noEmpty:empty;
    }

    @PostMapping("getByStockBalanceBatchTask")
    public Mono<R> getByStockBalanceBatchTask(@RequestBody Map map){
        String iyear=map.get("iyear").toString();
        List<String> name=Arrays.asList(map.get("name").toString().split(","));
        List<String> method=Arrays.asList(map.get("method").toString().split(","));
        List<String>  recordNum=map.get("recordNum")==null?null:Arrays.asList(map.get("recordNum").toString().split(","));
        Mono<R> empty=taskRepository.findAllByFunctionModule4(name,iyear,method).collectList().map(a->R.ok().setResult(a)).defaultIfEmpty(R.ok().setResult(""));
        Mono<R> noEmpty=taskRepository.findAllByFunctionModule5(name,iyear,method,recordNum).collectList().map(a->R.ok().setResult(a)).defaultIfEmpty(R.ok().setResult(""));
        return null != recordNum?noEmpty:empty;
    }

    @PostMapping("stockBalanceTaskSave")
    public Mono<R> stockBalanceTaskSave(@RequestBody Map map){
        String iyear=map.get("iyear").toString();
        String userName=map.get("userName").toString();
        String functionModule=map.get("functionModule").toString();
        String method=map.get("method").toString();
        String recordNum=map.get("recordNum")==null?"":map.get("recordNum").toString();
        String caozuoModule=map.get("caozuoModule")==null?"":map.get("caozuoModule").toString();
        Task obj=new Task();
        obj.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()))
                .setFunctionModule(functionModule)
                .setMethod(method)
                .setIyear(iyear)
                .setCaozuoUnique(userName)
                .setRecordNum(recordNum)
                .setCaozuoModule(caozuoModule)
                .setState("1");
        return taskRepository.save(obj).map(a->R.ok().setResult(a));
    }

    @PostMapping("stockBalanceTaskBatchSave")
    public Mono<R> stockBalanceTaskBatchSave(@RequestBody Map map){
        String iyear=map.get("iyear").toString();
        String userName=map.get("userName").toString();
        String functionModule=map.get("functionModule").toString();
        String method=map.get("method").toString();
        List<String>  recordNums=map.get("recordNum")==null?null:Arrays.asList(map.get("recordNum").toString().split(","));
        String caozuoModule=map.get("caozuoModule")==null?"":map.get("caozuoModule").toString();
        List<Task> saves = recordNums.stream().map(k -> {
            Task obj = new Task();
            obj.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()))
                    .setFunctionModule(functionModule)
                    .setMethod(method)
                    .setIyear(iyear)
                    .setCaozuoUnique(userName)
                    .setRecordNum(k)
                    .setCaozuoModule(caozuoModule)
                    .setState("1");
            return obj;
        }).collect(Collectors.toList());
        return taskRepository.saveAll(saves).collectList().map(a->R.ok().setResult(a));
    }
    @PostMapping("deleteByMethodAndRecordNum")
    public Mono<R> stockBalanceTaskSave(String method,String ccode){
        return taskRepository.deleteByMethodAndRecordNum(method,ccode).then(Mono.just(R.ok()));
    }
    @PostMapping("stockBalanceTaskEditNewTime")
    public Mono<R> stockBalanceTaskSave(String id){
        return taskRepository.editTimeByid(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),id).then(Mono.just(R.ok()));
    }
    @PostMapping("stockBalanceTaskDelByUserName")
    public Mono<R> stockBalanceTaskDelByUserName(@RequestBody Map map){
        String iyear=map.get("iyear").toString();
        String userName=map.get("userName").toString();
        String functionModule=map.get("functionModule").toString();
        String method=map.get("method").toString();
        String recordNum=map.get("recordNum")==null?"":map.get("recordNum").toString();

        Mono<R> empty=taskRepository.deleteByFunctionModuleAndCaozuoUnique2(userName,functionModule,iyear,method).then(Mono.just(R.ok()));
        Mono<R> noEmpty=taskRepository.deleteByFunctionModuleAndCaozuoUnique3(userName,functionModule,iyear,method,recordNum).then(Mono.just(R.ok()));
        return StrUtil.isNotBlank(recordNum)?noEmpty:empty;
    }
    @PostMapping("stockTaskDelById")
    public Mono<R> stockTaskDelById(String id){
        return taskRepository.deleteById(id).thenReturn(R.ok());
    }

    /************************************** End ********************************************/

    /************************************** 库存期初余额【插入现存量表】 ********************************************/
    @PostMapping("updateStockCurrentRevision")
    public Mono<R> updateStockCurrentRevision(@RequestBody Map map){
        String id=map.get("id").toString();
        Long revision=Long.valueOf(map.get("revision").toString());
        String lockUserId=map.get("lockUserId").toString();
        String lockUserDate=map.get("lockUserDate").toString();
        String lockReason=map.get("lockReason")==null?"":map.get("lockReason").toString();
        return stockCurrentstockRepository.updateStockCurrentRevision(id,revision,lockUserId,lockUserDate,lockReason).then(Mono.just(R.ok()));
    }
    @PostMapping("auditStockBalanceFindByLock")
    public Mono<R> auditStockBalanceFindByLock(@RequestBody Map map){
        String iyear=map.get("iyear").toString();
        List<StockBeginBalanceVo> list= JSON.parseArray(map.get("list").toString(), StockBeginBalanceVo.class);
        return stockCurrentstockRepository.findAlls().collectList()
        .flatMap(stockCurrentList->{
            List<Map>mapList=new ArrayList<>();
            if(stockCurrentList.size()>0){
                for (int i = 0; i < list.size(); i++) {
                    int finalI = i;

                    String text="存货名称【"+list.get(i).getStockName();
                    Map map1=new HashMap();
                    // 1 【仓库、存货】
                    List<StockCurrentstockVo> collect = stockCurrentList.stream().filter(sc -> sc.getIyear().equals(iyear)&&sc.getCwhcode().equals(list.get(finalI).getStockCangkuId()) && sc.getInvCode().equals(list.get(finalI).getStockNum())).collect(Collectors.toList());
                    // 2 【批号】
                    if(StrUtil.isNotBlank(list.get(i).getBatchNumber())){
                        text+=",批号【"+list.get(i).getBatchNumber()+"】";
                        collect=collect.stream().filter(a->a.getBatchId().equals(list.get(finalI).getBatchNumber())).collect(Collectors.toList());
                        // 2.1 生产日期、失效日期
                        if(StrUtil.isNotBlank(list.get(i).getDvdate())){
                            text+=",生产日期【"+list.get(i).getDpdate()+"】失效日期【"+list.get(i).getDvdate()+"】";
                            collect=collect.stream().filter(a->a.getDvdate().equals(list.get(finalI).getDvdate()) && a.getDpdate().equals(list.get(finalI).getDpdate())).collect(Collectors.toList());
                        }
                    }

                    if(collect.size()>0){
                        // 已锁住
                        if(collect.get(0).getRevision()==1){
                            map1.put("revision","1");
                            map1.put("stockCurrentsId","");
                            map1.put("lockCreatedUserName",collect.get(0).getLockCreatedUserName());
                            map1.put("text",text);
                            mapList.add(map1);
                            break;
                        }else{
                            map1.put("revision","0");
                            map1.put("stockCurrentsId",collect.get(0).getId());
                            map1.put("lockCreatedUserName","");
                            map1.put("text","");
                            mapList.add(map1);
                        }
                    }else{
                        map1.put("revision","");
                        map1.put("stockCurrentsId","");
                        map1.put("lockCreatedUserName","");
                        map1.put("text","");
                        mapList.add(map1);
                    }
                }
            }
            return Mono.just(mapList);
        }).map(a->R.ok().setResult(a));
    }
    @PostMapping("auditStockBalanceToStockCurrentstock_Increase")
    public Mono<R> auditStockBalanceToStockCurrentstock_Increase(@RequestBody List<StockBeginBalanceVo> list){
        // 所有现存量表数据
        return stockCurrentstockRepository.findAll().collectList()
        .flatMap(stockCurrList->{
            Map map=new HashMap<>();
            map.put("stockCurrList",stockCurrList);
            return Mono.just(map);
        })
        .flatMap(mapArr->{
            List<StockCurrentstock> stockCurrList= (List<StockCurrentstock>) mapArr.get("stockCurrList");

            List<StockCurrentstock> newlist=new ArrayList<>();
            list.forEach(temp->{
                StockCurrentstock c=new StockCurrentstock();
                List<StockCurrentstock> collect;
                // 查找现存量表是否存在【年度、存货ID、仓库ID】
                // 批号
                if(StrUtil.isNotBlank(temp.getBatchNumber())){
                    collect = stockCurrList.stream().filter(a ->StrUtil.isNotBlank(a.getBatchId())&&a.getBatchId().equals(temp.getBatchNumber()) && a.getIyear().equals(temp.getIyear()) && a.getInvCode().equals(temp.getStockNum()) && a.getCwhcode().equals(temp.getStockCangkuId())).collect(Collectors.toList());
                    // 批号下 生产日期与失效日期 是否 有相同数据
                    if(collect.size()>0){
                        collect=collect.stream().filter(a->StrUtil.isNotBlank(a.getDvdate())&&a.getDvdate().equals(temp.getDvdate())&&a.getDpdate().equals(temp.getDpdate())).collect(Collectors.toList());
                    }
                }else{
                    collect = stockCurrList.stream().filter(a -> a.getIyear().equals(temp.getIyear()) && a.getInvCode().equals(temp.getStockNum()) && a.getCwhcode().equals(temp.getStockCangkuId())).collect(Collectors.toList());
                }

                // 有就修改，没有就增加
                if(collect.size()==0){
                    c.setIyear(temp.getIyear());
                    c.setInvCode(temp.getStockNum());
                    c.setCwhcode(temp.getStockCangkuId());
                    c.setCwhcode1(temp.getCwhcode1());
                    c.setCwhcode2(temp.getCwhcode2());
                    c.setCwhcode3(temp.getCwhcode3());
                    c.setCwhcode4(temp.getCwhcode4());
                    c.setCwhcode5(temp.getCwhcode5());
                    c.setCwhcode6(temp.getCwhcode6());
                    c.setCunitidType(temp.getCunitidType());
                    c.setCunitidF1(StrUtil.isNotBlank(temp.getCunitidF1())?temp.getCunitidF1():"0");
                    c.setCunitidF2(StrUtil.isNotBlank(temp.getCunitidF2())?temp.getCunitidF2():"0");
                    c.setBaseQuantity(temp.getBaseQuantity());
                    c.setMoney(temp.getIcost());
                    c.setBatchId(temp.getBatchNumber());
                    c.setDpdate(temp.getDpdate());
                    c.setDvdate(temp.getDvdate());
                    newlist.add(c);
                }
                else{
                    BigDecimal baseQuantity=collect.get(0).getBaseQuantity().add(temp.getBaseQuantity());
                    BigDecimal money=collect.get(0).getMoney().add(temp.getIcost());
                    collect.get(0).setBaseQuantity(baseQuantity);
                    collect.get(0).setCunitidF1(StrUtil.isNotBlank(temp.getCunitidF1())?temp.getCunitidF1():"0");
                    collect.get(0).setCunitidF2(StrUtil.isNotBlank(temp.getCunitidF2())?temp.getCunitidF2():"0");
                    collect.get(0).setMoney(money);
                    newlist.add(collect.get(0));
                }
            });
            return Mono.just(newlist);
        })
        .flatMapMany(newlist->stockCurrentstockRepository.saveAll(newlist)).collectList()
        .map(a->R.ok().setResult(a));
    }
    @PostMapping("auditStockBalanceToStockCurrentstock_Decre")
    public Mono<R> auditStockBalanceToStockCurrentstock_Decre(@RequestBody List<StockBeginBalanceVo> list){
        // 所有现存量表数据
        return stockCurrentstockRepository.findAll().collectList()
                .flatMap(stockCurrList->{
                    Map map=new HashMap<>();
                    map.put("stockCurrList",stockCurrList);
                    return Mono.just(map);
                })
                .flatMap(mapArr->{
                    List<StockCurrentstock> stockCurrList= (List<StockCurrentstock>) mapArr.get("stockCurrList");

                    List<StockCurrentstock> newlist=new ArrayList<>();
                    list.forEach(temp->{
                        List<StockCurrentstock> collect;
                        // 查找现存量表是否存在【年度、存货ID、仓库ID】
                        if(StrUtil.isNotBlank(temp.getBatchNumber())){
                            collect = stockCurrList.stream().filter(a ->StrUtil.isNotBlank(a.getBatchId())&&a.getBatchId().equals(temp.getBatchNumber()) && a.getIyear().equals(temp.getIyear()) && a.getInvCode().equals(temp.getStockNum()) && a.getCwhcode().equals(temp.getStockCangkuId())).collect(Collectors.toList());
                            // 批号下 生产日期与失效日期 是否 有相同数据
                            if(collect.size()>0){
                                collect=collect.stream().filter(a->StrUtil.isNotBlank(a.getDvdate())&&a.getDvdate().equals(temp.getDvdate())&&a.getDpdate().equals(temp.getDpdate())).collect(Collectors.toList());
                            }
                        }else{
                            collect = stockCurrList.stream().filter(a -> a.getIyear().equals(temp.getIyear()) && a.getInvCode().equals(temp.getStockNum()) && a.getCwhcode().equals(temp.getStockCangkuId())).collect(Collectors.toList());
                        }

                        if(collect.size()>0){
                            BigDecimal baseQuantity=collect.get(0).getBaseQuantity().subtract(temp.getBaseQuantity());
                            BigDecimal money=collect.get(0).getMoney().subtract(temp.getIcost());
                            collect.get(0).setBaseQuantity(baseQuantity);
                            collect.get(0).setCunitidF1("0");
                            collect.get(0).setCunitidF2("0");
                            collect.get(0).setMoney(money);
                            newlist.add(collect.get(0));
                        }
                    });
                    return Mono.just(newlist);
                })
                .flatMapMany(newlist->stockCurrentstockRepository.saveAll(newlist)).collectList()
                .map(a->R.ok().setResult(a));
    }

    @PostMapping("auditStockBalanceToStockCurrentstock_Count")
    public Mono<R> auditStockBalanceToStockCurrentstock_Count(@RequestBody List<StockBeginBalanceVo> list){
        // 所有现存量表数据
        return stockCurrentstockRepository.findAll().collectList()
                .flatMap(stockCurrList->{
                    Map map=new HashMap<>();
                    map.put("stockCurrList",stockCurrList);
                    return Mono.just(map);
                })
                .flatMap(mapArr->{
                    List<StockBalanceLackVo> newlist=new ArrayList<>();
                    List<StockCurrentstock> stockCurrList= (List<StockCurrentstock>) mapArr.get("stockCurrList");

                    for (int i = 0; i < list.size(); i++) {
                        StockBalanceLackVo vo=new StockBalanceLackVo();

                        int finalI = i;
                        List<StockCurrentstock> collect;
                        // 查找现存量表是否存在【年度、存货ID、仓库ID】
                        if(StrUtil.isNotBlank(list.get(i).getBatchNumber())){
                            collect = stockCurrList.stream().filter(a ->StrUtil.isNotBlank(a.getBatchId())&&a.getBatchId().equals(list.get(finalI).getBatchNumber()) && a.getIyear().equals(list.get(finalI).getIyear()) && a.getInvCode().equals(list.get(finalI).getStockNum()) && a.getCwhcode().equals(list.get(finalI).getStockCangkuId())).collect(Collectors.toList());
                        }else{
                            collect = stockCurrList.stream().filter(a -> a.getIyear().equals(list.get(finalI).getIyear()) && a.getInvCode().equals(list.get(finalI).getStockNum()) && a.getCwhcode().equals(list.get(finalI).getStockCangkuId())).collect(Collectors.toList());
                        }
                        // 现存量表 数量小于 弃审数量
                        if(collect.size()>0 && collect.get(0).getBaseQuantity().compareTo(list.get(i).getBaseQuantity())==-1){
                            vo.setStockNum(list.get(i).getStockNum()).setStockName(list.get(i).getStockName()).setStockGgxh(list.get(i).getStockGgxh()).setUnitName(list.get(i).getStockUnitName()).setBatchId(list.get(i).getBatchNumber()).setBaseQuantity(list.get(i).getBaseQuantity()).setLackBaseQuantity(collect.get(0).getBaseQuantity());
                            newlist.add(vo);
                        }
                    }
                    return Mono.just(newlist);
                })
                .map(a->R.ok().setResult(a)).defaultIfEmpty(R.ok().setResult(""));
    }
    /************************************** End ********************************************/
}
