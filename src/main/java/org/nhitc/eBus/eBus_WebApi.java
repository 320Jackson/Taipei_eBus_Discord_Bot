package org.nhitc.eBus;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.nhitc.NetworkTask.Web_Crawler;

public class eBus_WebApi {
    private static Web_Crawler Downloader = new Web_Crawler(); /*Generate web crawler, get web api content.*/

    /*Get estimate arrival time every stop in specified route.*/
    public static ArrayList<String> get_RouteEstimateArrival(String RouteName) {
        ArrayList<String> Output = new ArrayList<String>();
        String str_City = "Taipei";
        JSONArray StopArrivalTime, BusLocationList;

        /*Get estimate arrive time.*/
        String Buffer = Downloader.Download_PTX(String.format("https://ptx.transportdata.tw/MOTC/v2/Bus/EstimatedTimeOfArrival/City/%s/%s?$orderby=StopID&$format=JSON", str_City, RouteName));
        if(Buffer.equals("[]null")) {
            str_City = "NewTaipei";
            Buffer = Downloader.Download_PTX(String.format("https://ptx.transportdata.tw/MOTC/v2/Bus/EstimatedTimeOfArrival/City/%s/%s?$orderby=StopID&$format=JSON", str_City, RouteName));
        }
        StopArrivalTime = new JSONArray(Buffer);

        /*Get bus location.*/
        Buffer = Downloader.Download_PTX(String.format("https://ptx.transportdata.tw/MOTC/v2/Bus/RealTimeNearStop/City/%s/%s?$orderby=StopID&$format=JSON", str_City, RouteName));
        BusLocationList = new JSONArray(Buffer);

        /*Get route station sequence.*/
        ArrayList<JSONObject> RouteMap_List = new ArrayList<JSONObject>();
        try {
            if(str_City.equals("Taipei")) {
                RouteMap_List = eBus_LocalData.getInstance().get_RouteMap("TPE", RouteName);
            }
            else {
                RouteMap_List = eBus_LocalData.getInstance().get_RouteMap("NWT", RouteName);
            }
        }
        catch (Exception Err) {
            System.out.println(Err.getMessage());
        }

        /*Get reply message.*/
        try {
            for(int Run = 0; Run < RouteMap_List.size(); Run++) {
                Output.add(get_OutputString(RouteMap_List.get(Run), StopArrivalTime, BusLocationList));
            }
        } 
        catch (Exception Err) {
            Output.add(Err.getMessage());
        }

        return Output;
    }

    private static String get_OutputString(JSONObject RouteMap, JSONArray EstimateArrival, JSONArray BusLocationList) throws Exception {
        String str_Output = "";

        String str_RouteUID = RouteMap.getString("RouteUID"); /*Get route UID.*/
        JSONObject RouteInfo = eBus_LocalData.getInstance().get_RouteInfo(str_RouteUID); /*Get route details.*/
        int int_Direction = RouteMap.getInt("Direction"); /*Get route direction.*/

        str_Output += RouteInfo.getJSONObject("RouteName").getString("Zh_tw") + "\n"; /*Get route name.*/
        /*Get destination station name.*/
        if(int_Direction == 0) {
            str_Output += String.format("往%s\n\n", RouteInfo.getString("DestinationStopNameZh"));
        }
        else {
            str_Output += String.format("往%s\n\n", RouteInfo.getString("DepartureStopNameZh"));
        }

        /*Get estimate arrival time from every stop.*/
        JSONArray StopList = RouteMap.getJSONArray("Stops");
        for(int Run = 0; Run < StopList.length(); Run++) {
            /*Get stop item.*/
            JSONObject StopInfo = StopList.getJSONObject(Run);
            String str_StopID = StopInfo.getString("StopID");
            
            JSONObject EstimateTime = eBus_LocalData.getInstance().SearchTarget(str_StopID, EstimateArrival.toString(), "StopID");
            JSONObject Obj_BusNum = eBus_LocalData.getInstance().SearchTarget(str_StopID, BusLocationList.toString(), "StopID");
            String str_BusNum = "";
            if(Obj_BusNum != null) {
                str_BusNum = String.format("[%s]", Obj_BusNum.getString("PlateNumb"));
            }
            str_Output += String.format("%s\t%s\t%s\r\n", Analysis_StopStatus(EstimateTime), StopInfo.getJSONObject("StopName").getString("Zh_tw"), str_BusNum);
        }

        return str_Output;
    }

    private static String Analysis_StopStatus(JSONObject Item) {
        String str_Output = "";
        try {
            int int_StatusFlag = Item.getInt("StopStatus");
            if(int_StatusFlag == 0) {
                int int_Mins = Math.round(Item.getInt("EstimateTime") / 60);
                if(int_Mins < 2) {
                    str_Output = "即將進站";
                }
                else {
                    str_Output = String.format("約%s分鐘", int_Mins);
                }
            }
            else if(int_StatusFlag == 1) {
                try {
                    int int_Mins = Math.round(Item.getInt("EstimateTime") / 60);
                    str_Output = String.format("%s分鐘", int_Mins);
                } 
                catch (Exception Err) {
                    str_Output = "目前未發車";
                }
            }
            else if(int_StatusFlag == 2) {
                str_Output = "交管不停靠";
            }
            else if(int_StatusFlag == 3) {
                str_Output = "末班車已過";
            }
            else if(int_StatusFlag == 4) {
                str_Output = "今日未營運";
            }
        } 
        catch (Exception e) {
            return "站位異動中";
        }
        return str_Output;
    }
}
