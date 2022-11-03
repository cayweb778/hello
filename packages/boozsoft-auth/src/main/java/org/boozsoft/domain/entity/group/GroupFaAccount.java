package org.boozsoft.domain.entity.group;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.boozsoft.domain.entity.audit.BaseModificationAwareEntity;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "数据表：固定资产账", description = "固定资产账")
@Table("_app_group_fa_account")
@Data
@EqualsAndHashCode(callSuper = false)
public class GroupFaAccount{

    @ApiModelProperty(value = "主键id", position = 0)
    @Id
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "公司唯一码", position = 1)
    private String uniqueCode;
    @ApiModelProperty(value = "固定资产账套代码", position = 1)
    private String coCode;
    @ApiModelProperty(value = "关联财务账套代码", position = 1)
    private String associateCoCode;
    @ApiModelProperty(value = "固定资产账标识")
    private String faAccId;
    @ApiModelProperty(value = "账套类型")
    private String independent;
    @ApiModelProperty(value = "默认管理代码（FA01～FA99）")
    private String faCode;
    @ApiModelProperty(value = "账套名称")
    private String faAccName;
    @ApiModelProperty(value = "启用年月")
    private String startYearMonth;
    @ApiModelProperty(value = "国家（地区）（国际代码CHN）")
    private String countryId;
    @ApiModelProperty(value = "本位币代码（三位国际代码CNY）")
    private String currencyType;
    @ApiModelProperty(value = "折旧方法唯一码（取自折旧方法表）")
    private String zhejiuCode;
    @ApiModelProperty(value = "折旧分配周期(取值1,2,3，默认值为1,代表1个月，2个月，3个月)")
    private String zhejiuPeriod;
    @ApiModelProperty(value = "是否计提折旧（1是，0否，默认值为1）")
    private String isZhejiu;
    @ApiModelProperty(value = "月末结账前必须完成制单（1是，0否,默认值为0）")
    private String isFilledIn;
    @ApiModelProperty(value = "业务发生后立即生单（1是，0否,默认值为0）")
    private String isNowZhidan;
    @ApiModelProperty(value = "新增资产当月计提折旧（1是，0否，默认值为0）")
    private String isCreateZhejiu;
    @ApiModelProperty(value = "原值变动当月生效（1是，0否，默认值为1）")
    private String isYuanzhi;
    @ApiModelProperty(value = "对账不平允许结账（1是，0否，默认值为1）")
    private String isDzSettle;
    @ApiModelProperty(value = "累计折旧调整当月生效（1是，0否，默认值为1）")
    private String isLeijizjtz;
    @ApiModelProperty(value = "净殘值调整当月生效（1是，0否，默认值为1）")
    private String isJcanzhi;

    @ApiModelProperty(value = "管理分类控制（1是，0否，默认值为1）")
    private String isGlfl;
    @ApiModelProperty(value = "与总账模块凭证关联（1是，0否，默认值为1）")
    private String isZzpz;
    @ApiModelProperty(value = "最后折旧月剩余折旧全部提足（1是，0否，默认值为1）")
    private String isZjtt;
    @ApiModelProperty(value = "控制资产类别权限（1是，0否，默认值为1）")
    private String isZjlb;
    @ApiModelProperty(value = "银行现金支付科目")
    private String kmCode;
    @ApiModelProperty(value = "1多部门 2多项目")
    private String cardType;

    private String beiyong1;
    private String beiyong2;
    private String beiyong3;
    private String beiyong4;
    private String beiyong5;
    private String beiyong6;
    private String beiyong7;
    private String beiyong8;
    private String beiyong9;
    private String beiyong10;


    @ApiModelProperty(value = "业务删除")
    private String isDel;
    @ApiModelProperty(value = "删除操作员")
    private String delName;
    @ApiModelProperty(value = "删除时间")
    private String delDate;

}


