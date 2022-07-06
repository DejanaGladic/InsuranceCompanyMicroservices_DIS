cd spring-cloud
spring init \
--boot-version=2.3.0.RELEASE \
--build=gradle \
--java-version=1.8 \
--packaging=jar \
--name=config-server \
--package-name=se.magnus.spring-cloud.config-server \
--groupId=se.magnus.spring-cloud.config-server \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
config-server