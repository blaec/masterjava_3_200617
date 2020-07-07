rem https://mohitgoyal.co/2019/04/01/prepare-failback-strategy-for-database-changes-with-liquibase/
set LB_HOME="C:\Program Files\Java\liquibase"
call %LB_HOME%\liquibase.bat --driver=org.postgresql.Driver ^
--classpath=%LB_HOME%\lib ^
--changeLogFile=databaseChangeLog.sql ^
--url="jdbc:postgresql://localhost:5432/masterjava" ^
--username=user ^
--password=password ^
rollback rollbackCount 1