package org.boozsoft.domain.entity.group;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.boozsoft.domain.entity.audit.BaseModificationAwareEntity;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;

@ApiModel(value = "数据表：集团公司表", description = "集团公司表")
@Table("_app_group_sys_corp")
@Data
@EqualsAndHashCode(callSuper = false)
public class GroupSysCorp implements Serializable {

    @ApiModelProperty(value = "主键id", position = 0)
    @Id
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;

    @ApiModelProperty(value = "公司代码-2位数字/字母", position = 68)
    private String coCode;

    @ApiModelProperty(value = "账套名称", position = 2)
    private String accName;

    @ApiModelProperty(value = "账套机构简称(公司或单位名称) ", position = 13)
    private String accNameCn;

    @ApiModelProperty(value = "是否为独立账套 1 or 0", position = 3)
    private String independent;

    @ApiModelProperty(value = "账套所属组织编码 ", position = 6)
    private String accGroup;

    @ApiModelProperty(value = "上级公司唯一码（可为空） ", position = 16)
    private String corpCode;


    @ApiModelProperty(value = "行政区划唯一码 ", position = 18)
    private String uniqueCodeZone;

    @ApiModelProperty(value = "行业性质编码 ", position = 19)
    private String industryclassCode;

    @ApiModelProperty(value = "国家地区ID ", position = 19)
    private String countryId;

    @ApiModelProperty(value = "税号 ", position = 20)
    private String taxCode;

    @ApiModelProperty(value = "地址 ", position = 22)
    private String address;

    @ApiModelProperty(value = "网站", position = 23)
    private String website;

    @ApiModelProperty(value = "联系人")
    private String contacts;

    @ApiModelProperty(value = "联系电话")
    private String telephone;

    @ApiModelProperty(value = "说明")
    private String remarks;

    @ApiModelProperty(value = "开票抬头全称")
    private String invoiceLookUp;

    @ApiModelProperty(value = "纳税人识别号")
    private String invoiceIdentifier;

    @ApiModelProperty(value = "地址及电话")
    private String invoiceAddressPhone;

    @ApiModelProperty(value = "开户行及账户")
    private String invoiceBanks;

    private String beiyong1;

    private String beiyong2;

    private String beiyong3;

    @ApiModelProperty(value = "业务删除")
    private String isDel;
    @ApiModelProperty(value = "删除操作员")
    private String delName;
    @ApiModelProperty(value = "删除时间")
    private String delDate;

}


