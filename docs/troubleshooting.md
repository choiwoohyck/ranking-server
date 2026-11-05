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
