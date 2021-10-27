<#-- @ftlvariable name="post_id" type="java.lang.String" -->
<#-- @ftlvariable name="posts" type="ru.itmo.web.lesson4.model.Post[]" -->
<#import "./common.ftlh" as c>

<@c.page>
    <#assign post = c.findBy(posts, "id", post_id)!/>
    <#if post?has_content>
        <@c.post post "full"/>
    <#else>
        No such post
    </#if>
</@c.page>