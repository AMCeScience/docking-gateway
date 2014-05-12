function init_project_buttons() {
    $('.update').click(function() {
        function ajaxContinue(response) {
            return;
        }

//        var project_id = $(this).parent().parent().data('project-id');

        var project_id = 1;

        var ajax_data = {
            project_id: project_id
        }

        updateStatus(ajax_data, ajaxContinue);
    });
    
    $('.download').click(function() {
        var data = $(this).parent().parent().parent().parent().data('project');
        
        function ajaxContinue(response) {
            window.location = "http://localhost:9090/webdav/" + data.project_name + "/pdbqt.tar.gz";
        }
        
        var ajax_data = {
            project_id: data.project_id,
            compound_count: $('input[name=max_compounds]', $(this).parent()).val()
        }
        
        
    });
}

function update_this_project(ob) {
    var project_id = $(ob).data('project-id');
    
    log.log(project_id);
}