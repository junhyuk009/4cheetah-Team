package controller.member;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import controller.common.Action;
import controller.common.ActionForward;

public class KakaoPayReadyAction implements Action {

	// 카카오페이 결제준비 API
	private static final String READY_URL = "https://open-api.kakaopay.com/online/v1/payment/ready";

	// 테스트 CID(단건 결제)
	private static final String CID = "TC0ONETIME";

	// Web 플랫폼에 등록한 사이트 도메인과 동일하게 맞추기
	private static final String BASE_URL = "http://localhost:8088";

	// Secret key(dev) 사용
	private static final String SECRET_KEY_DEV = "*********************"; // 키값 입력 필요

	/*
	 * [KakaoPayReadyAction 흐름]
	 * 1) 로그인 사용자 확인(세션 memberId)
	 * 2) 충전 금액 수신 + 숫자 변환 + 허용 금액 검증
	 *    - selectCash : 요청 파라미터(문자열)
	 *    - cashCharge : 실제 충전 금액(int)
	 * 3) 가맹점 주문번호(partner_order_id) 생성 (UUID 기반)
	 * 4) 콜백 URL 3종 생성 (승인/취소/실패)
	 * 5) ready API 요청 JSON 바디 구성
	 * 6) 카카오 ready API 호출 (서버 → 카카오 서버)
	 * 7) 카카오 응답(JSON) 수신 후 tid, next_redirect_pc_url 추출
	 * 8) tid 세션 저장 (Approve 단계에서 tid + pg_token으로 승인 요청하기 위해 필요)
	 * 9) 사용자 브라우저를 카카오 결제 페이지(next_redirect_pc_url)로 redirect
	 */

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession(false);
		ActionForward forward = new ActionForward();

		try {
			System.out.println("[카카오페이 READY 로그] 시작");

			// 1) 로그인 체크 (세션에서 memberId 사용)
			// 비로그인이면 로그인페이지로 강제 이동
			if (session == null || session.getAttribute("memberId") == null) {
				forward.setRedirect(true);
				forward.setPath(request.getContextPath() + "/loginPage.do");
				return forward;
			}
			// 세션에 저장된 사용자 PK
			Integer memberIdObj = (Integer) session.getAttribute("memberId");
			if (memberIdObj == null) {
			    forward.setRedirect(true);
			    forward.setPath(request.getContextPath() + "/loginPage.do");
			    return forward;
			}
			int memberId = memberIdObj; // 언박싱

			// cashcharge.jsp에서 selectCash로 넘어온 금액 받기
			String selectCash = request.getParameter("selectCash");
			if (selectCash == null) {
				selectCash = "";
			}
			selectCash = selectCash.trim();

			// 숫자 변환 (문자/빈값이면 실패 처리)
			int cashCharge;
			try {
				cashCharge = Integer.parseInt(selectCash);
			} catch (NumberFormatException e) {
				System.out.println("[카카오페이 READY 로그] 금액 파싱 실패 : selectCash=[" + selectCash + "]");
				// 사용자에게 결과 페이지에서 실패 안내
				request.setAttribute("payResult", "FAIL");
				request.setAttribute("message", "충전 금액 형식이 올바르지 않습니다.");
				forward.setRedirect(false);
				forward.setPath("/cashresult.jsp");
				return forward;
			}

			// 2) 허용 금액 검증 (현재는 옵션이 4개라서 OR 비교로 막아둠)
			if (!(cashCharge == 1000 || cashCharge == 5000 || cashCharge == 10000 || cashCharge == 50000)) {
				System.out.println("[카카오페이 READY 로그] 허용되지 않은 금액 : cashCharge=[" + cashCharge + "]");
				request.setAttribute("payResult", "FAIL");
				request.setAttribute("message", "허용되지 않은 충전 금액입니다.");
				forward.setRedirect(false);
				forward.setPath("/cashresult.jsp");
				return forward;
			}

			// 3) partner_order_id 생성(임시 주문번호)
			// UUID를 통한 고유번호 생성
			String partnerOrderId = "CASH_" + UUID.randomUUID();

			// 4) approval/cancel/fail URL 만들기 (컨텍스트: /ANIMale)
			// - request.getContextPath() = "/ANIMale"
			String path = request.getContextPath();
			String approvalUrl = BASE_URL + path + "/KakaoPayApprove.do"; //성공 결과를 가지고 결과페이지로 이동
			String cancelUrl = BASE_URL + path + "/KakaoPayCancel.do"; //취소 안내 후 마이페이지로 이동
			String failUrl = BASE_URL + path + "/KakaoPayFail.do"; //실패 결과를 가지고 결과페이지로 이동
	        System.out.println("[카카오페이 READY 로그] approval_url=" + approvalUrl);
	        System.out.println("[카카오페이 READY 로그] cancel_url=" + cancelUrl);
	        System.out.println("[카카오페이 READY 로그] fail_url=" + failUrl);

			// 5) ready 요청 JSON 구성
	        // - 문서 필드 그대로 구성
	        // - total_amount / vat_amount는 문자열로 넣어도 OK (JSON 숫자로 넣어도 됨)
			JsonObject kakaoPay = new JsonObject();
			kakaoPay.addProperty("cid", CID);
			kakaoPay.addProperty("partner_order_id", partnerOrderId);
			kakaoPay.addProperty("partner_user_id", String.valueOf(memberId));
			kakaoPay.addProperty("item_name", "캐시 충전 " + cashCharge + "원");
			kakaoPay.addProperty("quantity", 1);
			kakaoPay.addProperty("total_amount", cashCharge);
			kakaoPay.addProperty("vat_amount", calcVat(cashCharge));
			kakaoPay.addProperty("tax_free_amount", 0);
			kakaoPay.addProperty("approval_url", approvalUrl);
			kakaoPay.addProperty("fail_url", failUrl);
			kakaoPay.addProperty("cancel_url", cancelUrl);

			// 6) 카카오 ready API 호출 - 메서드는 아래 별도로 정의
			String responseJson = postJson(READY_URL, kakaoPay.toString());
			System.out.println("[카카오페이 READY 로그] responseJson=" + responseJson);

			// 7) 응답에서 tid / next_redirect_pc_url 추출
			JsonObject responseObj = JsonParser.parseString(responseJson).getAsJsonObject();
			String tid = responseObj.get("tid").getAsString();
			String nextRedirectPcUrl = responseObj.get("next_redirect_pc_url").getAsString();

			// 8) approve 단계용 세션 저장 (tid 필수)
			session.setAttribute("kakaopay_tid", tid);
			session.setAttribute("kakaopay_partner_order_id", partnerOrderId);
			session.setAttribute("kakaopay_partner_user_id", String.valueOf(memberId));
			session.setAttribute("kakaopay_total_amount", cashCharge);

			// 9) 카카오 결제 대기화면으로 리다이렉트
			forward.setRedirect(true);
			forward.setPath(nextRedirectPcUrl);
			return forward;

		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("payResult", "FAIL");
			request.setAttribute("message", "결제 준비(READY) 실패");
			forward.setRedirect(false);
			forward.setPath("/cashresult.jsp");
			return forward;
		}
	}

	//6) 카카오 ready API 호출 부분 postJson 메서드 정의
	// 카카오 API 호출(POST + JSON)
	// - url       : 호출할 API 주소(Ready URL 등)
	// - jsonBody  : 전송할 JSON 문자열(요청 바디)
	// - return    : 카카오 서버가 내려준 응답 JSON 문자열
	private String postJson(String url, String jsonBody) throws IOException {
	    // Secret Key 설정 검증
	    // 키가 비어있다면 실제 요청을 보내도 인증 실패가 나거나 보안상 위험하므로 미리 차단
		if (SECRET_KEY_DEV == null || SECRET_KEY_DEV.trim().isEmpty()) {
			throw new IllegalStateException("SECRET_KEY_DEV 값을 설정하세요.");
		}
		
	    // 1) HTTP 연결 객체 생성
	    // - new URL(url).openConnection() 으로 서버 연결 준비를 하고
	    // - HttpURLConnection으로 캐스팅해서 HTTP 관련 설정(메서드/헤더/타임아웃)을 한다.
		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
	
		// 2) 요청 기본 설정
		// - Ready API는 POST 요청이므로 POST 지정
		conn.setRequestMethod("POST");
	    // - 요청 바디(JSON)를 서버로 보내려면 OutputStream을 써야 하므로 true
	    // (이걸 안 켜면 conn.getOutputStream() 호출 시 예외가 날 수 있음)
		conn.setDoOutput(true);
		// - 서버 연결 타임아웃: 연결 자체가 10초 넘게 걸리면 실패 처리
		conn.setConnectTimeout(10000);
		// - 응답 읽기 타임아웃: 연결 후 응답을 10초 안에 못 받으면 실패 처리
		conn.setReadTimeout(10000);

	    // 3) 요청 헤더 세팅 (카카오 문서 스펙)
	    // - Authorization: Secret Key 방식 인증
	    // "SECRET_KEY {키값}" 형태로 보내야 카카오가 인증을 통과시킨다.
		conn.setRequestProperty("Authorization", "SECRET_KEY " + SECRET_KEY_DEV);		
	    // - Content-Type: 지금은 JSON 바디로 보내므로 application/json 지정
	    // - UTF-8: 한글 item_name 등 깨짐 방지
		conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");


	    // 4) 요청 바디(JSON) 전송
	    // - getOutputStream()을 호출하는 순간 실제 네트워크 연결/요청 준비가 진행될 수 있고,
	    // - write()로 jsonBody를 UTF-8 바이트로 보내면 서버(카카오)로 요청 바디가 전송된다.
	    // - try-with-resources를 쓰면 os.close()가 자동 호출되어 누수 방지
		try (OutputStream os = conn.getOutputStream()) {
			os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
			// 보통 close() 시 flush 포함되어 처리됨
		}

		// 5) 응답 코드 수신
	    // - getResponseCode() 호출 시점에 요청이 확정적으로 날아가고,
	    //   서버 응답(상태코드)이 도착한다.
		int code = conn.getResponseCode();
		
	    // 6) 응답 바디 스트림 선택
	    // - 2xx면 정상 응답이므로 InputStream(getInputStream)
	    // - 2xx가 아니면 에러 응답이므로 ErrorStream(getErrorStream)
	    //   (에러 바디를 읽어야 실패 원인이 로그에 남음)
	    InputStream is = (code >= 200 && code < 300)
	            ? conn.getInputStream()
	            : conn.getErrorStream();

	    // 7) 응답 바디(JSON 문자열) 읽기
	    // - BufferedReader로 한 줄씩 읽어서 StringBuilder에 누적
	    // - UTF-8로 읽어야 한글/특수문자 깨짐 방지
		StringBuilder sb = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		}
		
	    // 8) 응답 코드가 2xx가 아니면 실패 처리
	    // - 호출부에서는 이 예외를 catch해서 "READY 실패" 화면 처리 가능
	    // - body=... 를 포함시켜두면 카카오가 준 에러 메시지를 같이 확인 가능
		if (code < 200 || code >= 300) {
			throw new IOException("KakaoPay READY 실패: HTTP ["+code+"] / body ["+sb+"]");
		}
		
	    // 9) 성공 시: 응답 JSON 문자열 반환
		return sb.toString();
	}

	// VAT 계산: round(total/11)
	// - 예) 2200원 → 200원
	// - 카카오 요구 파라미터(vat_amount)에 넣을 값 계산용
	private int calcVat(int cashCharge) {
		return (int) Math.round(cashCharge / 11.0);
	}
}