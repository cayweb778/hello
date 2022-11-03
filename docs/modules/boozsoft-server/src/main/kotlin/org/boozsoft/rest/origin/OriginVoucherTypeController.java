package org.boozsoft.rest.origin;

import cn.hutool.core.util.IdUtil;
import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.origin.OriginSysVoucherType;
import org.boozsoft.repo.origin.OriginSysVoucherTypeRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/originVoucherType")
public class OriginVoucherTypeController {

    @Autowired
    OriginSysVoucherTypeRepository voucherTypeRepository;

    @GetMapping("findAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAll(Pageable pageable){
        return voucherTypeRepository.findAllByOrderById().collectList().map(R::page);
    }

    @GetMapping("findByNum")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByNum(String voucherNum){
        return voucherTypeRepository.findByVoucherNumOrderById(voucherNum).map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByCode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByCode(String voucherTypeCode){
        return voucherTypeRepository.findByVoucherTypeCodeOrderById(voucherTypeCode).map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByName")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByName(String voucherTypeName){
        return voucherTypeRepository.findByVoucherTypeNameOrderById(voucherTypeName).map(o -> R.ok().setResult(o));
    }

    @PostMapping
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono save(@RequestBody OriginSysVoucherType object){
        if (object.getVoucherNum()==null || object.getVoucherNum().equals("")) {
            object.setVoucherNum(IdUtil.fastUUID());
        }
        return voucherTypeRepository.save(object)
                .map(o-> R.ok().setResult(o));
    }

    @DeleteMapping
    @ApiOperation(value = "删除", notes = "传入code")
    public Mono delete(@RequestBody OriginSysVoucherType object){
        return voucherTypeRepository.deleteById(object.getId())
                .then(Mono.just(R.ok()));
    }

    @PostMapping("excel")
    @ApiOperation(value = "前台解析excel,后台直接保存集合", notes = "传入code")
    @Transactional
    public Mono excel(@RequestBody List<OriginSysVoucherType> object){
        return voucherTypeRepository.saveAll(object)
                .collectList()
                .map(o-> R.ok().setResult(o));
    }

}
