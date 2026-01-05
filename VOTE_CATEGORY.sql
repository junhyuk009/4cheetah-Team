CREATE TABLE VOTE_CATEGORY(
	VOTE_CATEGORY_ID INT PRIMARY KEY,
	VOTE_CATEGORY_NAME VARCHAR(100) NOT NULL
);

CREATE SEQUENCE VOTE_CATEGORY_ID_SEQ  /* VOTE_CATEGORY_ID 시퀀스 */
	START WITH 1
	INCREMENT BY 1
	NOCACHE
	NOCYCLE;
=========================================================
-- 카테고리 추가
INSERT INTO VOTE_CATEGORY(
	VOTE_CATEGORY_ID,
	VOTE_CATEGORY_NAME
)VALUES(
	VOTE_CATEGORY_ID_SEQ.NEXTVAL,'로맨스코미디'
);
=========================================================
-- 카테고리 전체 조회
SELECT
VC.VOTE_CATEGORY_ID,
VC.VOTE_CATEGORY_NAME
FROM VOTE_CATEGORY VC
ORDER BY VC.VOTE_CATEGORY_ID ASC;
=========================================================
-- 카테고리 1개 조회(ID로 조회)
SELECT
VC.VOTE_CATEGORY_ID,
VC.VOTE_CATEGORY_NAME
FROM VOTE_CATEGORY VC
WHERE VC.VOTE_CATEGORY_ID = 1 ;
=========================================================
-- 카테고리 수정(ID로)
UPDATE VOTE_CATEGORY VC
SET VC.VOTE_CATEGORY_NAME = '액션'
WHERE VC.VOTE_CATEGORY_ID = 1;
=========================================================
-- 카테고리 삭제(ID로)
DELETE FROM VOTE_CATEGORY VC
WHERE VC.VOTE_CATEGORY_ID = 2;
=========================================================
SELECT * FROM VOTE_CATEGORY;
	
DROP TABLE VOTE_CATEGORY;

DROP SEQUENCE VOTE_CATEGORY_ID_SEQ /* VOTE_CATEGORY_ID 시퀀스 드랍 */