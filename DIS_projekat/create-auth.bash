cd spring-cloud
spring init \
--boot-version=2.3.0.RELEASE \
--build=gradle \
--java-version=1.8 \
--packaging=jar \
--name=authorization-server \
--package-name=se.magnus.spring-cloud.authorization-server \
--groupId=se.magnus.spring-cloud.authorization-server \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
authorization-server