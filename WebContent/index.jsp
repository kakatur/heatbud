<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authorize access="isAuthenticated()">
	<%response.sendRedirect("https://www.heatbud.com/post/singing-bowls-pashmina-the-softness-of-gold");%>
</sec:authorize>
<sec:authorize access="!isAuthenticated()">
	<%response.sendRedirect("https://www.heatbud.com/do/login");%>
</sec:authorize>