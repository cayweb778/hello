package org.boozsoft.rest.group;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.fa.FaPandianMaster;
import org.boozsoft.domain.entity.group.GroupSysAccount;
import org.boozsoft.domain.entity.group.GroupSysOrg;
import org.boozsoft.domain.entity.group.GroupSysPlant;
import org.boozsoft.repo.GroupSysAccountRepository;
import org.boozsoft.repo.SysAccountRepository;
import org.boozsoft.repo.group.GroupSysOrgRepository;
import org.boozsoft.repo.group.GroupSysPlantRepository;
import org.boozsoft.util.FtpUtil;
import org.boozsoft.util.ReflectionUtil;
import org.boozsoft.utils.CollectOfUtils;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/group/sys-plant")
public class GroupSysPlantController {
    @Autowired
    GroupSysPlantRepository groupSysPlantRepository;

    @GetMapping
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findPageList(Pageable pageable, String queryMark) {
        return groupSysPlantRepository.findAll().filter(it->it.getIsDel().equals("0")).filter(it -> ((null == queryMark || queryMark.equals("all"))) || it.getStatus().equals(queryMark))
                /*.flatMap(item -> {
            if (StrUtil.isNotBlank(item.getBeiyong1())) {
                item.setBeiyong2(item.getBeiyong1());
                item.setBeiyong1(FtpUtil.ImgToBase64("/ncpz/group", item.getBeiyong1()));
            }
            return Mono.just(item);
        })*/.collectList().map(list -> list.stream().sorted(Comparator.comparing(GroupSysPlant::getPlantCode)).collect(Collectors.toList())).flatMap(item -> groupSysPlantRepository.countAllBy().map(total -> R.page(item, pageable, total)));
    }

    private List<GroupSysPlant> queryFilter(List<GroupSysPlant> list, Map map) {
        if (list.size() > 0) {
            Map<String, String> searchConditon = (HashMap<String, String>) map.get("searchConditon");
            list = list.stream().filter(item -> {
                if (StrUtil.isNotBlank(searchConditon.get("requirement")) && StrUtil.isNotBlank(searchConditon.get("value"))) {
                    Object dbValue = ReflectionUtil.getValue(item, searchConditon.get("requirement").trim());
                    if (null != dbValue && !dbValue.toString().contains(searchConditon.get("value").trim()))
                        return false;
                }
                return true;
            }).collect(Collectors.toList());
        }
        return list;
    }

    @GetMapping("all")
    @ApiOperation(value = "查询所有", notes = "传入code")
    public Mono<R> findByAll() {
        return groupSysPlantRepository.findAll().filter(it -> it.getIsDel().equals("0")).filter(it -> it.getStatus().equals("1")).collectList().map(R::ok);
    }
    @GetMapping("allDel")
    @ApiOperation(value = "查询所有", notes = "传入code")
    public Mono<R> allDel() {
        return groupSysPlantRepository.findAll().filter(it -> it.getIsDel().equals("1")).filter(it -> it.getStatus().equals("1")).collectList().map(R::ok);
    }

    @PostMapping
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono save(@RequestBody GroupSysPlant entity) {
        // 存储
        if (null == entity.getId()) {
        }
        entity.setBeiyong2(null);
        return groupSysPlantRepository.save(entity).map(item -> R.ok().setResult(item));
    }

    @DeleteMapping
    @Transactional
    @ApiOperation(value = "删除", notes = "传入code")
    public Mono delete(@RequestBody GroupSysPlant entity) {
        return groupSysPlantRepository.findById(entity.getId()).map(it -> {
            it.setIsDel("1");
            it.setDelName(entity.getDelName());
            it.setDelDate(DateUtil.now());
            return it;
        }).flatMap(e -> groupSysPlantRepository.save(e).map(o -> R.ok()));
    }

    @DeleteMapping("true")
    @Transactional
    @ApiOperation(value = "删除", notes = "传入code")
    public Mono business(@RequestBody GroupSysPlant entity) {
        return groupSysPlantRepository.deleteById(entity.getId()).thenReturn(R.ok());
    }

    @PostMapping("reduction")
    public Mono<R> reduction(@RequestBody GroupSysOrg entity) {
        return groupSysPlantRepository.findById(entity.getId()).map(it -> {
            it.setIsDel("0");
            it.setDelName(null);
            it.setDelDate(null);
            return it;
        }).flatMap(e -> groupSysPlantRepository.save(e).map(o -> R.ok()));
    }


    @GetMapping("findByCode")
    @ApiOperation(value = "查询编码重复", notes = "传入code")
    public Mono findByCode(String id, String checkVal) {
        return groupSysPlantRepository.findFirstByPlantCode(checkVal).collectList().map(list -> {
            if (StrUtil.isNotBlank(id) && list.size() > 0 && list.get(0).getId().equals(id)) {
                return R.ok().setResult(0);
            } else {
                return R.ok().setResult(list.size());
            }
        });
    }

    @GetMapping("findByName")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono findByName(String id, String checkVal) {
        return groupSysPlantRepository.findFirstByPlantNamej(checkVal).collectList().map(list -> {
            if (StrUtil.isNotBlank(id) && list.size() > 0 && list.get(0).getId().equals(id)) {
                return R.ok().setResult(0);
            } else {
                return R.ok().setResult(list.size());
            }
        });
    }

    @PostMapping("findById")
    public Mono findById(String id) {
        return groupSysPlantRepository.findById(id).map(list -> R.ok().setResult(list));
    }

    @PostMapping("findByUniqueCode")
    public Mono findByUniqueCode(String uniqueCode) {
        return groupSysPlantRepository.findAllByCorpUniqueCode(uniqueCode).filter(it->it.getIsDel().equals("0")).collectList().map(list -> R.ok().setResult(list));
    }
    @PostMapping("findByCorpUniqueCode")
    public Mono findByCorpUniqueCode(String corpUniqueCode) {
        return groupSysPlantRepository.findByCorpUniqueCode(corpUniqueCode).filter(it->it.getIsDel().equals("0")).collectList().map(list -> R.ok().setResult(list)).defaultIfEmpty(R.ok().setResult("不存在"));
    }

}
