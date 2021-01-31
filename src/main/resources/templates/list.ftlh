<#import "parts/common.ftlh" as c>
<@c.page>
    <h2>Список пользователей</h2>
    <table class="table table-striped">
        <thead>
        <tr>
            <th>E-mail</th>
            <th>Имя</th>
            <th>Роль</th>
            <th></th>
        </tr>
        </thead>
        <tbody>
        <#list users as user>
            <#if user.userId != 1>
                <tr>
                    <td>${user.email}</td>
                    <td>${user.username}</td>
                    <td><#list user.roles as role>${role}<#sep>, </#list></td>
                    <td><a href="/user/${user.userId}">изменить</a></td>
                </tr>
            </#if>
        </#list>
        </tbody>
    </table>
</@c.page>