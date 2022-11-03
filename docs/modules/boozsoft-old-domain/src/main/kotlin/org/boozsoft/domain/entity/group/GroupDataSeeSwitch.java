package org.boozsoft.domain.entity.group;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "数据表：数据权限控制表", description = "数据权限控制表")
@Table("_app_group_data_see_switch")
@Data
public class GroupDataSeeSwitch {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "控制范围(zt或zz)", hidden = true)
    private String ctrlRange;
    @ApiModelProperty(value = "账套或组织唯一码", hidden = true)
    private String uniqueCode;
    @ApiModelProperty(value = "档案类型（档案名称）", hidden = true)
    private String dataName;
    @ApiModelProperty(value = "对应数据库表名称", hidden = true)
    private String databaseName;
    @ApiModelProperty(value = "是否控制（1控制，0或其他不控制）", hidden = true)
    private String isCtrl;
    @ApiModelProperty(value = "是否按年度控制（1控制，0或其他不控制）", hidden = true)
    private String isCtrlYear;
    private String beiyong1;
    private String beiyong2;
    private String beiyong3;

    @ApiModelProperty(value = "模块名称", hidden = true)
    private String moduleName;
    @ApiModelProperty(value = "唯一码列对应DB字段", hidden = true)
    private String uniqueColCode;
    @ApiModelProperty(value = "code列对应DB字段", hidden = true)
    private String databaseColCode;
    @ApiModelProperty(value = "name对应DB字段", hidden = true)
    private String databaseColName;
    @ApiModelProperty(value = "条件对应DB字段", hidden = true)
    private String databaseColCondition;
    @ApiModelProperty(value = "关联条件对应DB字段", hidden = true)
    private String relation;

}
