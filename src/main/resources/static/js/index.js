$(function() {

    $.ajaxSetup({
        xhrFields: {
            withCredentials: true
        }
    });

    $('#login-form-link').click(function(e) {
        $("#login-form").delay(100).fadeIn(100);
        $("#register-form").fadeOut(100);
        $('#register-form-link').removeClass('active');
        $(this).addClass('active');
        e.preventDefault();
    });

    $('#register-form-link').click(function(e) {
        $("#register-form").delay(100).fadeIn(100);
        $("#login-form").fadeOut(100);
        $('#login-form-link').removeClass('active');
        $(this).addClass('active');
        e.preventDefault();
    });

});

function login() {

    var email = $.trim($("#email").val());
    var password = $.trim($("#password").val());

    var emailRegx = /^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$/i;

    if(email === ""){
        $("#email").focus();
        alert("이메일을 입력하세요.");
        return false;
    }

    if(!emailRegx.test(email)) {
        $("#email").focus();
        alert("이메일형식이 아닙니다.");
        return false;
    }

    if(password === ""){
        $("#password").focus();
        alert("패스워드를 입력하세요.");
        return false;
    }

    $.ajax({
        url: "/api/users/login",
        type: "POST",
        contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
        dataType: 'json',
        data: jQuery.param({
            email: $('#email').val(),
            password: $('#password').val(),
            isWeb: "true"
        }),
        success: function () {
            window.location.href = "/";
        },
        error: function (error) {

            if (error.responseJSON.code === 401) {
                alert("이메일이 없거나 비밀번호가 틀립니다.");
            } else {
                alert(error.responseJSON.code);
            }

            return false;
        }
    });

    return false;
}

function register() {

    var email = $('#reg_email').val();
    var password = $('#reg_password').val();
    var confirmPassword = $('#confirm-password').val();

    var emailRegx = /^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$/i;

    if(email === ''){
        $('#reg_email').focus();
        alert("이메일을 입력하세요.");
        return false;
    }

    if(!emailRegx.test(email)) {
        $('#reg_email').focus();
        alert("이메일형식이 아닙니다.");
        return false;
    }

    if(password === ''){
        $('#reg_password').focus();
        alert("패스워드를 입력하세요.");
        return false;
    }

    if(confirmPassword === '' || password !== confirmPassword){
        $('#confirm-password').focus();
        alert("패스워드 확인을 해주세요.");
        return false;
    }

    $.ajax({
        url: "/api/users",
        type: "POST",
        contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
        dataType: 'json',
        data: jQuery.param({
            email: $('#reg_email').val(),
            password: $('#reg_password').val()
        }),
        success: function () {
            window.location.href = "/";
        },
        error: function (error) {

            if (error.responseJSON.code === 4001) {
                alert("이미 존재하는 이메일 입니다.");
            } else {
                alert(error.responseJSON.code);
            }

            return false;
        }
    });

    return false;
}