package controller.news;

import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import controller.common.Action;
import controller.common.ActionForward;
import model.dao.NewsDAO;
import model.dto.NewsDTO;

public class NewsWriteAction implements Action {

   // 썸네일 저장 폴더 (웹루트 하위)
   private static final String THUMB_DIR = "/upload/newsThumb";

   // 썸네일 업로드 정책
   private static final long THUMB_MAX_BYTES = 5L * 1024 * 1024; // 5MB

   // Collections.unmodifiableSet + HashSet
   private static final Set<String> ALLOWED_THUMB_EXT =
         Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("jpg", "jpeg", "png")));

   @Override
   public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
      ActionForward forward = new ActionForward();

      System.out.println("[로그] NewsWriteAction execute : start");

      try {
         request.setCharacterEncoding("UTF-8");

         // 0) GET이면 작성페이지로
         String method = request.getMethod();
         System.out.println("[로그] NewsWriteAction execute : method=" + method);

         if ("GET".equalsIgnoreCase(method)) {
            forward.setPath("newsWritePage.do");
            forward.setRedirect(true);
            return forward;
         }

         // 1) 세션 체크 + 관리자 체크
         HttpSession session = request.getSession(false);
         if (session == null || session.getAttribute("memberId") == null) {
            request.setAttribute("msg", "로그인이 필요합니다.");
            request.setAttribute("location", "loginPage.do");
            forward.setPath("message.jsp");
            forward.setRedirect(false);
            return forward;
         }

         String memberRole = (String) session.getAttribute("memberRole");
         if (!"ADMIN".equals(memberRole)) {
            request.setAttribute("msg", "관리자만 뉴스 작성이 가능합니다.");
            request.setAttribute("location", "mainPage.do");
            forward.setPath("message.jsp");
            forward.setRedirect(false);
            return forward;
         }

         // 2) 파라미터 수신
         String animeIdStr = request.getParameter("animeId");
         String newsTitle = request.getParameter("newsTitle");
         String newsContent = request.getParameter("newsContent");

         // 2-1) 기본 유효성(최소)
         if (newsTitle == null || newsTitle.trim().isEmpty()) {
            request.setAttribute("msg", "제목은 필수입니다.");
            request.setAttribute("location", "newsWritePage.do");
            forward.setPath("message.jsp");
            forward.setRedirect(false);
            return forward;
         }
         newsTitle = newsTitle.trim();
         if (newsTitle.length() > 255) {
            request.setAttribute("msg", "제목은 255자 이내로 작성해주세요.");
            request.setAttribute("location", "newsWritePage.do");
            forward.setPath("message.jsp");
            forward.setRedirect(false);
            return forward;
         }

         if (newsContent == null || newsContent.trim().isEmpty()) {
            request.setAttribute("msg", "내용은 필수입니다.");
            request.setAttribute("location", "newsWritePage.do");
            forward.setPath("message.jsp");
            forward.setRedirect(false);
            return forward;
         }
         newsContent = newsContent.trim();

         // 아주 약한 XSS 1차 방어
         String lower = newsContent.toLowerCase(Locale.ROOT);
         if (lower.contains("<script") || lower.contains("javascript:")) {
            request.setAttribute("msg", "허용되지 않는 내용이 포함되어 있습니다.");
            request.setAttribute("location", "newsWritePage.do");
            forward.setPath("message.jsp");
            forward.setRedirect(false);
            return forward;
         }

         // 3) 썸네일 업로드 (multipart)
         Part thumbPart = request.getPart("thumbFile");
         if (thumbPart == null || thumbPart.getSize() == 0) {
            request.setAttribute("msg", "썸네일 이미지를 선택해주세요.");
            request.setAttribute("location", "newsWritePage.do");
            forward.setPath("message.jsp");
            forward.setRedirect(false);
            return forward;
         }

         // 3-1) 썸네일 검증 + 저장
         String newsThumbnailUrl = saveThumbAndReturnUrl(request, thumbPart, THUMB_DIR);

         // 3-2) 본문에서 대표 이미지 1장 추출(있으면)
         String newsImageUrl = extractFirstImgSrc(newsContent);

         // 4) DTO 세팅
         NewsDTO newsDTO = new NewsDTO();

         if (animeIdStr != null && !animeIdStr.trim().isEmpty()) {
            try {
               newsDTO.setAnimeId(Integer.parseInt(animeIdStr.trim()));
            } catch (NumberFormatException ignore) {
               // 선택값이니까 무시
            }
         }

         newsDTO.setNewsTitle(newsTitle);
         newsDTO.setNewsContent(newsContent);
         newsDTO.setNewsThumbnailUrl(newsThumbnailUrl);
         newsDTO.setNewsImageUrl(newsImageUrl);
         newsDTO.setCondition("NEWS_INSERT");

         // 5) DAO insert
         NewsDAO newsDAO = new NewsDAO();
         boolean result = newsDAO.insert(newsDTO);
         System.out.println("[로그] NewsWriteAction 추가완료 : newsContent=" + newsContent);

         if (!result || newsDTO.getNewsId() <= 0) {
            request.setAttribute("msg", "뉴스 등록에 실패했습니다.");
            request.setAttribute("location", "newsWritePage.do");
            forward.setPath("message.jsp");
            forward.setRedirect(false);
            return forward;
         }

         // 6) 상세로 이동
         int newsId = newsDTO.getNewsId();
         forward.setPath("newsDetail.do?newsId=" + newsId);
         forward.setRedirect(true);
         return forward;

      } catch (IllegalArgumentException e) {
         request.setAttribute("msg", e.getMessage());
         request.setAttribute("location", "newsWritePage.do");
         forward.setPath("message.jsp");
         forward.setRedirect(false);
         return forward;

      } catch (Exception e) {
         e.printStackTrace();
         request.setAttribute("msg", "뉴스 작성 처리 중 오류가 발생했습니다. (서버 로그 확인 필요)");
         request.setAttribute("location", "newsWritePage.do");
         forward.setPath("message.jsp");
         forward.setRedirect(false);
         return forward;
      }
   }

   private String saveThumbAndReturnUrl(HttpServletRequest request, Part part, String webDir) throws Exception {

      // 1) 용량 제한
      if (part.getSize() > THUMB_MAX_BYTES) {
         int maxMB = (int) (THUMB_MAX_BYTES / (1024 * 1024));
         throw new IllegalArgumentException("썸네일 용량은 최대 " + maxMB + "MB 까지만 업로드할 수 있습니다.");
      }

      // 2) Content-Type 1차 체크
      String ct = part.getContentType();
      if (ct == null || !ct.toLowerCase(Locale.ROOT).startsWith("image/")) {
         throw new IllegalArgumentException("썸네일은 이미지 파일만 업로드할 수 있습니다.");
      }

      // 3) 파일명/확장자 추출
      String submitted = part.getSubmittedFileName();
      if (submitted == null || submitted.trim().isEmpty()) {
         throw new IllegalArgumentException("썸네일 파일명이 올바르지 않습니다.");
      }

      String safeName = Paths.get(submitted).getFileName().toString();
      String ext = getExt(safeName);
      if (ext.isEmpty() || !ALLOWED_THUMB_EXT.contains(ext)) {
         throw new IllegalArgumentException("썸네일은 jpg/jpeg/png만 업로드할 수 있습니다.");
      }

      // 4) 실제 이미지인지 2차 검증(ImageIO 디코딩)
      BufferedImage img = ImageIO.read(part.getInputStream());
      if (img == null) {
         throw new IllegalArgumentException("올바른 이미지 파일이 아닙니다.");
      }

      // 5) 저장 폴더 준비
      String realDir = request.getServletContext().getRealPath(webDir);
      if (realDir == null) {
         throw new IllegalArgumentException("서버 저장 경로를 찾을 수 없습니다. (getRealPath가 null)");
      }

      Path dir = Paths.get(realDir);
      Files.createDirectories(dir);

      // 6) 저장 파일명(UUID)
      String savedName = UUID.randomUUID().toString().replace("-", "") + "." + ext;
      Path savedPath = dir.resolve(savedName);
     
      InputStream in = null;
      try {
         in = part.getInputStream();
         Files.copy(in, savedPath, StandardCopyOption.REPLACE_EXISTING);
      } finally {
         if (in != null) try { in.close(); } catch (Exception ignore) {}
      }

      // 7) 접근 URL 반환
      return request.getContextPath() + webDir + "/" + savedName;
   }

   private String getExt(String filename) {
      int dot = filename.lastIndexOf('.');
      if (dot < 0 || dot == filename.length() - 1) return "";
      return filename.substring(dot + 1).toLowerCase(Locale.ROOT);
   }

   private String extractFirstImgSrc(String html) {
      if (html == null) return null;

      java.util.regex.Matcher m = java.util.regex.Pattern
            .compile("<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"]", java.util.regex.Pattern.CASE_INSENSITIVE)
            .matcher(html);

      return m.find() ? m.group(1) : null;
   }
}
