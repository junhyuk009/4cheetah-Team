package controller.news;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import controller.common.Action;
import controller.common.ActionForward;
import model.dao.NewsDAO;
import model.dto.NewsDTO;

public class NewsDeleteAction implements Action {

    @Override
    public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
        ActionForward forward = new ActionForward();

        // 0) 로그인 체크
        HttpSession session = request.getSession(false);
        Integer memberId = (session != null) ? (Integer) session.getAttribute("memberId") : null;
        String memberRole = (session != null) ? (String) session.getAttribute("memberRole") : null;

        if (memberId == null) {
            forward.setPath("loginPage.do"); 
            forward.setRedirect(true);
            return forward;
        }

        if (!"ADMIN".equals(memberRole)) {
            request.setAttribute("msg", "관리자만 접근할 수 있습니다.");
            request.setAttribute("location", "mainPage.do");
            forward.setPath("message.jsp");
            forward.setRedirect(false);
            return forward;
        }
   
     
        
        // 1) 뉴스 PK 받기 (V)
        String newsIdStr = request.getParameter("newsId");
        if (newsIdStr == null || newsIdStr.trim().isEmpty()) {
            request.setAttribute("errorMsg", "잘못된 요청입니다. (news_id 누락)");
            forward.setPath("error.jsp");
            forward.setRedirect(false);
            return forward;
        }

        int newsId;
        try {
            newsId = Integer.parseInt(newsIdStr);
        } catch (NumberFormatException e) {
            request.setAttribute("errorMsg", "잘못된 요청입니다. (news_id 형식 오류)");
            forward.setPath("error.jsp");
            forward.setRedirect(false);
            return forward;
        }

        // 3) 뉴스 PK / 유저 ID / 컨디션 담기 
        NewsDTO newsDTO = new NewsDTO();
        newsDTO.setNewsId(newsId);
        newsDTO.setCondition("NEWS_DELETE");

        // 4) 삭제 메서드 호출 (M) 
        NewsDAO newsDAO = new NewsDAO();
        boolean deleteNews = newsDAO.delete(newsDTO);

        if (deleteNews) {
            // 성공 → 뉴스 목록으로
            forward.setPath("newsList.do"); // 뉴스 전체 페이지 URL로 교체
            forward.setRedirect(true);
        } else {
            // 실패 (권한 없음/대상 없음/이미 삭제됨 등)
            request.setAttribute("errorMsg", "삭제에 실패했습니다. (권한 없음 또는 대상 없음)");
            forward.setPath("newsDetail.do?news_id=" + newsId);
            forward.setRedirect(false);
        }

        return forward;
    }
}