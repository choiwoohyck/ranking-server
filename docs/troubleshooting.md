# Troubleshooting

## 1. GET /ping 이 404 Not Found 반환
- 문제: `GET /ping` 요청 시 `pong` 대신 404 Not Found 응답. 서버는 정상 기동(8080) 상태였으므로
  서버 문제가 아니라 `/ping` 경로가 등록되지 않은 것으로 판단함.
- 원인:
    1. 컨트롤러 클래스에 애노테이션이 없어 컴포넌트 스캔 대상이 아님 → 스프링 빈으로 등록되지 않아
       `@GetMapping("ping")`도 등록되지 않음.
    2. `@Controller`만 붙이면 반환값 `"pong"`을 뷰 이름으로 해석해 `pong.html` 템플릿을 찾음
       → 템플릿이 없어 다시 404.
- 해결:
    1. 1차: 클래스에 `@Controller` 추가 → 여전히 404.
    2. 2차: `@RestController`(= `@Controller` + `@ResponseBody`)로 변경해 반환값을 HTTP 응답 본문에
       그대로 담도록 수정.
- 결과: `GET /ping` → `200 OK`, 본문 `pong` 확인. `@Controller`(뷰 반환)와 `@RestController`(데이터 반환)의
  차이를 이해함.


## 2. 잘못된 요청 값이 검증 없이 저장됨
- 문제: `POST /scores`에 `{"userId": "", "score": -999}`를 보내면 `200 OK`로 저장되고, 랭킹에
  이름 없는 유저가 -999점으로 나타남. 잘못된 데이터가 랭킹에 섞임.
- 원인: 스프링은 요청 JSON을 DTO에 담기만 하고 값은 검사하지 않음. DTO에 값 규칙(제약 조건)이
  없었고, 컨트롤러도 검사를 요청하지 않았음.
- 해결: Bean Validation 적용.
    1. `userId`에 `@NotBlank`: `@NotEmpty`는 `"   "`(공백만)을 통과시키므로, 공백까지 막는 `@NotBlank` 선택.
    2. `score`에 `@Min(0)`: 음수는 막되, 게임을 막 시작해 누적 골드가 0인 유저는 허용.
    3. `@RequestBody` 앞에 `@Valid`: 이게 없으면 DTO에 규칙이 있어도 검사하지 않음.
    4. `limit`에 `@Min(1)`, `@Max(100)`: `limit=10000000`처럼 큰 값으로 한 번에 전체 데이터를 요청하는 것을 막음.
- 결과: 경계값 확인.
    - 400: `userId` `""`·`"   "`, `score` `-999`, `score` 필드 없음, `limit` `-5`·`0`·`101`
    - 200: `score` `0`, `limit` `1`·`100`


## 3. Docker MySQL 컨테이너가 실행되지 않음
- 문제: `docker run ... -p 3306:3306 mysql:8.4`로 컨테이너를 만들었지만 `docker ps`에 보이지 않음.
  `docker ps -a`로 보니 상태가 `Up`이 아니라 `Created`에서 멈춰 있었음.
- 원인: `docker start ranking-mysql`로 다시 실행하자 에러 메시지가 나옴.
  ```
  ports are not available: exposing port TCP 0.0.0.0:3306 ... bind: Only one usage of each socket address ... is normally permitted.
  ```
  3306 포트를 누가 쓰는지 확인해 보니(`Get-NetTCPConnection -LocalPort 3306`) PC에 원래 설치된
  MySQL(`mysqld`)이 이미 사용 중이었음. 한 포트는 한 프로그램만 쓸 수 있으므로 컨테이너가 포트를 열지 못함.
- 해결: 기존 MySQL은 다른 용도로 쓰고 있을 수 있어 끄지 않고, 컨테이너를 PC의 3307 포트로 연결함.
    1. `docker rm ranking-mysql`로 기존 컨테이너 삭제
    2. `-p 3307:3306`으로 다시 실행 (PC의 3307 → 컨테이너 안의 3306)
    3. `application.properties`의 접속 주소를 `jdbc:mysql://localhost:3307/ranking`으로 변경
- 결과: 컨테이너가 `Up` 상태가 되고 애플리케이션이 MySQL에 정상 연결됨.
  `-p 앞:뒤`에서 **앞은 내 PC 포트, 뒤는 컨테이너 안 포트**라는 것을 이해함.


## 4. `@Id`가 의도하지 않은 필드에 적용됨
- 문제: `Score`를 JPA 엔티티로 바꾸면서 `@Id`를 `userId` 아래 줄에 작성함. 코드 리뷰에서
  `userId`가 아니라 `score`가 기본키(PK)로 지정된 것을 발견함.
  ```java
  private String userId;
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long score;   // ← @Id가 여기에 적용됨
  ```
  이대로라면 **점수가 같은 유저 두 명을 저장할 수 없고**, 같은 유저의 기록이 여러 개 생길 수 있어
  "유저당 최고 기록 1개"라는 설계(design.md 결정 1)와 반대로 동작함.
- 원인:
    1. 필드에 붙이는 어노테이션은 **바로 아래에 선언된 필드**에 적용됨.
    2. `@GeneratedValue(IDENTITY)`는 DB가 1, 2, 3… 번호를 자동으로 매기는 기능인데, `userId`는
       클라이언트가 보내 주는 값이라 자동 생성이 필요 없음. 강의 예제(`Member.id`)를 그대로 따라 쓰면서 붙음.
- 해결: `@Id`를 `userId` 위로 옮기고 `@GeneratedValue` 제거.
- 결과: JPA가 만든 테이블에서 `PRIMARY KEY (user_id)` 확인. 같은 유저가 더 높은 점수를 보내면
  새 행이 생기지 않고 기존 행이 갱신됨(100 → 300). 유저당 기록 1개가 **DB 레벨에서도 보장**됨.
