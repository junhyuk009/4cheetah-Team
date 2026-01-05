package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import model.common.JDBCUtil;
import model.dto.BoardDTO;

public class BoardDAO {
	// SELECT ALL
	// CATEGORY_LIST: 해당 카테고리 글 목록(관리자 글 제외)
	private static final String SELECT_CATEGORY_LIST = "SELECT " + "B.BOARD_ID, " + "B.MEMBER_ID, " + "M.MEMBER_ROLE WRITER_ROLE, " + "CASE WHEN M.MEMBER_ROLE = 'WITHDRAWN' THEN '탈퇴한 회원' ELSE M.MEMBER_NICKNAME END WRITER_NICKNAME, " + "B.BOARD_TITLE, " + "B.BOARD_VIEWS, " + "B.BOARD_CATEGORY, " + "NVL(L.LIKE_CNT, 0) LIKE_CNT " + "FROM BOARD B " + "JOIN MEMBER M ON M.MEMBER_ID = B.MEMBER_ID " + "LEFT JOIN (SELECT BOARD_ID, COUNT(*) LIKE_CNT FROM BOARD_LIKE GROUP BY BOARD_ID) L " + "ON L.BOARD_ID = B.BOARD_ID " + "WHERE B.BOARD_CATEGORY = ? " + "AND M.MEMBER_ROLE <> 'ADMIN' " + "ORDER BY B.BOARD_ID DESC";

	// BOARD_NOTICE_LIST: 공지(관리자 글만)
	private static final String SELECT_BOARD_NOTICE_LIST = "SELECT " + "B.BOARD_ID, " + "B.MEMBER_ID, " + "M.MEMBER_ROLE WRITER_ROLE, " + "M.MEMBER_NICKNAME WRITER_NICKNAME, " + "B.BOARD_TITLE, " + "B.BOARD_VIEWS, " + "B.BOARD_CATEGORY, " + "NVL(L.LIKE_CNT, 0) LIKE_CNT " + "FROM BOARD B " + "JOIN MEMBER M ON M.MEMBER_ID = B.MEMBER_ID " + "LEFT JOIN (SELECT BOARD_ID, COUNT(*) LIKE_CNT FROM BOARD_LIKE GROUP BY BOARD_ID) L " + "ON L.BOARD_ID = B.BOARD_ID " + "WHERE B.BOARD_CATEGORY = ? " + "AND M.MEMBER_ROLE = 'ADMIN' " + "ORDER BY B.BOARD_ID DESC";

	// MY_BOARD_WRITE_LIST: 내가 쓴 글
	private static final String SELECT_MY_BOARD_WRITE_LIST = "SELECT " + "B.BOARD_ID, " + "B.MEMBER_ID, " + "M.MEMBER_ROLE WRITER_ROLE, " + "CASE WHEN M.MEMBER_ROLE = 'WITHDRAWN' THEN '탈퇴한 회원' ELSE M.MEMBER_NICKNAME END WRITER_NICKNAME, " + "B.BOARD_TITLE, " + "B.BOARD_VIEWS, " + "B.BOARD_CATEGORY, " + "NVL(L.LIKE_CNT, 0) LIKE_CNT " + "FROM BOARD B " + "JOIN MEMBER M ON M.MEMBER_ID = B.MEMBER_ID " + "LEFT JOIN (SELECT BOARD_ID, COUNT(*) LIKE_CNT FROM BOARD_LIKE GROUP BY BOARD_ID) L " + "ON L.BOARD_ID = B.BOARD_ID " + "WHERE B.MEMBER_ID = ? " + "ORDER BY B.BOARD_ID DESC";

	// MY_BOARD_LIKE_LIST: 내가 좋아요 누른 글
	private static final String SELECT_MY_BOARD_LIKE_LIST = "SELECT " + "B.BOARD_ID, " + "B.MEMBER_ID, " + "M.MEMBER_ROLE WRITER_ROLE, " + "CASE WHEN M.MEMBER_ROLE = 'WITHDRAWN' THEN '탈퇴한 회원' ELSE M.MEMBER_NICKNAME END WRITER_NICKNAME, " + "B.BOARD_TITLE, " + "B.BOARD_VIEWS, " + "B.BOARD_CATEGORY, " + "NVL(L.LIKE_CNT, 0) LIKE_CNT " + "FROM BOARD_LIKE BL " + "JOIN BOARD B ON B.BOARD_ID = BL.BOARD_ID " + "JOIN MEMBER M ON M.MEMBER_ID = B.MEMBER_ID " + "LEFT JOIN (SELECT BOARD_ID, COUNT(*) LIKE_CNT FROM BOARD_LIKE GROUP BY BOARD_ID) L " + "ON L.BOARD_ID = B.BOARD_ID " + "WHERE BL.MEMBER_ID = ? " + "ORDER BY BL.BOARD_LIKE_ID DESC";

	// BOARD_LIKE_MEMBER_LIST: 특정 게시글에 좋아요 누른 회원 닉네임 목록
	private static final String SELECT_BOARD_LIKE_MEMBER_LIST = "SELECT " + "  BL.MEMBER_ID, " + "  CASE WHEN M.MEMBER_ROLE = 'WITHDRAWN' THEN '탈퇴한 회원' ELSE M.MEMBER_NICKNAME END LIKE_MEMBER_NICKNAME " + "FROM BOARD_LIKE BL " + "JOIN MEMBER M ON M.MEMBER_ID = BL.MEMBER_ID " + "WHERE BL.BOARD_ID = ? " + "ORDER BY BL.BOARD_LIKE_ID DESC";

	// BOARD_BEST_LIKE_LIST: 베스트(좋아요 많은 글)
	private static final String SELECT_BOARD_BEST_LIKE_LIST = "SELECT " + "B.BOARD_ID, " + "B.MEMBER_ID, " + "M.MEMBER_ROLE WRITER_ROLE, " + "CASE WHEN M.MEMBER_ROLE = 'WITHDRAWN' THEN '탈퇴한 회원' ELSE M.MEMBER_NICKNAME END WRITER_NICKNAME, " + "B.BOARD_TITLE, " + "B.BOARD_VIEWS, " + "B.BOARD_CATEGORY, " + "COUNT(BL.MEMBER_ID) LIKE_CNT " + "FROM BOARD B " + "JOIN MEMBER M ON M.MEMBER_ID = B.MEMBER_ID " + "JOIN BOARD_LIKE BL ON BL.BOARD_ID = B.BOARD_ID " + "WHERE B.BOARD_CATEGORY = ? " + "AND M.MEMBER_ROLE <> 'ADMIN' " + "GROUP BY " + "B.BOARD_ID, B.MEMBER_ID, M.MEMBER_ROLE, M.MEMBER_NICKNAME, B.BOARD_TITLE, B.BOARD_VIEWS, B.BOARD_CATEGORY " + "HAVING COUNT(BL.MEMBER_ID) >= 10 " + "ORDER BY LIKE_CNT DESC, B.BOARD_ID DESC";

	// BOARD_SEARCH_TITLE: 제목 검색
	private static final String SELECT_BOARD_SEARCH_TITLE = "SELECT " + "B.BOARD_ID, " + "B.MEMBER_ID, " + "M.MEMBER_ROLE WRITER_ROLE, " + "CASE WHEN M.MEMBER_ROLE = 'WITHDRAWN' THEN '탈퇴한 회원' ELSE M.MEMBER_NICKNAME END WRITER_NICKNAME, " + "B.BOARD_TITLE, " + "B.BOARD_VIEWS, " + "B.BOARD_CATEGORY, " + "NVL(L.LIKE_CNT, 0) LIKE_CNT " + "FROM BOARD B " + "JOIN MEMBER M ON M.MEMBER_ID = B.MEMBER_ID " + "LEFT JOIN (SELECT BOARD_ID, COUNT(*) LIKE_CNT FROM BOARD_LIKE GROUP BY BOARD_ID) L " + "ON L.BOARD_ID = B.BOARD_ID " + "WHERE B.BOARD_CATEGORY = ? " + "AND B.BOARD_TITLE LIKE ? " + "ORDER BY B.BOARD_ID DESC";

	// BOARD_SEARCH_WRITER: 작성자 검색(닉네임 포함검색, ACTIVE만)
	private static final String SELECT_BOARD_SEARCH_WRITER = "SELECT " + "B.BOARD_ID, " + "B.MEMBER_ID, " + "M.MEMBER_ROLE WRITER_ROLE, " + "M.MEMBER_NICKNAME WRITER_NICKNAME, " + "B.BOARD_TITLE, " + "B.BOARD_VIEWS, " + "B.BOARD_CATEGORY, " + "NVL(L.LIKE_CNT, 0) LIKE_CNT " + "FROM BOARD B " + "JOIN MEMBER M ON M.MEMBER_ID = B.MEMBER_ID " + "LEFT JOIN (SELECT BOARD_ID, COUNT(*) LIKE_CNT FROM BOARD_LIKE GROUP BY BOARD_ID) L " + "ON L.BOARD_ID = B.BOARD_ID " + "WHERE B.BOARD_CATEGORY = ? " + "AND M.MEMBER_ROLE = 'ACTIVE' " + "AND M.MEMBER_NICKNAME LIKE ? " + "ORDER BY B.BOARD_ID DESC";

	// BOARD_SEARCH_CONTENT: 내용 검색(CLOB)
	private static final String SELECT_BOARD_SEARCH_CONTENT = "SELECT " + "B.BOARD_ID, " + "B.MEMBER_ID, " + "M.MEMBER_ROLE WRITER_ROLE, " + "CASE WHEN M.MEMBER_ROLE = 'WITHDRAWN' THEN '탈퇴한 회원' ELSE M.MEMBER_NICKNAME END WRITER_NICKNAME, " + "B.BOARD_TITLE, " + "B.BOARD_VIEWS, " + "B.BOARD_CATEGORY, " + "NVL(L.LIKE_CNT, 0) LIKE_CNT " + "FROM BOARD B " + "JOIN MEMBER M ON M.MEMBER_ID = B.MEMBER_ID " + "LEFT JOIN (SELECT BOARD_ID, COUNT(*) LIKE_CNT FROM BOARD_LIKE GROUP BY BOARD_ID) L " + "ON L.BOARD_ID = B.BOARD_ID " + "WHERE B.BOARD_CATEGORY = ? " + "AND DBMS_LOB.INSTR(B.BOARD_CONTENT, ?) > 0 " + "ORDER BY B.BOARD_ID DESC";

	// ADMIN_BOARD_CATEGORY: 관리자 게시글 관리 카테고리 목록(중복 제거)
	private static final String SELECT_ADMIN_BOARD_CATEGORY = "SELECT DISTINCT BOARD_CATEGORY FROM BOARD ORDER BY BOARD_CATEGORY ASC";

	// SELECT ONE
	private static final String SELECT_BOARD_DETAIL = "SELECT " + "B.BOARD_ID, " + "B.MEMBER_ID, " + "M.MEMBER_ROLE WRITER_ROLE, " + "CASE WHEN M.MEMBER_ROLE = 'WITHDRAWN' THEN '탈퇴한 회원' ELSE M.MEMBER_NICKNAME END WRITER_NICKNAME, " + "B.BOARD_TITLE, " + "B.BOARD_CONTENT, " + "B.BOARD_VIEWS, " + "B.BOARD_CATEGORY, " + "NVL(L.LIKE_CNT, 0) LIKE_CNT " + "FROM BOARD B " + "JOIN MEMBER M ON M.MEMBER_ID = B.MEMBER_ID " + "LEFT JOIN (SELECT BOARD_ID, COUNT(*) LIKE_CNT FROM BOARD_LIKE GROUP BY BOARD_ID) L " + "ON L.BOARD_ID = B.BOARD_ID " + "WHERE B.BOARD_ID = ?";

	// BOARD_EXISTS: 게시글 존재 여부 체크
	private static final String SELECT_BOARD_EXISTS = "SELECT BOARD_ID FROM BOARD WHERE BOARD_ID = ?";


	// INSERT / UPDATE / DELETE
	private static final String INSERT_BOARD = "INSERT INTO BOARD (BOARD_ID, MEMBER_ID, BOARD_TITLE, BOARD_CONTENT, BOARD_CATEGORY) " + "VALUES (BOARD_ID_SEQ.NEXTVAL, ?, ?, ?, ?)";
	private static final String UPDATE_BOARD = "UPDATE BOARD SET BOARD_TITLE = ?, BOARD_CONTENT = ? WHERE BOARD_ID = ? AND MEMBER_ID = ?";
	private static final String UPDATE_BOARD_VIEWS = "UPDATE BOARD SET BOARD_VIEWS = BOARD_VIEWS + 1 WHERE BOARD_ID = ?";
	private static final String DELETE_BOARD = "DELETE FROM BOARD WHERE BOARD_ID = ? AND MEMBER_ID = ?";

	// INSERT 직후 같은 세션에서만 CURRVAL을 조회할 수 있음
	private static final String SELECT_BOARD_ID_CURRVAL = "SELECT BOARD_ID_SEQ.CURRVAL AS BOARD_ID FROM DUAL";

	// SELECT_ALL(condition 분기)
	public ArrayList<BoardDTO> selectAll(BoardDTO boardDTO) {

		ArrayList<BoardDTO> datas = new ArrayList<>();
		Connection conn = JDBCUtil.connect();
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			String condition = boardDTO.getCondition();

			// 컨디션에 맞는 SQL 선택 + ? 값 세팅
			if ("CATEGORY_LIST".equals(condition)) {
				pstmt = conn.prepareStatement(SELECT_CATEGORY_LIST);
				pstmt.setString(1, boardDTO.getBoardCategory());
			}
			else if ("BOARD_NOTICE_LIST".equals(condition)) {
				pstmt = conn.prepareStatement(SELECT_BOARD_NOTICE_LIST);
				pstmt.setString(1, boardDTO.getBoardCategory());
			}
			else if ("MY_BOARD_WRITE_LIST".equals(condition)) {
				pstmt = conn.prepareStatement(SELECT_MY_BOARD_WRITE_LIST);
				pstmt.setInt(1, boardDTO.getMemberId());
			}
			else if ("MY_BOARD_LIKE_LIST".equals(condition)) {
				pstmt = conn.prepareStatement(SELECT_MY_BOARD_LIKE_LIST);
				pstmt.setInt(1, boardDTO.getMemberId());
			}
			else if ("BOARD_LIKE_MEMBER_LIST".equals(condition)) {
				pstmt = conn.prepareStatement(SELECT_BOARD_LIKE_MEMBER_LIST);
				pstmt.setInt(1, boardDTO.getBoardId()); // 조회할 게시글 번호
			}
			else if ("BOARD_BEST_LIKE_LIST".equals(condition)) {
				pstmt = conn.prepareStatement(SELECT_BOARD_BEST_LIKE_LIST);
				pstmt.setString(1, boardDTO.getBoardCategory());
			}
			else if ("BOARD_SEARCH_TITLE".equals(condition)) {
				pstmt = conn.prepareStatement(SELECT_BOARD_SEARCH_TITLE);
				pstmt.setString(1, boardDTO.getBoardCategory());
				pstmt.setString(2, "%" + boardDTO.getKeyword() + "%");
			}
			else if ("BOARD_SEARCH_WRITER".equals(condition)) {
				pstmt = conn.prepareStatement(SELECT_BOARD_SEARCH_WRITER);
				pstmt.setString(1, boardDTO.getBoardCategory());
				pstmt.setString(2, "%" + boardDTO.getKeyword() + "%");
			}
			else if ("BOARD_SEARCH_CONTENT".equals(condition)) {
				pstmt = conn.prepareStatement(SELECT_BOARD_SEARCH_CONTENT);
				pstmt.setString(1, boardDTO.getBoardCategory());
				pstmt.setString(2, boardDTO.getKeyword());
			}
			else if ("ADMIN_BOARD_CATEGORY".equals(condition)) {
				pstmt = conn.prepareStatement(SELECT_ADMIN_BOARD_CATEGORY);
			}
			else {
				return datas;
			}

			rs = pstmt.executeQuery();

			// ADMIN_BOARD_CATEGORY는 컬럼 구조가 다르므로 별도 매핑
			if ("ADMIN_BOARD_CATEGORY".equals(condition)) {
				while (rs.next()) {
					BoardDTO data = new BoardDTO();
					data.setBoardCategory(rs.getString("BOARD_CATEGORY"));
					datas.add(data);
				}
				return datas;
			}
			// BOARD_LIKE_MEMBER_LIST도 컬럼 구조가 다르므로 별도 처리
			if ("BOARD_LIKE_MEMBER_LIST".equals(condition)) {
				while (rs.next()) {
					BoardDTO data = new BoardDTO();
					data.setMemberId(rs.getInt("MEMBER_ID")); // 좋아요 누른 사람의 memberId
					data.setLikeMemberNickname(rs.getString("LIKE_MEMBER_NICKNAME")); // 좋아요 누른 사람 닉네임
					datas.add(data);
				}
				return datas;
			}

			while (rs.next()) {
				BoardDTO data = new BoardDTO();

				data.setBoardId(rs.getInt("BOARD_ID"));
				data.setMemberId(rs.getInt("MEMBER_ID"));
				data.setWriterRole(rs.getString("WRITER_ROLE"));
				data.setWriterNickname(rs.getString("WRITER_NICKNAME"));

				data.setBoardTitle(rs.getString("BOARD_TITLE"));
				data.setBoardViews(rs.getInt("BOARD_VIEWS"));
				data.setBoardCategory(rs.getString("BOARD_CATEGORY"));
				data.setLikeCnt(rs.getInt("LIKE_CNT"));

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

	// SELECT_ONE (BOARD_DETAIL / BOARD_EXISTS)
	public BoardDTO selectOne(BoardDTO boardDTO) {

	    BoardDTO data = null;
	    Connection conn = JDBCUtil.connect();
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;

	    try {
	        String condition = boardDTO.getCondition();

	        // 1) 게시글 상세
	        if ("BOARD_DETAIL".equals(condition)) {

	            pstmt = conn.prepareStatement(SELECT_BOARD_DETAIL);
	            pstmt.setInt(1, boardDTO.getBoardId());
	            rs = pstmt.executeQuery();

	            if (rs.next()) {
	                data = new BoardDTO();
	                data.setBoardId(rs.getInt("BOARD_ID"));
	                data.setMemberId(rs.getInt("MEMBER_ID"));
	                data.setWriterRole(rs.getString("WRITER_ROLE"));
	                data.setWriterNickname(rs.getString("WRITER_NICKNAME"));

	                data.setBoardTitle(rs.getString("BOARD_TITLE"));
	                data.setBoardContent(getClobAsString(rs, "BOARD_CONTENT"));

	                data.setBoardViews(rs.getInt("BOARD_VIEWS"));
	                data.setBoardCategory(rs.getString("BOARD_CATEGORY"));
	                data.setLikeCnt(rs.getInt("LIKE_CNT"));
	            }
	        }

	        // 2) 게시글 존재 여부 체크 (좋아요 토글 서블릿에서 사용)
	        else if ("BOARD_EXISTS".equals(condition)) {

	            pstmt = conn.prepareStatement(SELECT_BOARD_EXISTS);
	            pstmt.setInt(1, boardDTO.getBoardId());
	            rs = pstmt.executeQuery();

	            if (rs.next()) {
	                data = new BoardDTO();
	                data.setBoardId(rs.getInt("BOARD_ID")); // 존재만 확인하면 됨
	            }
	        }

	        else {
	            return null;
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        try { if (rs != null) rs.close(); } catch (Exception ignored) {}
	        JDBCUtil.disconnect(conn, pstmt);
	    }

	    return data;
	}

	// INSERT 직후 PK 반환
	public Integer insertReturnId(BoardDTO boardDTO) {
		Connection conn = JDBCUtil.connect();
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			if (!"BOARD_INSERT".equals(boardDTO.getCondition())) {
				return null;
			}

			pstmt = conn.prepareStatement(INSERT_BOARD);
			pstmt.setInt(1, boardDTO.getMemberId());
			pstmt.setString(2, boardDTO.getBoardTitle());
			pstmt.setString(3, boardDTO.getBoardContent());
			pstmt.setString(4, boardDTO.getBoardCategory());

			int result = pstmt.executeUpdate();
			if (result <= 0) {
				return null;
			}

			pstmt.close();
			pstmt = conn.prepareStatement(SELECT_BOARD_ID_CURRVAL);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				return rs.getInt("BOARD_ID");
			}

			return null;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try { if (rs != null) rs.close(); } catch (Exception ignored) {}
			JDBCUtil.disconnect(conn, pstmt);
		}
	}

	// UPDATE
	public boolean update(BoardDTO boardDTO) {
		Connection conn = JDBCUtil.connect();
		PreparedStatement pstmt = null;

		try {
			String condition = boardDTO.getCondition();

			if ("BOARD_UPDATE".equals(condition)) {
				pstmt = conn.prepareStatement(UPDATE_BOARD);
				pstmt.setString(1, boardDTO.getBoardTitle());
				pstmt.setString(2, boardDTO.getBoardContent());
				pstmt.setInt(3, boardDTO.getBoardId());
				pstmt.setInt(4, boardDTO.getMemberId());
			}
			else if ("UPDATE_BOARD_VIEWS".equals(condition)) {
				pstmt = conn.prepareStatement(UPDATE_BOARD_VIEWS);
				pstmt.setInt(1, boardDTO.getBoardId());
			}
			else {
				return false;
			}

			int result = pstmt.executeUpdate();
			return result > 0;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			JDBCUtil.disconnect(conn, pstmt);
		}
	}

	// DELETE
	public boolean delete(BoardDTO boardDTO) {
		Connection conn = JDBCUtil.connect();
		PreparedStatement pstmt = null;

		try {
			if (!"BOARD_DELETE".equals(boardDTO.getCondition())) {
				return false;
			}

			pstmt = conn.prepareStatement(DELETE_BOARD);
			pstmt.setInt(1, boardDTO.getBoardId());
			pstmt.setInt(2, boardDTO.getMemberId());

			int result = pstmt.executeUpdate();
			return result > 0;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			JDBCUtil.disconnect(conn, pstmt);
		}
	}

	// CLOB 변환 함수   
	private String getClobAsString(ResultSet rs, String colName) {
		try {
			java.sql.Clob clob = rs.getClob(colName);
			if (clob == null) return null;
			return clob.getSubString(1, (int) clob.length());
		} catch (Exception e) {
			return null;
		}
	}
}