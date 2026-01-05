package controller.boardpage;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import controller.common.Action;
import controller.common.ActionForward;
import model.dao.BoardDAO;
import model.dto.BoardDTO;

public class MyPostPageAction implements Action {
	
	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession(false);
		ActionForward forward = new ActionForward();

		if (session == null) {
			System.out.println("[내 글보기 이동 로그] 세션 없음 : 로그인 필요");
			request.setAttribute("msg", "로그인 정보가 없습니다.");
			request.setAttribute("location", "loginPage.do");
			forward.setPath("message.jsp");
			forward.setRedirect(false);			
		    return forward;
		}
		
		//세션에 담긴 PK값 불러와서 정수형 변환하고 담기
		Integer memberId = (Integer)session.getAttribute("memberId");
		System.out.println("[내 글보기 이동 로그] 세션 확인 : memberId=[" + memberId + "]");
		
		//유효성검사 - 세션에 id없으면 로그인해라
		if (memberId == null) {
			System.out.println("[내 글보기 이동 로그] memberId 없음 : 로그인 필요");
			request.setAttribute("msg", "로그인 정보가 없습니다.");
			request.setAttribute("location", "loginPage.do");
			forward.setPath("message.jsp");
			forward.setRedirect(false);			
		    return forward;
		}
		
		//셀렉트올 호출 후 넘어온 게시글목록 list에 담기
		BoardDAO boardDAO = new BoardDAO();
		BoardDTO boardDTO = new BoardDTO();
		boardDTO.setCondition("MY_BOARD_LIKE_LIST");
		boardDTO.setMemberId(memberId);
		
		List<BoardDTO> myBoardLikeList = boardDAO.selectAll(boardDTO);
		System.out.println("[내 글보기 이동 로그] 좋아요글 조회 완료: count=[" + myBoardLikeList.size() + "]");
		request.setAttribute("myBoardLikeList", myBoardLikeList);
		
		//내가 작성한 글 리스트 담기		
		//멤버id 활용해서 작성한글 모두 셀렉트올 호출 후 list에 담기
		boardDTO = new BoardDTO();
		boardDTO.setCondition("MY_BOARD_WRITE_LIST");
		boardDTO.setMemberId(memberId);
		
		List<BoardDTO> myBoardWriteList = boardDAO.selectAll(boardDTO);
		System.out.println("[내 글보기 이동 로그] 작성글 조회 완료 : count=[" + myBoardWriteList.size() + "]");
		request.setAttribute("myBoardWriteList", myBoardWriteList);		
		
		forward.setPath("mypost.jsp");
		forward.setRedirect(false);		
		return forward;
	}
}
