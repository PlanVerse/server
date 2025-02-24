![Gitlab](https://img.shields.io/badge/gitlab-e24329?style=for-the-badge&logo=gitlab&logoColor=white)
![Gitlab Ci](https://img.shields.io/badge/gitlab%20ci-e24329?style=for-the-badge&logo=gitlab&logoColor=white)

![Java](https://img.shields.io/badge/Java%2017-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)   
![Gradle](https://img.shields.io/badge/Gradle%208.11.1-02303A?style=for-the-badge&logo=Gradle&logoColor=white)   
![Kotlin](https://img.shields.io/badge/Kotlin%201.9.25-7F52FF?style=for-the-badge&logo=Kotlin&logoColor=white)  
![Spring Boot](https://img.shields.io/badge/spring%20boot%203.4.0-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Cloud](https://img.shields.io/badge/spring%20cloud%204.2.0(2024.0.0)-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/spring%20security%206.4.1-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)   
![Jib](https://img.shields.io/badge/jib%203.4.4-4285F4?style=for-the-badge&logo=googlecloud&logoColor=white)  

![Postgresql](https://img.shields.io/badge/postgresql%2017.2-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)  
![Redis](https://img.shields.io/badge/redis%207.4.1-DC382D?style=for-the-badge&logo=redis&logoColor=white)


![Docker](https://img.shields.io/badge/docker-257bd6?style=for-the-badge&logo=docker&logoColor=white)

![Cloudflare](https://img.shields.io/badge/cloudflare-F38020?style=for-the-badge&logo=cloudflare&logoColor=white)

---

# PMS - PlanVerse

---

- JWT를 이용한 사용자 인증
- jpa, mybatis 연동
- gitlab ci를 이용한 빌드, 배포 자동화
  - runner
    - window
    - macos
- 모니터링
  - prometheus
  - grafana
  - loki
  - portainer
- 파일 서비스
  - minio
    - dev : standalone
    - main : distributed ( TODO )
- DNS
  - Cloudflare (Proxy)
  - Cloudflare zero trust

---

## 개발 참고 자료

- [Check](doc/check.md)
- [Kotlin 스코프 함수 정리](doc/scope.md)
- [Mybatis 관련](doc/mybatis.md)

---

## Need Attention

- [ ] K8S 도입
- [ ] zero-downtime deployment
- [ ] Netdata, Portainer 비교
- [ ] WatchTower
- [ ] OCI(Oracle Cloud Infrastructure)
  - [ ] instances 활용
- [ ] Nexus 활용 방안 고민