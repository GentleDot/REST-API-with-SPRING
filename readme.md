# REST-API 실습

## 이력
| 날짜 | 내용 |
| :---: | --- |
| 2019.10.26 | 프로젝트 최초 생성 |
| 2019.12.06 | 완강, 1차완성 |

## 목차
- [목표](#목표)
- [출처](#출처)
- 학습 내용
    - [REST API는 무엇인가?](#REST-API)

## 목표
1. 스프링부트 기본기 습득
2. 테스트 주도 개발(TDD) 익히기
3. Self-Describtive Message와 HATEOAS를 만족하는 REST API 이해
4. OAuth2 활용 이해 (SPRING SECURITY) 

## 출처 
* 강좌
    1. [스프링 기반 REST-API 개발 / 백기선](https://www.inflearn.com/course/spring_rest-api)

* 문서
    1. [Spring HATEOAS Reference](https://docs.spring.io/spring-hateoas/docs/current/reference/html)
    1. [MAVEN WINDOWS 설치 - 제타위키](https://zetawiki.com/wiki/%EC%9C%88%EB%8F%84%EC%9A%B0_%EB%A9%94%EC%9D%B4%EB%B8%90_%EC%84%A4%EC%B9%98)
    1. [What is REST?](https://midnightcow.tistory.com/entry/REST-What-is-REST-1)
    1. [REST API 제대로 알고 사용하기](https://meetup.toast.com/posts/92)
    1. [REST API란?](https://jcon.tistory.com/88)
    

## 학습내용

### REST API
REST(<b>Re</b>presentational <b>S</b>tate <b>T</b>ransfer)  

- 인터넷 상에서 시스템 간의 상호 운용성(interoperability)을 제공하는 방법중 하나

- 시스템 제각각의 독립적인 진화를 보장하기
위한 방법

- REST API: REST 아키텍처 스타일을 따르는 API

- REST Architecture  
    출처 : [What is REST?](https://midnightcow.tistory.com/entry/REST-What-is-REST-1)
    >  REST는 철저히 Resource 중심적 설계를 중시하고, Create / Read / Update / Delete (CURD)에 해당하는 HTTP의 4 가지 Method POST / GET / PUT / DELECT 를 그 용도에 맞게 정확하게 사용함으로써, 기존의 Service 중심의 Web Application(SOA - Service, Verbs 중심)을 Resource 중심의 Web Application(ROA - Resource, Nouns)으로의 변화를 일으켰다. REST는 Protocol이 아닌 설계론이라는 것을 이해하자.

    - Client-Server
    - Stateless
    - Cache
    - __Uniform Interface__
        - identification of resources
        - manipulation of resources through represenations
        - _Self-descriptive messages_
        - _hypermedia as the engine of appliaction state (HATEOAS)_
    - Layered System
    - Code-On-Demand (optional)

- Self-descriptive messages 와 HATEOAS 를 만족시키려면?
1. Self-descriptive message
    - 요건
        - 메시지 스스로 메시지에 대한 설명이 가능해야 한다.
            - 메시지의 State, Information 등을 HTTP method, HTTP status code, HTTP Header 등을 활용하여 전달
        - 서버가 변동이 있더라도 클라이언트는 메시지를 통해 해석이 가능해야 한다(코드만 보고도 온전한 해석이 가능해야)
        - 확장 가능한 커뮤니케이션
    
    - 방법
        1. 리소스를 리턴할 때 IANA(The Internet Assigned Numbers Authority)에 미디어 타입을 등록하고 이 정의된 미디어 타입을 Content-Type으로 사용한다.
            - 단점 : 매번 media type을 정의해야 한다.
        1. profile 링크 헤더에 의미를 정의한 명세를 추가한다. (대안으로 HAL의 링크 데이터에 profile 링크를 추가)
    
1. HATEOAS
    - 요건
        - 하이퍼미디어(links)를 통해 application 상태 변화가 가능해야 한다.(다음 상태로 전이가 가능) 
        - 링크 정보를 동적으로 바꿀 수 있다.
    
    - 방법
        > 출처 : [REST API란?](https://jcon.tistory.com/88)
        1. 데이터에 링크를 제공한다.
            - 단점 : 링크를 표현하는 방법을 profile 또는 media type에 직접 정의해야 한다.
        
        1. 데이터에 JSON으로 hyperlink를 표현하는 방법을 정의한 명세들을 활용
            - JSON API
            - HAL
            - UBER
            - Siren
            - Collection+json 등
            - 단점 : 기존 API에서 추가 필요 (침투적) 
        
        1. HTTP Header로 link, location 등을 표현
            - 단점 : 정의된 relation만 활용한다면 표현에 한계가 있음.

1. (필자가 이해한)결론
    - 리턴 시 HTTP Header에 Content-type 제공
    - HAL 링크 데이터를 통해 profile, self 등을 추가
    - 명세는 Spring REST Docs를 통해 문서화하여 링크 제공
  