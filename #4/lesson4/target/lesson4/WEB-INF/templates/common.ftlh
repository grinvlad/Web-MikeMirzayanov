<#-- @ftlvariable name="post" type="ru.itmo.web.lesson4.model.Post" -->

<#macro page>  
    <!DOCTYPE html>
    <html lang="ru">
    <head>
        <meta charset="UTF-8">
        <title>Codeforces</title>
        <link rel="stylesheet" href="/css/normalize.css">
        <link rel="stylesheet" href="/css/style.css">
        <link rel="icon" href="/favicon.ico">
    </head>
    <body>
    <@header/>
    <div class="middle">
        <@sidebar/>
        <main>
            <#nested/>
        </main>
    </div>
    <@footer/>
    </body>
    </html>
</#macro>

<#macro header>
    <header>
        <a href="#" class="logo"><img alt="Codeforces" title="Codeforces"
                                      src="/img/codeforces-logo-with-telegram.png"></a>
        <div class="languages">
            <a href="#"><img alt="In English" title="In English"
                             src="/img/gb.png"></a>
            <a href="#"><img alt="In Russian" title="In Russian"
                             src="/img/ru.png"></a>
        </div>
        <div class="enter-or-register-box">
            <#if user??>
                <@userlink user/>
                |
                <a href="/logout">Logout</a>
            <#else>
                <a href="/enter">Enter</a>
                |
                <a href="/register">Register</a>
            </#if>
        </div>
        <nav>
            <#assign items=[
            {"href": "/index", "name": "Home"},
            {"href": "/users", "name": "Users"},
            {"href": "/misc/help", "name": "Help"},
            {"href": "/contests", "name": "Contests"}]/>
            <ul>
                <#list items as item>
                    <li>
                        <#if uri?? && item.href == uri>
                            <a href="${item.href}" class="current-link">${item.name}</a>
                        <#else>
                            <a href="${item.href}">${item.name}</a>
                        </#if>
                    </li>
                </#list>
            </ul>
        </nav>
    </header>
</#macro>

<#macro sidebar>
    <aside>
        <#list posts?reverse as post>
            <section>
                <div class="header">Post #${post.id}</div>
                <div class="body">${postShowType(post, "short")}</div>
                <div class="footer">
                    <a href="/post?post_id=${post.id}">View all</a>
                </div>
            </section>
        </#list>
    </aside>
</#macro>

<#macro post post showType>
    <article>
        <div class="title">${post.title}</div>
        <div class="information">By <@userlink findBy(users, "id", post.getUser_id())!/>, 2 days ago, translation</div>
        <div class="body">
            <p class="text">${postShowType(post, showType)}</p>
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
</#macro>

<#macro footer>
    <footer>
        2010-2021 &copy; by Mike Mirzayanov and Vlad Grin
    </footer>
</#macro>

<#macro userlink user>
    <a href="/user?handle=${user.handle}">${user.name}</a>
</#macro>

<#function postShowType post showType>
    <#if showType=="full">
        <#return post.text>
    <#else>
        <#if post.text?length &lt; 250>
            <#return post.text>
        <#else>
            <#return post.text?substring(0, 250) + "...">
        </#if>
    </#if>
</#function>

<#function prevUser curUser>
    <#list 0..users?size-1 as i>
        <#if users[i].getId() == curUser.getId() && i != 0>
            <#return users[i - 1]/>
        </#if>
    </#list>
</#function>

<#function nextUser curUser>
    <#list 0..users?size-1 as i>
        <#if users[i].getId() == curUser.getId() && i != users?size-1>
            <#return users[i + 1]/>
        </#if>
    </#list>
</#function>

<#function findBy items key value>
    <#list items as item>
        <#if item[key] == value>
            <#return item/>
        </#if>
    </#list>
</#function>

<#function countPosts userId>
    <#assign c = 0>
    <#list posts as post>
        <#if post.user_id == userId>
            <#assign c++>
        </#if>
    </#list>
    <#return c/>
</#function>
