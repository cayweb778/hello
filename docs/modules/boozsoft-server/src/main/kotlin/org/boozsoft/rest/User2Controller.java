package org.boozsoft.rest;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.swing.clipboard.ClipboardUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.group.GroupAuditUser;
import org.boozsoft.domain.entity.group.GroupSysLoginLog;
import org.boozsoft.domain.entity.group.GroupUser;
import org.boozsoft.repo.group.GroupAuditSysUserRepository;
import org.boozsoft.repo.group.GroupSysLoginLogRepository;
import org.boozsoft.repo.group.GroupSysUserRepository;
import org.boozsoft.utils.CollectOfUtils;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.awt.datatransfer.Clipboard;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


/**
 * @author LZ
 * @version 1.0
 * @title 操作人员
 * @company 财税达软件科技
 * @date 2021/3/22 10:47 上午
 */
@RestController
@RequestMapping("/sys/user2")
public class User2Controller {

    /*  @Autowired
      OAuth2ClientProperties oAuth2ClientProperties;
      @Autowired
      UserCaozuoyuanRepository userCaozuoyuanRepository;
      @Autowired
      UserCaozuoyuanRollbackRepository userCaozuoyuanRollbackRepository;
      @Autowired
      UserCaozuoyuanService userCaozuoyuanService;*/
/*  @Autowired
  UserRepository userRepository;*/
    @Autowired
    GroupSysUserRepository groupSysUserRepository;

    @Autowired
    GroupAuditSysUserRepository auditSysUserRepository;

    // 操作日志
    @Autowired
    GroupSysLoginLogRepository loginLogRepository;

    @PostMapping("findAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAll(@RequestBody Map object) {
        return groupSysUserRepository.findAll()
                .flatMap(item ->
                Mono.just(item.getId()).flatMap(id -> groupSysUserRepository.findRolesByUserId(id).collectList().flatMap(arr -> {
                            item.setBeiyong5(JSON.toJSONString(arr));
                            return Mono.just(item);
                        }).flatMap(item1 ->
                                loginLogRepository.findFirstByUserIdOrderByLoginTimeDesc(item1.getUsername())
                                        .defaultIfEmpty(new GroupSysLoginLog())
                                        .map(dbLoginLog -> {
                                            item1.setBeiyong1(dbLoginLog.getLoginTime());
                                            return item1;
                                        })
                        )
                )).filter(eneity -> {
            String flag = object.containsKey("flag") ? object.get("flag").toString() : "";
            if (StrUtil.isNotBlank(flag) && (flag.equals("1") || flag.equals("0")) && !eneity.getFlag().equals(flag))
                return false;
            String role = object.containsKey("roleValue") ? object.get("roleValue").toString() : "";
            if (StrUtil.isNotBlank(role) && !role.equals("all") && StrUtil.isNotBlank(eneity.getBeiyong5()) && !(JSON.parseArray(eneity.getBeiyong5()).contains(role)))
                return false;
            Map<String, String> searchConditon = object.containsKey("searchConditon") ? (HashMap<String, String>) object.get("searchConditon") : null;
            if (null != searchConditon && StrUtil.isNotBlank(searchConditon.get("value"))) {
                String key = searchConditon.get("requirement");
                if (!key.equals("beiyong4") && !key.equals("beiyong5") && !key.equals("beiyong1") && !key.equals("beiyong2")) {
                    Object fieldValue = BeanUtil.getFieldValue(eneity, key);
                    if (null != fieldValue && !fieldValue.toString().contains(searchConditon.get("value")))
                        return false;
                }
            }
            return true;
        }).collectList().map(list -> list.stream().sorted(Comparator.comparing(GroupUser::getCreateTime)).collect(Collectors.toList())).map(R::page);
    }

    @GetMapping("count")
    @ApiOperation(value = "统计总条数", notes = "传入code")
    public Mono<R> count(){
        return groupSysUserRepository.count().map(R::ok);
    }

    @PostMapping("findAllById")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAllById(@RequestBody Map object) {
        return groupSysUserRepository.findById(object.get("userId").toString()).flatMap(item ->
                Mono.just(item.getId()).flatMap(id -> groupSysUserRepository.findRolesByUserId(id).collectList().flatMap(arr -> {
                    item.setBeiyong5(JSON.toJSONString(arr));
                    return Mono.just(item);
                }))).map(o -> R.ok(o));
    }

    @PostMapping("findAllList")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAllList() {
        return groupSysUserRepository.findAll().collectList().map(o -> R.ok(o));
    }

/*  @DeleteMapping
  @ApiOperation(value = "删除", notes = "传入code")
  public Mono delete(String id) {
    return userCaozuoyuanRepository.findById(id)
        .map(item -> {
              UserCaozuoyuanRollback rollback = BeanUtil.copyProperties(item, UserCaozuoyuanRollback.class);
              rollback.setBiandongDate(LocalDate.now().toString());
              rollback.setBiandongMethod("3");
              rollback.setId(null);
              rollback.setBiandongName("2");
              rollback.setBiandongUniqueCode("1");
              return rollback;
            }
        )
        .flatMap(userCaozuoyuanRollbackRepository::save)
        .flatMap(v -> userCaozuoyuanRepository.deleteById(id)).then(Mono.just(R.ok()))
        .defaultIfEmpty(R.error());
  }*/

/*  @GetMapping("findAll")

  @ApiOperation(value = "查询列表", notes = "传入code")
  public Mono<R> findAll(Pageable pageable) {
    Mono<R> map = userCaozuoyuanRepository.findAllByOrderByFlagDesc(pageable).map(item -> {
      return userRepository.findByOpenid(item.getUniqueCode()).map(var -> {
        return item;
      });
    })
        .flatMap(item -> item)
        .collectList()
        .map(R::page);
    return userCaozuoyuanRepository.findAll().collectList().flatMap(item -> map);
  }*/

  /*@GetMapping("findByZoneId")
  @ApiOperation(value = "查询", notes = "传入code")
  public Mono findByZoneId(String id) {
    Mono mono = userCaozuoyuanRepository.findById(id);
    return mono;
  }*/


    @PostMapping
    @ApiOperation(value = "新增或修改用户", notes = "传入data")
    @Transactional
    public Mono save(@RequestBody Map object) {
        if (object.keySet().size() == 0) return Mono.just("").map(o -> R.error());
   /* if (Objects.isNull(object.getUniqueCode())) {
      object.setFlag("1");
      // 设置唯一码和统一OpenID
      object.setUniqueCode(IdUtil.objectId());
    }*/

        // 保存到鉴权服务器
//    Mono<User> saveAuthServerMono = userCaozuoyuanService.saveAccount(object);

        // 保存到sys_user和user_caozuoyuan表
  /*  Mono<UserCaozuoyuanVo> saveSystemUserAndUserCaoZuoYuanMono = userCaozuoyuanService.saveNc(object);
    // _app_group_sys_user_role
    return Mono.zip(saveAuthServerMono, saveSystemUserAndUserCaoZuoYuanMono)
        .map(o -> R.ok().setResult(o));*/
        return Mono.just(object).flatMap(
                map -> {
                    String id = object.get("id").toString();
                    String realName = object.get("realName").toString().trim();
                    String operName = object.get("operName").toString().trim();
                    String username = object.get("username").toString().trim();
                    String password = object.get("password").toString().trim();
                    String sex = object.get("sex").toString().trim();
                    String phone = null != object.get("phone") ? object.get("phone").toString().trim() : "";
                    String email = null != object.get("email") ? object.get("email").toString().trim() : "";
                    String startDate = null != object.get("effectiveDate") ? object.get("effectiveDate").toString().trim() : "";
                    String endDate = null != object.get("invalidDate") ? object.get("invalidDate").toString().trim() : "";
                    String tactics = null != object.get("pwdPolicy") ? object.get("pwdPolicy").toString().trim() : "";

                    String createTime = null != object.get("createTime") ? object.get("createTime").toString().trim() : "";
                    String passwordTime = null != object.get("passwordTime") ? object.get("passwordTime").toString().trim() : "";
                    String passwordOldTime = null != object.get("passwordOldTime") ? object.get("passwordOldTime").toString().trim() : "";
                    String passwordOld = null != object.get("passwordOld") ? object.get("passwordOld").toString().trim() : "";
                    List<String> roleList = (List<String>) object.get("roleList");
                    GroupUser groupUser = new GroupUser();
                    groupUser.setRealName(realName);
                    groupUser.setUsername(username);
                    groupUser.setSex(sex);
                    String now = DateUtil.now();
                    if (StrUtil.isNotBlank(id)) {
                        groupUser.setId(id);
                        groupUser.setFlag(object.containsKey("flag") ? object.get("flag").toString() : "1");
                        groupUser.setOpenid(object.containsKey("openid") ? object.get("openid").toString() : IdUtil.objectId());
                    } else {
                        groupUser.setOpenid(IdUtil.objectId());
                        groupUser.setFlag("1");
                        groupUser.setCreateTime(now);
                        groupUser.setPasswordTime(now);
                        groupUser.setPasswordOldTime(now);
                        groupUser.setPasswordOld(StrUtil.isNotBlank(password)?password:"123456");
                    }
                    if (StrUtil.isNotBlank(password)) {
                        groupUser.setPassword(password);
                    } else {
                        groupUser.setPassword("123456");
                    }
                    if (StrUtil.isNotBlank(phone)) groupUser.setPhone(phone);
                    if (StrUtil.isNotBlank(email)) groupUser.setEmail(email);
                    if (StrUtil.isNotBlank(startDate)) groupUser.setEffectiveDate(startDate);
                    if (StrUtil.isNotBlank(endDate)) groupUser.setInvalidDate(endDate);
                    if (StrUtil.isNotBlank(tactics)) groupUser.setPwdPolicy(tactics);
                    if (StrUtil.isNotBlank(passwordTime)) groupUser.setPasswordTime(passwordTime);
                    if (StrUtil.isNotBlank(passwordOldTime)) groupUser.setPasswordOldTime(passwordOldTime);
                    if (StrUtil.isNotBlank(passwordOld)) groupUser.setPasswordOld(passwordOld);
                    if (StrUtil.isNotBlank(createTime)) groupUser.setCreateTime(createTime);
                    if (object.containsKey("beiyong4") && null != object.get("beiyong4") && object.get("beiyong4").toString().equals("1")) { // 密码修改
                        return groupSysUserRepository.save(groupUser);
                    } else {
                        Mono<String> auditM = StrUtil.isBlank(id) ? Mono.just("") :
                                groupSysUserRepository.findById(id).flatMap(it -> {
                                    GroupAuditUser auditUser = new GroupAuditUser();
                                    BeanUtils.copyProperties(it, auditUser);
                                    auditUser.setId(null);
                                    auditUser.setAuditId(it.getId());
                                    auditUser.setOptTime(now);
                                    auditUser.setOptMethod("0");
                                    auditUser.setOptUsername(operName);
                                    return auditSysUserRepository.save(auditUser).thenReturn("");
                                });
                        AtomicReference<GroupUser> db = new AtomicReference<>();
                        return auditM.flatMap(a->
                                        groupSysUserRepository.save(groupUser).flatMap(dbEntity -> {
                                            List<Map<String, String>> list = new ArrayList<>();
                                            for (String s : roleList) {
                                                list.add(CollectOfUtils.mapof("userId", dbEntity.getId(), "roleId", s));
                                            }
                                            dbEntity.setBeiyong5(JSON.toJSONString(roleList));
                                            db.set(dbEntity);
                                            return StrUtil.isBlank(id) ? Mono.just(list) : groupSysUserRepository.deleteUserRoleByUserId(id).thenReturn(list);
                                        }).flatMap(list -> Flux.fromIterable(list).flatMap(item -> groupSysUserRepository.saveUserRole(IdUtil.objectId(),item.get("userId"), item.get("roleId"))).collectList().thenReturn(db.get()))
                                );
                    }
                }).flatMap(list -> Mono.just(R.ok(list)));
    }

    @PostMapping("batch")
    @ApiOperation(value = "批量修改用户", notes = "传入data")
    @Transactional
    public Mono<R> batch(@RequestBody Map object) {
        if (object.keySet().size() == 0) return Mono.just("").map(o -> R.error());
        return Mono.just(object).flatMap(
                map -> {
                    List<String> ids = (List<String>) object.get("ids");
                    String type = object.get("type").toString();
                    String operName = object.get("operName").toString().trim();
                    String password = object.get("password").toString().trim();
                    String startDate = object.containsKey("effectiveDate") && null != object.get("effectiveDate") ? object.get("effectiveDate").toString().trim() : "";
                    String tactics = object.containsKey("pwdPolicy") && null != object.get("pwdPolicy") ? object.get("pwdPolicy").toString().trim() : "";
                    String flag = object.containsKey("flag") && null != object.get("flag") ? object.get("flag").toString().trim() : "";
                    List<String> roleList = (List<String>) object.get("roleList");
                    AtomicReference<List<GroupAuditUser>> audits = new AtomicReference<>();
                    return groupSysUserRepository.findAllById(ids).flatMap(eneity -> {
                        GroupAuditUser auditUser = new GroupAuditUser();
                        BeanUtils.copyProperties(eneity, auditUser);
                        auditUser.setId(null);
                        auditUser.setAuditId(eneity.getId());
                        auditUser.setOptTime(DateUtil.now());
                        auditUser.setOptMethod("0");
                        auditUser.setOptUsername(operName);
                        if (type.equals("edit")) {
                            audits.get().add(auditUser);
                            if (StrUtil.isNotBlank(startDate)) eneity.setEffectiveDate(startDate);
                            if (StrUtil.isNotBlank(tactics)) eneity.setPwdPolicy(tactics);
                            if (StrUtil.isNotBlank(flag)) eneity.setFlag(flag);
                        } else {
                            eneity.setPasswordOld(eneity.getPassword());
                            eneity.setPasswordOldTime(eneity.getPasswordTime());
                            eneity.setPassword(password);
                            eneity.setPasswordTime(DateUtil.now());
                        }
                        return Mono.just(eneity);
                    }).collectList().flatMap(list ->
                                    (audits.get().size() == 0?Mono.just(new ArrayList()): auditSysUserRepository.saveAll(audits.get()).collectList()).flatMap(a->
                                                    groupSysUserRepository.saveAll(list).flatMap(dbEneity -> {
                                                        if (type.equals("edit") && roleList.size() > 0) { // 替换角色
                                                            return Mono.just(dbEneity).flatMap(dbEntity -> {
                                                                List<Map<String, String>> list1 = new ArrayList<>();
                                                                for (String s : roleList) {
                                                                    list1.add(CollectOfUtils.mapof("userId", dbEntity.getId(), "roleId", s));
                                                                }
                                                                dbEntity.setBeiyong5(JSON.toJSONString(roleList));
                                                                return groupSysUserRepository.deleteUserRoleByUserId(dbEneity.getId()).thenReturn(list1);
                                                            }).flatMap(
                                                                    list1 -> Flux.fromIterable(list1).flatMap(item -> groupSysUserRepository.saveUserRole(IdUtil.objectId(),item.get("userId"), item.get("roleId"))).collectList()
                                                            ).flatMap(sst->Mono.just(dbEneity));
                                                        }
                                                        return Mono.just(dbEneity);
                                                    }).collectList()
                                            )
                            );
                }).flatMap(list -> Mono.just(R.ok(list)));
    }

    @PostMapping("changePass")
    @ApiOperation(value = "批量修改用户", notes = "传入data")
    @Transactional
    public Mono<R> changePass(@RequestBody Map object){
        if (object.keySet().size() == 0) return Mono.just("").map(o -> R.error());
        String id = object.get("id").toString();
        String password = object.get("password").toString().trim();
        return  groupSysUserRepository.findById(id).flatMap(eneity->{
            eneity.setPasswordOld(eneity.getPassword());
            eneity.setPasswordOldTime(eneity.getPasswordTime());
            eneity.setPassword(password);
            eneity.setPasswordTime(DateUtil.now());
            return groupSysUserRepository.save(eneity).thenReturn("").map(s->R.ok());
        });
    }

    @PostMapping("all")
    @ApiOperation(value = "新增或修改用户", notes = "传入data")
    @Transactional
    public Mono saveAll(@RequestBody Flux<GroupUser> listFlux) {
        return listFlux.collectList().flatMap(list -> {
            Map<String, String> roleMap = new HashMap<>();
            for (GroupUser groupUser : list) {
                groupUser.setOpenid(IdUtil.objectId());
                roleMap.put(groupUser.getUsername(), groupUser.getBeiyong5());
                groupUser.setBeiyong5(null);
            }
            return groupSysUserRepository.saveAll(list).flatMap(dbEntity -> {
                List<Map<String, String>> list2 = new ArrayList<>();
                String roleJson = roleMap.get(dbEntity.getUsername());
                for (String s : JSON.parseArray(roleJson, String.class)) {
                    list2.add(CollectOfUtils.mapof("userId", dbEntity.getId(), "roleId", s));
                }
                return Flux.fromIterable(list2).flatMap(item -> groupSysUserRepository.saveUserRole(IdUtil.objectId(),item.get("userId"), item.get("roleId"))).collectList().thenReturn(dbEntity);
            }).collectList().map(R::ok);
        });
    }



      @GetMapping("/updateFlag/{id}/{flag}")
      @ApiOperation(value = "启用停用", notes = "传入id和类型")
      public Mono updateFlag(@PathVariable String id, @PathVariable String flag) {
          return groupSysUserRepository.findById(id).flatMap(it->{
                       it.setFlag(flag);
                       it.setLockTime(null);
                       return groupSysUserRepository.save(it).thenReturn("");
                  }).map(o -> R.ok(o));
      }

    @PostMapping("check")
    @ApiOperation(value = "检查数据", notes = "map")
    @Transactional
    public Mono<R> check(@RequestBody Map object) {
        String type = object.containsKey("type") ? object.get("type").toString() : "";
        if (StrUtil.isNotBlank(type)) {
            String id = object.containsKey("id") && null != object.get("id") ? object.get("id").toString() : "";
            Flux<GroupUser> list = null;
            if (type.equals("username")) {
                list = groupSysUserRepository.findByUsername(object.get("username").toString());
            } else if (type.equals("phone")) {
                list = groupSysUserRepository.findByPhone(object.get("phone").toString());
            } else if (type.equals("email")) {
                list = groupSysUserRepository.findByEmail(object.get("email").toString());
            }else if (type.equals("realName")){
                list = groupSysUserRepository.findByRealName(object.get("realName").toString());
            }
            return list.collectList().flatMap(userList -> {
                R r = null;
                String value = type.equals("username") ? object.get("username").toString() : type.equals("email") ? object.get("email").toString() : type.equals("realName") ? object.get("realName").toString() : object.get("phone").toString();
                String msg = type.equals("username") ? ("登录账户：" + value + "系统已存在,请重新输入！") : type.equals("email") ? ("邮箱：" + value + "系统已存在,请重新输入！") :type.equals("realName") ? ("操作员名称：" + value + "系统已存在,请补充“手机号”或“邮箱”信息区分！") : ("手机号：" + value + "系统已存在,请重新输入！");
                if (StrUtil.isBlank(id) && userList.size() > 0) {
                    r = R.ok(CollectOfUtils.mapof("pass", false, "msg", msg));
                } else if (StrUtil.isNotBlank(id) && userList.size() > 0) {
                    int size = userList.stream().filter(item -> (!item.getId().equals(id))).collect(Collectors.toList()).size();
                    if (size > 0) {
                        r = R.ok(CollectOfUtils.mapof("pass", false, "msg", msg));
                    } else {
                        r = R.ok(CollectOfUtils.mapof("pass", true));
                    }
                } else {
                    r = R.ok(CollectOfUtils.mapof("pass", true));
                }
                return Mono.just(r);
            });
        } else {
            return Mono.just(R.ok(CollectOfUtils.mapof("pass", false, "msg", "参数异常！")));
        }
    }

    @PostMapping("delCheck")
    @ApiOperation(value = "检查数据", notes = "map")
    @Transactional
    public Mono<R> delCheck(@RequestBody Map object) {
        String username = object.get("username").toString();
        return loginLogRepository.countAllByUserId(username).map(l->R.ok(l>0));
    }

    @PostMapping("checkList")
    @ApiOperation(value = "检查数据", notes = "map")
    @Transactional
    public Mono<R> checkList(@RequestBody Map object) {
        String type = object.containsKey("type") ? object.get("type").toString() : "";
        if (StrUtil.isNotBlank(type)) {
            Flux<GroupUser> list = null;
            if (type.equals("username")) {
                list = groupSysUserRepository.findByUsernameIn(JSON.parseArray(object.get("username").toString(), String.class));
            } else if (type.equals("phone")) {
                list = groupSysUserRepository.findByPhoneIn(JSON.parseArray(object.get("phone").toString(), String.class));
            }
            return list.collectList().flatMap(userList -> {
                R r = null;
                String value = type.equals("username") ? object.get("username").toString() : object.get("phone").toString();
                String msg = type.equals("username") ? ("登录账户：" + value + "系统已存在,请重新输入！") : ("手机号：" + value + "系统已存在,请重新输入！");
                if (userList.size() > 0) {
                    r = R.ok(CollectOfUtils.mapof("pass", false, "msg", msg));
                } else {
                    r = R.ok(CollectOfUtils.mapof("pass", true));
                }
                return Mono.just(r);
            });
        } else {
            return Mono.just(R.ok(CollectOfUtils.mapof("pass", false, "msg", "参数异常！")));
        }
    }
}
