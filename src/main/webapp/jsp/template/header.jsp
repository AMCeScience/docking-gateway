<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>

<% // Navigation url declarations %>
<portlet:actionURL name="goToPage" var="new_job_page">
    <portlet:param name="page" value="new_job" />
</portlet:actionURL>

<portlet:actionURL name="goToPage" var="jobs_page">
    <portlet:param name="page" value="project_display" />
</portlet:actionURL>

<% // Ajax url declarations %>
<portlet:resourceURL var="ajaxLigandsUrl" id="LigandCollector"></portlet:resourceURL>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">

<html>
    <head>
        <!-- load CSS files -->
        <link href="${pageContext.request.contextPath}/css/styles.css?v=180" rel="stylesheet" type="text/css"/>
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
                "ligandsUrl": "<%= ajaxLigandsUrl %>"
            });
        </script>
        
        <!-- load javascript function files -->
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/ajax-functions.js?v=164"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/combined.js?v=234"></script>
    </head>
    <body>
        <div id="portlet-wrapper">
            <div id="content">