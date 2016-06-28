package com.reflection.didu.didu.map;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.Log;

import com.baidu.mapapi.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Created by zhaoz on 2016/6/27.
 * json 对象格式[ timestamp: {content: "..",status: "", path=[,]}, {content, ..}]
 * 1. 新建对象
 * 2. 插入数据
 * 3. 搜索数据 //json搜索比较麻烦，如果用到建议用数据库，sql select
 * 4. 根据状态删除 true/false
 * 5. 文件读写
 */

/*class Latlng {
    float lat;
    float lng;

    public Latlng(float lat, float lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLng() {
        return lng;
    }

    public void setLng(float lng) {
        this.lng = lng;
    }
}*/

public class JsonUtil {
    private JSONArray history;
    private Context context;
    public JsonUtil(Context context){
        this.context = context;
    }
    //read file and construct json object
    public void init(){
        File file = new File(context.getFilesDir(),"data.json");
        FileReader reader = null;

        try {
            //if file exists, read it, otherwise, create a new one
            if ( !file.createNewFile() ) {
                reader = new FileReader(file);
                BufferedReader breader = new BufferedReader(reader);
                StringBuffer buffer = new StringBuffer();
                String string = null;
                while ((string = breader.readLine()) != null)
                    buffer.append(string);

                //System.out.println("Read file: \n " + buffer);
                if(buffer.length() > 2) {
                    JSONTokener tokener = new JSONTokener(buffer.toString());
                    history = (JSONArray) tokener.nextValue();
                    Log.d("DIDU","file exists in"+file.getAbsolutePath());
                    Log.i("DIDU","history: "+history.toString());
                }else
                    history = new JSONArray();
                //System.out.println("constructed json: \n " + history);

                breader.close();
            }
            else{
                history  = new JSONArray();
                Log.i("DIDU","no such file, create one");
                fsync();
            }
            Log.i("DIDU"," file path； "+file.getAbsolutePath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void clear() throws IOException {
        File file = new File(context.getFilesDir(), "data.json");
        FileWriter writer = new FileWriter(file);
        writer.write("");
        Log.i("DIDU", "Clear file");
        writer.flush();
        writer.close();
    }
    //write records to disk
    public void fsync(){
        FileWriter writer = null;
        try {
            if(history != null) {
                File file = new File(context.getFilesDir(), "data.json");
                writer = new FileWriter(file);
                writer.write(history.toString());
                Log.i("DIDU", "write " + history);
                writer.flush();
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //List<LatLng> -> json string
    public static String list2string(List<LatLng> pos) {
        JSONArray path = new JSONArray(); //一条路径多个点
        JSONObject point;
        for (LatLng p : pos){
                point = new JSONObject();
            try {
                point.put("lat", p.latitude).put("lng", p.longitude);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            path.put(point);
        }
        return path.toString();
    }

    //插入提醒和点list, 坐标待更改
    public void insert(String content, String pos) throws JSONException {
        if (history == null)
            init();
        JSONObject record = new JSONObject();
        record.put("id", System.currentTimeMillis());
        record.put("content",content);
        record.put("status",true);      //有效提醒

        //一条路径多个点
        JSONTokener tokener = new JSONTokener(pos);
        JSONArray path = (JSONArray)tokener.nextValue();
        Log.i("DIDU", "path: "+path);
        /*JSONObject point = new JSONObject();
        for (LatLng p : pos){
            point.put("lat", p.latitude).put("lng", p.longitude);
            path.put(point);
        }
*/
        record.put("path",path);    //add path
        history.put(record);
        //write to disk
        fsync();
        Log.i("DIDU","insert record : " + record);
    }


    //设置标志位为false
    @TargetApi(19)
    public void delete(long id) throws JSONException {
        if (history == null)
            init();
        for (int i= 0; i< history.length(); i++){
            JSONObject rec = history.getJSONObject(i);
            if (rec.optLong("id", System.currentTimeMillis()) == id) {
                rec.put("status",false);
                history.remove(i); //only api 19 provided
                history.put(rec);
                fsync();
                Log.i("DIDU","delete record : " + rec);
                return ;
            }
        }
    }

    //返回路径圈，可以是list格式
    public JSONArray getPath(long id) throws JSONException {
        if (history == null)
            init();
        JSONArray route = null;
        for (int i= 0; i< history.length(); i++){
            JSONObject rec = history.getJSONObject(i);
            if (rec.optLong("id", System.currentTimeMillis()) == id) {
                route = rec.getJSONArray("path");
                break;
            }
            System.out.println(route);
            Log.i("DIDU","get path : " + route);
        }
        return route;
    }

    //查询
    public JSONObject find(long id) throws JSONException {
        if (history == null)
            init();
        JSONObject rec = null;
        boolean isFind =false;
        for (int i= 0; i< history.length(); i++){
            rec = history.getJSONObject(i);
            if (rec.optLong("id", System.currentTimeMillis()) == id)
                isFind = true;
        }
        Log.i("DIDU","find record : " + id+"  : "+isFind);
        return isFind ? rec : null;
    }

    public JSONArray create() throws JSONException {
        JSONArray records = new JSONArray();

        JSONObject record = new JSONObject();
        record.put("id", System.currentTimeMillis());
        record.put("content","提醒我去华联买牛奶");
        record.put("status",true);      //有效提醒

        JSONArray path = new JSONArray(); //一条路径多个点
        JSONObject point = new JSONObject();
        point.put("lat",123.11).put("lng",112.33);
        path.put(point);
        point = new JSONObject();
        point.put("lat",153.11).put("lng",172.33);
        path.put(point);
        point = new JSONObject();
        path.put(point);
        point.put("lat",113.11).put("lng",112.2223);

        record.put("path",path);    //add path

        records.put(record);

        record = new JSONObject();
        record.put("id", System.currentTimeMillis());
        record.put("content","RUNNING FOR A MOMENT");
        record.put("status",true);      //有效提醒

        path = new JSONArray(); //一条路径多个点
        point = new JSONObject();
        point.put("lat",12).put("lng",33);
        path.put(point);
        point = new JSONObject();
        point.put("lat",44).put("lng",172.33);
        path.put(point);
        point = new JSONObject();
        path.put(point);
        point.put("lat",657).put("lng",43);

        record.put("path",path);    //add path

        records.put(record);

        //another record
        record = new JSONObject();
        record.put("id", System.currentTimeMillis());
        record.put("content","提醒我去华联买牛奶");
        record.put("status",true);      //有效提醒

        path = new JSONArray(); //一条路径多个点
        point = new JSONObject();
        point.put("lat",123.11).put("lng",112.33);
        path.put(point);
        point = new JSONObject();
        point.put("lat",153.11).put("lng",172.33);
        path.put(point);
        point = new JSONObject();
        path.put(point);
        point.put("lat",113.11).put("lng",112.2223);

        record.put("path",path);    //add path

        records.put(record);
        //System.out.println(records);
        history = records;
        return  records;
    }


    //文件不大，以静态变量存在内存当中完全可以
    //文件读写

    public static void main(String[] args) throws JSONException {
        /*init();
        System.out.println(history);
        List<Latlng> list = new ArrayList<Latlng>();

        list.add(new Latlng(112.23f, 3.33f));
        list.add(new Latlng(132.22f, 32.11f));
        long time = System.currentTimeMillis();
        insert("我想去游泳",list);

        System.out.println(history);

        System.out.println("FInd: "+time+" "+ find(time));

        delete(time);
        System.out.println("delete last one"+ history);


        System.out.println(getPath(1467023519280L));
       JSONArray records = create();

       File file = new File("data.json");
        try {
            FileWriter writer = new FileWriter(file,true);
            writer.write(records.toString());
            writer.flush();
            writer.close();


        } catch (IOException e) {
            e.printStackTrace();
        }*/

        //read file and construct json object

        //insert data

        //delete data

    }

}
