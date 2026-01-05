package controller.board;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import controller.common.Action;
import controller.common.ActionForward;
import model.dao.BoardDAO;
import model.dto.BoardDTO;

public class BoardDeleteAction implements Action{

   @Override
   public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
      ActionForward forward = new ActionForward();
      BoardDAO boardDAO = new BoardDAO();
      
      // 로그인 체크(삭제는 회원 기능)
      
      HttpSession session = request.getSession(false);
      
      if (session == null || session.getAttribute("memberId") == null) {
         System.out.println("[게시글 삭제 로그] 실패: 로그인 세션 없음");

         request.setAttribute("msg", "로그인이 필요한 기능입니다.");
         request.setAttribute("location", "mainPage.do");
         forward.setPath("message.jsp");
         forward.setRedirect(false);
         return forward;
      }

      int loginMemberId = (int) session.getAttribute("memberId");
      String memberRole = (String) session.getAttribute("memberRole"); // 없으면 null 가능
      System.out.println("[게시글 삭제 로그] 로그인 memberId=" + loginMemberId + ", role=" + memberRole);

      // 2) boardId 파라미터 유효성 검사
      
      String boardIdParam = request.getParameter("boardId");
      if (boardIdParam == null || boardIdParam.trim().isEmpty()) {
         System.out.println("[게시글 삭제 로그] 실패: boardId 파라미터 없음");

         request.setAttribute("msg", "잘못된 접근입니다.");
         request.setAttribute("location", "boardList.do");
         forward.setPath("message.jsp");
         forward.setRedirect(false);
         return forward;
      }

      int boardId;
      try {
         boardId = Integer.parseInt(boardIdParam.trim());
      } catch (NumberFormatException e) {
         System.out.println("[게시글 삭제 로그] 실패: boardId 정수 변환 오류");

         request.setAttribute("msg", "게시글 번호가 올바르지 않습니다.");
         request.setAttribute("location", "boardList.do");
         forward.setPath("message.jsp");
         forward.setRedirect(false);
         return forward;
      }

      if (boardId <= 0) {
         System.out.println("[게시글 삭제 로그] 실패: boardId 0 이하");

         request.setAttribute("msg", "잘못된 게시글 접근입니다.");
         request.setAttribute("location", "boardList.do");
         forward.setPath("message.jsp");
         forward.setRedirect(false);
         return forward;
      }

      // 3) 게시글 존재 확인 (삭제 전에 selectOne으로 방어) - 없는 글이면 바로 차단
      
      BoardDTO selectParam = new BoardDTO();
      selectParam.setBoardId(boardId);
      selectParam.setCondition("BOARD_DETAIL"); // DAO selectOne 분기 조건

      BoardDTO boardData = boardDAO.selectOne(selectParam);

      if (boardData == null) {
         System.out.println("[게시글 삭제 로그] 실패: 존재하지 않는 게시글 boardId=" + boardId);

         request.setAttribute("msg", "존재하지 않는 게시글입니다.");
         request.setAttribute("location", "boardList.do");
         forward.setPath("message.jsp");
         forward.setRedirect(false);
         return forward;
      }

      // 4) 권한 체크 (작성자 본인 OR 관리자)
      
      boolean isWriter = (boardData.getMemberId() == loginMemberId);
      boolean isAdmin = "ADMIN".equals(memberRole);

      System.out.println("[게시글 삭제 로그] isWriter=" + isWriter + ", isAdmin=" + isAdmin);

      if (!isWriter && !isAdmin) {
         request.setAttribute("msg", "삭제 권한이 없습니다.");
         request.setAttribute("location", "boardDetail.do?boardId=" + boardId);
         forward.setPath("message.jsp");
         forward.setRedirect(false);
         return forward;
      }

      // 5) DAO delete 호출 준비 - 중요: BoardDAO.DELETE_BOARD 가  "WHERE BOARD_ID=? AND MEMBER_ID=?" 라서
      // 관리자가 남의 글 삭제할 때는 "작성자 memberId"를 넣어야 삭제가 됨.

      BoardDTO deleteParam = new BoardDTO();
      deleteParam.setCondition("BOARD_DELETE");
      deleteParam.setBoardId(boardId);

      if (isAdmin) { // 관리자 삭제: 작성자 memberId로 세팅해야 DAO WHERE조건이 맞습니다.
         deleteParam.setMemberId(boardData.getMemberId());
      } else { // 본인 삭제: 로그인 memberId로 세팅
         deleteParam.setMemberId(loginMemberId);
      }

      boolean result = boardDAO.delete(deleteParam);

      if (!result) {
         System.out.println("[게시글 삭제 로그] 실패: DELETE 실패 boardId=" + boardId);

         request.setAttribute("msg", "게시글 삭제에 실패했습니다.");
         request.setAttribute("location", "boardDetail.do?boardId=" + boardId);
         forward.setPath("message.jsp");
         forward.setRedirect(false);
         return forward;
      }

      // 6) 성공 처리 - 삭제 후에는 목록으로 보내는 게 자연스러움
      
      System.out.println("[게시글 삭제 로그] 성공: boardId=" + boardId);
      
      request.setAttribute("msg", "게시글이 삭제되었습니다.");
      request.setAttribute("location", "boardList.do?category=" + boardData.getBoardCategory());
      forward.setPath("message.jsp");
      forward.setRedirect(false);
      return forward;
   }
}