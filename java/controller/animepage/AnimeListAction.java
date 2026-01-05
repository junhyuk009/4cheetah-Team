package controller.animepage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import controller.common.Action;
import controller.common.ActionForward;

public class AnimeListAction implements Action {
   @Override
   public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
	  request.setAttribute("activeMenu", "ANILIST"); //메뉴 활성화용
      ActionForward forward = new ActionForward();


      /*
       * [역할] - 비동기 페이지네이션 적용 후, 이 Action은 DB 조회를 하지 않는다. - 대신 "검색 조건(컨디션) + 키워드"만
       * 정리해서 anime.jsp로 넘긴다. - 실제 목록 데이터는 anime.jsp의 JS가 AnimeListDataAction(JSON)을
       * 호출해서 받아온다.
       */

      // 1) 검색 분기값은 condition으로 받음
      String condition = request.getParameter("condition");
      String keyword = request.getParameter("keyword");
      System.out.println("[애니리스트 이동 로그] condition : [" + condition + "]");
      System.out.println("[애니리스트 이동 로그] keyword : [" + keyword + "]");

      // 애니 검색(제목) ANIME_SEARCH_TITLE
      // 애니 검색(줄거리) ANIME_SEARCH_STORY
      // 그 외 값은 기본 목록으로 보정
      if (!"ANIME_SEARCH_TITLE".equals(condition) && !"ANIME_SEARCH_STORY".equals(condition)) {
         condition = "ANIME_LIST_RECENT";
      }

      // 2) 검색인지 여부 판단 (보정된 condition 기준)
      boolean isSearch = "ANIME_SEARCH_TITLE".equals(condition) || "ANIME_SEARCH_STORY".equals(condition);
      System.out.println("[애니리스트 이동 로그] isSearch : [" + isSearch + "]");

      // 3) keyword 정리
      if (keyword != null) {
         keyword = keyword.trim();
      }

      // 4) 검색인데 keyword가 null/빈값이면 message.jsp로 차단
      if (isSearch && (keyword == null || keyword.isEmpty())) {
         request.setAttribute("msg", "검색어가 없습니다.");
         request.setAttribute("location", "animeList.do"); // 돌아갈 곳
         forward.setPath("message.jsp");
         forward.setRedirect(false);
         return forward;
      }

      // 5) 검색이 아닌 경우(기본목록) 빈 문자열이면 null로 정리(선택)
      if (!isSearch && keyword != null && keyword.isEmpty()) {
         keyword = null;
      }

      // 6) JSP에서 폼 상태 유지 + JS가 비동기 호출에 그대로 쓰게 전달
      System.out.println("[애니리스트 이동 로그] 최종 condition : [" + condition + "]");
      System.out.println("[애니리스트 이동 로그] 최종 keyword : [" + keyword + "]");
      request.setAttribute("condition", condition);
      request.setAttribute("keyword", keyword);

      forward.setPath("anime.jsp");
      forward.setRedirect(false);
      return forward;
   }
}