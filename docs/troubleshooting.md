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


## 5. 사용자 실수인 요청이 모두 500 Internal Server Error로 응답됨
- 문제: 회원가입·로그인·토큰 검증을 붙인 뒤 `http/auth.http`, `http/scores.http`로 확인하니
  사용자가 잘못 보낸 요청이 전부 500으로 응답됨.
  ```
  POST /auth/signup (이미 있는 아이디)   → 500 {"status":500,"error":"Internal Server Error",...}
  POST /auth/login  (비밀번호 틀림)      → 500
  POST /scores      (가짜 토큰)          → 500
  ```
  500은 "서버가 예상 못 한 문제"라는 뜻이라, 클라이언트(게임)는 **자기가 뭘 잘못했는지 알 수 없고**,
  서버 쪽에서도 진짜 장애와 사용자 실수를 구분할 수 없음.
- 원인:
    1. 서비스가 던진 예외(`IllegalStateException`)를 컨트롤러도 잡지 않아 스프링까지 올라감
       → 스프링은 처리되지 않은 예외를 500으로 응답함.
    2. 중복 가입과 로그인 실패가 같은 `IllegalStateException`이라 **종류를 구분할 방법이 없었음**.
    3. 가짜 토큰은 우리 코드가 아니라 jjwt 라이브러리의 `JwtException`이 발생함.
- 해결:
    1. 상황별 예외 클래스 생성: `DuplicateUserException`(→ 409), `AuthenticationException`(→ 401).
       `throws`를 모든 메서드에 붙이지 않도록 `RuntimeException`을 상속함
       (스프링 `@Transactional`도 `RuntimeException`일 때 롤백하는 것이 기본).
    2. `@RestControllerAdvice` + `@ExceptionHandler`로 예외를 한 곳(`GlobalExceptionHandler`)에서 받아
       상태 코드와 `ErrorResponse(code, message)` JSON으로 변환. 게임 클라이언트는 문구가 아니라 `code`로 분기.
    3. jjwt 예외는 `JwtTokenProvider.getUserId` 안에서 잡아 `AuthenticationException`으로 바꿔 던짐.
       핸들러에 `JwtException`을 직접 등록하는 방법도 있지만, 그러면 핸들러가 jjwt에 의존하게 됨.
       **외부 라이브러리를 아는 곳을 한 파일로 제한**하기 위해 이 방법을 선택.
    4. 검증 실패(`MethodArgumentNotValidException`, `HandlerMethodValidationException`)와
       `Authorization` 헤더 누락(`MissingRequestHeaderException`)도 같은 형식으로 통일.
       헤더 누락은 "인증 정보 없음"이므로 400이 아닌 401로 응답.
    5. 기존 테스트 3개가 `IllegalStateException`을 기대해 실패 → 새 예외 타입으로 수정.
- 결과:

  | 요청 | 전 | 후 |
  |---|---|---|
  | 중복 가입 | 500 | 409 `DUPLICATE_USER` |
  | 비밀번호 틀림 / 없는 아이디 | 500 | 401 `UNAUTHORIZED` (같은 메시지) |
  | 가짜 토큰 / `Bearer` 형식 오류 / 헤더 없음 | 500·400 | 401 `UNAUTHORIZED` |
  | 비밀번호 8자 미만, 음수 점수, `limit=0` | 400 (기본 형식) | 400 `INVALID_REQUEST` + 필드별 이유 |

  예: `{"code":"INVALID_REQUEST","message":"password: 크기가 8에서 72 사이여야 합니다, userId: 공백일 수 없습니다"}`
