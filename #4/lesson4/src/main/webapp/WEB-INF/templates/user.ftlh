<#-- @ftlvariable name="handle" type="java.lang.String" -->
<#-- @ftlvariable name="user_id" type="long" -->
<#import "./common.ftlh" as c>

<@c.page>
    <#if user_id?has_content && user_id?is_number>
        <#assign u = c.findBy(users, "id", user_id)!/>
    <#elseif handle?has_content>
        <#assign u = c.findBy(users, "handle", handle)!/>
    </#if>
    <#if u?has_content>
        <div class="user-box">
            <div class="user-info">
                <@c.userlink u/>
                <ul>
                    <li>id: ${u.id}</li>
                    <li>handle: ${u.handle}</li>
                    <li>name: ${u.name}</li>
                    <li>posts: <a href="/posts?user_id=${u.id}">${c.countPosts(u.id)}</a></li>
                </ul>
            </div>
            <div class="user-photo">
                <img src="img/user-photo.png" alt=${u.handle}>
            </div>
        </div>
        <div class="arrows">
            <#if c.prevUser(u)??>
                <a href="user?handle=${c.prevUser(u).handle}" class="true-href">←</a>
            <#else>
                <span class="empty-href">←</span>
            </#if>

            <#if c.nextUser(u)??>
                <a href="user?handle=${c.nextUser(u).handle}" class="true-href">→</a>
            <#else>
                <span class="empty-href">→</span>
            </#if>
        </div>
    <#else>
        No such user
    </#if>
</@c.page>