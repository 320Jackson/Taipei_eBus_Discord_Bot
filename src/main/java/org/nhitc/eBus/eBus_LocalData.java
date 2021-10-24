package org.nhitc.eBus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class eBus_LocalData {
    private static eBus_LocalData Singleton; /*Singleton class instance.*/

    /*Private constructor.*/
    private eBus_LocalData() {
    }

    /*Get class instance.*/
    public static eBus_LocalData getInstance() {
        if(Singleton == null) {
            Singleton = new eBus_LocalData();
        }
        return Singleton;
    }

    JSONObject SearchTarget(String TargetID, String JsonContent, String Target) {
        /*Get route info json array.*/
        JSONArray RouteArr = new JSONArray(JsonContent);
        JSONObject Output = null;
        
        /*Linear search.*/
        for(int Run = 0; Run < RouteArr.length(); Run++) {
            JSONObject RouteItem = RouteArr.getJSONObject(Run);
            String RouteItemID = RouteItem.getString(Target);
            if(RouteItemID.equals(TargetID)) {
                Output = RouteItem;
            }
        }
        return Output;
    }

    private String Read_JSONFile(String str_FilePath) throws Exception {
        String str_Content = ""; /*File content.*/
        
        /*Generate file reader.*/
        File TargetFile = new File(str_FilePath);
        FileReader FileStream = new FileReader(TargetFile);
        BufferedReader BufferStream = new BufferedReader(FileStream);
        /*Read data content.*/
        while(BufferStream.ready() == true) {
            str_Content += BufferStream.readLine();
        }
        BufferStream.close();

        return str_Content;
    }

    public JSONObject get_RouteInfo(String RouteUID) throws Exception {
        /*Get route's city and its ID.*/
        String str_City = RouteUID.substring(0, 3);
        String RouteID = RouteUID.substring(3);
        
        String str_JsonContent = Read_JSONFile(String.format("./Local_Database/RouteInfo/%s_Route.json", str_City));
        JSONObject RouteInfo = SearchTarget(RouteID, str_JsonContent, "RouteID");
        return RouteInfo;
    }

    public ArrayList<JSONObject> get_RouteMap(String Region, String RouteName) throws Exception {
        /*Get route maps.(Go and back.)*/
        ArrayList<JSONObject> RouteMaps = new ArrayList<>();
        JSONArray RouteMap_DataArr = new JSONArray(Read_JSONFile(String.format("./Local_Database/RouteMap/%s_RouteMap.json", Region)));
        
        /*Search route.*/
        for(int Index = 0; Index < RouteMap_DataArr.length(); Index++) {
            JSONObject MapItem = RouteMap_DataArr.getJSONObject(Index);
            if(MapItem.getJSONObject("RouteName").getString("Zh_tw").equals(RouteName)) {
                RouteMaps.add(MapItem);
            }
        }
        return RouteMaps;
    }
}
