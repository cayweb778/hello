package org.boozsoft.rest.group;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.group.GroupSysAccount;
import org.boozsoft.domain.entity.group.GroupSysOrg;
import org.boozsoft.domain.entity.group.GroupSysOrgDataaccess;
import org.boozsoft.repo.GroupSysAccountRepository;
import org.boozsoft.repo.SysAccountRepository;
import org.boozsoft.repo.group.GroupSysOrgDataaccessRepository;
import org.boozsoft.repo.group.GroupSysOrgRepository;
import org.boozsoft.util.FtpUtil;
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
@RequestMapping("/group/sys-org")
public class GroupSysOrgController {


    @Autowired
    GroupSysOrgRepository groupSysOrgRepository;

    @Autowired
    GroupSysAccountRepository groupSysAccountRepository;

    @Autowired
    GroupSysOrgDataaccessRepository groupSysOrgDataaccessRepository;

    @GetMapping
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByClass(Pageable pageable,String userId) {
       return groupSysOrgDataaccessRepository.findAllByUniqueCodeOrderById(userId).map(it->it.getOrgUniqueCode()).collectList().flatMap(
                codes->  (userId == null?groupSysOrgRepository.findAll(): groupSysOrgRepository.findAllByUniqueCodeIn(codes)).filter(it -> it.getIsDel().equals("0"))./*flatMap(item -> {
                    if (StrUtil.isNotBlank(item.getBeiyong1())) {
                        item.setBeiyong2(item.getBeiyong1());
                        item.setBeiyong1(FtpUtil.ImgToBase64("/ncpz/group", item.getBeiyong1()));
                    }
                    return Mono.just(item);
                }).*/collectList().map(list -> list.stream().sorted(Comparator.comparing(GroupSysOrg::getOrgCode)).collect(Collectors.toList())).flatMap(item -> groupSysOrgRepository.countAllBy().map(total -> R.page(item, pageable, total)))
        );
    /*    return groupSysOrgRepository.findAll().filter(it -> it.getIsDel().equals("0")).*//*flatMap(item -> {
                    if (StrUtil.isNotBlank(item.getBeiyong1())) {
                        item.setBeiyong2(item.getBeiyong1());
                        item.setBeiyong1(FtpUtil.ImgToBase64("/ncpz/group", item.getBeiyong1()));
                    }
                    return Mono.just(item);
                }).*//*collectList().map(list -> list.stream().sorted(Comparator.comparing(GroupSysOrg::getOrgCode)).collect(Collectors.toList())).flatMap(item -> groupSysOrgRepository.countAllBy().map(total -> R.page(item, pageable, total)));
   */ }

    @GetMapping("all")
    @ApiOperation(value = "查询所有组织", notes = "传入code")
    public Mono<R> findByAll() {
        return groupSysOrgRepository.findAll().filter(it -> it.getIsDel().equals("0")).collectList().map(R::ok);
    }

    @GetMapping("allDel")
    @ApiOperation(value = "查询业务删除的所有组织", notes = "传入code")
    public Mono<R> findByDelAll() {
        return groupSysOrgRepository.findAll().filter(it -> it.getIsDel().equals("1")).collectList().map(R::ok);
    }

    @GetMapping("allAndUnit")
    @ApiOperation(value = "查询所有组织以及下属公司", notes = "传入code")
    public Mono<R> allAndUnit() {
        return groupSysOrgRepository.findAll().flatMap(dbEntity -> groupSysAccountRepository.findAllByAccGroup(dbEntity.getUniqueCode()).filter(it -> it.getIsDel().equals("0")).collectList().map(list -> {
            String codes = "";
            for (GroupSysAccount groupSysAccount : list) {
                codes += (groupSysAccount.getCoCode() + "、");
            }
            if (list.size() > 0) dbEntity.setBeiyong3(codes.substring(0, codes.length() - 1));
            return dbEntity;
        })).collectList().map(R::ok);
    }

    @PostMapping
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono save(@RequestBody GroupSysOrg entity) {
        // 存储
        if (null == entity.getId()) {
            entity.setUniqueCode(IdUtil.objectId());
            entity.setFlag("1");
            entity.setJiciLength(null != entity.getCcodeLevel() ? entity.getCcodeLevel().replaceAll("-", "").length() + "" : "0");
        }
        entity.setBeiyong2(null);
        return groupSysOrgRepository.save(entity).map(item -> R.ok().setResult(item));
    }

    @Autowired
    SysAccountRepository accountRepository;

    @PostMapping("reduction")
    public Mono<R> reduction(@RequestBody GroupSysOrg entity) {
        return groupSysOrgRepository.findById(entity.getId()).map(it -> {
            it.setIsDel("0");
            it.setDelName(null);
            it.setDelDate(null);
            return it;
        }).flatMap(e -> groupSysOrgRepository.save(e).map(o -> R.ok(e)));
    }

    @DeleteMapping
    @Transactional
    @ApiOperation(value = "删除", notes = "传入code")
    public Mono business(@RequestBody GroupSysOrg entity) {
        return groupSysOrgRepository.countAllByOrgSuperior(entity.getUniqueCode()).flatMap(size -> {
            if (size > 0) {
                return Mono.just(R.ok().setResult(CollectOfUtils.mapof("isOk", false, "type", "D01")));
            } else {
                return accountRepository.countAllByAccGroup(entity.getUniqueCode()).flatMap(size2 -> {
                    if (size2 > 0)
                        return Mono.just(R.ok().setResult(CollectOfUtils.mapof("isOk", false, "type", "D02")));
                   /* Mono<Boolean> delImgMono = Mono.just(StrUtil.isBlank(entity.getBeiyong1())).flatMap(flag -> {
                        if (!flag) return Mono.just(FtpUtil.delFile("ncpz/group", entity.getBeiyong2()));
                        return Mono.just(true);
                    });
                    Mono<Void> delEntityMono = groupSysOrgRepository.deleteById(entity.getId());
                    return Mono.zip(delImgMono, delEntityMono).then(Mono.just(R.ok()));*/
                    return groupSysOrgRepository.findById(entity.getId()).map(it -> {
                        it.setIsDel("1");
                        it.setDelName(entity.getBeiyong2());
                        it.setDelDate(DateUtil.now());
                        return it;
                    }).flatMap(e -> groupSysOrgRepository.save(e).map(o -> R.ok()));
                });
            }
        });
    }

    @DeleteMapping("true")
    @Transactional
    @ApiOperation(value = "删除", notes = "传入code")
    public Mono delete(@RequestBody GroupSysOrg entity) {
        return groupSysOrgRepository.countAllByOrgSuperior(entity.getUniqueCode()).flatMap(size -> {
            if (size > 0) {
                return Mono.just(R.ok().setResult(CollectOfUtils.mapof("isOk", false, "type", "D01")));
            } else {
                return accountRepository.countAllByAccGroup(entity.getUniqueCode()).flatMap(size2 -> {
                    if (size2 > 0)
                        return Mono.just(R.ok().setResult(CollectOfUtils.mapof("isOk", false, "type", "D02")));
                    Mono<Boolean> delImgMono = Mono.just(StrUtil.isBlank(entity.getBeiyong1())).flatMap(flag -> {
                        if (!flag) return Mono.just(FtpUtil.delFile("ncpz/group", entity.getBeiyong2()));
                        return Mono.just(true);
                    });
                    Mono<Void> delEntityMono = groupSysOrgRepository.deleteById(entity.getId());
                    return Mono.zip(delImgMono, delEntityMono).then(Mono.just(R.ok()));
                });
            }
        });
    }

    @GetMapping("findByCode")
    @ApiOperation(value = "查询编码重复", notes = "传入code")
    public Mono findByCode(String id, String orgCode) {
        return groupSysOrgRepository.findFirstByOrgCode(orgCode).collectList().map(list -> {
            if (StrUtil.isNotBlank(id) && list.size() > 0 && list.get(0).getId().equals(id)) {
                return R.ok().setResult(0);
            } else {
                return R.ok().setResult(list.size());
            }
        });
    }

    @GetMapping("findByName")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono findByName(String id, String orgName) {
        return groupSysOrgRepository.findFirstByOrgName(orgName).collectList().map(list -> {
            if (StrUtil.isNotBlank(id) && list.size() > 0 && list.get(0).getId().equals(id)) {
                return R.ok().setResult(0);
            } else {
                return R.ok().setResult(list.size());
            }
        });
    }

    @PostMapping("findById")
    public Mono findById(String id) {
        return groupSysOrgRepository.findById(id).map(list -> R.ok().setResult(list));
    }

    @PostMapping("findByUniqueCode")
    public Mono findByUniqueCode(String uniqueCode) {
        return groupSysOrgRepository.findByUniqueCode(uniqueCode).map(list -> R.ok().setResult(list));
    }

    @PostMapping("countAllByUniqueAccStandard")
    public Mono countAllByUniqueAccStandard(String uniqueAccStandard) {
        return groupSysOrgRepository.countAllByUniqueAccStandard(uniqueAccStandard).map(list -> R.ok().setResult(list));
    }


    @PostMapping("findAuthors")
    @ApiOperation(value = "获取登录用户账套权限", notes = "传入用户标识")
    @Transactional
    public Mono<R> findAuthors(@RequestBody Map map) {
        if (map.keySet().size() != 1) return Mono.just("请求参数异常").map(m -> R.error());
        return groupSysOrgDataaccessRepository.findAllByOrgUniqueCodeOrderById(map.get("code").toString()).collectList().map(R::ok);
    }

    @PostMapping("findOrgAuthorsById")
    @ApiOperation(value = "获取登录用户账套权限", notes = "传入用户标识")
    @Transactional
    public Mono<R> findOrgAuthorsById(@RequestBody Map map) {
        if (map.keySet().size() != 1) return Mono.just("请求参数异常").map(m -> R.error());
        return groupSysOrgDataaccessRepository.findAllByUniqueCodeOrderById(map.get("userId").toString()).collectList().map(R::ok);
    }

    @PostMapping("modifyAuthor")
    @ApiOperation(value = "更新用户账套权限", notes = "传入用户标识")
    @Transactional
    public Mono<R> modifyAuthor(@RequestBody Map map) {
        if (map.keySet().size() != 3) return Mono.just("请求参数异常").map(m -> R.error());
        String code = map.get("uniqueCode").toString();
        Set<String> supervisors = new HashSet<>(((ArrayList<String>) map.get("supervisors")));
        Set<String> authorizes = new HashSet<>(((ArrayList<String>) map.get("authorizes")));
        return groupSysOrgDataaccessRepository.findAllByOrgUniqueCodeOrderById(code).collectList().flatMap(list -> {// 已存在删除
          //  List<GroupSysOrgDataaccess> dels = list.stream().filter(item -> !supervisors.contains(item.getUniqueCode()) && !authorizes.contains(item.getUniqueCode())).collect(Collectors.toList());
            return list.size() > 0 ? groupSysOrgDataaccessRepository.deleteAll(list).thenReturn("") : Mono.just("");
        }).flatMap(o -> {
            List<GroupSysOrgDataaccess> saveList = new ArrayList<>();
            if (supervisors.size() > 0) {
                for (String supervisor : supervisors) {
                    saveList.add(new GroupSysOrgDataaccess().setUniqueCode(supervisor).setOrgUniqueCode(code).setSupervisor("1"));
                }
            }
            if (authorizes.size() > 0) {
                for (String authorize : authorizes) {
                    saveList.add(new GroupSysOrgDataaccess().setUniqueCode(authorize).setOrgUniqueCode(code));
                    ;
                }
            }
            return saveList.size() > 0 ? groupSysOrgDataaccessRepository.saveAll(saveList).collectList().map(R::ok) : Mono.just("").map(R::ok);
        });
    }

}
