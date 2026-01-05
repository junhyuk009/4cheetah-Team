package controller.member;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import controller.common.Action;
import controller.common.ActionForward;
import model.dao.MemberDAO;
import model.dto.MemberDTO;

/**
 * JoinAction
 *
 * 역할
 * - 회원가입 처리
 * - JSP에서 전달된 회원 정보 수신
 * - 이메일 인증 여부 최종 검증
 * - 이메일 중복 최종 검증 (DB 기준)
 * - DTO 세팅 후 DAO insert 호출
 * - 성공 시 로그인 페이지 이동
 */
public class JoinAction implements Action {

    @Override
    public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("[로그] JoinAction execute : start");

        /* 1. 파라미터 수신 */
        String memberName = request.getParameter("memberName");
        String memberNickname = request.getParameter("memberNickname");
        String memberEmail = request.getParameter("memberEmail");
        String memberPassword = request.getParameter("memberPassword");

        System.out.println("[로그] JoinAction : memberName=" + memberName);
        System.out.println("[로그] JoinAction : memberNickname=" + memberNickname);
        System.out.println("[로그] JoinAction : memberEmail=" + memberEmail);

        /* 2. 필수 값 검증 */
        if (memberName == null || memberNickname == null
                || memberEmail == null || memberPassword == null
                || memberName.trim().isEmpty()
                || memberNickname.trim().isEmpty()
                || memberEmail.trim().isEmpty()
                || memberPassword.trim().isEmpty()) {

            System.out.println("[로그] JoinAction : invalid parameter");

            ActionForward forward = new ActionForward();
            forward.setRedirect(true);
            forward.setPath("joinPage.do");
            return forward;
        }

        /* 3. 이메일 인증 여부 검증 */
        HttpSession session = request.getSession(false);

        System.out.println("[로그] JoinAction Session ID = "
                + (session != null ? session.getId() : "null"));

        Boolean joinEmailVerified = (session != null)
                ? (Boolean) session.getAttribute("joinEmailVerified")
                : null;

        String verifiedEmail = (session != null)
                ? (String) session.getAttribute("joinEmail")
                : null;

        System.out.println("[로그] JoinAction : joinEmailVerified = " + joinEmailVerified);
        System.out.println("[로그] JoinAction : verifiedEmail = " + verifiedEmail);

        if (joinEmailVerified == null || !joinEmailVerified) {
            System.out.println("[로그] JoinAction : email not verified");

            ActionForward forward = new ActionForward();
            forward.setRedirect(true);
            forward.setPath("joinPage.do");
            return forward;
        }

        if (!memberEmail.trim().equals(verifiedEmail)) {
            System.out.println("[로그] JoinAction : email mismatch");

            ActionForward forward = new ActionForward();
            forward.setRedirect(true);
            forward.setPath("joinPage.do");
            return forward;
        }

        /* 4. 이메일 중복 최종 검증 (DB 기준) */
        MemberDAO dao = new MemberDAO();

        MemberDTO emailCheckDTO = new MemberDTO();
        emailCheckDTO.setCondition("MEMBER_EMAIL_CHECK");
        emailCheckDTO.setMemberEmail(memberEmail.trim());

        MemberDTO existEmail = dao.selectOne(emailCheckDTO);

        if (existEmail != null) {
            System.out.println("[로그] JoinAction : email already exists in DB");

            if (session != null) {
                session.setAttribute("joinError", "EMAIL_DUPLICATE");
            }

            ActionForward forward = new ActionForward();
            forward.setRedirect(true);
            forward.setPath("joinPage.do");
            return forward;
        }

        /* 5. DTO 세팅 */
        MemberDTO dto = new MemberDTO();
        dto.setMemberName(memberName.trim());
        dto.setMemberNickname(memberNickname.trim());
        dto.setMemberEmail(memberEmail.trim());
        dto.setMemberPassword(memberPassword.trim());

        // 기본값 세팅
        dto.setMemberProfileImage(null);
        dto.setMemberPhoneNumber(null);
        dto.setCondition("MEMBER_JOIN");

        System.out.println("[로그] JoinAction DTO : " + dto);

        /* 6. DAO insert*/
        boolean result = dao.insert(dto);

        System.out.println("[로그] JoinAction : insert result=" + result);

        /* 7. 회원가입 성공 시 인증 세션 정리*/
        if (result && session != null) {
            session.removeAttribute("joinEmailVerified");
            session.removeAttribute("joinEmail");
            session.removeAttribute("joinEmailCode");
            session.removeAttribute("joinEmailExpireAt");

            session.setAttribute("joinSuccess", true);
        } else if (session != null) {
            session.setAttribute("joinError", "JOIN_FAIL");
        }

        /* 8. 이동 처리 */
        ActionForward forward = new ActionForward();
        forward.setRedirect(true);

        if (result) {
            forward.setPath("loginPage.do?joinSuccess=true");
        } else {
            forward.setPath("joinPage.do");
        }

        return forward;
    }
}
