<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<% // Navigation url declarations %>
<portlet:actionURL name="goToPage" var="new_job_page">
    <portlet:param name="page" value="new_job" />
    <portlet:param name="page_type" value="" />
</portlet:actionURL>

<portlet:actionURL name="goToPage" var="in_process_page">
    <portlet:param name="page" value="project_display" />
    <portlet:param name="page_type" value="in_process" />
</portlet:actionURL>

<portlet:actionURL name="goToPage" var="outcomes_page">
    <portlet:param name="page" value="project_display" />
    <portlet:param name="page_type" value="outcomes" />
</portlet:actionURL>

<% // Ajax url declarations %>
<portlet:resourceURL var="ajaxSearchUrl" id="doSearch"></portlet:resourceURL>
<portlet:resourceURL var="ajaxUpdateStatusUrl" id="updateStatus"></portlet:resourceURL>
<portlet:resourceURL var="ajaxLigandsUrl" id="getLigands"></portlet:resourceURL>
<portlet:resourceURL var="ajaxDownloadOutputUrl" id="doDownload"></portlet:resourceURL>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">

<html>
    <head>
        <!-- load CSS files -->
        <link href="${pageContext.request.contextPath}/css/styles.css?v=169" rel="stylesheet" type="text/css"/>
        <link href="${pageContext.request.contextPath}/css/jquery-ui.min.css" rel="stylesheet" type="text/css"/>
        
        <!-- load javascript libraries -->
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/logger.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery/jquery.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery/jquery-ui-1.10.4.custom.min.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery/validation.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery/additional-methods.min.js"></script>
        
        <!-- bind ajax urls to body as data, add validation methods -->
        <script type="text/javascript">
        	$.validator.addMethod("ligandCount", function() {
        		return $('input[name=compound_check]:checked').size() > 0;
        	}, "Please select ligands/compounds.");
        
            $('body').data('ajax-urls', {
                "searchUrl": "<%= ajaxSearchUrl %>",
                "updateStatusUrl": "<%= ajaxUpdateStatusUrl %>",
                "ligandsUrl": "<%= ajaxLigandsUrl %>",
                "downloadOutputUrl": "<%= ajaxDownloadOutputUrl %>"
            });
        </script>
        
        <!-- load javascript function files -->
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/ajax-functions.js?v=156"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/project-functions.js?v=109"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/combined.js?v=219"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/search.js?v=107"></script>
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
                                <a href="<%= new_job_page.toString() %>" class="active">New Job</a>
                            <% } else { %>
                                <a href="<%= new_job_page.toString() %>">New Job</a>
                            <% } %>
                        </li>
                        <li>
                            <% if(request.getParameter("nextJSP") != null 
                                && request.getParameter("nextJSP").equals("project_display") 
                                && request.getParameter("page_type") != null
                                && request.getParameter("page_type").equals("in_process")) { %>
                                <a href="<%= in_process_page.toString() %>" class="active">In Process</a>
                            <% } else { %>
                                <a href="<%= in_process_page.toString() %>">In Process</a>
                            <% } %>
                        </li>
                        <li>
                            <% if(request.getParameter("nextJSP") != null 
                                && request.getParameter("nextJSP").equals("project_display") 
                                && request.getParameter("page_type") != null
                                && request.getParameter("page_type").equals("outcomes")) { %>
                                <a href="<%= outcomes_page.toString() %>" class="active">Outcomes</a>
                            <% } else { %>
                                <a href="<%= outcomes_page.toString() %>">Outcomes</a>
                            <% } %>
                        </li>
                    </ul>
                    <!-- end menu -->
                </div>
                <!-- end menu wrapper -->