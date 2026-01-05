package model.dto;

public class BoardLikeDTO {
	
	private int boardLikeId;
	private int boardId;
	private int memberId;
	
	// condition / view
	private String condition;
	private int likeCnt; // 좋아요 개수(LIKE_CNT)
	private int isLiked; // 내가 눌렀는지(0/1, IS_LIKED)
	public int getBoardLikeId() {
		return boardLikeId;
	}
	public void setBoardLikeId(int boardLikeId) {
		this.boardLikeId = boardLikeId;
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
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
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
	@Override
	public String toString() {
		return "BoardLikeDTO [boardLikeId=" + boardLikeId + ", boardId=" + boardId + ", memberId=" + memberId
				+ ", condition=" + condition + ", likeCnt=" + likeCnt + ", isLiked=" + isLiked + "]";
	}
}
