package org.boozsoft.rest;

import cn.hutool.core.util.IdUtil;
import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.SysLog;
import org.boozsoft.domain.entity.account.VoucherType;
import org.boozsoft.domain.entity.account.VoucherTypeRollback;
import org.boozsoft.domain.entity.group.GroupDataAuthorization;
import org.boozsoft.repo.AccvoucherRepository;
import org.boozsoft.repo.VoucherTypeRepository;
import org.boozsoft.repo.VoucherTypeRollbackRepository;
import org.boozsoft.repo.group.GroupDataAuthorizationRepository;
import org.boozsoft.repo.group.GroupDataSeeSwitchRepository;
import org.boozsoft.repo.group.GroupSysVoucherTypeRepository;
import org.boozsoft.service.SysLogService;
import org.boozsoft.utils.CollectOfUtils;
import org.checkerframework.checker.units.qual.A;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/voucherType")
public class VoucherTypeController {

    @Autowired
    VoucherTypeRepository voucherTypeRepository;
    @Autowired
    VoucherTypeRollbackRepository voucherTypeRollbackRepository;

    @Autowired
    GroupSysVoucherTypeRepository groupSysVoucherTypeRepository;

    @Autowired
    SysLogService sysLogService;

    @Autowired
    AccvoucherRepository accvoucherRepository;

    @GetMapping("findAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAll(Pageable pageable) {
        return voucherTypeRepository.findAllByOrderById().collectList().cache().map(R::page);
    }

    @GetMapping("/group/findAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> groupFindAll(Pageable pageable) {
        return groupSysVoucherTypeRepository.findAll().collectList().map(R::ok);
    }

    @GetMapping("findAll2")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAll() {
        return voucherTypeRepository.findAllByOrderById().collectList().map(R::page);
    }

    @GetMapping("findByNum")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByNum(String voucherNum) {
        return voucherTypeRepository.findByVoucherNumOrderById(voucherNum)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByCode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByCode(String voucherTypeCode) {
        return voucherTypeRepository.findByVoucherTypeCodeOrderById(voucherTypeCode)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByName")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByName(String voucherTypeName) {
        return voucherTypeRepository.findByVoucherTypeNameOrderById(voucherTypeName)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono save(@RequestBody VoucherType object) {
        if (object.getVoucherNum() == null || object.getVoucherNum().equals("")) {
            object.setVoucherNum(IdUtil.fastUUID());
        }
        return voucherTypeRepository.save(object)
                .map(o -> R.ok().setResult(o));
    }

    @DeleteMapping
    @ApiOperation(value = "删除", notes = "传入code")
    public Mono delete(@RequestBody VoucherType object) {
        return voucherTypeRepository.deleteById(object.getId())
                .then(Mono.just(R.ok()));
    }

    @PostMapping("excel")
    @ApiOperation(value = "前台解析excel,后台直接保存集合", notes = "传入code")
    @Transactional
    public Mono excel(@RequestBody List<VoucherType> object) {
        return Flux.fromIterable(object).map(item -> {
                    if (item.getVoucherNum() == null || item.getVoucherNum().equals("")) {
                        item.setVoucherNum(IdUtil.fastUUID());
                    }
                    return item;
                })
                .collectList()
                .flatMapMany(voucherTypeRepository::saveAll)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findAccountByCode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAccountByCode(String voucherTypeCode) {
        return accvoucherRepository.countByCsign(voucherTypeCode)
                .map(o -> R.ok().setResult(o));
    }

    @Autowired
    private GroupDataSeeSwitchRepository groupDataSeeSwitchRepository;
    @Autowired
    private GroupDataAuthorizationRepository groupDataAuthorizationRepository;

    @PostMapping("authorizedList")
    @ApiOperation(value = "获取已授权的凭证类型列表", notes = "传入code")
    public Mono<R> authorizedList(@RequestBody Map map) {
        String userId = map.get("userId").toString();
        String tenantId = map.get("tenantId").toString();
        String name = "sys_voucher_type";
        return groupDataSeeSwitchRepository.findAllByUniqueCodeAndDatabaseNameAndIsCtrl(tenantId, name, "1").collectList().flatMap(list ->
                list.size() == 0 ? voucherTypeRepository.findAllByOrderById().collectList().map(R::ok) :
                        groupDataAuthorizationRepository.findAllByOperatorIdAndTenantryIdAndArchivesIdOrderByTenantryIdAsc(userId, tenantId, name).collectList().flatMap(
                                alist -> alist.size() == 0 ? Mono.just(R.ok()) :
                                        voucherTypeRepository.findAllById(Flux.fromIterable(alist.stream().map(t -> t.getDataId()).collect(Collectors.toList()))).collectList().map(R::ok)
                        )
        );
    }
}
