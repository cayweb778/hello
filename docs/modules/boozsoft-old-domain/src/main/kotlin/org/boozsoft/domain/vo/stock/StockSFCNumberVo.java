package org.boozsoft.domain.vo.stock;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;


@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value="数量收发存汇总",description="数量收发存汇总")
public class StockSFCNumberVo implements Serializable {
	private String stockNum;
	private String stockName;
	private String stockGgxh;
	private String cwhcode;
	private String stockCangku;
	private String stockCangkuName;
	private String unitName;
	private String unitName1;
	private String unitName2;
	private String stockMeasurementType;
	private String stockMeasurementUnit;
	private BigDecimal qichu;
	private BigDecimal rk;
	private BigDecimal ck;
	private BigDecimal qimo;
	private BigDecimal rate;
	private BigDecimal rate1;
	private BigDecimal rate2;

	// 金额汇总表用到
	private BigDecimal qcprice;
	private BigDecimal qcicost;
	private BigDecimal rkprice;
	private BigDecimal rkicost;
	private BigDecimal ckprice;
	private BigDecimal ckicost;
}
