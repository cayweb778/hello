package org.boozsoft.rest;

import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.SysLog;
import org.boozsoft.domain.entity.SysNotice;
import org.boozsoft.domain.entity.account.ProjectCategory;
import org.boozsoft.domain.entity.account.ProjectCategoryRollback;
import org.boozsoft.domain.entity.account.ProjectColumn;
import org.boozsoft.domain.vo.ProjectCategoryVo;
import org.boozsoft.repo.*;
import org.boozsoft.service.CustomerService;
import org.boozsoft.service.ProjectCategoryService;
import org.springbooz.core.tool.result.R;

import org.springbooz.datasource.r2dbc.annotation.SCHEMA_TYPE;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/sys/project/category")
public class SysProjectCategoryController {

    @Autowired
    DatabaseClient client;
    @Autowired
    GroupDistRepository groupDistRepository;
    @Autowired
    SysDistReocrdRepository sysDistReocrdRepository;
    @Autowired
    ProjectCategoryRepository projectCategoryRepository;
    @Autowired
    ProjectColumnRepository projectColumnRepository;
    @Autowired
    ProjectCategoryService projectCategoryService;
    @Autowired
    CustomerService customerService;
    @Autowired
    SysLogRepository sysLogRepository;
    @Autowired
    SysNoticeRepository sysNoticeRepository;
    @Autowired
    ProjectCategoryRollbackRepository projectCategoryRollbackRepository;

    /**
     * 查询  集团档案分配管控权限表
     * @return
     */
    @GetMapping("findByDatabaseUniqueCode")

    public Mono<R> findByDatabaseUniqueCode() {
        return groupDistRepository.findByDatabaseUniqueCodeAndTableName("abc001","project_category")
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findAll")
    @ApiOperation(value = "查询列表", notes = "传入code")

    public Mono<R> findAll(Pageable pageable) {
        return projectCategoryRepository.findAllByOrderBySuccessState(pageable).collectList().map(R::page);
    }

    @GetMapping("findById")
    @ApiOperation(value = "查询", notes = "传入code")

    public Mono findById(String id) {
//        Mono<List<ProjectColumn>> projectColumnListMono = R2dbcHelper.use("booz-nc", "booznc-group").exec(() -> projectColumnRepository.findAll()).collectList();
//        Mono<List<User>> userListMono=userRepository.findAll().collectList();
//
//        return userListMono.zipWith(projectColumnListMono,(userList,projectColumnList)->{
//            userList.stream().map(user->{
//                ProjectColumn a=projectColumnList.stream().filter(item->user.getId().equals(item.getId())).map(item->{});
////                item.setAvatar("aa");
////               return item;
//            });
//            projectColumnList.stream().map(item->{
//                item.setProjectCateCode("");
//                return item;
//            });
//            return R.ok().setResult(Map.of(
//                    "userList",userList,
//                    "pList",projectColumnList
//            ));
//        });
        Mono mono = projectCategoryRepository.findById(id);
        return mono.map(item->R.ok().setResult(item));
    }

    @GetMapping("findColumnByCate")
    @ApiOperation(value = "查询", notes = "传入code")

    public Mono<R> findColumnByCate(String projectCateCode) {
        return projectColumnRepository.findByProjectCateCodeOrderByNum(projectCateCode)
                .collectList()
                .map(item->R.ok().setResult(item));
    }

//    @PostMapping
//    @ApiOperation(value = "新增或修改", notes = "传入code")
//    public Mono save(@AuthenticationPrincipal DefaultOidcUser oauth2User,@RequestBody final ProjectCategoryVo vo) {
//        //新增
//        if (vo.getId() == null || vo.getId().equals("")) {
//            return add(oauth2User,vo);
//        }
//        //修改
//        return edit(vo);
//    }

//    /**
//     * 新增方法
//     * @param vo
//     * @return
//     */
//    public Mono add(DefaultOidcUser oauth2User, ProjectCategoryVo vo){
//        Object object=oauth2User.getClaims().get("prod_userinfo");
//        vo.setApplyDatabaseUniqueCode("abc001");//申请账套唯一码
//        vo.setApplyUser("test");//申请人
//        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
//        vo.setApplyDate(time);
//        vo.setBiandongMethod("1");
//        String year = new SimpleDateFormat("yyyy").format(new Date());
//        //系统操作日志
//        SysLog sysLog = new SysLog();
//        sysLog.setOperationDate(time);
//        sysLog.setUniqueCode("a001");
//        sysLog.setUserName("test");
//        sysLog.setAccId("bjxgkj-001");
//        sysLog.setIyear(year);
//        sysLog.setOperationCont("【"+sysLog.getUserName()+"在"+time+"】增加了一条项目大类信息,大类编码：【"+vo.getProjectCateCode()+"】,大类名称：【"+vo.getProjectCateName()+"】");
//        //通知集团操作员
//        SysNotice sysNotice = new SysNotice();
//        sysNotice.setCorpUniqueCode("abc001");
//        sysNotice.setUserUniqueCode("user001");
//        sysNotice.setNoticeType("增加");
//        sysNotice.setNoticeTime(time);
//        sysNotice.setNoticeContent(sysLog.getOperationCont());
//        sysNotice.setNoticeName("项目大类");
//        sysNotice.setNoticeCode("project_category");
//        sysNotice.setReceiveRole("001");
//        sysNotice.setReceiveUser("test");
//        sysNotice.setNoticeState("0");
//        //管控
//        if (vo.getIsControl().equals("1")){
//            //是否自动分配
//            //自动分配系统和账套同时添加
//            if (vo.getIsAuto().equals("1")){
//                vo.setSuccessState("1");
//                //系统增加大类并创建项目目录表和创建回滚表
//                return projectCategoryService.sys_save(vo)
//                        //账套增加大类并创建项目目录表和创建回滚表
//                        .flatMap(item -> projectCategoryService.acc_save(vo))
//                        //增加集团档案分配表
//                        .flatMap(item -> customerService.sysDistRecord_save("abc001", vo.getProjectCateCode(), "project_category"))
//                        //增加系统日志表
//                        .flatMap(item -> sysLogRepository.save(sysLog))
//                        //增加系统通知信息表
//                        .flatMap(item -> sysNoticeRepository.save(sysNotice))
//                        .map(o -> R.ok().setResult(o));
//            }
//            //不自动分配系统添加需要走审批
//            vo.setSuccessState("0");
//            sysLog.setAccId("booznc-group");
//            //系统增加大类
//            return projectCategoryService.save(vo)
//                    //增加系统日志表
//                    .flatMap(item -> sysLogRepository.save(sysLog))
//                    //增加系统通知信息表
//                    .flatMap(item -> sysNoticeRepository.save(sysNotice))
//                    .map(o -> R.ok().setResult(o));
//        }
//        //不管控
//        vo.setSuccessState("1");
//        //账套增加大类并创建项目目录表和创建回滚表
//        return projectCategoryService.acc_save(vo)
//                //增加系统日志表
//                .flatMap(item -> sysLogRepository.save(sysLog))
//                .map(o -> R.ok().setResult(o));
//    }

    /**
     * 修改方法
     * @param vo
     * @return
     */
    public Mono edit(ProjectCategoryVo vo){
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        vo.setBiandongMethod("2");
        String year = new SimpleDateFormat("yyyy").format(new Date());
        //系统操作日志
        SysLog sysLog = new SysLog();
        sysLog.setOperationDate(time);
        sysLog.setUniqueCode("a001");
        sysLog.setUserName("test");
        sysLog.setAccId("bjxgkj-001");
        sysLog.setIyear(year);
        sysLog.setOperationCont("【"+sysLog.getUserName()+"在"+time+"】修改了一条项目大类信息,大类编码：【"+vo.getProjectCateCode()+"】,大类名称：【"+vo.getProjectCateName()+"】");
        //通知集团操作员
        SysNotice sysNotice = new SysNotice();
        sysNotice.setCorpUniqueCode("abc001");
        sysNotice.setUserUniqueCode("user001");
        sysNotice.setNoticeType("修改");
        sysNotice.setNoticeTime(time);
        sysNotice.setNoticeContent(sysLog.getOperationCont());
        sysNotice.setNoticeName("项目大类");
        sysNotice.setNoticeCode("project_category");
        sysNotice.setReceiveRole("001");
        sysNotice.setReceiveUser("test");
        sysNotice.setNoticeState("0");
        //管控
        if (vo.getIsControl().equals("1")){
            //是否自动分配
            //自动分配系统和账套同时修改
            if (vo.getIsAuto().equals("1")){
                //系统修改大类，新增回滚表记录并创建项目目录表和创建回滚表
                return projectCategoryService.sys_edit(vo)
                        //账套修改大类，新增回滚表记录并创建项目目录表和创建回滚表
                        .flatMap(item -> projectCategoryService.acc_edit(vo))
                        //增加系统日志表
                        .flatMap(item -> sysLogRepository.save(sysLog))
                        //增加系统通知信息表
                        .flatMap(item -> sysNoticeRepository.save(sysNotice))
                        .map(o -> R.ok().setResult(o));
            }
            //不自动分配系统添加
            sysLog.setAccId("booznc-group");
            //生效时需要新增一条大类信息
            if (vo.getSuccessState().equals("1")) {
                vo.setId(null);
                vo.setSuccessState("0");
                vo.setApplyDate(time);
            }
            //系统增加大类
            return projectCategoryService.save(vo)
                    //增加系统日志表
                    .flatMap(item -> sysLogRepository.save(sysLog))
                    //增加系统通知信息表
                    .flatMap(item -> sysNoticeRepository.save(sysNotice))
                    .map(o -> R.ok().setResult(o));
        }
        //不管控
        //账套修改大类，新增回滚表记录并创建项目目录表和创建回滚表
        return projectCategoryService.acc_edit(vo)
                //增加系统日志表
                .flatMap(item -> sysLogRepository.save(sysLog))
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("sysSave")
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono sysSave(@RequestBody final ProjectCategoryVo vo) {
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String year = new SimpleDateFormat("yyyy").format(new Date());
        vo.setSuccessState("1");
        //系统操作日志
        SysLog sysLog = new SysLog();
        sysLog.setOperationDate(time);
        sysLog.setUniqueCode("admin");
        sysLog.setUserName("admin");
        sysLog.setAccId("booznc-group");
        sysLog.setIyear(year);
        //新增
        if (vo.getId() == null || vo.getId().equals("")) {
            sysLog.setOperationCont("【"+sysLog.getUserName()+"在"+time+"】增加了一条项目大类信息,大类编码：【"+vo.getProjectCateCode()+"】,大类名称：【"+vo.getProjectCateName()+"】");
            vo.setApplyUser("admin");//申请人
            vo.setApplyDate(time);
            vo.setBiandongMethod("1");
            //账套增加大类并创建项目目录表和创建回滚表
            return projectCategoryService.sys_save(vo)
                    //增加系统日志表
                    .zipWith(sysLogRepository.save(sysLog))
                    .map(o -> R.ok().setResult(o));
        }
        //修改
        sysLog.setOperationCont("【"+sysLog.getUserName()+"在"+time+"】修改了一条项目大类信息,大类编码：【"+vo.getProjectCateCode()+"】,大类名称：【"+vo.getProjectCateName()+"】");
        vo.setBiandongMethod("2");
        //如果分配账套修改
        if (vo.getApplyDatabaseUniqueCode()!=null && !vo.getApplyDatabaseUniqueCode().equals("")) {
            //通知集团操作员
            SysNotice sysNotice = new SysNotice();
            sysNotice.setCorpUniqueCode("abc001");
            sysNotice.setUserUniqueCode("admin");
            sysNotice.setNoticeType("修改");
            sysNotice.setNoticeTime(time);
            sysNotice.setNoticeContent(sysLog.getOperationCont());
            sysNotice.setNoticeName("项目大类");
            sysNotice.setNoticeCode("project_category");
            sysNotice.setReceiveRole("001");
            sysNotice.setReceiveUser("test");
            sysNotice.setNoticeState("0");
            //系统修改大类，新增回滚表记录并创建项目目录表和创建回滚表
            return projectCategoryService.sys_edit(vo)
                    //账套修改大类，新增回滚表记录并创建项目目录表和创建回滚表
                    .flatMap(item -> projectCategoryService.acc_edit(vo))
                    //增加系统日志表
                    .flatMap(item -> sysLogRepository.save(sysLog))
                    //增加系统通知信息表
                    .flatMap(item -> sysNoticeRepository.save(sysNotice))
                    .map(o -> R.ok().setResult(o));
        }
        //系统修改大类，新增回滚表记录并创建项目目录表和创建回滚表
        return projectCategoryService.sys_edit(vo)
                //增加系统日志表
                .zipWith(sysLogRepository.save(sysLog))
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("approve")
    @ApiOperation(value = "审批操作", notes = "传入code")
    public Mono approve(@RequestBody final ProjectCategoryVo vo) {
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String year = new SimpleDateFormat("yyyy").format(new Date());
        vo.setApproveDate(time);
        vo.setApproveUser("admin");
        //系统操作日志
        SysLog sysLog = new SysLog();
        sysLog.setOperationDate(time);
        sysLog.setUniqueCode("admin");
        sysLog.setUserName("admin");
        sysLog.setAccId("bjxgkj-001");
        sysLog.setIyear(year);
        sysLog.setOperationCont("【"+sysLog.getUserName()+"在"+time+"】审批了一条项目大类信息,大类编码：【"+vo.getProjectCateCode()+"】,大类名称：【"+vo.getProjectCateName()+"】");
        //通知集团操作员
        SysNotice sysNotice = new SysNotice();
        sysNotice.setCorpUniqueCode("abc001");
        sysNotice.setUserUniqueCode("admin");
        sysNotice.setNoticeType("审批");
        sysNotice.setNoticeTime(time);
        sysNotice.setNoticeContent(sysLog.getOperationCont()+",审批意见："+vo.getApproveIdea());
        sysNotice.setNoticeName("项目大类");
        sysNotice.setNoticeCode("project_category");
        sysNotice.setReceiveRole("001");
        sysNotice.setReceiveUser("test");
        sysNotice.setNoticeState("0");
        if (vo.getSuccessState().equals("0")){
            return sysLogRepository.save(sysLog)//增加系统通知信息表
                    .flatMap(item -> sysNoticeRepository.save(sysNotice))
                    .map(o -> R.ok().setResult(o));
        }
        //新增审批
        if (vo.getBiandongMethod().equals("1")){
            return projectCategoryService.sys_save(vo)
                    .flatMap(item -> projectCategoryService.acc_save(vo))
                    //增加系统日志表
                    .flatMap(item -> sysLogRepository.save(sysLog))
                    //增加系统通知信息表
                    .flatMap(item -> sysNoticeRepository.save(sysNotice))
                    .map(o -> R.ok().setResult(o));
        }
        //修改审批
        return projectCategoryService.sys_approval_edit(vo,"1","admin","admin")
                .flatMap(item -> projectCategoryService.acc_edit(vo))
                //增加系统日志表
                .flatMap(item -> sysLogRepository.save(sysLog))
                //增加系统通知信息表
                .flatMap(item -> sysNoticeRepository.save(sysNotice))
                .map(o -> R.ok().setResult(o));
    }

    @DeleteMapping
    @ApiOperation(value = "删除", notes = "传入code")

    public Mono delete(@RequestBody final ProjectCategory category) {
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String year = new SimpleDateFormat("yyyy").format(new Date());
//        return projectCategoryService.sys_delete(category)
//                .zipWith(projectCategoryService.acc_delete(category))
//                .then(Mono.just(R.ok()));
        //系统操作日志
        SysLog sysLog = new SysLog();
        sysLog.setOperationDate(time);
        sysLog.setUniqueCode("admin");
        sysLog.setUserName("admin");
        sysLog.setAccId("bjxgkj-001");
        sysLog.setIyear(year);
        if (category.getBiandongMethod().equals("2")) {
            sysLog.setOperationCont("【" + sysLog.getUserName() + "在" + time + "】删除了一条项目大类修改信息,大类编码：【" + category.getProjectCateCode() + "】,大类名称：【" + category.getProjectCateName() + "】");
        }
        sysLog.setOperationCont("【" + sysLog.getUserName() + "在" + time + "】删除了一条项目大类信息,大类编码：【" + category.getProjectCateCode() + "】,大类名称：【" + category.getProjectCateName() + "】");
        return projectCategoryService.notState_delete(category,"2","admin","admin")
                //增加系统日志表
                .flatMap(item -> sysLogRepository.save(sysLog))
                .map(o -> R.ok().setResult(o));
    }

    @DeleteMapping("deleteColumn")
    @ApiOperation(value = "删除", notes = "传入code")
    public Mono deleteColumn(@RequestBody ProjectColumn column) {
        return projectCategoryService.sys_deleteColumn(column.getId())
                .zipWith(projectCategoryService.acc_deleteColumn(column.getProjectCateCode(),column.getNum()))
                .then(Mono.just(R.ok()));
    }

    @GetMapping("fromColumnByCate")
    @ApiOperation(value = "查询", notes = "传入code")

    public Mono<R> fromColumnByCate(String projectCateCode) {
        return projectColumnRepository.findByProjectCateCodeAndIslistAndSuccessStateOrderByNum(projectCateCode,"1","1")
                .collectList()
                .map(item->R.ok().setResult(item));
    }

    @GetMapping("findByCode")

    public Mono<R> findByCode(String projectCateCode) {
        return projectCategoryRepository.findByProjectCateCodeOrderById(projectCateCode)
                .collectList()
                .map(item -> R.ok().setResult(item));
    }

    @GetMapping("findBySuccessStateAndFlag")

    public Mono<R> findBySuccessStateAndFlag() {
        return projectCategoryRepository.findBySuccessStateAndFlagOrderByProjectCateCode("1","1")
                .collectList()
                .map(item->R.ok().setResult(item));
    }

    @GetMapping("findByName")

    public Mono<R> findByName(String projectCateName) {
        return projectCategoryRepository.findByProjectCateNameOrderById(projectCateName)
                .collectList()
                .map(item -> R.ok().setResult(item));
    }

    @PostMapping("editFlag")
    @ApiOperation(value = "停用启用操作", notes = "传入code")

    public Mono editFlag(@RequestBody ProjectCategory category) {
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String year = new SimpleDateFormat("yyyy").format(new Date());
        ProjectCategoryRollback rollback = new ProjectCategoryRollback();
        BeanUtils.copyProperties(category, rollback);
        rollback.setId(null);
        rollback.setBiandongDate(time);
        rollback.setBiandongMethod("3");
        rollback.setBiandongUniqueCode("admin");
        rollback.setBiandongName("admin");
        //系统操作日志
        SysLog sysLog = new SysLog();
        sysLog.setOperationDate(time);
        sysLog.setUniqueCode("admin");
        sysLog.setUserName("admin");
        sysLog.setAccId("bjxgkj-001");
        sysLog.setIyear(year);
        if (category.getFlag().equals("1")){
            sysLog.setOperationCont("【"+sysLog.getUserName()+"在"+time+"】停用了一条项目大类信息,大类编码：【"+category.getProjectCateCode()+"】,大类名称：【"+category.getProjectCateName()+"】");
            category.setFlag("0");
        } else {
            sysLog.setOperationCont("【"+sysLog.getUserName()+"在"+time+"】启用了一条项目大类信息,大类编码：【"+category.getProjectCateCode()+"】,大类名称：【"+category.getProjectCateName()+"】");
            category.setFlag("1");
        }
        //大类回滚表保存记录
        Mono mono1 = projectCategoryRollbackRepository.save(rollback);
        //大类修改
        Mono mono2 = projectCategoryRepository.save(category);
        //增加系统日志表
        Mono mono3 = sysLogRepository.save(sysLog);
        return Mono.zip(mono1,mono2,mono3).map(item->R.ok().setResult(item));
    }

}
