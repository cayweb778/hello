package org.boozsoft.domain.entity.account;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "数据表：项目栏目回滚表", description = "项目栏目回滚表")
@Table("project_column_rollback")
@Data
public class ProjectColumnRollback {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "项目大类编码", hidden = true)
    private String projectCateCode;
    @ApiModelProperty(value = "唯一标识", hidden = true)
    private String uniqueCode;
    @ApiModelProperty(value = "字段排序号", hidden = true)
    private Integer num;
    @ApiModelProperty(value = "字段名称", hidden = true)
    private String cname;
    @ApiModelProperty(value = "字段标题", hidden = true)
    private String ctitle;
    @ApiModelProperty(value = "字段属性", hidden = true)
    private String shuxing;
    @ApiModelProperty(value = "数据类型", hidden = true)
    private String ctype;
    @ApiModelProperty(value = "字段长度", hidden = true)
    private String clength;
    @ApiModelProperty(value = "宽度", hidden = true)
    private String cwidth;
    @ApiModelProperty(value = "数据来源类型", hidden = true)
    private String sourceType;
    @ApiModelProperty(value = "来源表名称", hidden = true)
    private String sourceName;
    @ApiModelProperty(value = "来源表关联字段", hidden = true)
    private String sourceColumnUnique;
    @ApiModelProperty(value = "来源表显示字段", hidden = true)
    private String sourceColumn;
    @ApiModelProperty(value = "是否显示（1.显示；0.不显示）", hidden = true)
    private String islist;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong1;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong2;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong3;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong4;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong5;
    @ApiModelProperty(value = "变动日期", hidden = true)
    private String biandongDate;
    @ApiModelProperty(value = "变动方式(1.修改;2.删除;3.停用)", hidden = true)
    private String biandongMethod;
    @ApiModelProperty(value = "操作员姓名", hidden = true)
    private String biandongName;
    @ApiModelProperty(value = "操作员唯一标识", hidden = true)
    private String biandongUniqueCode;
    @ApiModelProperty(value = "变动id", hidden = true)
    @JsonSerialize(using = ToStringSerializer.class)
    private String biandongId;

    @ApiModelProperty(value = "生效状态（1是；0否）", hidden = true)
    private String successState;
    @ApiModelProperty(value = "申请账套唯一码", hidden = true)
    private String applyDatabaseUniqueCode;
    @ApiModelProperty(value = "申请人", hidden = true)
    private String applyUser;
    @ApiModelProperty(value = "申请时间", hidden = true)
    private String applyDate;
    @ApiModelProperty(value = "审批人", hidden = true)
    private String approveUser;
    @ApiModelProperty(value = "审批时间", hidden = true)
    private String approveDate;

}
