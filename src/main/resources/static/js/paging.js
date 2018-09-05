Paging = function (totalCnt, dataSize, pageSize, pageNo) {

    totalCnt = parseInt(totalCnt);
    dataSize = parseInt(dataSize);
    pageSize = parseInt(pageSize);
    pageNo = parseInt(pageNo);

    var html = new Array();

    if (totalCnt == 0) {

        html.push('<ul class="pagination justify-content-center">');
        html.push('<li class="page-item disabled"><a class="page-link" href="#">');
        html.push('◀');
        html.push('</a></li>');
        html.push('<li class="page-item">');
        html.push('<a class="page-link" href="#">1</a>');
        html.push('</li>');
        html.push('<li class="page-item disabled"><a class="page-link" href="#">');
        html.push('▶');
        html.push('</a></li>');
        html.push('</ul>');

        return html.join("");
    }

    var pageCnt = totalCnt % dataSize;

    if (pageCnt == 0) {
        pageCnt = parseInt(totalCnt / dataSize);
    } else {
        pageCnt = parseInt(totalCnt / dataSize) + 1;
    }

    var pRCnt = parseInt(pageNo / pageSize);

    if (pageNo % pageSize == 0) {
        pRCnt = parseInt(pageNo / pageSize) - 1;
    }

    html.push('<ul class="pagination justify-content-center">');

    if (pageNo > pageSize) {

        var s2;

        if (pageNo % pageSize == 0) {
            s2 = pageNo - pageSize;
        } else {
            s2 = pageNo - pageNo % pageSize;
        }

        html.push('<li class="page-item"><a class="page-link" href=javascript:viewTasks(');
        html.push(s2);
        html.push(');>');
        html.push('◀');
        html.push("</a></li>");
    } else {
        html.push('<li class="page-item disabled"><a class="page-link" href="#">');
        html.push('◀');
        html.push('</a></li>');
    }

    for (var index = pRCnt * pageSize + 1; index < (pRCnt + 1) * pageSize + 1; index++) {

        if (index == pageNo) {
            html.push('<li class="page-item"><a class="page-link" href="#">');
            html.push(index + '<span class="sr-only">(current)</span>');
            html.push('</a></li>');
        } else {
            html.push('<li class="page-item"><a class="page-link" href=javascript:viewTasks(');
            html.push(index);
            html.push(');>');
            html.push(index);
            html.push('</a></li>');
        }

        if (index == pageCnt) {
            break;
        }
    }

    if (pageCnt > (pRCnt + 1) * pageSize) {
        html.push('<li class="page-item"><a class="page-link" href=javascript:viewTasks(');
        html.push((pRCnt + 1) * pageSize + 1);
        html.push(');>');
        html.push('▶');
        html.push('</a></li>');
    } else {
        html.push('<li class="page-item disabled"><a class="page-link" href="#">');
        html.push('▶');
        html.push('</a></li>');
    }

    html.push('</ul>');

    return html.join("");
}