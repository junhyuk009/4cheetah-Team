<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html>
<html lang="ko">

<head>
<meta charset="UTF-8">
<title>AniMale | 애니리스트</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<link rel="icon" type="image/png"
	href="<%=request.getContextPath()%>/favicon.png">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/elegant-icons.css">

<!-- Google Font -->
<link
	href="https://fonts.googleapis.com/css2?family=Oswald:wght@300;400;500;600;700&display=swap"
	rel="stylesheet">
<link
	href="https://fonts.googleapis.com/css2?family=Mulish:wght@300;400;500;600;700;800;900&display=swap"
	rel="stylesheet">

<!-- CSS -->
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/css/bootstrap.min.css">
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/css/font-awesome.min.css">
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/css/style.css">

<style>

.product__page__filter select option{
  background: #0b0c2a;
  color: #fff;
}

.product__page__filter.sort-box{
  display: flex;
  align-items: center;
  justify-content: flex-end;
  margin-top: 10px;
}

.product__page__filter p{
  margin: 0 8px 0 0;
  font-size: 13px;
  color: #aaa;
}

.sort-selected{
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  border-radius: 20px;
  border: 1px solid #2a2c5a;
  font-size: 13px;
  cursor: pointer;
}

/* 검색라인 배치(레이아웃)만 유지 */
.anime-search-wrapper{
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
}

/* 페이지네이션 */
.anime-pagination{
  display: flex;
  justify-content: center;
  gap: 12px;
  margin-top: 40px;
}

.anime-pagination li{
  list-style: none;
}

.anime-pagination li a{
  min-width: 38px;
  height: 38px;
  padding: 0 14px;
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.4);
  background: rgba(255, 255, 255, 0.12);
  color: #fff;
  font-size: 14px;
  font-weight: 500;
  display: flex;
  align-items: center;
  justify-content: center;
  text-decoration: none;
  cursor: pointer;
  transition: all 0.2s ease;
}

.anime-pagination li a:hover{
  background: rgba(255, 255, 255, 0.22);
}

.anime-pagination li.active a{
  background: rgba(255, 255, 255, 0.35);
  border-color: rgba(255, 255, 255, 0.8);
  font-weight: 600;
}

.pagination-wrapper{
  position: relative;
  z-index: 50;
}

.anime-pagination{
  position: relative;
  z-index: 51;
}

/* =========================
   Anime List Page - Main 카드 느낌 적용
========================= */

/* 링크 파란색/visited 방지 */
.anime-list-page a.anime-link,
.anime-list-page a.anime-link:hover,
.anime-list-page a.anime-link:focus,
.anime-list-page a.anime-link:visited{
  color: inherit;
  text-decoration: none;
  outline: none;
}

/* 카드 진입 애니메이션 */
@keyframes animeCardIn{
  from { opacity: 0; transform: translateY(10px); }
  to   { opacity: 1; transform: translateY(0); }
}
.anime-list-page .anime-card{
  animation: animeCardIn .35s ease both;
}

/* 카드 스타일 */
.anime-list-page .product__item{
  background: rgba(255,255,255,0.03);
  border: 1px solid rgba(255,255,255,0.08);
  border-radius: 14px;
  padding: 12px 12px 16px;
  margin-bottom: 28px;
  transition: transform .2s ease, box-shadow .2s ease, border-color .2s ease;
}

.anime-list-page .product__item:hover{
  transform: translateY(-4px);
  border-color: rgba(229,54,55,0.35);
  box-shadow: 0 18px 42px rgba(0,0,0,0.38);
}

/* 포스터 영역 (템플릿과 통일: contain + 어두운 배경) */
.anime-list-page .product__item__pic{
  height: 330px;
  border-radius: 12px;
  background-size: contain;
  background-position: center;
  background-repeat: no-repeat;
  background-color: #070720;
}

/* 텍스트 영역 */
.anime-list-page .product__item__text{
  padding-top: 14px;
  text-align: left;
}

/* 메타 pill (년도/분기) */
.anime-list-page .anime-meta{
  display: flex;
  gap: 8px;
  margin: 0 0 10px;
}
.anime-list-page .anime-meta li{
  font-size: 12px;
  font-weight: 800;
  color: rgba(255,255,255,0.85);
  background: rgba(255,255,255,0.06);
  border: 1px solid rgba(255,255,255,0.10);
  padding: 4px 10px;
  border-radius: 999px;
}
.anime-list-page .anime-meta li.badge-q{
  color: #ffffff;
  background: rgba(229,54,55,0.16);
  border-color: rgba(229,54,55,0.35);
}

/* 타이틀 */
.anime-list-page .anime-title{
  margin: 0;
  font-size: 15px;
  font-weight: 900;
  line-height: 1.35;
  letter-spacing: 0.2px;
  opacity: 0.95;

  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.anime-list-page .product__item:hover .anime-title{
  color: #e53637;
  opacity: 1;
}
</style>
</head>

<body>

	<jsp:include page="/common/header.jsp" />

	<section class="product-page spad anime-list-page">
		<div class="container">

			<!-- 제목 + 검색 + 정렬 -->
			<div class="product__page__title">
				<div class="row align-items-center">
					<div class="col-lg-8">
						<div class="section-title">
							<h4>애니</h4>
						</div>
					</div>

					<div class="col-lg-4">

						<!-- 검색 + 전체보기 -->
						<div class="anime-search-wrapper">
							<div class="anime-search-box">
								<select id="animeSearchType">
									<option value="ANIME_SEARCH_TITLE">제목</option>
									<option value="ANIME_SEARCH_STORY">줄거리</option>
								</select>
								<input type="text" id="animeSearchInput" placeholder="검색어를 입력하세요">
								<button type="button" id="animeSearchBtn">
									<i class="fa fa-search"></i>
								</button>
							</div>

							<button type="button" id="animeResetBtn" class="anime-reset-btn">전체보기</button>

							<c:if test="${fn:toUpperCase(sessionScope.memberRole) eq 'ADMIN'}">
								<a class="anime-admin-btn"
								   href="${pageContext.request.contextPath}/animeWritePage.do">애니 추가</a>
							</c:if>
						</div>

						<!-- 정렬 -->
						<div class="product__page__filter sort-box">
							<p>정렬 기준</p>
							<div class="sort-selected" id="sortToggle">
								<span id="sortText">최신 등록순</span> <i class="fa fa-angle-down"></i>
							</div>

							<ul class="sort-list" style="display: none;">
								<li data-sort="RECENT">최신 등록순</li>
								<li data-sort="OLDEST">오래된 순</li>
								<li data-sort="TITLE">제목 가나다순</li>
							</ul>
						</div>

					</div>
				</div>
			</div>

			<!-- 카드 리스트 -->
			<div class="row" id="animeContainer"></div>

			<!-- 검색 결과 없음 -->
			<div class="search-result-wrapper" id="searchEmpty" style="display: none;">
				<div class="search-empty">검색 결과가 없습니다.</div>
			</div>

			<!-- 페이지네이션 -->
			<div class="row">
				<div class="col-lg-12">
					<div class="pagination-wrapper">
						<ul class="anime-pagination" id="pagingArea"></ul>
					</div>
				</div>
			</div>

		</div>
	</section>

	<%@ include file="/common/footer.jsp"%>

	<script src="<%=request.getContextPath()%>/js/jquery-3.3.1.min.js"></script>
	<script src="<%=request.getContextPath()%>/js/bootstrap.min.js"></script>
	<script src="<%=request.getContextPath()%>/js/main.js"></script>

	<script>
const contextPath = "<%=request.getContextPath()%>";
let currentSort = "RECENT";

let condition = "ANIME_LIST_RECENT"; // 전체 목록 고정
let keyword = null;                // 검색 아님

$(function () {
	loadAnimeList(1);

	$("#sortToggle").on("click", function (e) {
		e.stopPropagation();
		$(".sort-list").toggle();
	});

	$(".sort-list").on("click", "li", function () {
		currentSort = $(this).data("sort");
		$("#sortText").text($(this).text());
		$(".sort-list").hide();
		loadAnimeList(1);
	});

	$(document).on("click", function () {
		$(".sort-list").hide();
	});

	/* 검색 버튼 / 엔터 처리 */
	$("#animeSearchBtn").on("click", function () {
		const value = $("#animeSearchInput").val().trim();
		const searchType = $("#animeSearchType").val();

		if (value === "") {
			keyword = null;
			condition = "ANIME_LIST_RECENT";
		} else {
			keyword = value;
			condition = searchType;
		}

		loadAnimeList(1);
	});

	$("#animeSearchInput").on("keydown", function (e) {
		if (e.key === "Enter") {
			$("#animeSearchBtn").click();
		}
	});

	/* 전체보기 버튼 */
	$("#animeResetBtn").on("click", function () {
		$("#animeSearchInput").val("");
		$("#animeSearchType").val("ANIME_SEARCH_TITLE");

		keyword = null;
		condition = "ANIME_LIST_RECENT";
		currentSort = "RECENT";

		$("#sortText").text("최신 등록순");

		loadAnimeList(1);
	});
});

function loadAnimeList(page) {
	$.ajax({
		url: contextPath + "/AnimeListData.do",
		type: "get",
		dataType: "json",
		data: {
		    page: page,
		    condition: condition,
		    keyword: keyword,
		    sort: currentSort
		},
		success: function (res) {
			renderAnimeList(res.animeList);
			renderPaging(res.paging);
		},
		error: function (xhr) {
			console.log("[에러] AnimeListData:", xhr);
		}
	});
}

function renderAnimeList(list) {
  const $container = $("#animeContainer");
  $container.empty();

  if (!list || list.length === 0) {
    $("#searchEmpty").show();
    return;
  }
  $("#searchEmpty").hide();

  list.forEach((item, idx) => {
    if (!item.animeThumbnailUrl) return;

    const raw = item.animeThumbnailUrl;
    const thumbUrl = raw.startsWith("http")
      ? raw
      : (raw.startsWith("/") ? (contextPath + raw) : (contextPath + "/" + raw));

    const yearText = item.animeYear ? (item.animeYear + "년") : "연도 미정";
    const quarterText = item.animeQuarter ? item.animeQuarter : "분기 미정";

    const delay = Math.min(idx * 35, 350);

    const html =
      '<div class="col-lg-3 col-md-4 col-sm-6 anime-card" style="animation-delay:' + delay + 'ms">' +
        '<a href="' + contextPath + '/animeDetail.do?animeId=' + item.animeId + '" class="anime-link">' +
          '<div class="product__item">' +
            '<div class="product__item__pic set-bg" data-setbg="' + thumbUrl + '"></div>' +
            '<div class="product__item__text">' +
              '<ul class="anime-meta">' +
                '<li>' + yearText + '</li>' +
                '<li class="badge-q">' + quarterText + '</li>' +
              '</ul>' +
              '<h5 class="anime-title">' + item.animeTitle + '</h5>' +
            '</div>' +
          '</div>' +
        '</a>' +
      '</div>';

    $container.append(html);
  });

  $(".set-bg").each(function () {
    const bg = $(this).data("setbg");
    if (bg) $(this).css("background-image", "url('" + bg + "')");
  });
}

function renderPaging(p) {
    const $paging = $("#pagingArea");
    $paging.empty();

    if (!p) return;

    if (p.totalPage <= 1) {
        return;
    }

    if (p.hasPrev) {
        $paging.append(
            '<li class="arrow">' +
                '<a href="javascript:loadAnimeList(' + (p.startPage - 1) + ')">&lt;</a>' +
            '</li>'
        );
    }

    for (let i = p.startPage; i <= p.endPage; i++) {
        const active = (i === p.page) ? "active" : "";
        $paging.append(
            '<li class="' + active + '">' +
                '<a href="javascript:loadAnimeList(' + i + ')">' + i + '</a>' +
            '</li>'
        );
    }

    if (p.hasNext) {
        $paging.append(
            '<li class="arrow">' +
                '<a href="javascript:loadAnimeList(' + (p.endPage + 1) + ')">&gt;</a>' +
            '</li>'
        );
    }
}
</script>

</body>
</html>
