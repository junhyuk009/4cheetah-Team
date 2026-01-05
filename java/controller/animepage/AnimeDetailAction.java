package controller.animepage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import controller.common.Action;
import controller.common.ActionForward;
import model.dao.AnimeDAO;
import model.dto.AnimeDTO;

public class AnimeDetailAction implements Action {

   @Override
   public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
      request.setAttribute("activeMenu", "ANILIST");
      ActionForward forward = new ActionForward();
      AnimeDAO animeDAO = new AnimeDAO();
      AnimeDTO animeDTO = new AnimeDTO();
      /*
      1. animeId 파라미터 검증
      2. 애니 selectOne
      3. animedetail.jsp 이동
       */

      //1. id 파라미터 유효성 검사
      String animeIdCheck = request.getParameter("animeId");
      //브라우저에서 넘어온 애니id값이 문자열이기 때문에 String에 대입 후 검사

      if (animeIdCheck == null || animeIdCheck.isEmpty()) {
         //애니id값이 null이거나 비었다면 오류 -> 메인페이지로 이동
         System.out.println("[애니 상세보기 로그] 조회 실패 : animeId 유효하지 않음");
         request.setAttribute("msg", "잘못된 애니 선택입니다...");
         request.setAttribute("location", "mainPage.do");
         forward.setPath("message.jsp");
         forward.setRedirect(false);         
         return forward;
      }

      int animeId;
      try {//유효성 검사 통과 시 파라미터 정수로 변환
         animeId = Integer.parseInt(animeIdCheck);
      } catch (NumberFormatException e) {
         System.out.println("[애니 상세보기 로그] 조회 실패 : animeId 정수 변환 오류");
         request.setAttribute("msg", "잘못된 애니 선택입니다...");
         request.setAttribute("location", "mainPage.do");
         forward.setPath("message.jsp");
         forward.setRedirect(false);         
         return forward;
      }

      if (animeId <= 0) {//애니id값이 0이거나 음수면 -> 메인페이지로 이동
         System.out.println("[애니 상세보기 로그] 조회 실패 : animeId 0이하 오류");
         request.setAttribute("msg", "잘못된 애니 선택입니다...");
         request.setAttribute("location", "mainPage.do");
         forward.setPath("message.jsp");
         forward.setRedirect(false);         
         return forward;
      }   
      
      
      
      // 2. 애니 selectOne       
      animeDTO.setAnimeId(animeId);
      animeDTO.setCondition("ANIME_DETAIL");       
      System.out.println("[애니 상세 로그] param animeId = [" + animeId + "]");
      AnimeDTO animeData = animeDAO.selectOne(animeDTO);   
      System.out.println("[애니 상세 로그] selectOne result = " + animeData);
      //애니 존재여부 검사
      //셀렉트원 유효성 검사 - url조작을 통한 없는 게시글 확인 방어
      if (animeData == null) {
         System.out.println("[애니 상세보기 로그] 조회 실패 : 해당 애니는 없는 애니");
         request.setAttribute("msg", "잘못된 애니 선택입니다...");
         request.setAttribute("location", "mainPage.do");
         forward.setPath("message.jsp");
         forward.setRedirect(false);
         return forward;
      }
      
      // 썸네일 경로 보정
      String thumb = animeData.getAnimeThumbnailUrl();
      if (thumb != null && !thumb.startsWith("/")) {
          animeData.setAnimeThumbnailUrl("/" + thumb);
      }

      
  
      System.out.println("[애니 상세보기 로그] 조회 완료 : animeData = ["+animeData+"]");
      
      // 3. 결과 담아서 페이지 전달
      request.setAttribute("animeData", animeData);
      forward.setPath("animedetail.jsp");
      forward.setRedirect(false);      
      return forward;      
   }
}