package org.boozsoft.rest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.FaLocation;
import org.boozsoft.domain.entity.SysMultiSet;
import org.boozsoft.domain.entity.codekemu.CodeKemu;
import org.boozsoft.repo.SettMultiSetRepository;
import org.boozsoft.repo.codekemu.CodeKemuRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;


/**
 * 多栏目设置
 *
 * @author lz
 */
@Slf4j
@RestController
@RequestMapping("/settMultiSet")
@Api(value = "多栏目设置", tags = "多栏目设置")
public class SysMultiSetController {

    @Autowired
    SettMultiSetRepository settMultiSetRepository;
    @Autowired
    CodeKemuRepository codeKemuRepository;


    @PostMapping("save")
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono save(@RequestBody Map maps){

        String km = maps.get("km").toString();
        String list = JSONObject.toJSONString(maps.get("list"));
        List<SysMultiSet> list1 = JSONObject.parseArray(list, SysMultiSet.class);
        list1.forEach(v->{
            v.setKm(km);
        });

        //先删除该科目下所有数据 在新增

        return settMultiSetRepository.deleteByKm(km).thenReturn(1)
                .zipWith(settMultiSetRepository.saveAll(list1).collectList())
                .thenReturn(R.ok());
    }


    @GetMapping("/loadKm/{km}")
    public Mono loadKm(@PathVariable String km){
        return settMultiSetRepository.findAllByKm(km).collectList()
                .map(o-> R.ok().setResult(o));
    }

    @GetMapping("/findCodeByKm/{km}")
    public Mono findCodeByKm(@PathVariable String km){
        return codeKemuRepository.findAllBySuperiorCcodeOrderByCcode(km).collectList()
                .map(o-> R.ok().setResult(o));
    }
}


