package org.boozsoft.rest.origin;

import cn.hutool.extra.pinyin.PinyinUtil;
import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.origin.OriginProjectCategory;
import org.boozsoft.domain.entity.origin.OriginProjectColumn;
import org.boozsoft.domain.vo.OriginProjectCategoryVo;
import org.boozsoft.repo.origin.OriginProjectCategoryRepository;
import org.boozsoft.repo.origin.OriginProjectColumnRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/originProject/category")
public class OriginProjectCategoryController {

    @Autowired
    OriginProjectCategoryRepository projectCategoryRepository;
    @Autowired
    OriginProjectColumnRepository projectColumnRepository;

    @GetMapping("findAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAll(Pageable pageable,String originId) {
        return projectCategoryRepository.findAllByOriginIdOrderByProjectCateCode(originId)
                .collectList().map(R::page);
    }

    @GetMapping("findByFlag")
    public Mono<R> findByFlag(String originId) {
        return projectCategoryRepository.findByFlagAndOriginIdOrderByProjectCateCode("1",originId)
                .collectList()
                .map(item->R.ok().setResult(item));
    }

    @GetMapping("findById")
    @ApiOperation(value = "查询", notes = "传入code")
    public Mono findById(String id) {
        Mono mono = projectCategoryRepository.findById(id);
        return mono.map(item->R.ok().setResult(item));
    }

    @GetMapping("findColumnByCate")
    @ApiOperation(value = "查询", notes = "传入code")
    public Mono<R> findColumnByCate(String projectCateCode,String originId) {
        return projectColumnRepository.findByProjectCateCodeAndOriginIdOrderByNum(projectCateCode,originId)
                .collectList()
                .map(item->R.ok().setResult(item));
    }

    @PostMapping
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono save(@RequestBody final OriginProjectCategoryVo vo) {

        // 大类的逻辑
        Mono mono1 = Mono.fromSupplier(
                () -> {
                    OriginProjectCategory category = new OriginProjectCategory();
                    category.setId(vo.getId());
                    category.setProjectCateCode(vo.getProjectCateCode());
                    category.setProjectCateName(vo.getProjectCateName());
                    if (vo.getFlag()!=null && !vo.getFlag().equals("")) {
                        category.setFlag(vo.getFlag());
                    } else {
                        category.setFlag("1");
                    }
                    category.setProjectTable(vo.getProjectTable());
                    if (category.getProjectTable() == null || category.getProjectTable().equals("")) {
                        category.setProjectTable("project_" + category.getProjectCateCode());
                    }
                    return category;
                })
                .flatMap(projectCategoryRepository::save);

        // 栏目的逻辑
        Mono<List<OriginProjectColumn>> mono2 = Mono.fromSupplier(
                () -> Flux.fromIterable(vo.getTable())
                    .map(item -> {
                        if (item.getCname() != null && !item.getCname().equals("")) {
                            item.setProjectCateCode(vo.getProjectCateCode());
                            if (item.getCtitle()==null || item.getCtitle().equals("")) {
                                item.setCtitle(PinyinUtil.getPinyin(item.getCname(), ""));
                            }
                            if (item.getNum()==null) {
                                item.setNum(vo.getTable().indexOf(item) + 1);
                            }
                            if (item.getSourceType().equals("2")){
                                item.setSourceColumnUnique("unique_code");
                            } else {
                                item.setSourceColumnUnique(null);
                            }
                            switch (item.getCtype()) {
                                case "1":
                                    item.setShuxing("Input");
                                    break;
                                case "2":
                                    item.setShuxing("InputNumber");
                                    break;
                                case "3":
                                    item.setShuxing("InputNumber");
                                    break;
                                case "4":
                                    item.setShuxing("DatePicker");
                                    break;
                                case "5":
                                    item.setShuxing("Checkbox");
                                    break;
                            }
                        }
                        return item;
                    }))
                .flatMapMany(item->item)
                .collectList()
                .flatMapMany(projectColumnRepository::saveAll)
                .collectList();

        // 返回结果
        return mono1.zipWith(mono2).map(o -> R.ok().setResult(o));
    }

    @DeleteMapping
    @ApiOperation(value = "删除", notes = "传入code")
    public Mono delete(@RequestBody final OriginProjectCategory category) {
        return projectCategoryRepository.deleteById(category.getId())
                .zipWith(projectColumnRepository.deleteByProjectCateCodeAndOriginId(category.getProjectCateCode(),category.getOriginId()))
                .then(Mono.just(R.ok()));
    }

    @DeleteMapping("deleteColumn")
    @ApiOperation(value = "删除", notes = "传入code")
    public Mono deleteColumn(String id) {
        return projectColumnRepository.deleteById(id)
                .then(Mono.just(R.ok()));
    }

    @PostMapping("editFlag")
    @ApiOperation(value = "停用启用操作", notes = "传入code")
    public Mono editFlag(@RequestBody OriginProjectCategory category) {
        return projectCategoryRepository.save(category)
                .map(item->R.ok().setResult(item));
    }

    @GetMapping("fromColumnByCate")
    @ApiOperation(value = "查询", notes = "传入code")
    public Mono<R> fromColumnByCate(String projectCateCode,String originId) {
        return projectColumnRepository.findByProjectCateCodeAndIslistAndOriginIdOrderByNum(projectCateCode,"1",originId)
                .collectList()
                .map(item->R.ok().setResult(item));
    }

    @GetMapping("findMaxProjectCateCode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findMaxProjectCateCode(String originId) {
        return projectCategoryRepository.findMaxProjectCateCode(originId)
                .map(item->R.ok().setResult(item));
    }

    @GetMapping("findBukongProjectCateCode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findBukongProjectCateCode(String originId) {
        return projectCategoryRepository.findBukongProjectCateCode(originId)
                .map(item->R.ok().setResult(item));
    }

}
