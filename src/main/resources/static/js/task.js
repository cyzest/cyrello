$(function() {

    viewTasks(1);

    $('#task-body').on("click", "tr", function () {
        if ($(this).is('[role=row]')) {
            viewTaskPopUp($(this).children(":first").text());
        }
    });

});

function viewTaskPopUp(id) {

    $('#uptTaskId').val('');
    $('#uptContent').val('');
    $('#uptRelationTaskIds').val('');
    $('.task-update-btn').removeAttr("disabled");

    var getTask = $.ajax({
        url: "/api/tasks/" + id,
        type: "GET",
        contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
        dataType: 'json'
    });

    getTask.done(function(data) {

        $('#uptTaskId').val(data.extra.taskInfo.id);
        $('#uptContent').val(data.extra.taskInfo.content);

        var relationTasks = data.extra.taskInfo.relationTasks;

        var relString = "";

        if (relationTasks != null) {
            for (var i=0; i < relationTasks.length; i++) {
                relString += relationTasks[i].id;
                if (i < relationTasks.length - 1) {
                    relString += ",";
                }
            }
        }

        $('#uptRelationTaskIds').val(relString);

        if (data.extra.taskInfo.completeDate != null) {
            $('.task-update-btn').attr("disabled","disabled");
        }

        $('#updatePopup').modal('toggle');

    });

}

function viewTasks(page) {

    var getTask = $.ajax({
        url: "/api/tasks?page="+page+"&size=5",
        type: "GET",
        contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
        dataType: 'json'
    });

    getTask.done(refreshTable);

}

function registerTask() {

    var content = $.trim($('#content').val());
    var relationTaskIds = $.trim($('#relationTaskIds').val());

    if (content === '') {
        alert("할일을 입력하세요.");
        return false;
    }

    if (content.length > 100) {
        alert("할일은 100자 이내로 작성해주세요.");
        return false;
    }

    var splitTaskIds = [];

    if (relationTaskIds !== '') {

        var numRegx = /^([1-9])([0-9])*$/;

        splitTaskIds = relationTaskIds.split(",");

        for(var i=0 ; i < splitTaskIds.length ; i++){
            if (!numRegx.test(splitTaskIds[i])) {
                alert("참조 할 할일 작성규칙을 확인해보세요.");
                return false;
            }
        }

        if (splitTaskIds.length > 10) {
            alert("참조 할 할일은 10개 이내로 참조 가능합니다.");
            return false;
        }
    }

    $.ajax({
        url: "/api/tasks",
        type: "POST",
        contentType: 'application/json; charset=UTF-8',
        dataType: 'json',
        data: JSON.stringify({
            content: content,
            relationTaskIds:splitTaskIds
        }),
        success: function () {
            viewTasks(1);
            $('#content').val('');
            $('#relationTaskIds').val('');
        },
        error: function (error) {

            var errorCode = error.responseJSON.code;

            if (errorCode === 4102) {
                alert("유효하지 않은 할일 ID를 참조 할 수 없습니다.");
                $('#relationTaskIds').val('');
            } else if (errorCode === 4103) {
                alert("완료처리 된 할일을 참조 할 수 없습니다.");
                $('#relationTaskIds').val('');
            } else if (errorCode === 400) {
                alert("참조 할 할일은 ID와 쉼표로 만 작성해주세요.");
                $('#relationTaskIds').val('');
            } else {
                alert(error.responseJSON.code);
            }

            return false;
        }
    });

    return false;
}

function updateTask() {

    var taskId = $('#uptTaskId').val();
    var content = $.trim($('#uptContent').val());
    var relationTaskIds = $.trim($('#uptRelationTaskIds').val());

    if (content === '') {
        alert("할일을 입력하세요.");
        return false;
    }

    if (content.length > 100) {
        alert("할일은 100자 이내로 작성해주세요.");
        return false;
    }

    var splitTaskIds = [];

    if (relationTaskIds !== '') {

        var numRegx = /^([1-9])([0-9])*$/;

        splitTaskIds = relationTaskIds.split(",");

        for(var i=0 ; i < splitTaskIds.length ; i++){
            if (!numRegx.test(splitTaskIds[i])) {
                alert("참조 할 할일 작성규칙을 확인해보세요.");
                return false;
            }
        }

        if (splitTaskIds.length > 10) {
            alert("참조 할 할일은 10개 이내로 참조 가능합니다.");
            return false;
        }
    }

    $.ajax({
        url: "/api/tasks/" + taskId,
        type: "PUT",
        contentType: 'application/json; charset=UTF-8',
        dataType: 'json',
        data: JSON.stringify({
            content: content,
            relationTaskIds: splitTaskIds
        }),
        success: function () {
            $('#updatePopup').modal('toggle');
            viewTasks(1);
        },
        error: function (error) {

            var errorCode = error.responseJSON.code;

            if (errorCode === 4102) {
                alert("유효하지 않은 할일 ID를 참조 할 수 없습니다.");
                $('#relationTaskIds').val('');
            } else if (errorCode === 4103) {
                alert("완료처리 된 할일을 참조 할 수 없습니다.");
                $('#relationTaskIds').val('');
            } else if (errorCode === 4104) {
                alert("참조 할 할일이 이미 해당 할일을 참조하고 있어 참조 할 수 없습니다.");
                $('#relationTaskIds').val('');
            } else if (errorCode === 4106) {
                alert("완료처리된 할일은 수정할 수 없습니다.");
            } else if (errorCode === 4107) {
                alert("자기 자신을 참조 할 수 없습니다.");
            } else if (errorCode === 400) {
                alert("참조 할 할일은 ID와 쉼표로 만 작성해주세요.");
                $('#relationTaskIds').val('');
            } else {
                alert(error.responseJSON.code);
            }

            return false;
        }
    });

    return false;
}

function completeTask() {

    var taskId = $('#uptTaskId').val();

    if (!confirm("완료한 할일은 수정할 수 없습니다. 수정하시겠습니까?")) {
        return false;
    }

    $.ajax({
        url: "/api/tasks/" + taskId + "/complete",
        type: "POST",
        contentType: 'application/json; charset=UTF-8',
        dataType: 'json',
        success: function () {
            $('#updatePopup').modal('toggle');
            viewTasks(1);
        },
        error: function (error) {

            var errorCode = error.responseJSON.code;

            if (errorCode === 4105) {
                alert("해당 할일을 참조하는 완료되지 않은 할일이 존재합니다.");
            }  else {
                alert(error.responseJSON.code);
            }

            return false;
        }
    });

    return false;
}

function logout() {

    $.ajax({
        url: "/api/users/logout",
        type: "POST",
        success: function () {
            window.location.href = "/";
        }
    });

}

function refreshTable(data) {

    $('#task-list').DataTable({
        searching: false,
        ordering: false,
        paging: false,
        destroy: true,
        info: false,
        lengthChange: false,
        data: data.extra.taskInfos,
        columns: [
            { data: 'id', width: '10%'},
            { data: 'content', width: '30%'},
            {
                "data": "relationTasks",
                "width": "20%",
                "render": function (relationTasks) {
                    var relString = "";
                    if (relationTasks != null) {
                        for (var i=0; i < relationTasks.length; i++) {
                            relString += "@" + relationTasks[i].id + " ";
                        }
                    }
                    return relString;
                }
            },
            { data: 'registerDateToFormatString', width: '15%'},
            { data: 'updateDateToFormatString', width: '15%'},
            {
                "data": "completeDate",
                "width": "10%",
                "render": function (completeDate) {
                    return completeDate != null ? '완료' : '미완료';
                }
            }
        ]
    });

    var pageView = Paging(data.extra.totalCount, data.extra.paging.size, 10 , data.extra.paging.page);

    $("#paging").empty().html(pageView);

}