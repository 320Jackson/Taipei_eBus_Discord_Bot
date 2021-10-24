package org.nhitc.Bot_Body;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.user.UserStatus;
import org.nhitc.eBus.eBus_Command;

public class Bot_Body extends Thread {
    private DiscordApi api;
    private String token;

    /*Constructor, input token.*/
    public Bot_Body(String InputToken) {
        token = InputToken;
        api = new DiscordApiBuilder().setToken(token).login().join();
    }
    
    @Override
    public void run() {
        /*Generate message receiver.*/
        api.addMessageCreateListener(new eBus_Command());
    }

    /*Send message from console.*/
    public void sendmsg(String str_ChannelID, String str_msgContent) {
        TextChannel TargetChannel = api.getTextChannelById(str_ChannelID).get();
        TargetChannel.sendMessage(str_msgContent);
    }
    
    /*Change bot status.*/
    public void ChangeStatus(int Flag) {
        switch(Flag) {
            case 1:
                api.updateStatus(UserStatus.ONLINE);
                break;
            case 2:
                api.updateStatus(UserStatus.DO_NOT_DISTURB);
                break;
            case 3:
                api.updateStatus(UserStatus.IDLE);
                break;
            case 4:
                api.updateStatus(UserStatus.INVISIBLE);
                break;
        }
    }

    /*Disconnect bot.*/
    public void exit() {
        api.disconnect();
    }
}
