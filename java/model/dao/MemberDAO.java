package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import model.common.JDBCUtil;
import model.dto.MemberDTO;

public class MemberDAO {
   // SELECT_ONE(condition 분기)

   // JOIN_NAME: 아이디(회원명) 중복검사
   private static final String SELECT_JOIN_NAME = "SELECT MEMBER_ID FROM MEMBER WHERE MEMBER_NAME = ?";
   // JOIN_NICKNAME: 닉네임 중복검사
   private static final String SELECT_JOIN_NICKNAME = "SELECT MEMBER_ID FROM MEMBER WHERE MEMBER_NICKNAME = ?";
   //  MEMBER_EMAIL_CHECK: 이메일 중복검사
   private static final String SELECT_MEMBER_EMAIL_CHECK = "SELECT MEMBER_ID FROM MEMBER WHERE MEMBER_EMAIL = ?";

   // MEMBER_LOGIN: 로그인(ACTIVE/ADMIN만 허용)
   private static final String SELECT_MEMBER_LOGIN =
         "SELECT MEMBER_ID, MEMBER_NAME, MEMBER_PASSWORD, MEMBER_NICKNAME, MEMBER_CASH, MEMBER_ROLE, " +
         "MEMBER_PROFILE_IMAGE, MEMBER_EMAIL, MEMBER_PHONE_NUMBER " +
         "FROM MEMBER " +
         "WHERE MEMBER_NAME = ? " +
         "AND MEMBER_PASSWORD = ? " +
         "AND MEMBER_ROLE IN ('ACTIVE','ADMIN')";

   // MEMBER_MYPAGE: 마이페이지(ID로 회원정보 조회)
   private static final String SELECT_MEMBER_MYPAGE =
         "SELECT MEMBER_ID, MEMBER_NAME, MEMBER_PASSWORD, MEMBER_NICKNAME, MEMBER_CASH, MEMBER_ROLE, " +
         "MEMBER_PROFILE_IMAGE, MEMBER_EMAIL, MEMBER_PHONE_NUMBER " +
         "FROM MEMBER " +
         "WHERE MEMBER_ID = ?";

   // MEMBER_ADMINPAGE: 관리자 여부 확인(ID + ROLE=ADMIN)
   private static final String SELECT_MEMBER_ADMINPAGE =
         "SELECT MEMBER_ID, MEMBER_NAME, MEMBER_PASSWORD, MEMBER_NICKNAME, MEMBER_CASH, MEMBER_ROLE, " +
         "MEMBER_PROFILE_IMAGE, MEMBER_EMAIL, MEMBER_PHONE_NUMBER " +
         "FROM MEMBER " +
         "WHERE MEMBER_ID = ? " +
         "AND MEMBER_ROLE = 'ADMIN'";

   // MEMBER_PASSWORD_CHECK: 현재 비밀번호 확인(본인 검증)
   private static final String SELECT_PASSWORD_CHECK = "SELECT MEMBER_ID FROM MEMBER WHERE MEMBER_ID = ? AND MEMBER_PASSWORD = ?";

   // MEMBER_FIND_ID: 아이디(회원명) 존재 확인(비번찾기 1단계)
   private static final String SELECT_FIND_ID = "SELECT MEMBER_ID FROM MEMBER WHERE MEMBER_NAME = ?";

   // MEMBER_FIND_EMAIL: 아이디(회원명)로 이메일 조회(비번찾기 2단계)
   private static final String SELECT_FIND_EMAIL = "SELECT MEMBER_NAME, MEMBER_EMAIL FROM MEMBER WHERE MEMBER_NAME = ?";

   // MEMBER_ID_EMAIL: 아이디(회원명)로 아이디 + 이메일 반환
   private static final String SELECT_MEMBER_EMAIL = "SELECT MEMBER_ID, MEMBER_NAME, MEMBER_EMAIL FROM MEMBER WHERE MEMBER_NAME = ?";

   // MEMBER_CASH_SELECT: 캐시 현재값 조회
   private static final String SELECT_CASH = "SELECT MEMBER_CASH FROM MEMBER WHERE MEMBER_ID = ?";

   // SELECT_ALL(관리자/활성 회원 용)
   private static final String SELECT_MEMBER_LIST_ALL =
         "SELECT MEMBER_ID, MEMBER_NAME, MEMBER_NICKNAME, MEMBER_CASH, MEMBER_ROLE, MEMBER_EMAIL, MEMBER_PHONE_NUMBER " +
         "FROM MEMBER ORDER BY MEMBER_ID ASC";

   private static final String SELECT_MEMBER_LIST_ACTIVE =
         "SELECT MEMBER_ID, MEMBER_NAME, MEMBER_NICKNAME, MEMBER_CASH, MEMBER_ROLE, MEMBER_EMAIL, MEMBER_PHONE_NUMBER " +
         "FROM MEMBER WHERE MEMBER_ROLE = 'ACTIVE' ORDER BY MEMBER_ID ASC";

   // INSERT
   private static final String INSERT_MEMBER_JOIN =
         "INSERT INTO MEMBER (" +
         "MEMBER_ID, MEMBER_NAME, MEMBER_PASSWORD, MEMBER_NICKNAME, MEMBER_CASH, MEMBER_ROLE, " +
         "MEMBER_PROFILE_IMAGE, MEMBER_EMAIL, MEMBER_PHONE_NUMBER" +
         ") VALUES (" +
         "MEMBER_ID_SEQ.NEXTVAL, ?, ?, ?, 0, 'ACTIVE', ?, ?, ?" +
         ")";

   // UPDATE (condition 분기)
   private static final String UPDATE_NICKNAME =
         "UPDATE MEMBER SET MEMBER_NICKNAME = ?, MEMBER_CASH = MEMBER_CASH - ? WHERE MEMBER_ID = ? AND MEMBER_CASH >= ?";

   private static final String UPDATE_PROFILE_IMAGE =
         "UPDATE MEMBER SET MEMBER_PROFILE_IMAGE = ?, MEMBER_CASH = MEMBER_CASH - ? WHERE MEMBER_ID = ? AND MEMBER_CASH >= ?";

   private static final String UPDATE_MEMBER_INFORM =
         "UPDATE MEMBER SET MEMBER_NICKNAME = ?, MEMBER_PROFILE_IMAGE = ?, MEMBER_CASH = MEMBER_CASH - ? WHERE MEMBER_ID = ? AND MEMBER_CASH >= ?";

   private static final String UPDATE_PASSWORD =
         "UPDATE MEMBER SET MEMBER_PASSWORD = ? WHERE MEMBER_ID = ?";

   private static final String UPDATE_CASH_PLUS =
         "UPDATE MEMBER SET MEMBER_CASH = MEMBER_CASH + ? WHERE MEMBER_ID = ?";

   private static final String UPDATE_CASH_MINUS =
         "UPDATE MEMBER SET MEMBER_CASH = MEMBER_CASH - ? WHERE MEMBER_ID = ? AND MEMBER_CASH >= ?";

   private static final String UPDATE_WITHDRAWN =
         "UPDATE MEMBER SET MEMBER_ROLE = 'WITHDRAWN' WHERE MEMBER_ID = ?";

   public MemberDTO selectOne(MemberDTO memberDTO) {
      MemberDTO data = null;
      Connection conn = JDBCUtil.connect();
      PreparedStatement pstmt = null;
      ResultSet rs = null;

      try {
         String condition = memberDTO.getCondition();

         if ("JOIN_NAME".equals(condition)) {
            pstmt = conn.prepareStatement(SELECT_JOIN_NAME);
            pstmt.setString(1, memberDTO.getMemberName());
         }
         else if ("JOIN_NICKNAME".equals(condition)) {
            pstmt = conn.prepareStatement(SELECT_JOIN_NICKNAME);
            pstmt.setString(1, memberDTO.getMemberNickname());
         }
         // 이메일 중복검사 (요청한 member_email_check 지원 + 대문자도 지원)
         else if ("member_email_check".equals(condition) || "MEMBER_EMAIL_CHECK".equals(condition)) {
            pstmt = conn.prepareStatement(SELECT_MEMBER_EMAIL_CHECK);
            pstmt.setString(1, memberDTO.getMemberEmail());
         }
         else if ("MEMBER_LOGIN".equals(condition)) {
            pstmt = conn.prepareStatement(SELECT_MEMBER_LOGIN);
            pstmt.setString(1, memberDTO.getMemberName());
            pstmt.setString(2, memberDTO.getMemberPassword());
         }
         else if ("MEMBER_MYPAGE".equals(condition)) {
            pstmt = conn.prepareStatement(SELECT_MEMBER_MYPAGE);
            pstmt.setInt(1, memberDTO.getMemberId());
         }
         else if ("MEMBER_ADMINPAGE".equals(condition)) {
            pstmt = conn.prepareStatement(SELECT_MEMBER_ADMINPAGE);
            pstmt.setInt(1, memberDTO.getMemberId());
         }
         else if ("MEMBER_PASSWORD_CHECK".equals(condition)) {
            pstmt = conn.prepareStatement(SELECT_PASSWORD_CHECK);
            pstmt.setInt(1, memberDTO.getMemberId());
            pstmt.setString(2, memberDTO.getMemberPassword());
         }
         else if ("MEMBER_FIND_ID".equals(condition)) {
            pstmt = conn.prepareStatement(SELECT_FIND_ID);
            pstmt.setString(1, memberDTO.getMemberName());
         }
         else if ("MEMBER_FIND_EMAIL".equals(condition)) {
            pstmt = conn.prepareStatement(SELECT_FIND_EMAIL);
            pstmt.setString(1, memberDTO.getMemberName());
         }
         else if ("MEMBER_CASH_SELECT".equals(condition)) {
            pstmt = conn.prepareStatement(SELECT_CASH);
            pstmt.setInt(1, memberDTO.getMemberId());
         }
         else if ("MEMBER_ID_EMAIL".equals(condition)) {
            pstmt = conn.prepareStatement(SELECT_MEMBER_EMAIL);
            pstmt.setString(1, memberDTO.getMemberName());
         }
         else {
            return null;
         }

         rs = pstmt.executeQuery();

         if (rs.next()) {
            data = new MemberDTO();

            // 중복검사/검증류는 MEMBER_ID만 있으면 OK
            if ("JOIN_NAME".equals(condition)
                  || "JOIN_NICKNAME".equals(condition)
                  || "MEMBER_PASSWORD_CHECK".equals(condition)
                  || "MEMBER_FIND_ID".equals(condition)
                  || "member_email_check".equals(condition)
                  || "MEMBER_EMAIL_CHECK".equals(condition)) {
               data.setMemberId(rs.getInt("MEMBER_ID"));
            }
            // 이메일 찾기
            else if ("MEMBER_FIND_EMAIL".equals(condition)) {
               data.setMemberEmail(rs.getString("MEMBER_EMAIL"));
            }
            // 캐시 조회
            else if ("MEMBER_CASH_SELECT".equals(condition)) {
               data.setMemberCash(rs.getInt("MEMBER_CASH"));
            }
            // 아이디+이메일 반환
            else if ("MEMBER_ID_EMAIL".equals(condition)) {
               data.setMemberId(rs.getInt("MEMBER_ID"));
               data.setMemberName(rs.getString("MEMBER_NAME"));
               data.setMemberEmail(rs.getString("MEMBER_EMAIL"));
            }
            // 로그인/마이페이지/관리자페이지는 풀 세팅
            else {
               data.setMemberId(rs.getInt("MEMBER_ID"));
               data.setMemberName(rs.getString("MEMBER_NAME"));
               data.setMemberPassword(rs.getString("MEMBER_PASSWORD"));
               data.setMemberNickname(rs.getString("MEMBER_NICKNAME"));
               data.setMemberCash(rs.getInt("MEMBER_CASH"));
               data.setMemberRole(rs.getString("MEMBER_ROLE"));
               data.setMemberProfileImage(getClobAsString(rs, "MEMBER_PROFILE_IMAGE"));
               data.setMemberEmail(rs.getString("MEMBER_EMAIL"));
               data.setMemberPhoneNumber(rs.getString("MEMBER_PHONE_NUMBER"));
            }
         }
      } catch (Exception e) {
         e.printStackTrace();
      } finally {
         try { if (rs != null) rs.close(); } catch (Exception ignored) {}
         JDBCUtil.disconnect(conn, pstmt);
      }
      return data;
   }

   // SELECT_ALL
   public ArrayList<MemberDTO> selectAll(MemberDTO memberDTO) {
      ArrayList<MemberDTO> datas = new ArrayList<>();
      Connection conn = JDBCUtil.connect();
      PreparedStatement pstmt = null;
      ResultSet rs = null;

      try {
         String condition = memberDTO.getCondition();

         if ("MEMBER_LIST_ALL".equals(condition)) {
            pstmt = conn.prepareStatement(SELECT_MEMBER_LIST_ALL);
         }
         else if ("MEMBER_LIST_ACTIVE".equals(condition)) {
            pstmt = conn.prepareStatement(SELECT_MEMBER_LIST_ACTIVE);
         }
         else {
            return datas;
         }

         rs = pstmt.executeQuery();

         while (rs.next()) {
            MemberDTO data = new MemberDTO();
            data.setMemberId(rs.getInt("MEMBER_ID"));
            data.setMemberName(rs.getString("MEMBER_NAME"));
            data.setMemberNickname(rs.getString("MEMBER_NICKNAME"));
            data.setMemberCash(rs.getInt("MEMBER_CASH"));
            data.setMemberRole(rs.getString("MEMBER_ROLE"));
            data.setMemberEmail(rs.getString("MEMBER_EMAIL"));
            data.setMemberPhoneNumber(rs.getString("MEMBER_PHONE_NUMBER"));
            datas.add(data);
         }
      } catch (Exception e) {
         e.printStackTrace();
      } finally {
         try { if (rs != null) rs.close(); } catch (Exception ignored) {}
         JDBCUtil.disconnect(conn, pstmt);
      }
      return datas;
   }

   // INSERT
   public boolean insert(MemberDTO memberDTO) {
      Connection conn = JDBCUtil.connect();
      PreparedStatement pstmt = null;

      try {
         if (!"MEMBER_JOIN".equals(memberDTO.getCondition())) {
            return false;
         }

         pstmt = conn.prepareStatement(INSERT_MEMBER_JOIN);

         pstmt.setString(1, memberDTO.getMemberName());
         pstmt.setString(2, memberDTO.getMemberPassword());
         pstmt.setString(3, memberDTO.getMemberNickname());

         pstmt.setString(4, memberDTO.getMemberProfileImage());
         pstmt.setString(5, memberDTO.getMemberEmail());
         pstmt.setString(6, memberDTO.getMemberPhoneNumber());

         int result = pstmt.executeUpdate();
         if (result <= 0) return false;

      } catch (Exception e) {
         e.printStackTrace();
         return false;
      } finally {
         JDBCUtil.disconnect(conn, pstmt);
      }
      return true;
   }

   // UPDATE
   public boolean update(MemberDTO memberDTO) {
      Connection conn = JDBCUtil.connect();
      PreparedStatement pstmt = null;

      try {
         String condition = memberDTO.getCondition();

         if ("MEMBER_NICKNAME_UPDATE".equals(condition)) {
            pstmt = conn.prepareStatement(UPDATE_NICKNAME);
            pstmt.setString(1, memberDTO.getMemberNickname());
            pstmt.setInt(2, memberDTO.getMemberPayCash());
            pstmt.setInt(3, memberDTO.getMemberId());
            pstmt.setInt(4, memberDTO.getMemberPayCash());
         }
         else if ("MEMBER_PROFILE_UPDATE".equals(condition)) {
            pstmt = conn.prepareStatement(UPDATE_PROFILE_IMAGE);
            pstmt.setString(1, memberDTO.getMemberProfileImage());
            pstmt.setInt(2, memberDTO.getMemberPayCash());
            pstmt.setInt(3, memberDTO.getMemberId());
            pstmt.setInt(4, memberDTO.getMemberPayCash());
         }
         else if ("MEMBER_INFORM_UPDATE".equals(condition)) {
            pstmt = conn.prepareStatement(UPDATE_MEMBER_INFORM);
            pstmt.setString(1, memberDTO.getMemberNickname());
            pstmt.setString(2, memberDTO.getMemberProfileImage());
            pstmt.setInt(3, memberDTO.getMemberPayCash());
            pstmt.setInt(4, memberDTO.getMemberId());
            pstmt.setInt(5, memberDTO.getMemberPayCash());
         }
         else if ("MEMBER_PASSWORD_UPDATE".equals(condition)) {
            pstmt = conn.prepareStatement(UPDATE_PASSWORD);
            pstmt.setString(1, memberDTO.getMemberPassword());
            pstmt.setInt(2, memberDTO.getMemberId());
         }
         else if ("MEMBER_CASH_PLUS".equals(condition)) {
            pstmt = conn.prepareStatement(UPDATE_CASH_PLUS);
            pstmt.setInt(1, memberDTO.getMemberPayCash());
            pstmt.setInt(2, memberDTO.getMemberId());
         }
         else if ("MEMBER_CASH_MINUS".equals(condition)) {
            pstmt = conn.prepareStatement(UPDATE_CASH_MINUS);
            pstmt.setInt(1, memberDTO.getMemberPayCash());
            pstmt.setInt(2, memberDTO.getMemberId());
            pstmt.setInt(3, memberDTO.getMemberPayCash());
         }
         else if ("MEMBER_WITHDRAWN".equals(condition)) {
            pstmt = conn.prepareStatement(UPDATE_WITHDRAWN);
            pstmt.setInt(1, memberDTO.getMemberId());
         }
         else {
            return false;
         }

         int result = pstmt.executeUpdate();
         if (result <= 0) return false;

      } catch (Exception e) {
         e.printStackTrace();
         return false;
      } finally {
         JDBCUtil.disconnect(conn, pstmt);
      }
      return true;
   }

   // CLOB 변환 함수
   private String getClobAsString(ResultSet rs, String colName) {
      try {
         java.sql.Clob clob = rs.getClob(colName);
         if (clob == null) return null;
         return clob.getSubString(1, (int) clob.length());
      } catch (Exception e) {
         try { return rs.getString(colName); } catch (Exception ignored) {}
         return null;
      }
   }
}
