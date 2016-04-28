package com.coco_sh.ctao;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.coco_sh.ctao.base.BaseActivity;
import com.coco_sh.ctao.route.RouteActivity;
import com.coco_sh.ctao.utils.AMapUtil;
import com.coco_sh.ctao.utils.ToastUtil;

import butterknife.Bind;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements
        AMapLocationListener ,GeocodeSearch.OnGeocodeSearchListener {


    @Bind(R.id.but)
    Button button;
    private double dLongitude,dLatitude;
    private String mProvince;

    private TextView tvReult;
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;
    //地理编码
    private GeocodeSearch geocoderSearch;
    private String addressName;
    private AMap aMap;
    @Override
    protected int getBodyLayoutResId() {
        return R.layout.content_main;
    }

    @Override
    protected int getMenuLayoutResId() {
        return 0;
    }

    @Override
    public void onEventMainThread(Object o) {

    }

    @Override
    protected void init() {
        mCenterTitleTxt.setText("路线定位导航");
        showView(mCenterTitleTxt);
        tvReult = (TextView) findViewById(R.id.tv_result);
        locationClient = new AMapLocationClient(this.getApplicationContext());
        locationOption = new AMapLocationClientOption();
        // 设置定位模式为低功耗模式
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        // 设置定位监听
        locationClient.setLocationListener(this);

//定位--
        locationOption.setOnceLocation(true);
        initOption();
        // 设置定位参数
        locationClient.setLocationOption(locationOption);
        // 启动定位
        locationClient.startLocation();
        mHandler.sendEmptyMessage(Utils.MSG_LOCATION_START);
//地理编码
        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);
        Button geoButton = (Button) findViewById(R.id.geoButton);
        geoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLatlon("中山公园");
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != locationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
    }
    @OnClick({R.id.but})
    @Override
    protected void onClickView(View v) {
        if (v.getId()==R.id.but){
//            Bundle bundle=new Bundle();
//            bundle.putDouble("dLongitude",dLongitude);
//            bundle.putDouble("dLatitude",dLatitude);
//            bundle.putString("mProvince",mProvince);
//            bundle.putString("mFlag","mFlag1");
//            startAty(bundle, RouteActivity.class,false);
//            Log.i("TAG", dLongitude+" === "+dLatitude);

            Intent intent=new Intent(MainActivity.this,RouteActivity.class);
            intent.putExtra("dLongitude",dLongitude);
            intent.putExtra("dLatitude",dLatitude);
            intent.putExtra("mProvince",mProvince);
            intent.putExtra("mProvince",mProvince);
            startActivity(intent);

        }

    }

    // 根据控件的选择，重新设置定位参数
    private void initOption() {
        // 设置是否需要显示地址信息
//        locationOption.setNeedAddress(cbAddress.isChecked());
        locationOption.setNeedAddress(true);
        String strInterval = "1000";
        if (!TextUtils.isEmpty(strInterval)) {
            /**
             *  设置发送定位请求的时间间隔,最小值为1000，如果小于1000，按照1000算
             *  只有持续定位设置定位间隔才有效，单次定位无效
             */
            locationOption.setInterval(Long.valueOf(strInterval));
        }

    }

    Handler mHandler = new Handler() {
        public void dispatchMessage(Message msg) {
            switch (msg.what) {
                case Utils.MSG_LOCATION_START:
                    tvReult.setText("正在定位...");
                    break;
                //定位完成
                case Utils.MSG_LOCATION_FINISH:
                    AMapLocation loc = (AMapLocation) msg.obj;
                    String result = Utils.getLocationStr(loc);
                    tvReult.setText(result);
                    //自己设置----------------
                    if (loc.getErrorCode() == 0) {
                        dLongitude=loc.getLongitude();
                        dLatitude=loc.getLatitude();
                        //显示定位的城市loc.getProvince()与loc.getCityCode()皆可
                        mProvince=loc.getProvince();//省（市）
//                        mProvince=loc.getCityCode(); //城市编码
                        Log.i("TAG", "经    度    : " + loc.getLatitude() + "纬    度    : " + loc.getLongitude());
                    } else {
                        Toast.makeText(MainActivity.this, "网络连接定位失败", Toast.LENGTH_SHORT).show();
                        Log.i("TAG", "网络连接定位失败");
                    }
                    break;
                case Utils.MSG_LOCATION_STOP:
                    tvReult.setText("定位停止");
                    break;
                default:
                    break;
            }
        }

        ;
    };

    // 定位监听
    @Override
    public void onLocationChanged(AMapLocation loc) {
        if (null != loc) {
            Message msg = mHandler.obtainMessage();
            msg.obj = loc;
            msg.what = Utils.MSG_LOCATION_FINISH;
            mHandler.sendMessage(msg);
        }
    }

    /**
     * 响应地理编码
     */
    public void getLatlon(final String name) {
//        showDialog();
        GeocodeQuery query = new GeocodeQuery(name, "021");// 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
        geocoderSearch.getFromLocationNameAsyn(query);// 设置同步地理编码请求
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {

    }

    @Override
    public void onGeocodeSearched(GeocodeResult result, int rCode) {
//        dismissDialog();
        if (rCode == 1000) {
            if (result != null && result.getGeocodeAddressList() != null
                    && result.getGeocodeAddressList().size() > 0) {
                GeocodeAddress address = result.getGeocodeAddressList().get(0);
                addressName = "经纬度值:" + address.getLatLonPoint() +
                              "\n位置描述:" + address.getFormatAddress();

                Log.i("TAG", "{经} "+address.getLatLonPoint().getLatitude()+"{纬} "+address.getLatLonPoint().getLongitude() );
                ToastUtil.show(MainActivity.this, addressName);
            } else {
                ToastUtil.show(MainActivity.this, R.string.no_result);
            }

        } else {
            ToastUtil.showerror(MainActivity.this, rCode);
        }

    }


}
