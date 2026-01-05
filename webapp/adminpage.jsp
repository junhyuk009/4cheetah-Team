<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<c:set var="urlNewsManage" value="${ctx}/newsList.do"/>
<c:set var="urlAnimeListManage" value="${ctx}/animeList.do"/>
<c:set var="urlMyPosts" value="${ctx}/myPostPage.do" />
<c:set var="urlChangePassword" value="${ctx}/changePasswordPage.do"/>

<%-- 게시글 관리는 category 없으면 튕기니까 현재 존재하는 ANIME만 강제 링크 --%>
<c:set var="urlBoardManageAnime" value="${ctx}/boardList.do?category=ANIME"/>

<c:if test="${empty sessionScope.memberId}">
	<c:redirect url="${ctx}/loginPage.do" />
</c:if>

<c:if test="${empty sessionScope.memberRole or sessionScope.memberRole ne 'ADMIN'}">
	<c:redirect url="${ctx}/mainPage.do" />
</c:if>

<c:if test="${empty memberData}">
	<c:redirect url="${ctx}/adminPage.do" />
</c:if>

<c:if test="${empty activeMenu}">
	<c:set var="activeMenu" value="ADMIN" />
</c:if>

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>AniMale | 관리자 페이지</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<link rel="icon" type="image/png" href="${ctx}/favicon.png">

<link href="https://fonts.googleapis.com/css2?family=Oswald:wght@300;400;500;600;700&display=swap" rel="stylesheet">
<link href="https://fonts.googleapis.com/css2?family=Mulish:wght@300;400;500;600;700;800;900&display=swap" rel="stylesheet">

<link rel="stylesheet" href="${ctx}/css/bootstrap.min.css">
<link rel="stylesheet" href="${ctx}/css/font-awesome.min.css">
<link rel="stylesheet" href="${ctx}/css/style.css">

<style>
/* 타이틀/레이아웃 */
.mypage-title{ margin-top: 24px; text-align: center; }
.mypage-title__h1{ font-weight: 800; margin: 0; }
.mypage-spad{ padding-top: 60px; padding-bottom: 80px; }

.login-box-clean{
	background: rgba(255, 255, 255, 0.02);
	border-radius: 18px;
	padding: 28px;
	box-shadow: 0 10px 28px rgba(0,0,0,0.22);
}

/* 공통 버튼 */
.mypage-btn{
	display: block;
	width: 100%;
	text-align: center;
	padding: 14px 18px;
	border-radius: 999px;
	font-weight: 600;
	color: #fff;
	background: rgba(255, 255, 255, 0.08);
	border: none;
	transition: .2s;
	position: relative;
	overflow: hidden;
	box-shadow: 0 10px 24px rgba(0, 0, 0, 0.28);
	transform: translateY(0);
}
.mypage-btn:hover{ background: rgba(255, 255, 255, 0.12); transform: translateY(-1px); }
.mypage-btn:active{ transform: translateY(0); }
.mypage-btn::after{
	content: "";
	position: absolute; top: -40%; left: -60%;
	width: 60%; height: 180%;
	background: linear-gradient(90deg,
		rgba(255,255,255,0) 0%,
		rgba(255,255,255,0.16) 50%,
		rgba(255,255,255,0) 100%);
	transform: skewX(-18deg);
	transition: left .45s ease;
	pointer-events: none;
}
.mypage-btn:hover::after{ left: 120%; }

.primary-red{
	background: linear-gradient(135deg, #ff4c4c 0%, #e53637 55%, #c92c2d 100%) !important;
	color: #fff !important;
}
.primary-red:hover{ filter: brightness(1.04); }

.disabled-btn{ opacity: 0.45; pointer-events: none; }

/* 프로필 이미지 로더 */
.profile-img-wrap{
  width: 256px; height: 256px;
  margin: 0 auto 16px;
  border-radius: 18px;
  overflow: hidden;
  position: relative;
  background: #0b0c2a;
}
.profile-img-wrap img{
	width: 100%; height: 100%;
	object-fit: cover;
	object-position: center;
	display: block;
	opacity: 1;
	transition: opacity .2s ease;
}
.profile-img-wrap.is-loading{
	background: linear-gradient(110deg,
		rgba(255,255,255,.03) 20%,
		rgba(255,255,255,.07) 40%,
		rgba(255,255,255,.03) 60%), #0b0c2a;
	background-size: 220% 100%;
	animation: skeletonShimmer 1.15s ease-in-out infinite;
}
.profile-img-wrap.is-loading img{ opacity: 0; }

.profile-loader{
	position: absolute; inset: 0;
	display: none;
	align-items: center;
	justify-content: center;
	flex-direction: column;
	gap: 10px;
	background: rgba(11, 12, 42, .80);
	backdrop-filter: blur(4px);
	-webkit-backdrop-filter: blur(4px);
}
.profile-img-wrap.is-loading .profile-loader{ display: flex; }

.loader-bar{ width: 54px; height: 54px; border-radius: 999px; position: relative; }
.loader-bar::before{
	content:""; position:absolute; inset:0; border-radius:999px;
	border: 5px solid rgba(255,255,255,.14);
	border-top-color: rgba(255,255,255,.90);
	border-right-color: rgba(229,54,55,.85);
	animation: spin .9s linear infinite;
}
.loader-bar::after{
	content:""; position:absolute; inset:13px; border-radius:999px;
	background: rgba(255,255,255,.07);
	box-shadow: 0 0 18px rgba(255,255,255,.08);
	animation: pulse 1.1s ease-in-out infinite;
}
.loader-text{
	font-size: 12px;
	font-weight: 900;
	color: rgba(255,255,255,.78);
	animation: textPulse 1.2s ease-in-out infinite;
}

/* 여기 keyframes는 절대 @ 줄바꿈 나면 안 됨 */
@keyframes spin { to { transform: rotate(360deg); } }
@keyframes pulse{ 0%,100%{ transform: scale(.98); opacity:.75; } 50%{ transform: scale(1.03); opacity:1; } }
@keyframes textPulse{ 0%,100%{ opacity:.65; } 50%{ opacity:1; } }
@keyframes skeletonShimmer{ 0%{ background-position:120% 0; } 100%{ background-position:-80% 0; } }

.mypage-btn.profile-btn{
  width: 256px !important;
  padding: 14px 18px !important;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-left: auto;
  margin-right: auto;
  cursor: pointer;
}
.mypage-btn.profile-btn .btn-text{ font-size: 16px; font-weight: 600; }

.msg-area{ margin-top: 10px; font-size: 13px; line-height: 1.35; }
.msg-ok{ color: #25d366; font-weight: 800; }
.msg-error{ color: #ff4c4c; font-weight: 800; }
.msg-info{ color: rgba(255,255,255,0.75); }

/* 입력 행 UI */
:root{
  --accent: 229, 54, 55;
  --hud-bg: rgba(255,255,255,0.030);
  --hud-border: rgba(255,255,255,0.095);
}
.split-pill{
  height: 52px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  overflow: hidden;
  background: var(--hud-bg);
  border: 1px solid var(--hud-border);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  box-shadow: 0 12px 28px rgba(0,0,0,0.22), inset 0 1px 0 rgba(255,255,255,0.05);
  margin-bottom: 14px;
}
.split-label{
  width: 154px;
  display:flex;
  align-items:center;
  gap: 12px;
  padding: 0 14px 0 16px;
  position: relative;
}
.split-label::after{
  content:"";
  position:absolute;
  right: 0; top: 10px; bottom: 10px;
  width: 1px;
  background: rgba(255,255,255,0.06);
}
.split-label .split-icon{
  width: 34px; height: 34px;
  border-radius: 12px;
  display:flex; align-items:center; justify-content:center;
  background: rgba(255,255,255,0.035);
  border: 1px solid rgba(var(--accent),0.22);
}
.split-label svg{ width: 16px; height: 16px; stroke: rgba(var(--accent),0.90); stroke-width: 2; fill: none; }
.split-label .split-text{
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.45px;
  color: rgba(255,255,255,0.58);
  white-space: nowrap;
}
.split-value{ flex: 1; display:flex; align-items:center; padding: 0 18px; }
.split-value input{
  width: 100%; height: 52px;
  border: 0; outline: 0;
  background: transparent;
  padding-left: 12px;
  font-size: 14.5px;
  font-weight: 620;
  color: rgba(255,255,255,0.90);
}
.split-row{
  display: grid;
  grid-template-columns: 1fr 128px;
  gap: 12px;
  align-items: center;
  margin-bottom: 14px;
}
.split-row .split-pill{ margin-bottom: 0; }

/* 중복확인 버튼 */
.nick-check-btn{
  height: 52px;
  border-radius: 16px;
  font-weight: 800;
  letter-spacing: .2px;

  background: linear-gradient(135deg,
    rgba(255,76,76,0.18) 0%,
    rgba(229,54,55,0.10) 55%,
    rgba(201,44,45,0.08) 100%);
  border: 1px solid rgba(var(--accent),0.28);
  color: rgba(255,255,255,0.92);

  display:flex;
  align-items:center;
  justify-content:center;
  transition: .18s ease;
  box-shadow:
    0 12px 24px rgba(0,0,0,0.18),
    inset 0 1px 0 rgba(255,255,255,0.06);
}
.nick-check-btn:hover{
  filter: brightness(1.05);
  border-color: rgba(var(--accent),0.36);
}
.nick-check-btn.disabled-btn{
  opacity: .45;
  pointer-events:none;
}

#nicknameMsg:empty{ display:none; }

/* 좌측 메뉴 */
.side-menu{
	list-style: none;
	padding-left: 0;
	margin: 0;
	display: flex;
	flex-direction: column;
	gap: 10px;
}
.side-menu .menu-link, .side-menu .menu-btn{
	width: 100%;
	display: flex;
	align-items: center;
	gap: 10px;
	padding: 12px 14px;
	border-radius: 14px;
	color: rgba(255,255,255,0.88);
	background: rgba(255,255,255,0.03);
	border: 1px solid rgba(255,255,255,0.06);
	font-weight: 600;
	transition: 0.18s ease;
}
.side-menu .menu-link i, .side-menu .menu-btn i{ width: 18px; text-align: center; opacity: 0.9; }
.side-menu .menu-link:hover, .side-menu .menu-btn:hover{
	background: rgba(255,255,255,0.06);
	border-color: rgba(255,255,255,0.10);
	transform: translateY(-1px);
}
.side-menu .menu-btn.danger{
	color: #ff6b6b;
	background: rgba(229, 54, 55, 0.06);
	border-color: rgba(229, 54, 55, 0.18);
}
.side-menu .menu-btn.danger:hover{
	background: rgba(229, 54, 55, 0.10);
	border-color: rgba(229, 54, 55, 0.26);
}

/* 좌측 드롭다운 (게시글 관리) */
.side-dropdown{ position: relative; }
.side-dropdown .dd-icon{
	margin-left: auto;
	opacity: 0.9;
	transition: transform .18s ease;
}

.side-dropdown-menu{
	list-style: none;
	margin: 8px 0 0 0;
	padding: 10px;
	border-radius: 14px;
	background: rgba(255,255,255,0.02);
	border: 1px solid rgba(255,255,255,0.06);
	display: none;
}

/* hover로 열림 */
.side-dropdown:hover .side-dropdown-menu{ display: block; }
.side-dropdown:hover .dd-icon{ transform: rotate(180deg); }

.side-dropdown-menu li{ margin-bottom: 8px; }
.side-dropdown-menu li:last-child{ margin-bottom: 0; }

.side-dropdown-menu a{
	display: flex;
	align-items: center;
	padding: 10px 12px;
	border-radius: 12px;
	color: rgba(255,255,255,0.82);
	background: rgba(255,255,255,0.02);
	border: 1px solid rgba(255,255,255,0.05);
	font-weight: 600;
	transition: 0.18s ease;
}
.side-dropdown-menu a:hover{
	background: rgba(255,255,255,0.06);
	border-color: rgba(255,255,255,0.10);
	transform: translateY(-1px);
}

/* 레이아웃 */
.mypage-col-left{ padding-right: 28px !important; }
.mypage-col-right{ padding-left: 28px !important; }
.mypage-side-card{ position: relative; z-index: 2; overflow: visible; }
.mypage-right-card{
	position: relative;
	z-index: 1;
	overflow: hidden;
	border: 1px solid rgba(255,255,255,0.06);
	box-shadow: 0 18px 60px rgba(0,0,0,.35);
}
body.mypage-editing .mypage-right-card{
	border-color: rgba(229,54,55,.18);
	box-shadow: 0 18px 60px rgba(0,0,0,.45);
}
.edit-actions{ margin-top: 20px; display: flex; gap: 12px; }
.edit-actions .mypage-btn{ width: 100%; }

@media (max-width: 991.98px){
  .mypage-col-left{ padding-right: 0 !important; margin-bottom: 18px; }
  .mypage-col-right{ padding-left: 0 !important; }
  .split-row{ grid-template-columns: 1fr; }
  .nick-check-btn{ width: 100%; }
  .profile-img-wrap{ width: 100%; max-width: 256px; height: auto; aspect-ratio: 1/1; }
}
</style>
</head>

<body>
	<jsp:include page="/common/header.jsp" />

	<c:if test="${not empty msg}">
		<div class="container" style="margin-top: 18px;">
			<div class="alert alert-warning" style="border-radius: 14px;">${msg}</div>
		</div>
	</c:if>

	<div class="container mypage-title">
		<h1 class="mypage-title__h1">관리자 페이지</h1>
	</div>

	<section class="spad mypage-spad">
		<div class="container">
			<div class="row">

				<!-- ===== LEFT ===== -->
				<div class="col-12 col-lg-4 mypage-col-left">
					<div class="login-box-clean mypage-side-card text-center">

						<div class="profile-img-wrap is-loading" id="profileWrap">
							<c:choose>
								<c:when test="${not empty memberData.memberProfileImage}">
									<img id="profilePreview" alt="프로필 이미지"
										data-real-src="${ctx}${memberData.memberProfileImage}"
										data-initial-src="${ctx}${memberData.memberProfileImage}"
										data-default-src="${ctx}/img/profile-default.jpg"
										src="data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///ywAAAAAAQABAAACAUwAOw==">
								</c:when>
								<c:otherwise>
									<img id="profilePreview" alt="프로필 이미지"
										data-initial-src="${ctx}/img/profile-default.jpg"
										data-default-src="${ctx}/img/profile-default.jpg"
										src="data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///ywAAAAAAQABAAACAUwAOw==">
								</c:otherwise>
							</c:choose>

							<div class="profile-loader" role="status" aria-live="polite">
								<div class="loader-bar" aria-hidden="true"></div>
								<div class="loader-text">이미지 불러오는 중</div>
							</div>
						</div>

						<label id="profileBtnLabel" class="mypage-btn profile-btn disabled-btn">
							<span class="btn-text">프로필 사진 변경</span>
							<input type="file" id="profileInput" accept="image/*" hidden>
						</label>
					</div>

					<div class="login-box-clean mypage-side-card" style="margin-top: 20px;">
						<ul class="side-menu">
							<li>
								<a class="menu-link" href="${urlNewsManage}">
									<i class="fa fa-newspaper-o"></i><span>뉴스 관리</span>
								</a>
							</li>

							<%-- 게시글 관리: ANIME만 드롭다운 노출 --%>
							<li class="side-dropdown">
								<a class="menu-link" href="#" onclick="return false;">
									<i class="fa fa-list-alt"></i><span>게시글 관리</span>
									<i class="fa fa-angle-down dd-icon"></i>
								</a>
								<ul class="side-dropdown-menu">
									<li>
										<a href="${urlBoardManageAnime}">ANIME</a>
									</li>
								</ul>
							</li>

							<li>
								<a class="menu-link" href="${urlAnimeListManage}">
									<i class="fa fa-film"></i><span>애니리스트 관리</span>
								</a>
							</li>

							<%-- 내 글 보기: 드롭다운 금지 (원래 페이지로 바로 이동) --%>
							<li>
								<a class="menu-link" href="${urlMyPosts}">
									<i class="fa fa-pencil"></i><span>내 글 보기</span>
								</a>
							</li>

							<li>
								<a class="menu-link" href="${urlChangePassword}">
									<i class="fa fa-lock"></i><span>비밀번호 변경</span>
								</a>
							</li>

							<li>
								<form action="${ctx}/logout.do" method="post" style="margin: 0;">
									<button type="submit" class="menu-btn danger"
										onclick="return confirm('로그아웃 하시겠습니까?');">
										<i class="fa fa-sign-out"></i><span>로그아웃</span>
									</button>
								</form>
							</li>
						</ul>
					</div>
				</div>

				<!-- RIGHT -->
				<div class="col-12 col-lg-8 mypage-col-right">
					<div class="login-box-clean mypage-right-card">
						<h5 style="margin-bottom: 24px;">내 정보</h5>

						<form id="mypageForm" action="${ctx}/changeProfile.do" method="post">
							<input type="hidden" id="temporaryProfileImageToken" name="temporaryProfileImageToken" value="" />

							<div class="split-pill">
								<div class="split-label">
									<span class="split-icon" aria-hidden="true">
										<svg viewBox="0 0 24 24"><path d="M20 21a8 8 0 0 0-16 0"></path><circle cx="12" cy="8" r="4"></circle></svg>
									</span>
									<span class="split-text">아이디</span>
								</div>
								<div class="split-value">
									<input type="text" value="${memberData.memberName}" readonly>
								</div>
							</div>

							<div class="split-pill">
								<div class="split-label">
									<span class="split-icon" aria-hidden="true">
										<svg viewBox="0 0 24 24"><path d="M4 6h16v12H4z"></path><path d="m4 7 8 6 8-6"></path></svg>
									</span>
									<span class="split-text">이메일</span>
								</div>
								<div class="split-value">
									<input type="email" value="${memberData.memberEmail}" readonly>
								</div>
							</div>

							<div class="split-pill">
								<div class="split-label">
									<span class="split-icon" aria-hidden="true">
										<svg viewBox="0 0 24 24"><path d="M12 2 20 6v6c0 5-3.5 9.5-8 10-4.5-.5-8-5-8-10V6z"></path><path d="M9 12l2 2 4-4"></path></svg>
									</span>
									<span class="split-text">권한</span>
								</div>
								<div class="split-value">
									<input type="text" value="${sessionScope.memberRole}" readonly>
								</div>
							</div>

							<div class="split-row">
								<div class="split-pill" id="nicknamePill">
									<div class="split-label">
										<span class="split-icon" aria-hidden="true">
											<svg viewBox="0 0 24 24"><rect x="3" y="5" width="18" height="14" rx="2"></rect><circle cx="8.5" cy="12" r="2"></circle><path d="M13 11h6"></path><path d="M13 14h6"></path></svg>
										</span>
										<span class="split-text">닉네임</span>
									</div>
									<div class="split-value">
										<input id="nicknameInput" name="memberNickname" type="text"
											value="${memberData.memberNickname}" readonly>
									</div>
								</div>

								<button id="nickCheckBtn" class="nick-check-btn disabled-btn" type="button">중복 확인</button>
							</div>

							<div class="msg-area" id="nicknameMsg"></div>

							<div class="row" style="margin-top: 26px;" id="viewActions">
								<div class="col-md-12">
									<button id="editBtn" type="button" class="mypage-btn">내 정보 수정</button>
								</div>
							</div>

							<div class="edit-actions" id="editActions" style="display: none;">
								<button id="saveBtn" type="button" class="mypage-btn primary-red disabled-btn">수정 완료</button>
								<button id="cancelBtn" type="button" class="mypage-btn">취소</button>
							</div>
						</form>
					</div>
				</div>

			</div>
		</div>
	</section>

	<%@ include file="/common/footer.jsp"%>

	<script src="${ctx}/js/jquery-3.3.1.min.js"></script>
	<script src="${ctx}/js/bootstrap.min.js"></script>

	<script>
	/* 프로필 이미지 로딩/재시도 */
	(function() {
		function addCacheBust(url) {
			const sep = url.includes("?") ? "&" : "?";
			return url + sep + "v=" + Date.now();
		}

		document.addEventListener("DOMContentLoaded", function() {
			const wrap = document.getElementById("profileWrap");
			const img = document.getElementById("profilePreview");
			if (!wrap || !img) return;

			const real = img.dataset.realSrc || "";
			const initial = img.dataset.initialSrc || img.dataset.defaultSrc || "";
			const fallback = img.dataset.defaultSrc || "";

			wrap.classList.add("is-loading");

			if (!real) {
				img.src = addCacheBust(initial || fallback);
				wrap.classList.remove("is-loading");
				return;
			}

			const START_DELAY_MS = 1000;
			const INTERVAL_MS = 250;
			const MAX_WAIT_MS = 15000;
			const startAt = Date.now();

			function tryLoad() {
				const elapsed = Date.now() - startAt;

				if (elapsed >= MAX_WAIT_MS) {
					img.src = addCacheBust(fallback || initial);
					wrap.classList.remove("is-loading");
					return;
				}

				const testSrc = addCacheBust(real);
				const pre = new Image();

				pre.onload = function() {
					img.src = testSrc;
					wrap.classList.remove("is-loading");
				};
				pre.onerror = function() {
					setTimeout(tryLoad, INTERVAL_MS);
				};

				pre.src = testSrc;
			}

			setTimeout(tryLoad, START_DELAY_MS);
		});
	})();
	</script>

	<script>
	$(function() {

		const URL_PROFILE_UPLOAD = "${ctx}/profileImageUpload.do";
		const URL_NICK_CHECK = "${ctx}/MemberNickNameCheck";

		const NICK_REGEX = /^[A-Za-z0-9가-힣]{2,12}$/;

		let editMode = false;
		let nicknameChecked = false;
		let profileChanged = false;

		const originalNickname = $("#nicknameInput").val().trim();
		const originalProfileSrc = $("#profilePreview").data("initialSrc");

		function addCacheBust(url) {
			if (!url) return url;
			const sep = url.indexOf("?") >= 0 ? "&" : "?";
			return url + sep + "v=" + Date.now();
		}

		function updateSaveButton() {
			const newNickname = $("#nicknameInput").val().trim();
			const token = $("#temporaryProfileImageToken").val().trim();

			const nickChanged = editMode && (newNickname !== originalNickname);
			const profileOk = editMode && profileChanged && token.length > 0;

			let canSave = true;

			if (!nickChanged && !profileOk) canSave = false;

			if (nickChanged) {
				if (!NICK_REGEX.test(newNickname)) canSave = false;
				if (!nicknameChecked) canSave = false;
			}

			if (editMode && profileChanged && token.length === 0) canSave = false;

			if (editMode && canSave) $("#saveBtn").removeClass("disabled-btn");
			else $("#saveBtn").addClass("disabled-btn");
		}

		$("#editBtn").on("click", function() {
			editMode = true;
			$("body").addClass("mypage-editing");

			$("#viewActions").hide();
			$("#editActions").show();

			$("#nicknameInput").prop("readonly", false);
			$("#nickCheckBtn").removeClass("disabled-btn");
			$("#profileBtnLabel").removeClass("disabled-btn");

			nicknameChecked = false;
			profileChanged = false;
			$("#nicknameMsg").text("");

			$("#temporaryProfileImageToken").val("");
			$("#profileInput").val("");

			updateSaveButton();
		});

		$("#cancelBtn").on("click", function() {
			exitEditMode(true);
		});

		function exitEditMode(resetValues) {
			editMode = false;
			$("body").removeClass("mypage-editing");

			if (resetValues) {
				$("#nicknameInput").val(originalNickname);

				const base = (originalProfileSrc || "").split("?")[0];
				$("#profileWrap").addClass("is-loading");
				$("#profilePreview").attr("src", addCacheBust(base));
				setTimeout(function() { $("#profileWrap").removeClass("is-loading"); }, 150);

				$("#temporaryProfileImageToken").val("");
				$("#profileInput").val("");
			}

			$("#nicknameInput").prop("readonly", true);
			$("#nickCheckBtn").addClass("disabled-btn").text("중복 확인");
			$("#profileBtnLabel").addClass("disabled-btn");

			$("#editActions").hide();
			$("#viewActions").show();

			$("#nicknameMsg").text("");

			nicknameChecked = false;
			profileChanged = false;

			updateSaveButton();
		}

		$("#nicknameInput").on("input", function() {
			if (!editMode) return;

			const val = $("#nicknameInput").val().trim();
			if (val.length > 0 && !NICK_REGEX.test(val)) {
				$("#nicknameMsg").removeClass("msg-ok").addClass("msg-error")
					.text("닉네임은 2~12자, 한글/영문/숫자만 사용할 수 있습니다.");
			} else {
				$("#nicknameMsg").text("");
			}

			nicknameChecked = false;
			$("#nickCheckBtn").text("중복 확인");
			updateSaveButton();
		});

		$("#nickCheckBtn").on("click", function() {
			if (!editMode) return;

			const nickname = $("#nicknameInput").val().trim();

			if (!NICK_REGEX.test(nickname)) {
				$("#nicknameMsg").removeClass("msg-ok").addClass("msg-error")
					.text("닉네임은 2~12자, 한글/영문/숫자만 사용할 수 있습니다.");
				nicknameChecked = false;
				updateSaveButton();
				return;
			}

			if (nickname === originalNickname) {
				$("#nicknameMsg").removeClass("msg-ok").addClass("msg-error")
					.text("현재 닉네임과 동일합니다.");
				nicknameChecked = false;
				updateSaveButton();
				return;
			}

			$.ajax({
				url: URL_NICK_CHECK,
				type: "GET",
				dataType: "json",
				data: { memberNickname: nickname },
				success: function(res) {
					if (!res || res.success !== true) {
						$("#nicknameMsg").removeClass("msg-ok").addClass("msg-error")
							.text((res && res.message) ? res.message : "중복확인에 실패했습니다.");
						nicknameChecked = false;
						$("#nickCheckBtn").text("중복 확인");
						updateSaveButton();
						return;
					}

					if (res.available === true) {
						$("#nicknameMsg").removeClass("msg-error").addClass("msg-ok")
							.text("사용 가능한 닉네임입니다.");
						nicknameChecked = true;
						$("#nickCheckBtn").text("확인 완료");
					} else {
						$("#nicknameMsg").removeClass("msg-ok").addClass("msg-error")
							.text("이미 사용 중인 닉네임입니다.");
						nicknameChecked = false;
						$("#nickCheckBtn").text("중복 확인");
					}
					updateSaveButton();
				},
				error: function() {
					$("#nicknameMsg").removeClass("msg-ok").addClass("msg-error")
						.text("중복확인 서버 호출에 실패했습니다.");
					nicknameChecked = false;
					$("#nickCheckBtn").text("중복 확인");
					updateSaveButton();
				}
			});
		});

		$("#profileInput").on("change", function() {
			if (!editMode) return;

			const file = this.files[0];
			if (!file) return;

			if (!file.type || !file.type.startsWith("image/")) {
				alert("이미지 파일만 선택할 수 있습니다.");
				$(this).val("");
				return;
			}

			$("#profileWrap").addClass("is-loading");

			const formData = new FormData();
			formData.append("profileImageFile", file);

			$.ajax({
				url: URL_PROFILE_UPLOAD,
				type: "POST",
				data: formData,
				processData: false,
				contentType: false,
				dataType: "json",
				success: function(res) {
					if (!res || res.result !== "SUCCESS") {
						alert((res && res.errorMessage) ? res.errorMessage : "업로드에 실패했습니다.");
						$("#profileInput").val("");
						$("#profileWrap").removeClass("is-loading");
						return;
					}

					$("#profilePreview").attr("src", addCacheBust(res.temporaryProfileImageUrl));
					$("#profileWrap").removeClass("is-loading");

					$("#temporaryProfileImageToken").val(res.temporaryProfileImageToken);

					profileChanged = true;
					updateSaveButton();
				},
				error: function(xhr) {
					alert("프로필 업로드 실패(" + xhr.status + "). 다시 시도해주세요.");
					$("#profileInput").val("");
					$("#profileWrap").removeClass("is-loading");
				}
			});
		});

		$("#saveBtn").on("click", function() {
			if ($(this).hasClass("disabled-btn")) return;
			if (!confirm("수정을 완료할까요?")) return;

			$("#saveBtn").addClass("disabled-btn");
			$("#mypageForm").submit();
		});

		updateSaveButton();
	});
	</script>
</body>
</html>
