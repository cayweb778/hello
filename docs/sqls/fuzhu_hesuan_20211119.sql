alter table fuzhu_hesuan
    add ctype varchar;

comment on column fuzhu_hesuan.ctype is '类型（1.系统档案；2.自定义档案）';

alter table fuzhu_hesuan
    add cfield varchar;

comment on column fuzhu_hesuan.cfield is '凭证字段';

alter table fuzhu_hesuan
    add bend varchar;

comment on column fuzhu_hesuan.bend is '是否末级（1.末级；0.标准）';



alter table _app_group_fuzhu_hesuan
    add ctype varchar;

comment on column _app_group_fuzhu_hesuan.ctype is '类型（1.系统档案；2.自定义档案）';

alter table _app_group_fuzhu_hesuan
    add cfield varchar;

comment on column _app_group_fuzhu_hesuan.cfield is '凭证字段';

alter table _app_group_fuzhu_hesuan
    add bend varchar;

comment on column _app_group_fuzhu_hesuan.bend is '是否末级（1.末级；0.标准）';



alter table _app_origin_fuzhu_hesuan
    add ctype varchar;

comment on column _app_origin_fuzhu_hesuan.ctype is '类型（1.系统档案；2.自定义档案）';

alter table _app_origin_fuzhu_hesuan
    add cfield varchar;

comment on column _app_origin_fuzhu_hesuan.cfield is '凭证字段';

alter table _app_origin_fuzhu_hesuan
    add bend varchar;

comment on column _app_origin_fuzhu_hesuan.bend is '是否末级（1.末级；0.标准）';