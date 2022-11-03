package org.boozsoft.rest.group;

import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.group.GroupReportTemplate;
import org.boozsoft.domain.entity.group.GroupReportTemplateColumn;
import org.boozsoft.domain.entity.group.GroupReportTemplateColumnFormula;
import org.boozsoft.repo.group.GroupReportTemplateColumnFormulaRepository;
import org.boozsoft.repo.group.GroupReportTemplateColumnRepository;
import org.boozsoft.repo.group.GroupReportTemplateRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/groupReportTemplate")
public class GroupReportTemplateController {

    @Autowired
    GroupReportTemplateRepository reportTemplateRepository;
    @Autowired
    GroupReportTemplateColumnRepository reportTemplateColumnRepository;
    @Autowired
    GroupReportTemplateColumnFormulaRepository reportTemplateColumnFormulaRepository;

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
    public Mono saveTemplate(@RequestBody final GroupReportTemplate object) {
        //模板表保存
        Mono<GroupReportTemplate> mono1 = reportTemplateRepository.save(object);
        object.getTable().addAll(object.getZcTable());
        object.getTable().addAll(object.getFzTable());

        //模板栏目保存
        Flux<GroupReportTemplateColumn> map = Flux.fromIterable(object.getTable())
                .map(item -> {
                    item.setTemplateId(object.getId());
                    return item;
                });
        Mono<List<GroupReportTemplateColumn>> mono2 = reportTemplateColumnRepository.saveAll(map).collectList();

        //模板栏目公式保存
        Flux<GroupReportTemplateColumnFormula> formulaList = Flux.fromIterable(object.getTable())
                .flatMap(item -> Flux.fromIterable(item.getFormulaTable())
                        .doOnNext(item2 -> item2.setColumnId(item.getId()))
                        .doOnNext(item2 -> item2.setId(null))
                        .collectList().map(item2 -> item)
                )
                .flatMapIterable(GroupReportTemplateColumn::getFormulaTable)
                .doOnNext(formulas -> formulas.setTemplateId(object.getId()));
        Mono<List<GroupReportTemplateColumnFormula>> listMono = reportTemplateColumnFormulaRepository.saveAll(formulaList).collectList();


        return mono1.flatMap(item -> mono2).flatMap(item -> listMono).map(o -> R.ok().setResult(o));
    }

    @PostMapping("saveColumn")
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono saveColumn(@RequestBody GroupReportTemplateColumn object) {
        return reportTemplateColumnRepository.save(object)
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("saveFormula")
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono saveFormula(@RequestBody GroupReportTemplateColumnFormula object) {
        return reportTemplateColumnFormulaRepository.save(object)
                .map(o -> R.ok().setResult(o));
    }

    @DeleteMapping("deleteTemplate")
    @ApiOperation(value = "根据id删除模板", notes = "传入code")
    public Mono deleteTemplate(@RequestBody GroupReportTemplate object) {
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
    public Mono deleteColumn(@RequestBody GroupReportTemplateColumn object) {
        Mono mono1 = reportTemplateColumnRepository.deleteById(object.getId());
        return mono1
                .defaultIfEmpty(object)
                .flatMap(item -> reportTemplateColumnFormulaRepository.deleteByColumnId(object.getId()))
                .then(Mono.just(R.ok()));
    }

    @DeleteMapping("deleteFormula")
    @ApiOperation(value = "根据id删除计算公式", notes = "传入code")
    public Mono deleteFormula(@RequestBody GroupReportTemplateColumnFormula object) {
        Mono mono1 = reportTemplateColumnFormulaRepository.deleteById(object.getId());
        return mono1
                .then(Mono.just(R.ok()));
    }

    @DeleteMapping("deleteFormulaByTemplate")
    @ApiOperation(value = "根据栏目id删除计算公式", notes = "传入code")
    public Mono deleteFormulaByTemplate(@RequestBody GroupReportTemplate object) {
        Mono mono1 = reportTemplateColumnFormulaRepository.deleteByTemplateId(object.getId());
        return mono1
                .then(Mono.just(R.ok()));
    }

    @PostMapping("editFlag")
    @ApiOperation(value = "停用启用操作", notes = "传入code")
    public Mono editFlag(@RequestBody GroupReportTemplate object) {
        if (object.getFlag().equals("1")) {
            object.setFlag("0");
        } else {
            object.setFlag("1");
        }
        return reportTemplateRepository.save(object).map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByAccStandardAndReportName")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByAccStandardAndReportName(String accStandard, String reportName) {
        return reportTemplateRepository.findByAccStandardAndReportNameOrderByNum(accStandard, reportName)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

}
