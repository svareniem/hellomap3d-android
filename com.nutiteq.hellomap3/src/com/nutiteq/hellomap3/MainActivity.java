package com.nutiteq.hellomap3;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.nutiteq.core.MapPos;
import com.nutiteq.core.MapRange;
import com.nutiteq.datasources.HTTPVectorTileDataSource;
import com.nutiteq.datasources.UnculledVectorDataSource;
import com.nutiteq.datasources.VectorTileDataSource;
import com.nutiteq.layers.VectorLayer;
import com.nutiteq.layers.VectorTileLayer;
import com.nutiteq.projections.EPSG3857;
import com.nutiteq.styles.MarkerStyle;
import com.nutiteq.styles.MarkerStyleBuilder;
import com.nutiteq.ui.MapView;
import com.nutiteq.utils.AssetUtils;
import com.nutiteq.utils.BitmapUtils;
import com.nutiteq.vectorelements.Marker;
import com.nutiteq.vectortiles.MBVectorTileDecoder;
import com.nutiteq.vectortiles.MBVectorTileStyleSet;
import com.nutiteq.wrappedcommons.UnsignedCharVector;

public class MainActivity extends Activity {

    private static final String TAG = "3dmap";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // 1. Basic map setup
        // Create map view 
        MapView mapView = (MapView) this.findViewById(R.id.map_view);
        com.nutiteq.utils.Log.SetShowDebug(true);
        com.nutiteq.utils.Log.SetShowInfo(true);
        
        // Set the base projection, that will be used for most MapView, MapEventListener and Options methods
        EPSG3857 proj = new EPSG3857();
        mapView.getOptions().setBaseProjection(proj);
        
        // Set initial location and other parameters, don't animate
//        mapView.setFocusPos(proj.fromWgs84(new MapPos(24.650415, 59.420773)), 0); // tallinn
        mapView.setFocusPos(proj.fromWgs84(new MapPos(19.04468, 47.4965)), 0); // budapest
        mapView.setZoom(6, 0);
        mapView.setMapRotation(0, 0);
        mapView.setTilt(90, 0);
        
        // General options
        mapView.getOptions().setTileDrawSize(256);

        UnsignedCharVector styleBytes = AssetUtils.loadBytes("osmbright.zip");
        if(styleBytes != null){
            MBVectorTileStyleSet vectorTileStyleSet = new MBVectorTileStyleSet(styleBytes);
            MBVectorTileDecoder vectorTileDecoder = new MBVectorTileDecoder(
                    vectorTileStyleSet);
//             VectorTileDataSource vectorTileDataSource = new
//             HTTPVectorTileDataSource(0, 14,
//             "http://a.tiles.mapbox.com/v4/mapbox.mapbox-streets-v5/{zoom}/{x}/{y}.vector.pbf?access_token=pk.eyJ1IjoibnV0aXRlcSIsImEiOiJVQnF2Wk9jIn0.EKSkygY5CkVp-I8byaquBQ");
            VectorTileDataSource vectorTileDataSource = new HTTPVectorTileDataSource(
                    0, 14,
                    "http://api.nutiteq.com/v1/nutiteq.mbstreets/{zoom}/{x}/{y}.vt?user_key=15cd9131072d6df68b8a54feda5b0496"
                    );

            VectorTileLayer baseLayer = new VectorTileLayer(
                    vectorTileDataSource, vectorTileDecoder);
            mapView.getLayers().add(baseLayer);
        }else
        {
            Log.e(TAG, "vector style not found");
        }
        
        
        // 2. Add a pin marker to map
        // Initialize an unculled vector data source
        UnculledVectorDataSource vectorDataSource1 = new UnculledVectorDataSource(proj);
        // Initialize a vector layer with the previous data source
        VectorLayer vectorLayer1 = new VectorLayer(vectorDataSource1);
        // Add the previous vector layer to the map
        mapView.getLayers().add(vectorLayer1);
        // Set visible zoom range for the vector layer
        vectorLayer1.setVisibleZoomRange(new MapRange(0, 24));
        
        Bitmap androidMarkerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.marker);
        com.nutiteq.graphics.Bitmap markerBitmap = BitmapUtils.CreateBitmapFromAndroidBitmap(androidMarkerBitmap);
        
        // Create marker style
        MarkerStyleBuilder markerStyleBuilder = new MarkerStyleBuilder();
        markerStyleBuilder.setBitmap(markerBitmap);
        //markerStyleBuilder.setHideIfOverlapped(false);
        markerStyleBuilder.setSize(30);
        MarkerStyle sharedMarkerStyle = markerStyleBuilder.buildStyle();
        // Add marker
        Marker marker1 = new Marker(proj.fromWgs84(new MapPos(19.04468, 47.4965)), sharedMarkerStyle);
        vectorDataSource1.add(marker1);
    }
}