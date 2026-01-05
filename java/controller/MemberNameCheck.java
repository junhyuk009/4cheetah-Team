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

@WebServlet("/MemberNameCheck") // 이 URL로 들어오면 이 서블릿이 실행됨
public class MemberNameCheck extends HttpServlet {
   private static final long serialVersionUID = 1L;

   // GET 요청 처리 (AJAX에서 보통 GET/POST 둘 다 가능)
   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response)
         throws ServletException, IOException {

      System.out.println("[ID 중복검사 서블릿] GET 요청");

      // 1) JSON 응답 기본 세팅
      response.setCharacterEncoding("UTF-8");
      response.setContentType("application/json; charset=UTF-8");
      
      // Gson: Java 객체 > JSON 문자열로 변환
      Gson gson = new Gson();
      // 결과를 JSON 형태로 만들기 위해 Map 사용
      // result.put("key", value)로 값 쌓고 마지막에 gson.toJson(result)
      Map<String, Object> result = new HashMap<>();

      // 2) 파라미터 받기 (프론트에서 보내는 이름과 반드시 같아야 함)
      String memberName = request.getParameter("memberName");
      System.out.println("[ID 중복검사 서블릿] memberName=[" + memberName + "]");

      // 3) 유효성 검사 (null/빈값) JSON으로 에러 메시지 내려줌
      if (memberName == null || memberName.trim().isEmpty()) {
         response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
         result.put("success", false);
         result.put("message", "memberName이 비었습니다.");
         response.getWriter().print(gson.toJson(result));
         return;
      }

      // 4) 공백 제거
      memberName = memberName.trim();

      // 5) 길이 제한 (DB가 VARCHAR(50)이면 여기서 막는 게 안전)
      if (memberName.length() > 50) {
         response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
         result.put("success", false);
         result.put("message", "ID는 50자 이내여야 합니다.");
         response.getWriter().print(gson.toJson(result));
         return;
      }

      // 6) DAO로 중복 여부 확인 - MemberDAO.selectOne()을 호출해서 해당 memberName이 존재하는지 확인
      MemberDAO memberDAO = new MemberDAO();
      MemberDTO memberDTO = new MemberDTO();

      // MemberDAO가 이 컨디션으로 selectOne에서 중복검사를 해줘야 함
      memberDTO.setCondition("JOIN_NAME");
      memberDTO.setMemberName(memberName);

      MemberDTO data = memberDAO.selectOne(memberDTO);

      // data가 null이면 DB에 없음 => 사용 가능(true)
      boolean available = (data == null);

      // 7) JSON 응답 만들기 
      result.put("success", true);
      result.put("available", available);
      result.put("memberName", memberName);

      System.out.println("[ID 중복검사 서블릿] available=[" + available + "]");
      // Map > JSON 문자열로 변환해서 응답
      response.getWriter().print(gson.toJson(result));
   }

   // POST로 오면 GET 로직 재사용
   @Override
   protected void doPost(HttpServletRequest request, HttpServletResponse response)
         throws ServletException, IOException {
      System.out.println("[ID 중복검사 서블릿] POST 요청 -> doGet 실행");
      doGet(request, response);
   }
}