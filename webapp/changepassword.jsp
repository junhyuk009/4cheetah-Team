<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String ctx = request.getContextPath();

    if (session.getAttribute("memberId") == null) {
        response.sendRedirect(ctx + "/loginPage.do");
        return;
    }

    String role = (String) session.getAttribute("memberRole"); // ✅ 역할
    boolean isAdmin = role != null && role.toUpperCase().contains("ADMIN");

    String backUrl  = isAdmin ? (ctx + "/adminPage.do") : (ctx + "/myPage.do");
    String backText = isAdmin ? "← 관리자 페이지로" : "← 마이페이지로";
%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>AniMale | 비밀번호 변경</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<link rel="icon" type="image/png"
	href="<%=request.getContextPath()%>/favicon.png">

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

<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>

<style>
.icon_profile, .icon_search, .search-switch {
	display: none !important;
}

.change-wrap {
	max-width: 520px;
	margin: 0 auto;
	background: #fff;
	padding: 40px 30px;
	border-radius: 16px;
}

.change-box h4 {
	text-align: center;
	font-weight: 700;
	margin-bottom: 30px;
	color: #000;
}

.msg {
	font-size: 12px;
	margin-top: 6px;
	min-height: 16px;
}

.msg.error {
	color: #ff4c4c;
}

.msg.success {
	color: #4caf50;
}

.btn-main {
	width: 100%;
	height: 52px;
	border-radius: 30px;
	background: #e53637 !important;
	border: none;
	color: #fff !important;
	font-weight: 600;
	margin-top: 20px;
}

.btn-main:disabled {
	background: #bdbdbd !important;
}

.cancel-link {
	display: block;
	margin-top: 16px;
	text-align: center;
	color: #2f80ed;
	font-size: 14px;
}

.login-input input {
	border: 1.5px solid rgba(0, 0, 0, 0.35) !important;
	border-radius: 30px !important;
	height: 50px;
	padding-left: 48px;
	background: #fff !important;
}

.login-input input:focus {
	outline: none;
	border-color: #000 !important;
}
</style>

<script>
$(function() {

    const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[!@#$%^&*()_+=-]).{8,16}$/;

    let currentPasswordValid = false;
    let newPasswordValid = false;

    function toggleButton() {
        $(".btn-main").prop("disabled", !(currentPasswordValid && newPasswordValid));
    }

    $("#currentPassword").on("input", function() {
        if ($(this).val().length >= 4) {
            $("#currentPasswordMsg")
                .text("비밀번호 입력이 확인되었습니다.")
                .removeClass("error").addClass("success");
            currentPasswordValid = true;
        } else {
            $("#currentPasswordMsg")
                .text("비밀번호를 입력해주세요.")
                .removeClass("success").addClass("error");
            currentPasswordValid = false;
        }

        $("#newPassword").trigger("input");
        toggleButton();
    });

    $("#newPassword, #newPasswordConfirm").on("input", function() {

        const currentPassword = $("#currentPassword").val();
        const newPassword = $("#newPassword").val();
        const newPasswordConfirm = $("#newPasswordConfirm").val();

        if (!passwordRegex.test(newPassword)) {
            $("#newPasswordMsg")
                .text("8~16자, 영문/숫자/특수문자 포함")
                .removeClass("success").addClass("error");
            newPasswordValid = false;
            toggleButton();
            return;
        }

        if (currentPassword.length > 0 && currentPassword === newPassword) {
            $("#newPasswordMsg")
                .text("현재 비밀번호와 다른 비밀번호를 입력해주세요.")
                .removeClass("success").addClass("error");
            newPasswordValid = false;
            toggleButton();
            return;
        }

        if (newPassword === newPasswordConfirm) {
            $("#newPasswordMsg")
                .text("새 비밀번호가 일치합니다.")
                .removeClass("error").addClass("success");
            newPasswordValid = true;
        } else {
            $("#newPasswordMsg")
                .text("새 비밀번호가 일치하지 않습니다.")
                .removeClass("success").addClass("error");
            newPasswordValid = false;
        }

        toggleButton();
    });

    $("form").on("submit", function(e) {

        const currentPassword = $("#currentPassword").val();
        const newPassword = $("#newPassword").val();

        if (currentPassword === newPassword) {
            e.preventDefault();
            return false;
        }

        if (!(currentPasswordValid && newPasswordValid)) {
            e.preventDefault();
            return false;
        }
    });

});
</script>
</head>

<body>

	<%@ include file="/common/header.jsp"%>

	<section class="spad">
		<div class="container">

			<div class="change-wrap">
				<div class="login-box-clean change-box">

					<h4>비밀번호 변경</h4>

					<form action="<%=request.getContextPath()%>/changePassword.do"
						method="post">

						<div class="login-input">
							<i class="fa fa-lock"></i> <input type="password"
								id="currentPassword" name="currentPassword"
								placeholder="현재 비밀번호">
						</div>
						<div id="currentPasswordMsg" class="msg"></div>

						<div class="login-input" style="margin-top: 18px;">
							<i class="fa fa-key"></i> <input type="password" id="newPassword"
								name="newPassword" placeholder="새 비밀번호">
						</div>

						<div class="login-input">
							<i class="fa fa-key"></i> <input type="password"
								id="newPasswordConfirm" placeholder="새 비밀번호 확인">
						</div>
						<div id="newPasswordMsg" class="msg"></div>

						<button type="submit" class="btn-main" disabled>비밀번호 변경</button>
						<a href="<%= backUrl %>" class="cancel-link"><%= backText %></a>

					</form>

				</div>
			</div>

		</div>
	</section>

	<%@ include file="/common/footer.jsp"%>

	<script src="<%=request.getContextPath()%>/js/bootstrap.min.js"></script>
</body>
</html>
