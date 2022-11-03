package org.boozsoft.domain.vo.stock;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.boozsoft.domain.entity.stock.StockChange;

import java.io.Serializable;


@Data
public class StockAdVo  {
	private String bcheck;
	private String ddate;
	private String ccode;
	private String adStyle;
	private String bomName;
	private String bomVer;
	private String bomVerName;
	private String unitName;
	private String cnumber;
	private String bomLevel;
	private String feiyongJe;
	private String deptName;
	private String cmakerName;
	private String bcheckName;
}
