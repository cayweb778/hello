package org.boozsoft.domain.entity.stock;

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
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;

/**
 * @Description  
 * @Author  myh
 * @Date 2022-08-22 
 */

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table ( "stock_jiesuans" )
@ApiModel(value="采购结算单子表",description="")
public class StockJiesuans  implements Serializable {


	@Id
	@CreatedBy
	@JsonSerialize(using = ToStringSerializer.class)
	private String id;

	private String tenantId;

	private String iyear;

	@ApiModelProperty(value = "入库行次唯一码")
	private String lineCode;

	@ApiModelProperty(value = "结算单据类型（空，CGDHD到货单，CGRKD采购发票）")
	private String ccodeLy;

	@ApiModelProperty(value = "单据日期")
	private String ddate;

	@ApiModelProperty(value = "单据编号")
	private String ccode;

	@ApiModelProperty(value = "存货唯一码")
	private String cinvode;

	@ApiModelProperty(value = "结算供应商唯一码")
	private String cvencodeJs;

	@ApiModelProperty(value = "计量单位唯一码")
	private String cgUnitId;

	@ApiModelProperty(value = "入库结算数量 （10位小数点）")
	private String quantityRuku;

	@ApiModelProperty(value = "到货结算数量 （10位小数点）（采购发票）")
	private String quantityDaohuo;

	@ApiModelProperty(value = "结算单价")
	private String priceJs;

	@ApiModelProperty(value = "结算金额")
	private String icostJs;

	@ApiModelProperty(value = "暂估单价")
	private String priceZg;

	@ApiModelProperty(value = "暂估金额")
	private String icostZg;

	@ApiModelProperty(value = "入库单号码")
	private String ccodeRuku;

	@ApiModelProperty(value = "到货单（采购发票）号码")
	private String ccodeDaohuo;

	@ApiModelProperty(value = "项目唯一码")
	private String citemcode;

}
