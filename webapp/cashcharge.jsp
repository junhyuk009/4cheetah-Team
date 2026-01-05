<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>AniMale | 캐시 충전</title>
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

<style>
/*캐시 충전 박스 */
.charge-box {
	background: #0b0c2a;
	border-radius: 12px;
	padding: 40px;
}

.charge-title {
	font-size: 22px;
	font-weight: 700;
	color: #fff;
	margin-bottom: 30px;
}

/* 금액 버튼 */
.charge-amounts {
	display: flex;
	gap: 15px;
	margin-bottom: 30px;
}

.charge-amounts button {
	flex: 1;
	background: #1c1d4a;
	border: none;
	border-radius: 30px;
	padding: 14px 0;
	color: #fff;
	font-weight: 600;
	cursor: pointer;
}

.charge-amounts button.active {
	background: #e53637;
}

/* 선택 금액 표시 */
.selected-amount {
	background: #fff;
	border-radius: 30px;
	padding: 14px 20px;
	font-weight: 600;
	margin-bottom: 30px;
}

/* 결제 버튼 */
.kakao-pay-btn {
	width: 100%;
	background: #e53637;
	border: none;
	border-radius: 30px;
	padding: 16px;
	color: #fff;
	font-size: 16px;
	font-weight: 700;
	cursor: pointer;
}

/* 버튼 묶음 */
.charge-btn-wrap {
	display: flex;
	gap: 14px;
	margin-top: 24px;
}

/* 결제 버튼 */
.btn-pay {
	flex: 1;
	height: 56px;
	background: #e53637;
	color: #fff;
	border: none;
	border-radius: 28px;
	font-size: 16px;
	font-weight: 600;
	cursor: pointer;
}

/* 결제 버튼 비활성화 상태 */
.btn-pay:disabled {
	opacity: 0.45;
	cursor: not-allowed;
}

/* 취소 버튼 */
.btn-cancel {
	flex: 1;
	height: 56px;
	background: #2a2a4a;
	color: #fff;
	border: 1px solid #444;
	border-radius: 28px;
	font-size: 16px;
	cursor: pointer;
}

/* hover 효과 */
.btn-pay:hover {
	background: #ff4c4c;
}

.btn-cancel:hover {
	background: #3a3a6a;
}

/* 캐시 충전 페이지에서는 검색 아이콘 숨김 */
.header__right__icons .icon_search {
	display: none;
}
</style>
</head>

<body class="cash-charge-page">

	<%@ include file="/common/header.jsp"%>

	<!-- Breadcrumb -->
	<section class="normal-breadcrumb set-bg"
		data-setbg="<%=request.getContextPath()%>/img/normal-breadcrumb.jpg">
		<div class="container">
			<div class="row">
				<div class="col-lg-12 text-center">
					<div class="normal__breadcrumb__text">
						<h2>캐시 충전</h2>
						<p>카카오페이로 간편하게 캐시를 충전하세요.</p>
					</div>
				</div>
			</div>
		</div>
	</section>

	<section class="spad">
		<div class="container">
			<div class="row justify-content-center">
				<div class="col-lg-6">

					<div class="charge-box">

						<div class="charge-title">충전 금액 선택</div>

						<!-- 금액 선택 -->
						<div class="charge-amounts">
							<button type="button" data-amount="1000">1,000원</button>
							<button type="button" data-amount="5000">5,000원</button>
							<button type="button" data-amount="10000">10,000원</button>
							<button type="button" data-amount="50000">50,000원</button>
						</div>

						<!-- 선택된 금액 -->
						<div class="selected-amount">
							선택 금액 : <span id="selectedAmountText">0</span> 원
						</div>

						<!-- 결제 -->
						<form id="chargeForm" action="<%=request.getContextPath()%>/KakaoPayReady.do"
							method="post">
							<input type="hidden" name="selectCash" id="amountInput">

							<div class="charge-btn-wrap">
								<!-- 금액 선택 전에는 disabled -->
								<button id="payBtn" type="submit" class="btn-pay" disabled>카카오페이로 결제하기</button>

								<button type="button" class="btn-cancel"
									onclick="location.href='<%=request.getContextPath()%>/myPage.do'">충전
									취소</button>
							</div>
						</form>

					</div>

				</div>
			</div>
		</div>
	</section>

	<%@ include file="/common/footer.jsp"%>

	<script src="<%=request.getContextPath()%>/js/jquery-3.3.1.min.js"></script>
	<script>
		$(function() {

			function setPayEnabled(enabled) {
				$("#payBtn").prop("disabled", !enabled);
			}

			// 초기: 결제 버튼 비활성화
			setPayEnabled(false);

			$(".charge-amounts button").click(function() {

				// 이미 선택된 버튼을 다시 눌렀을 경우 → 선택 취소
				if ($(this).hasClass("active")) {
					$(this).removeClass("active");
					$("#amountInput").val("");
					$("#selectedAmountText").text("0");
					setPayEnabled(false);
					return;
				}

				// 다른 금액 선택
				$(".charge-amounts button").removeClass("active");
				$(this).addClass("active");

				const amount = $(this).data("amount");
				$("#amountInput").val(amount);
				$("#selectedAmountText").text(amount.toLocaleString());

				// 선택되면 결제 버튼 활성화
				setPayEnabled(true);
			});

			// 최종 방어: 금액 없이 submit 막기 (프론트 우회 대비)
			$("#chargeForm").on("submit", function(e) {
				const v = $("#amountInput").val();
				if (!v || parseInt(v, 10) <= 0) {
					alert("충전 금액을 선택해주세요.");
					setPayEnabled(false);
					e.preventDefault();
				}
			});

		});
	</script>
</body>
</html>
