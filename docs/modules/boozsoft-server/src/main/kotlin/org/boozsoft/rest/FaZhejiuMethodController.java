package org.boozsoft.rest;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.FaLocation;
import org.boozsoft.domain.entity.FaZhejiuMethod;
import org.boozsoft.domain.vo.group.GroupFaZheJiuMethodVo;
import org.boozsoft.repo.FaZhejiuMethodRepository;
import org.boozsoft.repo.group.GroupFaZhejiuMethodRepository;
import org.boozsoft.util.TreeUtils;
import org.boozsoft.utils.CollectOfUtils;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sys/faZhejiuMethod")
public class FaZhejiuMethodController {

    @Autowired
    private FaZhejiuMethodRepository groupExpenditureClassRepository;

    @GetMapping("treeDept")
    public Mono<Map> treeDept(String id, String flag) {
        if (StrUtil.isNotBlank(id) && !id.equals("0")) {
            return groupExpenditureClassRepository.findByIdOrderByDeptCode(id)
                    .collectList()
//                    .map(list->TreeUtils.castTreeList(list,GroupExpenditureClassVo.class))
                    .map(list -> {
                        if (StrUtil.isNotBlank(flag)) {
                            return list.stream().filter(item -> flag.contains(item.getFlag())).collect(Collectors.toList());
                        }
                        return list;
                    })
                    .map(list -> CollectOfUtils.mapof(
                            "code", 0,
                            "message", "ok",
                            "type", "success",
                            "result", list
                    ));
        } else {
            return groupExpenditureClassRepository.findAllByOrderByCreateDate()
                    .collectList()
                    .map(list -> TreeUtils.castTreeList(list, GroupFaZheJiuMethodVo.class))
                    .map(list -> {
                        if (StrUtil.isNotBlank(flag)) {
                            return list.stream().filter(item -> flag.contains(item.getFlag())).collect(Collectors.toList());
                        }
                        return list;
                    })
                    .map(list -> CollectOfUtils.mapof(
                            "code", 0,
                            "message", "ok",
                            "type", "success",
                            "result", list
                    ));
        }
    }

    @GetMapping("treeDeptByFlag")
    public Mono<Map> treeDeptByFlag() {
        return groupExpenditureClassRepository.findByFlagOrderByCreateDate("1")
                .collectList()
                .map(list->TreeUtils.castTreeList(list, GroupFaZheJiuMethodVo.class))
                .map(list -> CollectOfUtils.mapof(
                        "code", 0,
                        "message", "ok",
                        "type", "success",
                        "result", list
                ));
    }

    @GetMapping("treeDeptByIsEnd")
    public Mono<Map> treeDeptByIsEnd() {
        return groupExpenditureClassRepository.findByFlagOrderByCreateDate("1")
                .collectList()
                .map(list->TreeUtils.castTreeList(list, GroupFaZheJiuMethodVo.class))
                .map(list -> CollectOfUtils.mapof(
                        "code", 0,
                        "message", "ok",
                        "type", "success",
                        "result", list
                ));
    }


    @GetMapping("findAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAll(Pageable pageable){
        return groupExpenditureClassRepository.findAllByOrderByCreateDate().collectList().map(R::page);
    }

    @PostMapping("excel")
    @ApiOperation(value = "前台解析excel,后台直接保存集合", notes = "传入code")
    @Transactional
    public Mono excel(@RequestBody List<FaZhejiuMethod> object){
        object.stream().forEach(v->{
            v.setUniqueCode(IdUtil.objectId());
            v.setParentId("0");
        });
        return groupExpenditureClassRepository.saveAll(object)
                .collectList()
                .map(o-> R.ok().setResult(o));
    }

    @GetMapping("/getTotalData")
    @ApiOperation(value = "获取所有数据总条数", notes = "获取所有数据总条数")
    public Mono getTotalData(){
        return groupExpenditureClassRepository.findAll()
                .collectList()
                .map(v-> v.size())
                .map(v-> R.ok().setResult(v));
    }

}
