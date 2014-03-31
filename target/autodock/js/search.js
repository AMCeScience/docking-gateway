function init_search(ajaxContinue) {
    var scope = $('.search_form');
    var search_input = $(scope).find(':input');

    scope.submit(function(e) {
        e.preventDefault();
    });

    $.each(search_input, function() {
        $(this).change(function() {
            var search_terms = $('input[name=search_terms]', scope).val();
            var date_started = $('select[name=date_started] option:selected', scope).val();
            var project_name = $('select[name=project_name] option:selected', scope).val();
            var status = "all";
            if ($('select[name=status]', scope).length > 0) {
                status = $('select[name=status] option:selected', scope).val();
            }
            
            var ajax_data = {
                search_terms: search_terms,
                date_started: date_started,
                project_name: project_name,
                status: status,
                page_type: page_type
            };
            
            doSearch(ajax_data, ajaxContinue);
        });
    });
}

function initListSearch() {
    log("start quicksearch");
    
    $('.compound_search').show();
    $('.spinner').hide();
    
    $("#list_search").quicksearch("ul.library_list li", {
        noResults: "div.noresults",
        loader: "span.loading"
    });
}