package controller.memberpage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import controller.common.Action;
import controller.common.ActionForward;
import model.dao.MemberDAO;
import model.dto.MemberDTO;


public class MyPageAction implements Action {	
	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {	
		HttpSession session = request.getSession(false);
		ActionForward forward = new ActionForward();
		MemberDTO memberDTO = new MemberDTO();
		MemberDAO memberDAO = new MemberDAO();
		
		//1. 세션에서 id꺼내서 유효성 검사
		//2. 해당 ID로 셀렉트원 DB 조회 후
		//3. 이상 없으면 회원데이터 모두 담아서 mypage.jsp로 이동
		
		if (session == null) {
			System.out.println("[마이페이지 이동 로그] 세션 없음 : 로그인 필요");
			request.setAttribute("msg", "로그인 정보가 없습니다.");
			request.setAttribute("location", "loginPage.do");
			forward.setPath("message.jsp");
			forward.setRedirect(false);			
		    return forward;
		}
		
		//세션에 담긴 PK값 불러와서 정수형 변환하고 담기
		Integer memberId = (Integer)session.getAttribute("memberId");
		System.out.println("[마이페이지 이동 로그] 세션 확인 : memberId=[" + memberId + "]");
		
		//유효성검사 - 세션에 id없으면 로그인해라
		if (memberId == null) {
			System.out.println("[마이페이지 이동 로그] memberId 없음 : 로그인 필요");
			request.setAttribute("msg", "로그인 정보가 없습니다.");
			request.setAttribute("location", "loginPage.do");
			forward.setPath("message.jsp");
			forward.setRedirect(false);			
		    return forward;
		}		
		
		//DB조회할 멤버DTO 세팅 - 컨디션, Id 담기
		memberDTO.setCondition("MEMBER_MYPAGE");		
		memberDTO.setMemberId(memberId);
		MemberDTO memberData = memberDAO.selectOne(memberDTO);
		
		if (memberData == null) {//정보 조회 실패 - 잘못된 접근
			System.out.println("[마이페이지 이동 로그] 조회 실패 : memberData 없음");
            request.setAttribute("msg", "회원 정보를 불러올 수 없습니다.");
            request.setAttribute("location", "mainPage.do");
            forward.setPath("message.jsp");
            forward.setRedirect(false);
            return forward;
        }
		
		System.out.println("[마이페이지 이동 로그] 조회 완료 : memberData = ["+memberData+"]");
		request.setAttribute("memberData", memberData);				
		forward.setPath("mypage.jsp");
		forward.setRedirect(false);		
		return forward;
	}
}

