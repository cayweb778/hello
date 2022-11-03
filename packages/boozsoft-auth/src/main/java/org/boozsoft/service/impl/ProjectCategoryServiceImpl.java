package org.boozsoft.service.impl;

import cn.hutool.extra.pinyin.PinyinUtil;
import org.boozsoft.domain.entity.account.ProjectCategory;
import org.boozsoft.domain.entity.account.ProjectCategoryRollback;
import org.boozsoft.domain.entity.account.ProjectColumn;
import org.boozsoft.domain.entity.account.ProjectColumnRollback;
import org.boozsoft.domain.vo.ProjectCategoryVo;
import org.boozsoft.repo.ProjectCategoryRepository;
import org.boozsoft.repo.ProjectCategoryRollbackRepository;
import org.boozsoft.repo.ProjectColumnRepository;
import org.boozsoft.repo.ProjectColumnRollbackRepository;
import org.boozsoft.service.ProjectCategoryService;
import org.springbooz.core.tool.result.R;

import org.springbooz.datasource.r2dbc.annotation.SCHEMA_TYPE;
import org.springbooz.datasource.r2dbc.domain.vo.R2dbcRouterBuilder;
import org.springbooz.datasource.r2dbc.service.R2dbcRouterLoader;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ProjectCategoryServiceImpl implements ProjectCategoryService {

    @Autowired
    ProjectCategoryRepository projectCategoryRepository;
    @Autowired
    ProjectColumnRepository projectColumnRepository;
    @Autowired
    DatabaseClient client;
    @Autowired
    ProjectCategoryRollbackRepository projectCategoryRollbackRepository;
    @Autowired
    ProjectColumnRollbackRepository projectColumnRollbackRepository;
    @Autowired
    R2dbcRouterLoader router;

    @Override
    
    public Mono acc_save(ProjectCategoryVo vo) {
        //清空vo对象ID
        vo.setId(null);
        for (ProjectColumn column : vo.getTable()) {
            vo.getTable().get(vo.getTable().indexOf(column)).setId(null);
        }
        //根据大类编码查询大类信息
        Mono<ProjectCategoryVo> mono1 = projectCategoryRepository.findByProjectCateCodeOrderById(vo.getProjectCateCode()).collectList().map(category -> {
            if (category.size() > 0) {
                vo.setId(category.get(0).getId());
            }
            return vo;
        });
        //根据大类编码查询栏目信息
        Mono<ProjectCategoryVo> mono2 = projectColumnRepository.findByProjectCateCodeOrderByNum(vo.getProjectCateCode()).collectList().map(columnList -> {
            for (int i = 0; i < columnList.size(); i++) {
                ProjectColumn column = columnList.get(i);
                //for (ProjectColumn column : columnList) {
                if (i < vo.getTable().size()) {
                    vo.getTable().get(i).setId(column.getId());
                }
            }
            return vo;
        });
        //保存vo对象
        Mono mono3 = save(vo);
        //创建项目目录表
//        Mono mono4 = createProject(vo);
        //创建项目目录回滚表
//        Mono mono5 = createProjectRollback(vo);
        return mono1
                .defaultIfEmpty(vo)
                .flatMap(item -> mono2)
                .defaultIfEmpty(vo)
                .flatMap(item -> mono3)
                .defaultIfEmpty(vo)
                .map(o -> R.ok().setResult(o));
    }

    @Override
    
    public Mono sys_save(ProjectCategoryVo vo) {
        Mono bind = router.bind(() -> {
                    return save(vo).map(o -> R.ok().setResult(o));
                },
                R2dbcRouterBuilder.r2dbcRouterBuider().database("boozsoft-nc").schema("booznc-group").build()
        );
        return bind;
//        return save(vo).map(o -> R.ok().setResult(o));
    }

    @Override
    
    public Mono acc_edit(ProjectCategoryVo vo) {
        //执行回滚操作
        Mono mono1 = save_rollback(vo, "1", "a001", "test");
        //清空vo对象ID
        vo.setId(null);
        for (ProjectColumn column : vo.getTable()) {
            vo.getTable().get(vo.getTable().indexOf(column)).setId(null);
        }
        //根据大类编码查询大类信息
        Mono mono2 = projectCategoryRepository.findByProjectCateCodeOrderById(vo.getProjectCateCode()).collectList().map(category -> {
            if (category.size() > 0) {
                vo.setId(category.get(0).getId());
            }
            return vo;
        });
        //根据大类编码查询栏目信息
        Mono mono3 = projectColumnRepository.findByProjectCateCodeOrderByNum(vo.getProjectCateCode()).collectList().map(columnList -> {
            for (int i = 0; i < columnList.size(); i++) {
                ProjectColumn column = columnList.get(i);
                //for (ProjectColumn column : columnList) {
                if (i < vo.getTable().size()) {
                    vo.getTable().get(i).setId(column.getId());
                }
            }
            return vo;
        });
        //保存vo对象
        Mono mono4 = save(vo);
        //创建项目目录表
//        Mono mono5 = addProjectColumn(vo);
        //创建项目目录回滚表
//        Mono mono6 = addProjectColumnRollback(vo);
        return mono1
                .defaultIfEmpty(vo)
                .flatMap(item -> mono2)
                .defaultIfEmpty(vo)
                .flatMap(item -> mono3)
                .defaultIfEmpty(vo)
                .flatMap(item -> mono4)
                .defaultIfEmpty(vo)
                .map(o -> R.ok().setResult(o));
    }

    @Override
    
    public Mono sys_edit(ProjectCategoryVo vo) {
        //执行回滚操作
        Mono mono1 = save_rollback(vo,"1","a001","test");
        Mono bind = router.bind(() -> {
                    return mono1
                            .defaultIfEmpty(vo)
                            //保存vo对象
                            .flatMap(item -> save(vo))
                            .defaultIfEmpty(vo)
                            .map(o -> R.ok().setResult(o));
                },
                R2dbcRouterBuilder.r2dbcRouterBuider().database("boozsoft-nc").schema("booznc-group").build()
        );
        return bind;
//        return mono1
//                .defaultIfEmpty(vo)
//                //保存vo对象
//                .flatMap(item -> save(vo))
//                .defaultIfEmpty(vo)
//                .map(o -> R.ok().setResult(o));
    }

    @Override
    public Mono save(ProjectCategoryVo vo) {
        // 大类的逻辑
        Mono<ProjectCategory> mono1=Mono.fromSupplier(
                () -> {
                    ProjectCategory category = new ProjectCategory();
                    if (vo.getId()!=null && !vo.getId().equals("")){
                        category.setId(vo.getId());
                    }
                    category.setProjectCateCode(vo.getProjectCateCode());
                    category.setProjectCateName(vo.getProjectCateName());
                    category.setProjectTable(vo.getProjectTable());
                    if (category.getProjectTable() == null || category.getProjectTable().equals("")) {
                        category.setProjectTable("project_" + category.getProjectCateCode());
                    }
                    category.setSuccessState(vo.getSuccessState());
                    category.setApplyDatabaseUniqueCode(vo.getApplyDatabaseUniqueCode());
                    category.setApplyUser(vo.getApplyUser());
                    category.setApplyDate(vo.getApplyDate());
                    category.setBiandongMethod(vo.getBiandongMethod());
                    category.setApproveUser(vo.getApproveUser());
                    category.setApproveDate(vo.getApproveDate());
                    category.setFlag("1");
                    return category;
                })
                .flatMap(projectCategoryRepository::save);

        // 栏目的逻辑
        Mono<List<ProjectColumn>> mono2 = Mono.fromSupplier(
                () -> Flux.fromIterable(vo.getTable())
                        .map(item -> {
                            if (item.getCname() != null && !item.getCname().equals("")) {
                                item.setProjectCateCode(vo.getProjectCateCode());
//                                if (item.getCtitle() == null || item.getCtitle().equals("")) {
//                                    item.setCtitle(PinyinUtil.getPinyin(item.getCname(), ""));
//                                }
                                if (vo.getTable().indexOf(item)>7) {
                                    item.setCtitle("menu"+(vo.getTable().indexOf(item)-7));
                                }
                                if (item.getNum() == null) {
                                    item.setNum(vo.getTable().indexOf(item) + 1);
                                }
                                if (item.getSourceType().equals("2")) {
                                    item.setSourceColumnUnique("unique_code");
                                } else {
                                    item.setSourceColumnUnique(null);
                                }
                                switch (item.getCtype()) {
                                    case "1":
                                        item.setShuxing("Input");
                                        break;
                                    case "2":
                                        item.setShuxing("InputNumber");
                                        break;
                                    case "3":
                                        item.setShuxing("InputNumber");
                                        break;
                                    case "4":
                                        item.setShuxing("DatePicker");
                                        break;
                                    case "5":
                                        item.setShuxing("Checkbox");
                                        break;
                                }
                                if (vo.getSuccessState().equals("0")) {
                                    if (item.getSuccessState() == null || item.getSuccessState().equals("") || !item.getSuccessState().equals("0")) {
                                        item.setSuccessState(vo.getSuccessState());
                                    }
                                } else {
                                    item.setSuccessState(vo.getSuccessState());
                                }
                                item.setApplyDatabaseUniqueCode(vo.getApplyDatabaseUniqueCode());
                                item.setApplyUser(vo.getApplyUser());
                                if (item.getApplyDate()==null || item.getApplyDate().equals("")) {
                                    item.setApplyDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                                }
                                item.setApproveUser(vo.getApproveUser());
                                item.setApproveDate(vo.getApproveDate());
                            }
                            return item;
                        }))
                .flatMapMany(item -> item)
                .collectList()
                .flatMapMany(projectColumnRepository::saveAll)
                .collectList();

        // 返回结果
        return mono1.zipWith(mono2).map(o -> R.ok().setResult(o));
    }

    @Override
    
    public Mono sys_deleteColumn(String id) {
        Mono bind = router.bind(() -> {
                    return projectColumnRepository.deleteById(id).then(Mono.just(R.ok()));
                },
                R2dbcRouterBuilder.r2dbcRouterBuider().database("boozsoft-nc").schema("booznc-group").build()
        );
        return bind;
//        return projectColumnRepository.deleteById(id).then(Mono.just(R.ok()));
    }

    @Override
    
    public Mono acc_deleteColumn(String projectCateCode, Integer num) {
        return projectColumnRepository.deleteByProjectCateCodeAndNum(projectCateCode, num).then(Mono.just(R.ok()));
    }

    @Override
    
    public Mono sys_delete(ProjectCategory category) {
        Mono bind = router.bind(() -> {
                    return projectCategoryRepository.deleteById(category.getId())
                            .zipWith(projectColumnRepository.deleteByProjectCateCode(category.getProjectCateCode()))
                            .then(Mono.just(R.ok()));
                },
                R2dbcRouterBuilder.r2dbcRouterBuider().database("boozsoft-nc").schema("booznc-group").build()
        );
        return bind;
//        return projectCategoryRepository.deleteById(category.getId())
//                .zipWith(projectColumnRepository.deleteByProjectCateCode(category.getProjectCateCode()))
//                .then(Mono.just(R.ok()));
    }

    @Override
    
    public Mono acc_delete(ProjectCategory category) {
        return projectCategoryRepository.deleteByProjectCateCode(category.getProjectCateCode())
                .zipWith(projectColumnRepository.deleteByProjectCateCode(category.getProjectCateCode()))
                .then(Mono.just(R.ok()));
    }

    @Override
    public Mono createProject(ProjectCategoryVo vo) {
        String columnSql = "";
        for (int i = 0; i < vo.getTable().size(); i++) {
            ProjectColumn column = vo.getTable().get(i);
//        for (ProjectColumn column : vo.getTable()) {
            if (column.getCtitle() == null || column.getCtitle().equals("")) {
                column.setCtitle(PinyinUtil.getPinyin(column.getCname(), ""));
            }
            columnSql += column.getCtitle() + " varchar, ";
        }
        String sql = " create table if not exists project_" + vo.getProjectCateCode() +
                " (id serial not null constraint project_" + vo.getProjectCateCode() + "_pk primary key, " +
                " unique_code         varchar not null, " +
                columnSql +
                " success_state              varchar, " +
                " apply_database_unique_code varchar, " +
                " apply_user                 varchar, " +
                " apply_date                 varchar, " +
                " approve_user               varchar, " +
                " approve_date               varchar, " +
                " biandong_method            varchar);" +
                " alter table project_" + vo.getProjectCateCode() +
                " owner to postgres; ";
        return client.sql(sql).fetch().all().collectList().map(item -> R.ok().setResult(item));
    }

    @Override
    public Mono createProjectRollback(ProjectCategoryVo vo) {
        String columnSql = "";
        for (int i = 0; i < vo.getTable().size(); i++) {
            ProjectColumn column = vo.getTable().get(i);
//        for (ProjectColumn column : vo.getTable()) {
            if (column.getCtitle() == null || column.getCtitle().equals("")) {
                column.setCtitle(PinyinUtil.getPinyin(column.getCname(), ""));
            }
            columnSql += column.getCtitle() + " varchar, ";
        }
        String sql = " create table if not exists project_" + vo.getProjectCateCode() + "_rollback " +
                " (id serial not null constraint project_" + vo.getProjectCateCode() + "_rollback_pk primary key, " +
                " unique_code         varchar not null, " +
                columnSql +
                " biandong_date              varchar, " +
                " biandong_method            varchar, " +
                " biandong_name              varchar, " +
                " biandong_unique_code       varchar, " +
                " success_state              varchar, " +
                " apply_database_unique_code varchar, " +
                " apply_user                 varchar, " +
                " apply_date                 varchar, " +
                " approve_user               varchar, " +
                " approve_date               varchar);" +
                " alter table project_" + vo.getProjectCateCode() + "_rollback " +
                " owner to postgres; ";
        return client.sql(sql).fetch().all().collectList().map(item -> R.ok().setResult(item));
    }

    @Override
    public Mono addProjectColumn(ProjectCategoryVo vo) {
        String columnSql = "";
        String str = "";
        for (int i = 0; i < vo.getTable().size(); i++) {
            ProjectColumn column = vo.getTable().get(i);
//        for (ProjectColumn column : vo.getTable()) {
            if (column.getCtitle() == null || column.getCtitle().equals("")) {
                column.setCtitle(PinyinUtil.getPinyin(column.getCname(), ""));
            }
            if (i + 1 == vo.getTable().size()) {
                columnSql += " ADD COLUMN " + column.getCtitle() + " varchar; ";
                str = column.getCtitle();
            } else {
                columnSql += " ADD COLUMN " + column.getCtitle() + " varchar, ";
                str = column.getCtitle() + ", ";
            }
        }
        String sql = "DO $$ " +
                "    BEGIN " +
                "        BEGIN " +
                "            ALTER TABLE project_" + vo.getProjectCateCode() + " " + columnSql +
                "        EXCEPTION " +
                "            WHEN duplicate_column THEN RAISE NOTICE 'column " + str +
                " already exists in project_" + vo.getProjectCateCode() + ".'; " +
                "        END; " +
                "    END; " +
                "$$;";
        return client.sql(sql).fetch().all().collectList().map(item -> R.ok().setResult(item));
    }

    @Override
    public Mono addProjectColumnRollback(ProjectCategoryVo vo) {
        String columnSql = "";
        String str = "";
        for (int i = 0; i < vo.getTable().size(); i++) {
            ProjectColumn column = vo.getTable().get(i);
//        for (ProjectColumn column : vo.getTable()) {
            if (column.getCtitle() == null || column.getCtitle().equals("")) {
                column.setCtitle(PinyinUtil.getPinyin(column.getCname(), ""));
            }
            if (i + 1 == vo.getTable().size()) {
                columnSql += " ADD COLUMN " + column.getCtitle() + " varchar; ";
                str = column.getCtitle();
            } else {
                columnSql += " ADD COLUMN " + column.getCtitle() + " varchar, ";
                str = column.getCtitle() + ", ";
            }
        }
        String sql = "DO $$ " +
                "    BEGIN " +
                "        BEGIN " +
                "            ALTER TABLE project_" + vo.getProjectCateCode() + "_rollback " +
                columnSql +
                "        EXCEPTION " +
                "            WHEN duplicate_column THEN RAISE NOTICE 'column " + str +
                " already exists in project_" + vo.getProjectCateCode() + "_rollback.'; " +
                "        END; " +
                "    END; " +
                "$$;";
        return client.sql(sql).fetch().all().collectList().map(item -> R.ok().setResult(item));
    }

    @Override
    public Mono save_category_rollback(ProjectCategory projectCategory, String biandongMethod, String biandongUniqueCode, String biandongName) {
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        ProjectCategoryRollback rollback = new ProjectCategoryRollback();
        BeanUtils.copyProperties(projectCategory, rollback);
        rollback.setId(null);
        rollback.setBiandongId(projectCategory.getId());
        rollback.setBiandongDate(time);
        rollback.setBiandongMethod(biandongMethod);
        rollback.setBiandongUniqueCode(biandongUniqueCode);
        rollback.setBiandongName(biandongName);
        return projectCategoryRollbackRepository.save(rollback).map(item -> R.ok().setResult(item));
    }

    @Override
    public Mono save_column_rollback(List<ProjectColumn> projectColumnList, String biandongMethod, String biandongUniqueCode, String biandongName) {
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        List<ProjectColumnRollback> list = new ArrayList<ProjectColumnRollback>();
        for (ProjectColumn column : projectColumnList){
            ProjectColumnRollback rollback = new ProjectColumnRollback();
            BeanUtils.copyProperties(column, rollback);
            rollback.setId(null);
            rollback.setBiandongId(column.getId());
            rollback.setBiandongDate(time);
            rollback.setBiandongMethod(biandongMethod);
            rollback.setBiandongUniqueCode(biandongUniqueCode);
            rollback.setBiandongName(biandongName);
            list.add(rollback);
        }
        Flux<ProjectColumnRollback> flux = Mono.just(list).flatMapMany(Flux::fromIterable);
        return projectColumnRollbackRepository.saveAll(flux).collectList().map(item -> R.ok().setResult(item));
    }

    @Override
    public Mono save_rollback(ProjectCategoryVo vo, String biandongMethod, String biandongUniqueCode, String biandongName) {
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        //根据大类编码查询大类信息
        ProjectCategoryRollback categoryRollback = new ProjectCategoryRollback();
        Mono<ProjectCategoryRollback> mono1=projectCategoryRepository.findByProjectCateCodeAndSuccessStateOrderById(vo.getProjectCateCode(),"1").map(category -> {
            BeanUtils.copyProperties(category, categoryRollback);
            categoryRollback.setId(null);
            categoryRollback.setBiandongId(category.getId());
            categoryRollback.setBiandongDate(time);
            categoryRollback.setBiandongMethod(biandongMethod);
            categoryRollback.setBiandongUniqueCode(biandongUniqueCode);
            categoryRollback.setBiandongName(biandongName);
            return categoryRollback;
        });
        //根据大类编码查询栏目信息
        List<ProjectColumnRollback> list = new ArrayList<ProjectColumnRollback>();
        Mono<List<ProjectColumnRollback>> mono2= projectColumnRepository.findByProjectCateCodeAndSuccessStateOrderByNum(vo.getProjectCateCode(),"1").collectList().map(columnList -> {
            for (int i = 0; i < columnList.size(); i++) {
                ProjectColumn column = columnList.get(i);
                ProjectColumnRollback rollback = new ProjectColumnRollback();
                BeanUtils.copyProperties(column, rollback);
                rollback.setId(null);
                rollback.setBiandongId(column.getId());
                rollback.setBiandongDate(time);
                rollback.setBiandongMethod(biandongMethod);
                rollback.setBiandongUniqueCode(biandongUniqueCode);
                rollback.setBiandongName(biandongName);
                list.add(rollback);
            }
            return list;
        });
        Flux<ProjectColumnRollback> flux = Mono.just(list).flatMapMany(Flux::fromIterable);
        //保存项目大类回滚表信息
        Mono<ProjectCategoryRollback> mono3 = projectCategoryRollbackRepository.save(categoryRollback);
        //保存项目栏目回滚表信息
        Mono<List<ProjectColumnRollback>> mono4 = projectColumnRollbackRepository.saveAll(flux).collectList();
        // 返回结果
        return mono1
                .defaultIfEmpty(categoryRollback)
                .flatMap(item -> mono2)
                .defaultIfEmpty(list)
                .flatMap(item -> mono3)
                .defaultIfEmpty(categoryRollback)
                .flatMap(item -> mono4)
                .defaultIfEmpty(list)
                .map(o -> R.ok().setResult(o));
    }

    @Override
    public Mono sys_approval_edit(ProjectCategoryVo vo, String biandongMethod, String biandongUniqueCode, String biandongName) {
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        //大类回滚表添加记录
        Mono mono1 = projectCategoryRepository.findByProjectCateCodeAndSuccessStateOrderById(vo.getProjectCateCode(),"1")
                .map(category -> {
                    ProjectCategoryRollback categoryRollback = new ProjectCategoryRollback();
                    BeanUtils.copyProperties(category, categoryRollback);
                    categoryRollback.setId(null);
                    categoryRollback.setBiandongId(category.getId());
                    categoryRollback.setBiandongDate(time);
                    categoryRollback.setBiandongMethod(biandongMethod);
                    categoryRollback.setBiandongUniqueCode(biandongUniqueCode);
                    categoryRollback.setBiandongName(biandongName);
                    return categoryRollback;
                }).flatMap(projectCategoryRollbackRepository :: save);
        //项目栏目回滚表添加记录
        Mono mono2 = projectColumnRepository.findByProjectCateCodeAndSuccessStateOrderByNum(vo.getProjectCateCode(),"1").collectList()
                .map(columnList -> {
                    List<ProjectColumnRollback> list = new ArrayList<ProjectColumnRollback>();
                    for (ProjectColumn column : columnList){
                        ProjectColumnRollback rollback = new ProjectColumnRollback();
                        BeanUtils.copyProperties(column, rollback);
                        rollback.setId(null);
                        rollback.setBiandongId(column.getId());
                        rollback.setBiandongDate(time);
                        rollback.setBiandongMethod(biandongMethod);
                        rollback.setBiandongUniqueCode(biandongUniqueCode);
                        rollback.setBiandongName(biandongName);
                        list.add(rollback);
                    }
                    Flux<ProjectColumnRollback> flux = Mono.just(list).flatMapMany(Flux::fromIterable);
                    return flux;
                })
                .flatMapMany(projectColumnRollbackRepository::saveAll)
                .collectList();
        //根据id删除大类信息记录
        Mono mono3 = projectCategoryRepository.findByProjectCateCodeAndSuccessStateOrderById(vo.getProjectCateCode(),"1")
                .map(category -> category.getId())
                .flatMap(projectCategoryRepository :: deleteById);
        //保存vo对象
        Mono mono4 = save(vo);
        //创建项目目录表
        Mono mono5 = addProjectColumn(vo);
        //创建项目目录回滚表
        Mono mono6 = addProjectColumnRollback(vo);
        Mono bind = router.bind(() -> {
                    return mono1
                            .defaultIfEmpty(vo)
                            .flatMap(item -> mono2)
                            .defaultIfEmpty(vo)
                            .flatMap(item -> mono3)
                            .defaultIfEmpty(vo)
                            .flatMap(item -> mono4)
                            .defaultIfEmpty(vo)
                            .flatMap(item -> mono5)
                            .defaultIfEmpty(vo)
                            .flatMap(item -> mono6)
                            .defaultIfEmpty(vo)
                            .map(o -> R.ok().setResult(o));
                },
                R2dbcRouterBuilder.r2dbcRouterBuider().database("boozsoft-nc").schema("booznc-group").build()
        );
        return bind;
//        return mono1
//                .defaultIfEmpty(vo)
//                .flatMap(item -> mono2)
//                .defaultIfEmpty(vo)
//                .flatMap(item -> mono3)
//                .defaultIfEmpty(vo)
//                .flatMap(item -> mono4)
//                .defaultIfEmpty(vo)
//                .flatMap(item -> mono5)
//                .defaultIfEmpty(vo)
//                .flatMap(item -> mono6)
//                .defaultIfEmpty(vo)
//                .map(o -> R.ok().setResult(o));
    }

    @Override
    public Mono notState_delete(ProjectCategory category, String biandongMethod, String biandongUniqueCode, String biandongName) {
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        //大类回滚表添加记录
        Mono mono1 = projectCategoryRepository.findByProjectCateCodeAndSuccessStateOrderById(category.getProjectCateCode(),"1")
                .map(cate -> {
                    ProjectCategoryRollback categoryRollback = new ProjectCategoryRollback();
                    BeanUtils.copyProperties(cate, categoryRollback);
                    categoryRollback.setId(null);
                    categoryRollback.setBiandongId(cate.getId());
                    categoryRollback.setBiandongDate(time);
                    categoryRollback.setBiandongMethod(biandongMethod);
                    categoryRollback.setBiandongUniqueCode(biandongUniqueCode);
                    categoryRollback.setBiandongName(biandongName);
                    return categoryRollback;
                }).flatMap(projectCategoryRollbackRepository :: save);
        //项目栏目回滚表添加记录
        Mono mono2 = projectColumnRepository.findByProjectCateCodeAndSuccessStateOrderByNum(category.getProjectCateCode(),"1").collectList()
                .map(columnList -> {
                    List<ProjectColumnRollback> list = new ArrayList<ProjectColumnRollback>();
                    for (ProjectColumn column : columnList){
                        ProjectColumnRollback rollback = new ProjectColumnRollback();
                        BeanUtils.copyProperties(column, rollback);
                        rollback.setId(null);
                        rollback.setBiandongId(column.getId());
                        rollback.setBiandongDate(time);
                        rollback.setBiandongMethod(biandongMethod);
                        rollback.setBiandongUniqueCode(biandongUniqueCode);
                        rollback.setBiandongName(biandongName);
                        list.add(rollback);
                    }
                    Flux<ProjectColumnRollback> flux = Mono.just(list).flatMapMany(Flux::fromIterable);
                    return flux;
                })
                .flatMapMany(projectColumnRollbackRepository::saveAll)
                .collectList();
        //根据id删除大类信息记录
        Mono mono3 = projectCategoryRepository.deleteById(category.getId());
        Mono mono4 = projectColumnRepository.deleteByProjectCateCodeAndSuccessState(category.getProjectCateCode(),"0");
        Mono bind = router.bind(() -> {
                    return mono1
                            .defaultIfEmpty(category)
                            .flatMap(item -> mono2)
                            .defaultIfEmpty(category)
                            .flatMap(item -> mono3)
                            .defaultIfEmpty(category)
                            .flatMap(item -> mono4)
                            .defaultIfEmpty(category)
                            .map(o -> R.ok().setResult(o));
                },
                R2dbcRouterBuilder.r2dbcRouterBuider().database("boozsoft-nc").schema("booznc-group").build()
        );
        return bind;
//        return mono1
//                .defaultIfEmpty(category)
//                .flatMap(item -> mono2)
//                .defaultIfEmpty(category)
//                .flatMap(item -> mono3)
//                .defaultIfEmpty(category)
//                .flatMap(item -> mono4)
//                .defaultIfEmpty(category)
//                .map(o -> R.ok().setResult(o));
    }

}
