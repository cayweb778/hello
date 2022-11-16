package org.boozsoft.rest.stock;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.ReportEncodingRules;
import org.boozsoft.domain.entity.stock.StockBorrow;
import org.boozsoft.domain.entity.stock.StockBorrows;
import org.boozsoft.domain.vo.stock.StockBorrowListVo;
import org.boozsoft.repo.ReportEncodingRulesRepository;
import org.boozsoft.repo.stock.StockBorrowRepository;
import org.boozsoft.repo.stock.StockBorrowsRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description: 借入借用单
 * @author: miao
 * @date: 2022/10/14 14:12
 * @param:
 * @return: null
 **/
@Slf4j
@RestController
@RequestMapping("/stock_borrow")
public class StockBorrowController {

    @Autowired
    StockBorrowRepository borrowRepository;
    @Autowired
    StockBorrowsRepository borrowsRepository;
    @Autowired
    private ReportEncodingRulesRepository encodingRulesRepository;
    @Autowired
    DatabaseClient client;


    @PostMapping("StockBorrowSave")
    @Transactional
    public Mono<R> save(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        Map<String, Object> obj = assemblyCorrespondingRulesData(map);
        StockBorrow master = (StockBorrow) obj.get("master");
        List<StockBorrows> sub = (List<StockBorrows>) obj.get("sub");
        boolean b = master.getId() == null;
        return borrowRepository.save(master).flatMap(db -> {
            Mono<StockBorrow> s = borrowsRepository.saveAll(sub).collectList().thenReturn(db); //添加
            Mono<StockBorrow> d = borrowsRepository.findAllByBorrowStyleAndCcodeOrderByCcodeAscLineIdAsc(db.getBorrowStyle(), db.getCcode()).collectList().flatMap(list -> borrowsRepository.deleteAll(list).thenReturn("").flatMap(i -> s)); //修改
            return b ? s : d;
        }).map(o -> R.ok());
    }

    @PostMapping("getStockBorrowNewCode")
    public Mono<R> getStockBorrowNewCode(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        String date = map.get("date").toString();
        String key = map.get("key").toString();
        String borrowStyle = map.get("borrowStyle").toString();
        return borrowRepository.findAllOrderByDdateDesc(borrowStyle).collectList().flatMap(list->{
            return encodingRulesRepository.findByFileType(key).flatMap(tips->{
                ReportEncodingRules obj = tips;
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
                    List<StockBorrow> collect = list.stream().filter(a -> a.getCcode().contains(pre.toString())).collect(Collectors.toList());
                    if(collect.size()>0){
                        collect.sort(Comparator.comparing(StockBorrow::getCcode).reversed());
                        int t = Integer.parseInt(collect.get(0).getCcode().replace(pre.toString(),""))+1;
                        return Mono.just(pre.toString()+String.format("%0"+l+"d",t));
                    }
                    return Mono.just(pre.toString()+"0001");
                }else{
                    return Mono.just(pre.toString()+"0001");
                }
            });
        }).map(R::ok).defaultIfEmpty(R.ok().setResult(""));
    }

    @PostMapping("findByStockBorrow")
    public Mono<R> findByStockBorrow(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        // 单据编码
        String ccode = map.get("ccode")==null?"":map.get("ccode").toString();
        String action = map.get("action").toString();
        String type = map.get("type").toString();
        String currPdId = map.containsKey("curr") ? map.get("curr").toString() : "";
        return borrowRepository.findAllOrderByDdateDesc(type)
                .collectList().cache()
                .flatMap(list -> {
                    if (list.size() == 0) {
                        return Mono.just(R.ok());
                    } else {
                        StockBorrow master = null;
                        switch (action) {
                            case "curr":
                                long count = list.stream().filter(a -> a.getCcode().equals(currPdId)).count();
                                if(count>0){
                                    master = list.get((list.stream().map(e -> e.getCcode()).collect(Collectors.toList())).indexOf(currPdId));
                                    break;
                                }
                            case "tail":
                                List<StockBorrow> collect = list.stream().filter(a -> a.getCcode().equals(currPdId)).collect(Collectors.toList());

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
                        StockBorrow finalMaster = master;
                        return borrowsRepository.findAllByBorrowStyleAndCcodeOrderByCcodeAscLineIdAsc(master.getBorrowStyle(), master.getCcode()).collectList().map(enlist -> {
                            if (enlist.size() > 0) {
                                finalMaster.setEntryList(JSON.toJSONString(enlist));
                            }
                            return R.ok(finalMaster);
                        });
                    }
                });
    }

    @PostMapping("saveStockBorrowPojo")
    @Transactional
    public Mono<R> saveStockBorrowPojo(@RequestBody StockBorrow pojo) {
        return borrowRepository.save(pojo).map(a->R.ok().setResult(a));
    }
    @PostMapping("saveStockBorrowMxPojoList")
    @Transactional
    public Mono<R> saveStockBorrowMxPojoList(@RequestBody List<StockBorrows> pojo) {
        return borrowsRepository.saveAll(pojo).collectList().map(a->R.ok().setResult(a));
    }

    @PostMapping("delStockBorrowByccode")
    @Transactional
    public Mono<R> delStockBorrowByccode(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        return borrowRepository.findById(map.get("id").toString()).flatMap(db ->
                borrowsRepository.findAllByBorrowStyleAndCcodeOrderByCcodeAscLineIdAsc(db.getBorrowStyle(), db.getCcode()).collectList()
                        .flatMap(list -> borrowsRepository.deleteAll(list).thenReturn("").flatMap(i -> borrowRepository.delete(db).thenReturn(""))) //修改
        ).map(o -> R.ok());
    }
    @PostMapping("delStockBorrowByCcodeZip")
    @Transactional
    public Mono<R> delStockBorrowByCcodeZip(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        Mono<Void> a=borrowRepository.deleteByCcode(map.get("ccode").toString()).then();
        Mono<Void> b=borrowsRepository.deleteByCcode(map.get("ccode").toString()).then();
        return Mono.zip(a,b).then(Mono.just(R.ok()));
    }
    @PostMapping("updateStockBorrowBcheckByCcodeList")
    @Transactional
    public Mono<R> updateStockBorrowBcheckByCcodeList(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        String bcheck=map.get("bcheck").toString();
        String bcheckTime=map.get("bcheckTime").toString();
        String bcheckUser=map.get("bcheckUser").toString();
        List<String> ccode= (List<String>) map.get("ccode");
        Mono<Void> a=borrowRepository.updeteStockBorrowBcheckByCcodelist(bcheck,bcheckTime,bcheckUser, ccode).then();
        Mono<Void> b=borrowsRepository.updeteStockBorrowBcheckByCcodelist(bcheck,bcheckTime,bcheckUser, ccode).then();
        return Mono.zip(a,b).then(Mono.just(R.ok()));
    }
    @PostMapping("getUnitRate")
    public Mono<R> getUnitRate(@RequestBody Map map){
        String cgUnitId=map.get("cgUnitId").toString();
        String ccode=map.get("ccode").toString();
        return borrowsRepository.getUnitRate(cgUnitId,ccode).map(a->R.ok().setResult(a)).defaultIfEmpty(R.ok().setResult(""));
    }

    /**
     * @description: 可以参照的单据
     * @author: miao
     * @date: 2022/10/25 15:37
     * @param: [map]
     * @return: Mono<R>
     **/
    @PostMapping("getReferData")
    public Mono<R> getReferData(@RequestBody Map map){
        String iyear=map.get("iyear").toString();
        String strDate=map.get("strDate").toString();
        String endDate=map.get("endDate").toString();
        String borrowStyle=map.get("borrowStyle").toString();
        return borrowRepository.getReferData(iyear,strDate,endDate,borrowStyle).collectList().map(a->R.ok().setResult(a));
    }

    @PostMapping("findByStockBorrowCcode")
    public Mono<R> findByStockBorrowCcode(@RequestBody Map map){
        String ccode=map.get("ccode").toString();
        return borrowRepository.findByCcode(ccode).map(R::ok);
    }



    private Map<String, Object> assemblyCorrespondingRulesData(Map map) {
        HashMap<String, Object> resut = new HashMap<>();
        StockBorrow warehousing = new StockBorrow();
        warehousing.setId(map.containsKey("id") ? map.get("id").toString() : null)
                .setCcode(map.containsKey("ccode") ? map.get("ccode").toString() : null) //单号
                .setDdate(map.containsKey("ddate") ? map.get("ddate").toString().substring(0, 10) : null) // 日期
                .setIyear(map.containsKey("ddate") ? map.get("ddate").toString().substring(0, 4) : null)
                .setCmaker(map.containsKey("cmaker") ? map.get("cmaker").toString() : null) // 制单人
                .setUnitType(map.containsKey("unitType") ? map.get("unitType")==null?null:map.get("unitType").toString() : null) // 单位类型:1其他,2客户,3供应商,4员工,5项目
                .setUnitValue(map.containsKey("unitValue") ? map.get("unitValue")==null?null:map.get("unitValue").toString() : null) // 业务单位
                .setFeiyongJe(map.containsKey("feiyongJe") ? map.get("feiyongJe")==null?null:map.get("feiyongJe").toString() : null)
                .setCdepcode(map.containsKey("cdepcode") ? map.get("cdepcode")==null?null:map.get("cdepcode").toString() : null) // 部门
                .setCpersoncode(map.containsKey("cpersoncode") ? map.get("cpersoncode")==null?null:map.get("cpersoncode").toString() : null) // 业务员
                .setCmemo(map.containsKey("cmemo") ? map.get("cmemo")==null?null: map.get("cmemo").toString(): null)
                .setBorrowStyle(map.containsKey("borrowStyle") ? map.get("borrowStyle").toString() : null)
                .setCmakerTime(map.get("cmakerTime").toString());
        String entryList = map.containsKey("entryList") ? map.get("entryList").toString() : ""; // 表体
        List<StockBorrows> entrys = JSON.parseArray(entryList, StockBorrows.class);
        for (StockBorrows entry : entrys) {
            entry.setId(null).setCmaker(warehousing.getCmaker());
        }
        resut.put("master", warehousing);
        resut.put("sub", entrys);
        return resut;
    }



    //****************************************** LIST ************************************************************
    @PostMapping("findByStockBorrowTableList")
    public Mono<R> findByStockBorrowTableList(@RequestBody Map map) {
        String strNum=map.get("strNum").toString();
        String endNum=map.get("endNum").toString();
        String dept=map.get("dept").toString();
        String psn=map.get("psn").toString();
        String bcheck=map.get("bcheck").toString();
        String cinvode=map.get("cinvode").toString();
        String strDate=map.get("strDate").toString();
        String endDate=map.get("endDate").toString();
        Map<String, String> pageSearch = ((HashMap<String,  String>) map.get("pageSearch"));

        StringBuffer sb=new StringBuffer();
        if(StrUtil.isNotBlank(strNum) && StrUtil.isNotBlank(endNum)){
            sb.append("and sbs.right(ccode,4) between '"+strNum+"' and '"+endNum+"' ");
        }
        if(StrUtil.isNotBlank(dept)){
            sb.append("and sb.cdepcode='"+dept+"' ");
        }
        if(StrUtil.isNotBlank(psn)){
            sb.append("and sb.cpersoncode='"+psn+"' ");
        }
        if(StrUtil.isNotBlank(bcheck)){
            sb.append("and sbs.bcheck='"+bcheck+"' ");
        }
        if(StrUtil.isNotBlank(cinvode)){
            sb.append("and sbs.cinvode='"+cinvode+"' ");
        }
        if(StrUtil.isNotBlank(strDate)){
            sb.append("and sb.ddate between '"+strDate+"' and '"+endDate+"' ");
        }

        String sql="select case when sbs.bcheck = '1' then '已审核' else '未审核' end as bcheck,\n" +
                "       sb.ddate,\n" +
                "       sb.ccode,\n" +
                "       (case\n" +
                "            when sb.unit_type = 'etc' then '其他'\n" +
                "            when sb.unit_type = 'cust' then '客户'\n" +
                "            when sb.unit_type = 'supplier' then '供应商'\n" +
                "            when sb.unit_type = 'user' then '员工'\n" +
                "            when sb.unit_type = 'project' then '项目'\n" +
                "            when sb.unit_type = 'dept' then '部门'\n" +
                "            else '' end\n" +
                "           )                                                as unit_type,\n" +
                "       (case\n" +
                "            when sb.unit_type = 'cust' then (select cust.cust_abbname from customer cust where cust.id = sb.unit_value)\n" +
                "            when sb.unit_type = 'supplier' then (select sup.cust_abbname from supplier sup where sup.id = sb.unit_value)\n" +
                "            when sb.unit_type = 'user' then (select psn.psn_name from sys_psn psn where psn.id = sb.unit_value)\n" +
                "            when sb.unit_type = 'project' then (select pro.project_name from project pro where pro.id = sb.unit_value)\n" +
                "            when sb.unit_type = 'dept' then (select dept.dept_name\n" +
                "                                             from sys_department dept\n" +
                "                                             where dept.unique_code = sb.unit_value)\n" +
                "            else '' end\n" +
                "           )                                                as unit_value,\n" +
                "       dept.dept_name,\n" +
                "       psn.psn_name,\n" +
                "       sk.stock_num,\n" +
                "       sk.stock_name,\n" +
                "       sk.stock_barcode,\n" +
                "       sk.stock_ggxh,\n" +
                "       (case\n" +
                "            when sbs.cg_unit_id = sk.stock_unit_id then sk.stock_unit_name\n" +
                "            else\n" +
                "                ((case\n" +
                "                      when sbs.cg_unit_id = sk.stock_unit_id1 then sk.stock_unit_name1\n" +
                "                      else\n" +
                "                          (case\n" +
                "                               when sbs.cg_unit_id = sk.stock_unit_id2 then sk.stock_unit_name2\n" +
                "                               else '' end) end)) end)         unit_name,\n" +
                "       sbs.cnumber,\n" +
                "       sk.stock_unit_name,\n" +
                "       sbs.base_quantity,\n" +
                "       sbs.batch_id,\n" +
                "       sbs.yjdate,\n" +
                "       sbs.ljhh_sum,\n" +
                "        cast(sbs.base_quantity as float)-cast(coalesce(sbs.ljhh_sum,'10') as float) as no_ljhh_num,\n" +
                "        sbs.ljzh_sum,\n" +
                "        psn2.real_name as cmaker,\n" +
                "        psn3.real_name as bcheck_user_name\n" +
                "from stock_borrows sbs\n" +
                "         left join stock_borrow sb on sb.ccode = sbs.ccode\n" +
                "         left join sys_department dept on dept.unique_code = sb.cdepcode\n" +
                "         left join sys_psn psn on psn.id = sb.cpersoncode\n" +
                "         left join _app_group_sys_user psn2 on psn2.id = sbs.cmaker\n" +
                "         left join _app_group_sys_user psn3 on psn3.id = sbs.bcheck_user\n" +
                "         left join stock sk on sk.stock_num = sbs.cinvode "+sb+" " ;

        return client.sql(sql).fetch().all().collectList()
        .flatMap(list->{
            List<StockBorrowListVo> newlist=JSON.parseArray(JSON.toJSONString(list),StockBorrowListVo.class);
            if(StrUtil.isNotBlank(pageSearch.get("selectValue"))){
                if(pageSearch.get("selectType").equals("ccode")){
                    newlist=newlist.stream().filter(a->a.getCcode().contains(pageSearch.get("selectValue"))).collect(Collectors.toList());
                }else if(pageSearch.get("selectType").equals("stockNum")){
                    newlist=newlist.stream().filter(a->a.getStockNum().contains(pageSearch.get("selectValue"))).collect(Collectors.toList());
                }else if(pageSearch.get("selectType").equals("stockName")){
                    newlist=newlist.stream().filter(a->a.getStockName().contains(pageSearch.get("selectValue"))).collect(Collectors.toList());
                }
            }
            return Mono.just(newlist);
        }).map(R::ok);
    }

    @PostMapping("findByStockBorrowTableLineCode")
    public Mono<R> findByStockBorrowTableLineCode(@RequestBody Map map) {
        String linCode=map.get("linCode").toString();
        return borrowsRepository.findByLineCode(linCode).map(R::ok);
    }

    @PostMapping("findByStockBorrowTableCcode")
    public Mono<R> findByStockBorrowTableCcode(@RequestBody Map map){
        String ccode=map.get("ccode").toString();
        return borrowsRepository.findAllByCcode(ccode).collectList().map(a->R.ok().setResult(a));
    }

    @PostMapping("verifyDataState")
    public Mono<R> verifyDataState(@RequestBody Map map){
        // 操作类型【rowEdit（列表点击行跳转）,edit：修改,del：删除,audit：审核】
        String operation=map.get("operation").toString();
        // 单号>>>审核状态
        List<String> list= (List<String>) map.get("list");
        return borrowRepository.findAll().collectList()
        .flatMap(datalist->{
            String txt="1";
            for (int i = 0; i < list.size(); i++) {
                String ccode = list.get(i).split(">>>")[0];
                String bcheck = list.get(i).split(">>>")[1];
                long count = datalist.stream().filter(t -> operation.equals("rowEdit")?t.getCcode().equals(ccode):t.getCcode().equals(ccode)&&t.getBcheck().equals(bcheck)).count();
                if (count==0){txt="";break;}
            }
            return Mono.just(txt);
        }).map(R::ok);
    }
}
