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

@WebServlet("/FindPasswordVerifyCode")
public class FindPasswordVerifyCodeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");

        Gson gson = new Gson();
        Map<String, Object> result = new HashMap<>();

        /* purpose 분기 */
        String purpose = request.getParameter("purpose");
        if (purpose == null) {
            purpose = "FIND_PASSWORD";
        }

        /* 1. FIND_PASSWORD */
        if ("FIND_PASSWORD".equals(purpose)) {

            HttpSession session = request.getSession(false);
            if (session == null) {
                result.put("success", false);
                result.put("message", "세션이 만료되었습니다. 다시 인증번호를 발송해주세요.");
                response.getWriter().print(gson.toJson(result));
                return;
            }

            String code = request.getParameter("code");
            if (code == null || code.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                result.put("success", false);
                result.put("message", "인증번호를 입력해주세요.");
                response.getWriter().print(gson.toJson(result));
                return;
            }
            code = code.trim();

            String savedCode = (String) session.getAttribute("findPasswordCode");
            Long expireAt = (Long) session.getAttribute("findPasswordExpireAt");

            if (savedCode == null || expireAt == null) {
                result.put("success", false);
                result.put("message", "인증 요청 정보가 없습니다. 다시 인증번호를 발송해주세요.");
                response.getWriter().print(gson.toJson(result));
                return;
            }

            if (System.currentTimeMillis() > expireAt) {
                session.setAttribute("findPasswordVerified", false);
                result.put("success", false);
                result.put("message", "인증번호가 만료되었습니다.");
                response.getWriter().print(gson.toJson(result));
                return;
            }

            if (!savedCode.equals(code)) {
                session.setAttribute("findPasswordVerified", false);
                result.put("success", false);
                result.put("message", "인증번호가 올바르지 않습니다.");
                response.getWriter().print(gson.toJson(result));
                return;
            }

            session.setAttribute("findPasswordVerified", true);

            result.put("success", true);
            response.getWriter().print(gson.toJson(result));
            return;
        }

        /* 2. JOIN (회원가입 이메일 인증) */
        else if ("JOIN".equals(purpose)) {

            HttpSession session = request.getSession(false);
            if (session == null) {
                result.put("success", false);
                result.put("message", "세션이 만료되었습니다. 다시 인증번호를 발송해주세요.");
                response.getWriter().print(gson.toJson(result));
                return;
            }

            String code = request.getParameter("code");
            if (code == null || code.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                result.put("success", false);
                result.put("message", "인증번호를 입력해주세요.");
                response.getWriter().print(gson.toJson(result));
                return;
            }
            code = code.trim();

            String savedCode = (String) session.getAttribute("joinEmailCode");
            Long expireAt = (Long) session.getAttribute("joinEmailExpireAt");
            String joinEmail = (String) session.getAttribute("joinEmail"); // 🔑 발송 시 저장된 이메일

            if (savedCode == null || expireAt == null || joinEmail == null) {
                result.put("success", false);
                result.put("message", "인증 요청 정보가 없습니다. 다시 인증번호를 발송해주세요.");
                response.getWriter().print(gson.toJson(result));
                return;
            }

            if (System.currentTimeMillis() > expireAt) {
                session.setAttribute("joinEmailVerified", false);
                result.put("success", false);
                result.put("message", "인증번호가 만료되었습니다.");
                response.getWriter().print(gson.toJson(result));
                return;
            }

            if (!savedCode.equals(code)) {
                session.setAttribute("joinEmailVerified", false);
                result.put("success", false);
                result.put("message", "인증번호가 올바르지 않습니다.");
                response.getWriter().print(gson.toJson(result));
                return;
            }

            session.setAttribute("joinEmailVerified", true);
            session.setAttribute("joinEmail", joinEmail);

            result.put("success", true);
            result.put("verified", true);
            response.getWriter().print(gson.toJson(result));
            return;
        }

        /* 잘못된 purpose */
        result.put("success", false);
        result.put("message", "잘못된 요청입니다.");
        response.getWriter().print(gson.toJson(result));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}
