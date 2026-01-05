<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:set var="typeRaw"
	value="${empty param.type ? requestScope.type : param.type}" />
<c:set var="type" value="${fn:toUpperCase(typeRaw)}" />
<c:set var="isEditPage" value="true" />

<c:if test="${type eq 'NEWS'}">
	<c:set var="activeMenu" value="news" />
</c:if>
<c:if test="${type eq 'BOARD'}">
	<c:set var="activeMenu" value="board" />
</c:if>

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>AniMale | 작성</title>

<!-- CKEditor 5 커스텀 빌드 -->
<script
	src="${pageContext.request.contextPath}/js/ckeditor.js?v=20260102_6"></script>

<link rel="icon" type="image/png"
	href="${pageContext.request.contextPath}/favicon.png">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/elegant-icons.css">

<link
	href="https://fonts.googleapis.com/css2?family=Oswald:wght@300;400;500;600;700&display=swap"
	rel="stylesheet">
<link
	href="https://fonts.googleapis.com/css2?family=Mulish:wght@300;400;500;600;700;800;900&display=swap"
	rel="stylesheet">

<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/bootstrap.min.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/font-awesome.min.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/style.css">

<style>
/* 상단 검색 UI 숨김 */
.header__right .search-switch, .search-model, .fa-search {
	display: none !important;
}

/* 썸네일 미리보기 */
.preview-box {
	aspect-ratio: 3/4;
	background: #1e1e30;
	border-radius: 16px;
	border: 1px solid rgba(255, 255, 255, .15);
	display: flex;
	align-items: center;
	justify-content: center;
	overflow: hidden;
}

.preview-img {
	width: 100%;
	height: 100%;
	object-fit: contain;
	display: none;
}

/* 폼 */
.admin-form label {
	color: #fff !important;
	font-weight: 600;
}

.admin-form .form-control {
	background: #fff;
	border-radius: 8px;
}

.thumb-btn {
	background: #e53637;
	color: #fff;
	padding: 8px 16px;
	border-radius: 6px;
	cursor: pointer;
	font-size: 14px;
	display: inline-block;
	margin-right: 12px;
}

.thumb-file {
	display: none;
}

.thumb-filename {
	color: rgba(255, 255, 255, .85);
	font-size: 14px;
	vertical-align: middle;
}

/* CKEditor - 글자 색 */
.ck-editor__editable, .ck-editor__editable *, .ck-content, .ck-content *
	{
	color: #000 !important;
}

.ck-editor__editable.ck-placeholder::before {
	color: #888 !important;
}

/* 버튼 */
.btn-submit {
	background: #e53637;
	color: #fff;
	padding: 10px 24px;
	border-radius: 6px;
	border: none;
}

.btn-cancel {
	background: #444;
	color: #fff;
	padding: 10px 24px;
	border-radius: 6px;
}

.form-actions {
	display: flex;
	justify-content: flex-end;
	gap: 10px;
	margin-top: 24px;
}

.access-deny-box {
	background: rgba(0, 0, 0, .25);
	border: 1px solid rgba(255, 255, 255, .15);
	border-radius: 12px;
	padding: 22px;
	color: #fff;
}

.preview-help {
	color: rgba(255, 255, 255, .75);
	font-size: 13px;
	margin-top: 10px;
}

/* 관련 애니 검색 드롭다운 */
.related-wrap {
	margin-top: 24px;
	position: relative;
}

.anime-search-result {
	position: absolute;
	top: calc(100% + 6px);
	left: 0;
	right: 0;
	background: rgba(20, 20, 35, .98);
	border: 1px solid rgba(255, 255, 255, .12);
	border-radius: 12px;
	box-shadow: 0 12px 30px rgba(0, 0, 0, .35);
	max-height: 280px;
	overflow: auto;
	display: none;
	z-index: 9999;
	padding: 6px;
	backdrop-filter: blur(6px);
}

.anime-search-result li {
	padding: 10px 12px;
	border-radius: 10px;
	cursor: pointer;
	color: #fff;
	display: flex;
	align-items: center;
	gap: 10px;
	user-select: none;
}

.anime-search-result li:hover, .anime-search-result li.is-active {
	background: rgba(229, 54, 55, .18);
	outline: 1px solid rgba(229, 54, 55, .25);
}

.anime-search-result li.is-hint {
	cursor: default;
	background: transparent !important;
	outline: none !important;
}

.anime-search-hint {
	padding: 10px 12px;
	color: rgba(255, 255, 255, .7);
	font-size: 13px;
}

.anime-thumb {
	width: 34px;
	height: 46px;
	border-radius: 8px;
	object-fit: cover;
	background: rgba(255, 255, 255, .08);
	border: 1px solid rgba(255, 255, 255, .12);
	flex: 0 0 auto;
}

.anime-meta {
	display: flex;
	flex-direction: column;
	min-width: 0;
	flex: 1;
}

.anime-title {
	font-weight: 700;
	font-size: 14px;
	line-height: 1.1;
	white-space: nowrap;
	overflow: hidden;
	text-overflow: ellipsis;
}

.anime-sub {
	display: flex;
	gap: 6px;
	margin-top: 6px;
	flex-wrap: wrap;
}

.anime-sub .badge {
	font-size: 12px;
	padding: 2px 8px;
	border-radius: 999px;
	background: rgba(255, 255, 255, .10);
	color: rgba(255, 255, 255, .9);
	border: 1px solid rgba(255, 255, 255, .10);
}

/* CKEditor 본문/이미지 캡션 보정 */
.ck-editor__editable {
	min-height: 200px !important;
	max-height: 600px;
	overflow-y: auto;
}

.ck-content figure.image {
	display: table;
	margin: 0;
	max-width: 100%;
}

.ck-content figure.image>img {
	display: block;
	max-width: 100%;
	height: auto;
}

.ck-content figure.image>figcaption {
	padding: 4px 0 !important;
	line-height: 1.2 !important;
	font-size: 13px !important;
	text-align: center;
	min-height: 0 !important;
	margin: 0 !important;
	color: #666 !important;
}

/* 관련 애니 선택 완료 상태 표시 */
.anime-input-wrap {
	position: relative;
}

.anime-input-wrap .form-control {
	padding-right: 44px;
}

.anime-select-state {
	position: absolute;
	right: 12px;
	top: 50%;
	transform: translateY(-50%);
	width: 22px;
	height: 22px;
	border-radius: 999px;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 13px;
	font-weight: 800;
	background: rgba(255, 255, 255, .14);
	border: 1px solid rgba(255, 255, 255, .14);
	color: rgba(255, 255, 255, .9);
	pointer-events: none;
}

#relatedWrap .anime-select-state::before {
	content: "…";
}

#relatedWrap.is-picked .anime-select-state {
	background: rgba(80, 200, 120, .18);
	border-color: rgba(80, 200, 120, .35);
}

#relatedWrap.is-picked .anime-select-state::before {
	content: "✓";
}

#relatedWrap.is-invalid .anime-select-state {
	background: rgba(229, 54, 55, .16);
	border-color: rgba(229, 54, 55, .30);
}

#relatedWrap.is-invalid .anime-select-state::before {
	content: "!";
}

.anime-select-warn {
	margin-top: 8px;
	padding: 10px 12px;
	border-radius: 10px;
	background: rgba(229, 54, 55, .14);
	border: 1px solid rgba(229, 54, 55, .22);
	color: rgba(255, 255, 255, .92);
	font-size: 13px;
	line-height: 1.35;
	white-space: pre-line;
}

/* 선택 완료 카드 */
.anime-selected-card {
	margin-top: 10px;
	padding: 12px;
	border-radius: 12px;
	background: rgba(255, 255, 255, .06);
	border: 1px solid rgba(255, 255, 255, .14);
	display: flex;
	align-items: center;
	gap: 12px;
	color: rgba(255, 255, 255, .92);
}

.anime-selected-card .anime-thumb {
	width: 42px;
	height: 58px;
	border-radius: 10px;
}

.anime-selected-card .anime-title {
	color: rgba(255, 255, 255, .95) !important;
}

.anime-selected-card .anime-sub .badge {
	color: rgba(255, 255, 255, .92);
}

.picked-pill {
	display: inline-flex;
	align-items: center;
	gap: 6px;
	margin-left: 8px;
	padding: 2px 8px;
	border-radius: 999px;
	font-size: 12px;
	background: rgba(80, 200, 120, .18);
	border: 1px solid rgba(80, 200, 120, .30);
	color: #fff;
}

.anime-clear-btn {
	margin-left: auto;
	background: rgba(229, 54, 55, .16);
	border: 1px solid rgba(229, 54, 55, .28);
	color: #fff;
	padding: 7px 10px;
	border-radius: 999px;
	font-size: 12px;
	cursor: pointer;
}

.anime-clear-btn:hover {
	background: rgba(229, 54, 55, .24);
}
</style>
</head>

<body>
	<%@ include file="/common/header.jsp"%>

	<c:choose>

		<%-- NEWS 작성 --%>
		<c:when test="${type eq 'NEWS'}">
			<c:choose>
				<c:when
					test="${not empty sessionScope.memberId and sessionScope.memberRole eq 'ADMIN'}">

					<section class="anime-details spad">
						<div class="container">
							<div class="row">

								<div class="col-lg-3">
									<div class="preview-box">
										<img id="thumbPreviewImg" class="preview-img"
											alt="thumbnail preview">
									</div>
									<div class="preview-help">썸네일을 변경하면 즉시 반영됩니다.</div>
								</div>

								<div class="col-lg-9">
									<h3 style="color: #fff;">뉴스 작성</h3>

									<form class="admin-form" method="post"
										action="${pageContext.request.contextPath}/newsWrite.do"
										enctype="multipart/form-data">

										<input type="hidden" name="animeId" id="animeIdHidden">

										<div class="form-group">
											<label>제목</label> <input type="text" class="form-control"
												name="newsTitle">
										</div>

										<div class="form-group">
											<label>썸네일</label><br> <label class="thumb-btn"
												for="thumbFile">파일 선택</label> <span class="thumb-filename"
												id="thumbFileName"></span> <input type="file" id="thumbFile"
												name="thumbFile" class="thumb-file" accept="image/*">
										</div>

										<div class="form-group">
											<label>상세 내용</label>
											<textarea id="editor" name="newsContent"></textarea>
										</div>

										<div class="related-wrap" id="relatedWrap">
											<label style="color: #fff;">관련 애니</label>

											<div class="anime-input-wrap">
												<input type="text" id="animeSearchInput"
													class="form-control" placeholder="애니 제목을 입력하고 목록에서 선택하세요"
													autocomplete="off"> <span id="animeSelectState"
													class="anime-select-state" aria-hidden="true"></span>
											</div>

											<div id="animeSelectWarn" class="anime-select-warn"
												style="display: none;"></div>

											<ul id="animeSearchResult" class="anime-search-result"></ul>

											<!-- 선택 완료 카드 -->
											<div id="animeSelectedCard" class="anime-selected-card"
												style="display: none;"></div>
										</div>

										<div class="form-actions">
											<button type="submit" class="btn-submit">작성 완료</button>
											<a href="${pageContext.request.contextPath}/newsList.do"
												class="btn-cancel">취소</a>
										</div>

									</form>
								</div>

							</div>
						</div>
					</section>

				</c:when>

				<c:otherwise>
					<section class="anime-details spad">
						<div class="container">
							<div class="access-deny-box">
								<p>뉴스 작성/수정은 관리자만 가능합니다.</p>
							</div>
						</div>
					</section>
				</c:otherwise>
			</c:choose>
		</c:when>

		<%-- BOARD 작성 --%>
		<c:when test="${type eq 'BOARD'}">
			<c:choose>
				<c:when test="${not empty sessionScope.memberId}">

					<section class="anime-details spad">
						<div class="container">

							<form class="admin-form" method="post"
								action="${pageContext.request.contextPath}/boardWrite.do">

								<h3 style="color: #fff;">게시글 작성</h3>
								<input type="hidden" name="boardCategory" value="ANIME">

								<div class="form-group">
									<label>게시글 제목</label> <input type="text" class="form-control"
										name="boardTitle">
								</div>

								<div class="form-group">
									<label>텍스트 내용</label>
									<textarea id="boardEditor" name="boardContent"></textarea>
								</div>

								<div class="form-actions">
									<button type="submit" class="btn-submit">작성 완료</button>
									<c:set var="cancelCategory"
										value="${empty param.category ? 'ANIME' : fn:toUpperCase(param.category)}" />
									<a
										href="${pageContext.request.contextPath}/boardList.do?category=${cancelCategory}"
										class="btn-cancel">취소</a>
								</div>

							</form>

						</div>
					</section>

				</c:when>

				<c:otherwise>
					<section class="anime-details spad">
						<div class="container">
							<div class="access-deny-box">
								<p>게시글 작성은 로그인 후 가능합니다.</p>
							</div>
						</div>
					</section>
				</c:otherwise>
			</c:choose>
		</c:when>

		<c:otherwise>
			<section class="anime-details spad">
				<div class="container">
					<div class="access-deny-box">
						<p>잘못된 접근입니다.</p>
					</div>
				</div>
			</section>
		</c:otherwise>

	</c:choose>

	<%@ include file="/common/footer.jsp"%>

	<script src="${pageContext.request.contextPath}/js/jquery-3.3.1.min.js"></script>
	<script src="${pageContext.request.contextPath}/js/bootstrap.min.js"></script>
	<script src="${pageContext.request.contextPath}/js/player.js"></script>
	<script
		src="${pageContext.request.contextPath}/js/jquery.nice-select.min.js"></script>
	<script src="${pageContext.request.contextPath}/js/mixitup.min.js"></script>
	<script src="${pageContext.request.contextPath}/js/jquery.slicknav.js"></script>
	<script src="${pageContext.request.contextPath}/js/owl.carousel.min.js"></script>
	<script src="${pageContext.request.contextPath}/js/main.js"></script>

	<script>
(function(){
  var ctx = '${pageContext.request.contextPath}';

  /* 썸네일 URL 보정 */
  function resolveThumb(url){
    var fallback = ctx + '/images/anisample/bleach.jpg';
    if (!url) return fallback;
    var u = String(url).trim();
    if (!u) return fallback;
    if (/^https?:\/\//i.test(u)) return u;
    if (u.charAt(0) === '/') return ctx + u;
    return ctx + '/' + u;
  }

  function esc(s){
    var str = (s === null || s === undefined) ? '' : String(s);
    return str.replace(/[&<>"']/g, function(m){
      return ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;','\'':'&#39;'}[m]);
    });
  }

  function hasRelDebug(){
    return (location.search.indexOf('rel_debug=1') >= 0);
  }

  function endsWithJamo(text){
    if (!text) return false;
    var ch = text.charCodeAt(text.length - 1);
    return (ch >= 0x3131 && ch <= 0x318E); // ㄱ-ㅎ/ㅏ-ㅣ
  }

  document.addEventListener('DOMContentLoaded', function(){

    /* CKEditor - NEWS */
    var newsEditorEl = document.querySelector('#editor');
    if (newsEditorEl && window.ClassicEditor) {
      ClassicEditor.create(newsEditorEl, {
        simpleUpload: { uploadUrl: ctx + '/ContentImageUpload?type=news' }
      }).catch(function(error){
        console.error('[CKEditor NEWS 초기화 실패]', error);
      });
    }

    /* CKEditor - BOARD */
    var boardEditorEl = document.querySelector('#boardEditor');
    if (boardEditorEl && window.ClassicEditor) {
      ClassicEditor.create(boardEditorEl, {
        simpleUpload: { uploadUrl: ctx + '/ContentImageUpload?type=board' }
      }).catch(function(error){
        console.error('[CKEditor BOARD 초기화 실패]', error);
      });
    }

    /* 썸네일 미리보기 */
    var thumbInput = document.getElementById('thumbFile');
    var previewImg = document.getElementById('thumbPreviewImg');
    var fileNameEl = document.getElementById('thumbFileName');

    if (thumbInput && previewImg) {
      thumbInput.addEventListener('change', function(){
        var file = this.files && this.files[0];
        if (!file) return;
        if (fileNameEl) fileNameEl.textContent = file.name;

        var reader = new FileReader();
        reader.onload = function(e){
          previewImg.src = e.target.result;
          previewImg.style.display = 'block';
        };
        reader.readAsDataURL(file);
      });
    }

    /* 관련 애니 검색 */

    var input        = document.getElementById('animeSearchInput');
    var resultBox    = document.getElementById('animeSearchResult');
    var hiddenId     = document.getElementById('animeIdHidden');
    var relatedWrap  = document.getElementById('relatedWrap');
    var selectedCard = document.getElementById('animeSelectedCard');
    var warnEl       = document.getElementById('animeSelectWarn');

    if (!input || !resultBox) return;

    // 중복 바인딩 방지
    if (input.dataset.relAnimeBound === '1') return;
    input.dataset.relAnimeBound = '1';

    var REL_DBG = hasRelDebug(); // rel_debug=1 일때만 콘솔 출력
    var REL_ID  = Math.random().toString(16).slice(2, 7);
    function dbg(tag, extra){
      if (!REL_DBG) return;
      try {
        console.log('REL-ANIME[' + REL_ID + '] ' + tag, extra || '');
      } catch(e) {}
    }

    var timer = null;
    var abortCtrl = null;
    var scheduledKeyword = ''; // 같은 키워드 타이머 중복 방지
    var inflight = false;
    var inflightKeyword = '';
    var lastSentAt = 0;

    var currentList = [];
    var activeIndex = -1;

    var lastValue = input.value || '';
    var lastQuery = '';

    var composing = false;
    var hoveringResult = false;
    var pickingFromList = false;
    var ignoreInputOnce = false;

    var pickedTitle = '';

    var lockUntil = 0;
    var pendingKeyword = '';
    var stashedList = null;
    var stashedQuery = '';

    function nowTs(){
      return (window.performance && performance.now) ? performance.now() : Date.now();
    }
    function isLocked(){
      return nowTs() < lockUntil;
    }
    function lock(ms){
      lockUntil = nowTs() + ms;
      dbg('LOCK', { ms: ms });
    }

    function cancelTimerOnly(){
      if (timer) { clearTimeout(timer); timer = null; }
      scheduledKeyword = '';
    }

    function abortRequestOnly(){
      if (abortCtrl) { try { abortCtrl.abort(); } catch(e){} }
      abortCtrl = null;
      inflight = false;
      inflightKeyword = '';
    }

    function cancelPending(){
      cancelTimerOnly();
      abortRequestOnly();
    }

    function doClearPicked(){
      lock(200);
      pickingFromList = true;

      cancelPending();

      input.value = '';
      lastValue = '';
      lastQuery = '';
      pendingKeyword = '';
      stashedQuery = '';
      stashedList = null;

      ignoreInputOnce = false;

      clearPickedUI();
      hideResult();

      composing = false;
      hoveringResult = false;
      pickingFromList = false;

      input.focus();
      dbg('doClearPicked');
    }

    function bindClearDelegation(){
      if (!selectedCard) return;
      if (selectedCard.dataset.relAnimeClearBound === '1') return;
      selectedCard.dataset.relAnimeClearBound = '1';

      function handler(ev){
        var btn = (ev.target && ev.target.closest) ? ev.target.closest('.anime-clear-btn') : null;
        if (!btn) return;
        ev.preventDefault();
        ev.stopPropagation();
        doClearPicked();
      }

      selectedCard.addEventListener('pointerdown', handler, true);
      selectedCard.addEventListener('click', handler, false);
    }
    bindClearDelegation();

    function showResult(){
      resultBox.style.display = 'block';
      dbg('showResult');
    }

    function hideResult(){
      resultBox.style.display = 'none';
      resultBox.innerHTML = '';
      currentList = [];
      activeIndex = -1;
      hoveringResult = false;
      dbg('hideResult');
    }

    function renderHint(message){
      currentList = [];
      activeIndex = -1;
      resultBox.innerHTML = '<li class="is-hint anime-search-hint">' + esc(message) + '</li>';
      showResult();
      dbg('renderHint', { message: message });
    }

    function setPickedUI(on){
      if (!relatedWrap) return;
      relatedWrap.classList.toggle('is-picked', !!on);
      relatedWrap.classList.remove('is-invalid');
    }

    function showWarn(msg){
      if (!warnEl || !relatedWrap) return;
      warnEl.textContent = msg;
      warnEl.style.display = 'block';
      relatedWrap.classList.add('is-invalid');
    }

    function hideWarn(){
      if (!warnEl || !relatedWrap) return;
      warnEl.style.display = 'none';
      warnEl.textContent = '';
      relatedWrap.classList.remove('is-invalid');
    }

    function clearPickedUI(){
      if (hiddenId) hiddenId.value = '';
      pickedTitle = '';
      if (selectedCard){
        selectedCard.style.display = 'none';
        selectedCard.innerHTML = '';
      }
      setPickedUI(false);
      hideWarn();
    }

    function renderSelectedCard(anime){
      if (!selectedCard) return;

      var thumb = resolveThumb(anime.animeThumbnailUrl);
      selectedCard.innerHTML =
        '<img class="anime-thumb" src="' + thumb + '" alt="">' +
        '<div class="anime-meta">' +
          '<div class="anime-title">' + esc(anime.animeTitle) +
            '<span class="picked-pill">선택됨</span>' +
          '</div>' +
          '<div class="anime-sub">' +
            (anime.animeYear ? '<span class="badge">' + esc(anime.animeYear) + '</span>' : '') +
            (anime.animeQuarter ? '<span class="badge">' + esc(anime.animeQuarter) + '</span>' : '') +
          '</div>' +
        '</div>' +
        '<button type="button" class="anime-clear-btn" id="animeClearBtn">선택 해제</button>';

      selectedCard.style.display = 'flex';
    }

    function selectAnime(anime){
      if (!hiddenId) return;

      pickingFromList = true;
      lock(700);
      ignoreInputOnce = true;

      cancelPending();
      pendingKeyword = '';
      stashedList = null;
      stashedQuery = '';

      input.value = anime.animeTitle;
      lastValue = input.value;
      hiddenId.value = anime.animeId;
      pickedTitle = anime.animeTitle;

      renderSelectedCard(anime);
      setPickedUI(true);
      hideWarn();
      hideResult();

      dbg('selectAnime', { title: anime.animeTitle, id: anime.animeId });

      setTimeout(function(){
        pickingFromList = false;
      }, 0);
    }

    function setActive(idx){
      var items = resultBox.querySelectorAll('li[data-index]');
      for (var i=0; i<items.length; i++) items[i].classList.remove('is-active');

      activeIndex = idx;
      if (idx < 0) return;

      var target = resultBox.querySelector('li[data-index="' + idx + '"]');
      if (!target) return;
      target.classList.add('is-active');

      var top = target.offsetTop;
      var bottom = top + target.offsetHeight;
      if (top < resultBox.scrollTop) resultBox.scrollTop = top - 6;
      if (bottom > resultBox.scrollTop + resultBox.clientHeight) {
        resultBox.scrollTop = bottom - resultBox.clientHeight + 6;
      }
    }

    function renderList(list, keyword){
      resultBox.innerHTML = '';
      currentList = [];
      activeIndex = -1;

      if (!list || list.length === 0){
        renderHint('검색 결과가 없습니다.');
        return;
      }

      currentList = list;
      for (var i=0; i<list.length; i++){
        var anime = list[i];
        var li = document.createElement('li');
        li.setAttribute('data-index', String(i));

        var thumb = resolveThumb(anime.animeThumbnailUrl);
        li.innerHTML =
          '<img class="anime-thumb" src="' + thumb + '" alt="">' +
          '<div class="anime-meta">' +
            '<div class="anime-title">' + esc(anime.animeTitle) + '</div>' +
            '<div class="anime-sub">' +
              (anime.animeYear ? '<span class="badge">' + esc(anime.animeYear) + '</span>' : '') +
              (anime.animeQuarter ? '<span class="badge">' + esc(anime.animeQuarter) + '</span>' : '') +
            '</div>' +
          '</div>';

        resultBox.appendChild(li);
      }

      showResult();
      setActive(0);
      dbg('renderList', { keyword: keyword, count: list.length });
    }

    function doFetch(keyword){

      // 페이지 전체에서 동일 키워드 중복 호출 방지
      window.__REL_ANIME_GUARD__ = window.__REL_ANIME_GUARD__ || { k: '', t: 0 };
      var g = window.__REL_ANIME_GUARD__;
      var tnow = Date.now();
      if (g.k === keyword && (tnow - g.t) < 1200) {
        dbg('GLOBAL GUARD dedup', { keyword: keyword, dt: (tnow - g.t) });
        return;
      }
      g.k = keyword;
      g.t = tnow;

      // 아주 짧은 시간 내 동일 키워드 재호출 방지 (중복 GET 방지)
      var now = Date.now();
      if (keyword === lastQuery && (now - lastSentAt) < 1000){
        dbg('doFetch dedup by time', { keyword: keyword, dt: (now - lastSentAt) });
        return;
      }

      // 같은 키워드가 이미 요청 중이면 추가 요청 금지
      if (inflight && inflightKeyword === keyword){
        dbg('doFetch skip inflight same', { keyword: keyword });
        return;
      }

      // 다른 키워드 요청이면 이전 요청 abort
      if (inflight && inflightKeyword !== keyword){
        if (abortCtrl) { try { abortCtrl.abort(); } catch(e){} }
        abortCtrl = null;
        inflight = false;
        inflightKeyword = '';
      }

      cancelPending();

      if (hoveringResult){
        pendingKeyword = keyword;
        dbg('doFetch blocked by hover -> pendingKeyword', { pendingKeyword: pendingKeyword });
        return;
      }

      if (hiddenId && String(hiddenId.value || '').trim().length > 0) {
        dbg('doFetch blocked by picked');
        return;
      }

      if (keyword === lastQuery && currentList.length > 0 && resultBox.style.display === 'block'){
        dbg('doFetch skip same lastQuery');
        return;
      }

      if (resultBox.style.display !== 'block' || currentList.length === 0){
        renderHint('검색 중...');
      }

      abortCtrl = new AbortController();
      inflight = true;
      inflightKeyword = keyword;

      lastQuery = keyword;
      lastSentAt = now;

      var myQuery = keyword;
      var localCtrl = abortCtrl;

      dbg('FETCH start', { myQuery: myQuery });

      fetch(ctx + '/NewsAnimeSearch?keyword=' + encodeURIComponent(keyword), { signal: localCtrl.signal })
        .then(function(res){ return res.json(); })
        .then(function(list){
          dbg('FETCH done', { myQuery: myQuery, lastQuery: lastQuery, count: (list ? list.length : 0) });

          if (myQuery !== lastQuery) { dbg('FETCH ignore late'); return; }
          if (hiddenId && String(hiddenId.value || '').trim().length > 0) { dbg('FETCH ignore picked'); return; }

          if (hoveringResult || pickingFromList || isLocked()){
            stashedList = list || [];
            stashedQuery = myQuery;
            dbg('FETCH stashed (hover/pick/lock)', { stashedQuery: stashedQuery, count: stashedList.length });
            return;
          }

          renderList(list || [], myQuery);
        })
        .catch(function(err){
          if (err && err.name === 'AbortError') { dbg('FETCH aborted'); return; }
          console.error('[NewsAnimeSearch 오류]', err);
          renderHint('검색 중 오류가 발생했습니다.');
        })
        .finally(function(){
          if (abortCtrl === localCtrl){
            abortCtrl = null;
            inflight = false;
            inflightKeyword = '';
          }
        });
    }

    function scheduleSearch(keyword){

      // 전역가드 조건이면 "예약 자체"를 안 함 (불필요한 타이머/시도 제거)
      var gg = window.__REL_ANIME_GUARD__;
      if (gg && gg.k === keyword && (Date.now() - gg.t) < 1200) {
        dbg('scheduleSearch skip by GLOBAL GUARD', { keyword: keyword });
        return;
      }

      if (timer && scheduledKeyword === keyword){
        dbg('scheduleSearch skip same scheduled', { keyword: keyword });
        return;
      }

      if (inflight && inflightKeyword === keyword){
        dbg('scheduleSearch skip inflight same', { keyword: keyword });
        return;
      }

      cancelTimerOnly();
      scheduledKeyword = keyword;

      timer = setTimeout(function(){
        scheduledKeyword = '';
        doFetch(keyword);
      }, 250);

      dbg('scheduleSearch', { keyword: keyword });
    }

    function handleValueChanged(force){
      var raw = input.value || '';
      var keyword = raw.trim();

      if (isLocked() || pickingFromList){
        lastValue = raw;
        dbg('handleValueChanged blocked', { force: force });
        return;
      }

      if (ignoreInputOnce){
        ignoreInputOnce = false;
        lastValue = raw;
        dbg('handleValueChanged ignore once');
        return;
      }

      if (!force && raw === lastValue) return;
      lastValue = raw;

      var picked = hiddenId && String(hiddenId.value || '').trim().length > 0;
      if (picked && pickedTitle && keyword !== pickedTitle){
        clearPickedUI();
        dbg('picked cleared by typing');
      }

      //    IME 특성: 결과 뜬 직후 값이 잠깐 1글자로 튀는 경우 방어
      //    방금 2글자 이상 검색을 보냈고 아주 최근이면 그 짧아짐 이벤트는 무시해서 hideResult / 재검색 스케줄이 발생하지 않게 막는다.
      if (keyword.length < 2 && lastQuery && lastQuery.length >= 2 && (Date.now() - lastSentAt) < 1500) {
        dbg('ignore transient short keyword', { raw: raw, lastQuery: lastQuery });
        return;
      }

      if (keyword.length < 2){
        lastQuery = '';
        pendingKeyword = '';
        stashedList = null;
        stashedQuery = '';
        cancelPending();
        hideResult();
        return;
      }

      if (hoveringResult && !force){
        pendingKeyword = keyword;
        dbg('pendingKeyword set by hover', { pendingKeyword: pendingKeyword });
        return;
      }

      // 조합중인데 끝이 자모로 끝나면 스킵 (ㄱ/ㅏ 등)
      if (!force && composing && endsWithJamo(keyword)){
        dbg('skip composing jamo tail', { keyword: keyword });
        return;
      }

      scheduleSearch(keyword);
    }

    /* 이벤트 */

    input.addEventListener('focus', function(){
      if (resultBox.innerHTML && resultBox.innerHTML.trim().length > 0) showResult();
    });

    input.addEventListener('compositionstart', function(){
      composing = true;
      dbg('EVT compositionstart');
    });

    input.addEventListener('compositionend', function(){
      composing = false;
      dbg('EVT compositionend');
      // 여기서 handleValueChanged 호출하지 않음 (중복/튀는 이벤트의 원인 구간 차단)
      // 검색은 input 이벤트에서만 처리
    });

    input.addEventListener('input', function(e){
      var isComp = !!(e && e.isComposing);

      if (isComp) composing = true;

      dbg('EVT input', { isComposing: isComp, v: input.value });

      // 조합중에도 2글자 완성되면 바로 검색되게 (끝자모는 handleValueChanged가 차단)
      handleValueChanged(false);
    });

    resultBox.addEventListener('pointerenter', function(){
      hoveringResult = true;
      dbg('EVT result pointerenter');
    });

    resultBox.addEventListener('pointerleave', function(){
      hoveringResult = false;
      dbg('EVT result pointerleave');

      if (stashedList !== null){
        var q = stashedQuery;
        var list = stashedList;
        stashedList = null;
        stashedQuery = '';
        renderList(list, q);
        return;
      }

      if (pendingKeyword){
        var k = pendingKeyword;
        pendingKeyword = '';
        scheduleSearch(k);
      }
    });

    resultBox.addEventListener('pointerdown', function(ev){
      var li = ev.target.closest('li[data-index]');
      dbg('EVT result pointerdown', { hasLi: !!li, tag: (ev.target && ev.target.tagName) });
      if (!li) return;

      ev.preventDefault();
      ev.stopPropagation();

      var idx = parseInt(li.getAttribute('data-index'), 10);
      var anime = currentList && currentList[idx];
      if (anime) selectAnime(anime);
    }, true);

    resultBox.addEventListener('mouseover', function(ev){
      var li = ev.target.closest('li[data-index]');
      if (!li) return;
      var idx = parseInt(li.getAttribute('data-index'), 10);
      if (!isNaN(idx)) setActive(idx);
    });

    document.addEventListener('click', function(e){
      if (!resultBox.contains(e.target) && e.target !== input) hideResult();
    });

    input.addEventListener('keydown', function(e){
      if (resultBox.style.display !== 'block') return;
      var max = currentList.length - 1;

      if (e.key === 'Escape') { hideResult(); return; }

      if (e.key === 'ArrowDown'){
        e.preventDefault();
        if (max < 0) return;
        setActive(activeIndex < max ? activeIndex + 1 : 0);
        return;
      }

      if (e.key === 'ArrowUp'){
        e.preventDefault();
        if (max < 0) return;
        setActive(activeIndex > 0 ? activeIndex - 1 : max);
        return;
      }

      if (e.key === 'Enter'){
        if (max < 0) return;
        e.preventDefault();
        var pick = (activeIndex >= 0) ? activeIndex : 0;
        var anime = currentList[pick];
        if (anime) selectAnime(anime);
      }
    });

    var form = input.closest('form');
    if (form){
      form.addEventListener('submit', function(e){
        var hasText = input.value.trim().length > 0;
        var picked = hiddenId && String(hiddenId.value || '').trim().length > 0;

        if (hasText && !picked){
          e.preventDefault();
          showWarn('⚠️ 목록에서 애니를 클릭해서 "선택"해야 저장됩니다.\n(지금은 텍스트만 입력된 상태예요)');
          input.focus();
        }
      });
    }

  });
})();
	</script>

</body>
</html>
