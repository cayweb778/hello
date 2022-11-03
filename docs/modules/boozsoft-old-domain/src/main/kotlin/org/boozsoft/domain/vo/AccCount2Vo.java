package org.boozsoft.domain.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.boozsoft.domain.entity.accstyle.AccStyle;

import java.util.List;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class AccCount2Vo {
	private String uniqueAccStandard;
	private String templateId;
	private String ccodeLevel;
	// 是否独立账套 1
	private String independent;
	private String accStyleUnique;
	// 是否预算会计 1是
	private String ibudgetAccounting;
	// 账套所属组织编码
	private String accGroup;
	private List<AccStyle> stylist;
}
