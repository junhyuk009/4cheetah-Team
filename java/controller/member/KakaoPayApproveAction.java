package controller.member;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import controller.common.Action;
import controller.common.ActionForward;
import model.dao.MemberDAO;
import model.dto.MemberDTO;

public class KakaoPayApproveAction implements Action {

	// 카카오페이 결제승인 API
	private static final String APPROVE_URL = "https://open-api.kakaopay.com/online/v1/payment/approve";

	// 테스트 CID(단건 결제)
	private static final String CID = "TC0ONETIME";

	// Secret key(dev) 사용
	private static final String SECRET_KEY_DEV = "*********************"; // 키값 입력 필요

	/*
	 * [KakaoPayApproveAction 흐름]
	 * 1) pg_token 수신
	 * 2) 세션/로그인 사용자 확인
	 * 3) 세션에 저장된 tid/order/user/amount 꺼내기
	 * 4) 중복 승인 방지
	 * 5) approve 요청 JSON 구성
	 * 6) 카카오 approve API 호출
	 * 7) 응답 파싱(금액 등)
	 * 8) 금액 검증(선택)
	 * 9) DB 캐시 충전 처리
	 * 10) 결과 페이지 이동
	 */

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
		ActionForward forward = new ActionForward();
		HttpSession session = request.getSession(false);

		try {
			System.out.println("[카카오페이 APPROVE 로그] 시작");

			// 1) pg_token 수신
			// - 카카오 결제창에서 결제가 완료되면 approval_url로 돌아오는데,
			//   이때 쿼리 파라미터로 pg_token이 전달된다.
			String pgToken = request.getParameter("pg_token");
			if (pgToken == null || pgToken.trim().isEmpty()) {
				System.out.println("[카카오페이 APPROVE 로그] pg_token 없음 : 잘못된 접근/만료");
				request.setAttribute("payResult", "FAIL");
				forward.setRedirect(false);
				forward.setPath("/cashresult.jsp");
				return forward;
			}

			// 2) 세션/로그인 사용자 확인
			if (session == null || session.getAttribute("memberId") == null) {
				System.out.println("[카카오페이 APPROVE 로그] 세션 없음 또는 로그인 정보 없음");
				request.setAttribute("payResult", "FAIL");
				forward.setRedirect(false);
				forward.setPath("/cashresult.jsp");
				return forward;
			}

			// 로그인 사용자 PK (마이페이지 로직처럼 memberId 검사 가능)
			Integer memberId = (Integer) session.getAttribute("memberId");

			// 3) READY에서 세션에 저장해둔 결제 정보 꺼내기
			// - READY 성공 후 저장한 값이 없으면 approve를 진행할 수 없다.
			String tid = (String) session.getAttribute("kakaopay_tid");
			String partnerOrderId = (String) session.getAttribute("kakaopay_partner_order_id");
			String partnerUserId = (String) session.getAttribute("kakaopay_partner_user_id");
			Integer cashCharge = (Integer) session.getAttribute("kakaopay_total_amount");

			if (tid == null || partnerOrderId == null || partnerUserId == null) {
				System.out.println("[카카오페이 APPROVE 로그] 세션 결제정보 누락 (tid/order/user)");
				request.setAttribute("payResult", "FAIL");
				forward.setRedirect(false);
				forward.setPath("/cashresult.jsp");
				return forward;
			}

			// 4) (중요) 중복 승인 방지
			// - approve URL은 새로고침/뒤로가기 등으로 중복 호출될 수 있다.
			// - 최소 방어: 세션에 "처리완료된 주문번호"를 저장해두고, 동일 주문이면 재처리 차단
			// - 정석: DB에 partner_order_id UNIQUE로 저장해서 중복 자체를 막는게 가장 안전
			String processedOrderId = (String) session.getAttribute("kakaopay_processed_order_id");
			// 즉 진행중인 OrderId가 있었다면 재승인을 막는 방식
			if (partnerOrderId.equals(processedOrderId)) {
				System.out.println("[카카오페이 APPROVE 로그] 중복 호출 차단 (이미 처리된 주문) orderId = ["+partnerOrderId+"]");
				forward.setRedirect(true);
				forward.setPath(request.getContextPath() + "/myPage.do");
				return forward;
			}

			// 5) approve 요청 JSON 구성
			// - 카카오 문서 스펙 필드 그대로 구성해야 함
			JsonObject kakaoPayResult = new JsonObject();
			kakaoPayResult.addProperty("cid", CID);
			kakaoPayResult.addProperty("tid", tid);
			kakaoPayResult.addProperty("partner_order_id", partnerOrderId);
			kakaoPayResult.addProperty("partner_user_id", partnerUserId);
			kakaoPayResult.addProperty("pg_token", pgToken);

			// 6) 카카오 approve API 호출 (서버 → 카카오 서버)
			String responseJson = postJson(APPROVE_URL, kakaoPayResult.toString());
			System.out.println("[카카오페이 APPROVE 로그] approve API 호출 : responseJson = ["+responseJson+"]");

			// 7) 응답 파싱
			JsonObject responseObj = JsonParser.parseString(responseJson).getAsJsonObject();

			// 7-1) 승인된 총 금액 추출 (amount.total)
			// - 카카오 응답 구조에 amount 객체가 들어오는 경우가 많음
			Integer approvedTotal = null;
			if (responseObj.has("amount") && responseObj.get("amount").isJsonObject()) {
				JsonObject amountObj = responseObj.getAsJsonObject("amount");
				if (amountObj.has("total")) {
					approvedTotal = amountObj.get("total").getAsInt();
				}
			}

			// 8) 금액 검증 (강제)
			// - cashCharge: READY 때 세션에 저장해둔 "요청 금액"
			// - approvedTotal: 카카오가 최종 승인한 "승인 금액"
			if (cashCharge == null) { //요청 금액이 널이면 실패
				System.out.println("[카카오페이 APPROVE 로그] cashCharge 없음(세션 누락)");
				request.setAttribute("payResult", "FAIL");
				forward.setRedirect(false);
				forward.setPath("/cashresult.jsp");
				return forward;
			}

			if (approvedTotal == null) { //승인 금액이 널이면 실패
				System.out.println("[카카오페이 APPROVE 로그] approvedTotal 없음(amount.total 파싱 실패)");
				request.setAttribute("payResult", "FAIL");
				forward.setRedirect(false);
				forward.setPath("/cashresult.jsp");
				return forward;
			}

			if (!cashCharge.equals(approvedTotal)) { //두 금액이 같지 않아도 실패
				System.out.println("[카카오페이 APPROVE 로그] 금액 불일치 : 요청=["+cashCharge+"], 승인=["+approvedTotal+"]");
				request.setAttribute("payResult", "FAIL");
				forward.setRedirect(false);
				forward.setPath("/cashresult.jsp");
				return forward;
			}

			// 9) 결제 성공 시: DB 캐시 충전 반영
			// - 승인 성공했으니, 회원 캐시를 증가시키고(+approvedTotal),
			// - 중복 방지를 위해 세션에 partnerOrderId값 저장 필요

			// 회원 캐시 UPDATE (+approvedTotal)
			MemberDAO memberDAO = new MemberDAO();
			MemberDTO memberDTO = new MemberDTO();

			memberDTO.setCondition("MEMBER_CASH_PLUS");
			memberDTO.setMemberId(memberId);
			memberDTO.setMemberPayCash(approvedTotal);
			boolean updateCash = memberDAO.update(memberDTO);
			System.out.println("[카카오페이 APPROVE 로그] 충전 승인 금액 : approvedTotal=["+approvedTotal+"]");

			if (!updateCash) {
				System.out.println("[카카오페이 APPROVE 로그] 캐시 충전 DB 업데이트 실패 : memberId=["+memberId+"]");
				request.setAttribute("payResult", "FAIL");
				forward.setRedirect(false);
				forward.setPath("/cashresult.jsp");
				return forward;
			}

			// (추가) 결과 페이지에서 "보유 캐시(totalCash)"를 보여주기 위해 DB 재조회
			// - update()는 성공/실패만 반환하므로, 반영된 최종 캐시 값은 select로 다시 가져오는게 안전
			memberDTO = new MemberDTO();
			memberDTO.setCondition("MEMBER_MYPAGE");
			memberDTO.setMemberId(memberId);
		
			MemberDTO memberData = memberDAO.selectOne(memberDTO);

			// getMemberCash()가 int여도(Integer로 자동 boxing), Integer여도 둘 다 안전하게 처리
			Integer totalCashObj = null;
			if (memberData != null) {
				totalCashObj = memberData.getMemberCash();
			}

			int totalCash = (totalCashObj == null) ? 0 : totalCashObj;
			System.out.println("[카카오페이 APPROVE 로그] 캐시 충전 후 금액 조회 : totalCash=["+totalCash+"]");

			// (추가) cashresult.jsp에서 출력할 데이터 세팅
			// - totalAmount : 이번에 충전한 금액
			// - approvedAt  : 카카오 승인시각(응답에 있을 때만 세팅)
			// - totalCash   : 충전 반영 후 최종 보유 캐시
			request.setAttribute("totalAmount", String.format("%,d", approvedTotal));
			request.setAttribute("totalCash", String.format("%,d", totalCash));

			// 카카오 응답에 approved_at이 존재하면 결과 페이지에 표시
			if (responseObj.has("approved_at") && !responseObj.get("approved_at").isJsonNull()) {
				request.setAttribute("approvedAt", responseObj.get("approved_at").getAsString());
			}

			// 10) 성공 시 세션에 셋 어트리뷰트 (partnerOrderId)
			// 처리 완료 표시(중복 승인 방지용)
			session.setAttribute("kakaopay_processed_order_id", partnerOrderId);

			// 결과 페이지로 성공 전달
			request.setAttribute("payResult", "SUCCESS");
			forward.setRedirect(false);
			forward.setPath("/cashresult.jsp");
			return forward;

		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("payResult", "FAIL");
			forward.setRedirect(false);
			forward.setPath("/cashresult.jsp");
			return forward;
		}
	}

	// 카카오 API 호출(POST + JSON) - READY에서 쓰던 패턴 그대로
	private String postJson(String url, String jsonBody) throws IOException {

		// Secret Key 설정 검증
		if (SECRET_KEY_DEV == null || SECRET_KEY_DEV.trim().isEmpty()) {
			throw new IllegalStateException("SECRET_KEY_DEV 값을 설정하세요.");
		}

		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setConnectTimeout(10000);
		conn.setReadTimeout(10000);

		// 문서 스펙 헤더
		conn.setRequestProperty("Authorization", "SECRET_KEY " + SECRET_KEY_DEV);
		conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

		// 요청 바디 전송
		try (OutputStream os = conn.getOutputStream()) {
			os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
		}

		// 응답 코드 + 바디 읽기
		int code = conn.getResponseCode();
		InputStream is = (code >= 200 && code < 300) ? conn.getInputStream() : conn.getErrorStream();

		StringBuilder sb = new StringBuilder();
		if (is != null) {
			try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
				String line;
				while ((line = br.readLine()) != null) sb.append(line);
			}
		}

		if (code < 200 || code >= 300) {
			throw new IOException("KakaoPay APPROVE 실패: HTTP ["+code+"] / body ["+sb+"]");
		}
		return sb.toString();
	}
}