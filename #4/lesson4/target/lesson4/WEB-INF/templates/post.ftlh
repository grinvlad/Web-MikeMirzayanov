<#-- @ftlvariable name="post_id" type="java.lang.String" -->
<#-- @ftlvariable name="posts" type="ru.itmo.web.lesson4.model.Post[]" -->
<#import "./common.ftlh" as c>

<@c.page>
    <#list posts?reverse as post>
        <#if post.user_id == user_id>
            <@c.post post "full"/>
        </#if>
    </#list>
</@c.page>