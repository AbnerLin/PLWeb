<%
helper = request.get('helper')
%>
<h2>恭喜！註冊已經完成</h2>
<p class="static-text">感謝您耐心完成 PLWeb 註冊程序，您是我們的第 ${helper.attr('usercount')} 位會員。</p>
<p class="static-text">如果您是<b>教師</b>，請於身份審核通過後(系統將以E-Mail通知)，利用「<b>課程管理</b>」功能開設新的線上課程，並將「<b>課程代碼</b>」通知學生選修。更多說明，請參考「<a href="http://help.plweb.org/howto:create_a_new_class" class="embedded-link">教師開課程序</a>」指南。</p>
<p class="static-text">如果您是<b>學生</b>，請向授課教師或助教取得「<b>課程代碼</b>」，並完成選修設定。更多說明，請參考「<a href="http://help.plweb.org/howto:join_a_new_class" class="embedded-link">課程選修程序</a>」指南。</p>
<p class="static-text">歡迎 [<a href="${response.encodeURL('/login/index.groovy')}"><b>立即登入</b></a>] 程式設計練習系統。</p>

