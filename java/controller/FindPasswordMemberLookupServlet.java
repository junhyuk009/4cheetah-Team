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

@WebServlet("/FindPasswordMemberLookup")
public class FindPasswordMemberLookupServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // JSON 응답 세팅
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");

        Gson gson = new Gson();
        Map<String, Object> result = new HashMap<>();

        // 1) 파라미터 수신 (JSP의 id/name = memberName)
        String memberName = request.getParameter("memberName");
        if (memberName == null || memberName.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            result.put("success", false);
            result.put("message", "아이디를 입력해주세요.");
            response.getWriter().print(gson.toJson(result));
            return;
        }
        memberName = memberName.trim();

        // 2) DB 조회 (아이디 존재 여부 + 이메일)
        MemberDAO memberDAO = new MemberDAO();
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setCondition("MEMBER_ID_EMAIL");
        memberDTO.setMemberName(memberName);

        MemberDTO memberData = memberDAO.selectOne(memberDTO);

        // 3) 응답(JSON)
        result.put("success", true);

        if (memberData == null) {
            result.put("exists", false);
            result.put("message", "존재하지 않는 아이디입니다.");
        } else {
            result.put("exists", true);
            result.put("memberId", memberData.getMemberId());
            result.put("memberEmail", memberData.getMemberEmail());
        }

        response.getWriter().print(gson.toJson(result));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}
