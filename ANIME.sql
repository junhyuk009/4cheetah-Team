CREATE TABLE ANIME (
    ANIME_ID INT PRIMARY KEY,
    ANIME_TITLE VARCHAR(255) NOT NULL,
    ORIGINAL_TITLE VARCHAR(255) NULL,
    ANIME_YEAR INT NULL,
    ANIME_QUARTER VARCHAR(50) NULL,
    ANIME_STORY CLOB NULL, 	
    ANIME_THUMBNAIL_URL CLOB NULL
);

CREATE SEQUENCE ANIME_ID_SEQ    /* ANIME_ID 시퀀스 */
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;
============================================================
-- 샘플데이터
INSERT INTO ANIME (
  ANIME_ID,
  ANIME_TITLE,
  ORIGINAL_TITLE,
  ANIME_YEAR,
  ANIME_QUARTER,
  ANIME_STORY,
  ANIME_THUMBNAIL_URL
) VALUES (
  ANIME_ID_SEQ.NEXTVAL,
  '슬램덩크',
  'SLAM DUNK',
  1993,
  '4분기', 
  '농구를 통해 성장하는 고교생들의 이야기. 북산고 농구부의 전국 제패를 향한 도전.',
  '/images/anisample/slamdunk.jpg'
);

INSERT INTO ANIME (
  ANIME_ID,
  ANIME_TITLE,
  ORIGINAL_TITLE,
  ANIME_YEAR,
  ANIME_QUARTER,
  ANIME_STORY,
  ANIME_THUMBNAIL_URL
) VALUES (
  ANIME_ID_SEQ.NEXTVAL,
  '미소녀 전사 세일러문',
  '美少女戦士セーラームーン / Sailor Moon',
  1992,
  '1분기',
  '평범한 소녀가 세일러 전사로 각성해 동료들과 함께 악에 맞서 싸우는 이야기.',
  '/images/anisample/sailormoon.jpg'
);

INSERT INTO ANIME (
  ANIME_ID, ANIME_TITLE, ORIGINAL_TITLE, ANIME_YEAR, ANIME_QUARTER, ANIME_STORY, ANIME_THUMBNAIL_URL
) VALUES (
  ANIME_ID_SEQ.NEXTVAL,
  '사이버포뮬러',
  '新世紀GPXサイバーフォーミュラ / Future GPX Cyber Formula',
  1991,
  '1분기',
  '최첨단 머신으로 펼쳐지는 미래 레이스 GPX. 천재 소년 레이서가 성장하며 챔피언에 도전하는 이야기.',
  'images/anisample/cyberformula.jpg'
);

INSERT INTO ANIME (
  ANIME_ID, ANIME_TITLE, ORIGINAL_TITLE, ANIME_YEAR, ANIME_QUARTER, ANIME_STORY, ANIME_THUMBNAIL_URL
) VALUES (
  ANIME_ID_SEQ.NEXTVAL,
  '드래곤볼',
  'ドラゴンボール / Dragon Ball',
  1986,
  '1분기',
  '드래곤볼을 찾아 모험을 떠나는 손오공과 동료들의 이야기. 수많은 강적과의 대결 속에서 성장한다.',
  'images/anisample/dragonball.jpg'
);

INSERT INTO ANIME (
  ANIME_ID, ANIME_TITLE, ORIGINAL_TITLE, ANIME_YEAR, ANIME_QUARTER, ANIME_STORY, ANIME_THUMBNAIL_URL
) VALUES (
  ANIME_ID_SEQ.NEXTVAL,
  '기동전사 Z건담',
  '機動戦士Ζガンダム / Mobile Suit Zeta Gundam',
  1985,
  '1분기',
  '그리프스 전쟁을 배경으로 한 우주세기 건담의 핵심 서사. 주인공이 전쟁의 비극 속에서 변화한다.',
  'images/anisample/zgundam.jpg'
);

INSERT INTO ANIME (
  ANIME_ID, ANIME_TITLE, ORIGINAL_TITLE, ANIME_YEAR, ANIME_QUARTER, ANIME_STORY, ANIME_THUMBNAIL_URL
) VALUES (
  ANIME_ID_SEQ.NEXTVAL,
  '신세기 에반게리온',
  '新世紀エヴァンゲリオン / Neon Genesis Evangelion',
  1995,
  '4분기',
  '정체불명의 사도에 맞서기 위해 에바에 탑승하는 소년·소녀들. 인간 내면과 관계의 상처를 깊게 다룬다.',
  'images/anisample/evangelion.jpg'
);

INSERT INTO ANIME (
  ANIME_ID, ANIME_TITLE, ORIGINAL_TITLE, ANIME_YEAR, ANIME_QUARTER, ANIME_STORY, ANIME_THUMBNAIL_URL
) VALUES (
  ANIME_ID_SEQ.NEXTVAL,
  '카우보이비밥',
  'カウボーイビバップ / Cowboy Bebop',
  1998,
  '2분기',
  '우주를 떠도는 현상금 사냥꾼들의 에피소드. 재즈 감성과 누아르 분위기 속에서 과거와 마주한다.',
  'images/anisample/cowboybebop.jpg'
);

INSERT INTO ANIME (
  ANIME_ID, ANIME_TITLE, ORIGINAL_TITLE, ANIME_YEAR, ANIME_QUARTER, ANIME_STORY, ANIME_THUMBNAIL_URL
) VALUES (
  ANIME_ID_SEQ.NEXTVAL,
  '하울의 움직이는 성',
  'ハウルの動く城 / Howl''s Moving Castle',
  2004,
  '4분기',
  '저주로 노파가 된 소녀가 마법사 하울과 만나며 변화하는 이야기. 전쟁과 사랑, 자아를 다룬다.',
  'images/anisample/howl.jpg'
);

INSERT INTO ANIME (
  ANIME_ID, ANIME_TITLE, ORIGINAL_TITLE, ANIME_YEAR, ANIME_QUARTER, ANIME_STORY, ANIME_THUMBNAIL_URL
) VALUES (
  ANIME_ID_SEQ.NEXTVAL,
  '란마 1/2',
  'らんま1/2 / Ranma 1/2',
  1989,
  '2분기',
  '찬물·더운물에 따라 성별이 바뀌는 저주를 가진 소년 란마의 코믹 액션 러브스토리.',
  'images/anisample/ranma.jpg'
);

INSERT INTO ANIME (
  ANIME_ID, ANIME_TITLE, ORIGINAL_TITLE, ANIME_YEAR, ANIME_QUARTER, ANIME_STORY, ANIME_THUMBNAIL_URL
) VALUES (
  ANIME_ID_SEQ.NEXTVAL,
  '오소마츠군',
  'おそ松くん / Osomatsu-kun',
  1988,
  '1분기',
  '여섯쌍둥이 오소마츠와 친구들의 일상을 그린 개그 애니메이션. 시대 풍자를 섞은 코미디가 특징.',
  'images/anisample/osomatsukun.jpg'
);

INSERT INTO ANIME (
  ANIME_ID, ANIME_TITLE, ORIGINAL_TITLE, ANIME_YEAR, ANIME_QUARTER, ANIME_STORY, ANIME_THUMBNAIL_URL
) VALUES (
  ANIME_ID_SEQ.NEXTVAL,
  '웨딩피치',
  '愛天使伝説ウェディングピーチ / Wedding Peach',
  1995,
  '2분기',
  '사랑의 천사가 되어 악에 맞서는 소녀들의 변신 액션. 우정과 사랑을 중심으로 전개된다.',
  'images/anisample/weddingpeach.jpg'
);

INSERT INTO ANIME (
  ANIME_ID, ANIME_TITLE, ORIGINAL_TITLE, ANIME_YEAR, ANIME_QUARTER, ANIME_STORY, ANIME_THUMBNAIL_URL
) VALUES (
  ANIME_ID_SEQ.NEXTVAL,
  '천사소녀네티',
  '怪盗セイント・テール / Saint Tail',
  1995,
  '4분기',
  '낮에는 수녀 지망생, 밤에는 괴도 세인트 테일로 활동하는 소녀의 정의로운 활약과 로맨스.',
  'images/anisample/sainttail.jpg'
);

INSERT INTO ANIME (
  ANIME_ID, ANIME_TITLE, ORIGINAL_TITLE, ANIME_YEAR, ANIME_QUARTER, ANIME_STORY, ANIME_THUMBNAIL_URL
) VALUES (
  ANIME_ID_SEQ.NEXTVAL,
  '나루토',
  'NARUTO -ナルト- / Naruto',
  2002,
  '4분기',
  '인정받고 싶은 문제아 닌자 나루토가 동료들과 성장하며 강해지는 소년 성장 액션.',
  'images/anisample/naruto.jpg'
);

INSERT INTO ANIME (
  ANIME_ID, ANIME_TITLE, ORIGINAL_TITLE, ANIME_YEAR, ANIME_QUARTER, ANIME_STORY, ANIME_THUMBNAIL_URL
) VALUES (
  ANIME_ID_SEQ.NEXTVAL,
  '원피스',
  'ONE PIECE / One Piece',
  1999,
  '4분기',
  '해적왕을 꿈꾸는 루피와 동료들의 대모험. 바다를 무대로 한 우정과 자유의 서사.',
  'images/anisample/onepiece.jpg'
);

INSERT INTO ANIME (
  ANIME_ID, ANIME_TITLE, ORIGINAL_TITLE, ANIME_YEAR, ANIME_QUARTER, ANIME_STORY, ANIME_THUMBNAIL_URL
) VALUES (
  ANIME_ID_SEQ.NEXTVAL,
  '블리치',
  'BLEACH / Bleach',
  2004,
  '4분기',
  '사신의 힘을 얻게 된 소년이 영혼과 세계를 지키며 성장하는 배틀 액션.',
  'images/anisample/bleach.jpg'
);

    
============================================================
-- 메인페이지 (12개)
SELECT
ANIME_ID,
ANIME_TITLE,
ANIME_YEAR,
ANIME_QUARTER,
ANIME_THUMBNAIL_URL
FROM(
SELECT
ANIME_ID,
ANIME_TITLE,
ANIME_YEAR,
ANIME_QUARTER,
ANIME_THUMBNAIL_URL
FROM ANIME
ORDER BY ANIME_ID DESC
)
WHERE ROWNUM <= 12;
============================================================
-- 애니 전체 목록 - 기본 최신 등록순
SELECT
ANIME_ID,
ANIME_TITLE,
ANIME_YEAR,
ANIME_QUARTER,
ANIME_THUMBNAIL_URL
FROM ANIME
ORDER BY ANIME_ID DESC;
============================================================
-- 애니 전체 목록 출력 - 제목 가나다순
SELECT
ANIME_ID,
ANIME_TITLE,
ANIME_YEAR,
ANIME_QUARTER,
ANIME_THUMBNAIL_URL
FROM ANIME
ORDER BY ANIME_TITLE, ANIME_ID DESC;
============================================================
-- 애니 전체 목록 출력 - 방영년도별
SELECT
ANIME_ID,
ANIME_TITLE,
ANIME_YEAR,
ANIME_QUARTER,
ANIME_THUMBNAIL_URL
FROM ANIME
ORDER BY ANIME_YEAR DESC NULLS LAST, ANIME_ID DESC;
============================================================
-- 애니 검색 - 제목
SELECT
ANIME_ID,
ANIME_TITLE,
ANIME_YEAR,
ANIME_QUARTER,
ANIME_THUMBNAIL_URL
FROM ANIME WHERE ANIME_TITLE LIKE '%그 비스크 돌은 사랑을 한다%'
ORDER BY ANIME_ID DESC;
============================================================
-- 애니 검색 - 줄거리
SELECT
ANIME_ID,
ANIME_TITLE,
ANIME_YEAR,
ANIME_QUARTER,
ANIME_THUMBNAIL_URL
FROM ANIME WHERE DBMS_LOB.INSTR(ANIME_STORY,'이건') > 0
ORDER BY ANIME_ID DESC;
============================================================
-- 애니 상세보기
SELECT
ANIME_ID,
ANIME_TITLE,
ANIME_YEAR,
ANIME_QUARTER,
ANIME_STORY,
ANIME_THUMBNAIL_URL
FROM ANIME WHERE ANIME_ID = 1;
============================================================
SELECT * FROM ANIME;

DROP TABLE ANIME CASCADE CONSTRAINTS PURGE;

DROP SEQUENCE ANIME_ID_SEQ;