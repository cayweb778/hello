package org.boozsoft.rest.origin;

import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.origin.OriginReportTemplate;
import org.boozsoft.domain.entity.origin.OriginReportTemplateColumn;
import org.boozsoft.domain.entity.origin.OriginReportTemplateColumnFormula;
import org.boozsoft.repo.origin.OriginReportTemplateColumnFormulaRepository;
import org.boozsoft.repo.origin.OriginReportTemplateColumnRepository;
import org.boozsoft.repo.origin.OriginReportTemplateRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/originReportTemplate")
public class OriginReportTemplateController {

    @Autowired
    OriginReportTemplateRepository reportTemplateRepository;
    @Autowired
    OriginReportTemplateColumnRepository reportTemplateColumnRepository;
    @Autowired
    OriginReportTemplateColumnFormulaRepository reportTemplateColumnFormulaRepository;

    @GetMapping("findByReportName")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByReportName(String reportName,String originId) {
        return reportTemplateRepository.findByReportNameAndOriginIdOrderByNum(reportName,originId)
                .collectList()
                .map(R::page);
    }

    @GetMapping("findByReportNameAndFlag")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByReportNameAndFlag(String reportName,String originId) {
        return reportTemplateRepository.findByReportNameAndFlagAndOriginIdOrderByNum(reportName, "1",originId)
                .collectList()
                .map(R::page);
    }

    @GetMapping("findByTemplateName")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByTemplateName(String templateName,String originId) {
        return reportTemplateRepository.findByTemplateNameAndOriginIdOrderByNum(templateName,originId)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByTitleName")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByTitleName(String reportName,String titleName,String originId) {
        return reportTemplateRepository.findByReportNameAndTitleNameAndOriginIdOrderByNum(reportName,titleName,originId)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByNum")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByNum(Integer num,String originId) {
        return reportTemplateRepository.findByNumAndOriginIdOrderByNum(num,originId)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByColumn")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByColumn(String templateId) {
        return reportTemplateColumnRepository.findByTemplateIdOrderByXuhao(templateId)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByColumnAndType")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByColumnAndType(String templateId, String columnType) {
        return reportTemplateColumnRepository.findByTemplateIdAndColumnTypeOrderByXuhao(templateId, columnType)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByFormula")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByFormula(String columnId) {
        return reportTemplateColumnFormulaRepository.findByColumnIdOrderById(columnId)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findFormulaByTemplate")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findFormulaByTemplate(String templateId) {
        return reportTemplateColumnFormulaRepository.findByTemplateIdOrderById(templateId)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findById")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findById(String id) {
        return reportTemplateRepository.findById(id)
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("saveTemplate")
    @ApiOperation(value = "新增或修改", notes = "传入code")
    @Transactional
    public Mono saveTemplate(@RequestBody final OriginReportTemplate object) {
        //模板表保存
        Mono<OriginReportTemplate> mono1 = reportTemplateRepository.save(object);
        object.getTable().addAll(object.getZcTable());
        object.getTable().addAll(object.getFzTable());

        //模板栏目保存
        Flux<OriginReportTemplateColumn> map = Flux.fromIterable(object.getTable())
                .map(item -> {
                    item.setTemplateId(object.getId());
                    item.setOriginId(object.getOriginId());
                    return item;
                });
        Mono<List<OriginReportTemplateColumn>> mono2 = reportTemplateColumnRepository.saveAll(map).collectList();

        //模板栏目公式保存
        Flux<OriginReportTemplateColumnFormula> formulaList = Flux.fromIterable(object.getTable())
                .flatMap(item -> Flux.fromIterable(item.getFormulaTable())
                        .doOnNext(item2 -> item2.setColumnId(item.getId()))
                        .doOnNext(item2 -> item2.setId(null))
                        .collectList().map(item2 -> item)
                )
                .flatMapIterable(OriginReportTemplateColumn::getFormulaTable)
                .doOnNext(formulas -> formulas.setTemplateId(object.getId()))
                .doOnNext(formulas -> formulas.setOriginId(object.getOriginId()));
        Mono<List<OriginReportTemplateColumnFormula>> listMono = reportTemplateColumnFormulaRepository.saveAll(formulaList).collectList();


        return mono1.flatMap(item -> mono2).flatMap(item -> listMono).map(o -> R.ok().setResult(o));
    }

    @PostMapping("saveColumn")
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono saveColumn(@RequestBody OriginReportTemplateColumn object) {
        return reportTemplateColumnRepository.save(object)
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("saveFormula")
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono saveFormula(@RequestBody OriginReportTemplateColumnFormula object) {
        return reportTemplateColumnFormulaRepository.save(object)
                .map(o -> R.ok().setResult(o));
    }

    @DeleteMapping("deleteTemplate")
    @ApiOperation(value = "根据id删除模板", notes = "传入code")
    public Mono deleteTemplate(@RequestBody OriginReportTemplate object) {
        Mono mono1 = reportTemplateRepository.deleteById(object.getId());
        return mono1
                .defaultIfEmpty(object)
                .flatMap(item -> reportTemplateColumnRepository.deleteByTemplateId(object.getId()))
                .defaultIfEmpty(object)
                .flatMap(item -> reportTemplateColumnFormulaRepository.deleteByTemplateId(object.getId()))
                .then(Mono.just(R.ok()));
    }

    @DeleteMapping("deleteColumn")
    @ApiOperation(value = "根据id删除栏目", notes = "传入code")
    public Mono deleteColumn(@RequestBody OriginReportTemplateColumn object) {
        Mono mono1 = reportTemplateColumnRepository.deleteById(object.getId());
        return mono1
                .defaultIfEmpty(object)
                .flatMap(item -> reportTemplateColumnFormulaRepository.deleteByColumnId(object.getId()))
                .then(Mono.just(R.ok()));
    }

    @DeleteMapping("deleteFormula")
    @ApiOperation(value = "根据id删除计算公式", notes = "传入code")
    public Mono deleteFormula(@RequestBody OriginReportTemplateColumnFormula object) {
        Mono mono1 = reportTemplateColumnFormulaRepository.deleteById(object.getId());
        return mono1
                .then(Mono.just(R.ok()));
    }

    @DeleteMapping("deleteFormulaByTemplate")
    @ApiOperation(value = "根据栏目id删除计算公式", notes = "传入code")
    public Mono deleteFormulaByTemplate(@RequestBody OriginReportTemplate object) {
        Mono mono1 = reportTemplateColumnFormulaRepository.deleteByTemplateId(object.getId());
        return mono1
                .then(Mono.just(R.ok()));
    }

    @PostMapping("editFlag")
    @ApiOperation(value = "停用启用操作", notes = "传入code")
    public Mono editFlag(@RequestBody OriginReportTemplate object) {
        if (object.getFlag().equals("1")) {
            object.setFlag("0");
        } else {
            object.setFlag("1");
        }
        return reportTemplateRepository.save(object).map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByAccStandardAndReportName")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByAccStandardAndReportName(String accStandard, String reportName,String originId) {
        return reportTemplateRepository.findByAccStandardAndReportNameAndOriginIdOrderByNum(accStandard, reportName,originId)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

}
