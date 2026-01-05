<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>AniMale | 비밀번호 재설정</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<link rel="icon" type="image/png" href="<%=request.getContextPath()%>/favicon.png">

<link href="https://fonts.googleapis.com/css2?family=Oswald:wght@300;400;500;600;700&display=swap" rel="stylesheet">
<link href="https://fonts.googleapis.com/css2?family=Mulish:wght@300;400;500;600;700;800;900&display=swap" rel="stylesheet">

<link rel="stylesheet" href="<%=request.getContextPath()%>/css/bootstrap.min.css">
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/font-awesome.min.css">
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/style.css">

<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>

<style>
/* 헤더 아이콘 숨김 (비로그인 페이지용) */
.icon_profile,
.icon_search,
.search-switch { display:none !important; }

#logoutBtn,
.logoutBtn,
.logout-btn,
.logout,
a[href*="logout"],
a[href$="logout.do"],
a[href$="/logout.do"] { display:none !important; }

/* 카드 래퍼 */
.reset-wrap {
	max-width:420px;
	margin:0 auto;
	background:#fff;
	padding:40px 30px;
	border-radius:16px;
	position: relative; /* 로딩 오버레이 기준 */
}

.reset-wrap input {
	width:100%;
	height:50px;
	border-radius:30px;
	padding:0 18px;
}

/* 한 줄 (인풋+버튼) */
.row-inline {
	display:flex;
	gap:12px;
	align-items:center;
	margin-bottom:14px;
}
.row-inline input { flex:1; }

/* 버튼 */
.btn-outline {
	height:50px;
	min-width:110px;
	border-radius:30px;
	background:#fff;
	border:2px solid #e53637;
	color:#e53637;
	font-weight:600;
}
.btn-outline:disabled {
	opacity:.4;
	cursor:not-allowed;
}

.btn-main {
	width:100%;
	height:52px;
	border-radius:30px;
	background:#e53637;
	border:none;
	color:#fff;
	font-weight:600;
}
.btn-main:disabled { background:#bdbdbd; }

/* 메시지 */
.msg { font-size:12px; margin:4px 0 12px; }
.msg.error { color:#ff4c4c; }
.msg.success { color:#4caf50; }

.timer { font-size:13px; color:#e53637; margin-bottom:8px; }

#memberPassword,
#memberPasswordConfirm { margin-top:12px; }

/* 상태별 테두리/버튼 색 (빨강/초록/로딩) */
.row-inline input{
  border:2px solid #e53637;
  outline:none;
}
.row-inline input:focus{
  box-shadow:0 0 0 3px rgba(229,54,55,0.15);
}

/* 성공 (초록) */
.row-inline.is-ok input,
.row-inline.is-ok .btn-outline{
  border-color:#4caf50 !important;
  color:#4caf50 !important;
}
.row-inline.is-ok input:focus{
  box-shadow:0 0 0 3px rgba(76,175,80,0.15);
}

/* 실패 (빨강 강조) */
.row-inline.is-bad input,
.row-inline.is-bad .btn-outline{
  border-color:#ff4c4c !important;
  color:#ff4c4c !important;
}
.row-inline.is-bad input:focus{
  box-shadow:0 0 0 3px rgba(255,76,76,0.15);
}

/* 로딩 상태 */
.row-inline.is-loading .btn-outline{
  opacity:.75;
}

/* 로딩 오버레이 (메일 발송 중 빙글빙글) */
.loading-overlay{
  position:absolute;
  inset:0;
  background:rgba(255,255,255,0.75);
  display:flex;
  align-items:center;
  justify-content:center;
  border-radius:16px;
  z-index:50;
}
.loading-card{
  display:flex;
  flex-direction:column;
  align-items:center;
  gap:10px;
  padding:18px 16px;
  border-radius:14px;
  background:#fff;
  box-shadow:0 10px 25px rgba(0,0,0,0.12);
}
.spinner{
  width:28px; height:28px;
  border:3px solid rgba(229,54,55,0.25);
  border-top-color:#e53637;
  border-radius:50%;
  animation:spin 0.8s linear infinite;
}
@keyframes spin{
  from{ transform:rotate(0deg); }
  to{ transform:rotate(360deg); }
}
.loading-text{
  font-size:13px;
  font-weight:700;
  color:#333;
}

/* 로그인 화면으로 이동 링크 */
.login-back {
  display:flex;
  align-items:center;
  justify-content:center;
  gap:8px;
  margin-top:18px;
  height:46px;
  border-radius:999px;
  border:1px solid rgba(0,0,0,0.10);
  background:#fafafa;
  color:#222;
  font-size:14px;
  font-weight:700;
  text-decoration:none;
  transition:transform .12s ease, box-shadow .12s ease, background .12s ease;
}
.login-back:hover{
  background:#fff;
  box-shadow:0 10px 20px rgba(0,0,0,0.08);
  transform:translateY(-1px);
  text-decoration:none;
  color:#111;
}
.login-back:active{
  transform:translateY(0);
  box-shadow:none;
}
.login-back .chev{
  font-size:16px;
  line-height:1;
  display:inline-block;
  transform:translateY(-1px);
}
.login-back .hint{
  font-weight:800;
}
</style>

<script>
$(function () {

  const ctx = "<%=request.getContextPath()%>";

  let resendTimer = null;
  let resendLeft = 0;
  let resendEnableAt = 0;

  let idChecked = false;
  let verified = false;

  const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[!@#$%^&*()_+=-]).{8,16}$/;

  // 초기 UI 
  $("#authArea").hide();
  $("#pwArea").hide();

  $("#memberEmail").prop("readonly", true);
  $("#sendBtn").prop("disabled", true);
  $("#verifyBtn").prop("disabled", true);
  $("#resendBtn").prop("disabled", true);
  $(".btn-main").prop("disabled", true);

  // 헬퍼 
  function showMsg($el, msg, ok) {
    $el.text(msg).removeClass("error success").addClass(ok ? "success" : "error");
  }
  function clearMsg($el) {
    $el.text("").removeClass("error success");
  }

  function setRowState(rowId, state){ // "ok" | "bad" | "loading" | "clear"
    const $row = $("#" + rowId);
    $row.removeClass("is-ok is-bad is-loading");
    if(state === "ok") $row.addClass("is-ok");
    if(state === "bad") $row.addClass("is-bad");
    if(state === "loading") $row.addClass("is-loading");
  }

  function showLoading(text){
    $("#loadingText").text(text || "처리 중...");
    $("#loadingOverlay").show();
  }
  function hideLoading(){
    $("#loadingOverlay").hide();
  }

  function resetAuthUI() {
    verified = false;
    $(".btn-main").prop("disabled", true);
    $("#pwArea").hide();

    clearInterval(resendTimer);
    resendTimer = null;
    resendLeft = 0;

    $("#authArea").hide();
    $("#timer").text("");
    $("#verificationCode").val("");
    clearMsg($("#verificationCodeMsg"));

    $("#verifyBtn").prop("disabled", true).text("인증번호 확인");
    $("#resendBtn").prop("disabled", true);

    $("#memberPassword").val("");
    $("#memberPasswordConfirm").val("");
    clearMsg($("#memberPasswordMsg"));

    setRowState("rowEmail", "clear");
    setRowState("rowCode", "clear");

    $("#sendBtn").text("인증번호 발송");
  }

  function startTimer(seconds) {
    clearInterval(resendTimer);
    resendLeft = seconds;

    resendEnableAt = Date.now() + 5000; // 5초 뒤 재요청 가능
    $("#resendBtn").prop("disabled", true);

    const render = () => {
      const m = Math.floor(resendLeft / 60);
      const s = resendLeft % 60;
      $("#timer").text(String(m).padStart(2, "0") + ":" + String(s).padStart(2, "0"));
    };

    render();

    resendTimer = setInterval(function () {
      resendLeft--;
      render();

      if (!verified && Date.now() >= resendEnableAt) {
        $("#resendBtn").prop("disabled", false);
      }

      if (resendLeft <= 0) {
        clearInterval(resendTimer);
        $("#timer").text("만료됨 (재요청 가능)");
        $("#resendBtn").prop("disabled", false);
      }
    }, 1000);
  }

  $("#memberName").on("input", function () {
    idChecked = false;
    verified = false;

    $("#sendBtn").prop("disabled", true).text("인증번호 발송");
    $("#memberEmail").val("");

    clearMsg($("#memberNameMsg"));
    clearMsg($("#memberEmailMsg"));

    $("#memberName").prop("readonly", false);

    setRowState("rowMember", "clear");
    resetAuthUI();
  });

  // 1) 아이디 확인
  $("#memberNameCheckBtn").click(function () {
    const memberName = $("#memberName").val().trim();
    if (!memberName) {
      showMsg($("#memberNameMsg"), "아이디를 입력해주세요.", false);
      setRowState("rowMember", "bad");
      return;
    }

    $("#memberNameCheckBtn").prop("disabled", true);
    setRowState("rowMember", "loading");

    $.ajax({
      url: ctx + "/FindPasswordMemberLookup",
      method: "POST",
      dataType: "json",
      data: { memberName: memberName },
      success: function (res) {
        if (!res || res.success !== true) {
          showMsg($("#memberNameMsg"), (res && res.message) ? res.message : "요청에 실패했습니다.", false);
          idChecked = false;
          $("#sendBtn").prop("disabled", true);
          setRowState("rowMember", "bad");
          return;
        }

        if (res.exists !== true) {
          showMsg($("#memberNameMsg"), res.message || "존재하지 않는 아이디입니다.", false);
          idChecked = false;
          $("#sendBtn").prop("disabled", true);
          $("#memberEmail").val("");
          setRowState("rowMember", "bad");
          resetAuthUI();
          return;
        }

        idChecked = true;
        showMsg($("#memberNameMsg"), "아이디가 확인되었습니다.", true);
        setRowState("rowMember", "ok");

        $("#memberEmail").val(res.memberEmail || "");
        clearMsg($("#memberEmailMsg"));

        $("#sendBtn").prop("disabled", false).text("인증번호 발송");
        $("#memberName").prop("readonly", true);

        resetAuthUI();
      },
      error: function () {
        showMsg($("#memberNameMsg"), "서버 통신 오류", false);
        idChecked = false;
        $("#sendBtn").prop("disabled", true);
        setRowState("rowMember", "bad");
      },
      complete: function () {
        $("#memberNameCheckBtn").prop("disabled", false);
        if (!$("#rowMember").hasClass("is-ok") && !$("#rowMember").hasClass("is-bad")) {
          setRowState("rowMember", "clear");
        }
      }
    });
  });

  // 2) 인증번호 발송
  $("#sendBtn").click(function () {
    if (!idChecked) {
      showMsg($("#memberEmailMsg"), "아이디 확인을 먼저 진행해주세요.", false);
      setRowState("rowEmail", "bad");
      return;
    }

    const memberName = $("#memberName").val().trim();
    if (!memberName) {
      showMsg($("#memberNameMsg"), "아이디를 입력해주세요.", false);
      setRowState("rowMember", "bad");
      return;
    }

    $("#sendBtn").prop("disabled", true).text("발송 중...");
    clearMsg($("#memberEmailMsg"));

    setRowState("rowEmail", "loading");
    showLoading("인증번호 발송 중...");

    $.ajax({
      url: ctx + "/FindPasswordSendCode",
      method: "POST",
      dataType: "json",
      data: { memberName: memberName },
      success: function (res) {
        if (!res || res.success !== true) {
          setRowState("rowEmail", "bad");
          $("#sendBtn").prop("disabled", false).text("인증번호 발송");
          showMsg($("#memberEmailMsg"), (res && res.message) ? res.message : "인증번호 발송 실패", false);
          return;
        }

        setRowState("rowEmail", "ok");
        $("#sendBtn").prop("disabled", true).text("발송 완료");
        showMsg($("#memberEmailMsg"), "인증번호를 발송했습니다. 이메일을 확인해주세요.", true);

        $("#authArea").slideDown();
        $("#verifyBtn").prop("disabled", false).text("인증번호 확인");

        $("#verificationCode").val("");
        clearMsg($("#verificationCodeMsg"));
        setRowState("rowCode", "clear");

        verified = false;
        $("#pwArea").hide();
        $(".btn-main").prop("disabled", true);

        const expireSeconds = res.expireSeconds || 180;
        startTimer(expireSeconds);
      },
      error: function () {
        setRowState("rowEmail", "bad");
        $("#sendBtn").prop("disabled", false).text("인증번호 발송");
        showMsg($("#memberEmailMsg"), "서버 통신 오류", false);
      },
      complete: function(){
        hideLoading();
        $("#rowEmail").removeClass("is-loading");
      }
    });
  });

  // 3) 재요청
  $("#resendBtn").click(function () {
    if (!idChecked) return;

    const memberName = $("#memberName").val().trim();
    if (!memberName) return;

    $("#resendBtn").prop("disabled", true);
    $("#sendBtn").prop("disabled", true).text("발송 중...");
    setRowState("rowEmail", "loading");
    showLoading("인증번호 재발송 중...");

    clearMsg($("#verificationCodeMsg"));

    $.ajax({
      url: ctx + "/FindPasswordSendCode",
      method: "POST",
      dataType: "json",
      data: { memberName: memberName },
      success: function (res) {
        if (!res || res.success !== true) {
          showMsg($("#verificationCodeMsg"), (res && res.message) ? res.message : "재요청 실패", false);
          $("#sendBtn").prop("disabled", true).text("발송 완료");
          setRowState("rowEmail", "bad");
          return;
        }

        showMsg($("#verificationCodeMsg"), "인증번호를 재발송했습니다.", true);
        $("#verificationCode").val("");

        setRowState("rowEmail", "ok");
        $("#sendBtn").prop("disabled", true).text("발송 완료");

        const expireSeconds = res.expireSeconds || 180;
        startTimer(expireSeconds);
      },
      error: function () {
        showMsg($("#verificationCodeMsg"), "서버 통신 오류", false);
        setRowState("rowEmail", "bad");
      },
      complete: function(){
        hideLoading();
        $("#rowEmail").removeClass("is-loading");
      }
    });
  });

  // 4) 인증번호 확인
  $("#verifyBtn").click(function () {
    const code = $("#verificationCode").val().trim();
    if (!code) {
      showMsg($("#verificationCodeMsg"), "인증번호를 입력해주세요.", false);
      setRowState("rowCode", "bad");
      return;
    }

    $("#verifyBtn").prop("disabled", true).text("확인 중...");
    setRowState("rowCode", "loading");

    $.ajax({
      url: ctx + "/FindPasswordVerifyCode",
      method: "POST",
      dataType: "json",
      data: { code: code },
      success: function (res) {
        if (!res || res.success !== true) {
          showMsg($("#verificationCodeMsg"), (res && res.message) ? res.message : "인증 실패", false);
          $("#verifyBtn").prop("disabled", false).text("인증번호 확인");
          setRowState("rowCode", "bad");
          return;
        }

        verified = true;
        clearInterval(resendTimer);

        showMsg($("#verificationCodeMsg"), "인증되었습니다.", true);
        setRowState("rowCode", "ok");

        $("#verifyBtn").text("인증 완료").prop("disabled", true);
        $("#resendBtn").prop("disabled", true);
        $("#sendBtn").prop("disabled", true).text("발송 완료");

        $("#pwArea").slideDown();
      },
      error: function () {
        showMsg($("#verificationCodeMsg"), "서버 통신 오류", false);
        $("#verifyBtn").prop("disabled", false).text("인증번호 확인");
        setRowState("rowCode", "bad");
      },
      complete: function(){
        $("#rowCode").removeClass("is-loading");
      }
    });
  });

  // 5) 인증 취소 (전체 재시작)
  $("#emailEditBtn").click(function () {
    idChecked = false;
    verified = false;

    $("#memberName").prop("readonly", false).val("");
    $("#memberEmail").val("");

    $("#sendBtn").prop("disabled", true).text("인증번호 발송");
    $("#verifyBtn").prop("disabled", true).text("인증번호 확인");
    $("#resendBtn").prop("disabled", true);

    clearMsg($("#memberNameMsg"));
    clearMsg($("#memberEmailMsg"));
    clearMsg($("#verificationCodeMsg"));
    clearMsg($("#memberPasswordMsg"));

    setRowState("rowMember", "clear");
    resetAuthUI();
  });

  // 6) 비밀번호 입력 검증
  $("#memberPassword, #memberPasswordConfirm").on("input", function () {
    if (!verified) return;

    const pw = $("#memberPassword").val();
    const pw2 = $("#memberPasswordConfirm").val();

    if (!passwordRegex.test(pw)) {
      showMsg($("#memberPasswordMsg"), "8~16자, 영문/숫자/특수문자 포함", false);
      $(".btn-main").prop("disabled", true);
      return;
    }

    if (pw === pw2 && pw2.length > 0) {
      showMsg($("#memberPasswordMsg"), "비밀번호가 일치합니다.", true);
      $(".btn-main").prop("disabled", false);
    } else {
      showMsg($("#memberPasswordMsg"), "비밀번호가 일치하지 않습니다.", false);
      $(".btn-main").prop("disabled", true);
    }
  });

});
</script>

</head>

<body>

<%@ include file="/common/header.jsp" %>

<section class="login spad">
	<div class="container">

		<h3 style="text-align:center;font-weight:700;color:#fff;margin:30px 0 24px;">
			비밀번호 재설정
		</h3>

		<div class="reset-wrap">

			<form action="<%=request.getContextPath()%>/findPassword.do" method="post">

				<div class="row-inline" id="rowMember">
					<input type="text" id="memberName" name="memberName" placeholder="아이디">
					<button type="button" id="memberNameCheckBtn" class="btn-outline">아이디 확인</button>
				</div>
				<div id="memberNameMsg" class="msg"></div>

				<div class="row-inline" id="rowEmail">
					<input type="email" id="memberEmail" name="memberEmail" placeholder="이메일">
					<button type="button" id="sendBtn" class="btn-outline">인증번호 발송</button>
				</div>
				<div id="memberEmailMsg" class="msg"></div>

				<div id="authArea">
					<div class="timer" id="timer"></div>

					<div class="row-inline" id="rowCode">
						<input type="text" id="verificationCode" placeholder="인증번호">
						<button type="button" id="verifyBtn" class="btn-outline">인증번호 확인</button>
					</div>
					<div id="verificationCodeMsg" class="msg"></div>

					<div class="row-inline">
						<button type="button" id="resendBtn" class="btn-outline">재요청</button>
						<button type="button" id="emailEditBtn" class="btn-outline">인증 취소</button>
					</div>
				</div>

        <div id="pwArea" style="display:none;">
				  <input type="password" id="memberPassword" name="memberPassword" placeholder="새 비밀번호">
				  <input type="password" id="memberPasswordConfirm" placeholder="새 비밀번호 확인">
				  <div id="memberPasswordMsg" class="msg"></div>

				  <button type="submit" class="btn-main" disabled>비밀번호 변경</button>
        </div>

        <a href="<%=request.getContextPath()%>/loginPage.do" class="login-back">
          <span class="chev">&lt;</span>
          <span class="hint">로그인 화면으로 이동</span>
        </a>

			</form>

      <!-- 메일 발송 중 로딩 오버레이 -->
      <div id="loadingOverlay" class="loading-overlay" style="display:none;">
        <div class="loading-card">
          <div class="spinner"></div>
          <div id="loadingText" class="loading-text">처리 중...</div>
        </div>
      </div>

		</div>
	</div>
</section>

<%@ include file="/common/footer.jsp" %>

<script src="<%=request.getContextPath()%>/js/bootstrap.min.js"></script>
</body>
</html>
