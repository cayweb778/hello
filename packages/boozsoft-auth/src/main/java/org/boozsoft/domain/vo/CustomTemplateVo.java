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
@ApiModel(value="自定义-科目模板",description="acc_template: table")
public class CustomTemplateVo {
	private String id;
	private String tName;
	private String tType;
	private String styleName;
	private String uniqueAccStandard;
	private String accStandardName;
	private String tJici;
	private String accStyleUnique;
	private Integer codeFirst;
	private String parentName;
	private String orgName;
	private String tOrganization;
	private String tPid;
	private String tFlg;
}
