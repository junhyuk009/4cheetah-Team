package controller.newspage;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import controller.common.Action;
import controller.common.ActionForward;
import model.dao.NewsDAO;
import model.dto.NewsDTO;

public class NewsListAction implements Action {

	/*
	[뉴스 동기 페이지네이션 + 검색(제목/내용) 컨트롤러 흐름 요약]

	1) page 파라미터 수신/검증 (외부 입력 방어)
	2) condition(검색 분기) + keyword(검색어) 수신/정리
	   - 검색인데 keyword가 없으면 message.jsp로 차단
	3) condition에 따라 COUNT/LIST_PAGE 컨디션 매핑
	   - 검색이면 COUNT/LIST_PAGE 모두 "검색용"으로 변경
	4) newsCount 조회 → totalPage 계산 → page 상한 보정
	5) startRow/endRow 계산 → 해당 범위 newsList 조회
	6) 페이지블록 계산(startPage/endPage/hasPrev/hasNext)
	7) JSP로 전달 후 news.jsp forward
	*/

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
		request.setAttribute("activeMenu", "NEWS"); //메뉴 활성화용
		ActionForward forward = new ActionForward();

		// 0) 정책: 뉴스는 항상 12개 고정
		final int listSize = 12;

		// 1) page 파라미터 수신 + 검증
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

		// 2) 검색 파라미터 수신/정리
		// - 검색 분기값은 condition으로 받음
		//   · NEWS_SEARCH_TITLE - 제목 검색
		//   · NEWS_SEARCH_CONTENT - 내용 검색
		String condition = request.getParameter("condition");
		String keyword = request.getParameter("keyword");
		System.out.println("[뉴스리스트 이동 로그] condition : ["+condition+"]");
		System.out.println("[뉴스리스트 이동 로그] keyword : ["+keyword+"]");

		if (keyword != null) {
			keyword = keyword.trim();
			if (keyword.isEmpty()) keyword = null;
		}
		
		// 컨디션이 아래 두 경우면 검색 희망으로 판단
		boolean isSearch =
				"NEWS_SEARCH_TITLE".equals(condition) ||
				"NEWS_SEARCH_CONTENT".equals(condition);

		// 검색인데 keyword가 없으면 차단 (UX/불필요 조회 방지)
		if (isSearch && (keyword == null)) {
			request.setAttribute("msg", "검색어가 없습니다.");
			request.setAttribute("location", "newsList.do"); // 돌아갈 곳
			forward.setPath("message.jsp");
			forward.setRedirect(false);
			return forward;
		}

		// 3) condition → DAO용 컨디션 매핑(COUNT/LIST_PAGE)
		//    검색이 들어오면 COUNT/LIST_PAGE 둘 다 검색용으로 바뀌어야 함
		String countCondition;
		String listCondition;

		if ("NEWS_SEARCH_TITLE".equals(condition)) {
			countCondition = "NEWS_COUNT_TITLE";
			listCondition = "NEWS_LIST_PAGE_TITLE";
		} else if ("NEWS_SEARCH_CONTENT".equals(condition)) {
			countCondition = "NEWS_COUNT_CONTENT";
			listCondition = "NEWS_LIST_PAGE_CONTENT";
		} else {
			// 기본 전체 목록 - 최신순 전체 뉴스
			condition = "NEWS_LIST"; // 상태 유지용 (원하면 값은 아무거나 OK)
			countCondition = "NEWS_COUNT";
			listCondition = "NEWS_LIST_PAGE";
		}

		NewsDAO newsDAO = new NewsDAO();

		// 4) newsCount 조회 (COUNT)
		NewsDTO newsDTO = new NewsDTO();
		newsDTO.setCondition(countCondition);
		newsDTO.setKeyword(keyword); // 검색이면 사용, 기본목록이면 null이라 영향 없음

		NewsDTO countData = newsDAO.selectOne(newsDTO);

		int newsCount = 0;
		if (countData != null) {
			newsCount = countData.getNewsCount(); 
		}

		// totalPage 계산 (검색 결과 기준이어야 함)
		int totalPage = (int) Math.ceil((double) newsCount / listSize);

		// 데이터 0개여도 페이지바 처리를 단순하게 하려면 1로 보정
		if (totalPage < 1) totalPage = 1;

		// 마지막 페이지 초과 요청 방어
		if (page > totalPage) page = totalPage;

		// 5) startRow / endRow 계산
		int startRow = (page - 1) * listSize + 1;
		int endRow = page * listSize;

		// 6) 현재 페이지 뉴스 목록 조회 (LIST_PAGE)
		newsDTO = new NewsDTO();
		newsDTO.setCondition(listCondition);
		newsDTO.setStartRow(startRow);
		newsDTO.setEndRow(endRow);
		newsDTO.setKeyword(keyword); // 검색이면 적용

		List<NewsDTO> newsList = newsDAO.selectAll(newsDTO);

		// 7) 페이지 버튼 블록 계산
		int blockSize = 5;

		int startPage = ((page - 1) / blockSize) * blockSize + 1;
		int endPage = Math.min(startPage + blockSize - 1, totalPage);

		boolean hasPrev = startPage > 1;
		boolean hasNext = endPage < totalPage;

		// 8) JSP로 전달 (검색 상태 유지값도 같이 전달)
		request.setAttribute("newsList", newsList); // 뉴스 목록 데이터

		request.setAttribute("page", page); // 현재 페이지 번호 (활성화 표시용)
		request.setAttribute("listSize", listSize); // 페이지당 출력 개수 (정책: 12개)
		request.setAttribute("newsCount", newsCount); // 전체 뉴스 개수 (또는 검색 결과 총 개수)
		request.setAttribute("totalPage", totalPage); // 총 페이지 수 (마지막 페이지)

		request.setAttribute("startPage", startPage); // 현재 블록의 시작 페이지
		request.setAttribute("endPage", endPage); // 현재 블록의 끝 페이지
		request.setAttribute("hasPrev", hasPrev); // - hasPrev=true  → « (이전 블록) 버튼 노출
		request.setAttribute("hasNext", hasNext); // - hasNext=true  → » (다음 블록) 버튼 노출

		// 검색 상태 유지 (페이지 이동 시 계속 유지하려면 필요)
		request.setAttribute("condition", condition);
		request.setAttribute("keyword", keyword);

		forward.setPath("news.jsp");
		forward.setRedirect(false);
		return forward;
	}
}