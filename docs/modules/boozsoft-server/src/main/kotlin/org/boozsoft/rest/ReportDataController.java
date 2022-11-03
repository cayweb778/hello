package org.boozsoft.rest;

import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.share.ReportData;
import org.boozsoft.repo.ReportDataColumnRepository;
import org.boozsoft.repo.ReportDataRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/reportData")
public class ReportDataController {

    @Autowired
    ReportDataRepository reportDataRepository;
    @Autowired
    ReportDataColumnRepository reportDataColumnRepository;

    @GetMapping("findByReportName")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByReportName(String reportName,String iyear) {
        return reportDataRepository.findByReportNameAndIyearOrderByDataCode(reportName,iyear)
                .collectList()
                .map(R::page);
    }

    @GetMapping("findByTemplateId")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByTemplateId(String templateId) {
        return reportDataRepository.findByTemplateIdOrderById(templateId)
                .collectList()
                .map(R::page);
    }

    @GetMapping("findByDataType")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByDataType(String templateId, String dataType,String iyear,String jidu,String iperiod) {
        if (dataType.equals("1")){
            return reportDataRepository.findByTemplateIdAndDataTypeAndIyearAndIperiodOrderById(templateId,dataType,iyear,iperiod)
                    .collectList()
                    .map(o -> R.ok().setResult(o));
        } else if (dataType.equals("2")){
            return reportDataRepository.findByTemplateIdAndDataTypeAndIyearAndJiduOrderById(templateId,dataType,iyear,jidu)
                    .collectList()
                    .map(o -> R.ok().setResult(o));
        }
        return reportDataRepository.findByTemplateIdAndDataTypeAndIyearOrderById(templateId,dataType,iyear)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByColumn")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByColumn(String dataId) {
        return reportDataColumnRepository.findByDataIdOrderByXuhao(dataId)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByDataIdAndColumnType")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByDataIdAndColumnType(String dataId, String columnType) {
        return reportDataColumnRepository.findByDataIdAndColumnTypeOrderByXuhao(dataId, columnType)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping
    @ApiOperation(value = "新增或修改", notes = "传入code")
    @Transactional
    public Mono save(@RequestBody ReportData object) {
        object.getTable().addAll(object.getZcTable());
        object.getTable().addAll(object.getFzTable());
        //保存头部
        Mono mono1 = reportDataRepository.save(object);
        //保存栏目
        Mono mono2 = Flux.fromIterable(object.getTable())
                .map(item -> {
                    item.setDataId(object.getId());
                    return item;
                })
                .collectList()
                .flatMapMany(reportDataColumnRepository::saveAll)
                .collectList();
        return mono1
                .flatMap(item -> mono2)
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("saveReportData")
    @ApiOperation(value = "新增或修改", notes = "传入code")
    @Transactional
    public Mono saveReportData(@RequestBody ReportData object) {
        return reportDataRepository.save(object)
                .map(o -> R.ok().setResult(o));
    }

    @DeleteMapping("deleteData")
    @ApiOperation(value = "根据id删除数据", notes = "传入code")
    public Mono deleteData(String id){
        Mono mono = reportDataRepository.deleteById(id);
        return mono
                .defaultIfEmpty(id)
                .flatMap(item -> reportDataColumnRepository.deleteByDataId(id))
                .then(Mono.just(R.ok()));
    }

    @DeleteMapping("deleteColumn")
    @ApiOperation(value = "根据dataId删除数据", notes = "传入code")
    public Mono deleteColumn(String dataId){
        return reportDataColumnRepository.deleteByDataId(dataId)
                .then(Mono.just(R.ok()));
    }

}
