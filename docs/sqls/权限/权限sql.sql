# 数据库权限
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "用户名";

# 行级权限 
create policy "用户名-表名" on 表名  for all to "用户名"  using (tenant_id  = "用户名")

# 租户间数据出现交叉,需执行
ALTER TABLE 表名 ENABLE ROW LEVEL SECURITY;