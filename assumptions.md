# Assumptions

프로젝트 초기 구성 중 문서 기준과 충돌하는 사항은 발견되지 않았다.

## 적용한 전제

- PostgreSQL은 Docker Compose로만 실행한다.
- DB 초기화 SQL은 `db/init/001_schema.sql`, 샘플 데이터는 `db/init/002_seed.sql`에 둔다.
- Backend는 Spring Initializr 스타일의 단일 Gradle 프로젝트 구조로 구성한다.
- 네트워크 없이 표준 Gradle wrapper jar를 생성하지 않기 위해 `backend/gradlew`는 로컬 Gradle 또는 Docker Gradle 이미지에 위임하는 프로토타입용 실행 스크립트로 둔다.
- 현재 Docker Gradle 이미지의 `linux/arm64` 실행에서 native library 초기화 오류가 발생하므로, 프로토타입 검증에서는 Gradle 컨테이너를 `linux/amd64` 플랫폼으로 실행한다.
- Frontend는 Nuxt 없이 Vite 기반 Vue3 프로젝트 구조로 구성한다.
- 현재 단계에서는 내 업무 상세 API와 화면 기능을 구현하지 않고, 실행 가능한 프로젝트 골격만 생성한다.
