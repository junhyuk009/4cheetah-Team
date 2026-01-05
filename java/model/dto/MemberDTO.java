package model.dto;

public class MemberDTO {
	private int memberId;
	private String memberName;
	private String memberPassword;
	private String memberNickname;
	private int memberCash;
	private int memberPayCash;
	private String memberRole; // ACTIVE(활성), WITHDRAWN(탈퇴), ADMIN(관리자)
	private String memberProfileImage; // CLOB(URL) 
	private String memberEmail;
	private String memberPhoneNumber;
	private String condition;
	
	public int getMemberId() {
		return memberId;
	}
	public void setMemberId(int memberId) {
		this.memberId = memberId;
	}
	public String getMemberName() {
		return memberName;
	}
	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}
	public String getMemberPassword() {
		return memberPassword;
	}
	public void setMemberPassword(String memberPassword) {
		this.memberPassword = memberPassword;
	}
	public String getMemberNickname() {
		return memberNickname;
	}
	public void setMemberNickname(String memberNickname) {
		this.memberNickname = memberNickname;
	}
	public int getMemberCash() {
		return memberCash;
	}
	public void setMemberCash(int memberCash) {
		this.memberCash = memberCash;
	}
	public String getMemberRole() {
		return memberRole;
	}
	public void setMemberRole(String memberRole) {
		this.memberRole = memberRole;
	}
	public String getMemberProfileImage() {
		return memberProfileImage;
	}
	public void setMemberProfileImage(String memberProfileImage) {
		this.memberProfileImage = memberProfileImage;
	}
	public String getMemberEmail() {
		return memberEmail;
	}
	public void setMemberEmail(String memberEmail) {
		this.memberEmail = memberEmail;
	}
	public String getMemberPhoneNumber() {
		return memberPhoneNumber;
	}
	public void setMemberPhoneNumber(String memberPhoneNumber) {
		this.memberPhoneNumber = memberPhoneNumber;
	}
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	public int getMemberPayCash() {
		return memberPayCash;
	}
	public void setMemberPayCash(int memberPayCash) {
		this.memberPayCash = memberPayCash;
	}
	@Override
	public String toString() {
		return "MemberDTO [memberId=" + memberId + ", memberName=" + memberName + ", memberPassword=" + memberPassword
				+ ", memberNickname=" + memberNickname + ", memberCash=" + memberCash + ", memberPayCash="
				+ memberPayCash + ", memberRole=" + memberRole + ", memberProfileImage=" + memberProfileImage
				+ ", memberEmail=" + memberEmail + ", memberPhoneNumber=" + memberPhoneNumber + ", condition="
				+ condition + "]";
	}	
}
