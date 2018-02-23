package com.baristapushsdk.api;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import com.baristapushsdk.Constants;
import com.baristapushsdk.entity.Message;
import com.baristapushsdk.utils.DebugLog;
import com.baristapushsdk.utils.NetworkUtil;

/**
 * Created by Trung Kien on 10/6/2015.
 */
public class ApiManager {

    public interface OnRegisStatus {
        void onRegis(boolean isSuccess);
    }

    public interface OnRegisAcceptPush {
        void onAccept(boolean isSuccess);
    }

    public interface OnRetrieveStatusPush {
        void onRetrieveStatus(boolean isSuccess, String status);
    }

    public interface OnLoadContent {
        void onLoadContent(boolean isSuccess, String data);
    }

    public interface OnUpdateRecordStatus {
        void onUpdateRecord(boolean isSuccess);
    }

    public interface OnSyncMessages {
        void onSyncMessages(boolean isSuccess, String data, String delete_id);
    }

    public interface OnGetBadgeCount {
        void onGetBadgeCount(boolean isSuccess, String count);
    }

    public interface OnGetDataGCM {
        void onGetDataGCM(List<Message> messageList);
    }

    /**
     * Register gcm_id to server
     * @param bundle_id
     * @param device_id
     * @param push_token
     * @param os_type
     * @param os_version
     * @param listener
     */
    public static void registerPush(String bundle_id, String device_id, String push_token, String os_type,
                                    String os_version,  final OnRegisStatus listener) {
        RequestParams requestParams = new RequestParams();
        requestParams.add("Name", "");

        NetworkUtil.post(Constants.BASE_URL + Constants.API_URL_REGISTER_PUSH, requestParams,bundle_id, device_id, push_token,
                os_type, os_version, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                boolean success = false;
                try {
                    String str = new String(responseBody);
                    if (str.contains("200") || str.contains("Regist Token success")) {
                        success = true;
                    }
                } catch (Exception ex) {
                    DebugLog.e(ex.toString());
                }

                if (listener != null) {
                    listener.onRegis(success);
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                if (listener != null) {
                    listener.onRegis(false);
                }
            }

        });
    }

    /**
     * Accept notifications from the server or not
     * @param bundle_id
     * @param device_id
     * @param push_token
     * @param os_type
     * @param os_version
     * @param accept_push
     * @param listener
     */
    public static void registerAcceptPush(String bundle_id, String device_id, String push_token, String os_type,
                                    String os_version,String accept_push,  final OnRegisAcceptPush listener) {
        RequestParams requestParams = new RequestParams();
        requestParams.add("accept_push", accept_push);

        NetworkUtil.post(Constants.BASE_URL + Constants.API_URL_REGISTER_ACCPET_PUSH, requestParams,bundle_id, device_id, push_token,
            os_type, os_version, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                        boolean success = false;
                        try {
                            String str = new String(responseBody);
                            if (str.contains("200") || str.contains("Register Success")) {
                                success = true;
                            }
                        } catch (Exception ex) {
                            DebugLog.e(ex.toString());
                        }

                        if (listener != null) {
                            listener.onAccept(success);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                        if (listener != null) {
                            listener.onAccept(false);
                        }
                    }
        });
    }

    /**
     * Retrieve Status Push
     * @param bundle_id
     * @param device_id
     * @param push_token
     * @param os_type
     * @param os_version
     * @param listener
     */
    public static void retrieveStatusPush(String bundle_id, String device_id, String push_token, String os_type,
                                          String os_version, final OnRetrieveStatusPush listener) {
        RequestParams requestParams = new RequestParams();
        requestParams.add("accept_push", "");

        NetworkUtil.post(Constants.BASE_URL + Constants.API_URL_RETRIEVE_ACCPET_PUSH, requestParams,bundle_id, device_id, push_token,
            os_type, os_version, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                boolean success = false;
                JSONObject obj = null;
                try {
                    String str = new String(responseBody);
                    obj = new JSONObject(str);
                    if (str.contains("200") || str.contains("Get status push success")) {
                        success = true;
                    }
                } catch (Exception ex) {
                    DebugLog.e(ex.toString());
                }

                if (listener != null) {
                    try {
                        listener.onRetrieveStatus(success, obj.getString("accept_push"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                if (listener != null) {
                    listener.onRetrieveStatus(false, "1");
                }
            }
        });
    }

    /**
     * Update status when read message
     * @param bundle_id
     * @param device_id
     * @param push_token
     * @param os_type
     * @param os_version
     * @param u_push_id
     * @param listener
     */
    public static void updateRecordStatus(String bundle_id, String device_id, String push_token, String os_type,
                                          String os_version, String u_push_id, final OnUpdateRecordStatus listener) {
        RequestParams requestParams = new RequestParams();
        requestParams.add("u_push_done_id", u_push_id);

        NetworkUtil.post(Constants.BASE_URL + Constants.API_URL_UPDATE_RECORD_STATUS, requestParams,bundle_id, device_id, push_token,
            os_type, os_version, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                boolean success = false;
                try {
                    String str = new String(responseBody);
                    if (str.contains("200")) {
                        success = true;
                    }
                } catch (Exception ex) {
                    DebugLog.e(ex.toString());
                }

                if (listener != null) {
                    listener.onUpdateRecord(success);
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                if (listener != null) {
                    listener.onUpdateRecord(false);
                }
            }
        });
    }

    public static void loadContentMessage(String bundle_id, String device_id, String push_token, String os_type,
                                          String os_version, String u_push_id, final OnLoadContent listener) {
        RequestParams requestParams = new RequestParams();
        requestParams.add("u_push_done_id", u_push_id);

        NetworkUtil.post(Constants.BASE_URL + Constants.API_URL_LOAD_CONTENT, requestParams,bundle_id, device_id, push_token,
                os_type, os_version, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                        boolean success = false;
                        JSONObject obj = null;
                        try {
                            String str = new String(responseBody);
                            if (str.contains("200")) {
                                obj = new JSONObject(str);
                                success = true;
                            }
                        } catch (Exception ex) {
                            DebugLog.e(ex.toString());
                        }

                        if (listener != null && obj != null) {
                            try {
                                listener.onLoadContent(success, obj.getString("data_message"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else {
                            if (listener != null) {
                                listener.onLoadContent(false,"");
                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                        if (listener != null) {
                            listener.onLoadContent(false,"");
                        }
                    }
                });
    }

    /**
     * Get all data not push from server
     * @param bundle_id
     * @param device_id
     * @param push_token
     * @param os_type
     * @param os_version
     */
    public static void syncMessages(String bundle_id, String device_id, String push_token, String os_type,
                                          String os_version, String page, final OnSyncMessages listener) {
        RequestParams requestParams = new RequestParams();
        requestParams.add("page", page);

        NetworkUtil.post(Constants.BASE_URL + Constants.API_SYNC_MESSAGES, requestParams,bundle_id, device_id, push_token,
        os_type, os_version, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                boolean success = false;
                JSONObject obj = null;
                try {
                    String str = new String(responseBody);
                    if (str.contains("200")) {
                        obj = new JSONObject(str);
                        success = true;
                        if (listener != null) {
                            try {
                                listener.onSyncMessages(success, obj.getString("data"), obj.getString("deleted_ids"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }else {
                        listener.onSyncMessages(success,"","");
                    }
                } catch (Exception ex) {
                    DebugLog.e(ex.toString());
                }

            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                if (listener != null) {
                    listener.onSyncMessages(false,"","");
                }
            }
        });
    }

    public static void countBadge(String bundle_id, String device_id, String push_token, String os_type,
                                    String os_version, String limit_badge, final OnGetBadgeCount listener) {
        RequestParams requestParams = new RequestParams();
        requestParams.add("limit_badge", limit_badge);

        NetworkUtil.post(Constants.BASE_URL + Constants.API_COUNT_BADGE, requestParams,bundle_id, device_id, push_token,
        os_type, os_version, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                boolean success = false;
                JSONObject obj = null;
                try {
                    String str = new String(responseBody);
                    if (str.contains("200")) {
                        obj = new JSONObject(str);
                        success = true;
                        if (listener != null) {
                            try {
                                listener.onGetBadgeCount(success, obj.getString("badge"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }else {
                        listener.onGetBadgeCount(success,"");
                    }
                } catch (Exception ex) {
                    DebugLog.e(ex.toString());
                }

            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                if (listener != null) {
                    listener.onGetBadgeCount(false,"");
                }
            }
        });
    }
}
