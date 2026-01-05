package model.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class JDBCUtil {
   private static final String driverName = "oracle.jdbc.driver.OracleDriver";
   private static final String url = "jdbc:oracle:thin:@localhost:1521:xe";
   private static final String user = "TEEMO";
   private static final String password = "1234";
   
   public static Connection connect() {
      Connection conn = null;
      try {
         Class.forName(driverName);
         conn = DriverManager.getConnection(url, user, password);
      } catch (Exception e) {
         e.printStackTrace();
      }      
      return conn;
   }
   
   public static void disconnect(Connection conn,PreparedStatement pstmt) {
      try {
    	  if (pstmt != null) pstmt.close();
          if (conn != null) conn.close();
      } catch (SQLException e) {
         e.printStackTrace();
      }
   }
}
