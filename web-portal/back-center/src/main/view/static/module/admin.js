layui.extend({
    md5: 'md5/md5',
    cover: 'md5/cover'
});
layui.define(['config', 'layer', 'cover', 'md5'], function (exports) {
    var config = layui.config;
    var layer = layui.layer;
    var md5 = layui.md5;
    var cover = layui.cover;
    var popupRightIndex, popupCenterIndex, popupCenterParam;

    var admin = {
        isRefresh: false,
        // 设置侧栏折叠
        flexible: function (expand) {
            var isExapnd = $('.layui-layout-admin').hasClass('admin-nav-mini');
            if (isExapnd == !expand) {
                return;
            }
            if (expand) {
                $('.layui-layout-admin').removeClass('admin-nav-mini');
            } else {
                $('.layui-layout-admin').addClass('admin-nav-mini');
            }
            admin.onResize();
        },
        // 设置导航栏选中
        activeNav: function (url) {
            $('.layui-layout-admin .layui-side .layui-nav .layui-nav-item .layui-nav-child dd').removeClass('layui-this');
            $('.layui-layout-admin .layui-side .layui-nav .layui-nav-item').removeClass('layui-this');
            if (url && url != '') {
                $('.layui-layout-admin .layui-side .layui-nav .layui-nav-item').removeClass('layui-nav-itemed');
                var $a = $('.layui-layout-admin .layui-side .layui-nav>.layui-nav-item>.layui-nav-child>dd>a[href="#!' + url + '"]');
                $a.parent('dd').addClass('layui-this');
                $a.parent('li').addClass('layui-this');
                $a.parent('dd').parent('.layui-nav-child').parent('.layui-nav-item').addClass('layui-nav-itemed');
            }
        },
        // 右侧弹出
        popupRight: function (path) {
            var param = new Object();
            param.path = path;
            param.id = 'adminPopupR';
            param.title = false;
            param.anim = 2;
            param.isOutAnim = false;
            param.closeBtn = false;
            param.offset = 'r';
            param.shadeClose = true;
            param.area = '336px';
            param.skin = 'layui-layer-adminRight';
            param.end = function () {
                layer.closeAll('tips');
            };
            popupRightIndex = admin.open(param);
            return popupRightIndex;
        },
        // 关闭右侧弹出
        closePopupRight: function () {
            layer.close(popupRightIndex);
        },
        // 中间弹出
        popupCenter: function (param) {
            param.id = 'adminPopupC';
            popupCenterParam = param;
            popupCenterIndex = admin.open(param);
            return popupCenterIndex;
        },
        // 关闭中间弹出并且触发finish回调
        finishPopupCenter: function () {
            layer.close(popupCenterIndex);
            popupCenterParam.finish ? popupCenterParam.finish() : '';
        },
        // 关闭中间弹出
        closePopupCenter: function () {
            layer.close(popupCenterIndex);
        },
        // 封装layer.open
        open: function (param) {
            var sCallBack = param.success;
            param.type = 1;
            param.area = param.area ? param.area : '450px';
            param.offset = param.offset ? param.offset : '120px';
            param.resize ? param.resize : false;
            param.shade ? param.shade : .2;
            param.success = function (layero, index) {
                sCallBack ? sCallBack(layero, index) : '';
                $(layero).children('.layui-layer-content').load(param.path);
            };
            return layer.open(param);
        },
        // 封装ajax请求，返回数据类型为json
        req: function (url, data, success, method, noHeaderToken) {
            if ('put' == method.toLowerCase()) {
                method = 'PUT';
            } else if ('delete' == method.toLowerCase()) {
                method = 'DELETE';
            }
            //add by owen ajax 执行前置处理器  
            admin.ajax({
                url: config.base_server + url,
                data: data,
                type: method,
                dataType: 'json',
                contentType: "application/json",
                success: success,
                beforeSend: function (xhr) {
                    if (!noHeaderToken) {
                        let token = config.getToken();
                        if (token) {
                            xhr.setRequestHeader('Authorization', 'Bearer ' + token.access_token);
                        }
                    }
                    //前端灰度版本匹配
                    let isolationVersion = config.isolationVersion;
                    if (isolationVersion) {
                        xhr.setRequestHeader('o-c-p-version', isolationVersion);
                    }
                }
            });
        },
        // 封装ajax请求，返回数据类型为json，请求头签名校验
        reqS: function (url, data, success, method) {
            if ('put' == method.toLowerCase()) {
                method = 'PUT';
            } else if ('delete' == method.toLowerCase()) {
                method = 'DELETE';
            }
            //创建签名
            let timestamp = new Date().getTime();
            let token = this.md5Encryption(timestamp);
            //add by owen ajax 执行前置处理器
            admin.ajax({
                url: config.base_server + url,
                data: data,
                type: method,
                dataType: 'json',
                contentType: "application/json",
                success: success,
                beforeSend: function (xhr) {
                    // 签名和时间戳
                    xhr.setRequestHeader('webToken', token);
                    xhr.setRequestHeader('webTm', timestamp);
                    //前端灰度版本匹配
                    let isolationVersion = config.isolationVersion;
                    if (isolationVersion) {
                        xhr.setRequestHeader('o-c-p-version', isolationVersion);
                    }
                }
            });
        },

        // 封装ajax请求
        ajax: function (param) {
            var successCallback = param.success;
            param.success = function (result, status, xhr) {
                // 判断登录过期和没有权限
                var jsonRs;
                if ('json' == param.dataType.toLowerCase()) {
                    jsonRs = result;
                } else if ('html' == param.dataType.toLowerCase() || 'text' == param.dataType.toLowerCase()) {
                    jsonRs = admin.parseJSON(result);
                }
                if (jsonRs) {
                    if (jsonRs.statusCodeValue == 401) {
                        config.removeToken();
                        layer.msg('登录过期', {icon: 2, time: 1500}, function () {
                            location.replace('/login.html');
                        }, 1000);
                        return;
                    } else if (jsonRs.statusCodeValue == 403) {
                        layer.msg('没有权限', {icon: 2});
                        layer.closeAll('loading');
                        return;
                    }
                }
                successCallback(result, status, xhr);
            };
            param.error = function (xhr) {
				var str = xhr.responseJSON.msg ;
                param.success({statusCodeValue: xhr.status, msg:
               		str==undefined || str==null ||str=="" ? xhr.statusText : xhr.responseJSON.msg
                 });
            };
            //发送同步ajax请求
            param.async = false;
            //console.log(param);
            $.ajax(param);
        },
        // 判断是否有权限
        hasPerm: function (auth) {
            var permissions = admin.getTempData("permissions");
            if (permissions){
                for (var i = 0; i < permissions.length; i++) {
                    if (auth == permissions[i]) {
                        return true;
                    }
                }
            }
            return false;
        },
        // 窗口大小改变监听
        onResize: function () {
            if (config.autoRender) {
                if ($('.layui-table-view').length > 0) {
                    setTimeout(function () {
                        admin.events.refresh();
                    }, 800);
                }
            }
        },
        // 显示加载动画
        showLoading: function (element) {
            $(element).append('<i class="layui-icon layui-icon-loading layui-anim layui-anim-rotate layui-anim-loop admin-loading"></i>');
        },
        // 移除加载动画
        removeLoading: function (element) {
            $(element + '>.admin-loading').remove();
        },
        // 缓存临时数据
        putTempData: function (key, value) {
            if (value) {
                layui.sessionData('tempData', {key: key, value: value});
            } else {
                layui.sessionData('tempData', {key: key, remove: true});
            }
        },
        // 获取缓存临时数据
        getTempData: function (key) {
            return layui.sessionData('tempData')[key];
        },
        // 滑动选项卡
        rollPage: function (d) {
            var $tabTitle = $('.layui-layout-admin .layui-body .layui-tab .layui-tab-title');
            var left = $tabTitle.scrollLeft();
            if ('left' === d) {
                $tabTitle.scrollLeft(left - 120);
            } else if ('auto' === d) {
                var autoLeft = 0;
                $tabTitle.children("li").each(function () {
                    if ($(this).hasClass('layui-this')) {
                        return false;
                    } else {
                        autoLeft += $(this).outerWidth();
                    }
                });
                $tabTitle.scrollLeft(autoLeft - 47);
            } else {
                $tabTitle.scrollLeft(left + 120);
            }
        },
        refresh: function () {
            admin.isRefresh = true;
            Q.refresh();
        },
        // 判断是否为json
        parseJSON: function (str) {
            if (typeof str == 'string') {
                try {
                    var obj = JSON.parse(str);
                    if (typeof obj == 'object' && obj) {
                        return obj;
                    }
                } catch (e) {
                }
            }
        }
        /**
         * 页面添加水印
         * @param txt   水印内容
         * @param zIndex z-index属性
         */
        ,watermark: function (txt, zIndex) {
            if (zIndex == null || zIndex == '') {
                zIndex = 9999999999;
            }
            let html = '<div id="water-div"></div>';
            $('body').append(html);
            //初始设置水印容器高度
            let water = document.getElementById('water-div');
            water.innerHTML = "";
            water.style.height = window.screen.availHeight + "px";
            water.style.height = document.documentElement.clientHeight + "px";
            water.style.zIndex = zIndex;
            //水印样式默认设置
            let defaultSettings = {
                watermark_txt:txt,
                watermark_x:20,//水印起始位置x轴坐标
                watermark_y:20,//水印起始位置Y轴坐标
                watermark_rows:2000,//水印行数
                watermark_cols:2000,//水印列数
                watermark_x_space:70,//水印x轴间隔
                watermark_y_space:30,//水印y轴间隔
                watermark_color:'#aaaaaa',//水印字体颜色
                watermark_alpha:0.3,//水印透明度
                watermark_fontsize:'15px',//水印字体大小
                watermark_font:'微软雅黑',//水印字体
                watermark_width:210,//水印宽度
                watermark_height:80,//水印长度
                watermark_angle:15//水印倾斜度数
            };
            //获取页面最大宽度
            let page_width = Math.max(water.scrollWidth,water.clientWidth);
            //获取页面最大高度
            let page_height = Math.max(water.scrollHeight,water.clientHeight);

            //水印数量自适应元素区域尺寸
            defaultSettings.watermark_cols=Math.ceil(page_width/(defaultSettings.watermark_x_space+defaultSettings.watermark_width));
            defaultSettings.watermark_rows=Math.ceil(page_height/(defaultSettings.watermark_y_space+defaultSettings.watermark_height));
            let x;
            let y;
            for (let i = 0; i < defaultSettings.watermark_rows; i++) {
                y = defaultSettings.watermark_y + (defaultSettings.watermark_y_space + defaultSettings.watermark_height) * i;
                for (let j = 0; j < defaultSettings.watermark_cols; j++) {
                    x = defaultSettings.watermark_x + (defaultSettings.watermark_width + defaultSettings.watermark_x_space) * j;
                    let mask_div = document.createElement('div');
                    //mask_div.id = 'mask_div' + i + j;
                    mask_div.className = 'mask_div';
                    //mask_div.appendChild(document.createTextNode(defaultSettings.watermark_txt));
                    mask_div.innerHTML=(defaultSettings.watermark_txt);
                    //设置水印div倾斜显示
                    mask_div.style.webkitTransform = "rotate(-" + defaultSettings.watermark_angle + "deg)";
                    mask_div.style.MozTransform = "rotate(-" + defaultSettings.watermark_angle + "deg)";
                    mask_div.style.msTransform = "rotate(-" + defaultSettings.watermark_angle + "deg)";
                    mask_div.style.OTransform = "rotate(-" + defaultSettings.watermark_angle + "deg)";
                    mask_div.style.transform = "rotate(-" + defaultSettings.watermark_angle + "deg)";
                    mask_div.style.visibility = "";
                    mask_div.style.position = "absolute";
                    mask_div.style.left = x + 'px';
                    mask_div.style.top = y + 'px';
                    mask_div.style.overflow = "hidden";
                    mask_div.style.zIndex = "9999";
                    mask_div.style.pointerEvents='none';//pointer-events:none 让水印不遮挡页面的点击事件
                    //mask_div.style.border="solid #eee 1px";//兼容IE9以下的透明度设置  mask_div.style.filter="alpha(opacity=50)";
                    mask_div.style.opacity = defaultSettings.watermark_alpha;
                    mask_div.style.fontSize = defaultSettings.watermark_fontsize;
                    mask_div.style.fontFamily = defaultSettings.watermark_font;
                    mask_div.style.color = defaultSettings.watermark_color;
                    mask_div.style.textAlign = "center";
                    mask_div.style.width = defaultSettings.watermark_width + 'px';
                    mask_div.style.height = defaultSettings.watermark_height + 'px';
                    mask_div.style.display = "block";
                    water.appendChild(mask_div);
                }
            }
        }
        /**
         * 添加水印
         * */
        ,addWater: function () {
            if (config.waterMark) {
                let href = location.href;
                if (!(/\/login.html\??[^]*/.test(href.substring(href.lastIndexOf("/"))))) {
                    let loginUser = config.getUser();
                    let text = admin.parseTime(new Date());
                    if (loginUser != null) {
                        text = loginUser.username + loginUser.mobile + '<br/>' + admin.parseTime(new Date());
                    }
                    if (top.$('#water-div').length == 0) {
                        if (config.waterMarkTop) {
                            $(window).on('load', admin.watermark(text));
                        } else {
                            $(window).on('load', admin.watermark(text, 999));
                        }
                    }
                }
            }
        }
        // 时间解析
        ,parseTime: function (date, format) {
            if (admin.isNull(format)) {
                format = 'yyyy-MM-dd HH:mm:ss';
            }
            let o = {
                "M+": date.getMonth() + 1, //月份
                "d+": date.getDate(), //日
                "H+": date.getHours(), //小时
                "m+": date.getMinutes(), //分
                "s+": date.getSeconds(), //秒
                "q+": Math.floor((date.getMonth() + 3) / 3), //季度
                "S": date.getMilliseconds() //毫秒
            };
            if (/(y+)/.test(format)) format = format.replace(RegExp.$1, (date.getFullYear() + "").substr(4 - RegExp.$1.length));
            for (let k in o) {
                if (new RegExp("(" + k + ")").test(format)) format = format.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
            }
            return format;
        }
        /**
         * 判断字符串是否为空
         * @param txt
         * @returns {boolean}
         */
        ,isNull: function (txt) {
            if (typeof txt == 'undefined' || txt == null || txt == '' || txt.trim() == '' || txt.toLowerCase() == 'null') {
                return true;
            }
            return false;
        }
        /**
         * md5加密
         * @param t
         * @returns {string|*}
         */
        ,md5Encryption: function (t) {
            return md5.md5(cover.fromCode(md5.secret()) + t);
        }
        /**
         * 解密secret
         * @param s
         * @returns {*}
         */
        ,coverCode: function (s) {
            return cover.fromCode(s);
        }
    };

    // ewAdmin提供的事件
    admin.events = {
        flexible: function (e) {  // 折叠侧导航
            var expand = $('.layui-layout-admin').hasClass('admin-nav-mini');
            admin.flexible(expand);
        },
        refresh: function () {  // 刷新主体部分
            admin.refresh();
        },
        back: function () {  //后退
            history.back();
        },
        theme: function () {  // 设置主题
            admin.popupRight('pages/tpl/theme.html');
        },
        fullScreen: function (e) {  // 全屏
            var ac = 'layui-icon-screen-full', ic = 'layui-icon-screen-restore';
            var ti = $(this).find('i');

            var isFullscreen = document.fullscreenElement || document.msFullscreenElement || document.mozFullScreenElement || document.webkitFullscreenElement || false;
            if (isFullscreen) {
                var efs = document.exitFullscreen || document.webkitExitFullscreen || document.mozCancelFullScreen || document.msExitFullscreen;
                if (efs) {
                    efs.call(document);
                } else if (window.ActiveXObject) {
                    var ws = new ActiveXObject('WScript.Shell');
                    ws && ws.SendKeys('{F11}');
                }
                ti.addClass(ac).removeClass(ic);
            } else {
                var el = document.documentElement;
                var rfs = el.requestFullscreen || el.webkitRequestFullscreen || el.mozRequestFullScreen || el.msRequestFullscreen;
                if (rfs) {
                    rfs.call(el);
                } else if (window.ActiveXObject) {
                    var ws = new ActiveXObject('WScript.Shell');
                    ws && ws.SendKeys('{F11}');
                }
                ti.addClass(ic).removeClass(ac);
            }
        },
        // 左滑动tab
        leftPage: function () {
            admin.rollPage("left");
        },
        // 右滑动tab
        rightPage: function () {
            admin.rollPage();
        },
        // 关闭当前选项卡
        closeThisTabs: function () {
            var $title = $('.layui-layout-admin .layui-body .layui-tab .layui-tab-title');
            if ($title.find('li').first().hasClass('layui-this')) {
                return;
            }
            $title.find('li.layui-this').find(".layui-tab-close").trigger("click");
        },
        // 关闭其他选项卡
        closeOtherTabs: function () {
            $('.layui-layout-admin .layui-body .layui-tab .layui-tab-title li:gt(0):not(.layui-this)').find(".layui-tab-close").trigger("click");
        },
        // 关闭所有选项卡
        closeAllTabs: function () {
            $('.layui-layout-admin .layui-body .layui-tab .layui-tab-title li:gt(0)').find(".layui-tab-close").trigger("click");
        },
        // 关闭所有弹窗
        closeDialog: function () {
            layer.closeAll('page');
        }
    };

    // 所有ew-event
    $('body').on('click', '*[ew-event]', function () {
        var event = $(this).attr('ew-event');
        var te = admin.events[event];
        te && te.call(this, $(this));
    });

    // 移动设备遮罩层点击事件
    $('.site-mobile-shade').click(function () {
        admin.flexible(true);
    });

    // 侧导航折叠状态下鼠标经过显示提示
    $('body').on('mouseenter', '.layui-layout-admin.admin-nav-mini .layui-side .layui-nav .layui-nav-item>a', function () {
        var tipText = $(this).find('cite').text();
        if (document.body.clientWidth > 750) {
            layer.tips(tipText, this);
        }
    }).on('mouseleave', '.layui-layout-admin.admin-nav-mini .layui-side .layui-nav .layui-nav-item>a', function () {
        layer.closeAll('tips');
    });

    // 侧导航折叠状态下点击展开
    $('body').on('click', '.layui-layout-admin.admin-nav-mini .layui-side .layui-nav .layui-nav-item>a', function () {
        if (document.body.clientWidth > 750) {
            layer.closeAll('tips');
            admin.flexible(true);
        }
    });

    // 所有lay-tips处理
    $('body').on('mouseenter', '*[lay-tips]', function () {
        var tipText = $(this).attr('lay-tips');
        var dt = $(this).attr('lay-direction');
        layer.tips(tipText, this, {tips: dt || 1, time: -1});
    }).on('mouseleave', '*[lay-tips]', function () {
        layer.closeAll('tips');
    });

    exports('admin', admin);
});
