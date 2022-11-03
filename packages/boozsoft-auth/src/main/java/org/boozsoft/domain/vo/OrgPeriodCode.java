package org.boozsoft.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.boozsoft.domain.entity.codekemu.CodeKemu;

import java.util.List;

/**
 * 组织新建下年期间生成下年科目用到
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class OrgPeriodCode {

    private String accId;
    private List<CodeKemu> codeKemuList;
}
