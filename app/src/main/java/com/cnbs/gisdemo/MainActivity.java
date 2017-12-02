package com.cnbs.gisdemo;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.TileCache;
import com.esri.arcgisruntime.geometry.CoordinateFormatter;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.ArcGISTiledLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.IdentifyGraphicsOverlayResult;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;
import com.esri.arcgisruntime.util.ListenableList;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.cnbs.gisdemo.R.id.mapView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private MapView mMapView;
    private TextView showTV;
    private GraphicsOverlay pointGraphicOverlay;
    private GraphicsOverlay lineGraphicOverlay;
    private Graphic pointGraphic0;
    private Graphic pointGraphic1;
    private Graphic pointGraphic2;
    private Graphic lineGraphic0;
    private Graphic lineGraphic1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        showTV = (TextView) findViewById(R.id.show_tv);
        mMapView = (MapView) findViewById(mapView);
        Utils utils = new Utils();
        //tpk--缓存显示
        TileCache tileCache = new TileCache(utils.Save_Path + "/" + utils.File_name);
        final ArcGISTiledLayer tiledLayer = new ArcGISTiledLayer(tileCache);
        tiledLayer.setMinScale(4000);  //控制缩小，数值越大，缩小倍数越大，看的范围越广
        tiledLayer.setMaxScale(1000);   //控制放大，数值越小，放大倍数越高
        Basemap basemap = new Basemap(tiledLayer);
        final ArcGISMap mArcGISMap = new ArcGISMap(basemap);

        //添加点击事件
        MapViewTouchListener mMapViewTouchListener = new MapViewTouchListener(this, mMapView);
        mMapView.setOnTouchListener(mMapViewTouchListener);
        //添加覆盖物
        addGraphicsOverlay();

        mMapView.setMap(mArcGISMap);
//        Point point1 = new Point(12724178.110558, 3573932.336934, SpatialReferences.getWebMercator());
        Point pointGeometry1 = CoordinateFormatter.fromLatitudeLongitude("30.5459N 114.3035E", null);
        mMapView.setViewpointCenterAsync(pointGeometry1, 3800);
        //添加缩放控件
        mMapView.setCanMagnifierPanMap(true);
        mMapView.setMagnifierEnabled(true);
    }

    class MapViewTouchListener extends DefaultMapViewOnTouchListener {

        public MapViewTouchListener(Context context, MapView mapView) {
            super(context, mapView);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            // get the screen point where user tapped
            android.graphics.Point screenPoint = new android.graphics.Point((int) e.getX(), (int) e.getY());
            final ListenableFuture<List<IdentifyGraphicsOverlayResult>> overlaysAsync = mMapView.identifyGraphicsOverlaysAsync(screenPoint, 15.0, false, 2);
            overlaysAsync.addDoneListener(new Runnable() {
                @Override
                public void run() {
                    try {
                        List<IdentifyGraphicsOverlayResult> overlayResultList = overlaysAsync.get();
                        if (!overlayResultList.isEmpty() && overlayResultList.size() >= 0) {
                            //如果只取第一个点到的，就不用for循环了，直接get(0)；如果需要取所有点击到的覆盖物，就可以用for循环
                            int size = overlayResultList.size();
                            if (size > 1) {
                                showTV.setText("你点到了 --" + size + "个元素");
                            }else {
                                showTV.setText("GISDemo");
                            }
//                            for (int i = 0; i < size; i++) {
                                List<Graphic> graphics = overlayResultList.get(0).getGraphics();
//                                List<Graphic> graphics = overlayResultList.get(i).getGraphics();
                                if (!graphics.isEmpty() && graphics.size() >= 0) {
                                    String s = "";
                                    Graphic graphic = graphics.get(0);//取点击的第一个
                                    if (graphic == pointGraphic0) {
                                        s = "点0";
                                    }
                                    if (graphic == pointGraphic1) {
                                        s = "点1";
                                    }
                                    if (graphic == pointGraphic2) {
                                        s = "点2";
                                    }
                                    if (graphic == lineGraphic0) {
                                        s = "线0";
                                    }
                                    if (graphic == lineGraphic1) {
                                        s = "线1";
                                    }
                                    Graphic graphic0 = graphics.get(0);
                                    // show a toast message if graphic was returned
                                    Toast.makeText(getApplicationContext(), "你点到了 --" + s, Toast.LENGTH_SHORT).show();
                                }
//                            }
                        }

                    } catch (InterruptedException | ExecutionException ie) {
                        ie.printStackTrace();
                    }
                }
            });
            return super.onSingleTapConfirmed(e);
        }

    }

//-0-------------------------------------------------------------------------

    /**
     * 绘制点和线
     */
    private void addGraphicsOverlay() {
//-----------画点-------------
        // point graphic
//        Point pointXY0 = new Point(12724154.362253, 3573937.672715, SpatialReferences.getWebMercator());
//        Point pointXY1 = new Point(12724178.110558, 3573928.336934, SpatialReferences.getWebMercator());
//        Point pointXY2 = new Point(12724205.001669, 3573832.994711, SpatialReferences.getWebMercator());
        Point pointGeometry0 = CoordinateFormatter.fromLatitudeLongitude("30.5469N 114.3036E", null);
        Point pointGeometry1 = CoordinateFormatter.fromLatitudeLongitude("30.5459N 114.3035E", null);
        Point pointGeometry2 = CoordinateFormatter.fromLatitudeLongitude("30.5449N 114.3034E", null);
        // create graphic for point
        pointGraphic0 = new Graphic(pointGeometry0);
        pointGraphic1 = new Graphic(pointGeometry1);
        pointGraphic2 = new Graphic(pointGeometry2);
        // create a graphic overlay for the point
        pointGraphicOverlay = new GraphicsOverlay();
        BitmapDrawable drawable = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_map_red);
//        BitmapDrawable drawable = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_map_blue);
        final PictureMarkerSymbol pointSymbol = new PictureMarkerSymbol(drawable);
        // create simple renderer
        SimpleRenderer pointRenderer = new SimpleRenderer(pointSymbol);
        pointGraphicOverlay.setRenderer(pointRenderer);
        // add graphic to overlay
        pointGraphicOverlay.getGraphics().add(pointGraphic0);
        pointGraphicOverlay.getGraphics().add(pointGraphic1);
        pointGraphicOverlay.getGraphics().add(pointGraphic2);

//--------------画线---------------
        PointCollection borderCAtoNV0 = new PointCollection(SpatialReferences.getWgs84());
        PointCollection borderCAtoNV1 = new PointCollection(SpatialReferences.getWgs84());
        borderCAtoNV0.add(pointGeometry0);
        borderCAtoNV0.add(pointGeometry1);
        borderCAtoNV1.add(pointGeometry1);
        borderCAtoNV1.add(pointGeometry2);
        Polyline polyline0 = new Polyline(borderCAtoNV0);
        Polyline polyline1 = new Polyline(borderCAtoNV1);
        SimpleLineSymbol lineSymbol0 = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, R.color.colorAccent, 2);
        SimpleLineSymbol lineSymbol1 = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, R.color.colorPrimary, 2);
        SimpleRenderer lineRenderer0 = new SimpleRenderer(lineSymbol0);
        SimpleRenderer lineRenderer1 = new SimpleRenderer(lineSymbol1);
        lineGraphic0 = new Graphic(polyline0, lineSymbol0);
        lineGraphic1 = new Graphic(polyline1, lineSymbol1);
        lineGraphicOverlay = new GraphicsOverlay();
        lineGraphicOverlay.setRenderer(lineRenderer0);
        lineGraphicOverlay.setRenderer(lineRenderer1);
        lineGraphicOverlay.getGraphics().add(lineGraphic0);
        lineGraphicOverlay.getGraphics().add(lineGraphic1);

        // add graphics overlay to the MapView
        mMapView.getGraphicsOverlays().add(lineGraphicOverlay);
        mMapView.getGraphicsOverlays().add(pointGraphicOverlay);
//------------------闪烁---------------------
        handler.postDelayed(runnable, 1000);
    }

    private boolean isShow = true;
    private int recLen = 600;//设置有效时间
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            isShow = !isShow;
            recLen--;
            handler.postDelayed(runnable, 1000);
            //显示和消失错开
            if(isShow){
                ListenableList<GraphicsOverlay> list = mMapView.getGraphicsOverlays();
                for (GraphicsOverlay overlay : list) {
                    ListenableList<Graphic> graphics = overlay.getGraphics();
                    for (Graphic graphic : graphics) {
                        graphic.setVisible(true);
                    }
                }
            }else {
                int index = recLen % 5;
                //顺序是4,3,2,1,0
                switch (index){
                    case 4:
                        pointGraphic0.setVisible(false);
                        break;
                    case 3:
                        lineGraphic0.setVisible(false);
                        break;
                    case 2:
                        pointGraphic1.setVisible(false);
                        break;
                    case 1:
                        lineGraphic1.setVisible(false);
                        break;
                    case 0:
                        pointGraphic2.setVisible(false);
                        break;
                }
            }
            if (recLen <= 0) {
                recLen = 60;//重置
                Message message = new Message();
                message.what = 1;
                handlerStop.sendMessage(message);
            }
        }
    };

    final Handler handlerStop = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    handler.removeCallbacks(runnable);
                    ListenableList<GraphicsOverlay> list = mMapView.getGraphicsOverlays();
                    for (GraphicsOverlay overlay : list) {
                        ListenableList<Graphic> graphics = overlay.getGraphics();
                        for (Graphic graphic : graphics) {
                            graphic.setVisible(true);
                        }
                    }
                    break;
            }
            super.handleMessage(msg);
        }

    };

    @Override
    protected void onPause() {
        mMapView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}
