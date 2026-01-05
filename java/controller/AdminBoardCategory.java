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

@WebServlet("/AdminBoardCategory")
public class AdminBoardCategory extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		System.out.println("[관리자 카테고리 서블릿] GET 요청");

		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json; charset=UTF-8");

		Gson gson = new Gson();
		Map<String, Object> result = new HashMap<>();

		// 1) DAO 호출 준비
		BoardDAO boardDAO = new BoardDAO();
		BoardDTO boardDTO = new BoardDTO();
		boardDTO.setCondition("ADMIN_BOARD_CATEGORY"); // BoardDAO에 이 컨디션이 있어야 함

		// 2) 카테고리 목록 조회 (BoardDTO의 boardCategory만 채워진 리스트)
		List<BoardDTO> datas = boardDAO.selectAll(boardDTO);

		// 3) JSON에 넣기 좋은 형태로 변환(String 리스트)
		List<String> categories = new ArrayList<>();
		for (BoardDTO d : datas) {
			if (d.getBoardCategory() != null) {
				categories.add(d.getBoardCategory());
			}
		}

		// 4) 응답 JSON 구성
		result.put("success", true);
		result.put("categories", categories);

		System.out.println("[관리자 카테고리 서블릿] count=[" + categories.size() + "]");
		response.getWriter().print(gson.toJson(result));
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("[관리자 카테고리 서블릿] POST 요청 -> doGet 실행");
		doGet(request, response);
	}
}
