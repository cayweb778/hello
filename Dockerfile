FROM postgres:9.4
RUN mkdir /sql
COPY src/main/resources/sql_scripts/* /sql/
RUN psql -f /sql/create_user.sql
RUN psql -U user -W 123 -f create_db.sql
RUN psql -U user -W 123 -d school_ats -f create_tables.sql