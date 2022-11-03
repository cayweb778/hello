package org.boozsoft.rest;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.boozsoft.domain.entity.accstandard.AccStandard;
import org.boozsoft.domain.entity.accstyle.AccStyle;
import org.boozsoft.domain.entity.acctemplate.AccTemplate;
import org.boozsoft.repo.accstyle.AccStyleRepository;
import org.boozsoft.repo.acctemplate.AccTemplateRepository;
import org.boozsoft.repo.codekemu.GroupCodeKemuRepository;
import org.boozsoft.repo.standard.AccStandardRepository;
import org.boozsoft.util.XlsUtils;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @ClassName : AccStandardController
 * @Description : 会计准则
 * @Author : miao
 * @Date: 2021-05-12 10:40
 */

@Slf4j
@RestController
@RequestMapping("/sys/accstandard")
@Api(value = "会计准则", tags = "API系统：会计准则")
public class AccStandardController {

    @Autowired
    AccStandardRepository standardRepository;
    @Autowired
    AccStyleRepository accStyleRepository;
    @Autowired
    AccTemplateRepository accTemplateRepository;
    @Autowired
    GroupCodeKemuRepository groupCodeKemuRepository;

    @GetMapping("/findAllAcctandard")
    public Mono<R> findAllAcctandard() {
        return standardRepository.findAllAssStandard().collectList().map(R::page);
    }

    // 查询有自定义模板的会计准则
    @PostMapping("/findByTypeAcctandardList")
    public Mono<R> findByTypeAcctandardList() {
        return standardRepository.findByTypeAcctandardList().collectList()
                .map(o->R.ok().setResult(o));
    }
    @PostMapping("/findAllAcctandardList")
    public Mono<R> findAllAcctandardList() {
        return standardRepository.findAllAssStandard().collectList()
                .map(o->R.ok().setResult(o));
    }

    @PostMapping("/findByStandardID")
    public Mono<R> findByStandardID(String id) {
        return standardRepository.findById(id).map(o->R.ok().setResult(o));
    }
    
    /**
     * @Author myh
     * @Description 重新查询科目类型
     * @Date 9:19 2021/7/14
     * @Param [unique]
     * @return reactor.core.publisher.Mono<org.springbooz.core.tool.result.R> 
     **/
    @PostMapping("findAllStyleByUnique")
    public Mono<R> findAllStyleByUnique(String unique) {
        return accStyleRepository.findAllStyleByUnique(unique).collectList().map(o->R.ok().setResult(o));
    }
    @PostMapping("company_findAllStyleByUnique")
    public Mono<R> company_findAllStyleByUnique() {
        return accStyleRepository.findAlls().collectList().map(o->R.ok().setResult(o));
    }

    @PostMapping("findByAccStyle")
    public Mono<R> findByAccStyle(String uniqueAccStandard) {
        return accStyleRepository.findByUnique(uniqueAccStandard).collectList().map(o->R.ok().setResult(o)).defaultIfEmpty(R.ok().setResult(""));
    }

    /* *************************************** 获取账套数据 ******************************************* */
    @GetMapping("/company/company_findAllAcctandardList")
    public Mono<R> company_findAllAcctandardList() {
        return standardRepository.findAllAssStandard().collectList().map(o->R.ok().setResult(o));
    }

    @PostMapping("findByStandardUnique")
    public Mono<R> findByStandardUnique(String unique) {
        return standardRepository.findByStandardUnique(unique).map(o->R.ok().setResult(o)).defaultIfEmpty(R.ok().setResult(""));
    }

    /**
     * 根据账套ID查询会计科目类型
     * @param accId
     * @return
     */
    @PostMapping("findByAccIdAccStyle")
    public Mono<R> findByAccIdAccStyle(String accId) {
        return accStyleRepository.findByAccIdAccStyle(accId).map(o->R.ok().setResult(o));
    }


    @GetMapping("findByAccStyleStr")
    public Mono<R> findByAccStyleStr() {
        return accStyleRepository.groupByAccStyleUnique().collectList()
            .flatMap(accStyleUnique->{
               return accStyleRepository.findAllOrderByNum().collectList()
                    .flatMap(accStyleList->{
                        List<Map> list=new ArrayList<>();
                        accStyleUnique.forEach(unique->{
                            List<AccStyle> collect = accStyleList.stream().filter(v -> v.getUnique().equals(unique)).collect(Collectors.toList());
                            String str="";
                            boolean yusuan=false; // 是否预算会计
                            for (int i = 0; i < collect.size(); i++) {
                                str+=collect.get(i).getCclass()+"-";
                                if(StringUtils.isNotBlank(collect.get(i).getFlagYusuan()) && collect.get(i).getFlagYusuan().equals("1")){
                                    yusuan=true;
                                }
                            }
                            Map map=new HashMap();
                            map.put("value",unique);
                            map.put("yusuan",yusuan);
                            map.put("title",str.substring(0,str.length()-1));
                            list.add(map);
                        });
                        return Mono.just(list);
                    });
            }).map(o->R.ok().setResult(o));
    }

    @PostMapping("findByStandardName")
    public Mono<R> findByStandardName(String name) {
        return standardRepository.countByAccStandardName(name.trim()).map(o->R.ok().setResult(o));
    }

    @PostMapping("saveStandard")
    public Mono<R> saveStandard(String accStandardName,String codeType) {
        String type=codeType.split("-")[0];
        String flag_yusuan=codeType.split("-")[1].equals("true")?"1":"0";
        String yusuan="";   // 公式
        String caiwu="";    // 公式
        switch (type) {
            case "1":
                caiwu="资产+成本=负债+权益+损益";
                break;
            case "2":
                caiwu="资产+共同+成本=负债+权益+损益";
                break;
            case "3":
                caiwu="资产=负债+净资产+收入费用";
                break;
            case "4":
                caiwu="资产+支出=负债+净资产+收入";
                break;
            case "5":
                caiwu="占用=来源";
                break;
            case "6":
                caiwu="资产+费用=负债+净资产+收入";
                yusuan="预算支出=预算收入+预算结余";
                break;
            default: ;
                break;
        }
        String finalCaiwu = caiwu;
        String finalYusuan = yusuan;
        return standardRepository.getAccStandardMaxUnique().defaultIfEmpty(1L)
                .flatMap(maxunique->{
                    AccStandard standard=new AccStandard();
                    standard.setUniqueAccStandard(String.valueOf(maxunique))
                            .setAccStandardName(accStandardName)
                            .setFlagYusuan(flag_yusuan)
                            .setNum(Integer.valueOf(String.valueOf(maxunique)))
                            .setAccStyleUnique(type)
                            .setCodeFirst(4)
                            .setYusuan(finalYusuan)
                            .setCaiwu(finalCaiwu)
                            .setLawsTime(new SimpleDateFormat("yyyy").format(new Date())+".1.1")
                            .setCountry("中国");

                    AccTemplate t=new AccTemplate();
                    t.setTName("系统模板")
                            .setTJici("4-2-2-2-2-2-2-2-2-2-2-2-2-2-2-2")
                            .setTType("系统模板")
                            .setUniqueAccStandard(String.valueOf(maxunique))
                            .setTNum(0);

                    return standardRepository.save(standard).zipWith(accTemplateRepository.save(t));
                }).map(o->R.ok().setResult(o));
    }

    @PostMapping("findByUniqueAccStandardDelStandard")
    public Mono<R> findByUniqueAccStandardDelStandard(String uniqueAccStandard,String templateId) {
        return standardRepository.findByUniqueAccStandardDelStandard(uniqueAccStandard)
                .zipWith(accTemplateRepository.delByTypeAndUniqueStandarr("系统模板",uniqueAccStandard))
                .zipWith(groupCodeKemuRepository.delByStandardAndTemplate(uniqueAccStandard,templateId))
                .then(Mono.just(R.ok()));
    }
}
