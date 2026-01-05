package controller.boardpage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import controller.common.Action;
import controller.common.ActionForward;

/*
글 작성 페이지로 이동하는 Action (공용)
- write.jsp 하나를 공용으로 사용
- type(BOARD/NEWS)와 (BOARD일 때) category를 파라미터로 전달

URL 예시
- 게시글: /boardWritePage.do?type=BOARD&category=ANIME
- 뉴스  : /boardWritePage.do?type=NEWS
*/
public class BoardWritePageAction implements Action {

    @Override
    public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
        ActionForward forward = new ActionForward();
        String type = request.getParameter("type");
		System.out.println("[글쓰기 이동 로그] 타입 검사: ["+type+"]");
        
		// 로그인 체크하기
		// 게시글 작성은 무조건 회원 기능이라서 memeberId가 없으면 INSERT 자체가 안됨.
		HttpSession session = request.getSession(false);
		// 세션이 없으면 새로 만들지 말고 null 리턴
		if (session == null || session.getAttribute("memberId") == null) {
			// 세션이 없거나, 세션이 memberId가 없으면 = 로그인 X
			System.out.println("[글쓰기 이동 로그] 실패: 로그인 세션 없음");

			request.setAttribute("msg", "로그인이 필요한 기능입니다.");
			request.setAttribute("location", "mainPage.do"); // 프로젝트 메인으로 이동
			forward.setPath("message.jsp");
			forward.setRedirect(false); // forward로 메시지 전달(리퀘스트 유지)
			return forward;
		}
        
        
        // 1) type 수신 + 정리
        if (type == null || type.trim().isEmpty()) {
            type = "BOARD"; // 기본값(원하면 message.jsp로 차단 정책도 가능)
        }
        type = type.trim().toUpperCase();
        System.out.println("[글쓰기 이동 로그] type=[" + type + "]");

        boolean isBoard = "BOARD".equals(type);
        boolean isNews  = "NEWS".equals(type);

        // 2) type 화이트리스트 검증
        if (!isBoard && !isNews) {
            System.out.println("[글쓰기 이동 로그] type 검증 실패 → message.jsp");
            request.setAttribute("msg", "잘못된 글쓰기 접근입니다.");
            request.setAttribute("location", "mainPage.do");
            forward.setPath("message.jsp");
            forward.setRedirect(false);
            return forward;
        }

        // 3) type별 파라미터 구성
        String path;

        // 게시글이면 category 필요
        if (isBoard) {
            String category = request.getParameter("category");
            System.out.println("[글쓰기 이동 로그] category(raw)=[" + category + "]");

            if (category == null || category.trim().isEmpty()) {
                category = "ANIME"; // 기본 게시판
                System.out.println("[글쓰기 이동 로그] category 없음 → 기본값 적용 [" + category + "]");
            }

            category = category.trim().toUpperCase();

            path = "write.jsp?type=BOARD&category=" + category;
        }
        // 뉴스면 category 없이 type만
        else {
            path = "write.jsp?type=NEWS";
        }

        System.out.println("[글쓰기 이동 로그] 이동 경로 : path=[" + path + "]");

        // 4) 단순 페이지 이동이므로 redirect
        forward.setPath(path);
        forward.setRedirect(true);
        return forward;
    }
}