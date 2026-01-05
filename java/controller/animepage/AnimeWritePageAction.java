package controller.animepage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import controller.common.Action;
import controller.common.ActionForward;

public class AnimeWritePageAction implements Action {
	@Override
    public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession(false);
		ActionForward forward = new ActionForward();		
		//애니추가는 관리자 전용 기능으로 관리자 권한 우선적으로 확인
		//url에 담아서 보내기 X
		
		if (session == null) {
			System.out.println("[애니 추가 이동 로그] 세션 없음 : 로그인 필요");
			request.setAttribute("msg", "로그인 정보가 없습니다.");
			request.setAttribute("location", "loginPage.do");
			forward.setPath("message.jsp");
			forward.setRedirect(false);			
		    return forward;
		}
		
		//세션에 담긴 PK, ROLE 값 불러와서 다운캐스팅하고 담기
		Integer memberId = (Integer)session.getAttribute("memberId");		
		System.out.println("[애니 추가 이동 로그] 세션 : memberId=[" + memberId + "]");
        String memberRole = (String)session.getAttribute("memberRole");
		System.out.println("[애니 추가 이동 로그] 세션 : memberRole=[" + memberRole + "]");
		
		// 널포인트익셉션 확인 후 관리자 권한 확인
        if (memberId == null || !"ADMIN".equals(memberRole)) {
            request.setAttribute("msg", "관리자만 접근할 수 있습니다.");
            request.setAttribute("location", "mainPage.do");
            forward.setPath("message.jsp");
            forward.setRedirect(false);
            return forward;
        }
        
        //페이지 경로 담아서 전달
        forward.setPath("animewrite.jsp");
        forward.setRedirect(false);
        return forward;
	}
}

