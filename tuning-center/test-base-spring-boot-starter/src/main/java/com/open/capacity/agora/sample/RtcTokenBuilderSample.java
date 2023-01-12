package com.open.capacity.agora.sample;

import com.open.capacity.common.agora.media.RtcTokenBuilder;
import com.open.capacity.common.agora.media.RtcTokenBuilder.Role;

public class RtcTokenBuilderSample {
    // 0f27f03f87bc4628ba44d5aefeb24136
    //
    static String appId = "970CA35de60c44645bbae8a215061b33";
    static String appCertificate = "5CFd2fd1755d40ecb72977518be15d32";
    static String channelName = "7d72365eb983485397e3e3f9d460bdda";
    static String userAccount = "2082341273";
    static int uid = 2082341273;
    static int expirationTimeInSeconds = 3600;

    public static void main(String[] args) throws Exception {
        RtcTokenBuilder token = new RtcTokenBuilder();
        int timestamp = (int)(System.currentTimeMillis() / 1000 + expirationTimeInSeconds);
        String result = token.buildTokenWithUserAccount(appId, appCertificate,
        		 channelName, userAccount, Role.Role_Publisher, timestamp);
        System.out.println(result);

        result = token.buildTokenWithUid(appId, appCertificate,
       		 channelName, uid, Role.Role_Publisher, timestamp);
        System.out.println(result);
    }
}
