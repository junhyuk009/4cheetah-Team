package controller.common;

import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class InitListener implements ServletContextListener {

    public InitListener() {
    }

    public void contextDestroyed(ServletContextEvent sce)  { 
    }

	public void contextInitialized(ServletContextEvent sce)  {
		System.out.println("[로그] 서버가 시작될때를 감지(모니터링)하여 리스너가 자동수행됨");

		// application scope에 datas 생성해서 저장
		// 크롤링용 페이지
		ServletContext application = sce.getServletContext();		
		
    }
	
}
