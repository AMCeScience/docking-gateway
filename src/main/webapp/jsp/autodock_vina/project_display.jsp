<jsp:include page="/jsp/autodock_vina/template/header.jsp" />

<!-- Remove search for provenance pages -->
<% if (!request.getParameter("page_type").equals("provenance")) { %>
    <jsp:include page="/jsp/autodock_vina/search.jsp" />
<% } %>

<script type="text/javascript">
    $(function() {
        var page_type = "<%= request.getParameter("page_type") %>";
        
        // Initiate javascript function for the appropriate page
        <%= request.getParameter("page_type") %>();
    });
</script>

<div class="running_projects">
    <div class="accordion">
        <span class="spinner"</span>
    </div>
</div>

<jsp:include page="/jsp/autodock_vina/template/footer.jsp" />