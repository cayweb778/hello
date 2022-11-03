package org.boozsoft.domain.entity.group;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "集团：集团信息", description = "集团信息实体类")
@Table("_app_group_sys_group")
@Data
public class GroupSysGroup {
    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "唯一编码 ", hidden = true)
    private String uniqueCode;
    @ApiModelProperty(value = "集团编码（一位数字或字母，不允许重复） ", hidden = true)
    private String groupCode;
    @ApiModelProperty(value = "集团公司全称（不允许重复） ", hidden = true)
    private String groupName;
    @ApiModelProperty(value = "成立日期 ", hidden = true)
    private String createDate;
    @ApiModelProperty(value = "行业性质编码 ", hidden = true)
    private String industryclassCode;
    @ApiModelProperty(value = "行政区划唯一码 ", hidden = true)
    private String uniqueCodeZone;
    @ApiModelProperty(value = "联系人 ", hidden = true)
    private String contacts;
    @ApiModelProperty(value = "电话 ", hidden = true)
    private String telephone;
    @ApiModelProperty(value = "地址 ", hidden = true)
    private String address;
    @ApiModelProperty(value = "Email ", hidden = true)
    private String email;
    @ApiModelProperty(value = "所属国家编码 ", hidden = true)
    private String countryId;
    @ApiModelProperty(value = "官网地址 ", hidden = true)
    private String website;
    @ApiModelProperty(value = "简介 ", hidden = true)
    private String remarks;
    @ApiModelProperty(value = "集团性质（用于区分企业或行政事业、民非单位，0为公司，1为非公司） ", hidden = true)
    private String icorp;
    @ApiModelProperty(value = "是否停用(1.启用;0停用) ", hidden = true)
    private String flag;
    @ApiModelProperty(value = "所属品牌", hidden = true)
    private String brand;

    private String beiyong1;  // ftp 图片路径默认空
    private String beiyong2;
    private String beiyong3;
    private String beiyong4;
    private String beiyong5;
}
