<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>AniMale | 뉴스</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<c:set var="ctx" value="${pageContext.request.contextPath}" />
<link rel="icon" type="image/png" href="${ctx}/favicon.png">

<!-- 템플릿 CSS -->
<link rel="stylesheet" href="${ctx}/css/bootstrap.min.css">
<link rel="stylesheet" href="${ctx}/css/font-awesome.min.css">
<link rel="stylesheet" href="${ctx}/css/elegant-icons.css">
<link rel="stylesheet" href="${ctx}/css/plyr.css">
<link rel="stylesheet" href="${ctx}/css/nice-select.css">
<link rel="stylesheet" href="${ctx}/css/owl.carousel.min.css">
<link rel="stylesheet" href="${ctx}/css/slicknav.min.css">
<link rel="stylesheet" href="${ctx}/css/style.css">

<style>
/* NEWS Page */

/* -10px 마진으로 생기는 미세 어긋남 방지: 거터 통일 */
.blog .row{ margin-left:-6px; margin-right:-6px; }
.blog [class*="col-"]{ padding-left:6px; padding-right:6px; }

/* 카드(큰) / 카드(작은) 높이 */
.blog__item{
  height:580px;
  display:block;
  position:relative;

  margin:0 0 12px 0 !important;
  border-radius:14px;
  overflow:hidden;

  /* border 제거 → inset 테두리로 교체 */
  border: none !important;
  box-shadow: inset 0 0 0 1px rgba(255,255,255,0.08);

  background-color: rgba(255,255,255,0.03);
  background-clip: padding-box;
  transform: translateZ(0);

  text-decoration:none !important;
  color:inherit !important;

  transition: transform .18s ease, box-shadow .18s ease, border-color .18s ease;
}

.blog__item.small__item{ height:285px; }

/* 이미지가 너무 쩅할 때: 얇은 톤 오버레이
   - 더 밝게: 0.10 → 0.06~0.08
   - 더 죽이기: 0.10 → 0.14~0.18 */
.blog__item::before{
  content:"";
  position:absolute;
  inset:0;
  background: rgba(11, 12, 42, 0.10); /* 톤 조절 포인트 */
  z-index:1;
  pointer-events:none;
}

/* 가독성용 하단 그라데이션 */
.blog__item::after{
  content:"";
  position:absolute;
  inset:0;
  background: linear-gradient(
    180deg,
    rgba(0,0,0,0.00) 30%,
    rgba(0,0,0,0.45) 65%,
    rgba(0,0,0,0.78) 100%
  );
  z-index:2;
  pointer-events:none;
}

.blog__item::before,
.blog__item::after{
  border-radius: inherit;
}

/* hover */
.blog__item:hover{
  transform: translateY(-3px);
  box-shadow:
    inset 0 0 0 1px rgba(229,54,55,0.22),
    0 18px 42px rgba(0,0,0,0.38);
}

.blog__item:hover::before{
  background: rgba(11, 12, 42, 0.40);
}

/* 텍스트 */
.blog__item__text{
  position:absolute;
  left:0;
  bottom:25px;
  width:100%;
  text-align:center;
  padding:0 105px;
  z-index:3;
}
.blog__item.small__item .blog__item__text{ padding:0 30px; }

.blog__item__text h4{
  margin:0;
  color:#fff;
  text-shadow: 0 10px 30px rgba(0,0,0,0.55);
  font-weight:900;
  letter-spacing:0.2px;

  display:-webkit-box;
  -webkit-box-orient:vertical;
  overflow:hidden;
  -webkit-line-clamp:2;
}
.blog__item:not(.small__item) .blog__item__text h4{
  -webkit-line-clamp:3;
}

/* NEWS 배지 (빨간 pill) + 글자 흰색 */
.blog__item__text::before{
  content:"NEWS";
  display:inline-flex;
  align-items:center;
  padding:5px 12px;
  border-radius:999px;
  background: rgba(229,54,55,0.92);
  color:#fff !important; /* 흰색 */
  font-size:11px;
  font-weight:900;
  letter-spacing:0.6px;
  margin-bottom:12px;
}

/* 검색 UI */
.news-search-wrap{ margin-bottom:22px; }
.news-search-row{
  display:flex;
  justify-content:flex-end;
  align-items:center;
  gap:10px;
  flex-wrap:wrap;
}


@media (max-width: 991px){
  .news-search-row{ justify-content:flex-start; }
}

/* 페이지네이션 */
.product__pagination{
  margin-top:25px;
  text-align:center;
}
.product__pagination a{
  display:inline-flex;
  align-items:center;
  justify-content:center;
  min-width:44px;
  height:44px;
  padding:0 14px;
  margin:0 4px 8px 4px;
  border-radius:10px;
  border:1px solid rgba(255,255,255,0.10);
  background: rgba(11,12,42,0.55);
  color: rgba(255,255,255,0.85);
  transition: all .15s ease;
  text-decoration:none !important;
}
.product__pagination a:hover{
  transform: translateY(-1px);
  background: rgba(255,255,255,0.08);
  color:#fff;
}
.product__pagination a.current-page{
  background: rgba(255,255,255,0.18);
  border-color: rgba(255,255,255,0.25);
  color:#fff;
  font-weight:900;
}


</style>
</head>

<body>

  <jsp:include page="/common/header.jsp" />

  <c:set var="isSearch" value="${not empty keyword}" />
  <c:set var="nlen" value="${empty newsList ? 0 : newsList.size()}" />

  <section class="normal-breadcrumb set-bg" data-setbg="${ctx}/img/normal-breadcrumb.jpg">
    <div class="container">
      <div class="row">
        <div class="col-lg-12 text-center">
          <div class="normal__breadcrumb__text">
            <h2>News</h2>
            <p>지금 뜨는 애니 뉴스, 빠르게 모아보기</p>
          </div>
        </div>
      </div>
    </div>
  </section>

  <section class="blog spad">
    <div class="container">

      <%-- 검색 폼 --%>
      <div class="news-search-wrap">
        <form action="${ctx}/newsList.do" method="get">
          <div class="news-search-row">

            <div class="news-search-box">
              <select name="condition">
                <option value="NEWS_SEARCH_TITLE" <c:if test="${condition eq 'NEWS_SEARCH_TITLE'}">selected</c:if>>제목</option>
                <option value="NEWS_SEARCH_CONTENT" <c:if test="${condition eq 'NEWS_SEARCH_CONTENT'}">selected</c:if>>내용</option>
              </select>

              <input type="text" name="keyword" value="<c:out value='${keyword}'/>" placeholder="검색어를 입력하세요">

              <button type="submit" class="news-search-btn" aria-label="검색">
                <i class="fa fa-search"></i>
              </button>
            </div>

            <c:if test="${isSearch}">
              <a href="${ctx}/newsList.do" class="news-reset-btn">전체보기</a>
            </c:if>

            <c:if test="${not empty sessionScope.memberId and fn:toUpperCase(sessionScope.memberRole) eq 'ADMIN'}">
              <a href="${ctx}/boardWritePage.do?type=NEWS" class="news-admin-btn">뉴스 작성</a>
            </c:if>

          </div>
        </form>
      </div>

      <c:choose>
        <c:when test="${nlen == 0}">
          <div class="row">
            <div class="col-lg-12">
              <p style="color:#b7b7b7; text-align:center; padding:40px 0;">
                <c:choose>
                  <c:when test="${isSearch}">검색 결과가 없습니다.</c:when>
                  <c:otherwise>등록된 뉴스가 없습니다.</c:otherwise>
                </c:choose>
              </p>
            </div>
          </div>
        </c:when>

        <c:otherwise>
          <div class="row">

            <c:choose>
              <%-- 검색 결과: 작은 카드만 (좌 6 / 우 나머지) --%>
              <c:when test="${isSearch}">

                <div class="col-lg-6">
                  <div class="row">
                    <c:forEach var="news" items="${newsList}" begin="0" end="${nlen-1}" varStatus="st">
                      <c:if test="${st.index < 6}">

                        <c:set var="thumb" value="${empty news.newsThumbnailUrl ? '/img/normal-breadcrumb.jpg' : news.newsThumbnailUrl}" />
                        <c:choose>
                          <c:when test="${fn:startsWith(thumb,'http')}"></c:when>
                          <c:when test="${fn:startsWith(thumb, ctx)}"></c:when>
                          <c:when test="${fn:startsWith(thumb,'/')}">
                            <c:set var="thumb" value="${ctx}${thumb}" />
                          </c:when>
                          <c:otherwise>
                            <c:set var="thumb" value="${ctx}/${thumb}" />
                          </c:otherwise>
                        </c:choose>

                        <c:url var="detailUrl" value="/newsDetail.do">
                          <c:param name="newsId" value="${news.newsId}" />
                          <c:param name="page" value="${page}" />
                          <c:param name="condition" value="${condition}" />
                          <c:if test="${not empty keyword}">
                            <c:param name="keyword" value="${keyword}" />
                          </c:if>
                        </c:url>

                        <div class="col-lg-6 col-md-6 col-sm-6">
                          <a href="${detailUrl}" class="blog__item small__item set-bg" data-setbg="${thumb}">
                            <div class="blog__item__text">
                              <h4><c:out value="${news.newsTitle}" /></h4>
                            </div>
                          </a>
                        </div>

                      </c:if>
                    </c:forEach>
                  </div>
                </div>

                <div class="col-lg-6">
                  <div class="row">
                    <c:forEach var="news" items="${newsList}" begin="0" end="${nlen-1}" varStatus="st">
                      <c:if test="${st.index >= 6}">

                        <c:set var="thumb" value="${empty news.newsThumbnailUrl ? '/img/normal-breadcrumb.jpg' : news.newsThumbnailUrl}" />
                        <c:choose>
                          <c:when test="${fn:startsWith(thumb,'http')}"></c:when>
                          <c:when test="${fn:startsWith(thumb, ctx)}"></c:when>
                          <c:when test="${fn:startsWith(thumb,'/')}">
                            <c:set var="thumb" value="${ctx}${thumb}" />
                          </c:when>
                          <c:otherwise>
                            <c:set var="thumb" value="${ctx}/${thumb}" />
                          </c:otherwise>
                        </c:choose>

                        <c:url var="detailUrl" value="/newsDetail.do">
                          <c:param name="newsId" value="${news.newsId}" />
                          <c:param name="page" value="${page}" />
                          <c:param name="condition" value="${condition}" />
                          <c:if test="${not empty keyword}">
                            <c:param name="keyword" value="${keyword}" />
                          </c:if>
                        </c:url>

                        <div class="col-lg-6 col-md-6 col-sm-6">
                          <a href="${detailUrl}" class="blog__item small__item set-bg" data-setbg="${thumb}">
                            <div class="blog__item__text">
                              <h4><c:out value="${news.newsTitle}" /></h4>
                            </div>
                          </a>
                        </div>

                      </c:if>
                    </c:forEach>
                  </div>
                </div>

              </c:when>

              <%-- 기본 목록: (왼쪽 BIG+SMALL+SMALL) + (오른쪽 SMALL+SMALL+BIG) 6개 단위 반복 --%>
              <c:otherwise>

                <c:forEach var="base" begin="0" end="${nlen-1}" step="6">

                  <div class="col-lg-12">
                    <div class="row">

                      <div class="col-lg-6">
                        <div class="row">

                          <c:set var="idx" value="${base}" />
                          <c:if test="${idx < nlen}">
                            <c:set var="news" value="${newsList[idx]}" />

                            <c:set var="thumb" value="${empty news.newsThumbnailUrl ? '/img/normal-breadcrumb.jpg' : news.newsThumbnailUrl}" />
                            <c:choose>
                              <c:when test="${fn:startsWith(thumb,'http')}"></c:when>
                              <c:when test="${fn:startsWith(thumb, ctx)}"></c:when>
                              <c:when test="${fn:startsWith(thumb,'/')}"><c:set var="thumb" value="${ctx}${thumb}" /></c:when>
                              <c:otherwise><c:set var="thumb" value="${ctx}/${thumb}" /></c:otherwise>
                            </c:choose>

                            <c:url var="detailUrl" value="/newsDetail.do">
                              <c:param name="newsId" value="${news.newsId}" />
                              <c:param name="page" value="${page}" />
                              <c:param name="condition" value="${condition}" />
                              <c:if test="${not empty keyword}">
                                <c:param name="keyword" value="${keyword}" />
                              </c:if>
                            </c:url>

                            <div class="col-lg-12">
                              <a href="${detailUrl}" class="blog__item set-bg" data-setbg="${thumb}">
                                <div class="blog__item__text">
                                  <h4><c:out value="${news.newsTitle}" /></h4>
                                </div>
                              </a>
                            </div>
                          </c:if>

                          <c:set var="idx" value="${base + 1}" />
                          <c:if test="${idx < nlen}">
                            <c:set var="news" value="${newsList[idx]}" />

                            <c:set var="thumb" value="${empty news.newsThumbnailUrl ? '/img/normal-breadcrumb.jpg' : news.newsThumbnailUrl}" />
                            <c:choose>
                              <c:when test="${fn:startsWith(thumb,'http')}"></c:when>
                              <c:when test="${fn:startsWith(thumb, ctx)}"></c:when>
                              <c:when test="${fn:startsWith(thumb,'/')}"><c:set var="thumb" value="${ctx}${thumb}" /></c:when>
                              <c:otherwise><c:set var="thumb" value="${ctx}/${thumb}" /></c:otherwise>
                            </c:choose>

                            <c:url var="detailUrl" value="/newsDetail.do">
                              <c:param name="newsId" value="${news.newsId}" />
                              <c:param name="page" value="${page}" />
                              <c:param name="condition" value="${condition}" />
                              <c:if test="${not empty keyword}">
                                <c:param name="keyword" value="${keyword}" />
                              </c:if>
                            </c:url>

                            <div class="col-lg-6 col-md-6 col-sm-6">
                              <a href="${detailUrl}" class="blog__item small__item set-bg" data-setbg="${thumb}">
                                <div class="blog__item__text">
                                  <h4><c:out value="${news.newsTitle}" /></h4>
                                </div>
                              </a>
                            </div>
                          </c:if>

                          <c:set var="idx" value="${base + 2}" />
                          <c:if test="${idx < nlen}">
                            <c:set var="news" value="${newsList[idx]}" />

                            <c:set var="thumb" value="${empty news.newsThumbnailUrl ? '/img/normal-breadcrumb.jpg' : news.newsThumbnailUrl}" />
                            <c:choose>
                              <c:when test="${fn:startsWith(thumb,'http')}"></c:when>
                              <c:when test="${fn:startsWith(thumb, ctx)}"></c:when>
                              <c:when test="${fn:startsWith(thumb,'/')}"><c:set var="thumb" value="${ctx}${thumb}" /></c:when>
                              <c:otherwise><c:set var="thumb" value="${ctx}/${thumb}" /></c:otherwise>
                            </c:choose>

                            <c:url var="detailUrl" value="/newsDetail.do">
                              <c:param name="newsId" value="${news.newsId}" />
                              <c:param name="page" value="${page}" />
                              <c:param name="condition" value="${condition}" />
                              <c:if test="${not empty keyword}">
                                <c:param name="keyword" value="${keyword}" />
                              </c:if>
                            </c:url>

                            <div class="col-lg-6 col-md-6 col-sm-6">
                              <a href="${detailUrl}" class="blog__item small__item set-bg" data-setbg="${thumb}">
                                <div class="blog__item__text">
                                  <h4><c:out value="${news.newsTitle}" /></h4>
                                </div>
                              </a>
                            </div>
                          </c:if>

                        </div>
                      </div>

                      <div class="col-lg-6">
                        <div class="row">

                          <c:set var="idx" value="${base + 3}" />
                          <c:if test="${idx < nlen}">
                            <c:set var="news" value="${newsList[idx]}" />

                            <c:set var="thumb" value="${empty news.newsThumbnailUrl ? '/img/normal-breadcrumb.jpg' : news.newsThumbnailUrl}" />
                            <c:choose>
                              <c:when test="${fn:startsWith(thumb,'http')}"></c:when>
                              <c:when test="${fn:startsWith(thumb, ctx)}"></c:when>
                              <c:when test="${fn:startsWith(thumb,'/')}"><c:set var="thumb" value="${ctx}${thumb}" /></c:when>
                              <c:otherwise><c:set var="thumb" value="${ctx}/${thumb}" /></c:otherwise>
                            </c:choose>

                            <c:url var="detailUrl" value="/newsDetail.do">
                              <c:param name="newsId" value="${news.newsId}" />
                              <c:param name="page" value="${page}" />
                              <c:param name="condition" value="${condition}" />
                              <c:if test="${not empty keyword}">
                                <c:param name="keyword" value="${keyword}" />
                              </c:if>
                            </c:url>

                            <div class="col-lg-6 col-md-6 col-sm-6">
                              <a href="${detailUrl}" class="blog__item small__item set-bg" data-setbg="${thumb}">
                                <div class="blog__item__text">
                                  <h4><c:out value="${news.newsTitle}" /></h4>
                                </div>
                              </a>
                            </div>
                          </c:if>

                          <c:set var="idx" value="${base + 4}" />
                          <c:if test="${idx < nlen}">
                            <c:set var="news" value="${newsList[idx]}" />

                            <c:set var="thumb" value="${empty news.newsThumbnailUrl ? '/img/normal-breadcrumb.jpg' : news.newsThumbnailUrl}" />
                            <c:choose>
                              <c:when test="${fn:startsWith(thumb,'http')}"></c:when>
                              <c:when test="${fn:startsWith(thumb, ctx)}"></c:when>
                              <c:when test="${fn:startsWith(thumb,'/')}"><c:set var="thumb" value="${ctx}${thumb}" /></c:when>
                              <c:otherwise><c:set var="thumb" value="${ctx}/${thumb}" /></c:otherwise>
                            </c:choose>

                            <c:url var="detailUrl" value="/newsDetail.do">
                              <c:param name="newsId" value="${news.newsId}" />
                              <c:param name="page" value="${page}" />
                              <c:param name="condition" value="${condition}" />
                              <c:if test="${not empty keyword}">
                                <c:param name="keyword" value="${keyword}" />
                              </c:if>
                            </c:url>

                            <div class="col-lg-6 col-md-6 col-sm-6">
                              <a href="${detailUrl}" class="blog__item small__item set-bg" data-setbg="${thumb}">
                                <div class="blog__item__text">
                                  <h4><c:out value="${news.newsTitle}" /></h4>
                                </div>
                              </a>
                            </div>
                          </c:if>

                          <c:set var="idx" value="${base + 5}" />
                          <c:if test="${idx < nlen}">
                            <c:set var="news" value="${newsList[idx]}" />

                            <c:set var="thumb" value="${empty news.newsThumbnailUrl ? '/img/normal-breadcrumb.jpg' : news.newsThumbnailUrl}" />
                            <c:choose>
                              <c:when test="${fn:startsWith(thumb,'http')}"></c:when>
                              <c:when test="${fn:startsWith(thumb, ctx)}"></c:when>
                              <c:when test="${fn:startsWith(thumb,'/')}"><c:set var="thumb" value="${ctx}${thumb}" /></c:when>
                              <c:otherwise><c:set var="thumb" value="${ctx}/${thumb}" /></c:otherwise>
                            </c:choose>

                            <c:url var="detailUrl" value="/newsDetail.do">
                              <c:param name="newsId" value="${news.newsId}" />
                              <c:param name="page" value="${page}" />
                              <c:param name="condition" value="${condition}" />
                              <c:if test="${not empty keyword}">
                                <c:param name="keyword" value="${keyword}" />
                              </c:if>
                            </c:url>

                            <div class="col-lg-12">
                              <a href="${detailUrl}" class="blog__item set-bg" data-setbg="${thumb}">
                                <div class="blog__item__text">
                                  <h4><c:out value="${news.newsTitle}" /></h4>
                                </div>
                              </a>
                            </div>
                          </c:if>

                        </div>
                      </div>

                    </div>
                  </div>

                </c:forEach>

              </c:otherwise>
            </c:choose>

            <div class="col-lg-12">
              <div class="product__pagination">

                <c:if test="${hasPrev}">
                  <c:url var="prevUrl" value="/newsList.do">
                    <c:param name="page" value="${startPage - 1}" />
                    <c:param name="condition" value="${condition}" />
                    <c:if test="${not empty keyword}">
                      <c:param name="keyword" value="${keyword}" />
                    </c:if>
                  </c:url>
                  <a href="${prevUrl}"><i class="fa fa-angle-double-left"></i></a>
                </c:if>

                <c:forEach var="p" begin="${startPage}" end="${endPage}">
                  <c:url var="pageUrl" value="/newsList.do">
                    <c:param name="page" value="${p}" />
                    <c:param name="condition" value="${condition}" />
                    <c:if test="${not empty keyword}">
                      <c:param name="keyword" value="${keyword}" />
                    </c:if>
                  </c:url>
                  <a href="${pageUrl}" class="${p == page ? 'current-page' : ''}">${p}</a>
                </c:forEach>

                <c:if test="${hasNext}">
                  <c:url var="nextUrl" value="/newsList.do">
                    <c:param name="page" value="${endPage + 1}" />
                    <c:param name="condition" value="${condition}" />
                    <c:if test="${not empty keyword}">
                      <c:param name="keyword" value="${keyword}" />
                    </c:if>
                  </c:url>
                  <a href="${nextUrl}"><i class="fa fa-angle-double-right"></i></a>
                </c:if>

              </div>
            </div>

          </div>
        </c:otherwise>
      </c:choose>

    </div>
  </section>

  <jsp:include page="/common/footer.jsp" />

  <!-- set-bg 동작 필수 -->
  <script src="${ctx}/js/jquery-3.3.1.min.js"></script>
  <script src="${ctx}/js/bootstrap.min.js"></script>
  <script src="${ctx}/js/player.js"></script>
  <script src="${ctx}/js/jquery.nice-select.min.js"></script>
  <script src="${ctx}/js/mixitup.min.js"></script>
  <script src="${ctx}/js/jquery.slicknav.js"></script>
  <script src="${ctx}/js/owl.carousel.min.js"></script>
  <script src="${ctx}/js/main.js"></script>

</body>
</html>
