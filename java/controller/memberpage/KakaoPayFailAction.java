package controller.memberpage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import controller.common.Action;
import controller.common.ActionForward;

public class KakaoPayFailAction implements Action {

    @Override
    public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
        ActionForward forward = new ActionForward();

        System.out.println("[카카오페이 FAIL 로그] 결제 실패 : 완료페이지 이동");

        // cashresult.jsp에서 실패 판단/출력할 데이터
        request.setAttribute("payResult", "FAIL"); // 성공/실패 판별용
        forward.setRedirect(false); // forward로 가야 request 값이 유지됨 = 성공 실패 유무를 리다이렉트로 초기화 X
        forward.setPath("cashresult.jsp"); // 성공/실패는 결제결과 페이지에서 출력
        return forward;
    }
}