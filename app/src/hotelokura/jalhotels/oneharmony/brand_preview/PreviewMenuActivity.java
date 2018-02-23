package hotelokura.jalhotels.oneharmony.brand_preview;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import hotelokura.jalhotels.oneharmony.MainApplication;
import hotelokura.jalhotels.oneharmony.R;
import hotelokura.jalhotels.oneharmony.activity.splash.SplashActivity;
import hotelokura.jalhotels.oneharmony.net.AsyncCallback;
import hotelokura.jalhotels.oneharmony.net.AsyncResult;
import hotelokura.jalhotels.oneharmony.net.DownloadManager;
import hotelokura.jalhotels.oneharmony.util.LogUtil;
import hotelokura.jalhotels.oneharmony.view.MyProgressDialog;

public class PreviewMenuActivity extends FragmentActivity {
	static final String TAG = "PreviewMenuActivity";

    private boolean isTablet;
    private MyProgressDialog mProgress;
    private int mReqCnt;
    private boolean mDlError = false;
    private PreviewManager mPrevManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        isTablet = MainApplication.getInstance().isTabletDevice();
        if (!isTablet) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            setContentView(R.layout.activity_preview_menu);
        } else {
            setContentView(R.layout.activity_preview_menu_tb);
        }


        mPrevManager = PreviewManager.getInstance(getApplicationContext());

        mProgress = new MyProgressDialog(PreviewMenuActivity.this);
        mProgress.setMessage(getString(R.string.prev_progress_start));

        // プレビュー開始ボタン
        ImageView startButton = (ImageView) findViewById(R.id.start_preview);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainApplication mainApp = MainApplication.getInstance();

                // ネットワーク接続チェック
                if (!mainApp.isNetConnected()) {
                    Toast.makeText(getApplicationContext(), getString(R.string.err_not_connected), Toast.LENGTH_LONG).show();
                    return;
                }

                mReqCnt = 0;
                mDlError = false;

                mProgress.show();

                if (!isTablet) {
                    if (mainApp.isHDpi()) {
                        downloadImage(PreviewManager.SPLASH_NAME_P);
                    } else if (mainApp.isMDpi()) {
                        downloadImage(PreviewManager.SPLASH_NAME_P);
                    } else {
                        downloadImage(PreviewManager.SPLASH_NAME_P);
                    }
                } else {
                    if (mainApp.isMDpi()) {
                        downloadImage(PreviewManager.SPLASH_NAME_P);
                        downloadImage(PreviewManager.SPLASH_NAME_L);
                    } else{
                        downloadImage(PreviewManager.SPLASH_NAME_P);
                        downloadImage(PreviewManager.SPLASH_NAME_L);
                    }
                }
            }
        });

        // ログイン名表示
        TextView nameText = (TextView) findViewById(R.id.name);
        nameText.setText(mPrevManager.getName());

        // ログアウト
        TextView logoutButton = (TextView) findViewById(R.id.logout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPrevManager.logout();
                mPrevManager.setName("");
                mPrevManager.setUrl("");

                Intent intent = new Intent(getApplicationContext(), PreviewLoginActivity.class);
                startActivity(intent);
                finish();
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

    private void downloadImage(String filename) {
        String url = mPrevManager.getSplashUrl();
        MainApplication.getInstance().removeCacheFile(url, filename);

        mReqCnt++;

        // 画像のダウンロード
        DownloadManager.getInstance().offerImage("preview", 5,
                url, filename, new AsyncCallback<Bitmap>() {
            @Override
            public void onSuccess(AsyncResult<Bitmap> result) {
                mReqCnt--;
                LogUtil.i(TAG, "onSuccess");
                if (mReqCnt <= 0) {
                    mProgress.dismiss();
                    if (!mDlError) {
                        SplashActivity.startActivity(PreviewMenuActivity.this, SplashActivity.MODE_DL);
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.err_status_server), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailed(AsyncResult<Bitmap> result) {
                mDlError = true;
                mReqCnt--;
                LogUtil.i(TAG, "onFailed");
                if (mReqCnt <= 0) {
                    mProgress.dismiss();
                    Toast.makeText(getApplicationContext(), getString(R.string.err_status_server), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
