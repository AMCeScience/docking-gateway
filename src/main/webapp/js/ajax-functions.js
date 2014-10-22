function getLigands(successFunc) {
    log('getLigands function running');

    ajax_call(null, successFunc, $('body').data('ajax-urls').ligandsUrl, "GET", true);
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