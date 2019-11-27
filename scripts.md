# PostgreSQL 적용 실습
- 출처 : 스프링 기반 REST-API 개발 (https://www.inflearn.com/course/spring_rest-api) / 백기선

## Postgres

### Run Postgres Container

```
// -name : 컨테이너 이름, -p: 포트(InBound, OutBound), -e : 내부 환경설정, -d : 데몬모드 실행, postgres : 이미지명
// -e POSTGRES_DATABASE 를 통해 새로운 DB 생성 가능  
docker run --name rest -p 5432:5432 -e POSTGRES_PASSWORD=pass -d postgres

```

### Docker 컨테이너의 인스턴스 내 실행
```
// exec : 커맨드 실행, -i : interactive mode, -t : set target container, bash : 실행 명령어 (꼭 bash일 필요는 없음)
docker exec -i -t rest bash
```

#### bash 내 실행
```
su - postgres
psql -d postgres -U postgres -W
```

* database: postgres
* username: postgres
* password: pass
* post: 5432


### Getting into the Postgres container

```
docker exec -i -t ndb bash
```

Then you will see the containers bash as a root user.

### Connect to a database

```
psql -d postgres -U postgres
```

### Query Databases

```
\l
```

### Query Tables

```
\dt
```

### Quit

```
\q
```

## application.properties

### Datasource

```
spring.datasource.username=postgres
spring.datasource.password=pass
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.driver-class-name=org.postgresql.Driver
```

### Hibernate

```
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation=true
spring.jpa.properties.hibernate.format_sql=true

logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

### Test Database

```
spring.datasource.username=sa
spring.datasource.password=
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver

spring.datasource.hikari.jdbc-url=jdbc:h2:mem:testdb 

spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect 

```
