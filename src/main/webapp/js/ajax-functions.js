function doSearch(dataArr, successFunc) {
    log('getProjectsAjax function running');

    $.ajax({
        type: "POST",
        dataType: "json",
        url: $('body').data('ajax-urls').searchUrl,
        cache: false,
        data: dataArr,
        success: function(data) {
            console.log("success");
            console.log("success", arguments);
            console.log("data", typeof data, data);

            successFunc(data);
        }
    });
}

function getLigands(successFunc) {
    log('getLigands function running');

    $.ajax({
        type: "GET",
        dataType: "json",
        url: $('body').data('ajax-urls').ligandsUrl,
        cache: false,
        success: function(data) {
            console.log("success");
            console.log("success", arguments);
            console.log("data", typeof data, data); // Verify the response

            successFunc(data);
        }
    });
}

function updateStatus(dataArr, successFunc) {
    log('updateStatus function running');

    $.ajax({
        type: "POST",
        dataType: "json",
        url: $('body').data('ajax-urls').updateStatusUrl,
        cache: false,
        data: dataArr,
        success: function(data) {
            console.log("success");
            console.log("success", arguments);
            console.log("data", typeof data, data); // Verify the response

            successFunc(data);
        }
    });
}

function downloadOutput(dataArr, successFunc) {
    log('downloadOutput function running');
    
    $.ajax({
        type: "POST",
        dataType: "json",
        url: $('body').data('ajax-urls').downloadOutputUrl,
        cache: false,
        data: dataArr,
        success: function(data) {
            console.log("success");
            console.log("success", arguments);
            console.log("data", typeof data, data); // Verify the response

            successFunc(data);
        }
    });
}