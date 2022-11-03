package org.boozsoft.domain.entity.account;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.boozsoft.domain.entity.audit.BaseModificationAwareEntity;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "数据表：账套表", description = "账套表")
@Table("sys_account")
@Data
@EqualsAndHashCode(callSuper = false)
public class SysAccount extends BaseModificationAwareEntity {

    @ApiModelProperty(value = "主键id", position = 0)
    @Id
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;

    @ApiModelProperty(value = "账套唯一码：公司8位首字母-001-年度", position = 1)
    private String uniqueCode;

    @ApiModelProperty(value = "账套编码", position = 1)
    private String accId;

    @ApiModelProperty(value = "公司名称", position = 2)
    private String accName;

    @ApiModelProperty(value = "账套启用日期 ", position = 3)
    private String accStartDate;

    @ApiModelProperty(value = "是否为独立账套 1 or 0", position = 3)
    private String independent;

    @ApiModelProperty(value = "账套会计年度起始日期（如01-01或04-01） ", position = 4)
    private String periodMonth;

    @ApiModelProperty(value = "账套启用会计期间号（12~16） ", position = 5)
    private String periodNum;

    @ApiModelProperty(value = "账套所属组织编码 ", position = 6)
    private String accGroup;

    @ApiModelProperty(value = "账套会计准则名称 ", position = 7)
    private String accStandard;

    @ApiModelProperty(value = "账套本位币国际代码（CHY） ", position = 8)
    private String currency;

    @ApiModelProperty(value = "账套本位币中文简称（元） ", position = 9)
    private String currencyCh;

    @ApiModelProperty(value = "账套本位币中文全称（人民币） ", position = 10)
    private String currencyName;

    @ApiModelProperty(value = "科目级次（4-2-2-2-2-2-2-2） ", position = 11)
    private String ccodeLevel;

    @ApiModelProperty(value = "财务账套全称", position = 12)
    private String accNameFull;

    @ApiModelProperty(value = "账套机构简称(公司或单位名称) ", position = 13)
    private String accNameCn;

    @ApiModelProperty(value = "上级集团唯一码 ", position = 14)
    private String groupCode;

    @ApiModelProperty(value = "上级集团占股比例 ", position = 15)
    private String groupShareRatio;

    @ApiModelProperty(value = "上级公司唯一码（可为空） ", position = 16)
    private String corpCode;

    @ApiModelProperty(value = "上级公司占股比例 ", position = 17)
    private String corpShareRatio;

    @ApiModelProperty(value = "行政区划唯一码 ", position = 18)
    private String uniqueCodeZone;

    @ApiModelProperty(value = "行业性质编码 ", position = 19)
    private String industryclassCode;

    @ApiModelProperty(value = "国家地区ID ", position = 19)
    private String countryId;

    @ApiModelProperty(value = "税号 ", position = 20)
    private String taxCode;

    @ApiModelProperty(value = "默认税率 ", position = 21)
    private String taxRate;

    @ApiModelProperty(value = "地址 ", position = 22)
    private String address;

    @ApiModelProperty(value = "网站", position = 23)
    private String website;

    @ApiModelProperty(value = "数量小数位数 ", position = 24)
    private String numberDec;

    @ApiModelProperty(value = "单价小数位数 ", position = 25)
    private String unitPriceDec;

    @ApiModelProperty(value = "汇率小数位数 ", position = 26)
    private String rateDec;

    @ApiModelProperty(value = "纳税性质（一般纳税人、小规模纳税人） ", position = 27)
    private String taxClass;

    @ApiModelProperty(value = "制单序时控制（1是，0否，默认为是） ", position = 28)
    private String ichronological;

    @ApiModelProperty(value = "现金流量必录（1是、0否，默认为否） ", position = 29)
    private String icashFlow;

    @ApiModelProperty(value = "资金及往来赤字控制（1是、0否，默认为是） ", position = 30)
    private String ideficit;

    @ApiModelProperty(value = "结算方式必录（1是、0否，默认为否） ", position = 31)
    private String isettlement;

    @ApiModelProperty(value = "往来单位票据号必录（1是、0否，默认为否） ", position = 32)
    private String ibill;

    @ApiModelProperty(value = "可以使用应收受控科目（1是、0否，默认为否） ", position = 33)
    private String iar;

    @ApiModelProperty(value = "可以使用应付受控科目（1是、0否，默认为否） ", position = 34)
    private String iap;

    @ApiModelProperty(value = "是否手工编号（1是、0否，默认为否） ", position = 35)
    private String iautoCode;

    @ApiModelProperty(value = "是否自动填补凭证断号（1是、0否、默认为是） ", position = 36)
    private String ibreakCode;

    @ApiModelProperty(value = "凭证号按年度统一编码排序（1是，0否，默认为否） ", position = 37)
    private String iyearCode;

    @ApiModelProperty(value = "制单与审核允许为同一人（1是，0否，默认为是） ", position = 38)
    private String iverify;

    @ApiModelProperty(value = "审核与取消审核须为同一人（1是，0否，默认为是） ", position = 39)
    private String iverifyCancel;

    @ApiModelProperty(value = "出纳凭证必须进行出纳签字（1是，0否，默认为否） ", position = 40)
    private String icashier;

    @ApiModelProperty(value = "凭证必须大经由主管签字（1是，0否，默认为否） ", position = 41)
    private String imanager;

    @ApiModelProperty(value = "凭证必须审核后记账（1是，0否，默认为是） ", position = 42)
    private String ifVerify;

    @ApiModelProperty(value = "允许修改业务系统凭证（1是，0否，默认为否） ", position = 43)
    private String iotherAccvouch;

    @ApiModelProperty(value = "凭证是否使用套打纸（1是，0否，默认为否）", position = 44)
    private String ivoucherPrint;

    @ApiModelProperty(value = "账簿是否使用套打（1是，0否，默认为否）", position = 45)
    private String ibookPrint;

    @ApiModelProperty(value = "是否允许修改、作废他人填制的凭证（1是，0否，默认为是）", position = 47)
    private String imodifyOther;

    @ApiModelProperty(value = "是否允许查看他人凭证（1是，0否，默认为是）", position = 48)
    private String iseeOther;

    @ApiModelProperty(value = "允许删除业务系统凭证（1是、0否，默认为否）", position = 49)
    private String iotherAccvouchDel;

    @ApiModelProperty(value = "新增凭证日期规则（1、登录日期，2、系统日期，3、手动添加）", position = 50)
    private String addAccDate;

    @ApiModelProperty(value = "出纳签字日期规则（1、登录日期，2、系统日期，3、手动添加）", position = 51)
    private String cashhierDate;

    @ApiModelProperty(value = "审核凭证日期规则（1、登录日期，2、系统日期，3、手动添加）", position = 52)
    private String verifyDate;

    @ApiModelProperty(value = "主管签字日期规则（1、登录日期，2、系统日期，3、手动添加）", position = 53)
    private String managerDate;

    @ApiModelProperty(value = "凭证记账日期规则（1、登录日期，2、系统日期，3、手动添加）", position = 54)
    private String bookDate;

    @ApiModelProperty(value = "往来核销日期规则（1、登录日期，2、系统日期，3、手动添加）", position = 55)
    private String wotDate;

    @ApiModelProperty(value = "是否允许跨公司审核记账（1是、0否，默认为否）", position = 56)
    private String ifreeConfirm;

    @ApiModelProperty(value = "是否允许取消记账（1是、0否，默认为是）", position = 57)
    private String ibook;

    @ApiModelProperty(value = "是否允许取消结账（1是、0否，默认为是）", position = 58)
    private String icheckOut;

    @ApiModelProperty(value = "是否控制先出纳签字后审核凭证（1是、0否，默认为是）", position = 59)
    private String icash;

    @ApiModelProperty(value = "结账检查损益结轩是否完成（1是、0否，默认为否）", position = 60)
    private String iprofit;

    @ApiModelProperty(value = "上月未结账本月允许结账（1是、0否，默认为否）", position = 61)
    private String icheckOutNext;

    @ApiModelProperty(value = "是否外币汇率管控（1是、0否，默认为否）", position = 70)
    private String iexchangeRateControl;

    @ApiModelProperty(value = "账套状态 1：正常 0：暂存 -1:业务删除", position = 62)
    private String flag;

  /*  @ApiModelProperty(value = "唯一编码", position = 63)
    private String uniqueCode;*/

    @ApiModelProperty(value = "数量核算科目单价允许为空（1是、0否，默认为否）", position = 64)
    private String price;

    @ApiModelProperty(value = "数量核算科目数量允许为空（1是、1否，默认为否）", position = 65)
    private String inum;

    @ApiModelProperty(value = "借贷不平是否保存为暂存凭证（1是、0否，默认为是）", position = 66)
    private String isave;

    @ApiModelProperty(value = "外币核算科目汇率为空是否允许保存（1是、0否，默认为否）", position = 67)
    private String iexchange;

    @ApiModelProperty(value = "出纳签字取消须为同一人")
    private String icashierNo;

    @ApiModelProperty(value = "主管签字取消须为同一人")
    private String icdirectorNo;

    @ApiModelProperty(value = "允许跨公司制单", position = 68)
    private String ifreecorp;

    @ApiModelProperty(value = "是否预算会计（1是，0否，默认为否）", position = 68)
    private String ibudgetAccounting;

    @ApiModelProperty(value = "是否双凭证（1是，0否，默认为否）", position = 68)
    private String idoubleAccvoucher;

    @ApiModelProperty(value = "是否为非企业机构（1是，0否，默认为否）", position = 68)
    private String icorp;

    @ApiModelProperty(value = "公司代码-3位数字/字母", position = 68)
    private String coCode;

    /****************  补充  *****************/
    @ApiModelProperty(value = "是否预置会计准则（1是，0否，默认为是）")
    private String ibudgetAccStandard;

    @ApiModelProperty(value = "凭证类别编码")
    private String voucherTypeNum;

    @ApiModelProperty(value = "联系人")
    private String contacts;

    @ApiModelProperty(value = "联系电话")
    private String telephone;

    @ApiModelProperty(value = "启用期间")
    private String startPeriod;

    @ApiModelProperty(value = "年度开始日期")
    private String yearStartDate;

    @ApiModelProperty(value = "年度结束日期")
    private String yearEndDate;

    @ApiModelProperty(value = "说明")
    private String remarks;

    @ApiModelProperty(value = "凭证编号小数位数 ")
    private String accvouchDec;

    @ApiModelProperty(value = "其他凭证编码 ")
    private String  voucherTypeOtherNums;

    private String beiyong1; // frp 地址
    private String beiyong2;
    private String beiyong3;
    @ApiModelProperty(value = "租户Id", position = 45)
    @Transient
    private String tenantId;
}


