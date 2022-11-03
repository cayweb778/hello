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
@ApiModel(value = "FaChaifenVo对象", description = "FaChaifenVo对象")
public class FaChaifenVo {
    @ApiModelProperty(value = "公司唯一码", hidden = true)
    private String uniqueCode;
    @ApiModelProperty(value = "管理代码", hidden = true)
    private String manageCode;
    @ApiModelProperty(value = "卡片唯一码", hidden = true)
    private String cardUnique;
    @ApiModelProperty(value = "拆分编码", hidden = true)
    private String chaifenCode;
    @ApiModelProperty(value = "拆分日期", hidden = true)
    private String chaifenTime;
    @ApiModelProperty(value = "拆分原因", hidden = true)
    private String chaifenReason;
    @ApiModelProperty(value = "系统编号", hidden = true)
    private String sysId;
    @ApiModelProperty(value = "资产卡片编码", hidden = true)
    private String cardCode;
    @ApiModelProperty(value = "资产名称", hidden = true)
    private String faName;
    @ApiModelProperty(value = "规格型号", hidden = true)
    private String speciType;
    @ApiModelProperty(value = "原值", hidden = true)
    private String yuanzhi;
    @ApiModelProperty(value = "累计折旧", hidden = true)
    private String ljzhejiu;
    @ApiModelProperty(value = "拆旧方式", hidden = true)
    private String zhejiuMethod;
    @ApiModelProperty(value = "净残值率", hidden = true)
    private String jingcanzhilv;
    @ApiModelProperty(value = "资产分类", hidden = true)
    private String faClass;
    @ApiModelProperty(value = "月折旧率", hidden = true)
    private String yuezhejiulv;
    @ApiModelProperty(value = "月折旧额", hidden = true)
    private String yuezhejiue;
    @ApiModelProperty(value = "本月折旧", hidden = true)
    private String zjBy;
    @ApiModelProperty(value = "累计折旧", hidden = true)
    private String zjLj;
    @ApiModelProperty(value = "本年折旧", hidden = true)
    private String zjBn;
    @ApiModelProperty(value = "数量", hidden = true)
    private String shuliang;
    @ApiModelProperty(value = "计量单位", hidden = true)
    private String unitName;
    @ApiModelProperty(value = "年", hidden = true)
    private String iyear;
    @ApiModelProperty(value = "月", hidden = true)
    private String imonth;
}
