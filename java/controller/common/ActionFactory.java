package controller.common;

import java.util.HashMap;
import java.util.Map;

import controller.anime.AnimeDeleteAction;
import controller.anime.AnimeEditAction;
import controller.anime.AnimeListDataAction;
import controller.anime.AnimeWriteAction;
import controller.animepage.AnimeDetailAction;
import controller.animepage.AnimeEditPageAction;
import controller.animepage.AnimeListAction;
import controller.animepage.AnimeWritePageAction;
import controller.board.BoardDeleteAction;
import controller.board.BoardEditAction;
import controller.board.BoardLikeAction;
import controller.board.BoardWriteAction;
import controller.board.ReplyDeleteAction;
import controller.board.ReplyEditAction;
import controller.board.ReplyWriteAction;
import controller.boardpage.BoardDetailAction;
import controller.boardpage.BoardEditPageAction;
import controller.boardpage.BoardListAction;
import controller.boardpage.BoardWritePageAction;
import controller.boardpage.MyPostPageAction;
import controller.member.ChangePasswordAction;
import controller.member.ChangeProfileAction;
import controller.member.FindPasswordAction;
import controller.member.JoinAction;
import controller.member.KakaoPayApproveAction;
import controller.member.KakaoPayReadyAction;
import controller.member.LoginAction;
import controller.member.LogoutAction;
import controller.member.ProfileImageUploadAction;
import controller.member.WithdrawAction;
import controller.memberpage.AdminPageAction;
import controller.memberpage.CashChargePageAction;
import controller.memberpage.ChangePasswordPageAction;
import controller.memberpage.FindPasswordPageAction;
import controller.memberpage.JoinPageAction;
import controller.memberpage.KakaoPayCancelAction;
import controller.memberpage.KakaoPayFailAction;
import controller.memberpage.LoginPageAction;
import controller.memberpage.MyPageAction;
import controller.news.NewsDeleteAction;
import controller.news.NewsEditAction;
import controller.news.NewsWriteAction;
import controller.newspage.NewsDetailAction;
import controller.newspage.NewsEditPageAction;
import controller.newspage.NewsListAction;
import controller.newspage.NewsWritePageAction;

public class ActionFactory {
	private Map<String, Action> map;
	
	public ActionFactory() {
		map = new HashMap<>();			
			
		map.put("/mainPage.do", new MainPageAction());
		map.put("/loginPage.do", new LoginPageAction());
		map.put("/login.do", new LoginAction());
		map.put("/findPasswordPage.do", new FindPasswordPageAction());
		map.put("/findPassword.do", new FindPasswordAction());
		map.put("/joinPage.do", new JoinPageAction());
		map.put("/join.do", new JoinAction());
		map.put("/myPage.do", new MyPageAction());
		map.put("/changeProfile.do", new ChangeProfileAction());
		map.put("/profileImageUpload.do", new ProfileImageUploadAction());
		map.put("/cashChargePage.do", new CashChargePageAction());
		map.put("/KakaoPayReady.do", new KakaoPayReadyAction());
		map.put("/KakaoPayApprove.do", new KakaoPayApproveAction());
		map.put("/KakaoPayFail.do", new KakaoPayFailAction());
		map.put("/KakaoPayCancel.do", new KakaoPayCancelAction());
		map.put("/changePasswordPage.do", new ChangePasswordPageAction());
		map.put("/changePassword.do", new ChangePasswordAction());
		map.put("/logout.do", new LogoutAction());
		map.put("/myPostPage.do", new MyPostPageAction());
		map.put("/withdraw.do", new WithdrawAction());

		map.put("/boardList.do", new BoardListAction());
		map.put("/boardWritePage.do", new BoardWritePageAction());
		map.put("/boardWrite.do", new BoardWriteAction());
		map.put("/boardDetail.do", new BoardDetailAction());
		map.put("/boardEditPage.do", new BoardEditPageAction());
		map.put("/boardEdit.do", new BoardEditAction());
		map.put("/boardDelete.do", new BoardDeleteAction());
		map.put("/boardLike.do", new BoardLikeAction());

		map.put("/replyWrite.do", new ReplyWriteAction());
		map.put("/replyEdit.do", new ReplyEditAction());
		map.put("/replyDelete.do", new ReplyDeleteAction());

		map.put("/newsList.do", new NewsListAction());
		map.put("/newsDetail.do", new NewsDetailAction());
		map.put("/newsWritePage.do", new NewsWritePageAction());
		map.put("/newsWrite.do", new NewsWriteAction());
		map.put("/newsEditPage.do", new NewsEditPageAction());
		map.put("/newsEdit.do", new NewsEditAction());
		map.put("/newsDelete.do", new NewsDeleteAction());

		map.put("/animeList.do", new AnimeListAction());
		map.put("/animeDetail.do", new AnimeDetailAction());
		map.put("/animeWritePage.do", new AnimeWritePageAction());
		map.put("/animeWrite.do", new AnimeWriteAction());
		map.put("/animeEditPage.do", new AnimeEditPageAction());
		map.put("/animeEdit.do", new AnimeEditAction());
		map.put("/animeDelete.do", new AnimeDeleteAction());
		map.put("/AnimeListData.do", new AnimeListDataAction());

		map.put("/adminPage.do", new AdminPageAction());		
		

	}
	
	public Action getAction(String command) {
		return map.get(command);
	}
}
