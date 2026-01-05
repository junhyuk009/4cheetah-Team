package controller.animepage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import controller.common.Action;
import controller.common.ActionForward;
import model.dao.AnimeDAO;
import model.dto.AnimeDTO;

public class AnimeEditPageAction implements Action {
	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
		ActionForward forward = new ActionForward();
		AnimeDAO animeDAO = new AnimeDAO();
		AnimeDTO animeDTO = new AnimeDTO();	
		
		//1. 로그인 여부 확인 - 사용자가 있는가
		//2. 애니 PK 파라미터 유효성 검사 - 요청 자체가 유효한가
		//3. 관리자 권한 확인 - 접근 자체가 가능한가(관리자는 데이터 호출보다 중요)
		//4. 해당 PK로 애니 selectOne
		//5. 데이터 유효성 검사 - 실제 수정 대상이 존재하는가
		//6. 데이터 담아서 수정 페이지로 이동
		
		//1. 로그인 체크
		HttpSession session = request.getSession(false);
		//이미 세션이 있으면 가져오고, 없으면 새로 만들지 마라		
		if (session == null || session.getAttribute("memberId") == null) {
			System.out.println("[애니 수정 이동 로그] 세션 없음 또는 memberId 유효하지 않음");
			request.setAttribute("msg", "잘못된 접근입니다.");
			request.setAttribute("location", "mainPage.do");
			forward.setPath("message.jsp");
			forward.setRedirect(false);
			return forward;
		}
		
		//검사 통과 시 role담기
		String memberRole = (String)session.getAttribute("memberRole");
		System.out.println("[애니 수정 이동 로그] 로그인 유저 ROLE : memberRole ["+memberRole+"]");
		
		//2. 애니 PK(animeId) 파라미터 검증
		String animeIdCheck = request.getParameter("animeId");
		System.out.println("[애니 수정 이동 로그] 애니 PK 검사 : animeIdCheck ["+animeIdCheck+"]");
		if (animeIdCheck == null) {
			request.setAttribute("msg", "잘못된 접근입니다.");
			request.setAttribute("location", "mainPage.do");
			forward.setPath("message.jsp");
			forward.setRedirect(false);
			return forward;
		}

		int animeId;
		try {
			animeId = Integer.parseInt(animeIdCheck);
		} catch (NumberFormatException e) {
			System.out.println("[애니 수정 이동 로그] 애니 PK 검사 : animeId 정수 변환 오류");
			request.setAttribute("msg", "애니 번호가 올바르지 않습니다.");
			request.setAttribute("location", "animeList.do");
			forward.setPath("message.jsp");
			forward.setRedirect(false);
			return forward;
		}		
		if (animeId <= 0) {//애니id값이 0이거나 음수면 -> 메인페이지로 이동
			System.out.println("[애니 수정 이동 로그] 애니 PK 검사 : animeId 0이하 오류");
			request.setAttribute("msg", "잘못된 애니 접근입니다...");
			request.setAttribute("location", "mainPage.do");
			forward.setPath("message.jsp");
			forward.setRedirect(false);			
		    return forward;
		}			

		// 3. 관리자 권한 확인
		if (!"ADMIN".equals(memberRole)) {
			System.out.println("[애니 수정 이동 로그] 관리자 권한 없음 : ["+memberRole+"]");
			request.setAttribute("msg", "관리자만 접근할 수 있습니다.");
			request.setAttribute("location", "mainPage.do");
			forward.setPath("message.jsp");
			forward.setRedirect(false);
			return forward;
		}
		
		animeDTO.setAnimeId(animeId);
		animeDTO.setCondition("ANIME_DETAIL");
		AnimeDTO animeData = animeDAO.selectOne(animeDTO);
		System.out.println("[애니 수정 이동 로그] 애니 조회 완료 : animeData = ["+animeData+"]");
		
		// 5. 데이터 유효성 검사
		if (animeData == null) {
			request.setAttribute("msg", "존재하지 않는 애니입니다.");
			request.setAttribute("location", "animeList.do");
			forward.setPath("message.jsp");
			forward.setRedirect(false);
			return forward;
		}

		// 6. 수정 페이지로 이동 (데이터 세팅)
		request.setAttribute("animeData", animeData);
		forward.setPath("animeedit.jsp");
		forward.setRedirect(false);
		return forward;
	}
}
