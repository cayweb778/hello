package org.boozsoft.domain.entity.stock;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.Accessors;
import io.swagger.annotations.ApiModel;
import org.springframework.data.annotation.Id;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * @Description  存货档案
 * @Author  myh
 * @Date 2021-12-29 
 */

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table ( "stock" )
@ApiModel(value="存货档案",description="存货档案")
public class Stock  implements Serializable {


	@Id
	@CreatedBy
	@JsonSerialize(using = ToStringSerializer.class)
	private String id;

	private String tenantId;

	@ApiModelProperty(value = "添加日期")
	private String createTime;

	@ApiModelProperty(value = "存货编码")
	private String stockNum;

	@ApiModelProperty(value = "存货名称")
	private String stockName;

	@ApiModelProperty(value = "规格型号")
	private String stockGgxh;

	@ApiModelProperty(value = "助记码")
	private String stockZjm;

	@ApiModelProperty(value = "存货分类")
	private String stockClass;

	@ApiModelProperty(value = "条形码")
	private String stockBarcode;

	@ApiModelProperty(value = "国家（地区）")
	private String stockCountry;

	@ApiModelProperty(value = "生产厂商")
	private String stockManufacture;

	@ApiModelProperty(value = "生产地点")
	private String stockManufactureAddress;

	@ApiModelProperty(value = "品牌")
	private String stockBrand;

	@ApiModelProperty(value = "售后网址")
	private String stockAfterSaleWeb;

	@ApiModelProperty(value = "售后方式")
	private String stockAfterSaleType;

	@ApiModelProperty(value = "计量方式:单计量/多计量")
	private String stockMeasurementType;

	@ApiModelProperty(value = "计量单位ID")
	private String stockMeasurementUnit;

	@ApiModelProperty(value = "报表单位")
	private String stockReportingUnit;

	@ApiModelProperty(value = "库存单位")
	private String stockInventoryUnit;

	@ApiModelProperty(value = "采购单位")
	private String stockPurchaseUnit;

	@ApiModelProperty(value = "销售单位")
	private String stockMarketUnit;

	@ApiModelProperty(value = "生产单位")
	private String stockProductionUnit;

	@ApiModelProperty(value = "零售单位")
	private String stockRetailUnit;

	@ApiModelProperty(value = "海关单位")
	private String stockCustomsUnit;

	@ApiModelProperty(value = "默认供应商ID")
	private String stockSupplier;

	@ApiModelProperty(value = "默认仓库ID")
	private String stockCangku;

	@ApiModelProperty(value = "默认采购部门ID")
	private String stockPurchaseDept;

	@ApiModelProperty(value = "默认采购员ID")
	private String stockPurchaseUser;

	@ApiModelProperty(value = "是否销售 1是")
	private String stockPropertyMarket;

	@ApiModelProperty(value = "是否采购 1是")
	private String stockPropertyPurchase;

	@ApiModelProperty(value = "是否生产 1是")
	private String stockPropertyProduction;

	@ApiModelProperty(value = "是否委外 1是")
	private String stockPropertyEntrust;

	@ApiModelProperty(value = "是否应税劳务费用 1是")
	private String stockPropertyYslwfy;

	@ApiModelProperty(value = "是否批次管理 1是")
	private String stockPropertyBatch;

	@ApiModelProperty(value = "是否出库跟踪入库 1是")
	private String stockPropertyCkrk;

	@ApiModelProperty(value = "是否序列号管理 1是")
	private String stockPropertySeriesNumber;

	@ApiModelProperty(value = "MA码管理 1是")
	private String stockPropertyMa;

	@ApiModelProperty(value = "录入日期")
	private String stockEnterTime;
	@ApiModelProperty(value = "状态 1正常 0")
	private String stockFlag;



	@ApiModelProperty(value = "存货代码")
	private String stockCode;
	@ApiModelProperty(value = "出入库质检 1是")
	private String stockPropertyCkrkzj;
	@ApiModelProperty(value = "危化品 1是")
	private String stockPropertyDanger;
	@ApiModelProperty(value = "ROSH物料 1是")
	private String stockPropertyRosh;
	@ApiModelProperty(value = "有效期管理（1是，其他否）")
	private String stockIndateManage;
	@ApiModelProperty(value = "有效期单位")
	private String stockIndateUnit;
	@ApiModelProperty(value = "有效期时长")
	private String stockIndateDuration;
	@ApiModelProperty(value = "预警天数")
	private String stockIndateWarningDay;
	@ApiModelProperty(value = "计价方式")
	private String stockValuationType;
	@ApiModelProperty(value = "是否独立仓库 1是 0否（查仓库级别档案表）")
	private String stockCangkuDuli;

	@ApiModelProperty(value = "编码方式：0自动，1手动")
	private String isManual;

	@ApiModelProperty(value = "一级仓库名称")
	private String stockCangkuLevel1Name;
	@ApiModelProperty(value = "2级仓库名称")
	private String stockCangkuLevel2Name;
	@ApiModelProperty(value = "3级仓库名称")
	private String stockCangkuLevel3Name;
	@ApiModelProperty(value = "4级仓库名称")
	private String stockCangkuLevel4Name;
	@ApiModelProperty(value = "5级仓库名称")
	private String stockCangkuLevel5Name;
	@ApiModelProperty(value = "6级仓库名称")
	private String stockCangkuLevel6Name;

	@ApiModelProperty(value = "主计量名称")
	private String stockUnitName;
	@ApiModelProperty(value = "计量1名称")
	private String stockUnitName1;
	@ApiModelProperty(value = "计量1规格型号")
	private String stockUnitGgxh1;
	@ApiModelProperty(value = "计量1条形码")
	private String stockUnitBarcode1;
	@ApiModelProperty(value = "计量2名称")
	private String stockUnitName2;
	@ApiModelProperty(value = "计量2规格型号")
	private String stockUnitGgxh2;
	@ApiModelProperty(value = "计量2条形码")
	private String stockUnitBarcode2;
	@ApiModelProperty(value = "计量1ID")
	private String stockUnitId1;
	@ApiModelProperty(value = "计量2ID")
	private String stockUnitId2;
	@ApiModelProperty(value = "主计量ID")
	private String stockUnitId;

	@Transient
	private String label;
	@Transient
	private String value;
	@Transient
	private String title;
	@Transient
	@ApiModelProperty(value = "主计量数 （10位小数点）",hidden = true)
	private BigDecimal xcl;
}
