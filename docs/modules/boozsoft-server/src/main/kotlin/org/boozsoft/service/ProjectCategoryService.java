package org.boozsoft.service;

import org.boozsoft.domain.entity.account.ProjectCategory;
import org.boozsoft.domain.entity.account.ProjectColumn;
import org.boozsoft.domain.vo.ProjectCategoryVo;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ProjectCategoryService {

    /** 账套保存 */
    Mono acc_save(ProjectCategoryVo vo);
    /** 系统保存 */
    Mono sys_save(ProjectCategoryVo vo);
    /** 保存方法 */
    Mono save(ProjectCategoryVo vo);

    /** 账套修改 */
    Mono acc_edit(ProjectCategoryVo vo);
    /** 系统修改 */
    Mono sys_edit(ProjectCategoryVo vo);

    /** 系统栏目行删除 */
    Mono sys_deleteColumn(String id);
    /** 账套栏目行删除 */
    Mono acc_deleteColumn(String projectCateCode, Integer num);

    /** 系统删除大类 */
    Mono sys_delete(ProjectCategory category);
    /** 账套删除大类 */
    Mono acc_delete(ProjectCategory category);

    /** 创建项目目录表 */
    Mono createProject(ProjectCategoryVo vo);
    /** 创建项目目录回滚表 */
    Mono createProjectRollback(ProjectCategoryVo vo);

    /** 添加项目目录表栏目 */
    Mono addProjectColumn(ProjectCategoryVo vo);
    /** 添加项目目录回滚表栏目 */
    Mono addProjectColumnRollback(ProjectCategoryVo vo);

    /** 项目大类回滚 */
    Mono save_category_rollback(ProjectCategory projectCategory, String biandongMethod, String biandongUniqueCode, String biandongName);
    /** 项目栏目回滚 */
    Mono save_column_rollback(List<ProjectColumn> projectColumnList, String biandongMethod, String biandongUniqueCode, String biandongName);

    /** 回滚方法 */
    Mono save_rollback(ProjectCategoryVo vo, String biandongMethod, String biandongUniqueCode, String biandongName);

    /** 审批修改内容 */
    Mono sys_approval_edit(ProjectCategoryVo vo, String biandongMethod, String biandongUniqueCode, String biandongName);

    /** 删除不生效大类 */
    Mono notState_delete(ProjectCategory category, String biandongMethod, String biandongUniqueCode, String biandongName);

}
