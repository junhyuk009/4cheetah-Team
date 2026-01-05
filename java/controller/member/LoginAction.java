package controller.member;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import controller.common.Action;
import controller.common.ActionForward;
import model.dao.MemberDAO;
import model.dto.MemberDTO;

public class LoginAction implements Action {

   @Override
   public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {

      ActionForward forward = new ActionForward();

      /* 0) 이미 로그인 되어 있으면 안내 후 이동
         - ADMIN  -> adminPage.do
         - USER   -> myPage.do */
      HttpSession session = request.getSession(false); // 없으면 null (세션 생성 X)
      if (session != null && session.getAttribute("memberId") != null) {

         String memberRole = (String) session.getAttribute("memberRole");
         String goPage = "ADMIN".equals(memberRole) ? "adminPage.do" : "myPage.do";

         request.setAttribute("msg", "이미 로그인되어 있습니다.");
         request.setAttribute("location", goPage);
         forward.setPath("message.jsp");
         forward.setRedirect(false);
         return forward;
      }

      // 여기부터는 "로그인 시도"이므로 세션 생성 OK
      session = request.getSession(); // 없으면 생성

      MemberDAO memberDAO = new MemberDAO();
      MemberDTO memberDTO = new MemberDTO();

      // V → 받은 id/pw
      memberDTO.setCondition("MEMBER_LOGIN");
      memberDTO.setMemberName(request.getParameter("memberName"));
      memberDTO.setMemberPassword(request.getParameter("memberPassword"));
      System.out.println("[로그인 액션 로그] DAO 호출 전 : memberDTO = [" + memberDTO + "]");

      MemberDTO data = memberDAO.selectOne(memberDTO);
      System.out.println("[로그인 액션 로그] DAO 호출 후 : data = [" + data + "]");

      if (data != null) { // 로그인 성공
         System.out.println("[로그인 액션 로그] 로그인 성공 - ID : [" + data.getMemberName() + "]");

         // 탈퇴 회원 차단
         if ("WITHDRAWN".equals(data.getMemberRole())) {
            request.setAttribute("msg", "탈퇴한 계정입니다.");
            request.setAttribute("location", "mainPage.do");
            forward.setPath("message.jsp");
            forward.setRedirect(false);
            return forward; // 여기서 끝
         }

         // 세션 저장
         session.setAttribute("memberId", data.getMemberId());
         session.setAttribute("memberName", data.getMemberName());
         session.setAttribute("memberNickName", data.getMemberNickname());
         session.setAttribute("memberRole", data.getMemberRole());
         session.setAttribute("memberProfileImage", data.getMemberProfileImage());
         session.setAttribute("memberEmail", data.getMemberEmail());
         session.setAttribute("memberPhoneNumber", data.getMemberPhoneNumber());

         System.out.println("[로그인 액션 로그] 세션 확인 - memberName : ["
               + String.valueOf(session.getAttribute("memberName")) + "]");

         // 자동 로그인 체크되었으면 쿠키 생성
         // HTML에서 <input type="checkbox" name="autoLogin" value="Y">
         String autoLogin = request.getParameter("autoLogin");
         if ("Y".equals(autoLogin)) {
            Cookie cookie = new Cookie("autoLogin", data.getMemberName());
            cookie.setMaxAge(60 * 60 * 24 * 7); // 7일
            cookie.setPath("/"); // 전체 경로에서 유효하도록
            cookie.setHttpOnly(true); // ⭐ HTTP Only
            response.addCookie(cookie);
            System.out.println("자동 로그인 쿠키 생성됨 : " + cookie.getValue());
         }

         // 관리자 권한 분기
         if ("ADMIN".equals(data.getMemberRole())) {
            request.setAttribute("msg", "로그인 성공!");
            request.setAttribute("location", "adminPage.do");
            forward.setPath("message.jsp");
            forward.setRedirect(false);
         } else {
            request.setAttribute("msg", "로그인 성공!");
            request.setAttribute("location", "mainPage.do");
            forward.setPath("message.jsp");
            forward.setRedirect(false);
         }

      } else { // 로그인 실패
         System.out.println("[로그인 액션 로그] 로그인 실패");
         request.setAttribute("msg", "로그인 실패...");
         request.setAttribute("location", "loginPage.do");
         forward.setPath("message.jsp");
         forward.setRedirect(false);
      }

      return forward;
   }
}
