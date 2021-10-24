package org.nhitc.eBus;

import java.util.ArrayList;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

public class eBus_Command implements MessageCreateListener {
    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        String[] str_msgCommand = event.getMessageContent().split(" ");
        if(str_msgCommand[0].equals("/route")){
            ArrayList<String> msg_List = eBus_WebApi.get_RouteEstimateArrival(str_msgCommand[1]);
            if(str_msgCommand.length == 3) {
                event.getChannel().sendMessage(msg_List.get(Integer.valueOf(str_msgCommand[2])));
            }
            else {
                TextChannel TargetChannel = event.getChannel();
                for(int Index = 0; Index < msg_List.size(); Index++) {
                    TargetChannel.sendMessage(msg_List.get(Index));
                }
            }
        }
        else {            
            event.getChannel().sendMessage(CommandHandler(str_msgCommand));
        }
    }
    
    private String CommandHandler(String[] str_CommandList) {
        String str_Output = "";
        
        if(str_CommandList[0].equals("/station")) {
            str_Output = "功能開發中";
        }
        else if(str_CommandList[0].equals("/bus")) {
            str_Output = "功能開發中";
        }

        return str_Output;
    }
}