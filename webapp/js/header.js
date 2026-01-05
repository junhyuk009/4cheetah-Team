/**
 * header.js
 *
 * 역할
 * - 공용 헤더 기본 UI 처리
 * - 스크롤 시 헤더 상태 변경
 * - 검색 / 페이지 의존 로직 없음
 */

document.addEventListener("DOMContentLoaded", function () {

    var header = document.querySelector(".header");
    if (!header) return;

    window.addEventListener("scroll", function () {
        if (window.scrollY > 50) {
            header.classList.add("header--scrolled");
        } else {
            header.classList.remove("header--scrolled");
        }
    });

});
