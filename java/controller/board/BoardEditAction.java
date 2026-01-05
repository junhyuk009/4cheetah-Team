package controller.board;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import controller.common.Action;
import controller.common.ActionForward;
import model.dao.BoardDAO;
import model.dto.BoardDTO;

public class BoardEditAction implements Action{

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
		ActionForward forward = new ActionForward();
		BoardDAO boardDAO = new BoardDAO();
		BoardDTO boardDTO = new BoardDTO();
		// 로그인 체크(게시글 수정은 회원 기능) - 세션 없거나 memberId 없으면 차단
		
		HttpSession session = request.getSession(false);
		if (session == null || session.getAttribute("memberId") == null) {
			System.out.println("[게시글 수정 처리 로그] 실패: 로그인 세션 없음.");
			
			request.setAttribute("msg", "로그인이 필요한 기능입니다.");
			request.setAttribute("location", "mainPage.do");
			forward.setPath("message.jsp");
			forward.setRedirect(false);
			return forward;
		}
		int loginMemberId = (int) session.getAttribute("memberId");
		String memberRole = (String) session.getAttribute("memberRole"); // 없으면 null 일 수 있음
		System.out.println("[게시글 수정 처리 로그] 로그인 memberId=" + loginMemberId + ", role="+ memberRole);
		
		// 유효성 검사 파라미터 - URL 조작/빈 값/ 정수 변환 실패 방어
		String boardIdParam = request.getParameter("boardId");
		if (boardIdParam == null || boardIdParam.trim().isEmpty()) {
			System.out.println("[게시글 수정 처리 로그] 실패: boardId 파라미터 없음");
			
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
			System.out.println("[게시글 수정 처리 로그] 실패: boardId 정수 변환 오류");

			request.setAttribute("msg", "게시글 번호가 올바르지 않습니다.");
			request.setAttribute("location", "boardList.do");
			forward.setPath("message.jsp");
			forward.setRedirect(false);
			return forward;
		}
		if (boardId <= 0) {
			System.out.println("[게시글 수정 처리 로그] 실패: boardId 0 이하");

			request.setAttribute("msg", "잘못된 게시글 접근입니다.");
			request.setAttribute("location", "boardList.do");
			forward.setPath("message.jsp");
			forward.setRedirect(false);
			return forward;
		}
		// 게시글 존재 여부 확인(SELECT_ONE) - 수정 전에 원본 글이 존재하는지 확인
		BoardDTO param = new BoardDTO();
		param.setBoardId(boardId);
		param.setCondition("BOARD_DETAIL"); // 시트/DAO 컨디션과 정확히 일치해야 함
		BoardDTO boardData = boardDAO.selectOne(param);

		if (boardData == null) {
			System.out.println("[게시글 수정 처리 로그] 실패: 존재하지 않는 게시글 boardId=" + boardId);

			request.setAttribute("msg", "존재하지 않는 게시글입니다.");
			request.setAttribute("location", "boardList.do");
			forward.setPath("message.jsp");
			forward.setRedirect(false);
			return forward;
		}
		// 권한 체크 (본인 OR 관리자) - 시트에도 “본인/관리자” 권한 확인 흐름이 있음
		boolean isWriter = (boardData.getMemberId() == loginMemberId);
		boolean isAdmin = "ADMIN".equals(memberRole);

		System.out.println("[게시글 수정 처리 로그] isWriter=" + isWriter + ", isAdmin=" + isAdmin);

		if (!isWriter && !isAdmin) {
			request.setAttribute("msg", "수정 권한이 없습니다.");
			request.setAttribute("location", "boardDetail.do?boardId=" + boardId);
			forward.setPath("message.jsp");
			forward.setRedirect(false);
			return forward;
		}
		// 5) 수정 폼 파라미터(제목/내용) 받기 + 유효성 검사
		String title = request.getParameter("boardTitle");
		String content = request.getParameter("boardContent");
		// 제목 필수 + 공백 제거 후 체크
				if (title == null || title.trim().isEmpty()) {
					System.out.println("[게시글 수정 처리 로그] 실패: 제목 없음");

					request.setAttribute("msg", "제목은 필수입니다.");
					request.setAttribute("location", "boardEditPage.do?boardId=" + boardId);
					forward.setPath("message.jsp");
					forward.setRedirect(false);
					return forward;
				}
				title = title.trim(); // 앞/뒤 공백만 제거(중간 공백은 유지)

				// DB 컬럼이 VARCHAR(255)라면 안전하게 255 제한
				if (title.length() > 255) {
					System.out.println("[게시글 수정 처리 로그] 실패: 제목 길이 초과 len=" + title.length());

					request.setAttribute("msg", "제목은 255자 이내로 작성해주세요.");
					request.setAttribute("location", "boardEditPage.do?boardId=" + boardId);
					forward.setPath("message.jsp");
					forward.setRedirect(false);
					return forward;
				}

				// 내용 필수
				if (content == null || content.trim().isEmpty()) {
					System.out.println("[게시글 수정 처리 로그] 실패: 내용 없음");

					request.setAttribute("msg", "내용은 필수입니다.");
					request.setAttribute("location", "boardEditPage.do?boardId=" + boardId);
					forward.setPath("message.jsp");
					forward.setRedirect(false);
					return forward;
				}
				content = content.trim();

				// (선택) 너무 큰 입력 방어 - CLOB이라도 서버 부담 줄이기
				if (content.length() > 100000) {
					System.out.println("[게시글 수정 처리 로그] 실패: 내용 너무 김 len=" + content.length());

					request.setAttribute("msg", "내용이 너무 깁니다.");
					request.setAttribute("location", "boardEditPage.do?boardId=" + boardId);
					forward.setPath("message.jsp");
					forward.setRedirect(false);
					return forward;
				}
				// UPDATE 호출 준비
				boardDTO.setCondition("BOARD_UPDATE");
				boardDTO.setBoardId(boardId);
				boardDTO.setBoardTitle(title);
				boardDTO.setBoardContent(content);
				
				// 관리자라면 작성자 ID로 세팅해야 DAO UPDATE 조건을 만족함
				if (isAdmin) {
					boardDTO.setMemberId(boardData.getMemberId()); // 관리자 수정이지만 “작성자 ID”로 UPDATE 성공시키는 방식
				} else {
					boardDTO.setMemberId(loginMemberId); // 본인 수정
				}

				boolean result = boardDAO.update(boardDTO);

				if (!result) {
					System.out.println("[게시글 수정 처리 로그] 실패: UPDATE 실패 boardId=" + boardId);

					request.setAttribute("msg", "게시글 수정에 실패했습니다.");
					request.setAttribute("location", "boardDetail.do?boardId=" + boardId);
					forward.setPath("message.jsp");
					forward.setRedirect(false);
					return forward;
				}
				// 7) 성공 시 상세페이지로 redirect (새로고침 중복 제출 방지)

				System.out.println("[게시글 수정 처리 로그] 성공: boardId=" + boardId);

				forward.setPath("boardDetail.do?boardId=" + boardId);
				forward.setRedirect(true);
				return forward;
			}
	}