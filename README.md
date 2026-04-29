# ⭐ MiniProject Task-Manager ⭐
## 팀/개인 작업을 관리하기 위한 간단한 프로젝트 관리 시스템

## 📢 개요 
🔹**Task-Manager**는 소규모 팀 또는 개인 사용자를 대상으로 프로젝트 단위로 **할 일(Task)을 생성·관리**하고,
**상태/우선순위/담당자/마감일/태그 분류/댓글 등**의 기능을 제공하는 경량 협업 도구


## 👉 목표
### 🔹핵심 기능
- **회원가입/로그인** (JWT 기반 인증)
- **프로젝트(Project)** 생성 / 조회 / 수정 / 삭제
- **할 일(Task)**
  - 생성 / 조회 / 수정 / 삭제
  - 상태(TODO / IN_PROGRESS / DONE) 변경
  - 담당자(User) 배정
  - 마감일 (Due Date) 설정
  - 태그(Tag) 연결(M:N)
  - 필터링(상태 / 태그 / 담당자 / 기간)
- 댓글(Comment) 기반 작업 단위 커뮤니케이션

## 👉 최소 엔티티 구조
### 🔹필수 엔티티(5+)
1. User

    사용자 정보, 권한(USER/MANAGER/OWNER)
2. Project

    작업 컨테이너, owner(생성자)와 1:N
3. Task

    프로젝트에 속하는 작업
    - 상태, 우선순위, 담당자, 마감일 포함
4. Tag

    Task와 M:N 관계
5. Comment

    Task의 댓글 (작성자 User)

### 🔹선택 엔티티
6. TaskHistory

    Task의 변경 시점 상태 기록(상태/담당자/마감일)

## 👉 유스케이스 흐름 (기본 동작 과정)
1. **회원가입 & 로그인**
    - 로그인 시 JWT 발급
2. **프로젝트 생성**
    - OWNER = 현재 로그인 사용자
3. **Task 생성**
    - Tag 추가
    - 담당자 & 마감일 지정
4. **상태/담당자/마감일 수정**
    - 변경 시 TaskHistory 자동 기록
5. **댓글(Comment) 작성**

## 👉 기술적 설계 포인트(요약)
- REST 기반 API
- JWT 기반 인증/인가
- Task 상태/담당자/마강일 변경 시 이력 기록
- Tag M:N 매핑(task_tag 조인 테이블)
- 댓글 기반 협업 모델

## 🔐 인증 흐름

- 로그인 성공 시 JWT Access Token 발급  
- 이후 모든 API 요청 시 Authorization 헤더에 Bearer Token 포함  
- 서버는 토큰을 검증하여 사용자 인증 처리  

---

## 📊 상태 정의

### TaskStatus
- TODO: 작업 대기 상태  
- IN_PROGRESS: 작업 진행 중  
- DONE: 작업 완료  

### PriorityStatus
- LOW: 낮음  
- MEDIUM: 보통  
- HIGH: 높음  

---


## 📡 API 예시

※ 모든 API는 JWT 기반 인증을 사용하며, Authorization 헤더에 Bearer Token을 포함해야 합니다.

---

## 🔐 인증 (Auth)

### 로그인

POST /api/v1/auth/sign-in

#### Request
```json
{
  "loginId": "testuser",
  "password": "password123"
}
```

#### Response
```json
{
  "success": true,
  "data": {
    "tokenType": "Bearer",
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6...",
    "expiresAt": 1710000000000,
    "username": "testuser",
    "roles": ["USER"]
  }
}
```

## Task

### Task 생성

POST /api/v1/projects/{projectId}/tasks  
Authorization: Bearer {accessToken}

#### Request
```json
{
  "title": "할 일 제목",
  "content": "할 일 내용입니다.",
  "assigneeIds": [1, 2],
  "tagIds": [1],
  "newTags": [
    { "name": "긴급" }
  ],
  "status": "TODO",
  "priority": "HIGH",
  "dueDate": "2026-05-01"
}
```

#### Response
```json
{
  "success": true,
  "data": {
    "projectId": 1,
    "title": "할 일 제목",
    "createUserId": 1,
    "content": "할 일 내용입니다.",
    "assignees": ["user1", "user2"],
    "tags": [
      { "id": 1, "name": "긴급" }
    ],
    "status": "TODO",
    "priority": "HIGH",
    "dueDate": "2026-05-01"
  }
}
```

### Task 상태변경
PUT /api/v1/projects/{projectId}/tasks/{taskId}/status  
Authorization: Bearer {accessToken}

#### Request
```json
{
  "status": "IN_PROGRESS"
}
```

#### Response
```json
{
  "success": true,
  "data": {
    "id": 1,
    "projectId": 1,
    "title": "할 일 제목",
    "content": "할 일 내용입니다.",
    "createUserId": 1,
    "assignees": ["user1"],
    "status": "IN_PROGRESS",
    "priority": "HIGH",
    "tags": [
      { "id": 1, "name": "긴급" }
    ],
    "dueDate": "2026-05-01",
    "comments": []
  }
}
```

## 🔄 서비스 흐름

회원가입 → 로그인 → 프로젝트 생성 → Task 생성 → 상태 변경 → 댓글 작성

---

## 🧩 구조

- Controller → Service → Repository 계층 구조  
- DTO 기반 요청/응답 처리  
- Spring Security + JWT 인증 구조  
