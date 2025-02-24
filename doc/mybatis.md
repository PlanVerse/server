# Mybatis Null check

| 방법                           | 코드 예제                                              | 설명                                        |
|------------------------------|----------------------------------------------------|-------------------------------------------|
| **기본적인 `null` 체크**           | `if (value == null) { ... }`                       | 일반적인 `null` 체크 방식                         |
| **`let`을 활용한 처리**            | `value?.let { println(it) } ?: println("null")`    | `null`이 아닐 때만 실행                          |
| **✅ 기본값 처리 (`?:`)**          | `val user = value ?: defaultValue`                 | `null`일 경우 기본값 사용                         |
| **예외 발생 (`requireNotNull`)** | `val user = requireNotNull(value) { "Not found" }` | `null`이면 예외 발생 (IllegalArgumentException) |
| **예외 발생 (`checkNotNull`)**   | `val user = checkNotNull(value) { "Not found" }`   | `null`이면 예외 발생 (IllegalStateException)    |
| **Java `Optional`과 연동**      | `if (value.isPresent) { println(value.get()) }`    | Java `Optional` 사용 시                      |

> **✅ `nullable` 타입을 활용하는 방식 (`?.`, `?:`) 주로 확인**
