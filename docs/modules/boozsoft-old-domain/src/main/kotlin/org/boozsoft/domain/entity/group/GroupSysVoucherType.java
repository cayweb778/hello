package org.boozsoft.domain.entity.group;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "集团表：凭证类别表", description = "凭证类别表")
@Table("_app_group_voucher_type")
@Data
public class GroupSysVoucherType {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "编码（不允许重复）", hidden = true)
    private String voucherNum;
    @ApiModelProperty(value = "类别字简称（不允许重复）", hidden = true)
    private String voucherTypeCode;
    @ApiModelProperty(value = "凭证类别名称（不允许重复）", hidden = true)
    private String voucherTypeName;
    @ApiModelProperty(value = "限制类型", hidden = true)
    private String limitClass;
    @ApiModelProperty(value = "限制科目", hidden = true)
    private String limitKemu;
    @ApiModelProperty(value = "1 代表默认类型 ", hidden = true)
    private String beiyong1;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong2;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong3;
}
