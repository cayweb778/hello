//package org.boozsoft.rest;
//
//import cn.hutool.core.date.DateUtil;
//import cn.hutool.core.util.IdUtil;
//import cn.hutool.core.util.NumberUtil;
//import cn.hutool.core.util.StrUtil;
//import com.alibaba.fastjson.JSON;
//import com.alibaba.nacos.client.naming.utils.CollectionUtils;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import lombok.extern.slf4j.Slf4j;
//import org.boozsoft.domain.entity.Accvoucher;
//import org.boozsoft.domain.entity.Customer;
//import org.boozsoft.domain.entity.Supplier;
//import org.boozsoft.domain.entity.Task;
//import org.boozsoft.domain.entity.account.SysDepartment;
//import org.boozsoft.domain.entity.account.SysPsn;
//import org.boozsoft.domain.entity.codekemu.CodeKemu;
//import org.boozsoft.repo.*;
//import org.boozsoft.repo.accountInfo.AccountInfoRepository;
//import org.boozsoft.repo.codekemu.CodeKemuRepository;
//import org.boozsoft.service.AccvoucherService;
//import org.boozsoft.service.AccvoucherTemplateService;
//import org.boozsoft.service.ProjectService;
//import org.boozsoft.util.XlsUtils3;
//import org.springbooz.core.tool.result.R;
//import org.springbooz.core.tool.utils.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.io.buffer.DataBufferUtils;
//import org.springframework.http.codec.multipart.FilePart;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.bind.annotation.*;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//import reactor.util.function.Tuple8;
//
//import java.io.IOException;
//import java.lang.reflect.Method;
//import java.math.BigDecimal;
//import java.nio.channels.AsynchronousFileChannel;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.StandardOpenOption;
//import java.util.*;
//import java.util.concurrent.atomic.AtomicInteger;
//import java.util.concurrent.atomic.AtomicReference;
//import java.util.stream.Collectors;
//
///**
// * @ClassName :
// * @Description : 会计凭证
// * @Author : miao
// * @Date: 2021-04-01 09:25
// */
//@Slf4j
//@RestController
//@RequestMapping("/accvoucher2")
//@Api(value = "凭证处理", tags = "API系统：凭证处理")
//
//public class Accvoucher2Controller {
//
//
//  @Autowired
//  AccvoucherRepository accvoucherRepository;
//
//  @ApiOperation(value = "保存凭证", notes = "保存凭证")
//  @RequestMapping("savePingZheng")
//  public Mono<R> savePingZheng(Flux<Accvoucher> flux){
//    return accvoucherRepository.saveAll(flux).then().thenReturn(R.ok("ok"));
//  }
//
//  @ApiOperation(value = "插入凭证", notes = "插入凭证")
//  @RequestMapping("saveInsertPingZheng")
//  public Mono<R> insertPingZheng(Flux<Accvoucher> flux){
//    String yearMonth="";
//    String vouchType="";
//    String pingzhengInoId="";
//    // 原凭证编码(inoId) 编号加一
//    Flux<Accvoucher> accvoucherFlux = accvoucherRepository
//        .findByIyperiodAndCsignAndInoIdGreaterThanEqualOrderByInidAscUniqueCodeAsc(
//            yearMonth,
//            vouchType,
//            pingzhengInoId
//        )
//        .doOnNext(pingZhengRow -> pingZhengRow.setInoId(String.format("%03d", Integer.parseInt(pingZhengRow.getInoId()) + 1)));
//    Flux<Accvoucher> saveAll = accvoucherRepository.saveAll(accvoucherFlux);
//
//    return saveAll.then().thenMany( accvoucherRepository.saveAll(flux)).then().thenReturn(R.ok("ok"));
//  }
//
//}
