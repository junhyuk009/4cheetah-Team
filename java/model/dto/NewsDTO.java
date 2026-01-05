package model.dto;

public class NewsDTO {

	private int newsId;
	private Integer animeId; // NULL 가능(FK)
	private String newsTitle;
	private String newsContent; // CLOB
	private String newsImageUrl; // CLOB
	private String newsThumbnailUrl; // CLOB
	private int newsCount; // 뉴스 전체 개수(COUNT)
	private int startRow;  // 페이징 시작 행
	private int endRow;    // 페이징 끝 행
	
	 // condition / search / join
	private String condition;
	private String keyword; // 뉴스 검색어(제목/내용)
	
	
	// [JOIN] NEWS_DETAIL에서 ANIME 조인 결과 담기용
	private String animeTitle;
	private Integer animeYear; // NULL 가능
	private String animeQuarter;
	private String animeThumbnailUrl;
	
	public int getNewsId() {
		return newsId;
	}
	public void setNewsId(int newsId) {
		this.newsId = newsId;
	}
	public Integer getAnimeId() {
		return animeId;
	}
	public void setAnimeId(Integer animeId) {
		this.animeId = animeId;
	}
	public String getNewsTitle() {
		return newsTitle;
	}
	public void setNewsTitle(String newsTitle) {
		this.newsTitle = newsTitle;
	}
	public String getNewsContent() {
		return newsContent;
	}
	public void setNewsContent(String newsContent) {
		this.newsContent = newsContent;
	}
	public String getNewsImageUrl() {
		return newsImageUrl;
	}
	public void setNewsImageUrl(String newsImageUrl) {
		this.newsImageUrl = newsImageUrl;
	}
	public String getNewsThumbnailUrl() {
		return newsThumbnailUrl;
	}
	public void setNewsThumbnailUrl(String newsThumbnailUrl) {
		this.newsThumbnailUrl = newsThumbnailUrl;
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
	public String getAnimeTitle() {
		return animeTitle;
	}
	public void setAnimeTitle(String animeTitle) {
		this.animeTitle = animeTitle;
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
	public String getAnimeThumbnailUrl() {
		return animeThumbnailUrl;
	}
	public void setAnimeThumbnailUrl(String animeThumbnailUrl) {
		this.animeThumbnailUrl = animeThumbnailUrl;
	}
	public int getNewsCount() {
		return newsCount;
	}
	public void setNewsCount(int newsCount) {
		this.newsCount = newsCount;
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
		return "NewsDTO [newsId=" + newsId + ", animeId=" + animeId + ", newsTitle=" + newsTitle + ", newsContent="
				+ newsContent + ", newsImageUrl=" + newsImageUrl + ", newsThumbnailUrl=" + newsThumbnailUrl
				+ ", newsCount=" + newsCount + ", startRow=" + startRow + ", endRow=" + endRow + ", condition="
				+ condition + ", keyword=" + keyword + ", animeTitle=" + animeTitle + ", animeYear=" + animeYear
				+ ", animeQuarter=" + animeQuarter + ", animeThumbnailUrl=" + animeThumbnailUrl + "]";
	}	
}