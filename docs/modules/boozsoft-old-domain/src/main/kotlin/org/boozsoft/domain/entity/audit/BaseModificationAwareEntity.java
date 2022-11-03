package org.boozsoft.domain.entity.audit;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Getter
@Setter
public abstract class BaseModificationAwareEntity {
  /**
   * 创建时间。
   */
  @CreatedDate
  private LocalDateTime createdAt;
  /**
   * 修改时间。
   */
  @LastModifiedDate
  private LocalDateTime modifiedAt;
  /**
   * 创建人。
   */
  @CreatedBy
  private String createdBy;
  /**
   * 修改人。
   */
  @LastModifiedBy
  private String modifiedBy;

}
