package controller.newspage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import controller.common.Action;
import controller.common.ActionForward;
import model.dto.NewsDTO;
import model.dao.NewsDAO;

public class NewsDetailAction implements Action {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
		request.setAttribute("activeMenu", "NEWS"); //메뉴 활성화용
		ActionForward forward = new ActionForward();
		NewsDAO newsDAO = new NewsDAO();
		NewsDTO newsDTO = new NewsDTO();
		/*
		1. newsId 파라미터 검증
		2. 뉴스 selectOne
		3. 목록 복귀용 파라미터(page/condition/keyword) 전달
		4. newsdetail.jsp 이동
		 */

		// 1) id 파라미터 유효성 검사
		String newsIdCheck = request.getParameter("newsId");

		if (newsIdCheck == null || newsIdCheck.isEmpty()) {
			System.out.println("[뉴스 상세보기 로그] 조회 실패 : newsId 유효하지 않음");
			request.setAttribute("msg", "잘못된 뉴스 선택입니다...");
			request.setAttribute("location", "mainPage.do");
			forward.setPath("message.jsp");
			forward.setRedirect(false);
			return forward;
		}

		int newsId;
		try {
			newsId = Integer.parseInt(newsIdCheck);
		} catch (NumberFormatException e) {
			System.out.println("[뉴스 상세보기 로그] 조회 실패 : newsId 정수 변환 오류");
			request.setAttribute("msg", "잘못된 뉴스 선택입니다...");
			request.setAttribute("location", "mainPage.do");
			forward.setPath("message.jsp");
			forward.setRedirect(false);
			return forward;
		}

		if (newsId <= 0) {
			System.out.println("[뉴스 상세보기 로그] 조회 실패 : newsId 0이하 오류");
			request.setAttribute("msg", "잘못된 뉴스 선택입니다...");
			request.setAttribute("location", "mainPage.do");
			forward.setPath("message.jsp");
			forward.setRedirect(false);
			return forward;
		}

		// 2) 뉴스 selectOne
		newsDTO.setNewsId(newsId);
		newsDTO.setCondition("NEWS_DETAIL");
		NewsDTO newsData = newsDAO.selectOne(newsDTO);

		if (newsData == null) {
			System.out.println("[뉴스 상세보기 로그] 조회 실패 : 해당 뉴스는 없는 뉴스");
			request.setAttribute("msg", "잘못된 뉴스 선택입니다...");
			request.setAttribute("location", "mainPage.do");
			forward.setPath("message.jsp");
			forward.setRedirect(false);
			return forward;
		}
		System.out.println("[뉴스 상세보기 로그] 조회 완료 : newsData = [" + newsData + "]");

		// 3) 결과 담아서 페이지 전달
		request.setAttribute("newsData", newsData);

		// 목록 복귀용 파라미터 전달
		// - news.jsp에서 detail 링크에 page/condition/keyword를 실어주면
		//   상세에서 "목록으로" 눌렀을 때 그대로 되돌아갈 수 있음
		int page = 1;
		try {
			String pageParam = request.getParameter("page");
			if (pageParam != null && !pageParam.trim().isEmpty()) {
				page = Integer.parseInt(pageParam.trim());
			}
		} catch (NumberFormatException e) {
			page = 1;
		}
		if (page < 1) page = 1;

		String condition = request.getParameter("condition");
		String keyword = request.getParameter("keyword");
		if (keyword != null) {
			keyword = keyword.trim();
			if (keyword.isEmpty()) keyword = null;
		}

		request.setAttribute("page", page);
		request.setAttribute("condition", condition);
		request.setAttribute("keyword", keyword);

		// 4) 상세 JSP 이동
		forward.setPath("newsdetail.jsp");
		forward.setRedirect(false);
		return forward;
	}
}