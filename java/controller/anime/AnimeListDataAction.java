package controller.anime;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import controller.common.Action;
import controller.common.ActionForward;
import model.dao.AnimeDAO;
import model.dto.AnimeDTO;

public class AnimeListDataAction implements Action {

	/*
	[애니 리스트 비동기 페이지네이션 + 정렬(JSON) 액션 흐름 요약]

	1) 파라미터 수신
	   - page      : 현재 페이지 번호
	   - condition : 목록/검색 분기 (ANIME_LIST_RECENT / ANIME_SEARCH_TITLE / ANIME_SEARCH_STORY)
	   - keyword   : 검색어(검색일 때만 의미)
	   - sort      : 정렬 기준
	      · RECENT : 최신등록순(=PK DESC)
	      · OLDEST : 오래된순(=PK ASC)
	      · TITLE  : 제목 가나다순(=TITLE ASC)

	2) page 검증/보정
	   - 숫자 아닌 값/0 이하 방어 → 기본 1
	   - 마지막 페이지 초과 요청 방어 → 마지막 페이지로 보정

	3) COUNT 조회(animeCount)
	   - totalPage 계산을 위해 전체 개수 필요 (정렬은 COUNT에 영향 없음)

	4) startRow/endRow 계산 후 LIST_PAGE 조회
	   - LIST_PAGE 쿼리에서 sort(정렬)을 반영한 ORDER BY 적용

	5) JSON 응답 구성
	   - animeList + paging(page/totalPage/hasPrev/hasNext/condition/keyword/sort)

	6) JSON 응답 후 return null
	   - FrontController에서 forward==null이면 추가 이동 없이 종료
	*/

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {
		// 0) JSON 응답 세팅
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json; charset=UTF-8");
		Gson gson = new Gson();

		// 0-1) 정책: 한 페이지에 보여줄 애니 개수 (3열 UI 기준 15개)
		final int listSize = 16;

		// =========================================================
		// 1) 파라미터 수신 + 정리
		// 1-1) page: 현재 페이지 번호
		int page = 1;
		try {
			String pageParam = request.getParameter("page");
			if (pageParam != null && !pageParam.trim().isEmpty()) {
				page = Integer.parseInt(pageParam.trim());
			}
		} catch (NumberFormatException e) {
			page = 1;
		}
		System.out.println("[애니리스트 데이터 액션 로그] page : ["+page+"]");
		if (page < 1) page = 1;

		// 1-2) condition: 검색/기본 목록 분기
		String condition = request.getParameter("condition");
		System.out.println("[애니리스트 데이터 액션 로그] condition : ["+condition+"]");

		// 1-3) keyword: 검색어 (빈 값은 null로 정리)
		String keyword = request.getParameter("keyword");
		if (keyword != null) {
			keyword = keyword.trim();
			if (keyword.isEmpty()) keyword = null;
		}
		System.out.println("[애니리스트 데이터 액션 로그] keyword : ["+keyword+"]");

		// 1-4) sort: 정렬 기준
		// - RECENT : 최신등록순(=PK DESC)
		// - OLDEST : 오래된순(=PK ASC)
		// - TITLE  : 제목 가나다순(=TITLE ASC)
		String sort = request.getParameter("sort");
		if (sort != null) sort = sort.trim();
		System.out.println("[애니리스트 데이터 액션 로그] sort : ["+sort+"]");

		// sort 검증/기본값 보정(화이트리스트)
		if (!"RECENT".equals(sort) && !"OLDEST".equals(sort) && !"TITLE".equals(sort)) {
			sort = "RECENT";
		}
		System.out.println("[애니리스트 데이터 액션 로그] sort 보정값 : ["+sort+"]");

		// =========================================================
		// 2) condition → DAO 컨디션 매핑(COUNT/LIST_PAGE)
		String countCondition;
		String listCondition;

		if ("ANIME_SEARCH_TITLE".equals(condition)) {
			// 제목 검색 상황이면 제목 포함 개수와 페이지 계산
			countCondition = "ANIME_COUNT_TITLE";
			listCondition  = "ANIME_LIST_PAGE_TITLE";
		} else if ("ANIME_SEARCH_STORY".equals(condition)) {
			// 줄거리 검색 상황이면 줄거리가 포함되는 개수와 페이지 계산
			countCondition = "ANIME_COUNT_STORY";
			listCondition  = "ANIME_LIST_PAGE_STORY";
		} else {
			// 둘다 아니면 기본값인 최신순으로 로직 진행
			condition = "ANIME_LIST_RECENT"; // 상태 유지용 보정
			countCondition = "ANIME_COUNT_RECENT";
			listCondition  = "ANIME_LIST_PAGE_RECENT";
		}

		// =========================================================
		// 3) animeCount 조회 (COUNT)
		AnimeDAO animeDAO = new AnimeDAO();
		AnimeDTO animeDTO = new AnimeDTO();
		animeDTO.setCondition(countCondition);
		animeDTO.setKeyword(keyword);

		AnimeDTO countData = animeDAO.selectOne(animeDTO);

		int animeCount = 0;
		if (countData != null) {
			animeCount = countData.getAnimeCount();
		}
		System.out.println("[애니리스트 데이터 액션 로그] animeCount : ["+animeCount+"]");

		// =========================================================
		// 4) totalPage 계산 + page 상한 보정
		int totalPage = (int) Math.ceil((double) animeCount / listSize);
		System.out.println("[애니리스트 데이터 액션 로그] totalPage : ["+totalPage+"]");

		// 데이터 0개여도 UI 처리를 편하게 하려면 1로 보정(목록만 빈 리스트)
		if (totalPage < 1) totalPage = 1;

		// 존재하지 않는 페이지로 조작 요청해도 마지막 페이지로 제한
		if (page > totalPage) page = totalPage;

		// =========================================================
		// 5) startRow / endRow 계산
		int startRow = (page - 1) * listSize + 1;
		int endRow = page * listSize;

		// =========================================================
		// 6) 현재 페이지 목록 조회 (LIST_PAGE)
		animeDTO = new AnimeDTO();
		animeDTO.setCondition(listCondition);
		animeDTO.setStartRow(startRow);
		animeDTO.setEndRow(endRow);
		animeDTO.setKeyword(keyword);
		animeDTO.setSort(sort); // ★ 정렬 기준 전달(DAO에서 ORDER BY 분기할 때 사용)

		List<AnimeDTO> animeList = animeDAO.selectAll(animeDTO);
		if (animeList == null) animeList = Collections.emptyList();
		// null이면 빈 리스트로 만들어서 전달 - NPE 방지

		// =========================================================
		// 7) 페이지 버튼 블록 계산
		int blockSize = 5;

		int startPage = ((page - 1) / blockSize) * blockSize + 1;
		int endPage = Math.min(startPage + blockSize - 1, totalPage);

		boolean hasPrev = startPage > 1;       // 이전 블록 존재 여부 («)
		boolean hasNext = endPage < totalPage; // 다음 블록 존재 여부 (»)

		// =========================================================
		// 8) JSON 응답 구성 - result에 담아서 전달(animeList/paging)
		// animeList : 현재 페이지에 뿌릴 데이터 목록
		// paging : 페이지바/상태유지에 필요한 메타정보 묶음
		Map<String, Object> result = new HashMap<>();
		Map<String, Object> paging = new HashMap<>();
		paging.put("page", page); // 현재 페이지(활성화 표시)
		paging.put("listSize", listSize); // 페이지당 개수
		paging.put("animeCount", animeCount); // 전체 개수(검색 결과 안내문구 등에 사용 가능)
		paging.put("totalPage", totalPage); // 총 페이지 수
		paging.put("startPage", startPage); // 페이지바 시작
		paging.put("endPage", endPage); // 페이지바 끝
		paging.put("hasPrev", hasPrev); // 이전 블록 버튼 표시 여부(T/F)
		paging.put("hasNext", hasNext); // 다음 블록 버튼 표시 여부(T/F)

		// 상태 유지용(프론트가 다음 요청 만들 때 그대로 재사용)
		paging.put("condition", condition);
		paging.put("keyword", keyword);
		paging.put("sort", sort);

		result.put("animeList", animeList);
		result.put("paging", paging);

		// =========================================================
		// 9) JSON 출력 후 종료
		try {
			response.getWriter().write(gson.toJson(result));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
}