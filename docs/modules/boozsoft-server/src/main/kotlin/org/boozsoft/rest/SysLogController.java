package org.boozsoft.rest;

import cn.hutool.core.date.DateUtil;
import org.boozsoft.repo.SysLogRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author myh
 * @Description 记录系统操作日志
 * @Date 11:11 2021/7/8
 * @Param
 * @return
 **/
@RestController
@RequestMapping("/sys/log")
public class SysLogController {

    @Autowired
    SysLogRepository logRepository;

/************************************************ 之后可能删除 **********************************************************************/
    /**
     * 获取 区/县 信息
     *
     * @return
     */
//    @PostMapping("save")
//    public Mono<R> savesyslog(@RequestBody Map map) {
//        String accId = map.get("accId").toString();   // 账套号
//        String text = map.get("text").toString();     // 日志
//        String username = map.get("username").toString();     // 日志
//        String logMethod = map.get("flag").toString();     // 1、修改2、删除3、停用、4查看、5导入、6导出、7打印
//        return logRepository.save(
//                new SysLog()
//                        .setOperationDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()))
//                        .setUniqueCode(accId)
//                        .setAccId(accId)
//                        .setUserName(username)
//                        .setIyear(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)))
//                        .setOperationCont(text)
//                        .setLogMethod(logMethod)
//        ).map(e -> R.ok().setResult(e));
//    }

    @PostMapping("interval")
    public Mono<R> interval(Pageable pageable, @RequestBody Map map) {
        return Mono.just(R.ok());
    }
    @GetMapping("serverDate")
    public Mono<R> serverDate() {
        return Mono.just(R.ok(DateUtil.today()));
    }
    @GetMapping("serverTime")
    public Mono<R> serverTime() {
        return Mono.just(R.ok(DateUtil.now()));
    }
/************************************************ 之后可能删除 End**********************************************************************/
}
