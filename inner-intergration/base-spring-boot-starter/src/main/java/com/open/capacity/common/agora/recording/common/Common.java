package com.open.capacity.common.agora.recording.common;

import java.nio.ByteBuffer;

/**
 * @author: <a href="https://github.com/hiwepy">hiwepy</a>
 *
 */
public class Common{
  /** Error codes.
   * @note When using the Recording SDK, you may receive error codes from the Native SDK. See more <a href="https://docs.agora.io/en/Interactive%20Broadcast/API%20Reference/java/classio_1_1agora_1_1rtc_1_1_i_rtc_engine_event_handler_1_1_error_code.html"> Error Codes</a>.
   */
  public enum ERROR_CODE_TYPE {
    /** 0: No error. */
    ERR_OK(0),
    /** 1: General error with no classified reason. */
    ERR_FAILED(1),
    /** 2: Invalid parameter is called. For example, the specific channel name contains illegal characters. */
    ERR_INVALID_ARGUMENT(2),
    /** 3: The SDK module is not ready. Agora recommends the following methods to solve this error:
    * <ul>
    *   <li>Check the audio device.</li>
    *   <li>Check the completeness of the application.</li>
    *   <li>Re-initialize the SDK.</li>
    * </ul>
    */
    ERR_INTERNAL_FAILED(3);

    private final int value;

    private ERROR_CODE_TYPE(int value){
      this.value = value;
    }

    public int getValue() {
      return value;
    }

    public static ERROR_CODE_TYPE getByCode(int code) {
      ERROR_CODE_TYPE[] values = ERROR_CODE_TYPE.values();
      for (ERROR_CODE_TYPE value : values) {
        if (value.getValue() == code) {
          return value;
        }
      }
      return null;
    }
  }

  /** State codes. */
  public enum STAT_CODE_TYPE {
    /** 0: Everything is normal. */
    STAT_OK(0),
    /** 1: Errors from the Agora Native/Web SDK. See more <a href="https://docs.agora.io/en/Interactive%20Broadcast/API%20Reference/java/classio_1_1agora_1_1rtc_1_1_i_rtc_engine_event_handler_1_1_error_code.html"> Error Codes</a>. */
    STAT_ERR_FROM_ENGINE(1),
    /** 2: Failure to join the channel. */
    STAT_ERR_ARS_JOIN_CHANNEL(2),
    /** 3: Failure to create a process. */
    STAT_ERR_CREATE_PROCESS(3),
    /** 4: Invalid parameters of the video profile of the mixed video. See <a href="https://docs.agora.io/en/faq/recording_video_profile">Video Profile Table</a> to set the `mixResolution` parameter. */
    STAT_ERR_MIXED_INVALID_VIDEO_PARAM(4),
    /** 5: Null pointer. */
    STAT_ERR_NULL_POINTER(5),
    /** 6: Invalid parameters of the proxy server. */
    STAT_ERR_PROXY_SERVER_INVALID_PARAM(6),
    /** 0x8: Error in polling. */
    STAT_POLL_ERR(8),
    /** 0x10: Polling hangs up. */
    STAT_POLL_HANG_UP(16),
    /** 0x20: Invalid polling request. */
    STAT_POLL_NVAL(32);

    private final int value;

    private STAT_CODE_TYPE(int value){
      this.value = value;
    }

    public int getValue() {
      return value;
    }

    public static STAT_CODE_TYPE getByCode(int code) {
      STAT_CODE_TYPE[] values = STAT_CODE_TYPE.values();
      for (STAT_CODE_TYPE value : values) {
        if (value.getValue() == code) {
          return value;
        }
      }
      return null;
    }
  }

  /** The reasons why the recording server leaves the channel. You can perform a bitwise AND operation on the code and each enum value, and those with non-zero results are the reason for the exit. For example, if you perform a bit-by-bit AND operation on code 6 (binary 110) and each enum value, only `LEAVE_CODE_SIG` (binary 10) and `LEAVE_CODE_NO_USERS` (binary 100) get a non-zero result. The reasons for exiting, in this case, include a timeout and a signal triggering the exit. */
  public enum LEAVE_PATH_CODE {
    /** 0: The initialization fails. */
    LEAVE_CODE_INIT(0),
    /** 2 (binary 10): The AgoraCoreService process receives the SIGINT signal. */
    LEAVE_CODE_SIG(1<<1),
    /** 4 (binary 100): The recording server automatically leaves the channel and stops recording because the channel has no user. */
    LEAVE_CODE_NO_USERS(1<<2),
    /** 8 (binary 1000): Ignore it. */
    LEAVE_CODE_TIMER_CATCH(1<<3),
    /** 16 (binary 10000): The recording server calls the {@link com.open.capacity.common.agora.recording.RecordingSDK.leaveChannel leaveChannel} method to leave the channel. */
    LEAVE_CODE_CLIENT_LEAVE(1<<4);

    private final int value;

    private LEAVE_PATH_CODE(int value){
      this.value = value;
    }

    public int getValue() {
      return value;
    }

    public static LEAVE_PATH_CODE getByCode(int code) {
      LEAVE_PATH_CODE[] values = LEAVE_PATH_CODE.values();
      for (LEAVE_PATH_CODE value : values) {
        if (value.getValue() == code) {
          return value;
        }
      }
      return null;
    }

  }

  /** The states of the remote stream. */
  public enum REMOTE_STREAM_STATE{
    /** The remote stream is received normally. */
    REMOTE_STREAM_STATE_RUNNING(0),
    /** The remote stream is stopped. */
    REMOTE_STREAM_STATE_STOPPED(1);
    private final int value;
    private REMOTE_STREAM_STATE(int value) {
      this.value = value;
    }
    public int getValue() {
      return value;
    }

    public static REMOTE_STREAM_STATE getByCode(int code) {
      REMOTE_STREAM_STATE[] values = REMOTE_STREAM_STATE.values();
      for (REMOTE_STREAM_STATE value : values) {
        if (value.getValue() == code) {
          return value;
        }
      }
      return null;
    }
  }

  /** The reasons causing the change of the remote stream state. */
  public enum REMOTE_STREAM_STATE_CHANGED_REASON{
    /** Starts pulling the stream of the remote user. */
    REASON_REMOTE_STREAM_STARTED(0),
    /** Stops pulling the stream of the remote user. */
    REASON_REMOTE_STREAM_STOPPED(1);
    private final int value;
    private REMOTE_STREAM_STATE_CHANGED_REASON(int value) {
      this.value = value;
    }
    public int getValue() {
      return value;
    }

    public static REMOTE_STREAM_STATE_CHANGED_REASON getByCode(int code) {
      REMOTE_STREAM_STATE_CHANGED_REASON[] values = REMOTE_STREAM_STATE_CHANGED_REASON.values();
      for (REMOTE_STREAM_STATE_CHANGED_REASON value : values) {
        if (value.getValue() == code) {
          return value;
        }
      }
      return null;
    }

  }

  /** Warning codes.
   *
   * @note When using the Recording SDK, you may receive warning codes from the Native SDK. See <a href="https://docs.agora.io/en/Interactive%20Broadcast/API%20Reference/java/classio_1_1agora_1_1rtc_1_1_i_rtc_engine_event_handler_1_1_warn_code.html"> Interactive Broadcast Warning Codes</a>.
   */
  public enum WARN_CODE_TYPE {
    /** 103: No channel resources are available. Maybe because the server cannot
     * allocate any channel resource.
     */
    WARN_NO_AVAILABLE_CHANNEL(103),
    /** 104: A timeout when looking up the channel. When a user joins a channel, the SDK
     * looks up the specified channel. This warning usually occurs when the network
     * conditions are too poor to connect to the server.
     */
    WARN_LOOKUP_CHANNEL_TIMEOUT(104),
    /** 105: The server rejected the request to look up the channel.
     * The server cannot process this request or the request is illegal.
     */
    WARN_LOOKUP_CHANNEL_REJECTED(105),
    /** 106: A timeout occurred when opening the channel. Once the specific channel
     * is found, the SDK opens the channel. This warning usually occurs when the
     * network conditions are too poor to connect to the server.
     */
    WARN_OPEN_CHANNEL_TIMEOUT(106),
    /** 107: The server rejected the request to open the channel. The server cannot
     * process this request or the request is illegal.
     */
    WARN_OPEN_CHANNEL_REJECTED(107),
    /** 108: An abnormal error occurs. The SDK would resume the recording. */
    WARN_RECOVERY_CORE_SERVICE_FAILURE(108);

    private final int value;
    private WARN_CODE_TYPE(int value) {
      this.value = value;
    }
    public int getValue() {
      return value;
    }

    public static WARN_CODE_TYPE getByCode(int code) {
      WARN_CODE_TYPE[] values = WARN_CODE_TYPE.values();
      for (WARN_CODE_TYPE value : values) {
        if (value.getValue() == code) {
          return value;
        }
      }
      return null;
    }

  }

  /** Sets the channel mode.
   @note The Recording SDK must use the same channel profile as the Agora Native/Web SDK, otherwise issues may occur.
   */
  public enum CHANNEL_PROFILE_TYPE {
    /** 0: (Default) Communication mode. This is used in one-on-one or group calls,
     * where all users in the channel can talk freely.
     */
    CHANNEL_PROFILE_COMMUNICATION(0),
    /** 1: Live broadcast. The host sends and receives voice/video, while the audience
     * only receives voice/video. Host and audience roles can be set by calling setClientRole.
     */
    CHANNEL_PROFILE_LIVE_BROADCASTING(1);
    private final int value;
    private CHANNEL_PROFILE_TYPE(int value) {
      this.value = value;
    }
    public int getValue() {
      return value;
    }

    public static CHANNEL_PROFILE_TYPE getByCode(int code) {
      CHANNEL_PROFILE_TYPE[] values = CHANNEL_PROFILE_TYPE.values();
      for (CHANNEL_PROFILE_TYPE value : values) {
        if (value.getValue() == code) {
          return value;
        }
      }
      return null;
    }

  }

  /** Connection states. */
  public enum CONNECTION_STATE_TYPE {
    /** 1: The SDK is disconnected from Agora's edge server.
     * <ul>
     *  <li>This is the initial state before calling the {@link com.open.capacity.common.agora.recording.RecordingSDK#createChannel createChannel} method.</li>
     *  <li>The SDK also enters this state when the app calls the {@link com.open.capacity.common.agora.recording.RecordingSDK#leaveChannel leaveChannel} method.</li>
     * </ul>
     */
    CONNECTION_STATE_DISCONNECTED(1),
    /** 2: The SDK is connecting to Agora's edge server.
     * <ul>
     *  <li>When the app calls the {@link com.open.capacity.common.agora.recording.RecordingSDK#createChannel createChannel} method, the SDK starts to establish a connection to the specified channel, triggers the {@link com.open.capacity.common.agora.recording.RecordingEventHandler#onConnectionStateChanged onConnectionStateChanged} callback, and switches to the {@link CONNECTION_STATE_TYPE#CONNECTION_STATE_CONNECTING CONNECTION_STATE_CONNECTING} state.
     *  <li>When a user successfully joins a channel, the SDK triggers the {@link com.open.capacity.common.agora.recording.RecordingEventHandler#onConnectionStateChanged onConnectionStateChanged} callback and switches to the {@link CONNECTION_STATE_TYPE#CONNECTION_STATE_CONNECTED CONNECTION_STATE_CONNECTED} state.
     *  <li>After the SDK joins the channel and when it finishes initializing the media engine, the SDK triggers the {@link com.open.capacity.common.agora.recording.RecordingEventHandler#onJoinChannelSuccess onJoinChannelSuccess} callback.
     * </ul>
     */
    CONNECTION_STATE_CONNECTING(2),
    /** 3: The SDK is connected to Agora's edge server and has joined a channel. You can now publish or subscribe to a media stream in the channel.
     *  If the connection to the channel is lost because, for example, the network is down or switched, the SDK triggers:
     * <ul>
     * <li>The {@link com.open.capacity.common.agora.recording.RecordingEventHandler#onConnectionInterrupted onConnectionInterrupted} callback.
     * <li>The {@link com.open.capacity.common.agora.recording.RecordingEventHandler#onConnectionStateChanged onConnectionStateChanged} callback, and switches to the {@link CONNECTION_STATE_TYPE#CONNECTION_STATE_RECONNECTING CONNECTION_STATE_RECONNECTING} state.
     * </ul>
     */
    CONNECTION_STATE_CONNECTED(3),
    /** 4: The SDK keeps rejoining the channel after being disconnected from a joined channel because of network issues.
     * <ul>
     *  <li>If the SDK cannot join the channel within 10 seconds after being disconnected from Agora's edge server, the SDK triggers the {@link com.open.capacity.common.agora.recording.RecordingEventHandler#onConnectionLost onConnectionLost} callback, stays in the {@link CONNECTION_STATE_TYPE#CONNECTION_STATE_RECONNECTING CONNECTION_STATE_RECONNECTING} state, and keeps rejoining the channel.
     *  <li>If the SDK fails to rejoin the channel 20 minutes after being disconnected from Agora's edge server, the SDK triggers the {@link com.open.capacity.common.agora.recording.RecordingEventHandler#onConnectionStateChanged onConnectionStateChanged} callback, switches to the {@link CONNECTION_STATE_TYPE#CONNECTION_STATE_FAILED CONNECTION_STATE_FAILED} state, and keeps rejoining the channel.
     * </ul>
     */
    CONNECTION_STATE_RECONNECTING(4),
    /** 5: The SDK fails to connect to Agora's edge server or join the channel.
     * You must call the {@link com.open.capacity.common.agora.recording.RecordingSDK#leaveChannel leaveChannel} method to leave this state and call the {@link com.open.capacity.common.agora.recording.RecordingSDK#createChannel createChannel} method again to rejoin the channel.
     *
     * If the SDK is banned from joining the channel by the Agora server (through the RESTful API), the SDK triggers the {@link com.open.capacity.common.agora.recording.RecordingEventHandler#onConnectionStateChanged onConnectionStateChanged} callback and switch to the {@link CONNECTION_STATE_TYPE#CONNECTION_STATE_FAILED CONNECTION_STATE_FAILED} state.
     */
    CONNECTION_STATE_FAILED(5);
    private final int value;
    private CONNECTION_STATE_TYPE(int value) {
      this.value = value;
    }
    public int getValue() {
      return value;
    }

    public static CONNECTION_STATE_TYPE getByCode(int code) {
      CONNECTION_STATE_TYPE[] values = CONNECTION_STATE_TYPE.values();
      for (CONNECTION_STATE_TYPE value : values) {
        if (value.getValue() == code) {
          return value;
        }
      }
      return null;
    }

  }

  /** Reasons for a connection state change. */
  public enum CONNECTION_CHANGED_REASON_TYPE {
    /** 0: The SDK is connecting to Agora's edge server. */
    CONNECTION_CHANGED_CONNECTING(0),
    /** 1: The SDK has joined the channel successfully. */
    CONNECTION_CHANGED_JOIN_SUCCESS(1),
    /** 2: The connection between the SDK and Agora's edge server is interrupted. */
    CONNECTION_CHANGED_INTERRUPTED(2),
    /** 3: The connection between the SDK and Agora's edge server is banned by Agora's edge server. */
    CONNECTION_CHANGED_BANNED_BY_SERVER(3),
    /** 4: The SDK fails to join the channel for more than 20 minutes and stops reconnecting to the channel. */
    CONNECTION_CHANGED_JOIN_FAILED(4),
    /** 5: The SDK has left the channel. */
    CONNECTION_CHANGED_LEAVE_CHANNEL(5);
    private final int value;
    private CONNECTION_CHANGED_REASON_TYPE(int value) {
      this.value = value;
    }
    public int getValue() {
      return value;
    }
  }

  /** The reasons why a user leaves the channel or goes offline.*/
  public enum USER_OFFLINE_REASON_TYPE  {
    /** 0: The user has quit the call. */
    USER_OFFLINE_QUIT(0),
    /** 1: The SDK timed out and the user dropped offline because it has not
     * received any data packet for a period of time. If a user quits the call
     * and the message is not passed to the SDK (due to an unreliable channel),
     * the SDK assumes the user has dropped offline.
     */
    USER_OFFLINE_DROPPED(1),
    /** 2: The client role has changed from the host to the audience. The option
     * is only valid when you set the channel profile as live broadcast when calling `joinChannel`.
     */
    USER_OFFLINE_BECOME_AUDIENCE(2);
    private USER_OFFLINE_REASON_TYPE(int code) {}
  }

  /** `streamType` takes effect only when the Native SDK or Web SDK has enabled dual-stream mode (high stream by default). */
  public enum REMOTE_VIDEO_STREAM_TYPE {
    /** 0: (Default) High stream. */
    REMOTE_VIDEO_STREAM_HIGH(0),
    /** 1: Low stream. */
    REMOTE_VIDEO_STREAM_LOW(1);
    private final int value;
    private REMOTE_VIDEO_STREAM_TYPE(int value) {
      this.value = value;
    }
    public int getValue() {
      return value;
    }
  }

  public enum SERVICE_MODE {
    RECORDING_MODE(0),//down stream
    SERVER_MODE(1),//up-down stream
    IOT_MODE(2);//up-down stream
    private SERVICE_MODE(int value){}
  }

  /** Video decoding format. */
  public enum VIDEO_FORMAT_TYPE {
    /** 0: Default video format. Depending on your codec, the recording service generates either MP4 or WebM files.*/
    VIDEO_FORMAT_DEFAULT_TYPE(0),
    /** 1: Video frame in H.264 format.
     * @deprecated From v2.9.0. Use VIDEO_FORMAT_ENCODED_FRAME_TYPE(1) instead.
     */
    VIDEO_FORMAT_H264_FRAME_TYPE(1),
    /** 2: Video frame in YUV format. You receive video frames from the {@link io::agora::recording::RecordingEventHandler::videoFrameReceived videoFrameReceived} callback.*/
    VIDEO_FORMAT_YUV_FRAME_TYPE(2),
    /** 3: Video frame in JPG format. You receive video frames from the {@link io::agora::recording::RecordingEventHandler::videoFrameReceived videoFrameReceived} callback. */
    VIDEO_FORMAT_JPG_FRAME_TYPE(3),
    /** 4: JPG file format. */
    VIDEO_FORMAT_JPG_FILE_TYPE(4),
    /** 5: JPG file format and MP4 or WebM video.
     * <ul>
     *   <li>Individual mode ({@link RecordingConfig#isMixingEnabled isMixingEnabled} is set as `false`): MP4 or WebM video and JPG files are supported. </li>
     *   <li>Composite mode ({@link RecordingConfig#isMixingEnabled isMixingEnabled} is set as `true`): MP4 or WebM video files for combined streams and JPG files for individual streams are supported. </li>
     * </ul>
     */
    VIDEO_FORMAT_JPG_VIDEO_FILE_TYPE(5),
    /** 6: Video frame in H.264 or H.265 format. You receive video frames from the {@link io::agora::recording::RecordingEventHandler::videoFrameReceived videoFrameReceived} callback.*/
    VIDEO_FORMAT_ENCODED_FRAME_TYPE(6);
    private int value;
    private VIDEO_FORMAT_TYPE(int value) {
    this.value = value;
    }
    private int getValue(){
      return value;
    }
  }

  /** Audio decoding format. */
  public enum AUDIO_FORMAT_TYPE {
    /** 0: Default audio file format (ACC). */
    AUDIO_FORMAT_DEFAULT_TYPE(0),
    /** 1: Audio frame in AAC format. You receive audio frames from the {@link io::agora::recording::RecordingEventHandler::audioFrameReceived audioFrameReceived} callback. */
    AUDIO_FORMAT_AAC_FRAME_TYPE(1),
    /** 2: Audio frame in PCM format. You receive audio frames from the {@link io::agora::recording::RecordingEventHandler::audioFrameReceived audioFrameReceived} callback. */
    AUDIO_FORMAT_PCM_FRAME_TYPE(2),
    /** 3: Audio frame in PCM audio-mixing format. You receive audio frames from the {@link io::agora::recording::RecordingEventHandler::audioFrameReceived audioFrameReceived} callback.*/
    AUDIO_FORMAT_MIXED_PCM_FRAME_TYPE(3);
    private int value;
    private AUDIO_FORMAT_TYPE(int value) {
      this.value = value;
    }
    private int getValue(){
      return value;
    }
  }

  /** Audio frame type. */
  public enum AUDIO_FRAME_TYPE {
    /** Audio frame in PCM format. */
    AUDIO_FRAME_RAW_PCM,
    /** Audio frame in AAC format. */
    AUDIO_FRAME_AAC
  }

  /**  Video frame type.*/
  public class VIDEO_FRAME_TYPE {
    /** 0: Video frame in YUV format. */
    public final int VIDEO_FRAME_RAW_YUV = 0;
    /** 1: Video frame in H.264 format. */
    public final int VIDEO_FRAME_H264 = 1;
    /** 2: Video frame in JPG format. */
    public final int VIDEO_FRAME_JPG = 2;
    /** 3: Video frame in H.265 format. */
    public final int VIDEO_FRAME_H265 = 3;
    /** 4: Video frame in JPG file format. */
    public final int VIDEO_JPG_FILE = 4;
    public int type = 0;
    public int getValue(){
      return type;
    }
  }


  /** Mix audio and video in real time.
   *
   * See [Supported Players](https://docs.agora.io/en/faq/recording_player).
   */
  public enum MIXED_AV_CODEC_TYPE {
      /** 0: (Default) Not mixes the audio and video. */
      MIXED_AV_DEFAULT(0),
      /** 1: Mixes the audio and video in real time into an MP4 file. Supports limited players. */
      MIXED_AV_CODEC_V1(1),
      /** 2: Mixes the audio and video in real time into an MP4 file. Supports more players. */
      MIXED_AV_CODEC_V2(2),
      /** 4: Outputs an TS file only in the individual recording mode. */
      AV_CODEC_INDIVIDUAL_TS_ONLY(4),
      /** 5: Outputs an TS file and an MP4 file respectively in the individual recording mode.

      @note The Agora SDK deletes the TS file after successful recording. */
      AV_CODEC_INDIVIDUAL_TS_AND_MP4(5),
      /** 6: Outputs an TS file only in the composite recording mode. */
      AV_CODEC_MIXED_TS_ONLY (6),
      /** 7: Outputs an TS file and an MP4 file respectively in the composite recording mode.
       *
       * @note The Agora SDK deletes the TS file after successful recording.
       */
      AV_CODEC_MIXED_TS_AND_MP4(7);

      private int value;
      private MIXED_AV_CODEC_TYPE(int value) {
          this.value = value;
      }
      private int getValue(){
          return value;
      }
  }

  /** Whether to record automatically or manually. */
  public enum TRIGGER_MODE_TYPE {
    /** 0: (Default) Automatically. */
    AUTOMATICALLY_MODE(0),
    /** 1: Manually. To start and stop recording, call {@link com.open.capacity.common.agora.recording.RecordingSDK#startService startService}
     * and {@link com.open.capacity.common.agora.recording.RecordingSDK#stopService stopService} respectively.
     */
    MANUALLY_MODE(1);
    private int value;
    private TRIGGER_MODE_TYPE(int value) {
      this.value = value;
    }
    public int getValue() {
      return value;
    }
  }

  /** Audio profile. Sets the sampling rate, bitrate, encode mode, and the number of channels. */
  public enum AUDIO_PROFILE_TYPE {
    /** 0: (Default) Sampling rate of 48 kHz, communication encoding, mono, and a bitrate of up to 48 Kbps. */
    AUDIO_PROFILE_DEFAULT(0), //use default settings.
    /** 1: Sampling rate of 48 kHz, music encoding, mono, and a bitrate of up to 128 Kbps. */
    AUDIO_PROFILE_HIGH_QUALITY(1), //48khz, 128kbps, mono, music
    /** 2: Sampling rate of 48 kHz, music encoding, stereo, and a bitrate of up to 192 Kbps. */
    AUDIO_PROFILE_HIGH_QUALITY_STEREO(2); //48khz, 192kbps, stereo, music
    private int value;
    private AUDIO_PROFILE_TYPE(int value) {
      this.value = value;
    }
    public int getValue() {
      return value;
    }
  };


  /** Log level. */
  public enum agora_log_level {
    /** 1: Fatal. */
    AGORA_LOG_LEVEL_FATAL(1),
    /** 2: Error. */
    AGORA_LOG_LEVEL_ERROR(2),
    /** 3: Warning. */
    AGORA_LOG_LEVEL_WARN(3),
    /** 4: Notice. */
    AGORA_LOG_LEVEL_NOTICE(4),
    /** 5: Info. */
    AGORA_LOG_LEVEL_INFO(5),
    /** 6: Debug. */
    AGORA_LOG_LEVEL_DEBUG(6);
    private int value;
    private agora_log_level(int value) {
      this.value = value;
    }
    public int getValue() {
      return value;
    }
  };

  /** The audio frame format. */
  public class AudioFrame {
    /** AUDIO_FRAME_TYPE indicates the audio frame format, PCM or AAC. */
    public AUDIO_FRAME_TYPE type;
    /** If type is AUDIO_FRAME_RAW_PCM, then PCM field points to PCM format audio data, AAC should not be used. See the structure of {@link Common#AudioPcmFrame AudioPcmFrame}. */
    public AudioPcmFrame pcm;
    /** If type is AUDIO_FRAME_AAC, then AAC field points to AAC format audio data, PCM should not be used. See the structure of {@link Common#AudioAacFrame AudioAacFrame}. */
    public AudioAacFrame aac;
  }

  /** The parameters of raw audio data in PCM format. */
  public class AudioPcmFrame {
    public AudioPcmFrame(long frame_ms, long sample_rates, long samples) {
    }
    /** Timestamp of the frame. */
    public long frame_ms;
    /** Number of audio channels. */
    public long channels; // 1
    /** Bitrate of the sampling data. */
    public long sample_bits; // 16
    /** Sampling rate. */
    public long sample_rates; // 8k, 16k, 32k
    /** Number of samples of the frame. */
    public long samples;
    /** Audio frame buffer. */
    public byte[] pcmBuf;
    /** Size of the audio frame buffer. */
    public long pcmBufSize;
  }

  /** TThe parameters of raw audio data in AAC format. */
  public class AudioAacFrame {
    public AudioAacFrame(long framems) {
      frame_ms = framems;
      aacBufSize = 0;
    }
    /** Timestamp of the frame. */
    public long frame_ms;
    /** Audio frame buffer. */
    public byte[] aacBuf;
    /** Size of the audio frame buffer. */
    public long aacBufSize;
    /** Channels Number of audio channels. */
    public int channels;
    /** Bitrate Bitrate of the audio.*/
    public int bitrate;
  }

  /** The parameters of raw video data in YUV format. */
  public class VideoYuvFrame {
    VideoYuvFrame(long framems, int width, int height, int ystride,int ustride, int vstride){
      this.frame_ms = framems;
      this.width = width;
      this.height = height;
      this.ystride = ystride;
      this.ustride = ustride;
      this.vstride = vstride;
    }
    /** Timestamp of the frame. */
    public long frame_ms;
    /** Y buffer pointer. */
    public ByteBuffer ybuf;
    /** U buffer pointer. */
    public ByteBuffer ubuf;
    /** V buffer pointer. */
    public ByteBuffer vbuf;
    /** Width of the video in the number of pixels. */
    public int width;
    /** Height of the video in the number of pixels. */
    public int height;
    /** Line span of the Y buffer. */
    public int ystride;
    /** Line span of the U buffer. */
    public int ustride;
    /** Line span of the V buffer. */
    public int vstride;
    /** Video frame buffer. */
    public byte[] buf;
    /** Size of the video frame buffer. */
    public long bufSize;
  }

  /** The parameters of raw video data in H.264 format.  */
  public class VideoH264Frame {
    VideoH264Frame(){
      frame_ms = 0;
      frame_num = 0;
      bufSize = 0;
    }
    /** Timestamp of the frame. */
    public long frame_ms;
    /** Index of the frame. */
    public long frame_num;
    /** Video frame buffer. */
    public byte[] buf;
    /** Size of the video frame buffer. */
    public long bufSize;
  }

  /** The parameters of raw video data in H.265 format.  */
  public class VideoH265Frame {
    VideoH265Frame(){
      frame_ms = 0;
      frame_num = 0;
      bufSize = 0;
    }
    /** Timestamp (ms) of the frame. */
    public long frame_ms;
    /** Index of the frame. */
    public long frame_num;
    /** Video frame buffer. */
    public byte[] buf;
    /** Size of the video frame buffer. */
    public long bufSize;
  }

  /** The parameters of raw video data in JPG format. */
  public class VideoJpgFrame {
    VideoJpgFrame(){
      frame_ms = 0;
      bufSize = 0;
    }
    /** Timestamp of the frame. */
    public long frame_ms;
    /** Video frame buffer. */
    public byte[] buf;
    /** Size of the video frame buffer. */
    public long bufSize;
  }

  /** The parameters of the video frame in JPG file format. */
  public class VideoJpgFile {
    VideoJpgFile(){
      frame_ms = 0;
      file_name = "";
    }
    /** Timestamp (ms) of the frame. */
    public long frame_ms;
    /** Jpg file name. */
    public String file_name;
  }

  /** The video frame format. */
  public class VideoFrame {
    /** If `VideoFrame` is in YUV format, then YUV points to video data in YUV format. See {@link Common#VideoYuvFrame VideoYuvFrame}. */
    public VideoYuvFrame yuv;
    /** If `VideoFrame` is in H.264 format, then H.264 points to video data in H.264 format. See {@link Common#VideoH264Frame VideoH264Frame}. */
    public VideoH264Frame h264;
    /** If `VideoFrame` is in H.265 format, then H.265 points to video data in H.265 format. See {@link Common#VideoH265Frame VideoH265Frame}. */
    public VideoH265Frame h265;
    /** If `VideoFrame` is in JPG format, then JPG points to video data in JPG format. See {@link Common#VideoJpgFrame VideoJpgFrame}. */
    public VideoJpgFrame jpg;
    /** If `VideoFrame` is in JPG file format, then JPG file points to video data in JPG file format. See {@link Common#VideoJpgFile VideoJpgFile}. */
    public VideoJpgFile jpg_file;
    /** Indicates the rotation of the video frame. */
    public int rotation; // 0, 90, 180, 270
  }

  /** The statistics of the remote video stream. For details, see <a href="https://docs.agora.io/en/Interactive%20Broadcast/in-call_quality_windows?platform=Windows#statistics-of-remote-video-streams">Statistics of remote video streams</a>. */
  public class RemoteVideoStats {
    /** Delay (ms). */
    public int delay = 0;
    /** The width (pixel) of the remote video. */
    public int width = 0;
    /** The height (pixel) of the remote video. */
    public int height = 0;
    /** The receiving bitrate (Kbps). */
    public int receivedBitrate = 0;
    /** The decoder output frame rate (fps) of the remote video.
     *
     * @note This parameter is only valid in Composite Recording mode or when the video decoding format is set as YUV.
     */
    public int decoderOutputFrameRate = 0;
    /** The video stream type:
     * <ul>
     *   <li>0: High-stream video.</li>
     *   <li>1: Low-stream video.</li>
     * </ul>
     */
    public int rxStreamType = 0;
  }

  /** The statistics of the remote audio stream. For details, see <a href="https://docs.agora.io/en/Interactive%20Broadcast/in-call_quality_windows?platform=Windows#statistics-of-remote-audio-streams">Statistics of remote audio streams</a>. */
  public class RemoteAudioStats {
    /** The audio quality.
     * <ul>
     *   <li>0: The quality is unknown. </li>
     *   <li>1: The quality is excellent. </li>
     *   <li>2: The quality is quite good, but the bitrate may be slightly lower than 1. </li>
     *   <li>3: Users can feel the communication slightly impaired. </li>
     *   <li>4: Users cannot communicate smoothly. </li>
     *   <li>5: The quality is so bad that users can barely communicate. </li>
     *   <li>6: The quality is down and users cannot communicate at all. </li>
     *   <li>7: Users cannot detect the quality (not in use). </li>
     *   <li>8: Detecting the quality. </li>
     * </ul>
     */
    public int quality = 0;
    /** The network delay (ms) from the sender to the receiver. */
    public int networkTransportDelay = 0;
    /** The jitter buffer delay (ms) at the receiver. */
    public int jitterBufferDelay = 0;
    /** The packet loss rate in the reported interval. */
    public int audioLossRate = 0;
  }

  /** The statistics of the recording. */
  public class RecordingStats {
    /** The length of time (s) when the recording server is in the channel, represented by an aggregate value. */
    public int duration = 0;
    /** The total number of received bytes, represented by an aggregate value. */
    public int rxBytes = 0;
    /** The receiving bitrate (Kbps), represented by an instantaneous value. */
    public int rxKBitRate = 0;
    /** The receiving bitrate (Kbps) of audio, represented by an instantaneous value. */
    public int rxAudioKBitRate = 0;
    /** The receiving bitrate (Kbps) of video, represented by an instantaneous value. */
    public int rxVideoKBitRate = 0;
    /** The latency (ms) between the recording client and the Agora server. */
    public int lastmileDelay = 0;
    /** The number of users in the channel.
     * <ul>
     *   <li>Communication profile: userCount = The number of users in the channel (including the recording server). </li>
     *   <li>Live broadcast profile: userCount = The number of hosts in the channel + 1. </li>
     * </ul>
     */
    public int userCount = 0;
    /** Application CPU usage (%). */
    public int cpuAppUsage = 0;
    /** System CPU usage (%). */
    public int cpuTotalUsage = 0;
  }

  /** User information. */
  public class UserJoinInfos {
    /** The relative path of the recorded files and recording log. */
    String storageDir;
    UserJoinInfos(){
      storageDir = "";
    }
  }

  /** Properties of the audio volume information. An array containing the user ID and volume information for each speaker. */
  public class AudioVolumeInfo {
    /** The user ID of the speaker. The uid of the local user is 0. */
    public long uid;
     /** The volume of the speaker. The value ranges between 0 (lowest volume) and 255 (highest volume).*/
    public int volume; // 0 ~ 255
  }

  /** The configuration of the text watermark. */
  public class LiteraWatermarkConfig {
    /** The watermark text in the string format.
     *
     * @note
     * <ul>
     * <li>The supported characters depend on the font. The default font is NotoSansMonoCJKsc-Regular. See the [font introduction](https://www.google.com/get/noto/help/cjk/). To change the font, you can also set the `font_file_path` parameter to specify the path of the font file.</li>
     * <li>Supports word wrap. The text will wrap to next line when it exceeds the watermark rectangle.</li>
     * <li>Supports line breaks.</li>
     * <li>There is no limit on the string length. The display of the text on the watermark rectangle is influenced by the font size and the size of the watermark rectangle. The part that exceeds the rectangle will not be displayed.</li>
     * </ul>
     */
    public String wmLitera = "";
    /** The path of the font file. If not specified, use the default font NotoSansMonoCJKsc-Regular.
     *
     * @note Supports font files in the formats such as ttf and otf.
     */
    public String fontFilePath = "";
    /** The font size. The default value is 10, which equals to 10 x 15 points at 144 dpi. */
    public int fontSize = 10;
    /** The horizontal shift (pixel) of the rectangle containing the watermark from the left border of the canvas. The default value is 0. */
    public int offsetX = 0;
    /** The vertical shift (pixel) of the rectangle containing the watermark from the top border of the canvas. The default value is 0. */
    public int offsetY = 0;
    /** The width (pixel) of the rectangle containing the watermark. The default value is 0. */
    public int wmWidth = 0;
    /** The height (pixel) of the rectangle containing the watermark. The default value is 0. */
    public int wmHeight = 0;
    public LiteraWatermarkConfig() {}
  }

  /** The configuration of the timestamp watermark.
   *
   * The dynamic timestamp shows the current time of the recording server, such as "2019:06:18 14:30:35".
   */
  public class TimestampWatermarkConfig {
    /** The font size (pt). The default value is 10, which equals to 10 x 15 points at 144 dpi. */
    public int fontSize = 10;
    /** The horizontal shift (pixel) of the rectangle containing the watermark from the left border of the canvas. The default value is 0. */
    public int offsetX = 0;
    /** The vertical shift (pixel) of the rectangle containing the watermark from the top border of the canvas. The default value is 0. */
    public int offsetY = 0;
    /** The height (pixel) of the rectangle containing the watermark. The default value is 0. */
    public int wmWidth = 0;
    /** The width (pixel) of the rectangle containing the watermark. The default value is 0. */
    public int wmHeight = 0;
    public TimestampWatermarkConfig() {}
  }

  /** The configuration of the image watermark. */
  public class ImageWatermarkConfig {
    /** The path of the image file.
     *
     * @note
     * <ul>
     * <li>Only supports local PNG images. </li>
     * <li>The resolution of the image should not exceed 480p. </li>
     * <li>If the image is smaller than the watermark rectangle, the SDK centers the image and does not stretch the image; if the image is larger than the watermark rectangle, the SDK scales down the image and then centers the image in the watermark rectangle.</li>
     * </ul>
     */
    public String imagePath = "";
    /** The horizontal shift (pixel) of the rectangle containing the watermark from the left border of the canvas. The default value is 0. */
    public int offsetX = 0;
    /** The vertical shift (pixel) of the rectangle containing the watermark from the top border of the canvas. The default value is 0. */
    public int offsetY = 0;
    /** The width (pixel) of the rectangle containing the watermark. The default value is 0. */
    public int wmWidth = 0;
    /** The height (pixel) of the rectangle containing the watermark. The default value is 0. */
    public int wmHeight = 0;
    public ImageWatermarkConfig() {}
  }

  /** The structure of VideoMixingLayout. */
  public class VideoMixingLayout
  {
    /** The width of the canvas (the display window or screen). */
    public int canvasWidth = 0;
    /** THE Height of the canvas (the display window or screen). */
    public int canvasHeight = 0;
    /** The background color of the canvas (the display window or screen) in RGB hex value.
     *
     * @note If you set the {@link RecordingConfig#defaultVideoBgPath defaultVideoBgPath} parameter in recordingconfig when calling the {@link com.open.capacity.common.agora.recording.RecordingSDK#createChannel createChannel} method, the `backgroundColor` parameter does not take effect.
     */
    public String backgroundColor = "";//e.g. "#C0C0C0" in RGB
    /** The number of the users (communication mode) or hosts (live broadcast mode) in the channel. */
    public int regionCount = 0;
    /** The users (communication mode) or hosts (live broadcast mode) list of VideoMixingLayout. Each user (communication mode) or host (live broadcast mode) in the channel has a region to display the video on the screen. See {@link Common#VideoMixingLayout#Region Region} to set parameters. */
    public Region[] regions;
    /** User-defined data. */
    public String appData = "";
    /** The length of the user-defined data. */
    public int appDataLength = 0;
    /** Sets whether or not to show the last frame of a user in the region after the user leaves a channel:
     * <ul>
     *   <li>true: The user's last frame shows in the region.</li>
     *   <li>false: (Default) The user's last frame does not show in the region.</li>
     * </ul>
     */
    public int keepLastFrame = 0;
    /** Configurates text watermarks. Pointer to an array of {@link Common#LiteraWatermarkConfig LiteraWatermarkConfig}.
     *
     * @note You can add up to ten text watermarks.
     */
    public LiteraWatermarkConfig[] literalWms;
    /** Configurates a timestamp watermark. Pointer to {@link Common#TimestampWatermarkConfig TimestampWatermarkConfig}.
     *
     * @note You can only add one timestamp watermark.
     */
    public TimestampWatermarkConfig[] timestampWms;
    /** Configurates image watermarks. Pointer to an array of {@link Common#ImageWatermarkConfig ImageWatermarkConfig}.
     *
     * @note You can add up to four image watermarks.
     */
    public ImageWatermarkConfig[] imageWms;
    /** Defines the parameters of the region. */
    public class Region {
      /** User IDs of the users (communication mode) or host (live broadcast mode) displaying the video in the region. */
      public long uid = 0;
      /** Relative horizontal position of the top-left corner of the region. The value is between 0.0 and 1.0. */
      public double x = 0;//[0,1]
      /** Relative vertical position of the top-left corner of the region. The value is between 0.0 and 1.0. */
      public double y = 0;//[0,1]
      /**  Relative width of the region. The value is between 0.0 and 1.0. */
      public double width = 0;//[0,1]
      /** Relative height of the region. The value is between 0.0 and 1.0. */
      public double height = 0;//[0,1]
      //Optional
      //[0, 1.0] where 0 denotes throughly transparent, 1.0 opaque
      /**  The transparency of the image. The value is between 0.0 (transparent) and 1.0 (opaque).
      * @note
      * This parameter is reserved for future use.
      */
      public double alpha = 1.0;
      /**  Render mode:
       * <ul>
       *   <li> RENDER_MODE_HIDDEN(0): (Default) Cropped mode. Uniformly scale the video until it fills the visible boundaries (cropped). One dimension of the video may have clipped contents.</li>
       *   <li> RENDER_MODE_FIT(1): Fit mode. Uniformly scale the video until one of its dimension fits the boundary (zoomed to fit). Areas that are not filled due to the disparity in the aspect ratio will be filled with black.</li>
       * </ul>
       */
      public int renderMode = 0;//RENDER_MODE_HIDDEN: Crop, RENDER_MODE_FIT: Zoom to fit
      public Region() {}
    }
  }
}
