package org.boozsoft.rest.group;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.group.GroupSysZfClass;
import org.boozsoft.domain.vo.group.GroupExpenditureClassVo;
import org.boozsoft.domain.vo.group.GroupFaZheJiuMethodVo;
import org.boozsoft.repo.group.GroupFaZhejiuMethodRepository;
import org.boozsoft.repo.group.GroupSysZfClassRepository;
import org.boozsoft.util.TreeUtils;
import org.boozsoft.utils.CollectOfUtils;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/group/faZhejiuMethod")
public class GroupFaZhejiuMethodController {

    @Autowired
    private GroupFaZhejiuMethodRepository groupExpenditureClassRepository;

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

}
