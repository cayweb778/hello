package org.boozsoft.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;


/**
 * 组织会计科目使用
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class AOrgKemutemp {
    private String accName;
    private String accvoucherCount;
    private String superiorCcode;
}
