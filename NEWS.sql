CREATE TABLE NEWS(
	NEWS_ID INT PRIMARY KEY,
	ANIME_ID INT NULL,
	NEWS_TITLE VARCHAR(255) NOT NULL,
	NEWS_CONTENT CLOB NOT NULL,
	NEWS_IMAGE_URL CLOB NULL,
	NEWS_THUMBNAIL_URL CLOB NULL,
	
	CONSTRAINT FK_NEWS_ANIME
		FOREIGN KEY (ANIME_ID)
      	REFERENCES ANIME(ANIME_ID)
);

CREATE SEQUENCE NEWS_ID_SEQ  /* NEWS_ID 시퀀스로 1부터 1씩 증가 */
	START WITH 1
	INCREMENT BY 1
	NOCACHE
	NOCYCLE;
	
=========================================================
-- 샘플 NEWS를 CKEditor 방식으로 통일
-- 규칙:
-- 1) 커버(썸네일) = /upload/newsThumb/N.jpg
-- 2) 본문 대표 이미지 = /upload/news/N.jpg
-- 3) NEWS_CONTENT 안에 <img> 포함

-- 1) 슬램덩크
INSERT INTO NEWS (
  NEWS_ID, ANIME_ID, NEWS_TITLE, NEWS_CONTENT, NEWS_IMAGE_URL, NEWS_THUMBNAIL_URL
) VALUES (
  NEWS_ID_SEQ.NEXTVAL,
  (SELECT MIN(ANIME_ID) FROM ANIME WHERE ANIME_TITLE = '슬램덩크'),
  '슬램덩크, 여전히 레전드로 남는 이유',
  q'~
<p>슬램덩크는 단순 스포츠물이 아니라 성장과 팀워크를 깊게 다루는 작품입니다.</p>
<figure class="image"><img src="/upload/news/1.jpg" alt="슬램덩크 본문 이미지"></figure>
<p>캐릭터의 감정선과 경기 연출이 여전히 회자되며, 농구를 잘 몰라도 몰입할 수 있는 스토리 구조가 강점입니다.</p>
~',
  '/upload/news/1.jpg',
  '/upload/newsThumb/1.jpg'
);

-- 2) 세일러문
INSERT INTO NEWS (
  NEWS_ID, ANIME_ID, NEWS_TITLE, NEWS_CONTENT, NEWS_IMAGE_URL, NEWS_THUMBNAIL_URL
) VALUES (
  NEWS_ID_SEQ.NEXTVAL,
  (SELECT MIN(ANIME_ID) FROM ANIME WHERE ANIME_TITLE = '미소녀 전사 세일러문'),
  '세일러문, 시대를 앞서간 히로인 서사의 교과서',
  q'~
<p>세일러문은 소녀들의 연대와 성장이라는 테마를 대중적으로 확장시킨 작품입니다.</p>
<figure class="image"><img src="/upload/news/2.jpg" alt="세일러문 본문 이미지"></figure>
<p>다양한 개성과 관계성, 상징적인 변신 연출이 지금도 전 세계적으로 큰 영향력을 갖고 있습니다.</p>
~',
  '/upload/news/2.jpg',
  '/upload/newsThumb/2.jpg'
);

-- 3) 드래곤볼
INSERT INTO NEWS (
  NEWS_ID, ANIME_ID, NEWS_TITLE, NEWS_CONTENT, NEWS_IMAGE_URL, NEWS_THUMBNAIL_URL
) VALUES (
  NEWS_ID_SEQ.NEXTVAL,
  (SELECT MIN(ANIME_ID) FROM ANIME WHERE ANIME_TITLE = '드래곤볼'),
  '드래곤볼이 만든 “배틀물 문법” 다시 보기',
  q'~
<p>드래곤볼은 수련-성장-강적-각성이라는 배틀물의 기본 문법을 대중화한 작품입니다.</p>
<figure class="image"><img src="/upload/news/3.jpg" alt="드래곤볼 본문 이미지"></figure>
<p>지금 봐도 시원한 전개와 캐릭터 매력이 확실하며, 명장면들이 밈처럼 살아있습니다.</p>
~',
  '/upload/news/3.jpg',
  '/upload/newsThumb/3.jpg'
);

-- 4) 기동전사 Z건담
INSERT INTO NEWS (
  NEWS_ID, ANIME_ID, NEWS_TITLE, NEWS_CONTENT, NEWS_IMAGE_URL, NEWS_THUMBNAIL_URL
) VALUES (
  NEWS_ID_SEQ.NEXTVAL,
  (SELECT MIN(ANIME_ID) FROM ANIME WHERE ANIME_TITLE = '기동전사 Z건담'),
  'Z건담: 건담 세계관의 무게감을 완성한 작품',
  q'~
<p>Z건담은 전쟁의 후유증과 이념 갈등을 한층 진하게 담아낸 작품입니다.</p>
<figure class="image"><img src="/upload/news/4.jpg" alt="Z건담 본문 이미지"></figure>
<p>인물들의 선택이 쉽게 “정답”으로 귀결되지 않아 더 깊게 여운이 남는다는 평가가 많습니다.</p>
~',
  '/upload/news/4.jpg',
  '/upload/newsThumb/4.jpg'
);

-- 5) 신세기 에반게리온
INSERT INTO NEWS (
  NEWS_ID, ANIME_ID, NEWS_TITLE, NEWS_CONTENT, NEWS_IMAGE_URL, NEWS_THUMBNAIL_URL
) VALUES (
  NEWS_ID_SEQ.NEXTVAL,
  (SELECT MIN(ANIME_ID) FROM ANIME WHERE ANIME_TITLE = '신세기 에반게리온'),
  '에반게리온, “해석하는 재미”가 남다른 이유',
  q'~
<p>에반게리온은 로봇물의 외형을 빌려 인간 심리와 관계를 파고든 작품입니다.</p>
<figure class="image"><img src="/upload/news/5.jpg" alt="에반게리온 본문 이미지"></figure>
<p>상징과 연출이 많아 보는 사람에 따라 다양한 해석이 가능하고, 그 자체가 콘텐츠가 되었습니다.</p>
~',
  '/upload/news/5.jpg',
  '/upload/newsThumb/5.jpg'
);

-- 6) 카우보이비밥
INSERT INTO NEWS (
  NEWS_ID, ANIME_ID, NEWS_TITLE, NEWS_CONTENT, NEWS_IMAGE_URL, NEWS_THUMBNAIL_URL
) VALUES (
  NEWS_ID_SEQ.NEXTVAL,
  (SELECT MIN(ANIME_ID) FROM ANIME WHERE ANIME_TITLE = '카우보이비밥'),
  '카우보이비밥: 분위기와 음악이 완성한 명작',
  q'~
<p>카우보이비밥은 에피소드 구성, 액션, 음악이 유기적으로 맞물린 작품입니다.</p>
<figure class="image"><img src="/upload/news/6.jpg" alt="카우보이비밥 본문 이미지"></figure>
<p>완결 이후에도 “감성”으로 회자되는 대표작이며, 입문자에게도 추천이 많은 편입니다.</p>
~',
  '/upload/news/6.jpg',
  '/upload/newsThumb/6.jpg'
);

-- 7) 하울의 움직이는 성
INSERT INTO NEWS (
  NEWS_ID, ANIME_ID, NEWS_TITLE, NEWS_CONTENT, NEWS_IMAGE_URL, NEWS_THUMBNAIL_URL
) VALUES (
  NEWS_ID_SEQ.NEXTVAL,
  (SELECT MIN(ANIME_ID) FROM ANIME WHERE ANIME_TITLE = '하울의움직이는성'),
  '하울의 움직이는 성, 지금 다시 보면 더 좋은 포인트',
  q'~
<p>하울의 움직이는 성은 판타지 로맨스의 정석처럼 보이지만, 성장과 자기 수용의 메시지가 강합니다.</p>
<figure class="image"><img src="/upload/news/7.jpg" alt="하울의 움직이는 성 본문 이미지"></figure>
<p>배경 미술과 음악의 완성도가 높아 재감상 만족도가 큽니다.</p>
~',
  '/upload/news/7.jpg',
  '/upload/newsThumb/7.jpg'
);

-- 8) 란마 1/2
INSERT INTO NEWS (
  NEWS_ID, ANIME_ID, NEWS_TITLE, NEWS_CONTENT, NEWS_IMAGE_URL, NEWS_THUMBNAIL_URL
) VALUES (
  NEWS_ID_SEQ.NEXTVAL,
  (SELECT MIN(ANIME_ID) FROM ANIME WHERE ANIME_TITLE = '란마1/2'),
  '란마 1/2: “클래식 개그 감성”의 정점',
  q'~
<p>란마 1/2는 캐릭터 코미디의 템포가 뛰어나고, 설정 자체가 강력한 개그 장치입니다.</p>
<figure class="image"><img src="/upload/news/8.jpg" alt="란마 1/2 본문 이미지"></figure>
<p>가볍게 보기 좋으면서도 캐릭터 관계성이 탄탄해 오래 사랑받는 작품입니다.</p>
~',
  '/upload/news/8.jpg',
  '/upload/newsThumb/8.jpg'
);

-- 9) 웨딩피치
INSERT INTO NEWS (
  NEWS_ID, ANIME_ID, NEWS_TITLE, NEWS_CONTENT, NEWS_IMAGE_URL, NEWS_THUMBNAIL_URL
) VALUES (
  NEWS_ID_SEQ.NEXTVAL,
  (SELECT MIN(ANIME_ID) FROM ANIME WHERE ANIME_TITLE = '웨딩피치'),
  '웨딩피치, 변신물의 다른 결을 보여준 작품',
  q'~
<p>웨딩피치는 변신 요소에 “웨딩” 테마를 결합해 독특한 인상을 남겼습니다.</p>
<figure class="image"><img src="/upload/news/9.jpg" alt="웨딩피치 본문 이미지"></figure>
<p>당시 감성의 작화와 연출이 지금 보면 오히려 신선하게 느껴지는 포인트가 있습니다.</p>
~',
  '/upload/news/9.jpg',
  '/upload/newsThumb/9.jpg'
);

-- 10) 천사소녀네티
INSERT INTO NEWS (
  NEWS_ID, ANIME_ID, NEWS_TITLE, NEWS_CONTENT, NEWS_IMAGE_URL, NEWS_THUMBNAIL_URL
) VALUES (
  NEWS_ID_SEQ.NEXTVAL,
  (SELECT MIN(ANIME_ID) FROM ANIME WHERE ANIME_TITLE = '천사소녀네티'),
  '천사소녀네티: 순정+미스터리 조합의 매력',
  q'~
<p>천사소녀네티는 순정 감성에 미스터리 요소를 섞어 차별화된 재미를 줍니다.</p>
<figure class="image"><img src="/upload/news/10.jpg" alt="천사소녀네티 본문 이미지"></figure>
<p>캐릭터의 감정선과 “정체”를 둘러싼 전개가 꾸준히 흥미를 유지합니다.</p>
~',
  '/upload/news/10.jpg',
  '/upload/newsThumb/10.jpg'
);

-- 11) 나루토
INSERT INTO NEWS (
  NEWS_ID, ANIME_ID, NEWS_TITLE, NEWS_CONTENT, NEWS_IMAGE_URL, NEWS_THUMBNAIL_URL
) VALUES (
  NEWS_ID_SEQ.NEXTVAL,
  (SELECT MIN(ANIME_ID) FROM ANIME WHERE ANIME_TITLE = '나루토'),
  '나루토: 성장서사의 교과서, 다시 정리',
  q'~
<p>나루토는 주인공의 성장뿐 아니라 주변 인물들의 서사도 촘촘한 편입니다.</p>
<figure class="image"><img src="/upload/news/11.jpg" alt="나루토 본문 이미지"></figure>
<p>회차가 길어도 “기억에 남는 감정”이 강해서 여전히 추천작으로 꼽힙니다.</p>
~',
  '/upload/news/11.jpg',
  '/upload/newsThumb/11.jpg'
);

-- 12) 원피스
INSERT INTO NEWS (
  NEWS_ID, ANIME_ID, NEWS_TITLE, NEWS_CONTENT, NEWS_IMAGE_URL, NEWS_THUMBNAIL_URL
) VALUES (
  NEWS_ID_SEQ.NEXTVAL,
  (SELECT MIN(ANIME_ID) FROM ANIME WHERE ANIME_TITLE = '원피스'),
  '원피스: “모험”이 주는 순수한 재미의 힘',
  q'~
<p>원피스는 세계관 확장과 떡밥 회수의 재미가 큰 작품입니다.</p>
<figure class="image"><img src="/upload/news/12.jpg" alt="원피스 본문 이미지"></figure>
<p>동료 서사와 감정 연출이 강하고, 큰 줄기 목표가 명확해서 장기 연재임에도 몰입도가 유지됩니다.</p>
~',
  '/upload/news/12.jpg',
  '/upload/newsThumb/12.jpg'
);

-- 13) 주술회전
INSERT INTO NEWS (
  NEWS_ID, ANIME_ID, NEWS_TITLE, NEWS_CONTENT, NEWS_IMAGE_URL, NEWS_THUMBNAIL_URL
) VALUES (
  NEWS_ID_SEQ.NEXTVAL,
  (SELECT MIN(ANIME_ID) FROM ANIME WHERE ANIME_TITLE = '주술회전'),
  '주술회전, 긴급 속보 멋있음..',
  q'~
<p>주술회전은 주인공이 두명에 페이크 주인공이 1명이다.</p>
<figure class="image"><img src="/upload/news/13.jpg" alt="주술회전 본문 이미지"></figure>
<p>분위기 미쳤음…</p>
~',
  '/upload/news/13.jpg',
  '/upload/newsThumb/13.jpg'
);

=========================================================
-- 뉴스 전체 출력
SELECT
N.NEWS_ID,
N.NEWS_TITLE,
N.NEWS_THUMBNAIL_URL
FROM NEWS N
ORDER BY N.NEWS_ID DESC;
=========================================================
-- 뉴스 상세보기
SELECT
N.NEWS_ID,
N.NEWS_TITLE,
N.NEWS_IMAGE_URL,
N.NEWS_CONTENT,
N.NEWS_THUMBNAIL_URL,
N.ANIME_ID,
A.ANIME_TITLE,
A.ANIME_YEAR,
A.ANIME_QUARTER,
A.ANIME_THUMBNAIL_URL ANIME_THUMBNAIL_URL
FROM NEWS N 
LEFT JOIN ANIME A ON A.ANIME_ID = N.ANIME_ID
WHERE N.NEWS_ID = 1; 
=========================================================
-- 뉴스 검색 -제목 포함
SELECT
N.NEWS_ID,
N.NEWS_TITLE,
N.NEWS_THUMBNAIL_URL
FROM NEWS N 
WHERE N.NEWS_TITLE LIKE '%비스크%'
ORDER BY N.NEWS_ID DESC;
=========================================================
SELECT
N.NEWS_ID,
N.NEWS_TITLE,
N.NEWS_THUMBNAIL_URL
FROM NEWS N
WHERE DBMS_LOB.INSTR(N.NEWS_CONTENT,'이건') > 0
ORDER BY N.NEWS_ID DESC;
=========================================================
-- 13번 뉴스 글 삭제
DELETE FROM NEWS
WHERE NEWS_ID = 13;

DELETE FROM NEWS;

DROP TABLE NEWS;

DROP SEQUENCE NEWS_ID_SEQ /* NEWS_ID 시퀀스 드랍 */