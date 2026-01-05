package controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import model.dao.BoardDAO;          // ★ board 존재 검증용 (프로젝트에 맞게 import)
import model.dao.BoardLikeDAO;
import model.dto.BoardDTO;          // ★ board 존재 검증용 (프로젝트에 맞게 import)
import model.dto.BoardLikeDTO;

@WebServlet("/BoardLikeToggle.do")
public class BoardLikeToggle extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("[좋아요 토글 서블릿] AJAX 요청 도착 (POST /BoardLikeToggle.do)");

        // =========================================================
        // (응답 준비)
        // 이 서블릿은 "페이지 이동"이 목적이 아니라,
        // 좋아요 처리 결과를 JSON으로 돌려줘서 프론트(UI)가 즉시 갱신하도록 돕는다.
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");

        Gson gson = new Gson();
        Map<String, Object> result = new HashMap<>();

        // =========================================================
        // 1) 로그인 체크 (누가 눌렀는지 확인)
        // - 좋아요는 "회원이 특정 글에 누른 기록"이므로 memberId가 필수다.
        // - memberId는 클라이언트에서 받지 않고(조작 위험),
        //   반드시 서버 세션에서 꺼내서 신뢰할 수 있게 만든다.
        // - 비로그인이면 DB 작업 자체가 불가능하므로 즉시 401 반환한다.
        HttpSession session = request.getSession(false);
        Integer memberId = (session == null) ? null : (Integer) session.getAttribute("memberId");

        if (memberId == null) {
            System.out.println("[좋아요 토글 서블릿] 로그인 없음 -> 401 반환");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401

            result.put("result", "FAIL");
            result.put("msg", "LOGIN_REQUIRED");
            response.getWriter().write(gson.toJson(result));
            return;
        }

        // =========================================================
        // 2) boardId 검증 (어느 글에 눌렀는지 확인)
        // - 좋아요는 대상 글(boardId)이 없으면 처리할 수 없다.
        // - 숫자 파싱 실패, 누락 등 비정상 요청은 400으로 종료한다.
        String boardIdParam = request.getParameter("boardId");
        int boardId;

        try {
            boardId = Integer.parseInt(boardIdParam);
        } catch (Exception e) {
            System.out.println("[좋아요 토글 서블릿] boardId 파싱 실패: boardIdParam=[" + boardIdParam + "] -> 400 반환");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400

            result.put("result", "FAIL");
            result.put("msg", "INVALID_BOARD_ID");
            response.getWriter().write(gson.toJson(result));
            return;
        }

        System.out.println("[좋아요 토글 서블릿] 요청 정보 확인 memberId=[" + memberId + "], boardId=[" + boardId + "]");

        // =========================================================
        // 3) boardId 존재 검증 (정상 글인지 확인)
        // - 없는 글에 좋아요 요청이 오면 UX/무결성이 깨진다.
        // - 따라서 토글 전에 board_id가 실제 존재하는지 selectOne으로 검증한다.
        BoardDAO boardDAO = new BoardDAO();
        BoardDTO boardDTO = new BoardDTO();

        // DB에 실제로 있는지 검증
        boardDTO.setCondition("BOARD_EXISTS");
        boardDTO.setBoardId(boardId);

        BoardDTO boardData = boardDAO.selectOne(boardDTO);

        if (boardData == null) {
            System.out.println("[좋아요 토글 서블릿] 게시글 없음 -> 404 반환 boardId=[" + boardId + "]");
            response.setStatus(HttpServletResponse.SC_NOT_FOUND); // 404

            result.put("result", "FAIL");
            result.put("msg", "BOARD_NOT_FOUND");
            response.getWriter().write(gson.toJson(result));
            return;
        }

        System.out.println("[좋아요 토글 서블릿] 게시글 존재 확인 완료 : boardId=[" + boardId + "]");

        // =========================================================
        // 4) 좋아요 토글 실행 (상태 변경)
        // 토글(toggle)이란:
        // - 지금 좋아요가 "없으면" INSERT -> 좋아요 ON
        // - 지금 좋아요가 "있으면" DELETE -> 좋아요 OFF(취소)
        //
        // 여기서 중요한 점:
        // - 먼저 DB 상태를 바꾸는 것이 1순위다.
        // - DAO가 성공하면 DTO.isLiked(0/1)에 "최종 상태"를 담아준다(DAO 설계 전제)        
        BoardLikeDAO boardLikeDAO = new BoardLikeDAO();
        BoardLikeDTO boardLikeDTO = new BoardLikeDTO();
        
        boardLikeDTO.setCondition("BOARD_LIKE_TOGGLE");
        boardLikeDTO.setBoardId(boardId);
        boardLikeDTO.setMemberId(memberId);

        boolean isUpdate = boardLikeDAO.update(boardLikeDTO);

        if (!isUpdate) {
            System.out.println("[좋아요 토글 서블릿] 토글(DB 상태 변경) 실패 -> 500 반환 (memberId=[" + memberId + "], boardId=[" + boardId + "])");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500

            result.put("result", "FAIL");
            result.put("msg", "TOGGLE_FAILED");
            response.getWriter().write(gson.toJson(result));
            return;
        }

        // 토글이 끝난 뒤 "내 최종 상태" (ON이면 1, OFF면 0)
        // - 이후 DTO를 COUNT용으로 새로 만들어도 isLiked는 이미 변수에 보관했으므로 안전함
        int isLiked = boardLikeDTO.getIsLiked();
        System.out.println("[좋아요 토글 서블릿] 토글 성공 isLiked=[" + isLiked + "] (memberId=[" + memberId + "], boardId=[" + boardId + "])");

        // =========================================================
        // 5) 토글 후 좋아요 개수 조회 (표시 갱신용)
        // - UI에는 "최신 좋아요 수"가 보여야 한다.
        // - 토글 반영 후 카운트를 보내야 숫자가 정확하게 맞는다.
        // - selectOne()은 결과를 '반환 DTO(countData)'로 주므로 반환값에서 likeCnt를 꺼낸다.
        boardLikeDTO = new BoardLikeDTO();
        boardLikeDTO.setCondition("BOARD_LIKE_COUNT");
        boardLikeDTO.setBoardId(boardId);

        BoardLikeDTO countData = boardLikeDAO.selectOne(boardLikeDTO);
        int likeCnt = (countData == null) ? 0 : countData.getLikeCnt();

        System.out.println("[좋아요 토글 서블릿] 카운트 조회 완료 likeCnt=[" + likeCnt + "] (boardId=[" + boardId + "])");

        // =========================================================
        // 6) JSON 응답 반환 (프론트 UI 갱신 재료)
        // jQuery.ajax success에서 이 값을 받아서:
        // - isLiked == 1 -> 하트 채움
        // - isLiked == 0 -> 하트 비움
        // - likeCnt -> 숫자 갱신
        // =========================================================
        result.put("result", "OK");
        result.put("isLiked", isLiked); // 0 or 1
        result.put("likeCnt", likeCnt);

        System.out.println("[좋아요 토글 서블릿] 응답 반환 OK -> " + gson.toJson(result));
        response.getWriter().write(gson.toJson(result));
    }
}
