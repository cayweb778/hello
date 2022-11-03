package org.boozsoft.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class KmHuiZongVo {

	private String cclass; 					// 科目类型
	private String unitMeasurement; 		// 计量单位
	private String ccode; 					// 科目编码
	private String ccodeName;				// 科目名称
	private String dbillDate;				// 凭证日期
	private String foreignCurrency;			// 外币名称
	private String cbill;					// 制单人姓名
	private String ibook;					// 是否记账 1已记账
	private String csign;					// 凭证类型（记、转）
	private String ifrag;					// 凭证状态
	private Integer inoId;					// 凭证号
	private BigDecimal md;
	private BigDecimal mc;
	private BigDecimal ndS;
	private BigDecimal ncS;
	private BigDecimal nfratMd;
	private BigDecimal nfratMc;
	private BigDecimal ljMd;
	private BigDecimal ljMc;
}
