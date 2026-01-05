package controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * ConfirmEmailCode
 *
 * 역할
 * - 이메일 인증 코드 확인 (AJAX)
 * - 세션에 저장된 인증 코드와 입력값 비교
 * - 인증 성공 시 emailVerified = true 설정
 */
@WebServlet("/confirmEmailCode.do")
public class ConfirmEmailCode extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("[로그] ConfirmEmailCode doPost : start");

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain; charset=UTF-8");

        PrintWriter out = response.getWriter();

        // 1️) 사용자 입력 인증 코드
        String inputCode = request.getParameter("code");
        System.out.println("[로그] ConfirmEmailCode : inputCode=" + inputCode);

        // 2️) 세션 확인
        HttpSession session = request.getSession(false);
        if (session == null) {
            System.out.println("[로그] ConfirmEmailCode : session is null");
            out.print("NO_SESSION");
            return;
        }

        // 3️) 세션에 저장된 인증 코드
        String sessionCode = (String) session.getAttribute("emailCode");
        System.out.println("[로그] ConfirmEmailCode : sessionCode=" + sessionCode);

        if (sessionCode == null) {
            System.out.println("[로그] ConfirmEmailCode : emailCode not found in session");
            out.print("NO_SESSION");
            return;
        }

        // 4️) 코드 비교
        if (sessionCode.equals(inputCode)) {

            session.setAttribute("emailVerified", true);
            session.removeAttribute("emailCode"); // 재사용 방지

            System.out.println("[로그] ConfirmEmailCode : emailVerified=true");
            System.out.println("[로그] ConfirmEmailCode : emailCode removed");

            out.print("OK");

        } else {
            System.out.println("[로그] ConfirmEmailCode : code mismatch");
            out.print("FAIL");
        }
    }
}
