package org.boozsoft.domain.vo.fa;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 视图实体类
 *
 * @author Chill
 */
@Data
@EqualsAndHashCode(callSuper=false)
@Accessors(chain = true)
@ApiModel(value = "FaCardVo对象", description = "FaCardVo对象")
public class FaCardVo {
    @ApiModelProperty(value = "公司唯一码", hidden = true)
    private String uniqueCode;
    @ApiModelProperty(value = "管理代码", hidden = true)
    private String manageCode;
    @ApiModelProperty(value = "卡片唯一码", hidden = true)
    private String cardUnique;
    @ApiModelProperty(value = "系统编号", hidden = true)
    private String sysId;
    @ApiModelProperty(value = "资产卡片编码", hidden = true)
    private String cardCode;
    @ApiModelProperty(value = "资产名称", hidden = true)
    private String faName;
    @ApiModelProperty(value = "首次添加时间", hidden = true)
    private String cdate;
    @ApiModelProperty(value = "卡片入账日期", hidden = true)
    private String creatTime;
    @ApiModelProperty(value = "购买日期", hidden = true)
    private String buyTime;
    @ApiModelProperty(value = "第一次折旧年月(通过入账日期、计提方式次月或当月确定折旧年月)", hidden = true)
    private String firstTime;
    @ApiModelProperty(value = "折旧到期日（隐藏字段）", hidden = true)
    private String jzdqrTime;
    @ApiModelProperty(value = "规格型号", hidden = true)
    private String speciType;
    @ApiModelProperty(value = "折旧分配（0多部门，1多项目）", hidden = true)
    private String zhejiuType;
    @ApiModelProperty(value = "使用状态", hidden = true)
    private String useType;
    @ApiModelProperty(value = "使用年限(月)", hidden = true)
    private String life;
    @ApiModelProperty(value = "已计提年限(月)", hidden = true)
    private String jitiLife;
    @ApiModelProperty(value = "原值", hidden = true)
    private String yuanzhi;
    @ApiModelProperty(value = "累计折旧", hidden = true)
    private String ljzhejiu;
    @ApiModelProperty(value = "拆旧方式", hidden = true)
    private String zhejiuMethod;
    @ApiModelProperty(value = "净残值率", hidden = true)
    private String jingcanzhilv;
    @ApiModelProperty(value = "部门名称", hidden = true)
    private String deptName;
    @ApiModelProperty(value = "项目名称", hidden = true)
    private String projectName;
    @ApiModelProperty(value = "资产分类", hidden = true)
    private String faClass;
    @ApiModelProperty(value = "月折旧率", hidden = true)
    private String yuezhejiulv;
    @ApiModelProperty(value = "月折旧额", hidden = true)
    private String yuezhejiue;
    @ApiModelProperty(value = "是否拆分", hidden = true)
    private String chaifen;
    @ApiModelProperty(value = "是否借出", hidden = true)
    private String jiechu;
    @ApiModelProperty(value = "是否减少", hidden = true)
    private String jianshao;
    @ApiModelProperty(value = "本月折旧", hidden = true)
    private String zjBy;
    @ApiModelProperty(value = "累计折旧", hidden = true)
    private String zjLj;
    @ApiModelProperty(value = "本年折旧", hidden = true)
    private String zjBn;
    @ApiModelProperty(value = "数量", hidden = true)
    private String shuliang;
    @ApiModelProperty(value = "年", hidden = true)
    private String iyear;
    @ApiModelProperty(value = "月", hidden = true)
    private String imonth;
    @ApiModelProperty(value = "进销税额变动值", hidden = true)
    private String jingxiangshui;
    @ApiModelProperty(value = "进项税率", hidden = true)
    private String jinxiangshuilv;
}
