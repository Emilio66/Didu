package com.reflection.didu.didu.map;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.app.Activity;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.SupportMapFragment;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.reflection.didu.didu.R;
import com.reflection.didu.didu.news.NewsActivity;
import com.reflection.didu.didu.setting.SettingActivity;
import com.baidu.location.BDLocation;
//import com.baidu.mapapi.overlayutil.PoiOverlay;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.GroundOverlayOptions;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;

import com.reflection.didu.didu.R;
import com.reflection.didu.didu.news.NewsActivity;
import com.reflection.didu.didu.setting.SettingActivity;


import java.util.ArrayList;
import java.util.List;


public class MapActivity extends Activity implements
        View.OnClickListener,DialogComplete.TimeUpListener {
    private ImageView bnSetting;
    private ImageView bnNews;
    private ImageView bnLocate;
    private ImageView bnDrawing;

    /**
     * MapView 是地图主控件
     */
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private UiSettings mUiSettings;

    /*location 相关*/
    LocationClient mLocClient;
    public MyLocationListenner myListener1 = new MyLocationListenner();
    private MyLocationConfiguration.LocationMode mCurrentMode;
    private LatLng mylocll;
    BitmapDescriptor mCurrentMarker;

    private List<LatLng> path =null; //drawing path
    private List<LatLng> path_record =null; //record path

    boolean isFirstLoc = true; // 是否首次定位
    private static final int accuracyCircleFillColor = 0xAAFFFF88;
    private static final int accuracyCircleStrokeColor = 0xAA00FF00;

    boolean WantDraw = true;
    double max_latitude, max_longitude,min_latitude,min_longitude;

    //搜索相关
    private PoiSearch mPoiSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.content_map);
        initUI();
        //search init


        // 地图初始化
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        mUiSettings = mBaiduMap.getUiSettings();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.showMapPoi(false);
        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener1);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();
        mCurrentMarker = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_coordinate1);
        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
        mBaiduMap
                .setMyLocationConfigeration(new MyLocationConfiguration(
                        mCurrentMode, true, mCurrentMarker));

        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(14.0f);
        mBaiduMap.setMapStatus(msu);

        //init poi search
        OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener(){
            public void onGetPoiResult(PoiResult result){
                //获取POI检索结果
            }
            public void onGetPoiDetailResult(PoiDetailResult result){
                //获取Place详情页检索结果
            }
        };

        mPoiSearch =  PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(poiListener);

        mPoiSearch.searchInCity((new PoiCitySearchOption())
                .city("北京")
                .keyword("美食")
                .pageNum(10));

    }



    private void initUI(){
        bnSetting = (ImageView)findViewById(R.id.bn_setting);
        bnNews = (ImageView)findViewById(R.id.bn_news);
        bnLocate = (ImageView)findViewById(R.id.bn_locate);
        bnDrawing = (ImageView)findViewById(R.id.bn_drawing);

        bnSetting.setOnClickListener(this);
        bnNews.setOnClickListener(this);
        bnLocate.setOnClickListener(this);
        bnDrawing.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bn_setting:
                setting();
                break;
            case R.id.bn_news:
                news();
                break;
            case R.id.bn_locate:
                locate();
                break;
            case R.id.bn_drawing:
                drawing();
                break;
            default:
                break;
        }
    }

    //setting按钮
    private void setting(){
        Intent intent = new Intent(MapActivity.this, SettingActivity.class);
        startActivity(intent);
    }


    //new按钮
    private void news(){
        Intent intent = new Intent(MapActivity.this, NewsActivity.class);
        startActivity(intent);
    }


    //locate按钮
    private void locate(){
        MapStatusUpdate statusUpdate = MapStatusUpdateFactory.newLatLng(mylocll);
        mBaiduMap.animateMapStatus(statusUpdate);
    }


    //drawing按钮
    private void drawing(){
        if(WantDraw == true) {
            // start draw
            WantDraw = false;
            mUiSettings.setAllGesturesEnabled(WantDraw);
            start_draw_set();

        } else {
            //stop draw
            WantDraw = true;
            mUiSettings.setAllGesturesEnabled(WantDraw);
            stop_draw_set();

        }
    }
    private void startAlert(){
        DialogComplete complete = new DialogComplete();
        Bundle bundle = new Bundle();
        bundle.putString("test","娴嬭瘯娴嬭瘯娴嬭瘯");
        complete.setArguments(bundle);
        complete.show(getFragmentManager(),"test");
    }


    @Override
    public void onTimeUp() {
        startAlert();
    }


    //拿到一个多边形的外切矩形，用于第一步判断
    private void get_max_min(List<LatLng> path) {
        int path_size = path.size();
        max_latitude = 0;
        min_latitude = 1000;
        max_longitude = 0;
        min_longitude = 1000;
        for (int i = 0; i < path_size; i++) {
            LatLng p = path.get(i);
            if (max_longitude < p.longitude)
                max_longitude = p.longitude;
            if (max_latitude < p.latitude)
                max_latitude = p.latitude;
            if (min_longitude > p.longitude)
                min_longitude = p.longitude;
            if (min_latitude > p.latitude)
                min_latitude = p.latitude;
        }
    }

    //判断一个点是否在多边形内部
    private boolean point_in_poly(LatLng point, List<LatLng> path1, int path_size){
        int nCross = 0;
        get_max_min(path1);
        Log.d("DIDU", "min la "+min_latitude+ " min lo" + min_longitude
                +" max la "+ max_latitude + " max lo " +max_longitude);

        //get_max_min(path1);
        if(point.latitude > max_latitude || point.latitude < min_latitude
                || point.longitude > max_longitude || point.longitude < min_longitude)
            return false;

        for (int i = 0; i < path_size; i++) {
            LatLng p1 = path1.get(i);
            LatLng p2 = path1.get((i + 1) % path_size);
            // 求解 y=p.latitude 与 p1 p2 的交点
            // p1p2 与 y=p0.latitude平行
            if (p1.latitude == p2.latitude)
                continue;
            // 交点在p1p2延长线上
            if (point.latitude < Math.min(p1.latitude, p2.latitude))
                continue;
            // 交点在p1p2延长线上
            if (point.latitude >= Math.max(p1.latitude, p2.latitude))
                continue;
            // 求交点的 X 坐标
            double x = (double) (point.latitude - p1.latitude) * (double) (p2.latitude - p1.latitude)
                    / (double) (p2.latitude - p1.latitude) + p1.longitude;
            // 只统计单边交点
            if (x > point.longitude)
                nCross++;
        }
        return (nCross % 2 == 1);
    }



    //在开始画图前修改对于地图的触摸事件的处理
    private void start_draw_set() {
        mBaiduMap.setOnMapClickListener(null);
        //捕捉用户动作，开始记录触摸路线
        mBaiduMap.setOnMapTouchListener(new BaiduMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent ev) {
                //new an list to save all the points
                LatLng pos=null;
                if(ev.getAction() == MotionEvent.ACTION_DOWN){
                    path = new ArrayList<>();//redrawing
                    pos = mBaiduMap.getProjection().fromScreenLocation(new Point((int)ev.getX(),(int)ev.getY()));
                    path.add(pos);
                    Log.d("DIDU"," Action down,First Path " + +path.size()+" "+path.toString());
                }
                //手指移动，画出历史轨迹
                else if(ev.getAction() == MotionEvent.ACTION_MOVE) {
                    //get every point in real-time, transformed to latitude, longitude
                    pos = mBaiduMap.getProjection().fromScreenLocation(new Point((int) ev.getX(), (int) ev.getY()));
                    path.add(pos);
                    OverlayOptions ooPolyline = new PolylineOptions().points(path).color(0xAA00FF00);
                    mBaiduMap.addOverlay(ooPolyline);
                }
                //手指抬起，
                else if(ev.getAction() == MotionEvent.ACTION_UP) {
                    pos = mBaiduMap.getProjection().fromScreenLocation(new Point((int) ev.getX(), (int) ev.getY()));
                    path.add(pos);

                    if (path.size() > 3) {
                        OverlayOptions ooPolygon = new PolygonOptions().points(path)
                                .stroke(new Stroke(5, 0xAA00FF00)).fillColor(0xAAFFFF00);
                        mBaiduMap.addOverlay(ooPolygon);
                        Log.d("DIDU", " up drawing polygon");
                        path_record = path;
                        Log.d("DIDU", "path_record "+path_record.toString());

                        SystemClock.sleep(1000);
                        //弹出文本框，并且让用户输入提醒的内容
                        DialogInput input = new DialogInput();
                        input.show(getFragmentManager(),"test");



                    } else {
                        //当用户不小心画了一条线，并不构成图时，我们就只显示这条线
                        OverlayOptions ooPolyline = new PolylineOptions().width(10)
                                .color(0xAAFF0000).points(path);
                        mBaiduMap.addOverlay(ooPolyline);
                        Log.d("DIDU", "  up drawing polyline");
                    }

                }

            }

        });

    }

    //在结束画图后修改对于地图的触摸事件的处理和点击事件的处理
    private void stop_draw_set() {

        //获得用户点击的位置，将其转化为经纬度，判断是否在多边形内
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                //经纬度
                Toast.makeText(getApplicationContext(),latLng.toString(),Toast.LENGTH_LONG).show();
                boolean click_point_in_poly = point_in_poly(latLng, path_record,path_record.size());
                Log.d("DIDU", "path_record "+path_record.toString());
                Log.d("DIDU"," --- IS point in circle "+ click_point_in_poly );
                if(click_point_in_poly){
                    //弹出文本框，显示提醒内容
                    SystemClock.sleep(1000);
                    //弹出文本框，并且让用户输入提醒的内容
                    DialogComplete complete = new DialogComplete();
                    Bundle bundle = new Bundle();
                    bundle.putString("test","测试测试测试");
                    complete.setArguments(bundle);
                    complete.show(getFragmentManager(),"test");


                }
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });
        mBaiduMap.setOnMapTouchListener(null);
    }




    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                mylocll = ll;
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }


    /*
    //下面是android的默认对于界面的函数
    @Override
    protected void onPause() {
        // MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        // MapView的生命周期与Activity同步，当activity恢复时需调用MapView.onResume()
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        // MapView的生命周期与Activity同步，当activity销毁时需调用MapView.destroy()
        mMapView.onDestroy();
        super.onDestroy();
        // 回收 bitmap 资源

    }
*/



}


