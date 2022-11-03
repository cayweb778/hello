package org.boozsoft.domain.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class AccStandardVo {
	private Long id;
	private Integer uniqueAccStandard;
	private String accStandardName;
	private String flagYusuan;
	private Integer num;
	private String accStyleUnique;
	private String styleName;
	private String styleId;
	private Integer codeFirst;
}
