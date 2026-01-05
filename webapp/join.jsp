<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>AniMale | 회원가입</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<link rel="icon" type="image/png"
   href="<%=request.getContextPath()%>/favicon.png">

<link
   href="https://fonts.googleapis.com/css2?family=Oswald:wght@300;400;500;600;700&display=swap"
   rel="stylesheet">
<link
   href="https://fonts.googleapis.com/css2?family=Mulish:wght@300;400;500;600;700;800;900&display=swap"
   rel="stylesheet">

<link rel="stylesheet"
   href="<%=request.getContextPath()%>/css/bootstrap.min.css">
<link rel="stylesheet"
   href="<%=request.getContextPath()%>/css/font-awesome.min.css">
<link rel="stylesheet"
   href="<%=request.getContextPath()%>/css/style.css">

<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>

<style>
.reset-wrap {
   width: 420px;
   margin: 0 auto;
   background: #fff;
   border-radius: 20px;
   padding: 36px 34px;
   box-shadow: 0 12px 30px rgba(0, 0, 0, 0.2);
}

.row-inline {
   display: flex;
   gap: 10px;
   align-items: center;
   margin-bottom: 10px;
}

.row-inline input {
   flex: 1;
   height: 46px;
   border: 2px solid #222;
   border-radius: 999px;
   padding: 0 18px;
   outline: none;
   background: #eaf1ff;
}

.row-inline input:disabled {
   background: #f0f0f0;
   color: #888;
   border-color: #bbb;
}

.btn-outline {
   height: 46px;
   border-radius: 999px;
   padding: 0 18px;
   border: 2px solid #ff2c2c;
   color: #ff2c2c;
   background: #fff;
   font-weight: 700;
   cursor: pointer;
   white-space: nowrap;
}

.btn-outline:disabled {
   border-color: #bbb;
   color: #bbb;
   cursor: not-allowed;
}

.msg {
   font-size: 13px;
   margin: 4px 0 12px;
}

.msg.error {
   color: #ff2c2c;
}

.msg.success {
   color: #2aa84a;
}

#emailAuthArea .row-inline input {
   background: #fff;
   border-color: #bbb;
}

#emailAuthTimer {
   font-weight: 800;
   margin-top: 6px;
   font-size: 13px;
   color: #333;
}

#resendWaitMsg {
   font-size: 12px;
   color: #777;
   margin-top: 6px;
   display: none;
}

#pwArea {
   display: none;
}

#joinBtn {
   width: 100%;
   height: 50px;
   border-radius: 999px;
   border: none;
   background: #bbb;
   color: #fff;
   font-weight: 800;
   letter-spacing: 1px;
   cursor: not-allowed;
   margin-top: 10px;
}

#joinBtn.active {
   background: #444;
   cursor: pointer;
}

.back-login-btn {
   width: 100%;
   margin-top: 14px;
   padding: 12px 0;
   border-radius: 999px;
   border: 1px solid #ddd;
   background: #fff;
   color: #555;
   font-size: 14px;
   cursor: pointer;
}

.back-login-btn:hover {
   background: #f7f7f7;
}

/* 로딩 오버레이 */
#loadingOverlay {
   display: none;
   position: fixed;
   left: 0;
   top: 0;
   width: 100%;
   height: 100%;
   background: rgba(0, 0, 0, 0.45);
   z-index: 9999;
}

#loadingBox {
   position: absolute;
   left: 50%;
   top: 50%;
   transform: translate(-50%, -50%);
   background: #fff;
   padding: 16px 18px;
   border-radius: 12px;
   font-weight: 700;
}
</style>
</head>

<body>

   <%@ include file="/common/header.jsp"%>

   <section class="spad">
      <div class="container">

         <h3 style="text-align: center; color: #fff; margin-bottom: 24px;">회원가입</h3>

         <div class="reset-wrap">
            <form id="joinForm" action="<%=request.getContextPath()%>/join.do"
               method="post">

               <!-- 아이디 -->
               <div class="row-inline" id="rowMember">
                  <input type="text" id="memberName" name="memberName"
                     placeholder="아이디">
                  <button type="button" id="memberNameCheckBtn" class="btn-outline">중복확인</button>
               </div>
               <div id="memberNameMsg" class="msg"></div>

               <!-- 닉네임 -->
               <div class="row-inline" id="rowNickname">
                  <input type="text" id="memberNickname" name="memberNickname"
                     placeholder="닉네임">
                  <button type="button" id="memberNicknameCheckBtn"
                     class="btn-outline">중복확인</button>
               </div>
               <div id="memberNicknameMsg" class="msg"></div>

               <!-- 이메일 -->
               <div class="row-inline" id="rowEmail">
                  <input type="email" id="memberEmail" name="memberEmail"
                     placeholder="이메일">
                  <button type="button" id="emailCheckBtn" class="btn-outline">중복확인</button>
               </div>
               <div id="memberEmailMsg" class="msg"></div>

               <!-- 인증번호 발송 (중복확인 후 노출) -->
               <div class="row-inline" id="emailSendArea" style="display: none;">
                  <button type="button" id="sendEmailAuthBtn" class="btn-outline"
                     style="width: 100%;">인증번호 발송</button>
               </div>

               <!-- 이메일 인증 영역 (발송 성공 후 노출) -->
               <div id="emailAuthArea" style="display: none; margin-top: 10px;">
                  <div class="row-inline">
                     <input type="text" id="emailAuthCode" placeholder="인증번호 입력"
                        disabled>
                     <button type="button" id="verifyEmailAuthBtn" class="btn-outline"
                        disabled>인증번호 확인</button>
                  </div>

                  <button type="button" id="resendEmailAuthBtn" class="btn-outline"
                     style="width: 110px;" disabled>재요청</button>

                  <div id="emailAuthTimer"></div>
                  <div id="resendWaitMsg"></div>
                  <div id="emailAuthMsg" class="msg"></div>
               </div>

               <!-- 비밀번호 (이메일 인증 완료 후 노출) -->
               <div id="pwArea">

                  <div class="row-inline">
                     <input type="password" id="memberPassword" name="memberPassword"
                        placeholder="비밀번호">
                  </div>
                  <div id="memberPasswordMsg" class="msg"></div>

                  <div class="row-inline">
                     <input type="password" id="memberPasswordConfirm"
                        placeholder="비밀번호 확인">
                  </div>
                  <div id="memberPasswordConfirmMsg" class="msg"></div>

               </div>


               <button type="button" id="joinBtn" disabled>회원가입</button>

               <button type="button" class="back-login-btn"
                  onclick="location.href='<%=request.getContextPath()%>/loginPage.do'">
                  &lt; 로그인 화면으로 이동</button>

            </form>
         </div>
      </div>
   </section>

   <!-- 로딩 -->
   <div id="loadingOverlay">
      <div id="loadingBox">
         <span id="loadingText">처리 중...</span>
      </div>
   </div>

   <%@ include file="/common/footer.jsp"%>

   <script>
$(function () {

   const ctx = "<%=request.getContextPath()%>";

         /* 상태 변수 */
         let memberNameChecked = false;
         let memberNicknameChecked = false;
         let memberPasswordValid = false;

         let emailChecked = false;
         let emailAuthVerified = false;

         let emailAuthTimer = null;
         let emailAuthRemain = 0;

         // 재요청 정책:
         // - 최초 발송 후 5초는 무조건 잠금 (안내 문구)
         // - 재요청을 1번이라도 하면 유효시간 (3분) 끝날 때까지 잠금
         let firstRequestLockUntil = 0; // 최초 발송 후 5초 잠금 종료 시각
         let hasResentOnce = false;

         /* 정규식 */
         const memberNameRegex = /^[a-zA-Z0-9]{4,12}$/;
         const memberNicknameRegex = /^[a-zA-Z0-9가-힣]{2,8}$/;
         const memberEmailRegex = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;
         const memberPasswordRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[!@#$%^&*()_+=-]).{8,16}$/;

         /* 공통 함수 */
         function setMsg($el, msg, type) {
            $el.text(msg).removeClass("error success");
            if (type === "error")
               $el.addClass("error");
            if (type === "success")
               $el.addClass("success");
         }

         function showLoading(text) {
            $("#loadingText").text(text || "처리 중...");
            $("#loadingOverlay").show();
         }

         function hideLoading() {
            $("#loadingOverlay").hide();
         }

         function toggleJoinBtn() {
            const ok = memberNameChecked && memberNicknameChecked
                  && memberPasswordValid && emailAuthVerified;
            $("#joinBtn").prop("disabled", !ok).toggleClass("active", ok);
         }

         function clearEmailTimer() {
            if (emailAuthTimer) {
               clearInterval(emailAuthTimer);
               emailAuthTimer = null;
            }
            emailAuthRemain = 0;
            $("#emailAuthTimer").text("");
         }

         function updateTimerText() {
            const min = String(Math.floor(emailAuthRemain / 60)).padStart(
                  2, '0');
            const sec = String(emailAuthRemain % 60).padStart(2, '0');
            $("#emailAuthTimer").text(min + ":" + sec);
         }

         function updateResendInfoText() {
            // 1) 재요청 한 번이라도 했으면 -> 3분 끝날 때까지 고정
            if (hasResentOnce && emailAuthRemain > 0 && !emailAuthVerified) {
               $("#resendWaitMsg").text("재요청은 인증 유효시간(3분)이 끝난 후에 가능합니다.")
                     .show();
               return;
            }

            // 2) 최초 발송 직후 5초 잠금
            if (!hasResentOnce && !emailAuthVerified
                  && Date.now() < firstRequestLockUntil) {
               $("#resendWaitMsg").text("재요청은 5초 후 가능합니다.").show();
               return;
            }

            // 3) 기본 안내
            if (!emailAuthVerified) {
               $("#resendWaitMsg").text("재요청은 인증 유효시간(3분)이 끝난 후에 가능합니다.")
                     .show();
            } else {
               $("#resendWaitMsg").hide();
            }
         }

         function resetEmailAuthUI() {
            emailChecked = false;
            emailAuthVerified = false;
            hasResentOnce = false;
            firstRequestLockUntil = 0;

            clearEmailTimer();

            $("#emailSendArea").hide();
            $("#emailAuthArea").hide();

            $("#sendEmailAuthBtn").prop("disabled", true).removeClass(
                  "done").text("인증번호 발송");
            $("#emailAuthCode").prop("disabled", true).val("");
            $("#verifyEmailAuthBtn").prop("disabled", true).text("인증번호 확인");
            $("#resendEmailAuthBtn").prop("disabled", true)
                  .addClass("wait").text("재요청");

            $("#resendWaitMsg").hide();
            setMsg($("#emailAuthMsg"), "", "");
            setMsg($("#memberEmailMsg"), "", "");

            $("#pwArea").hide();
            memberPasswordValid = false;
            setMsg($("#memberPasswordMsg"), "", "");
            setMsg($("#memberPasswordConfirmMsg"), "", "");

            toggleJoinBtn();
         }

         function expireEmailAuth() {
            emailAuthVerified = false;
            setMsg($("#emailAuthMsg"), "인증번호가 만료되었습니다. 재요청 후 다시 입력해주세요.",
                  "error");
            $("#emailAuthTimer").text("만료됨");

            // 만료되면 재요청 가능
            $("#resendEmailAuthBtn").prop("disabled", false).removeClass(
                  "wait").text("재요청");
            $("#verifyEmailAuthBtn").prop("disabled", false)
                  .text("인증번호 확인");
            $("#emailAuthCode").prop("disabled", false);

            $("#resendWaitMsg").text("인증번호가 만료되었습니다. 재요청 해주세요.").show();

            toggleJoinBtn();
         }

         function startEmailTimer(seconds) {
            clearEmailTimer();

            emailAuthRemain = seconds || 180;
            updateTimerText();

            emailAuthTimer = setInterval(function() {
               emailAuthRemain--;
               updateTimerText();

               if (emailAuthRemain <= 0) {
                  clearEmailTimer();
                  expireEmailAuth();
               }

            }, 1000);
         }

         /* 초기 UI */
         resetEmailAuthUI(); // 초기화 한 번

         /* 아이디 중복확인 */
         $("#memberNameCheckBtn").on(
               "click",
               function() {
                  const memberName = $("#memberName").val().trim();

                  if (!memberNameRegex.test(memberName)) {
                     setMsg($("#memberNameMsg"), "4~12자 (영문/숫자)",
                           "error");
                     memberNameChecked = false;
                     toggleJoinBtn();
                     return;
                  }

                  $.ajax({
                     url : ctx + "/MemberNameCheck",
                     type : "POST",
                     dataType : "json",
                     data : {
                        memberName : memberName
                     },
                     success : function(res) {
                        if (!res || res.success === false) {
                           memberNameChecked = false;
                           setMsg($("#memberNameMsg"),
                                 (res && res.message) ? res.message
                                       : "중복확인 중 오류가 발생했습니다.",
                                 "error");
                           toggleJoinBtn();
                           return;
                        }

                        if (res.available) {
                           memberNameChecked = true;
                           setMsg($("#memberNameMsg"),
                                 "사용 가능한 아이디입니다.", "success");
                        } else {
                           memberNameChecked = false;
                           setMsg($("#memberNameMsg"),
                                 "이미 사용 중인 아이디입니다.", "error");
                        }
                        toggleJoinBtn();
                     },
                     error : function() {
                        memberNameChecked = false;
                        setMsg($("#memberNameMsg"),
                              "중복확인 중 오류가 발생했습니다.", "error");
                        toggleJoinBtn();
                     }
                  });
               });

         /* 닉네임 중복확인 */
         $("#memberNicknameCheckBtn").on(
               "click",
               function() {
                  const memberNickname = $("#memberNickname").val()
                        .trim();

                  if (!memberNicknameRegex.test(memberNickname)) {
                     setMsg($("#memberNicknameMsg"), "2~8자 (한글/영문/숫자)",
                           "error");
                     memberNicknameChecked = false;
                     toggleJoinBtn();
                     return;
                  }

                  $.ajax({
                     url : ctx + "/MemberNickNameCheck",
                     type : "POST",
                     dataType : "json",
                     data : {
                        memberNickname : memberNickname
                     },
                     success : function(res) {
                        if (!res || res.success === false) {
                           memberNicknameChecked = false;
                           setMsg($("#memberNicknameMsg"),
                                 (res && res.message) ? res.message
                                       : "중복확인 중 오류가 발생했습니다.",
                                 "error");
                           toggleJoinBtn();
                           return;
                        }

                        if (res.available) {
                           memberNicknameChecked = true;
                           setMsg($("#memberNicknameMsg"),
                                 "사용 가능한 닉네임입니다.", "success");
                        } else {
                           memberNicknameChecked = false;
                           setMsg($("#memberNicknameMsg"),
                                 "이미 사용 중인 닉네임입니다.", "error");
                        }
                        toggleJoinBtn();
                     },
                     error : function() {
                        memberNicknameChecked = false;
                        setMsg($("#memberNicknameMsg"),
                              "중복확인 중 오류가 발생했습니다.", "error");
                        toggleJoinBtn();
                     }
                  });
               });

         /* 이메일 입력 변경 시: 인증 무효 */
         $("#memberEmail").on("input", function() {
            // 이메일을 바꾸면 다시 처음부터
            resetEmailAuthUI();

            const value = $(this).val().trim();
            if (value === "") {
               setMsg($("#memberEmailMsg"), "", "");
            } else if (!memberEmailRegex.test(value)) {
               setMsg($("#memberEmailMsg"), "올바른 이메일 형식이 아닙니다.", "error");
            } else {
               setMsg($("#memberEmailMsg"), "이메일 중복확인을 진행해주세요.", "error");
            }
            toggleJoinBtn();
         });

         /* 이메일 중복확인 */
         $("#emailCheckBtn").on(
               "click",
               function() {
                  const email = $("#memberEmail").val().trim();

                  if (!memberEmailRegex.test(email)) {
                     setMsg($("#memberEmailMsg"), "올바른 이메일 형식이 아닙니다.",
                           "error");
                     return;
                  }

                  showLoading("이메일 중복확인 중...");

                  $.ajax({
                     url : ctx + "/MemberEmailCheck",
                     type : "POST",
                     dataType : "json",
                     data : {
                        memberEmail : email
                     },
                     success : function(res) {
                        if (!res || res.success === false) {
                           emailChecked = false;
                           setMsg($("#memberEmailMsg"),
                                 (res && res.message) ? res.message
                                       : "이메일 중복확인 중 오류가 발생했습니다.",
                                 "error");
                           $("#emailSendArea").hide();
                           return;
                        }

                        if (res.available) {
                           emailChecked = true;
                           setMsg($("#memberEmailMsg"), res.message,
                                 "success");

                           // 중복확인 OK -> 발송 버튼 영역 노출 + 활성화
                           $("#emailSendArea").show();
                           $("#sendEmailAuthBtn").prop("disabled",
                                 false).removeClass("done").text(
                                 "인증번호 발송");

                           // 인증영역은 발송 성공 후에만 열림
                           $("#emailAuthArea").hide();
                        } else {
                           emailChecked = false;
                           setMsg($("#memberEmailMsg"), res.message,
                                 "error");
                           $("#emailSendArea").hide();
                           $("#emailAuthArea").hide();
                        }
                     },
                     error : function() {
                        emailChecked = false;
                        setMsg($("#memberEmailMsg"),
                              "이메일 중복검사 중 오류가 발생했습니다.", "error");
                        $("#emailSendArea").hide();
                        $("#emailAuthArea").hide();
                     },
                     complete : hideLoading
                  });
               });

         /* 인증번호 발송 */
         $("#sendEmailAuthBtn")
               .on(
                     "click",
                     function(e) {
                        e.preventDefault();
                        e.stopPropagation();

                        const email = $("#memberEmail").val().trim();

                        if (!emailChecked) {
                           setMsg($("#memberEmailMsg"),
                                 "이메일 중복확인을 먼저 진행해주세요.", "error");
                           return;
                        }
                        if (!memberEmailRegex.test(email)) {
                           setMsg($("#memberEmailMsg"),
                                 "올바른 이메일 형식이 아닙니다.", "error");
                           return;
                        }

                        showLoading("인증번호 발송 중...");

                        // 발송 중 UI
                        $("#sendEmailAuthBtn").prop("disabled", true)
                              .removeClass("done").text("발송 중...");
                        $("#emailAuthArea").show();
                        $("#emailAuthCode").prop("disabled", true).val(
                              "");
                        $("#verifyEmailAuthBtn").prop("disabled", true)
                              .text("인증번호 확인");
                        $("#resendEmailAuthBtn").prop("disabled", true)
                              .addClass("wait").text("재요청");
                        $("#resendWaitMsg").hide();
                        setMsg($("#emailAuthMsg"), "", "");

                        $
                              .ajax({
                                 url : ctx + "/FindPasswordSendCode",
                                 type : "POST",
                                 dataType : "json",
                                 data : {
                                    purpose : "JOIN",
                                    memberEmail : email
                                 },
                                 success : function(res) {
                                    if (res && res.success) {
                                       // 발송 완료 UI
                                       $("#sendEmailAuthBtn")
                                             .prop("disabled",
                                                   true)
                                             .addClass("done")
                                             .text("발송 완료");

                                       // 입력/확인 버튼 활성화
                                       $("#emailAuthCode").prop(
                                             "disabled", false)
                                             .val("").focus();
                                       $("#verifyEmailAuthBtn")
                                             .prop("disabled",
                                                   false)
                                             .text("인증번호 확인");

                                       // 최초 발송 후 5초 잠금
                                       firstRequestLockUntil = Date
                                             .now() + 5000;
                                       hasResentOnce = false;

                                       // 타이머 시작
                                       const expireSeconds = (res.expireSeconds) ? res.expireSeconds
                                             : 180;
                                       startEmailTimer(expireSeconds);

                                       // 재요청 버튼은 최초 5초 동안 비활성화 이후 활성화
                                       $("#resendEmailAuthBtn")
                                             .prop("disabled",
                                                   true)
                                             .addClass("wait")
                                             .text("재요청");
                                       updateResendInfoText();

                                       setMsg(
                                             $("#emailAuthMsg"),
                                             "인증번호가 발송되었습니다. 이메일을 확인해주세요.",
                                             "success");

                                       // 5초 후에만 재요청 버튼 활성화 (재요청을 한 번이라도 하면 다시 잠김 정책으로 전환)
                                       setTimeout(
                                             function() {
                                                if (!emailAuthVerified
                                                      && !hasResentOnce
                                                      && emailAuthRemain > 0) {
                                                   $(
                                                         "#resendEmailAuthBtn")
                                                         .prop(
                                                               "disabled",
                                                               false)
                                                         .removeClass(
                                                               "wait")
                                                         .text(
                                                               "재요청");
                                                }
                                                updateResendInfoText();
                                             }, 5000);

                                    } else {
                                       $("#sendEmailAuthBtn")
                                             .prop("disabled",
                                                   false)
                                             .removeClass("done")
                                             .text("인증번호 발송");
                                       setMsg(
                                             $("#emailAuthMsg"),
                                             (res && res.message) ? res.message
                                                   : "인증번호 발송에 실패했습니다.",
                                             "error");
                                    }
                                 },
                                 error : function() {
                                    $("#sendEmailAuthBtn").prop(
                                          "disabled", false)
                                          .removeClass("done")
                                          .text("인증번호 발송");
                                    setMsg(
                                          $("#emailAuthMsg"),
                                          "인증번호 발송 중 오류가 발생했습니다.",
                                          "error");
                                 },
                                 complete : hideLoading
                              });
                     });

         /* 인증번호 재요청 */
         $("#resendEmailAuthBtn")
               .on(
                     "click",
                     function() {

                        if (emailAuthVerified)
                           return;

                        // 최초 5초 잠금
                        if (!hasResentOnce
                              && Date.now() < firstRequestLockUntil) {
                           updateResendInfoText();
                           return;
                        }

                        // 재요청을 이미 했다면 유효시간 끝날 때까지 차단
                        if (hasResentOnce && emailAuthRemain > 0) {
                           updateResendInfoText();
                           return;
                        }

                        const email = $("#memberEmail").val().trim();
                        if (!memberEmailRegex.test(email)) {
                           setMsg($("#memberEmailMsg"),
                                 "올바른 이메일 형식이 아닙니다.", "error");
                           return;
                        }

                        hasResentOnce = true;
                        updateResendInfoText();

                        showLoading("인증번호 재발송 중...");

                        $("#resendEmailAuthBtn").prop("disabled", true)
                              .addClass("wait").text("재요청 중...");
                        $("#verifyEmailAuthBtn").prop("disabled", true)
                              .text("인증번호 확인");
                        $("#emailAuthCode").prop("disabled", true);

                        $
                              .ajax({
                                 url : ctx + "/FindPasswordSendCode",
                                 type : "POST",
                                 dataType : "json",
                                 data : {
                                    purpose : "JOIN",
                                    memberEmail : email
                                 },
                                 success : function(res) {
                                    if (res && res.success) {
                                       setMsg(
                                             $("#emailAuthMsg"),
                                             "인증번호를 재발송했습니다. 새 인증번호를 입력해주세요.",
                                             "success");

                                       $("#emailAuthCode").prop(
                                             "disabled", false)
                                             .val("").focus();
                                       $("#verifyEmailAuthBtn")
                                             .prop("disabled",
                                                   false)
                                             .text("인증번호 확인");

                                       const expireSeconds = (res.expireSeconds) ? res.expireSeconds
                                             : 180;
                                       startEmailTimer(expireSeconds);

                                       // 재요청 이후에는 3분 끝날 때까지 비활성화 유지
                                       $("#resendEmailAuthBtn")
                                             .prop("disabled",
                                                   true)
                                             .addClass("wait")
                                             .text("재요청");
                                       updateResendInfoText();

                                    } else {
                                       hasResentOnce = false; // 실패면 정책 복구
                                       $("#resendEmailAuthBtn")
                                             .prop("disabled",
                                                   false)
                                             .removeClass("wait")
                                             .text("재요청");
                                       $("#verifyEmailAuthBtn")
                                             .prop("disabled",
                                                   false)
                                             .text("인증번호 확인");
                                       $("#emailAuthCode").prop(
                                             "disabled", false);

                                       setMsg(
                                             $("#emailAuthMsg"),
                                             (res && res.message) ? res.message
                                                   : "재요청에 실패했습니다.",
                                             "error");
                                       updateResendInfoText();
                                    }
                                 },
                                 error : function() {
                                    hasResentOnce = false;
                                    $("#resendEmailAuthBtn").prop(
                                          "disabled", false)
                                          .removeClass("wait")
                                          .text("재요청");
                                    $("#verifyEmailAuthBtn").prop(
                                          "disabled", false)
                                          .text("인증번호 확인");
                                    $("#emailAuthCode").prop(
                                          "disabled", false);

                                    setMsg(
                                          $("#emailAuthMsg"),
                                          "인증번호 재발송 중 오류가 발생했습니다.",
                                          "error");
                                    updateResendInfoText();
                                 },
                                 complete : hideLoading
                              });
                     });

         /* 인증번호 확인 */
         $("#verifyEmailAuthBtn")
               .on(
                     "click",
                     function() {

                        const code = $("#emailAuthCode").val().trim();
                        if (!code) {
                           setMsg($("#emailAuthMsg"), "인증번호를 입력해주세요.",
                                 "error");
                           return;
                        }

                        showLoading("인증번호 확인 중...");

                        $("#verifyEmailAuthBtn").prop("disabled", true)
                              .text("확인 중...");

                        $
                              .ajax({
                                 url : ctx
                                       + "/FindPasswordVerifyCode",
                                 type : "POST",
                                 dataType : "json",
                                 data : {
                                    purpose : "JOIN",
                                    code : code
                                 },
                                 success : function(res) {
                                    if (res && res.success) {
                                       emailAuthVerified = true;

                                       setMsg(
                                             $("#memberEmailMsg"),
                                             "이메일 인증이 완료되었습니다.",
                                             "success");
                                       setMsg($("#emailAuthMsg"),
                                             "이메일 인증이 완료되었습니다.",
                                             "success");

                                       clearEmailTimer();

                                       $("#emailAuthCode").prop(
                                             "disabled", true);
                                       $("#verifyEmailAuthBtn")
                                             .prop("disabled",
                                                   true).text(
                                                   "인증 완료");
                                       $("#resendEmailAuthBtn")
                                             .prop("disabled",
                                                   true)
                                             .addClass("wait")
                                             .text("재요청");
                                       $("#sendEmailAuthBtn")
                                             .prop("disabled",
                                                   true)
                                             .addClass("done")
                                             .text("발송 완료");
                                       $("#resendWaitMsg").hide();

                                       $("#pwArea").slideDown();

                                    } else {
                                       $("#verifyEmailAuthBtn")
                                             .prop("disabled",
                                                   false)
                                             .text("인증번호 확인");
                                       setMsg(
                                             $("#emailAuthMsg"),
                                             (res && res.message) ? res.message
                                                   : "인증번호가 올바르지 않습니다.",
                                             "error");
                                    }
                                    toggleJoinBtn();
                                 },
                                 error : function() {
                                    $("#verifyEmailAuthBtn").prop(
                                          "disabled", false)
                                          .text("인증번호 확인");
                                    setMsg(
                                          $("#emailAuthMsg"),
                                          "인증번호 확인 중 오류가 발생했습니다.",
                                          "error");
                                    toggleJoinBtn();
                                 },
                                 complete : hideLoading
                              });
                     });

         /* 비밀번호 유효성 */
         function validatePassword() {
            const pw = $("#memberPassword").val().trim();
            const pw2 = $("#memberPasswordConfirm").val().trim();

            if (!memberPasswordRegex.test(pw)) {
               memberPasswordValid = false;
               setMsg($("#memberPasswordMsg"), "8~16자 / 영문+숫자+특수문자 포함",
                     "error");
               setMsg($("#memberPasswordConfirmMsg"), "", "");
               toggleJoinBtn();
               return;
            }
            setMsg($("#memberPasswordMsg"), "사용 가능한 비밀번호입니다.", "success");

            if (pw2.length > 0 && pw !== pw2) {
               memberPasswordValid = false;
               setMsg($("#memberPasswordConfirmMsg"), "비밀번호가 일치하지 않습니다.",
                     "error");
               toggleJoinBtn();
               return;
            }

            if (pw2.length > 0 && pw === pw2) {
               memberPasswordValid = true;
               setMsg($("#memberPasswordConfirmMsg"), "비밀번호가 일치합니다.",
                     "success");
            } else {
               memberPasswordValid = false;
               setMsg($("#memberPasswordConfirmMsg"), "", "");
            }

            toggleJoinBtn();
         }

         $("#memberPassword").on("input", validatePassword);
         $("#memberPasswordConfirm").on("input", validatePassword);

         /* 최종 회원가입 버튼 */
         $("#joinBtn").on("click", function() {
            if ($(this).prop("disabled"))
               return;
            $("#joinForm").submit();
         });

      });
   </script>

</body>
</html>
