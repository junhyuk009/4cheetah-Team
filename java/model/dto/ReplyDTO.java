package model.dto;

public class ReplyDTO {
	private int replyId;
	private int boardId;
	private int memberId;
	private String replyContent;
	
	// condition / join / view
	private String condition;
	private String writerNickname; // JOIN MEMBER + 탈퇴회원 
	public int getReplyId() {
		return replyId;
	}
	public void setReplyId(int replyId) {
		this.replyId = replyId;
	}
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
	public String getReplyContent() {
		return replyContent;
	}
	public void setReplyContent(String replyContent) {
		this.replyContent = replyContent;
	}
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	public String getWriterNickname() {
		return writerNickname;
	}
	public void setWriterNickname(String writerNickname) {
		this.writerNickname = writerNickname;
	}
	@Override
	public String toString() {
		return "ReplyDTO [replyId=" + replyId + ", boardId=" + boardId + ", memberId=" + memberId + ", replyContent="
				+ replyContent + ", condition=" + condition + ", writerNickname=" + writerNickname + "]";
	}
}
