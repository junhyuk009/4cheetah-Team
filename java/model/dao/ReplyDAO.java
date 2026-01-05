package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import model.common.JDBCUtil;
import model.dto.ReplyDTO;

public class ReplyDAO {
	
	// 댓글 목록 (최신순 / 오래된순)
	// [REPLY_LIST_RECENT] 최신순 댓글 목록
	private static final String SELECT_REPLY_LIST_RECENT = "SELECT " + "R.REPLY_ID, " + "R.BOARD_ID, " + "R.MEMBER_ID, " + "CASE WHEN M.MEMBER_ROLE = 'WITHDRAWN' THEN '탈퇴한 회원' " + "ELSE M.MEMBER_NICKNAME END WRITER_NICKNAME, " + "R.REPLY_CONTENT " + "FROM REPLY R " + "JOIN MEMBER M ON M.MEMBER_ID = R.MEMBER_ID " + "WHERE R.BOARD_ID = ? " + "ORDER BY R.REPLY_ID DESC";
	// [REPLY_LIST_OLDEST] 오래된순 댓글 목록
	private static final String SELECT_REPLY_LIST_OLDEST = "SELECT " + "R.REPLY_ID, " + "R.BOARD_ID, " + "R.MEMBER_ID, " + "CASE WHEN M.MEMBER_ROLE = 'WITHDRAWN' THEN '탈퇴한 회원' " + "ELSE M.MEMBER_NICKNAME END WRITER_NICKNAME, " + "R.REPLY_CONTENT " + "FROM REPLY R " + "JOIN MEMBER M ON M.MEMBER_ID = R.MEMBER_ID " + "WHERE R.BOARD_ID = ? " + "ORDER BY R.REPLY_ID ASC";
	// [REPLY_INSERT] 댓글 작성
	private static final String INSERT_REPLY = "INSERT INTO REPLY (REPLY_ID, BOARD_ID, MEMBER_ID, REPLY_CONTENT) " + "VALUES (REPLY_ID_SEQ.NEXTVAL, ?, ?, ?)";
	// [REPLY_UPDATE] 댓글 수정(본인만 가능)
	private static final String UPDATE_REPLY = "UPDATE REPLY SET REPLY_CONTENT = ? WHERE REPLY_ID = ? AND MEMBER_ID = ?";
	// REPLY_ADMIN_DELETE : 관리자 삭제(내용 치환)
	 private static final String UPDATE_REPLY_ADMIN_DELETE = "UPDATE REPLY R " + "SET R.REPLY_CONTENT = '관리자에 의해 삭제된 댓글입니다.' " + "WHERE R.REPLY_ID = ? " + "AND EXISTS ( " + "    SELECT 1 FROM MEMBER M " + "    WHERE M.MEMBER_ID = ? " +            "    AND M.MEMBER_ROLE = 'ADMIN' " + ")";
	// [REPLY_DELETE] 댓글 삭제(본인 또는 관리자만 가능)
	private static final String DELETE_REPLY = "DELETE FROM REPLY R " + "WHERE R.REPLY_ID = ? " + "AND ( " + " R.MEMBER_ID = ? " + " OR EXISTS ( " + " SELECT 1 FROM MEMBER M " + " WHERE M.MEMBER_ID = ? " +  " AND M.MEMBER_ROLE = 'ADMIN' " + "    ) " + ")";
	// SELECT_ALL: 댓글 목록
	public ArrayList<ReplyDTO> selectAll(ReplyDTO dto) {
		ArrayList<ReplyDTO> datas = new ArrayList<>();
		
		Connection conn = JDBCUtil.connect(); // DB연결
		PreparedStatement pstmt = null; // SQL 실행 준비
		ResultSet rs = null; // SELECT 결과
		
		try {
			String condition = dto.getCondition(); // 어떤 기능 요청인지 확인
			
			// 컨디션에 따라 SQL 선택
			if ("REPLY_LIST_RECENT".equals(condition)) {
				pstmt = conn.prepareStatement(SELECT_REPLY_LIST_RECENT);
				pstmt.setInt(1, dto.getBoardId()); // ?자리에 BOARD_ID 넣기
			}
			else if ("REPLY_LIST_OLDEST".equals(condition)) {
				pstmt = conn.prepareStatement(SELECT_REPLY_LIST_OLDEST);
				pstmt.setInt(1, dto.getBoardId());
			}
			else {
				return datas; // 지원한지 않는 컨디션이면 빈 리스트
			}
			// SELECT 실행 > rs에 결과가 들어옴
			rs = pstmt.executeQuery();
			
			// rs 한 줄 = 댓글1개 > ReplyDTO로 변환해서 리스트에 담기
			while (rs.next()) {
				ReplyDTO data = new ReplyDTO();
				
				data.setReplyId(rs.getInt("REPLY_ID"));
				data.setBoardId(rs.getInt("BOARD_ID"));
				data.setMemberId(rs.getInt("MEMBER_ID"));
				
				// JOIN + 탈퇴회원 WRITER_NICKNAME
				data.setWriterNickname(rs.getString("WRITER_NICKNAME"));
				data.setReplyContent(rs.getString("REPLY_CONTENT"));
				datas.add(data);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally { // 자원 정리 
			try {
				if(rs != null)rs.close();
			} catch (Exception ignored) {}
			JDBCUtil.disconnect(conn, pstmt);
		}
		return datas;
	}
	
	// 댓글 작성
	public boolean insert(ReplyDTO dto) {
		Connection conn = JDBCUtil.connect();
		PreparedStatement pstmt = null;
		
		try { // 컨디션 확인
			if (!"REPLY_INSERT".equals(dto.getCondition())) {
				return false;
			} // SQL 준비
			pstmt = conn.prepareStatement(INSERT_REPLY);
			// 값 세팅
			pstmt.setInt(1, dto.getBoardId());
			pstmt.setInt(2, dto.getMemberId());
			pstmt.setString(3, dto.getReplyContent());
			
			// INSERT 실행 결과가 1이상이면 성공
			return pstmt.executeUpdate() > 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}finally {
			JDBCUtil.disconnect(conn, pstmt);
		}
	}
	// UPDATE: 댓글 수정(본인만)
	public boolean update(ReplyDTO dto) {
        Connection conn = JDBCUtil.connect();
        PreparedStatement pstmt = null;

        try {
            String condition = dto.getCondition();

            // 1) 본인 댓글 수정
            if ("REPLY_UPDATE".equals(condition)) {
                pstmt = conn.prepareStatement(UPDATE_REPLY);
                pstmt.setString(1, dto.getReplyContent()); // 바꿀 내용
                pstmt.setInt(2, dto.getReplyId());         // 대상 댓글
                pstmt.setInt(3, dto.getMemberId());        // 작성자 본인인지 체크
            }
            // 2) 관리자 삭제(내용 치환)
            else if ("REPLY_ADMIN_DELETE".equals(condition)) {
                pstmt = conn.prepareStatement(UPDATE_REPLY_ADMIN_DELETE);
                pstmt.setInt(1, dto.getReplyId());   // 대상 댓글
                pstmt.setInt(2, dto.getMemberId());  // 요청자(관리자) ID로 ADMIN인지 검증
            }
            else {
                return false;
            }

            return pstmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            JDBCUtil.disconnect(conn, pstmt);
        }
    }

	// DELETE: 댓글 삭제(본인 or 관리자)
	public boolean delete(ReplyDTO dto) {
		Connection conn = JDBCUtil.connect();
		PreparedStatement pstmt = null;
		try { // 컨디션 확인
			if (!"REPLY_DELETE".equals(dto.getCondition())) {
				return false;
			} // SQL 준비
			pstmt = conn.prepareStatement(DELETE_REPLY);
			
			// 값 세팅
			pstmt.setInt(1, dto.getReplyId()); // 삭제할 댓글
			pstmt.setInt(2, dto.getMemberId()); // 본인인지 체크
			pstmt.setInt(3, dto.getMemberId()); // ADMIN인지 체크(요청자)
			// DELETE 성공하면 1이상
			return pstmt.executeUpdate() > 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}finally {
			JDBCUtil.disconnect(conn, pstmt);
		}
	}
}
