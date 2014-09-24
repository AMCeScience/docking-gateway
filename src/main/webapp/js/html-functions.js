var header_div = "header_div_id_";
var data_div = "data_div_id_";

function update_panel(project_id, processing_id, update_status) {
	log("update panel called");
	
	function draw_panel(response) {
		var project = response.projects[0];
		var scope = '.' + data_div + project.project_id + project.processing_id;
		
		var html = build_inside_project_html(project);
		var header = $('.' + header_div + project.project_id + project.processing_id);
		
		$(header).removeClass("red");
		$(header).removeClass("yellow");
		$(header).removeClass("light-blue");
		$(header).removeClass("green");
		
		$(header).addClass(get_header_color(project.overall_status));
		$('.second', header).text(project.overall_status);
		
		$('.project_content', '.' + data_div + project.project_id + project.processing_id).html(html);

		if ($('.graph', '.' + data_div + project.project_id + project.processing_id).length > 0 && project.output !== undefined && project.output.graph !== undefined) {
            graph('.graph_' + project.project_id, project.output.graph);
        }
		
		// Init slider
		init_slider(scope);
		
        // Init the project buttons
        init_project_buttons();
	}
	
	var ajax_data = {
		"project_id": project_id,
		"processing_id": processing_id
	}
	
	if (update_status !== undefined && update_status === true) {
		//TODO: Disabled for now until processing manager can handle status updates simultaneously
		//do_search_and_update(ajax_data, draw_panel);
		do_search(ajax_data, draw_panel);
	} else {
		do_search(ajax_data, draw_panel);
	}
}

function build_project_html(div, ajax_data) {
    log("buildProjectHtml called");

    log(ajax_data);

    $.each(ajax_data.projects, function(key, project) {
        $(div).append(project_html(project));
        
        $('.' + data_div + project.project_id + project.processing_id, div).data('project', project);
    });

    return;
}

function check_status(status, checkStatus) {
	if (status.toLowerCase().indexOf(checkStatus) > -1) {
		return true;
	}
	
	return false;
}

function get_header_color(status) {
	if (check_status(status, "on hold")) {
		return "red";
	}
	
	if (check_status(status, "aborted")) {
		return "red";
	}
	
	if (check_status(status, "in preparation")) {
		return "yellow";	
	}
	
	if (check_status(status, "in progress")) {
		return "light-blue";
	}
	
	if (check_status(status, "done")) {
		return "green";
	}
	
	return "red";
}

function project_html(project_data) {
	var header_class = get_header_color(project_data.overall_status);
	
    return html =
        "<h3 class='header " + header_class + " " + header_div + project_data.project_id + project_data.processing_id + "'>\
        	<span class='header-cols first'>" + project_data.date_started + "</span>\
    		| <span class='header-cols second'>" + project_data.overall_status + "</span>\
			| <span class='header-cols third'>" + project_data.project_name + "</span>\
		</h3>\
        <div class='project_data_div " + data_div + project_data.project_id + project_data.processing_id + "' data-project=''>\
            <div class='accordion_content'>\
                <div class='project_content'>\
            		<div class='spinner'></div>\
        		</div>\
            </div>\
        </div>";
}

function build_inside_project_html(project_data) {
	log(project_data);
	var count = 0;
	
	var project_html = "<h2>Description</h2>\
    <span>" + project_data.description + "</span>\
    <div class='input_wrapper'>\
        <h2>Input</h2>";

        $.each(project_data.submissions[0].submissionIO, function(key, value) {
        	var data_element = value.data_element;
            var class_name = "input_details";
            
            // Skip output elements
            if (value.type === 'Output') {
            	return true;
            }
            
            if (count === 0) {
                class_name = "input_details first";
            }
            
            project_html += "<div class='" + class_name + "'>\
                <a href='/webdav/" + project_data.project_name.replace("_pilot", "") + "/" + data_element.name + "'>Name: " + data_element.name + "</a>";

            if (data_element.ligand_count !== null) {
                project_html += "<span>Ligand count: " + data_element.ligand_count + "</span>";
            }

            project_html += "<span>Format: " + data_element.format + "</span>\
                </div>";
            
            count++;
        });
    
    project_html += "</div><div class='clear'></div>";

    if (page_type === "in_process") {
        project_html +=
        "<div class='in_process_items_wrapper'>\
            <h2>Status</h2>\
            <span class='project_status_disp'>" + project_data.submissions[0].status + "</span>";
            
        	if (project_data.overall_status.indexOf("In Progress") > -1
				|| project_data.overall_status.indexOf("In Preparation") > -1
				|| project_data.overall_status.indexOf("On Hold") > -1
				|| project_data.overall_status.indexOf("Resuming") > -1) {
				project_html += "<input class='button update' type='button' value='Update'/>";
			}
        	
        project_html += "</div>";
    }
    
    if (page_type === "outcomes"
    	&& (project_data.overall_status.indexOf("Aborted") > -1
		|| project_data.overall_status.indexOf("Failed") > -1)) {
		project_html += "<input class='button details' data-submission_id='" + project_data.submissions[0].submission_id + "' type='button' value='Details'/>";
	}

    if (page_type === "outcomes" && project_data.overall_status.indexOf("Done") > -1) {
        project_html +=
        "<div class='outcomes_items_wrapper'>\
            <h2>Output</h2>\
            <div class='graph graph_" + project_data.project_id + "'></div>\
            \
            <span class='bold' style='display:none;'>Select % of data to download</span>\
            <div class='download_slider' style='display:none;'></div>\
            \
            <input class='download_input' name='download_input' type='text' style='display:none;' value='100'/>\
            <label class='download_input' style='display:none;' for='download_input'>%</label>\
            \
            <span>Selected number of compounds: <span class='compound_disp'>" + project_data.compound_count + "</span></span>\
            \
            <input name='max_compounds' type='hidden' value='" + project_data.compound_count + "'/>\
            \
            <input class='download button' type='button' value='Download Data'/>";

            // If no provenance exists for this project do not display it
            if (project_data.provenance_count * 1 !== 0) {
                project_html +=
                "<span>This experiment is linked to " + project_data.provenance_count + " others</span>\
                <input class='button' type='button' value='View Provenance'/>";
            }
        
        project_html += "</div>";
    }
    
    return project_html;
}

function buildNoProjectsHtml() {
	return '<span class="no_projects">No Projects</span>';
}

function buildLibraryHtml(ajax_data) {
    log("buildLibraryHtml called");
    
    log(ajax_data);
    
    var html = [];
    
    $.each(ajax_data, function(key, files) {
        var folder_name = key;
        
        /*html.push("<li class='lib_" + folder_name + " library'>\
                    <span class='library_drop arrow_right'></span>\
                    <input name='library_check' type='checkbox' value='1'/>\
                    " + folder_name + "<br/>\
                    <ul class='lib_" + folder_name + " compound_list'>");*/
        
        html.push("<li class='lib_" + folder_name + " library'>\
                <input name='library_check' type='checkbox' value='1'/>\
        		" + folder_name + "</li>");
        
        /*$.each(files, function(key, file_name) {
            html.push("<li class='compounds compound_" + file_name + "'>\
                        <input name='compound_check' type='checkbox' value='" + file_name + "'/>" + file_name + "\
                       </li>");
        });*/
        
        //html.push("</ul></li>");
    });
    
    return html;
}