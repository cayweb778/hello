package org.boozsoft.rest.stock;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.stock.StockWuliu;
import org.boozsoft.domain.entity.stock.StockWuliuSonghuo;
import org.boozsoft.domain.entity.stock.StockWulius;
import org.boozsoft.repo.stock.StockWuLiuRepository;
import org.boozsoft.repo.stock.StockWuLiuSongHuoRepository;
import org.boozsoft.repo.stock.StockWuLiusRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/stock/wuliuSongHuo")
public class StockWuLiuSongHuoController {

    @Autowired
    private StockWuLiuSongHuoRepository songHuoRepository;

    @PostMapping("delByWuliuSongHuoCode")
    public Mono<R> delByWuliuSongHuoCode(String ccode){
        return songHuoRepository.deleteByWuliuCode(ccode).then(Mono.just(R.ok()));
    }

    @PostMapping("findAllByWuliuSongHuoCode")
    public Mono<R> findAllByWuliuSongHuoCode(String ccode){
       return songHuoRepository.findAllByWuliuCode(ccode).collectList().map(a->R.ok().setResult(a));
    }

    @PostMapping("wuliuSongHuoSave")
    @Transactional
    public Mono<R> wuliuSongHuoSave(@RequestBody Map map) {
        String ccode=map.get("ccode").toString();
        String iyear=map.get("iyear").toString();
        List<String> person= Arrays.asList(map.get("person").toString().split(","));

        List<StockWuliuSonghuo> list=new ArrayList<>();
        person.forEach(a->{
            StockWuliuSonghuo obj=new StockWuliuSonghuo();
            obj.setIyear(iyear).setWuliuCode(ccode).setWuliuUser(a);
            list.add(obj);
        });
        return songHuoRepository.saveAll(list).collectList().map(a->R.ok().setResult(a));
    }
}
