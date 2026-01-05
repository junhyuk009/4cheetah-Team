package controller.boardpage;

import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import controller.common.Action;
import controller.common.ActionForward;
import model.dao.BoardDAO;
import model.dao.BoardLikeDAO;
import model.dao.ReplyDAO;
import model.dto.BoardDTO;
import model.dto.BoardLikeDTO;
import model.dto.ReplyDTO;

public class BoardDetailAction implements Action {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
		request.setAttribute("activeMenu", "COMMUNITY");		
		ActionForward forward = new ActionForward();
		BoardDAO boardDAO = new BoardDAO();
		BoardDTO boardDTO = new BoardDTO();
		ReplyDAO replyDAO = new ReplyDAO();
		ReplyDTO replyDTO = new ReplyDTO();
		BoardLikeDAO boardlikeDAO = new BoardLikeDAO();
		/*
		1. boardId 파라미터 검증
		2. 조회수 쿠키 검사
		3. (쿠키 없으면) 조회수 증가 시도
		   - 성공 시 : 쿠키 생성
		   - 실패 시 : 쿠키 생성 X (조회수만 실패로 처리하고 계속 진행)
		4. 게시글 selectOne (존재여부 최종 확인)
		5. 댓글 selectAll
		6. boarddetail.jsp 이동
		*/

		// 1) boardId 파라미터 유효성 검사
		String boardIdCheck = request.getParameter("boardId");

		if (boardIdCheck == null || boardIdCheck.isEmpty()) {
			System.out.println("[게시글 상세보기 로그] boardIdCheck 실패 : boardId 유효하지 않음");
			request.setAttribute("msg", "잘못된 게시글 접근입니다...");
			request.setAttribute("location", "mainPage.do");
			forward.setPath("message.jsp");
			forward.setRedirect(false);
			return forward;
		}

		int boardId;
		try {
			boardId = Integer.parseInt(boardIdCheck);
		} catch (NumberFormatException e) {
			System.out.println("[게시글 상세보기 로그] boardId 변환 실패 : boardId 정수 변환 오류");
			request.setAttribute("msg", "잘못된 게시글 접근입니다...");
			request.setAttribute("location", "mainPage.do");
			forward.setPath("message.jsp");
			forward.setRedirect(false);
			return forward;
		}

		if (boardId <= 0) {
			System.out.println("[게시글 상세보기 로그] boardId 유효값 아님 : boardId 0이하 오류");
			request.setAttribute("msg", "잘못된 게시글 접근입니다...");
			request.setAttribute("location", "mainPage.do");
			forward.setPath("message.jsp");
			forward.setRedirect(false);
			return forward;
		}

		// 2) 조회수 쿠키 검사
		String targetCookieName = "board_view_" + boardId; //로그 검사용 임시 추가	
		boolean isViewed = false;
		Cookie[] cookies = request.getCookies();
		
		if (cookies != null) {
			for (Cookie c : cookies) {
				if (("board_view_" + boardId).equals(c.getName())) {
					isViewed = true;
					break;
				}
			}
		}
		System.out.println("[게시글 상세보기 로그] 쿠키 검사 결과 name=["+targetCookieName+"]");
		System.out.println("[게시글 상세보기 로그] 쿠키 검사 결과 isViewed=["+isViewed+"]");
		
		// 3) 쿠키 없으면 조회수 증가 시도
		// - 성공 시에만 쿠키 생성
		// - 실패해도 글은 보여주고(최종 존재여부는 selectOne에서 판단), 조회수만 실패로 처리
		if (!isViewed) {
			boardDTO.setBoardId(boardId);
			boardDTO.setCondition("UPDATE_BOARD_VIEWS");
			boolean isUpdated = boardDAO.update(boardDTO);

			if (isUpdated) {
				System.out.println("[게시글 상세보기 로그] 조회수 증가 성공 : boardId=" + boardId);

				// update 성공했을 때만 쿠키 생성(정책 꼬임 방지)
				Cookie viewCookie = new Cookie("board_view_" + boardId, "Y");
				viewCookie.setMaxAge(60 * 60 * 24); // 유효기간 1일
				viewCookie.setPath("/");
				viewCookie.setHttpOnly(true);
				response.addCookie(viewCookie);

			} else {
				// 조회수 증가 실패 시: 쿠키 생성 X, 강제종료 X
				// (DB 오류/락/예외 등으로 조회수만 실패할 수 있으므로 글은 보여주되 로그만 남김)
				// 여기서 바로 사용자를 다른 페이지로 이동시켜도 되지만(성능 효율좋음)
				// 그러면 함수가 return으로 종료되게 되고 실패 원인을 바로 알아보기 힘들다. 
				// 가장 확률이 높은 셀릭트원 null 이슈를 파악하기 좋게 바로 밑에서 자동으로 진행시키기 위함
				System.out.println("[게시글 상세보기 로그] 조회수 증가 실패 : boardId=" + boardId);
			}
		}

		// 4) 게시글 selectOne (존재여부 최종 확인)
		boardDTO = new BoardDTO();
		boardDTO.setBoardId(boardId);
		boardDTO.setCondition("BOARD_DETAIL");
		BoardDTO boardData = boardDAO.selectOne(boardDTO);

		// 존재하지 않는 게시글이면 차단
		if (boardData == null) {
			System.out.println("[게시글 상세보기 로그] boardData 조회 실패 : 해당 게시글은 없는 게시글");
			request.setAttribute("msg", "존재하지 않는 게시글입니다...");
			request.setAttribute("location", "mainPage.do");
			forward.setPath("message.jsp");
			forward.setRedirect(false);
			return forward;
		}
		
		// 좋아요 개수
		BoardLikeDTO boardlikeDTO = new BoardLikeDTO();
		boardlikeDTO.setBoardId(boardId);
		boardlikeDTO.setCondition("BOARD_LIKE_COUNT");
		boardlikeDTO = boardlikeDAO.selectOne(boardlikeDTO);
		request.setAttribute("likeCount", boardlikeDTO == null ? 0 : boardlikeDTO.getLikeCnt());

		// 내가 눌렀는지(로그인 시에만)
		Integer memberId = (Integer) request.getSession().getAttribute("memberId");
		boolean likedByMe = false;
		if (memberId != null) {
		    boardlikeDTO = new BoardLikeDTO();
		    boardlikeDTO.setBoardId(boardId);
		    boardlikeDTO.setMemberId(memberId);
		    boardlikeDTO.setCondition("BOARD_LIKE_CHECK");
		    BoardLikeDTO checkRes =  boardlikeDAO.selectOne(boardlikeDTO);
		    likedByMe = (checkRes != null && checkRes.getIsLiked() > 0);
		}
		request.setAttribute("likedByMe", likedByMe);

		
		// 5) 댓글 selectAll
		replyDTO.setBoardId(boardId);
		replyDTO.setCondition("REPLY_LIST_RECENT");
		List<ReplyDTO> replyList = replyDAO.selectAll(replyDTO);
        System.out.println("[게시글 상세보기 로그] 댓글 조회 완료 : count=["+replyList.size()+"]");

		// 6) 게시글/댓글 담아서 페이지 이동
		request.setAttribute("boardData", boardData);
		request.setAttribute("replyList", replyList);

		forward.setPath("boarddetail.jsp");
		forward.setRedirect(false);
		return forward;
	}
}