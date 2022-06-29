mkdir spring-cloud
cd spring-cloud
spring init \
--boot-version=2.3.0.RELEASE \
--build=gradle \
--java-version=1.8 \
--packaging=jar \
--name=eureka-server \
--package-name=se.magnus.spring-cloud.eureka-server \
--groupId=se.magnus.spring-cloud.eureka-server \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
eureka-server