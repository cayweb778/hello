package org.boozsoft.domain.entity.account;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "数据表：批文表", description = "批文表")
@Table("approval_doc")
@Data
public class ApprovalDoc {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "批文文号", hidden = true)
    private String docCode;
    @ApiModelProperty(value = "批文名称", hidden = true)
    private String docName;
    @ApiModelProperty(value = "批准日期", hidden = true)
    private String pizhunDate;
    @ApiModelProperty(value = "开始日期", hidden = true)
    private String startDate;
    @ApiModelProperty(value = "结束日期", hidden = true)
    private String endDate;
    @ApiModelProperty(value = "是否涉密（1.涉密；0.不涉密）", hidden = true)
    private String isSecrecy;
    @ApiModelProperty(value = "颁发机构全称", hidden = true)
    private String awardName;
    @ApiModelProperty(value = "颁发日期", hidden = true)
    private String awardDate;
    @ApiModelProperty(value = "存放内容", hidden = true)
    private String docContent;
    @ApiModelProperty(value = "存放位置", hidden = true)
    private String docAddress;
    @ApiModelProperty(value = "保管人", hidden = true)
    private String psnCode;
    @ApiModelProperty(value = "备注", hidden = true)
    private String remark;
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
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong6;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong7;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong8;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong9;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong10;

}
