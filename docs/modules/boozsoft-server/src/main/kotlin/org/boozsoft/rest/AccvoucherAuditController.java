package org.boozsoft.rest;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.nacos.client.naming.utils.CollectionUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.*;
import org.boozsoft.domain.entity.account.ProjectCategory;
import org.boozsoft.domain.entity.account.ProjectItem;
import org.boozsoft.domain.entity.account.SysDepartment;
import org.boozsoft.domain.entity.account.SysPsn;
import org.boozsoft.domain.entity.codekemu.CodeKemu;
import org.boozsoft.domain.entity.share.project.base.Project;
import org.boozsoft.domain.vo.SubjectInitialBalanceVo;
import org.boozsoft.domain.vo.VoucherBusCheckVo;
import org.boozsoft.repo.*;
import org.boozsoft.repo.accountInfo.AccountInfoRepository;
import org.boozsoft.repo.codekemu.CodeKemuRepository;
import org.boozsoft.repo.project.base.ProjectRepositoryBase;
import org.boozsoft.service.AccvoucherService;
import org.boozsoft.service.AccvoucherTemplateService;
import org.boozsoft.service.ProjectService;
import org.boozsoft.util.ReflectionUtil;
import org.boozsoft.util.XlsUtils3;
import org.boozsoft.utils.CollectOfUtils;
import org.springbooz.core.tool.result.R;
import org.springbooz.core.tool.utils.StringUtils;
import org.springbooz.datasource.r2dbc.service.R2dbcRouterLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ZeroCopyHttpOutputMessage;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple7;
import reactor.util.function.Tuple8;
import reactor.util.function.Tuples;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @ClassName :
 * @Description : 会计凭证审计表
 * @Author : gao
 */
@Slf4j
@RestController
@RequestMapping("/accvoucherAudit")
@Api(value = "凭证审计表", tags = "凭证审计表")

public class AccvoucherAuditController {

    @Autowired
    AccvoucherAuditRepository accvoucherAuditRepository;

    @GetMapping("/findAll")
    public Mono<R> findAll() {
        return accvoucherAuditRepository.findAll().collectList().map(R::ok);
    }

    @PostMapping("/saveAll")
    public Mono<R> saveAll(@RequestBody Map<String,List<Map>> map) {
        Flux<AccvoucherAudit> list=Flux.fromIterable(map.get("list")).map(it->{
            AccvoucherAudit entity = JSON.parseObject(JSON.toJSONString(it), AccvoucherAudit.class);

            return entity;
        }).map(it->{
            return it;
        })

                ;
        return accvoucherAuditRepository.saveAll(list).map(it->{
            return it;
        }).then().map(R::ok);
    }

}
