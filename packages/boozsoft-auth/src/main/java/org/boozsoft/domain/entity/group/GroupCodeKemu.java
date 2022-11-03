package org.boozsoft.domain.entity.group;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table ("_app_group_code_kemu" )
@ApiModel(value="code_kemu: table",description="code_kemu: table")
public class GroupCodeKemu {

	@Id
	@CreatedBy
	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty(value = "主键", position = 0)
	private String id;

	@ApiModelProperty(value = "唯一编码", position = 1)
	private String uniqueCode;

	@ApiModelProperty(value = "所属会计准则唯一码", position = 2)
	private String uniqueAccStandard;

	@ApiModelProperty(value = "科目类型", position = 3)
	private String cclass;

	@ApiModelProperty(value = "类型英文名称", position = 4)
	private String cclassEngl;

	@ApiModelProperty(value = "科目编码", position = 5)
	private String ccode;

	@ApiModelProperty(value = "科目名称", position = 6)
	private String ccodeName;

	@ApiModelProperty(value = "科目英文名称", position = 7)
	private String ccodeEngl;

	@ApiModelProperty(value = "科目级次（从1至最大30）", position = 8)
	private String igrade;

	@ApiModelProperty(value = "科目方向（1：借，0：贷）", position = 9)
	private String bprogerty;

	@ApiModelProperty(value = "账页格式", position = 10)
	private String cbookType;

	@ApiModelProperty(value = "账页格式英文名称", position = 11)
	private String cbookTypeEngl;
	@ApiModelProperty(value = "预算会计；1是", position = 21)
	private String yusuan;
	/**************************************辅助核算***************************************************/
	@ApiModelProperty(value = "是否个人核算（1是，0否）", position = 12)
	private String bperson;
	@ApiModelProperty(value = "是否客户核算（1是，0否）", position = 13)
	private String bcus;
	@ApiModelProperty(value = "是否供应商核算（1是，0否）", position = 14)
	private String bsup;
	@ApiModelProperty(value = "是否部门核算（1是，0否）", position = 15)
	private String bdept;
	@ApiModelProperty(value = "是否项目核算（1是，0否）", position = 16)
	private String bitem;
	@ApiModelProperty(value = "项目核算大类编码", position = 17)
	private String cassItem;
	@ApiModelProperty(value = "是否数量核算（1是，0否）：需要默认值", position = 18)
	private String bnum;
	@ApiModelProperty(value = "数量核算计量单位名称", position = 19)
	private String menterage;
	@ApiModelProperty(value = "是否存货核算（1是，0否）", position = 20)
	private String bstock;
	@ApiModelProperty(value = "外币核算（1是，0否）需要默认值", position = 27)
	private String currency;
	@ApiModelProperty(value = "币种 3位国际代码", position = 27)
	private String currencyType;
	/***************************************END**************************************************/

	@ApiModelProperty(value = "是否现金（1是，0否）", position = 21)
	private String bcash;

	@ApiModelProperty(value = "是否银行（1是，0否）", position = 22)
	private String bbank;

	@ApiModelProperty(value = "是否末级科目（1是，0否）", position = 23)
	private String bend;

	@ApiModelProperty(value = "是否日记账（1是，0否）", position = 25)
	private String bdaybook;

	@ApiModelProperty(value = "是否停用(1.启用;0停用)", position = 26)
	private String flag;

	@ApiModelProperty(value = "所属年度", position = 27)
	private String iyear;

	@ApiModelProperty(value = "科目模板ID", position = 48)
	private String templateId;

	@ApiModelProperty(value = "新增下级控制,1是", position = 27)
	private String lowerControl;

	@ApiModelProperty(value = "辅助核算控制,1是", position = 27)
	private String fuzhuControl;

	@ApiModelProperty(value = "上级科目编码", position = 27)
	private String superiorCcode;

	@ApiModelProperty(value = "现金等价物", position = 21)
	private String bcashEquivalence;

	@ApiModelProperty(value = "业务受控科目", position = 21)
	private String controlled;
	@ApiModelProperty(value = "平行记账；1是", position = 21)
	private String pxjz;
	@ApiModelProperty(value = "现金流量；1是", position = 21)
	private String xjll;
	@ApiModelProperty(value = "差异科目；1是", position = 21)
	private String cyfx;

	/********** 是预算会计显示四列 *********/
	@ApiModelProperty(value = "预算科目：预算来源", position = 21)
	private String ysYsly;
	@ApiModelProperty(value = "预算科目：支出功能分类", position = 21)
	private String ysZcgnfl;
	@ApiModelProperty(value = "预算科目：政府支出经济分类", position = 21)
	private String ysZfzcjjfl;
	@ApiModelProperty(value = "预算科目：部门支出经济分类", position = 21)
	private String ysBmzcjjfl;
	/********** END *********/

	@ApiModelProperty(value = "会计准测名称", position = 21)
	@Transient
	private String uniqueAccStandardName;
	@ApiModelProperty(value = "模板名称", position = 21)
	@Transient
	private String templateName;

	@ApiModelProperty(value = "辅助项1（辅助核算项编码）", position = 44)
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

	@Transient
	private String value;
	@Transient
	private String fuzhu;





	public GroupCodeKemu(String uniqueAccStandard, String cclass, String cclassEngl, String ccode, String ccodeName, String ccodeEngl, String igrade, String bprogerty, String cbookType, String cbookTypeEngl, String bperson, String bcus, String bsup, String bdept, String bitem, String cassItem, String bnum, String menterage, String bstock, String bcash, String bbank, String bend, String bdaybook, String flag, String iyear, String templateId, String currency, String currencyType, String lowerControl, String fuzhuControl, String superiorCcode, String bcashEquivalence, String controlled, String pxjz, String xjll, String cyfx/*, String uniqueAccStandardName, String templateName*/) {
		this.uniqueAccStandard = uniqueAccStandard;
		this.cclass = cclass;
		this.cclassEngl = cclassEngl;
		this.ccode = ccode;
		this.ccodeName = ccodeName;

		this.ccodeEngl = ccodeEngl;
		this.igrade = igrade;
		this.bprogerty = bprogerty;
		this.cbookType = cbookType;
		this.cbookTypeEngl = cbookTypeEngl;

		this.bperson = bperson;
		this.bcus = bcus;
		this.bsup = bsup;
		this.bdept = bdept;
		this.bitem = bitem;

		this.cassItem = cassItem;
		this.bnum = bnum;
		this.menterage = menterage;
		this.bstock = bstock;
		this.bcash = bcash;

		this.bbank = bbank;
		this.bend = bend;
		this.bdaybook = bdaybook;
		this.flag = flag;
		this.iyear = iyear;

		this.currency = currency;
		this.currencyType = currencyType;
		this.templateId = templateId;
		this.lowerControl = lowerControl;
		this.fuzhuControl = fuzhuControl;

		this.superiorCcode = superiorCcode;
		this.bcashEquivalence = bcashEquivalence;
		this.controlled = controlled;
		this.pxjz = pxjz;
		this.xjll = xjll;

		this.cyfx = cyfx;
	/*	this.uniqueAccStandardName = uniqueAccStandardName;
		this.templateName = templateName;*/
	}


}
