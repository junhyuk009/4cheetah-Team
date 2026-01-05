package model.dto;

public class VoteCategoryDTO {
	private int voteCategoryId;
	private String voteCategoryName;
	
	// condition / search
	private String condition;
	private String keyword; // 카테고리명 검색 
	public int getVoteCategoryId() {
		return voteCategoryId;
	}
	public void setVoteCategoryId(int voteCategoryId) {
		this.voteCategoryId = voteCategoryId;
	}
	public String getVoteCategoryName() {
		return voteCategoryName;
	}
	public void setVoteCategoryName(String voteCategoryName) {
		this.voteCategoryName = voteCategoryName;
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
	@Override
	public String toString() {
		return "VoteCategoryDTO [voteCategoryId=" + voteCategoryId + ", voteCategoryName=" + voteCategoryName
				+ ", condition=" + condition + ", keyword=" + keyword + "]";
	}
	
	
}
