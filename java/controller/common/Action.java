package controller.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Action {
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response);
	// 어디로 갈지 : main.jsp board.jsp controller.jsp
	// 어떻게 갈지 : 리다이렉트 포워드
}