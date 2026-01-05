package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import model.common.JDBCUtil;
import model.dto.AnimeDTO;

public class AnimeDAO {
	// SELECT_ALL (컨디션 분기)
	// MAIN_ANIMELIST : 메인 최근 애니 12개
	private static final String SELECT_MAIN_ANIMELIST = "SELECT " + "    A.ANIME_ID, " + "    A.ANIME_TITLE, " + "    A.ANIME_YEAR, " + "    A.ANIME_QUARTER, " + "    A.ANIME_THUMBNAIL_URL " + "FROM ( " + "    SELECT " + "        A.ANIME_ID, " + "        A.ANIME_TITLE, " + "        A.ANIME_YEAR, " + "        A.ANIME_QUARTER, " + "        A.ANIME_THUMBNAIL_URL " + "    FROM ANIME A " + "    ORDER BY A.ANIME_ID DESC " + ") A " + "WHERE ROWNUM <= 12";
	// ANIME_LIST_RECENT : 전체(최신등록순)
	private static final String SELECT_ANIME_LIST_RECENT = "SELECT " + "    A.ANIME_ID, " + "    A.ANIME_TITLE, " + "    A.ANIME_YEAR, " + "    A.ANIME_QUARTER, " + "    A.ANIME_THUMBNAIL_URL " + "FROM ANIME A " + "ORDER BY A.ANIME_ID DESC";
	// ANIME_LIST_TITLE : 전체(제목 가나다순)
	private static final String SELECT_ANIME_LIST_TITLE = "SELECT " + "    A.ANIME_ID, " + "    A.ANIME_TITLE, " + "    A.ANIME_YEAR, " + "    A.ANIME_QUARTER, " + "    A.ANIME_THUMBNAIL_URL " + "FROM ANIME A " + "ORDER BY A.ANIME_TITLE ASC, A.ANIME_ID DESC";
	// ANIME_LIST_YEAR : 전체(방영년도별, NULL은 뒤로)
	private static final String SELECT_ANIME_LIST_YEAR = "SELECT " + "    A.ANIME_ID, " + "    A.ANIME_TITLE, " + "    A.ANIME_YEAR, " + "    A.ANIME_QUARTER, " + "    A.ANIME_THUMBNAIL_URL " + "FROM ANIME A " + "ORDER BY A.ANIME_YEAR DESC NULLS LAST, A.ANIME_ID DESC";
	// ANIME_SEARCH_TITLE : 제목 검색
	private static final String SELECT_ANIME_SEARCH_TITLE = "SELECT " + "    A.ANIME_ID, " + "    A.ANIME_TITLE, " + "    A.ANIME_YEAR, " + "    A.ANIME_QUARTER, " + "    A.ANIME_THUMBNAIL_URL " + "FROM ANIME A " + "WHERE A.ANIME_TITLE LIKE ? " + "ORDER BY A.ANIME_ID DESC";
	// ANIME_SEARCH_STORY : 줄거리 검색(CLOB)
	private static final String SELECT_ANIME_SEARCH_STORY = "SELECT " + "    A.ANIME_ID, " + "    A.ANIME_TITLE, " + "    A.ANIME_YEAR, " + "    A.ANIME_QUARTER, " + "    A.ANIME_THUMBNAIL_URL " + "FROM ANIME A " + "WHERE DBMS_LOB.INSTR(A.ANIME_STORY, ?) > 0 " + "ORDER BY A.ANIME_ID DESC";

	// PAGE 전용 공통 
	private static final String SELECT_ANIME_LIST_PAGE_BASE = "SELECT * FROM (" + "  SELECT ROWNUM RN, A.* FROM (" + "    SELECT ANIME_ID, ANIME_TITLE, ANIME_YEAR, ANIME_QUARTER, ANIME_THUMBNAIL_URL " + "    FROM ANIME %s" + "  ) A WHERE ROWNUM <= ?" + ") WHERE RN >= ?";
	// PAGE + 제목 검색
	private static final String SELECT_ANIME_LIST_PAGE_TITLE = "SELECT * FROM (" + "  SELECT ROWNUM RN, A.* FROM (" + "    SELECT ANIME_ID, ANIME_TITLE, ANIME_YEAR, ANIME_QUARTER, ANIME_THUMBNAIL_URL " + "    FROM ANIME WHERE ANIME_TITLE LIKE ? %s" + "  ) A WHERE ROWNUM <= ?" + ") WHERE RN >= ?";
	// PAGE + 줄거리 검색 
	private static final String SELECT_ANIME_LIST_PAGE_STORY = "SELECT * FROM (" + "  SELECT ROWNUM RN, A.* FROM (" + "    SELECT ANIME_ID, ANIME_TITLE, ANIME_YEAR, ANIME_QUARTER, ANIME_THUMBNAIL_URL " + "    FROM ANIME WHERE DBMS_LOB.INSTR(ANIME_STORY, ?) > 0 %s" + "  ) A WHERE ROWNUM <= ?" + ") WHERE RN >= ?";
	
	// SELECT_ONE
	// ANIME_DETAIL : 상세보기
	private static final String SELECT_ANIME_DETAIL = "SELECT " + "    A.ANIME_ID, " + "    A.ANIME_TITLE, " + "    A.ORIGINAL_TITLE, " + "    A.ANIME_YEAR, " + "    A.ANIME_QUARTER, " + "    A.ANIME_STORY, " + "    A.ANIME_THUMBNAIL_URL " + "FROM ANIME A " + "WHERE A.ANIME_ID = ?";	
	// [ANIME_INSERT] 애니 추가(관리자)
	private static final String INSERT_ANIME = "INSERT INTO ANIME (ANIME_ID, ANIME_TITLE, ORIGINAL_TITLE, ANIME_YEAR, ANIME_QUARTER, ANIME_STORY, ANIME_THUMBNAIL_URL) " + "VALUES (ANIME_ID_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?)";
	// [ANIME_UPDATE] 애니 수정(관리자)
	private static final String UPDATE_ANIME = "UPDATE ANIME SET ANIME_TITLE = ?, ORIGINAL_TITLE = ?, ANIME_YEAR = ?, ANIME_QUARTER = ?, ANIME_STORY = ?, ANIME_THUMBNAIL_URL = ? " + "WHERE ANIME_ID = ?";
	// [ANIME_DELETE] 애니 삭제(관리자)
	private static final String DELETE_ANIME = "DELETE FROM ANIME WHERE ANIME_ID = ?";
	// COUNT (SELECT_ONE)
	private static final String SELECT_ANIME_COUNT_RECENT = "SELECT COUNT(*) CNT FROM ANIME";
	private static final String SELECT_ANIME_COUNT_TITLE  = "SELECT COUNT(*) CNT FROM ANIME WHERE ANIME_TITLE LIKE ?";
	private static final String SELECT_ANIME_COUNT_STORY  = "SELECT COUNT(*) CNT FROM ANIME WHERE DBMS_LOB.INSTR(ANIME_STORY, ?) > 0";

	// SELECT_ALL (컨디션 분기 실행)
	public ArrayList<AnimeDTO> selectAll(AnimeDTO dto){
		ArrayList<AnimeDTO> datas = new ArrayList<>();
		Connection conn = JDBCUtil.connect();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			String condition = dto.getCondition();
			
			// 컨디션에 따라 SQL 선택
			if ("MAIN_ANIMELIST".equals(condition)) {
				pstmt = conn.prepareStatement(SELECT_MAIN_ANIMELIST);
			}
			else if ("ANIME_LIST_RECENT".equals(condition)) {
				pstmt = conn.prepareStatement(SELECT_ANIME_LIST_RECENT);
			}
			else if ("ANIME_LIST_TITLE".equals(condition)) {
				pstmt = conn.prepareStatement(SELECT_ANIME_LIST_TITLE);
			}
			else if ("ANIME_LIST_YEAR".equals(condition)) {
				pstmt = conn.prepareStatement(SELECT_ANIME_LIST_YEAR);
			}
			else if ("ANIME_SEARCH_TITLE".equals(condition)) {
				pstmt = conn.prepareStatement(SELECT_ANIME_SEARCH_TITLE);
				pstmt.setString(1, "%" + dto.getKeyword() + "%");
			}
			else if ("ANIME_SEARCH_STORY".equals(condition)) {
				pstmt = conn.prepareStatement(SELECT_ANIME_SEARCH_STORY);
				pstmt.setString(1, dto.getKeyword());
			}
			else if ("ANIME_LIST_PAGE_RECENT".equals(condition)) {
				String orderBy = getOrderBy(dto.getSort());
				String sql = String.format(SELECT_ANIME_LIST_PAGE_BASE, orderBy);
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, dto.getEndRow());
				pstmt.setInt(2, dto.getStartRow());
			}
			else if ("ANIME_LIST_PAGE_TITLE".equals(condition)) {
				String orderBy = getOrderBy(dto.getSort());
				String sql = String.format(SELECT_ANIME_LIST_PAGE_TITLE, orderBy);
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, "%" + dto.getKeyword() + "%");
				pstmt.setInt(2, dto.getEndRow());
				pstmt.setInt(3, dto.getStartRow());
			}
			else if ("ANIME_LIST_PAGE_STORY".equals(condition)) {
				String orderBy = getOrderBy(dto.getSort());
				String sql = String.format(SELECT_ANIME_LIST_PAGE_STORY, orderBy);
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, dto.getKeyword());
				pstmt.setInt(2, dto.getEndRow());
				pstmt.setInt(3, dto.getStartRow());
			}
			else {
				return datas; // 지원하지 않는 컨디션이면 빈 리스트 반환
			}			

			rs = pstmt.executeQuery();			
			while (rs.next()) {
				AnimeDTO data = new AnimeDTO();
				data.setAnimeId(rs.getInt("ANIME_ID"));
				data.setAnimeTitle(rs.getString("ANIME_TITLE"));
				data.setAnimeYear(getIntOrNull(rs, "ANIME_YEAR"));
				data.setAnimeQuarter(rs.getString("ANIME_QUARTER"));
				data.setAnimeThumbnailUrl(rs.getString("ANIME_THUMBNAIL_URL"));			
				datas.add(data);				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if(rs != null) rs.close();
			} catch (Exception ignored) {}
			JDBCUtil.disconnect(conn, pstmt);
		}
		return datas;
	}	
	// 정렬 기준 공통 처리
	private String getOrderBy(String sort) {
		if ("OLDEST".equals(sort)) {
			return "ORDER BY ANIME_ID ASC";
		}
		if ("TITLE".equals(sort)) {
			return "ORDER BY ANIME_TITLE ASC, ANIME_ID DESC";
		}
		return "ORDER BY ANIME_ID DESC";
	}
	// SELECT_ONE (ANIME_DETAIL)
	public AnimeDTO selectOne(AnimeDTO dto) {
	    AnimeDTO data = null;
	    Connection conn = JDBCUtil.connect();
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;

	    try {
	        String condition = dto.getCondition();

	        if ("ANIME_DETAIL".equals(condition)) {
	            pstmt = conn.prepareStatement(SELECT_ANIME_DETAIL);
	            pstmt.setInt(1, dto.getAnimeId());
	        }  
	        else if ("ANIME_COUNT_RECENT".equals(condition)) {
	            pstmt = conn.prepareStatement(SELECT_ANIME_COUNT_RECENT);
	        }
	        else if ("ANIME_COUNT_TITLE".equals(condition)) {
	            pstmt = conn.prepareStatement(SELECT_ANIME_COUNT_TITLE);
	            pstmt.setString(1, "%" + safeKeyword(dto.getKeyword()) + "%");
	        }
	        else if ("ANIME_COUNT_STORY".equals(condition)) {
	            pstmt = conn.prepareStatement(SELECT_ANIME_COUNT_STORY);
	            pstmt.setString(1, safeKeyword(dto.getKeyword()));
	        }
	        else {
	            return null;
	        }
	        rs = pstmt.executeQuery();
	        if (rs.next()) {
	            // COUNT면 CNT만 담아서 반환
	            if ("ANIME_COUNT_RECENT".equals(condition) || "ANIME_COUNT_TITLE".equals(condition) || "ANIME_COUNT_STORY".equals(condition)) {
	                data = new AnimeDTO();
	                data.setAnimeCount(rs.getInt("CNT"));
	                return data;
	            }
	            data = new AnimeDTO();
	            data.setAnimeId(rs.getInt("ANIME_ID"));
	            data.setAnimeTitle(rs.getString("ANIME_TITLE"));
	            data.setOriginalTitle(rs.getString("ORIGINAL_TITLE"));
	            data.setAnimeYear(getIntOrNull(rs, "ANIME_YEAR"));
	            data.setAnimeQuarter(rs.getString("ANIME_QUARTER"));
	            data.setAnimeStory(getClobAsString(rs, "ANIME_STORY"));
	            data.setAnimeThumbnailUrl(getClobAsString(rs, "ANIME_THUMBNAIL_URL"));
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        try { if (rs != null) rs.close(); } catch (Exception ignored) {}
	        JDBCUtil.disconnect(conn, pstmt);
	    }
	    return data;
	}
	private String safeKeyword(String keyword) {
	    return (keyword == null) ? "" : keyword.trim();
	}

	// INSERT
	public boolean insert(AnimeDTO dto) {
	    Connection conn = JDBCUtil.connect();
	    PreparedStatement pstmt = null;

	    try {
	        if (!"ANIME_INSERT".equals(dto.getCondition())) {
	            return false;
	        }

	        pstmt = conn.prepareStatement(INSERT_ANIME);
	        pstmt.setString(1, dto.getAnimeTitle());
	        pstmt.setString(2, dto.getOriginalTitle());
	        pstmt.setObject(3, dto.getAnimeYear());
	        pstmt.setString(4, dto.getAnimeQuarter());
	        pstmt.setString(5, dto.getAnimeStory());
	        pstmt.setString(6, dto.getAnimeThumbnailUrl());

	        return pstmt.executeUpdate() > 0;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    } finally {
	        JDBCUtil.disconnect(conn, pstmt);
	    }
	}

	// UPDATE
	public boolean update(AnimeDTO dto) {
	    Connection conn = JDBCUtil.connect();
	    PreparedStatement pstmt = null;

	    try {
	        if (!"ANIME_UPDATE".equals(dto.getCondition())) {
	            return false;
	        }

	        pstmt = conn.prepareStatement(UPDATE_ANIME);
	        pstmt.setString(1, dto.getAnimeTitle());
	        pstmt.setString(2, dto.getOriginalTitle());
	        pstmt.setObject(3, dto.getAnimeYear());
	        pstmt.setString(4, dto.getAnimeQuarter());
	        pstmt.setString(5, dto.getAnimeStory());
	        pstmt.setString(6, dto.getAnimeThumbnailUrl());
	        pstmt.setInt(7, dto.getAnimeId());

	        return pstmt.executeUpdate() > 0;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    } finally {
	        JDBCUtil.disconnect(conn, pstmt);
	    }
	}

	// DELETE
	public boolean delete(AnimeDTO dto) {
	    Connection conn = JDBCUtil.connect();
	    PreparedStatement pstmt = null;

	    try {
	        if (!"ANIME_DELETE".equals(dto.getCondition())) {
	            return false;
	        }

	        pstmt = conn.prepareStatement(DELETE_ANIME);
	        pstmt.setInt(1, dto.getAnimeId());

	        return pstmt.executeUpdate() > 0;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    } finally {
	        JDBCUtil.disconnect(conn, pstmt);
	    }
	}

	// NUMBER 컬럼 NULL 안전 처리(Integer로 받기)
	private Integer getIntOrNull(ResultSet rs, String colName) {
		try {
			Object obj = rs.getObject(colName);
			if (obj == null)return null;
			return((Number) obj).intValue();
		} catch (Exception e) {
			return null;
		}
	}

	// CLOB -> String
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