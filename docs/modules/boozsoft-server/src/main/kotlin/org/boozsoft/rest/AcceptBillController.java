package org.boozsoft.rest;

import cn.hutool.core.util.IdUtil;
import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.account.AcceptBill;
import org.boozsoft.repo.AcceptBillRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/acceptBill")
public class AcceptBillController {

    @Autowired
    AcceptBillRepository acceptBillRepository;

    @GetMapping("findAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAll() {
        return acceptBillRepository.findAllByOrderByDaoqiDate()
                .collectList()
                .map(R::page);
    }

    @GetMapping("findByQichu")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByQichu() {
        return acceptBillRepository.findAllByQichuOrderByDaoqiDate("1")
                .collectList()
                .map(R::page);
    }

    @GetMapping("findByBillCode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByBillCode(String billCode) {
        return acceptBillRepository.findByBillCodeOrderByDaoqiDate(billCode)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono save(@RequestBody AcceptBill object){
        if (object.getUniqueCode()==null || object.getUniqueCode().equals("")){
            object.setUniqueCode(IdUtil.objectId());
        }
        if (object.getReceiveDate()!=null && !object.getReceiveDate().equals("")){
            object.setReceiveDate(object.getReceiveDate().substring(0,10));
        }
        if (object.getChupiaoDate()!=null && !object.getChupiaoDate().equals("")){
            object.setChupiaoDate(object.getChupiaoDate().substring(0,10));
        }
        if (object.getDaoqiDate()!=null && !object.getDaoqiDate().equals("")){
            object.setDaoqiDate(object.getDaoqiDate().substring(0,10));
        }
        return acceptBillRepository.save(object)
                .map(o-> R.ok().setResult(o));
    }

    @DeleteMapping
    @ApiOperation(value = "删除", notes = "传入code")
    public Mono delete(@RequestBody String id){
        return acceptBillRepository.deleteById(id)
                .map(o-> R.ok().setResult(o));
    }

    @GetMapping("findByDaoqiDate")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByDaoqiDate() {
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        return acceptBillRepository.findByDaoqiDate(date)
                .collectList()
                .map(R::page);
    }

}
