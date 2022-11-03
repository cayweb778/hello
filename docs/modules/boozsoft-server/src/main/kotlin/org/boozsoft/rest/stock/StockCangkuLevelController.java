package org.boozsoft.rest.stock;

import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.stock.StockCangkuLevel;
import org.boozsoft.domain.entity.stock.StockCangkuLevelName;
import org.boozsoft.domain.vo.findByCangkuLevelVo;
import org.boozsoft.repo.stock.StockCangkuLevelNameRepository;
import org.boozsoft.repo.stock.StockCangkuLevelRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/cangku_level")
public class StockCangkuLevelController {

    @Autowired
    StockCangkuLevelRepository stockCangkuLevelRepository;
    @Autowired
    StockCangkuLevelNameRepository stockCangkuLevelNameRepository;


    @PostMapping("findAll")
    @ApiOperation(value = "查询列表", notes = "传入类型")
    public Mono<R> findAll(){
        String str="仓库,通道,货架,库位";
        // 1、判断级别名称表 是否 0
        return stockCangkuLevelNameRepository.countLevelName()
        .flatMap(level->{
            List<StockCangkuLevelName> list=new ArrayList<>();
            for (int i = 0; i < str.split(",").length; i++) {
                StockCangkuLevelName n=new StockCangkuLevelName();
                n.setLevelName(str.split(",")[i]).setLevelType("1");
                list.add(n);
            }

            // 一条没有,默认增加4条
            return level==0?stockCangkuLevelNameRepository.saveAll(list).collectList()
                    .flatMap(levelName->{
                        // 获取仓库名称ID
                        String name=levelName.stream().filter(item->item.getLevelName().equals("仓库")).collect(Collectors.toList()).get(0).getLevelName();
                        // 增加到级别表中,默认级别1
                        return stockCangkuLevelRepository.save(new StockCangkuLevel().setLevel1(name).setLevel2("").setLevel3("").setLevel4("").setLevel5("").setLevel6("").setLevelOrder("1"))
                                .flatMap(levelItem->{
                                    // 查询级别中
                                    return stockCangkuLevelRepository.findAllOrderLevelOrder().collectList().flatMap(levellist->Mono.just(levellist));
                                });
                    })
                    :
                    stockCangkuLevelRepository.findAllOrderLevelOrder().collectList().flatMap(levellist->Mono.just(levellist));
        })
            .map(a->R.ok().setResult(a));
    }

    @PostMapping("countByLevel2")
    public Mono<R> countByLevel2(String level2){
        return stockCangkuLevelRepository.countByLevel2(level2).map(a->R.ok().setResult(a));
    }
    @PostMapping("countByLevel3")
    public Mono<R> countByLevel3(String level2,String level3){
        return stockCangkuLevelRepository.countByLevel2AndLevel3(level2,level3).map(a->R.ok().setResult(a));
    }
    @PostMapping("countByLevel4")
    public Mono<R> countByLevel4(String level2,String level3,String level4){
        return stockCangkuLevelRepository.countByLevel2AndLevel3AndLevel4(level2,level3,level4).map(a->R.ok().setResult(a));
    }
    @PostMapping("countByLevel5")
    public Mono<R> countByLevel5(String level2,String level3,String level4,String level5){
        return stockCangkuLevelRepository.countByLevel2AndLevel3AndLevel4AndLevel5(level2,level3,level4,level5).map(a->R.ok().setResult(a));
    }
    @PostMapping("countByLevel6")
    public Mono<R> countByLevel6(String level2,String level3,String level4,String level5,String level6){
        return stockCangkuLevelRepository.countByLevel2AndLevel3AndLevel4AndLevel5AndLevel6(level2,level3,level4,level5,level6).map(a->R.ok().setResult(a));
    }

    @PostMapping("cangkuLevelSave")
    public Mono<R> cangkuLevelSave(@RequestBody StockCangkuLevel level){
        return stockCangkuLevelRepository.save(level).map(a->R.ok().setResult(a));
    }
    @PostMapping("delByIdList")
    public Mono<R> delByIdList(@RequestBody Map map){
        List<String>list = Arrays.asList(map.get("id").toString().split(","));
        return stockCangkuLevelRepository.delByIdList(list).then(Mono.just(R.ok()));
    }

    @PostMapping("findByIdLevel")
    public Mono<R> findByIdLevel(String id){
        return stockCangkuLevelRepository.findById(id).flatMap(a->{
            String level="";
            if(StrUtil.isNotBlank(a.getLevel2())){
                level+=a.getLevel2()+"_2,";
            }
            if(StrUtil.isNotBlank(a.getLevel3())){
                level+=a.getLevel3()+"_3,";
            }
            if(StrUtil.isNotBlank(a.getLevel4())){
                level+=a.getLevel4()+"_4,";
            }
            if(StrUtil.isNotBlank(a.getLevel5())){
                level+=a.getLevel5()+"_5,";
            }
            if(StrUtil.isNotBlank(a.getLevel6())){
                level+=a.getLevel6()+"_6,";
            }

            List<findByCangkuLevelVo> list=new LinkedList<>();
            String test=level.substring(0,level.length()-1);
            for (int i = 0; i < test.split(",").length; i++) {
                String temp=test.split(",")[i];
                findByCangkuLevelVo vo=new findByCangkuLevelVo();
                vo.setId(a.getId()).setLevel(temp.split("_")[0]).setLevelOrder(Integer.valueOf(temp.split("_")[1]));
                list.add(vo);
            }
            return Mono.just(list);
        }).map(a->R.ok().setResult(a));
    }

    @PostMapping("findByOneLevel")
    public Mono<R> findByOneLevel(){
        return stockCangkuLevelRepository.findByLevelOrder("1").map(a->R.ok().setResult(a));
    }
    @PostMapping("findByIdLevelPojo")
    public Mono<R> findByIdLevelPojo(String id){
        return stockCangkuLevelRepository.findById(id).map(a->R.ok().setResult(a)).defaultIfEmpty(R.ok().setResult(""));
    }
}
