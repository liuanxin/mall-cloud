package com.zzz;

import com.github.common.util.Compressor;

public class WebNil {

    public static void main(String[] args) {
        String str = "<!doctype html>\n" +
                "<html lang=\"zh-CN\">\n" +
                "<head>\n" +
                "    <title>接口文档</title>\n" +
                "    <meta charset=\"utf-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width,initial-scale=1,maximum-scale=1\">\n" +
                "    <link rel=\"stylesheet\" href=\"//cdn.bootcss.com/bootstrap/3.3.5/css/bootstrap.min.css\">\n" +
                "    <style type=\"text/css\">\n" +
                "        html {\n" +
                "            position: relative;\n" +
                "            min-height: 100%;\n" +
                "        }\n" +
                "        body {\n" +
                "            font-size: 16px;\n" +
                "            margin-bottom: 105px;\n" +
                "        }\n" +
                "        footer {\n" +
                "            position: absolute;\n" +
                "            bottom: 0;\n" +
                "            width: 100%;\n" +
                "            height: 127px;\n" +
                "            background-color: #f5f5f5;\n" +
                "        }\n" +
                "        footer > div {\n" +
                "            padding: 10px;\n" +
                "        }\n" +
                "        footer p {\n" +
                "            margin: 0 0 5px;\n" +
                "            text-align: center;\n" +
                "            font-size: 16px;\n" +
                "        }\n" +
                "        #table-of-contents {\n" +
                "            margin-top: 20px;\n" +
                "            margin-bottom: 20px;\n" +
                "        }\n" +
                "        blockquote p {\n" +
                "            font-size: 18px;\n" +
                "        }\n" +
                "        pre {\n" +
                "            font-size: 14px;\n" +
                "            overflow: auto;\n" +
                "            margin: 0 0 1em;\n" +
                "            padding: .5em 1em;\n" +
                "        }\n" +
                "        pre .line-number {\n" +
                "            color: #2B91AF;\n" +
                "            display: block;\n" +
                "            float: left;\n" +
                "            margin: 0 1em 0 -1em;\n" +
                "            border-right: 1px solid;\n" +
                "            text-align: right;\n" +
                "        }\n" +
                "        pre .line-number span {\n" +
                "            display: block;\n" +
                "            padding: 0 .5em 0 1em;\n" +
                "        }\n" +
                "        pre .comment {\n" +
                "            color: #998;\n" +
                "            font-style: italic;\n" +
                "        }\n" +
                "        figcaption {\n" +
                "            font-size: 16px;\n" +
                "            color: #666;\n" +
                "            font-style: italic;\n" +
                "            padding-bottom: 15px;\n" +
                "        }\n" +
                "        .bs-docs-sidebar.affix {\n" +
                "            position: static;\n" +
                "        }\n" +
                "        @media (min-width: 768px) {\n" +
                "            .bs-docs-sidebar {\n" +
                "                padding-left: 20px;\n" +
                "            }\n" +
                "        }\n" +
                "        .bs-docs-sidebar > .nav {\n" +
                "            overflow-y: auto;\n" +
                "            max-height: 76vh;\n" +
                "        }\n" +
                "        .bs-docs-sidebar .nav > li > a {\n" +
                "            display: block;\n" +
                "            padding: 4px 20px;\n" +
                "            font-size: 14px;\n" +
                "            font-weight: 500;\n" +
                "            color: #999;\n" +
                "        }\n" +
                "        .bs-docs-sidebar .nav > li > a:hover, .bs-docs-sidebar .nav > li > a:focus {\n" +
                "            padding-left: 19px;\n" +
                "            color: #A1283B;\n" +
                "            text-decoration: none;\n" +
                "            background-color: transparent;\n" +
                "            border-left: 1px solid #A1283B;\n" +
                "        }\n" +
                "        .bs-docs-sidebar .nav > .active > a, .bs-docs-sidebar .nav > .active:hover > a, .bs-docs-sidebar .nav > .active:focus > a {\n" +
                "            padding-left: 18px;\n" +
                "            font-weight: bold;\n" +
                "            color: #A1283B;\n" +
                "            background-color: transparent;\n" +
                "            border-left: 2px solid #A1283B;\n" +
                "        }\n" +
                "        .bs-docs-sidebar .nav .nav {\n" +
                "            display: none;\n" +
                "            padding-bottom: 10px;\n" +
                "        }\n" +
                "        .bs-docs-sidebar .nav .nav > li > a {\n" +
                "            padding-top: 1px;\n" +
                "            padding-bottom: 1px;\n" +
                "            padding-left: 30px;\n" +
                "            font-size: 12px;\n" +
                "            font-weight: normal;\n" +
                "        }\n" +
                "        .bs-docs-sidebar .nav .nav > li > a:hover, .bs-docs-sidebar .nav .nav > li > a:focus {\n" +
                "            padding-left: 29px;\n" +
                "        }\n" +
                "        .bs-docs-sidebar .nav .nav > .active > a, .bs-docs-sidebar .nav .nav > .active:hover > a, .bs-docs-sidebar .nav .nav > .active:focus > a {\n" +
                "            padding-left: 28px;\n" +
                "            font-weight: 500;\n" +
                "        }\n" +
                "        .bs-docs-sidebar .nav .nav .nav {\n" +
                "            padding-bottom: 10px;\n" +
                "        }\n" +
                "        .bs-docs-sidebar .nav .nav .nav > li > a {\n" +
                "            padding-top: 1px;\n" +
                "            padding-bottom: 1px;\n" +
                "            padding-left: 40px;\n" +
                "            font-size: 12px;\n" +
                "            font-weight: normal;\n" +
                "        }\n" +
                "        .bs-docs-sidebar .nav .nav .nav > li > a:hover, .bs-docs-sidebar .nav .nav .nav > li > a:focus {\n" +
                "            padding-left: 39px;\n" +
                "        }\n" +
                "        .bs-docs-sidebar .nav .nav .nav > .active > a, .bs-docs-sidebar .nav .nav .nav > .active:hover > a, .bs-docs-sidebar .nav .nav .nav > .active:focus > a {\n" +
                "            padding-left: 38px;\n" +
                "            font-weight: 500;\n" +
                "        }\n" +
                "        @media (min-width: 992px) {\n" +
                "            .bs-docs-sidebar .nav > .active > ul {\n" +
                "                display: block;\n" +
                "            }\n" +
                "            .bs-docs-sidebar.affix, .bs-docs-sidebar.affix-bottom {\n" +
                "                width: 213px;\n" +
                "            }\n" +
                "            .bs-docs-sidebar.affix {\n" +
                "                position: fixed;\n" +
                "                top: 20px;\n" +
                "            }\n" +
                "            .bs-docs-sidebar.affix-bottom {\n" +
                "                position: absolute;\n" +
                "            }\n" +
                "            .bs-docs-sidebar.affix .bs-docs-sidenav, .bs-docs-sidebar.affix-bottom .bs-docs-sidenav {\n" +
                "                margin-top: 0;\n" +
                "                margin-bottom: 0\n" +
                "            }\n" +
                "        }\n" +
                "        @media (min-width: 1200px) {\n" +
                "            .bs-docs-sidebar.affix-bottom, .bs-docs-sidebar.affix {\n" +
                "                width: 263px;\n" +
                "            }\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "<div class=\"container\">\n" +
                "    <div class=\"row\">\n" +
                "        <div class=\"col-md-9\">\n" +
                "            <script id=\"url-render\" type=\"text/x-dot-template\">\n" +
                "                {{~ it.moduleList  :value:index}}\n" +
                "                <div class=\"outline-2\">\n" +
                "                    <h2 id=\"{{! value.name}}\"><span class=\"section-number-2\">{{! index + 1}}</span> {{! value.info}}</h2>\n" +
                "                    {{~ value.urlList  :url:urlIndex}}\n" +
                "                    <div class=\"outline-3\">\n" +
                "                        <h3 id=\"{{! value.name}}-{{! url.id}}\">\n" +
                "                            <span class=\"section-number-3\">{{! index + 1}}.{{! urlIndex + 1}}</span> {{! url.title}}\n" +
                "                        </h3>\n" +
                "                        <div class=\"outline-text-3\">\n" +
                "<pre class=\"example\">\n" +
                "{{? url.develop && url.develop !== ''}}开发: {{! url.develop}}{{??}}未标明开发者{{?}}<br>\n" +
                "{{? url.desc}}说明: {{! url.desc}}<br>{{?}}<br>\n" +
                "<a target=\"_blank\" href=\"{{! url.exampleUrl}}\">返回结果示例地址</a>\n" +
                "</pre>\n" +
                "                            <p> 接口地址 <code>{{! url.method}} {{! url.url}}</code></p>\n" +
                "\n" +
                "                            {{? url.paramList && url.paramList.length > 0}}\n" +
                "                            <p>参数说明如下</p>\n" +
                "                            <table class=\"table table-striped table-bordered table-hover table-condensed\">\n" +
                "                                <thead>\n" +
                "                                <tr>\n" +
                "                                    <th scope=\"col\" class=\"text-left\">名称</th>\n" +
                "                                    <th scope=\"col\" class=\"text-left\">数据类型</th>\n" +
                "                                    <th scope=\"col\" class=\"text-left\">必须</th>\n" +
                "                                    <th scope=\"col\" class=\"text-left\">说明</th>\n" +
                "                                    {{? url.hasExample}} <th scope=\"col\" class=\"text-left\">示例</th> {{?}}\n" +
                "                                    {{? url.hasHeader}} <th scope=\"col\" class=\"text-left\">参数类型</th> {{?}}\n" +
                "                                </tr>\n" +
                "                                </thead>\n" +
                "                                <tbody>\n" +
                "                                {{~ url.paramList  :param:paramIndex}}\n" +
                "                                <tr>\n" +
                "                                    {{? param.must}}\n" +
                "                                        <td class=\"text-left\"><b>{{! param.name}}</b></td>\n" +
                "                                        <td class=\"text-left\"><b>{{! param.dataType}}</b></td>\n" +
                "                                        <td class=\"text-left\"><b>Yes</b></td>\n" +
                "                                        <td class=\"text-left\"><b>{{! param.desc}}</b></td>\n" +
                "                                        {{? url.hasExample}}<td class=\"text-left\"><b>{{! param.example}}</b></td>{{?}}\n" +
                "                                        {{? url.hasHeader}}<td class=\"text-left\"><b>{{! param.paramType}}</b></td>{{?}}\n" +
                "                                    {{??}}\n" +
                "                                        <td class=\"text-left\">{{! param.name}}</td>\n" +
                "                                        <td class=\"text-left\">{{! param.dataType}}</td>\n" +
                "                                        <td class=\"text-left\">No</td>\n" +
                "                                        <td class=\"text-left\">{{! param.desc}}</td>\n" +
                "                                        {{? url.hasExample}}<td class=\"text-left\">{{! param.example}}</td>{{?}}\n" +
                "                                        {{? url.hasHeader}}<td class=\"text-left\">{{! param.paramType}}</td>{{?}}\n" +
                "                                    {{?}}\n" +
                "                                </tr>\n" +
                "                                {{~}}\n" +
                "                                </tbody>\n" +
                "                            </table>\n" +
                "                            {{?}}\n" +
                "\n" +
                "                            <p>返回示例及说明如下</p>\n" +
                "                            <div class=\"org-src-container\">\n" +
                "                                <pre class=\"src src-json\">{{! url.commentJson}}</pre>\n" +
                "                                {{? url.returnList && url.returnList.length > 0}}\n" +
                "                                <table class=\"table table-striped table-bordered table-hover table-condensed\">\n" +
                "                                    <thead>\n" +
                "                                    <tr>\n" +
                "                                        <th scope=\"col\" class=\"text-left\">名称</th>\n" +
                "                                        <th scope=\"col\" class=\"text-left\">类型</th>\n" +
                "                                        <th scope=\"col\" class=\"text-left\">说明</th>\n" +
                "                                    </tr>\n" +
                "                                    </thead>\n" +
                "                                    <tbody>\n" +
                "                                    {{~ url.returnList  :rtn}}\n" +
                "                                    <tr>\n" +
                "                                        <td class=\"text-left\">{{! rtn.name}}</td>\n" +
                "                                        <td class=\"text-left\">{{! rtn.type}}</td>\n" +
                "                                        <td class=\"text-left\">{{! rtn.desc}}</td>\n" +
                "                                    </tr>\n" +
                "                                    {{~}}\n" +
                "                                    </tbody>\n" +
                "                                </table>\n" +
                "                                {{?}}\n" +
                "                            </div>\n" +
                "\n" +
                "                            {{? (it.responseList && it.responseList.length > 0) || (url.responseList && url.responseList.length > 0)}}\n" +
                "                            <p>返回码说明如下</p>\n" +
                "                            <table class=\"table table-striped table-bordered table-hover table-condensed\">\n" +
                "                                <thead>\n" +
                "                                <tr>\n" +
                "                                    <th scope=\"col\" class=\"text-left\">返回码</th>\n" +
                "                                    <th scope=\"col\" class=\"text-left\">说明</th>\n" +
                "                                </tr>\n" +
                "                                </thead>\n" +
                "                                <tbody>\n" +
                "                                    {{~ (url.responseList || it.responseList)  :response}}\n" +
                "                                    <tr>\n" +
                "                                        <td class=\"text-left\">{{! response.code}}</td>\n" +
                "                                        <td class=\"text-left\">{{! response.msg}}</td>\n" +
                "                                    </tr>\n" +
                "                                    {{~}}\n" +
                "                                </tbody>\n" +
                "                            </table>\n" +
                "                            {{?}}\n" +
                "\n" +
                "                            <hr/>\n" +
                "                        </div>\n" +
                "                    </div>\n" +
                "                    {{~}}\n" +
                "                </div>\n" +
                "                {{~}}\n" +
                "            </script>\n" +
                "        </div>\n" +
                "        <div class=\"col-md-3\">\n" +
                "            <nav id=\"table-of-contents\">\n" +
                "                <div class=\"bs-docs-sidebar\">\n" +
                "                    <ul class=\"nav\">\n" +
                "                        <script id=\"url-nav-render\" type=\"text/x-dot-template\">\n" +
                "                            {{~ it.moduleList  :value:index}}\n" +
                "                            <li>\n" +
                "                                <a href=\"#{{! value.name}}\">{{! index + 1}} {{! value.info}}</a>\n" +
                "                                <ul class=\"nav\">\n" +
                "                                    {{~ value.urlList  :url:urlIndex}}\n" +
                "                                    <li>\n" +
                "                                        <a href=\"#{{! value.name}}-{{! url.id}}\">{{! index + 1}}.{{! urlIndex + 1}} {{! url.title}}</a>\n" +
                "                                    </li>\n" +
                "                                    {{~}}\n" +
                "                                </ul>\n" +
                "                            </li>\n" +
                "                            {{~}}\n" +
                "                        </script>\n" +
                "                    </ul>\n" +
                "                </div>\n" +
                "            </nav>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</div>\n" +
                "<footer class=\"postamble\">\n" +
                "    <script id=\"copyright\" type=\"text/x-dot-template\">\n" +
                "        <div>\n" +
                "            <p class=\"version\">Version: {{! it.version}}</p>\n" +
                "            <p class=\"copyright\">Copyright: {{! it.copyright}}</p>\n" +
                "            <p class=\"team\">Team: {{! it.team}}</p>\n" +
                "            <p class=\"statistics\">共 {{! it.groupCount}} 个模块, {{! it.apiCount}} 个接口</p>\n" +
                "        </div>\n" +
                "    </script>\n" +
                "</footer>\n" +
                "\n" +
                "<script src=\"//cdn.bootcss.com/jquery/1.11.3/jquery.min.js\"></script>\n" +
                "<script src=\"//cdn.bootcss.com/twitter-bootstrap/3.3.5/js/bootstrap.min.js\"></script>\n" +
                "<script type=\"text/javascript\">\n" +
                "    $(function () {\n" +
                "        'use strict';\n" +
                "        $(document.body).scrollspy({target: '.bs-docs-sidebar'});\n" +
                "        $('.bs-docs-sidebar').affix();\n" +
                "    });\n" +
                "</script>\n" +
                "\n" +
                "<script src=\"//cdn.bootcss.com/dot/1.1.2/doT.min.js\"></script>\n" +
                "<script type=\"text/javascript\">\n" +
                "    /* var projectDomain = \"//api.example.net\"; */\n" +
                "    var projectDomain = \"\"; // 如果自定义使用上面的\n" +
                "    $.get(projectDomain + \"/api/info\", function (module) {\n" +
                "        if (module === null || module === \"\") {\n" +
                "            $(\".col-md-9\").html(\"<span style='color:red;'>无接口信息</span>\");\n" +
                "        } else {\n" +
                "            var urlRender = $(\"#url-render\");\n" +
                "            var urlRenderTemplate = doT.template(urlRender.html());\n" +
                "            urlRender.parent().html(urlRenderTemplate(module));\n" +
                "\n" +
                "            var navRender = $(\"#url-nav-render\");\n" +
                "            var navRenderTemplate = doT.template(navRender.html());\n" +
                "            navRender.parent().html(navRenderTemplate(module));\n" +
                "\n" +
                "            $(\"pre[class='src src-json']\").each(function () {\n" +
                "                var html = $(this).html();\n" +
                "                if (html !== null && html !== \"\") {\n" +
                "                    html = html.replace(/(.*?)(\\/\\*.*?\\*\\/)/g, \"$1<span class=\\\"comment\\\">$2</span>\");\n" +
                "\n" +
                "                    var lineNum = html.split(/\\n/).length;\n" +
                "                    var lineHtml = '<span class=\"line-number\">';\n" +
                "                    for (var i = 0; i < lineNum; i++) {\n" +
                "                        lineHtml += '<span>' + (i) + '</span>';\n" +
                "                    }\n" +
                "                    lineHtml += '</span>';\n" +
                "                    $(this).html(lineHtml + html);\n" +
                "                }\n" +
                "            });\n" +
                "\n" +
                "            var href = window.location.href;\n" +
                "            if (href.includes(\"#\")) {\n" +
                "                href = href.substring(href.indexOf(\"#\"), href.length);\n" +
                "                var anchor = $(href);\n" +
                "                if (anchor.length > 0) {\n" +
                "                    var li = $(\"a[href='\" + href + \"']\").parent();\n" +
                "                    li.addClass(\"active\");\n" +
                "                    li.parent().parent().addClass(\"active\");\n" +
                "                    $(\"html,body\").animate({scrollTop: anchor.offset().top}, 0);\n" +
                "                }\n" +
                "            } else {\n" +
                "                $('.bs-docs-sidebar li').first().addClass('active');\n" +
                "            }\n" +
                "        }\n" +
                "    }).error(function () {\n" +
                "        $(\".col-md-9\").html(\"<span style='color:red;'>接口请求错误</span>\");\n" +
                "    });\n" +
                "\n" +
                "    $.get(projectDomain + \"/api/version\", function (urlCopyright) {\n" +
                "        if (urlCopyright !== null && urlCopyright !== \"\") {\n" +
                "            if (urlCopyright.title !== null && urlCopyright.title !== \"\") {\n" +
                "                $(\"title\").html(urlCopyright.title);\n" +
                "            }\n" +
                "            var copyrightRender = $(\"#copyright\");\n" +
                "            var template = doT.template(copyrightRender.html());\n" +
                "            copyrightRender.parent().html(template(urlCopyright));\n" +
                "        }\n" +
                "    });\n" +
                "</script>\n" +
                "</body>\n" +
                "</html>\n";
        System.out.println(Compressor.html(str));

//        System.out.println(HttpClientUtil.get("https://valid-isrgrootx1.letsencrypt.org/"));
//        System.out.println("\n\n\n==================\n\n\n");
//        System.out.println(HttpOkClientUtil.get("https://valid-isrgrootx1.letsencrypt.org/"));

    }
}