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
        
        libraryInit();
        
        initListSearch();
    }

    log("getLigands call for new job page");

    getLigands(ajaxContinue);
}

function new_job_submit() {
    // TODO: Basic validation

    $('#dialog-modal').dialog({
        closeOnEscape: false
    });

    // Get all selected compounds in a map
    var checked_compounds = $('input[name=compound_check]:checked', '.new_job').map(function() {
        // Put value in array
        var folder_name = $(this).parent().parent().attr('class').split(' ')[0];
        
        return "{\"" + folder_name.slice(4, folder_name.length) + "\":\"" + this.value + "\"}";
    });

    // Remove checkboxes from form, so we don't post these to the backend
    $('input[name=library_check]').remove();
    $('input[name=compound_check]').remove();

    // Add values to one input field
    $('input[name=compound_list]').val("{ \"compound_array\": [" + checked_compounds.get().join(',') + "]}");

    $('.new_job').submit();
}

function outcomes() {
    function init_slider() {
        var slider_start = 100;

        // Init slider
        $('.download_slider').slider({
            min: 0,
            max: 100,
            value: slider_start,
            slide: function(event, ui) {
                var scope = $(this).parent();

                // Update input field upon slider change
                $('.download_input', scope).val(ui.value);

                // Update selected number of compounds
                var max_compounds = parseInt($('input[name=max_compounds]', scope).val());

                $('.compound_disp', scope).text(Math.round((max_compounds / 100) * ui.value));
            }
        });

        // Handle slider upon input
        $('.download_input').change(function() {
            var scope = $(this).parent();

            $('.download_slider', $(this).parent()).slider('value', this.value);

            // Update selected number of compounds
            var max_compounds = parseInt($('input[name=max_compounds]', scope).val());

            $('.compound_disp', scope).text(Math.round((max_compounds / 100) * this.value));
        });
        
        init_project_buttons();
    }
    
    function ajaxContinue(ajax_data) {
        if (ajax_data.no_projects) {
            if ($('.accordion').hasClass('ui-accordion')) {
                $('.accordion').accordion('destroy');
            }
        
            $('.accordion').html('<span class="no_projects">No Projects</span>');
            
            return;
        }

        $('.spinner').remove();
        
        buildProjectHtml('.accordion', ajax_data);

        // Init the accordion        
        if ($('.accordion').hasClass('ui-accordion')) {
            $('.accordion').accordion('refresh');
        } else {
            $('.accordion').accordion();
        }

        // Init the slider
        init_slider();
    }
    
    // Bind search inputs to search function
    init_search(ajaxContinue);
    
    var ajax_data = {
        status: "done",
        date_started: "descending",
        page_type: "outcomes"
    };

    log("getProjectsAjax call for outcomes page with data:");
    log(ajax_data);

    doSearch(ajax_data, ajaxContinue);
}

function in_process() {
    function ajaxContinue(ajax_data) {
        if (ajax_data.no_projects) {
            if ($('.accordion').hasClass('ui-accordion')) {
                $('.accordion').accordion('destroy');
            }
        
            $('.accordion').html('<span class="no_projects">No Projects</span>');
            
            return;
        }
        
        $('.spinner').remove();
        
        buildProjectHtml('.accordion', ajax_data);
        
        // Init the accordion        
        if ($('.accordion').hasClass('ui-accordion')) {
            $('.accordion').accordion('refresh');
        } else {
            $('.accordion').accordion({
                heightStyle: "content"
//                activate: function( event, ui ) { update_this_project(ui.newPanel) }
            });
        }
        
        init_project_buttons();
    }
    
    // Bind search inputs to search function
    init_search(ajaxContinue);

    var ajax_data = {
        status: "in progress,in preparation,on hold,failed",
        date_started: "descending",
        page_type: "in_process"
    };
    
    doSearch(ajax_data, ajaxContinue);
}

function provenance() {

}

function buildLibraryHtml(ajax_data) {
    log("buildLibraryHtml called");
    
    log(ajax_data);
    
    var html = [];
    
    $.each(ajax_data, function(key, files) {
        var folder_name = key;
        
        html.push("<li class='lib_" + folder_name + " library'>\
                    <span class='library_drop arrow_right'></span>\
                    <input name='library_check' type='checkbox' value='1'/>\
                    " + folder_name + "<br/>\
                    <ul class='lib_" + folder_name + " compound_list'>");
        
        /*html.push("<li class='compounds compound'>");
        
        html.push(files.join("</li><li class='compounds compound'>"));
        
        html.push("</li>");*/
        
        $.each(files, function(key, file_name) {
            html.push("<li class='compounds compound_" + file_name + "'>\
                        <input name='compound_check' type='checkbox' value='" + file_name + "'/>" + file_name + "\
                       </li>");
        });
        
        html.push("</ul></li>");
    });
    
    return html;
}

function buildProjectHtml(div, ajax_data) {
    log("buildProjectHtml called");

    log(ajax_data);

    var page_type = ajax_data.page_type;

    $.each(ajax_data.projects, function(key, project) {
        $(div).append(projectHtml(project, page_type));
        
        $('.project_data_div', div).last().data('project', project);
        
        if (ajax_data.page_type === 'outcomes') {
            graph('.graph_' + project.project_id, project.output.graph);
        }
    });

    return;
}

function projectHtml(project_data, page_type) {
    /*
     * 1: {"project_id":"1234",
     * "project_name":"my project",
     * "description":"This is a random text!",
     * "user":"Allard van Altena",
     * "date_started":"2014-02-26 12:55:08.0",
     * "latest_status":"1 On Hold",
     * "inputs":
     *      {"1":
     *          {"name":"receptor_file.pdbqt",
     *          "scan_id":null,
     *          "subject":null,
     *          "type":null,
     *          "format":".pdbqt"},
     *       "2":
     *          {"name":"config_file.txt",
     *          "scan_id":null,
     *          "subject":null,
     *          "type":null,
     *          "format":".txt"},
     *       "3":
     *          {"name":"ligands_zip.zip",
     *          "scan_id":"1234",
     *          "subject":null,
     *          "type":null,
     *          "format":".txt"},
     *      },
     * "outputs":
     *      {"1":
     *          {"name":"receptor_file.pdbqt",
     *          "scan_id":null,
     *          "subject":null,
     *          "type":null,
     *          "format":".pdbqt"}
     *      },
     *"provenance_count":0}}
     */
    
    var count = 0;
    
    var project_html =
            "<h3>" + project_data.project_name + " - Started: " + project_data.date_started + "</h3>\
            <div class='project_data_div' data-project=''>\
                <div class='accordion_content'>\
                    <span class='icon'>X</span>\
                    <div class='project_content'>\
                        <h2>Description</h2>\
                        <span>" + project_data.description + "</span>\
                        <div class='input_wrapper'>\
                            <h2>Input</h2>";

                            $.each(project_data.inputs, function(key, input) {
                                var class_name = "input_details";
                                
                                if (count === 0) {
                                    class_name = "input_details first"
                                }
                                
                                project_html += "<div class='" + class_name + "'>\
                                    <span>Name: " + input.name + "</span>";

                                if (input.scan_id !== null) {
                                    project_html += "<span>Ligand count: " + input.scan_id + "</span>";
                                }

                                project_html += "<span>Format: " + input.format + "</span>\
                                    </div>";
                                
                                count++;
                            });
                        
                        project_html += "</div><div class='clear'></div>";

                        // TODO: hook up buttons
                        if (page_type === "in_process") {
                            project_html +=
                            "<div class='in_process_items_wrapper' data-project-id='" + project_data.project_id + "'>\
                                <h2>Status</h2>\
                                <span>" + project_data.latest_status + "</span>\
                                \
                                <input class='button update' type='button' value='Update'/>\
                                <input class='button' type='button' value='View'/>\
                                <input class='button' type='button' value='Restart'/>\
                                <input class='button' type='button' value='Abort'/>\
                            </div>";
                        }

                        if (page_type === "outcomes") {
                            project_html +=
                            "<div class='outcomes_items_wrapper'>\
                                <h2>Output</h2>\
                                <div class='graph graph_" + project_data.project_id + "'></div>\
                                \
                                <span class='bold'>Select % of data to download</span>\
                                <div class='download_slider'></div>\
                                \
                                <input class='download_input' name='download_input' type='text' value='100'/>\
                                <label class='download_input' for='download_input'>%</label>\
                                \
                                <span>Selected number of compounds: <span class='compound_disp'>" + project_data.output.compound_count + "</span></span>\
                                \
                                <input name='max_compounds' type='hidden' value='" + project_data.output.compound_count + "'/>\
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
                
                project_html +=
                "</div>\
            </div>\
        </div>";

    return project_html;
}