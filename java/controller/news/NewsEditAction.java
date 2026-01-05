package controller.news;

import java.io.File;
import java.nio.file.Paths;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import controller.common.Action;
import controller.common.ActionForward;
import model.dao.NewsDAO;
import model.dto.NewsDTO;

public class NewsEditAction implements Action {

    private static final String THUMB_DIR = "/upload/newsThumb";
    private static final String IMAGE_DIR = "/upload/newsImage";

    @Override
    public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
        ActionForward forward = new ActionForward();

        try {
            request.setCharacterEncoding("UTF-8");

            

            // ✅ 0) GET으로 들어오면 작성페이지로 돌려보냄
            String method = request.getMethod();
            System.out.println("[로그] NewsEditAction execute : method=" + method);
            
            // 0) 로그인 체크 (작성자 권한 체크를 DAO에서 할 거면 member_id 필요)
            // ✅ 1) 세션 체크
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("memberId") == null) {
               System.out.println("[로그] NewsEditAction execute : session null or memberId null");
               forward.setPath("loginPage.do");
               forward.setRedirect(true);
               return forward;
            }
       

            // 1) 선택한 입력값 모두 받기(V)
            // - news_id(필수), anime_id, news_title, news_content
            String newsIdStr = request.getParameter("newsId");
            String animeIdStr = request.getParameter("animeId");
            String newsTitle = request.getParameter("newsTitle");
            String newsContent = request.getParameter("newsContent");

            
            if (newsIdStr == null || newsIdStr.trim().isEmpty()) {
                request.setAttribute("errorMsg", "잘못된 요청입니다. (news_id 누락)");
                forward.setPath("error.jsp");
                forward.setRedirect(false);
                return forward;
            }
            int newsId = Integer.parseInt(newsIdStr);

            
            if (animeIdStr == null || animeIdStr.trim().isEmpty()) {
                request.setAttribute("errorMsg", "관련 애니가 선택되지 않았습니다. (anime_id 누락)");
                forward.setPath("newsEdit.jsp?news_id=" + newsId); // 수정페이지로
                forward.setRedirect(false);
                return forward;
            }
            
            int animeId = Integer.parseInt(animeIdStr);

            
            if (newsTitle == null || newsTitle.trim().isEmpty()) {
                request.setAttribute("errorMsg", "뉴스 제목을 입력해주세요.");
                forward.setPath("newsEdit.jsp?newsId=" + newsId);
                forward.setRedirect(false);
                return forward;
            }

            
            if (newsContent == null || newsContent.trim().isEmpty()) {
                request.setAttribute("errorMsg", "뉴스 내용을 입력해주세요.");
                forward.setPath("newsEdit.jsp?newsId=" + newsId);
                forward.setRedirect(false);
                return forward;
            }

            // 2) 기존 이미지 URL(새 파일이 없으면 유지)
            String existingThumbUrl = request.getParameter("existingThumbUrl");
            String existingImageUrl = request.getParameter("existingImageUrl");

            String newsThumbnailUrl = existingThumbUrl;
            String newsImageUrl = existingImageUrl;

            // 3) 썸네일 업로드(선택: 새 파일 있으면 교체, 없으면 유지)
            Part thumbPart = request.getPart("thumbFile");
            if (thumbPart != null && thumbPart.getSize() > 0) {
                newsThumbnailUrl = savePartAndReturnUrl(request, thumbPart, THUMB_DIR);
            }

            // 4) 본문 대표 이미지 업로드(선택: 새 파일 있으면 교체, 없으면 유지)
            Part newsImgPart = request.getPart("newsImageFile");
            if (newsImgPart != null && newsImgPart.getSize() > 0) {
                newsImageUrl = savePartAndReturnUrl(request, newsImgPart, IMAGE_DIR);
            }
            

            // 5) 뉴스 업데이트 호출(M) + 컨디션 담기(업데이트 뉴스)
            NewsDTO newsDTO = new NewsDTO();
            newsDTO.setNewsId(newsId);
            newsDTO.setAnimeId(animeId);        
            newsDTO.setNewsTitle(newsTitle);
            newsDTO.setNewsContent(newsContent); // HTML 저장
            newsDTO.setNewsImageUrl(newsImageUrl); // null 가능
            newsDTO.setNewsThumbnailUrl(newsThumbnailUrl);
            newsDTO.setCondition("NEWS_UPDATE");

            
            NewsDAO newsDAO = new NewsDAO();
            boolean newsUpdate = newsDAO.update(newsDTO);

            
            if (!newsUpdate) {
                request.setAttribute("errorMsg", "수정에 실패했습니다. (권한 없음/대상 없음/이미 삭제됨)");
                forward.setPath("newsDetail.do?newsId=" + newsId);
                forward.setRedirect(false);
                return forward;
            }
            
            // 6) 성공 → 방금 수정한 뉴스PK 기준 상세로 이동
            forward.setPath("newsDetail.do?newsId=" + newsId);
            forward.setRedirect(true);
            return forward;

            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMsg", "뉴스 수정 처리 중 오류가 발생했습니다.");
            forward.setPath("error.jsp");
            forward.setRedirect(false);
            return forward;
        }
    }
    
    

    private String savePartAndReturnUrl(HttpServletRequest request, Part part, String webDir) throws Exception {
        String submitted = Paths.get(part.getSubmittedFileName()).getFileName().toString();
        String ext = "";
        int dot = submitted.lastIndexOf('.');
        if (dot != -1) ext = submitted.substring(dot);

        
        String realDir = request.getServletContext().getRealPath(webDir);
        File dir = new File(realDir);
        if (!dir.exists()) dir.mkdirs();

        
        String savedName = UUID.randomUUID().toString().replace("-", "") + ext;
        File savedFile = new File(dir, savedName);
        part.write(savedFile.getAbsolutePath());

        return request.getContextPath() + webDir + "/" + savedName;
    }
}