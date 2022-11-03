-- auto-generated definition
create table budget_source
(
    id           varchar not null
        constraint budget_source_pk
        primary key,
    bs_code varchar,
    bs_name      varchar,
    create_date     varchar,
    create_code     varchar,
    flag     varchar,
    stop_date   varchar,
    tenant_id varchar default CURRENT_USER
);

comment on table budget_source is '支出功能分类';
comment on column budget_source.id is '主键';
comment on column budget_source.bs_code is '编码';
comment on column budget_source.bs_name is '名称';
comment on column budget_source.create_date is '创建日期';
comment on column budget_source.create_code is '创建人';
comment on column budget_source.stop_date is '停用日期';
comment on column budget_source.flag is '是否停用(1.启用;0停用)';

alter table budget_source
    owner to postgres;


ALTER TABLE budget_source ENABLE ROW LEVEL SECURITY;

GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "zsyxgfyx-001-2019";
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "bjtrcxsm-002-2021";
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "bjtrcxsm-004-2021";
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "swcs10y1-001-2021";
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "wjxhjkmy-002-2021";
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "wjxhjkmy-003-2021";
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "20211019-001-2021";
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "swcs-001-2021";
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "bjmfwhfz-001-2021";
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "swcs-jt1-001-2021";
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "swcs-jt3-001-2021";
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "csjc-002-2021";
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "bjjdkjyx-001-2021";
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "bjxgkjyx-002-2021";
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "cszfzd2-001-2021";
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "gse-001-2021";
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "ndyw-001-2022";
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "dlztcesh-001-2021";
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "eleyn-001-2021";
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "cs0922-001-2021";
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "cs0917-001-2021";
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "eley-001-2021";
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "dlzt-001-2021";
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "swzycs-001-2021";
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "20210928-001-2021";
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "gss-001-2021";
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "syzd-001-2022";
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "cs-001-2021";
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "xgcs-001-2015";
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "cjdlgs-001-2021";
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "rk-001-2021";
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "dlztcs-001-2021";
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "0916cs-001-2021";
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "mkzbjt-001-2021";
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "wjxhjkmy-001-2021";
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "2021n10y-001-2021";
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "wdyn-001-2022";
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "csxjfl-001-2021";
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "csxj2-001-2021";
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "xahls-001-2021";
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "bjxgkjyx-001-2015";
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "gsy-001-2021";
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "0917cs-001-2021";
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "205-001-2021";
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "20210906-001-2021";
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "wyrjjjhs-001-2022";
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "bjtrcxsm-001-2021";
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "bjtrcxsm-003-2021";
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "20211011-001-2021";
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "20211013-001-2021";
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "20211014-001-2021";
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "bjftgjwh-001-2021";
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "bjhzzkgl-001-2021";
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "bjqydzkj-001-2021";
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "swcs-jt2-001-2021";
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "zfhjzdcs-001-2020";
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "csjc-001-2021";
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "zfhjzdcs-002-2021";

create policy "tenent-zsyxgfyx-001-2019-table-budget_source" on budget_source  for all to "zsyxgfyx-001-2019"  using (tenant_id  = 'zsyxgfyx-001-2019');
create policy "tenent-bjtrcxsm-002-2021-table-budget_source" on budget_source  for all to "bjtrcxsm-002-2021"  using (tenant_id  = 'bjtrcxsm-002-2021');
create policy "tenent-bjtrcxsm-004-2021-table-budget_source" on budget_source  for all to "bjtrcxsm-004-2021"  using (tenant_id  = 'bjtrcxsm-004-2021');
create policy "tenent-swcs10y1-001-2021-table-budget_source" on budget_source  for all to "swcs10y1-001-2021"  using (tenant_id  = 'swcs10y1-001-2021');
create policy "tenent-wjxhjkmy-002-2021-table-budget_source" on budget_source  for all to "wjxhjkmy-002-2021"  using (tenant_id  = 'wjxhjkmy-002-2021');
create policy "tenent-wjxhjkmy-003-2021-table-budget_source" on budget_source  for all to "wjxhjkmy-003-2021"  using (tenant_id  = 'wjxhjkmy-003-2021');
create policy "tenent-20211019-001-2021-table-budget_source" on budget_source  for all to "20211019-001-2021"  using (tenant_id  = '20211019-001-2021');
create policy "tenent-swcs-001-2021-table-budget_source" on budget_source  for all to "swcs-001-2021"  using (tenant_id  = 'swcs-001-2021');
create policy "tenent-bjmfwhfz-001-2021-table-budget_source" on budget_source  for all to "bjmfwhfz-001-2021"  using (tenant_id  = 'bjmfwhfz-001-2021');
create policy "tenent-swcs-jt1-001-2021-table-budget_source" on budget_source  for all to "swcs-jt1-001-2021"  using (tenant_id  = 'swcs-jt1-001-2021');
create policy "tenent-swcs-jt3-001-2021-table-budget_source" on budget_source  for all to "swcs-jt3-001-2021"  using (tenant_id  = 'swcs-jt3-001-2021');
create policy "tenent-csjc-002-2021-table-budget_source" on budget_source  for all to "csjc-002-2021"  using (tenant_id  = 'csjc-002-2021');
create policy "tenent-bjjdkjyx-001-2021-table-budget_source" on budget_source  for all to "bjjdkjyx-001-2021"  using (tenant_id  = 'bjjdkjyx-001-2021');
create policy "tenent-bjxgkjyx-002-2021-table-budget_source" on budget_source  for all to "bjxgkjyx-002-2021"  using (tenant_id  = 'bjxgkjyx-002-2021');
create policy "tenent-cszfzd2-001-2021-table-budget_source" on budget_source  for all to "cszfzd2-001-2021"  using (tenant_id  = 'cszfzd2-001-2021');
create policy "tenent-gse-001-2021-table-budget_source" on budget_source  for all to "gse-001-2021"  using (tenant_id  = 'gse-001-2021');
create policy "tenent-ndyw-001-2022-table-budget_source" on budget_source  for all to "ndyw-001-2022"  using (tenant_id  = 'ndyw-001-2022');
create policy "tenent-dlztcesh-001-2021-table-budget_source" on budget_source  for all to "dlztcesh-001-2021"  using (tenant_id  = 'dlztcesh-001-2021');
create policy "tenent-eleyn-001-2021-table-budget_source" on budget_source  for all to "eleyn-001-2021"  using (tenant_id  = 'eleyn-001-2021');
create policy "tenent-cs0922-001-2021-table-budget_source" on budget_source  for all to "cs0922-001-2021"  using (tenant_id  = 'cs0922-001-2021');
create policy "tenent-cs0917-001-2021-table-budget_source" on budget_source  for all to "cs0917-001-2021"  using (tenant_id  = 'cs0917-001-2021');
create policy "tenent-eley-001-2021-table-budget_source" on budget_source  for all to "eley-001-2021"  using (tenant_id  = 'eley-001-2021');
create policy "tenent-dlzt-001-2021-table-budget_source" on budget_source  for all to "dlzt-001-2021"  using (tenant_id  = 'dlzt-001-2021');
create policy "tenent-swzycs-001-2021-table-budget_source" on budget_source  for all to "swzycs-001-2021"  using (tenant_id  = 'swzycs-001-2021');
create policy "tenent-20210928-001-2021-table-budget_source" on budget_source  for all to "20210928-001-2021"  using (tenant_id  = '20210928-001-2021');
create policy "tenent-gss-001-2021-table-budget_source" on budget_source  for all to "gss-001-2021"  using (tenant_id  = 'gss-001-2021');
create policy "tenent-syzd-001-2022-table-budget_source" on budget_source  for all to "syzd-001-2022"  using (tenant_id  = 'syzd-001-2022');
create policy "tenent-cs-001-2021-table-budget_source" on budget_source  for all to "cs-001-2021"  using (tenant_id  = 'cs-001-2021');
create policy "tenent-xgcs-001-2015-table-budget_source" on budget_source  for all to "xgcs-001-2015"  using (tenant_id  = 'xgcs-001-2015');
create policy "tenent-cjdlgs-001-2021-table-budget_source" on budget_source  for all to "cjdlgs-001-2021"  using (tenant_id  = 'cjdlgs-001-2021');
create policy "tenent-rk-001-2021-table-budget_source" on budget_source  for all to "rk-001-2021"  using (tenant_id  = 'rk-001-2021');
create policy "tenent-dlztcs-001-2021-table-budget_source" on budget_source  for all to "dlztcs-001-2021"  using (tenant_id  = 'dlztcs-001-2021');
create policy "tenent-0916cs-001-2021-table-budget_source" on budget_source  for all to "0916cs-001-2021"  using (tenant_id  = '0916cs-001-2021');
create policy "tenent-mkzbjt-001-2021-table-budget_source" on budget_source  for all to "mkzbjt-001-2021"  using (tenant_id  = 'mkzbjt-001-2021');
create policy "tenent-wjxhjkmy-001-2021-table-budget_source" on budget_source  for all to "wjxhjkmy-001-2021"  using (tenant_id  = 'wjxhjkmy-001-2021');
create policy "tenent-2021n10y-001-2021-table-budget_source" on budget_source  for all to "2021n10y-001-2021"  using (tenant_id  = '2021n10y-001-2021');
create policy "tenent-wdyn-001-2022-table-budget_source" on budget_source  for all to "wdyn-001-2022"  using (tenant_id  = 'wdyn-001-2022');
create policy "tenent-csxjfl-001-2021-table-budget_source" on budget_source  for all to "csxjfl-001-2021"  using (tenant_id  = 'csxjfl-001-2021');
create policy "tenent-csxj2-001-2021-table-budget_source" on budget_source  for all to "csxj2-001-2021"  using (tenant_id  = 'csxj2-001-2021');
create policy "tenent-xahls-001-2021-table-budget_source" on budget_source  for all to "xahls-001-2021"  using (tenant_id  = 'xahls-001-2021');
create policy "tenent-bjxgkjyx-001-2015-table-budget_source" on budget_source  for all to "bjxgkjyx-001-2015"  using (tenant_id  = 'bjxgkjyx-001-2015');
create policy "tenent-gsy-001-2021-table-budget_source" on budget_source  for all to "gsy-001-2021"  using (tenant_id  = 'gsy-001-2021');
create policy "tenent-0917cs-001-2021-table-budget_source" on budget_source  for all to "0917cs-001-2021"  using (tenant_id  = '0917cs-001-2021');
create policy "tenent-205-001-2021-table-budget_source" on budget_source  for all to "205-001-2021"  using (tenant_id  = '205-001-2021');
create policy "tenent-20210906-001-2021-table-budget_source" on budget_source  for all to "20210906-001-2021"  using (tenant_id  = '20210906-001-2021');
create policy "tenent-wyrjjjhs-001-2022-table-budget_source" on budget_source  for all to "wyrjjjhs-001-2022"  using (tenant_id  = 'wyrjjjhs-001-2022');
create policy "tenent-bjtrcxsm-001-2021-table-budget_source" on budget_source  for all to "bjtrcxsm-001-2021"  using (tenant_id  = 'bjtrcxsm-001-2021');
create policy "tenent-bjtrcxsm-003-2021-table-budget_source" on budget_source  for all to "bjtrcxsm-003-2021"  using (tenant_id  = 'bjtrcxsm-003-2021');
create policy "tenent-20211011-001-2021-table-budget_source" on budget_source  for all to "20211011-001-2021"  using (tenant_id  = '20211011-001-2021');
create policy "tenent-20211013-001-2021-table-budget_source" on budget_source  for all to "20211013-001-2021"  using (tenant_id  = '20211013-001-2021');
create policy "tenent-20211014-001-2021-table-budget_source" on budget_source  for all to "20211014-001-2021"  using (tenant_id  = '20211014-001-2021');
create policy "tenent-bjftgjwh-001-2021-table-budget_source" on budget_source  for all to "bjftgjwh-001-2021"  using (tenant_id  = 'bjftgjwh-001-2021');
create policy "tenent-bjhzzkgl-001-2021-table-budget_source" on budget_source  for all to "bjhzzkgl-001-2021"  using (tenant_id  = 'bjhzzkgl-001-2021');
create policy "tenent-bjqydzkj-001-2021-table-budget_source" on budget_source  for all to "bjqydzkj-001-2021"  using (tenant_id  = 'bjqydzkj-001-2021');
create policy "tenent-swcs-jt2-001-2021-table-budget_source" on budget_source  for all to "swcs-jt2-001-2021"  using (tenant_id  = 'swcs-jt2-001-2021');
create policy "tenent-zfhjzdcs-001-2020-table-budget_source" on budget_source  for all to "zfhjzdcs-001-2020"  using (tenant_id  = 'zfhjzdcs-001-2020');
create policy "tenent-csjc-001-2021-table-budget_source" on budget_source  for all to "csjc-001-2021"  using (tenant_id  = 'csjc-001-2021');
create policy "tenent-zfhjzdcs-002-2021-table-budget_source" on budget_source  for all to "zfhjzdcs-002-2021"  using (tenant_id  = 'zfhjzdcs-002-2021');

