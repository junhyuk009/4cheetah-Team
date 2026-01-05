<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>AniMale | 내 글 보기</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="icon" type="image/png" href="${ctx}/favicon.png">

<!-- Google Font -->
<link href="https://fonts.googleapis.com/css2?family=Oswald:wght@300;400;500;600;700&display=swap" rel="stylesheet">
<link href="https://fonts.googleapis.com/css2?family=Mulish:wght@300;400;500;600;700;800;900&display=swap" rel="stylesheet">

<!-- CSS -->
<link rel="stylesheet" href="${ctx}/css/bootstrap.min.css">
<link rel="stylesheet" href="${ctx}/css/font-awesome.min.css">
<link rel="stylesheet" href="${ctx}/css/style.css">
<link rel="stylesheet" href="${ctx}/css/elegant-icons.css">

<link rel="stylesheet" href="${ctx}/css/board.css">

<style>
/* 내 글 보기 페이지에서만 검색 아이콘 숨기기 */
.header__right .icon_search { display: none !important; }

.mypost-toolbar .board-sub{
  margin: 6px 0 0;
}

/* 토글 (내 글/좋아요) 버튼 */
.mypost-toggle{
  display:flex;
  align-items:center;
  gap:10px;
}

.mypost-pill{
  display:inline-flex;
  align-items:center;
  gap:8px;
  padding: 10px 14px;
  border-radius: 999px;
  font-size: 13px;
  font-weight: 900;
  cursor:pointer;
  user-select:none;
  text-decoration:none !important;

  background: rgba(255,255,255,0.06);
  border: 1px solid rgba(255,255,255,0.14);
  color: rgba(255,255,255,0.88);
  transition: transform .15s ease, box-shadow .15s ease, background .15s ease, border-color .15s ease;
}

.mypost-pill:hover{
  transform: translateY(-1px);
  box-shadow: 0 12px 28px rgba(0,0,0,0.28);
  background: rgba(255,255,255,0.10);
  border-color: rgba(255,255,255,0.22);
}

.mypost-pill.is-active{
  background: rgba(229,54,55,0.16);
  border-color: rgba(229,54,55,0.35);
  color:#fff;
}

.mypost-count{
  font-size: 12px;
  font-weight: 900;
  padding: 3px 10px;
  border-radius: 999px;
  background: rgba(255,255,255,0.10);
  border: 1px solid rgba(255,255,255,0.10);
  color: rgba(255,255,255,0.9);
}
.mypost-pill.is-active .mypost-count{
  background: rgba(229,54,55,0.22);
  border-color: rgba(229,54,55,0.28);
}

/* 리스트 폭/정렬 */
.mypost-wrap{
  max-width: 980px;
  margin: 0 auto;
}

/* my-post 리스트를 board-item 카드로 통일 */
.mypost-list .board-item{
  cursor: pointer;
}

/* 카테고리 뱃지 (공지/인기글처럼) */
.mypost-badge{
  display:inline-flex;
  align-items:center;
  gap:6px;
  font-size: 12px;
  font-weight: 900;
  padding: 4px 10px;
  border-radius: 999px;
  background: rgba(255,255,255,0.08);
  border: 1px solid rgba(255,255,255,0.12);
  color: rgba(255,255,255,0.88);
}

/* 빈 상태 */
.mypost-empty{
  margin-top: 8px;
}

/* 모바일 */
@media (max-width: 991px){
  .mypost-toggle{ width:100%; flex-wrap: wrap; }
  .mypost-pill{ width:100%; justify-content: space-between; }
}
</style>
</head>

<body>

<%@ include file="/common/header.jsp"%>

<section class="product-page spad">
  <div class="container">
    <div class="mypost-wrap">

      <!-- board.jsp 툴바 구조 재사용 -->
      <div class="board-toolbar mypost-toolbar">
        <div class="board-title-wrap">
          <c:choose>
            <c:when test="${sessionScope.memberRole eq 'ADMIN'}">
              <h3>관리자 내 글 보기</h3>
              <p class="board-sub">내가 작성한 글과 좋아요한 글을 한 번에 확인하세요</p>
            </c:when>
            <c:otherwise>
              <h3>내 글 보기</h3>
              <p class="board-sub">내가 작성한 글과 좋아요한 글을 한 번에 확인하세요</p>
            </c:otherwise>
          </c:choose>
        </div>

        <!-- board-actions 자리에는 토글 UI -->
        <div class="board-actions mypost-toggle">
          <button type="button" class="mypost-pill is-active" id="btnMy">
            <i class="fa fa-pencil"></i>
            내가 작성한 글
            <span class="mypost-count">
              <c:out value="${empty myBoardWriteList ? 0 : fn:length(myBoardWriteList)}" />건
            </span>
          </button>

          <button type="button" class="mypost-pill" id="btnLike">
            <i class="fa fa-heart"></i>
            내가 좋아요한 글
            <span class="mypost-count">
              <c:out value="${empty myBoardLikeList ? 0 : fn:length(myBoardLikeList)}" />건
            </span>
          </button>
        </div>
      </div>

      <!-- board-list / board-posts 구조로 통일 -->
      <div class="board-list mypost-list">

        <!-- 내가 작성한 글 -->
        <div id="myPostBox" class="board-posts">
          <c:if test="${empty myBoardWriteList}">
            <div class="search-empty board-search-empty mypost-empty">작성한 글이 없습니다.</div>
          </c:if>

          <c:forEach var="post" items="${myBoardWriteList}">
            <c:set var="categoryKey" value="${fn:toUpperCase(fn:trim(post.boardCategory))}" />
            <c:set var="categoryTitle" value="${categoryKey} 게시판" />

            <c:choose>
              <c:when test="${categoryKey eq 'ANIME'}"><c:set var="categoryTitle" value="애니 게시판" /></c:when>
              <c:when test="${categoryKey eq 'FREE'}"><c:set var="categoryTitle" value="자유 게시판" /></c:when>
              <c:when test="${categoryKey eq 'QNA'}"><c:set var="categoryTitle" value="Q&amp;A 게시판" /></c:when>
              <c:when test="${categoryKey eq 'INFO'}"><c:set var="categoryTitle" value="정보 게시판" /></c:when>
            </c:choose>

            <div class="board-item post-item"
                 data-href="${ctx}/boardDetail.do?boardId=${post.boardId}">
              <div class="board-title">
                <span class="mypost-badge">${categoryTitle}</span>
                <a href="${ctx}/boardDetail.do?boardId=${post.boardId}">
                  ${post.boardTitle}
                </a>
              </div>
            </div>
          </c:forEach>
        </div>

        <!-- 내가 좋아요한 글 -->
        <div id="likePostBox" class="board-posts" style="display:none;">
          <c:if test="${empty myBoardLikeList}">
            <div class="search-empty board-search-empty mypost-empty">좋아요한 글이 없습니다.</div>
          </c:if>

          <c:forEach var="post" items="${myBoardLikeList}">
            <c:set var="categoryKey" value="${fn:toUpperCase(fn:trim(post.boardCategory))}" />
            <c:set var="categoryTitle" value="${categoryKey} 게시판" />

            <c:choose>
              <c:when test="${categoryKey eq 'ANIME'}"><c:set var="categoryTitle" value="애니 게시판" /></c:when>
              <c:when test="${categoryKey eq 'FREE'}"><c:set var="categoryTitle" value="자유 게시판" /></c:when>
              <c:when test="${categoryKey eq 'QNA'}"><c:set var="categoryTitle" value="Q&amp;A 게시판" /></c:when>
              <c:when test="${categoryKey eq 'INFO'}"><c:set var="categoryTitle" value="정보 게시판" /></c:when>
            </c:choose>

            <div class="board-item post-item"
                 data-href="${ctx}/boardDetail.do?boardId=${post.boardId}">
              <div class="board-title">
                <span class="mypost-badge">${categoryTitle}</span>
                <a href="${ctx}/boardDetail.do?boardId=${post.boardId}">
                  ${post.boardTitle}
                </a>
              </div>
            </div>
          </c:forEach>
        </div>

      </div>

    </div>
  </div>
</section>

<%@ include file="/common/footer.jsp"%>

<script src="${ctx}/js/jquery-3.3.1.min.js"></script>
<script src="${ctx}/js/bootstrap.min.js"></script>
<script src="${ctx}/js/main.js"></script>

<script>
(function(){
  // 토글 버튼 UI + 기존 show/hide
  const btnMy = document.getElementById("btnMy");
  const btnLike = document.getElementById("btnLike");
  const myBox = document.getElementById("myPostBox");
  const likeBox = document.getElementById("likePostBox");

  function setMode(mode){
    if(mode === "MY"){
      myBox.style.display = "";
      likeBox.style.display = "none";
      btnMy.classList.add("is-active");
      btnLike.classList.remove("is-active");
    }else{
      myBox.style.display = "none";
      likeBox.style.display = "";
      btnMy.classList.remove("is-active");
      btnLike.classList.add("is-active");
    }
  }

  btnMy.addEventListener("click", () => setMode("MY"));
  btnLike.addEventListener("click", () => setMode("LIKE"));

  // 카드 전체 클릭 이동 (제목 a 클릭은 유지) - board.jsp랑 동일
  document.querySelectorAll(".board-item[data-href]").forEach(function(item){
    item.addEventListener("click", function(e){
      if(e.target.closest("a")) return;
      location.href = item.getAttribute("data-href");
    });
  });

  setMode("MY");
})();
</script>

</body>
</html>
