CREATE TABLE BOARD_LIKE(
	BOARD_LIKE_ID INT PRIMARY KEY,
	BOARD_ID INT NOT NULL,
	MEMBER_ID INT NOT NULL,
	
	CONSTRAINT UQ_BOARD_LIKE
		UNIQUE(BOARD_ID, MEMBER_ID), 
	
	CONSTRAINT FK_BOARD_LIKE_BOARD 
		FOREIGN KEY (BOARD_ID)
		REFERENCES BOARD(BOARD_ID)
		ON DELETE CASCADE, /*게시글 삭제 시 좋아요삭제*/
		
	CONSTRAINT FK_BOARD_LIKE_MEMBER
		FOREIGN KEY (MEMBER_ID)
		REFERENCES MEMBER(MEMBER_ID)
);

CREATE SEQUENCE BOARD_LIKE_ID_SEQ  /* BOARD_LIKE_ID 시퀀스로 1부터 1씩 증가 */
	START WITH 1
	INCREMENT BY 1
	NOCACHE
	NOCYCLE;
	
============================================================
-- 좋아요 추가 (중복 x)
INSERT INTO BOARD_LIKE (BOARD_LIKE_ID, BOARD_ID, MEMBER_ID)
VALUES(BOARD_LIKE_ID_SEQ.NEXTVAL,1,4);
============================================================
-- 좋아요 취소
DELETE FROM BOARD_LIKE WHERE BOARD_ID = 1 AND MEMBER_ID = 4;
============================================================
-- 좋아요 개수
SELECT COUNT(*) LIKE_CNT FROM BOARD_LIKE WHERE BOARD_ID = 1;
============================================================
-- 내가 이 글 좋아요 눌렀는지(1이면 눌렀음 / 0이면 안눌렀음)
SELECT COUNT(*) IS_LIKED FROM BOARD_LIKE WHERE BOARD_ID = 1 AND MEMBER_ID = 4;
============================================================
SELECT * FROM BOARD_LIKE;


DROP TABLE BOARD_LIKE CASCADE CONSTRAINTS PURGE;
DROP SEQUENCE BOARD_LIKE_ID_SEQ; /* BOARD_LIKE_ID 시퀀스 드랍 */