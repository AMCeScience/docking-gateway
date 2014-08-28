function init_search(scope_in, ajax_continue) {
	log('init called');
	
    var scope = $(scope_in);
    var search_input = $(scope).find(':input');
    
    $(scope).data("page", 1);
    $(scope).data("last_search", {});
    $(scope).data("ajax_function", ajax_continue);

    scope.submit(function(e) {
        e.preventDefault();
    });

    // Reset search when escape is pressed
    $('input[name=search_terms]', scope).keydown(function(e) {
    	// Escape is pressed
		if (e.which === 27) {
			reset_search(scope);
		}
    });
    
    // Bind search fields to trigger search
    $.each(search_input, function() {
        $(this).change(function() {
        	trigger_search(scope);
        });
    });
    
    // Bind reset search button
    $('.reset_search').click(function() {
		reset_search(scope);
    });
    
    $(scope).on('changeData', function() {
    	log('trigger called');
    	
		trigger_search(scope);
	});
}

function reset_search(scope) {
	log('reset_search called');
	
	$('input[name=search_terms]', scope).val("");
    $('select[name=date_started]', scope).val("descending");
    $('select[name=status]', scope).val("all");
    $(scope).data("page", 1);
    $(scope).data("last_search", {});
    
    // Trigger search
    trigger_search(scope);
}

function trigger_search(scope) {
	log('trigger_search called');
	
	$('.running_projects').html("<div class='accordion'><span class='spinner'></span></div>");
	$('.pagination').remove();
	
	var last_search = $(scope).data("last_search");
	var page = $(scope).data("page");
    
    var search_terms = $('input[name=search_terms]', scope).val();
    var date_started = $('select[name=date_started] option:selected', scope).val();
    var project_name = $('select[name=project_name] option:selected', scope).val();
    var status = 'all';
    if ($('select[name=status]', scope).length > 0) {
        status = $('select[name=status] option:selected', scope).val();
    }
    
    if (status == 'all') {
    	log('in');
    	
    	status = 'in preparation, in progress, on hold';
    } 
    
    if (page_type == 'outcomes') {
    	status = 'done, aborted, failed';
    }
    
    log(status);
    
    var this_search = {
        search_terms: search_terms,
        date_started: date_started,
        project_name: project_name,
        status: status,
        page: page
    };
    
    // If any of the search fields changed set page to 1
    if (last_search.search_terms != this_search.search_terms
		|| last_search.date_started != this_search.date_started
		|| last_search.status != this_search.status) {
    	this_search.page = 1;
    }
    
    $(scope).data('last_search', this_search);
    
    do_search(this_search, $(scope).data("ajax_function"));
}

function init_list_search() {
    log("start quicksearch");
    
    $("#list_search").quicksearch("ul.library_list li", {
        noResults: "div.noresults",
        loader: "span.loading"
    });
}