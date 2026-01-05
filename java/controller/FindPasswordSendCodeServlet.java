package controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import controller.util.EmailService;
import model.dao.MemberDAO;
import model.dto.MemberDTO;

@WebServlet("/FindPasswordSendCode")
public class FindPasswordSendCodeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // 인증 유효시간 3분 (180초)
    private static final int EXPIRE_SECONDS = 180;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");

        Gson gson = new Gson();
        Map<String, Object> result = new HashMap<>();

        /*   purpose 분기
           - FIND_PASSWORD : 기존 비밀번호 찾기
           - JOIN          : 회원가입 이메일 인증 */
        String purpose = request.getParameter("purpose");
        if (purpose == null) {
            purpose = "FIND_PASSWORD";
        }

        /* 1. FIND_PASSWORD (기존 비밀번호 찾기 로직) */
        if ("FIND_PASSWORD".equals(purpose)) {

            // 1) 파라미터 수신
            String memberName = request.getParameter("memberName");
            if (memberName == null || memberName.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                result.put("success", false);
                result.put("message", "아이디를 입력해주세요.");
                response.getWriter().print(gson.toJson(result));
                return;
            }
            memberName = memberName.trim();

            // 2) DB에서 이메일 조회 (클라이언트가 준 이메일은 신뢰하지 않음)
            MemberDAO memberDAO = new MemberDAO();
            MemberDTO memberDTO = new MemberDTO();
            memberDTO.setCondition("MEMBER_ID_EMAIL");  // Lookup에서 사용하는 조건
            memberDTO.setMemberName(memberName);

            MemberDTO memberData = memberDAO.selectOne(memberDTO);

            if (memberData == null) {
                result.put("success", false);
                result.put("message", "존재하지 않는 아이디입니다.");
                response.getWriter().print(gson.toJson(result));
                return;
            }

            String email = memberData.getMemberEmail();
            if (email == null || email.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "해당 계정의 이메일 정보가 없습니다.");
                response.getWriter().print(gson.toJson(result));
                return;
            }

            // 3) 인증코드 생성 (6자리)
            String code = String.valueOf(100000 + new Random().nextInt(900000));

            // 4) 이메일 발송
            try {
                EmailService emailService = new EmailService();
                emailService.sendPasswordResetCode(email, code);
            } catch (Exception e) {
                e.printStackTrace();
                result.put("success", false);
                result.put("message", "이메일 발송에 실패했습니다.");
                response.getWriter().print(gson.toJson(result));
                return;
            }

            // 5) 세션 저장 (검증/최종 변경에서 사용)
            HttpSession session = request.getSession(true);

            session.setAttribute("findPasswordMemberId", memberData.getMemberId());
            session.setAttribute("findPasswordEmail", email);
            session.setAttribute("findPasswordCode", code);
            session.setAttribute("findPasswordExpireAt",
                    System.currentTimeMillis() + (EXPIRE_SECONDS * 1000L));
            session.setAttribute("findPasswordVerified", false);

            // 6) JSON 응답
            result.put("success", true);
            result.put("expireSeconds", EXPIRE_SECONDS);
            response.getWriter().print(gson.toJson(result));
            return;
        }

        /* 2.JOIN (회원가입 이메일 인증 코드 발송) */
        else if ("JOIN".equals(purpose)) {

            // 1) 이메일 파라미터 수신
            String email = request.getParameter("memberEmail");
            if (email == null || email.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "이메일을 입력해주세요.");
                response.getWriter().print(gson.toJson(result));
                return;
            }
            email = email.trim();

            // 2) 이메일 중복 체크 (이미 가입된 이메일 차단)
            MemberDAO memberDAO = new MemberDAO();
            MemberDTO checkDTO = new MemberDTO();
            checkDTO.setCondition("MEMBER_EMAIL_CHECK"); // DAO에 조건 필요
            checkDTO.setMemberEmail(email);


            // 3) 인증코드 생성 (6자리)
            String code = String.valueOf(100000 + new Random().nextInt(900000));

            // 4) 이메일 발송
            try {
                EmailService emailService = new EmailService();
                emailService.sendPasswordResetCode(email, code);
            } catch (Exception e) {
                e.printStackTrace();
                result.put("success", false);
                result.put("message", "이메일 발송에 실패했습니다.");
                response.getWriter().print(gson.toJson(result));
                return;
            }

            // 5) 세션 저장 (회원가입 이메일 인증용)
            HttpSession session = request.getSession(true);

            session.setAttribute("joinEmail", email);
            session.setAttribute("joinEmailCode", code);
            session.setAttribute("joinEmailExpireAt",
                    System.currentTimeMillis() + (EXPIRE_SECONDS * 1000L));
            session.setAttribute("joinEmailVerified", false);

            // 6) JSON 응답
            result.put("success", true);
            result.put("expireSeconds", EXPIRE_SECONDS);
            response.getWriter().print(gson.toJson(result));
            return;
        }

        /* 잘못된 purpose 처리 */
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
