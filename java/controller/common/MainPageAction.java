package controller.common;

import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.dao.AnimeDAO;
import model.dao.MemberDAO;
import model.dao.NewsDAO;
import model.dto.AnimeDTO;
import model.dto.MemberDTO;
import model.dto.NewsDTO;


public class MainPageAction implements Action {

    @Override
    public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
    	request.setAttribute("activeMenu", "HOME"); //메뉴 활성화용
  	    // 자동 로그인 쿠키 검사
    	 HttpSession session = request.getSession();
         if(session.getAttribute("memberName") == null) {  // 세션 X일 때만 실행
             Cookie[] cookies = request.getCookies();
          // 브라우저에 저장된 쿠키 배열 가져오기
             if(cookies != null) { // 쿠키 배열이 null이 아닌 경우에만 검사
                 for(Cookie c : cookies) { // 모든 쿠키를 하나씩 확인
                	 System.out.println("[메인페이지 로그] 자동로그인 쿠키 : ["+c.getName()+"]");
                     if(c.getName().equals("autoLogin")) {// 자동 로그인 쿠키 발견 = 이름이 "autoLogin"
                    	System.out.println("[메인페이지 로그] 자동로그인 쿠키 확인 완료 : ["+c.getName()+"]");
                     	String autoMemberName = c.getValue();
                     	// 찾았으면 쿠키에 저장된 ID 가져오기
                         MemberDAO memberDAO = new MemberDAO();
                         MemberDTO memberDTO = new MemberDTO();
                         memberDTO.setCondition("JOIN_NAME");
                         //ID 일치 여부를 확인하는 컨디션 - ID중복검사용
                         memberDTO.setMemberName(autoMemberName);
                         //검사해야하는 쿠키에 담긴 회원ID 담기
                         MemberDTO memberData = memberDAO.selectOne(memberDTO);
                         //해당 ID가 DB에 있다면 data에 담긴다

                         if(memberData != null) {
                        	 System.out.println("[메인페이지 로그] 쿠키 조회 성공 ID : ["+memberData.getMemberName()+"]");
                             // 자동 로그인 성공 → 세션 생성
                             session.setAttribute("memberId", memberData.getMemberId());
                             session.setAttribute("memberName", memberData.getMemberName());
                             session.setAttribute("memberNickName", memberData.getMemberNickname());
                             session.setAttribute("memberRole", memberData.getMemberRole());
                             session.setAttribute("memberProfileImage", memberData.getMemberProfileImage());
                             session.setAttribute("memberEmail", memberData.getMemberEmail());
                             session.setAttribute("memberPhoneNumber", memberData.getMemberPhoneNumber());
                         } 
                         else {
                             // DB에 없는 ID면 쿠키 삭제
                        	 System.out.println("[메인페이지 로그] 자동로그인 쿠키 값으로 조회 실패 (DB에 없음) autoMemberName=["+autoMemberName+"] → 쿠키 삭제");
                             Cookie cookieToDelete = new Cookie("autoLogin", "");
                             cookieToDelete.setMaxAge(0);
                             cookieToDelete.setPath("/");
                             response.addCookie(cookieToDelete);
                         }                                                
                     }
                 }
             }
         }
    	
    	//애니 뉴스 최신 3개 정보
    	//메인배너 컨디션 넣어서 최신 3개만 가져오기
    	NewsDAO newsDAO = new NewsDAO();
    	NewsDTO newsDTO = new NewsDTO();
    	newsDTO.setCondition("MAIN_BANNER_NEWSLIST");
    	List<NewsDTO> mainBannerNewsList = newsDAO.selectAll(newsDTO);
    	System.out.println("[메인페이지 로그] 메인배너 조회 mainBannerNewsList=["+mainBannerNewsList.size()+"]");
    	
    	//애니 리스트 최신 12개 정보
    	//메인페이지용 컨디션 넣어서 최신 12개만 가져오기
    	AnimeDAO animeDAO = new AnimeDAO();
    	AnimeDTO animeDTO = new AnimeDTO();
    	animeDTO.setCondition("MAIN_ANIMELIST");
    	List<AnimeDTO> mainAnimeList = animeDAO.selectAll(animeDTO);
    	System.out.println("[메인페이지 로그] 메인애니 조회 mainAnimeList=["+mainAnimeList.size()+"]");
    	    	
    	request.setAttribute("mainBannerNewsList", mainBannerNewsList);
    	request.setAttribute("mainAnimeList", mainAnimeList);
    	
    	ActionForward forward = new ActionForward();
    	forward.setPath("mainpage.jsp");
    	forward.setRedirect(false);
    	return forward;     
      }

}
