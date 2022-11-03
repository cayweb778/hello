package org.boozsoft.domain.vo;


import lombok.Data;
import java.math.BigDecimal;

@Data
public class SsphVo {

	private String ccode;
	private String ccodeName;
	private String cclass;
	private String bprogerty;
	// 是否数量核算
	private String bnum;
	// 数量单位
	private String menterage;
	// 是否外币核算
	private String currency;
	// 外币单位
	private String currencyType;

	private String cnum;
	private String nfrat;

	private BigDecimal md;
	private BigDecimal mc;

	// 1是0否 试算平衡标志
	private String msg;
}
