package controller.board;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import controller.common.Action;
import controller.common.ActionForward;
import model.dao.BoardDAO;
import model.dto.BoardDTO;

public class BoardWriteAction implements Action{

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
		ActionForward forward = new ActionForward(); // 어디로 이동할지(redirect/forward 못함)
		BoardDAO boardDAO = new BoardDAO(); // DB 처리 
		BoardDTO boardDTO = new BoardDTO(); // 값 담을 그릇

		// 로그인 체크하기
		// 게시글 작성은 무조건 회원 기능이라서 memeberId가 없으면 INSERT 자체가 안됨.
		HttpSession session = request.getSession(false);
		// 세션이 없으면 새로 만들지 말고 null 리턴
		if (session == null || session.getAttribute("memberId") == null) {
			// 세션이 없거나, 세션이 memberId가 없으면 = 로그인 X
			System.out.println("[게시글 작성 로그] 실패: 로그인 세션 없음");

			request.setAttribute("msg", "로그인이 필요한 기능입니다.");
			request.setAttribute("location", "mainPage.do"); // 프로젝트 메인으로 이동
			forward.setPath("message.jsp");
			forward.setRedirect(false); // forward로 메시지 전달(리퀘스트 유지)
			return forward;
		}

		// 로그인 유저 정보 꺼내기
		int memberId = (int)session.getAttribute("memberId");

		// memberRole로 세션에 있을 수도/없을 수도 있음
		// 없으면 null일 수 있으니 그대로 string으로 받기
		String memberRole = (String)session.getAttribute("memberRole");
		System.out.println("[게시글 작성 로그] 로그인 memberId=" + memberId + ", role=" + memberRole);

		// 파라미터 받기(폼에서 넘어오는 값) - JSP form input name과 여기 parameter명이 일치해야 함
		String category = request.getParameter("boardCategory");
		String title = request.getParameter("boardTitle"); 
		String content = request.getParameter("boardContent");

		// 유효성 검사 - 카테고리 검사
		if (category == null || category.trim().isEmpty()) {
			System.out.println("[게시글 작성 로그] 실패: boardCategory 없음");

			request.setAttribute("msg", "게시판 카테고리가 올바르지 않습니다.");
			request.setAttribute("location", "mainPage.do");
			forward.setPath("message.jsp");
			forward.setRedirect(false);
			return forward;
		}
		category = category.trim();

		// 유효성 검사 - 제목 검사
		if (title == null || title.trim().isEmpty()) {
			System.out.println("[게시글 작성 로그] 실패: 제목 없음");

			request.setAttribute("msg", "제목은 필수입니다.");
			request.setAttribute("location", "boardWritePage.do?category="+ category);
			forward.setPath("message.jsp");
			forward.setRedirect(false);
			return forward;
		}
		title = title.trim();

		// 제목 255
		if (title.length() > 255) {
			System.out.println("[게시글 작성 로그] 실패: 제목 길이 초과 (" +title.length() +")");

			request.setAttribute("msg", "제목은 255자 이내로 작성해주세요.");
			request.setAttribute("location", "boardWritePage.do?category="+ category);
			forward.setPath("message.jsp");
			forward.setRedirect(false);
			return forward;
		}

		// 유효성 검사 - 내용 검사
		if (content == null || content.trim().isEmpty()) {
			System.out.println("[게시글 작성 로그] 실패: 내용 없음");

			request.setAttribute("msg", "내용은 필수입니다.");
			request.setAttribute("location", "boardWritePage.do?category="+ category);
			forward.setPath("message.jsp");
			forward.setRedirect(false);
			return forward;
		}
		
		content = content.trim();
		// 최소 XSS 방어: script / javascript: 차단
		String lowerContent = content.toLowerCase();
		if (lowerContent.contains("<script") || lowerContent.contains("javascript:")) {
		    System.out.println("[게시글 작성 로그] 실패: XSS 의심 태그 포함");

		    request.setAttribute("msg", "허용되지 않는 내용이 포함되어 있습니다.");
		    request.setAttribute("location", "boardWritePage.do?category=" + category);
		    forward.setPath("message.jsp");
		    forward.setRedirect(false);
		    return forward;
		}

		// DTO에 값 + 컨디션 세팅하기 - DAO가 condition으로 분기하므로 정확히 맞춰야 함.
		boardDTO.setMemberId(memberId); // 작성자(로그인한 유저)
		boardDTO.setBoardCategory(category); // 게시판 카테고리
		boardDTO.setBoardTitle(title); // 제목
		boardDTO.setBoardContent(content); // 내용

		// DAO에서 insert 할 때 사용하기 위한 condition 
		boardDTO.setCondition("BOARD_INSERT");

		// INSERT + 방금 생성된 boardId(PK)받기 - 작성 후 상세페이지 이동하기 위해서는 PK가 필요함.
		Integer newBoardId = boardDAO.insertReturnId(boardDTO);

		// 유효성 검사 INSERT 못했을 때
		if (newBoardId == null || newBoardId <= 0) {
			System.out.println("[게시글 작성 로그] 실패: INSERT 실패");

			request.setAttribute("msg", "게시글 작성에 실패했습니다.");
			request.setAttribute("location", "boardList.do?category=" + category);
			forward.setPath("message.jsp"); 
			forward.setRedirect(false);
			return forward;
		}
		System.out.println("[게시글 작성 로그] 성공: newBoardId=" + newBoardId);

		// 성공 시 게시글 상세 페이지로 이동(redirect) - 새로고침 시 중복 등록 방지하기
		forward.setPath("boardDetail.do?boardId=" + newBoardId);
		forward.setRedirect(true);
		return forward;
		}
}
