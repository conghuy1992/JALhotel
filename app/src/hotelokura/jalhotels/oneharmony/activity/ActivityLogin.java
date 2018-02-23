package hotelokura.jalhotels.oneharmony.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import hotelokura.jalhotels.oneharmony.JSONParser;
import hotelokura.jalhotels.oneharmony.MainApplication;
import hotelokura.jalhotels.oneharmony.R;
import hotelokura.jalhotels.oneharmony.activity.web.WebViewActivity;
import hotelokura.jalhotels.oneharmony.activity.webtop.WebTopActivity;
import hotelokura.jalhotels.oneharmony.view.ProgressIndicatorDialog;

/**
 * Created by barista7 on 2/23/16.
 */
public class ActivityLogin extends Activity implements View.OnClickListener {
    JSONParser jsonParser = new JSONParser();
    private ProgressIndicatorDialog progress;
    private ImageView imgback, imgfinish;
    private TextView txtnotify, forgot, join;
    private EditText email;
    private EditText pass;
    private CheckBox checkBox;
    private Button btnLogin;
    private boolean isTablet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        isTablet = MainApplication.getInstance().isTabletDevice();
        if (!isTablet) {
            setContentView(R.layout.activity_login);
        } else {
            setContentView(R.layout.activity_login_tablet);
        }
        initView();
    }

    public void initView() {
        progress = new ProgressIndicatorDialog(this);
        progress.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (progress != null) {
                    progress.dismiss();
                    progress = null;
                }
                finish();
            }
        });
        join = (TextView) findViewById(R.id.txtjoinusnow);
        forgot = (TextView) findViewById(R.id.txtforgotpw);
        btnLogin = (Button) findViewById(R.id.button2);
        checkBox = (CheckBox) findViewById(R.id.checkBox);
        txtnotify = (TextView) findViewById(R.id.txtnotify);
        email = (EditText) findViewById(R.id.editText);
        pass = (EditText) findViewById(R.id.editText2);
        imgback = (ImageView) findViewById(R.id.imgback);
        imgback.setOnClickListener(this);
        imgfinish = (ImageView) findViewById(R.id.imgfinish);
        imgfinish.setOnClickListener(this);

        btnLogin.setOnClickListener(this);
        join.setOnClickListener(this);
        forgot.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == imgfinish) {
            finish();
        } else if (v == imgback) {
            finish();
        } else if (v == btnLogin) {
            if (email.getText().length() == 0 || pass.getText().length() == 0) {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setMessage(getResources().getString(R.string.customer_login_alert));
                alert.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alert.create().show();
            } else {
                new LoginAction().execute();
            }
        } else if (v == join) {
            intentBrowser(JSONParser.url_join);
        } else if (v == forgot) {
            intentBrowser(JSONParser.url_forgot);
        }
    }

    public void intentBrowser(String url) {
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra("StartUrl", url);
        startActivity(intent);
    }

    class LoginAction extends AsyncTask<String, String, String> {
        String strResponse = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.show();
        }

        protected String doInBackground(String... args) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(JSONParser.API_LOGIN);
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("UserName", email.getText().toString().trim()));
                nameValuePairs.add(new BasicNameValuePair("Password", pass.getText().toString().trim()));
                nameValuePairs.add(new BasicNameValuePair("RememberMe", "false"));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity resEntity = response.getEntity();
                BufferedReader br = new BufferedReader(new InputStreamReader(resEntity.getContent(), "UTF-8"));
                String line;
                while (((line = br.readLine()) != null)) {
                    strResponse = strResponse + line + " ";
                }
            } catch (ClientProtocolException e) {
            } catch (IOException e) {
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            progress.dismiss();
//            Log.d("TAG", "strResponse: " + strResponse);
            if (strResponse.contains(email.getText().toString().trim()) && strResponse.contains("memberCurrentPointsBalance")) {
                WebTopActivity.check_Login = true;
                Intent intent = new Intent(ActivityLogin.this, WebViewActivity.class);
                intent.putExtra("StartUrl", "loginfromnative");
                intent.putExtra("user", email.getText().toString().trim());
                intent.putExtra("pw", pass.getText().toString().trim());
                startActivity(intent);
                finish();
            } else {
                AlertDialog.Builder alert = new AlertDialog.Builder(ActivityLogin.this);
                alert.setMessage(getResources().getString(R.string.customer_login_fail));
                alert.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alert.create().show();
            }

        }
    }

    public void savingPreferences() {
        SharedPreferences pre = getSharedPreferences
                (JSONParser.prefname, MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        String user = email.getText().toString();
        String pwd = pass.getText().toString();
        boolean bchk = checkBox.isChecked();
        if (!bchk) {
            editor.clear();
        } else {
            editor.putString("user", user);
            editor.putString("pwd", pwd);
            editor.putBoolean("checked", bchk);
        }
        editor.commit();
    }

    public void restoringPreferences() {
        SharedPreferences pre = getSharedPreferences
                (JSONParser.prefname, MODE_PRIVATE);
        boolean bchk = pre.getBoolean("checked", false);
        if (bchk) {
            String user = pre.getString("user", "");
            String pwd = pre.getString("pwd", "");
            email.setText(user);
            pass.setText(pwd);
        }
        checkBox.setChecked(bchk);
    }

    @Override
    protected void onPause() {
        super.onPause();
        savingPreferences();
    }

    @Override
    protected void onResume() {
        super.onResume();
        restoringPreferences();
    }
}
