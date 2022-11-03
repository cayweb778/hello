package org.boozsoft.domain.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

// 结转上年使用
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class CarryDownCodeKemuVo {
	private String key;			// 页面用
	private String upCcode;
	private String upCcodeName;
	private String upCcodeBprogerty;
	private BigDecimal upMd;
	private BigDecimal upMc;

	private String thisCcode;
	private String thisCcodeName;

	private List<AccvoucherVo3> fuzhuQclist;
}
