package org.boozsoft.domain.vo;


import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value="部门科目明细",description="部门科目明细")
public class DeptCodeAccvoucherVo {
	private String iyperiod;
	private String csign;
	private String inoId;
	private String imonth;
	private String ccode;
	private String ccodeName;
	private String cclass;
	private String cdeptId;
	private String cpersonId;
	private String projectId;
	private String ccusId;
	private String csupId;
	private String ibook;
	private String dbillDate;// 凭证日期
	private String cdigest;// 凭证摘要
	private String cunitPrice;// 单价
	private String unitMeasurement;// 计量单位
	private String foreignCurrency;// 外币名称
	private String mdF;// 外币汇率
	private BigDecimal md;
	private BigDecimal mc;
	private BigDecimal ndS;
	private BigDecimal ncS;
	private BigDecimal nfratMd;
	private BigDecimal nfratMc;
}
