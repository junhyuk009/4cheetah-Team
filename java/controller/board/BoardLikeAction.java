package controller.board;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import controller.common.Action;
import controller.common.ActionForward;
import model.dao.BoardLikeDAO;
import model.dto.BoardLikeDTO;

public class BoardLikeAction implements Action {
    @Override
    public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");

        Gson gson = new Gson();
        Map<String, Object> res = new HashMap<>();

        try {
            // 1) 로그인 체크 (당신 프로젝트는 session에 memberId를 쓰고 있음)
            HttpSession session = request.getSession(false);
            Integer memberId = (session == null) ? null : (Integer) session.getAttribute("memberId");

            if (memberId == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                res.put("ok", false);
                res.put("message", "로그인 후 이용 가능합니다.");
                response.getWriter().print(gson.toJson(res));
                return null;
            }

            // 2) boardId 검증
            String boardIdStr = request.getParameter("boardId");
            boardIdStr = (boardIdStr == null) ? "" : boardIdStr.trim();

            if (boardIdStr.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                res.put("ok", false);
                res.put("message", "boardId가 비었습니다.");
                response.getWriter().print(gson.toJson(res));
                return null;
            }

            int boardId;
            try {
                boardId = Integer.parseInt(boardIdStr);
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                res.put("ok", false);
                res.put("message", "boardId가 숫자가 아닙니다.");
                response.getWriter().print(gson.toJson(res));
                return null;
            }

            if (boardId <= 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                res.put("ok", false);
                res.put("message", "boardId는 1 이상이어야 합니다.");
                response.getWriter().print(gson.toJson(res));
                return null;
            }

            // 3) 토글 실행
            BoardLikeDAO boardlikeDAO = new BoardLikeDAO();
            BoardLikeDTO boardlikeDTO = new BoardLikeDTO();
            boardlikeDTO.setBoardId(boardId);
            boardlikeDTO.setMemberId(memberId);
            boardlikeDTO.setCondition("BOARD_LIKE_TOGGLE");

            boolean isliked = boardlikeDAO.update(boardlikeDTO);
            if (!isliked) {
                res.put("ok", false);
                res.put("message", "좋아요 처리에 실패했습니다.");
                response.getWriter().print(gson.toJson(res));
                return null;
            }

            // 4) 최신 좋아요 개수 조회
            boardlikeDTO = new BoardLikeDTO();
            boardlikeDTO.setBoardId(boardId);
            boardlikeDTO.setCondition("BOARD_LIKE_COUNT");
            boardlikeDTO = boardlikeDAO.selectOne(boardlikeDTO);

            int likeCount = (boardlikeDTO == null) ? 0 : boardlikeDTO.getLikeCnt();

            // 5) JS가 기대하는 키로 반환
            res.put("ok", true);
            res.put("liked", boardlikeDTO.getIsLiked() == 1);
            res.put("likeCount", likeCount);

            response.getWriter().print(gson.toJson(res));
            return null;

        } catch (IOException e) {
            e.printStackTrace();
            try {
                res.put("ok", false);
                res.put("message", "서버 응답 처리 중 오류가 발생했습니다.");
                response.getWriter().print(gson.toJson(res));
            } catch (IOException ignored) {}
            return null;
        }
    }
}
