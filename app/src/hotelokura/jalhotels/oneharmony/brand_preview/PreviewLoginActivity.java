package hotelokura.jalhotels.oneharmony.brand_preview;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import hotelokura.jalhotels.oneharmony.MainApplication;
import hotelokura.jalhotels.oneharmony.R;
import hotelokura.jalhotels.oneharmony.setting.AppNetSetting;
import hotelokura.jalhotels.oneharmony.util.LogUtil;
import hotelokura.jalhotels.oneharmony.view.MyProgressDialog;

public class PreviewLoginActivity extends FragmentActivity {
	static final String TAG = "PreviewLoginActivity";

    private PreviewManager mPrevManager;

    private boolean isTablet;
    private EditText mEmail;
    private EditText mPasswd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_preview_login);
        mEmail = (EditText) findViewById(R.id.editMail);
        mPasswd = (EditText) findViewById(R.id.editPass
        );

        isTablet = MainApplication.getInstance().isTabletDevice();
        if (!isTablet) {
            // スマホの時は縦固定
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        mPrevManager = PreviewManager.getInstance(getApplicationContext());

        ImageView button = (ImageView) findViewById(R.id.loginBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!MainApplication.getInstance().isNetConnected()) {
                    Toast.makeText(getApplicationContext(), getString(R.string.err_not_connected), Toast.LENGTH_LONG).show();
                    return;
                }

                final MyProgressDialog progress = new MyProgressDialog(PreviewLoginActivity.this);
                progress.setMessage(getString(R.string.prev_progress_login));


                AppNetSetting appNetSetting = MainApplication.getInstance().getAppNetSetting();
                if (appNetSetting == null) {
                    finish();
                    return;
                }
                String url = appNetSetting.getNetAppURL() + "sp/preview/login";
                RequestParams params = new RequestParams();
                params.put("identifier", getPackageName());
                params.put("email", mEmail.getText().toString());
                params.put("password", mPasswd.getText().toString());
                LogUtil.d(TAG, mEmail.getText().toString());
                LogUtil.d(TAG, mPasswd.getText().toString());

                final AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
                client.setTimeout(10 * 1000);
                client.post(url, params, new JsonHttpResponseHandler() {
//                    @Override
                    public void onSuccess(JSONObject response) {
                        progress.dismiss();
                        LogUtil.i(TAG, "AsyncHttpClient : onSuccess");
                        LogUtil.v(TAG, response.toString());

                        // URLが取得できない場合は遷移させない
                        try {
                            mPrevManager.setUrl(response.getString("net_url"));
                        } catch (JSONException e) {
                            LogUtil.v(TAG, e.toString());
                            Toast.makeText(getApplicationContext(), getString(R.string.prev_toast_get_url_fail), Toast.LENGTH_LONG).show();
                            return;
                        }

                        // AppIDが取得できない場合は遷移させない
                        try {
                            mPrevManager.setAppId(response.getString("app_id"));
                        } catch (JSONException e) {
                            LogUtil.v(TAG, e.toString());
                            Toast.makeText(getApplicationContext(), getString(R.string.prev_toast_get_url_fail), Toast.LENGTH_LONG).show();
                            return;
                        }

                        // アプリ名は未設定のケースがあるため取得出来なくても処理を継続する
                        try {
                            mPrevManager.setName(response.getString("user_name"));
                        } catch (JSONException e) {
                            LogUtil.v(TAG, e.toString());
                        }

                        // アプリ名は未設定のケースがあるため取得出来なくても処理を継続する
                        try {
                            mPrevManager.setSplashUrl(response.getString("splash_url"));
                        } catch (JSONException e) {
                            LogUtil.v(TAG, e.toString());
                        }

                        mPrevManager.login();
                        Intent intent = new Intent(getApplicationContext(), PreviewMenuActivity.class);
                        startActivity(intent);
                    }

//                    @Override
                    public void onFailure(Throwable error) {
                        progress.dismiss();
                        LogUtil.i(TAG, "AsyncHttpClient : onFailure");
                        if (error != null) LogUtil.v(TAG, error.toString());
                        Toast.makeText(getApplicationContext(), getString(R.string.prev_toast_connect_fail), Toast.LENGTH_LONG).show();
                    }

//                    @Override
                    public void onFailure(Throwable error, JSONObject response) {
                        progress.dismiss();
                        LogUtil.i(TAG, "AsyncHttpClient : onFailure with params");
                        if (response != null) {
                            LogUtil.v(TAG, response.toString());
                            Toast.makeText(getApplicationContext(), getString(R.string.prev_toast_authentication_fail), Toast.LENGTH_LONG).show();
                        } else {
                            if (error != null) LogUtil.v(TAG, error.toString());
                            Toast.makeText(getApplicationContext(), getString(R.string.prev_toast_connect_fail), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFinish() {
                        LogUtil.i(TAG, "AsyncHttpClient : onFinish");
                        progress.dismiss();
                    }

                    @Override
                    public void onStart() {
                        LogUtil.i(TAG, "AsyncHttpClient : onStart");
                        progress.show();
                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        LogUtil.i(TAG, "onStart");
        super.onStart();
    }

    @Override
    protected void onResume() {
        LogUtil.i(TAG, "onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        LogUtil.i(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        LogUtil.i(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        LogUtil.i(TAG, "onDestroy");
        super.onDestroy();
    }
}
