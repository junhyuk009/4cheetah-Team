package controller.anime;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import controller.common.Action;
import controller.common.ActionForward;
import model.dao.AnimeDAO;
import model.dto.AnimeDTO;


public class AnimeDeleteAction implements Action {

    @Override
    public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
        ActionForward forward = new ActionForward();

        // 0) 로그인 체크 (서버에서 member_id 신뢰성 확보)
        // 1) 세션 체크
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("memberId") == null) {
           System.out.println("[로그] NewsWriteAction execute : session null or memberId null");
           forward.setPath("loginPage.do");
           forward.setRedirect(true);
           return forward;
        }
        // 1) 애니 PK 받기 (V)
        String animeIdStr = request.getParameter("animeId");
        if (animeIdStr == null || animeIdStr.trim().isEmpty()) {
           System.out.println("[로그] 애니 아이디가 존재하지 않습니다.");
            request.setAttribute("errorMsg", "잘못된 요청입니다. (anime_id 누락)");
            forward.setPath("error.jsp"); // 프로젝트 에러 페이지로 교체
            forward.setRedirect(false);
            return forward;
        }

        int animeId;
        try {
            animeId = Integer.parseInt(animeIdStr);
        } catch (NumberFormatException e) {
           System.out.println("[로그] 애니 아이디의 형식이 올바르지 않습니다.");
            request.setAttribute("errorMsg", "잘못된 요청입니다. (anime_id 형식 오류)");
            forward.setPath("error.jsp");
            forward.setRedirect(false);
            return forward;
        }

        // 2) 유저 ID (member_id) - 세션 기준
       

        // 3) 컨디션 담기 (딜리트 애니)
        String condition = "ANIME_DELETE";

        AnimeDTO dto = new AnimeDTO();
      
        dto.setAnimeId(animeId);     // DTO 필드명에 맞게 수정
      
        dto.setCondition(condition);  // DTO 필드명에 맞게 수정

        // 4) 삭제 메서드 호출 (M)
        AnimeDAO dao = new AnimeDAO();
        boolean AnimeDelete = dao.delete(dto);

        
        if (AnimeDelete) {
            // 삭제 성공 → 목록으로 리다이렉트
           System.out.println("[로그] 애니 삭제 성공");
            forward.setPath("animeList.do"); // 실제 목록 url로 교체
            forward.setRedirect(true);
        } else {
            // 소유자 불일치 / 이미 삭제됨 / 대상 없음 등
            request.setAttribute("errorMsg", "삭제에 실패했습니다. (권한 없음 또는 대상 없음)");
            forward.setPath("animeDetail.do?anime_id=" + animeId); // 또는 error.jsp
            forward.setRedirect(false);
        }

        return forward;
    }
}
