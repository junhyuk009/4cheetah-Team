CREATE TABLE BOARD (
	BOARD_ID INT PRIMARY KEY,
	MEMBER_ID INT NOT NULL,
	BOARD_TITLE VARCHAR(255) NOT NULL,
	BOARD_CONTENT CLOB NOT NULL,
	BOARD_VIEWS INT DEFAULT 0 NOT NULL,
	BOARD_CATEGORY VARCHAR(100) DEFAULT 'ANIME' NOT NULL,
	
	CONSTRAINT FK_BOARD_MEMBER
		FOREIGN KEY (MEMBER_ID)
		REFERENCES MEMBER(MEMBER_ID)
);

CREATE SEQUENCE BOARD_ID_SEQ  /* BOARD_ID 시퀀스로 1부터 1씩 증가 */
	START WITH 1
	INCREMENT BY 1
	NOCACHE
	NOCYCLE;
==============================================================
-- 관리자 글 (공지처럼 보일 예정)
INSERT INTO BOARD (
  BOARD_ID, MEMBER_ID, BOARD_TITLE, BOARD_CONTENT, BOARD_VIEWS, BOARD_CATEGORY
) VALUES (
  BOARD_ID_SEQ.NEXTVAL,
  (SELECT MEMBER_ID FROM MEMBER WHERE MEMBER_NAME='admin'),
  '[공지] 사이트 이용 안내',
  '공지 내용입니다.',
  0,
  'ANIME'
);

-- 글 작성
INSERT INTO BOARD(
BOARD_ID,
MEMBER_ID,
BOARD_TITLE,
BOARD_CONTENT,
BOARD_CATEGORY)
VALUES (BOARD_ID_SEQ.NEXTVAL, '4', '그 비스크 돌은 사랑을 한다', '이건 못참지~', 'ANIME');
==============================================================
-- 애니 게시판 목록 (관리자 글 제외)
SELECT 
B.BOARD_ID,
B.MEMBER_ID,
CASE WHEN M.MEMBER_ROLE = 'WITHDRAWN' THEN '탈퇴한 회원'
ELSE M.MEMBER_NICKNAME 
END WRITER_NICKNAME, B.BOARD_TITLE, B.BOARD_VIEWS, B.BOARD_CATEGORY 
FROM BOARD B
JOIN MEMBER M ON M.MEMBER_ID = B.MEMBER_ID 
WHERE B.BOARD_CATEGORY = 'ANIME' AND M.MEMBER_ROLE <> 'ADMIN'  --관리자 제외
ORDER BY B.BOARD_ID DESC;
==============================================================
-- 공지사항(관리자 상단고정 = 관리자 글만)
SELECT
B.BOARD_ID,
B.MEMBER_ID,
M.MEMBER_NICKNAME WRITER_NICKNAME,
B.BOARD_TITLE,
B.BOARD_VIEWS,
B.BOARD_CATEGORY
FROM BOARD B
JOIN MEMBER M ON M.MEMBER_ID = B.MEMBER_ID
WHERE B.BOARD_CATEGORY = 'ANIME'
AND B.MEMBER_ID = 'ADMIN'
ORDER BY B.BOARD_ID DESC;
==============================================================
-- 베스트 좋아요
SELECT
B.BOARD_ID,
B.MEMBER_ID,
CASE WHEN M.MEMBER_ROLE = 'WITHDRAWN' THEN '탈퇴한 회원'
ELSE M.MEMBER_NICKNAME 
END WRITER_NICKNAME, B.BOARD_TITLE, B.BOARD_VIEWS, COUNT(BL.MEMBER_ID) LIKE_CNT
FROM BOARD B
JOIN MEMBER M ON M.MEMBER_ID = B.MEMBER_ID
JOIN BOARD_LIKE BL ON BL.BOARD_ID = B.BOARD_ID
WHERE B.BOARD_CATEGORY = 'ANIME'
GROUP BY B.BOARD_ID, B.MEMBER_ID, M.MEMBER_ROLE, M.MEMBER_NICKNAME,B.BOARD_TITLE, B.BOARD_VIEWS
HAVING COUNT(BL.MEMBER_ID) >= 10
ORDER BY LIKE_CNT DESC, B.BOARD_ID DESC;
==============================================================
-- 검색 - 제목 포함
SELECT
B.BOARD_ID,
B.MEMBER_ID,
CASE WHEN M.MEMBER_ROLE = 'WITHDRAWN' THEN '탈퇴한 회원'
ELSE M.MEMBER_NICKNAME 
END WRITER_NICKNAME, B.BOARD_TITLE, B.BOARD_VIEWS, B.BOARD_CATEGORY
FROM BOARD B 
JOIN MEMBER M ON M.MEMBER_ID = B.MEMBER_ID
WHERE B.BOARD_CATEGORY = 'ANIME'
AND B.BOARD_TITLE LIKE '%비스크%'
ORDER BY B.BOARD_ID DESC;
==============================================================
-- 검색 - 작성자 닉네임(탈퇴회원 제외)
SELECT
B.BOARD_ID,
B.MEMBER_ID,
M.MEMBER_NICKNAME WRITER_NICKNAME,
B.BOARD_TITLE,
B.BOARD_VIEWS,
B.BOARD_CATEGORY
FROM BOARD B
JOIN MEMBER M ON M.MEMBER_ID = B.MEMBER_ID
WHERE B.BOARD_CATEGORY = 'ANIME'
AND M.MEMBER_ROLE = 'ACTIVE'
AND M.MEMBER_NICKNAME = '김유신장군'
ORDER BY B.BOARD_ID DESC;
==============================================================
-- 검색 - 내용 포함
SELECT
B.BOARD_ID,
B.MEMBER_ID,
CASE WHEN M.MEMBER_ROLE = 'WITHDRAWN' THEN '탈퇴한 회원'
ELSE M.MEMBER_NICKNAME
END WRITER_NICKNAME, B.BOARD_TITLE, B.BOARD_VIEWS, B.BOARD_CATEGORY
FROM BOARD B
JOIN MEMBER M ON M.MEMBER_ID = B.MEMBER_ID
WHERE B.BOARD_CATEGORY = 'ANIME'
AND DBMS_LOB.INSTR(B.BOARD_CONTENT,'이건') > 0
ORDER BY B.BOARD_ID DESC;
==============================================================
-- 상세 조회
SELECT 
B.BOARD_ID,
B.MEMBER_ID,
CASE WHEN M.MEMBER_ROLE = 'WITHDRAWN' THEN '탈퇴한 회원'
ELSE M.MEMBER_NICKNAME
END WRITER_NICKNAME,
B.BOARD_TITLE,
B.BOARD_CONTENT,
B.BOARD_VIEWS,
B.BOARD_CATEGORY
FROM BOARD B
JOIN MEMBER M ON M.MEMBER_ID = B.MEMBER_ID
WHERE B.BOARD_ID = '1';
==============================================================
-- 조회수 증가
UPDATE BOARD SET BOARD_VIEWS = BOARD_VIEWS + 1 WHERE BOARD_ID = '1';
==============================================================
-- 수정
UPDATE BOARD SET BOARD_TITLE = '그 비스크 돌은 사랑을 한다',
BOARD_CONTENT = '마린'
WHERE BOARD_ID = '1'
AND MEMBER_ID='4';
==============================================================
-- 삭제
DELETE FROM BOARD WHERE BOARD_ID = '1' AND MEMBER_ID = '4';
==============================================================

SELECT * FROM BOARD;

DROP TABLE BOARD;

DROP TABLE BOARD CASCADE CONSTRAINT;                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    S PURGE;

DROP SEQUENCE BOARD_ID_SEQ; /*  BOARD_ID 시퀀스 드랍 */