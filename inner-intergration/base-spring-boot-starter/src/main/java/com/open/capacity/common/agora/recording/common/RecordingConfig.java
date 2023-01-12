package com.open.capacity.common.agora.recording.common;

import com.open.capacity.common.agora.recording.common.Common.*;
import lombok.Data;

/**
 * @author: <a href="https://github.com/hiwepy">hiwepy</a>
 */
@Data
public class RecordingConfig {
  public RecordingConfig() {
    isAudioOnly = false;
    isVideoOnly = false;
    isMixingEnabled = false;
    mixedVideoAudio = MIXED_AV_CODEC_TYPE.MIXED_AV_DEFAULT;

    mixResolution = "";
    decryptionMode = "";
    secret = "";
    appliteDir = "";
    recordFileRootDir = "";
    cfgFilePath = "";
    proxyType = 1;
    proxyServer = "";
    defaultVideoBgPath = "";
    defaultUserBgPath = "";

    // Agora 建议指定录制进程使用端口的范围。你可以为多个录制进程统一配置较大的端口范围（Agora 建议 40000 ~ 41000 或更大）。
    // 此时，本地服务端录制 SDK 会在指定范围内为每个录制进程分配端口，并避免端口的冲突。
    lowUdpPort = 40000;//40000;
    highUdpPort = 41000;//40004;
    idleLimitSec = 300;
    captureInterval = 5;
    triggerMode = 0;
    audioIndicationInterval = 0;
    audioProfile = 0;

    decodeVideo = VIDEO_FORMAT_TYPE.VIDEO_FORMAT_DEFAULT_TYPE;
    decodeAudio = AUDIO_FORMAT_TYPE.AUDIO_FORMAT_DEFAULT_TYPE;
    channelProfile = CHANNEL_PROFILE_TYPE.CHANNEL_PROFILE_COMMUNICATION;
    streamType = REMOTE_VIDEO_STREAM_TYPE.REMOTE_VIDEO_STREAM_HIGH;

    autoSubscribe = true;
    enableCloudProxy = false;
    subscribeVideoUids = "";
    subscribeAudioUids = "";

    enableIntraRequest = true;
    enableH265Support = false;
  }

  /** Sets whether or not to record audio only:
   * <ul>
   *  <li>true: Enables audio recording and disables video recording.</li>
   *  <li>false: (Default) Enables both audio and video recording.</li>
   * </ul>
   *
   * Used together with isVideoOnly:
   * <ul>
   *   <li>If isAudioOnly is true and isVideoOnly is false, only records audio.</li>
   *   <li>If isAudioOnly is false and isVideoOnly is true, only records video.</li>
   *   <li>If isAudioOnly is false and isVideoOnly is false, records both audio and video.</li>
   *   <li>isAudioOnly and isVideoOnly can not be set as true at the same time.</li>
   * </ul>
   */
  public boolean isAudioOnly;

  /** Sets whether or not to record video only:
   * <ul>
   *   <li>true: Enables video recording and disable audio recording.</li>
   *   <li>false: (Default) Enables both audio and video recording.</li>
   * </ul>
   *
   * Used together with isAudioOnly:
   * <ul>
   *   <li>If isAudioOnly is true and isVideoOnly is false, only records audio.</li>
   *   <li>If isAudioOnly is false and isVideoOnly is true, only records video.</li>
   *   <li>If isAudioOnly is false and isVideoOnly is false, records both audio and video.</li>
   *   <li>isAudioOnly and isVideoOnly can not be set as true at the same time.</li>
   * </ul>
   */
  public boolean isVideoOnly;

  /** Sets whether or not to enable the audio- or video-composite mode.
   *
   * <ul>
   *   <li>true: Enables composite recording mode, which means the audio of all uids is mixed in an audio file and the video of all uids is mixed in a video file. You can set the audio profile of the recording file by the {@link io::agora::recording::common::RecordingConfig#audioProfile audioProfile} parameter and set the video profile by the {@link io::agora::recording::common::RecordingConfig#mixResolution mixResolution} parameter.</li>
   *   <li>false: (Default) Enables individual recording mode, which means one audio or video file for each uid. The sampling rate of the recording file is 48 kHz, and the bitrate and audio channel number of the recording file are the same as those of the original audio stream. The video profile of the recording file is the same as that of the original video stream. </li>
   * </ul>
   */
  public boolean isMixingEnabled;

  /** If you set {@link RecordingConfig#isMixingEnabled isMixingEnabled} as true, {@link RecordingConfig#mixedVideoAudio mixedVideoAudio} allows you to mix the audio and video in an MP4 file in real time. For more information, see {@link Common#MIXED_AV_CODEC_TYPE MIXED_AV_CODEC_TYPE}.
   */
  public MIXED_AV_CODEC_TYPE mixedVideoAudio;

  /** If you set {@link RecordingConfig#isMixingEnabled isMixingEnabled} as true, {@link RecordingConfig#mixResolution mixResolution} allows you to set the video profile, including the width, height, frame rate, and bitrate. The default setting is 360 x 640, 15 fps, 500 Kbps.
   *
   * @note Agora only supports the following frame rates: 1 fps, 7 fps, 10 fps, 15 fps, 24 fps, 30 fps and 60 fps. The default value is 15 fps. If you set the frame rate as other values, the SDK uses the default value.
   *
   * See the <a href="https://docs.agora.io/en/faq/recording_video_profile">Video Profile Table</a>.
   */
  public String mixResolution;

  /** When the whole channel is encrypted, the recording SDK uses decryptionMode to enable the built-in decryption function:
   * <ul>
   *   <li>"aes-128-xts": AES-128, XTS mode</li>
   *   <li>"aes-128-ecb": AES-128, ECB mode</li>
   *   <li>"aes-256-xts": AES-256, XTS mode</li>
   *   <li>"aes-128-gcm": AES-128, GCM mode</li>
   *   <li>"aes-256-gcm": AES-256, GCM mode</li>
   * </ul>
   *
   * The default value is NULL.
   *
   * @note The decryption method of the recording server must be the same as that of the Native/Web SDK.
   */
  public String decryptionMode;

  /** The decryption password when decryption mode is enabled. The default value is NULL. */
  public String secret;

  /** Sets the path of AgoraCoreService.
   * The default path of AgoraCoreService is Agora_Recording_SDK_for_Linux_FULL/bin/.
   */
  public String appliteDir;

  /** Sets the path of the recorded files. The default value is NULL.
   *
   * After setting `recordFileRootDir`, the subdirectory will be automatically generated according to the date of the recording.
   */
  public String recordFileRootDir;

  /** Sets the path of the configuration file. The default value is NULL. For example, `--cfgFilePath /home/guest/recording_dir/cfg.json`.
   *
   * The content in the configuration file must be in JSON format. You can configure the following parameters:
   *
   * - `Recording_Dir`: The absolute directory of the output in the string format. The recording service does not automatically create a subdirectory.
   *
   *   For example, `{"Recording_Dir": "/home/guest/recording_dir/"}`, which means the recording service stores the recording files in the `/home/guest/recording_dir/` directory.
   *
   * - `Chunk_Time_Span`: Time interval (s) between two successive recorded files in the string format. Only in the `LiveBroadcasting` channel profile and in an individual recording session can you set `Chunk_Time_Span`, which must be >= 10.
   *
   *   For example: `{"Chunk_Time_Span": "15"}`, which means the recording service creates a file every 15 seconds.
   *
   * @note
   * - You can set the `Chunk_Time_Span` parameter only when you use the recording service in individual recording mode in an interactive live streaming channel.
   * - To use the `Chunk_Time_Span` parameter, you must set `enableIntraRequest` as `true` to enable the keyframe request. Whether the sender sends the keyframe depends on the Agora RTC SDK version used by the sender.
   * - Slicing occurs only when an I frame appears, therefore the actual slicing time interval may be slightly different from the set time interval.
   *
   */
  public String cfgFilePath;

  //decodeVideo: default 0 (0:save as file, 1:h.264 or h.265, 2:yuv, 3:jpg buffer, 4:jpg file, 5:jpg file and video file)
  /** Sets the video decoding format. See {@link Common#VIDEO_FORMAT_TYPE VIDEO_FORMAT_TYPE}.
   * @note When {@link Common#VIDEO_FORMAT_TYPE VIDEO_FORMAT_TYPE} = 1, 2, 3 or 4, {@link RecordingConfig#isMixingEnabled isMixingEnabled} cannot be set as true.
   */
  public VIDEO_FORMAT_TYPE decodeVideo;

  //decodeAudio:  (default 0 (0:save as file, 1:aac frame, 2:pcm frame, 3:mixed pcm frame) (Can't combine with isMixingEnabled) /option)
  /** Sets the audio decoding format. See {@link Common#AUDIO_FORMAT_TYPE AUDIO_FORMAT_TYPE}.
   *
   * @note When {@link Common#AUDIO_FORMAT_TYPE AUDIO_FORMAT_TYPE} = 1 or 2, {@link RecordingConfig#isMixingEnabled isMixingEnabled} cannot be set as true.
  */
  public AUDIO_FORMAT_TYPE decodeAudio;

  /** Sets the lowest UDP port. The default value is 0. Ensure that the value of highUdpPort - lowUdpPort &ge; 6. */
  public int lowUdpPort;

  /** Sets the highest UDP port. The default value is 0. Ensure that the value of highUdpPort - lowUdpPort &ge; 6. */
  public int highUdpPort;

  /** Sets a time period. The value must be &ge; 3 seconds. The default value is 300 seconds.
   *
   * When the Agora Recording SDK is recording, if there is no user in the channel after a time period of `idleLimitSec`, it automatically stops recording and leaves the channel.
   *
   * @note
   * <ul>
   *  <li>We charge you this time period.</li>
   *  <li>In a communication channel, the recording service does not recognize a channel as an idle channel, so long as the channel has users, regardless of whether they send stream or not.</li>
   *  <li>If a live streaming channel has an audience without a host for a set time (`idleLimitSec`), the recording service automatically stops and leaves the channel.</li>
   * </ul>
   */
  public int idleLimitSec;

  /** Sets the interval of the screen capture. The interval must be longer than 1 second and the default value is 5 seconds.
   *
   * @note  `captureInterval` is only valid when {@link RecordingConfig#decodeVideo decodeVideo} is set as 3, 4 or 5.
   */
  public int captureInterval;

  /** Sets whether or not to detect the users who speak.
   *
   * <ul>
   *   <li>&le; 0: (Default) Do not detect the users who speak.</li>
   *   <li>> 0: Sets the interval (ms) of detecting the users who speak. Agora recommends setting the interval to be longer than 200 ms. When the SDK detects the users who speak, the SDK returns the UID of the user who speaks loudest in the {@link RecordingEventHandler#onActiveSpeaker onActiveSpeaker} callback and returns the UIDs of all users who speak and their voice volumes in the {@link io::agora::recording::RecordingEventHandler#onAudioVolumeIndication onAudioVolumeIndication} callback.</li>
   * </ul>
   */
  public int audioIndicationInterval;

  //channelProfile:0 communicate, 1:braodacast; default is 0
  /** Sets the channel mode. See {@link Common#CHANNEL_PROFILE_TYPE CHANNEL_PROFILE_TYPE}. */
  public CHANNEL_PROFILE_TYPE channelProfile;

  //streamType:0:get high stream 1:get low stream; default is 0
  /** `streamType` takes effect only when the Agora Native SDK/Web SDK enables the dual-stream
   * mode (high stream by default). See {@link Common#REMOTE_VIDEO_STREAM_TYPE REMOTE_VIDEO_STREAM_TYPE}.
   */
  public REMOTE_VIDEO_STREAM_TYPE streamType;

  /** Sets whether to start the recording automatically or manually:
   * <ul>
   *   <li>0: (Default) Automatically</li>
   *   <li>1: Manually</li>
   * </ul>
   *
   * If you wish to call {@link RecordingSDK#startService() startService} and {@link RecordingSDK#stopService() stopService}, then choose Manually.
   */
  public int triggerMode;

  /** Sets the type of the proxy server:
   * <ul>
   *   <li>0: Deploy the proxy server of the SOCKS5 type.</li>
   *   <li>1: (Default) Use the cloud proxy service, and configure the domain (recommended).</li>
   *   <li>2: Use the cloud proxy service, and configure the IP list (recommended when you can not resolve a domain to an IP address).</li>
   * </ul>
   *
   * After setting the `proxyType` parameter, you need to set the `proxyServer` parameter. See <a href="https://docs.agora.io/en/Recording/cloudproxy_recording?platform=Linux">Use Cloud Proxy</a> for details.
  */
  public int proxyType;

  /** Sets the IP address (domain) and port of the proxy server for a recording within the intranet according to the type of the proxy server that you choose with the `proxyType` parameter.
   * <ul>
   *   <li>If `proxyType` is `0`, set it as `"<ip>:<port>"`.</li>
   *   <li>If `proxyType` is `1`, set it as `"<domain>:<port>"`.</li>
   *   <li>If `proxyType` is `2`, set it as `"<ip1>,<ip2>,...,<ipx>:<port>"`.</li>
   * </ul>
   * See <a href="https://docs.agora.io/en/Recording/cloudproxy_recording?platform=Linux">Use Cloud Proxy</a> for details.
  */
  public String proxyServer; //format ipv4:port

  /** If you set {@link RecordingConfig#isMixingEnabled isMixingEnabled} as true, {@link RecordingConfig#mixResolution mixResolution} allows you to set the audio profile of the recording file:
   * <ul>
   *   <li>AUDIO_PROFILE_DEFAULT = 0: (Default) Sampling rate of 48 KHz, communication encoding, mono, and a bitrate of up to 48 Kbps.</li>
   *   <li>AUDIO_PROFILE_MUSIC_HIGH_QUALITY = 1: Sampling rate of 48 KHz, music encoding, mono, and a bitrate of up to 128 Kbps.</li>
   *   <li>AUDIO_PROFILE_MUSIC_HIGH_QUALITY_STEREO = 2: Sampling rate of 48 KHz, music encoding, stereo, and a bitrate of up to 192 Kbps.</li>
   * </ul>
   */
  public int audioProfile;

  /** Sets the path of the default background image of the canvas in composite recording mode.
   *
   * If `defaultVideoBgPath` is not set, the canvas displays the background color.
   *
   * @note Only supports local images in JPEG format.
   */
  public String defaultVideoBgPath;

  /** Sets the path of the default background image of users in composite recording mode.
   *
   * The background image is displayed when a user is online and does not send any video stream.
   *
   * If `defaultUserBgPath` is not set, the user region displays the background color.
   *
   * @note
   * <ul>
   *   <li>Only supports local images in JPEG format.</li>
   *   <li>The background image is not displayed for users using the Agora Web SDK.</li>
   * </ul>
   */
  public String defaultUserBgPath;
  /** Sets whether to record the streams of all users or specified users.
   * <ul>
   *   <li>true: (Default) Record the streams of all users.</li>
   *   <li>false: Record the streams of specified users.</li>
   * </ul>
   *
   * @note If you set `autoSubscribe` as false, you should set {@link RecordingConfig#subscribeVideoUids subscribeVideoUids} or {@link RecordingConfig#subscribeAudioUids subscribeAudioUids} to specify users whose video or audio you want to record.
   */
  public boolean autoSubscribe;
  /** Sets whether or not to enable the cloud proxy:
   * <ul>
   * <li>true: Enables the cloud proxy. </li>
   * <li>false: (Default) Disables the cloud proxy.</li>
   * </ul>
   *
   * See <a href="https://docs.agora.io/en/Recording/cloudproxy_recording?platform=Linux">Use Cloud Proxy</a> for details.
   */
  public boolean enableCloudProxy;
  /** An array of UIDs whose video streams you want to record.
   *
   * If you set {@link RecordingConfig#autoSubscribe autoSubscribe} as false, `subscribeVideoUids` enables you to record the video streams of specified users. */
  public String subscribeVideoUids;
  /** An array of UIDs whose audio streams you want to record.
   *
   * If you set {@link RecordingConfig#autoSubscribe autoSubscribe} as false, `subscribeAudioUids` enables you to record the audio streams of specified users. */
  public String subscribeAudioUids;

  /** Sets whether to enable the keyframe request. The default value is `true`, which can improve the audio and video quality under poor network conditions. To play the video file recorded in individual recording mode from a specified position, you must set `enableIntraRequest` as false.
   *
   * <ul>
   * <li> true: (Default) Leave it to the sender to decide whether to enable the keyframe request. After the keyframe request is enabled, you cannot play a video file, which is recorded in individual recording mode, from a specified position.</li>
   * <li> false: Disable the keyframe request. All senders in the channel send the keyframe at an interval of 2 seconds. After the keyframe request is disabled, you can play a video file, which is recorded in individual recording mode, from a specified position.</li>
   * </ul>
   *
   * @note If the sender uses Agora RTC SDK v2.9.2 or earlier, this parameter is valid only in the live-broadcast scenario.
   */
  public boolean enableIntraRequest;

  /** Sets whether to enable recording video stream in H.265 format:
   *
   * <ul>
   * <li> true: Enable recording video stream in H.265 format.</li>
   * <li> false: (Default) Disable recording stream in H.265 format. Other remote users in the channel can no longer send video stream in H.265 format.</li>
   * </ul>
   */
  public boolean enableH265Support;
}
