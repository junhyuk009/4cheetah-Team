package controller.member;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import controller.common.Action;
import controller.common.ActionForward;
import model.dao.MemberDAO;
import model.dto.MemberDTO;

public class ChangeProfileAction implements Action {

	// ProfileImageUploadAction과 완전 동일 세션 키
	private static final String SESSION_PROFILE_IMAGE_TOKEN = "temporaryProfileImageToken";
	private static final String SESSION_PROFILE_IMAGE_ABSOLUTE_PATH = "temporaryProfileImageAbsolutePath";

	// 최종 저장 폴더 (웹 경로)
	private static final String FINAL_PROFILE_IMAGE_DIR = "/upload/profile";

	// 임시 저장 폴더 (경로 안전장치 체크용, ProfileImageUploadAction과 동일)
	private static final String TEMP_PROFILE_IMAGE_DIR = "/upload/profile_temp";

	// 폼 파라미터 이름
	private static final String PARAM_MEMBER_NICKNAME = "memberNickname";
	private static final String PARAM_TEMPORARY_PROFILE_IMAGE_TOKEN = "temporaryProfileImageToken";

	// 비용 정책
	private static final int COST_NICKNAME_ONLY = 300;
	private static final int COST_PROFILE_IMAGE = 500;

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
		ActionForward forward = new ActionForward();
		MemberDAO memberDAO = new MemberDAO();
		MemberDTO memberDTO = new MemberDTO();

		try {
			request.setCharacterEncoding("UTF-8");

			// 1) 로그인 체크
			HttpSession session = request.getSession(false);
			if (session == null) {
				forward.setRedirect(true);
				forward.setPath(request.getContextPath() + "/loginPage.do");
				return forward;
			}

			Object sessionMemberIdObject = session.getAttribute("memberId");
			if (sessionMemberIdObject == null) {
				forward.setRedirect(true);
				forward.setPath(request.getContextPath() + "/loginPage.do");
				return forward;
			}

			Integer memberId = (Integer) sessionMemberIdObject;

			// 관리자 여부 판정 (세션 memberRole 사용)
			Object roleObj = session.getAttribute("memberRole");
			boolean isAdmin = (roleObj != null && "ADMIN".equals(roleObj.toString()));

			// 돌아갈 페이지 (일반: myPage / 관리자: adminPage)
			final String RETURN_DO = isAdmin ? "/adminPage.do" : "/myPage.do";

			System.out.println("[프로필 변경 액션 로그] 시작 memberId=[" + memberId + "], isAdmin=[" + isAdmin + "]");

			// 2) DB에서 현재 회원정보 조회
			memberDTO.setCondition("MEMBER_MYPAGE");
			memberDTO.setMemberId(memberId);

			MemberDTO memberData = memberDAO.selectOne(memberDTO);
			if (memberData == null) {
				forward.setRedirect(true);
				forward.setPath(request.getContextPath() + "/mainPage.do");
				return forward;
			}

			String currentMemberNickname = memberData.getMemberNickname();
			int currentMemberCash = memberData.getMemberCash();
			String currentMemberProfileImage = memberData.getMemberProfileImage();

			System.out.println("[프로필 변경 액션 로그] currentMemberNickname : [" + currentMemberNickname + "]");
			System.out.println("[프로필 변경 액션 로그] currentMemberCash : [" + currentMemberCash + "]");
			System.out.println("[프로필 변경 액션 로그] currentMemberProfileImage : [" + currentMemberProfileImage + "]");

			// 3) 닉네임 변경 판정
			String requestMemberNicknameParameter = request.getParameter(PARAM_MEMBER_NICKNAME);
			String requestMemberNickname = currentMemberNickname; // 기본 유지
			if (requestMemberNicknameParameter != null) {
				String trimmedNickname = requestMemberNicknameParameter.trim();
				if (!trimmedNickname.isEmpty()) {
					requestMemberNickname = trimmedNickname;
				}
			}

			boolean isMemberNicknameChanged = !requestMemberNickname.equals(currentMemberNickname);
			System.out.println("[프로필 변경 액션 로그] CHECK isNickChanged=[" + isMemberNicknameChanged + "]");
			System.out.println("[프로필 변경 액션 로그] CHECK requestNickname=[" + requestMemberNickname + "]");

			// 4) 이미지 변경 판정 (토큰 기반 커밋)
			String requestTemporaryProfileImageTokenParameter = request.getParameter(PARAM_TEMPORARY_PROFILE_IMAGE_TOKEN);
			String requestTemporaryProfileImageToken = null;

			if (requestTemporaryProfileImageTokenParameter != null) {
				String trimmedToken = requestTemporaryProfileImageTokenParameter.trim();
				if (!trimmedToken.isEmpty()) {
					requestTemporaryProfileImageToken = trimmedToken;
				}
			}

			String sessionTemporaryProfileImageToken = (String) session.getAttribute(SESSION_PROFILE_IMAGE_TOKEN);
			String sessionTemporaryProfileImageAbsolutePath = (String) session.getAttribute(SESSION_PROFILE_IMAGE_ABSOLUTE_PATH);

			boolean isMemberProfileImageChanged = false;

			if (requestTemporaryProfileImageToken != null) {
				if (sessionTemporaryProfileImageToken == null || sessionTemporaryProfileImageAbsolutePath == null) {
					request.setAttribute("msg", "임시 이미지 정보가 없습니다. 다시 업로드 해주세요.");
					forward.setRedirect(false);
					forward.setPath(RETURN_DO);
					System.out.println("[프로필 변경 액션 로그] FAIL imgCommit - noSessionTempInfo");
					return forward;
				}

				if (!requestTemporaryProfileImageToken.equals(sessionTemporaryProfileImageToken)) {
					request.setAttribute("msg", "프로필 이미지 토큰이 일치하지 않습니다.");
					forward.setRedirect(false);
					forward.setPath(RETURN_DO);
					System.out.println("[프로필 변경 액션 로그] FAIL imgCommit - tokenMismatch");
					return forward;
				}

				File temporaryProfileImageFile = new File(sessionTemporaryProfileImageAbsolutePath);
				if (!temporaryProfileImageFile.exists() || !temporaryProfileImageFile.isFile()) {
					request.setAttribute("msg", "임시 이미지 파일이 존재하지 않습니다. 다시 업로드 해주세요.");
					forward.setRedirect(false);
					forward.setPath(RETURN_DO);
					System.out.println("[프로필 변경 액션 로그] FAIL imgCommit - tempFileMissing");
					return forward;
				}

				isMemberProfileImageChanged = true;
			}

			// 변경 없음 체크는 "금액"이 아니라 "변경 플래그"로 해야 함 (관리자는 금액이 0이니까)
			if (!isMemberNicknameChanged && !isMemberProfileImageChanged) {
				request.setAttribute("msg", "변경된 내용이 없습니다.");
				forward.setRedirect(false);
				forward.setPath(RETURN_DO);
				return forward;
			}

			// 5) 비용 계산 (관리자는 무료)
			int cashPayAmount = 0;

			if (!isAdmin) {
				if (isMemberNicknameChanged) cashPayAmount += COST_NICKNAME_ONLY;
				if (isMemberProfileImageChanged) cashPayAmount += COST_PROFILE_IMAGE;
			} else {
				cashPayAmount = 0; // 관리자 무료
			}

			// 6) 잔액 검증 (관리자는 스킵)
			if (!isAdmin) {
				if (currentMemberCash < cashPayAmount) {
					request.setAttribute("msg", "캐쉬가 부족합니다. (필요: " + cashPayAmount + ", 보유: " + currentMemberCash + ")");
					forward.setRedirect(false);
					forward.setPath(RETURN_DO);
					System.out.println("[프로필 변경 액션 로그] FAIL insufficientCash 필요=[" + cashPayAmount + "], 보유=[" + currentMemberCash + "]");
					return forward;
				}
			}

			// 7) 이미지 커밋 (임시 → 최종 이동)
			String newMemberProfileImage = null;
			Path temporaryProfileImagePath = null;
			Path finalProfileImagePath = null;

			if (isMemberProfileImageChanged) {
				ServletContext servletContext = request.getServletContext();

				temporaryProfileImagePath = Paths.get(sessionTemporaryProfileImageAbsolutePath);

				String finalProfileImageDirRealPath = servletContext.getRealPath(FINAL_PROFILE_IMAGE_DIR);
				if (finalProfileImageDirRealPath == null) {
					request.setAttribute("msg", "최종 저장 경로를 확인할 수 없습니다.");
					forward.setRedirect(false);
					forward.setPath(RETURN_DO);
					return forward;
				}

				Path finalProfileImageDirPath = Paths.get(finalProfileImageDirRealPath);
				Files.createDirectories(finalProfileImageDirPath);

				String finalProfileImageFileName = "profile_" + memberId + "_" + System.currentTimeMillis() + ".jpg";
				newMemberProfileImage = FINAL_PROFILE_IMAGE_DIR + "/" + finalProfileImageFileName;

				String finalProfileImageRealPath = servletContext.getRealPath(newMemberProfileImage);
				if (finalProfileImageRealPath == null) {
					request.setAttribute("msg", "최종 파일 경로를 확인할 수 없습니다.");
					forward.setRedirect(false);
					forward.setPath(RETURN_DO);
					return forward;
				}

				finalProfileImagePath = Paths.get(finalProfileImageRealPath);

				System.out.println("[프로필 변경 액션 로그] MOVE start temp=[" + temporaryProfileImagePath.getFileName()
						+ "] -> final=[" + finalProfileImagePath.getFileName() + "]");

				Files.move(temporaryProfileImagePath, finalProfileImagePath, StandardCopyOption.REPLACE_EXISTING);

				for (int i = 0; i < 10; i++) {
					if (Files.exists(finalProfileImagePath) && Files.size(finalProfileImagePath) > 0) break;
					Thread.sleep(50);
				}
			}

			// 8) DB 업데이트
			memberDTO = new MemberDTO();
			memberDTO.setMemberId(memberId);
			memberDTO.setMemberPayCash(cashPayAmount); // 관리자는 0원 (차감 없음)

			if (isMemberNicknameChanged && isMemberProfileImageChanged) {
				memberDTO.setCondition("MEMBER_INFORM_UPDATE");
				memberDTO.setMemberNickname(requestMemberNickname);
				memberDTO.setMemberProfileImage(newMemberProfileImage);

			} else if (isMemberNicknameChanged) {
				memberDTO.setCondition("MEMBER_NICKNAME_UPDATE");
				memberDTO.setMemberNickname(requestMemberNickname);

			} else if (isMemberProfileImageChanged) {
				memberDTO.setCondition("MEMBER_PROFILE_UPDATE");
				memberDTO.setMemberProfileImage(newMemberProfileImage);

			} else {
				request.setAttribute("msg", "변경된 내용이 없습니다.");
				forward.setRedirect(false);
				forward.setPath(RETURN_DO);
				return forward;
			}

			System.out.println("[프로필 변경 액션 로그] DB UPDATE try condition=[" + memberDTO.getCondition()
					+ "], nick=[" + memberDTO.getMemberNickname()
					+ "], img=[" + memberDTO.getMemberProfileImage()
					+ "], pay=[" + memberDTO.getMemberPayCash() + "]");

			boolean isUpdated = memberDAO.update(memberDTO);
			System.out.println("[프로필 변경 액션 로그] DB UPDATE result=[" + isUpdated + "]");

			if (!isUpdated) {
				if (finalProfileImagePath != null) {
					try {
						boolean isDeleted = Files.deleteIfExists(finalProfileImagePath);
						System.out.println("[프로필 변경 액션 로그] 최종파일 롤백 삭제 path=[" + finalProfileImagePath + "], deleted=[" + isDeleted + "]");
					} catch (Exception e) {
						System.out.println("[프로필 변경 액션 로그] 최종파일 롤백 삭제 실패 path=[" + finalProfileImagePath + "], msg=[" + e.getMessage() + "]");
					}
				}

				request.setAttribute("msg", "회원정보 수정에 실패했습니다.");
				forward.setRedirect(false);
				forward.setPath(RETURN_DO);
				return forward;
			}

			// 성공 완료 시 업데이트된 정보로 세션 재세팅
			memberDTO = new MemberDTO();
			memberDTO.setCondition("MEMBER_MYPAGE");
			memberDTO.setMemberId(memberId);
			MemberDTO memberData2 = memberDAO.selectOne(memberDTO);

			if (memberData2 != null) {
				session.setAttribute("memberNickName", memberData2.getMemberNickname());
				session.setAttribute("memberProfileImage", memberData2.getMemberProfileImage());
				session.setAttribute("memberCash", memberData2.getMemberCash());
			}

			// 9) 성공 후 세션 임시정보 제거 + 임시파일 정리 + 기존 파일 삭제
			String remainTemporaryAbsolutePath = sessionTemporaryProfileImageAbsolutePath;

			session.removeAttribute(SESSION_PROFILE_IMAGE_TOKEN);
			session.removeAttribute(SESSION_PROFILE_IMAGE_ABSOLUTE_PATH);

			if (remainTemporaryAbsolutePath != null) {
				File temporaryRemainFile = new File(remainTemporaryAbsolutePath);

				if (temporaryRemainFile.exists() && temporaryRemainFile.isFile()) {
					boolean isSafeTempPath = remainTemporaryAbsolutePath.replace("\\", "/").contains(TEMP_PROFILE_IMAGE_DIR);
					String temporaryRemainFileName = temporaryRemainFile.getName();

					if (isSafeTempPath
							&& temporaryRemainFileName.startsWith("temporary_profile_")
							&& temporaryRemainFileName.endsWith(".jpg")) {

						boolean isDeleted = temporaryRemainFile.delete();
						System.out.println("[프로필 변경 액션 로그] 임시 파일 정리 path=[" + remainTemporaryAbsolutePath + "], deleted=[" + isDeleted + "]");
					}
				}
			}

			System.out.println("[프로필 변경 액션 로그] 기존 파일 삭제 시도 old=[" + currentMemberProfileImage + "], new=[" + newMemberProfileImage + "]");
			if (isMemberProfileImageChanged) {
				deleteOldProfileImage(
						request.getServletContext(),
						currentMemberProfileImage,
						newMemberProfileImage
				);
			}

			// 성공 후 이동: 관리자면 adminPage / 일반이면 myPage
			forward.setRedirect(true);
			forward.setPath(request.getContextPath() + RETURN_DO);
			return forward;

		} catch (Exception exception) {
			exception.printStackTrace();
			request.setAttribute("msg", "처리 중 오류가 발생했습니다.");
			forward.setRedirect(false);

			// 예외에서도 역할에 맞게 복귀 (세션 없으면 myPage가 의미 없으니 기본 myPage로)
			forward.setPath("/myPage.do");
			return forward;
		}
	}

	private static void deleteOldProfileImage(ServletContext servletContext,
	                                          String oldMemberProfileImage,
	                                          String newMemberProfileImage) {

	    if (oldMemberProfileImage == null) return;
	    if (oldMemberProfileImage.trim().isEmpty()) return;
	    if (newMemberProfileImage != null && oldMemberProfileImage.equals(newMemberProfileImage)) return;
	    if (!oldMemberProfileImage.startsWith("/upload/profile/")) return;
	    if (oldMemberProfileImage.contains("..")) return;

	    try {
	        String oldProfileImageRealPath = servletContext.getRealPath(oldMemberProfileImage);
	        if (oldProfileImageRealPath == null) return;

	        File oldProfileImageFile = new File(oldProfileImageRealPath);

	        if (oldProfileImageFile.exists() && oldProfileImageFile.isFile()) {
	            boolean deleted = oldProfileImageFile.delete();
	            System.out.println("[프로필 변경 액션 로그] 기존 프로필 이미지 삭제 완료 path=[" + oldProfileImageRealPath + "], deleted=[" + deleted + "]");
	        }

	    } catch (Exception ignore) { }
	}
}
