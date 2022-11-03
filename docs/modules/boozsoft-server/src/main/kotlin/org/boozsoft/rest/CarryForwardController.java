package org.boozsoft.rest;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiOperation;
import org.springbooz.core.tool.utils.StringUtils;
import org.boozsoft.domain.entity.carryovers.AccCarryOver;
import org.boozsoft.domain.entity.carryovers.AccCarryOverEntry;
import org.boozsoft.domain.entity.carryovers.AccCarryOverEntryFormula;
import org.boozsoft.domain.entity.codekemu.CodeKemu;
import org.boozsoft.domain.vo.CarryOverVo;
import org.boozsoft.repo.carryover.AccCarryOverEntryFormulaRepository;
import org.boozsoft.repo.carryover.AccCarryOverEntryRepository;
import org.boozsoft.repo.carryover.AccCarryOverRepository;
import org.boozsoft.repo.codekemu.CodeKemuRepository;
import org.boozsoft.service.SysLogService;
import org.springbooz.core.tool.result.R;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/carryForward")
public class CarryForwardController {
    @Autowired
    CodeKemuRepository codeKemuRepository;
    @Autowired
    SysLogService sysLogService;

    @GetMapping("period")
    @ApiOperation(value = "查询指定年指定类别科目", notes = "条件：科目分类、年")

    public Mono<R> findAllPeriod(Pageable pageable, String year) {
        //   Mono<List<CodeKemu>> list = codeKemuRepository.findAllByCclassAndFlagAndIyearAndBendOrderByCcodeAscBendAsc("损益", "1", year, "1").collectList();
      /*  Mono<List<CodeKemu>> codeList = codeKemuRepository.findAllByCclassAndFlagAndIyearAndBendOrderByCcodeAscBendAsc("权益", "1", year, "1").collectList();
        return Mono.zip(list,codeList,(a,b)->{
            return  a;
        }).map(R::ok);*/
        return codeKemuRepository.findAllByCclassAndFlagAndIyearAndBendOrderByCcodeAscBendAsc("损益", "1", "2021", "1").collectList().map(R::page);
    }


    @GetMapping("period/incomeList")
    @ApiOperation(value = "查询指定年指定类别科目", notes = "条件：科目分类、年")

    public Mono<R> incomeList() {
        return codeKemuRepository.findAllByCclassAndFlagAndIyearAndBendOrderByCcodeAscBendAsc("权益", "1", "2021", "1").collectList().map(R::ok);
    }


    /************************************************* start 对应结转 or 自定义结转 ********************************************/
    @Autowired
    private AccCarryOverRepository carryOverRepository;
    @Autowired
    private AccCarryOverEntryRepository carryOverEntryRepository;
    @Autowired
    private AccCarryOverEntryFormulaRepository carryOverEntryFormulaRepository;

    /**
     * 查询列表
     *
     * @return
     */
    @PostMapping("carryover/list")
    @ApiOperation(value = "查询指定年指定类别科目", notes = "条件：科目分类、年")

    public Mono<R> templateList(@RequestBody CarryOverVo vo) {
        String mark = vo.getCarryOverModule();
        String zdr = "";
        String msg = "sss";
        // admin管理一键生单模板
        return Mono.just(msg)
                .flatMapMany(item -> "ysd".equals("sss") ?
                        carryOverRepository.findAllByCarryOverModuleAndKmtypeOrderByOrderNumAsc(mark, msg) :
                        carryOverRepository.findAllByCarryOverModuleOrderByOrderNumAsc(mark)
                )
                .map(accCarryOver -> {
                    CarryOverVo carryOverVo = new CarryOverVo();
                    BeanUtils.copyProperties(accCarryOver, carryOverVo);
                    return carryOverVo;
                })
                .flatMap(carryOverVo -> carryOverVo.getCarryOverType().equals("1") ?
                        //  公式结转
                        a(carryOverVo) :
                        // 对应结转  转出送对面 转入判断大小觉得方向
                        b(carryOverVo)
                )
                .map(carryOverVo2 -> {
                    carryOverVo2.setCarryOverSum("?");
                    carryOverVo2.setCarryOverModel("");
                    return carryOverVo2;
                })
                .collectList()
                .map(R::ok);
    }

    public Mono<CarryOverVo> a(CarryOverVo carryOverVo) {
        //公式结转
        return carryOverEntryRepository.findAllByOnlyNumOrderById(carryOverVo.getId())
                .flatMap(carryOverEntry -> Mono.just(carryOverEntry.getOwnership())
                        .flatMapMany(this::getCarryOverEntryFormulaFlux)
                        .collectList()
                        .flatMap(item -> item.size() == 0 ? Mono.just("") : Mono.just(JSON.toJSONString(item)))
                        .doOnNext(carryOverEntry::setEntryFormulas).map(s -> carryOverEntry)
                )
                .collectList()
                .map(JSON::toJSONString)
                .doOnNext(carryOverVo::setCarryOverEntries)
                .map(item -> carryOverVo);
    }


    public Mono<CarryOverVo> b(CarryOverVo carryOverVo) {
        //对应结转  转出送对面 转入判断大小觉得方向
        return carryOverEntryRepository.findAllByOnlyNumOrderById(carryOverVo.getId())
                .collectList()
                .flatMap(item->{
                    item.forEach(v->{
                        v.setMd(new BigDecimal(0)).setMc(new BigDecimal(0));
                    });
                    return Mono.just(item);
                })
                .map(JSON::toJSONString)
                .doOnNext(carryOverVo::setCarryOverEntries)
                .map(item -> carryOverVo);
    }


    public Flux<AccCarryOverEntryFormula> getCarryOverEntryFormulaFlux(String ownership) {
        return carryOverEntryFormulaRepository.findAllByOwnership(ownership);
    }

    /**
     * 查询并计算
     * @return
     */
    /*public String templateListCalculation(){
        try {
            String mark = vo.getCarryOverModule();
            AccountDatabaseVo db = (AccountDatabaseVo) session.get("UFDATA");
            String zdr = ((UserL) session.get("ConUser")).getUName();
            // admin管理一键生单模板
            String ysd_sql="";
            if(db==null){
                db= new AccountDatabaseVo();
                db.setAdNum("ysd");
                ysd_sql="and stid='"+msg+"' ";
            }
            jpaDynamicRoutingDataSourceConfig.chooseSchema(db.getAdNum());
            List<AccCarryOver> all;
            if("ysd".equals(db.getAdNum())){
                all = carryOverRepository.findAllByCarryOverModuleAndKmtypeOrderByOrderAsc(mark,msg);
            }else{
                all = carryOverRepository.findAllByCarryOverModuleOrderByOrderAsc(mark);
            }
            List<CarryOverVo> accCarryOvers = new ArrayList<>(all.size());
            String yearMonth = vo.getOrder();
            for (AccCarryOver accCarryOver : all) {
                CarryOverVo carryOverVo = new CarryOverVo();
                BeanUtils.copyProperties(accCarryOver,carryOverVo);
                jpaDynamicRoutingDataSourceConfig.chooseSchema(db.getAdNum());
                List<AccCarryOverEntry> carryOverEntries = carryOverEntryRepository.findAllByOnlyNumOrderById(accCarryOver.getOnlyNum());
                String thisLastDay = DateUtil.endOfMonth(DateUtil.parse(yearMonth.substring(0, 4) + "-" + yearMonth.substring(4, 6) + "-01 22:33:23")).toString("yyyy-MM-dd");
                if (carryOverVo.getCarryOverType().equals("1")){ //公式结转
                    for (AccCarryOverEntry carryOverEntry : carryOverEntries) {
                        List<AccCarryOverEntryFormula> dbOwnership = carryOverEntryFormulaRepository.findAllByOwnership(carryOverEntry.getOwnership());
                        if (dbOwnership.size() > 0) {
                            carryOverEntry.setEntryFormulas(JSON.toJSONString(dbOwnership));
                        }else {
                            carryOverEntry.setEntryFormulas("");
                        }
                    }
                }else { //对应结转  转出送对面 转入判断大小觉得方向
                    JSONObject props = new JSONObject();
                    props.put("pingZhengFrom",accCarryOver.getTemplateNum());
                    props.put("date", thisLastDay);
                    props.put("type","记");
                    props.put("danJuNum","0");
                    props.put("zdr",zdr);
                    if (carryOverEntries.size() > 0) {
                        JSONArray rows = new JSONArray();
                        String start = yearMonth.substring(0,4)+"01";
                        String end = yearMonth;
                        try{
                            if (accCarryOver.getTemplateNum().equals("QJSYJZ")){   // 期间损益特殊处理
                                // 获取当前账套 为损益的  损益的 借sum 与贷sum
                                List<Map<Object,Object>> listMap = baseDao.queryMap("SELECT cateid FROM "+db.getAdNum()+".codecategory where catename = '损益'");
                                String cateid = listMap.get(0).get("cateid").toString();
                                List<Map<Object, Object>> sylist  = baseDao.queryMap(" SELECT DISTINCT a.ccode,c.bproperty,sum(mc) as df,sum(md) as jf from "+db.getAdNum()+".accvoucher a left join "+db.getAdNum()+".code c on c.ccode =" +
                                        "                                    a.ccode where 1 = 1 and a.flag_invaild <> '1' and a.ccode like '" + cateid+ "%' and c.bend = '1' and a.iyperiod" +
                                        "                                    = " + yearMonth + " "+ysd_sql+" group by a.ccode,c.bproperty");
                                if (sylist.size() > 0){
                                    for (Map<Object, Object> objectObjectMap : sylist) {
                                        JSONObject o = new JSONObject();
                                        o.put("zhaiYao","期间损益结转");
                                        o.put("kuaiJiKeMuCode",objectObjectMap.get("ccode").toString());
                                        // o.put("daiMoney",new BigDecimal(Double.parseDouble(StringUtils.isBlank(objectObjectMap.get("jf").toString()) ? "0.00" : objectObjectMap.get("jf").toString())).setScale(2,BigDecimal.ROUND_HALF_UP));
                                        // o.put("jieMoney",new BigDecimal(Double.parseDouble(StringUtils.isBlank(objectObjectMap.get("df").toString()) ? "0.00" : objectObjectMap.get("df").toString())).setScale(2,BigDecimal.ROUND_HALF_UP));
                                        // 贷方：(期初贷方+（1～本期贷方）贷方)-（期初借方+（1～本期借方））
                                        List<Map<Object, Object>> yuelist = GET_SubjectYue(db.getAdNum(), start, end, "1", "6", objectObjectMap.get("ccode").toString(), "", "");
                                        String fz = objectObjectMap.get("bproperty").toString();
                                        double jyu = 0.00;
                                        double dyu = 0.00;
                                        double jqyu = Double.parseDouble(StringUtils.isBlank(yuelist.get(0).get("md").toString()) ? "0.00" : yuelist.get(0).get("md").toString());
                                        double dqyu = Double.parseDouble(StringUtils.isBlank(yuelist.get(0).get("mc").toString()) ? "0.00" : yuelist.get(0).get("mc").toString());
                                        double jbqlj = Double.parseDouble(StringUtils.isBlank(yuelist.get(0).get("b_year_md").toString()) ? "0.00" : yuelist.get(0).get("b_year_md").toString());
                                        double dbqlj = Double.parseDouble(StringUtils.isBlank(yuelist.get(0).get("b_year_mc").toString()) ? "0.00" : yuelist.get(0).get("b_year_mc").toString());
                                        if (fz.equals("1")){
                                            jyu = (jqyu+jbqlj) - (dqyu+dbqlj);
                                        }else{
                                            dyu = (dqyu+dbqlj) - (jqyu+jbqlj);
                                        }
                                        // 获取指定科目的期末余额
                                        // 赋值反方向
                                        o.put("daiMoney",new BigDecimal(jyu).setScale(2,BigDecimal.ROUND_HALF_UP));
                                        o.put("jieMoney",new BigDecimal(dyu).setScale(2,BigDecimal.ROUND_HALF_UP));
                                        if(!(Double.parseDouble(o.get("daiMoney").toString())==0) || !(Double.parseDouble(o.get("jieMoney").toString())==0))
                                            rows.add(o);
                                    }
                                    JSONObject oThere = new JSONObject();
                                    /// 获取最后一条 转出数据
                                    String accountSubjectNum = carryOverEntries.get(carryOverEntries.size() - 1).getAccountSubjectNum();
                                    oThere.put("zhaiYao","期间损益结转");
                                    oThere.put("kuaiJiKeMuCode",accountSubjectNum);
                                    List<Map<Object, Object>> list = baseDao.queryMap("select bproperty from " + db.getAdNum() + ".code where iyear = '" + yearMonth.substring(0,4) + "' and ccode = '" +accountSubjectNum + "' "+ysd_sql+" ");
                                    String dbFx = list.get(0).get("bproperty").toString();
                                    carryOverVo.setCarryOverTransferInNum(accountSubjectNum+"--"+dbFx);
                                    BigDecimal JSum = new BigDecimal("0");
                                    BigDecimal DSum = new BigDecimal("0");
                                    for (Object row : rows) {
                                        JSONObject obj = (JSONObject) row;
                                        JSum = JSum.add(new BigDecimal(obj.get("jieMoney").toString()));
                                        DSum = DSum.add(new BigDecimal(obj.get("daiMoney").toString()));
                                    }
                                    if (dbFx.equals("1")){ // 为借
                                        BigDecimal val = DSum.subtract(JSum);
                                        BigDecimal value = new BigDecimal(val.doubleValue()).setScale(2, BigDecimal.ROUND_HALF_UP);
                                        if (val.doubleValue() > 0){
                                            oThere.put("jieMoney",value); // 差额
                                            oThere.put("daiMoney",new BigDecimal("0").setScale(2,BigDecimal.ROUND_HALF_UP));
                                        }else if (val.doubleValue() < 0){
                                            value = value.abs();
                                            oThere.put("daiMoney",value);
                                            oThere.put("jieMoney",new BigDecimal("0").setScale(2,BigDecimal.ROUND_HALF_UP));
                                        }else {
                                            oThere.put("daiMoney","0.00");
                                            oThere.put("jieMoney","0.00");
                                        }
                                        carryOverVo.setCarryOverSum(value.setScale(2,BigDecimal.ROUND_HALF_UP).toString()); // 贷 - 借
                                    }else { //为贷
                                        BigDecimal val =JSum.subtract(DSum); // 差额
                                        BigDecimal value = new BigDecimal(val.doubleValue()).setScale(2, BigDecimal.ROUND_HALF_UP);
                                        if (val.doubleValue() > 0){
                                            oThere.put("daiMoney", value);
                                            oThere.put("jieMoney",new BigDecimal("0").setScale(2,BigDecimal.ROUND_HALF_UP));
                                        }else if (val.doubleValue() < 0){
                                            value = value.abs();
                                            oThere.put("jieMoney", value);
                                            oThere.put("daiMoney",new BigDecimal("0").setScale(2,BigDecimal.ROUND_HALF_UP));
                                        }else {
                                            oThere.put("daiMoney","0.00");
                                            oThere.put("jieMoney","0.00");
                                        }
                                        carryOverVo.setCarryOverSum(value.setScale(2,BigDecimal.ROUND_HALF_UP).toString()); // 借 - 贷
                                    }
                                    rows.add(oThere);
                                }else {
                                    JSONObject oOne = new JSONObject();
                                    oOne.put("zhaiYao","期间损益收入");
                                    oOne.put("kuaiJiKeMuCode","收入");
                                    oOne.put("jieMoney",new BigDecimal("0").setScale(2,BigDecimal.ROUND_HALF_UP));
                                    oOne.put("daiMoney",new BigDecimal("0").setScale(2,BigDecimal.ROUND_HALF_UP));
                                    rows.add(oOne);
                                }
                            }else {
                                for (AccCarryOverEntry carryOverEntry : carryOverEntries) {
                                    String jieMoney = "0";
                                    String daiMoney = "0";
                                    if (carryOverEntry.getTransferMethod().equals("2")){ // 转出
                                        List<Map<Object, Object>> sylist = null;
                                        if (carryOverEntry.getDirection().equals("1")){ //借
                                            sylist = baseDao.queryMap("SELECT (SELECT ROUND(SUM(md),2) from " + db.getAdNum() + ".accvoucher where 1=1 and flag_invaild <> '1' and ccode = '" + carryOverEntry.getAccountSubjectNum() + "' and iyperiod ='" + yearMonth + "' ) jf");
                                            daiMoney =  null == sylist.get(0).get("jf") ? "0" : sylist.get(0).get("jf").toString();
                                        }else if (carryOverEntry.getDirection().equals("2")){//贷
                                            sylist =  baseDao.queryMap("SELECT (SELECT ROUND(SUM(mc),2) from " + db.getAdNum() + ".accvoucher where 1=1 and flag_invaild <> '1' and ccode = '" + carryOverEntry.getAccountSubjectNum() + "' and iyperiod ='" + yearMonth + "' ) df");
                                            jieMoney = null == sylist.get(0).get("df") ? "0" : sylist.get(0).get("df").toString();
                                        }else {// 余额
                                            // 获取当前转出会计科目的基础信息
                                            List<Map<Object, Object>> list = baseDao.queryMap("select bproperty from " + db.getAdNum() + ".code where iyear = '" + yearMonth.substring(0,4) + "' and ccode = '" + carryOverEntry.getAccountSubjectNum() + "' "+ysd_sql+" ");
                                            String dbFx = list.get(0).get("bproperty").toString();
                                            List<Map<Object, Object>> yuelist = GET_SubjectYue(db.getAdNum(), start, end, "1", "6", carryOverEntry.getAccountSubjectNum(), "", "");
                                            double v = Double.parseDouble(StringUtils.isBlank(yuelist.get(0).get("yue").toString()) ? "0.00" : yuelist.get(0).get("yue").toString());
                                            daiMoney = StringUtils.isBlank(yuelist.get(0).get("b_md_yue").toString()) ? "0.00" : yuelist.get(0).get("b_md_yue").toString();
                                            jieMoney = StringUtils.isBlank(yuelist.get(0).get("b_mc_yue").toString()) ? "0.00" : yuelist.get(0).get("b_mc_yue").toString();
                                            // 当余额大于 0 时 本方向 反之
                                       *//* if (dbFx.equals("1")){ // 余额 （借余 + 借发 ）-（贷余+贷发）
                                            daiMoney = v+"";
                                            jieMoney = "0.00";
                                        }else { // 贷
                                            daiMoney = "0.00";
                                            jieMoney = v+"";
                                        }*//*
                                        }
                                    }else { // 转入 （为贷  贷-借）
                                        // 获取当前转出会计科目的基础信息
                                        List<Map<Object, Object>> list = baseDao.queryMap("select bproperty from " + db.getAdNum() + ".code where iyear = '" + yearMonth.substring(0,4) + "' and ccode = '" + carryOverEntry.getAccountSubjectNum() + "' "+ysd_sql+" ");
                                        String dbFx = list.get(0).get("bproperty").toString();
                                        carryOverVo.setCarryOverTransferInNum(carryOverEntry.getAccountSubjectNum()+"--"+dbFx);
                                        if (rows.size() > 0){ // 将转出的进行
                                            BigDecimal JSum = new BigDecimal("0");
                                            BigDecimal DSum = new BigDecimal("0");
                                            for (Object row : rows) {
                                                JSONObject obj = (JSONObject) row;
                                                JSum = JSum.add(new BigDecimal(obj.get("jieMoney").toString()));
                                                DSum = DSum.add(new BigDecimal(obj.get("daiMoney").toString()));
                                            }
                                            // 余额 与发生
                                            if (dbFx.equals("1")){ // 为借
                                                BigDecimal val = null;
                                                val = DSum.subtract(JSum);
                                                if (val.doubleValue() > 0){
                                                    jieMoney = val.doubleValue()+"";
                                                }else if (val.doubleValue() < 0){
                                                    val = val.abs();
                                                    daiMoney = val.doubleValue()+"";
                                                }else {
                                                    daiMoney="0.00";
                                                    jieMoney = "0.00";
                                                }
                                                carryOverVo.setCarryOverSum(val.setScale(2,BigDecimal.ROUND_HALF_UP).toString()); // 贷 - 借
                                            }else { //为贷
                                                BigDecimal val = null;
                                                val = JSum.subtract(DSum);
                                                if (val.doubleValue() > 0){
                                                    daiMoney = val.doubleValue()+"";
                                                }else if (val.doubleValue() < 0){
                                                    val = val.abs();
                                                    jieMoney = val.doubleValue()+"";
                                                }else {
                                                    daiMoney="0.00";
                                                    jieMoney = "0.00";
                                                }
                                                carryOverVo.setCarryOverSum(val.setScale(2,BigDecimal.ROUND_HALF_UP).toString()); // 借 - 贷
                                            }
                                        }
                                    }
                                    JSONObject o = new JSONObject();
                                    o.put("zhaiYao",carryOverEntry.getSummary());
                                    o.put("kuaiJiKeMuCode",carryOverEntry.getAccountSubjectNum());
                                    o.put("jieMoney",new BigDecimal(jieMoney).setScale(2,BigDecimal.ROUND_HALF_UP));
                                    o.put("daiMoney",new BigDecimal(daiMoney).setScale(2,BigDecimal.ROUND_HALF_UP));
                                    rows.add(o);
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        JSONObject map = new JSONObject();
                        map.put("props",props);
                        map.put("rows",rows);
                        carryOverVo.setCarryOverModel(map.toJSONString());
                    }else { // 不存在
                        carryOverVo.setCarryOverModel("");
                    }
                }
                if (StringUtils.isBlank(carryOverVo.getCarryOverSum()))carryOverVo.setCarryOverSum("?");
                *//*根据编码查询指定年月日 凭证表是否存在 当余额为0 替换金额显示查看 *//*
                if (null !=  carryOverVo.getCarryOverSum() && !carryOverVo.getCarryOverSum().equals("?")){
                    String[] inArr = carryOverVo.getCarryOverTransferInNum().split("--");
                    String yearAndMonth = thisLastDay.substring(0, 7).replace("-", "");
                    List<Map<Object,Object>> voucherList =  baseDao.queryMap("SELECT (SELECT ROUND(SUM("+(inArr[1].equals("1")?"md":"mc")+"),2) from " + db.getAdNum() + ".accvoucher where 1=1 and flag_invaild <> '1' and from_num = '" + carryOverVo.getTemplateNum() + "' and iyperiod ='" + yearAndMonth + "' and ccode='"+inArr[0]+"') df,(SELECT coutno_id from " + db.getAdNum() + ".accvoucher where 1=1 and flag_invaild <> '1' and from_num = '" + carryOverVo.getTemplateNum() + "' and iyperiod ='" + yearAndMonth + "'  and ccode='"+inArr[0]+"' order by fill_in_date desc,id desc limit 1 ) no");
                    if(Double.parseDouble(carryOverVo.getCarryOverSum()) == 0 && voucherList.size() > 0 && null != voucherList.get(0).get("df")){
                        carryOverVo.setCarryOverIsShow("1");
                        carryOverVo.setCarryOverSum(new BigDecimal(voucherList.get(0).get("df").toString()).setScale(2,BigDecimal.ROUND_HALF_UP).toString());
                        carryOverVo.setCarryOverShowNum(voucherList.get(0).get("no").toString());
                        carryOverVo.setCarryOverTransferInNum("");
                    }
                }
                carryOverVo.setCarryOverEntries(JSON.toJSONString(carryOverEntries));
                accCarryOvers.add(carryOverVo);
            }
            results.setList(accCarryOvers);
        }catch (Exception e){
            e.printStackTrace();
        }
        return "results";
    }*/

    /**
     * 检查结转名称是否相同
     *
     * @return
     */
    @PostMapping("carryover/checkNumAndName")
    @ApiOperation(value = "检查结转模板是否重复", notes = "条件：指定年度")

    public Mono<R> checkNumAndName(@RequestBody CarryOverVo vo) {
        String msg = "";
        String kmtype = "2";
        String overModule = vo.getCarryOverModule();
        return Mono.just(msg)
                .flatMap(item -> "ysd".equals("sss") ?
                        carryOverRepository.findFirstByCarryOverModuleAndTemplateNumAndKmtypeOrCarryOverModuleAndTemplateNameAndKmtype(overModule, vo.getTemplateNum(), kmtype, overModule, vo.getTemplateName(), kmtype) :
                        carryOverRepository.findFirstByCarryOverModuleAndTemplateNumOrCarryOverModuleAndTemplateName(overModule, vo.getTemplateNum(), overModule, vo.getTemplateName())
                ).defaultIfEmpty(new AccCarryOver())
                .map(entity -> {
                    R r = new R().setCode(0L);
                    if (null == vo.getId()) { //add
                        if (null != entity && vo.getTemplateNum().equals(entity.getTemplateNum())) {
                            r.setMessage("模板保存失败: 模板编码已存在!");
                        } else if (null != entity && vo.getTemplateNum().equals(entity.getTemplateName())) {
                            r.setMessage("模板保存失败: 模板姓名已存在!");
                        }
                    } else {
                        if (null != entity && !vo.getId().equals(entity.getId()) && vo.getTemplateNum().equals(entity.getTemplateNum())) {
                            r.setMessage("模板保存失败: 模板编码已存在!");
                        } else if (null != entity && !vo.getId().equals(entity.getId()) && vo.getTemplateName().equals(entity.getTemplateName())) {
                            r.setMessage("模板保存失败: 模板姓名已存在!");
                        }
                    }
                    return r;
                });
    }


    @GetMapping("kemuList")
    @ApiOperation(value = "查询当前账套末级与全部", notes = "条件：指定年度")

    public Mono<R> kemuList(String year) {
        Mono<List<CodeKemu>> lastList = codeKemuRepository.findAllByFlagAndIyearAndBendOrderByCcodeAscBendAsc("1", year, "1").collectList();
        Mono<List<CodeKemu>> allList = codeKemuRepository.findAllByFlagAndIyearOrderByCcodeAscBendAsc("1", year).collectList();
        return Mono.zip(lastList, allList).map(R::ok);
    }

    /**
     * 添加一条结转
     *
     * @return
     */
    @PostMapping("carryover/save")
    @ApiOperation(value = "添加一条结转", notes = "Vo")

    public Mono<R> save(@RequestBody CarryOverVo vo) {
        String kmtype = "2";
        List<AccCarryOverEntry> overEntries = JSON.parseArray(vo.getCarryOverEntries(), AccCarryOverEntry.class);
        List<AccCarryOverEntryFormula> saveEntryFormulas = new ArrayList<>();
        return Mono.just(vo)
                .filter(item -> vo.getId() != null)
                .flatMap(item2 -> // 修改
                        Mono.just(vo.getId())
                                .flatMap(item -> carryOverRepository.getById(vo.getId()))
                                .map(dbCarryOver -> {
                                            dbCarryOver.setTemplateNum(vo.getTemplateNum());
                                            dbCarryOver.setTemplateName(vo.getTemplateName());
                                            dbCarryOver.setOrderNum(vo.getOrderNum());
                                            if ("ysd".equals("ssss")) dbCarryOver.setKmtype(kmtype);
                                            return dbCarryOver;
                                        }
                                )
                                .doOnNext(entity -> formulasToEntitys(overEntries, saveEntryFormulas, entity.getId()))
                                // 删除操作
                                .flatMap(entity -> {
                                    Flux<AccCarryOverEntry> two = carryOverEntryRepository.findAllByOnlyNumOrderById(entity.getId());
                                    return two
                                            // 删除3级
                                            .filter(item -> entity.getCarryOverType().equals("1"))
                                            .map(AccCarryOverEntry::getOwnership)
                                            .map(carryOverEntryFormulaRepository::findAllByOwnership)
                                            .flatMap(carryOverEntryFormulaRepository::deleteAll)
                                            // 删除2级
                                            .then(carryOverEntryRepository.deleteAll(two))
                                            .thenReturn(entity);
                                })
                                .flatMap(entityMonm -> carryOverRepository.save(entityMonm))
                                .flatMap(entityMonm -> {
                                    for (AccCarryOverEntry overEntry : overEntries) {
                                        overEntry.setId(null);
                                    }
                                    return carryOverEntryRepository.saveAll(overEntries).collectList().thenReturn(entityMonm);
                                })
                                .filter(entityMonm -> entityMonm.getCarryOverType().equals("1"))
                                .then(
                                        Mono.just(saveEntryFormulas).flatMap(list -> {
                                            for (AccCarryOverEntryFormula saveEntryFormula : list) {
                                                saveEntryFormula.setId(null);
                                            }
                                            return carryOverEntryFormulaRepository.saveAll(list).collectList();
                                        })
                                )
                                .flatMap(item -> Mono.just(R.ok()))
                )
                .switchIfEmpty(Mono.just("").flatMap(item2 -> { // 添加user2/findAll
                    AccCarryOver accCarryOver = new AccCarryOver();
                    accCarryOver.setTemplateNum(vo.getTemplateNum());
                    accCarryOver.setTemplateName(vo.getTemplateName());
                    accCarryOver.setCarryOverType(vo.getCarryOverType());
                    accCarryOver.setCarryOverModule(vo.getCarryOverModule());
                    accCarryOver.setCreateMan(vo.getCreateMan());
                    accCarryOver.setCreateDate(vo.getCreateDate());
                    // 修改ysd数据库 一键生单
                    if ("ysd".equals("sss")) {
                        accCarryOver.setKmtype(kmtype);
                    }
                    Mono<R> map = Mono.just(accCarryOver)
                            .flatMap(entity ->
                                    getLastOrderNum(entity.getCarryOverModule())
                                            .doOnNext(entity::setOrderNum)
                                            .map(item -> entity)
                            )
                            .flatMap(carryOverRepository::save)
                            .map(item -> item.getId())
                            .doOnNext(id -> formulasToEntitys(overEntries, saveEntryFormulas, id))
                            .flatMap(
                                    item -> carryOverEntryRepository.saveAll(overEntries).collectList()
                                            .flatMap(entirys ->
                                                    accCarryOver.getCarryOverType().equals("1") ?
                                                            carryOverEntryFormulaRepository.saveAll(saveEntryFormulas).collectList() : Flux.just(new AccCarryOverEntryFormula()).collectList())
                            )
                            .map(item -> R.ok());
                    return map;
                }));
    }


    @DeleteMapping("carryover")
    @ApiOperation(value = "删除一条结转", notes = "Vo")

    public Mono<R> del(@RequestBody CarryOverVo vo) {
        return carryOverRepository.getById(vo.getId()).flatMap(entity -> {
            Flux<AccCarryOverEntry> two = carryOverEntryRepository.findAllByOnlyNumOrderById(entity.getId().toString());
            return two
                    // 删除3级
                    .filter(item -> entity.getCarryOverType().equals("1"))
                    .map(AccCarryOverEntry::getOwnership)
                    .map(carryOverEntryFormulaRepository::findAllByOwnership)
                    .flatMap(carryOverEntryFormulaRepository::deleteAll)
                    // 删除2级
                    .then(carryOverEntryRepository.deleteAll(two))
                    .thenReturn(entity)
                    .then(carryOverRepository.delete(entity))
                    .thenReturn(entity);
        }).map(R::ok);
    }

    private Mono<String> getLastOrderNum(String model) {
        return carryOverRepository.findFirstByCarryOverModuleOrderByOrderNumDesc(model)
                .map(entity -> {
                    int num = (Integer.parseInt(entity.getOrderNum()) + 1);
                    return (num < 10) ? "0" + num : "" + num;
                })
                .switchIfEmpty(Mono.just("01"));
    }

    private void formulasToEntitys(List<AccCarryOverEntry> overEntries, List<AccCarryOverEntryFormula> saveEntryFormulas, String only) {
        for (AccCarryOverEntry overEntry : overEntries) {
            overEntry.setOnlyNum(only);
            String entryOnly = IdUtil.objectId();
            overEntry.setOwnership(entryOnly);
            if (StringUtils.isNotBlank(overEntry.getAmountFormula())) { // 不为空时
                List<AccCarryOverEntryFormula> formulas = JSON.parseArray(overEntry.getEntryFormulas(), AccCarryOverEntryFormula.class);
                for (AccCarryOverEntryFormula formula : formulas) {
                    formula.setOwnership(entryOnly);
                }
                if (formulas.size() > 0) saveEntryFormulas.addAll(formulas);
            }
            overEntry.setEntryFormulas("");
        }
    }
    /************************************************* 对应结转 or 自定义结转 end ********************************************/

}
