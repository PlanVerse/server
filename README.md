![Springboot](https://shields.io/badge/v3.4.0-6DB33F?logo=springboot&label=Spring%20Boot)  
![Gradle](https://img.shields.io/badge/v8.11.1-02303A?logo=gradle&label=Gradle)  
![Postgresql](https://img.shields.io/badge/v13-4169E1?logo=postgresql&label=PostgreSQL)

# PMS - PlanVerse

---

- JWT 이용
- PostgreSQL 사용
- Redis 사용
- Spring Cloud 사용

---

1. 회원 가입 후 팀을 생성한 유저는 자동적으로 SUPER USER(SU)의 권한을 얻는다.
2. SU는 권한을 생성하고 수정 삭제가 가능하다.  
   권한 생성 템플릿은 다음과 같으며, 각 항목을 ture/false로 권한 부여가 가능하다.  
   그러나 모든 권한이 ture인 것은 SU만 가능하며, 이 권한을 누군가에게 이전이 가능하며 이전 후 자신은 모든 권한을 읽는다.
   - 팀 Modify
   - 팀 Remove
   - 팀 멤버 Invite
   - 팀 멤버 Invite Accept
   - 팀 멤버 Excluding(내보내기)
   - 프로젝트 Create
3. SU 권한 멤버는 프로젝트를 생성, 수정, 삭제가 가능하다.  
   생성한 프로젝트에는 자동적으로 생성자가 PM의 권한을 얻는다   
   권한 생성 템플릿은 다음과 같으며, 각 항목을 ture/false로 권한 부여가 가능하다.  
   그러나 모든 권한이 ture인 것은 PM만 가능하며, 이 권한을 누군가에게 이전이 가능하며 이전 후 자신은 모든 권한을 잃는다.
   - 프로젝트 Modify
   - 프로젝트 Remove
   - 팀 멤버 Invite
   - 팀 멤버 Invite Accept
   - 팀 멤버 Excluding(내보내기)
4. a
5. b
