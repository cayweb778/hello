package org.boozsoft.domain.vo.stock;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;


/**
 * @description: 存货执行情况表VO
 * @author: miao
 * @date: 2022/10/17 11:13
 * @param:
 * @return: null
 **/
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class StockDetailedVo implements Serializable {
	private String ddate;
	private String ccode;
	private String bcheck;
	private String rkFlag;
	private String fkFlag;
	private String fpFlag;
	private String cvencode;
	private String cvencodeJs;
	private String cvencodeName;
	private String cvencodeJsName;
	private String cwhcode;
	private String batchId;
	private String cinvode;
	private String cinvodeName;
	private String ggxh;
	private String unitName;
	private BigDecimal baseQuantity;
	private BigDecimal isum;
	private String isGive;
	private BigDecimal isumTuiHuo;
	private BigDecimal isumRuku;
	private BigDecimal noRuku;
	private BigDecimal hxIsum;
	private BigDecimal noHxIsum;
	private BigDecimal isumFapiao;
	private BigDecimal isumFapiaoMoney;
	private BigDecimal noFapiao;
	private String cmemo;
	private String stockBarcode;
	private String deptName;
	private String psnName;
	private String shName;
	private String zdName;
	private String fhName;
	private String billStyle;
}
