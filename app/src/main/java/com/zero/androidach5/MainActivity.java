package com.zero.androidach5;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private TextView textView;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_main);

        webView = (WebView) findViewById(R.id.webview);
        textView = (TextView) findViewById(R.id.textview);

        initWebView();
        webView.loadUrl("file:///android_asset/index.html");
    }

    /**
     * #################  Android Native --> Web
     */
    public void native2Web(View view) {
        //调用javascript中的方法，传入string数据
        webView.loadUrl("javascript:getFromAndroid('the data is from android!')");
    }


    /**
     * #################  Web --> Android Native
     * Android漏洞原因 SDK 17 以上需要添加一个接口 JavascriptInterface,否则给出17以下不能使用的警告
     */
    final class InJavaScript {

        //若需要方法直接被WebView调用,必须在方法前添加此接口注解
        @JavascriptInterface
        public void web2Native(final String str) {
            handler.post(new Runnable() {
                public void run() {
                    textView.setText(str);
                }
            });
        }

        //若需要方法直接被WebView调用,必须在方法前添加此接口注解
        @JavascriptInterface
        public void test() {

        }
    }


    /**
     * ################# WebView 进行设置
     */
    private void initWebView() {

        //把本类的一个实例添加到js的全局对象window中,这样就可以使用window.injs来调用它的方法
        webView.addJavascriptInterface(new InJavaScript(), "injs");


        // 获取设置对象
        WebSettings webSettings = webView.getSettings();


        // 允许执行JavaScript脚本
        webSettings.setJavaScriptEnabled(true);


        // 允许访问文件系统
        webSettings.setAllowFileAccess(true);


        // 使用内置的缩放机制(手势、控件)
        webSettings.setBuiltInZoomControls(true);
        // 隐藏内置的缩放控件 默认ture
        webSettings.setDisplayZoomControls(false);


        // DOM存储API是否可用
        webSettings.setDomStorageEnabled(true);


        // 数据库存储API是否可用,该设置对同一进程中的所有WebView实例均有效
        // 注意: 只能在当前进程的任意WebView加载页面之前修改此项，因为此节点之后WebView的实现类可能会忽略该项设置的改变。
        webSettings.setDatabaseEnabled(true);
        //TODO 已废弃，数据库路径由实现（implementation）管理，调用此方法无效。
        String dir = getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
        webSettings.setDatabasePath(dir);


        // 定位是否可用。请注意，为了确保定位API在WebView的页面中可用，必须遵守如下约定:
        // (1) app必须有定位的权限，参见ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION；
        // (2) app必须提供onGeolocationPermissionsShowPrompt(String, GeolocationPermissions.Callback)回调方法的实现，
        //      在页面通过JavaScript定位API请求定位时接收通知。
        //
        webSettings.setGeolocationEnabled(true);
        // 定位数据库的保存路径。
        // 为了确保定位权限和缓存位置的持久化，该方法应该传入一个应用可写的（数据库）路径。
        webSettings.setGeolocationDatabasePath(dir);


        // 覆盖默认后退按钮的作用，替换成WebView里的查看历史页面
        webView.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if ((keyCode == KeyEvent.KEYCODE_BACK)
                            && webView.canGoBack()) {
                        webView.goBack();
                        return true;
                    }
                }
                return false;
            }
        });


        /**
         * 设置WebViewClient
         * 作用：主要帮助WebView处理各种通知、请求事件等。
         * 更多更全的方法介绍， 博客：http://blog.csdn.net/qq_18669217/article/details/52792958
         */
        webView.setWebViewClient(new WebViewClient() {

            // 加载URL的时候，不是每次都调用
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }

            // 页面启动时
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            // 处理报错信息
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
            }

            // 网络加载结束时调用
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            // WebView尺寸发生变化
            @Override
            public void onScaleChanged(WebView view, float oldScale, float newScale) {
                super.onScaleChanged(view, oldScale, newScale);
            }

            //自动登录的帐号过程
            @Override
            public void onReceivedLoginRequest(WebView view, String realm, String account, String args) {
                super.onReceivedLoginRequest(view, realm, account, args);
            }
        });


        /**
         * 设置WebChromeClient
         * 作用：主要辅助WebView处理JavaScript的对话框、网站图标、网站title、加载进度等。
         * 更多更全的方法介绍， 博客：http://blog.csdn.net/qq_18669217/article/details/52792958
         */
        webView.setWebChromeClient(new WebChromeClient() {

            //设置网页加载的进度显示
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                MainActivity.this.getWindow().setFeatureInt(Window.FEATURE_PROGRESS, newProgress * 100);
                super.onProgressChanged(view, newProgress);
            }

            //网页标题title回调
            public void onReceivedTitle(WebView view, String title) {
                MainActivity.this.setTitle(title);
                super.onReceivedTitle(view, title);
            }

            //网页图标回调
            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                super.onReceivedIcon(view, icon);
            }


            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
            }

            //处理 javascript 中的 alert 弹框
            //应用程序接管必须返回：true
            //JS调用方式 alert("This is an alert!");
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                //构建一个Builder来显示网页中的对话框
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Alert");
                builder.setMessage(message);
                builder.setPositiveButton(android.R.string.ok,
                        new AlertDialog.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                result.confirm();
                            }
                        });
                builder.setCancelable(false);
                builder.create();
                builder.show();
                return true;
            }


            //处理 javascript 中的 confirm 确认框
            //应用程序接管必须返回：true
            //JS调用方式 confirm("This is an confirm!");
            @Override
            public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("confirm");
                builder.setMessage(message);
                builder.setPositiveButton(android.R.string.ok,
                        new AlertDialog.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                result.confirm();
                            }
                        });
                builder.setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                result.cancel();
                            }
                        });
                builder.setCancelable(false);
                builder.create();
                builder.show();
                return true;
            }


            //处理 javascript 中的 prompt 提示框
            //应用程序接管必须调用：result.confirm，并返回true
            //JS调用方式 prompt("This is an prompt!");
            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
//                result.confirm();
//                result.confirm(message);
                return super.onJsPrompt(view, url, message, defaultValue, result);
            }

            //当前页面请求是否允许H5定位
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
                super.onGeolocationPermissionsShowPrompt(origin, callback);
            }


            /**
             * 通知应用程序webview内核web sql 数据库超出配额，请求是否扩大数据库磁盘配额。默认行为是不会增加数据库配额。
             * 使用新的数据库操作 TODO WebView now uses the HTML5 / JavaScript Quota Management API.
             * @param url 触发这个数据库配额的url地址
             * @param databaseIdentifier 指示出现数据库超过配额的标识
             * @param currentQuota 原始数据库配额的大小，是字节单位bytes
             * @param estimatedSize 到达底线的数据大小 bytes
             * @param totalUsedQuota 总的数据库配额大小 bytes
             * @param quotaUpdater 更新数据库配额的对象，可以使用 quotaUpdater.updateQuota(newQuota);配置新的数据库配额大小
             */
            public void onExceededDatabaseQuota(String url,
                                                String databaseIdentifier, long currentQuota,
                                                long estimatedSize, long totalUsedQuota,
                                                WebStorage.QuotaUpdater quotaUpdater) {
                quotaUpdater.updateQuota(estimatedSize * 2);
            }

            /**
             * 通知应用程序内核已经到达最大的appcache。appcache是HTML5针对offline的一个数据处理标准。
             * 使用新的数据库操作 TODO WebView now uses the HTML5 / JavaScript Quota Management API.
             * @param spaceNeeded
             * @param totalUsedQuota
             * @param quotaUpdater
             */
            public void onReachedMaxAppCacheSize(long spaceNeeded, long totalUsedQuota, WebStorage.QuotaUpdater quotaUpdater) {
                quotaUpdater.updateQuota(spaceNeeded * 2);
            }
        });
    }
}
