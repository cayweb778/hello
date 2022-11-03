package org.boozsoft.rest;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.pinyin.PinyinUtil;
import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.SysLog;
import org.boozsoft.domain.entity.account.ProjectCategory;
import org.boozsoft.domain.entity.account.ProjectCategoryRollback;
import org.boozsoft.domain.entity.account.ProjectColumn;
import org.boozsoft.domain.vo.ProjectCategoryVo;
import org.boozsoft.repo.ProjectCategoryRepository;
import org.boozsoft.repo.ProjectCategoryRollbackRepository;
import org.boozsoft.repo.ProjectColumnRepository;
import org.boozsoft.repo.SysLogRepository;
import org.springbooz.core.tool.result.R;

import org.springbooz.datasource.r2dbc.annotation.SCHEMA_TYPE;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.data.domain.Pageable;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/project/category")
public class ProjectCategoryController {

//    @Autowired
//    OAuth2ClientProperties oAuth2ClientProperties;
    @Autowired
    ProjectCategoryRepository projectCategoryRepository;
    @Autowired
    ProjectColumnRepository projectColumnRepository;
    @Autowired
    DatabaseClient client;

    @GetMapping("findAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAll(Pageable pageable, String flag) {
        return projectCategoryRepository.findAllByOrderByProjectCateCode()
                .collectList()
                .map(list -> {
                    if (StrUtil.isNotBlank(flag)) {
                        return list.stream().filter(item -> flag.contains(item.getFlag())).collect(Collectors.toList());
                    }
                    return list;
                })
                .map(R::page);
//        return projectCategoryRepository.findAllByApplyDatabaseUniqueCodeAndApplyUserAndSuccessState("abc001", "test", "0")
//                .collectList().map(R::page);
    }

    @GetMapping("findAllList")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAllList() {
        return projectCategoryRepository.findAllByOrderByProjectCateCode()
                .collectList()
                .map(item->R.ok().setResult(item));
    }

    @GetMapping("findMaxProjectCateCode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findMaxProjectCateCode() {
        return projectCategoryRepository.findMaxProjectCateCode()
                .map(item->R.ok().setResult(item));
    }

    @GetMapping("findBukongProjectCateCode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findBukongProjectCateCode() {
        return projectCategoryRepository.findBukongProjectCateCode()
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

    public Mono<R> findColumnByCate(String projectCateCode) {
        return projectColumnRepository.findByProjectCateCodeOrderByNum(projectCateCode)
                .collectList()
                .map(item->R.ok().setResult(item));
    }

    @PostMapping
    @ApiOperation(value = "新增或修改", notes = "传入code")

    public Mono save(@RequestBody final ProjectCategoryVo vo) {

        // 大类的逻辑
        Mono mono1 = Mono.fromSupplier(
                () -> {
                    ProjectCategory category = new ProjectCategory();
                    category.setId(vo.getId());
                    category.setProjectCateCode(vo.getProjectCateCode());
                    category.setProjectCateName(vo.getProjectCateName());
                    if (vo.getFlag()!=null && !vo.getFlag().equals("")) {
                        category.setFlag(vo.getFlag());
                    } else {
                        category.setFlag("1");
                    }
                    if (vo.getSuccessState()!=null && !vo.getSuccessState().equals("")) {
                        category.setSuccessState(vo.getSuccessState());
                    } else {
                        category.setSuccessState("1");
                    }
                    category.setProjectTable(vo.getProjectTable());
                    if (category.getProjectTable() == null || category.getProjectTable().equals("")) {
                        category.setProjectTable("project_" + category.getProjectCateCode());
                    }
                    return category;
                })
                .flatMap(projectCategoryRepository::save);

        // 栏目的逻辑
        Mono<List<ProjectColumn>> mono2 = Mono.fromSupplier(
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
                            if (vo.getSuccessState()!=null && !vo.getSuccessState().equals("")) {
                                item.setSuccessState(vo.getSuccessState());
                            } else {
                                item.setSuccessState("1");
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

    public Mono delete(@RequestBody final ProjectCategory category) {
        return projectCategoryRepository.deleteById(category.getId())
                .zipWith(projectColumnRepository.deleteByProjectCateCode(category.getProjectCateCode()))
                .then(Mono.just(R.ok()));
    }

    @DeleteMapping("deleteColumn")
    @ApiOperation(value = "删除", notes = "传入code")

    public Mono deleteColumn(String id) {
        return projectColumnRepository.deleteById(id).then(Mono.just(R.ok()));
    }

    @GetMapping("findBySuccessStateAndFlag")

    public Mono<R> findBySuccessStateAndFlag() {
        return projectCategoryRepository.findBySuccessStateAndFlagOrderByProjectCateCode("1","1")
                .collectList()
                .map(item->R.ok().setResult(item));
    }

    @PostMapping("editFlag")
    @ApiOperation(value = "停用启用操作", notes = "传入code")

    public Mono editFlag(@RequestBody ProjectCategory category) {
        if (category.getFlag().equals("1")){
            category.setFlag("0");
        } else {
            category.setFlag("1");
        }
        return projectCategoryRepository.save(category).map(item -> R.ok().setResult(item));
    }

    @GetMapping("findByCode")
    public Mono<R> findByCode(String projectCateCode) {
        return projectCategoryRepository.findByProjectCateCodeOrderById(projectCateCode)
                .collectList()
                .map(item -> R.ok().setResult(item));
    }

    @GetMapping("findByName")
    public Mono<R> findByName(String projectCateName) {
        return projectCategoryRepository.findByProjectCateNameOrderById(projectCateName)
                .collectList()
                .map(item -> R.ok().setResult(item));
    }

}
