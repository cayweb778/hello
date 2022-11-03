package org.boozsoft.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.pinyin.PinyinUtil;
import com.alibaba.fastjson.JSON;
import org.boozsoft.domain.entity.*;
import org.boozsoft.domain.entity.account.*;
import org.boozsoft.domain.entity.acctemplate.AccTemplate;
import org.boozsoft.domain.entity.group.*;
import org.boozsoft.domain.entity.share.project.base.Project;
import org.boozsoft.domain.vo.SysAccountPeriodVo;
import org.boozsoft.repo.acctemplate.AccTemplateRepository;
import org.boozsoft.repo.codekemu.GroupCodeKemuRepository;
import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.codekemu.CodeKemu;
import org.boozsoft.domain.entity.share.ProjectCash;
import org.boozsoft.repo.*;
import org.boozsoft.repo.codekemu.CodeKemuRepository;
import org.boozsoft.repo.group.*;
import org.boozsoft.repo.origin.OriginProjectCashRepository;
import org.boozsoft.repo.project.base.ProjectRepositoryBase;
import org.boozsoft.service.DatabaseInitService;
import org.springbooz.datasource.r2dbc.domain.vo.R2dbcRouterBuilder;
import org.springbooz.datasource.r2dbc.service.R2dbcRouterLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple8;
import reactor.util.function.Tuples;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


@Slf4j
@Service
public class DatabaseInitServiceImpl implements DatabaseInitService {

    @Autowired
    private DatabaseClient databaseClient;

    @Autowired
    private CodeKemuRepository codeKemuRepository;

    @Autowired
    private GroupCodeKemuRepository groupCodeKemuRepository;

    @Autowired
    GroupSysSettModesRepository groupSysSettModesRepository;

    @Autowired
    SettModesRepository settModesRepository;

    @Autowired
    VoucherTypeRepository voucherTypeRepository;

    @Autowired
    ProjectCashRepository projectCashRepository;

    @Autowired
    GroupProjectCashRepository groupProjectCashRepository;

    @Autowired
    OriginProjectCashRepository originProjectCashRepository;

    @Autowired
    SysAccountPeriodRepository accountPeriodRepository;

    @Autowired
    SysPeriodRepository sysPeriodRepository;

    @Autowired
    SysPsnTypeRepository sysPsnTypeRepository;

    @Autowired
    GroupSysPsnTypeRepository groupSysPsnTypeRepository;

    @Autowired
    GroupCustomerClassRepository groupCustomerClassRepository;

    @Autowired
    CustomerClassRepository customerClassRepository;

    @Autowired
    GroupSupplierClassRepository groupSupplierClassRepository;

    @Autowired
    SupplierClassRepository supplierClassRepository;

    @Autowired
    GroupProjectCategoryRepository groupProjectCategoryRepository;

    @Autowired
    ProjectCategoryRepository projectCategoryRepository;

    @Autowired
    GroupProjectClassRepository groupProjectClassRepository;

    @Autowired
    ProjectClassRepository projectClassRepository;

    @Autowired
    SysDepartmentRepository departmentRepository;

    @Autowired
    SysPsnRepository sysPsnRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    SupplierRepository supplierRepository;

    @Autowired
    ProjectRepositoryBase projectRepository;

    @Autowired
    GroupUsedForeignCurrencyRepository groupUsedForeignCurrencyRepository;

    @Autowired
    UsedForeignCurrencyRepository usedForeignCurrencyRepository;

    @Autowired
    SysAccountRepository accountRepository;

    @Autowired
    SysAccAuthRepository sysAccAuthRepository;  // 暂时弃用

    @Autowired
    GroupProjectColumnRepository groupProjectColumnRepository;

    @Autowired
    ProjectColumnRepository projectColumnRepository;

    @Autowired
    R2dbcRouterLoader r2dbcRouterLoader;

    @Autowired
    AccTemplateRepository templateRepository;


    @Autowired
    GroupCodeKemuOrgRepository groupCodeKemuOrgRepository;


    @Autowired
    AccountAccvoucherCdigestRepository accvoucherCdigestRepository;

    @Autowired
    GroupFaAccountRepository groupFaAccountRepository;

    @Autowired
    GroupUserOperatAuthZtRepository groupUserOperatAuthZtRepository;
    @Autowired
    GroupUserOperatAuthRepository groupUserOperatAuthRepository;

    @Override
    public Mono<Integer> insertAccountsData(GroupSysAccount sysAccount, List<String> supers) {//
        AtomicReference<String> accUserName = new AtomicReference<>(sysAccount.getAccId() + "-" + sysAccount.getStartPeriod().substring(0, 4));
        Boolean isIndependent = sysAccount.getIndependent().equals("1"); // 是否为独立账套
        Mono<Integer> sqlMono = databaseClient.sql("CREATE USER \"%s\" WITH PASSWORD 'Sigoo@@123';".replaceAll("%s", accUserName.get())).fetch().rowsUpdated().flatMap(item -> databaseClient.sql("CREATE TABLE \"accvoucher_%s\" PARTITION OF accvoucher FOR VALUES in ('%s');".replaceAll("%s", accUserName.get())).fetch().rowsUpdated()).flatMap(item -> databaseClient.sql("GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO \"%s\";".replaceAll("%s", accUserName.get())).fetch().rowsUpdated()).flatMap(item -> databaseClient.sql("select tablename from pg_tables where schemaname='public'").fetch().all().map(item2 -> item2.get("tablename")).filter(item3 -> !item3.toString().contains("_app_group")).collectList().map(item2 -> item2).flatMapMany(Flux::fromIterable).map(item4 -> "create policy \"" + accUserName + "-table-" + item4 + "\" on \"" + item4 + "\"  for all to \"" + accUserName + "\" using (tenant_id  = '" + accUserName + "');").flatMap(item5 -> {
            return DatabaseClient.create(databaseClient.getConnectionFactory()).sql(item5).fetch().rowsUpdated().onErrorContinue((a, b) -> {
                System.out.printf("");
            });
        }).then(Mono.just(item)));
        Mono<Integer> dataMono = Mono.just(0).flatMap(item -> {
            // 账套表，会计科目，凭证类别，结算方式，数据精度，常用外币，客户分类，供应商分类，员工类别、项目大类，项目分类
            Mono<SysAccount> zero = Mono.just("").flatMap(l -> {
                SysAccount sysAccount1 = new SysAccount();
                BeanUtil.copyProperties(sysAccount, sysAccount1);
                sysAccount1.setId(null);
                return accountRepository.save(sysAccount1);
            });
            Mono<List<AccTemplate>> thisAccTeMon = templateRepository.findById(sysAccount.getAccStandard()).flatMapMany(Flux::just).collectList();
            // 会计科目
            Mono<Integer> one = sysAccount.getIbudgetAccStandard().equals("1") ? accountsCodeKemuInit(sysAccount.getAccGroup(), sysAccount.getStartPeriod().substring(0, 4), sysAccount.getAccStandard()) :
                    // 集团账套
                    Mono.just(0);
            // 账套年度 存在补充 不存在添加
            Mono<SysAccountPeriod> two = accountPeriodRepository.findAllByAccountCoCodeOrderByAccountYearDesc(sysAccount.getCoCode()).collectList().map(list -> getSysAccountPeriod(sysAccount, list)).flatMap(e -> accountPeriodRepository.save(e));
            // 账套期间
            Mono<List<SysPeriod>> six = dataCollectionDuringGeneration(sysAccount).collectList().flatMap(list2 -> sysPeriodRepository.saveAll(list2).collectList());
            Mono<List<SettModes>> three = groupSysSettModesRepository.findAll().map(enti -> { // 初始化结算方式
                SettModes settModes = new SettModes();
                BeanUtil.copyProperties(enti, settModes);
                settModes.setId(null);
                return settModes;
            }).collectList().flatMap(list -> settModesRepository.saveAll(list).collectList());
            Mono<VoucherType> four = voucherTypeRepository.save(new VoucherType("01", "记", "记账凭证", "1", "无限制"));
            // 现金流量项目
            Mono<List<ProjectCash>> five = thisAccTeMon.map(list -> list.get(0).getUniqueAccStandard()).flatMap(accSt -> (!isIndependent ? originProjectCashRepository.findByAccStandardOrderById(accSt) : originProjectCashRepository.findByAccStandardOrderById(accSt)).map(enti -> {
                ProjectCash settModes = new ProjectCash();
                BeanUtil.copyProperties(enti, settModes);
                settModes.setId(null);
                return settModes;
            }).collectList().flatMap(list -> projectCashRepository.saveAll(list).collectList()));
            //  员工类别
            Mono<List<SysPsnType>> seven = !isIndependent ? groupSysPsnTypeRepository.findAll().map(entity -> {
                entity.setId(null);
                entity.setUniqueCode(IdUtil.objectId());
                SysPsnType psnType = new SysPsnType();
                BeanUtil.copyProperties(entity, psnType);
                return psnType;
            }).collectList().flatMap(list -> sysPsnTypeRepository.saveAll(list).collectList()) : sysPsnTypeRepository.save(new SysPsnType("9999", "9999", "未分配")).flatMap(entity -> Flux.just(entity).collectList());
            // 客户分类
            Mono<List<CustomerClass>> eight = !isIndependent ? groupCustomerClassRepository.findAll().collectList().map(list -> {
                for (GroupCustomerClass groupCustomerClass : list) {
                    groupCustomerClass.setId(null);
                    if (groupCustomerClass.getCusBend().equals("0")) { // 非末级
                        assemblyLevelDataCus(list, groupCustomerClass);
                    } else {
                        groupCustomerClass.setUniqueCustclass(IdUtil.objectId());
                    }
                }
                return list;
            }).flatMap(list -> customerClassRepository.saveAll(list.stream().map(entiry -> {
                CustomerClass customerClass = new CustomerClass();
                BeanUtil.copyProperties(entiry, customerClass);
                return customerClass;
            }).collect(Collectors.toList())).collectList()) : customerClassRepository.save(new CustomerClass("9999", "9999", "1", "0", "未分配", "1", "1", "001")).flatMap(entity -> Flux.just(entity).collectList());
            // 供应商分类
            Mono<List<SupplierClass>> nine = !isIndependent ? groupSupplierClassRepository.findAll().collectList().map(list -> {
                for (GroupSupplierClass groupCustomerClass : list) {
                    groupCustomerClass.setId(null);
                    if (groupCustomerClass.getCusBend().equals("0")) { // 非末级
                        assemblyLevelDataSup(list, groupCustomerClass);
                    } else {
                        groupCustomerClass.setUniqueCustclass(IdUtil.objectId());
                    }
                }
                return list;
            }).flatMap(list -> supplierClassRepository.saveAll(list.stream().map(entiry -> {
                SupplierClass customerClass = new SupplierClass();
                BeanUtil.copyProperties(entiry, customerClass);
                return customerClass;
            }).collect(Collectors.toList())).collectList()) : supplierClassRepository.save(new SupplierClass("9999", "9999", "1", "未分配", "1", "1", "0", "001")).flatMap(entity -> Flux.just(entity).collectList());
            // 项目大类与项目分类
            Mono<List<ProjectCategory>> ten = !isIndependent ? groupProjectCategoryRepository.findAll().map(entiry -> {
                entiry.setId(null);
                ProjectCategory entiry1 = new ProjectCategory();
                BeanUtil.copyProperties(entiry, entiry1);
                return entiry1;
            }).collectList().flatMap(list -> projectCategoryRepository.saveAll(list).collectList().flatMap(rList -> groupProjectColumnRepository.findAll().map(gEntity -> {
                ProjectColumn entiry1 = new ProjectColumn();
                BeanUtil.copyProperties(gEntity, entiry1);
                entiry1.setId(null);
                return entiry1;
            }).collectList().flatMap(gList -> projectColumnRepository.saveAll(gList).collectList().flatMap(aList -> Mono.just(rList))))) : projectCategoryRepository.save(new ProjectCategory("01", "项目管理", "project_01", "1", "1")).flatMap(entity -> projectColumnRepository.saveAll(createList(entity.getProjectCateCode())).collectList().flatMap(o -> Flux.just(entity).collectList()));
            // 项目分类
            Mono<List<ProjectClass>> eleven = !isIndependent ? groupProjectClassRepository.findAll().map(entiry -> {
                entiry.setId(null);
                entiry.setUniqueCode(IdUtil.objectId());
                ProjectClass entiry1 = new ProjectClass();
                BeanUtil.copyProperties(entiry, entiry1);
                return entiry1;
            }).collectList().flatMap(list -> projectClassRepository.saveAll(list).collectList()) : Flux.just(new ProjectClass(IdUtil.objectId(), "1", "项目管理", "01", "0", "1"), new ProjectClass("9999", "9999", "未分配", "01", "0", "1")).collectList().flatMap(list -> projectClassRepository.saveAll(list).collectList());
            // 部门
            Mono<SysDepartment> twelve = departmentRepository.save(new SysDepartment("9999", "9999", "未分配", "0", DateUtil.now(), "1"));
            // 常用外币
            Mono<UsedForeignCurrency> thirteen = usedForeignCurrencyRepository.save(new UsedForeignCurrency("EUR", "欧元", "1EUR=100 euro cents（生丁）"));
            // 主管授权
            Mono<List<GroupSysAccAuth>> fourteen = null == supers ? Mono.just(new ArrayList<>()) : Flux.fromIterable(supers).map(str -> new GroupSysAccAuth().setSupervisor("1").setAccId(sysAccount.getAccId()).setUserNum(str).setIyear(sysAccount.getStartPeriod().substring(0, 4)).setAccvocherType("1").setCcodeAll("1")).collectList().flatMap(list -> sysAccAuthRepository.saveAll(list).collectList());
            return r2dbcRouterLoader.bind(() -> Mono.zip(one, two, three, four, five, six, seven, eight), R2dbcRouterBuilder.of("boozsoft-nc", "public", accUserName.get())).flatMap(onj -> r2dbcRouterLoader.bind(() -> Mono.zip(nine, ten, eleven, twelve, thirteen, zero, fourteen), R2dbcRouterBuilder.of("boozsoft-nc", "public", accUserName.get())).map(onj2 -> onj.getT1()));
        });
        Mono<Tuple2<List<GroupStockAccount>, List<GroupFaAccount>>> mono = groupStockAccountRepository.findAllByCoCode(sysAccount.getCoCode()).collectList().zipWith(groupFaAccountRepository.findAllByCoCode(sysAccount.getCoCode()).collectList());
        return mono.flatMap(dbCodes -> {
            if (dbCodes.getT1().size() > 0 || dbCodes.getT2().size() > 0) {
                accUserName.set(dbCodes.getT1().size() > 0 ? (dbCodes.getT1().get(0).getStockAccId() + "-" + dbCodes.getT1().get(0).getStartDate().substring(0, 4)) : (dbCodes.getT2().get(0).getFaAccId() + "-" + dbCodes.getT2().get(0).getStartYearMonth().substring(0, 4)));
                return dataMono.flatMap(i -> {// 统一账套名称
                        sysAccount.setAccNameFull(dbCodes.getT1().size() > 0?dbCodes.getT1().get(0).getStockAccName():dbCodes.getT2().get(0).getFaAccName());
                    return groupSysAccountRepository.save(sysAccount).flatMap(e -> Mono.just(1));
                });
            } else {
                return sqlMono.flatMap(integer -> dataMono);
            }
        });
    }

    private SysAccountPeriod getSysAccountPeriod(GroupSysAccount sysAccount, List<SysAccountPeriod> list) {
        List<SysAccountPeriod> czs = list.stream().filter(it -> it.getAccountYear().equals(sysAccount.getStartPeriod().substring(0, 4))).collect(Collectors.toList());
        if (czs.size() > 0) {
            czs.get(0).setJici(sysAccount.getCcodeLevel());
            czs.get(0).setJiciLength(sysAccount.getCcodeLevel().replaceAll("-", "").length());
            return czs.get(0);
        } else {
            if (list.size() > 0) {
                return new SysAccountPeriod(sysAccount.getCoCode().substring(0, 2), sysAccount.getCoCode(), list.get(0).getAccountId(), sysAccount.getStartPeriod().substring(0, 4), list.get(0).getAccountMode(), sysAccount.getCcodeLevel(), sysAccount.getCcodeLevel().replaceAll("-", "").length());
            } else {
                return new SysAccountPeriod(sysAccount.getCoCode().substring(0, 2), sysAccount.getCoCode(), sysAccount.getAccId(), sysAccount.getStartPeriod().substring(0, 4), sysAccount.getAccId() + "-" + sysAccount.getStartPeriod().substring(0, 4), sysAccount.getCcodeLevel(), sysAccount.getCcodeLevel().replaceAll("-", "").length());
            }
        }
    }

    @Autowired
    private ColumnSettingsRepository columnSettingsRepository;

    @Override
    public Mono<Integer> removeAccountsData(GroupSysAccount sysAccount) {
        if (StrUtil.isBlank(sysAccount.getStartPeriod())) return Mono.just(1);
        AtomicReference<SysAccountPeriodVo> per = new AtomicReference<>(new SysAccountPeriodVo());
        Mono<Boolean> existMono = groupStockAccountRepository.findAllByCoCode(sysAccount.getCoCode()).collectList().zipWith(groupFaAccountRepository.findAllByCoCode(sysAccount.getCoCode()).collectList()).map(tip -> tip.getT1().size() > 0 || tip.getT2().size() > 0);
        return existMono.flatMap(exist -> accountPeriodRepository.findByCoCodeAndAccountYear(sysAccount.getCoCode(), sysAccount.getStartPeriod().substring(0, 4)).flatMap(entiry -> {
            per.set(entiry);
            Mono<Integer> dataMono = Mono.just(per.get()).flatMap(it -> {
                String accountMode = it.getAccountMode();
                String accountId = it.getAccountId();
                Mono<Void> zero = accountRepository.deleteByAccId(accountId); // 账套信息
                Mono<Void> one = exist ? Mono.just(null) : accountPeriodRepository.deleteAllByAccountCoCode(sysAccount.getCoCode()); // 账套年度
                Mono<Void> two = sysPeriodRepository.deleteAllByAccountId(accountId); // 账套期间
                Mono<Void> fourteen = exist ? Mono.just("").then() : groupUserOperatAuthZtRepository.findAllByZtUniqueCodeOrderByZtYearDesc(sysAccount.getCoCode()).collectList().flatMap(list -> {
                    if (list.size() > 0)
                        return groupUserOperatAuthRepository.findAllByUsrAuthIdInOrderByPlatformMarkAscFunctionIdAsc(list.stream().map(it1 -> it1.getId()).collect(Collectors.toList())).collectList().map(groupUserOperatAuthRepository::deleteAll).flatMap(v -> groupUserOperatAuthZtRepository.deleteAll(list));
                    return groupUserOperatAuthZtRepository.deleteAll(list);
                }); // ZZ账套授权
                Mono<Void> three = columnSettingsRepository.deleteAllByAccountId(accountId); // 栏目设置
                /*Mono<Void> three = codeKemuRepository.deleteAllByTenantId(accountMode); // 账套科目
                Mono<Void> four = settModesRepository.deleteAllByTenantId(accountMode); // 结算方式
                Mono<Void> five = voucherTypeRepository.deleteAllByTenantId(accountMode); // 凭证类型
                Mono<Void> six = projectCashRepository.deleteAllByTenantId(accountMode); // 现金流量项目
                Mono<Void> seven = sysPsnTypeRepository.deleteAllByTenantId(accountMode); // 人员类别
                Mono<Void> eight = customerClassRepository.deleteAllByTenantId(accountMode); // 客户类别
                Mono<Void> nine = supplierClassRepository.deleteAllByTenantId(accountMode); // 供应商类别
                Mono<Void> ten = projectCategoryRepository.deleteAllByTenantId(accountMode); // 项目大类
                Mono<Void> eleven = projectClassRepository.deleteAllByTenantId(accountMode); // 项目大类
                Mono<Void> twelve = departmentRepository.deleteAllByTenantId(accountMode); // 部门
                Mono<Void> thirteen = usedForeignCurrencyRepository.deleteAllByTenantId(accountMode); // 常用外币
                Mono<Void> fifteen = sysPsnRepository.deleteAllByTenantId(accountMode); // 人员
                Mono<Void> sixteen = customerRepository.deleteAllByTenantId(accountMode); // 客户
                Mono<Void> seventeen = supplierRepository.deleteAllByTenantId(accountMode); // 供应商
                Mono<Void> eighteen = projectRepository.deleteAllByTenantId(accountMode); // 项目目录
                Mono<Void> nineteen = projectColumnRepository.deleteAllByTenantId(accountMode); // 项目栏目 (存在问题待检查)*/
                // return r2dbcRouterLoader.bind(() -> Mono.zip(one, two, fourteen, four, five, six, seven, eight), R2dbcRouterBuilder.of("boozsoft-nc", "public", accountMode)).zipWith(r2dbcRouterLoader.bind(() -> Mono.zip(nine, ten, eleven, twelve, thirteen, zero, three, fifteen), R2dbcRouterBuilder.of("boozsoft-nc", "public", accountMode))).zipWith(r2dbcRouterLoader.bind(() -> Mono.zip(sixteen, seventeen, eighteen, nineteen), R2dbcRouterBuilder.of("boozsoft-nc", "public", accountMode))).thenReturn(1);
                Mono<Integer> delT = databaseClient.sql("select zt_table_name from _group_accounts_del_statistics where 1=1 and zt_type='ZZ'").fetch().all().map(item2 -> item2.get("zt_table_name")).collectList().map(item2 -> item2).flatMapMany(Flux::fromIterable).map(item4 -> "delete from " + item4 + " where tenant_id = \'" + accountMode + "\';") // 删除指定租户的数据
                        .flatMap(item5 -> DatabaseClient.create(databaseClient.getConnectionFactory()).sql(item5).fetch().rowsUpdated().onErrorContinue((a12, b21) -> {
                        })).then(Mono.just(1));
                return r2dbcRouterLoader.bind(() -> Mono.zip(zero, one, two, fourteen, delT, three), R2dbcRouterBuilder.of("boozsoft-nc", "public", accountMode)).thenReturn(1);
            });
            Mono<Integer> sqlMono = Mono.just(per.get()).flatMap(it -> {
                String accountMode = it.getAccountMode();
                return r2dbcRouterLoader.bind(() -> {
                    Mono<Integer> a = databaseClient.sql("select tablename from pg_tables where schemaname='public'").fetch().all().map(item2 -> item2.get("tablename")).filter(item3 -> !item3.toString().contains("_app_group")).collectList().map(item2 -> item2).flatMapMany(Flux::fromIterable).map(item4 -> "DROP policy IF EXISTS \"" + accountMode + "-table-" + item4 + "\" on \"" + item4 + "\";") // 删除行安全策略
                            .flatMap(item5 -> DatabaseClient.create(databaseClient.getConnectionFactory()).sql(item5).fetch().rowsUpdated().onErrorContinue((a12, b21) -> {
                            })).then(Mono.just(1));
                    Mono<Integer> b = databaseClient.sql("DROP TABLE IF EXISTS \"accvoucher_%s\";".replaceAll("%s", accountMode)).fetch().rowsUpdated().then(Mono.just(1));// 删除逻辑表
                    Mono<Integer> c = databaseClient.sql("DROP OWNED BY \"%s\";".replaceAll("%s", accountMode)).fetch().rowsUpdated().then(Mono.just(1));// 删除指定用户任何权限
                    Mono<Integer> d = databaseClient.sql("DROP USER IF EXISTS \"%s\";".replaceAll("%s", accountMode)).fetch().rowsUpdated().then(Mono.just(1)); // 用户
                    return Mono.zip(b, c);
                }, R2dbcRouterBuilder.ofDefault()).zipWith(r2dbcRouterLoader.bind(() -> databaseClient.sql("DROP USER IF EXISTS \"%s\";".replaceAll("%s", accountMode)).fetch().rowsUpdated().then(Mono.just(1)), R2dbcRouterBuilder.ofDefault())).then(Mono.just(1));
            }).defaultIfEmpty(1);
            return exist ? dataMono : dataMono.flatMap(integer -> sqlMono);
        }));
    }

    @Override
    public Mono<Integer> copyAccountsData(GroupSysAccount sysAccount, GroupSysAccount oldAccount, List<String> supers) {//
        AtomicReference<String> accUserName = new AtomicReference<>(sysAccount.getAccId() + "-" + sysAccount.getStartPeriod().substring(0, 4));
        String oldAccName = oldAccount.getAccId() + "-" + oldAccount.getStartPeriod().substring(0, 4);
        Boolean isIndependent = sysAccount.getIndependent().equals("1"); // 是否为独立账套
        Mono<Integer> sqlMono = databaseClient.sql("CREATE USER \"%s\" WITH PASSWORD 'Sigoo@@123';".replaceAll("%s", accUserName.get())).fetch().rowsUpdated().flatMap(item -> databaseClient.sql("CREATE TABLE \"accvoucher_%s\" PARTITION OF accvoucher FOR VALUES in ('%s');".replaceAll("%s", accUserName.get())).fetch().rowsUpdated()).flatMap(item -> databaseClient.sql("GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO \"%s\";".replaceAll("%s", accUserName.get())).fetch().rowsUpdated()).flatMap(item -> databaseClient.sql("select tablename from pg_tables where schemaname='public'").fetch().all().map(item2 -> item2.get("tablename")).filter(item3 -> !item3.toString().contains("_app_group")).collectList().map(item2 -> item2).flatMapMany(Flux::fromIterable).map(item4 -> "create policy \"" + accUserName + "-table-" + item4 + "\" on \"" + item4 + "\"  for all to \"" + accUserName + "\" using (tenant_id  = '" + accUserName + "');").flatMap(item5 -> {
            return DatabaseClient.create(databaseClient.getConnectionFactory()).sql(item5).fetch().rowsUpdated().onErrorContinue((a, b) -> {
                System.out.printf("");
            });
        }).then(Mono.just(item)));
        Mono<Integer> dataMono = Mono.just(1).flatMap(item -> {
            // 查询所有旧的数据
            Mono<List<AccountAccvoucherCdigest>> zero = accvoucherCdigestRepository.findAll().collectList();// 凭证摘要
            Mono<List<SettModes>> one = settModesRepository.findAll().collectList(); // 结算
            Mono<List<ProjectCash>> two = projectCashRepository.findAll().collectList(); // 现金银行
            Mono<List<VoucherType>> three = voucherTypeRepository.findAll().collectList(); // 凭证类型
            Mono<List<UsedForeignCurrency>> four = usedForeignCurrencyRepository.findAll().collectList(); // 常用外币
            Mono<List<SysPsnType>> five = sysPsnTypeRepository.findAll().collectList(); // 员工类别
            Mono<List<CustomerClass>> six = customerClassRepository.findAll().collectList(); // 客户分类
            Mono<List<SupplierClass>> seven = supplierClassRepository.findAll().collectList(); // 供应商分类
            Mono<List<ProjectClass>> eight = projectClassRepository.findAll().collectList(); // 项目分类

            Mono<List<SysDepartment>> nine = departmentRepository.findAll().collectList(); // 部门
            Mono<List<SysPsn>> ten = sysPsnRepository.findAll().collectList();// 人员
            Mono<List<Customer>> eleven = customerRepository.findAll().collectList();// 客户
            Mono<List<Supplier>> twelve = supplierRepository.findAll().collectList();// 供应商
            Mono<List<ProjectCategory>> thirteen = projectCategoryRepository.findAll().collectList();// 项目大类
            Mono<List<Project>> fourteen = projectRepository.findAll().collectList();// 项目目录
            Mono<List<CodeKemu>> fifteen = codeKemuRepository.findAll().collectList();// 会计科目
            return r2dbcRouterLoader.bind(() -> Mono.zip(one, two, three, four, five, six, seven, eight), R2dbcRouterBuilder.of("boozsoft-nc", "public", oldAccName)).flatMap(onjOne -> r2dbcRouterLoader.bind(() -> Mono.zip(zero, nine, ten, eleven, twelve, thirteen, fourteen, fifteen), R2dbcRouterBuilder.of("boozsoft-nc", "public", oldAccName)).map(onjTow -> Tuples.of(onjOne, onjTow)));

        }).flatMap(tupleTwo -> {
            // 旧账套
            Tuple8<List<SettModes>, List<ProjectCash>, List<VoucherType>, List<UsedForeignCurrency>, List<SysPsnType>, List<CustomerClass>, List<SupplierClass>, List<ProjectClass>> t1 = tupleTwo.getT1();
            Tuple8<List<AccountAccvoucherCdigest>, List<SysDepartment>, List<SysPsn>, List<Customer>, List<Supplier>, List<ProjectCategory>, List<Project>, List<CodeKemu>> t2 = tupleTwo.getT2();
            // 账套信息表
            Mono<SysAccount> zero = Mono.just("").flatMap(l -> {
                SysAccount sysAccount1 = new SysAccount();
                BeanUtil.copyProperties(sysAccount, sysAccount1);
                sysAccount1.setId(null);
                return accountRepository.save(sysAccount1);
            });
            // 账套期间
            Mono<List<SysPeriod>> one = dataCollectionDuringGeneration(sysAccount).collectList().flatMap(list2 -> sysPeriodRepository.saveAll(list2).collectList());
            // 账套年度
            Mono<SysAccountPeriod> two = accountPeriodRepository.findAllByAccountCoCodeOrderByAccountYearDesc(sysAccount.getCoCode()).collectList().map(list -> getSysAccountPeriod(sysAccount, list)).flatMap(e -> accountPeriodRepository.save(e));
            //结算方式
            Mono<List<SettModes>> three = Flux.fromIterable(t1.getT1()).map(it -> {
                it.setId(null);
                it.setTenantId(null);
                return it;
            }).collectList().flatMap(list -> settModesRepository.saveAll(list).collectList());
            // 凭证类别
            Mono<List<VoucherType>> four = Flux.fromIterable(t1.getT3()).map(it -> {
                it.setId(null);
                return it;
            }).collectList().flatMap(list -> voucherTypeRepository.saveAll(list).collectList());
            // 现金流量项目
            Mono<List<ProjectCash>> five = Flux.fromIterable(t1.getT2()).map(it -> {
                it.setId(null);
                it.setTenantId(null);
                return it;
            }).collectList().flatMap(list -> projectCashRepository.saveAll(list).collectList());
            // 常用外币
            Mono<List<UsedForeignCurrency>> six = Flux.fromIterable(t1.getT4()).map(it -> {
                it.setId(null);
                it.setTenantId(null);
                return it;
            }).collectList().flatMap(list -> usedForeignCurrencyRepository.saveAll(list).collectList());
            // 员工类别
            Mono<List<SysPsnType>> seven = Flux.fromIterable(t1.getT5()).map(it -> {
                it.setId(null);
                return it;
            }).collectList().flatMap(list -> sysPsnTypeRepository.saveAll(list).collectList());
            // 客户分类
            Mono<List<CustomerClass>> eight = Flux.fromIterable(t1.getT6()).map(it -> {
                it.setId(null);
                return it;
            }).collectList().flatMap(list -> customerClassRepository.saveAll(list).collectList());
            // 供应商分类
            Mono<List<SupplierClass>> nine = Flux.fromIterable(t1.getT7()).map(it -> {
                it.setId(null);
                return it;
            }).collectList().flatMap(list -> supplierClassRepository.saveAll(list).collectList());
            // 项目分类
            Mono<List<ProjectClass>> ten = Flux.fromIterable(t1.getT8()).map(it -> {
                it.setId(null);
                return it;
            }).collectList().flatMap(list -> projectClassRepository.saveAll(list).collectList());
            // 常用摘要
            Mono<List<AccountAccvoucherCdigest>> eleven = Flux.fromIterable(t2.getT1()).map(it -> {
                it.setId(null);
                return it;
            }).collectList().flatMap(list -> accvoucherCdigestRepository.saveAll(list).collectList());
            // 部门
            Mono<List<SysDepartment>> twelve = Flux.fromIterable(t2.getT2()).map(it -> {
                it.setId(null);
                it.setTenantId(null);
                return it;
            }).collectList().flatMap(list -> departmentRepository.saveAll(list).collectList());
            // 人员
            Mono<List<SysPsn>> thirteen = Flux.fromIterable(t2.getT3()).map(it -> {
                it.setId(null);
                return it;
            }).collectList().flatMap(list -> sysPsnRepository.saveAll(list).collectList());
            // 客户
            Mono<List<Customer>> fourteen = Flux.fromIterable(t2.getT4()).map(it -> {
                it.setId(null);
                it.setTenantId(null);
                return it;
            }).collectList().flatMap(list -> customerRepository.saveAll(list).collectList());
            // 供应商
            Mono<List<Supplier>> fifteen = Flux.fromIterable(t2.getT5()).map(it -> {
                it.setId(null);
                it.setTenantId(null);
                return it;
            }).collectList().flatMap(list -> supplierRepository.saveAll(list).collectList());
            // 项目大类 及目录
            Mono<List<ProjectCategory>> sixteen = Flux.fromIterable(t2.getT6()).map(it -> {
                it.setId(null);
                it.setTenantId(null);
                return it;
            }).collectList().flatMap(list -> projectCategoryRepository.saveAll(list).collectList());
            Mono<List<Project>> seventeen = Flux.fromIterable(t2.getT7()).map(it -> {
                it.setId(null);
                it.setTenantId(null);
                return it;
            }).collectList().flatMap(list -> projectRepository.saveAll(list).collectList());
            // 主管授权
            Mono<List<GroupSysAccAuth>> eighteen = (null == supers) ? Mono.just(new ArrayList<>()) : Flux.fromIterable(supers).map(str -> new GroupSysAccAuth().setSupervisor("1").setAccId(sysAccount.getAccId()).setUserNum(str).setIyear(sysAccount.getStartPeriod().substring(0, 4)).setAccvocherType("1").setCcodeAll("1")).collectList().flatMap(list -> sysAccAuthRepository.saveAll(list).collectList());
            // 会计科目
            Mono<List<CodeKemu>> nineteen = Flux.fromIterable(t2.getT8()).map(it -> {
                it.setId(null);
                it.setTenantId(null);
                return it;
            }).collectList().flatMap(list -> codeKemuRepository.saveAll(list).collectList());
            // 自定义档案 twenty
            return r2dbcRouterLoader.bind(() -> Mono.zip(zero, one, two, three, four, five, six, seven), R2dbcRouterBuilder.of("boozsoft-nc", "public", accUserName.get())).flatMap(onj -> r2dbcRouterLoader.bind(() -> Mono.zip(eight, nine, ten, eleven, twelve, thirteen, fourteen, fifteen), R2dbcRouterBuilder.of("boozsoft-nc", "public", accUserName.get())).flatMap(onj2 -> r2dbcRouterLoader.bind(() -> Mono.zip(sixteen, seventeen, eighteen, nineteen), R2dbcRouterBuilder.of("boozsoft-nc", "public", accUserName.get())).map(onj3 -> 1)));
        });
        return groupFaAccountRepository.findAllByCoCode(sysAccount.getCoCode()).collectList().flatMap(dbCodes -> {
            if (dbCodes.size() > 0) {
                accUserName.set(dbCodes.get(0).getFaAccId() + "-" + dbCodes.get(0).getStartYearMonth().substring(0, 4));
                return dataMono;
            } else {
                return sqlMono.flatMap(integer -> dataMono);
            }
        });
    }

    private List<ProjectColumn> createList(String projectCateCode) {
        List<ProjectColumn> columns = JSON.parseArray("[{\n" + "    num: '1',\n" + "    cname: '项目编号',\n" + "    ctitle:'project_code',\n" + "    ctype: '1',\n" + "    clength: '60',\n" + "    sourceType: '1',\n" + "    sourceName: '',\n" + "    sourceColumn: '',\n" + "    islist:'1'\n" + "  },{\n" + "    num: '2',\n" + "    cname: '项目名称',\n" + "    ctitle:'project_name',\n" + "    ctype: '1',\n" + "    clength: '60',\n" + "    sourceType: '1',\n" + "    sourceName: '',\n" + "    sourceColumn: '',\n" + "    islist:'1'\n" + "  },{\n" + "    num: '3',\n" + "    cname: '所属分类码',\n" + "    ctitle:'project_class_code',\n" + "    ctype: '1',\n" + "    clength: '60',\n" + "    sourceType: '2',\n" + "    sourceName: 'project_class',\n" + "    sourceColumn: 'project_class_name',\n" + "    islist:'1'\n" + "  },{\n" + "    num: '4',\n" + "    cname: '是否结算',\n" + "    ctitle:'jiesuan',\n" + "    ctype: '5',\n" + "    clength: '',\n" + "    sourceType: '1',\n" + "    sourceName: '',\n" + "    sourceColumn: '',\n" + "    islist:'1'\n" + "  },{\n" + "    num: '5',\n" + "    cname: '开始日期',\n" + "    ctitle:'start_date',\n" + "    ctype: '4',\n" + "    clength: '',\n" + "    sourceType: '1',\n" + "    sourceName: '',\n" + "    sourceColumn: '',\n" + "    islist:'1'\n" + "  },{\n" + "    num: '6',\n" + "    cname: '结束日期',\n" + "    ctitle:'end_date',\n" + "    ctype: '4',\n" + "    clength: '',\n" + "    sourceType: '1',\n" + "    sourceName: '',\n" + "    sourceColumn: '',\n" + "    islist:'1'\n" + "  },{\n" + "    num: '7',\n" + "    cname: '项目负责人',\n" + "    ctitle:'psn_in_charge',\n" + "    ctype: '1',\n" + "    clength: '60',\n" + "    sourceType: '2',\n" + "    sourceName: 'sys_psn',\n" + "    sourceColumn: 'psn_name',\n" + "    islist:'1'\n" + "  },{\n" + "    num: '8',\n" + "    cname: '所属部门',\n" + "    ctitle:'dept_code',\n" + "    ctype: '1',\n" + "    clength: '60',\n" + "    sourceType: '2',\n" + "    sourceName: 'sys_department',\n" + "    sourceColumn: 'dept_name',\n" + "    islist:'1'\n" + "  }]", ProjectColumn.class);
        for (ProjectColumn column : columns) {
            column.setProjectCateCode(projectCateCode);
            if (column.getCtitle() == null || column.getCtitle().equals("")) {
                column.setCtitle(PinyinUtil.getPinyin(column.getCname(), ""));
            }

            if (column.getSourceType().equals("2")) {
                column.setSourceColumnUnique("unique_code");
            } else {
                column.setSourceColumnUnique(null);
            }
            column.setSuccessState("1");
            switch (column.getCtype()) {
                case "1":
                    column.setShuxing("Input");
                    break;
                case "2":
                    column.setShuxing("InputNumber");
                    break;
                case "3":
                    column.setShuxing("InputNumber");
                    break;
                case "4":
                    column.setShuxing("DatePicker");
                    break;
                case "5":
                    column.setShuxing("Checkbox");
                    break;
            }
        }
        return columns;
    }


    private void assemblyLevelDataCus(List<GroupCustomerClass> list, GroupCustomerClass groupCustomerClass) {
        String s = IdUtil.objectId();
        String oldV = groupCustomerClass.getUniqueCustclass();
        groupCustomerClass.setUniqueCustclass(s);
        for (GroupCustomerClass customerClass : list) {
            if (customerClass.getParentId().equals(oldV)) customerClass.setParentId(s);
        }
    }

    private void assemblyLevelDataSup(List<GroupSupplierClass> list, GroupSupplierClass groupCustomerClass) {
        String s = IdUtil.objectId();
        String oldV = groupCustomerClass.getUniqueCustclass();
        groupCustomerClass.setUniqueCustclass(s);
        for (GroupSupplierClass customerClass : list) {
            if (customerClass.getParentId().equals(oldV)) customerClass.setParentId(s);
        }
    }

    public Flux<SysPeriod> dataCollectionDuringGeneration(GroupSysAccount sysAccount) {
        String startDate = sysAccount.getYearStartDate();
        String periodNum = sysAccount.getPeriodNum();
        String periodMonth = sysAccount.getStartPeriod();
        String accId = sysAccount.getAccId();
        String year = startDate.substring(0, 4);
        String qYear = startDate.substring(0, 4);
        int number = Integer.parseInt(periodNum);
        List<SysPeriod> list = new ArrayList<>();
        int index = 0;
        for (int i = 1; i <= number; i++) {
            String num = i > 9 ? "" + i : "0" + i;
            SysPeriod period = new SysPeriod();
            period.setAccountId(accId);
            period.setIyear(year);
            period.setPeriod(qYear + num);
            period.setIperiodNum(num);
            period.setDateStart(DateUtil.parseDate(startDate).toString("MM-dd"));
            if (number != 12 && Integer.parseInt(num) >= 12) {//12~16  13 -12 =1
                int offset = 12 - number + index;
                if (Integer.parseInt(num) == 12) {
                    period.setDateEnd(DateUtil.offsetDay(DateUtil.endOfMonth(DateUtil.parseDate(startDate)), offset).toString("MM-dd"));
                } else {
                    period.setDateStart(DateUtil.offsetDay(DateUtil.endOfMonth(DateUtil.parseDate(startDate)), offset).toString("MM-dd"));
                    period.setDateEnd(DateUtil.offsetDay(DateUtil.endOfMonth(DateUtil.parseDate(startDate)), offset).toString("MM-dd"));
                }
                index++;
            } else {
                period.setDateEnd(DateUtil.endOfMonth(DateUtil.parseDate(startDate)).toString("MM-dd"));
                startDate = DateUtil.offsetMonth(DateUtil.parseDate(startDate), 1).toString("yyyy-MM-dd");
                year = startDate.substring(0, 4);
            }
            if (period.getPeriod().equals(periodMonth)) {
                period.setEnablePeriod("1");
            } else {
                period.setEnablePeriod("0");
            }
            list.add(period);

        }
        return Flux.fromIterable(list);
    }

    private Mono<Integer> accountsCodeKemuInit(String orgId, String year, String thisAcc) {
        Mono<List<CodeKemu>> listMono = StrUtil.isNotBlank(orgId) ? groupCodeKemuOrgRepository.findAllByIyearAndorgUniqueOrderByCcodeAsc(year, orgId).map(item -> {// 去组织
            item.setId(null);
            item.setUniqueCode(IdUtil.objectId());
            item.setIyear(year);
            CodeKemu code = new CodeKemu();
            BeanUtil.copyProperties(item, code);
            return code;
        }).collectList() : templateRepository.findById(thisAcc).flatMapMany(Flux::just).collectList().flatMap(list -> groupCodeKemuRepository.findAllByUniqueAccStandardAndTemplateIdOrderByCcodeAsc(list.get(0).getUniqueAccStandard(), list.get(0).getId()).map(item -> {
            item.setId(null); // 取集团
            item.setUniqueCode(IdUtil.objectId());
            item.setIyear(year);
            CodeKemu code = new CodeKemu();
            BeanUtil.copyProperties(item, code);
            return code;
        }).collectList());
        return listMono.flatMap(list -> (false ? Flux.just(new ArrayList<>()) : codeKemuRepository.saveAll(list)).collectList().map(list1 -> list1.size()));
    }


    @Override
    public Mono<Integer> resetKemuCode(GroupSysAccount entity) {
        return accountsCodeKemuInit(entity.getId(), entity.getStartPeriod(),/*entity.getAccStandard()*/null);
    }

    @Autowired
    GroupFaAccPeriodRepository groupFaAccPeriodRepository;

    @Autowired
    SysAccountPeriodRepository sysAccountPeriodRepository;

    @Autowired
    GroupSysAccountRepository groupSysAccountRepository;

    @Autowired
    GroupFaManageClassRepository groupFaManageClassRepository;

    @Override
    public Mono<Integer> insertFaAccountsData(GroupFaAccount dbEntity) {
        String year = dbEntity.getStartYearMonth().split("-")[0];
        // 获取指定公司代码的账套
        Mono<Boolean> existMono = groupStockAccountRepository.findAllByCoCode(dbEntity.getCoCode()).collectList().zipWith(groupSysAccountRepository.findAllByCoCode(dbEntity.getCoCode()).collectList()).map(tip -> tip.getT1().size() > 0 || tip.getT2().size() > 0);
        Mono<Tuple2<GroupFaAccPeriod, List<SysAccountPeriod>>> saveT = groupFaManageClassRepository.save(new GroupFaManageClass(null, dbEntity.getId(), "1", dbEntity.getFaCode(), DateUtil.now())).flatMap(dbEn -> groupFaAccPeriodRepository.save(generateFaPeriod(dbEntity, dbEn))).zipWith(sysAccountPeriodRepository.findAllByAccountCoCodeOrderByAccountYearDesc(dbEntity.getCoCode()).collectList());
        return existMono.flatMap(exist -> {
            if (exist) { // 相同使用旧
                return saveT.flatMap(ls -> ls.getT2().stream().filter(it -> it.getAccountYear().equals(year)).collect(Collectors.toList()).size() > 0 ? Mono.just(dbEntity) : sysAccountPeriodRepository.save(generateControl(dbEntity, ls.getT2())).thenReturn(dbEntity)).thenReturn(1);
            } else {// 不相同新建
                String accUserName = dbEntity.getFaAccId() + "-" + year;
                return databaseClient.sql("CREATE USER \"%s\" WITH PASSWORD 'Sigoo@@123';".replaceAll("%s", accUserName)).fetch().rowsUpdated().flatMap(item -> databaseClient.sql("CREATE TABLE \"accvoucher_%s\" PARTITION OF accvoucher FOR VALUES in ('%s');".replaceAll("%s", accUserName)).fetch().rowsUpdated()).flatMap(item -> databaseClient.sql("GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO \"%s\";".replaceAll("%s", accUserName)).fetch().rowsUpdated()).flatMap(item -> databaseClient.sql("select tablename from pg_tables where schemaname='public'").fetch().all().map(item2 -> item2.get("tablename")).filter(item3 -> !item3.toString().contains("_app_group")).collectList().map(item2 -> item2).flatMapMany(Flux::fromIterable).map(item4 -> "create policy \"" + accUserName + "-table-" + item4 + "\" on \"" + item4 + "\"  for all to \"" + accUserName + "\" using (tenant_id  = '" + accUserName + "');").flatMap(item5 -> {
                    return DatabaseClient.create(databaseClient.getConnectionFactory()).sql(item5).fetch().rowsUpdated().onErrorContinue((a, b) -> {
                    });
                }).then(Mono.just(item))).flatMap(a -> {
                    Mono<GroupFaAccPeriod> one = groupFaManageClassRepository.save(new GroupFaManageClass(null, dbEntity.getId(), "1", dbEntity.getFaCode(), DateUtil.now())).flatMap(dbEn -> groupFaAccPeriodRepository.save(generateFaPeriod(dbEntity, dbEn)));
                    Mono<SysAccountPeriod> two = sysAccountPeriodRepository.save(generateControl(dbEntity, new ArrayList<>()));
                    return r2dbcRouterLoader.bind(() -> Mono.zip(one, two), R2dbcRouterBuilder.of("boozsoft-nc", "public", accUserName)).thenReturn(1);
                });
            }
        });

    }


    @Autowired
    GroupStockPeriodRepository groupStockPeriodRepository;
    @Autowired
    GroupStockAccountRepository groupStockAccountRepository;

    @Override
    public Mono<Integer> removeFaAccountsData(GroupFaAccount sysAccount) {
        if (StrUtil.isBlank(sysAccount.getStartYearMonth())) return Mono.just(1);
        AtomicReference<SysAccountPeriodVo> per = new AtomicReference<>(new SysAccountPeriodVo());
        Mono<Boolean> existMono = groupStockAccountRepository.findAllByCoCode(sysAccount.getCoCode()).collectList().zipWith(groupSysAccountRepository.findAllByCoCode(sysAccount.getCoCode()).collectList()).map(tip -> tip.getT1().size() > 0 || tip.getT2().size() > 0);
        return existMono.flatMap(exist -> accountPeriodRepository.findByCoCodeAndAccountYear(sysAccount.getCoCode(), sysAccount.getStartYearMonth().substring(0, 4)).flatMap(entiry -> {
            per.set(entiry);
            Mono<Integer> dataMono = Mono.just(per.get()).flatMap(it -> {
                String accountMode = it.getAccountMode();
                String accountId = it.getAccountId();
                Mono<Void> one = exist ? Mono.just(null) : accountPeriodRepository.deleteAllByAccountCoCode(sysAccount.getCoCode()); // 账套年度
                Mono<Void> two = groupFaAccPeriodRepository.findAllByUniqueCodeOrderByIyearAscImonthAsc(sysAccount.getId()).collectList().flatMap(e -> groupFaAccPeriodRepository.deleteAll(e)); // 账套期间
                Mono<Void> three = groupFaManageClassRepository.findAllBySuperiorIdOrderByManageCodeAsc(sysAccount.getId()).collectList().flatMap(e -> groupFaManageClassRepository.deleteAll(e)); // 资产类别
                return r2dbcRouterLoader.bind(() -> Mono.zip(one, two, three), R2dbcRouterBuilder.of("boozsoft-nc", "public", accountMode)).thenReturn(1);
            });
            Mono<Integer> sqlMono = Mono.just(per.get()).flatMap(it -> {
                String accountMode = it.getAccountMode();
                return r2dbcRouterLoader.bind(() -> {
                    Mono<Integer> a = databaseClient.sql("select tablename from pg_tables where schemaname='public'").fetch().all().map(item2 -> item2.get("tablename")).filter(item3 -> !item3.toString().contains("_app_group")).collectList().map(item2 -> item2).flatMapMany(Flux::fromIterable).map(item4 -> "DROP policy IF EXISTS \"" + accountMode + "-table-" + item4 + "\" on \"" + item4 + "\";") // 删除行安全策略
                            .flatMap(item5 -> DatabaseClient.create(databaseClient.getConnectionFactory()).sql(item5).fetch().rowsUpdated().onErrorContinue((a12, b21) -> {
                            })).then(Mono.just(1));
                    Mono<Integer> b = databaseClient.sql("DROP TABLE IF EXISTS \"accvoucher_%s\";".replaceAll("%s", accountMode)).fetch().rowsUpdated().then(Mono.just(1));// 删除逻辑表
                    Mono<Integer> c = databaseClient.sql("DROP OWNED BY \"%s\";".replaceAll("%s", accountMode)).fetch().rowsUpdated().then(Mono.just(1));// 删除指定用户任何权限
                    Mono<Integer> d = databaseClient.sql("DROP USER IF EXISTS \"%s\";".replaceAll("%s", accountMode)).fetch().rowsUpdated().then(Mono.just(1)); // 用户
                    return Mono.zip(b, c);
                }, R2dbcRouterBuilder.ofDefault()).zipWith(r2dbcRouterLoader.bind(() -> databaseClient.sql("DROP USER IF EXISTS \"%s\";".replaceAll("%s", accountMode)).fetch().rowsUpdated().then(Mono.just(1)), R2dbcRouterBuilder.ofDefault())).then(Mono.just(1));
            }).defaultIfEmpty(1);
            return exist ? dataMono : dataMono.flatMap(integer -> sqlMono);
        }));
    }

    @Override
    public Mono<String> insertStockAccountsData(GroupStockAccount dbEntity) {
        String year = dbEntity.getStartDate().split("-")[0];
        AtomicReference<String> accUserName = new AtomicReference<>(dbEntity.getStockAccId() + "-" + year);
        Mono<Boolean> existMono = groupFaAccountRepository.findAllByCoCode(dbEntity.getCoCode()).collectList().zipWith(groupSysAccountRepository.findAllByCoCode(dbEntity.getCoCode()).collectList()).map(tip -> tip.getT1().size() > 0 || tip.getT2().size() > 0);
        return existMono.flatMap(exist -> {
            Mono<String> dataMono = sysAccountPeriodRepository.findAllByAccountCoCodeOrderByAccountYearDesc(dbEntity.getCoCode()).collectList().flatMap(codes -> {
                if (exist) accUserName.set(codes.get(0).getAccountMode());
                List<SysAccountPeriod> collect = codes.stream().filter(it -> it.getAccountYear().equals(year)).collect(Collectors.toList());
                Mono<List<GroupStockPeriod>> one = groupStockPeriodRepository.saveAll(generateAccountPeriod(dbEntity)).collectList();
                Mono<SysAccountPeriod> two = collect.size() > 0 ? Mono.just(new SysAccountPeriod()) : sysAccountPeriodRepository.save(generateStockControl(dbEntity, exist ? codes : new ArrayList<>()));
                return r2dbcRouterLoader.bind(() -> Mono.zip(one, two), R2dbcRouterBuilder.of("boozsoft-nc", "public", accUserName.get())).thenReturn(accUserName.get());
            });
            if (exist) { // 相同
                return dataMono;
            } else {
                Mono<Integer> sqlMono = databaseClient.sql("CREATE USER \"%s\" WITH PASSWORD 'Sigoo@@123';".replaceAll("%s", accUserName.get())).fetch().rowsUpdated().flatMap(item -> databaseClient.sql("CREATE TABLE \"accvoucher_%s\" PARTITION OF accvoucher FOR VALUES in ('%s');".replaceAll("%s", accUserName.get())).fetch().rowsUpdated()).flatMap(item -> databaseClient.sql("GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO \"%s\";".replaceAll("%s", accUserName.get())).fetch().rowsUpdated()).flatMap(item -> databaseClient.sql("select tablename from pg_tables where schemaname='public'").fetch().all().map(item2 -> item2.get("tablename")).filter(item3 -> !item3.toString().contains("_app_group")).collectList().map(item2 -> item2).flatMapMany(Flux::fromIterable).map(item4 -> "create policy \"" + accUserName.get() + "-table-" + item4 + "\" on \"" + item4 + "\"  for all to \"" + accUserName.get() + "\" using (tenant_id  = '" + accUserName.get() + "');").flatMap(item5 -> {
                    return DatabaseClient.create(databaseClient.getConnectionFactory()).sql(item5).fetch().rowsUpdated().onErrorContinue((a, b) -> {
                        System.out.printf("");
                    });
                }).then(Mono.just(item)));
                return sqlMono.flatMap(i -> dataMono);
            }
        });

    }

    @Override
    public Mono<Integer> removeStockAccountsData(GroupStockAccount dbEntity) {
        if (StrUtil.isBlank(dbEntity.getStartDate())) return Mono.just(1);
        AtomicReference<SysAccountPeriodVo> per = new AtomicReference<>(new SysAccountPeriodVo());
        Mono<Boolean> existMono = groupSysAccountRepository.findAllByCoCode(dbEntity.getCoCode()).collectList().zipWith(groupFaAccountRepository.findAllByCoCode(dbEntity.getCoCode()).collectList()).map(tip -> tip.getT1().size() > 0 || tip.getT2().size() > 0);
        return existMono.flatMap(exist -> accountPeriodRepository.findByCoCodeAndAccountYear(dbEntity.getCoCode(), dbEntity.getStartDate().substring(0, 4)).flatMap(entiry -> {
            per.set(entiry);
            Mono<Integer> dataMono = Mono.just(per.get()).flatMap(it -> {
                String accountMode = it.getAccountMode();
                Mono<GroupStockAccount> one = exist ? Mono.just(null) : accountPeriodRepository.deleteAllByAccountCoCode(dbEntity.getCoCode()).thenReturn(dbEntity); // 账套年度
                Mono<GroupStockAccount> two = groupStockPeriodRepository.findAllByUniqueCode(dbEntity.getId()).collectList().flatMap(groupStockPeriodRepository::deleteAll).thenReturn(dbEntity); // 账套期间
                return r2dbcRouterLoader.bind(() -> Mono.zip(one, two), R2dbcRouterBuilder.of("boozsoft-nc", "public", accountMode)).thenReturn(1);
            });
            Mono<Integer> sqlMono = Mono.just(per.get()).flatMap(it -> {
                String accountMode = it.getAccountMode();
                return r2dbcRouterLoader.bind(() -> {
                    Mono<Integer> a = databaseClient.sql("select tablename from pg_tables where schemaname='public'").fetch().all().map(item2 -> item2.get("tablename")).filter(item3 -> !item3.toString().contains("_app_group")).collectList().map(item2 -> item2).flatMapMany(Flux::fromIterable).map(item4 -> "DROP policy IF EXISTS \"" + accountMode + "-table-" + item4 + "\" on \"" + item4 + "\";") // 删除行安全策略
                            .flatMap(item5 -> DatabaseClient.create(databaseClient.getConnectionFactory()).sql(item5).fetch().rowsUpdated().onErrorContinue((a12, b21) -> {
                            })).then(Mono.just(1));
                    Mono<Integer> b = databaseClient.sql("DROP TABLE IF EXISTS \"accvoucher_%s\";".replaceAll("%s", accountMode)).fetch().rowsUpdated().then(Mono.just(1));// 删除逻辑表
                    Mono<Integer> c = databaseClient.sql("DROP OWNED BY \"%s\";".replaceAll("%s", accountMode)).fetch().rowsUpdated().then(Mono.just(1));// 删除指定用户任何权限
                    Mono<Integer> d = databaseClient.sql("DROP USER IF EXISTS \"%s\";".replaceAll("%s", accountMode)).fetch().rowsUpdated().then(Mono.just(1)); // 用户
                    return Mono.zip(b, c);
                }, R2dbcRouterBuilder.ofDefault()).zipWith(r2dbcRouterLoader.bind(() -> databaseClient.sql("DROP USER IF EXISTS \"%s\";".replaceAll("%s", accountMode)).fetch().rowsUpdated().then(Mono.just(1)), R2dbcRouterBuilder.ofDefault())).then(Mono.just(1));
            }).defaultIfEmpty(1);
            return exist ? dataMono : dataMono.flatMap(integer -> sqlMono);
        }));
    }

    private GroupFaAccPeriod generateFaPeriod(GroupFaAccount dbEntity, GroupFaManageClass dbEn) {
        GroupFaAccPeriod period = new GroupFaAccPeriod();
        period.setUniqueCode(dbEntity.getId());
        period.setClassUniqueCode(dbEn.getId());
        String[] yAndM = dbEntity.getStartYearMonth().split("-");
        period.setIyear(yAndM[0]);
        period.setImonth(yAndM[1]);
        period.setIsZhejiu("0");
        period.setIsFilledIn("0");
        period.setIsSettle("0");
        return period;
    }

    private SysAccountPeriod generateControl(GroupFaAccount dbEntity, List<SysAccountPeriod> oldList) {
        SysAccountPeriod period = new SysAccountPeriod();
        period.setCorpCoCode(dbEntity.getCoCode().substring(0, 2));
        period.setAccountCoCode(dbEntity.getCoCode());
        period.setAccountYear(dbEntity.getStartYearMonth().split("-")[0]);
        if (oldList.size() > 0) {
            period.setAccountId(oldList.get(0).getAccountId());
            period.setAccountMode(oldList.get(0).getAccountMode());
        } else {
            period.setAccountId(dbEntity.getFaAccId());
            period.setAccountMode(dbEntity.getFaAccId() + "-" + dbEntity.getStartYearMonth().split("-")[0]);
        }
        return period;
    }

    private SysAccountPeriod generateStockControl(GroupStockAccount dbEntity, List<SysAccountPeriod> oldList) {
        SysAccountPeriod period = new SysAccountPeriod();
        period.setCorpCoCode(dbEntity.getCoCode().substring(0, 2));
        period.setAccountCoCode(dbEntity.getCoCode());
        period.setAccountYear(dbEntity.getStartDate().split("-")[0]);
        if (oldList.size() > 0) {
            period.setAccountId(oldList.get(0).getAccountId());
            period.setAccountMode(oldList.get(0).getAccountMode());
        } else {
            period.setAccountId(dbEntity.getStockAccId());
            period.setAccountMode(dbEntity.getStockAccId() + "-" + period.getAccountYear().split("-")[0]);
        }
        return period;
    }

    private List<GroupStockPeriod> generateAccountPeriod(GroupStockAccount dbEntity) {
        String[] dates = dbEntity.getStartDate().split("-");
        String id = dbEntity.getId();
        List<GroupStockPeriod> list = new ArrayList<>();
        for (int i = Integer.parseInt(dates[1]); i <= 12; i++) {
            String num = i > 9 ? "" + i : "0" + i;
            GroupStockPeriod period = new GroupStockPeriod();
            period.setUniqueCode(id);
            period.setStockYear(dates[0]);
            period.setStockMonth(num);
            if (num.equals(dates[1]))period.setStockFlag("1");
            list.add(period);
        }
        return list;
    }
}
