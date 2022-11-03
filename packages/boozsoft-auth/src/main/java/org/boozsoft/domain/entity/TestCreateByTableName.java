package org.boozsoft.domain.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("table_name")
@Data
public class TestCreateByTableName {
  @Id
  @CreatedBy
  private String id;
  private String name;
  private Integer seril;
}
