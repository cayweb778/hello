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
@ApiModel(value = "FaDepreciationVo对象", description = "FaDepreciationVo对象")
public class FaDepreciationVo {
    @ApiModelProperty(value = "公司唯一码", hidden = true)
    private String uniqueCode;
    @ApiModelProperty(value = "管理代码", hidden = true)
    private String manageCode;
    @ApiModelProperty(value = "卡片唯一码", hidden = true)
    private String cardUnique;
    @ApiModelProperty(value = "年度（自然年度）", hidden = true)
    private String iyear;
    @ApiModelProperty(value = "月份（自然月份）", hidden = true)
    private String imonth;
    @ApiModelProperty(value = "本月折旧", hidden = true)
    private String zjBy;
    @ApiModelProperty(value = "累计折旧", hidden = true)
    private String zjLj;
    @ApiModelProperty(value = "本年折旧", hidden = true)
    private String zjBn;
    @ApiModelProperty(value = "本月工作量", hidden = true)
    private String gzlBy;
    @ApiModelProperty(value = "累计工作量", hidden = true)
    private String gzlLj;
    @ApiModelProperty(value = "部门唯一码", hidden = true)
    private String deptUnique;
    @ApiModelProperty(value = "部门折旧百分比，单部门为100%", hidden = true)
    private String deptBl;
    @ApiModelProperty(value = "项目唯一码", hidden = true)
    private String projectUnique;
    @ApiModelProperty(value = "项目折旧百分比，单项目为100%", hidden = true)
    private String projectBl;
    @ApiModelProperty(value = "资产卡片编码", hidden = true)
    private String cardCode;
    @ApiModelProperty(value = "资产名称", hidden = true)
    private String faName;
    @ApiModelProperty(value = "规格型号", hidden = true)
    private String speciType;
    @ApiModelProperty(value = "资产原值", hidden = true)
    private String yuanzhi;
    @ApiModelProperty(value = "月折旧率", hidden = true)
    private String yuezhejiulv;
    @ApiModelProperty(value = "月折旧额", hidden = true)
    private String yuezhejiue;
}
