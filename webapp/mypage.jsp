<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<%-- 1) 로그인 세션 없으면 로그인 페이지로 --%>
<c:if test="${empty sessionScope.memberId}">
	<c:redirect url="${ctx}/loginPage.do" />
</c:if>

<%-- 2) memberData가 없으면 Action을 타도록 myPage.do로 --%>
<c:if test="${empty memberData}">
	<c:redirect url="${ctx}/myPage.do" />
</c:if>

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>AniMale | 마이페이지</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<link rel="icon" type="image/png" href="${ctx}/favicon.png">

<link href="https://fonts.googleapis.com/css2?family=Oswald:wght@300;400;500;600;700&display=swap" rel="stylesheet">
<link href="https://fonts.googleapis.com/css2?family=Mulish:wght@300;400;500;600;700;800;900&display=swap" rel="stylesheet">

<link rel="stylesheet" href="${ctx}/css/bootstrap.min.css">
<link rel="stylesheet" href="${ctx}/css/font-awesome.min.css">
<link rel="stylesheet" href="${ctx}/css/style.css">

<style>
/* MyPage */
.mypage-title{
	margin-top: 24px;
	text-align: center;
}
.mypage-title__h1{
	font-weight: 800;
	margin: 0;
}
.mypage-spad{
	padding-top: 60px;
	padding-bottom: 80px;
}

/* 마이페이지 카드 (좌/우 공통 베이스) */
.login-box-clean{
	background: rgba(255, 255, 255, 0.02);
	border-radius: 18px;
	padding: 28px;
	box-shadow: 0 10px 28px rgba(0,0,0,0.22);
}

/* Buttons */
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
.mypage-btn:hover{
	background: rgba(255, 255, 255, 0.12);
	transform: translateY(-1px);
}
.mypage-btn:active{ transform: translateY(0); }
.mypage-btn::after{
	content: "";
	position: absolute;
	top: -40%;
	left: -60%;
	width: 60%;
	height: 180%;
	background: linear-gradient(90deg,
		rgba(255, 255, 255, 0) 0%,
		rgba(255, 255, 255, 0.16) 50%,
		rgba(255, 255, 255, 0) 100%);
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

.disabled-btn{
	opacity: 0.45;
	pointer-events: none;
}

/* Profile Image (Loader) */
.profile-img-wrap{
  width: 256px;
  height: 256px;
  margin: 0 auto 16px;
  border-radius: 18px;
  overflow: hidden;
  position: relative;
  background: #0b0c2a;
}
.profile-img-wrap img{
	width: 100%;
	height: 100%;
	object-fit: cover;
	object-position: center;
	display: block;
	opacity: 1;
	transition: opacity .2s ease;
}
.profile-img-wrap.is-loading{
	background: linear-gradient(110deg,
		rgba(255, 255, 255, .03) 20%,
		rgba(255, 255, 255, .07) 40%,
		rgba(255, 255, 255, .03) 60%), #0b0c2a;
	background-size: 220% 100%;
	animation: skeletonShimmer 1.15s ease-in-out infinite;
}
.profile-img-wrap.is-loading img{ opacity: 0; }

.profile-loader{
	position: absolute;
	inset: 0;
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

.loader-bar{
	width: 54px;
	height: 54px;
	border-radius: 999px;
	position: relative;
}
.loader-bar::before{
	content: "";
	position: absolute;
	inset: 0;
	border-radius: 999px;
	border: 5px solid rgba(255, 255, 255, .14);
	border-top-color: rgba(255, 255, 255, .90);
	border-right-color: rgba(229, 54, 55, .85);
	animation: spin .9s linear infinite;
}
.loader-bar::after{
	content: "";
	position: absolute;
	inset: 13px;
	border-radius: 999px;
	background: rgba(255, 255, 255, .07);
	box-shadow: 0 0 18px rgba(255, 255, 255, .08);
	animation: pulse 1.1s ease-in-out infinite;
}
.loader-text{
	font-size: 12px;
	font-weight: 900;
	color: rgba(255, 255, 255, .78);
	letter-spacing: .2px;
	animation: textPulse 1.2s ease-in-out infinite;
}
@keyframes spin { to { transform: rotate(360deg); } }
@keyframes pulse{
	0%,100%{ transform: scale(.98); opacity: .75; }
	50%{ transform: scale(1.03); opacity: 1; }
}
@keyframes textPulse{
	0%,100%{ opacity: .65; }
	50%{ opacity: 1; }
}
@keyframes skeletonShimmer{
	0%{ background-position: 120% 0; }
	100%{ background-position: -80% 0; }
}
@media (prefers-reduced-motion: reduce){
	.profile-img-wrap.is-loading,
	.loader-bar::before,
	.loader-bar::after,
	.loader-text{ animation: none !important; }
}

/* 프로필 버튼: 캐시충전 버튼과 동일 톤/높이 */
.mypage-btn.profile-btn{
  width: 256px !important;
  height: auto !important;
  padding: 14px 18px !important;
  display: flex;
  align-items: center;
  justify-content: center;
  line-height: 1.2;
  font-size: inherit;
  font-weight: 600;
  margin-left: auto;
  margin-right: auto;
  cursor: pointer;
}
.mypage-btn.profile-btn .btn-text{
  font-size: 16px;
  font-weight: inherit;
  line-height: 1.2;
}

/* 안내문구 */
.msg-area{
	margin-top: 10px;
	font-size: 13px;
	line-height: 1.35;
}
.msg-ok{ color: #25d366; font-weight: 800; }
.msg-error{ color: #ff4c4c; font-weight: 800; }
.msg-info{ color: rgba(255, 255, 255, 0.75); }

/* 비용 안내 문구 정렬 */
.cost-hint{ text-align: center; }
.cost-hint__text{ display: inline-block; line-height: 1.35; }
.cost-hint__text b{ line-height: 1; }

/* 아이콘 옆 작대기 3개 제거: ::before/::after 장식 끔 */
:root{
  --accent: 229, 54, 55;
  --hud-bg: rgba(255,255,255,0.030);
  --hud-border: rgba(255,255,255,0.095);
  --hud-ink: rgba(255,255,255,0.92);
  --hud-muted: rgba(255,255,255,0.62);
}

/* 한 줄 패널 */
.split-pill{
  height: 52px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  overflow: hidden;
  position: relative;
  background: var(--hud-bg);
  border: 1px solid var(--hud-border);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  box-shadow:
    0 12px 28px rgba(0,0,0,0.22),
    inset 0 1px 0 rgba(255,255,255,0.05);
  margin-bottom: 14px;
}

/* 작대기 (브라켓/스캔라인) 원인: pseudo 제거 */
.split-pill::before,
.split-pill::after{
  content: none !important;
  display: none !important;
}

/* 라벨 영역 */
.split-label{
  width: 154px;
  display:flex;
  align-items:center;
  gap: 12px;
  padding: 0 14px 0 16px;
  position: relative;
  z-index: 1;
}

/* 라벨/값 분리선 (은은하게 1개만) */
.split-label::after{
  content:"";
  position:absolute;
  right: 0;
  top: 10px;
  bottom: 10px;
  width: 1px;
  background: rgba(255,255,255,0.06);
}

/* 아이콘: 점/마커 말고 '미니 글래스 칩 + 라인아이콘' */
.split-label .split-icon{
  width: 34px;
  height: 34px;
  border-radius: 12px;
  display:flex;
  align-items:center;
  justify-content:center;

  background: rgba(255,255,255,0.035);
  border: 1px solid rgba(var(--accent),0.22);
  box-shadow:
    0 10px 18px rgba(0,0,0,0.20),
    inset 0 1px 0 rgba(255,255,255,0.06);
}

/* svg 아이콘 스타일 */
.split-label svg{
  width: 16px;
  height: 16px;
  stroke: rgba(var(--accent), 0.90);
  stroke-width: 2;
  fill: none;
  stroke-linecap: round;
  stroke-linejoin: round;
}

/* 라벨 텍스트 (마른 느낌) */
.split-label .split-text{
  font-size: 12px;
  font-weight: 700;
  padding-left: 4px;
  letter-spacing: 0.45px;
  color: rgba(255,255,255,0.58);
  white-space: nowrap;
}

/* 값 영역 */
.split-value{
  flex: 1;
  display:flex;
  align-items:center;
  padding: 0 18px;
  position: relative;
  z-index: 1;
}
.split-value input{
  width: 100%;
  height: 52px;
  border: 0;
  outline: 0;
  background: transparent;
  padding-left: 12px;
  font-size: 14.5px;
  font-weight: 620;
  letter-spacing: -0.25px;
  color: rgba(255,255,255,0.90);
}
.split-value input[readonly]{
  color: rgba(255,255,255,0.86);
}
#cashDisplay{
  font-variant-numeric: tabular-nums;
  letter-spacing: -0.15px;
}

/* edit 모드: 은은한 링 */
body.mypage-editing .split-pill--editable{
  border-color: rgba(var(--accent), 0.22);
  box-shadow:
    0 12px 28px rgba(0,0,0,0.26),
    inset 0 1px 0 rgba(255,255,255,0.05),
    0 0 0 3px rgba(var(--accent),0.08);
}

/* 닉네임 + 중복확인 줄 */
.split-row{
  display: grid;
  grid-template-columns: 1fr 128px;
  gap: 12px;
  align-items: center;
  margin-bottom: 14px;
}
.split-row .split-pill{ margin-bottom: 0; }

/* 중복확인 버튼: 레드톤 + 가시성 유지 */
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

/* 닉네임 메시지 줄: 비면 공간 제거 */
#nicknameMsg{ margin: 6px 0 6px !important; }
#nicknameMsg:empty{ display:none; margin:0 !important; }

/* Cost Box (가독성 + 투박함 완화) */
.cost-box{
	margin-top: 18px;
	background: rgba(255, 255, 255, 0.035);
	border: 1px solid rgba(255,255,255,0.06);
	border-radius: 16px;
	padding: 18px;
}
.cost-row{
	display: flex;
	justify-content: space-between;
	padding: 10px 0;
	border-bottom: 1px solid rgba(255, 255, 255, 0.04);
	font-size: 14.5px;         
	line-height: 1.2;
}
.cost-row:last-child{ border-bottom: none; }
.cost-row .label{
	color: rgba(255, 255, 255, 0.78);
	font-weight: 600;
}
.cost-row .value{
	color: #fff;
	font-size: 14.5px;    
	font-weight: 800;
}
#cashWarn{ word-break: keep-all; }

/* 하단 액션 버튼 */
.edit-actions{
	margin-top: 20px;
	display: flex;
	gap: 12px;
}
.edit-actions .mypage-btn{ width: 100%; }

/* Modal */
.modal-backdrop-custom{
	position: fixed;
	left: 0; top: 0;
	width: 100%;
	height: 100%;
	background: rgba(0, 0, 0, 0.65);
	backdrop-filter: blur(0.3px);
	-webkit-backdrop-filter: blur(0.3px);
	display: none;
	align-items: center;
	justify-content: center;
	z-index: 20000;
	padding: 14px;
	box-sizing: border-box;
}
.modal-box{
	width: min(560px, 96vw);
	background: rgba(15, 20, 60, 0.98);
	border-radius: 18px;
	box-shadow: 0 16px 60px rgba(0,0,0,0.55);
	padding: 32px 34px 24px;
	box-sizing: border-box;
	max-height: 90vh;
	overflow-y: auto;
}
.modal-title{
	font-size: 21px;              /* 제목 크기 */
	font-weight: 900;
	color: #fff;
	margin-bottom: 6px;
	line-height: 1.25;
	word-break: keep-all;
}
.modal-desc{
	font-size: 14px;              /* 안내 문구 크기 */
	color: rgba(255, 255, 255, 0.78);
	margin-bottom: 14px;
	line-height: 1.5;
	word-break: keep-all;
}
.modal-cost{
	background: rgba(255,255,255,0.05);
	border-radius: 14px;
	padding: 18px 20px;
	margin-bottom: 14px;
	box-sizing: border-box;
}
.modal-cost .row{
	margin: 0 !important;
	padding: 10px 8px;            
	display: flex;
	justify-content: space-between;
	align-items: center;
	gap: 12px;
}
.modal-cost .row .l{
	color: rgba(255, 255, 255, 0.78);
	font-weight: 650;
	font-size: 14px;              /* 좌측 항목 글씨 */
	min-width: 0;
	flex: 1;
}
.modal-cost .row .r{
	color: #fff;
	font-weight: 850;
	font-size: 14.5px;            /* 우측 금액 글씨 */
	white-space: nowrap;
	flex: 0 0 auto;
	background: rgba(255,255,255,0.06);
	border: 1px solid rgba(255,255,255,0.08);
	padding: 5px 10px;
	border-radius: 999px;
	box-shadow: inset 0 1px 0 rgba(255,255,255,0.06);
}
.modal-actions{
	display: flex;
	gap: 10px;
}
.modal-btn{
	flex: 1;
	border: none;
	border-radius: 999px;
	padding: 12px 14px;
	font-weight: 900;
	color: #fff;
	background: rgba(255, 255, 255, 0.10);
	transition: .2s;
}
.modal-btn:hover{ background: rgba(255, 255, 255, 0.16); }
.modal-btn.yes{
	background: linear-gradient(135deg, #ff4c4c 0%, #e53637 55%, #c92c2d 100%) !important;
}

/* LEFT MENU */
.side-menu{
	list-style: none;
	padding-left: 0;
	margin: 0;
	display: flex;
	flex-direction: column;
	gap: 10px;
}
.side-menu .menu-item{ width: 100%; }
.side-menu .menu-link, .side-menu .menu-btn{
	width: 100%;
	display: flex;
	align-items: center;
	gap: 10px;
	padding: 12px 14px;
	border-radius: 14px;
	color: rgba(255, 255, 255, 0.88);
	background: rgba(255, 255, 255, 0.03);
	border: 1px solid rgba(255, 255, 255, 0.06);
	font-weight: 600;
	transition: 0.18s ease;
}
.side-menu .menu-link i, .side-menu .menu-btn i{
	width: 18px;
	text-align: center;
	opacity: 0.9;
}
.side-menu .menu-link:hover, .side-menu .menu-btn:hover{
	background: rgba(255, 255, 255, 0.06);
	border-color: rgba(255, 255, 255, 0.10);
	transform: translateY(-1px);
}
.side-menu .menu-link.is-active{
	background: rgba(229, 54, 55, 0.10);
	border-color: rgba(229, 54, 55, 0.22);
	color: #fff;
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

/* Layout fix */
.mypage-col-left{ padding-right: 28px !important; }
.mypage-col-right{ padding-left: 28px !important; }

.mypage-side-card{
	position: relative;
	z-index: 2;
	overflow: visible;
}
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
.mypage-side-card.text-center{
	display: flex;
	flex-direction: column;
	align-items: center;
}

/* 모바일 */
@media (max-width: 991.98px){
  .mypage-col-left{ padding-right: 0 !important; margin-bottom: 18px; }
  .mypage-col-right{ padding-left: 0 !important; }

  .split-row{ grid-template-columns: 1fr; }
  .nick-check-btn{ width: 100%; }

  .split-label{ width: 128px; }
  .split-value{ padding: 0 14px; }
  .split-value input{ padding-left: 10px; }

  .modal-actions{ flex-direction: column; }
  .profile-img-wrap{ width: 100%; max-width: 256px; height: auto; aspect-ratio: 1/1; }
}

/* "닉네임 변경 시 300원..." 문구 정렬 */
#nickCostMsg{
  display: flex;
  align-items: center;
  gap: 6px;
  min-height: 22px;
  margin: 8px 0 14px;
  line-height: 1;
}
#nickCostMsg b{ line-height: 1; }
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
		<h1 class="mypage-title__h1">마이페이지</h1>
	</div>

	<section class="spad mypage-spad">
		<div class="container">
			<div class="row">

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

						<div class="msg-area msg-info cost-hint" id="profileCostMsg" style="margin-top: 10px; display: none;">
							<span class="cost-hint__text">
								※ 프로필 사진 변경 시 <b>500원</b> 이 차감됩니다.
							</span>
						</div>
					</div>

					<div class="login-box-clean mypage-side-card" style="margin-top: 20px;">
						<ul class="side-menu">
							<li class="menu-item">
								<a class="menu-link ${activeMenu eq 'PW' ? 'is-active' : ''}"
									href="${ctx}/changePasswordPage.do">
									<i class="fa fa-lock"></i><span>비밀번호 변경</span>
								</a>
							</li>

							<li class="menu-item">
								<a class="menu-link ${activeMenu eq 'MYPOST' ? 'is-active' : ''}"
									href="${ctx}/myPostPage.do">
									<i class="fa fa-pencil-square-o"></i><span>내 글 보기</span>
								</a>
							</li>

							<li class="menu-item">
								<form action="${ctx}/withdraw.do" method="post" style="margin: 0;">
									<button type="submit" class="menu-btn danger"
										onclick="return confirm('정말 탈퇴하시겠습니까?\n탈퇴 후에는 복구할 수 없습니다.');">
										<i class="fa fa-sign-out"></i><span>회원 탈퇴</span>
									</button>
								</form>
							</li>
						</ul>
					</div>
				</div>

				<div class="col-12 col-lg-8 mypage-col-right">
					<div class="login-box-clean mypage-right-card">
						<h5 style="margin-bottom: 24px;">내 정보</h5>

						<form id="mypageForm" action="${ctx}/changeProfile.do" method="post">
							<input type="hidden" id="temporaryProfileImageToken" name="temporaryProfileImageToken" value="" />

							<!-- 아이디 -->
							<div class="split-pill" id="idPill">
								<div class="split-label">
									<span class="split-icon" aria-hidden="true">
										<svg viewBox="0 0 24 24">
											<path d="M20 21a8 8 0 0 0-16 0"></path>
											<circle cx="12" cy="8" r="4"></circle>
										</svg>
									</span>
									<span class="split-text">아이디</span>
								</div>
								<div class="split-value">
									<input id="idInput" type="text" value="${memberData.memberName}" readonly>
								</div>
							</div>

 

							<!-- 이메일 -->
							<div class="split-pill" id="emailPill">
								<div class="split-label">
									<span class="split-icon" aria-hidden="true">
										<svg viewBox="0 0 24 24">
											<path d="M4 6h16v12H4z"></path>
											<path d="m4 7 8 6 8-6"></path>
										</svg>
									</span>
									<span class="split-text">이메일</span>
								</div>
								<div class="split-value">
									<input id="emailInput" type="email" value="${memberData.memberEmail}" readonly>
								</div>
							</div>

							<!-- 닉네임 + 중복확인 -->
							<div class="split-row">
								<div class="split-pill split-pill--editable" id="nicknamePill">
									<div class="split-label">
										<span class="split-icon" aria-hidden="true">
											<svg viewBox="0 0 24 24">
												<rect x="3" y="5" width="18" height="14" rx="2"></rect>
												<circle cx="8.5" cy="12" r="2"></circle>
												<path d="M13 11h6"></path>
												<path d="M13 14h6"></path>
											</svg>
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

							<div class="msg-area msg-info" id="nickCostMsg" style="display: none;">
								※ 닉네임 변경 시 <b>300원</b>이 차감됩니다.
							</div>

							<c:set var="cashSafe" value="${empty memberData.memberCash ? 0 : memberData.memberCash}" />
							<fmt:formatNumber value="${cashSafe}" type="number" var="cashFmt" />

							<!-- 보유 캐시 -->
							<div class="split-pill" id="cashPill">
								<div class="split-label">
									<span class="split-icon" aria-hidden="true">
										<svg viewBox="0 0 24 24">
											<path d="M4 7h16a2 2 0 0 1 2 2v10a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V9a2 2 0 0 1 2-2z"></path>
											<path d="M2 11h20"></path>
											<path d="M16 15h4"></path>
										</svg>
									</span>
									<span class="split-text">보유 캐시</span>
								</div>
								<div class="split-value">
									<input id="cashDisplay" type="text" value="${cashFmt}원" readonly>
									<input type="hidden" id="cashRaw" value="${cashSafe}">
								</div>
							</div>

							<div class="cost-box" id="costBox" style="display: none;">
								<div class="cost-row">
									<div class="label">닉네임 변경</div>
									<div class="value" id="costNick">0원</div>
								</div>
								<div class="cost-row">
									<div class="label">프로필 사진 변경</div>
									<div class="value" id="costProfile">0원</div>
								</div>
								<div class="cost-row">
									<div class="label">총 차감 캐시</div>
									<div class="value" id="costTotal">0원</div>
								</div>
								<div class="cost-row">
									<div class="label">차감 후 보유 캐시</div>
									<div class="value" id="cashAfter">-</div>
								</div>
								<div class="msg-area msg-error" id="cashWarn" style="display: none; margin-top: 10px;">
									보유 캐시를 확인해주세요.
								</div>
							</div>

							<div class="row" style="margin-top: 30px;" id="viewActions">
								<div class="col-md-6">
									<button id="editBtn" type="button" class="mypage-btn">내 정보 수정</button>
								</div>
								<div class="col-md-6">
									<a href="${ctx}/cashChargePage.do" class="mypage-btn primary-red">캐시 충전</a>
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

	<!-- 확인 모달 -->
	<div id="modalBackdrop" class="modal-backdrop-custom">
		<div class="modal-box" role="dialog" aria-modal="true" aria-labelledby="modalTitle">
			<div class="modal-title" id="modalTitle">정말 수정하시겠습니까?</div>
			<div class="modal-desc">수정 완료 시 캐시가 차감되며 되돌릴 수 없습니다.</div>

			<div class="modal-cost">
				<div class="row">
					<div class="l">닉네임 변경</div>
					<div class="r" id="mCostNick">0원</div>
				</div>
				<div class="row">
					<div class="l">프로필 사진 변경</div>
					<div class="r" id="mCostProfile">0원</div>
				</div>
				<div class="row" style="border-top: 1px solid rgba(255, 255, 255, 0.08); margin-top: 6px; padding-top: 10px;">
					<div class="l">총 차감 캐시</div>
					<div class="r" id="mCostTotal">0원</div>
				</div>
			</div>

			<div class="modal-actions">
				<button id="modalYesBtn" class="modal-btn yes" type="button">확인</button>
				<button id="modalNoBtn" class="modal-btn" type="button">취소</button>
			</div>
		</div>
	</div>

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
		const COST_NICK = 300;
		const COST_PROFILE = 500;

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

		function formatWon(num) {
			return (num || 0).toLocaleString("ko-KR") + "원";
		}

		$("#editBtn").on("click", function() {
			editMode = true;
			$("body").addClass("mypage-editing");

			$("#viewActions").hide();
			$("#editActions").show();
			$("#costBox").show();

			$("#nicknameInput").prop("readonly", false);
			$("#nickCheckBtn").removeClass("disabled-btn");
			$("#profileBtnLabel").removeClass("disabled-btn");

			$("#nickCostMsg").show();
			$("#profileCostMsg").show();

			nicknameChecked = false;
			profileChanged = false;
			$("#nicknameMsg").text("");

			$("#temporaryProfileImageToken").val("");
			$("#profileInput").val("");

			updateCostAndButtons();
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
				setTimeout(function() {
					$("#profileWrap").removeClass("is-loading");
				}, 150);

				$("#temporaryProfileImageToken").val("");
				$("#profileInput").val("");
			}

			$("#nicknameInput").prop("readonly", true);
			$("#nickCheckBtn").addClass("disabled-btn").text("중복 확인");
			$("#profileBtnLabel").addClass("disabled-btn");

			$("#nickCostMsg").hide();
			$("#profileCostMsg").hide();

			$("#costBox").hide();
			$("#editActions").hide();
			$("#viewActions").show();

			$("#cashWarn").hide();
			$("#nicknameMsg").text("");

			nicknameChecked = false;
			profileChanged = false;

			updateCostAndButtons();
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
			updateCostAndButtons();
		});

		$("#nickCheckBtn").on("click", function() {
			if (!editMode) return;

			const nickname = $("#nicknameInput").val().trim();

			if (!NICK_REGEX.test(nickname)) {
				$("#nicknameMsg").removeClass("msg-ok").addClass("msg-error")
					.text("닉네임은 2~12자, 한글/영문/숫자만 사용할 수 있습니다.");
				nicknameChecked = false;
				updateCostAndButtons();
				return;
			}

			if (nickname === originalNickname) {
				$("#nicknameMsg").removeClass("msg-ok").addClass("msg-error")
					.text("현재 닉네임과 동일합니다.");
				nicknameChecked = false;
				updateCostAndButtons();
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
						updateCostAndButtons();
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
					updateCostAndButtons();
				},
				error: function() {
					$("#nicknameMsg").removeClass("msg-ok").addClass("msg-error")
						.text("중복확인 서버 호출에 실패했습니다.");
					nicknameChecked = false;
					$("#nickCheckBtn").text("중복 확인");
					updateCostAndButtons();
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

					const tempUrl = addCacheBust(res.temporaryProfileImageUrl);
					$("#profilePreview").attr("src", tempUrl);
					$("#profileWrap").removeClass("is-loading");

					$("#temporaryProfileImageToken").val(res.temporaryProfileImageToken);

					profileChanged = true;
					updateCostAndButtons();
				},
				error: function(xhr) {
					alert("프로필 업로드 실패(" + xhr.status + "). 다시 시도해주세요.");
					$("#profileInput").val("");
					$("#profileWrap").removeClass("is-loading");
				}
			});
		});

		function updateCostAndButtons() {
			const currentCash = parseInt($("#cashRaw").val(), 10) || 0;
			const newNickname = $("#nicknameInput").val().trim();
			const token = $("#temporaryProfileImageToken").val().trim();

			const nickChanged = editMode && (newNickname !== originalNickname);
			const nickCost = nickChanged ? COST_NICK : 0;

			const profileCost = (editMode && profileChanged && token.length > 0) ? COST_PROFILE : 0;

			const totalCost = nickCost + profileCost;
			const cashAfter = currentCash - totalCost;

			$("#costNick").text(formatWon(nickCost));
			$("#costProfile").text(formatWon(profileCost));
			$("#costTotal").text(formatWon(totalCost));
			$("#cashAfter").text(formatWon(Math.max(cashAfter, 0)));

			let canSave = true;

			if (!nickChanged && !(profileChanged && token.length > 0)) canSave = false;

			if (nickChanged) {
				if (!NICK_REGEX.test(newNickname)) canSave = false;
				if (!nicknameChecked) canSave = false;
			}

			if (profileChanged && token.length === 0) canSave = false;

			if (totalCost > currentCash) {
				canSave = false;
				$("#cashWarn").show();
			} else {
				$("#cashWarn").hide();
			}

			if (editMode && canSave) $("#saveBtn").removeClass("disabled-btn");
			else $("#saveBtn").addClass("disabled-btn");

			$("#mCostNick").text(formatWon(nickCost));
			$("#mCostProfile").text(formatWon(profileCost));
			$("#mCostTotal").text(formatWon(totalCost));
		}

		$("#saveBtn").on("click", function() {
			if ($(this).hasClass("disabled-btn")) return;
			$("#modalBackdrop").css("display", "flex");
		});

		function hideConfirmModal() {
			$("#modalBackdrop").hide();
		}

		$(".modal-box").on("click", function(e) {
			e.stopPropagation();
		});

		$("#modalNoBtn").on("click", function() {
			hideConfirmModal();
			exitEditMode(true);
		});

		$("#modalYesBtn").on("click", function() {
			if ($("#saveBtn").hasClass("disabled-btn")) return;
			$("#modalYesBtn").prop("disabled", true);
			$("#mypageForm").submit();
		});

		updateCostAndButtons();
	});
	</script>

</body>
</html>
