package controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import model.dao.AnimeDAO;
import model.dto.AnimeDTO;

@WebServlet("/NewsAnimeSearch")
public class NewsAnimeSearch extends HttpServlet {
   private static final long serialVersionUID = 1L;

   public NewsAnimeSearch() {
      super();
   }

   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response)
         throws ServletException, IOException {
      System.out.println("[뉴스 연관애니 검색 서블릿 로그] GET 요청");
      // JSON 응답 세팅
      response.setCharacterEncoding("UTF-8");
      response.setContentType("application/json; charset=UTF-8");
      Gson gson = new Gson();
      AnimeDTO animeDTO = new AnimeDTO();
      AnimeDAO animeDAO = new AnimeDAO();
            
      // GET요청이므로 관리자 권한 체크 필요
      // 로그인 유무 우선 확인 - 세션 널체크
      HttpSession session = request.getSession(false);
      if (session == null) {
          response.getWriter().print("[]");
          return;
      }
      
      // 로그인 유저 권한 확인 - 관리자 전용 기능
      String memberRole = (String)session.getAttribute("memberRole");
      if (!"ADMIN".equals(memberRole)) {
          response.getWriter().print("[]");
          return;
      }

      // 1) 파라미터 수신
      String keyword = request.getParameter("keyword");
      if (keyword == null) {
          keyword = "";
      }
      keyword = keyword.trim();

      // 2) 검색어 검증 (빈 값이면 그냥 빈 배열)
      if (keyword.length() < 1) {
         response.getWriter().print("[]");
         return;
      }

      // 3) DTO + DAO 호출 
      animeDTO.setCondition("ANIME_SEARCH_TITLE");
      animeDTO.setKeyword(keyword); // 이걸로 바꿔야 DAO가 검색한다
      List<AnimeDTO> animeList = animeDAO.selectAll(animeDTO);
      System.out.println("[뉴스 연관애니 검색 서블릿 로그] 조회완료 : keyword = ["+keyword+"], count = ["+animeList.size()+"]");

      // 4) JSON 반환
        String json = gson.toJson(animeList);
        response.getWriter().print(json);
   }
   @Override
   protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      System.out.println("[뉴스 연관애니 검색 서블릿 로그] POST 요청 : doGet 실행");
      doGet(request, response);
   }
}