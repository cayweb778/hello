create table stock_price
(
    id           varchar not null
        constraint budget_source_pk
            primary key,
    stock_num      varchar,
    stock_market_unit      varchar,
    regular_price      varchar,
    memberprice      varchar,
    invscost1      varchar,
    invscost2      varchar,
    invscost3      varchar,
    invscost4      varchar,
    invscost5      varchar,
    invscost6      varchar,
    invscost7      varchar,
    invscost8      varchar,
    start_date      varchar,
    stop_date      varchar,
    status      varchar,
    new_price      varchar,
    operator_name      varchar,
    operator_time      varchar,
    cfree1      varchar,
    cfree2      varchar,
    cfree3      varchar,
    cfree4      varchar,
    cfree5      varchar,
    cfree6      varchar,
    cfree7      varchar,
    cfree8      varchar,
    cfree9      varchar,
    cfree10      varchar,
    tenant_id varchar default CURRENT_USER
);

comment on table stock_price is '存货价格表';
comment on column stock_price.stock_num is '存货编码';
comment on column stock_price.stock_market_unit is '默认销售单位';
comment on column stock_price.regular_price is '零售价';
comment on column stock_price.memberprice is '普通客户价';
comment on column stock_price.start_date is '价格生效日期';
comment on column stock_price.stop_date is '价格失效日期';
comment on column stock_price.status is '状态(1停用，0若其他正常）';
comment on column stock_price.new_price is '最新进价';
comment on column stock_price.operator_name is '创建人';
comment on column stock_price.operator_time is '创建日期';
comment on column stock_price.invscost1 is '1级客户价';


alter table stock_price
    owner to postgres;

ALTER TABLE stock_price ENABLE ROW LEVEL SECURITY;

GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "bjhzzkgl-003-2022";

create policy "bjhzzkgl-003-2022-table-stock_price" on stock_price  for all to "bjhzzkgl-003-2022"  using (tenant_id  = 'bjhzzkgl-003-2022');

