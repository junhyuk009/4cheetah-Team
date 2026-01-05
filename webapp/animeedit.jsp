<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>AniMale | 애니 수정</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<link rel="icon" type="image/png" href="${ctx}/favicon.png">

<!-- CSS -->
<link rel="stylesheet" href="${ctx}/css/bootstrap.min.css">
<link rel="stylesheet" href="${ctx}/css/font-awesome.min.css">
<link rel="stylesheet" href="${ctx}/css/elegant-icons.css">
<link rel="stylesheet" href="${ctx}/css/style.css">

<style>
/* 헤더 돋보기 제거 */
.header__right__icons .icon_search {
	display: none !important;
}

/* 폼 톤 */
.admin-form label {
	color: #fff;
	font-weight: 700;
	margin-bottom: 8px;
	display: block;
}

.admin-form .form-control {
	border-radius: 10px;
	height: 46px;
	line-height: 46px;
}

.admin-form textarea.form-control {
	height: 180px;
	line-height: 1.6;
	resize: vertical;
	padding-top: 10px;
	padding-bottom: 10px;
}

.help {
	color: rgba(255, 255, 255, 0.78);
	font-size: 12px;
	margin-top: 6px;
}

/* 썸네일 미리보기 */
.preview-box {
	height: 440px;
	border-radius: 12px;
	border: 1px solid rgba(255, 255, 255, 0.12);
	background: rgba(255, 255, 255, 0.03);
	overflow: hidden;
	display: flex;
	align-items: center;
	justify-content: center;
	background-size: cover;
	background-position: center;
}

.preview-fallback {
	color: rgba(255, 255, 255, 0.75);
	font-size: 13px;
	text-align: center;
	padding: 0 12px;
}

/* 버튼 */
.form-actions {
	display: flex;
	justify-content: flex-end;
	gap: 10px;
	margin-top: 18px;
}

.btn-submit {
	background: #e53637;
	color: #fff;
	padding: 10px 22px;
	border-radius: 8px;
	border: none;
	cursor: pointer;
}

.btn-cancel {
	background: #444;
	color: #fff;
	padding: 10px 22px;
	border-radius: 8px;
	text-decoration: none !important;
	display: inline-flex;
	align-items: center;
	justify-content: center;
}

.header__right__icons .icon_search {
	display: none !important;
}

.anime-details.spad {
	padding-top: 120px;
	padding-bottom: 120px;
}

/* 글래스 카드 */
.anime-edit-card {
	position: relative;
	border-radius: 22px;
	padding: 34px;
	background: rgba(255, 255, 255, 0.06);
	border: 1px solid rgba(255, 255, 255, 0.10);
	backdrop-filter: blur(18px) saturate(140%);
	-webkit-backdrop-filter: blur(18px) saturate(140%);
	box-shadow: 0 18px 60px rgba(0, 0, 0, 0.45);
	overflow: hidden;
}

.anime-edit-card::before {
	content: "";
	position: absolute;
	top: -55%;
	left: -35%;
	width: 70%;
	height: 110%;
	transform: rotate(18deg);
	background: radial-gradient(circle at 28% 28%, rgba(255, 255, 255, 0.20),
		rgba(255, 255, 255, 0.00) 62%);
	pointer-events: none;
	opacity: .55;
	filter: blur(4px);
	z-index: 0;
}

.anime-edit-card::after {
	content: "";
	position: absolute;
	inset: 0;
	border-radius: 22px;
	padding: 1px;
	background: linear-gradient(135deg, rgba(255, 255, 255, 0.35),
		rgba(255, 255, 255, 0.08), rgba(120, 190, 255, 0.18),
		rgba(255, 255, 255, 0.10));
	-webkit-mask: linear-gradient(#000 0 0) content-box,
		linear-gradient(#000 0 0);
	-webkit-mask-composite: xor;
	mask-composite: exclude;
	pointer-events: none;
	opacity: .80;
	z-index: 0;
}

.anime-edit-card * {
	position: relative;
	z-index: 1;
}

/* 타이틀 */
.anime__details__title h3 {
	color: #fff;
	font-weight: 900;
	letter-spacing: -0.02em;
}

.anime__details__title span {
	color: rgba(255, 255, 255, 0.75);
}

/* 썸네일 미리보기 */
.preview-box {
	height: 440px;
	border-radius: 16px;
	border: 1px solid rgba(255, 255, 255, 0.12);
	background: rgba(0, 0, 0, 0.18);
	overflow: hidden;
	display: flex;
	align-items: center;
	justify-content: center;
	background-size: contain; /* ✅ 크면 contain이 안정적 */
	background-position: center;
	background-repeat: no-repeat;
}

.preview-fallback {
	color: rgba(255, 255, 255, 0.75);
	font-size: 13px;
	text-align: center;
	padding: 0 12px;
}

/* 폼 */
.admin-form label {
	color: #fff;
	font-weight: 800;
	margin-bottom: 8px;
	display: block;
}

.admin-form .form-control {
	border-radius: 12px;
	height: 46px;
	line-height: 46px;
	background: rgba(255, 255, 255, 0.06);
	border: 1px solid rgba(255, 255, 255, 0.14);
	color: #fff;
}

.admin-form .form-control:focus {
	outline: none;
	box-shadow: 0 0 0 3px rgba(140, 200, 255, 0.25);
	border-color: rgba(140, 200, 255, 0.35);
}

.admin-form .form-control::placeholder {
	color: rgba(255, 255, 255, 0.55);
}

.admin-form textarea.form-control {
	height: 200px;
	line-height: 1.7;
	padding-top: 12px;
	padding-bottom: 12px;
	resize: vertical;
}

/* select 옵션 배경 */
.admin-form select.form-control option {
	background: #0b0c2a;
	color: #fff;
}

/* help text */
.help {
	color: rgba(255, 255, 255, 0.70);
	font-size: 12px;
	margin-top: 8px;
}

/* 버튼 */
.ad-btn {
	display: inline-flex;
	align-items: center;
	justify-content: center;
	gap: 8px;
	padding: 10px 16px;
	border-radius: 999px;
	font-size: 14px;
	font-weight: 900;
	letter-spacing: -0.01em;
	text-decoration: none !important;
	user-select: none;
	border: 1px solid rgba(255, 255, 255, 0.16);
	background: rgba(255, 255, 255, 0.08);
	color: #fff !important;
	transition: transform .18s ease, box-shadow .18s ease, background .18s
		ease, border-color .18s ease;
	backdrop-filter: blur(12px) saturate(140%);
	-webkit-backdrop-filter: blur(12px) saturate(140%);
}

.ad-btn:hover {
	transform: translateY(-1px);
	box-shadow: 0 12px 28px rgba(0, 0, 0, 0.35);
	border-color: rgba(255, 255, 255, 0.26);
	background: rgba(255, 255, 255, 0.12);
}

.ad-btn--primary {
	background: linear-gradient(135deg, rgba(120, 190, 255, 0.45),
		rgba(255, 255, 255, 0.08));
	border-color: rgba(120, 190, 255, 0.35);
}

.ad-btn--ghost {
	background: rgba(255, 255, 255, 0.06);
	border-color: rgba(255, 255, 255, 0.14);
	opacity: .95;
}

/* 하단 버튼 정렬 */
.form-actions {
	display: flex;
	justify-content: flex-end;
	gap: 10px;
	margin-top: 18px;
	flex-wrap: wrap;
}

/* 반응형 */
@media ( max-width : 991px) {
	.anime-edit-card {
		padding: 24px;
	}
	.preview-box {
		height: 360px;
	}
	.form-actions {
		justify-content: flex-start;
	}
}

/* 파일 선택 UI 커스텀 */
.file-row {
	display: flex;
	align-items: center;
	gap: 12px;
	flex-wrap: wrap;
}

.file-input {
	position: absolute;
	left: -9999px;
	width: 1px;
	height: 1px;
	opacity: 0;
}

.file-btn {
	padding: 10px 16px; /* ad-btn 기본보다 살짝만 */
}

.file-name {
	color: rgba(255, 255, 255, 0.78);
	font-size: 13px;
	max-width: 420px;
	white-space: nowrap;
	overflow: hidden;
	text-overflow: ellipsis;
}

.file-reset {
	opacity: .92;
}
</style>
</head>

<body>

	<jsp:include page="/common/header.jsp" />

	<section class="anime-details spad">
		<div class="container">

			<!-- 혹시 직접 접근 등으로 animeData가 없을 때 방어 -->
			<div class="anime-edit-card">
				<c:if test="${empty animeData}">
					<div class="alert alert-danger">수정할 애니 데이터(animeData)가 없습니다.
						접근경로를 확인하세요.</div>
				</c:if>

				<c:if test="${not empty animeData}">
					<div class="anime__details__content">
						<div class="row">

							<!-- LEFT: 썸네일 미리보기 -->
							<div class="col-lg-3">
								<div id="thumbPreviewBox" class="preview-box">
									<div id="thumbFallback" class="preview-fallback">썸네일 미리보기</div>
								</div>
								<div class="help">파일을 새로 선택하지 않으면 기존 썸네일을 그대로 유지합니다.</div>
							</div>

							<!-- RIGHT: 수정 폼 -->
							<div class="col-lg-9">
								<div class="anime__details__text">

									<div class="anime__details__title">
										<h3>애니 수정 (관리자)</h3>
										<span>기존 정보를 불러온 뒤 필요한 항목만 수정하세요.</span>
									</div>

									<form class="admin-form" action="${ctx}/animeEdit.do"
										method="post" enctype="multipart/form-data"
										style="margin-top: 18px;">

										<!-- AnimeEditAction에 맞춘 파라미터 -->
										<input type="hidden" name="anime_id"
											value="${animeData.animeId}"> <input type="hidden"
											name="existingThumbUrl" id="existingThumbUrl"
											value="${animeData.animeThumbnailUrl}">

										<div class="row">

											<!-- anime_title -->
											<div class="col-lg-12">
												<div class="form-group" style="margin-bottom: 16px;">
													<label>애니 제목 <span style="color: #ff5b5b;">*</span></label>
													<input type="text" class="form-control" name="anime_title"
														id="anime_title" maxlength="200" required
														value="${animeData.animeTitle}">
												</div>
											</div>

											<!-- original_title -->
											<div class="col-lg-12">
												<div class="form-group" style="margin-bottom: 16px;">
													<label>오리지널 제목 <span style="color: #ff5b5b;">*</span></label>
													<input type="text" class="form-control"
														name="original_title" id="original_title" maxlength="200"
														required value="${animeData.originalTitle}">
												</div>
											</div>

											<!-- anime_year -->
											<div class="col-lg-6 col-md-6">
												<div class="form-group" style="margin-bottom: 16px;">
													<label>방영년도 <span style="color: #ff5b5b;">*</span></label>
													<input type="number" class="form-control" name="anime_year"
														id="anime_year" min="1960" max="2100" required
														value="${animeData.animeYear}">
												</div>
											</div>

											<!-- anime_quarter -->
											<div class="col-lg-6 col-md-6">
												<div class="form-group" style="margin-bottom: 16px;">
													<label>방영분기 <span style="color: #ff5b5b;">*</span></label>
													<select class="form-control" name="anime_quarter"
														id="anime_quarter" required>
														<option value="">분기 선택</option>

														<%-- DB에 "1/2/3/4" 또는 "1분기" 형태가 섞여도 선택되게 방어 --%>
														<option value="1분기"
															<c:if test="${animeData.animeQuarter eq '1' or animeData.animeQuarter eq '1분기'}">selected</c:if>>
															1분기 (1~3월)</option>
														<option value="2분기"
															<c:if test="${animeData.animeQuarter eq '2' or animeData.animeQuarter eq '2분기'}">selected</c:if>>
															2분기 (4~6월)</option>
														<option value="3분기"
															<c:if test="${animeData.animeQuarter eq '3' or animeData.animeQuarter eq '3분기'}">selected</c:if>>
															3분기 (7~9월)</option>
														<option value="4분기"
															<c:if test="${animeData.animeQuarter eq '4' or animeData.animeQuarter eq '4분기'}">selected</c:if>>
															4분기 (10~12월)</option>
													</select>
												</div>
											</div>

											<!-- thumbFile (선택) -->
											<div class="col-lg-12">
												<div class="form-group" style="margin-bottom: 16px;">
													<label>썸네일 이미지 (선택)</label>
													<div class="file-row">
														<input type="file" name="thumbFile" id="thumbFile"
															class="file-input" accept="image/*"> <label
															for="thumbFile" class="ad-btn ad-btn--ghost file-btn">
															<i class="fa fa-upload"></i> 파일 선택
														</label> <span id="thumbFileName" class="file-name">선택된 파일
															없음</span>

														<!-- 기존 썸네일로 되돌리기 (선택) -->
														<button type="button" id="thumbResetBtn"
															class="ad-btn ad-btn--ghost file-reset">
															<i class="fa fa-undo"></i> 되돌리기
														</button>
													</div>
													<div class="help">파일을 선택하면 좌측 미리보기에 반영됩니다. 선택하지 않으면
														기존 썸네일 유지.</div>
												</div>
											</div>

											<!-- anime_story -->
											<div class="col-lg-12">
												<div class="form-group" style="margin-bottom: 16px;">
													<label>상세 줄거리 <span style="color: #ff5b5b;">*</span></label>
													<textarea class="form-control" name="anime_story"
														id="anime_story" maxlength="4000" required>${animeData.animeStory}</textarea>
												</div>
											</div>

										</div>

										<div class="form-actions">
											<button type="submit" class="btn-submit">
												<i class="fa fa-check"></i> 수정 완료
											</button>
											<a class="btn-cancel"
												href="${ctx}/animeDetail.do?animeId=${animeData.animeId}">
												<i class="fa fa-times"></i>&nbsp;취소
											</a>
										</div>

									</form>

								</div>
							</div>
						</div>
					</div>
				</c:if>
			</div>
		</div>
	</section>

	<jsp:include page="/common/footer.jsp" />

	<script src="${ctx}/js/jquery-3.3.1.min.js"></script>
	<script src="${ctx}/js/bootstrap.min.js"></script>
	<script src="${ctx}/js/main.js"></script>

	<script>
(function(){
  const ctx = "${ctx}";
  const existing = document.getElementById("existingThumbUrl");
  const box = document.getElementById("thumbPreviewBox");
  const fallback = document.getElementById("thumbFallback");
  const fileEl = document.getElementById("thumbFile");
  const fileNameEl = document.getElementById("thumbFileName");
  const resetBtn = document.getElementById("thumbResetBtn");

  function resolveUrl(u){
    if(!u) return "";
    u = String(u).trim();
    if(!u) return "";
    if(/^https?:\/\//i.test(u)) return u;
    if(u.startsWith("/")) return ctx + u;
    return ctx + "/" + u;
  }

  function applyBg(url){
    if(!box) return;
    if(url){
      box.style.backgroundImage = "url('" + url.replace(/'/g, "\\'") + "')";
      if(fallback) fallback.style.display = "none";
    }else{
      box.style.backgroundImage = "none";
      if(fallback) fallback.style.display = "block";
    }
  }

  // 1) 기존 썸네일 표시
  const origin = existing ? resolveUrl(existing.value) : "";
  applyBg(origin);

  // 2) 새 파일 선택 시 미리보기
  if(fileEl){
	  fileEl.addEventListener("change", function(){
	    const f = fileEl.files && fileEl.files[0];
	    if(!f) {
	      if(fileNameEl) fileNameEl.textContent = "선택된 파일 없음";
	      applyBg(origin);
	      return;
	    }
	    if(fileNameEl) fileNameEl.textContent = f.name;

	    const reader = new FileReader();
	    reader.onload = function(e){ applyBg(e.target.result); };
	    reader.readAsDataURL(f);
	  });
	  if(resetBtn){
		  resetBtn.addEventListener("click", function(){
		    if(fileEl) fileEl.value = "";
		    if(fileNameEl) fileNameEl.textContent = "선택된 파일 없음";
		    applyBg(origin);
		  });
		}
	}
})();
</script>

</body>
</html>
