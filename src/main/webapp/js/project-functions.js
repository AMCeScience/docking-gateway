function init_project_buttons() {
    $('.update').click(function() {
        function ajaxContinue(response) {
        	// Update display line of status
        	$('.project_status_disp', '.project_id_' + response.project_id).text(response.new_status);
        	
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
}

function update_this_project(ob) {
    var project_id = $(ob).data('project-id');
    
    log.log(project_id);
}