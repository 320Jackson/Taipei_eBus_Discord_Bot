package org.nhitc;

import java.util.Scanner;
import java.util.ArrayList;

import org.nhitc.Bot_Body.Bot_Body;
import org.nhitc.NetworkTask.Web_Crawler;
import org.nhitc.eBus.eBus_WebApi;

public class Main {
    public static void main(String[] args) {
        /*Start bot thread.*/
        Bot_Body Bot_Main = new Bot_Body(""); /*Bot thread.*/
        Bot_Main.start();

        /*Bot console.*/
        Scanner CommandScan = new Scanner(System.in);
        String str_Command;
        do {
            str_Command = CommandScan.next();
            /*Change bot display status.*/
            if(str_Command.equals("status")) {
                Bot_Main.ChangeStatus(CommandScan.nextInt());
            }
            /*Get console's command explanation.*/
            else if(str_Command.equals("help")) {
                System.out.println("status [1:Online, 2:Do not disturb, 3:Idle, 4:Offline] --- Change display status.\nexit --- Stop bot.");
            }
            /*Send messages by console.*/
            else if(str_Command.equals("say")) {
                Bot_Main.sendmsg("", "");
            }
            /*Download file.*/
            else if(str_Command.equals("get")) {
                Web_Crawler Downloader = new Web_Crawler();
                Downloader.Download(CommandScan.next(), CommandScan.next(), CommandScan.next());
            }
            /*Get arrival time every stop in specified route.*/
            else if(str_Command.equals("route")) {
                ArrayList<String> RouteMoving_Info = eBus_WebApi.get_RouteEstimateArrival(CommandScan.next());
                for(int Run = 0; Run < RouteMoving_Info.size(); Run++) {
                    System.out.println(RouteMoving_Info.get(Run));
                }
            }
            else {
                if(!str_Command.equals("exit")){
                    System.out.println("Error: Command not found.");
                }
            }
        }
        while(!str_Command.equals("exit"));

        /*Exit bot.*/
        CommandScan.close();
        Bot_Main.exit();
        System.exit(0);
    }
}