package com.open.capacity.agora;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.open.capacity.common.agora.media.DynamicKey5;

/**
 * Created by Li on 10/1/2016.
 */
public class DynamicKey5_Test {
    private String appID   = "";
    private String appCertificate      = "";
    private String channel  = "";
    private int ts = 1446455472;
    private int r = 58964981;
    private long uid = 2882341273L;
    private int expiredTs=1446455471;

    @Test
    public void testGeneratePublicSharingKey() throws Exception {
        String expected = "005AwAoADc0QTk5RTVEQjI4MDk0NUI0NzUwNTk0MUFDMjM4MDU2NzIwREY3QjAQAJcMo13mDERkW7roohUGGzOwKDdW9buDA68oN1YAAA==";
        String result = DynamicKey5.generatePublicSharingKey(appID, appCertificate, channel, ts, r, uid, expiredTs);
        assertEquals(expected, result);
    }

    @Test
    public void testGenerateRecordingKey() throws Exception {
        String expected = "005AgAoADkyOUM5RTQ2MTg3QTAyMkJBQUIyNkI3QkYwMTg0MzhDNjc1Q0ZFMUEQAJcMo13mDERkW7roohUGGzOwKDdW9buDA68oN1YAAA==";
        String result = DynamicKey5.generateRecordingKey(appID, appCertificate, channel, ts, r, uid, expiredTs);
        assertEquals(expected, result);
    }

    @Test
    public void testGenerateMediaChannelKey() throws Exception {
        String expected = "005AQAoAEJERTJDRDdFNkZDNkU0ODYxNkYxQTYwOUVFNTM1M0U5ODNCQjFDNDQQAJcMo13mDERkW7roohUGGzOwKDdW9buDA68oN1YAAA==";
        String result = DynamicKey5.generateMediaChannelKey(appID, appCertificate, channel, ts, r, uid, expiredTs);
        assertEquals(expected, result);
    }

    @Test
    public void testInChannelPermission() throws Exception {
        String noUpload = "005BAAoADgyNEQxNDE4M0FGRDkyOEQ4REFFMUU1OTg5NTg2MzA3MTEyNjRGNzQQAJcMo13mDERkW7roohUGGzOwKDdW9buDA68oN1YBAAEAAQAw";
        String generatedNoUpload = DynamicKey5.generateInChannelPermissionKey(appID, appCertificate, channel, ts, r, uid, expiredTs, DynamicKey5.noUpload);
        assertEquals(noUpload, generatedNoUpload);

        String audioVideoUpload = "005BAAoADJERDA3QThENTE2NzJGNjQwMzY5NTFBNzE0QkI5NTc0N0Q1QjZGQjMQAJcMo13mDERkW7roohUGGzOwKDdW9buDA68oN1YBAAEAAQAz";
        String generatedAudioVideoUpload = DynamicKey5.generateInChannelPermissionKey(appID, appCertificate, channel, ts, r, uid, expiredTs, DynamicKey5.audioVideoUpload);
        assertEquals(audioVideoUpload, generatedAudioVideoUpload);
    }
}
