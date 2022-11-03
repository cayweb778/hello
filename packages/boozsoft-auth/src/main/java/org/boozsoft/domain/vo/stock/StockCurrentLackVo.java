package org.boozsoft.domain.vo.stock;

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
public class StockCurrentLackVo implements Serializable {

	private String cinvode;					// 必填 存货编码
	private String cwhcode;					// 仓库
	private String batchId;					// 批次
	private String dpdate;					// 生产日期
	private String dvdate;					// 失效日期
	private BigDecimal baseQuantity;        // 主数量
	private BigDecimal xcl;        			// 现存量
	private BigDecimal keyong;        		// 可用量

	// 前台弹窗页面显示值
	private String cinvodeName;
	private String unitName;
	private String unitId;
	private BigDecimal lackBaseQuantity;	// 不足数量

	// 账表现存量查询--所需参数
	private String stockNum;
	private String stockName;
	private String stockGgxh;
	private String stockUnitId;
	private String stockUnitId1;
	private String stockUnitId2;
	private String stockUnitName;
	private String stockUnitName1;
	private String stockUnitName2;
	private String rate;
	private String rate1;
	private String rate2;
	private BigDecimal midWayDh;        			// 在途到货
	private BigDecimal midWayRk;        			// 在途入库
	private BigDecimal midWayXh;        			// 在途销货
	private BigDecimal midWayCk;        			// 在途出库

}
