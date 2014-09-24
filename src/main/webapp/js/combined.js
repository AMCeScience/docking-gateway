var search_scope = '.search_form';

function new_job() {
    function libraryInit() {
        // Click the dropdown button on a library
        $('.library_drop').click(function() {
            // Get class of clicked library
            var library_class = $(this).parent().attr('class').split(' ')[0];

            log(library_class);

            // Toggle visibility of the compounds in this library
            // TODO: make use less cpu time
            $('ul.compound_list.' + library_class).toggle();

            $(this).toggleClass('arrow_down');
            $(this).toggleClass('arrow_right');
        });

        // Check/uncheck a library
        $('[name=library_check]').change(function() {
            // Get class of clicked library
            var library_class = $(this).parent().attr('class').split(' ')[0];

            // Get selection value of library checkbox
            var selected = $(this).is(':checked');

            // Toggle selection of compounds in library
            $('[name=compound_check]', 'ul.compound_list.' + library_class).prop('checked', selected);
        });
    }
    
    function ajaxContinue(project_data) {
        log(project_data);
        
        var html = buildLibraryHtml(project_data);
        
        $('.library_list').html(html.join(''));
        
        // Disable separate ligand selection for now
        //libraryInit();
        
        //init_list_search();
        
        $('.compound_search').show();
        $('.spinner').hide();
    }

    log("getLigands call for new job page");

    getLigands(ajaxContinue);
}

function new_job_submit() {
	$('.form_errors').hide();
	$('.form_errors').html('');

	var prev_errs = [];
	
    $('.new_job').validate({
    	rules: {
    		project_name: { ligandCount: true },
    		receptor_file: { extension: '.pdbqt' },
    		center_x: { number: true },
    		center_y: { number: true },
    		center_z: { number: true },
    		size_x: { number: true },
    		size_y: { number: true },
    		size_z: { number: true },
    		number_runs: { number: true },
    		exhaustiveness: { number: true },
    		energy_range: { number: true }
    	},
    	errorPlacement: function(error, element) {
    		var err_text = error.text();
    		
    		if (err_text === 'This field is required.') {
				err_text = 'Please fill out the required fields.';
			}
    		
    		// only add errors one time
    		if ($.inArray(err_text, prev_errs) === -1) {
    			$('.form_errors').append(err_text + '<br/>');
    		}
			
			prev_errs.push(err_text);
    	},
    	invalidHandler: function() {
    		log('invalid');
    		prev_errs = [];
    		$('.form_errors').show();
    	},
    	submitHandler: function(form) {
    		log('valid');
    		
    		$('#dialog-modal').dialog({
    	        closeOnEscape: false
    	    });

    	    // Get all selected compounds in a map
    	    /*var checked_compounds = $('input[name=compound_check]:checked', form).map(function() {
    	        // Put value in array
    	        var folder_name = $(this).parent().parent().attr('class').split(' ')[0];
    	        
    	        return "{\"" + folder_name.slice(4, folder_name.length) + "\":\"" + this.value + "\"}";
    	    });*/
    		
    		var checked_libraries = $('input[name=library_check]:checked', form).map(function() {
    			var folder_name = $(this).parent().attr('class').split(' ')[0];
    			
    			return '"' + folder_name.slice(4, folder_name.length) + '"';
    		});

    	    // Remove checkboxes from form, so we don't post these to the backend
    	    $('input[name=library_check]', form).remove();
    	    //$('input[name=compound_check]', form).remove();

    	    // Add values to one input field
    	    //$('input[name=compound_list]', form).val("{ \"compound_array\": [" + checked_compounds.get().join(',') + "]}");
    	    $('input[name=library_list]', form).val("{ \"library_array\": [" + checked_libraries.get().join(',') + "]}");

    	    form.submit();
    	}
	});
    
    $('.new_job').submit();
}

function outcomes() {
    // Bind search inputs to search function
    init_search(search_scope, after_search);
    
    // Trigger search
    trigger_search(search_scope);
}

function in_process() {
    // Bind search inputs to search function
    init_search(search_scope, after_search);
    
    // Trigger search
    trigger_search(search_scope);
}

function after_search(ajax_data) {
	log("in ajaxContinue");
	
    if (ajax_data.no_projects) {
        if ($('.accordion').hasClass('ui-accordion')) {
            $('.accordion').accordion('destroy');
        }
    
        $('.accordion').html(buildNoProjectsHtml());
        
        return;
    }

    $('.spinner').remove();
    
    build_project_html('.accordion', ajax_data);

	/*if (ajax_data.pages > 1) {
		build_pagination_html('.accordion', 'pagination', ajax_data);
		init_pagination(search_scope);
	}*/

    // Init the accordion        
    if ($('.accordion').hasClass('ui-accordion')) {
        $('.accordion').accordion('refresh');
    } else {
        $('.accordion').accordion({
        	collapsible: true,
        	heightStyle: "content",
        	active: false,
        	activate: function(e, ui) {
        		var data = $(ui.newPanel[0]).data('project');
        		
        		log(data);
        		
        		if (data && !data.loaded) {
        			log('loading');
        			
            		data.loaded = true;
            		
            		$(ui.newPanel[0]).data('project', data);
            		
            		var update_status = false;
            		
            		// Disabled for now
            		/*if (data.overall_status.indexOf("In Progress") > -1
						|| data.overall_status.indexOf("In Preparation") > -1
						|| data.overall_status.indexOf("On Hold") > -1) {
            			update_status = true;
            		}*/
            		
            		// Draw the panel html
            		update_panel(data.project_id, data.processing_id, update_status);
        		}
        	}
        });
    }
}