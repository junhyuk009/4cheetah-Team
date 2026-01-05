package controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import model.dao.MemberDAO;
import model.dto.MemberDTO;

@WebServlet("/MemberEmailCheck")
public class MemberEmailCheck extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");

        Gson gson = new Gson();
        Map<String, Object> result = new HashMap<>();

        String memberEmail = request.getParameter("memberEmail");

        if (memberEmail == null || memberEmail.trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "이메일을 입력해주세요.");
            response.getWriter().print(gson.toJson(result));
            return;
        }

        MemberDAO dao = new MemberDAO();
        MemberDTO dto = new MemberDTO();
        dto.setMemberEmail(memberEmail.trim());
        dto.setCondition("MEMBER_EMAIL_CHECK");

        MemberDTO exist = dao.selectOne(dto);

        if (exist != null) {
            // 이미 존재
            result.put("success", true);
            result.put("available", false);
            result.put("message", "이미 사용 중인 이메일입니다.");
        } else {
            // 사용 가능
            result.put("success", true);
            result.put("available", true);
            result.put("message", "사용 가능한 이메일입니다.");
        }

        response.getWriter().print(gson.toJson(result));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}
