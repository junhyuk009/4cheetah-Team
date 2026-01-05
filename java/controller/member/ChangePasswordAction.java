package controller.member;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import controller.common.Action;
import controller.common.ActionForward;
import model.dao.MemberDAO;
import model.dto.MemberDTO;

public class ChangePasswordAction implements Action {

    // message.jsp로 보내는 공통 헬퍼
    private ActionForward goMessage(ActionForward forward, HttpServletRequest request, String msg, String location) {
        request.setAttribute("msg", msg);
        request.setAttribute("location", location); 
        forward.setRedirect(false);
        forward.setPath("message.jsp");
        return forward;
    }

    @Override
    public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("[로그] ChangePasswordAction execute : start");

        ActionForward forward = new ActionForward();
        MemberDAO dao = new MemberDAO();

        try {
            request.setCharacterEncoding("UTF-8");

            HttpSession session = request.getSession(false);
            if (session == null) {
                return goMessage(forward, request, "로그인이 필요합니다.", "loginPage.do");
            }

            String newPassword = request.getParameter("newPassword");
            if (newPassword == null || newPassword.trim().isEmpty()) {
                return goMessage(forward, request, "새 비밀번호를 입력해주세요.", "changePasswordPage.do");
            }
            newPassword = newPassword.trim();

            /* 1) 로그인 상태 비밀번호 변경 */
            Integer memberId = (Integer) session.getAttribute("memberId");
            if (memberId != null) {

                String currentPassword = request.getParameter("currentPassword");
                if (currentPassword == null || currentPassword.trim().isEmpty()) {
                    return goMessage(forward, request, "현재 비밀번호를 입력해주세요.", "changePasswordPage.do");
                }
                currentPassword = currentPassword.trim();

                // 서버에서도 2차 방어
                if (currentPassword.equals(newPassword)) {
                    return goMessage(forward, request, "현재 비밀번호와 다른 비밀번호를 입력해주세요.", "changePasswordPage.do");
                }

                // 현재 비밀번호 확인 (조건 필수)
                MemberDTO checkDTO = new MemberDTO();
                checkDTO.setCondition("MEMBER_PASSWORD_CHECK");
                checkDTO.setMemberId(memberId);
                checkDTO.setMemberPassword(currentPassword);

                MemberDTO checkResult = dao.selectOne(checkDTO);
                if (checkResult == null) {
                    return goMessage(forward, request, "현재 비밀번호가 일치하지 않습니다.", "changePasswordPage.do");
                }

                // 비밀번호 변경 (조건 필수)
                MemberDTO updateDTO = new MemberDTO();
                updateDTO.setCondition("MEMBER_PASSWORD_UPDATE");
                updateDTO.setMemberId(memberId);
                updateDTO.setMemberPassword(newPassword);

                boolean updated = dao.update(updateDTO);
                if (!updated) {
                    return goMessage(forward, request, "비밀번호 변경에 실패했습니다. 다시 시도해주세요.", "changePasswordPage.do");
                }
                // 성공                
                
                // 관리자가 변경한거면 관리자페이지로, 일반회원이면 마이페이지로 이동
                String memberRole = (String) session.getAttribute("memberRole");
                if ("ADMIN".equals(memberRole)) {
                	return goMessage(forward, request, "비밀번호가 변경되었습니다.", "adminPage.do");
                }                	
                return goMessage(forward, request, "비밀번호가 변경되었습니다.", "myPage.do");
            }

            /* 2) 비밀번호 찾기 (이메일 인증) 후 재설정 */
            Boolean verified = (Boolean) session.getAttribute("findPasswordVerified");
            Integer findPasswordMemberId = (Integer) session.getAttribute("findPasswordMemberId");

            if (verified == null || !verified || findPasswordMemberId == null) {
                return goMessage(forward, request, "비밀번호 재설정 인증이 필요합니다.", "findPasswordPage.do");
            }

            MemberDTO updateDTO = new MemberDTO();
            updateDTO.setCondition("MEMBER_PASSWORD_UPDATE");
            updateDTO.setMemberId(findPasswordMemberId);
            updateDTO.setMemberPassword(newPassword);

            boolean updated = dao.update(updateDTO);
            if (!updated) {
                return goMessage(forward, request, "비밀번호 재설정에 실패했습니다. 다시 시도해주세요.", "findPasswordPage.do");
            }

            // 인증 세션 정리
            session.removeAttribute("findPasswordVerified");
            session.removeAttribute("findPasswordMemberId");

            return goMessage(forward, request, "비밀번호가 재설정되었습니다. 로그인해주세요.", "loginPage.do");

        } catch (Exception e) {
            e.printStackTrace();
            return goMessage(forward, request, "처리 중 오류가 발생했습니다.", "changePasswordPage.do");
        }
    }
}
