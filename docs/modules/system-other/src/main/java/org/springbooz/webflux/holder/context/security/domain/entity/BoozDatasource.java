package org.springbooz.webflux.holder.context.security.domain.entity;

import lombok.Data;

@Data
public class BoozDatasource {
  private String protocol;
  private String url;
  private String port;
  private String databaseName;
  private String schemaName;

  public String getProtocol() {
    return protocol;
  }

  public String getUrl() {
    return url;
  }

  public String getPort() {
    return port;
  }

  public String getDatabaseName() {
    return databaseName;
  }

  public String getSchemaName() {
    return schemaName;
  }
}
