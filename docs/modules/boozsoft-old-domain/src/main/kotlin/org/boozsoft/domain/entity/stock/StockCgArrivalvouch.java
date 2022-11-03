package org.boozsoft.domain.entity.stock;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table ( "stock_cg_arrivalvouch" )
@ApiModel(value="采购到货单主表",description="采购到货单主表")
public class StockCgArrivalvouch {

	@Id
	@ApiModelProperty(value = "ID", hidden = true)
	@CreatedBy
	@JsonSerialize(using = ToStringSerializer.class)
	private String id;
	@Transient
	@ApiModelProperty(value = "公司唯一码",hidden = true)
	private String tenantId;
	@ApiModelProperty(value = "年度标志",hidden = true)
	private String iyear;
	@ApiModelProperty(value = "采购到货单编号",hidden = true)
	private String ccode;
	@ApiModelProperty(value = "制单人",hidden = true)
	private String cmaker;
	@ApiModelProperty(value = "单据日期",hidden = true)
	private String ddate;
	@ApiModelProperty(value = "采购类别（0、到货，1、退货）",hidden = true)
	private String bstyle;
	@ApiModelProperty(value = "供应商唯一码",hidden = true)
	private String cvencode;
	@ApiModelProperty(value = "部门唯一码",hidden = true)
	private String cdepcode;
	@ApiModelProperty(value = "仓库唯一码",hidden = true)
	private String cwhcode;
	@ApiModelProperty(value = "审核状态（1审核，0或其他未审）",hidden = true)
	private String bcheck;
	@ApiModelProperty(value = "审核时间",hidden = true)
	private String bcheckTime;
	@ApiModelProperty(value = "审核人唯一码",hidden = true)
	private String bcheckUser;
	@ApiModelProperty(value = "关闭状态（1关闭、0或其他未关闭）",hidden = true)
	private String bcloser;
	@ApiModelProperty(value = "关闭时间",hidden = true)
	private String bcloserTime;
	@ApiModelProperty(value = "关闭人唯一码",hidden = true)
	private String bcloserUser;
	@ApiModelProperty(value = "备注",hidden = true)
	private String cmemo;
	@ApiModelProperty(value = "业务员编码",hidden = true)
	private String cpersoncode;
	@ApiModelProperty(value = "表头税率",hidden = true)
	private String ipertaxrate;
	@ApiModelProperty(value = "主数量合计",hidden = true)
	private String squantity;
	@ApiModelProperty(value = "计量一合计",hidden = true)
	private String squantity1;
	@ApiModelProperty(value = "计量二合计",hidden = true)
	private String squantity2;
	@ApiModelProperty(value = "税额合计",hidden = true)
	private String staxprice;
	@ApiModelProperty(value = "金额合计",hidden = true)
	private String scost;
	@ApiModelProperty(value = "价税合计",hidden = true)
	private String ssum;
	@ApiModelProperty(value = "",hidden = true)
	private String cfree1;
	@ApiModelProperty(value = "",hidden = true)
	private String cfree2;
	@ApiModelProperty(value = "",hidden = true)
	private String cfree3;
	@ApiModelProperty(value = "",hidden = true)
	private String cfree4;
	@ApiModelProperty(value = "",hidden = true)
	private String cfree5;
	@ApiModelProperty(value = "",hidden = true)
	private String cfree6;
	@ApiModelProperty(value = "",hidden = true)
	private String cfree7;
	@ApiModelProperty(value = "",hidden = true)
	private String cfree8;
	@ApiModelProperty(value = "",hidden = true)
	private String cfree9;
	@ApiModelProperty(value = "",hidden = true)
	private String cfree10;
	@ApiModelProperty(value = "",hidden = true)
	private String cfree11;
	@ApiModelProperty(value = "",hidden = true)
	private String cfree12;

}
