<#import "./common.ftlh" as c>

<@c.page>
    <#list posts?reverse as post>
        <@c.post post "short"/>
    </#list>
</@c.page>