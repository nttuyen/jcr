@set MAVEN_OPTS=-Duser.language=en -Duser.region=us -Dmaven.test.skip=true -Dexo.devtest.skip=true -DforkMode=never -Dorg.exoplatform.jcr.monitor.jdbcMonitor %MAVEN_OPTS% 

@start mvn clean install -Prun-its
