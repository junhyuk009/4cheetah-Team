<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>AniMale | 게시판</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<link rel="icon" type="image/png"
  href="<%=request.getContextPath()%>/favicon.png">

<link href="https://fonts.googleapis.com/css2?family=Oswald:wght@300;400;500;600;700&display=swap" rel="stylesheet">
<link href="https://fonts.googleapis.com/css2?family=Mulish:wght@300;400;500;600;700;800;900&display=swap" rel="stylesheet">

<link rel="stylesheet" href="<%=request.getContextPath()%>/css/bootstrap.min.css">
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/font-awesome.min.css">
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/style.css">
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/board.css">
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/elegant-icons.css">
</head>

<body>
<%@ include file="/common/header.jsp"%>

<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="category" value="${requestScope.category}" />
<c:set var="condition" value="${requestScope.condition}" />
<c:set var="keyword" value="${requestScope.keyword}" />

<c:set var="keywordTrim" value="${fn:trim(keyword)}" />

<c:set var="categoryKey" value="${fn:toUpperCase(fn:trim(category))}" />
<c:set var="categoryTitle" value="${categoryKey} 게시판" />

<c:choose>
  <c:when test="${categoryKey eq 'ANIME'}"><c:set var="categoryTitle" value="애니 게시판" /></c:when>
  <c:when test="${categoryKey eq 'FREE'}"><c:set var="categoryTitle" value="자유 게시판" /></c:when>
  <c:when test="${categoryKey eq 'QNA'}"><c:set var="categoryTitle" value="Q&A 게시판" /></c:when>
  <c:when test="${categoryKey eq 'INFO'}"><c:set var="categoryTitle" value="정보 게시판" /></c:when>
</c:choose>

<c:set var="selectedCondition" value="BOARD_SEARCH_TITLE" />
<c:choose>
  <c:when test="${condition eq 'BOARD_SEARCH_TITLE'}"><c:set var="selectedCondition" value="BOARD_SEARCH_TITLE" /></c:when>
  <c:when test="${condition eq 'BOARD_SEARCH_WRITER'}"><c:set var="selectedCondition" value="BOARD_SEARCH_WRITER" /></c:when>
  <c:when test="${condition eq 'BOARD_SEARCH_CONTENT'}"><c:set var="selectedCondition" value="BOARD_SEARCH_CONTENT" /></c:when>
</c:choose>

<section class="product-page spad">
  <div class="container">

    <div class="board-toolbar">
      <div class="board-title-wrap">
        <h3>${categoryTitle}</h3>
        <p class="board-sub">인기글/공지 확인하고 자유롭게 소통해보세요</p>
      </div>

      <div class="board-actions">

        <form id="boardSearchForm" action="${ctx}/boardList.do" method="get" class="board-search-box">
          <input type="hidden" name="category" value="${categoryKey}" />

          <select name="condition" id="boardSearchType">
            <option value="BOARD_SEARCH_TITLE"  <c:if test="${selectedCondition eq 'BOARD_SEARCH_TITLE'}">selected</c:if>>제목</option>
            <option value="BOARD_SEARCH_WRITER" <c:if test="${selectedCondition eq 'BOARD_SEARCH_WRITER'}">selected</c:if>>작성자</option>
            <option value="BOARD_SEARCH_CONTENT"<c:if test="${selectedCondition eq 'BOARD_SEARCH_CONTENT'}">selected</c:if>>내용</option>
          </select>

          <input type="text" name="keyword" id="boardSearchInput"
                 placeholder="검색어를 입력하세요" value="${keywordTrim}" />

          <button type="submit" title="검색">
            <i class="fa fa-search"></i>
          </button>
        </form>

        <c:if test="${not empty keywordTrim}">
          <a href="${ctx}/boardList.do?category=${categoryKey}" class="board-reset-btn">전체보기</a>
        </c:if>

        <a href="${ctx}/boardWritePage.do?type=BOARD&category=${categoryKey}" class="board-write-btn">글 작성</a>

      </div>
    </div>

    <div class="board-list">

      <c:if test="${not empty noticeList}">
        <div class="board-notice">
          <c:forEach var="n" items="${noticeList}">
            <div class="board-item notice-item"
                 data-href="${ctx}/boardDetail.do?boardId=${n.boardId}">
              <div class="board-title">
                <span class="notice-badge">공지</span>
                <a href="${ctx}/boardDetail.do?boardId=${n.boardId}">
                  <c:out value="${n.boardTitle}" />
                </a>
              </div>
              <div class="board-meta">
                <span><c:out value="${n.writerNickname}" /></span>
                <span>· <i class="fa fa-eye"></i> <c:out value="${n.boardViews}" default="0" /></span>
                <span>· <i class="fa fa-heart"></i> <c:out value="${n.likeCnt}" default="0" /></span>
              </div>
            </div>
          </c:forEach>
        </div>
      </c:if>

      <div class="board-posts">

        <c:if test="${empty boardList}">
          <div class="search-empty board-search-empty">
            <c:choose>
              <c:when test="${not empty keywordTrim}">검색 결과가 없습니다.</c:when>
              <c:otherwise>해당 글이 없습니다.</c:otherwise>
            </c:choose>
          </div>
        </c:if>

        <c:choose>
          <c:when test="${not empty keywordTrim}">
            <c:forEach var="b" items="${boardList}">
              <div class="board-item post-item"
                   data-href="${ctx}/boardDetail.do?boardId=${b.boardId}">
                <div class="board-title">
                  <a href="${ctx}/boardDetail.do?boardId=${b.boardId}">
                    <c:out value="${b.boardTitle}" />
                  </a>
                </div>
                <div class="board-meta">
                  <span><c:out value="${b.writerNickname}" /></span>
                  <span>· <i class="fa fa-eye"></i> <c:out value="${b.boardViews}" default="0" /></span>
                  <span>· <i class="fa fa-heart"></i> <c:out value="${b.likeCnt}" default="0" /></span>
                </div>
              </div>
            </c:forEach>
          </c:when>

          <c:otherwise>

            <c:set var="hasPopular" value="false" />
            <c:forEach var="t" items="${boardList}">
              <c:if test="${not empty t.likeCnt and t.likeCnt ge 10}">
                <c:set var="hasPopular" value="true" />
              </c:if>
            </c:forEach>

            <c:if test="${not empty boardList and hasPopular}">
              <div class="board-popular" style="margin-top:6px; margin-bottom:12px;">
                <div style="margin:6px 0 10px;">
                  <h5 style="margin:0;">🔥 인기글</h5>
                </div>

                <c:forEach var="b" items="${boardList}">
                  <c:if test="${not empty b.likeCnt and b.likeCnt ge 10}">
                    <div class="board-item post-item popular-item"
                         data-href="${ctx}/boardDetail.do?boardId=${b.boardId}">
                      <div class="board-title">
                        <a href="${ctx}/boardDetail.do?boardId=${b.boardId}">
                          <c:out value="${b.boardTitle}" />
                        </a>
                      </div>
                      <div class="board-meta">
                        <span><c:out value="${b.writerNickname}" /></span>
                        <span>· <i class="fa fa-eye"></i> <c:out value="${b.boardViews}" default="0" /></span>
                        <span>· <i class="fa fa-heart"></i> <c:out value="${b.likeCnt}" default="0" /></span>
                      </div>
                    </div>
                  </c:if>
                </c:forEach>
              </div>

              <div style="height:1px; background:rgba(255,255,255,0.08); margin:14px 0;"></div>
            </c:if>

            <c:forEach var="b" items="${boardList}">
              <c:if test="${empty b.likeCnt or b.likeCnt lt 10}">
                <div class="board-item post-item"
                     data-href="${ctx}/boardDetail.do?boardId=${b.boardId}">
                  <div class="board-title">
                    <a href="${ctx}/boardDetail.do?boardId=${b.boardId}">
                      <c:out value="${b.boardTitle}" />
                    </a>
                  </div>
                  <div class="board-meta">
                    <span><c:out value="${b.writerNickname}" /></span>
                    <span>· <i class="fa fa-eye"></i> <c:out value="${b.boardViews}" default="0" /></span>
                    <span>· <i class="fa fa-heart"></i> <c:out value="${b.likeCnt}" default="0" /></span>
                  </div>
                </div>
              </c:if>
            </c:forEach>

          </c:otherwise>
        </c:choose>

      </div>

    </div>

  </div>
</section>

<jsp:include page="/common/footer.jsp" />

<script src="<%=request.getContextPath()%>/js/jquery-3.3.1.min.js"></script>
<script src="<%=request.getContextPath()%>/js/bootstrap.min.js"></script>

<script>
  // 검색어 trim + 빈값 방지
  (function(){
    var form = document.getElementById("boardSearchForm");
    if(!form) return;

    form.addEventListener("submit", function(e){
      var input = document.getElementById("boardSearchInput");
      var k = (input && input.value ? input.value : "").trim();
      if(!k){
        e.preventDefault();
        alert("검색어를 입력하세요.");
        if(input) input.focus();
        return;
      }
      input.value = k;
    });
  })();

  // 카드 전체 클릭 이동 (제목 a 클릭은 유지)
  (function(){
    var items = document.querySelectorAll(".board-item[data-href]");
    items.forEach(function(item){
      item.addEventListener("click", function(e){
        if(e.target.closest("a")) return;
        location.href = item.getAttribute("data-href");
      });
    });
  })();
</script>

</body>
</html>
