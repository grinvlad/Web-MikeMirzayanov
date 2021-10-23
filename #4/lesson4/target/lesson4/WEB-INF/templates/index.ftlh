<#import "./common.ftlh" as c>

<@c.page>
    <#list posts?reverse as post>
        <article>
            <div class="title">${post.title}</div>
            <div class="information">By <@c.userlink c.findBy(users, "id", post.user_id)!/>, 2 days ago, translation</div>
            <div class="body">
            <#if post.text?length &lt; 250>
                <p class="text">${post.text}</p>
            <#else>
                <p class="text">${post.text?substring(0, 250) + "..."}</p>
            </#if>
            </div>
            <div class="footer">
                <div class="left">
                    <img src="img/voteup.png" title="Vote Up" alt="Vote Up"/>
                    <span class="positive-score">+173</span>
                    <img src="img/votedown.png" title="Vote Down" alt="Vote Down"/>
                </div>
                <div class="right">
                    <img src="img/date_16x16.png" title="Publish Time" alt="Publish Time"/>
                    2 days ago
                    <img src="img/comments_16x16.png" title="Comments" alt="Comments"/>
                    <a href="#">68</a>
                </div>
            </div>
        </article>
    </#list>










<#--    <ul>-->
<#--        <#list users as u>-->
<#--            <li>-->
<#--                <#if user?? && user.id == u.id>*</#if>-->
<#--                <@c.userlink u/>-->
<#--            </li>-->
<#--        </#list>-->
<#--    </ul>-->
</@c.page>
