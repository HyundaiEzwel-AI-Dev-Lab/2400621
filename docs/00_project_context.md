# H-PMS Project Context

## Goal

H-PMS 내 '내 업무' 기능을 프로토타입으로 개발하여
바이브코딩 개발 방법론을 검증한다.

## Architecture

기술 스택은 아래 기준을 따른다.
임의로 변경하거나 추가하지 않는다.

Frontend

- Vue3
- Nuxt 사용 금지
- Vue Router
- Pinia
- Vite
- TypeScript

Backend

- Java 21
- Spring Boot 3.x
- Gradle
- Spring Data JPA

Database

- PostgreSQL 16

Infra

- Docker
- Docker Compose
- GitHub
- CI/CD는 추후 구성

## Development Principles

- REST API
- Layered Architecture
- DTO 사용
- Entity 직접 노출 금지
- OpenAPI(Swagger) 제공
- 테스트 코드 필수

## Coding Convention

- Java : Google Java Style
- Vue : Composition API
- TypeScript Strict Mode
- Conventional Commit 사용

## Directory Structure

backend/
frontend/
docs/
