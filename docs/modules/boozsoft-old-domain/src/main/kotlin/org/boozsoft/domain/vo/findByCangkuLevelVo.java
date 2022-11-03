package org.boozsoft.domain.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class findByCangkuLevelVo {
	private String id;
	private String level;
	private Integer levelOrder;
}
