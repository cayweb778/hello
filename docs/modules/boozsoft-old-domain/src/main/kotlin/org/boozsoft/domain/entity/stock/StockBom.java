package org.boozsoft.domain.entity.stock;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;

@Data
@Table("stock_bom")
@Accessors(chain = true)
@ApiModel(value = "物料清单主表", description = "物料清单主表")
public class StockBom implements Serializable {

	@Id
	@ApiModelProperty(value = "ID", hidden = true)
	@CreatedBy
	@JsonSerialize(using = ToStringSerializer.class)
	private String id;
	@Transient
	@ApiModelProperty(value = "公司唯一码", hidden = true)
	private String tenantId;
	@ApiModelProperty(value = "BOM唯一码", hidden = true)
	private String bomUniqueId;
	@ApiModelProperty(value = "BOM识别码（手工录入或规则编码生成，不允许重复）", hidden = true)
	private String bomSysId;
	@ApiModelProperty(value = "BOM父件编码（允许重复）", hidden = true)
	private String bomId;
	@ApiModelProperty(value = "BOM父件名称（允许重复）", hidden = true)
	private String bomName;
	@ApiModelProperty(value = "添加人唯一码（操作员）", hidden = true)
	private String cmaker;
	@ApiModelProperty(value = "添加时间", hidden = true)
	private String cmakerTime;
	@ApiModelProperty(value = "BOM父件版本号（允许重复）", hidden = true)
	private String bomVersion;
	@ApiModelProperty(value = "BOM父件版本名称", hidden = true)
	private String bomVerName;
	@ApiModelProperty(value = "BOM父件版本开始日期", hidden = true)
	private String bomVerStartDate;
	@ApiModelProperty(value = "BOM父件版本结束日期", hidden = true)
	private String bomVerStopDate;
	@ApiModelProperty(value = "BOM父件版本设置日期", hidden = true)
	private String ddate;
	@ApiModelProperty(value = "BOM类别（1研发设计EBOM、2生产制造MBOM、3销售及服务SBOM）", hidden = true)
	private String bstyle;
	@ApiModelProperty(value = "父件计量单位编码", hidden = true)
	private String unitId;
	@ApiModelProperty(value = "父件主计量单位编码", hidden = true)
	private String cunitid;
	@ApiModelProperty(value = "父件数量", hidden = true)
	private String quantity;
	@ApiModelProperty(value = "父件主计量数量", hidden = true)
	private String baseQuantity;
	@ApiModelProperty(value = "编制部门（部门唯一码）", hidden = true)
	private String cdepcode;
	@ApiModelProperty(value = "编制人（人员唯一码）", hidden = true)
	private String cpersoncode;
	@ApiModelProperty(value = "版本说明", hidden = true)
	private String bomExplain;
	@ApiModelProperty(value = "损耗率", hidden = true)
	private String sunhaoLv;
	@ApiModelProperty(value = "成品率", hidden = true)
	private String chengpingLv;
	@ApiModelProperty(value = "停用状态（1停用、0或其他未关闭）", hidden = true)
	private String bcloser;
	@ApiModelProperty(value = "停用时间", hidden = true)
	private String bcloserTime;
	@ApiModelProperty(value = "停用人名称（操作员）", hidden = true)
	private String bcloserUser;
	@ApiModelProperty(value = "停用原因", hidden = true)
	private String bcloserExplain;

	@ApiModelProperty(value = "是否审核 1 审核", hidden = true)
	private String  bcheck;
	@ApiModelProperty(value = "审核时间", hidden = true)
	private String  bcheckTime;
	@ApiModelProperty(value = "=审核操作员", hidden = true)
	private String  bcheckUser;


	@ApiModelProperty(value = "", hidden = true)
	private String cfree1;
	@ApiModelProperty(value = "", hidden = true)
	private String cfree2;
	@ApiModelProperty(value = "", hidden = true)
	private String cfree3;
	@ApiModelProperty(value = "", hidden = true)
	private String cfree4;
	@ApiModelProperty(value = "", hidden = true)
	private String cfree5;
	@ApiModelProperty(value = "", hidden = true)
	private String cfree6;
	@ApiModelProperty(value = "", hidden = true)
	private String cfree7;
	@ApiModelProperty(value = "", hidden = true)
	private String cfree8;
	@ApiModelProperty(value = "", hidden = true)
	private String cfree9;
	@ApiModelProperty(value = "", hidden = true)
	private String cfree10;
	@ApiModelProperty(value = "", hidden = true)
	private String cfree11;
	@ApiModelProperty(value = "", hidden = true)
	private String cfree12;

	@Transient
	private String label;
	@Transient
	private String value;
	@Transient
	private String title;

	@Transient
	private String entryList;
}
