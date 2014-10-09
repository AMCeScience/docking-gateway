<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<% // Navigation url declarations %>
<portlet:actionURL name="goToPage" var="new_job_page">
    <portlet:param name="page" value="new_job" />
</portlet:actionURL>

<portlet:actionURL name="goToPage" var="jobs_page">
    <portlet:param name="page" value="project_display" />
</portlet:actionURL>

<% // Ajax url declarations %>
<portlet:resourceURL var="ajaxSearchUrl" id="SearchProjects"></portlet:resourceURL>
<portlet:resourceURL var="ajaxPartialResultUrl" id="PartialResult"></portlet:resourceURL>
<portlet:resourceURL var="ajaxUpdateStatusUrl" id="StatusUpdater"></portlet:resourceURL>
<portlet:resourceURL var="ajaxGetDetailsUrl" id="SubmissionDetails"></portlet:resourceURL>
<portlet:resourceURL var="ajaxLigandsUrl" id="LigandCollector"></portlet:resourceURL>
<portlet:resourceURL var="ajaxDownloadOutputUrl" id="Downloader"></portlet:resourceURL>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">

<html>
    <head>
        <!-- load CSS files -->
        <link href="${pageContext.request.contextPath}/css/styles.css?v=173" rel="stylesheet" type="text/css"/>
        <link href="${pageContext.request.contextPath}/css/jquery-ui.structure.css" rel="stylesheet" type="text/css"/>
        <link href="${pageContext.request.contextPath}/css/jquery-ui.theme.css" rel="stylesheet" type="text/css"/>
        
        <!-- load javascript libraries -->
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/logger.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery/jquery.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery/jquery-ui-1.10.4.custom.min.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery/validation.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery/additional-methods.min.js"></script>
        
        <!-- bind ajax urls to body as data, add validation methods -->
        <script type="text/javascript">
        	$.validator.addMethod("ligandCount", function() {
        		return $('input[name=library_check]:checked').size() > 0;
        	}, "Please select ligands/compounds.");
        
            $('body').data('ajax-urls', {
                "searchUrl": "<%= ajaxSearchUrl %>",
                "updateStatusUrl": "<%= ajaxUpdateStatusUrl %>",
                "partialResultUrl": "<%= ajaxPartialResultUrl %>",
                "getDetailsUrl": "<%= ajaxGetDetailsUrl %>",
                "ligandsUrl": "<%= ajaxLigandsUrl %>",
                "downloadOutputUrl": "<%= ajaxDownloadOutputUrl %>"
            });
        </script>
        
        <!-- load javascript function files -->
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/ajax-functions.js?v=163"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/project-functions.js?v=116"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/html-functions.js?v=118"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/combined.js?v=232"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/search.js?v=122"></script>
    </head>
    <body>
        <div id="portlet-wrapper">
            <div id="content">
                <!-- menu wrapper -->
                <div class="menu-wrapper" id="navigation">
                    <!-- menu, includes jsp logic for setting active item -->
                    <ul class="menu">
                        <li>
                            <% if(request.getParameter("nextJSP") == null || request.getParameter("nextJSP").equals("new_job")) { %>
                                <a href="<%= new_job_page.toString() %>" class="active">New Submission</a>
                            <% } else { %>
                                <a href="<%= new_job_page.toString() %>">New Submission</a>
                            <% } %>
                        </li>
                        <li>
                            <% if(request.getParameter("nextJSP") != null && request.getParameter("nextJSP").equals("project_display")) { %>
                                <a href="<%= jobs_page.toString() %>" class="active">Submissions</a>
                            <% } else { %>
                                <a href="<%= jobs_page.toString() %>">Submissions</a>
                            <% } %>
                        </li>
                    </ul>
                    <!-- end menu -->
                </div>
                <!-- end menu wrapper -->