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
