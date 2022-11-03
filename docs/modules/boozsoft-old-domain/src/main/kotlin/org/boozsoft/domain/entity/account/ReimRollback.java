package org.boozsoft.domain.entity.account;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "数据表：报销单回滚表", description = "报销单回滚表")
@Table("reim_rollback")
@Data
public class ReimRollback {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "单据编码", hidden = true)
    private String reimCode;
    @ApiModelProperty(value = "单据名称", hidden = true)
    private String reimName;
    @ApiModelProperty(value = "报销单分类", hidden = true)
    private String reimClass;
    @ApiModelProperty(value = "单据日期", hidden = true)
    private String reimDate;
    @ApiModelProperty(value = "员工唯一码", hidden = true)
    private Long psnCode;
    @ApiModelProperty(value = "部门唯一码", hidden = true)
    private Long deptCode;
    @ApiModelProperty(value = "项目大类编码", hidden = true)
    private String projectCate;
    @ApiModelProperty(value = "项目唯一码", hidden = true)
    private Long projectCode;
    @ApiModelProperty(value = "核算单位", hidden = true)
    private String corp;
    @ApiModelProperty(value = "费用名称", hidden = true)
    private String costName;
    @ApiModelProperty(value = "费用金额", hidden = true)
    private String amount;
    @ApiModelProperty(value = "费用说明", hidden = true)
    private String costExplain;
    @ApiModelProperty(value = "对应凭证号（唯一标识）", hidden = true)
    private String vouchCode;
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

}
