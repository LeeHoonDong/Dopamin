<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html lang="ko">

<head>
    <title>도파민!</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="icon" type="image/x-icon" href="<c:url value='/image/favicon.png' />">
    <link rel="stylesheet" type="text/css" href="<c:url value='/css/common/normalize.css' />">
    <link rel="stylesheet" type="text/css" href="<c:url value='/css/common/default.css' />">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.1.2/css/all.min.css"
          integrity="sha512-1sCRPdkRXhBV2PBLUdRb4tMg1w2YPf37qatUFeS7zlBy7jJI8Lf4VHwWfZZfpXtYSLy85pkm9GaYVYMfw5BC1A=="
          crossorigin="anonymous" referrerpolicy="no-referrer"/>
    <style>
        @import url('https://fonts.googleapis.com/css2?family=Abril+Fatface&family=Noto+Sans+KR&family=Noto+Serif&display=swap');
    </style>
    <script src="https://code.jquery.com/jquery-1.11.3.js"></script>

</head>

<body>

<div class="topnav">
    <div class="responsive">
        <div class="logo-wrapper">
            <img alt="" src="<c:url value='/image/logo_example.svg' />" width="50" height="30">
        </div>
        <a class="topnav-menu" href="#" onclick='alert("홈으로 페이지 전환")'>홈</a>
        <a class="topnav-menu" href="#" onclick='alert("커뮤니티 페이지로 이동")'>커뮤니티</a>
        <a class="topnav-menu" href="#" onclick='alert("찜목록 페이지로 이동")'>찜목록</a>
        <a class="topnav-menu" href="#" onclick='alert("시청기록 게시판봐요")'>시청기록</a>
        <a class="topnav-menu" href="#" onclick='alert("룰렛 한판 돌려요")'>이벤트</a>
        <a class="topnav-menu" href="#" onclick='alert("도파~파워")'>상점</a>
    </div>
</div>

<div class="content">
    <div class="responsive-content">
        <div>
            <h1> 내가 작성한 커뮤니티 게시글 모아보기 </h1>
        </div>
        <div class="cnts">
            <c:forEach var="post" items="${postDtoList}">
                <br>
                <c:choose>
                    <c:when test="${post.post_visib eq 0}">
                        <tr>
                            <td> 가려진 게시물 입니다.</td>
                        </tr>
                        <br>
                    </c:when>
                    <c:otherwise>
                        <tr>
                            <td>${post.post_id}</td>
                            <td>${post.bbs_id == 1 ? '[후기]' : '[자게]'}</td>
                            <td>${post.post_title}</td>
                            <td>${post.post_reply_cnt}</td>
                            <td><fmt:formatDate value="${post.in_date}" pattern="yyyy.mm.dd"/></td>
                            <td>${post.post_view_cnt}</td>
                        </tr>
                        <br>
                    </c:otherwise>
                </c:choose>
            </c:forEach>
        </div>

        <div>
            <h3>post title 누르면 게시글 상세 페이지로 이동 추가</h3>
            <h3>체크박스 체크하면 게시글 한번에 삭제되는 기능 추가 </h3>
            <h3></h3>

        </div>
        <div>
            <i class="fa-brands fa-instagram"></i>
            <i class="fa-brands fa-facebook"></i>
            <i class="fa-brands fa-youtube"></i>
            <div class="content-preview">
            </div>
            <div class="content-teenager-girl-movie"></div>

        </div>
    </div>


    <div class="footer">
        <div>about</div>
        <div>2022 PSVM팀</div>
    </div>
</div>
</body>

</html>