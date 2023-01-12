package com.open.capacity.agora;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.open.capacity.common.agora.media.AccessToken;

public class AccessToken_Test {
    private String appId = "";
    private String appCertificate = "";
    private String channelName = "";
    private String uid = "84498484";
    private int ts = 1111111;
    private int salt = 1;
    private int expireTimestamp = 1446455471;

    @Test
    public void testGenerateDynamicKey() throws Exception {
        String expected = "006970CA35de60c44645bbae8a215061b33IACV0fZUBw+72cVoL9eyGGh3Q6Poi8bgjwVLnyKSJyOXR7dIfRBXoFHlEAABAAAAR/QQAAEAAQCvKDdW";
        AccessToken token = new AccessToken(appId, appCertificate, channelName, uid);
        token.message.ts = ts;
        token.message.salt = salt;
        token.addPrivilege(AccessToken.Privileges.kJoinChannel, expireTimestamp);
        String result = token.build();
        assertEquals(expected, result);
    }

    @Test
    public void testAccessTokenWithIntUid() throws Exception {
        String expected =
                "006970CA35de60c44645bbae8a215061b33IACV0fZUBw+72cVoL9eyGGh3Q6Poi8bgjwVLnyKSJyOXR7dIfRBXoFHlEAABAAAAR/QQAAEAAQCvKDdW";
        AccessToken key = new AccessToken(appId, appCertificate, channelName, uid);
        key.message.salt = salt;
        key.message.ts = ts;
        key.message.messages.put((short)AccessToken.Privileges.kJoinChannel.intValue, expireTimestamp);
        String result = key.build();
        System.out.println(result);
//        assertEquals(expected, result);
    }
}
