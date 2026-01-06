# 🐾 ANIMale

Java MVC 기반 애니메이션 커뮤니티 웹 프로젝트  
(MVC 패턴 + FrontController 구조)

---

## 📌 프로젝트 개요

'ANIMale'은 애니메이션 정보를 중심으로  
회원, 게시판, 뉴스, 애니 리스트, 결제, 이메일 인증 기능을 제공하는  
Java Servlet/JSP 기반 웹 커뮤니티 서비스입니다.

- Java Servlet / JSP 기반 웹 애플리케이션
- MVC 패턴 적용
- FrontController(`*.do`) 구조
- Oracle DB 연동
- 프론트 페이지 비동기 처리 적용

---

## 🛠 개발 환경

- 🧩 Language : Java 11  
- 🐱 Server : Apache Tomcat 9  
- 🗄️ DB : Oracle  
- 🧰 IDE : Eclipse  
- 🧱 Build : Maven  
- 🖥️ View : JSP / JSTL / JavaScript / jQuery  
- 📦 Library
  - gson
  - jstl
  - mail
  - ojdbc

---

## 🗂 프로젝트 구조

### src/main/java
- controller
  - anime / animepage
  - board / boardpage
  - member / memberpage
  - news / newspage
  - common
  - util
- model
  - dao
  - dto
  - common

### src/main/webapp
- common (header / footer)
- css
- js
- img
- WEB-INF/lib
- JSP pages (`*.jsp`)

---

## 🧭 컨트롤러 구조

### FrontController
- 모든 요청을 `*.do` 패턴으로 통합 처리
- ActionFactory를 통해 Action 분기

### Action
- 비즈니스 로직 처리
- DAO 호출 및 데이터 가공

### PageAction
- 화면 이동 및 화면 구성 담당
- JSP forward / redirect 처리
- 일부 PageAction은 화면 구성을 위한 데이터 전달 포함

### Async Servlet
- 중복 검사
- 좋아요 처리
- 댓글 조회 등 비동기 기능 처리

---

## ✨ 주요 기능

### 회원
- 회원가입 / 로그인 / 로그아웃
- 이메일 인증
- 비밀번호 찾기 / 변경
- 마이페이지 (정보 수정 및 이미지 변경, 이미지와 닉네임 변경은 유료로 구현)
- 회원 탈퇴

### 게시판
- 게시글 CRUD
- 댓글 CRUD
- 게시글 좋아요 (비동기처리)

### 뉴스
- 뉴스 목록 / 상세 조회
- 뉴스 CRUD (관리자)
- CKEditor 이미지 업로드

### 애니메이션
- 애니 리스트 / 상세 조회
- 애니 CRUD (관리자)

### 결제
- 카카오페이 결제
- 결제 성공 / 실패 / 취소 처리

---

## 🗄 DB 구조

- MEMBER  
- BOARD  
- REPLY  
- BOARD_LIKE  
- NEWS  
- ANIME  
- VOTE_CATEGORY  

각 테이블은 DAO / DTO 구조로 분리하여 관리합니다.

---

## ▶ 실행 방법

1. Oracle DB 테이블 생성  
   - 제공된 SQL 파일 실행
2. Tomcat 서버 실행
3. `index.jsp` 실행  
   → `mainPage.do`로 자동 이동

---

## 📎 참고

본 프로젝트는 학습 및 포트폴리오 목적의 팀 프로젝트입니다.
