package org.boozsoft.rest;

import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.*;
import org.boozsoft.domain.entity.account.Iperiod;
import org.boozsoft.domain.ro.GeneralLedgerRo;
import org.boozsoft.domain.vo.DeptGeneralLedgerVo;
import org.boozsoft.domain.vo.GeneralLedgerVo;
import org.boozsoft.repo.*;
import org.boozsoft.repo.group.GroupSysUserRepository;
import org.boozsoft.service.SysLogService;
import org.springbooz.core.tool.result.R;
import org.springbooz.datasource.r2dbc.domain.vo.R2dbcRouterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * 消息公告
 * @author lz
 */
@RestController
@RequestMapping("/api/message")
public class MessageController {


    @Autowired
    SysLogService sysLogService;
    @Autowired
    SysMessageRepository sysMessageRepository;
    @Autowired
    SysMessageUserRepository sysMessageUserRepository;
    @Autowired
    SysMessageFileRepository sysMessageFileRepository;
    @Autowired
    GroupSysUserRepository groupSysUserRepository;


    //查询筛选条件\
    @PostMapping(value = "/findAll")
    @ApiOperation(value = "查询消息列表", notes = "查询消息列表")
    public Mono<R> findAll(@RequestBody Map maps) {
        String type = maps.get("lb").toString();
        String ifrag = maps.get("ifrag").toString();
        String user = maps.get("user").toString();
        String types = maps.get("type").toString();
        String selectValue = maps.get("selectValue").toString();
        Mono<R> map = sysMessageUserRepository.findByUserId(user)
                .collectList()
                .map(flist->{
                    //过滤
                    Map<String, String> searchMap = ((HashMap<String, String>) maps.get("searchConditon"));
                    Map<String, String> filterMap = ((HashMap<String, String>) maps.get("filterConditon"));
                    return flist.stream().filter(item -> {

                        if(!"0".equals(types)){//全部 不包含隐藏
                            if (!item.getTypeId().equals(types)) {
                                return false;
                            }
                        }

                        if (Objects.nonNull(selectValue) && !item.getUsername().contains(selectValue)) {
                            return false;
                        }

                        if("-1".equals(ifrag)){//全部 不包含隐藏
                            if (item.getIfrag().equals("-1")) {
                                return false;
                            }
                        }else{
                            if (!item.getIfrag().equals(ifrag)) {
                                return false;
                            }
                        }

                        if (StrUtil.isNotBlank(searchMap.get("requirement")) && StrUtil.isNotBlank(searchMap.get("value"))) {
                            String value = searchMap.get("value");
                            String dbValue = getFieldValueByFieldName(searchMap.get("requirement").trim(), item);
                            if (Objects.nonNull(dbValue) && !dbValue.contains(value)) {
                                return false;
                            }
                        }
//                        if (StrUtil.isNotBlank(filterMap.get("amountMinJf")) && StrUtil.isNotBlank(filterMap.get("amountMaxJf"))) {
//
//                        }
                        return true;
                    }).collect(toList());
                })
                .map(o -> R.ok().setResult(o));

        Mono<R> map1 = sysMessageRepository.findByIfrag(user)
                .collectList()
                .map(flist->{
                    //过滤
                    Map<String, String> searchMap = ((HashMap<String, String>) maps.get("searchConditon"));
                    Map<String, String> filterMap = ((HashMap<String, String>) maps.get("filterConditon"));
                    return flist.stream().filter(item -> {
                        if(!"0".equals(types)){//全部 不包含隐藏
                            if (!item.getTypeId().equals(types)) {
                                return false;
                            }
                        }

                        if (Objects.nonNull(selectValue) && !item.getUsername().contains(selectValue)) {
                            return false;
                        }

                        if (StrUtil.isNotBlank(searchMap.get("requirement")) && StrUtil.isNotBlank(searchMap.get("value"))) {
                            String value = searchMap.get("value");
                            String dbValue = getFieldValueByFieldName(searchMap.get("requirement").trim(), item);
                            if (Objects.nonNull(dbValue) && !dbValue.contains(value)) {
                                return false;
                            }
                        }
//                        if (StrUtil.isNotBlank(filterMap.get("amountMinJf")) && StrUtil.isNotBlank(filterMap.get("amountMaxJf"))) {
//
//                        }
                        return true;
                    }).collect(toList());
                })
                .map(o -> R.ok().setResult(o));
        return Objects.nonNull(type) && "1".equals(type) ? map : map1;
    }

    @PostMapping(value = "/findAllIndex")
    @ApiOperation(value = "首页查询消息列表4条", notes = "首页查询消息列表4条")
    public Mono<R> findAllIndex(@RequestBody Map maps) {
        String user = maps.get("user").toString();
        return sysMessageUserRepository.findByUserIdLimtFour(user)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }
    @PostMapping(value = "/findNewMseeage")
    @ApiOperation(value = "新消息数量", notes = "新消息数量")
    public Mono<R> findNewMseeage(@RequestBody Map maps) {
        String user = maps.get("user").toString();
        return sysMessageUserRepository.findNewMseeageCount(user)
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping(value = "/hide/{ids}/{userId}")
    @ApiOperation(value = "隐藏", notes = "隐藏")
    public Mono<R> hide(@PathVariable List<String> ids, @PathVariable String userId) {
        return sysMessageUserRepository.updateById(ids, userId).thenReturn(R.ok());
    }

    @GetMapping(value = "/chexiao/{id}/{messageId}")
    @ApiOperation(value = "撤销", notes = "撤销")
    public Mono<R> chexiao(@PathVariable String id, @PathVariable String messageId) {
        return sysMessageRepository.deleteById(id).then(sysMessageUserRepository.deleteByMessageId(messageId)).thenReturn(R.ok());
    }

    @GetMapping(value = "/read/{ids}/{userId}")
    @ApiOperation(value = "已阅", notes = "已阅")
    public Mono<R> read(@PathVariable List<String> ids, @PathVariable String userId) {
        return sysMessageUserRepository.updateIfragById(ids,userId,LocalDate.now()).thenReturn(R.ok());
    }

    @PostMapping(value = "/save")
    @ApiOperation(value = "save", notes = "save")
    public Mono<R> save(@RequestBody SysMessage sysMessage) {

        sysMessage.setSendTime(LocalDateTime.now());
        sysMessage.setMessageId(UUID.randomUUID().toString());
        ArrayList<String> user = sysMessage.getUser();
        List<SysMessageUser> muList = new ArrayList<>();
        user.forEach(v->{
            SysMessageUser s = new SysMessageUser();
            s.setUserId(v);
            s.setIfrag("0");
            s.setMessageId(sysMessage.getMessageId());
            muList.add(s);
        });

        ArrayList<String> file = sysMessage.getFile();
        List<SysMessageFile> mfList = new ArrayList<>();
        file.forEach(v->{
            SysMessageFile s = new SysMessageFile();
            s.setFileId(v);
            s.setUniqueCode(sysMessage.getMessageId());
            mfList.add(s);
        });

        return sysMessageRepository.save(sysMessage)
                .zipWith(sysMessageUserRepository.saveAll(muList).collectList())
                .zipWith(sysMessageFileRepository.saveAll(mfList).collectList())
                .map(o-> R.ok().setResult(o));
    }

    @GetMapping(value = "/getUserList")
    @ApiOperation(value = "操作人员list", notes = "操作人员list")
    public Mono<R> getUserList() {
        return groupSysUserRepository.findByflag().collectList()
                .map(o-> R.ok().setResult(o));
    }

    @PostMapping("delete")
    @ApiOperation(value = "删除", notes = "传入code")
    public Mono delete(@RequestBody Map maps){
        List<String> list =  (List<String>)maps.get("ids");
        return sysMessageUserRepository.deleteByIds(list)
                .then(sysMessageRepository.deleteByIds(list))
                .then(Mono.just(R.ok()));
    }

    private String getFieldValueByFieldName(String fieldName, Object object) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = object.getClass().getMethod(getter, new Class[]{});
            Object value = method.invoke(object, new Object[]{});
            return value.toString();
        } catch (Exception e) {
            return null;
        }
    }


    @GetMapping(value = "/getTypeList")
    @ApiOperation(value = "类型list", notes = "类型list")
    public Mono<R> getTypeList() {
        return sysMessageRepository.findAllByType().collectList()
                .map(o-> R.ok().setResult(o));
    }
}
