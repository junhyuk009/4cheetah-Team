package controller.newspage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import controller.common.Action;
import controller.common.ActionForward;
import model.dao.NewsDAO;
import model.dto.NewsDTO;

public class NewsEditPageAction implements Action {
	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
		ActionForward forward = new ActionForward();
		NewsDAO newsDAO = new NewsDAO();
		NewsDTO newsDTO = new NewsDTO();		
		
		//1. 로그인 여부 확인 - 사용자가 있는가
		//2. 뉴스 PK 파라미터 유효성 검사 - 요청 자체가 유효한가
		//3. 관리자 권한 확인 - 접근 자체가 가능한가(관리자는 데이터 호출보다 중요)
		//4. 해당 PK로 뉴스 selectOne
		//5. 데이터 유효성 검사 - 실제 수정 대상이 존재하는가
		//6. 데이터 담아서 수정 페이지로 이동
		
		//1. 로그인 체크
		HttpSession session = request.getSession(false);
		//이미 세션이 있으면 가져오고, 없으면 새로 만들지 마라		
		if (session == null || session.getAttribute("memberId") == null) {
			System.out.println("[뉴스 수정 이동 로그] 세션 없음 또는 memberId 유효하지 않음");
			request.setAttribute("msg", "잘못된 접근입니다.");
			request.setAttribute("location", "mainPage.do");
			forward.setPath("message.jsp");
			forward.setRedirect(false);
			return forward;
		}
		
		//검사 통과 시 role담기
		String memberRole = (String)session.getAttribute("memberRole");
		System.out.println("[뉴스 수정 이동 로그] 로그인 유저 ROLE : memberRole ["+memberRole+"]");
		
		//2. 뉴스 PK(newsId) 파라미터 검증
		String newsIdCheck = request.getParameter("newsId");
		System.out.println("[뉴스 수정 이동 로그] 뉴스 PK 검사 : newsIdCheck ["+newsIdCheck+"]");
		if (newsIdCheck == null) {
			request.setAttribute("msg", "잘못된 접근입니다.");
			request.setAttribute("location", "mainPage.do");
			forward.setPath("message.jsp");
			forward.setRedirect(false);
			return forward;
		}

		int newsId;
		try {
			newsId = Integer.parseInt(newsIdCheck);
		} catch (NumberFormatException e) {
			System.out.println("[뉴스 수정 이동 로그] 뉴스 PK 검사 : newsId 정수 변환 오류");
			request.setAttribute("msg", "뉴스 번호가 올바르지 않습니다.");
			request.setAttribute("location", "newsList.do");
			forward.setPath("message.jsp");
			forward.setRedirect(false);
			return forward;
		}		
		if (newsId <= 0) { // 뉴스id값이 0이거나 음수면 -> 메인페이지로 이동
			System.out.println("[뉴스 수정 이동 로그] 뉴스 PK 검사 : newsId 0이하 오류");
			request.setAttribute("msg", "잘못된 뉴스 접근입니다...");
			request.setAttribute("location", "mainPage.do");
			forward.setPath("message.jsp");
			forward.setRedirect(false);			
		    return forward;
		}			

		// 3. 관리자 권한 확인
		if (!"ADMIN".equals(memberRole)) {
			System.out.println("[뉴스 수정 이동 로그] 관리자 권한 없음 : ["+memberRole+"]");
			request.setAttribute("msg", "관리자만 접근할 수 있습니다.");
			request.setAttribute("location", "mainPage.do");
			forward.setPath("message.jsp");
			forward.setRedirect(false);
			return forward;
		}
		
		newsDTO.setNewsId(newsId);
		newsDTO.setCondition("NEWS_DETAIL");
		NewsDTO newsData = newsDAO.selectOne(newsDTO);
		System.out.println("[뉴스 수정 이동 로그] 뉴스 조회 완료 : newsData = ["+newsData+"]");
		
		// 5. 데이터 유효성 검사
		if (newsData == null) {
			request.setAttribute("msg", "존재하지 않는 뉴스입니다.");
			request.setAttribute("location", "newsList.do");
			forward.setPath("message.jsp");
			forward.setRedirect(false);
			return forward;
		}

		// 6. 수정 페이지로 이동 (데이터 세팅)
		request.setAttribute("type", "NEWS");
		// edit은 뉴스와 게시글 공용 JSP이므로 분기용 데이터 넘겨줘야함.
		request.setAttribute("newsData", newsData);

		forward.setPath("edit.jsp");
		forward.setRedirect(false);
		return forward;
	}
}
