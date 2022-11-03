package org.boozsoft.domain.vo;


import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value="科目模板表",description="acc_template: table")
public class AccTemplateVo {
	private String id;
	private String tName;
	private String tJici;
	private String tType;
	private Integer tNum;
	private Long uniqueAccStandard;
	private String styleName;
	private String styleId;
}
