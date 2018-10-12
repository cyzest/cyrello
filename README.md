# cyrello

[![Build Status](https://travis-ci.org/cyzest/cyrello.svg?branch=master)](https://travis-ci.org/cyzest/cyrello)
[![Code Coverage](https://codecov.io/gh/cyzest/cyrello/branch/master/graph/badge.svg)](https://codecov.io/gh/cyzest/cyrello)
[![API Docs](https://img.shields.io/badge/api--docs-open-blue.svg)](https://cyrello.cyzest.com/docs/api-docs.html)

### Back-end 개발 환경

1. Java 8 버전을 사용
1. Spring Boot 2.0 사용 (Spring Framework 5.0 기반)
1. Spring Data JPA 사용 (Hibernate 5.2 구현체)
1. H2 DB 사용 (Dev서버는 MariaDB 사용)
1. Junit5 & Mockito로 단위테스트 작성

### Dev 서버 대응 추가 라이브러리

1. Sentry - Error 모니터링 용도
1. Spring Cloud Vault - 시크릿 설정 정보 분리 용도

### Front-end 개발 환경

* jQuery & Bootstrap을 활용하여 개발

### 빌드 및 실행 (Local)

* 빌드툴은 Maven을 활용  
* 빌드 시 Java8과 Maven이 미리 설치되어 있어야 한다.
```console
$ git clone https://github.com/cyzest/cyrello.git
$ cd cyrello
$ mvn clean package
$ java -jar ./target/cyrello-1.2.0.jar
```
* http://localhost:8080 으로 접속하여 확인
* 포트는 기본적으로 8080 을 사용 (application.properties 에서 수정가능)
* H2 DB를 인메모리 방식으로 사용하므로 서버 종료 시 저장된 데이터는 삭제된다.

### 빌드 및 실행 (Dev)

```console
$ git clone https://github.com/cyzest/cyrello.git
$ cd cyrello
$ mvn clean test
$ mvn clean package -Pdev -Dmaven.test.skip=true
$ java -DVAULT_TOKEN={VAULT_TOKEN} -Dsentry.dsn={SENTRY_DSN} -jar ./target/find-books-1.0.0.jar
```
* Dev 서버는 Sentry와 Vault를 연동
* Java System Property로 Vault Token과 Sentry DSN을 추가해야 합니다.

### Vault로 관리하는 설정정보

```txt
spring.datasource.username              // DB User
spring.datasource.password              // DB User Password
```
* Vault 정보를 자신의 환경에 맞게 변경해야 합니다. (bootstrap.properties 에서 수정가능)