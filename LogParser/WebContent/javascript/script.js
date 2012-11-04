// Popup window code
function newPopup(url) {
	popupWindow = window.open(url,'_blank','height=700,width=800,left=10,top=10,resizable=yes,scrollbars=yes,toolbar=yes,menubar=no,location=no,directories=no,status=yes');
	return false;
}

$(function () {
	$("table tbody tr td:not(.link)").click(function () {
		$(this).parents('tr').toggleClass("selected");
	});
	
	$("table tbody tr td.link").click(function () {
		$(this).parents('tr').addClass("selected");
	});
});

var afterReload = function () {
	window.location.hash = '#bottom';
};