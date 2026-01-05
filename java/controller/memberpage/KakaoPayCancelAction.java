package controller.memberpage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import controller.common.Action;
import controller.common.ActionForward;

public class KakaoPayCancelAction implements Action {

    @Override
    public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
        ActionForward forward = new ActionForward();

        System.out.println("[카카오페이 CANCEL 로그] 결제 취소 : 마이페이지 이동");  

        // message.jsp 재활용해서 마이페이지로 이동
        request.setAttribute("msg", "결제가 취소되었습니다.");
        request.setAttribute("location", "myPage.do"); // message.jsp에서 이 값으로 이동
        forward.setPath("message.jsp");
        forward.setRedirect(false);
        return forward;
    }
}