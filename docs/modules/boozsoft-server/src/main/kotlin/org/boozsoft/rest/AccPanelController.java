package org.boozsoft.rest;


import cn.hutool.json.JSONObject;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.boozsoft.domain.entity.AccPanel;
import org.boozsoft.domain.entity.codekemu.CodeKemu;
import org.boozsoft.domain.entity.group.GroupCustomerClassAccount;
import org.boozsoft.domain.vo.AccPanelVo;
import org.boozsoft.domain.vo.AccvoucherVo;
import org.boozsoft.domain.vo.SubjectInitialBalanceVo;
import org.boozsoft.repo.AccPanelRepository;
import org.boozsoft.repo.AccvoucherRepository;
import org.boozsoft.repo.codekemu.CodeKemuRepository;
import org.boozsoft.repo.group.GroupSysUserRepository;
import org.boozsoft.service.SubjectInitalBalanceService;
import org.boozsoft.service.dataBase.DataBaseService;
import org.boozsoft.util.BigDecimalUtils;
import org.boozsoft.utils.CollectOfUtils;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/accpanel")
@Api(value = "总账面板设置", tags = "总账面板设置")
public class AccPanelController {
    @Autowired
    AccPanelRepository accPanelRepository;
    @Autowired
    CodeKemuRepository codeKemuRepository;
    @Autowired
    AccvoucherRepository accvoucherRepository;
    @Autowired
    SubjectInitalBalanceService subjectInitalBalanceService;
    @Autowired
    GroupSysUserRepository groupSysUserRepository;
    @Autowired
    DataBaseService dataBaseService;

    @PostMapping("findAllByGroupSysUserFlag")
    public Mono<R> findAllByGroupSysUserFlag() {

        return groupSysUserRepository.findAllByflag("1").collectList().map(o -> R.ok().setResult(o));
    }

    @PostMapping("findAllByflag")
    public Mono<R> findAllByflag(@RequestBody Map map) {
        String flag = map.get("panelFlag").toString();
        String iyear = map.get("iyear").toString();
        String sysuser = map.get("sysuser").toString();
        String database = map.get("database").toString();
        String type = map.get("type").toString();

        return accPanelRepository.findAllByPanelFlagAndPanelIyear(flag, iyear, type).collectList()
            .flatMap(item -> {
                List<AccPanel> list = item;
                if ("2".equals(flag)) {
                    list = item.stream().filter(a -> a.getCaozuoUnique().equals(sysuser)).collect(Collectors.toList());
                }
                Collections.sort(list, Comparator.comparing(AccPanel::getPanelOrder));
                return Mono.just(list);
            }).map(o -> R.ok().setResult(o));
    }

    @PostMapping("savePanel")
    public Mono<R> savePanel(@RequestBody AccPanel panel) {
        panel.setPanelName(panel.getPanelName().trim());
        return accPanelRepository.save(panel).map(o -> R.ok().setResult(o));
    }

    @DeleteMapping("del")
    public Mono<R> delPanel(@RequestBody AccPanel panel) {
        return accPanelRepository.delete(panel).then(Mono.just(R.ok()));
    }

    @PostMapping("countByPanelName")
    public Mono<R> delPanel(String name) {
        return accPanelRepository.countByPanelName(name).map(o -> R.ok().setResult(o));
    }


    /**
     * 复制上年度总账面板
     * @return
     */
    @PostMapping("copyPanel")
    public Mono<R> copyPanel(@RequestBody List<AccPanel> list) {
        return accPanelRepository.saveAll(list).collectList().map(a->R.ok().setResult(a));
    }

    /************************************* 首页数据读取：个人方案 大于 集团方案 ************************************************/
    @PostMapping("firstShowData")
    public Mono<R> countByFlag(String iyear, String panelFlag,String panelType) {
        String year = iyear.split("-")[0];
        String imonth = iyear.split("-")[1];
        Mono<List<AccvoucherVo>> abc1 = accvoucherRepository.findAllByiyperiodpz(year).collectList().doOnNext(it->{
        }).cache();
        Mono<List<CodeKemu>> abc2 = codeKemuRepository.findAllByFlagAndIyearOrderByCcodeAscBendAsc("1", year).collectList().doOnNext(it->{
        }).cache();
        Mono<R> abc3 = subjectInitalBalanceService.findAllSubjectInitialBalance(year, "false", year + "00", "","","全部","类型").cache();
        Mono<List<AccPanel>> abc4 = accPanelRepository.findAllByPanelIyear(year,panelType).collectList().cache();

        Map m=new HashMap();
        m.put("sql","CREATE TABLE \"public\".\"acc_panel\" (\n" +
                "  \"id\" varchar COLLATE \"pg_catalog\".\"default\",\n" +
                "  \"caozuo_unique\" varchar COLLATE \"pg_catalog\".\"default\",\n" +
                "  \"panel_iyear\" varchar COLLATE \"pg_catalog\".\"default\",\n" +
                "  \"panel_name\" varchar COLLATE \"pg_catalog\".\"default\",\n" +
                "  \"panel_formula\" varchar COLLATE \"pg_catalog\".\"default\",\n" +
                "  \"panel_data_range\" varchar COLLATE \"pg_catalog\".\"default\",\n" +
                "  \"panel_bz\" varchar COLLATE \"pg_catalog\".\"default\",\n" +
                "  \"panel_bz_unit\" varchar COLLATE \"pg_catalog\".\"default\",\n" +
                "  \"panel_flag\" varchar COLLATE \"pg_catalog\".\"default\",\n" +
                "  \"panel_order\" int4,\n" +
                "  \"tenant_id\" varchar COLLATE \"pg_catalog\".\"default\" DEFAULT CURRENT_USER,\n" +
                "  \"split_code\" varchar COLLATE \"pg_catalog\".\"default\",\n" +
                "  \"split_fuhao\" varchar COLLATE \"pg_catalog\".\"default\"\n" +
                ")\n" +
                ";\n" +
                "\n" +
                "ALTER TABLE \"public\".\"acc_panel\" \n" +
                "  OWNER TO \"postgres\";\n" +
                "\n" +
                "COMMENT ON COLUMN \"public\".\"acc_panel\".\"caozuo_unique\" IS '操作员唯一编码（系统方案不需要）';\n" +
                "\n" +
                "COMMENT ON COLUMN \"public\".\"acc_panel\".\"panel_iyear\" IS '年度';\n" +
                "\n" +
                "COMMENT ON COLUMN \"public\".\"acc_panel\".\"panel_name\" IS '显示名称';\n" +
                "\n" +
                "COMMENT ON COLUMN \"public\".\"acc_panel\".\"panel_formula\" IS '科目公式（加减）';\n" +
                "\n" +
                "COMMENT ON COLUMN \"public\".\"acc_panel\".\"panel_data_range\" IS '取值方式：余额、借方余额、贷方余额、借方累计发生、贷方累计发生';\n" +
                "\n" +
                "COMMENT ON COLUMN \"public\".\"acc_panel\".\"panel_bz\" IS '币种';\n" +
                "\n" +
                "COMMENT ON COLUMN \"public\".\"acc_panel\".\"panel_bz_unit\" IS '显示币种单位';\n" +
                "\n" +
                "COMMENT ON COLUMN \"public\".\"acc_panel\".\"panel_flag\" IS '1系统方案、2个人方案';\n" +
                "\n" +
                "COMMENT ON COLUMN \"public\".\"acc_panel\".\"panel_order\" IS '排序号';\n" +
                "\n" +
                "COMMENT ON COLUMN \"public\".\"acc_panel\".\"split_code\" IS '设置公式后，分离后的科目';\n" +
                "\n" +
                "COMMENT ON COLUMN \"public\".\"acc_panel\".\"split_fuhao\" IS '设置公式后，分离后的符号';\n" +
                "COMMENT ON TABLE \"public\".\"acc_panel\" IS '总账面板设置表';");
        return dataBaseService.findByDataBaseName("acc_panel",m)
            .flatMap(tx->{
               return abc4
                    .flatMap(list -> {
                        List<AccPanel> newlist = list;
                        if (panelFlag.equals("1")) {
                            newlist = list.stream().filter(a -> a.getPanelFlag().equals("1")).collect(Collectors.toList());
                        } else {
                            newlist = list.stream().filter(a -> a.getPanelFlag().equals("2")).collect(Collectors.toList());
                        }
                        Collections.sort(newlist, Comparator.comparing(AccPanel::getPanelOrder));
                        List<AccPanel> finalNewlist = newlist;
                        return abc3.flatMap(qclist -> {
                            Map qcmap = (Map) qclist.getResult();
                            return abc1.flatMap(pzlist -> abc2
                                    .map(codelist -> CollectOfUtils.mapof("pzlist", pzlist, "qclist", qcmap.get("tablesData"), "list", finalNewlist, "codelist", codelist))
                            );
                        });
                    })
                    .flatMap(map -> {
                        List<AccPanel> list = (List<AccPanel>) map.get("list");
                        List<CodeKemu> codelist = (List<CodeKemu>) map.get("codelist");
                        List<AccvoucherVo> pzlist = (List<AccvoucherVo>) map.get("pzlist");
                        List<SubjectInitialBalanceVo> qclist = (List<SubjectInitialBalanceVo>) map.get("qclist");

                        List<Map<String, Object>> listmap = new ArrayList<>();
                        list.stream().forEach(panel -> {
                            BigDecimal money = new BigDecimal(0);
                            if (StringUtils.isNotBlank(panel.getSplitCode())) {
                                List<AccPanelVo> volist = new ArrayList<>();
                                for (int i = 0; i < panel.getSplitCode().split(",").length; i++) {
                                    BigDecimal qc_yue = new BigDecimal(0);
                                    BigDecimal qm_yue = new BigDecimal(0);

                                    String code = panel.getSplitCode().split(",")[i];
                                    String bprogerty ="1";      // 科目表没有此科目；默认借方
                                    List<CodeKemu> collect = codelist.stream().filter(ck -> ck.getCcode().equals(code)).collect(Collectors.toList());
                                    if(collect.size()>0){  bprogerty=collect.get(0).getBprogerty(); }
                                    // 获取期初
                                    List<SubjectInitialBalanceVo> qc = qclist.stream().filter(acv -> acv.getCcode().equals(code)).collect(Collectors.toList());
                                    BigDecimal qc_md = qc.size() > 0 ? qc.get(0).getMd() : new BigDecimal("0");
                                    BigDecimal qc_mc = qc.size() > 0 ? qc.get(0).getMc() : new BigDecimal("0");

                                    BigDecimal pz_md = new BigDecimal(0);
                                    BigDecimal pz_mc = new BigDecimal(0);
                                    // 期间或日期 不是从一月开始的 计算出上月的期末余额
                                    if (Integer.valueOf(imonth) > 1) {
                                        String lastmonth = Integer.valueOf(imonth) - 1 < 10 ? "0" + (Integer.valueOf(imonth) - 1) : imonth;
                                        // 获取上个月凭证发生数
                                        List<AccvoucherVo> lastMonthAccVoucher = pzlist.stream().filter(acv -> acv.getCcode().startsWith(code) && Integer.valueOf(acv.getImonth()) <= Integer.valueOf(lastmonth)).collect(Collectors.toList());
                                        pz_md = lastMonthAccVoucher.size() == 0 ? new BigDecimal("0") : lastMonthAccVoucher.stream().map(AccvoucherVo::getMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                                        pz_mc = lastMonthAccVoucher.size() == 0 ? new BigDecimal("0") : lastMonthAccVoucher.stream().map(AccvoucherVo::getMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                                    }
                                    // 借+借-贷+贷
                                    if (bprogerty.equals("1")) {
                                        qc_yue = qc_md.add(pz_md).subtract(qc_mc.add(pz_mc));
                                    } else {
                                        qc_yue = qc_mc.add(pz_mc).subtract(qc_md.add(pz_md));
                                    }
                                    // 凭证
                                    List<AccvoucherVo> accvoucher = pzlist.stream().filter(acv -> acv.getCcode().startsWith(code) && Integer.valueOf(acv.getImonth()) == Integer.valueOf(imonth)).collect(Collectors.toList());
                                    pz_md = accvoucher.size() == 0 ? new BigDecimal("0") : accvoucher.stream().map(AccvoucherVo::getMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                                    pz_mc = accvoucher.size() == 0 ? new BigDecimal("0") : accvoucher.stream().map(AccvoucherVo::getMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                                    if (bprogerty.equals("1")) {
                                        qm_yue = qc_yue.add(pz_md).subtract(pz_mc);
                                    } else {
                                        qm_yue = qc_yue.add(pz_mc).subtract(qc_md);
                                    }

                                    // 凭证累计发生
                                    List<AccvoucherVo> accvoucher2 = pzlist.stream().filter(acv -> acv.getCcode().startsWith(code) && Integer.valueOf(acv.getImonth()) <= Integer.valueOf(imonth)).collect(Collectors.toList());
                                    BigDecimal ljpz_md = accvoucher2.size() == 0 ? new BigDecimal("0") : accvoucher2.stream().map(AccvoucherVo::getMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                                    BigDecimal ljpz_mc = accvoucher2.size() == 0 ? new BigDecimal("0") : accvoucher2.stream().map(AccvoucherVo::getMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);

                                    volist.add(
                                            new AccPanelVo().setFuhao(panel.getSplitFuhao().split(",")[i])
                                                    .setYue(qm_yue)
                                                    .setYueMd(qm_yue.compareTo(BigDecimal.ZERO) > 0 ? qm_yue : new BigDecimal("0"))
                                                    .setYueMc(qm_yue.compareTo(BigDecimal.ZERO) < 0 ? qm_yue : new BigDecimal("0"))
                                                    .setMd(ljpz_md).setMc(ljpz_mc)
                                    );
//                                System.out.println(qc_md+"\t"+qc_mc+"\t"+pz_md+"\t"+pz_mc+"\t"+qm_yue);
                                }
                                BigDecimal yue = new BigDecimal(0);
                                BigDecimal yue_md = new BigDecimal(0);
                                BigDecimal yue_mc = new BigDecimal(0);
                                BigDecimal md = new BigDecimal(0);
                                BigDecimal mc = new BigDecimal(0);
                                for (int i = 0; i < volist.size(); i++) {
                                    if (volist.get(i).getFuhao().equals("+")) {
                                        yue = yue.add(volist.get(i).getYue());
                                        yue_md = yue_md.add(volist.get(i).getYueMd());
                                        yue_mc = yue_mc.add(volist.get(i).getYueMc());
                                        md = md.add(volist.get(i).getMd());
                                        mc = mc.add(volist.get(i).getMc());
                                    } else {
                                        yue = yue.subtract(volist.get(i).getYue());
                                        yue_md = yue_md.subtract(volist.get(i).getYueMd());
                                        yue_mc = yue_mc.subtract(volist.get(i).getYueMc());
                                        md = md.subtract(volist.get(i).getMd());
                                        mc = mc.subtract(volist.get(i).getMc());
                                    }
                                }
                                if (panel.getPanelDataRange().equals("余额")) {
                                    money = yue;
                                } else if (panel.getPanelDataRange().equals("借方余额")) {
                                    money = yue_md;
                                } else if (panel.getPanelDataRange().equals("贷方余额")) {
                                    money = yue_mc;
                                } else if (panel.getPanelDataRange().equals("借方累计发生")) {
                                    money = md;
                                } else if (panel.getPanelDataRange().equals("贷方累计发生")) {
                                    money = mc;
                                }
                            }
                            listmap.add(CollectOfUtils.mapof("panelFlag", panel.getPanelFlag(), "panelName", panel.getPanelName(), "bz", StringUtils.isNotBlank(panel.getPanelBz()) ? panel.getPanelBz() : "", "bzUnit", StringUtils.isNotBlank(panel.getPanelBzUnit()) ? panel.getPanelBzUnit() : "", "money", money));
                        });
                        return Mono.just(listmap);
                    });
            }).map(o -> R.ok().setResult(o));
    }
}
