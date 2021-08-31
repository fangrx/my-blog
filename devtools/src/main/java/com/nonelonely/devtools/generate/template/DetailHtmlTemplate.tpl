<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head th:replace="/common/template :: header(~{::title},~{::link},~{::style})">
</head>
<body>
    <div class="dolphin-detail-page">
        <div class="dolphin-detail-title">基本信息</div>
        <table class="layui-table dolphin-detail-table">
            <colgroup>
                <col width="100px"><col>
                <col width="100px"><col>
            </colgroup>
            <tbody jsoup="detail"></tbody>
        </table>
    </div>
<script th:replace="/common/template :: script"></script>
</body>
</html>
