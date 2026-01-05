package controller.anime;

import java.io.File;
import java.nio.file.Paths;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import controller.common.Action;
import controller.common.ActionForward;
import model.dao.AnimeDAO;
import model.dto.AnimeDTO;

public class AnimeWriteAction implements Action {

  @Override
  public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
   System.out.println("[애니 추가 액션] start");
    ActionForward forward = new ActionForward();
    
    
    try {
      // 1) 입력값 받기(V → C)
      
      String animeTitle    = request.getParameter("anime_title");
      String originalTitle = request.getParameter("original_title");
      String yearStr       = request.getParameter("anime_year");
      String quarterStr    = request.getParameter("anime_quarter");
      String animeStory    = request.getParameter("anime_story");

     
      int animeYear = Integer.parseInt(yearStr);
      String animeQuarter = (quarterStr == null) ? "" : quarterStr.trim();
      if (animeQuarter.isEmpty()) throw new IllegalArgumentException("quarter");
      
      
      
      // 2) 파일 파트
      Part thumbPart = request.getPart("thumbFile");
      if (thumbPart == null || thumbPart.getSize() == 0) {
        request.setAttribute("errorMsg", "썸네일 이미지를 선택해주세요.");
        forward.setPath("animewrite.jsp");
        forward.setRedirect(false);
        return forward;
      }

      // 3) 확장자 검증
      String submitted = Paths.get(thumbPart.getSubmittedFileName()).getFileName().toString();
      String ext = "";
      int dot = submitted.lastIndexOf(".");
      if (dot > -1) ext = submitted.substring(dot).toLowerCase();

      
      if (!(ext.equals(".jpg") || ext.equals(".jpeg") || ext.equals(".png") || ext.equals(".webp"))) {
        request.setAttribute("errorMsg", "이미지 파일(jpg, jpeg, png, webp)만 업로드 가능합니다.");
        forward.setPath("animewrite.jsp");
        forward.setRedirect(false);
        return forward;
      }

      
      // 4) 저장 파일명 생성
      String savedName = UUID.randomUUID().toString().replace("-", "") + ext;

      // 5) 저장 경로
      String uploadDirPath = request.getServletContext().getRealPath("/img/anime");
      File uploadDir = new File(uploadDirPath);
      if (!uploadDir.exists()) uploadDir.mkdirs();

      File savedFile = new File(uploadDir, savedName);
      thumbPart.write(savedFile.getAbsolutePath());

      // 6) DB에 저장할 URL(상대경로 저장 권장)
      String thumbnailUrl = "/img/anime/" + savedName;

      
      
      // 7) DTO + 컨디션 세팅
      AnimeDTO dto = new AnimeDTO();
      dto.setAnimeTitle(animeTitle);
      dto.setOriginalTitle(originalTitle);
      dto.setAnimeYear(animeYear);
      dto.setAnimeQuarter(animeQuarter);
      dto.setAnimeStory(animeStory);
      dto.setAnimeThumbnailUrl(thumbnailUrl);

      dto.setCondition("ANIME_INSERT"); // (추가_신규애니)

      // 8) INSERT 수행 후 “방금 추가된 PK” 확보
      AnimeDAO dao = new AnimeDAO();
      boolean ok = dao.insert(dto); // PK 리턴
      
      if (!ok) {
         request.setAttribute("errorMsg", "애니 등록에 실패했습니다.");
         forward.setPath("animeWritePage.do"); // JSP 직접 말고 페이지 액션 권장
         forward.setRedirect(false);
         return forward;
       }

       // 새 PK를 모르면 상세로 못 감 → 목록/관리자 페이지로 이동
       forward.setRedirect(true);
       forward.setPath("animeList.do");
       return forward;

      
    } catch (NumberFormatException e) {
      request.setAttribute("errorMsg", "연도/분기 값이 올바르지 않습니다.");
      forward.setPath("animewrite.jsp");
      forward.setRedirect(false);
      return forward;

      
    } catch (Exception e) {
      // getPart/IO/SQL 등 포함
      request.setAttribute("errorMsg", "처리 중 오류가 발생했습니다: " + e.getMessage());
      forward.setPath("animewrite.jsp");
      forward.setRedirect(false);
      return forward;
    }
  }
}
