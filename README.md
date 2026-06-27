# H-PMS Prototype

H-PMS 내 `내 업무` 기능 프로토타입입니다.

본 프로젝트는 기획, 설계, 개발, 테스트, 배포 흐름을 검증하기 위한 프로토타입이며 모든 설계와 구현 기준은 `docs/00_project_context.md`를 따른다.

## Tech Stack

- Frontend: Vue3, Vite, TypeScript, Vue Router, Pinia
- Backend: Spring Boot 3.x, Java 21, Gradle
- Database: PostgreSQL 16
- API: REST API
- Architecture: Layered Architecture
- Local Infra: Docker Compose

Nuxt는 사용하지 않는다.

## Directory Structure

```text
backend/
frontend/
db/init/
docs/
scripts/
docker-compose.yml
```

## Prerequisites

- Docker
- Docker Compose
- Java 21
- Node.js 20 이상 권장
- npm

## Local Development

전체 로컬 개발 환경을 Docker Compose로 실행한다.

```bash
./scripts/dev-start.sh
```

수동 실행:

```bash
docker compose up --build
```

기본 접속 정보:

| Service | URL |
| --- | --- |
| Frontend | `http://localhost:5173` |
| Backend | `http://localhost:8080` |
| Backend Health | `http://localhost:8080/actuator/health` |
| Swagger UI | `http://localhost:8080/swagger-ui/index.html` |
| PostgreSQL | `localhost:5432` |

PostgreSQL 기본 정보:

| 항목 | 값 |
| --- | --- |
| Database | `hpms` |
| User | `hpms` |
| Password | `hpms` |

DB 초기화 SQL:

- `db/init/001_schema.sql`
- `db/init/002_seed.sql`

## Frontend

```bash
cd frontend
npm install
npm run dev
```

빌드:

```bash
cd frontend
npm run build
```

타입 체크:

```bash
cd frontend
npm run type-check
```

테스트:

```bash
cd frontend
npm run test
```

## Backend

```bash
cd backend
./gradlew bootRun
```

`backend/gradlew`는 프로토타입용 실행 스크립트다. 로컬에 `gradle`이 있으면 로컬 Gradle을 사용하고, 없으면 Docker의 `gradle:8.10.2-jdk21` 이미지를 사용한다. 최초 실행 시 의존성 다운로드 또는 Docker 이미지 pull이 필요할 수 있다.

빌드:

```bash
cd backend
./gradlew build
```

테스트:

```bash
cd backend
./gradlew test
```

## Project Scripts

```bash
./scripts/dev-start.sh
./scripts/test.sh
./scripts/build.sh
```

## Current Scope

현재 구성은 프로젝트 초기 골격이다.

- Vue3/Vite/TypeScript 프론트엔드 기본 진입 화면
- Spring Boot 3/Java 21/Gradle 백엔드 기본 애플리케이션
- PostgreSQL Docker Compose 구성
- DB 초기 스키마 및 샘플 데이터
- 로컬 실행/테스트/빌드 스크립트

내 업무 API와 화면 상세 기능 구현은 후속 Phase에서 진행한다.
