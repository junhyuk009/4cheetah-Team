<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!-- active 클래스 계산 (B 네이밍 유지) -->
<c:set var="homeCls" value="" />
<c:if test="${activeMenu eq 'HOME'}"><c:set var="homeCls" value="active" /></c:if>

<c:set var="aniCls" value="" />
<c:if test="${activeMenu eq 'ANILIST'}"><c:set var="aniCls" value="active" /></c:if>

<c:set var="comCls" value="" />
<c:if test="${activeMenu eq 'COMMUNITY'}"><c:set var="comCls" value="active" /></c:if>

<c:set var="newsCls" value="" />
<c:if test="${activeMenu eq 'NEWS'}"><c:set var="newsCls" value="active" /></c:if>

<header class="header">
  <div class="container">
    <div class="row align-items-center">

      <%-- Logo --%>
      <div class="col-lg-2">
        <div class="header__logo">
          <a href="${ctx}/mainPage.do" class="logo-img">
            <img src="${ctx}/img/animale-logo.png" alt="AniMale 로고" />
          </a>
        </div>
      </div>

      <%-- Menu --%>
      <div class="col-lg-8">
        <nav class="header__menu">
          <ul>
            <li class="${homeCls}">
              <a href="${ctx}/mainPage.do">HOME</a>
            </li>

            <li class="${aniCls}">
              <a href="${ctx}/animeList.do">ANILIST</a>
            </li>

            <li class="dropdown ${comCls}">
              <a href="javascript:void(0)">COMMUNITY</a>
              <ul class="dropdown-menu">
                <li>
                  <a href="${ctx}/boardList.do?category=ANIME">ANIME</a>
                </li>
              </ul>
            </li>

            <li class="${newsCls}">
              <a href="${ctx}/newsList.do">NEWS</a>
            </li>
          </ul>
        </nav>
      </div>

      <%-- Right --%>
      <div class="col-lg-2">
        <div class="header__right">

          <%-- Profile Icon --%>
          <c:if test="${hideProfileIcon ne true}">
            <div class="header__right__icons">
              <c:choose>

                <%-- 비로그인: 로그인 페이지로 --%>
                <c:when test="${empty sessionScope.memberId}">
                  <a href="${ctx}/loginPage.do">
                    <span class="icon_profile"></span>
                  </a>
                </c:when>

                <%-- 관리자 --%>
                <c:when test="${sessionScope.memberRole eq 'ADMIN'}">
                  <a href="${ctx}/adminPage.do">
                    <span class="icon_profile"></span>
                  </a>
                </c:when>

                <%-- 일반 회원 --%>
                <c:otherwise>
                  <a href="${ctx}/myPage.do">
                    <span class="icon_profile"></span>
                  </a>
                </c:otherwise>

              </c:choose>
            </div>
          </c:if>

          <%-- 로그아웃 버튼 (로그인 상태에서만) --%>
          <c:if test="${not empty sessionScope.memberId}">
            <a href="${ctx}/logout.do" class="logout-link">로그아웃</a>
          </c:if>

        </div>
      </div>

    </div>
  </div>
</header>

<script src="${ctx}/js/header.js" defer></script>
