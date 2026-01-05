	package model.dao;
	
	import java.sql.Connection;
	import java.sql.PreparedStatement;
	import java.sql.ResultSet;
	
	import model.common.JDBCUtil;
	import model.dto.BoardLikeDTO;
	
	public class BoardLikeDAO {
		
		// 좋아요 개수
		private static final String SELECT_BOARD_LIKE_COUNT = "SELECT COUNT(*) LIKE_CNT " + "FROM BOARD_LIKE " + "WHERE BOARD_ID = ?";
		// 내가 눌렀는지(0/1)
		private static final String SELECT_BOARD_LIKE_CHECK = "SELECT COUNT(*) IS_LIKED " + "FROM BOARD_LIKE " + "WHERE BOARD_ID = ? " + "AND MEMBER_ID = ?";
		// 좋아요 추가(중복 방지 - Oracle 11g 호환)
		private static final String INSERT_BOARD_LIKE_SAFE = "INSERT INTO BOARD_LIKE (BOARD_LIKE_ID, BOARD_ID, MEMBER_ID) " + "SELECT BOARD_LIKE_ID_SEQ.NEXTVAL, ?, ? " + "FROM DUAL " + "WHERE NOT EXISTS ( " + "    SELECT 1 FROM BOARD_LIKE WHERE BOARD_ID = ? AND MEMBER_ID = ? " + ")";
		// 좋아요 취소
		private static final String DELETE_BOARD_LIKE = "DELETE FROM BOARD_LIKE " + "WHERE BOARD_ID = ? " + "AND MEMBER_ID = ?";
		
		// SELECT_ONE(COUNT / CHECK)
		
		public BoardLikeDTO selectOne(BoardLikeDTO dto) {
			BoardLikeDTO data = null;
			Connection conn = JDBCUtil.connect();
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			
			try {
				String condition = dto.getCondition();
				
				if ("BOARD_LIKE_COUNT".equals(condition)) {
					pstmt = conn.prepareStatement(SELECT_BOARD_LIKE_COUNT);
					pstmt.setInt(1, dto.getBoardId());
				}
				else if ("BOARD_LIKE_CHECK".equals(condition)) {
					pstmt = conn.prepareStatement(SELECT_BOARD_LIKE_CHECK);
					pstmt.setInt(1, dto.getBoardId());
					pstmt.setInt(2, dto.getMemberId());
				}
				else {
					return null;
				}
				
				rs = pstmt.executeQuery();
				
				if (rs.next()) {
					data = new BoardLikeDTO();
					data.setBoardId(dto.getBoardId());
					data.setMemberId(dto.getMemberId());
	
					if ("BOARD_LIKE_COUNT".equals(condition)) {
						data.setLikeCnt(rs.getInt("LIKE_CNT"));
					}
					else { // BOARD_LIKE_CHECK
						data.setIsLiked(rs.getInt("IS_LIKED")); // 0또는 1
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally {
				try {
					if(rs != null) rs.close();
				} catch (Exception ignored) {}
				JDBCUtil.disconnect(conn, pstmt);
			}
			return data;
		}
		
		// UPDATE (TOGGLE) >> 눌렀으면 DELETE, 아니면 INSERT
		public boolean update(BoardLikeDTO dto) {
			if (!"BOARD_LIKE_TOGGLE".equals(dto.getCondition())) {
				return false;
			}
			Connection conn = JDBCUtil.connect();
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			
			try {
				conn.setAutoCommit(false);
				
				// 현재 좋아요 여부 체크
				pstmt = conn.prepareStatement(SELECT_BOARD_LIKE_CHECK);
				pstmt.setInt(1, dto.getBoardId());
				pstmt.setInt(2, dto.getMemberId());
				rs = pstmt.executeQuery();
				
				int isLiked = 0;
				if (rs.next()) {
					isLiked = rs.getInt("IS_LIKED"); // 0 이나 1
				}
				
				// 리소스 정리 (다른 쿼리 위해서)
				rs.close();
				rs = null;
				pstmt.close();
				pstmt = null;
				
				int result = 0;
				
				// 토글 실행
				if (isLiked > 0) { // 이미 눌렀다면 취소 (DELETE)				
					pstmt = conn.prepareStatement(DELETE_BOARD_LIKE);
					pstmt.setInt(1, dto.getBoardId());
					pstmt.setInt(2, dto.getMemberId());
					result = pstmt.executeUpdate();
					
					// 토글 후 상태 (취소)
					dto.setIsLiked(0);
				}else { // 아직 안눌렀음 > 추가 (INSERT, 중복 방지) 
					pstmt = conn.prepareStatement(INSERT_BOARD_LIKE_SAFE);
					pstmt.setInt(1, dto.getBoardId());
					pstmt.setInt(2, dto.getMemberId());
					pstmt.setInt(3, dto.getBoardId());
					pstmt.setInt(4, dto.getMemberId());
					result = pstmt.executeUpdate();
					
					// result == 1이면 추가 성공, result == 0 이면 이미 존재라서 추가 안됨
					dto.setIsLiked(1);
				}
				if (result <= 0) {
					conn.rollback();
					return false;
				}
				
				conn.commit();
				return true;
				
			} catch (Exception e) {
				try {conn.rollback(); } catch (Exception ignored) {}
					e.printStackTrace();
					return false;
				}finally {
					try {
						if (rs != null)rs.close(); 
					} catch (Exception ignored) {}
					JDBCUtil.disconnect(conn, pstmt);
				}
			}
		}
