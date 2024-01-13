package org.apache.streampark.console.flow.model.flink;

public class FlinkTableBaseInfo {

  private String catalogName;

  private String dbname;
  private String schema;

  private boolean ifNotExists;

  private String registerTableName;

  private String registerTableComment;

  public String getCatalogName() {
    return catalogName;
  }

  public void setCatalogName(String catalogName) {
    this.catalogName = catalogName;
  }

  public String getDbname() {
    return dbname;
  }

  public void setDbname(String dbname) {
    this.dbname = dbname;
  }

  public String getSchema() {
    return schema;
  }

  public void setSchema(String schema) {
    this.schema = schema;
  }

  public boolean getIfNotExists() {
    return ifNotExists;
  }

  public void setIfNotExists(boolean ifNotExists) {
    this.ifNotExists = ifNotExists;
  }

  public String getRegisterTableName() {
    return registerTableName;
  }

  public void setRegisterTableName(String registerTableName) {
    this.registerTableName = registerTableName;
  }

  public String getRegisterTableComment() {
    return registerTableComment;
  }

  public void setRegisterTableComment(String registerTableComment) {
    this.registerTableComment = registerTableComment;
  }
}
