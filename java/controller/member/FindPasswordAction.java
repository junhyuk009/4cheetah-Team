package controller.member;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import controller.common.Action;
import controller.common.ActionForward;
import model.dao.MemberDAO;
import model.dto.MemberDTO;

public class FindPasswordAction implements Action {

    // 프론트 정규식과 동일하게 맞춤
    private static final String PASSWORD_REGEX =
            "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+=-]).{8,16}$";

    @Override
    public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {

        ActionForward forward = new ActionForward();
        forward.setRedirect(false);     // message.jsp forward
        forward.setPath("message.jsp");

        // 1) 세션 확인
        HttpSession session = request.getSession(false);
        if (session == null) {
            request.setAttribute("msg", "세션이 만료되었습니다. 다시 인증번호를 발송해주세요.");
            request.setAttribute("location", "findPasswordPage.do");
            return forward;
        }

        // 2) 인증 완료 여부
        Boolean verified = (Boolean) session.getAttribute("findPasswordVerified");
        if (verified == null || !verified) {
            request.setAttribute("msg", "이메일 인증이 완료되지 않았습니다.");
            request.setAttribute("location", "findPasswordPage.do");
            return forward;
        }

        // 3) 만료 시간 체크(서버 기준)
        Long expireAt = (Long) session.getAttribute("findPasswordExpireAt");
        if (expireAt == null || System.currentTimeMillis() > expireAt) {
            session.setAttribute("findPasswordVerified", false);
            request.setAttribute("msg", "인증 시간이 만료되었습니다. 재요청 후 다시 진행해주세요.");
            request.setAttribute("location", "findPasswordPage.do");
            return forward;
        }

        // 4) 대상 회원 ID 확보(서블릿에서 세션에 저장해둔 값)
        Integer memberId = (Integer) session.getAttribute("findPasswordMemberId");
        if (memberId == null) {
            request.setAttribute("msg", "대상 계정 정보가 없습니다. 다시 진행해주세요.");
            request.setAttribute("location", "findPasswordPage.do");
            return forward;
        }

        // 5) 새 비밀번호 파라미터
        String newPassword = request.getParameter("memberPassword");
        if (newPassword == null || newPassword.trim().isEmpty()) {
            request.setAttribute("msg", "새 비밀번호를 입력해주세요.");
            request.setAttribute("location", "findPasswordPage.do");
            return forward;
        }
        newPassword = newPassword.trim();

        // 6) 서버에서도 비밀번호 정책 검증
        if (!newPassword.matches(PASSWORD_REGEX)) {
            request.setAttribute("msg", "비밀번호 형식이 올바르지 않습니다. (8~16자, 영문/숫자/특수문자 포함)");
            request.setAttribute("location", "findPasswordPage.do");
            return forward;
        }

        // 7) DB 업데이트 (기존 DAO 컨디션 재사용)
        MemberDAO memberDAO = new MemberDAO();
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setMemberId(memberId);
        memberDTO.setMemberPassword(newPassword);
        memberDTO.setCondition("MEMBER_PASSWORD_UPDATE");

        boolean updated = memberDAO.update(memberDTO);

        if (!updated) {
            request.setAttribute("msg", "비밀번호 변경에 실패했습니다. 다시 시도해주세요.");
            request.setAttribute("location", "findPasswordPage.do");
            return forward;
        }

        // 8) 성공 → 인증 세션 정리(재사용/재시도 방지)
        session.removeAttribute("findPasswordMemberId");
        session.removeAttribute("findPasswordEmail");
        session.removeAttribute("findPasswordCode");
        session.removeAttribute("findPasswordExpireAt");
        session.removeAttribute("findPasswordVerified");

        request.setAttribute("msg", "비밀번호가 변경되었습니다. 로그인 해주세요.");
        request.setAttribute("location", "loginPage.do");
        return forward;
    }
}
