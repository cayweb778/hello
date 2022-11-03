package org.boozsoft.rest;

import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.share.ReportTemplate;
import org.boozsoft.domain.entity.share.ReportTemplateColumn;
import org.boozsoft.domain.entity.share.ReportTemplateColumnFormula;
import org.boozsoft.repo.ReportTemplateColumnFormulaRepository;
import org.boozsoft.repo.ReportTemplateColumnRepository;
import org.boozsoft.repo.ReportTemplateRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/reportTemplate")
public class ReportTemplateController {

    @Autowired
    ReportTemplateRepository reportTemplateRepository;
    @Autowired
    ReportTemplateColumnRepository reportTemplateColumnRepository;
    @Autowired
    ReportTemplateColumnFormulaRepository reportTemplateColumnFormulaRepository;

    @GetMapping("findByReportName")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByReportName(String reportName) {
        return reportTemplateRepository.findByReportNameOrderByNum(reportName)
                .collectList()
                .map(R::page);
    }

    @GetMapping("findByReportNameAndFlag")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByReportNameAndFlag(String reportName) {
        return reportTemplateRepository.findByReportNameAndFlagOrderByNum(reportName, "1")
                .collectList()
                .map(R::page);
    }

    @GetMapping("findByTemplateName")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByTemplateName(String templateName) {
        return reportTemplateRepository.findByTemplateNameOrderByNum(templateName)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByTitleName")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByTitleName(String reportName,String titleName) {
        return reportTemplateRepository.findByReportNameAndTitleNameOrderByNum(reportName,titleName)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByNum")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByNum(Integer num) {
        return reportTemplateRepository.findByNumOrderByNum(num)
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
    public Mono saveTemplate(@RequestBody final ReportTemplate object) {
        if (object.getSysFlag() == null || object.equals("")) {
            object.setSysFlag("0");
        }
        //模板表保存
        Mono<ReportTemplate> mono1 = reportTemplateRepository.save(object);
        object.getTable().addAll(object.getZcTable());
        object.getTable().addAll(object.getFzTable());

        //模板栏目保存
        Flux<ReportTemplateColumn> map = Flux.fromIterable(object.getTable())
                .map(item -> {
                    item.setTemplateId(object.getId());
                    return item;
                });
        Mono<List<ReportTemplateColumn>> mono2 = reportTemplateColumnRepository.saveAll(map).collectList();

        //模板栏目公式保存
        Flux<ReportTemplateColumnFormula> formulaList = Flux.fromIterable(object.getTable())
                .flatMap(item -> Flux.fromIterable(item.getFormulaTable())
                        .doOnNext(item2 -> item2.setColumnId(item.getId()))
                        .doOnNext(item2 -> item2.setId(null))
                        .collectList().map(item2 -> item)
                )
                .flatMapIterable(ReportTemplateColumn::getFormulaTable)
                .doOnNext(formulas -> formulas.setTemplateId(object.getId()));
        Mono<List<ReportTemplateColumnFormula>> listMono = reportTemplateColumnFormulaRepository.saveAll(formulaList).collectList();


        return mono1.flatMap(item -> mono2).flatMap(item -> listMono).map(o -> R.ok().setResult(o));
    }

    @PostMapping("saveColumn")
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono saveColumn(@RequestBody ReportTemplateColumn object) {
        return reportTemplateColumnRepository.save(object)
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("saveFormula")
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono saveFormula(@RequestBody ReportTemplateColumnFormula object) {
        return reportTemplateColumnFormulaRepository.save(object)
                .map(o -> R.ok().setResult(o));
    }

    @DeleteMapping("deleteTemplate")
    @ApiOperation(value = "根据id删除模板", notes = "传入code")
    public Mono deleteTemplate(@RequestBody ReportTemplate object) {
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
    public Mono deleteColumn(@RequestBody ReportTemplateColumn object) {
        Mono mono1 = reportTemplateColumnRepository.deleteById(object.getId());
        return mono1
                .defaultIfEmpty(object)
                .flatMap(item -> reportTemplateColumnFormulaRepository.deleteByColumnId(object.getId()))
                .then(Mono.just(R.ok()));
    }

    @DeleteMapping("deleteFormula")
    @ApiOperation(value = "根据id删除计算公式", notes = "传入code")
    public Mono deleteFormula(@RequestBody ReportTemplateColumnFormula object) {
        Mono mono1 = reportTemplateColumnFormulaRepository.deleteById(object.getId());
        return mono1
                .then(Mono.just(R.ok()));
    }

    @DeleteMapping("deleteFormulaByTemplate")
    @ApiOperation(value = "根据栏目id删除计算公式", notes = "传入code")
    public Mono deleteFormulaByTemplate(@RequestBody ReportTemplate object) {
        Mono mono1 = reportTemplateColumnFormulaRepository.deleteByTemplateId(object.getId());
        return mono1
                .then(Mono.just(R.ok()));
    }

    @PostMapping("editFlag")
    @ApiOperation(value = "停用启用操作", notes = "传入code")
    public Mono editFlag(@RequestBody ReportTemplate object) {
        if (object.getFlag().equals("1")){
            object.setFlag("0");
        } else {
            object.setFlag("1");
        }
        return reportTemplateRepository.save(object).map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByAccvoucherOrderAndTemplateId")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByAccvoucherOrderAndTemplateId(String templateId, String iyperiod1, String iyperiod2, String jizhang) {
        if (jizhang.equals("1") || jizhang.equals("true")) {
            return reportTemplateColumnFormulaRepository.findByAccvoucherOrderAndTemplateId(templateId, iyperiod1, iyperiod2)
                    .collectList()
                    .map(o -> R.ok().setResult(o));
        }
        return reportTemplateColumnFormulaRepository.findByAccvoucherOrderAndTemplateIdAndIbook(templateId, iyperiod1, iyperiod2)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findTemplateBySysFlag")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findTemplateBySysFlag(String sysFlag) {
        return reportTemplateRepository.findBySysFlagOrderByNum(sysFlag)
                .collectList()
                .map(R::page);
    }

    @GetMapping("findByCashOrderAndTemplateId")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByCashOrderAndTemplateId(String templateId, String iyear, String iperiod1, String iperiod2) {
        return reportTemplateColumnFormulaRepository.findByCashOrderAndTemplateId(templateId, iyear, iperiod1, iperiod2)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

}
