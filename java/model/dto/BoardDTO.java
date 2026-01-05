package model.dto;

public class BoardDTO {
   private int boardId;
   private int memberId;
   private String boardTitle;
   private String boardContent; // CLOB
   private int boardViews;
   private String boardCategory;
   
    // condition / join / view
   
   private String condition;
   private String keyword; // 검색어(제목,작성자/내용)
   
   private String writerNickname; // JOIN_MEMBER(회원가입) + 탈퇴회원 
   private String writerRole; // 작성자 등급
   
   private int likeCnt; // (좋아요 개수(LIKE_CNT)
   private int isLiked; //내가 눌렀는지(0,1 IS_LIKED)
   private String likeMemberNickname; // 좋아요 누른 사람 닉네임 목록용
   
   public int getBoardId() {
      return boardId;
   }
   public void setBoardId(int boardId) {
      this.boardId = boardId;
   }
   public int getMemberId() {
      return memberId;
   }
   public void setMemberId(int memberId) {
      this.memberId = memberId;
   }
   public String getBoardTitle() {
      return boardTitle;
   }
   public void setBoardTitle(String boardTitle) {
      this.boardTitle = boardTitle;
   }
   public String getBoardContent() {
      return boardContent;
   }
   public void setBoardContent(String boardContent) {
      this.boardContent = boardContent;
   }
   public int getBoardViews() {
      return boardViews;
   }
   public void setBoardViews(int boardViews) {
      this.boardViews = boardViews;
   }
   public String getBoardCategory() {
      return boardCategory;
   }
   public void setBoardCategory(String boardCategory) {
      this.boardCategory = boardCategory;
   }
   public String getCondition() {
      return condition;
   }
   public void setCondition(String condition) {
      this.condition = condition;
   }
   public String getKeyword() {
      return keyword;
   }
   public void setKeyword(String keyword) {
      this.keyword = keyword;
   }
   public String getWriterNickname() {
      return writerNickname;
   }
   public void setWriterNickname(String writerNickname) {
      this.writerNickname = writerNickname;
   }
   public int getLikeCnt() {
      return likeCnt;
   }
   public void setLikeCnt(int likeCnt) {
      this.likeCnt = likeCnt;
   }
   public int getIsLiked() {
      return isLiked;
   }
   public void setIsLiked(int isLiked) {
      this.isLiked = isLiked;
   }
   public String getWriterRole() {
      return writerRole;
   }
   public void setWriterRole(String writerRole) {
      this.writerRole = writerRole;
   }
   public String getLikeMemberNickname() {
   return likeMemberNickname;
   }
   public void setLikeMemberNickname(String likeMemberNickname) {
   this.likeMemberNickname = likeMemberNickname;
   }
   @Override
   public String toString() {
   return "BoardDTO [boardId=" + boardId + ", memberId=" + memberId + ", boardTitle=" + boardTitle + ", boardContent="
         + boardContent + ", boardViews=" + boardViews + ", boardCategory=" + boardCategory + ", condition="
         + condition + ", keyword=" + keyword + ", writerNickname=" + writerNickname + ", writerRole=" + writerRole
         + ", likeCnt=" + likeCnt + ", isLiked=" + isLiked + ", likeMemberNickname=" + likeMemberNickname + "]";
   }
   
}