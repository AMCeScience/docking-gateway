function doSearch(dataArr, successFunc) {
    log('getProjectsAjax function running');
    
    ajaxCall(dataArr, successFunc, $('body').data('ajax-urls').searchUrl, "POST", false);
}

function getLigands(successFunc) {
    log('getLigands function running');

    ajaxCall(null, successFunc, $('body').data('ajax-urls').ligandsUrl, "GET", true);
}

function updateStatus(dataArr, successFunc) {
    log('updateStatus function running');

    ajaxCall(dataArr, successFunc, $('body').data('ajax-urls').updateStatusUrl, "POST", false);
}

function downloadOutput(dataArr, successFunc) {
    log('downloadOutput function running');
    
    ajaxCall(dataArr, successFunc, $('body').data('ajax-urls').downloadOutputUrl, "POST", false);
}

function ajaxCall(dataIn, successFunc, urlIn, typeIn, cacheIn) {
    $.ajax({
        type: typeIn,
        dataType: "json",
        url: urlIn,
        cache: cacheIn,
        data: dataIn,
        success: function(data) {
            console.log("success");
            console.log("success", arguments);
            console.log("data", typeof data, data); // Verify the response

            successFunc(data);
        }
    });
}