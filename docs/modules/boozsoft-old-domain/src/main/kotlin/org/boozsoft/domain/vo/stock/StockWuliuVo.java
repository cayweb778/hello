package org.boozsoft.domain.vo.stock;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class StockWuliuVo {
	private String cvenName;
	private String psnName;
	private String deptName;
	private String cpersonName;
	private String custName;
	private String cnumber;
	private String bcheck;
	private String ddate;
	private String ccode;
	private String cvencode;
	private String cuserTel;
	private String cAddress;
	private String wuliuId;
	private String wuliuTel;
}
