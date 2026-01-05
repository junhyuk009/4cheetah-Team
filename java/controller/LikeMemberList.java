package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import model.dao.BoardDAO;
import model.dto.BoardDTO;

@WebServlet("/LikeMemberList")
public class LikeMemberList extends HttpServlet {
   private static final long serialVersionUID = 1L;

   public LikeMemberList() {
      super();
   }

   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response)
         throws ServletException, IOException {

      System.out.println("[좋아요 멤버 목록 서블릿 로그] GET 요청");

      // 1) JSON 응답 세팅
      response.setCharacterEncoding("UTF-8");
      response.setContentType("application/json; charset=UTF-8");
      Gson gson = new Gson();

   
      // 3) boardId 파라미터 수신/검증
      String boardIdStr = request.getParameter("boardId");
      boardIdStr = (boardIdStr == null) ? "" : boardIdStr.trim();

      if (boardIdStr.isEmpty()) {
         Map<String, Object> res = new HashMap<>();
         res.put("ok", false);
         res.put("message", "글 정보가 필요합니다.");
         res.put("users", new ArrayList<>());
         response.getWriter().print(gson.toJson(res));
         return;
      }

      int boardId;
      try {
         boardId = Integer.parseInt(boardIdStr);
      } catch (NumberFormatException e) {
         Map<String, Object> res = new HashMap<>();
         res.put("ok", false);
         res.put("message", "글 형식이 올바르지 않습니다.");
         res.put("users", new ArrayList<>());
         response.getWriter().print(gson.toJson(res));
         return;
      }

      try {
         
         BoardDAO boardDAO = new BoardDAO();
         BoardDTO boardDTO = new BoardDTO();
         boardDTO.setBoardId(boardId);
         boardDTO.setCondition("BOARD_LIKE_MEMBER_LIST");

      

         // DAO에서 MEMBER JOIN해서 memberNickname까지 채워서 List<BoardLikeDTO>로 반환해야 함
         
         List<BoardDTO> likeMembers = boardDAO.selectAll(boardDTO);
         if (likeMembers == null)
            likeMembers = new ArrayList<>();

            // 3) JS 호환 키로 변환: memberId, memberNickname
            List<Map<String, Object>> users = new ArrayList<>();
            for (BoardDTO d : likeMembers) {
                Map<String, Object> u = new HashMap<>();
                u.put("memberNickname", d.getLikeMemberNickname()); // 여기서 키 변환
                users.add(u);
            }

            Map<String, Object> res = new HashMap<>();
            res.put("ok", true);
            res.put("users", users);

            response.getWriter().print(gson.toJson(res));
            System.out.println(gson.toJson(res));
            System.out.println("[좋아요 멤버 목록 서블릿 로그] 조회완료 : boardId=[" + boardId + "], count=[" + users.size() + "]");

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> res = new HashMap<>();
            res.put("ok", false);
            res.put("message", "목록 조회 중 서버 오류가 발생했습니다.");
            res.put("users", new ArrayList<>());
            response.getWriter().print(gson.toJson(res));
          
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("[좋아요 멤버 목록 서블릿 로그] POST 요청 : doGet 실행");
        doGet(request, response);
    }
}
