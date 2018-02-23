package hotelokura.jalhotels.oneharmony.activity.map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import hotelokura.jalhotels.oneharmony.MainApplication;
import hotelokura.jalhotels.oneharmony.R;
import hotelokura.jalhotels.oneharmony.activity.web.WebViewActivity;
import hotelokura.jalhotels.oneharmony.analytics.CheckAction;
import hotelokura.jalhotels.oneharmony.setting.CsvLine;
import hotelokura.jalhotels.oneharmony.setting.MapSetting;
import hotelokura.jalhotels.oneharmony.util.LogUtil;
import hotelokura.jalhotels.oneharmony.lib.actionscontentview.ActionsContentView;
import hotelokura.jalhotels.oneharmony.lib.slidinguppanel.SlidingUpPanelLayout;

public class MapActivity extends FragmentActivity
        implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        MyListFragment.OnArticleSelectedListener {


    //設定（カスタマイズ用）
    static final LatLng DEFAULT_LOCATION = new LatLng(35.681382, 139.766084);
    static final float DEFAULT_ZOOM = 5.0f;
    static final float SHOW_INFO_ZOOM = 12.0f;
    static final int MOVE_DURATION = 3000;
    static final int ZOOM_WAIT_TIME = 2000;

    //定数
    static final String TAG = "MapActivity";
    static final String TAG_MAP_FRAGMENT = "MAP_FRAGMENT";

    //共通
    private Context mContext = null;
    private boolean lowPowerMoreImportantThanAccuracy = false;
    private Handler mHandler = new Handler();

    //店舗データ
    private ArrayList<MapSetting> mMapSettingArray = new ArrayList<MapSetting>();

    //地図表示
    private SupportMapFragment mMapFragment = null;
    private GoogleMap mMap = null;
    private RelativeLayout mMapRoot = null;
    private Location mCurrentLocation = null;
    private boolean mFirstMovedFlag = false;
    private boolean mReservMoveCurrentLocFlag = false;
    private LocationClient mLocationClient = null;
    private ImageView mTransImg = null;
    private Boolean mIsDownMap = false;

    //スライドアップ
    private SlidingUpPanelLayout mSlideUpRootView;
    private ListView mSlideUpListView = null;
    private InfoListAdapter mInfoListAdapter = null;
    private InfoListAdapter mStoreListAdapter = null;

    //サイドメニュー
    private ActionsContentView viewActionsContentView;
    private ListView mSideMenuListView = null;
    private MenuAdapter mMenuAdapter = null;
    private String preSelectedName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogUtil.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_map);
        mContext = getApplicationContext();
    }

    @Override
    protected void onResume() {
        LogUtil.d(TAG, "onResume()");
        super.onResume();

        if (mMapRoot == null) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    RelativeLayout rootView = (RelativeLayout) findViewById(R.id.root);
                    rootView.addView(getLayoutInflater().inflate(R.layout.view_map_main, null));

                    mMapRoot = (RelativeLayout) findViewById(R.id.content);
                    mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    mTransImg = (ImageView) findViewById(R.id.transparent_img);

                    String filter_id = getIntent().getStringExtra("filter_id");
                    if (filter_id != null) {
                        //ストアリストからマップ設定のリストを作成
                        makeMapSettings(MapSetting.findId(
                                MainApplication.getInstance().getStorelistCsvArray(), filter_id));
                    } else {
                        LogUtil.e(TAG, "filter_id is NULL");
                    }
                    initSideMenu(getIntent().getBooleanExtra("list", false));
                    initSlideUp();
                    registMap();
                    setButtonsOnMap();
                }
            }, 50);
        }

        if (mLocationClient != null && !mLocationClient.isConnected()
                && !mLocationClient.isConnecting()) {
            mLocationClient.connect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mLocationClient != null) {
            mLocationClient.disconnect();
            mLocationClient = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mLocationClient != null) {
            mLocationClient.disconnect();
            mLocationClient = null;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (mSlideUpRootView != null) {
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                if (!MainApplication.getInstance().isTabletDevice())
                    mSlideUpRootView.setSlideRate(1.0f);
            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                if (!MainApplication.getInstance().isTabletDevice())
                    mSlideUpRootView.setSlideRate(0.5f);
            }
        }

        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return true;
    }


    private void initCamera() {
        if (mMap == null || mFirstMovedFlag) return;

        mFirstMovedFlag = true;

        // デフォルトの位置を表示
        mMap.moveCamera(CameraUpdateFactory
                .newCameraPosition(new CameraPosition.Builder()
                        .target(DEFAULT_LOCATION)
                        .zoom(DEFAULT_ZOOM).build()));

        if (mMapSettingArray != null && mMapSettingArray.size() > 0) {
            // このルートでは地図初期化完了とみなし、現在地への移動は行わない
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mMapSettingArray.size() == 1) {
                        MapSetting mapSetting = mMapSettingArray.get(0);
                        moveCamera(new LatLng(mapSetting.getLatitude(), mapSetting.getLongtitude()));
                    } else {
                        // ピンが立っている場合はできるだけ多くのピンが画面に収まるよう調整
                        ArrayList<LatLng> locations = new ArrayList<LatLng>();
                        for (MapSetting mapSetting : mMapSettingArray) {
                            locations.add(new LatLng(mapSetting.getLatitude(), mapSetting.getLongtitude()));
                        }
                        moveCameraAllInclude(locations);

                    }
                }
            }, ZOOM_WAIT_TIME);
        } else {
            //ピンに移動できなかったため、現在地への移動を予約する
            mReservMoveCurrentLocFlag = true;
        }
    }

    /**
     * カメラを東京駅に移動する
     *
     * @param location アニメーション移動するかの判定。true でアニメーション移動。
     */
    private void moveCamera(LatLng location) {
        // カメラの位置情報を作成する
        CameraUpdate camera = CameraUpdateFactory
                .newCameraPosition(new CameraPosition.Builder()
                        .target(location)
                        .zoom(SHOW_INFO_ZOOM).build());

        // アニメーション移動する
        mMap.animateCamera(camera, MOVE_DURATION, null);
    }

    private void moveCameraAllInclude(ArrayList<LatLng> locations) {
        LatLngBounds.Builder b = new LatLngBounds.Builder();
        for (LatLng loc : locations) {
            b.include(loc);
        }
        LatLngBounds bounds = b.build();
        CameraUpdate camera = CameraUpdateFactory.
                newLatLngBounds(bounds,
                        (int) (getResources().getDisplayMetrics().widthPixels * 0.8),
                        (int) (getResources().getDisplayMetrics().heightPixels * 0.7),
                        MainApplication.getInstance().dp2px(5));

        mMap.animateCamera(camera, MOVE_DURATION, null);
    }

    @Override
    public void onConnected(Bundle bundle) {
        LogUtil.d(TAG, "onConnected");

        if (mLocationClient != null) {
            final Location loc = mLocationClient.getLastLocation();
            if (loc != null) {
                //現在地が取得できた場合は
                mCurrentLocation = loc;
                if (mReservMoveCurrentLocFlag) {
                    mReservMoveCurrentLocFlag = false;
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            moveCamera(new LatLng(loc.getLatitude(), loc.getLongitude()));
                        }
                    }, ZOOM_WAIT_TIME);
                }
            }
        }

        //現在地が取得できていない場合は現在地の変化を監視する
        if (mMap != null) {
            mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location loc) {
                    if (loc != null) {
                        mCurrentLocation = loc;
                        if (mReservMoveCurrentLocFlag) {
                            mReservMoveCurrentLocFlag = false;
                            moveCamera(new LatLng(loc.getLatitude(), loc.getLongitude()));
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onDisconnected() {
        LogUtil.d(TAG, "onDisconnected");
        showToast(getString(R.string.map_toast_getloc_unavailable), false);
        if (mLocationClient != null) {
            mLocationClient.disconnect();
            mLocationClient = null;
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        LogUtil.d(TAG, "onConnectionFailed");
        showToast(getString(R.string.map_toast_getloc_fail), false);
        if (mLocationClient != null) {
            mLocationClient.disconnect();
            mLocationClient = null;
        }
    }

    private void registMap() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext);

        if (resultCode == ConnectionResult.SUCCESS) {
            mLocationClient = new LocationClient(this, this, this);
            mLocationClient.connect();
        } else {
            showToast(getString(R.string.map_toast_unavailable), false);
            this.finish();
        }
        setupMap();
    }

    private void setupMap() {
        if (mMap == null) {
            // MapFragment から GoogleMap を取得する
            mMap = mMapFragment.getMap();
            if (mMap != null) {
                // マップをハイブリッド表示にする
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                // 屋内マップ表示を無効にする（標準は true）
                mMap.setIndoorEnabled(false);

                // 現在地表示ボタンを有効にする
                mMap.setMyLocationEnabled(true);

                // 拡大縮小ボタンを無効にする
                mMap.getUiSettings().setZoomControlsEnabled(false);

                // 現在地表示ボタンを無効にする（自前で設置するため）
                mMap.getUiSettings().setMyLocationButtonEnabled(false);

                // コンパスを無効にする
                mMap.getUiSettings().setCompassEnabled(false);

                //初期表示位置を設定
                initCamera();

                //地図タップでサイドメニュー、スライドを閉じる
                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        hideSlideUp();
                        hideSideMenu();
                    }
                });

                //ピンを立てる
                addPins();

                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    static final String TAG = "OnMarkerClickListener";

                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        markerClick(marker);
                        return false;
                    }
                });

                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        markerClick(marker);
                    }
                });
            } else {
                LogUtil.e(TAG, "getMap() fail");
            }
        }
    }

    private void markerClick(Marker marker) {
        if (mMapSettingArray != null) {
            for (MapSetting mapSetting : mMapSettingArray) {
                if (marker.getTitle().equals(mapSetting.getName())) {
                    showStoreInfo(mapSetting);
                    return;
                }
            }
        }
    }

    private void makeMapSettings(ArrayList<CsvLine> storeList) {
        if (storeList != null) {
            for (CsvLine stor : storeList) {
                mMapSettingArray.add(new MapSetting(stor));
            }
        }
    }

    private void addPins() {
        if (mMap != null && mMapSettingArray != null) {
            for (MapSetting mapSetting : mMapSettingArray) {
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(mapSetting.getLatitude(), mapSetting.getLongtitude()))
                        .title(mapSetting.getName())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
            }
        } else {
            LogUtil.d(TAG, "addPins : invalid param");
        }
    }

    private void setButtonsOnMap() {
        if (mMapRoot == null) {
            LogUtil.d(TAG, "setButtonsOnMap : invalid param");
            return;
        }

        mMapRoot.addView(getLayoutInflater().inflate(R.layout.view_map_overlay, null));

        ImageView toggleMenu = (ImageView) findViewById(R.id.button_menu);
        toggleMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSlideUp();
                toggleSlideMenu();
            }
        });

        ImageView toggleList = (ImageView) findViewById(R.id.button_list);
        toggleList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSideMenu();
                showAroundStore();
            }
        });

        ImageView buttonMyLocation = (ImageView) findViewById(R.id.button_mylocation);
        buttonMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mReservMoveCurrentLocFlag = false;
                if (mCurrentLocation != null) {
                    // アニメーション移動する
                    moveCamera(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));
                } else {
                    showToast(getString(R.string.map_toast_getloc_now), true);
                    if (mLocationClient != null) {
                        LocationRequest request = LocationRequest.create();
                        request.setInterval(10000);
                        request.setPriority(lowPowerMoreImportantThanAccuracy ?
                                LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY :
                                LocationRequest.PRIORITY_HIGH_ACCURACY);
                        mLocationClient.requestLocationUpdates(request, new LocationListener() {
                            @Override
                            public void onLocationChanged(Location location) {
                                if (location != null) {
                                    mCurrentLocation = location;
                                    moveCamera(new LatLng(location.getLatitude(), location.getLongitude()));
                                }
                            }
                        });
                    } else {
                        mReservMoveCurrentLocFlag = true;
                        registMap();
                    }
                }
            }
        });

        EditText searchInput = (EditText) findViewById(R.id.search_input);
        searchInput.setHint(R.string.map_search);
        searchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
                try {
                    List<Address> addressList = geocoder.getFromLocationName(v.getText().toString(), 1);
                    if (addressList.isEmpty()) {
                        showToast(getString(R.string.map_toast_no_address), false);
                    } else {
                        hideSlideUp();

                        Address address = addressList.get(0);
                        if (address != null) {
                            LatLng location = new LatLng(address.getLatitude(), address.getLongitude());
                            //ピンを初期化
                            mMap.clear();
                            mMap = null;
                            setupMap();

                            // アニメーション移動する
                            moveCamera(location);

                            // ピンを立てる
                            mMap.addMarker(new MarkerOptions()
                                    .position(location)
                                    .title(v.getText().toString())
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin)));

                        }
                    }
                } catch (IOException e) {
                    LogUtil.e(TAG, e.getMessage());
                    showToast(getString(R.string.map_toast_addr_search_fail), false);
                }
                return false;
            }

        });

    }

    private void initSideMenu(Boolean isList) {
        /* サイドメニュー全体の設定 */
        viewActionsContentView = (ActionsContentView) findViewById(R.id.actionsContentView);
        viewActionsContentView.setSwipingType(ActionsContentView.SWIPING_EDGE);
        viewActionsContentView.setActionsSpacingWidth(0);
        viewActionsContentView.setFadeType(ActionsContentView.FADE_NONE);
        if (isList) {
            viewActionsContentView.setSpacingWidth(0);
            viewActionsContentView.showActions();
            viewActionsContentView.setOnActionsContentListener(new ActionsContentView.OnActionsContentListener() {
                @Override
                public void onContentStateChanged(ActionsContentView v, boolean isContentShown) {
                    // メニュー非表示完了
                    if (isContentShown) {
                        // 余白が0の場合、次回以降は64dpの余白で表示
                        if (viewActionsContentView.getSpacingWidth() == 0) {
                            viewActionsContentView.setSpacingWidth(MainApplication.getInstance().dp2px(64));
                        }
                    }
                }
            });

        }

        /* メニュー部分の設定 */
        mSideMenuListView = (ListView) findViewById(R.id.menu);
        mMenuAdapter = new MenuAdapter(mContext);
        mSideMenuListView.setAdapter(mMenuAdapter);
        mSideMenuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                    long flags) {
                switch (position) {
                    case 0:
                        finish();
                        break;
                    case 1:
                        toggleSlideMenu();
                        break;
                    case 2:
                        toggleSlideMenu();
                        showAroundStore();
                        break;
                    default:
                        break;
                }

            }
        });

        /* リスト部分の設定 */
        setStoreListFragment(0, 0, null);
    }

    public void onListItemClick(int depth, int position, String selectedName) {
        setStoreListFragment(depth, position, selectedName);
    }

    private void setStoreListFragment(int depth, int position, String selectedName) {

        String filter_id = getIntent().getStringExtra("filter_id");
        if (filter_id != null && selectedName != null) {
            MainApplication app = (MainApplication) this.getApplication();
            CheckAction ca = app.getCheckAction();
            ca.sendAppTrackFromMapList(filter_id, selectedName);
        }

        FragmentManager fm = getSupportFragmentManager();

        //２階層目以降で先頭がタップされた場合は前の階層に戻る
        if (depth > 1 && position == 0) {
            fm.popBackStack();
            return;
        }

        //初回表示以外は選択された名前が店舗名に存在しないかチェック
        if (depth != 0) {
            for (MapSetting setting : mMapSettingArray) {
                if (setting.getName().equals(selectedName)) {
                    //店舗名が一致したら店舗情報を表示
                    toggleSlideMenu();
                    showStoreInfo(setting);
                    return;
                }
            }
        }

        //次画面のリストを作成する
        ArrayList<String> data = new ArrayList<String>();
        int index = 0;

        if (depth > 0 && index == 0) {
            //２階層目以降は前の階層に戻る項目を先頭に追加
            String backText;
            if (depth == 1) {
                backText = getString(R.string.map_storelist_back_to_top);
            } else {
                backText = preSelectedName + getString(R.string.map_storelist_back);
            }
            if (depth == 1 || index > 0) {
                preSelectedName = selectedName;
            }
            data.add(backText);
            index++;
        }

        for (MapSetting setting : mMapSettingArray) {
            //リスト一覧の作成
            String[] categorys = setting.getCategorys();
            if (categorys != null && categorys.length >= depth) {
                if (depth == 0) {
                    if (categorys.length == depth) {
                        data.add(index, setting.getName());
                        index++;
                    } else {
                        if (!data.contains(categorys[depth])) {
//                            Log.e(TAG,"ss: "+categorys[depth]+"index: "+index+"depth: "+depth);
                            data.add(index, categorys[depth]);
                            index++;
                        }
                    }
                } else {
                    if (categorys[depth - 1].equals(selectedName)) {
                        if (categorys.length == depth) {
//                            Log.e(TAG, "getName: " + setting.getName()+"  selectedName:"+selectedName);
                            data.add(setting.getName());
                            index++;
                        } else {
                            if (!data.contains(categorys[depth])) {
                                data.add(categorys[depth]);
                                index++;
                            }
                        }
                    }
                }
            }
        }

        //作成したリストをFragmentにセットしてコミット
        Fragment newFragment = MyListFragment.newInstance(data.toArray(new String[data.size()]), depth + 1);
        FragmentTransaction ft = fm.beginTransaction();

        if (depth == 0) {
            ft.add(R.id.side_menu, newFragment, TAG_MAP_FRAGMENT);
        } else {
            ft.setCustomAnimations(
                    R.anim.fragment_slide_right_enter,
                    R.anim.fragment_slide_left_exit,
                    R.anim.fragment_slide_left_enter,
                    R.anim.fragment_slide_right_exit);
            // Layout位置先の指定
            ft.replace(R.id.side_menu, newFragment, TAG_MAP_FRAGMENT);
            ft.addToBackStack(String.valueOf(depth));
        }
        ft.commit();
    }

    private void toggleSlideMenu() {
        if (viewActionsContentView.isActionsShown()) {
            viewActionsContentView.showContent();
        } else {
            viewActionsContentView.showActions();
        }
    }

    private void hideSideMenu() {
        if (viewActionsContentView.isActionsShown()) {
            viewActionsContentView.showContent();
        }
    }

    private void initSlideUp() {
        mSlideUpRootView = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        mSlideUpRootView.setPanelHeight(0);
        mSlideUpRootView.setDrawScrim(false);
        mSlideUpRootView.setDragView(findViewById(R.id.slideup_title));
        mSlideUpRootView.setShadowDrawable(getResources().getDrawable(R.drawable.above_shadow));
    }

    private void showStoreInfo(MapSetting setting) {
        //店舗名表示
        TextView name = (TextView) findViewById(R.id.name);
        name.setText(setting.getName());

        //住所、電話、その他
        final ArrayList<ListElement> elements = new ArrayList<ListElement>();
        ListElement element;

        if (setting.getUrl() != null) {
            element = new ListElement();
            element.setUrl(setting.getUrl());
            elements.add(element);
        }

        if (setting.getAddress() != null) {
            element = new ListElement();
            element.setTitle(getString(R.string.map_info_addr));
            element.setText(setting.getAddress());
            element.setButtonType(ListElement.MAP_BUTTON_TYPE_NAVI);
            elements.add(element);
        }

        if (setting.getTel() != null) {
            element = new ListElement();
            element.setTitle(getString(R.string.map_info_phone));
            element.setText(setting.getTel());
            element.setButtonType(ListElement.MAP_BUTTON_TYPE_CALL);
            element.setTel(setting.getTel());
            elements.add(element);
        }

        if (setting.getExt1() != null) {
            String[] param = setting.getExt1().split(":", 2);
            if (param != null && param.length == 2) {
                element = new ListElement();
                element.setTitle(param[0]);
                if (param[1].startsWith("http://") || param[1].startsWith("https://")) {
                    element.setLinkText(param[1]);
                } else {
                    element.setText(param[1]);
                }
                elements.add(element);
            }
        }

        if (setting.getExt2() != null) {
            String[] param = setting.getExt2().split(":", 2);
            if (param != null && param.length == 2) {
                element = new ListElement();
                element.setTitle(param[0]);
                if (param[1].startsWith("http://") || param[1].startsWith("https://")) {
                    element.setLinkText(param[1]);
                } else {
                    element.setText(param[1]);
                }
                elements.add(element);
            }
        }

        if (setting.getExt3() != null) {
            String[] param = setting.getExt3().split(":", 2);
            if (param != null && param.length == 2) {
                element = new ListElement();
                element.setTitle(param[0]);
                if (param[1].startsWith("http://") || param[1].startsWith("https://")) {
                    element.setLinkText(param[1]);
                } else {
                    element.setText(param[1]);
                }
                elements.add(element);
            }
        }

        if (mSlideUpListView == null) {
            mSlideUpListView = (ListView) findViewById(R.id.info);
        }

        //地図表示
        final Double selectedLatitude = setting.getLatitude();
        final Double selectedLongtitude = setting.getLongtitude();
        mInfoListAdapter = new InfoListAdapter(this, elements, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentLocation != null) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");

                    String saddr = mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude();
                    String daddr = selectedLatitude + "," + selectedLongtitude;

                    intent.setData(Uri.parse("http://maps.google.com/maps?saddr=" + saddr + "&daddr=" + daddr + "&dirflg=d"));
                    startActivity(intent);
                } else {
                    showToast(getString(R.string.map_toast_navi_unavailable), false);
                }
            }
        });
        mSlideUpListView.setAdapter(mInfoListAdapter);
        mSlideUpListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String text = elements.get(position).mLinkText;
                if (text != null && (text.startsWith("http://") || text.startsWith("https://"))) {
//                    Uri uri = Uri.parse(text);
//                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                    view.getContext().startActivity(intent);

                    Intent intent = new Intent(MapActivity.this, WebViewActivity.class);
                    intent.putExtra("StartUrl", text);
                    startActivity(intent);
                }
            }
        });

        // 地図表示を移動
        if (mMap != null) {
            //onMarkerClick()内で実行するとカメラ移動が無効化されるため別スレッドで実行する
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    switch (getResources().getConfiguration().orientation) {
                        case Configuration.ORIENTATION_PORTRAIT:
                            moveCamera(new LatLng(
                                    selectedLatitude - (0.004 * MainApplication.getInstance().getInch()),
                                    selectedLongtitude));
                            break;
                        case Configuration.ORIENTATION_LANDSCAPE:
                            moveCamera(new LatLng(
                                    selectedLatitude - (0.002 * MainApplication.getInstance().getInch()),
                                    selectedLongtitude));
                            break;
                    }
                }
            });
        }

        showSlideUp();
    }

    private void showAroundStore() {

        if (mCurrentLocation == null) {
            showToast(getString(R.string.map_toast_loc_search_unavailable), true);
            return;
        }

        //タイトル表示
        TextView name = (TextView) findViewById(R.id.name);
        name.setText(getString(R.string.map_loc_search_title));

        //店舗一覧表示
        final ArrayList<ListElement> elements = new ArrayList<ListElement>();
        ListElement element;

        //計算
        float[] result = {0, 0, 0};

        final Map<Float, Integer> tm = new TreeMap<Float, Integer>();
        int mapIndex = 0;
        for (MapSetting setting : mMapSettingArray) {
            Location.distanceBetween(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(),
                    setting.getLatitude(), setting.getLongtitude(), result);
            tm.put(result[0], mapIndex);
            mapIndex++;
        }
        Set<Map.Entry<Float, Integer>> entrySet = tm.entrySet();
        int cnt = 0;
        for (Map.Entry<Float, Integer> entry : entrySet) {
            element = new ListElement();
            element.setTitle(mMapSettingArray.get(entry.getValue()).getName());
            element.setDistance(entry.getKey());
            elements.add(element);
            if (cnt < 10) {
                cnt++;
            } else {
                break;
            }
        }

        if (mSlideUpListView == null) {
            mSlideUpListView = (ListView) findViewById(R.id.info);
        }
        mStoreListAdapter = new InfoListAdapter(this, elements, null);
        mSlideUpListView.setAdapter(mStoreListAdapter);
        mSlideUpListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showStoreInfo(mMapSettingArray.get(tm.get(elements.get(position).mDistance)));
            }
        });

        showSlideUp();
    }

    public void showSlideUp() {
        if (!mSlideUpRootView.isExpanded()) {
            mSlideUpRootView.expandPane();
        }
    }

    public void hideSlideUp() {
        if (mSlideUpRootView.isExpanded()) {
            mSlideUpRootView.collapsePane();
        }
    }

    private void showToast(String msg, boolean isLong) {
        Toast.makeText(mContext, msg, isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
    }

    public static void startActivity(Activity activity, String id, Boolean showList) {
        startActivity(activity, id, showList, -1);
    }

    public static void startActivity(Activity activity, String id, Boolean showList, int flag) {
        Intent intent = new Intent(activity, MapActivity.class);
        if (id != null) intent.putExtra("filter_id", id);
        intent.putExtra("list", showList);
        if (flag > 0) intent.setFlags(flag);
        activity.startActivity(intent);
    }
}
