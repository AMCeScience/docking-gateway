<jsp:include page="/jsp/template/header.jsp" />

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<portlet:actionURL name="submitUserSetupForm" var="submit_user_setup_url"></portlet:actionURL>

<script type="text/javascript">
    $(function() {
        user_setup();
    });
</script>

<% if(!(request.getParameter("form_errors") == null || request.getParameter("form_errors").equals(""))) { %>
    <div class="error">
        <span><%= request.getParameter("form_errors") %></span>
    </div>
<% } %>

<div class="form_errors error hidden"></div>

<form class="user_setup" action="<%= submit_user_setup_url.toString() %>" method="POST" enctype="multipart/form-data">
    <div class="top">
    	<h1>User Password Setup</h1>
    	<p>Either your password has changed or your account has not been setup yet. Please fill out and submit your liferay password in the form below.</p>
        <label for="liferay_password">Liferay Password</label>
        <input name="liferay_password" type="password" required/>

        <div class="submit_row">
            <input class="submit" type="button" value="Submit" onclick="user_setup_submit();" />
        </div>
    </div>
</form>

<jsp:include page="/jsp/template/footer.jsp" />