<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>AniMale | 애니 상세</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<link rel="icon" type="image/png" href="<%=request.getContextPath()%>/favicon.png">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/elegant-icons.css">

<!-- Google Font -->
<link href="https://fonts.googleapis.com/css2?family=Oswald:wght@400;600;700&display=swap" rel="stylesheet">
<link href="https://fonts.googleapis.com/css2?family=Mulish:wght@400;600;700&display=swap" rel="stylesheet">

<!-- CSS -->
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/bootstrap.min.css">
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/font-awesome.min.css">
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/style.css">

<style>
/* =========================
   Anime Detail
   - 페이지 전용 prefix로 충돌 최소화
========================= */
.header__right__icons .icon_search { display:none !important; }

.anime-detail-wrap{
  padding: 120px 0;
}

/* 카드: 글래스 + 그라데이션 보더 + 은은한 광택 */
.anime-detail-card{
  position: relative;
  border-radius: 22px;
  padding: 44px;
  background: rgba(255,255,255,0.06);
  border: 1px solid rgba(255,255,255,0.10);
  backdrop-filter: blur(18px) saturate(140%);
  -webkit-backdrop-filter: blur(18px) saturate(140%);
  box-shadow: 0 18px 60px rgba(0,0,0,0.45);
  overflow: hidden;
}

/* 상단 광택 */
.anime-detail-card::before{
  content:"";
  position:absolute;
  top:-60%;
  left:-30%;
  width: 80%;
  height: 120%;
  transform: rotate(18deg);
  background: radial-gradient(circle at 30% 30%, rgba(255,255,255,0.20), rgba(255,255,255,0.00) 60%);
  pointer-events:none;
  filter: blur(2px);
  opacity: .9;
}

/* 은은한 그라데이션 테두리 느낌 */
.anime-detail-card::after{
  content:"";
  position:absolute;
  inset:0;
  border-radius: 22px;
  padding: 1px;
  background: linear-gradient(135deg,
    rgba(255,255,255,0.35),
    rgba(255,255,255,0.08),
    rgba(120,190,255,0.18),
    rgba(255,255,255,0.10)
  );
  -webkit-mask:
    linear-gradient(#000 0 0) content-box,
    linear-gradient(#000 0 0);
  -webkit-mask-composite: xor;
  mask-composite: exclude;
  pointer-events:none;
  opacity: .8;
}

.anime-detail-row{ align-items:flex-start; }

.anime-thumb{
  width:100%;
  max-width: 310px;
  border-radius: 18px;
  overflow:hidden;
  box-shadow: 0 18px 60px rgba(0,0,0,0.50);
  border: 1px solid rgba(255,255,255,0.10);
  background: rgba(0,0,0,0.18);
}

.anime-thumb img{
  width:100%;
  height:auto;
  display:block;
}

.anime-info{ padding-left: 30px; }

.anime-title{
  font-size: 36px;
  font-weight: 800;
  letter-spacing: -0.02em;
  color:#fff;
  line-height: 1.15;
}

.meta-chips{
  margin-top: 18px;
  display:flex;
  gap:10px;
  flex-wrap: wrap; /* 모바일에서 줄바꿈 */
}

.meta-chip{
  display:inline-flex;
  align-items:center;
  gap:8px;
  padding: 7px 14px;
  border-radius: 999px;
  font-size: 13px;
  font-weight: 700;
  background: rgba(255,255,255,0.10);
  border: 1px solid rgba(255,255,255,0.10);
  color:#fff;
}

.meta-chip i{
  opacity:.9;
}

/* 시놉시스: 본문도 유리 박스에 넣어서 통일 */
.anime-synopsis{
  margin-top: 22px;
  padding: 18px 18px;
  border-radius: 16px;
  background: rgba(0,0,0,0.20);
  border: 1px solid rgba(255,255,255,0.10);
  color: rgba(255,255,255,0.92);
  line-height: 1.95;
}

/* 버튼 영역 */
.action-wrap{
  margin-top: 22px;
  display:flex;
  justify-content: flex-end;
  align-items:center;
  gap: 10px;
  flex-wrap: wrap;
}

/* 버튼 베이스 (글래스) */
.ad-btn{
  display:inline-flex;
  align-items:center;
  justify-content:center;
  gap:8px;
  padding: 10px 16px;
  border-radius: 999px;
  font-size: 14px;
  font-weight: 800;
  letter-spacing: -0.01em;
  text-decoration:none !important;
  user-select:none;
  border: 1px solid rgba(255,255,255,0.16);
  background: rgba(255,255,255,0.08);
  color:#fff !important;
  transition: transform .18s ease, box-shadow .18s ease, background .18s ease, border-color .18s ease, opacity .18s ease;
  backdrop-filter: blur(12px) saturate(140%);
  -webkit-backdrop-filter: blur(12px) saturate(140%);
}

.ad-btn:hover{
  transform: translateY(-1px);
  box-shadow: 0 12px 28px rgba(0,0,0,0.35);
  border-color: rgba(255,255,255,0.26);
  background: rgba(255,255,255,0.12);
}

.ad-btn:active{
  transform: translateY(0);
  box-shadow: none;
}

.ad-btn:focus-visible{
  outline: none;
  box-shadow: 0 0 0 3px rgba(140,200,255,0.35);
}

/* 수정 (프라이머리) */
.ad-btn--primary{
  background: linear-gradient(135deg, rgba(120,190,255,0.45), rgba(255,255,255,0.08));
  border-color: rgba(120,190,255,0.35);
}

/* 삭제 (위험) */
.ad-btn--danger{
  background: linear-gradient(135deg, rgba(255,90,120,0.35), rgba(255,255,255,0.06));
  border-color: rgba(255,90,120,0.35);
}

.ad-btn--danger:hover{
  background: linear-gradient(135deg, rgba(255,90,120,0.45), rgba(255,255,255,0.08));
}

/* 목록 (고스트) */
.ad-btn--ghost{
  background: rgba(255,255,255,0.06);
  border-color: rgba(255,255,255,0.14);
  opacity: .95;
}

/* 기존 템플릿 a:hover 오버라이드 방어 */
.action-wrap .ad-btn,
.action-wrap .ad-btn:visited,
.action-wrap .ad-btn:hover,
.action-wrap .ad-btn:focus,
.action-wrap .ad-btn:active{
  color:#fff !important;
  text-decoration:none !important;
}

/* 반응형 */
@media (max-width: 991px){
  .anime-detail-card{ padding: 28px; }
  .anime-info{ padding-left: 0; margin-top: 20px; }
  .anime-thumb{ max-width: 100%; }
  .action-wrap{ justify-content: flex-start; }
}
</style>
</head>

<body>

<c:set var="cp" value="${pageContext.request.contextPath}" />

<%@ include file="/common/header.jsp"%>

<section class="anime-detail-wrap">
  <div class="container">
    <div class="anime-detail-card">
      <div class="row anime-detail-row">

        <div class="col-lg-4">
          <div class="anime-thumb">
            <img src="${pageContext.request.contextPath}${animeData.animeThumbnailUrl}" alt="${animeData.animeTitle}">
          </div>
        </div>

        <div class="col-lg-8">
          <div class="anime-info">

            <div class="anime-title">${animeData.animeTitle}</div>

            <div class="meta-chips">
              <span class="meta-chip"><i class="fa fa-calendar"></i> 방영년도 ${animeData.animeYear}</span>
              <span class="meta-chip"><i class="fa fa-tag"></i> 방영분기 ${animeData.animeQuarter}</span>
            </div>

            <div class="anime-synopsis">
              ${animeData.animeStory}
            </div>

            <div class="action-wrap">

              <c:if test="${sessionScope.memberRole eq 'ADMIN'}">
                <a href="${cp}/animeEditPage.do?animeId=${animeData.animeId}"
                   class="ad-btn ad-btn--primary">
                  <i class="fa fa-pencil"></i> 수정
                </a>

                <a href="${cp}/animeDelete.do?animeId=${animeData.animeId}"
                   class="ad-btn ad-btn--danger"
                   onclick="return confirm('정말 삭제하시겠습니까?');">
                  <i class="fa fa-trash"></i> 삭제
                </a>
              </c:if>

              <a href="${cp}/animeList.do" class="ad-btn ad-btn--ghost">
                애니 전체페이지로 <i class="fa fa-angle-right"></i>
              </a>

            </div>

          </div>
        </div>

      </div>
    </div>
  </div>
</section>

<%@ include file="/common/footer.jsp"%>

<script src="${cp}/js/jquery-3.3.1.min.js"></script>
<script src="${cp}/js/bootstrap.min.js"></script>
<script src="${cp}/js/main.js"></script>

</body>
</html>
