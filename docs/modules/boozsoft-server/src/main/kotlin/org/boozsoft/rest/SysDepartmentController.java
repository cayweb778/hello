package org.boozsoft.rest;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.SysLog;
import org.boozsoft.domain.entity.account.SysDepartment;
import org.boozsoft.domain.entity.account.SysDepartmentRollback;
import org.boozsoft.domain.vo.SysDepartmentVo;
import org.boozsoft.repo.SysDepartmentRepository;
import org.boozsoft.repo.SysDepartmentRollbackRepository;
import org.boozsoft.service.SysLogService;
import org.boozsoft.util.TreeUtils;
import org.boozsoft.utils.CollectOfUtils;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sys/department")
public class SysDepartmentController {
//    @Autowired
//    OAuth2ClientProperties oAuth2ClientProperties;
    @Autowired
    SysDepartmentRepository sysDepartmentRepository;

    /*@GetMapping("all")
    @R2dbcRouter(schemaName = "bjxgkj-001")
    public Mono<Map<String, Object>> getAll() {

        Map params = Map.of(
                "code", 0,
                "message", "ok",
                "type", "success",
                "result", List.of(Map.of(
                        "id", "0",
                        "deptName", "华东分部",
                        "orderNo", 1,
                        "createTime", "1981-07-14 06,03,28",
                        "remark", "此受大根相提规太究角约十",
                        "status", "1",
                        "children", List.of(
                                new DeptModel().setId("01").setDeptName("研发部").setParentDept("0").setCreateTime("1978-04-12 06:03:52").setRemark("此受大根相提规太究角约十").setOrderNo("1").setStatus("1")
                                        .setChildren(List.of(
                                                new DeptModel().setId("011").setDeptName("开发部").setParentDept("0").setCreateTime("1978-04-12 06:03:52").setRemark("此受大根相提规太究角约十").setOrderNo("1").setStatus("1"),
                                                new DeptModel().setId("021").setDeptName("测试部").setParentDept("0").setCreateTime("1978-04-12 06:03:52").setRemark("此受大根相提规太究角约十").setOrderNo("2").setStatus("1")
                                                )
                                        ),
                                new DeptModel().setId("02").setDeptName("市场部").setParentDept("0").setCreateTime("1978-04-12 06:03:52").setRemark("此受大根相提规太究角约十").setOrderNo("2").setStatus("1").setChildren(null),
                                new DeptModel().setId("03").setDeptName("商务部").setParentDept("0").setCreateTime("1978-04-12 06:03:52").setRemark("此受大根相提规太究角约十").setOrderNo("3").setStatus("1"),
                                new DeptModel().setId("04").setDeptName("财务部").setParentDept("0").setCreateTime("1978-04-12 06:03:52").setRemark("此受大根相提规太究角约十").setOrderNo("4").setStatus("1"),
                                new DeptModel().setId("05").setDeptName("销售部").setParentDept("0").setCreateTime("1978-04-12 06:03:52").setRemark("此受大根相提规太究角约十").setOrderNo("5").setStatus("1"),
                                new DeptModel().setId("06").setDeptName("客服部").setParentDept("0").setCreateTime("1978-04-12 06:03:52").setRemark("此受大根相提规太究角约十").setOrderNo("6").setStatus("1")
                        )
                        ),
                        Map.of(
                                "id", "1",
                                "deptName", "华南分部",
                                "orderNo", 2,
                                "createTime", "1981-07-14 06,03,28",
                                "remark", "此受大根相提规太究角约十",
                                "status", "1",
                                "children", List.of(
                                        new DeptModel().setId("11").setDeptName("研发部").setParentDept("1").setCreateTime("1978-04-12 06:03:52").setRemark("此受大根相提规太究角约十").setOrderNo("1").setStatus("1"),
                                        new DeptModel().setId("12").setDeptName("市场部").setParentDept("1").setCreateTime("1978-04-12 06:03:52").setRemark("此受大根相提规太究角约十").setOrderNo("2").setStatus("1"),
                                        new DeptModel().setId("13").setDeptName("商务部").setParentDept("1").setCreateTime("1978-04-12 06:03:52").setRemark("此受大根相提规太究角约十").setOrderNo("3").setStatus("1"),
                                        new DeptModel().setId("14").setDeptName("财务部").setParentDept("1").setCreateTime("1978-04-12 06:03:52").setRemark("此受大根相提规太究角约十").setOrderNo("4").setStatus("1"),
                                        new DeptModel().setId("15").setDeptName("销售部").setParentDept("1").setCreateTime("1978-04-12 06:03:52").setRemark("此受大根相提规太究角约十").setOrderNo("5").setStatus("1"),
                                        new DeptModel().setId("16").setDeptName("客服部").setParentDept("1").setCreateTime("1978-04-12 06:03:52").setRemark("此受大根相提规太究角约十").setOrderNo("6").setStatus("1")
                                )
                        )
                )
        );
        return Mono.just(params);
    }*/

    @GetMapping("treeDept")

    public Mono<Map> treeDept(String id, String flag) {
        if (StrUtil.isNotBlank(id) && !id.equals("0")) {
            return sysDepartmentRepository.findByIdOrderByDeptCode(id)
                    .collectList()
//                    .map(list->TreeUtils.castTreeList(list,SysDepartmentVo.class))
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
            return sysDepartmentRepository.findAllByIsDelOrderByDeptCode("0")
                    .collectList()
                    .map(list -> TreeUtils.castTreeList(list, SysDepartmentVo.class))
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
        return sysDepartmentRepository.findByFlagAndIsDelOrderByDeptCode("1","0")
                .collectList()
                .map(list->TreeUtils.castTreeList(list,SysDepartmentVo.class))
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
        return sysDepartmentRepository.findAllByIsDelOrderByDeptCode("0").collectList().map(R::page);
    }

    @GetMapping("all")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAllDept(){
        return sysDepartmentRepository.findAllDeptCodeOrDeptNameByFlag().collectList().map(R::ok);
    }

    @GetMapping("findDeptByFlag")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findDeptByFlag(){
        return sysDepartmentRepository.findByFlagAndIsDelOrderByDeptCode("1","0").collectList().map(R::ok);
    }

    @GetMapping("findById")
    @ApiOperation(value = "查询", notes = "传入code")

    public Mono findById(String id){
        Mono mono = sysDepartmentRepository.findById(id);
        return mono;
    }

    @PostMapping
    @ApiOperation(value = "新增或修改", notes = "传入code")

    public Mono save(@RequestBody SysDepartment object){
        if (object.getUniqueCode()==null || object.getUniqueCode().equals("")){
            object.setUniqueCode(IdUtil.objectId());
        }
        if (object.getFlag()==null || object.getFlag().equals("")){
            object.setFlag("1");
        }
        if (object.getParentId()==null || object.getParentId().equals("")){
            object.setParentId("0");
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (object.getCreateDate()==null || object.getCreateDate().equals("")){
            object.setCreateDate(sdf.format(new Date()));
        }
        //增加
        if (object.getId()==null || object.getId().equals("")) {
            return sysDepartmentRepository.save(object)
                    .map(o -> R.ok().setResult(o));
        }
        return sysDepartmentRepository.save(object)
                .map(o -> R.ok().setResult(o));
    }

    @DeleteMapping
    @ApiOperation(value = "删除", notes = "传入code")

    public Mono delete(@RequestBody SysDepartment object) {
        return sysDepartmentRepository.findByIdOrderByDeptCode(object.getId())
                .collectList()
                .flatMap(item -> sysDepartmentRepository.deleteAll(item))
                .then(Mono.just(R.ok()));
    }

    @PostMapping("editFlag")
    @ApiOperation(value = "停用启用操作", notes = "传入code")

    public Mono editFlag(@RequestBody SysDepartment object) {
        if (object.getFlag().equals("1")){
            object.setFlag("0");
        } else {
            object.setFlag("1");
        }
        return sysDepartmentRepository.save(object)
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("excel")
    @ApiOperation(value = "前台解析excel,后台直接保存集合", notes = "传入code")
    @Transactional
    public Mono excel(@RequestBody List<SysDepartment> object){
        return Flux.fromIterable(object).map(item -> {
                    if (item.getUniqueCode()==null || item.getUniqueCode().equals("")){
                        item.setUniqueCode(IdUtil.objectId());
                    }
                    if (item.getFlag()==null || item.getFlag().equals("")){
                        item.setFlag("1");
                    }
                    if (item.getParentId() == null || item.getParentId().equals("")) {
                        item.setParentId("0");
                    }
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    if (item.getCreateDate() == null || item.getCreateDate().equals("")) {
                        item.setCreateDate(sdf.format(new Date()));
                    }
                    return item;
                })
                .collectList()
                .flatMapMany(sysDepartmentRepository::saveAll)
                .collectList()
                .flatMap(item -> {
                    return sysDepartmentRepository.findAll()
                            .collectList()
                            .map(list -> {
                                List<SysDepartment> deptList = new ArrayList<>();
                                for (int i = 0; i < list.size(); i++) {
                                    SysDepartment dept = list.get(i);
                                    for (int j = 0; j < object.size(); j++) {
                                        SysDepartment dept1 = object.get(j);
                                        if (dept.getDeptName().equals(dept1.getParentId())) {
                                            dept1.setParentId(dept.getId());
                                            deptList.add(dept1);
                                        }
                                    }
                                }
                                return deptList;
                            });
                })
                .flatMapMany(sysDepartmentRepository::saveAll)
                .collectList()
                .map(o-> R.ok().setResult(o));
    }

    @GetMapping("findDelList")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findDelList() {
        return sysDepartmentRepository.findAllByIsDelOrderByDeptCode("1")
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("editByIsDel")
    @ApiOperation(value = "修改删除状态", notes = "传入code")
    public Mono editByIsDel(@RequestBody SysDepartment object) {
        return sysDepartmentRepository.findByIdOrderByDeptCode(object.getId())
                .collectList()
                .map(list -> {
                    List<SysDepartment> deptList = new ArrayList<>();
                    for (int i = 0; i < list.size(); i++) {
                        SysDepartment dept = list.get(i);
                        dept.setIsDel(object.getIsDel());
                        dept.setDelName(object.getDelName());
                        dept.setDelDate(object.getDelDate());
                        deptList.add(dept);
                    }
                    return deptList;
                })
                .flatMapMany(item -> sysDepartmentRepository.saveAll(item))
                .collectList()
                .then(Mono.just(R.ok()));
    }

}
