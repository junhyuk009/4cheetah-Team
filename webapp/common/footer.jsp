<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<footer class="footer">
  <div class="container">

    <!-- 상단 영역 -->
    <div class="footer__top">

      <!-- 로고 -->
      <div class="footer__logo">
        <a href="${ctx}/mainPage.do">
          <img src="${ctx}/img/animale-logo.png" alt="AniMale 로고">
        </a>
      </div>

      <!-- 슬로건 -->
      <div class="footer__text">
        <h5>애니메일, 애니매일~</h5>
        <p>당신의 하루에 애니 한 스푼</p>
      </div>

    </div>

    <!-- 하단 영역 -->
    <div class="footer__bottom">
      <p class="footer__copy">Copyright © 2025 AniMale. 모든 권리 보유.</p>

      <!-- 찾아오시는 길 버튼 (푸터 안) -->
      <div class="footer__directions">
        <button type="button"
                id="btnOpenDirections"
                class="btn btn-primary footer__directions-btn">
          찾아오시는 길
        </button>
      </div>
    </div>

  </div>
</footer>

<!-- 지도 모달 (부트스트랩 모달) -->
<div class="modal fade" id="mapModal" tabindex="-1" role="dialog" aria-hidden="true">
  <div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">

      <div class="modal-header">
        <h5 class="modal-title">찾아오시는 길</h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>

      <div class="modal-body">
        <div id="kakaoMap" style="width:100%; height:420px;"></div>
        <div class="mapModal__address">주소: 서울특별시 강남구 논현로94길 13</div>
      </div>

    </div>
  </div>
</div>

<!-- Kakao Map SDK (공용) --> <!-- 키값 입력 필요 -->
<script src="//dapi.kakao.com/v2/maps/sdk.js?appkey=*********************&libraries=services"></script>

<script>
(function () {
  var ADDRESS = "서울특별시 강남구 논현로94길 13";

  var map = null;
  var marker = null;
  var cachedLatLng = null;
  var geocoder = null;

  function bindWhenReady() {
    // jQuery/Bootstrap modal 로드 기다리기
    if (!window.jQuery || !jQuery.fn || !jQuery.fn.modal) {
      return setTimeout(bindWhenReady, 50);
    }

    var $ = window.jQuery;

    // 버튼 클릭 -> 모달 열기
    $(document)
      .off("click.directions", "#btnOpenDirections")
      .on("click.directions", "#btnOpenDirections", function () {
        $("#mapModal").modal("show");
      });

    // 모달 열림 -> 지도 생성/리레이아웃
    $("#mapModal")
      .off("shown.bs.modal.directions")
      .on("shown.bs.modal.directions", function () {

        if (!window.kakao || !kakao.maps) {
          alert("카카오 지도 SDK 로딩 실패 (appkey/도메인 등록 확인)");
          return;
        }

        if (!geocoder) geocoder = new kakao.maps.services.Geocoder();

        // 이미 생성됐으면 리레이아웃만
        if (map) {
          map.relayout();
          if (cachedLatLng) map.setCenter(cachedLatLng);
          return;
        }

        var container = document.getElementById("kakaoMap");
        map = new kakao.maps.Map(container, {
          center: new kakao.maps.LatLng(37.5665, 126.9780),
          level: 3
        });

        geocoder.addressSearch(ADDRESS, function (result, status) {
          if (status !== kakao.maps.services.Status.OK || !result || !result[0]) {
            alert("주소 변환 실패(도메인/키/주소 확인)");
            return;
          }

          var lat = parseFloat(result[0].y);
          var lng = parseFloat(result[0].x);
          cachedLatLng = new kakao.maps.LatLng(lat, lng);

          map.setCenter(cachedLatLng);

          marker = new kakao.maps.Marker({ position: cachedLatLng });
          marker.setMap(map);

          map.relayout();
          map.setCenter(cachedLatLng);
        });
      });
  }

  bindWhenReady();
})();
</script>
