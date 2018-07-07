package cn.walkpast.js2theme;

import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mSwitchTheme;
    private WebView mWebView;
    /**
     * 记录是夜间模式；还是白昼模式，默认为白昼模式
     */
    private boolean isDayTheme = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //强制竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_main);

        mSwitchTheme = findViewById(R.id.switch_theme);
        mWebView = findViewById(R.id.web_view);
        mSwitchTheme.setOnClickListener(this);

        initWebView();
    }

    private void initWebView() {

        WebSettings mSettings = mWebView.getSettings();
        //默认是false 设置true允许和js交互
        mSettings.setJavaScriptEnabled(true);
        //设置webview推荐使用的窗口，使html界面自适应屏幕
        mSettings.setUseWideViewPort(true);
        //屏幕自适应网页,如果没有这个，在低分辨率的手机上显示可能会异常
        mSettings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        //提高渲染的优先级
        mSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        //非阻塞，设置网页在加载的时加载图片
        mSettings.setBlockNetworkImage(false);
        mSettings.setTextSize(WebSettings.TextSize.LARGER);
        //视频这一块
        mSettings.setPluginState(WebSettings.PluginState.ON);
        mSettings.setAllowFileAccess(true); // 允许访问文件
        mSettings.setSupportZoom(true); // 支持缩放
        mSettings.setLoadWithOverviewMode(true);
        mSettings.setDomStorageEnabled(true);
        mSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); // 不加载缓存内容

        //html涉及到中http和https模式时，兼容
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        //快照
        mWebView.setDrawingCacheEnabled(true);
        //允许多窗口
        mSettings.setSupportMultipleWindows(true);
        //加载本地html文件
        mWebView.loadUrl("https://github.com/KernHu");
        //加载网络url--百度新闻；
        // mWebView.loadUrl("http://news.china.com/internationalgd/10000166/20171024/31596520.html");
        //加载网络url--腾讯新闻；
        // mWebView.loadUrl(url == null ? "http://appstore.szprize.cn/appstore/web/test" : url);
        //mWebView.loadUrl("http://appstore.szprize.cn/appstore/web/test");
    }


    @Override
    public void onClick(View v) {

        isDayTheme = !isDayTheme;
        mSwitchTheme.setText(isDayTheme ? "切换夜间模式" : "切换白昼模式");

        //native注入js代码
        mWebView.post(new Runnable() {
            @Override
            public void run() {

                injectJs(mWebView, isDayTheme);
            }
        });
    }

    /**
     * 通过WebView向H5注入js代码
     *
     * @param webView
     */
    private void injectJs(WebView webView, boolean isDayTheme) {

        // && isOnPageFinished
        if (isDayTheme) {
            //API19，android4.4及以下无法分开处理
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                webView.evaluateJavascript("document.body.style.backgroundColor=\"white\";document.body.style.color=\"black\";", null);
            } else {
                webView.loadUrl("javascript:document.body.style.backgroundColor=\"#white\";document.body.style.color=\"black\";");
            }
        } else if (!isDayTheme) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                webView.evaluateJavascript("document.body.style.backgroundColor=\"black\";document.body.style.color=\"white\";", null);
            } else {
                webView.loadUrl("javascript:document.body.style.backgroundColor=\"#black\";document.body.style.color=\"white\";");
            }
        }
    }


    /**********************************************************/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        isDayTheme=true;
        switch (item.getItemId()) {

            case R.id.action_local:
                //加载本地html文件
                mWebView.loadUrl("file:///android_asset/test_theme.html");
                break;
            case R.id.action_github:
                //加载网络url--腾讯新闻；
                mWebView.loadUrl("http://www.walkpast.cn/");
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
