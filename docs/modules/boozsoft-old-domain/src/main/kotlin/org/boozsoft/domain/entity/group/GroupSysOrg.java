package org.boozsoft.domain.entity.group;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "集团：组织信息", description = "组织信息实体类")
@Table("_app_group_sys_org")
@Data
public class GroupSysOrg {
    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "唯一编码 ", hidden = true)
    private String uniqueCode;
    @ApiModelProperty(value = "组织编码（一位数字或字母，不允许重复） ", hidden = true)
    private String orgCode;
    @ApiModelProperty(value = "组织全称（不允许重复） ", hidden = true)
    private String orgName;
    @ApiModelProperty(value = "组织简称", hidden = true)
    private String orgSimpName;
    @ApiModelProperty(value = "所属集团", hidden = true)
    private String uniqueGroupCode;
    @ApiModelProperty(value = "会计准则", hidden = true)
    private String uniqueAccStandard;
    @ApiModelProperty(value = "科目级次", hidden = true)
    private String ccodeLevel;
    @ApiModelProperty(value = "上级组织", hidden = true)
    private String orgSuperior;
    @ApiModelProperty(value = "成立日期 ", hidden = true)
    private String createDate;
    @ApiModelProperty(value = "行业性质编码 ", hidden = true)
    private String industryclassCode;
    @ApiModelProperty(value = "行政区划唯一码 ", hidden = true)
    private String uniqueCodeZone;
    @ApiModelProperty(value = "联系人 ", hidden = true)
    private String personInCharge;
    @ApiModelProperty(value = "电话 ", hidden = true)
    private String telephone;
    @ApiModelProperty(value = "通讯地址 ", hidden = true)
    private String address;
    @ApiModelProperty(value = "所属国家编码 ", hidden = true)
    private String countryId;
    @ApiModelProperty(value = "简介 ", hidden = true)
    private String remarks;

    @ApiModelProperty(value = "数量小数位数 ", position = 24)
    private String numberDec;

    @ApiModelProperty(value = "单价小数位数 ", position = 25)
    private String unitPriceDec;

    @ApiModelProperty(value = "汇率小数位数 ", position = 26)
    private String rateDec;

    @ApiModelProperty(value = "凭证编号小数位数 ")
    private String accvouchDec;
    @ApiModelProperty(value = "凭证类别编码")
    private String voucherTypeNum;
    @ApiModelProperty(value = "其他凭证编码 ")
    private String voucherTypeOtherNums;

    @ApiModelProperty(value = "年度开始日期")
    private String yearStartDate;

    @ApiModelProperty(value = "年度结束日期")
    private String yearEndDate;
    @ApiModelProperty(value = "账套启用会计期间号（12~16） ", position = 5)
    private String periodNum;

    @ApiModelProperty(value = "是否停用(1.启用;0停用) ", hidden = true)
    private String flag;
    private String beiyong1; // ptf图片路径
    private String beiyong2; // 临时beae64
    private String beiyong3; // 临时 下属单位编码

    @ApiModelProperty(value = "科目级次长度")
    private String jiciLength;

    @ApiModelProperty(value = "业务删除")
    private String isDel;
    @ApiModelProperty(value = "删除操作员")
    private String delName;
    @ApiModelProperty(value = "删除时间")
    private String delDate;
}
