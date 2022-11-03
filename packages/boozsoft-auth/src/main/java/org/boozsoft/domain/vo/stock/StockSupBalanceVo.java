package org.boozsoft.domain.vo.stock;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;


@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class StockSupBalanceVo implements Serializable {
	private String custCode;
	private String custName;

	private String qichu;
	private String cgdhd;
	private String yingfu;
	private String fukuan;
	private String qimo;
	private String lastMonthCgdhd;
	private String lastMonthYingfu;
	private String lastMonthFukuan;
}
