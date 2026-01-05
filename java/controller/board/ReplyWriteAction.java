package controller.board;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import controller.common.Action;
import controller.common.ActionForward;
import model.dao.ReplyDAO;
import model.dto.ReplyDTO;

public class ReplyWriteAction implements Action {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {

		ActionForward forward = new ActionForward();
		ReplyDAO replyDAO = new ReplyDAO();
		ReplyDTO replyDTO = new ReplyDTO();

		
		// 1) 로그인 체크
		HttpSession session = request.getSession(false);
		if (session == null || session.getAttribute("memberId") == null) {
			System.out.println("[댓글 작성 로그] 실패: 로그인 세션 없음");

			request.setAttribute("msg", "로그인이 필요한 기능입니다.");
			request.setAttribute("location", "mainPage.do");
			forward.setPath("message.jsp");
			forward.setRedirect(false);
			return forward;
		}
		int memberId = (int) session.getAttribute("memberId");

		// 2) boardId 파라미터 검증
		String boardIdParam = request.getParameter("boardId");
		System.out.println("[댓글 작성 로그] boardIdParam=" + boardIdParam);

		if (boardIdParam == null || boardIdParam.trim().isEmpty()) {
			request.setAttribute("msg", "잘못된 접근입니다.(게시글 번호 없음)");
			request.setAttribute("location", "mainPage.do");
			forward.setPath("message.jsp");
			forward.setRedirect(false);
			return forward;
		}

		int boardId;
		try {
			boardId = Integer.parseInt(boardIdParam.trim());
		} catch (NumberFormatException e) {
			request.setAttribute("msg", "게시글 번호가 올바르지 않습니다.");
			request.setAttribute("location", "mainPage.do");
			forward.setPath("message.jsp");
			forward.setRedirect(false);
			return forward;
		}

		if (boardId <= 0) {
			request.setAttribute("msg", "잘못된 게시글 접근입니다.");
			request.setAttribute("location", "mainPage.do");
			forward.setPath("message.jsp");
			forward.setRedirect(false);
			return forward;
		}

		// 3) replyContent 파라미터 검증
		String replyContent = request.getParameter("replyContent");
		System.out.println("[댓글 작성 로그] replyContentParam=" + replyContent);

		if (replyContent == null || replyContent.trim().isEmpty()) {
			request.setAttribute("msg", "댓글 내용은 필수입니다.");
			request.setAttribute("location", "boardDetail.do?boardId=" + boardId);
			forward.setPath("message.jsp");
			forward.setRedirect(false);
			return forward;
		}

		replyContent = replyContent.trim(); // 공백만 입력 방지 + 앞뒤 공백 제거

		if (replyContent.length() > 500) { // DB: VARCHAR(500)
			request.setAttribute("msg", "댓글은 500자 이내로 작성해주세요.");
			request.setAttribute("location", "boardDetail.do?boardId=" + boardId);
			forward.setPath("message.jsp");
			forward.setRedirect(false);
			return forward;
		}

		// 4) DTO 세팅 + condition
		replyDTO.setCondition("REPLY_INSERT");
		replyDTO.setBoardId(boardId);
		replyDTO.setMemberId(memberId);
		replyDTO.setReplyContent(replyContent);

		// 5) DAO insert 실행
		boolean result = replyDAO.insert(replyDTO);
		if (!result) {
			System.out.println("[댓글 작성 로그] 실패: DAO INSERT 실패");

			request.setAttribute("msg", "댓글 작성에 실패했습니다.");
			request.setAttribute("location", "boardDetail.do?boardId=" + boardId);
			forward.setPath("message.jsp");
			forward.setRedirect(false);
			return forward;
		}

		// 6) 성공: redirect (중복등록 방지)
		System.out.println("[댓글 작성 로그] 성공: boardId=" + boardId);
		forward.setPath("boardDetail.do?boardId=" + boardId);
		forward.setRedirect(true);
		return forward;
	}
}
