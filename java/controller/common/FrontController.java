package controller.common;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@MultipartConfig // getPart()/multipart 처리를 위해 선언
@WebServlet("*.do")
public class FrontController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ActionFactory factory;

	public FrontController() {
		super();
		factory = new ActionFactory();
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doAction(request, response);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doAction(request, response);
	}

	private void doAction(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String command = request.getRequestURI();
		command = command.substring(request.getContextPath().length());
		System.out.println("[프론트 컨트롤러 로그] command : " + command);

		Action action = factory.getAction(command);
		System.out.println("[프론트 컨트롤러 로그] action : " + action);

		// 1) 매핑된 Action이 없으면 404 처리(방어)
		if (action == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		// 2) Action 실행
		ActionForward forward = action.execute(request, response);

		// 3) 비동기(JSON) 액션처럼 Action이 응답을 직접 작성한 경우
		// - 예: ProfileImageUploadAction에서 writeJsonResponse() 후 return null
		// - forward가 null이면 추가 이동/forward를 하면 안 됨(NPE/이중응답 방지)
		if (forward == null) {
			return;
		}

		// 4) 페이지 이동 처리
		if (forward.isRedirect()) {
			response.sendRedirect(forward.getPath());
		} else {
			RequestDispatcher dispatcher = request.getRequestDispatcher(forward.getPath());
			dispatcher.forward(request, response);
		}
	}
}
