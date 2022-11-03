package org.boozsoft.domain.vo.stock;

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
public class CaiGouTongJiSupVo implements Serializable {
	private String cvencode;
	private String bcheck;
	private String ddate;
	private String ccode;
	private String deptName;
	private String psnName;
	private String custId;
	private String custCode;
	private String custName;
	private String jscustCode;
	private String jscustName;
	private String mainUnitName;
	private String unitName;
	private String dingDanHao;
	private String isGive;
	private String cnumber;

	@ApiModelProperty(value = "数量")
	private String baseQuantity;
	@ApiModelProperty(value = "无税金额")
	private String icost;
	@ApiModelProperty(value = "税额")
	private String itaxprice;
	@ApiModelProperty(value = "价税合计")
	private String isum;
	@ApiModelProperty(value = "累计入库")
	private String isumRuku;
	@ApiModelProperty(value = "累计退货")
	private String isumTuihuo;
	@ApiModelProperty(value = "累计发票")
	private String isumFapiao;
	@ApiModelProperty(value = "累计结款")
	private String isumJieKuan;
	@ApiModelProperty(value = "未结款金额【计算得出】")
	private String noJieKuan;
}
