package controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.dao.MemberDAO;
import model.dto.MemberDTO;

/**
 * ConfirmPassword
 *
 * 역할
 * - 비밀번호 변경 전 본인 확인을 위한 AJAX 요청 처리
 * - 사용자가 입력한 현재 비밀번호가 DB에 저장된 값과 일치하는지 검증
 *
 * - R : MemberDAO.selectOne()
 */
@WebServlet("/confirmPassword.do")
public class ConfirmPassword extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("[로그] ConfirmPassword doPost : start");

        // 1. 요청 및 응답 기본 설정
        // 한글 데이터 처리를 위해 요청 및 응답 인코딩을 UTF-8로 설정
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain; charset=UTF-8");

        PrintWriter out = response.getWriter();

        // 2. 입력값 확인
        // 사용자가 입력한 현재 비밀번호를 요청 파라미터에서 조회
        String inputPassword = request.getParameter("password");
        System.out.println("[로그] ConfirmPassword : inputPassword=" + inputPassword);

        // 비밀번호가 전달되지 않았거나 공백인 경우 정상적인 검증이 불가능하므로 즉시 실패 처리
        if (inputPassword == null || inputPassword.trim().isEmpty()) {
            out.print("FAIL");
            return;
        }

        // 3. 세션 확인
        // 비밀번호 확인은 로그인 상태에서만 가능하므로 기존 세션이 없는 경우 비정상 요청으로 판단
        HttpSession session = request.getSession(false);
        if (session == null) {
            System.out.println("[로그] ConfirmPassword : session null");
            out.print("NO_SESSION");
            return;
        }

        // 세션에 저장된 로그인 사용자 식별자 조회
        Integer memberId = (Integer) session.getAttribute("memberId");
        System.out.println("[로그] ConfirmPassword : session memberId=" + memberId);

        // 로그인 정보가 없는 경우 인증 불가
        if (memberId == null) {
            out.print("NO_SESSION");
            return;
        }

        // 4. DAO 조회
        // 현재 로그인한 회원의 비밀번호 정보를 조회하기 위한 DTO 구성
        MemberDTO dto = new MemberDTO();
        dto.setMemberId(memberId);
        dto.setCondition("MEMBER_PASSWORD_CHECK");

        MemberDAO dao = new MemberDAO();
        MemberDTO dbMember = dao.selectOne(dto);

        // DB 조회 결과가 없는 경우 회원 정보가 정상적으로 존재하지 않는 상황으로 판단
        if (dbMember == null) {
            System.out.println("[로그] ConfirmPassword : dbMember null");
            out.print("FAIL");
            return;
        }

        // 5. 비밀번호 비교
        // 사용자가 입력한 비밀번호와 DB에 저장된 비밀번호를 비교
        if (inputPassword.equals(dbMember.getMemberPassword())) {
            out.print("OK");
            System.out.println("[로그] ConfirmPassword response=OK");
        } else {
            out.print("FAIL");
            System.out.println("[로그] ConfirmPassword response=FAIL");
        }
    }
}