package org.boozsoft.rest;

import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.HomeRightMenuInfo;
import org.boozsoft.repo.HomeRightMenuInfoRepository;
import org.boozsoft.repo.ParameterAccuracyRepository;
import org.boozsoft.repo.SysPeriodRepository;
import org.boozsoft.service.impl.KeMuMingXiServiceImpl;
import org.checkerframework.checker.units.qual.A;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/home-right")
@Api(value = "主页检索菜单信息", tags = "API系统：主页检索菜单信息")
public class HomeRightMenuInfoController {

    @Autowired
    private HomeRightMenuInfoRepository rightMenuInfoRepository;

    @Autowired
    private DatabaseClient databaseClient;

    @PostMapping("findAll")
    @ApiOperation(value = "根据平台查询菜单信息", notes = "")
    public Mono<R> findAll(@RequestBody Map map) {
        log.info("{}", map);
        if (map.keySet().size() != 1) return Mono.just(R.error());
        return rightMenuInfoRepository.findAllByPlatformIdOrderByMenuIdAsc(map.get("id").toString()).collectList().map(R::ok);
    }

    @PostMapping("queryData")
    @ApiOperation(value = "根据平台查询菜单信息", notes = "")
    public Mono<R> queryData(@RequestBody HomeRightMenuInfo entity) {
        log.info("{}", entity.toString());
        if (StrUtil.isBlank(entity.getBeiyong1())) return Mono.just(R.ok());
        String content = "%" + entity.getBeiyong1() + "%";
        StringBuilder biu = new StringBuilder(
                StrUtil.format("select id, {} as code, {} as name from {} where 1=1 and ({} like '{}' or {} like '{}') ",
                        entity.getCodeCol(), entity.getNameCol(), entity.getTableName(), entity.getCodeCol(), content, entity.getNameCol(), content)
        );
        return databaseClient.sql(
                (StrUtil.isNotBlank(entity.getQueryUserType()) && entity.getQueryUserType().equals("1") ?
                        biu.append(StrUtil.format(" and tenant_id = '{}' order by {} asc;", entity.getBeiyong2(), entity.getCodeCol())) :
                        biu.append(StrUtil.format(" order by {} asc;", entity.getCodeCol()))
                ).toString()
        ).fetch().all().collectList().map(R::ok);
    }

}
