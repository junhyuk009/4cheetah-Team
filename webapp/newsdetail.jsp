<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta http-equiv="X-UA-Compatible" content="ie=edge">
<title>ANIMale | 뉴스 상세</title>

<link rel="icon" type="image/png" href="${ctx}/favicon.png">

<link
	href="https://fonts.googleapis.com/css2?family=Oswald:wght@300;400;500;600;700&display=swap"
	rel="stylesheet">
<link
	href="https://fonts.googleapis.com/css2?family=Mulish:wght@300;400;500;600;700;800;900&display=swap"
	rel="stylesheet">

<!-- 상대경로 깨짐 방지 -->
<link rel="stylesheet" href="${ctx}/css/bootstrap.min.css" type="text/css">
<link rel="stylesheet" href="${ctx}/css/font-awesome.min.css" type="text/css">
<link rel="stylesheet" href="${ctx}/css/elegant-icons.css" type="text/css">
<link rel="stylesheet" href="${ctx}/css/plyr.css" type="text/css">
<link rel="stylesheet" href="${ctx}/css/nice-select.css" type="text/css">
<link rel="stylesheet" href="${ctx}/css/owl.carousel.min.css" type="text/css">
<link rel="stylesheet" href="${ctx}/css/slicknav.min.css" type="text/css">
<link rel="stylesheet" href="${ctx}/css/style.css">

<style>
/* News Detail */

.blog-details.spad{
  background: radial-gradient(1200px 520px at 30% 10%, rgba(70,90,255,.08), rgba(0,0,0,0)),
              radial-gradient(900px 420px at 85% 0%, rgba(180,120,255,.06), rgba(0,0,0,0));
}

.news-wrap { margin-top: 6px; }

.news-article{
  position: relative;
  border-radius: 22px;

  /* bd-card 톤 */
  background: rgba(255,255,255,0.035);
  border: 1px solid rgba(255,255,255,0.10);
  backdrop-filter: blur(18px) saturate(120%);
  -webkit-backdrop-filter: blur(18px) saturate(140%);
  box-shadow: 0 18px 60px rgba(0,0,0,0.45);

  overflow: hidden;
}

/* bd-card 대각 광택 */
.news-article::before{
  content:"";
  position:absolute;
  top:-55%;
  left:-35%;
  width:70%;
  height:110%;
  transform: rotate(18deg);
  background: radial-gradient(circle at 28% 28%,
    rgba(255,255,255,0.22),
    rgba(255,255,255,0.00) 62%);
  pointer-events:none;
  opacity:.28;
  filter: blur(4px);
  z-index:0;
}

/* bd-card 그라데이션 보더 */
.news-article::after{
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
  opacity:.65;
  z-index:0;
}

/* 카드 내용은 pseudo 위로 */
.news-article > *{
  position: relative;
  z-index: 1;
}

/* TOPBAR (상단 메뉴) = bd-topbar + btn-sm2 톤 */
.news-topbar{
  padding: 14px 16px;
  display:flex;
  justify-content:space-between;
  align-items:center;
  gap:12px;

  border-bottom: 1px solid rgba(255,255,255,.10);
  background: rgba(255,255,255,.03);
  backdrop-filter: blur(12px) saturate(120%);
  -webkit-backdrop-filter: blur(12px) saturate(140%);

  position: sticky;
  top: var(--news-sticky-top, 0px);
  z-index: 50;
}

.news-crumb{
  display:flex;
  align-items:center;
  gap:8px;
  font-size:14px;
  font-weight: 500;       
  letter-spacing: .2px; 
  color: rgba(255,255,255,.75);
}
.news-crumb a{
  color: rgba(255,255,255,.90);
  text-decoration:none;
}
.news-crumb a:hover{ color:#fff; }

.news-topbar-actions{
  display:flex;
  align-items:center;
  justify-content:flex-end;
  gap:10px;
  flex-wrap:wrap;
}

/* pill-btn => bd의 btn-sm2 톤으로 변경 */
.pill-btn{
  display:inline-flex;
  align-items:center;
  justify-content:center;
  gap:8px;
  padding: 10px 16px;
  border-radius: 999px;
  font-size: 14px;
  font-weight: 800;
  letter-spacing: -0.01em;
  line-height: 1;
  cursor:pointer;
  white-space: nowrap;
  user-select: none;
  text-decoration: none !important;

  border: 1px solid rgba(255,255,255,0.13);
  background: rgba(255,255,255,0.06);
  color:#fff !important;

  backdrop-filter: blur(12px) saturate(140%);
  -webkit-backdrop-filter: blur(12px) saturate(140%);

  transition: transform .18s ease, box-shadow .18s ease,
              background .18s ease, border-color .18s ease, opacity .18s ease;
}
.pill-btn:hover{
  transform: translateY(-1px);
  box-shadow: 0 12px 28px rgba(0,0,0,0.35);
  border-color: rgba(255,255,255,0.26);
  background: rgba(255,255,255,0.12);
}
.pill-btn:active{
  transform: translateY(0);
  box-shadow:none;
}
.pill-btn:focus-visible{
  outline: none;
  box-shadow: 0 0 0 3px rgba(140,200,255,0.35);
}

/* danger 버튼 = bd btn-danger2 톤 */
.pill-btn--danger{
  background: linear-gradient(135deg,
    rgba(255,90,120,0.35),
    rgba(255,255,255,0.06));
  border-color: rgba(255,90,120,0.35);
}
.pill-btn--danger:hover{
  background: linear-gradient(135deg,
    rgba(255,90,120,0.55),
    rgba(255,255,255,0.10));
  border-color: rgba(255,90,120,0.70);
}

.inline-form{ display:inline; margin:0; }

/* "뉴스 전체 목록" hover 시 파란색으로 변하는 문제 방지 */
.news-topbar-actions .pill-btn,
.news-topbar-actions .pill-btn:visited,
.news-topbar-actions .pill-btn:hover,
.news-topbar-actions .pill-btn:focus,
.news-topbar-actions .pill-btn:active{
  color:#fff !important;
  text-decoration:none !important;
}
.news-topbar-actions .pill-btn i{ color: inherit !important; }

/* COVER (썸네일) */
.news-cover{
  position: relative;
  min-height: 360px;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  isolation: isolate;
}
@media (max-width: 991px){
  .news-cover{ min-height: 290px; }
}

/* cover 오버레이 */
.news-cover::before{
  content:"";
  position:absolute;
  inset:0;
  background:
    radial-gradient(1100px 420px at 20% 10%, rgba(255,255,255,.10), rgba(0,0,0,0)),
    linear-gradient(180deg, rgba(0,0,0,.10), rgba(0,0,0,.78));
  z-index:0;
}
.news-cover::after{
  content:"";
  position:absolute;
  left:0; right:0; bottom:0;
  height:65%;
  background: linear-gradient(180deg, rgba(0,0,0,0), rgba(0,0,0,.78));
  z-index:0;
}

.news-cover__content{
  position:absolute;
  left:18px; right:18px; bottom:18px;
  z-index:1;
  display:flex;
  flex-direction:column;
  gap:8px;
  max-width:980px;
}
.news-cover__meta{
  font-size:13px;
  color: rgba(255,255,255,.80);
  letter-spacing:.2px;
}
.news-cover__title{
  margin:0;
  font-size:36px;
  line-height:1.18;
  color:#fff;
  font-weight:900;
  text-shadow: 0 10px 24px rgba(0,0,0,.45);
}
@media (max-width: 991px){
  .news-cover__title{ font-size: 26px; }
}

/* BODY (본문) - 추가 카드 없이 그대로 출력 */
.news-body{
  padding: 34px 18px 44px;
  background: linear-gradient(
    180deg,
    rgba(255,255,255,0.075),
    rgba(255,255,255,0.045)
  );
}

@media (max-width: 991px){
  .news-body{ padding: 26px 14px 34px; }
}

#newsContent.news-prose{
  padding: 0 10px;  
  max-width: 920px;
  margin: 0 auto;

  /* 내부 카드 제거 */
  padding: 0;
  border: 0;
  background: transparent;
  box-shadow: none;
  border-radius: 0;

  color: rgba(255,255,255,.92);
  font-size: 15.5px;
  line-height: 2.05;
  overflow-wrap: anywhere;
}

#newsContent.news-prose p,
#newsContent.news-prose p *{
  color: rgba(255,255,255,.92) !important;
}

/* 타이포 그림자 제거 */
#newsContent.news-prose,
#newsContent.news-prose *{
  text-shadow: none !important;
}



@media (max-width: 991px){
  #newsContent.news-prose{
    font-size: 15px;
    line-height: 2.0;
  }
}

/* 리드 문단 (첫 문단) */
#newsContent.news-prose p:first-of-type{
  font-size: 16.5px;
  line-height: 2.1;
  color: rgba(255,255,255,.95) !important;
}

#newsContent.news-prose p{ margin: 0 0 16px; }
#newsContent.news-prose p:last-child{
  margin-bottom: 10px !important;
}

/* 제목 */
#newsContent.news-prose h1,
#newsContent.news-prose h2,
#newsContent.news-prose h3{
  margin: 24px 0 12px;
  color:#fff;
  font-weight: 900;
  line-height: 1.25;
}
#newsContent.news-prose h4,
#newsContent.news-prose h5,
#newsContent.news-prose h6{
  margin: 18px 0 10px;
  color: rgba(255,255,255,.95);
  font-weight: 800;
}

/* hr */
#newsContent.news-prose hr{
  border:none;
  height:1px;
  margin: 22px 0;
  background: linear-gradient(90deg,
    rgba(255,255,255,0),
    rgba(255,255,255,.16),
    rgba(255,255,255,0)
  );
}

/* 링크 */
#newsContent.news-prose a{
  color:#fff;
  text-decoration: underline;
  text-underline-offset: 3px;
}
#newsContent.news-prose a:hover{ opacity:.9; }

/* 인용 */
#newsContent.news-prose blockquote{
  margin: 18px 0;
  padding: 12px 14px;
  border-radius: 12px;
  border-left: 3px solid rgba(255,255,255,.22);
  background: rgba(255,255,255,.05);
  color: rgba(255,255,255,.92);
}

/* 리스트 */
#newsContent.news-prose ul,
#newsContent.news-prose ol{ margin: 10px 0 16px 22px; }
#newsContent.news-prose li{
  margin: 6px 0;
  color: rgba(255,255,255,.92);
}

/* code/pre */
#newsContent.news-prose code{
  padding: 2px 6px;
  border-radius: 8px;
  background: rgba(0,0,0,.22);
  font-size: .95em;
}
#newsContent.news-prose pre{
  margin: 16px 0;
  padding: 14px 14px;
  border-radius: 14px;
  background: rgba(0,0,0,.25);
  overflow:auto;
}

/* CKEditor 이미지/figure (라운드 제거) */
#newsContent.news-prose img{
  max-width: 100%;
  height: auto;
  display: block;
  margin: 18px auto;

  border: 0;
  box-shadow: none;
  background: transparent;

  /* 직각 */
  border-radius: 0 !important;

  cursor: zoom-in;
}

#newsContent.news-prose figure{
  margin: 18px 0;
  padding: 0;
  border: 0;
  background: transparent;
}
#newsContent.news-prose figure.image{ padding:0; border:0; background:transparent; }

#newsContent.news-prose figure.image figcaption{
  margin-top: 10px;
  font-size: 12px;
  line-height: 1.6;
  color: rgba(255,255,255,.72);
  text-align: center;
  opacity: .95;
}

/* CKEditor 정렬 */
#newsContent.news-prose figure.image.image-style-align-left{
  float:left;
  max-width:46%;
  margin: 8px 16px 12px 0;
}
#newsContent.news-prose figure.image.image-style-align-right{
  float:right;
  max-width:46%;
  margin: 8px 0 12px 16px;
}
#newsContent.news-prose figure.image.image-style-align-center{
  margin-left:auto;
  margin-right:auto;
  max-width:92%;
}

/* float 정리 */
#newsContent.news-prose::after{
  content:"";
  display:block;
  clear:both;
}
@media (max-width: 991px){
  #newsContent.news-prose figure.image.image-style-align-left,
  #newsContent.news-prose figure.image.image-style-align-right{
    float:none;
    max-width:100%;
    margin: 14px 0;
  }
}

/* 테이블 */
#newsContent.news-prose table{
  width:100%;
  border-collapse: collapse;
  margin: 16px 0;
  overflow: hidden;
  border-radius: 12px;
  border: 1px solid rgba(255,255,255,.10);
  background: rgba(0,0,0,.10);
}
#newsContent.news-prose th,
#newsContent.news-prose td{
  padding: 10px 12px;
  border-bottom: 1px solid rgba(255,255,255,.08);
}
#newsContent.news-prose th{
  background: rgba(255,255,255,.06);
  color: rgba(255,255,255,.92);
}

/* RELATED (관련 애니) - bd 톤 */
.news-related{
  padding: 16px;
  border-top: 1px solid rgba(255,255,255,.10);
  background: rgba(255,255,255,.04);
  backdrop-filter: blur(12px) saturate(140%);
  -webkit-backdrop-filter: blur(12px) saturate(140%);
}

.related-anime-box{
  padding: 14px;
  border-radius: 16px;
  border: 1px solid rgba(255,255,255,.10);
  background: rgba(0,0,0,.20);
}

.news-related .meta{
  color: rgba(255,255,255,.75) !important;
  font-size: 12px;
}

/* 읽기 진행바 */
.reading-progress{
  position: fixed;
  left: 0;
  right: 0;
  top: var(--news-sticky-top, 0px);
  height: 3px;
  z-index: 9999;
  pointer-events: none;
}
.reading-progress__bar{
  height: 100%;
  width: 100%;
  transform-origin: 0 50%;
  transform: scaleX(0);
  background: rgba(255,255,255,.72);
  box-shadow: 0 6px 16px rgba(0,0,0,.25);
}
@media (prefers-reduced-motion: reduce){
  .pill-btn{ transition: none; }
}

/* 이미지 라이트박스 */
.img-lightbox{
  position: fixed;
  inset: 0;
  z-index: 10000;
  display: none;
  align-items: center;
  justify-content: center;
  padding: 18px;
  background: rgba(0,0,0,.72);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}
.img-lightbox.is-open{ display: flex; }

.img-lightbox__panel{
  max-width: min(980px, 92vw);
  max-height: 86vh;
  width: 100%;
  display: grid;
  gap: 10px;
}
.img-lightbox__img{
  width: 100%;
  max-height: 78vh;
  object-fit: contain;

  /* 직각 */
  border-radius: 0 !important;

  background: rgba(0,0,0,.20);
}
.img-lightbox__cap{
  font-size: 12px;
  line-height: 1.6;
  color: rgba(255,255,255,.78);
  text-align: center;
}
.img-lightbox__close{
  position: absolute;
  top: calc(var(--news-sticky-top, 0px) + 10px);
  right: 12px;
  border: 1px solid rgba(255,255,255,.18);
  background: rgba(0,0,0,.35);
  color: #fff;
  border-radius: 999px;
  padding: 8px 12px;
  cursor: pointer;
}
.img-lightbox__close:hover{
  background: rgba(255,255,255,.10);
  border-color: rgba(255,255,255,.28);
}
</style>

</head>

<body>
	<div id="preloder">
		<div class="loader"></div>
	</div>

	<jsp:include page="/common/header.jsp" />

	<section class="blog-details spad">
		<div class="container news-wrap">
			<div class="row d-flex justify-content-center">
				<div class="col-lg-10">

					<%-- 목록 복귀 URL (검색/페이지 유지) --%>
					<c:set var="backUrl" value="newsList.do" />
					<c:if test="${not empty page or not empty condition or not empty keyword}">
						<c:set var="backUrl" value="newsList.do?page=${page}" />
						<c:if test="${not empty condition}">
							<c:set var="backUrl" value="${backUrl}&condition=${condition}" />
						</c:if>
						<c:if test="${not empty keyword}">
							<c:set var="backUrl" value="${backUrl}&keyword=${fn:escapeXml(keyword)}" />
						</c:if>
					</c:if>

					<%-- 썸네일 URL 보정 (커버는 무조건 썸네일) --%>
					<c:set var="thumbRaw" value="${newsData.newsThumbnailUrl}" />
					<c:set var="thumbSrc" value="${thumbRaw}" />
					<c:if test="${not empty thumbRaw}">
						<c:choose>
							<c:when test="${fn:startsWith(thumbRaw,'http')}">
								<c:set var="thumbSrc" value="${thumbRaw}" />
							</c:when>

							<%-- DB값이 이미 /ANIMale/... 형태면 그대로 사용 --%>
							<c:when test="${fn:startsWith(thumbRaw, ctx)}">
								<c:set var="thumbSrc" value="${thumbRaw}" />
							</c:when>

							<c:when test="${fn:startsWith(thumbRaw,'/')}">
								<c:set var="thumbSrc" value="${ctx}${thumbRaw}" />
							</c:when>

							<c:otherwise>
								<c:set var="thumbSrc" value="${ctx}/${thumbRaw}" />
							</c:otherwise>
						</c:choose>
					</c:if>

					<%-- 커버는 무조건 썸네일 --%>
					<c:set var="coverSrc" value="${thumbSrc}" />

					<div class="news-article">

						<!-- TOPBAR (우측 상단: 목록/수정/삭제) -->
						<div class="news-topbar">
							<div class="news-crumb">
								<a href="${backUrl}">NEWS</a>
								<span style="opacity: .6;">›</span>
								<span>DETAIL</span>
							</div>

							<div class="news-topbar-actions">
								<a class="pill-btn" href="${backUrl}">
									<i class="fa fa-list"></i> 뉴스 전체 목록
								</a>

								<c:if test="${sessionScope.memberRole eq 'ADMIN'}">
									<form action="newsEditPage.do" method="get" class="inline-form">
										<input type="hidden" name="newsId" value="${newsData.newsId}">
										<input type="hidden" name="type" value="NEWS">
										<button type="submit" class="pill-btn">
											<i class="fa fa-pencil"></i> 수정
										</button>
									</form>

									<form id="deleteForm" action="newsDelete.do" method="post" class="inline-form">
										<input type="hidden" name="newsId" value="${newsData.newsId}">
										<input type="hidden" name="type" value="NEWS">
										<button type="button" class="pill-btn pill-btn--danger" onclick="confirmDelete();">
											<i class="fa fa-trash"></i> 삭제
										</button>
									</form>
								</c:if>
							</div>
						</div>

						<!-- COVER HERO -->
						<div class="news-cover"
							style="<c:if test='${not empty coverSrc}'>background-image:url('<c:out value='${coverSrc}'/>');</c:if>">
							<div class="news-cover__content">
								<div class="news-cover__meta">News - No.${newsData.newsId}</div>
								<h1 class="news-cover__title">${newsData.newsTitle}</h1>
							</div>
						</div>

						<!-- BODY: 본문은 CKEditor HTML 그대로 (이미지/텍스트 섞여 출력) -->
						<div class="news-body">
							<div id="newsContent" class="news-prose" data-cover="<c:out value='${coverSrc}'/>">
								<c:out value="${newsData.newsContent}" escapeXml="false" />
							</div>
						</div>

						<!-- RELATED -->
						<div class="news-related">
							<c:choose>
								<c:when test="${not empty newsData.animeId and newsData.animeId > 0}">
									<div class="related-anime-box">
										<div style="display: flex; justify-content: space-between; align-items: center; gap: 12px; flex-wrap: wrap;">
											<div>
												<div class="meta" style="margin-bottom: 6px;">관련 애니</div>
												<a href="animeDetail.do?animeId=${newsData.animeId}"
													style="font-weight: 800; font-size: 16px; color: #fff; text-decoration: underline;">
													<c:out value="${newsData.animeTitle}" default="관련 애니 보기" />
												</a>
												<c:if test="${not empty newsData.animeYear}">
													<span class="meta" style="margin-left: 10px;">
														(<c:out value="${newsData.animeYear}" /> / <c:out value="${newsData.animeQuarter}" />)
													</span>
												</c:if>
											</div>

											<c:set var="aThumbRaw" value="${newsData.animeThumbnailUrl}" />
											<c:set var="aThumbSrc" value="" />
											<c:if test="${not empty aThumbRaw}">
												<c:choose>
													<c:when test="${fn:startsWith(aThumbRaw,'http')}">
														<c:set var="aThumbSrc" value="${aThumbRaw}" />
													</c:when>
													<c:when test="${fn:startsWith(aThumbRaw,'/')}">
														<c:set var="aThumbSrc" value="${ctx}${aThumbRaw}" />
													</c:when>
													<c:otherwise>
														<c:set var="aThumbSrc" value="${ctx}/${aThumbRaw}" />
													</c:otherwise>
												</c:choose>
											</c:if>

											<c:if test="${not empty aThumbSrc}">
												<a href="animeDetail.do?animeId=${newsData.animeId}">
													<img src="${aThumbSrc}" alt="관련 애니 썸네일"
														style="width: 64px; height: 64px; object-fit: cover; border-radius: 12px; border: 1px solid rgba(255, 255, 255, .12);"
														onerror="this.style.display='none';">
												</a>
											</c:if>
										</div>
									</div>
								</c:when>
								<c:otherwise>
									<div class="meta">관련 애니 정보가 없습니다.</div>
								</c:otherwise>
							</c:choose>
						</div>

					</div>
					<!-- /news-article -->

				</div>
			</div>
		</div>
	</section>

	<%@ include file="/common/footer.jsp"%>

	<script src="${ctx}/js/jquery-3.3.1.min.js"></script>
	<script src="${ctx}/js/bootstrap.min.js"></script>
	<script src="${ctx}/js/player.js"></script>
	<script src="${ctx}/js/jquery.nice-select.min.js"></script>
	<script src="${ctx}/js/mixitup.min.js"></script>
	<script src="${ctx}/js/jquery.slicknav.js"></script>
	<script src="${ctx}/js/owl.carousel.min.js"></script>
	<script src="${ctx}/js/main.js"></script>

<script>
document.addEventListener("DOMContentLoaded", function () {
  const box = document.getElementById("newsContent");
  if (!box) return;

  const ctx = "<c:out value='${ctx}'/>";
  const cover = (box.dataset.cover || "").trim();

  // sticky top offset 계산
  function computeStickyTop(){
    const header = document.querySelector(".header") || document.querySelector("header");
    if (!header) return 0;

    const cs = window.getComputedStyle(header);
    const isFixed = cs.position === "fixed";
    if (!isFixed) return 0;

    return Math.round(header.getBoundingClientRect().height);
  }

  function setStickyTopVar(){
    const top = computeStickyTop();
    document.documentElement.style.setProperty("--news-sticky-top", top + "px");
    return top;
  }

  let headerOffset = setStickyTopVar();

  // 1) 본문 img src 경로 보정
  box.querySelectorAll("img").forEach(img => {
    const raw = (img.getAttribute("src") || "").trim();
    if (!raw) return;

    if (/^https?:\/\//i.test(raw)) return;
    if (raw.startsWith(ctx + "/")) return;

    if (raw.startsWith("/upload/")) {
      img.setAttribute("src", ctx + raw);
      return;
    }
    if (raw.startsWith("upload/")) {
      img.setAttribute("src", ctx + "/" + raw);
      return;
    }
  });

  // 2) 커버 중복 제거: 이미지 2장 이상일 때만
  if (cover) {
    const norm = (u) => {
      try { return new URL(u, window.location.href).pathname; }
      catch (e) { return (u || "").split("?")[0]; }
    };

    const coverPath = norm(cover);
    const imgs = Array.from(box.querySelectorAll("img"));

    if (imgs.length > 1) {
      imgs.forEach(img => {
        const src = (img.getAttribute("src") || "").trim();
        if (!src) return;
        if (norm(src) === coverPath) {
          const fig = img.closest ? img.closest("figure") : null;
          (fig || img).remove();
        }
      });
    }
  }

  // 3) 이미지 라이트박스
  const lightbox = (function createLightbox(){
    const el = document.createElement("div");
    el.className = "img-lightbox";
    el.setAttribute("aria-hidden", "true");
    el.innerHTML = `
      <button type="button" class="img-lightbox__close" aria-label="닫기 (ESC)">닫기 ✕</button>
      <div class="img-lightbox__panel" role="dialog" aria-modal="true">
        <img class="img-lightbox__img" alt="">
        <div class="img-lightbox__cap"></div>
      </div>
    `;
    document.body.appendChild(el);

    const imgEl = el.querySelector(".img-lightbox__img");
    const capEl = el.querySelector(".img-lightbox__cap");
    const closeBtn = el.querySelector(".img-lightbox__close");

    function open(src, caption){
      imgEl.src = src;
      capEl.textContent = caption || "";
      el.classList.add("is-open");
      el.setAttribute("aria-hidden", "false");
      document.documentElement.style.overflow = "hidden";
    }

    function close(){
      el.classList.remove("is-open");
      el.setAttribute("aria-hidden", "true");
      imgEl.src = "";
      capEl.textContent = "";
      document.documentElement.style.overflow = "";
    }

    closeBtn.addEventListener("click", close);

    el.addEventListener("click", (e) => {
      if (e.target === el) close();
    });

    document.addEventListener("keydown", (e) => {
      if (e.key === "Escape" && el.classList.contains("is-open")) close();
    });

    return { open, close };
  })();

  box.addEventListener("click", (e) => {
    const img = e.target && e.target.tagName === "IMG" ? e.target : null;
    if (!img) return;

    const src = (img.getAttribute("src") || "").trim();
    if (!src) return;

    let caption = "";
    const fig = img.closest ? img.closest("figure") : null;
    if (fig) {
      const fc = fig.querySelector("figcaption");
      if (fc && fc.textContent) caption = fc.textContent.trim();
    }
    if (!caption) caption = (img.getAttribute("alt") || "").trim();

    lightbox.open(src, caption);
  });

  // 4) 읽기 진행바
  const progress = (function createProgress(){
    let wrap = document.querySelector(".reading-progress");
    if (!wrap) {
      wrap = document.createElement("div");
      wrap.className = "reading-progress";
      wrap.innerHTML = `<div class="reading-progress__bar"></div>`;
      document.body.appendChild(wrap);
    }
    const bar = wrap.querySelector(".reading-progress__bar");

    function clamp(n, min, max){ return Math.max(min, Math.min(max, n)); }

    function update(){
      headerOffset = setStickyTopVar();

      const rect = box.getBoundingClientRect();
      const boxTop = window.pageYOffset + rect.top;
      const boxHeight = box.offsetHeight;

      const start = boxTop - headerOffset - 12;
      const end = (boxTop + boxHeight) - headerOffset - window.innerHeight + 12;

      let p = 0;
      if (end <= start) p = 1;
      else p = clamp((window.pageYOffset - start) / (end - start), 0, 1);

      bar.style.transform = `scaleX(${p})`;
    }

    let ticking = false;
    function onScroll(){
      if (ticking) return;
      ticking = true;
      requestAnimationFrame(() => {
        ticking = false;
        update();
      });
    }

    window.addEventListener("scroll", onScroll, { passive: true });
    window.addEventListener("resize", () => { update(); }, { passive: true });

    update();
    return { update };
  })();

  window.addEventListener("load", () => progress.update(), { passive: true });
});

// 삭제 confirm
function confirmDelete() {
  if (confirm("정말로 이 뉴스를 삭제하시겠습니까? 삭제 후 복구할 수 없습니다.")) {
    document.getElementById("deleteForm").submit();
  }
}
</script>
</body>
</html>
