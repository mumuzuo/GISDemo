# GISDemo

 GIS地图学习笔记二之Android开发 http://blog.csdn.net/m0_37168878/article/details/78695473


## 1、简单使用
 
**1、在项目的build.gradle文件中，添加以下代码**

```
allprojects {
    repositories {
        jcenter()
        // Add the Esri public Bintray Maven repository
        maven {
            url 'https://esri.bintray.com/arcgis'
        }
    }
}
```
**2、在app Module的build.gradle文件中，添加以下代码**
```
 //arcgis-android
 compile 'com.esri.arcgisruntime:arcgis-android:100.1.0'
```

**3、在项目的清单文件`AndroidManifest.xml `中，添加以下权限**

```
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-feature android:glEsVersion="0x00020000" android:required="true" />
```
**uses-feature说明:**
> AndroidManifest中的uses-feature配置用来声明一个app在运行时所依赖的外部的硬件或软件特征（feature），uses-feature还提供了一个required属性配置，表示此项依赖的软硬件特征是否是必须的，当它设置为true表示此app运行时必须使用此项特征，如果没有则无法工作，如果它设置为false，表示应用在运行时需要用到这些特征，但如果没有，应用可能会有一部分功能会受到影响，但大部分功能还是可以正常工作。例如一个拍照app，它使用时必须开启设备的摄像头，在没有摄像头的机器上任何功能都无法使用，这就需要通过uses-feature来声明该应用需要摄像头，并将required设置为true。再比如一个支付app，它支持扫码支付的功能，这项功能同样需要开启设备的摄像头，因此需要通过uses-feature声明该应用需要摄像头，但如果一个设备没有摄像头，仅意味着扫码支付的功能无法使用，其他支付方式仍然可以使用，这时就可以设置required属性为false，表明此项feature的需求不是必须的。

**布局文件**

```
    <com.esri.arcgisruntime.mapping.view.MapView
        android:id="@+id/mapView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_below="@+id/main_header" />
```

**代码中获取GIS地图信息**

```
mMapView = (MapView) findViewById(R.id.mapView);
ArcGISMap map = new ArcGISMap(Basemap.Type.TOPOGRAPHIC, 34.056295, -117.195800, 16); 
mMapView.setMap(map);
```
**注意加上下面的代码**

```
@Override 
protected void onPause(){
  mMapView.pause();
  super.onPause();
}

@Override 
protected void onResume(){
  super.onResume();
  mMapView.resume();
}
```
**到这里里的程序应该已经可以显示出Gis地图了。**
  ----------
## 2、离线地图加载

### 2-1、tpk-离线图层包加载

手机中的离线地图碎片位置`/data/data/com.cnbs.gisdemo/arcgis/GisTest.tpk`
![这里写图片描述](http://img.blog.csdn.net/20171130182026900?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvbTBfMzcxNjg4Nzg=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)
```
mMapView = (MapView) findViewById(R.id.mapView);
Utils utils = new Utils();
//tpk--缓存显示
TileCache tileCache = new TileCache(utils.Save_Path  + "/" +  utils.File_name);
ArcGISTiledLayer tiledLayer = new ArcGISTiledLayer(tileCache);
tiledLayer.setMinScale(8000);
tiledLayer.setMaxScale(1600);
Basemap basemap = new Basemap(tiledLayer);
ArcGISMap map = new ArcGISMap(basemap);
mMapView.setMap(map);
```

----------
### 2-2、mmpk-离线地图包加载

```
private void loadMobileMapPackage(String mmpkFile){
        mapPackage = new MobileMapPackage(mmpkFile);
        mapPackage.loadAsync();
        mapPackage.addDoneLoadingListener(new Runnable() {
            @Override
            public void run() {
                if(mapPackage.getLoadStatus() == LoadStatus.LOADED && mapPackage.getMaps().size() > 0){
                    mMapView.setMap(mapPackage.getMaps().get(0));
                }else{
                    // Log an issue if the mobile map package fails to load
                }
            }
        });
    }
```

 ----------
## 3、地图相关操作
###3-1、 在地图点击的位置绘制图标

```
 mMapView.setOnTouchListener(new DefaultMapViewOnTouchListener(this, mMapView) {
            @Override
            public boolean  onSingleTapConfirmed(MotionEvent v) {
                android.graphics.Point screenPoint=new android.graphics.Point(Math.round(v.getX()), Math.round(v.getY()));
                Point clickPoint = mMapView.screenToLocation(screenPoint);
                GraphicsOverlay graphicsOverlay_1=new GraphicsOverlay();
                //加个点
//                SimpleMarkerSymbol pointSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.DIAMOND, Color.RED, 10);
                //加个图标
                BitmapDrawable drawable = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_map_blue);
                PictureMarkerSymbol pointSymbol = new PictureMarkerSymbol(drawable);

                Graphic pointGraphic = new Graphic(clickPoint,pointSymbol);
                graphicsOverlay_1.getGraphics().add(pointGraphic);
                mMapView.getGraphicsOverlays().add(graphicsOverlay_1);
                return true;
            }
        });
```

点击事件对象`MotionEvent `

```
	MotionEvent {
	action=ACTION_UP, 
	id[0]=0, 
	x[0]=455.5782, 
	y[0]=1095.3904, 
	toolType[0]=TOOL_TYPE_FINGER, 
	buttonState=0, 
	metaState=0, 
	flags=0x0, 
	edgeFlags=0x0, 
	pointerCount=1, 
	historySize=0, 
	eventTime=351739375, 
	downTime=351739296, 
	deviceId=5, 
	source=0x1002 }
```

###3-2、 在地图上绘制点和线

- **用直角坐标系画**
```
private void addGraphicsOverlay() {
        // point graphic
        Point pointGeometry0 = new Point(12724154.362253, 3573937.672715, SpatialReferences.getWebMercator());
        Point pointGeometry1 = new Point(12724178.110558, 3573928.336934, SpatialReferences.getWebMercator());
        Point pointGeometry2 = new Point(12724205.001669, 3573832.994711, SpatialReferences.getWebMercator());
        // red diamond point symbol
//        SimpleMarkerSymbol pointSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.DIAMOND, Color.RED, 10);
        BitmapDrawable drawable = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_map_blue);
        PictureMarkerSymbol pointSymbol = new PictureMarkerSymbol(drawable);
        // create graphic for point
        Graphic pointGraphic0 = new Graphic(pointGeometry0);
        Graphic pointGraphic1 = new Graphic(pointGeometry1);
        Graphic pointGraphic2 = new Graphic(pointGeometry2);
        // create a graphic overlay for the point
        GraphicsOverlay pointGraphicOverlay = new GraphicsOverlay();
        // create simple renderer
        SimpleRenderer pointRenderer = new SimpleRenderer(pointSymbol);
        pointGraphicOverlay.setRenderer(pointRenderer);
        // add graphic to overlay
        pointGraphicOverlay.getGraphics().add(pointGraphic0);
        pointGraphicOverlay.getGraphics().add(pointGraphic1);
        pointGraphicOverlay.getGraphics().add(pointGraphic2);
        // add graphics overlay to the MapView
        mMapView.getGraphicsOverlays().add(pointGraphicOverlay);

        // line graphic
        PolylineBuilder lineGeometry = new PolylineBuilder(SpatialReferences.getWebMercator());
        lineGeometry.addPoint(12724178.110558, 3573928.336934);
        lineGeometry.addPoint(12724205.001669, 3573832.994711);
        // solid blue line symbol
        SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, R.color.colorAccent, 2);
        // create graphic for polyline
        Graphic lineGraphic = new Graphic(lineGeometry.toGeometry());
        // create graphic overlay for polyline
        GraphicsOverlay lineGraphicOverlay = new GraphicsOverlay();
        // create simple renderer
        SimpleRenderer lineRenderer = new SimpleRenderer(lineSymbol);
        // add graphic to overlay
        lineGraphicOverlay.setRenderer(lineRenderer);
        // add graphic to overlay
        lineGraphicOverlay.getGraphics().add(lineGraphic);
        // add graphics overlay to the MapView
        mMapView.getGraphicsOverlays().add(lineGraphicOverlay);
       
    }
```
- **用地理坐标系（经纬度）画**
和上面的是一样的，只是创建点的方式不一样

```
Point pointGeometry0 = CoordinateFormatter.fromLatitudeLongitude("30.5449N 114.3034E", null);
Point pointGeometry1 = CoordinateFormatter.fromLatitudeLongitude("30.5459N 114.3035E", null);
Point pointGeometry2 = CoordinateFormatter.fromLatitudeLongitude("30.5469N 114.3036E", null);
```

- **画线就有点不同了**

```
Point pointGeometry0 = CoordinateFormatter.fromLatitudeLongitude("30.5469N 114.3036E", null);
Point pointGeometry1 = CoordinateFormatter.fromLatitudeLongitude("30.5459N 114.3035E", null);
Point pointGeometry2 = CoordinateFormatter.fromLatitudeLongitude("30.5449N 114.3034E", null);
PointCollection borderCAtoNV = new PointCollection(SpatialReferences.getWgs84());
borderCAtoNV.add(pointGeometry0);
borderCAtoNV.add(pointGeometry1);
borderCAtoNV.add(pointGeometry2);
Polyline polyline = new Polyline(borderCAtoNV);

SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, R.color.colorAccent, 2);
SimpleRenderer lineRenderer = new SimpleRenderer(lineSymbol);
lineGraphic = new Graphic(polyline, lineSymbol);
lineGraphicOverlay = new GraphicsOverlay();
lineGraphicOverlay.setRenderer(lineRenderer);
lineGraphicOverlay.getGraphics().add(lineGraphic);
mMapView.getGraphicsOverlays().add(lineGraphicOverlay);
```


###3-3、 选中的要素闪烁

```
handler.postDelayed(runnable, 1000);
```

```
private boolean isShow;
    private int recLen = 5;//设置有效时间
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            isShow = !isShow;
            recLen--;
            handler.postDelayed(runnable, 1000);
            ListenableList<GraphicsOverlay> list = mMapView.getGraphicsOverlays();
            GraphicsOverlay overlay = list.get(1);
            overlay.setSelectionColor(R.color.colorPrimary);
            overlay.setVisible(isShow);
            if (recLen <= 0) {
                recLen = 5;//重置
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
                    GraphicsOverlay overlay = list.get(1);
                    overlay.setSelectionColor(R.color.colorAccent);
                    overlay.setVisible(true);
                    break;
            }
            super.handleMessage(msg);
        }

    };
```

```
 @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
```

###3-4、 设置地图中心点

```
  Point point1 = new Point(12724178.110558, 3573932.336934, SpatialReferences.getWebMercator());
        mMapView.setViewpointCenterAsync(point1,3800);
```


----------
### 3-5、切换图层
在Runtime100里，MapView是通过ArcGISMap类来完成图层的管理。

**首先是底图的加载**。ArcGISMap类是将底图和业务图层分开的，对于底图，ArcGISMap里用了Baemap类来进行管理。
例子就是上面的**4、离线地图加载**
```
手机中的离线地图碎片位置`/data/data/com.cnbs.gisdemo/arcgis/GisTest.tpk`
```

```
mMapView = (MapView) findViewById(R.id.mapView);
Utils utils = new Utils();
//tpk--缓存显示
TileCache tileCache = new TileCache(utils.Save_Path  + "/" +  utils.File_name);
ArcGISTiledLayer tiledLayer = new ArcGISTiledLayer(tileCache);
tiledLayer.setMinScale(8000);
tiledLayer.setMaxScale(1600);
Basemap basemap = new Basemap(tiledLayer);
ArcGISMap map = new ArcGISMap(basemap);
mMapView.setMap(map);
```
我们要切换底图时候，仅需要给ArcGISMap类重新赋值一个底图即可。

```
button.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Basemap basemap = new Basemap(layer);
        map.setBasemap(basemap);
        mMapView.setMap(arcGISMap);
    }
});
```

----------
### 3-6、地图要素点击事件

```
    //添加点击事件
        MapViewTouchListener mMapViewTouchListener = new MapViewTouchListener(this, mMapView);
        mMapView.setOnTouchListener(mMapViewTouchListener);
        //添加覆盖物
        addGraphicsOverlay();
```
```
      // 给覆盖物设置相关参数，可以把实体类的json字符串传过去
        Map<String, Object> map0 = new HashMap<>();
        Map<String, Object> map1 = new HashMap<>();
        Map<String, Object> map2 = new HashMap<>();
        map0.put("hint", "点0");
        map1.put("hint", "点1");
        map2.put("hint", "点2");
        pointGraphic0 = new Graphic(pointGeometry0, map0, pointSymbol0);
        pointGraphic1 = new Graphic(pointGeometry1, map1, pointSymbol1);
        pointGraphic2 = new Graphic(pointGeometry2, map2, pointSymbol0);
        ...
        Map<String, Object> mapl0 = new HashMap<>();
        Map<String, Object> mapl1 = new HashMap<>();
        mapl0.put("hint", "线0");
        mapl1.put("hint", "线1");
        lineGraphic0 = new Graphic(polyline0, mapl0, lineSymbol0);
        lineGraphic1 = new Graphic(polyline1, mapl1, lineSymbol1);
```

```
class MapViewTouchListener extends DefaultMapViewOnTouchListener {

        public MapViewTouchListener(Context context, MapView mapView) {
            super(context, mapView);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            // get the screen point where user tapped
            android.graphics.Point screenPoint = new android.graphics.Point((int) e.getX(), (int) e.getY());
            final ListenableFuture<List<IdentifyGraphicsOverlayResult>> overlaysAsync = mMapView.identifyGraphicsOverlaysAsync(screenPoint, 10.0, false, 2);
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
                             } else {
                                 showTV.setText("GISDemo");
                             }
//                            for (int i = 0; i < size; i++) {
                            List<Graphic> graphics = overlayResultList.get(0).getGraphics();
//                                List<Graphic> graphics = overlayResultList.get(i).getGraphics();
                            if (!graphics.isEmpty() && graphics.size() >= 0) {
                                Graphic graphic = graphics.get(0);//取点击的第一个
                                Map<String, Object> map = graphic.getAttributes();
                                String hint = (String) map.get("hint");
                                Toast.makeText(getApplicationContext(), "你点到了 - " + hint, Toast.LENGTH_SHORT).show();
                            }
                        }

                    } catch (InterruptedException | ExecutionException ie) {
                        ie.printStackTrace();
                    }
                }
            });
            return super.onSingleTapConfirmed(e);
        }

    }
```
