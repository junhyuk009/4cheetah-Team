package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import model.common.JDBCUtil;
import model.dto.VoteCategoryDTO;

public class VoteCategoryDAO {

	// SELECT_ALL / SELECT_ONE / INSERT / UPDATE / DELETE
	// VOTE_CATEGORY_LIST : 카테고리 전체 조회
	private static final String SELECT_VOTE_CATEGORY_LIST = "SELECT " + "    VC.VOTE_CATEGORY_ID, " + "    VC.VOTE_CATEGORY_NAME " + "FROM VOTE_CATEGORY VC " + "ORDER BY VC.VOTE_CATEGORY_ID ASC";
	// VOTE_CATEGORY_DETAIL : 카테고리 1개 조회(ID)
	private static final String SELECT_VOTE_CATEGORY_DETAIL = "SELECT " + "    VC.VOTE_CATEGORY_ID, " + "    VC.VOTE_CATEGORY_NAME " + "FROM VOTE_CATEGORY VC " + "WHERE VC.VOTE_CATEGORY_ID = ?";
	// VOTE_CATEGORY_INSERT : 카테고리 추가(관리자)
	private static final String INSERT_VOTE_CATEGORY = "INSERT INTO VOTE_CATEGORY (VOTE_CATEGORY_ID, VOTE_CATEGORY_NAME) " + "VALUES (VOTE_CATEGORY_ID_SEQ.NEXTVAL, ?)";
	// VOTE_CATEGORY_UPDATE : 카테고리 수정(관리자)
	private static final String UPDATE_VOTE_CATEGORY = "UPDATE VOTE_CATEGORY " + "SET VOTE_CATEGORY_NAME = ? " + "WHERE VOTE_CATEGORY_ID = ?";
	// VOTE_CATEGORY_DELETE : 카테고리 삭제(관리자)
	private static final String DELETE_VOTE_CATEGORY = "DELETE FROM VOTE_CATEGORY WHERE VOTE_CATEGORY_ID = ?";
	
	// SELECT_ALL (VOTE_CATEGORY_LIST)
	public ArrayList<VoteCategoryDTO> selectAll(VoteCategoryDTO dto){
		ArrayList<VoteCategoryDTO> datas = new ArrayList<>();
		Connection conn = JDBCUtil.connect();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try { // 컨디션 체크
			if (!"VOTE_CATEGORY_LIST".equals(dto.getCondition())) {
				return datas;
			}
			pstmt = conn.prepareStatement(SELECT_VOTE_CATEGORY_LIST);
			rs = pstmt.executeQuery();
			// rs > DTO 리스트 변환
			while (rs.next()) {
				VoteCategoryDTO data = new VoteCategoryDTO();
				data.setVoteCategoryId(rs.getInt("VOTE_CATEGORY_ID"));
				data.setVoteCategoryName(rs.getString("VOTE_CATEGORY_NAME"));
				datas.add(data);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {if(rs != null) rs.close();
		} catch (Exception ignored) {}
			JDBCUtil.disconnect(conn, pstmt);
		}
		return datas;
	}
	// SELECT_ONE
	public VoteCategoryDTO selectOne(VoteCategoryDTO dto) {
		VoteCategoryDTO data = null;
		Connection conn = JDBCUtil.connect();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try { // 컨디션 체크
			if (!"VOTE_CATEGORY_DETAIL".equals(dto.getCondition())) {
				return null;
			}
			pstmt = conn.prepareStatement(SELECT_VOTE_CATEGORY_DETAIL);
			pstmt.setInt(1, dto.getVoteCategoryId());
			rs = pstmt.executeQuery();
			if (rs.next()) {
				data = new VoteCategoryDTO();
				data.setVoteCategoryId(rs.getInt("VOTE_CATEGORY_ID"));
				data.setVoteCategoryName(rs.getString("VOTE_CATEGORY_NAME"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {if (rs != null)rs.close();
		}catch(Exception ignored) {}
			JDBCUtil.disconnect(conn, pstmt);
		}
		return data;
	}
	
	// INSERT
	public boolean insert(VoteCategoryDTO dto) {
		Connection conn = JDBCUtil.connect();
		PreparedStatement pstmt = null;
		try {
			if (!"VOTE_CATEGORY_INSERT".equals(dto.getCondition())) {
				return false;
			}
			pstmt = conn.prepareStatement(INSERT_VOTE_CATEGORY);
			pstmt.setString(1, dto.getVoteCategoryName());
			
			int result = pstmt.executeUpdate();
			return result > 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}finally {
			JDBCUtil.disconnect(conn, pstmt);
		}
	}
	
	// UPDATE
	public boolean update(VoteCategoryDTO dto) {
		Connection conn = JDBCUtil.connect();
		PreparedStatement pstmt = null;
		try {
			if (!"VOTE_CATEGORY_UPDATE".equals(dto.getCondition())) {
				return false;
			}
			pstmt = conn.prepareStatement(UPDATE_VOTE_CATEGORY);
			pstmt.setString(1, dto.getVoteCategoryName());
			pstmt.setInt(2, dto.getVoteCategoryId());
			
			int result = pstmt.executeUpdate();
			return result > 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}finally {
			JDBCUtil.disconnect(conn, pstmt);
		}
	}
	
	// DELETE
	public boolean delete(VoteCategoryDTO dto) {
		Connection conn = JDBCUtil.connect();
		PreparedStatement pstmt = null;
		try {
			if (!"VOTE_CATEGORY_DELETE".equals(dto.getCondition())) {
				return false;
			}
			pstmt = conn.prepareStatement(DELETE_VOTE_CATEGORY);
			pstmt.setInt(1, dto.getVoteCategoryId());
			
			int result = pstmt.executeUpdate();
			return result > 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}finally {
			JDBCUtil.disconnect(conn, pstmt);
		}
	}
}