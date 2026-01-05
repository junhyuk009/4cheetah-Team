package controller.memberpage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import controller.common.Action;
import controller.common.ActionForward;
import model.dao.MemberDAO;
import model.dto.MemberDTO;

public class AdminPageAction implements Action {	
	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession(false);
		ActionForward forward = new ActionForward();
		MemberDTO memberDTO = new MemberDTO();
		MemberDAO memberDAO = new MemberDAO();
		
		//1. 세션 유무 존재 확인 > 없으면 로그인 필요
		//2. 세션에서 id/role 꺼내서 유효성 검사
		//   - 절차에 따른 로그 확인을 위해 id 검사 후 role 검사 진행 
		//3. 해당 ID/ROLE로 셀렉트원 DB 조회 후
		//4. 이상 없으면 회원데이터 모두 담아서 adminpage.jsp로 이동
		
		if (session == null) {
			System.out.println("[관리자페이지 이동 로그] 세션 없음 : 로그인 필요");
			request.setAttribute("msg", "로그인 정보가 없습니다.");
			request.setAttribute("location", "loginPage.do");
			forward.setPath("message.jsp");
			forward.setRedirect(false);			
		    return forward;
		}
		
		//세션에 담긴 PK, ROLE 값 불러와서 다운캐스팅하고 담기
		Integer memberId = (Integer)session.getAttribute("memberId");
		System.out.println("[관리자페이지 이동 로그] 로그인 유저 ID : memberId=[" + memberId + "]");
		
		String memberRole = (String)session.getAttribute("memberRole");
		System.out.println("[관리자페이지 이동 로그] 로그인 유저 ROLE : memberRole ["+memberRole+"]");
		
		//유효성검사 - 세션에 id없으면 로그인해라
		if (memberId == null) {
			System.out.println("[관리자페이지 이동 로그] memberId 없음 : 로그인 필요");
			request.setAttribute("msg", "로그인 정보가 없습니다.");
			request.setAttribute("location", "loginPage.do");
			forward.setPath("message.jsp");
			forward.setRedirect(false);			
		    return forward;
		}		
		if (!"ADMIN".equals(memberRole)) {
			System.out.println("[관리자페이지 이동 로그] memberRole 다름 : 관리자 아님");
			request.setAttribute("msg", "접근 권한이 없습니다.");
			request.setAttribute("location", "mainPage.do");
			forward.setPath("message.jsp");
			forward.setRedirect(false);			
		    return forward;
		}	
		
		//DB조회할 멤버DTO 세팅 - 컨디션, Id, Role 담기
		memberDTO.setCondition("MEMBER_ADMINPAGE");		
		memberDTO.setMemberId(memberId);
		memberDTO.setMemberRole(memberRole);		
		MemberDTO memberData = memberDAO.selectOne(memberDTO);
		
		if (memberData == null) {//정보 조회 실패 - 잘못된 접근
			System.out.println("[관리자페이지 이동 로그] 조회 실패 : memberData 없음");
            request.setAttribute("msg", "회원 정보를 불러올 수 없습니다.");
            request.setAttribute("location", "mainPage.do");
            forward.setPath("message.jsp");
            forward.setRedirect(false);
            return forward;
        }
		
		System.out.println("[관리자페이지 이동 로그] 조회 완료 : memberData = ["+memberData+"]");
		request.setAttribute("memberData", memberData);				
		forward.setPath("adminpage.jsp");
		forward.setRedirect(false);		
		return forward;
	}
}
