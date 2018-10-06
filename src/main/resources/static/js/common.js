$(function() {

    $(document).on('click submit', '.ga', function() {
        var category = $(this).attr('data-ga-category');
        var action = $(this).attr('data-ga-action');
        var label = $(this).attr('data-ga-label');
        var value = $(this).attr('data-ga-value');
        ga('send', 'event', category, action, label, value);
    });

});

window.dataLayer = window.dataLayer || [];

function gtag() {
    dataLayer.push(arguments);
}

gtag('js', new Date());
gtag('config', 'UA-23325275-3');