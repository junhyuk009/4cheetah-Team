package controller.util;

import java.nio.charset.StandardCharsets;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailService {

    // 발신자 (인증 계정) = 동일해야 안전함 // 키값 입력 필요
    private static final String FROM_EMAIL = "********@gmail.com";      // 예: your@gmail.com    
    private static final String PASSWORD   = "********";      // 앱 비밀번호(16자리)


    public void sendPasswordResetCode(String toEmail, String code) {
    	String fromUsername = "AniMale"; //보내는 사람의 이름으로 표시됩니다
    	
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", "smtp.gmail.com");

        // 587 STARTTLS
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");

        // STARTTLS
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");

        // TLS 1.2 강제 (No appropriate protocol 해결용)
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        // 인증서 트러스트
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        // 원인 확인용 (성공하면 false로)
        props.put("mail.debug", "false");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, PASSWORD);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL, fromUsername, "UTF-8"));

            message.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(toEmail, false)
            );

            message.setSubject("[AniMale] 애니메일 본인 인증 확인 코드", StandardCharsets.UTF_8.name());
            message.setText("인증 코드: " + code, StandardCharsets.UTF_8.name());

            Transport.send(message);

            System.out.println("[메일] 전송 성공 to=" + toEmail);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("[메일] 전송 실패");
        }
    }
}
