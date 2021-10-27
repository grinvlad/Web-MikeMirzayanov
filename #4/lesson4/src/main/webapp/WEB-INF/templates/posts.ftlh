<#-- @ftlvariable name="post_id" type="java.lang.String" -->
<#-- @ftlvariable name="posts" type="ru.itmo.web.lesson4.model.Post[]" -->
<#import "./common.ftlh" as c>

<@c.page>
    <h1>${c.findBy(users, "id", user_id).handle}'s latest posts:</h1>
    <#list posts?reverse as post>
        <#if post.user_id == user_id>
            <@c.post post "full"/>
        </#if>
    </#list>
</@c.page>