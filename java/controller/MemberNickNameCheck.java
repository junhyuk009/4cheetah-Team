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

// 닉네임 중복검사 서블릿
@WebServlet("/MemberNickNameCheck")
public class MemberNickNameCheck extends HttpServlet {
   private static final long serialVersionUID = 1L;

   // doGet(): GET요청 처리
   
   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response)
         throws ServletException, IOException {

      System.out.println("[닉네임 중복검사 서블릿] GET 요청");
      // JSON 응답 기본 세팅
      response.setCharacterEncoding("UTF-8");
      response.setContentType("application/json; charset=UTF-8");

      Gson gson = new Gson();
      Map<String, Object> result = new HashMap<>();

      // 1) 파라미터 받기
      String memberNickname = request.getParameter("memberNickname");
      System.out.println("[닉네임 중복검사 서블릿] memberNickname=[" + memberNickname + "]");

      // 2) 유효성 검사 null / 빈값
      if (memberNickname == null || memberNickname.trim().isEmpty()) {
         response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
         result.put("success", false);
         result.put("message", "memberNickname이 비었습니다.");
         response.getWriter().print(gson.toJson(result));
         return;
      }
      // trim(): 앞뒤 공백 제거
      memberNickname = memberNickname.trim();

      // 3) 길이 제한
      if (memberNickname.length() > 50) {
         response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
         result.put("success", false);
         result.put("message", "닉네임은 50자 이내여야 합니다.");
         response.getWriter().print(gson.toJson(result));
         return;
      }

      // 4) DAO 중복검사
      MemberDAO memberDAO = new MemberDAO();
      MemberDTO memberDTO = new MemberDTO();

      // MemberDAO selectOne에서 이 컨디션 처리 필요
      memberDTO.setCondition("JOIN_NICKNAME");
      memberDTO.setMemberNickname(memberNickname);

      MemberDTO data = memberDAO.selectOne(memberDTO);
      // data가 null이면 DB에 없음 > true
      boolean available = (data == null);

      // 5) JSON 응답
      result.put("success", true);
      result.put("available", available);
      result.put("memberNickname", memberNickname);

      System.out.println("[닉네임 중복검사 서블릿] available=[" + available + "]");
      response.getWriter().print(gson.toJson(result));
   }
   
   // POST로 오면 GET 로직 재사용
   @Override
   protected void doPost(HttpServletRequest request, HttpServletResponse response)
         throws ServletException, IOException {
      System.out.println("[닉네임 중복검사 서블릿] POST 요청 -> doGet 실행");
      doGet(request, response);
   }
}