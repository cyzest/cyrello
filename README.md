# cyrello

[![Build Status](https://travis-ci.org/cyzest/cyrello.svg?branch=master)](https://travis-ci.org/cyzest/cyrello)
[![Code Coverage](https://codecov.io/gh/cyzest/cyrello/branch/master/graph/badge.svg)](https://codecov.io/gh/cyzest/cyrello)
[![API Docs](https://img.shields.io/badge/api--docs-open-blue.svg)](https://cyrello.cyzest.com/docs/api-docs.html)

### Back-end 개발 환경

1. Java 8 버전을 사용
1. Spring Boot 2.0 사용 (Spring Framework 5.0 기반)
1. Spring Data JPA 사용
1. H2 DB 사용
1. Junit5 & Mockito로 단위테스트 작성

### Front-end 개발 환경

* jQuery & Bootstrap을 활용하여 개발

### 빌드 및 실행 방법

* 빌드툴은 Maven을 활용  
* 빌드 시 Java8과 Maven이 미리 설치되어 있어야 한다.

```console
$ git clone https://github.com/cyzest/cyrello.git
$ cd cyrello
$ mvn clean package
$ java -jar ./target/cyrello-1.0.0.jar
```

* http://localhost:8080 으로 접속하여 확인
* 포트는 기본적으로 8080 을 사용 (application.properties 에서 수정가능)
* H2 DB를 인메모리 방식으로 사용하므로 서버 종료 시 저장된 데이터는 삭제된다.

### 문제 해결 전략

* 할일 추가 및 할일 수정 시 공통 이슈
  + 유효하지 않은 할일 ID를 참조 할 수 없도록 한다.
  + 완료처리 된 할일을 참조 할 수 없도록 한다.

* 할일 수정 시 이슈
  + 완료처리 된 할일은 수정 할 수 없도록 한다.

* 할일 완료 시 이슈
  + 해당 할일을 참조하는 완료되지 않는 할일이 있는 경우 완료할 수 없도록 한다.

* 백엔드 대응
  + 각각의 이슈사항이 발생하는 케이스에 대해서 로직검증 후 적절한 API 응답메시지를 전달
  + 기본적인 API 요청 파라미터 검증은 Bean Validation으로 검증

* 프론트엔드 대응
  + 서버로 요청할 데이터들을 1차적으로 검증할 수 있도록 처리
  + API 호출 시 서버의 응답메시지에 맞게 사용자가 알 수 있도록 처리

* 기능 관점
  + 멀티 유저가 사용 할 수 있게 회원가입과 로그인 기능 제공
  + 할일 수정과 할일 완료는 별도의 액션으로 구분
  + 프론트엔드에서 할일 완료처리 시 버튼을 비활성화 하도록 처리

* 데이터 관점
  + 완료처리는 별도의 플래그정보를 두지 않고 완료 시 완료날짜의 존재여부로 판단
  + 멀티 유저 간 할일 정보가 노출 되지 않도록 로직검증을 진행