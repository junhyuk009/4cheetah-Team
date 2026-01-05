package controller.member;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import controller.common.Action;
import controller.common.ActionForward;
import model.dao.MemberDAO;
import model.dto.MemberDTO;

/**
 * WithdrawAction
 *
 * 역할
 * - 회원 탈퇴 요청 처리
 * - 실제 데이터 삭제가 아닌 회원 상태 변경 방식으로 탈퇴 처리
 *
 * - U : MemberDAO.update()
 */
public class WithdrawAction implements Action {

    @Override
    public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("[로그] WithdrawAction execute : start");
        ActionForward forward = new ActionForward();
        
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            request.setAttribute("msg", "잘못된 접근입니다.");
            request.setAttribute("location", "mainPage.do");
            forward.setPath("message.jsp");
            forward.setRedirect(false);
            return forward;
        }

        // 1) 세션 확인 (탈퇴는 로그인 상태에서만)
        HttpSession session = request.getSession(false);
        if (session == null) {
            System.out.println("[로그] WithdrawAction : session null");

            request.setAttribute("msg", "로그인 정보가 없습니다.");
            request.setAttribute("location", "loginPage.do");
            forward.setPath("message.jsp");
            forward.setRedirect(false);
            return forward;
        }

        // 2) 로그인 사용자 식별
        Integer memberId = (Integer) session.getAttribute("memberId");
        System.out.println("[로그] WithdrawAction : session memberId=" + memberId);

        if (memberId == null) {
            request.setAttribute("msg", "로그인 정보가 없습니다.");
            request.setAttribute("location", "loginPage.do");
            forward.setPath("message.jsp");
            forward.setRedirect(false);
            return forward;
        }

        // 3) DAO 호출 (상태 변경)
        MemberDTO dto = new MemberDTO();
        dto.setMemberId(memberId);
        dto.setCondition("MEMBER_WITHDRAWN");

        MemberDAO dao = new MemberDAO();
        boolean result = dao.update(dto);

        System.out.println("[로그] WithdrawAction : withdraw update result=" + result);

        // 4) 성공/실패 처리 + 알림 메시지 세팅
        if (result) {
            // 성공이면 세션 끊기
            session.invalidate();
            System.out.println("[로그] WithdrawAction : session invalidated");

            request.setAttribute("msg", "회원 탈퇴가 완료되었습니다. 이용해주셔서 감사합니다.");
            request.setAttribute("location", "mainPage.do");
        } else {
            // 실패면 세션은 유지 (로그인 상태 그대로)
            request.setAttribute("msg", "회원 탈퇴에 실패했습니다. 잠시 후 다시 시도해주세요.");
            request.setAttribute("location", "myPage.do"); // ✅ 너 프로젝트 마이페이지 경로에 맞게 수정
        }

        // 5) message.jsp로 forward
        forward.setPath("message.jsp");
        forward.setRedirect(false);
        return forward;
    }
}
