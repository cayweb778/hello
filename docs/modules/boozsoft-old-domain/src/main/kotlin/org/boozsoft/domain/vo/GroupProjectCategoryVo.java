package org.boozsoft.domain.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.boozsoft.domain.entity.group.GroupProjectCategory;
import org.boozsoft.domain.entity.group.GroupProjectColumn;

import java.util.List;

/**
 * 视图实体类
 *
 * @author Chill
 */
@Data
@EqualsAndHashCode(callSuper=false)
@Accessors(chain = true)
@ApiModel(value = "GroupProjectCategoryVo对象", description = "GroupProjectCategoryVo对象")
public class GroupProjectCategoryVo extends GroupProjectCategory {
    private List<GroupProjectColumn> table;

    private String isControl; // 是否管控:1是0否
    private String isKeyword; // 是否允许修改关键字:1是0否
    private String isOther; // 是否允许修改其他信息:1是0否
    private String isAuto; // 新增是否自动分配:1是0否

    private String approveIdea;//审批意见
}
