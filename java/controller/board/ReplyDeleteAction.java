package controller.board;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import controller.common.Action;
import controller.common.ActionForward;
import model.dao.ReplyDAO;
import model.dto.ReplyDTO;

public class ReplyDeleteAction implements Action {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {

		ActionForward forward = new ActionForward();
		ReplyDAO replyDAO = new ReplyDAO();
		ReplyDTO replyDTO = new ReplyDTO();

		// 1) 로그인 체크
		HttpSession session = request.getSession(false);
		if (session == null || session.getAttribute("memberId") == null) {
			System.out.println("[댓글 삭제 로그] 실패: 로그인 세션 없음");

			request.setAttribute("msg", "로그인이 필요한 기능입니다.");
			request.setAttribute("location", "mainPage.do");
			forward.setPath("message.jsp");
			forward.setRedirect(false);
			return forward;
		}
		int memberId = (int) session.getAttribute("memberId");

		// role은 세션에 없을 수도 있으니 null-safe로 처리합니다.
		String memberRole = (String) session.getAttribute("memberRole");
		boolean isAdmin = "ADMIN".equals(memberRole);

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
		System.out.println("[댓글 삭제 로그] replyIdParam=" + replyIdParam);

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

		// 4) 관리자/일반회원 분기
		boolean result;

		if (isAdmin) {
			// 관리자: DELETE가 아니라 UPDATE로 내용 치환
			replyDTO.setCondition("REPLY_ADMIN_DELETE");
			replyDTO.setReplyId(replyId);
			replyDTO.setMemberId(memberId); // DAO에서 EXISTS로 ADMIN 검증하는 용도
			result = replyDAO.update(replyDTO);

			System.out.println("[댓글 삭제 로그] 관리자 삭제(내용치환) 실행: replyId=" + replyId);
		} else {
			// 일반회원: 물리삭제(본인만)
			replyDTO.setCondition("REPLY_DELETE");
			replyDTO.setReplyId(replyId);
			replyDTO.setMemberId(memberId);
			result = replyDAO.delete(replyDTO);

			System.out.println("[댓글 삭제 로그] 본인 삭제(물리삭제) 실행: replyId=" + replyId);
		}

		if (!result) {
			System.out.println("[댓글 삭제 로그] 실패: 삭제 처리 실패(권한/존재 여부 확인)");

			request.setAttribute("msg", "댓글 삭제에 실패했습니다.");
			request.setAttribute("location", "boardDetail.do?boardId=" + boardId);
			forward.setPath("message.jsp");
			forward.setRedirect(false);
			return forward;
		}

		// 5) 성공 redirect
		System.out.println("[댓글 삭제 로그] 성공: replyId=" + replyId + ", isAdmin=" + isAdmin);
		forward.setPath("boardDetail.do?boardId=" + boardId);
		forward.setRedirect(true);
		return forward;
	}
}
