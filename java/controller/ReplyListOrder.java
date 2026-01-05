package controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import model.dao.ReplyDAO;
import model.dto.ReplyDTO;

@WebServlet("/ReplyListOrder")
public class ReplyListOrder extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public ReplyListOrder() {
        super();        
    }
	
    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("[댓글 정렬 서블릿 로그] GET 요청");
        //JSON 응답 세팅
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
        Gson gson = new Gson();
        ReplyDAO replyDAO = new ReplyDAO();
        ReplyDTO replyDTO = new ReplyDTO();
        
        //컨디션 비교용 변수 세팅
		String recent = "REPLY_LIST_RECENT";
		String oldest = "REPLY_LIST_OLDEST";
		
		// 1) boardId 검증
        String boardIdCheck = request.getParameter("boardId");
        System.out.println("[댓글 정렬 서블릿 로그] boardId검증 : ["+boardIdCheck+"]");
		
		// 검증 1단계 > null/빈값 확인
		if (boardIdCheck == null || boardIdCheck.isEmpty()) {
		    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

		    Map<String, Object> error = new HashMap<>();
		    error.put("success", false);
		    error.put("message", "boardId가 비었습니다.");

		    response.getWriter().print(gson.toJson(error));
		    return;
		}

		// 검증 2단계 > 숫자 변환
		int boardId;
		try {
		    // 웹 파라미터는 String으로 넘어오므로 int로 변환(파싱)
		    boardId = Integer.parseInt(boardIdCheck);
		} catch (NumberFormatException e) {
		    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

		    Map<String, Object> error = new HashMap<>();
		    error.put("success", false);
		    error.put("message", "boardId가 숫자가 아닙니다.");

		    response.getWriter().print(gson.toJson(error));
		    return;
		}

		// 검증 3단계 > 숫자 범위 검증(0 이하 확인)
		if (boardId <= 0) {
		    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

		    Map<String, Object> error = new HashMap<>();
		    error.put("success", false);
		    error.put("message", "boardId는 1 이상이어야 합니다.");

		    response.getWriter().print(gson.toJson(error));
		    return;
		}

        // 2) condition 대입
        // 비교 결과에 따른 컨디션값 대입
        String condition = request.getParameter("condition");        
        System.out.println("[댓글 정렬 서블릿 로그] condition검증 : ["+condition+"]");
               
        if (oldest.equals(condition)) {
            condition = oldest;
        } 
        else if (recent.equals(condition)) {
            condition = recent;
        }
        else { 
        //어떤 경우도 아닌 오류상황에선 일단 기본값 정렬인 recent로 실행
        //추후 확장성을 고려한 안전장치
        	condition = recent;
        }

        // 3) 댓글 조회
        replyDTO.setBoardId(boardId);
        replyDTO.setCondition(condition);
        
        List<ReplyDTO> replyList = replyDAO.selectAll(replyDTO);
        System.out.println("[댓글 정렬 서블릿 로그] 댓글 조회 완료 : count=["+replyList.size()+"]");

        // 4) JSON 반환
        String json = gson.toJson(replyList);
        response.getWriter().print(json);
    }			
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("[댓글 정렬 서블릿 로그] POST 요청 : doGet 실행");
		doGet(request, response);
	}
}
