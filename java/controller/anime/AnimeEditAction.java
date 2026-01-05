package controller.anime;

import java.io.File;
import java.nio.file.Paths;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import controller.common.Action;
import controller.common.ActionForward;
import model.dao.AnimeDAO;
import model.dto.AnimeDTO;


public class AnimeEditAction implements Action {

    @Override
    public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
        ActionForward forward = new ActionForward();

        try {
            // 0) 로그인 체크 (member_id는 세션 기준)
            // 0) 로그인 체크 (서버에서 member_id 신뢰성 확보)
            // 1) 세션 체크
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("memberId") == null) {
               System.out.println("[로그] NewsWriteAction execute : session null or memberId null");
               forward.setPath("loginPage.do");
               forward.setRedirect(true);
               return forward;
            }
        

            // 1) 선택한 애니id / 입력값 모두 받기(V)
            int animeId = Integer.parseInt(request.getParameter("anime_id"));

            String animeTitle = request.getParameter("anime_title");
            String originalTitle = request.getParameter("original_title");
            int animeYear = Integer.parseInt(request.getParameter("anime_year"));
            String animeQuarter = request.getParameter("anime_quarter");
            String animeStory = request.getParameter("anime_story");

            // 기존 썸네일 URL (새 파일이 없으면 이 값을 유지)
            String existingThumbUrl = request.getParameter("existingThumbUrl");

            // 2) 썸네일 업로드 처리 (선택 사항)
            //    - name="thumbFile" 로 파일 input이 들어온다고 가정
            Part thumbPart = request.getPart("thumbFile");
            String thumbnailUrl = existingThumbUrl;

            if (thumbPart != null && thumbPart.getSize() > 0) {
                // 확장자 추출
                String submitted = Paths.get(thumbPart.getSubmittedFileName()).getFileName().toString();
                String ext = "";
                int dot = submitted.lastIndexOf('.');
                if (dot != -1) ext = submitted.substring(dot);

                // 저장 경로: /upload/animeThumb
                String uploadDir = request.getServletContext().getRealPath("/upload/animeThumb");
                File dir = new File(uploadDir);
                if (!dir.exists()) dir.mkdirs();

                String savedName = UUID.randomUUID().toString().replace("-", "") + ext;
                File savedFile = new File(dir, savedName);
                thumbPart.write(savedFile.getAbsolutePath());

                thumbnailUrl = "/upload/animeThumb/" + savedName;
            }

            // 3) 입력받은 값 / 컨디션 담기 (업데이트 애니수정)
            AnimeDTO animeDTO = new AnimeDTO();
            animeDTO.setAnimeId(animeId);
          
            animeDTO.setAnimeTitle(animeTitle);
            animeDTO.setOriginalTitle(originalTitle);
            animeDTO.setAnimeYear(animeYear);
            animeDTO.setAnimeQuarter(animeQuarter);
            animeDTO.setAnimeStory(animeStory);
            animeDTO.setAnimeThumbnailUrl(thumbnailUrl);

            animeDTO.setCondition("ANIME_UPDATE"); // “업데이트 전용 컨디션”

            // 4) 애니 업데이트 호출(M)
            AnimeDAO dao = new AnimeDAO();
            boolean ok = dao.update(animeDTO);

            if (!ok) {
                // 권한 없음 / 대상 없음 / 이미 삭제 등
                request.setAttribute("errorMsg", "수정에 실패했습니다. (권한 없음 또는 대상 없음)");
                forward.setPath("animeDetail.do?animeId=" + animeId);
                forward.setRedirect(false);
                return forward;
            }

            // 5) 애니 셀릭트원 호출 (전용 컨디션 담기)
            animeDTO = new AnimeDTO();
            animeDTO.setAnimeId(animeId);
            animeDTO.setCondition("ANIME_SELECT_ONE");
            AnimeDTO updated = dao.selectOne(animeDTO);

            // “방금 수정된 애니의 PK 알려줘” → PK는 animeId 그대로
            request.setAttribute("anime", updated);

            // 6) 성공 → 방금 수정한 PK 기준 상세로 이동
            //    상세 페이지가 DAO로 다시 조회한다면 redirect가 더 단순
            forward.setPath("animeDetail.do?animeId=" + animeId);
            forward.setRedirect(true);
            return forward;

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMsg", "수정 처리 중 오류가 발생했습니다.");
            forward.setPath("error.jsp"); // 프로젝트 에러 페이지로 교체
            forward.setRedirect(false);
            return forward;
        }
    }
}

