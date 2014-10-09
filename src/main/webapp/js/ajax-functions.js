function do_search(dataArr, successFunc) {
    log('getProjectsAjax function running');
    
    ajax_call(dataArr, successFunc, $('body').data('ajax-urls').searchUrl, "POST", false);
}

function getPartialResult(dataArr, successFunc) {
    log('getPartialResult function running');
    
    ajax_call(dataArr, successFunc, $('body').data('ajax-urls').partialResultUrl, "POST", false);
}

function updateStatus(dataArr, successFunc) {
    log('updateStatus function running');

    ajax_call(dataArr, successFunc, $('body').data('ajax-urls').updateStatusUrl, "POST", false);
}

function getLigands(successFunc) {
    log('getLigands function running');

    ajax_call(null, successFunc, $('body').data('ajax-urls').ligandsUrl, "GET", true);
}

function downloadOutput(dataArr, successFunc) {
    log('downloadOutput function running');
    
    ajax_call(dataArr, successFunc, $('body').data('ajax-urls').downloadOutputUrl, "POST", false);
}

function get_details(dataArr, success_func) {
    log('getDetails function running');

    ajax_call(dataArr, success_func, $('body').data('ajax-urls').getDetailsUrl, "GET", false);
}

function ajax_call(dataIn, successFunc, urlIn, typeIn, cacheIn) {
	log('ajax data:');
	log(dataIn);
	
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