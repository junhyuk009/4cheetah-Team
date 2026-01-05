package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import model.common.JDBCUtil;
import model.dto.NewsDTO;

public class NewsDAO {
   // SELECT_ALL 
   // [MAIN_BANNER_NEWSLIST] 최근 뉴스 3개
   private static final String SELECT_MAIN_BANNER_NEWSLIST = "SELECT " + "    N.NEWS_ID, " + "    N.NEWS_TITLE, " + "    N.NEWS_THUMBNAIL_URL " + "FROM ( " + "    SELECT " + "        N.NEWS_ID, " + "        N.NEWS_TITLE, " + "        N.NEWS_THUMBNAIL_URL " + "    FROM NEWS N " + "    ORDER BY N.NEWS_ID DESC " + ") N " + "WHERE ROWNUM <= 3";
   // [NEWS_LIST] 뉴스 전체보기
   private static final String SELECT_NEWS_LIST = "SELECT " + "    N.NEWS_ID, " + "    N.NEWS_TITLE, " + "    N.NEWS_THUMBNAIL_URL " + "FROM NEWS N " + "ORDER BY N.NEWS_ID DESC";
   // [NEWS_SEARCH_TITLE] 뉴스 제목 검색
   private static final String SELECT_NEWS_SEARCH_TITLE = "SELECT " + "    N.NEWS_ID, " + "    N.NEWS_TITLE, " + "    N.NEWS_THUMBNAIL_URL " + "FROM NEWS N " + "WHERE N.NEWS_TITLE LIKE ? " + "ORDER BY N.NEWS_ID DESC";
   // [NEWS_SEARCH_CONTENT] 뉴스 내용 검색(CLOB)
   private static final String SELECT_NEWS_SEARCH_CONTENT = "SELECT " + "    N.NEWS_ID, " + "    N.NEWS_TITLE, " + "    N.NEWS_THUMBNAIL_URL " + "FROM NEWS N " + "WHERE DBMS_LOB.INSTR(N.NEWS_CONTENT, ?) > 0 " + "ORDER BY N.NEWS_ID DESC";
   // PAGE LIST(startRow-endRow) Oracle에서 BETWEEN 페이징 형태(ROW_NUMBER)
   // [NEWS_LIST_PAGE] 뉴스 페이징 (최신순)
   private static final String SELECT_NEWS_LIST_PAGE = "SELECT NEWS_ID, NEWS_TITLE, NEWS_THUMBNAIL_URL " + "FROM ( " + "  SELECT ROW_NUMBER() OVER(ORDER BY NEWS_ID DESC) RN, " + "         NEWS_ID, NEWS_TITLE, NEWS_THUMBNAIL_URL " + "  FROM NEWS " + ") " + "WHERE RN BETWEEN ? AND ?";
   // [NEWS_LIST_PAGE_TITLE] 제목 검색 + 페이징
    private static final String SELECT_NEWS_LIST_PAGE_TITLE = "SELECT NEWS_ID, NEWS_TITLE, NEWS_THUMBNAIL_URL " + "FROM ( " + "  SELECT ROW_NUMBER() OVER(ORDER BY NEWS_ID DESC) RN, " + "         NEWS_ID, NEWS_TITLE, NEWS_THUMBNAIL_URL " + "  FROM NEWS " + "  WHERE NEWS_TITLE LIKE ? " + ") " + "WHERE RN BETWEEN ? AND ?";
    // [NEWS_LIST_PAGE_CONTENT] 내용 검색 + 페이징(CLOB)
    private static final String SELECT_NEWS_LIST_PAGE_CONTENT = "SELECT NEWS_ID, NEWS_TITLE, NEWS_THUMBNAIL_URL " + "FROM ( " + "  SELECT ROW_NUMBER() OVER(ORDER BY NEWS_ID DESC) RN, " + "         NEWS_ID, NEWS_TITLE, NEWS_THUMBNAIL_URL " + "  FROM NEWS " + "  WHERE DBMS_LOB.INSTR(NEWS_CONTENT, ?) > 0 " + ") " + "WHERE RN BETWEEN ? AND ?";
   
   // SELECT_ONE
   private static final String SELECT_NEWS_DETAIL = "SELECT " + "    N.NEWS_ID, " + "    N.ANIME_ID, " + "    N.NEWS_TITLE, " + "    N.NEWS_CONTENT, " + "    N.NEWS_IMAGE_URL, " +        "    N.NEWS_THUMBNAIL_URL, " + "    A.ANIME_TITLE, " + "    A.ANIME_YEAR, " + "    A.ANIME_QUARTER, " + "    A.ANIME_THUMBNAIL_URL AS ANIME_THUMBNAIL_URL " + "FROM NEWS N " + "LEFT JOIN ANIME A ON A.ANIME_ID = N.ANIME_ID " + "WHERE N.NEWS_ID = ?";
   // [추가] COUNT (SELECT_ONE) - 뉴스 총 개수/검색 개수
    // [NEWS_COUNT] 전체 개수
    private static final String SELECT_NEWS_COUNT = "SELECT COUNT(*) CNT FROM NEWS";
    // [NEWS_COUNT_TITLE] 제목 검색 개수
    private static final String SELECT_NEWS_COUNT_TITLE = "SELECT COUNT(*) CNT FROM NEWS WHERE NEWS_TITLE LIKE ?";
    // [NEWS_COUNT_CONTENT] 내용 검색 개수(CLOB)
    private static final String SELECT_NEWS_COUNT_CONTENT = "SELECT COUNT(*) CNT FROM NEWS WHERE DBMS_LOB.INSTR(NEWS_CONTENT, ?) > 0";
   
    // INSERT / UPDATE / DELETE
   // [NEWS_INSERT] 뉴스 등록(관리자)
   private static final String INSERT_NEWS = "INSERT INTO NEWS (NEWS_ID, ANIME_ID, NEWS_TITLE, NEWS_CONTENT, NEWS_IMAGE_URL, NEWS_THUMBNAIL_URL) " + "VALUES (NEWS_ID_SEQ.NEXTVAL, ?, ?, ?, ?, ?)";
   
   // [추가] INSERT 직후 방금 생성된 NEWS_ID 조회
   private static final String SELECT_NEWS_ID_CURRVAL = "SELECT NEWS_ID_SEQ.CURRVAL AS NEWS_ID FROM DUAL";

   // [NEWS_UPDATE] 뉴스 수정(관리자)
   private static final String UPDATE_NEWS = "UPDATE NEWS " + "SET ANIME_ID = ?, NEWS_TITLE = ?, NEWS_CONTENT = ?, NEWS_IMAGE_URL = ?, NEWS_THUMBNAIL_URL = ? " + "WHERE NEWS_ID = ?";

   // [NEWS_DELETE] 뉴스 삭제(관리자)
   private static final String DELETE_NEWS = "DELETE FROM NEWS WHERE NEWS_ID = ?";

   public ArrayList<NewsDTO> selectAll(NewsDTO dto) {
      ArrayList<NewsDTO> datas = new ArrayList<>();
      Connection conn = JDBCUtil.connect();
      PreparedStatement pstmt = null;
      ResultSet rs = null;
      
      try {
         String condition = dto.getCondition();
         
         if ("MAIN_BANNER_NEWSLIST".equals(condition)) {
            pstmt = conn.prepareStatement(SELECT_MAIN_BANNER_NEWSLIST);
         }
         else if ("NEWS_LIST".equals(condition)) {
            pstmt = conn.prepareStatement(SELECT_NEWS_LIST);
         }
         else if ("NEWS_SEARCH_TITLE".equals(condition)) {
            pstmt = conn.prepareStatement(SELECT_NEWS_SEARCH_TITLE);
            pstmt.setString(1, "%" + dto.getKeyword() + "%");
         }
         else if ("NEWS_SEARCH_CONTENT".equals(condition)) {
            pstmt = conn.prepareStatement(SELECT_NEWS_SEARCH_CONTENT);
            pstmt.setString(1, dto.getKeyword());
         }
          else if ("NEWS_LIST_PAGE".equals(condition)) {
                   pstmt = conn.prepareStatement(SELECT_NEWS_LIST_PAGE);
                   pstmt.setInt(1, dto.getStartRow()); // startRow
                   pstmt.setInt(2, dto.getEndRow());   // endRow
            }
            else if ("NEWS_LIST_PAGE_TITLE".equals(condition)) {
                pstmt = conn.prepareStatement(SELECT_NEWS_LIST_PAGE_TITLE);
                pstmt.setString(1, "%" + safeKeyword(dto.getKeyword()) + "%"); // 제목 LIKE
                pstmt.setInt(2, dto.getStartRow());
                pstmt.setInt(3, dto.getEndRow());
            }
            else if ("NEWS_LIST_PAGE_CONTENT".equals(condition)) {
                pstmt = conn.prepareStatement(SELECT_NEWS_LIST_PAGE_CONTENT);
                pstmt.setString(1, safeKeyword(dto.getKeyword())); // CLOB 검색 키워드
                pstmt.setInt(2, dto.getStartRow());
                pstmt.setInt(3, dto.getEndRow());
            }
         else {
            return datas; // 지원하지 않는 컨디션이면 빈 리스트
         }
         rs = pstmt.executeQuery();
         
         while (rs.next()) {
            NewsDTO data = new NewsDTO();
            data.setNewsId(rs.getInt("NEWS_ID"));
            data.setNewsTitle(rs.getString("NEWS_TITLE"));
            data.setNewsThumbnailUrl(rs.getString("NEWS_THUMBNAIL_URL"));
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
   
   // SELECT_ONE: NEWS_DETAIL
   public NewsDTO selectOne(NewsDTO dto) {
      NewsDTO data = null;
      Connection conn = JDBCUtil.connect();
      PreparedStatement pstmt = null;
      ResultSet rs = null;
      
       try {
               String condition = dto.getCondition();
               // 기존은 NEWS_DETAIL만 처리했는데, COUNT 컨디션도 여기서 같이 처리하도록 확장
               if ("NEWS_DETAIL".equals(condition)) {
                   pstmt = conn.prepareStatement(SELECT_NEWS_DETAIL);
                   pstmt.setInt(1, dto.getNewsId());
               }
               else if ("NEWS_COUNT".equals(condition)) { // [추가]
                   pstmt = conn.prepareStatement(SELECT_NEWS_COUNT);
               }
               else if ("NEWS_COUNT_TITLE".equals(condition)) { // [추가]
                   pstmt = conn.prepareStatement(SELECT_NEWS_COUNT_TITLE);
                   pstmt.setString(1, "%" + safeKeyword(dto.getKeyword()) + "%");
               }
               else if ("NEWS_COUNT_CONTENT".equals(condition)) { // [추가]
                   pstmt = conn.prepareStatement(SELECT_NEWS_COUNT_CONTENT);
                   pstmt.setString(1, safeKeyword(dto.getKeyword()));
               }
               else {
                   return null;
               }      
         rs = pstmt.executeQuery();
         
         if (rs.next()) {
            // COUNT면 newsCount만 채워서 반환
              if ("NEWS_COUNT".equals(condition) || "NEWS_COUNT_TITLE".equals(condition) || "NEWS_COUNT_CONTENT".equals(condition)) {
                       data = new NewsDTO();
                       data.setNewsCount(rs.getInt("CNT"));
                       return data;
                   }
            data = new NewsDTO();
            
            data.setNewsId(rs.getInt("NEWS_ID"));
            data.setNewsTitle(rs.getString("NEWS_TITLE"));
            data.setNewsImageUrl(rs.getString("NEWS_IMAGE_URL"));
            data.setNewsContent(getClobAsString(rs, "NEWS_CONTENT"));
            data.setNewsThumbnailUrl(rs.getString("NEWS_THUMBNAIL_URL"));
            
            // FK(NULL가능) 안전 처리
            data.setAnimeId(getIntOrNull(rs, "ANIME_ID"));
            
            // Join 결과(애니 정보) - 없으면 null로 돌아갈 수 있음
            data.setAnimeTitle(rs.getString("ANIME_TITLE"));
            data.setAnimeYear(getIntOrNull(rs, "ANIME_YEAR"));
            data.setAnimeQuarter(rs.getString("ANIME_QUARTER"));
            data.setAnimeThumbnailUrl(rs.getString("ANIME_THUMBNAIL_URL"));
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
   // INSERT
   public boolean insert(NewsDTO dto) {
       Connection conn = JDBCUtil.connect();
       PreparedStatement pstmt = null;
       PreparedStatement pstmt2 = null;
       ResultSet rs = null;

       try {
           // 1) 컨디션 체크
           if (!"NEWS_INSERT".equals(dto.getCondition())) {
               return false;
           }

           // 2) INSERT 실행
           pstmt = conn.prepareStatement(INSERT_NEWS);

           // ANIME_ID는 NULL 가능
           pstmt.setObject(1, dto.getAnimeId());
           pstmt.setString(2, dto.getNewsTitle());
           pstmt.setString(3, dto.getNewsContent());
           pstmt.setString(4, dto.getNewsImageUrl());
           pstmt.setString(5, dto.getNewsThumbnailUrl());

           int result = pstmt.executeUpdate();
           if (result <= 0) {
               return false;
           }

           // 3) 방금 들어간 NEWS_ID를 다시 조회해서 DTO에 세팅
           pstmt2 = conn.prepareStatement(SELECT_NEWS_ID_CURRVAL);
           rs = pstmt2.executeQuery();
           if (rs.next()) {
               int newsId = rs.getInt("NEWS_ID");
               dto.setNewsId(newsId);
           }

           System.out.println("[로그] NewsDAO insert : dto(after) = " + dto);
           return true;

       } catch (Exception e) {
           e.printStackTrace();
           return false;

       } finally {
           try { if (rs != null) rs.close(); } catch (Exception ignored) {}
           try { if (pstmt2 != null) pstmt2.close(); } catch (Exception ignored) {}
           JDBCUtil.disconnect(conn, pstmt);
       }
   }


   // UPDATE
   public boolean update(NewsDTO dto) {
       Connection conn = JDBCUtil.connect();
       PreparedStatement pstmt = null;

       try {
           // 1) 컨디션 체크
           if (!"NEWS_UPDATE".equals(dto.getCondition())) {
               return false;
           }

           // 2) SQL 준비
           pstmt = conn.prepareStatement(UPDATE_NEWS);

           // 3) 값 세팅
           pstmt.setObject(1, dto.getAnimeId()); // NULL 가능
           pstmt.setString(2, dto.getNewsTitle());
           pstmt.setString(3, dto.getNewsContent());
           pstmt.setString(4, dto.getNewsImageUrl());
           pstmt.setString(5, dto.getNewsThumbnailUrl());
           pstmt.setInt(6, dto.getNewsId()); // WHERE

           // 4) 실행
           return pstmt.executeUpdate() > 0;

       } catch (Exception e) {
           e.printStackTrace();
           return false;
       } finally {
           JDBCUtil.disconnect(conn, pstmt);
       }
   }

   // DELETE
   public boolean delete(NewsDTO dto) {
       Connection conn = JDBCUtil.connect();
       PreparedStatement pstmt = null;

       try {
           // 1) 컨디션 체크
           if (!"NEWS_DELETE".equals(dto.getCondition())) {
               return false;
           }

           // 2) SQL 준비
           pstmt = conn.prepareStatement(DELETE_NEWS);
           pstmt.setInt(1, dto.getNewsId());

           // 3) 실행
           return pstmt.executeUpdate() > 0;

       } catch (Exception e) {
           e.printStackTrace();
           return false;
       } finally {
           JDBCUtil.disconnect(conn, pstmt);
       }
   }
   // null 안전 키워드
   private String safeKeyword(String keyword) {
      return (keyword == null) ? "" : keyword.trim();
   }
   
   // NUMBER 컬럼 NULL 안전 처리 (Integer)
   private Integer getIntOrNull(ResultSet rs, String colName) {
      try {
         Object obj = rs.getObject(colName);
         if (obj == null)
         return null;
         return((Number) obj).intValue();
      } catch (Exception e) {
         return null;
      }
   }
   
   // CLOB >> String
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