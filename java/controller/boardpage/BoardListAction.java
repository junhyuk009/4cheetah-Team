package controller.boardpage;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import controller.common.Action;
import controller.common.ActionForward;
import model.dao.BoardDAO;
import model.dto.BoardDTO;

public class BoardListAction implements Action {

	/*
	[게시판(카테고리) 동기 목록 + 검색 컨트롤러 흐름 요약]

	0) 요청 형태
	- 기본 목록(전체글보기): boardList.do?category=free
	- 검색 목록: boardList.do?category=free&condition=BOARD_SEARCH_TITLE&keyword=자바
	  (카테고리 내부에서만 검색한다)

	1) category 파라미터 수신 + 검증
	- category는 "어느 게시판(카테고리)을 조회할지" 결정하는 필수값이다.
	- 없거나 비어있으면 잘못된 접근이므로 메인으로 돌려보낸다.

	2) 검색 파라미터 수신
	- condition : 검색 분기(제목/작성자/내용)
	  · BOARD_SEARCH_TITLE
	  · BOARD_SEARCH_WRITER
	  · BOARD_SEARCH_CONTENT
	- keyword : 검색어

	3) keyword 정리 + 검색 유효성 검사
	- keyword는 trim() 처리 후 빈 문자열이면 null로 정리한다.
	- 검색인데 keyword가 없으면 의미 없는 검색이므로 message.jsp로 차단한다.

	4) DAO 조회 컨디션 결정 (+ 공지 분리)
	- 공지(상단 고정): BOARD_NOTICE_LIST
	  · 동일 카테고리 내에서 작성자 role=ADMIN인 글만 조회(DAO 쿼리에서 처리)
	  · 검색 여부와 무관하게 상단에 보여주려면 항상 조회한다.
	    (만약 "검색 중엔 공지 숨김" 정책이면 isSearch일 때 noticeList 조회를 생략하면 됨)

	- 일반 목록:
	  · 검색이면 검색 컨디션(BOARD_SEARCH_*)을 그대로 DAO에 넘긴다.
	    (DAO 쿼리 자체에 WHERE BOARD_CATEGORY=? 가 포함되어 있어 카테고리 내부 검색이 보장됨)
	    · BOARD_SEARCH_TITLE   → (카테고리 + 제목 LIKE)
	    · BOARD_SEARCH_WRITER  → (카테고리 + 작성자 LIKE)
	    · BOARD_SEARCH_CONTENT → (카테고리 + 내용 CLOB 검색)
	  · 검색이 아니면 기본 전체글보기(카테고리 전체, 관리자 글 제외)
	    · CATEGORY_LIST

	5) selectAll 결과를 request에 담아 board.jsp로 forward
	- noticeList(공지), boardList(일반), category 전달
	- 검색 상태 유지용(condition, keyword)도 같이 전달
	*/

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
		request.setAttribute("activeMenu", "COMMUNITY"); //메뉴 활성화용
		ActionForward forward = new ActionForward();

		// =========================================================
		// 1) category 필수 파라미터 검증
		String category = request.getParameter("category");

		if (category == null || category.trim().isEmpty()) {
			System.out.println("[게시판 리스트 로그] category 없음/빈값 → 메인으로 리다이렉트");
			forward.setPath("mainPage.do");
			forward.setRedirect(true);
			return forward;
		}

		category = category.trim().toUpperCase();
		System.out.println("[게시판 리스트 로그] category=[" + category + "]");

		// =========================================================
		// 2) 검색 파라미터 수신
		String condition = request.getParameter("condition"); // BOARD_SEARCH_TITLE / WRITER / CONTENT
		String keyword = request.getParameter("keyword");

		System.out.println("[게시판 리스트 로그] condition=[" + condition + "]");
		System.out.println("[게시판 리스트 로그] keyword(raw)=[" + keyword + "]");

		// =========================================================
		// 3) 검색 여부 판단(화이트리스트) + keyword 정리(trim)
		boolean isSearch =
				"BOARD_SEARCH_TITLE".equals(condition) ||
				"BOARD_SEARCH_WRITER".equals(condition) ||
				"BOARD_SEARCH_CONTENT".equals(condition);

		if (keyword != null) {
			keyword = keyword.trim();
			if (keyword.isEmpty()) {
				keyword = null;
			}
		}

		System.out.println("[게시판 리스트 로그] isSearch=[" + isSearch + "], keyword(trim)=[" + keyword + "]");

		// 검색인데 키워드가 없으면 차단
		if (isSearch && keyword == null) {
			System.out.println("[게시판 리스트 로그] 검색 요청인데 keyword 없음 → message.jsp");
			request.setAttribute("msg", "검색어가 없습니다.");
			request.setAttribute("location", "boardList.do?category=" + category); // 카테고리 유지
			forward.setPath("message.jsp");
			forward.setRedirect(false);
			return forward;
		}

		// =========================================================
		// 4) DAO 조회 (공지 + 일반/검색)
		BoardDAO boardDAO = new BoardDAO();
		BoardDTO boardDTO = new BoardDTO();

		// 4-1) 공지 리스트(관리자 글만) 조회
		boardDTO.setBoardCategory(category);
		boardDTO.setCondition("BOARD_NOTICE_LIST");

		List<BoardDTO> noticeList = boardDAO.selectAll(boardDTO);
		System.out.println("[게시판 리스트 로그] 공지 조회 완료 : noticeCount=[" + (noticeList == null ? 0 : noticeList.size()) + "]");

		// 4-2) 일반 리스트(검색/전체글보기) 조회
		boardDTO = new BoardDTO(); // ★ 네가 말한 스타일: DTO 재생성해서 재사용
		boardDTO.setBoardCategory(category);

		if (isSearch) {
			boardDTO.setCondition(condition); // ★ BOARD_SEARCH_TITLE/WRITER/CONTENT 그대로
			boardDTO.setKeyword(keyword);     // ★ LIKE 검색에 사용
		} else {
			boardDTO.setCondition("CATEGORY_LIST");
			condition = "CATEGORY_LIST"; // 상태 유지용
		}

		System.out.println("[게시판 리스트 로그] DAO condition=[" + boardDTO.getCondition() + "], category=[" + category + "], keyword=[" + boardDTO.getKeyword() + "]");

		List<BoardDTO> boardList = boardDAO.selectAll(boardDTO);
		System.out.println("[게시판 리스트 로그] 일반/검색 조회 완료 : count=[" + (boardList == null ? 0 : boardList.size()) + "]");

		// =========================================================
		// 5) JSP로 전달 (검색 상태 유지 포함)
		request.setAttribute("noticeList", noticeList); // ★ 공지 리스트
		request.setAttribute("boardList", boardList);   // 일반/검색 리스트
		request.setAttribute("category", category);

		// 검색 폼 상태 유지(선택값/입력값 유지용)
		request.setAttribute("condition", condition);
		request.setAttribute("keyword", keyword);

		// =========================================================
		// 6) 게시판 페이지로 이동
		forward.setPath("board.jsp");
		forward.setRedirect(false);
		return forward;
	}
}
