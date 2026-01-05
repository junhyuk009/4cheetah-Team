package controller.member;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import controller.common.Action;
import controller.common.ActionForward;

public class ProfileImageUploadAction implements Action {

    /*
    [한글 흐름 요약 - ProfileImageUploadAction (비동기/임시저장/미리보기)]
      1) 마이페이지에서 프로필 이미지 파일을 multipart/form-data로 POST 전송
      2) 서버는 파일/이미지 유효성 검사(용량, Content-Type, 실제 디코딩 성공 여부, 해상도)
      3) 문제가 없으면 256x256으로 center-crop 리사이징(왜곡 방지) 후 임시 폴더에 저장
      4) 임시 이미지 URL(previewUrl)과 임시 토큰(token)을 JSON으로 반환
      5) 프론트는 previewUrl로 “리사이징된 결과”를 미리보기로 보여주고,
         token은 hidden input 등에 저장했다가 “회원정보 수정 완료(ChangeProfileAction)”에서 커밋에 사용
     
    [상단 상수 설정 이유]
    - (1) 문자열/숫자 하드코딩 방지: 오타·정책 변경 시 수정 누락을 막기 위해 상수로 통일
    - (2) 프론트/서버 연계 안정성: multipart 파트명, 임시폴더 경로, 세션 키는
          "한 글자만 달라도" 즉시 장애가 나므로 한 곳에서 관리하는 게 안전
    - (3) 유지보수성: 파일 제한 정책(용량/해상도/리사이즈) 변경 시 상단만 수정하면 끝	
	
    [하단 메서드를 따로 뺀 이유(가독성/재사용/일관성)]
    - createSuccessResponse(): 성공 응답(JSON) 규격을 한 곳에서 통일
    - createFailResponse(): 실패 응답(JSON) 규격을 한 곳에서 통일
    - writeJsonResponse(): 자바객체 → JSON 변환 + 응답 쓰기 로직을 한 곳에서 통일
    - isAllowedImageContentType(): Content-Type 1차 필터(확장자 신뢰 X)
    - resizeToSquareCenterCrop(): 왜곡 없는 256x256(중앙 크롭) 표준화 처리
    - saveAsJpeg(): JPEG 품질(압축률) 제어 저장 (ImageIO.write보다 제어 가능)  
  
     → 이제 execute()에는 "업로드 처리 흐름"만 남아서 읽기 쉬워짐
     → 정책 변경(허용 타입/리사이즈/응답 포맷)도 해당 메서드만 수정하면 됨
    */


    // ===== 업로드 정책 =====
    // - 대용량 파일 업로드로 인한 서버 부하(메모리/디스크/네트워크) 방지
    private static final long MAX_UPLOAD_FILE_BYTES = 2L * 1024 * 1024; // 2MB

    // - 초고해상도 이미지 폭탄 방지(디코딩/리사이즈 과정에서 서버 자원 소모 ↑)
    private static final int MAX_ORIGINAL_IMAGE_DIMENSION = 1024; // 원본 최대 픽셀 : 가로/세로

    // - 댓글 아바타/마이페이지 모두 커버 가능한 표준 사이즈
    // - center-crop 후 256x256으로 저장하면 UI 일관성과 성능이 좋아짐
    private static final int PROFILE_IMAGE_TARGET_SIZE = 256; // 256x256

    // 프론트에서 formData.append("profileImageFile", file) 로 보내는 "파트 이름"
    // - 서버는 request.getPart("파트이름")으로 파일을 받기 때문에
    //   프론트/서버 문자열이 불일치하면 파일을 못 받는 장애가 발생
    private static final String PROFILE_IMAGE_FILE = "profileImageFile";

    // 임시 저장 폴더(웹 경로)
    // - 미리보기는 "서버 리사이징 결과"를 보여줘야 하므로 웹에서 접근 가능한 위치가 필요
    // - 임시 폴더와 최종 폴더를 분리하면 "미리보기용(임시)" vs "확정 저장(커밋)" 책임이 명확해짐
    private static final String TEMPORARY_PROFILE_IMAGE = "/upload/profile_temp";

    // 세션 키
    // - 비동기 업로드(임시 저장) → 수정완료(커밋) 2단계 연계를 위해
    //   “내가 마지막으로 올린 임시 이미지” 정보를 세션에 저장해둠
    // - 문자열 오타를 막기 위해 상수로 관리(연계되는 다른 Action에서도 동일 키 사용)
    private static final String SESSION_PROFILE_IMAGE_TOKEN = "temporaryProfileImageToken";
    private static final String SESSION_PROFILE_IMAGE_PATH = "temporaryProfileImageAbsolutePath";

    @Override
    public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {

        // 비동기 호출이므로 JSON으로 응답
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
        Gson gson = new Gson();

        try {
            // 1) 로그인 체크(최소 방어)
            // - 마이페이지에서만 호출된다고 해도 URL 직접 호출이 가능하므로 세션 memberId 존재 유무만 확인
            HttpSession session = request.getSession(false);
            Integer memberId = (session == null) ? null : (Integer) session.getAttribute("memberId");

            if (memberId == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
                writeJsonResponse(response, gson, createFailResponse("LOGIN_REQUIRED", "로그인이 필요합니다."));
                return null;
            }

            // 2) 업로드 파일 파트 수신
            // - @MultipartConfig가 FrontController(요청을 받는 서블릿)에서 작용해야 request.getPart가 가능
            javax.servlet.http.Part profileImagePart = request.getPart(PROFILE_IMAGE_FILE);

            if (profileImagePart == null || profileImagePart.getSize() <= 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400
                writeJsonResponse(response, gson, createFailResponse("NO_FILE", "업로드 파일이 없습니다."));
                return null;
            }

            // 3) 용량 제한(서버 자원 보호)
            if (profileImagePart.getSize() > MAX_UPLOAD_FILE_BYTES) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                writeJsonResponse(response, gson, createFailResponse("FILE_TOO_LARGE", "파일 용량이 너무 큽니다. (최대 2MB)"));
                return null;
            }

            // 4) Content-Type 1차 검사(확장자 신뢰 X)
            String contentType = profileImagePart.getContentType();

            if (!isAllowedImageContentType(contentType)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                writeJsonResponse(response, gson, createFailResponse("INVALID_TYPE", "이미지 파일(jpg/png)만 업로드 가능합니다."));
                return null;
            }

            // 5) 실제 이미지 디코딩(최종 검증)
            BufferedImage originalImage;
            try (InputStream inputStream = profileImagePart.getInputStream()) {
                originalImage = ImageIO.read(inputStream);
            }

            if (originalImage == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                writeJsonResponse(response, gson, createFailResponse("INVALID_IMAGE", "이미지 파일이 아니거나 손상된 파일입니다."));
                return null;
            }

            // 6) 해상도 제한(초고해상도 폭탄 방지)
            int originalImageWidth = originalImage.getWidth();
            int originalImageHeight = originalImage.getHeight();

            if (originalImageWidth > MAX_ORIGINAL_IMAGE_DIMENSION || originalImageHeight > MAX_ORIGINAL_IMAGE_DIMENSION) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                writeJsonResponse(response, gson, createFailResponse("TOO_LARGE_DIM", "이미지 해상도가 너무 큽니다. (최대 1024px)"));
                return null;
            }

            // 7) 256x256 리사이징(비율 유지 + center-crop)
            // resizeToSquareCenterCrop 메서드 활용            
            BufferedImage resizedProfileImage = resizeToSquareCenterCrop(originalImage, PROFILE_IMAGE_TARGET_SIZE);

            // 8) 임시 저장 폴더 준비
            ServletContext servletContext = request.getServletContext();
            String temporaryProfileImageRealPath = servletContext.getRealPath(TEMPORARY_PROFILE_IMAGE);

            if (temporaryProfileImageRealPath == null) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500
                writeJsonResponse(response, gson, createFailResponse("PATH_ERROR", "서버 저장 경로를 확인할 수 없습니다."));
                return null;
            }

            File temporaryProfileImageDirectory = new File(temporaryProfileImageRealPath);

            // mkdirs(): 상위 폴더까지 모두 생성
            // 이미지 저장 임시 폴더가 없으면 생성 시도, 생성까지 실패하면 에러 반환
            if (!temporaryProfileImageDirectory.exists() && !temporaryProfileImageDirectory.mkdirs()) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                writeJsonResponse(response, gson, createFailResponse("DIR_CREATE_FAIL", "임시 폴더 생성에 실패했습니다."));
                return null;
            }

            // 9) UUID 기반 token/파일명 생성
            // UUID로 생성된 파일명 토큰 역할: 임시파일을 유일하게 식별(파일명 충돌 방지)하고,
            // 커밋(ChangeProfileAction) 단계에서 "사용자가 마지막으로 업로드한 임시 이미지"를 지정하는 키로 사용
            String temporaryProfileImageToken = UUID.randomUUID().toString();
            String temporaryProfileImageFileName = "temporary_profile_" + temporaryProfileImageToken + ".jpg";
            File temporaryProfileImageFile = new File(temporaryProfileImageDirectory, temporaryProfileImageFileName);

            // 10) JPEG 포맷으로 저장(.jpg 확장자 사용)
            // saveAsJpeg 메서드 활용
            saveAsJpeg(resizedProfileImage, temporaryProfileImageFile, 0.85f);

            // ============================================================
            // 10-1) (추가) 이전 임시 파일 삭제 시도
            // - 사용자가 미리보기 업로드를 여러 번 하면 임시파일이 계속 쌓일 수 있음
            // - 프로필 이미지 최종 수정 시 이전 파일은 더 이상 쓸 일이 없으니 정리하는 게 맞음
            // - 삭제 실패해도 기능은 계속 진행(운영 환경에서 파일 잠금/권한 이슈가 있을 수 있음)
            String previousTemporaryProfileImagePath = (String) session.getAttribute(SESSION_PROFILE_IMAGE_PATH);
            // 세션에 “이전 임시파일 절대경로”를 저장해놨었다면 그걸 꺼냄
            if (previousTemporaryProfileImagePath != null && !previousTemporaryProfileImagePath.isEmpty()) {
            	//세션에 값이 있을 때만 삭제 시도(없으면 삭제할 게 없음)
                File previousTemporaryProfileImageFile = new File(previousTemporaryProfileImagePath);
                //제 파일이 존재하면 삭제삭제 
                //성공/실패는 boolean으로 나오는데, 실패해도 기능을 중단시키진 않음(중요)
                if (previousTemporaryProfileImageFile.exists()) {
                    boolean isDeleted = previousTemporaryProfileImageFile.delete();
                    System.out.println("[프로필 임시파일 삭제] path = ["+previousTemporaryProfileImagePath+"], deleted = ["+isDeleted+"]");
                }
            }

            // 11) 세션에 임시 업로드 정보 저장(커밋 단계에서 사용)
            // “현재 사용자가 마지막으로 업로드한 임시 프로필 이미지”를 기억하는 저장소
            session.setAttribute(SESSION_PROFILE_IMAGE_TOKEN, temporaryProfileImageToken);
            session.setAttribute(SESSION_PROFILE_IMAGE_PATH, temporaryProfileImageFile.getAbsolutePath());
            // 세션 값이 ‘이전 파일’에서 ‘이번 파일’로 덮어써져서 최신 상태가 됨.

            // 12) 프론트로 미리보기 URL + token JSON 반환
            String temporaryProfileImageUrl =
                    request.getContextPath() + TEMPORARY_PROFILE_IMAGE + "/" + temporaryProfileImageFileName;

            response.setStatus(HttpServletResponse.SC_OK);
            writeJsonResponse(response, gson, createSuccessResponse(temporaryProfileImageToken, temporaryProfileImageUrl));
            return null;

        } catch (Exception exception) {
            exception.printStackTrace();
            try {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                writeJsonResponse(response, gson, createFailResponse("SERVER_ERROR", "서버 처리 중 오류가 발생했습니다."));
            } catch (Exception ignore) {}
            return null;
        }
    }

    // ============================================================
    // [JSON 응답 DTO]
    // - Gson이 이 객체를 JSON으로 변환해 응답 본문으로 내려줌
    // - 내부 클래스(private static)로 둔 이유:
    //   이 Action에서만 쓰는 응답 형태이므로 외부 노출/별도 파일 분리 없이 캡슐화하기 위함
    // ============================================================
    private static class UploadResponse {
        String result; // SUCCESS / FAIL

        // SUCCESS일 때
        String temporaryProfileImageToken;
        String temporaryProfileImageUrl;

        // FAIL일 때
        String errorCode;
        String errorMessage;
    }

    // ============================================================
    // [응답 생성 메서드]
    // - SUCCESS/FAIL 응답 규격을 통일하기 위해 메서드로 분리
    // - 필드 세팅 반복을 줄이고, 향후 응답 구조 변경 시 한 곳만 수정하도록 설계
    // ============================================================
    private UploadResponse createSuccessResponse(String temporaryProfileImageToken, String temporaryProfileImageUrl) {
        UploadResponse response = new UploadResponse();
        response.result = "SUCCESS";
        response.temporaryProfileImageToken = temporaryProfileImageToken;
        response.temporaryProfileImageUrl = temporaryProfileImageUrl;
        return response;
    }

    private UploadResponse createFailResponse(String errorCode, String errorMessage) {
        UploadResponse response = new UploadResponse();
        response.result = "FAIL";
        response.errorCode = errorCode;
        response.errorMessage = errorMessage;
        return response;
    }

    // ============================================================
    // [JSON 응답 쓰기]
    // - 자바 객체 → JSON 문자열 변환(gson.toJson)
    // - 응답 body에 write 후 flush
    // ============================================================
    private void writeJsonResponse(HttpServletResponse response, Gson gson, UploadResponse responseBody) throws IOException {
        response.getWriter().write(gson.toJson(responseBody));
        response.getWriter().flush();
    }

    // ============================================================
    // [업로드 Content-Type 1차 필터]
    // - 확장자/파일명은 조작 가능하므로 신뢰하지 않음
    // - Content-Type으로 1차 필터 후, ImageIO.read로 "실제 이미지" 여부를 최종 판별
    // - webp 제외: jpg/png만 허용
    // ============================================================
    private boolean isAllowedImageContentType(String contentType) {
        if (contentType == null) return false;
        String lower = contentType.toLowerCase();
        return lower.equals("image/jpeg") || lower.equals("image/jpg")
            || lower.equals("image/png");
    }

    // ============================================================
    // [256x256 비율 유지 + center-crop]
    // - 정사각형 아바타 표준화:
    //   1) 원본에서 중앙 기준으로 정사각형 영역을 crop
    //   2) crop 결과를 targetSize x targetSize로 고품질 리사이즈
    // - "늘려서 맞추기(왜곡)"를 피하기 위한 방식
    // ============================================================
    private BufferedImage resizeToSquareCenterCrop(BufferedImage sourceImage, int targetSize) {
        int sourceWidth = sourceImage.getWidth();
        int sourceHeight = sourceImage.getHeight();

        int cropSize = Math.min(sourceWidth, sourceHeight);
        int cropStartX = (sourceWidth - cropSize) / 2;
        int cropStartY = (sourceHeight - cropSize) / 2;

        BufferedImage croppedImage = sourceImage.getSubimage(cropStartX, cropStartY, cropSize, cropSize);

        // JPG 저장을 고려해 RGB로 생성(투명 채널 제거)
        BufferedImage outputImage = new BufferedImage(targetSize, targetSize, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = outputImage.createGraphics();

        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        graphics.drawImage(croppedImage, 0, 0, targetSize, targetSize, null);
        graphics.dispose();

        return outputImage;
    }

    // ============================================================
    // [JPEG 저장(품질 지정)]
    // - ImageIO.write(image, "jpg", file) 는 품질(압축률) 제어가 제한적
    // - ImageWriter + ImageWriteParam으로 압축 품질을 지정해 용량 최적화 가능
    // ============================================================
    private void saveAsJpeg(BufferedImage image, File file, float jpegQuality) throws IOException {
        ImageWriter jpegWriter = ImageIO.getImageWritersByFormatName("jpg").next();
        ImageWriteParam writeParam = jpegWriter.getDefaultWriteParam();

        writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        writeParam.setCompressionQuality(jpegQuality);

        try (ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(file)) {
            jpegWriter.setOutput(imageOutputStream);
            jpegWriter.write(null, new IIOImage(image, null, null), writeParam);
        } finally {
            jpegWriter.dispose();
        }
    }
}
