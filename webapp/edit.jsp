<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:set var="typeRaw" value="${empty param.type ? requestScope.type : param.type}" />
<c:set var="type" value="${fn:toUpperCase(typeRaw)}" />
<c:set var="isEditPage" value="true" />

<c:if test="${type eq 'NEWS'}">
  <c:set var="activeMenu" value="news" />
</c:if>
<c:if test="${type eq 'BOARD'}">
  <c:set var="activeMenu" value="board" />
</c:if>

<%-- ★ 핵심: PageAction이 넘긴 newsData / boardData를 post로 통일 바인딩 --%>
<c:choose>
  <c:when test="${type eq 'NEWS'}">
    <c:set var="post" value="${requestScope.newsData}" />
  </c:when>
  <c:when test="${type eq 'BOARD'}">
    <c:set var="post" value="${requestScope.boardData}" />
  </c:when>
</c:choose>

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>AniMale | 수정</title>

<!-- CKEditor 5 커스텀 빌드 -->
<script src="${pageContext.request.contextPath}/js/ckeditor.js?v=20260102_6"></script>

<link rel="icon" type="image/png" href="${pageContext.request.contextPath}/favicon.png">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/elegant-icons.css">

<link href="https://fonts.googleapis.com/css2?family=Oswald:wght@300;400;500;600;700&display=swap" rel="stylesheet">
<link href="https://fonts.googleapis.com/css2?family=Mulish:wght@300;400;500;600;700;800;900&display=swap" rel="stylesheet">

<link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/font-awesome.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">

<style>
/* 상단 검색 UI 숨김 */
.header__right .search-switch, .search-model, .fa-search { display: none !important; }

/* 썸네일 미리보기 */
.preview-box{
  aspect-ratio: 3/4;
  background:#1e1e30;
  border-radius:16px;
  border:1px solid rgba(255,255,255,0.15);
  display:flex;
  align-items:center;
  justify-content:center;
  overflow:hidden;
}
.preview-img{
  width:100%;
  height:100%;
  object-fit:contain;
  display:none;
}

/* 폼 */
.admin-form label{ color:#ffffff !important; font-weight:600; }
.admin-form .form-control{ background:#fff; border-radius:8px; }

.thumb-btn{
  background:#e53637;
  color:#fff;
  padding:8px 16px;
  border-radius:6px;
  cursor:pointer;
  font-size:14px;
  display:inline-block;
  margin-right:12px;
}
.thumb-file{ display:none; }
.thumb-filename{
  color:rgba(255,255,255,0.85);
  font-size:14px;
  vertical-align:middle;
}

/* 썸네일 되돌리기 버튼 */
.thumb-reset-btn{
  background:#2b2b3c;
  color:#fff;
  padding:8px 14px;
  border-radius:6px;
  border:1px solid rgba(255,255,255,0.18);
  cursor:pointer;
  font-size:13px;
  display:inline-block;
}
.thumb-reset-btn:hover{ background:#35354a; }

/* CKEditor - 글자 색 */
.ck-editor__editable, .ck-editor__editable * { color:#000 !important; }
.ck-content, .ck-content * { color:#000 !important; }
.ck-editor__editable.ck-placeholder::before { color:#888 !important; }

/* 버튼 */
.btn-submit{
  background:#e53637;
  color:#fff;
  padding:10px 24px;
  border-radius:6px;
  border:none;
}
.btn-cancel{
  background:#444;
  color:#fff;
  padding:10px 24px;
  border-radius:6px;
}
.form-actions{
  display:flex;
  justify-content:flex-end;
  gap:10px;
  margin-top:24px;
}

.access-deny-box{
  background:rgba(0,0,0,0.25);
  border:1px solid rgba(255,255,255,0.15);
  border-radius:12px;
  padding:22px;
  color:#fff;
}

.preview-help{
  color:rgba(255,255,255,0.75);
  font-size:13px;
  margin-top:10px;
}

/* 관련 애니 검색 드롭다운 */
.related-wrap{ margin-top:24px; position:relative; }

.anime-search-result{
  position:absolute;
  top: calc(100% + 6px);
  left:0; right:0;
  background: rgba(20,20,35,0.98);
  border: 1px solid rgba(255,255,255,0.12);
  border-radius: 12px;
  box-shadow: 0 12px 30px rgba(0,0,0,0.35);
  max-height: 280px;
  overflow:auto;
  display:none;
  z-index: 9999;
  padding: 6px;
  backdrop-filter: blur(6px);
}

.anime-search-result li{
  padding: 10px 12px;
  border-radius: 10px;
  cursor:pointer;
  color:#fff;
  display:flex;
  align-items:center;
  gap:10px;
  user-select:none;
}

.anime-search-result li:hover,
.anime-search-result li.is-active{
  background: rgba(229,54,55,0.18);
  outline: 1px solid rgba(229,54,55,0.25);
}

.anime-search-result li.is-hint{
  cursor: default;
  background: transparent !important;
  outline: none !important;
}

.anime-search-hint{
  padding: 10px 12px;
  color: rgba(255,255,255,0.7);
  font-size: 13px;
}

.anime-thumb{
  width:34px;
  height:46px;
  border-radius:8px;
  object-fit:cover;
  background: rgba(255,255,255,0.08);
  border: 1px solid rgba(255,255,255,0.12);
  flex: 0 0 auto;
}

.anime-meta{
  display:flex;
  flex-direction:column;
  min-width:0;
  flex:1;
}
.anime-title{
  font-weight:700;
  font-size:14px;
  line-height:1.1;
  white-space:nowrap;
  overflow:hidden;
  text-overflow:ellipsis;
}
.anime-sub{
  display:flex;
  gap:6px;
  margin-top:6px;
  flex-wrap:wrap;
}
.anime-sub .badge{
  font-size:12px;
  padding:2px 8px;
  border-radius:999px;
  background: rgba(255,255,255,0.10);
  color: rgba(255,255,255,0.9);
  border: 1px solid rgba(255,255,255,0.10);
}

/* CKEditor 본문/이미지 캡션 보정 */
.ck-editor__editable{
  min-height: 200px !important;
  max-height: 600px;
  overflow-y: auto;
}

.ck-content figure.image{
  display: table;
  margin: 0;
  max-width: 100%;
}
.ck-content figure.image > img{
  display:block;
  max-width:100%;
  height:auto;
}
.ck-content figure.image > figcaption{
  padding: 4px 0 !important;
  line-height: 1.2 !important;
  font-size: 13px !important;
  text-align: center;
  min-height: 0 !important;
  margin: 0 !important;
  color: #666 !important;
}
</style>
</head>

<body>
<%@ include file="/common/header.jsp"%>

<c:choose>

  <%-- NEWS 수정 --%>
  <c:when test="${type eq 'NEWS'}">
    <c:choose>
      <c:when test="${not empty sessionScope.memberId and sessionScope.memberRole eq 'ADMIN'}">

        <section class="anime-details spad">
          <div class="container">
            <div class="row">

              <div class="col-lg-3">
                <div class="preview-box">
                  <img id="thumbPreviewImg" class="preview-img" alt="thumbnail preview">
                </div>
                <div class="preview-help">썸네일을 변경하면 즉시 반영됩니다.</div>
              </div>

              <div class="col-lg-9">
                <h3 style="color:#fff;">뉴스 수정</h3>

                <form class="admin-form" method="post"
                      action="${pageContext.request.contextPath}/newsEdit.do"
                      enctype="multipart/form-data">

                  <%-- 수정 PK --%>
                  <input type="hidden" name="newsId" value="${post.newsId}">

                  <%-- 관련 애니 ID (기존값 유지 + 변경 가능) --%>
                  <input type="hidden" name="animeId" id="animeIdHidden" value="${post.animeId}">

                  <%-- 새 파일 없으면 기존 유지용 --%>
                  <input type="hidden" name="existingThumbUrl" id="existingThumbUrl" value="${post.newsThumbnailUrl}">
                  <input type="hidden" name="existingImageUrl" id="existingImageUrl" value="${post.newsImageUrl}">

                  <div class="form-group">
                    <label>제목</label>
                    <input type="text" class="form-control" name="newsTitle" value="${post.newsTitle}">
                  </div>

                  <div class="form-group">
                    <label>썸네일</label><br>
                    <label class="thumb-btn" for="thumbFile">파일 선택</label>
                    <span class="thumb-filename" id="thumbFileName"></span>
                    <button type="button" id="thumbResetBtn" class="thumb-reset-btn" style="display:none;">되돌리기</button>
                    <input type="file" id="thumbFile" name="thumbFile" class="thumb-file" accept="image/*">
                  </div>

                  <div class="form-group">
                    <label>상세 내용</label>
                    <textarea id="editor" name="newsContent">${post.newsContent}</textarea>
                  </div>

                  <div class="related-wrap">
                    <label style="color:#fff;">관련 애니</label>
                    <input type="text"
                           id="animeSearchInput"
                           class="form-control"
                           placeholder="애니 제목을 입력하세요"
                           autocomplete="off"
                           value="${post.animeTitle}">
                    <ul id="animeSearchResult" class="anime-search-result"></ul>
                  </div>

                  <div class="form-actions">
                    <button type="submit" class="btn-submit">수정 완료</button>
                    <a href="${pageContext.request.contextPath}/newsDetail.do?newsId=${post.newsId}" class="btn-cancel">취소</a>
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

  <%-- BOARD 수정 --%>
  <c:when test="${type eq 'BOARD'}">
    <c:choose>
      <c:when test="${not empty sessionScope.memberId}">

        <c:set var="canEdit"
               value="${sessionScope.memberRole eq 'ADMIN' or sessionScope.memberId eq post.memberId}" />

        <c:choose>
          <c:when test="${canEdit}">

            <section class="anime-details spad">
              <div class="container">

                <form class="admin-form" method="post"
                      action="${pageContext.request.contextPath}/boardEdit.do">

                  <h3 style="color:#fff;">게시글 수정</h3>

                  <input type="hidden" name="boardId" value="${post.boardId}">
                  <input type="hidden" name="boardCategory" value="${post.boardCategory}">

                  <div class="form-group">
                    <label>게시글 제목</label>
                    <input type="text" class="form-control" name="boardTitle" value="${post.boardTitle}">
                  </div>

                  <div class="form-group">
                    <label>텍스트 내용</label>
                    <textarea id="boardEditor" name="boardContent">${post.boardContent}</textarea>
                  </div>

                  <div class="form-actions">
                    <button type="submit" class="btn-submit">수정 완료</button>
                    <a href="${pageContext.request.contextPath}/boardDetail.do?boardId=${post.boardId}" class="btn-cancel">취소</a>
                  </div>

                </form>

              </div>
            </section>

          </c:when>

          <c:otherwise>
            <section class="anime-details spad">
              <div class="container">
                <div class="access-deny-box">
                  <p>게시글 수정은 관리자 또는 본인 작성자만 가능합니다.</p>
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
              <p>게시글 수정은 로그인 후 가능합니다.</p>
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
<script src="${pageContext.request.contextPath}/js/jquery.nice-select.min.js"></script>
<script src="${pageContext.request.contextPath}/js/mixitup.min.js"></script>
<script src="${pageContext.request.contextPath}/js/jquery.slicknav.js"></script>
<script src="${pageContext.request.contextPath}/js/owl.carousel.min.js"></script>
<script src="${pageContext.request.contextPath}/js/main.js"></script>

<script>
const ctx = '${pageContext.request.contextPath}';

function esc(s){
  return String(s ?? '').replace(/[&<>"']/g, m => ({
    '&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'
  }[m]));
}

/* 경로 보정 (뉴스 썸네일/본문/검색결과 공통)
   - data: / http(s): 그대로
   - 이미 ctx 포함이면 그대로
   - "/upload/.." -> ctx + "/upload/.."
   - "upload/.."  -> ctx + "/upload/.."
   - "/ANIMale/upload/.." 같이 ctx가 경로에 포함된 형태도 방어
*/
function resolvePath(url){
  if (!url) return '';
  const u = String(url).trim();
  if (!u) return '';

  if (/^data:/i.test(u)) return u;
  if (/^https?:\/\//i.test(u)) return u;

  // ctx 중복 방지 ("/ANIMale/..." 형태)
  if (ctx && (u === ctx || u.startsWith(ctx + '/'))) return u;

  // 혹시 "ANIMale/..." 같이 ctx 슬래시가 누락된 저장값 방어
  const ctxNoSlash = ctx ? ctx.replace(/^\//,'') : '';
  if (ctxNoSlash && (u === ctxNoSlash || u.startsWith(ctxNoSlash + '/'))) return '/' + u;

  if (u.startsWith('/')) return ctx + u;
  return ctx + '/' + u;
}

/* 본문 HTML의 이미지 src 보정
   - src="upload/..." 또는 src="/upload/..." 같은 상대경로를 ctx 기준 절대경로로 치환
   - 이미 http(s)/data/ctx 포함이면 유지
*/
function normalizeHtmlImageSrc(html){
  if (!html) return html;

  // src="upload/..."  -> src="ctx/upload/..."
  html = html.replace(/src=(["'])(upload\/[^"']+)\1/gi, function(_, q, path){
    return 'src=' + q + (ctx + '/' + path) + q;
  });

  // src="/upload/..." -> src="ctx/upload/..."
  html = html.replace(/src=(["'])(\/upload\/[^"']+)\1/gi, function(_, q, path){
    return 'src=' + q + (ctx + path) + q;
  });

  // src="ANIMale/upload/..."(ctx 없이 저장된 경우) 방어
  const ctxNoSlash = ctx ? ctx.replace(/^\//,'') : '';
  if (ctxNoSlash) {
    const re = new RegExp('src=(["\'])(' + ctxNoSlash + '\\/upload\\/[^\\"\\\']+)\\1','gi');
    html = html.replace(re, function(_, q, path){
      return 'src=' + q + '/' + path + q;
    });
  }

  return html;
}

/* NewsAnimeSearch 응답 키 불일치 방어용 매퍼
   - camelCase / snake_case 둘 다 지원
*/
function mapAnime(raw){
  const a = raw || {};
  return {
    animeId: (a.animeId ?? a.anime_id ?? ''),
    animeTitle: (a.animeTitle ?? a.anime_title ?? ''),
    animeThumbnailUrl: (a.animeThumbnailUrl ?? a.anime_thumbnail_url ?? ''),
    animeYear: (a.animeYear ?? a.anime_year ?? ''),
    animeQuarter: (a.animeQuarter ?? a.anime_quarter ?? '')
  };
}

document.addEventListener('DOMContentLoaded', function () {

  /* 1) 수정페이지 진입 시 본문 이미지 src 먼저 보정 (에디터 생성 전에 textarea 값 정리) */
  const newsTextArea = document.getElementById('editor');
  if (newsTextArea) {
    newsTextArea.value = normalizeHtmlImageSrc(newsTextArea.value);
  }
  const boardTextArea = document.getElementById('boardEditor');
  if (boardTextArea) {
    boardTextArea.value = normalizeHtmlImageSrc(boardTextArea.value);
  }

  /* 2) CKEditor - NEWS */
  const newsEditorEl = document.querySelector('#editor');
  if (newsEditorEl) {
    ClassicEditor.create(newsEditorEl, {
      simpleUpload: { uploadUrl: ctx + '/ContentImageUpload?type=news' }
    }).catch(error => console.error('[CKEditor NEWS 초기화 실패]', error));
  }

  /* 3) CKEditor - BOARD */
  const boardEditorEl = document.querySelector('#boardEditor');
  if (boardEditorEl) {
    ClassicEditor.create(boardEditorEl, {
      simpleUpload: { uploadUrl: ctx + '/ContentImageUpload?type=board' }
    }).catch(error => console.error('[CKEditor BOARD 초기화 실패]', error));
  }

  /* 4) 썸네일 미리보기 */
  const thumbInput = document.getElementById('thumbFile');
  const previewImg = document.getElementById('thumbPreviewImg');
  const fileNameEl = document.getElementById('thumbFileName');
  const existingThumbEl = document.getElementById('existingThumbUrl');
  const resetBtn = document.getElementById('thumbResetBtn');

  const originalThumb = (existingThumbEl && existingThumbEl.value) ? existingThumbEl.value : '';
  const originalThumbFixed = originalThumb ? resolvePath(originalThumb) : '';

  // 4-1) 진입 시 기존 썸네일 표시
  if (previewImg && originalThumbFixed) {
    previewImg.src = originalThumbFixed;
    previewImg.style.display = 'block';
    previewImg.onerror = function () {
      // 실패 시 샘플 고정으로 바꾸지 말고 숨김 처리 (실데이터 기반 유지)
      this.style.display = 'none';
    };

    // 기존 썸네일이 있으면 되돌리기 버튼 활성
    if (resetBtn) resetBtn.style.display = 'inline-block';
  }

  // 4-2) 새 파일 선택 시 즉시 미리보기 + 되돌리기 노출
  if (thumbInput && previewImg) {
    thumbInput.addEventListener('change', function () {
      const file = this.files && this.files[0];
      if (!file) return;

      if (fileNameEl) fileNameEl.textContent = file.name;

      const reader = new FileReader();
      reader.onload = function (e) {
        previewImg.src = e.target.result; // dataURL
        previewImg.style.display = 'block';
        previewImg.onerror = null;        // dataURL은 굳이 onerror 필요 없음
        if (resetBtn) resetBtn.style.display = 'inline-block';
      };
      reader.readAsDataURL(file);
    });
  }

  // 4-3) 되돌리기: 파일 선택 취소 + 기존 썸네일로 복원
  if (resetBtn && thumbInput && previewImg) {
    resetBtn.addEventListener('click', function(){
      // file input 초기화
      thumbInput.value = '';
      if (fileNameEl) fileNameEl.textContent = '';

      if (originalThumbFixed) {
        previewImg.src = originalThumbFixed;
        previewImg.style.display = 'block';
        previewImg.onerror = function () { this.style.display = 'none'; };
      } else {
        // 원래 썸네일이 없던 글이면 미리보기 숨김
        previewImg.removeAttribute('src');
        previewImg.style.display = 'none';
      }
    });
  }

  /* 5) 관련 애니 검색 */
  const input = document.getElementById('animeSearchInput');
  const resultBox = document.getElementById('animeSearchResult');
  const hiddenId = document.getElementById('animeIdHidden');

  let timer = null;
  let activeIndex = -1;
  let currentList = [];

  function hideResult(){
    if (!resultBox) return;
    resultBox.style.display = 'none';
    resultBox.innerHTML = '';
    activeIndex = -1;
    currentList = [];
  }
  function showResult(){
    if (!resultBox) return;
    resultBox.style.display = 'block';
  }
  function renderHint(message){
    if (!resultBox) return;
    resultBox.innerHTML = '<li class="is-hint anime-search-hint">' + esc(message) + '</li>';
    showResult();
  }
  function selectAnime(anime){
    if (!input || !hiddenId) return;
    input.value = anime.animeTitle;
    hiddenId.value = anime.animeId;
    hideResult();
  }
  function setActive(idx){
    if (!resultBox) return;
    const items = Array.from(resultBox.querySelectorAll('li[data-index]'));
    items.forEach(li => li.classList.remove('is-active'));
    activeIndex = idx;

    if (idx < 0) return;
    const target = resultBox.querySelector('li[data-index="' + idx + '"]');
    if (!target) return;
    target.classList.add('is-active');

    const top = target.offsetTop;
    const bottom = top + target.offsetHeight;
    if (top < resultBox.scrollTop) resultBox.scrollTop = top - 6;
    if (bottom > resultBox.scrollTop + resultBox.clientHeight) {
      resultBox.scrollTop = bottom - resultBox.clientHeight + 6;
    }
  }

  document.addEventListener('click', function(e) {
    if (!input || !resultBox) return;
    if (!resultBox.contains(e.target) && e.target !== input) hideResult();
  });

  if (input && resultBox) {

    input.addEventListener('focus', function(){
      if (resultBox.innerHTML.trim().length > 0) showResult();
    });

    input.addEventListener('input', function () {
      const keyword = this.value.trim();

      // 사용자가 타이핑을 시작하면 선택된 animeId는 무효 처리
      if (hiddenId) hiddenId.value = '';

      clearTimeout(timer);

      if (keyword.length < 2) {
        hideResult();
        return;
      }

      renderHint('검색 중...');

      timer = setTimeout(() => {
        fetch(ctx + '/NewsAnimeSearch?keyword=' + encodeURIComponent(keyword))
          .then(res => res.json())
          .then(list => {
            resultBox.innerHTML = '';
            activeIndex = -1;

            if (!list || list.length === 0) {
              renderHint('검색 결과가 없습니다.');
              return;
            }

            // 키 불일치 방어 매핑
            currentList = list.map(mapAnime).filter(a => String(a.animeId).trim() !== '');

            if (currentList.length === 0) {
              renderHint('검색 결과 형식이 올바르지 않습니다.');
              return;
            }

            currentList.forEach((anime, idx) => {
              const li = document.createElement('li');
              li.setAttribute('data-index', idx);

              const thumb = resolvePath(anime.animeThumbnailUrl);

              li.innerHTML =
                '<img class="anime-thumb" src="' + esc(thumb) + '" alt="">' +
                '<div class="anime-meta">' +
                  '<div class="anime-title">' + esc(anime.animeTitle) + '</div>' +
                  '<div class="anime-sub">' +
                    (anime.animeYear ? '<span class="badge">' + esc(anime.animeYear) + '</span>' : '') +
                    (anime.animeQuarter ? '<span class="badge">' + esc(anime.animeQuarter) + '</span>' : '') +
                  '</div>' +
                '</div>';

              // blur로 닫히기 전에 선택되게 mousedown 사용
              li.addEventListener('mousedown', function(ev){
                ev.preventDefault();
                selectAnime(anime);
              });
              li.addEventListener('mouseenter', function(){ setActive(idx); });

              resultBox.appendChild(li);
            });

            showResult();
            setActive(0);
          })
          .catch(() => {
            renderHint('검색 중 오류가 발생했습니다.');
          });
      }, 250);
    });

    // 키보드 조작
    input.addEventListener('keydown', function(e) {
      if (resultBox.style.display !== 'block') return;

      const max = currentList.length - 1;

      if (e.key === 'Escape') {
        hideResult();
        return;
      }
      if (e.key === 'ArrowDown') {
        e.preventDefault();
        if (max < 0) return;
        const next = (activeIndex < max) ? activeIndex + 1 : 0;
        setActive(next);
        return;
      }
      if (e.key === 'ArrowUp') {
        e.preventDefault();
        if (max < 0) return;
        const prev = (activeIndex > 0) ? activeIndex - 1 : max;
        setActive(prev);
        return;
      }
      if (e.key === 'Enter') {
        if (max < 0) return; // hint만 있거나 비었으면 무시
        e.preventDefault();
        const pick = (activeIndex >= 0) ? activeIndex : 0;
        const anime = currentList[pick];
        if (anime) selectAnime(anime);
      }
    });
  }

});
</script>

</body>
</html>