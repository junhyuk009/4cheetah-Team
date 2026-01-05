<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<c:if test="${empty activeMenu}">
	<c:set var="activeMenu" value="HOME" />
</c:if>

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="description" content="Anime Template">
<meta name="keywords" content="Anime, unica, creative, html">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta http-equiv="X-UA-Compatible" content="ie=edge">
<title>ANIMale</title>

<link rel="icon" type="image/png" href="${ctx}/favicon.png">

<!-- Google Font -->
<link
	href="https://fonts.googleapis.com/css2?family=Oswald:wght@300;400;500;600;700&display=swap"
	rel="stylesheet">
<link
	href="https://fonts.googleapis.com/css2?family=Mulish:wght@300;400;500;600;700;800;900&display=swap"
	rel="stylesheet">

<!-- Css -->
<link rel="stylesheet" href="${ctx}/css/bootstrap.min.css"
	type="text/css">
<link rel="stylesheet" href="${ctx}/css/font-awesome.min.css"
	type="text/css">
<link rel="stylesheet" href="${ctx}/css/elegant-icons.css"
	type="text/css">
<link rel="stylesheet" href="${ctx}/css/plyr.css" type="text/css">
<link rel="stylesheet" href="${ctx}/css/nice-select.css" type="text/css">
<link rel="stylesheet" href="${ctx}/css/owl.carousel.min.css"
	type="text/css">
<link rel="stylesheet" href="${ctx}/css/slicknav.min.css"
	type="text/css">
<link rel="stylesheet" href="${ctx}/css/style.css" type="text/css">

<style>
/* 배너 높이 고정: 배경이미지/슬라이더가 안 보이는 문제 방지 */
.hero__slider .hero__items.set-bg {
	display: block;
	height: 520px;
	padding: 200px 0 40px;
}
/* a로 감싸도 템플릿처럼 보이게 */
.hero__items.set-bg {
	background-size: cover;
	background-position: center;
	background-repeat: no-repeat;
}
</style>
</head>

<body>

	<%-- Preloader --%>
	<div id="preloder">
		<div class="loader"></div>
	</div>

	<%-- 공통 헤더 --%>
	<jsp:include page="/common/header.jsp" />

	<!-- Hero Section Begin -->
	<section class="hero">
		<div class="container">
			<div class="hero__slider owl-carousel">

				<c:choose>
					<c:when test="${empty mainBannerNewsList}">
						<div class="hero__items set-bg"
							data-setbg="${ctx}/img/hero/hero-1.jpg">
							<div class="row">
								<div class="col-lg-6">
									<div class="hero__text">
										<div class="label">News</div>
										<h2>표시할 뉴스가 없습니다.</h2>
										<p>최신 뉴스가 등록되면 이 영역에 배너로 노출됩니다.</p>
										<a href="${ctx}/newsList.do" class="hero__ctaLink"> <span>뉴스
												보러가기</span> <i class="fa fa-angle-right"></i>
										</a>
									</div>
								</div>
							</div>
						</div>
					</c:when>

					<c:otherwise>
						<c:forEach var="n" items="${mainBannerNewsList}" varStatus="st">
							<c:if test="${st.count <= 3}">

								<%-- 썸네일 경로 (NEWS_THUMBNAIL_URL) --%>
								<c:set var="bannerImg" value="${n.newsThumbnailUrl}" />

								<%-- 없으면 기본 배너 --%>
								<c:if test="${empty bannerImg}">
									<c:set var="bannerImg" value="${ctx}/img/hero/hero-1.jpg" />
								</c:if>

								<%-- 경로 정규화: http / ctx / / / 상대경로 --%>
								<c:choose>
									<c:when test="${fn:startsWith(bannerImg,'http')}">
										<%-- 그대로 사용 --%>
									</c:when>

									<c:when test="${fn:startsWith(bannerImg, ctx)}">
										<%-- 이미 ctx 포함 --%>
									</c:when>

									<c:when test="${fn:startsWith(bannerImg,'/')}">
										<c:set var="bannerImg" value="${ctx}${bannerImg}" />
									</c:when>

									<c:otherwise>
										<c:set var="bannerImg" value="${ctx}/${bannerImg}" />
									</c:otherwise>
								</c:choose>

								<%-- 템플릿은 div지만 클릭 이동 위해 a로 감싸도 OK --%>
								<a href="${ctx}/newsDetail.do?newsId=${n.newsId}"
									class="hero__items set-bg" data-setbg="${bannerImg}"
									style="text-decoration: none;">
									<div class="row">
										<div class="col-lg-6">
											<div class="hero__text">
												<div class="label">News</div>
												<h2>
													<c:out value="${n.newsTitle}" />
												</h2>
												<p>자세히 보려면 클릭하세요.</p>

												<!-- CTA 버튼 (중첩 a 방지: span으로 처리) -->
												<div class="hero__cta-wrap">
													<span class="hero__cta"> 뉴스 보러가기 <i
														class="fa fa-angle-right"></i>
													</span>
												</div>
											</div>
										</div>
									</div>
								</a>

							</c:if>
						</c:forEach>
					</c:otherwise>
				</c:choose>

			</div>
		</div>
	</section>
	<!-- Hero Section End -->

	<!-- Product Section Begin -->
	<%-- 구분감 + 메인 전용 스타일 적용을 위해 home-anime-section 클래스 추가 --%>
	<section class="product spad home-anime-section">
		<div class="container">

			<%-- 제목 + View All 우측 정렬 --%>
			<div class="row">
				<div class="col-12">
					<div class="home-section-head">
						<div class="section-title">
							<h4>최신 애니 리스트</h4>
							<p class="section-sub">이번 분기 신작 · 인기작을 한 번에 확인하세요</p>
						</div>

						<a href="${ctx}/animeList.do" class="btn-viewall"> View All<span
							class="arrow_right"></span>
						</a>
					</div>
				</div>
			</div>

			<div class="row">
				<c:choose>
					<c:when test="${empty mainAnimeList}">
						<div class="col-lg-12">
							<p style="color: #999; margin-top: 10px;">표시할 애니가 없습니다.</p>
						</div>
					</c:when>

					<c:otherwise>
						<c:forEach var="a" items="${mainAnimeList}" varStatus="st">
							<c:if test="${st.count <= 12}">

								<c:set var="thumbPath" value="${a.animeThumbnailUrl}" />

								<%-- 없으면 샘플 --%>
								<c:if test="${empty thumbPath}">
									<c:set var="thumbPath"
										value="${ctx}/images/anisample/bleach.jpg" />
								</c:if>

								<%-- 경로 정규화 --%>
								<c:choose>
									<c:when test="${fn:startsWith(thumbPath,'http')}">
										<%-- 그대로 --%>
									</c:when>

									<c:when test="${fn:startsWith(thumbPath, ctx)}">
										<%-- ctx 포함 --%>
									</c:when>

									<c:when test="${fn:startsWith(thumbPath,'/')}">
										<c:set var="thumbPath" value="${ctx}${thumbPath}" />
									</c:when>

									<c:otherwise>
										<c:set var="thumbPath" value="${ctx}/${thumbPath}" />
									</c:otherwise>
								</c:choose>

								<div class="col-lg-3 col-md-4 col-sm-6">
									<div class="product__item">

										<a href="${ctx}/animeDetail.do?animeId=${a.animeId}"
											style="display: block;">
											<div class="product__item__pic set-bg"
												data-setbg="${thumbPath}"></div>
										</a>

										<div class="product__item__text">
											<%-- 메타 UI (년도/분기) 가독성 개선용 클래스 적용 --%>
											<ul class="anime-meta">
												<li><c:choose>
														<c:when test="${empty a.animeYear}">연도 미정</c:when>
														<c:otherwise>
															<c:out value="${a.animeYear}" />년</c:otherwise>
													</c:choose></li>
												<li class="badge-q"><c:choose>
														<c:when test="${empty a.animeQuarter}">분기 미정</c:when>
														<c:otherwise>
															<c:out value="${a.animeQuarter}" />
														</c:otherwise>
													</c:choose></li>
											</ul>

											<h5 class="anime-title" style="margin-bottom: 0;">
												<a href="${ctx}/animeDetail.do?animeId=${a.animeId}"
													style="display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden;">
													<c:out value="${a.animeTitle}" />
												</a>
											</h5>
										</div>

									</div>
								</div>

							</c:if>
						</c:forEach>
					</c:otherwise>
				</c:choose>
			</div>

		</div>
	</section>
	<!-- Product Section End -->

	<%-- 공통 푸터 --%>
	<jsp:include page="/common/footer.jsp" />

	<!-- Js Plugins (순서 중요) -->
	<script src="${ctx}/js/jquery-3.3.1.min.js"></script>
	<script src="${ctx}/js/bootstrap.min.js"></script>

	<script src="${ctx}/js/player.js"></script>
	<script src="${ctx}/js/jquery.nice-select.min.js"></script>
	<script src="${ctx}/js/mixitup.min.js"></script>
	<script src="${ctx}/js/jquery.slicknav.js"></script>
	<script src="${ctx}/js/owl.carousel.min.js"></script>

	<script src="${ctx}/js/main.js"></script>
	

	<!-- 안전장치: main.js가 중간에 멈춰도 hero 슬라이더는 강제 초기화 -->
	<script>
    (function () {
      if (!window.jQuery || !jQuery.fn || !jQuery.fn.owlCarousel) return;

      // set-bg 보정
      jQuery(".set-bg").each(function () {
        var bg = jQuery(this).data("setbg");
        if (bg) jQuery(this).css("background-image", "url(" + bg + ")");
      });

      // hero 슬라이더 보정 (중복 초기화 방지)
      var $slider = jQuery(".hero__slider");
      if ($slider.length && !$slider.hasClass("owl-loaded")) {
        $slider.owlCarousel({
          items: 1,
          loop: true,
          nav: true,
          dots: true,
          autoplay: true,
          autoplayTimeout: 5000,
          smartSpeed: 1200
        });
      }
    })();
  </script>

</body>
</html>
