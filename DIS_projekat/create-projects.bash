#!/usr/bin/env bash

mkdir microservices
cd microservices

spring init \
--boot-version=2.3.0.RELEASE \
--build=gradle \
--java-version=1.8 \
--packaging=jar \
--name=insurance-company-service \
--package-name=se.magnus.microservices.core.insurance-company \
--groupId=se.magnus.microservices.core.insurance-company \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
insurance-company-service

spring init \
--boot-version=2.3.0.RELEASE \
--build=gradle \
--java-version=1.8 \
--packaging=jar \
--name=insurance-offer-service \
--package-name=se.magnus.microservices.core.insurance-offer \
--groupId=se.magnus.microservices.core.insurance-offer \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
insurance-offer-service

spring init \
--boot-version=2.3.0.RELEASE \
--build=gradle \
--java-version=1.8 \
--packaging=jar \
--name=employee-service \
--package-name=se.magnus.microservices.core.employee \
--groupId=se.magnus.microservices.core.employee \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
employee-service

spring init \
--boot-version=2.3.0.RELEASE \
--build=gradle \
--java-version=1.8 \
--packaging=jar \
--name=transaction-service \
--package-name=se.magnus.microservices.core.transaction \
--groupId=se.magnus.microservices.core.transaction \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
transaction-service

spring init \
--boot-version=2.3.0.RELEASE \
--build=gradle \
--java-version=1.8 \
--packaging=jar \
--name=insurance-company-composite-service \
--package-name=se.magnus.microservices.composite.insurance-company \
--groupId=se.magnus.microservices.composite.insurance-company \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
insurance-company-composite-service

cd ..