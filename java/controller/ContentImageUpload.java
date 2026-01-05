package controller;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

/*
[ContentImageUpload 전체 흐름 (CKEditor5 이미지 업로드 / type 분기 / 자동 리사이즈)]

0) 요청 개요
- CKEditor5(SimpleUploadAdapter)가 이미지를 선택하면 자동으로 POST 요청을 보냄
- 요청 URL 예시
  · /ContentImageUpload?type=board
  · /ContentImageUpload?type=news
- 파일은 multipart/form-data로 전송되며, 파일 파트 이름은 보통 "upload" 임

1) 파라미터(type) 검증
- request.getParameter("type")로 분기값 수신
- 허용 값: "board", "news"
- 그 외 값이면 즉시 실패 처리(400)
  → 목적: 폴더 경로 조작(../../ 같은) 공격 방지 + 저장 정책 통일

2) 업로드 파일(Part) 수신
- request.getPart("upload")로 파일 파트 받음
- null이거나 size=0이면 실패 처리
  → 목적: 빈 업로드/잘못된 요청 방어

3) 용량(Byte) 제한 체크 (전송 크기 제한)
- @MultipartConfig:
  · maxFileSize    : 파일 1개의 최대 크기
  · maxRequestSize : 요청 전체(파일+오버헤드)의 최대 크기
- 로직에서도 upload.getSize()로 2차 체크하여 사용자 친화적 에러 메시지 제공
  → 참고: 해상도(px) 제한은 MultipartConfig로 못함(파일 내용을 읽어야 알 수 있음)

4) 1차 파일 타입 검증
- upload.getContentType()이 "image/*"인지 확인
  → 목적: 이미지 외 파일(실행파일/스크립트 등) 업로드 방지(1차 필터)

5) 파일명/확장자 검증 (화이트리스트)
- upload.getSubmittedFileName()으로 원본 파일명 수신
- 경로가 섞여 들어오는 케이스(C:/fakepath/... 등) 방어 후 실제 파일명만 추출
- 확장자 추출 후 허용 목록(jpg/jpeg/png)만 통과
  → 목적: 위험 확장자 차단 + 저장 포맷 통일

6) 이미지 디코딩(진짜 이미지인지 검증)
- ImageIO.read(upload.getInputStream())로 이미지로 디코딩 시도
- 반환값이 null이면 "이미지가 아님"으로 판단하고 실패 처리
  → 목적: 확장자만 이미지인 척 하는 파일 방어(2차 검증)

7) 픽셀 폭탄 방지(선택)
- width*height(총 픽셀 수)가 일정 기준(MAX_PIXELS) 초과면 실패
  → 목적: 매우 큰 이미지 디코딩으로 인한 메모리/CPU 폭주 방지

8) 자동 리사이즈(해상도 제한)
- max(width, height)가 MAX_DIMENSION(예: 1920px) 초과면
  · 비율 유지(scale)로 resizeWidth/resizeHeight 계산
  · Graphics2D로 고품질 리사이즈 수행
- png는 투명 채널이 있을 수 있어 alpha 유지 여부를 고려
  → 목적: 업로드 이미지를 적정 크기로 통일(용량/성능/UX 개선)

9) 저장 경로 결정 (톰캣 웹루트 하위 저장 방식)
- relDir  = "/upload/" + type (예: /upload/board)
- realDir = getServletContext().getRealPath(relDir)
  → Eclipse WTP 환경이면 실제 저장 위치는 보통:
     .../.metadata/plugins/.../wtpwebapps/프로젝트명/upload/board
- 폴더 없으면 createDirectories로 생성

10) 저장 파일명 생성(충돌 방지)
- UUID 기반 파일명 생성 (원본명 그대로 저장 X)
  → 목적: 같은 이름 충돌 방지 + 보안(경로/스크립트/특수문자) 리스크 감소

11) 파일 저장(포맷별 저장 정책)
- jpg/jpeg: 품질(압축률) 조절 저장(용량 안정화)
- png: 기본 ImageIO 저장

12) 브라우저 접근 URL 생성
- fileUrl = contextPath + relDir + "/" + savedName
  예: /ANIMAle/upload/board/xxxxx.jpg
- 파일명 URL 인코딩 처리(특수문자/공백 대비)

13) CKEditor 규격 JSON 응답
- 성공: {"url":"<접근URL>"}
  → CKEditor가 자동으로 <img src="url"> 삽입
  → 브라우저가 해당 src로 GET 요청을 보내 이미지 렌더링
- 실패: {"error":{"message":"사유"}}
  → CKEditor가 업로드 실패 메시지로 표시

[핵심 요약]
- MultipartConfig = 용량(Byte) 제한용
- 해상도(px) 제한/리사이즈 = 이미지 디코딩 후 로직에서 처리
- type=board/news로 저장 폴더 분기
- 성공 JSON은 반드시 {"url": "..."} 형태로 반환해야 CKEditor가 자동 삽입됨
*/

@WebServlet("/ContentImageUpload")
@MultipartConfig( //이미지 파일을 수신할 수 있는 설정값
    //※업로드로 들어오는 파일 용량을 제한
    //자동 리사이즈를 하더라도, 업로드가 이 제한을 넘으면 리사이즈 전에 막힌다.
    maxFileSize = 5 * 1024 * 1024, // 5MB
    //이미지 업로드 요청 안에 들어있는 “파일 1개”의 최대 크기
    maxRequestSize = 7 * 1024 * 1024 // 7MB
    //HTTP 요청 전체(multipart/form-data 전체)의 최대 크기
)
public class ContentImageUpload extends HttpServlet {
	// 에디터 사용이 허용된건 뉴스 본문과 게시글 본문 뿐이므로 타입을 지정하여 선언
    private static final Set<String> ALLOWED_TYPES =
            Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("board", "news")));

    // webp는 기본 ImageIO에서 "읽기/쓰기"가 안 되는 환경이 많아서 일단 제외 권장
    private static final Set<String> ALLOWED_EXTENSION =
            Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("jpg", "jpeg", "png")));

    // 업로드 용량 제한값 설정(멀티파트컨픽과 동일한 값=maxFileSize)
    private static final long MAX_BYTES = 5L * 1024 * 1024; // 5MB

    // 자동 리사이즈 기준: 가로/세로 중 큰 값이 이 값을 넘으면 줄인다
    private static final int MAX_DIMENSION = 1920;

    // 디코딩 폭탄 방지(너무 큰 이미지 차단)
    private static final long MAX_PIXELS = 10_000_000L; // 1천만 픽셀

    // JPG 저장 품질(0~1). 값 낮출수록 용량↓ 품질↓
    private static final float JPEG_QUALITY = 0.85f;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");

        // 지정된 타입 형식인지 검사         
        try {
            // 0) type 파라미터 검증 (폴더 분기 + 디렉토리 조작 방지)
            String type = request.getParameter("type");
            if (type == null || !ALLOWED_TYPES.contains(type)) {
                writeCkError(response, "type 파라미터가 올바르지 않습니다. (board/news만 허용)");
                return;
            }

            // 1) CKEditor(SimpleUploadAdapter)의 기본 파일 필드명: upload
            Part upload = request.getPart("upload");

            // 2) null / empty 체크
            if (upload == null || upload.getSize() == 0) {
                writeCkError(response, "업로드된 파일이 없습니다.");
                return;
            }

            // 3) 업로드 용량 제한(로직 2차 체크)
            if (upload.getSize() > MAX_BYTES) {
            	int maxMB = (int) (MAX_BYTES / (1024 * 1024)); //계산결과로 5가 남게된다.
                writeCkError(response, "이미지 용량은 최대 " + maxMB + "MB 까지만 업로드할 수 있습니다.");
                return;
            }

            // 4) Content-Type 1차 체크
            // - 브라우저/클라이언트가 전송한 MIME 타입(예: "image/png", "image/jpeg") 확인
            // - "image/" 로 시작하지 않으면 이미지가 아니라고 판단하고 업로드 차단
            String contentType = upload.getContentType(); // ex) image/png
            if (contentType == null || !contentType.toLowerCase(Locale.ROOT).startsWith("image/")) {
                writeCkError(response, "이미지 파일만 업로드할 수 있습니다.");
                return;
            }

            // 5) 원본 파일명 / 확장자 체크
            String fileName = upload.getSubmittedFileName();
            if (fileName == null || fileName.trim().isEmpty()) {
                writeCkError(response, "파일명이 올바르지 않습니다.");
                return;
            }

            // 브라우저가 "C:\\fakepath\\xxx.png" 같이 보내는 경우 방어
            // 경로 부분을 전부 제거하고 "진짜 파일명(마지막 이름 부분)"만 추출한다.
            String originalName = Paths.get(fileName).getFileName().toString();
            // 추출된 안전한 파일명에서 확장자만 분리한다.
            String extension = getExt(originalName);

            if (extension.isEmpty() || !ALLOWED_EXTENSION.contains(extension)) {
                writeCkError(response, "허용되지 않은 확장자입니다. (jpg, jpeg, png만 지원)");
                return;
            }

            // 6) 이미지 디코딩 (여기서 진짜 이미지인지 판별됨)
            BufferedImage image = ImageIO.read(upload.getInputStream());
            if (image == null) {
                writeCkError(response, "올바른 이미지 파일이 아닙니다.");
                return;
            }

            int width = image.getWidth();
            int height = image.getHeight();

            // 7) 픽셀 총량 제한(메모리/폭탄 방지)
            long pixels = (long) width * (long) height;
            if (pixels > MAX_PIXELS) { // 1천만 픽셀보다 높으면 에러 처리
                writeCkError(response, "이미지가 너무 큽니다. (픽셀 수 제한 초과: " + width + "x" + height + ")");
                return;
            }

            // 8) 자동 리사이즈 여부 판단
            // - 가로/세로 중 큰 값이 MAX_DIMENSION 초과면 비율 유지 축소
            int maxSide = Math.max(width, height);
            if (maxSide > MAX_DIMENSION) { //1920;
                double scale = (double) MAX_DIMENSION / (double) maxSide;
                int resizeWidth = Math.max(1, (int) Math.round(width * scale));
                int resizeHeight = Math.max(1, (int) Math.round(height * scale));

                // png는 투명 채널이 있을 수 있으니 alpha 유지
                // → PNG면 투명 유지하면서 리사이즈
                // → JPG면 투명 없으니 그냥 RGB로 리사이즈(배경 흰색 등)
                boolean keepAlpha = extension.equals("png");
                image = resize(image, resizeWidth, resizeHeight, keepAlpha);
                // PNG 업로드 시 투명 배경이 깨지지 않게 “투명도 정보를 유지한 채로 리사이즈”하는 기능
            }

            // 9) 저장 폴더 결정: /upload/{type}
            // - 뉴스면 news폴더 게시글이면 board 폴더에 저장되도록 설정
            String relDir = "/upload/" + type;
            String realDir = request.getServletContext().getRealPath(relDir);
            if (realDir == null) {
                writeCkError(response, "서버 저장 경로를 찾을 수 없습니다. (getRealPath가 null)");
                return;
            }

            Path saveDir = Paths.get(realDir);
            // saveDir 경로에 폴더가 이미 있으면 아무 일도 안 함
            Files.createDirectories(saveDir);
            // 폴더가 없으면 자동 생성(상위 폴더까지 포함) / 이미 있으면 그대로 통과

            // 10) 저장 파일명(충돌 방지)
            String savedName = UUID.randomUUID().toString().replace("-", "") + "." + extension;
            File savedFile = saveDir.resolve(savedName).toFile();
           
            // 11) 파일 저장
            // - jpg/jpeg는 품질 조절 저장(용량 안정)
            // - png는 기본 ImageIO 저장
	            if (extension.equals("jpg") || extension.equals("jpeg")) {
	                // JPG는 알파가 없으니 RGB로 저장하는 게 안전
	                BufferedImage rgb = toRgb(image);
	                writeJpeg(rgb, savedFile, JPEG_QUALITY);
	            } else {
	                // png
	                ImageIO.write(image, extension, savedFile);
	            }

            // 12) 브라우저 접근 URL 생성
	        String fileUrl = request.getContextPath() + relDir + "/" + savedName;
            
            // 13) CKEditor 성공 응답
            response.getWriter().write("{\"url\":\"" + escapeJson(fileUrl) + "\"}");

        } catch (IllegalStateException e) {
            // MultipartConfig 용량 제한 초과 등
            writeCkError(response, "파일이 너무 큽니다. (업로드 용량 제한 초과)");
        } catch (Exception e) {
            e.printStackTrace();
            writeCkError(response, "업로드 중 오류가 발생했습니다.");
        }
    }

    /*
     * 확장자 추출
     */
    private String getExt(String filename) {
        int dot = filename.lastIndexOf('.');
        if (dot < 0 || dot == filename.length() - 1) return "";
        return filename.substring(dot + 1).toLowerCase(Locale.ROOT);
    }

    /*
     * 자동 리사이즈 (비율 유지)
     * - keepAlpha=true면 투명 채널 유지(ARGB)
     * - jpg 저장은 알파가 없으니 later 단계에서 RGB 변환한다.
     */
    private BufferedImage resize(BufferedImage src, int resizeWidth, int resizeHeight, boolean keepAlpha) {
        int type = keepAlpha ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;

        BufferedImage dst = new BufferedImage(resizeWidth, resizeHeight, type);
        Graphics2D g = dst.createGraphics();

        // 품질 관련 힌트(계단현상 줄이기)
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 알파가 없는 타입이면 배경을 흰색으로 깔아주기(검은 배경 방지)
        if (!keepAlpha) {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, resizeWidth, resizeHeight);
        }

        g.drawImage(src, 0, 0, resizeWidth, resizeHeight, null);
        g.dispose();
        return dst;
    }

    //JPG 저장을 위해 RGB 타입으로 변환     
    private BufferedImage toRgb(BufferedImage src) {
        if (src.getType() == BufferedImage.TYPE_INT_RGB) return src;

        BufferedImage rgb = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = rgb.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, rgb.getWidth(), rgb.getHeight());
        g.drawImage(src, 0, 0, null);
        g.dispose();
        return rgb;
    }

    //JPG 품질(압축률) 조절 저장    
    private void writeJpeg(BufferedImage img, File outFile, float quality) throws IOException {
        ImageWriter writer = null;
        ImageOutputStream ios = null;

        try {
            writer = ImageIO.getImageWritersByFormatName("jpeg").next();
            ImageWriteParam param = writer.getDefaultWriteParam();

            if (param.canWriteCompressed()) {
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(quality); // 0~1
            }

            ios = ImageIO.createImageOutputStream(outFile);
            writer.setOutput(ios);
            writer.write(null, new IIOImage(img, null, null), param);

        } finally {
            if (ios != null) ios.close();
            if (writer != null) writer.dispose();
        }
    }

    //CKEditor 실패 응답 규격     
    private void writeCkError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(400);
        response.getWriter().write("{\"error\":{\"message\":\"" + escapeJson(message) + "\"}}");
    }

    //JSON 문자열 이스케이프     
    private String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
