package org.boozsoft.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;


/**
 * 会计科目删除时使用
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class AAtemp {
    private String title;
    private String value;
    private String key;
    private String bend;
    private String parentId;
    private List<AAtemp> children;
}
