package org.boozsoft.rest;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.boozsoft.domain.entity.SysAccvoucherTemplate;
import org.boozsoft.domain.entity.SysVoucherImport;
import org.boozsoft.domain.entity.SysVoucherImportEntry;
import org.boozsoft.domain.entity.account.FuzhuHesuan;
import org.boozsoft.domain.vo.SubjectInitialBalanceVo;
import org.boozsoft.repo.*;
import org.boozsoft.service.impl.AccvoucherServiceImpl;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @ClassName :
 * @Description : 凭证导入模板设置
 * @Author : miao
 * @Date: 2021-04-01 09:25
 */
@Slf4j
@RestController
@RequestMapping("/sys/accvoucher_template")
@Api(value = "凭证导入模板设置", tags = "API系统：凭证导入模板设置")
public class AccvoucherTemplateController {
    @Autowired
    SysAccvoucherTemplateRepository sysAccvoucherTemplateRepository;

    @Autowired
    AccvoucherRepository accvoucherRepository;

    @Autowired
    FuzhuHesuanRepository fuzhuHesuanRepository;

    @PostMapping("/findAll")
    public Mono<R> findAll() {
        return sysAccvoucherTemplateRepository.findAll().collectList()
                .map(R::page);
    }

    @PostMapping("/save")
    public Mono<R> save(@RequestBody SysAccvoucherTemplate sysAccvoucherTemplate) {
        return sysAccvoucherTemplateRepository.save(sysAccvoucherTemplate)
                .map(o -> R.ok().setResult(o));
    }

    /********************************************************start新业务**********************************************************/
    @Autowired
    private SysVoucherImportRepository voucherImportRepository;

    @Autowired
    private SysVoucherImportEntryRepository voucherImportEntryRepository;

    @PostMapping("/new/findAll")
    public Mono<R> findAllList() {
        return voucherImportRepository.findAllByOrderByTemplateNumberAsc()
                .flatMap( // 组装数据
                        sysVoucherImport -> Mono.just(sysVoucherImport.getId())
                                .flatMapMany(id -> voucherImportEntryRepository.findAllByUniqueCodeOrderById(id + ""))
                                .collectList().map(item -> JSON.toJSONString(item))
                                .map(sysVoucherImport::setEntryList)
                ).collectList().map(list->
                     ListUtil.sort(list, (o1, o2) -> o1.getTemplateNumber().compareTo(o2.getTemplateNumber()))
                )
                .map(R::ok);
    }

    public Flux<SysVoucherImportEntry> toPingZhengImportEntryFlux(String entryListStr) {
        return Flux.fromIterable(JSON.parseArray(entryListStr, SysVoucherImportEntry.class));
    }


    @PostMapping("/new/save")
    @Transactional
    public Mono<R> save(@RequestBody Mono<SysVoucherImport> voucherImportMono) {
        AtomicReference<List<SysVoucherImportEntry>> pingZhengFenLuList = new AtomicReference();
        return voucherImportMono
                .flatMap(item ->
                        Mono.just(item)
                                .map(SysVoucherImport::getEntryList)
                                .flatMapMany(this::toPingZhengImportEntryFlux)
                                .collectList()
                                .doOnNext(pingZhengFenLuList::set)
                                .thenReturn(item)
                )/*flatMap(item -> //存入最新排序
                        voucherImportRepository.findFirstByOrderByTemplateNumberDesc()
                                .doOnNext(item2->
                                      item.setTemplateNumber((Integer.parseInt(item2.getTemplateNumber())+1)+"")
                                ).thenReturn(item)
                )*/
                .flatMap(item->{
                    return voucherImportRepository.save(item);
                })
                .flatMapMany(item -> {
                    return Flux.fromIterable(pingZhengFenLuList.get())
                            .doOnNext(item2 -> item2.setUniqueCode(item.getId()));
                })
                .collectList()
                .flatMapMany(voucherImportEntryRepository::saveAll).collectList()
                .map(R::ok);
    }


    @PostMapping("/new/update")
    @Transactional
    public Mono<R> update(@RequestBody Mono<SysVoucherImport> voucherImportMono) {
        AtomicReference<List<SysVoucherImportEntry>> pingZhengFenLuList = new AtomicReference();
        AtomicReference<SysVoucherImport> entity = new AtomicReference();
        return voucherImportMono
                .flatMap(item ->
                        Mono.just(item)
                                .map(SysVoucherImport::getEntryList)
                                .flatMapMany(this::toPingZhengImportEntryFlux)
                                .collectList()
                                .doOnNext(pingZhengFenLuList::set)
                                .doOnNext(item2 -> entity.set(item))
                                .thenReturn(item)
                )
                .flatMap(entry -> voucherImportRepository.findById(entry.getId()))
                .flatMap(dbEntry -> Mono.just("")
                        .map(item -> entity.get())
                        .doOnNext(entry ->
                                dbEntry
                                        .setTemplateName(entity.get().getTemplateName())
                                        .setTemplateNumber(entity.get().getTemplateNumber())
                        )
                        .thenReturn(dbEntry)
                )
                .flatMap(dbEntry ->
                        voucherImportEntryRepository.findAllByUniqueCodeOrderById(dbEntry.getId().toString())
                                .collectList()
                                .flatMap(voucherImportEntryRepository::deleteAll)
                                .doOnNext(item2 -> entity.set(dbEntry))
                                .thenReturn(dbEntry)
                )
                .flatMapMany(item ->
                     Flux.fromIterable(pingZhengFenLuList.get())
                            .doOnNext(item2 -> item2.setUniqueCode(item.getId().toString()).setId(null))
                )
                .collectList()
                .flatMapMany(list->  voucherImportEntryRepository.saveAll(list))
                .then(Mono.just("").map(item -> entity.get()))
                .flatMap(voucherImportRepository::save)
                .map(R::ok);
    }

    @DeleteMapping("/new")
    @ApiOperation(value = "删除模板及分录", notes = "Vo")
    public Mono<R> del(@RequestBody SysVoucherImport voucherImport) {
        return voucherImportRepository.findById(voucherImport.getId()).flatMap(dbEntry ->
                voucherImportEntryRepository.findAllByUniqueCodeOrderById(dbEntry.getId().toString())
                        .collectList()
                        .flatMap(voucherImportEntryRepository::deleteAll)
                        .thenReturn(dbEntry)
        ).flatMap(voucherImportRepository::delete).map(R::ok);
    }

    @PostMapping("/new/field")
    @ApiOperation(value = "获取指定模板字段", notes = "Vo")
    public Mono<R> field(@RequestBody Mono<SysVoucherImport> voucherImportMono) {
        AtomicReference<List<String>> fields = new AtomicReference();
       return voucherImportMono
                .flatMap(
                        item->
                            voucherImportEntryRepository.findAllByUniqueCodeOrderById(item.getId().toString())
                                    .doOnNext(entry->{
                                                String titleName = StringUtils.isNotBlank(entry.getCustomerFieldName())?entry.getCustomerFieldName():entry.getSystemFieldName();
                                                    AccvoucherServiceImpl.checkValue(fields, titleName,item.getTemplateType().equals("1"));
                                    })
                                    .then(Mono.just("").map(a -> fields.get()))
                ).map(R::ok);
    }

    /********************************************************新业务end**********************************************************/

    /**
     * 获取期初余额导入模板
     * @return
     */
    @PostMapping("/new/field_accass")
    @ApiOperation(value = "获取期初余额导入模板", notes = "Vo")
    public Mono<R> field_accass(@RequestBody Map map) {
        String iyear = "";
        String databasenum = "";
        iyear = (String) map.get("iyear");
        databasenum = (String) map.get("databasenum");
        AtomicReference<List<String>> fields = new AtomicReference();
        List<FuzhuHesuan> fzs = new ArrayList<>();
        return accvoucherRepository.findAllSubjectInitialBalanceLastCodeFuZhu2(iyear,databasenum)
                .flatMap(i -> {
                     return fuzhuHesuanRepository.findAll()
                             .collectList().map(v -> {
                                 fzs.addAll(v);
                                 return i;
                             });
                })
                .map( item -> {

                    if (null == fields.get()) fields.set(new ArrayList<>());

                    fields.get().add("科目名称");
                    fields.get().add("科目编码");

                    if(!"0".equals(item.getBperson())){fields.get().add("个人编码");fields.get().add("个人姓名");};
                    if(!"0".equals(item.getBdept())){fields.get().add("部门编码");fields.get().add("部门名称");}
                    if(!"0".equals(item.getBcus())){fields.get().add("客户编码");fields.get().add("客户名称");}
                    if(!"0".equals(item.getBsup())){fields.get().add("供应商编码");fields.get().add("供应商名称");}
                    if(!"0".equals(item.getBitem())){fields.get().add("项目大类编码");fields.get().add("项目大类名称");}
                    if(!"0".equals(item.getBitem())){fields.get().add("项目编码");fields.get().add("项目名称");}
                    if(!"0".equals(item.getBitem())){fields.get().add("项目编码");fields.get().add("项目名称");}

                    if(!"0".equals(item.getCdfine1())){String name = "自定义1";for (FuzhuHesuan f:fzs) {if("1".equals(f.getCdfine())){name = f.getCname();fields.get().add(name);break;}}}
                    if(!"0".equals(item.getCdfine2())){String name = "自定义2";for (FuzhuHesuan f:fzs) {if("2".equals(f.getCdfine())){name = f.getCname();fields.get().add(name);break;}}}
                    if(!"0".equals(item.getCdfine3())){String name = "自定义3";for (FuzhuHesuan f:fzs) {if("3".equals(f.getCdfine())){name = f.getCname();fields.get().add(name);break;}}}
                    if(!"0".equals(item.getCdfine4())){String name = "自定义4";for (FuzhuHesuan f:fzs) {if("4".equals(f.getCdfine())){name = f.getCname();fields.get().add(name);break;}}}
                    if(!"0".equals(item.getCdfine5())){String name = "自定义5";for (FuzhuHesuan f:fzs) {if("5".equals(f.getCdfine())){name = f.getCname();fields.get().add(name);break;}}}
                    if(!"0".equals(item.getCdfine6())){String name = "自定义6";for (FuzhuHesuan f:fzs) {if("6".equals(f.getCdfine())){name = f.getCname();fields.get().add(name);break;}}}
                    if(!"0".equals(item.getCdfine7())){String name = "自定义7";for (FuzhuHesuan f:fzs) {if("7".equals(f.getCdfine())){name = f.getCname();fields.get().add(name);break;}}}
                    if(!"0".equals(item.getCdfine8())){String name = "自定义8";for (FuzhuHesuan f:fzs) {if("8".equals(f.getCdfine())){name = f.getCname();fields.get().add(name);break;}}}
                    if(!"0".equals(item.getCdfine9())){String name = "自定义9";for (FuzhuHesuan f:fzs) {if("9".equals(f.getCdfine())){name = f.getCname();fields.get().add(name);break;}}}
                    if(!"0".equals(item.getCdfine10())){String name = "自定义10";for (FuzhuHesuan f:fzs) {if("10".equals(f.getCdfine())){name = f.getCname();fields.get().add(name);break;}}}
                    if(!"0".equals(item.getCdfine11())){String name = "自定义11";for (FuzhuHesuan f:fzs) {if("11".equals(f.getCdfine())){name = f.getCname();fields.get().add(name);break;}}}
                    if(!"0".equals(item.getCdfine12())){String name = "自定义12";for (FuzhuHesuan f:fzs) {if("12".equals(f.getCdfine())){name = f.getCname();fields.get().add(name);break;}}}
                    if(!"0".equals(item.getCdfine13())){String name = "自定义13";for (FuzhuHesuan f:fzs) {if("13".equals(f.getCdfine())){name = f.getCname();fields.get().add(name);break;}}}
                    if(!"0".equals(item.getCdfine14())){String name = "自定义14";for (FuzhuHesuan f:fzs) {if("14".equals(f.getCdfine())){name = f.getCname();fields.get().add(name);break;}}}
                    if(!"0".equals(item.getCdfine15())){String name = "自定义15";for (FuzhuHesuan f:fzs) {if("15".equals(f.getCdfine())){name = f.getCname();fields.get().add(name);break;}}}
                    if(!"0".equals(item.getCdfine16())){String name = "自定义16";for (FuzhuHesuan f:fzs) {if("16".equals(f.getCdfine())){name = f.getCname();fields.get().add(name);break;}}}
                    if(!"0".equals(item.getCdfine17())){String name = "自定义17";for (FuzhuHesuan f:fzs) {if("17".equals(f.getCdfine())){name = f.getCname();fields.get().add(name);break;}}}
                    if(!"0".equals(item.getCdfine18())){String name = "自定义18";for (FuzhuHesuan f:fzs) {if("18".equals(f.getCdfine())){name = f.getCname();fields.get().add(name);break;}}}
                    if(!"0".equals(item.getCdfine19())){String name = "自定义19";for (FuzhuHesuan f:fzs) {if("19".equals(f.getCdfine())){name = f.getCname();fields.get().add(name);break;}}}
                    if(!"0".equals(item.getCdfine20())){String name = "自定义20";for (FuzhuHesuan f:fzs) {if("20".equals(f.getCdfine())){name = f.getCname();fields.get().add(name);break;}}}
                    if(!"0".equals(item.getCdfine21())){String name = "自定义21";for (FuzhuHesuan f:fzs) {if("21".equals(f.getCdfine())){name = f.getCname();fields.get().add(name);break;}}}
                    if(!"0".equals(item.getCdfine22())){String name = "自定义22";for (FuzhuHesuan f:fzs) {if("22".equals(f.getCdfine())){name = f.getCname();fields.get().add(name);break;}}}
                    if(!"0".equals(item.getCdfine23())){String name = "自定义23";for (FuzhuHesuan f:fzs) {if("23".equals(f.getCdfine())){name = f.getCname();fields.get().add(name);break;}}}
                    if(!"0".equals(item.getCdfine24())){String name = "自定义24";for (FuzhuHesuan f:fzs) {if("24".equals(f.getCdfine())){name = f.getCname();fields.get().add(name);break;}}}
                    if(!"0".equals(item.getCdfine25())){String name = "自定义25";for (FuzhuHesuan f:fzs) {if("25".equals(f.getCdfine())){name = f.getCname();fields.get().add(name);break;}}}
                    if(!"0".equals(item.getCdfine26())){String name = "自定义26";for (FuzhuHesuan f:fzs) {if("26".equals(f.getCdfine())){name = f.getCname();fields.get().add(name);break;}}}
                    if(!"0".equals(item.getCdfine27())){String name = "自定义27";for (FuzhuHesuan f:fzs) {if("27".equals(f.getCdfine())){name = f.getCname();fields.get().add(name);break;}}}
                    if(!"0".equals(item.getCdfine28())){String name = "自定义28";for (FuzhuHesuan f:fzs) {if("28".equals(f.getCdfine())){name = f.getCname();fields.get().add(name);break;}}}
                    if(!"0".equals(item.getCdfine29())){String name = "自定义29";for (FuzhuHesuan f:fzs) {if("29".equals(f.getCdfine())){name = f.getCname();fields.get().add(name);break;}}}
                    if(!"0".equals(item.getCdfine30())){String name = "自定义30";for (FuzhuHesuan f:fzs) {if("30".equals(f.getCdfine())){name = f.getCname();fields.get().add(name);break;}}}

                    if(!"0".equals(item.getBnum())){
                        fields.get().add("数量");
                    }

                    if(!"0".equals(item.getCurrency())){
                        fields.get().add("原币金额");
                        fields.get().add("汇率");
                    }

                    fields.get().add("借方金额");
                    fields.get().add("贷方金额");

                    return fields;
                })
                .map(R::ok);
    }
}
