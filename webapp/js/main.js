'use strict';

(function ($) {

    /* ------------------
       Preloader
    -------------------- */
    $(window).on('load', function () {
        $(".loader").fadeOut();
        $("#preloder").delay(200).fadeOut("slow");
    });

    /* ------------------
       Background Image
    -------------------- */
	$('.set-bg').each(function () {
	    var bg = $(this).data('setbg');
	    $(this).css('background-image', 'url(' + bg + ')');
	});

})(jQuery);

$(function () {

  

	/* ======================
	   정렬 기준 UI
	====================== */

	/* $(function () {

	    // 초기 상태 강제 닫기
	    $(".sort-list").hide();

	    $("#sortToggle").on("click", function (e) {
	        e.stopPropagation();
	        $(".sort-list").toggle();
	    });

	    $(".sort-list").on("click", function (e) {
	        e.stopPropagation();
	    });

	    $(".sort-list li").on("click", function () {
	        $("#sortText").text($(this).text());
	        $(".sort-list").hide();
	    });

	}); */


    /* ======================
       바깥 클릭 시 닫기
    ====================== */

	$(document).on("click", function (e) {


	    // 정렬 영역
	    if (!$(e.target).closest(".board-sort-box").length) {
	        $(".sort-list").hide();
	    }

	});

});

/* ===== Scroll To Top ===== */
$(window).on("scroll", function () {
    if ($(this).scrollTop() > 300) {
        $("#scrollTopBtn").fadeIn();
    } else {
        $("#scrollTopBtn").fadeOut();
    }
});

$("#scrollTopBtn").on("click", function () {
    $("html, body").animate({ scrollTop: 0 }, 600);
});


/* ======================
   Profile Image Preview
====================== */

$(function () {
    const input = document.getElementById("profileInput");
    const preview = document.getElementById("profilePreview");

    if (!input || !preview) return;

    input.addEventListener("change", function () {
        const file = this.files[0];
        if (!file) return;

        if (!file.type.startsWith("image/")) {
            alert("이미지 파일만 선택할 수 있습니다.");
            input.value = "";
            return;
        }

        const reader = new FileReader();
        reader.onload = function (e) {
            preview.src = e.target.result;
        };
        reader.readAsDataURL(file);
    });
});

