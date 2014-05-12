function graph(div, dataIn) {
    var options = {
        series: {
            points: {show: true},
            lines: {show: true}
        },
        grid: {
            hoverable: true
        },
        xaxis: {
            show: false,
            mode: "categories",
            tickLength: 0
        },
        yaxis: {
            transform: function(v) {return -v;}
        }
    };

    var formatted_data = [
        dataIn
    ];

    $.plot($(div), formatted_data, options);

    bind_tooltips(div);
}

function bind_tooltips(div) {
    var previousPoint = 0;

    $(div).bind("plothover", function(event, pos, item) {
        if (item) {
            if (previousPoint !== item.dataIndex) {
                previousPoint = item.dataIndex;

                $("#tooltip").remove();

                var x = item.datapoint[0],
                    y = item.datapoint[1];

                var tt = '<span>Beds: ' + x + '</span><br/><span>%: ' + y + '</span>';

                showTooltip(item.pageX, item.pageY, tt);
            }
        } else {
            $("#tooltip").remove();
            previousPoint = null;
        }
    });
}

function showTooltip(x, y, contents) {
    $("<div id='tooltip'>" + contents + "</div>").css({top: y + 5, left: x + 5}).appendTo("body").fadeIn(200);
}