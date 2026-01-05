package controller.newspage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import controller.common.Action;
import controller.common.ActionForward;

public class NewsWritePageAction implements Action {
	@Override
    public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession(false);
		ActionForward forward = new ActionForward();		
		// 관리자 권한 우선적으로 확인
		// 타입 뉴스 전달
		// 뉴스는 관리자 전용 기능이므로 셋어트리뷰트로 담아서 보낸다.
		// url에 담아서 보내기 X
		
		if (session == null) {
			System.out.println("[뉴스 작성 이동 로그] 세션 없음 : 로그인 필요");
			request.setAttribute("msg", "로그인 정보가 없습니다.");
			request.setAttribute("location", "loginPage.do");
			forward.setPath("message.jsp");
			forward.setRedirect(false);			
		    return forward;
		}
		
		// 세션에 담긴 PK, ROLE 값 불러와서 다운캐스팅하고 담기
		Integer memberId = (Integer)session.getAttribute("memberId");
		System.out.println("[뉴스 작성 이동 로그] 세션 : memberId=[" + memberId + "]");
        String memberRole = (String)session.getAttribute("memberRole");
		System.out.println("[뉴스 작성 이동 로그] 세션 : memberRole=[" + memberRole + "]");
		
		// 널포인트익셉션 확인 후 관리자 권한 확인
        if (memberId == null || !"ADMIN".equals(memberRole)) {
            request.setAttribute("msg", "관리자만 접근할 수 있습니다.");
            request.setAttribute("location", "mainPage.do");
            forward.setPath("message.jsp");
            forward.setRedirect(false);
            return forward;
        }
        
        // 타입 뉴스와 페이지 경로 담아서 전달
        // 관리자 전용 기능이므로 게시판 페이지 이동과 달리 리퀘스트에 담아서 전달
        request.setAttribute("type", "NEWS");        
        forward.setPath("write.jsp");
        forward.setRedirect(false);
        return forward;
	}
}
