package model.dto;

public class AnimeDTO {

	private int animeId;
	private String animeTitle;
	private String originalTitle;
	private Integer animeYear; // 0이 되면 안됨 NULL 가능
	private String animeQuarter;
	private String animeStory; // CLOB
	private String animeThumbnailUrl; // CLOB(URL)
	private String sort; // 정렬 기준
	private int animeCount; // 애니 개수(COUNT)
	private int startRow; // 페이징 시작 행
	private int endRow;   // 페이징 끝 행

	//AnimeCount 추가
	
	// [자바만] condition / search / sort
	private String condition;
	private String keyword; // 검색어(제목/줄거리)
	// private String sort; // 정렬 기준
	
	public int getAnimeId() {
		return animeId;
	}
	public void setAnimeId(int animeId) {
		this.animeId = animeId;
	}
	public String getAnimeTitle() {
		return animeTitle;
	}
	public void setAnimeTitle(String animeTitle) {
		this.animeTitle = animeTitle;
	}
	public String getOriginalTitle() {
		return originalTitle;
	}
	public void setOriginalTitle(String originalTitle) {
		this.originalTitle = originalTitle;
	}
	public Integer getAnimeYear() {
		return animeYear;
	}
	public void setAnimeYear(Integer animeYear) {
		this.animeYear = animeYear;
	}
	public String getAnimeQuarter() {
		return animeQuarter;
	}
	public void setAnimeQuarter(String animeQuarter) {
		this.animeQuarter = animeQuarter;
	}
	public String getAnimeStory() {
		return animeStory;
	}
	public void setAnimeStory(String animeStory) {
		this.animeStory = animeStory;
	}
	public String getAnimeThumbnailUrl() {
		return animeThumbnailUrl;
	}
	public void setAnimeThumbnailUrl(String animeThumbnailUrl) {
		this.animeThumbnailUrl = animeThumbnailUrl;
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
	public String getSort() {
		return sort;
	}
	public void setSort(String sort) {
		this.sort = sort;
	}
	public int getAnimeCount() {
		return animeCount;
	}
	public void setAnimeCount(int animeCount) {
		this.animeCount = animeCount;
	}
	public int getStartRow() {
		return startRow;
	}
	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}
	public int getEndRow() {
		return endRow;
	}
	public void setEndRow(int endRow) {
		this.endRow = endRow;
	}
	@Override
	public String toString() {
		return "AnimeDTO [animeId=" + animeId + ", animeTitle=" + animeTitle + ", originalTitle=" + originalTitle
				+ ", animeYear=" + animeYear + ", animeQuarter=" + animeQuarter + ", animeStory=" + animeStory
				+ ", animeThumbnailUrl=" + animeThumbnailUrl + ", sort=" + sort + ", animeCount=" + animeCount
				+ ", startRow=" + startRow + ", endRow=" + endRow + ", condition=" + condition + ", keyword=" + keyword
				+ "]";
	}	
}
