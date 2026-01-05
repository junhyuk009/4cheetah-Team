package controller.boardpage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import controller.common.Action;
import controller.common.ActionForward;
import model.dao.BoardDAO;
import model.dto.BoardDTO;

public class BoardEditPageAction implements Action {	
	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
		request.setAttribute("activeMenu", "COMMUNITY");
		ActionForward forward = new ActionForward();
		BoardDAO boardDAO = new BoardDAO();
		BoardDTO boardDTO = new BoardDTO();	     
		// 1. 로그인 여부 확인 (세션에서 memberId / role 꺼냄)
		// 2. 게시글 PK(boardId) 파라미터 수신
		// 3. boardId로 게시글 selectOne
		// 4. 게시글 작성자 vs 로그인 사용자 비교
		// - 동일하면 OK 관리자면 OK
		// - 그 외 → 권한 없음 처리
		// 5. 수정 페이지로 이동하기 위한 데이터 세팅
		// - boardId (PK)
		// - 게시글 타입 (BOARD)
		// - 게시판 카테고리
		// - 게시글 기존 데이터 (제목, 내용)
		// 6. 수정 페이지jsp로 forward

		//1. 로그인 체크
		HttpSession session = request.getSession(false);
		//이미 세션이 있으면 가져오고, 없으면 새로 만들지 마라		
		if (session == null || session.getAttribute("memberId") == null) {
			System.out.println("[게시글 수정 이동 로그] 세션 없음 : memberId 유효하지 않음");
			request.setAttribute("msg", "잘못된 접근입니다.");
			request.setAttribute("location", "mainPage.do");
			forward.setPath("message.jsp");
			forward.setRedirect(false);
			return forward;
		}
		//검사 통과 시 id담기
		int memberId = (int) session.getAttribute("memberId");
		String memberRole = (String) session.getAttribute("memberRole");
		System.out.println("[게시글 수정 이동 로그] 로그인 유저 ID : memberId ["+memberId+"]");
		System.out.println("[게시글 수정 이동 로그] 로그인 유저 ROLE : memberRole ["+memberRole+"]");

		//2. 게시글 PK(boardId) 파라미터 검증
		String boardIdCheck = request.getParameter("boardId");
		System.out.println("[게시글 수정 이동 로그] 게시글 PK 검사 : boardIdCheck ["+boardIdCheck+"]");
		if (boardIdCheck == null) {
			request.setAttribute("msg", "잘못된 접근입니다.");
			request.setAttribute("location", "mainPage.do");
			forward.setPath("message.jsp");
			forward.setRedirect(false);
			return forward;
		}

		int boardId;
		try {
			boardId = Integer.parseInt(boardIdCheck);
		} catch (NumberFormatException e) {
			System.out.println("[게시글 수정 이동 로그] 게시글 PK 검사 : boardId 정수 변환 오류");
			request.setAttribute("msg", "게시글 번호가 올바르지 않습니다.");
			request.setAttribute("location", "boardList.do");
			forward.setPath("message.jsp");
			forward.setRedirect(false);
			return forward;
		}		
		if (boardId <= 0) {//게시글id값이 0이거나 음수면 -> 메인페이지로 이동
			System.out.println("[게시글 수정 이동 로그] 게시글 PK 검사 : boardId 0이하 오류");
			request.setAttribute("msg", "잘못된 게시글 접근입니다...");
			request.setAttribute("location", "mainPage.do");
			forward.setPath("message.jsp");
			forward.setRedirect(false);			
		    return forward;
		}			

		//3. 게시글 조회
		boardDTO.setBoardId(boardId);
	    boardDTO.setCondition("BOARD_DETAIL");
	    BoardDTO boardData = boardDAO.selectOne(boardDTO);   
	    System.out.println("[게시글 수정 이동 로그] 게시글 조회 완료 : boardData ["+boardData+"]");

		if (boardData == null) {
			request.setAttribute("msg", "존재하지 않는 게시글입니다.");
			request.setAttribute("location", "boardList.do");
			forward.setPath("message.jsp");
			forward.setRedirect(false);
			return forward;
		}

		//4. 권한 체크
		//- 작성자 본인 or 관리자 → 가능
		//- 그 외 → 차단
		boolean isWriter = boardData.getMemberId() == memberId;
		System.out.println("[게시글 수정 이동 로그] 작성자 일치 : isWriter ["+isWriter+"]");
		boolean isAdmin = "ADMIN".equals(memberRole);
		System.out.println("[게시글 수정 이동 로그] 관리자 권한 확인 : isAdmin ["+isAdmin+"]");

		if (!isWriter && !isAdmin) {
			request.setAttribute("msg", "수정 권한이 없습니다.");
			request.setAttribute("location", "mainPage.do");
			forward.setPath("message.jsp");
			forward.setRedirect(false);
			return forward;
		}

		//5. 수정 페이지(edit.jsp)로 데이터 전달
		// 타입(게시글)과 기존 게시글 데이터 담기
		request.setAttribute("type", "BOARD");
		request.setAttribute("boardData", boardData);

		// 수정 페이지는 forward (리퀘스트셋했으니까)
		forward.setPath("edit.jsp");
		forward.setRedirect(false);

		return forward;
	}
}
