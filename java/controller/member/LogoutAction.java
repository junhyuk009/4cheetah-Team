package controller.member;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import controller.common.Action;
import controller.common.ActionForward;

public class LogoutAction implements Action {

    @Override
    public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("[로그] LogoutAction execute : start");

        // 1) 자동로그인 쿠키 삭제 (세션보다 먼저/상관없이 해도 됨)
        Cookie killAuto = new Cookie("autoLogin", "");
        killAuto.setMaxAge(0);     // 삭제
        killAuto.setPath("/");     // 로그인에서 "/"로 만들었으니 반드시 "/"로 삭제
        killAuto.setHttpOnly(true);// (옵션) 만들 때 넣었으면 똑같이 넣어도 안전
        response.addCookie(killAuto);
        System.out.println("[로그] LogoutAction : autoLogin cookie delete header sent");

        // 2) 세션 종료
        HttpSession session = request.getSession(false);
        if (session != null) {
            Integer memberId = (Integer) session.getAttribute("memberId");
            String memberRole = (String) session.getAttribute("memberRole");

            System.out.println("[로그] LogoutAction : memberId=" + memberId);
            System.out.println("[로그] LogoutAction : memberRole=" + memberRole);

            session.invalidate();
            System.out.println("[로그] LogoutAction : session invalidated");
        }

        // 3) 메인으로 리다이렉트
        ActionForward forward = new ActionForward();
        request.setAttribute("msg", "로그아웃 성공!");
        request.setAttribute("location", "mainPage.do");
        forward.setPath("message.jsp");
        forward.setRedirect(false);
        return forward;
    }
}
