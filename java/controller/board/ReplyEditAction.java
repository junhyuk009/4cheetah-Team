package controller.board;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import controller.common.Action;
import controller.common.ActionForward;
import model.dao.ReplyDAO;
import model.dto.ReplyDTO;

public class ReplyEditAction implements Action {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {

		ActionForward forward = new ActionForward();
		ReplyDAO replyDAO = new ReplyDAO();
		ReplyDTO replyDTO = new ReplyDTO();

		// 1) 로그인 체크
		HttpSession session = request.getSession(false);
		if (session == null || session.getAttribute("memberId") == null) {
			System.out.println("[댓글 수정 로그] 실패: 로그인 세션 없음");

			request.setAttribute("msg", "로그인이 필요한 기능입니다.");
			request.setAttribute("location", "mainPage.do");
			forward.setPath("message.jsp");
			forward.setRedirect(false);
			return forward;
		}
		int memberId = (int) session.getAttribute("memberId");

		// 2) boardId 검증(redirect용)
		String boardIdParam = request.getParameter("boardId");
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

		// 3) replyId 검증
		String replyIdParam = request.getParameter("replyId");
		System.out.println("[댓글 수정 로그] replyIdParam=" + replyIdParam);

		if (replyIdParam == null || replyIdParam.trim().isEmpty()) {
			request.setAttribute("msg", "잘못된 접근입니다.(댓글 번호 없음)");
			request.setAttribute("location", "boardDetail.do?boardId=" + boardId);
			forward.setPath("message.jsp");
			forward.setRedirect(false);
			return forward;
		}

		int replyId;
		try {
			replyId = Integer.parseInt(replyIdParam.trim());
		} catch (NumberFormatException e) {
			request.setAttribute("msg", "댓글 번호가 올바르지 않습니다.");
			request.setAttribute("location", "boardDetail.do?boardId=" + boardId);
			forward.setPath("message.jsp");
			forward.setRedirect(false);
			return forward;
		}

		if (replyId <= 0) {
			request.setAttribute("msg", "잘못된 댓글 접근입니다.");
			request.setAttribute("location", "boardDetail.do?boardId=" + boardId);
			forward.setPath("message.jsp");
			forward.setRedirect(false);
			return forward;
		}

		// 4) replyContent 검증
		String replyContent = request.getParameter("replyContent");
		System.out.println("[댓글 수정 로그] replyContentParam=" + replyContent);

		if (replyContent == null || replyContent.trim().isEmpty()) {
			request.setAttribute("msg", "댓글 내용은 필수입니다.");
			request.setAttribute("location", "boardDetail.do?boardId=" + boardId);
			forward.setPath("message.jsp");
			forward.setRedirect(false);
			return forward;
		}

		replyContent = replyContent.trim();

		if (replyContent.length() > 500) {
			request.setAttribute("msg", "댓글은 500자 이내로 작성해주세요.");
			request.setAttribute("location", "boardDetail.do?boardId=" + boardId);
			forward.setPath("message.jsp");
			forward.setRedirect(false);
			return forward;
		}

		// 5) DTO 세팅 + condition(REPLY_UPDATE)
		replyDTO.setCondition("REPLY_UPDATE");
		replyDTO.setReplyId(replyId);
		replyDTO.setMemberId(memberId);       // ★본인 확인용
		replyDTO.setReplyContent(replyContent);

		boolean result = replyDAO.update(replyDTO);
		if (!result) {
			// 보통 실패 원인: 본인 댓글이 아닌데 수정 시도 / 댓글이 없음
			System.out.println("[댓글 수정 로그] 실패: UPDATE 실패(권한/존재 여부 확인)");

			request.setAttribute("msg", "댓글 수정에 실패했습니다.");
			request.setAttribute("location", "boardDetail.do?boardId=" + boardId);
			forward.setPath("message.jsp");
			forward.setRedirect(false);
			return forward;
		}

		// 6) 성공 redirect
		System.out.println("[댓글 수정 로그] 성공: replyId=" + replyId);
		forward.setPath("boardDetail.do?boardId=" + boardId);
		forward.setRedirect(true);
		return forward;
	}
}
