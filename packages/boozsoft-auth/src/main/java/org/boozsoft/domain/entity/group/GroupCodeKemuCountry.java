package org.boozsoft.domain.entity.group;

import lombok.Data;
import java.io.Serializable;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.Accessors;
import io.swagger.annotations.ApiModel;
import org.springframework.data.annotation.Id;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.relational.core.mapping.Table;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * @Description  
 * @Author  myh
 * @Date 2022-02-10 
 */

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table ( "_app_group_code_kemu_country" )
@ApiModel(value="国家科目表",description="国家科目表")
public class GroupCodeKemuCountry implements Serializable {


	@ApiModelProperty(value = "主键")
	@Id
	@CreatedBy
	@JsonSerialize(using = ToStringSerializer.class)
	private String id;

	@ApiModelProperty(value = "唯一编码")
	private String uniqueCode;

	@ApiModelProperty(value = "所属会计准则唯一码")
	private String uniqueAccStandard;

	@ApiModelProperty(value = "科目类型")
	private String cclass;

	@ApiModelProperty(value = "类型英文名称")
	private String cclassEngl;

	@ApiModelProperty(value = "科目编码")
	private String ccode;

	@ApiModelProperty(value = "科目名称")
	private String ccodeName;

	@ApiModelProperty(value = "科目英文名称")
	private String ccodeEngl;

	@ApiModelProperty(value = "科目级次（从1至最大30）")
	private String igrade;

	@ApiModelProperty(value = "科目方向（1：借，0：贷）")
	private String bprogerty;

	@ApiModelProperty(value = "账页格式")
	private String cbookType;

	@ApiModelProperty(value = "账页格式英文名称")
	private String cbookTypeEngl;

	@ApiModelProperty(value = "是否个人核算（1是，0否）")
	private String bperson;

	@ApiModelProperty(value = "是否客户核算（1是，0否）")
	private String bcus;

	@ApiModelProperty(value = "是否供应商核算（1是，0否）")
	private String bsup;

	@ApiModelProperty(value = "是否部门核算（1是，0否）")
	private String bdept;

	@ApiModelProperty(value = "是否项目核算（1是，0否）")
	private String bitem;

	@ApiModelProperty(value = "项目核算大类编码")
	private String cassItem;

	@ApiModelProperty(value = "是否数量核算（1是，0否）")
	private String bnum;

	@ApiModelProperty(value = "数量核算计量单位")
	private String menterage;

	@ApiModelProperty(value = "是否存货核算（1是，0否）")
	private String bstock;

	@ApiModelProperty(value = "是否现金（1是，0否）")
	private String bcash;

	@ApiModelProperty(value = "是否银行（1是，0否）")
	private String bbank;

	@ApiModelProperty(value = "是否末级科目（1是，0否）")
	private String bend;

	@ApiModelProperty(value = "是否日记账（1是，0否）")
	private String bdaybook;

	@ApiModelProperty(value = "是否停用(1.启用;0停用)")
	private String flag;

	@ApiModelProperty(value = "所属年度")
	private String iyear;

	@ApiModelProperty(value = "科目模板ID")
	private String templateId;

	@ApiModelProperty(value = "外币核算（1是，0否）")
	private String currency;

	@ApiModelProperty(value = "币种 3位国际代码")
	private String currencyType;

	@ApiModelProperty(value = "新增下级控制,1是")
	private String lowerControl;

	@ApiModelProperty(value = "辅助核算控制,1是")
	private String fuzhuControl;

	@ApiModelProperty(value = "上级科目编码")
	private String superiorCcode;

	@ApiModelProperty(value = "现金等价物")
	private String bcashEquivalence;

	@ApiModelProperty(value = "业务受控科目1是")
	private String controlled;

	@ApiModelProperty(value = "是否平行记账；1是")
	private String pxjz;

	@ApiModelProperty(value = "是否现金流量；1是")
	private String xjll;

	@ApiModelProperty(value = "是否差异分析；1是")
	private String cyfx;

	private String cdfine1;

	private String cdfine2;

	private String cdfine3;

	private String cdfine4;

	private String cdfine5;

	private String cdfine6;

	private String cdfine7;

	private String cdfine8;

	private String cdfine9;

	private String cdfine10;

	private String cdfine11;

	private String cdfine12;

	private String cdfine13;

	private String cdfine14;

	private String cdfine15;

	private String cdfine16;

	private String cdfine17;

	private String cdfine18;

	private String cdfine19;

	private String cdfine20;

	private String cdfine21;

	private String cdfine22;

	private String cdfine23;

	private String cdfine24;

	private String cdfine25;

	private String cdfine26;

	private String cdfine27;

	private String cdfine28;

	private String cdfine29;

	private String cdfine30;

	private String yusuan;

	@ApiModelProperty(value = "预算科目：预算来源")
	private String ysYsly;

	@ApiModelProperty(value = "预算科目：支出功能分类")
	private String ysZcgnfl;

	@ApiModelProperty(value = "预算科目：政府支出经济分类")
	private String ysZfzcjjfl;

	@ApiModelProperty(value = "预算科目：部门支出经济分类")
	private String ysBmzcjjfl;
}
