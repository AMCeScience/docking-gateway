function init_project_buttons() {
	$('.details').click(function() {
		function ajax_continue(response) {
			$('.dialog-message').html('<p class="dialog-error-msg"></p><p class="dialog-error-description"></p>')
        	$('.dialog-error-msg', '.dialog-message').text(response.message);
			$('.dialog-error-description', '.dialog-message').text(response.description);
        	
        	$('.dialog-div').dialog({
        		modal: true,
        		width: 500,
        		buttons: {
        			Ok: function() {
        				$(this).dialog('close');
        			}
        		}
        	});
        }
		
		var project_data = $(this).closest('.project_data_div').data('project');
		
		var ajax_data = {
			project_id: project_data.project_id,
			processing_id: project_data.processing_id,
			submission_id: $(this).data('submission_id')
		}
		
		get_details(ajax_data, ajax_continue);
	});
	
	$('.update').click(function() {
		function ajaxContinue(response) {
			// Update display line of status
			$('.project_status_disp', '.' + data_div + response.project_id + response.processing_id).text(response.new_status);
			
		    return;
		}
		
		var project_data = $(this).closest('.project_data_div').data('project');
		
		var ajax_data = {
			project_id: project_data.project_id,
		    processing_id: project_data.processing_id
		};
		
		$('.project_status_disp', $(this).parent()).text('Updating...');
		
		updateStatus(ajax_data, ajaxContinue);
	});
    
    $('.download').click(function() {
        var data = $(this).closest('.project_data_div').data('project');
        
        function ajaxContinue(response) {
            window.location = response.redirect;
        }
        
        var ajax_data = {
            project_id: data.project_id,
            project_name: data.project_name,
            compound_count: $('input[name=max_compounds]', $(this).parent()).val()
        };
        
        downloadOutput(ajax_data, ajaxContinue);
    });
    
    $('.partial-result').click(function() {
    	function ajaxContinue(response) {
    		alert("Hello World!");
    	}
    	
    	var project_data = $(this).closest('.project_data_div').data('project');
    	
    	var ajax_data = {
			project_id: project_data.project_id,
			processing_id: project_data.processing_id
    	}
    	
    	getPartialResult(ajax_data, ajaxContinue);
    });
}

function init_slider(scope) {
	// Check if elements exist
    if ($('.download_slider', scope).length > 0 && $('.download_input', scope).length > 0) {
    	var slider_start = 100;
    	
	    // Init slider
	    $('.download_slider', scope).slider({
	        min: 0,
	        max: 100,
	        value: slider_start,
	        slide: function(event, ui) {
	            // Update input field upon slider change
	            $('.download_input', scope).val(ui.value);
	
	            // Update selected number of compounds
	            var max_compounds = parseInt($('input[name=max_compounds]', scope).val());
	
	            $('.compound_disp', scope).text(Math.round((max_compounds / 100) * ui.value));
	        }
	    });
	
	    // Handle slider upon input
	    $('.download_input', scope).change(function() {
	        $('.download_slider', scope).slider('value', this.value);
	
	        // Update selected number of compounds
	        var max_compounds = parseInt($('input[name=max_compounds]', scope).val());
	
	        $('.compound_disp', scope).text(Math.round((max_compounds / 100) * this.value));
	    });
    }
}