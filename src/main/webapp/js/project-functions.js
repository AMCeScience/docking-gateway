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
}

function update_this_project(ob) {
    var project_id = $(ob).data('project-id');
    
    log.log(project_id);
}