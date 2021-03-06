<jsp:include page="/jsp/template/header.jsp" />

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<portlet:actionURL name="submitJobForm" var="submit_job_url"></portlet:actionURL>

<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery/jquery.quicksearch.js"></script>

<script type="text/javascript">
    $(function() {
        new_job();
        
        get_applications();
    });
</script>

<% if(!(request.getParameter("form_errors") == null || request.getParameter("form_errors").equals(""))) { %>
    <div class="error">
        <span><%= request.getParameter("form_errors") %></span>
    </div>
<% } %>

<div class="form_errors error hidden"></div>

<form class="new_job" action="<%= submit_job_url.toString() %>" method="POST" enctype="multipart/form-data">
    <div class="top">
        <label for="project_name">Project Name</label>
        <input name="project_name" type="text" required/>

        <label for="project_description">Project Description</label>
        <textarea name="project_description" required></textarea>

        <label for="receptor_file">Receptor File</label>
        <input name="receptor_file" type="file" required/>
    </div>

    <div class="middle">
        <div class="third_col first">
            <h3>Center</h3>
            <label for="center_x">X</label>
            <input name="center_x" type="text" required/>

            <label for="center_y">Y</label>
            <input name="center_y" type="text" required/>

            <label for="center_z">Z</label>
            <input name="center_z" type="text" required/>
        </div>

        <div class="third_col">
            <h3>Size</h3>
            <label for="size_x">X</label>
            <input name="size_x" type="text" required/>

            <label for="size_y">Y</label>
            <input name="size_y" type="text" required/>

            <label for="size_z">Z</label>
            <input name="size_z" type="text" required/>
        </div>

        <div class="third_col">
            <h3>&nbsp;</h3>
            <label for="number_runs">Number of Modes</label>
            <input name="number_runs" type="text" value="9"/>

            <label for="exhaustiveness">Exhaustiveness</label>
            <input name="exhaustiveness" type="text" value="8"/>

            <!--label for="energy_range">Energy Range</label-->
            <!--input name="energy_range" type="text"/-->
        </div>
        
        <div class="clear"></div>
    </div>

    <div class="bottom">
        <div class="compound_search">
        	<h3>Compounds</h3>
            <ul class="library_list"></ul>
            <div class="clear"></div>
        </div>
        
        <span class="spinner"></span>

        <!--input name="compound_list" type="hidden"/-->
        <input name="library_list" type="hidden"/>
    </div>
    
    <div class="submit_row">
		<input name="run_pilot" type="checkbox" value="1" checked="checked" title="Runs a virtual screening with 15 ligands, for configuration sanity check"/>
		<label for="run_pilot" title="Runs a virtual screening with 15 ligands, for configuration sanity check">Run pilot job</label>
	
		<br/>
	
		<label for="application_select">Target Infrastructure</label>
		<select name="application_select" class="application_dropdown"></select>
		
		<input class="submit" type="button" value="Submit" onclick="new_job_submit();" />
	</div>
</form>
    
<!-- <div id="dialog-modal" class="hidden">
    <span class="spinner"></span>
</div> -->

<jsp:include page="/jsp/template/footer.jsp" />