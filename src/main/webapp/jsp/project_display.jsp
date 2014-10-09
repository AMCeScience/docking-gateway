<jsp:include page="/jsp/template/header.jsp" />

<script type="text/javascript" src="${pageContext.request.contextPath}/js/output-graph.js?v=109"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery/jquery.flot.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery/jquery.flot.categories.min.js"></script>

<jsp:include page="/jsp/search.jsp" />

<script type="text/javascript">
    $(function() {
        project_display();
    });
</script>

<div class="running_projects">
    <div class="accordion">
        <span class="spinner"></span>
    </div>
</div>

<jsp:include page="/jsp/template/footer.jsp" />
<jsp:include page="/jsp/template/dialog.jsp" />