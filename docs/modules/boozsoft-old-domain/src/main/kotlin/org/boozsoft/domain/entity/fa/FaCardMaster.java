package org.boozsoft.domain.entity.fa;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "数据表：资产卡片主表", description = "资产卡片主表")
@Table("fa_card_master")
@Data
public class FaCardMaster {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "公司唯一码", hidden = true)
    private String uniqueCode;
    @ApiModelProperty(value = "管理代码", hidden = true)
    private String manageCode;
    @ApiModelProperty(value = "卡片唯一码", hidden = true)
    private String cardUnique;
    @ApiModelProperty(value = "首次添加时间", hidden = true)
    private String cdate;
    @ApiModelProperty(value = "操作员唯一码", hidden = true)
    private String cuserId;
    @ApiModelProperty(value = "是否原始卡片（1是，0或空否）", hidden = true)
    private String isYuanshi;
    @ApiModelProperty(value = "卡片入账日期", hidden = true)
    private String creatTime;
    @ApiModelProperty(value = "系统编号", hidden = true)
    private String sysId;
    @ApiModelProperty(value = "资产卡片编码", hidden = true)
    private String cardCode;
    @ApiModelProperty(value = "资产属性唯一码", hidden = true)
    private String faAssets;
    @ApiModelProperty(value = "资产名称", hidden = true)
    private String faName;
    @ApiModelProperty(value = "规格型号", hidden = true)
    private String speciType;
    @ApiModelProperty(value = "资产组", hidden = true)
    private String assetGroup;
    @ApiModelProperty(value = "使用人员唯一码", hidden = true)
    private String userId;
    @ApiModelProperty(value = "增加方式", hidden = true)
    private String addType;
    @ApiModelProperty(value = "计量单位", hidden = true)
    private String unitId;
    @ApiModelProperty(value = "经济用途", hidden = true)
    private String jingjiyongtu;
    /*@ApiModelProperty(value = "进项税率", hidden = true)
    private String jinxiangshuilv;*/
    /*@ApiModelProperty(value = "价税合计", hidden = true)
    private String jiashuiheji;*/
    @ApiModelProperty(value = "外币币种", hidden = true)
    private String wbCurrency;
    @ApiModelProperty(value = "外币原值", hidden = true)
    private String wbYuanzhi;
    @ApiModelProperty(value = "外币单位", hidden = true)
    private String wbUnit;
    @ApiModelProperty(value = "外币汇率", hidden = true)
    private String wbHuilv;
    @ApiModelProperty(value = "本位币币种", hidden = true)
    private String currencyCode;
    @ApiModelProperty(value = "对应计提科目", hidden = true)
    private String jitikemu;
    /*@ApiModelProperty(value = "是否拆分", hidden = true)
    private String chaifen;
    @ApiModelProperty(value = "是否借出", hidden = true)
    private String jiechu;
    @ApiModelProperty(value = "是否减少", hidden = true)
    private String jianshao;
    @ApiModelProperty(value = "拆分借出减少日期", hidden = true)
    private String jjTime;
    @ApiModelProperty(value = "折旧到期日（隐藏字段）", hidden = true)
    private String jzdqrTime;*/
    @ApiModelProperty(value = "购买日期", hidden = true)
    private String buyTime;
    @ApiModelProperty(value = "第一次折旧年月(通过入账日期、计提方式次月或当月确定折旧年月)", hidden = true)
    private String firstTime;
    @ApiModelProperty(value = "折旧分配（0多部门，1多项目）", hidden = true)
    private String zhejiuType;
    @ApiModelProperty(value = "拆分来源单据唯一码", hidden = true)
    private String sourceCardUnique;
    @ApiModelProperty(value = "", hidden = true)
    private String define1;
    @ApiModelProperty(value = "", hidden = true)
    private String define2;
    @ApiModelProperty(value = "", hidden = true)
    private String define3;
    @ApiModelProperty(value = "", hidden = true)
    private String define4;
    @ApiModelProperty(value = "", hidden = true)
    private String define5;
    @ApiModelProperty(value = "", hidden = true)
    private String define6;
    @ApiModelProperty(value = "", hidden = true)
    private String define7;
    @ApiModelProperty(value = "", hidden = true)
    private String define8;
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

    @Transient
    @ApiModelProperty(value = "计提方式", hidden = true)
    private String jitiType;
    @Transient
    @ApiModelProperty(value = "使用年限变动值", hidden = true)
    private String life;

}
