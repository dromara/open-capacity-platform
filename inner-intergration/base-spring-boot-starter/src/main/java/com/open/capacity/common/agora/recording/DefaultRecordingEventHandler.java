package com.open.capacity.common.agora.recording;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import com.open.capacity.common.agora.AgoraProperties;
import com.open.capacity.common.agora.AgoraRecordingProperties;
import com.open.capacity.common.agora.recording.RecordingEventHandler;
import com.open.capacity.common.agora.recording.RecordingSDK;
import com.open.capacity.common.agora.recording.common.Common;
import com.open.capacity.common.agora.recording.common.Common.AUDIO_FORMAT_TYPE;
import com.open.capacity.common.agora.recording.common.Common.AUDIO_FRAME_TYPE;
import com.open.capacity.common.agora.recording.common.Common.AudioFrame;
import com.open.capacity.common.agora.recording.common.Common.AudioVolumeInfo;
import com.open.capacity.common.agora.recording.common.Common.CHANNEL_PROFILE_TYPE;
import com.open.capacity.common.agora.recording.common.Common.CONNECTION_CHANGED_REASON_TYPE;
import com.open.capacity.common.agora.recording.common.Common.CONNECTION_STATE_TYPE;
import com.open.capacity.common.agora.recording.common.Common.REMOTE_STREAM_STATE;
import com.open.capacity.common.agora.recording.common.Common.REMOTE_STREAM_STATE_CHANGED_REASON;
import com.open.capacity.common.agora.recording.common.Common.RecordingStats;
import com.open.capacity.common.agora.recording.common.Common.RemoteAudioStats;
import com.open.capacity.common.agora.recording.common.Common.RemoteVideoStats;
import com.open.capacity.common.agora.recording.common.Common.VIDEO_FORMAT_TYPE;
import com.open.capacity.common.agora.recording.common.Common.VideoFrame;
import com.open.capacity.common.agora.recording.common.Common.VideoMixingLayout;
import com.open.capacity.common.agora.recording.common.RecordingConfig;
import com.open.capacity.common.agora.recording.common.RecordingResult;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: <a href="https://github.com/hiwepy">hiwepy</a>
 */
class RecordingCleanTimer extends TimerTask {
	DefaultRecordingEventHandler rs;

	public RecordingCleanTimer(DefaultRecordingEventHandler rs) {
		this.rs = rs;
	}

	@Override
	public void run() {
		rs.clean();
	}
}

@Data
class UserInfo {
	public long uid;
	public long last_receive_time;
	public FileOutputStream channel;
	public String fileName;
}

@Data
class RecordFile {
	private long uid;
	private String fileName;
}

/**
 * Agora应用程序发送回调通知
 *
 * @link {https://docs.agora.io/cn/Recording/API%20Reference/recording_java/index.html}
 * @link {https://docs.agora.io/cn/Recording/API%20Reference/recording_java/interfaceio_1_1agora_1_1recording_1_1_recording_event_handler.html}
 */
@Slf4j
public class DefaultRecordingEventHandler implements RecordingEventHandler {

	private AgoraProperties agoraProperties;
	private AgoraRecordingProperties recordingProperties;

	private int width = 0;
	private int height = 0;
	private int fps = 0;
	private int kbps = 0;
	private int count = 0;
	private String storageDir = "./";
	private CHANNEL_PROFILE_TYPE profile_type;
	// 频道内的观众ID
	Vector<Long> m_peers = new Vector<Long>();
	private RecordingConfig config = null;
	private RecordingSDK recordingSDKInstance = null;
	private boolean m_receivingAudio = false;
	private boolean m_receivingVideo = false;
	private HashSet<Long> subscribedVideoUids = new HashSet<Long>();
	private HashSet<String> subscribedVideoUserAccount = new HashSet<String>();

	HashMap<String, UserInfo> audioChannels = new HashMap<String, UserInfo>();
	HashMap<String, UserInfo> videoChannels = new HashMap<String, UserInfo>();
	Timer cleanTimer = null;
	private int layoutMode = 0;
	private long maxResolutionUid = -1;
	private String maxResolutionUserAccount = "";
	private int keepLastFrame = 0;
	public static final int DEFAULT_LAYOUT = 0;
	public static final int BESTFIT_LAYOUT = 1;
	public static final int VERTICALPRESENTATION_LAYOUT = 2;
	private String userAccount = "";
	private long keepMediaTime = 0;
	private long lastKeepAudioTime = 0;
	private long lastKeepVideoTime = 0;
	private long firstReceiveAudioTime = 0;
	private long firstReceiveAudioElapsed = 0;
	private long firstReceiveVideoTime = 0;
	private long firstReceiveVideoElapsed = 0;
	/**
	 * 当前对象录制的频道名称
	 */
	private String channelId;
	private long anchorUid;
	private long recordingUid;
	private Long recordingId;

	public DefaultRecordingEventHandler(String channelId, long anchorUid, long recordingUid, AgoraProperties agoraProperties,
			AgoraRecordingProperties recordingProperties, RecordingSDK recording) {
		this.channelId = channelId;
		this.anchorUid = anchorUid;
		this.recordingUid = recordingUid;
		this.agoraProperties = agoraProperties;
		this.recordingProperties = recordingProperties;
		this.recordingSDKInstance = recording;
		recordingSDKInstance.registerOberserver(this);
	}

	@Override
	public String getChannel() {
		return channelId;
	}

	public long getAnchorUid() {
		return anchorUid;
	}

	public long getRecordingUid() {
		return recordingUid;
	}

	public void setRecordingId(Long recordingId) {
		this.recordingId = recordingId;
	}

	public Long getRecordingId() {
		return recordingId;
	}

	public Vector<Long> getMPeers(){
		return this.m_peers;
	}

	public void unRegister() {
		recordingSDKInstance.unRegisterOberserver(this);
	}

	/**
	 * 该回调方法提示离开频道成功
	 * @param reason 录制端离开频道的原因，详见 LEAVE_PATH_CODE
	 */
	@Override
	public void onLeaveChannel(int reason) {
		log.info("RecordingSDK onLeaveChannel,code:" + reason);
	}

	/**
	 * 该回调方法表示 SDK 运行时出现了（网络或媒体相关的）错误。通常情况下，SDK 上报的错误意味着 SDK 无法自动恢复，需要 App 干预或提示用户
	 * @param error 错误代码
	 * @param stat_code 状态代码
	 */
	@Override
	public void onError(int error, int stat_code) {
		log.error("RecordingSDK onError,error:" + error + ",stat code:" + stat_code);
	}

	/**
	 * 1、发生警告回调：
	 * 该回调方法表示 SDK 运行时出现了（网络或媒体相关的）警告。通常情况下，SDK 上报的警告信息应用程序可以忽略，SDK 会自动恢复
	 * 文档：https://docs.agora.io/cn/Recording/API%20Reference/recording_java/enumio_1_1agora_1_1recording_1_1common_1_1_common_1_1_w_a_r_n___c_o_d_e___t_y_p_e.html
	 * <p>WARN_NO_AVAILABLE_CHANNEL (103): 没有可用的频道资源。可能是因为服务端没法分配频道资源 </p>
	 * <p>WARN_LOOKUP_CHANNEL_TIMEOUT (104): 查找频道超时。在加入频道时 SDK 先要查找指定的频道，出现该警告一般是因为网络太差，连接不到服务器 </p>
	 * <p>WARN_LOOKUP_CHANNEL_REJECTED (105): 查找频道请求被服务器拒绝。服务器可能没有办法处理这个请求或请求是非法的 </p>
	 * <p>WARN_OPEN_CHANNEL_TIMEOUT (106): 打开频道超时。查找到指定频道后，SDK 接着打开该频道，超时一般是因为网络太差，连接不到服务器 </p>
	 * <p>WARN_OPEN_CHANNEL_REJECTED (107): 打开频道请求被服务器拒绝。服务器可能没有办法处理该请求或该请求是非法的 </p>
	 * <p>WARN_RECOVERY_CORE_SERVICE_FAILURE (108): 录制程序出现了异常错误（例如崩溃），录制 SDK 会重新恢复录制 </p>
	 * @param warn
	 */
	@Override
	public void onWarning(int warn) {
		log.warn("RecordingSDK onWarning, warn code: {}", warn);
	}

	@Override
	public void onJoinChannelSuccess(String channelId, long uid) {
		if (config.getDecodeAudio() != AUDIO_FORMAT_TYPE.AUDIO_FORMAT_DEFAULT_TYPE) {
			cleanTimer.schedule(new RecordingCleanTimer(this), 10000);
		}
		log.info("RecordingSDK joinChannel success, channel Id: {} , uid: {}" , channelId, uid);
	}

	/**
	 * 重新加入频道回调
	 * 有时由于网络原因，录制客户端可能会和服务器失去连接，SDK 会进行自动重连，自动重连成功后触发此回调方法
	 * @param channelId 频道名
	 * @param uid 录制端的 UID
	 */
	@Override
	public void onRejoinChannelSuccess(String channelId, long uid) {
		log.info("onRejoinChannelSuccess, channel Id : {}, uid: {}", channelId, uid);
	}

	/**
	 * 网络连接状态已改变回调
	 * 该回调在网络连接状态发生改变的时候触发，并告知用户当前的网络连接状态和网络状态改变的原因。
	 * @param state 当前的网络连接状态，详见 CONNECTION_STATE_TYPE
	 * @param reason 网络连接状态发生改变的原因，详见 CONNECTION_CHANGED_REASON_TYPE
	 */
	@Override
	public void onConnectionStateChanged(CONNECTION_STATE_TYPE state, CONNECTION_CHANGED_REASON_TYPE reason) {
		 log.info("onConnectioNStatsChanged, stats: {}, reason: {}" , state, reason);
	}

	/**
	 * 远端音频流统计信息回调
	 * 该回调描述远端用户端到端的音频流统计信息，针对每个发送音频流的远端用户（通信模式）/主播（直播模式）每 2 秒触发一次。
	 * 如果远端有多个用户/主播发送音频流，该回调每 2 秒会被触发多次。
	 * @param uid 用户 ID，指定是哪个用户的音频流
	 * @param stats 远端音频统计数据
	 */
	@Override
	public void onRemoteAudioStats(long uid, RemoteAudioStats stats) {

		log.debug("onRemoteAudioStats, quality: " + stats.quality + ", networkTransportDelay : "
				+ stats.networkTransportDelay + ", jitterBufferDelay:" + stats.jitterBufferDelay
				+ ", audio loss rate : " + stats.audioLossRate);

	}

	@Override
	public void onRemoteVideoStats(long uid, RemoteVideoStats stats) {

		log.debug("onRemoteVideoStats, delay : " + stats.delay + ", width" + stats.width + ", height : " + stats.height
				+ ", receivedBitrate:" + stats.receivedBitrate + ", decoderOutputFrameRate:"
				+ stats.decoderOutputFrameRate + ", rxStreamType : " + stats.rxStreamType);

	}

	/**
	 * 录制统计信息回调
	 * 录制 SDK 定期向录制端报告当前录制的统计信息，每两秒触发一次。
	 * @param stats 详见 RecordingStats
	 */
	@Override
	public void onRecordingStats(RecordingStats stats) {

		log.debug("onRecordingStats, duration : " + stats.duration + ", rxByets " + stats.rxBytes + ", rxKBitRate: "
				+ stats.rxKBitRate + ", rxAudioKBitRate: " + stats.rxAudioKBitRate + ", rxVideoKBitRate:"
				+ stats.rxVideoKBitRate + ", lastmileDelay : " + stats.lastmileDelay + ", userCount : "
				+ stats.userCount + ", cpuAppUsage : " + stats.cpuAppUsage + ", cpuTotalUsage: " + stats.cpuTotalUsage);

	}

	/**
	 * 该回调方法提示有用户离开了频道（或掉线）。
	 * SDK 判断用户离开频道（或掉线）的依据是：在一定时间内（15 秒）没有收到对方的任何数据包。 在网络较差的情况下，可能会有误报。建议可靠的掉线检测应该由信令来做。
	 * <p>USER_OFFLINE_QUIT (0) : 用户主动离开</p>
	 * <p>USER_OFFLINE_DROPPED (1): 因过长时间收不到对方数据包，超时掉线。注意：可能有误判</p>
	 * <p>USER_OFFLINE_BECOME_AUDIENCE (2): 用户身份从主播切换为观众时触发。该选项仅适用于当你在调用 joinChannel 时将频道模式设置为直播的场景</p>
	 * @param uid 用户的 UID
	 * @param reason 用户离开当前频道或掉线的原因
	 */
	@Override
	public void onUserOffline(long uid, int reason) {
		log.info("RecordingSDK onUserOffline uid:" + uid + ",offline reason:" + reason);
		m_peers.remove(uid);
		// PrintUsersInfo(m_peers);
		setVideoMixingLayout();
	}

	protected void clean() {
		synchronized (this) {
			long now = System.currentTimeMillis();

			Iterator<Map.Entry<String, UserInfo>> audio_it = audioChannels.entrySet().iterator();
			while (audio_it.hasNext()) {
				Map.Entry<String, UserInfo> entry = audio_it.next();
				UserInfo info = entry.getValue();
				if (now - info.last_receive_time > 3000) {
					try {
						info.channel.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					audio_it.remove();
				}
			}
			Iterator<Map.Entry<String, UserInfo>> video_it = videoChannels.entrySet().iterator();
			while (video_it.hasNext()) {
				Map.Entry<String, UserInfo> entry = video_it.next();
				UserInfo info = entry.getValue();
				if (now - info.last_receive_time > 3000) {
					try {
						info.channel.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					video_it.remove();
				}
			}
		}
		cleanTimer.schedule(new RecordingCleanTimer(this), 10000);
	}

	/**
	 * 该回调方法提示有用户加入了频道，并返回新加入用户的 UID。
	 * 如果在录制端加入之前，已经有用户在频道中，SDK 也会上报这些已在频道中的用户 UID。频道内有多少用户，该回调就会调用几次。
	 * @param uid 用户的 UID
	 * @param recordingDir 录制的媒体文件和 log 的存放路径
	 */
	@Override
	public void onUserJoined(long uid, String recordingDir) {
		log.info("onUserJoined uid:" + uid + ",recordingDir:" + recordingDir);
		storageDir = recordingDir;
		m_peers.add(uid);
		// PrintUsersInfo(m_peers);
		// When the user joined, we can re-layout the canvas
		if (userAccount.length() > 0) {
			if (layoutMode != VERTICALPRESENTATION_LAYOUT
					|| recordingSDKInstance.getUidByUserAccount(maxResolutionUserAccount) != 0) {
				setVideoMixingLayout();
			}
		} else {
			setVideoMixingLayout();
		}
	}

	/**
	 * 该回调表示本地用户已成功注册 User Account。
	 * 录制端调用 createChannelWithUserAccount 方法成功注册 User Account 后，SDK 会触发该回调，并报告录制端的 UID 和 User Account。
	 * @param uid 录制端的 UID
	 * @param userAccount 录制端的 User Account
	 */
	@Override
	public void onLocalUserRegistered(long uid, String userAccount) {
		log.info("onLocalUserRegistered: " + uid + " => " + userAccount);
	}

	/**
	 * 该回调提示远端用户的信息已更新
	 * 远端用户加入频道后， SDK 会获取到该远端用户的 UID 和 User Account，在本地触发 onUserInfoUpdated
	 * @param uid 远端用户的 UID
	 * @param userAccount 录制端的 User Account
	 */
	@Override
	public void onUserInfoUpdated(long uid, String userAccount) {
		log.info("onUserInfoUpdated: " + uid + " => " + userAccount);
		if (subscribedVideoUserAccount.contains(userAccount)) {
			subscribedVideoUids.add(uid);
		}
		setVideoMixingLayout();
	}

	/**
	 * 远端用户视频流状态改变回调。
	 * 该回调在远端用户视频流状态发生变化时触发，并报告该用户当前的视频流状态和引起变化的原因。
	 * @param uid 远端用户的 UID
	 * @param state 该用户当前的视频流状态，详见 REMOTE_STREAM_STATE。
	 * @param reason 引起远端视频流状态变化的原因，详见 REMOTE_STREAM_STATE_CHANGED_REASON。
	 */
	@Override
	public void onRemoteVideoStreamStateChanged(long uid, REMOTE_STREAM_STATE state,
			REMOTE_STREAM_STATE_CHANGED_REASON reason) {
		log.info("OnRemoteVideoStreamState changed, state " + state + ", reason :" + reason);

	}

	/**
	 * 远端用户音频流状态改变回调。
	 * 该回调在远端用户音频流状态发生变化时触发，并报告该用户当前的音频流状态和引起变化的原因。
	 * @param uid 远端用户的 UID
	 * @param state 该用户当前的音频流状态，详见 REMOTE_STREAM_STATE
	 * @param reason 引起远端音频流状态变化的原因，详见 REMOTE_STREAM_STATE_CHANGED_REASON
	 */
	@Override
	public void onRemoteAudioStreamStateChanged(long uid, REMOTE_STREAM_STATE state,
			REMOTE_STREAM_STATE_CHANGED_REASON reason) {
		 log.info("OnRemoteAudioStreamState changed, state " + state + ", reason :" + reason);
	}

	private void checkUser(long uid, boolean isAudio, int frameType) {
		String path = storageDir + Long.toString(uid);
		String key = Long.toString(uid);
		synchronized (this) {
			if (isAudio && !audioChannels.containsKey(key)) {
				if (frameType == 0 || frameType == 1) {
					String audioPath = "";
					if (frameType == 0) {
						audioPath = path + ".pcm";
					} else if (frameType == 1) {
						audioPath = path + ".aac";
					}
					try {
						UserInfo info = new UserInfo();
						info.fileName = audioPath;
						info.channel = new FileOutputStream(audioPath, true);
						info.last_receive_time = System.currentTimeMillis();
						audioChannels.put(key, info);
					} catch (FileNotFoundException e) {
						log.info("Can't find file : " + audioPath);
					}
				}
			}

			if (!isAudio && !videoChannels.containsKey(key)) {
				if (frameType == 0 || frameType == 1 || frameType == 3) {
					String videoPath = "";
					if (frameType == 0) {
						videoPath = path + ".yuv";
					} else if (frameType == 1) {
						videoPath = path + ".h264";
					} else if (frameType == 3) {
						videoPath = path + ".h265";
					}
					try {
						UserInfo info = new UserInfo();
						info.fileName = videoPath;
						info.channel = new FileOutputStream(videoPath, true);
						info.last_receive_time = System.currentTimeMillis();
						videoChannels.put(key, info);
					} catch (FileNotFoundException e) {
						log.info("Can't find file : " + videoPath);
					}
				}
			}
		}
	}

	/**
	 * 监测到活跃用户回调
	 * RecordingConfig 中的 audioIndicationInterval 大于 0 时（建议设置时间间隔大于 200 ms），如果 SDK 监测到频道内有新的活跃用户说话时，会触发该回调，返回当前时间段声音最大的用户的 UID。
	 * @param uid 当前时间段声音最大的用户的 UID
	 */
	@Override
	public void onActiveSpeaker(long uid) {
		log.info("User:" + uid + "is speaking");
	}

	/**
	 * 该回调提示接收音频流或视频流的状态发生改变
	 * @param receivingAudio 录制端是否在接收音频流
	 * @param receivingVideo 录制端是否在接收视频流
	 */
	@Override
	public void onReceivingStreamStatusChanged(boolean receivingAudio, boolean receivingVideo) {
		log.info("pre receiving audio status is " + m_receivingAudio + ", now receiving audio status is "
				+ receivingAudio);
		log.info("pre receiving video status is " + m_receivingVideo + ", now receiving video  status is "
				+ receivingVideo);
		m_receivingAudio = receivingAudio;
		m_receivingVideo = receivingVideo;
	}

	/**
	 * 网络连接丢失回调
	 * SDK 在调用 createChannel 后无论是否加入成功，只要 10 秒和服务器无法连接就会触发该回调。
	 * onConnectionInterrupted 与 onConnectionLost 的区别是：
	 *  1.onConnectionInterrupted 回调一定是在加入频道成功后，且 SDK 失去和服务器的连接超过 4 秒时触发。
	 *  2.onConnectionLost 回调是无论之前加入频道是否成功，只要 10 秒内和服务器无法建立连接都会触发。
	 * 无论是哪种回调，除非应用程序主动调用 leaveChannel，不然 SDK 会一直自动重连。
	 */
	@Override
	public void onConnectionLost() {
		log.info("connection is lost");
	}

	/**
	 * 网络连接中断回调
	 * SDK 在和服务器建立连接后，失去网络连接超过 4 秒，就会触发该回调。在触发事件后，SDK 会主动重连服务器，所以该事件可以用于 UI 提示。
	 *
	 *     onConnectionInterrupted 回调一定是在加入频道成功后，且 SDK 失去和服务器的连接超过 4 秒时触发。
	 *     onConnectionLost 回调是无论之前加入频道是否成功，只要 10 秒内和服务器无法建立连接都会触发。
	 *
	 * 无论是哪种回调，除非应用程序主动调用 leaveChannel，不然 SDK 会一直自动重连。
	 */
	@Override
	public void onConnectionInterrupted() {
		log.info("connection is interrupted");
	}

	/**
	 * 该回调提示频道内谁正在说话以及说话者音量
	 * RecordingConfig 中的 audioIndicationInterval 大于 0 时（建议设置时间间隔大于 200 ms），该回调会返回在时间间隔内所有说话者的 UID 和音量
	 * @param infos 每个说话者的用户 ID 和音量信息的数组
	 */
	@Override
	public void onAudioVolumeIndication(AudioVolumeInfo[] infos) {
		if (infos.length == 0) {
			return;
		}
		for (int i = 0; i < infos.length; i++) {
			log.info("User:" + Long.toString(infos[i].uid) + ", audio volume:" + infos[i].volume);
		}
	}

	/**
	 * 该回调提示本地已接收到首帧远端视频并完成解码
	 * 本地收到首帧远端视频流并解码成功时，触发此调用。	 *
	 * @param uid 用户 ID，指定是哪个用户的视频流
	 * @param width 视频流宽（像素）
	 * @param height 视频流高（像素）
	 * @param elapsed 从本地用户调用 createChannel 到该回调触发的延迟（毫秒）
	 */
	@Override
	public void onFirstRemoteVideoDecoded(long uid, int width, int height, int elapsed) {
		log.info("onFirstRemoteVideoDecoded User:" + Long.toString(uid) + ", width:" + width + ", height:"
				+ height + ", elapsed:" + elapsed);
		this.firstReceiveVideoTime = this.firstReceiveVideoTime == 0 ? System.currentTimeMillis() : this.firstReceiveVideoTime;
		this.firstReceiveVideoElapsed = this.firstReceiveVideoTime == 0 ? elapsed : this.firstReceiveVideoElapsed;
	}

	/**
	 * 该回调提示本地已接收到首帧远端音频流
	 * @param uid 发送音频帧的远端用户的 ID
	 * @param elapsed 从本地用户调用 createChannel 到该回调触发的延迟（毫秒）
	 */
	@Override
	public void onFirstRemoteAudioFrame(long uid, int elapsed) {
		log.info("onFirstRemoteAudioFrame User:" + Long.toString(uid) + ", elapsed:" + elapsed);
		this.firstReceiveAudioTime = this.firstReceiveAudioTime == 0 ? System.currentTimeMillis() : this.firstReceiveAudioTime;
		this.firstReceiveAudioElapsed = this.firstReceiveAudioTime == 0 ? elapsed : this.firstReceiveAudioElapsed;
	}

	/**
	 * 当收到原始音频数据时，会触发该回调
	 * 当你将 RecordingConfig 中的 decodeAudio 设置为 1、2 或 3 时，可通过该回调接收原始音频数据
	 * @param uid 用户的 UID
	 * @param frame 返回的原始音频数据，格式为 PCM 或 AAC
	 */
	@Override
	public void audioFrameReceived(long uid, AudioFrame frame) {
		log.info("java demo audioFrameReceived,uid:"+uid+",type:"+ frame.type);
		byte[] buf = null;
		long size = 0;
		checkUser(uid, true, frame.type.ordinal());
		if (frame.type == AUDIO_FRAME_TYPE.AUDIO_FRAME_RAW_PCM) {// pcm
			buf = frame.pcm.pcmBuf;
			size = frame.pcm.pcmBufSize;
		} else if (frame.type == AUDIO_FRAME_TYPE.AUDIO_FRAME_AAC) {// aac
			buf = frame.aac.aacBuf;
			size = frame.aac.aacBufSize;
		} else {
			return;
		}
		WriteBytesToFileClassic(uid, buf, size, true);
	}

	/**
	 * 当收到视频数据时，会触发该回调
	 * 当你将 RecordingConfig 中的 decodeVideo 设置为 1、2 或 3 时，可通过该回调接收原始视频数据。
	 * 当你将 RecordingConfig 中的 decodeVideo 设置为 4 或 5 时，可通过该回调接收 JPG 文件格式的视频数据。
	 * 该回调可用于实现高级功能，如鉴黄。这些功能可以通过采集并分析 I 帧实现
	 * @param uid createChannel 方法中指定的远端用户的 UID。 如果先前未分配 UID，则 Agora 服务器会自动分配 UID
	 * @param type 返回的视频数据的格式：
	 *     0: YUV
	 *     1: H.264
	 *     2: JPG
	 *     3: H.265
	 *     4: JPG 文件
	 * @param frame 返回的视频数据。详见 VideoFrame
	 * @param rotation 旋转角度：0, 90, 180, or 270
	 */
	@Override
	public void videoFrameReceived(long uid, int type, VideoFrame frame, int rotation)// rotation:0,90,180,270
	{
		byte[] buf = null;
		long size = 0;
		checkUser(uid, false, type);
		// log.info("java demovideoFrameReceived,uid:"+uid+",type:"+type);

		if (type == 0) {// yuv
			buf = frame.yuv.buf;
			size = frame.yuv.bufSize;
			if (buf == null) {
				log.info("java demo videoFrameReceived null");
			}
		} else if (type == 1) {// h264
			buf = frame.h264.buf;
			size = frame.h264.bufSize;
		} else if (type == 2) {// jpg
			String path = storageDir + Long.toString(uid) + "_" + System.currentTimeMillis() + ".jpg";
			buf = frame.jpg.buf;
			size = frame.jpg.bufSize;
			try {
				FileOutputStream channel = new FileOutputStream(path, true);
				channel.write(buf, 0, (int) size);
				channel.close();
			} catch (Exception e) {
				log.info("Error write to " + path);
			}
			log.info("java demovideoFrameReceived,uid:" + uid + ",type:" + type + ",path:" + path);
			return;
		} else if (type == 3) { // h265
			buf = frame.h265.buf;
			size = frame.h265.bufSize;
		} else if (type == 4) { // jpg
			log.info("java demovideoFrameReceived,uid:" + uid + ",type:" + type + ",jpg_file:"
					+ frame.jpg_file.file_name);
			return;
		} else {
			return;
		}
		WriteBytesToFileClassic(uid, buf, size, false);
	}

	/**
	 * 该回调方法获取录制文件的存放路径
	 * Brief: Callback when call createChannel successfully
	 * @param path 录制文件的存放路径
	 */
	@Override
	public void recordingPathCallBack(String path) {
		storageDir = path;
	}

	/**
	 * 设置视频录制的混合布局
	 * @return
	 */
	private int setVideoMixingLayout() {
		Common ei = new Common();
		VideoMixingLayout layout = ei.new VideoMixingLayout();
		layout.keepLastFrame = this.keepLastFrame;
		int max_peers = profile_type == CHANNEL_PROFILE_TYPE.CHANNEL_PROFILE_COMMUNICATION ? 7 : 17;
		if (m_peers.size() > max_peers) {
			log.info("peers size is bigger than max m_peers:" + m_peers.size());
			return -1;
		}

		if (!recordingProperties.isMixingEnabled()) {
			return -1;
		}

		long maxuid = 0;
		if (userAccount.length() > 0) {
			maxuid = recordingSDKInstance.getUidByUserAccount(maxResolutionUserAccount);
		} else {
			maxuid = maxResolutionUid;
		}

		Vector<Long> videoUids = new Vector<Long>();
		Iterator<Long> it = m_peers.iterator();
		while (it.hasNext()) {
			Long uid = (Long) it.next();
			if (!config.isAutoSubscribe() && !subscribedVideoUids.contains(uid))
				continue;
			if (layoutMode == VERTICALPRESENTATION_LAYOUT) {
				String uc = recordingSDKInstance.getUserAccountByUid((int) (long) uid);
				if (uc.length() > 0 || maxuid != 0) {
					videoUids.add(uid);
				}
			} else {
				videoUids.add(uid);
			}
		}

		layout.canvasHeight = height;
		layout.canvasWidth = width;
		layout.backgroundColor = "#23b9dc";
		layout.regionCount = (int) (videoUids.size());

		if (!videoUids.isEmpty()) {
			log.info("java setVideoMixingLayout videoUids is not empty, start layout");
			VideoMixingLayout.Region[] regionList = new VideoMixingLayout.Region[videoUids.size()];
			log.info("mixing layout mode:" + layoutMode);
			if (layoutMode == BESTFIT_LAYOUT) {
				adjustBestFitVideoLayout(regionList, layout, videoUids);
			} else if (layoutMode == VERTICALPRESENTATION_LAYOUT) {
				adjustVerticalPresentationLayout(maxuid, regionList, layout, videoUids);
			} else {
				adjustDefaultVideoLayout(regionList, layout, videoUids);
			}

			layout.regions = regionList;

		} else {
			layout.regions = null;
		}

		return recordingSDKInstance.setVideoMixingLayout(layout);
	}

	private void adjustVerticalPresentationLayout(long maxResolutionUid, VideoMixingLayout.Region[] regionList,
			VideoMixingLayout layout, Vector<Long> videoUids) {
		log.info("begin adjust vertical presentation layout,peers size:" + videoUids.size()
				+ ", maxResolutionUid:" + maxResolutionUid);
		if (videoUids.size() <= 5) {
			adjustVideo5Layout(maxResolutionUid, regionList, layout, videoUids);
		} else if (videoUids.size() <= 7) {
			adjustVideo7Layout(maxResolutionUid, regionList, layout, videoUids);
		} else if (videoUids.size() <= 9) {
			adjustVideo9Layout(maxResolutionUid, regionList, layout, videoUids);
		} else {
			adjustVideo17Layout(maxResolutionUid, regionList, layout, videoUids);
		}
	}

	private void adjustBestFitVideoLayout(VideoMixingLayout.Region[] regionList, VideoMixingLayout layout,
			Vector<Long> videoUids) {
		if (videoUids.size() == 1) {
			adjustBestFitLayout_Square(regionList, 1, layout, videoUids);
		} else if (videoUids.size() == 2) {
			adjustBestFitLayout_2(regionList, layout, videoUids);
		} else if (2 < videoUids.size() && videoUids.size() <= 4) {
			adjustBestFitLayout_Square(regionList, 2, layout, videoUids);
		} else if (5 <= videoUids.size() && videoUids.size() <= 9) {
			adjustBestFitLayout_Square(regionList, 3, layout, videoUids);
		} else if (10 <= videoUids.size() && videoUids.size() <= 16) {
			adjustBestFitLayout_Square(regionList, 4, layout, videoUids);
		} else if (videoUids.size() == 17) {
			adjustBestFitLayout_17(regionList, layout, videoUids);
		} else {
			log.info("adjustBestFitVideoLayout is more than 17 users");
		}
	}

	private void adjustBestFitLayout_2(VideoMixingLayout.Region[] regionList, VideoMixingLayout layout,
			Vector<Long> videoUids) {
		float canvasWidth = (float) width;
		float canvasHeight = (float) height;
		float viewWidth = 0.235f;
		float viewHEdge = 0.012f;
		float viewHeight = viewWidth * (canvasWidth / canvasHeight);
		float viewVEdge = viewHEdge * (canvasWidth / canvasHeight);
		int peersCount = videoUids.size();
		for (int i = 0; i < peersCount; i++) {
			regionList[i] = layout.new Region();
			regionList[i].uid = videoUids.get(i);
			regionList[i].x = (((i + 1) % 2) == 0) ? 0 : 0.5;
			regionList[i].y = 0.f;
			regionList[i].width = 0.5f;
			regionList[i].height = 1.f;
			regionList[i].alpha = i + 1;
			regionList[i].renderMode = 0;
		}
	}

	private void adjustDefaultVideoLayout(VideoMixingLayout.Region[] regionList, VideoMixingLayout layout,
			Vector<Long> videoUids) {
		regionList[0] = layout.new Region();
		regionList[0].uid = videoUids.get(0);
		regionList[0].x = 0.f;
		regionList[0].y = 0.f;
		regionList[0].width = 1.f;
		regionList[0].height = 1.f;
		regionList[0].alpha = 1.f;
		regionList[0].renderMode = 0;
		float f_width = width;
		float f_height = height;
		float canvasWidth = f_width;
		float canvasHeight = f_height;
		float viewWidth = 0.235f;
		float viewHEdge = 0.012f;
		float viewHeight = viewWidth * (canvasWidth / canvasHeight);
		float viewVEdge = viewHEdge * (canvasWidth / canvasHeight);
		for (int i = 1; i < videoUids.size(); i++) {
			regionList[i] = layout.new Region();

			regionList[i].uid = videoUids.get(i);
			float f_x = (i - 1) % 4;
			float f_y = (i - 1) / 4;
			float xIndex = f_x;
			float yIndex = f_y;
			regionList[i].x = xIndex * (viewWidth + viewHEdge) + viewHEdge;
			regionList[i].y = 1 - (yIndex + 1) * (viewHeight + viewVEdge);
			regionList[i].width = viewWidth;
			regionList[i].height = viewHeight;
			regionList[i].alpha = (i + 1);
			regionList[i].renderMode = 0;
		}
		layout.regions = regionList;
	}

	private void setMaxResolutionUid(int number, long maxResolutionUid, VideoMixingLayout.Region[] regionList,
			double weight_ratio) {
		regionList[number].uid = maxResolutionUid;
		regionList[number].x = 0.f;
		regionList[number].y = 0.f;
		regionList[number].width = 1.f * weight_ratio;
		regionList[number].height = 1.f;
		regionList[number].alpha = 1.f;
		regionList[number].renderMode = 1;
	}

	private void changeToVideo7Layout(long maxResolutionUid, VideoMixingLayout.Region[] regionList,
			VideoMixingLayout layout, Vector<Long> videoUids) {
		log.info("changeToVideo7Layout");
		adjustVideo7Layout(maxResolutionUid, regionList, layout, videoUids);
	}

	private void changeToVideo9Layout(long maxResolutionUid, VideoMixingLayout.Region[] regionList,
			VideoMixingLayout layout, Vector<Long> videoUids) {
		log.info("changeToVideo9Layout");
		adjustVideo9Layout(maxResolutionUid, regionList, layout, videoUids);
	}

	private void changeToVideo17Layout(long maxResolutionUid, VideoMixingLayout.Region[] regionList,
			VideoMixingLayout layout, Vector<Long> videoUids) {
		log.info("changeToVideo17Layout");
		adjustVideo17Layout(maxResolutionUid, regionList, layout, videoUids);
	}

	private void adjustBestFitLayout_Square(VideoMixingLayout.Region[] regionList, int nSquare,
			VideoMixingLayout layout, Vector<Long> videoUids) {
		float canvasWidth = (float) width;
		float canvasHeight = (float) height;
		float viewWidth = (float) (1.f * 1.0 / nSquare);
		float viewHEdge = (float) (1.f * 1.0 / nSquare);
		float viewHeight = viewWidth * (canvasWidth / canvasHeight);
		float viewVEdge = viewHEdge * (canvasWidth / canvasHeight);
		int peersCount = videoUids.size();
		for (int i = 0; i < peersCount; i++) {
			regionList[i] = layout.new Region();
			float xIndex = (float) (i % nSquare);
			float yIndex = (float) (i / nSquare);
			regionList[i].uid = videoUids.get(i);
			regionList[i].x = 1.f * 1.0 / nSquare * xIndex;
			regionList[i].y = 1.f * 1.0 / nSquare * yIndex;
			regionList[i].width = viewWidth;
			regionList[i].height = viewHEdge;
			regionList[i].alpha = (double) (i + 1);
			regionList[i].renderMode = 0;
		}
	}

	private void adjustBestFitLayout_17(VideoMixingLayout.Region[] regionList, VideoMixingLayout layout,
			Vector<Long> videoUids) {
		float canvasWidth = (float) width;
		float canvasHeight = (float) height;
		int n = 5;
		float viewWidth = (float) (1.f * 1.0 / n);
		float viewHEdge = (float) (1.f * 1.0 / n);
		float totalWidth = (float) (1.f - viewWidth);
		float viewHeight = viewWidth * (canvasWidth / canvasHeight);
		float viewVEdge = viewHEdge * (canvasWidth / canvasHeight);
		int peersCount = videoUids.size();
		for (int i = 0; i < peersCount; i++) {
			regionList[i] = layout.new Region();
			float xIndex = (float) (i % (n - 1));
			float yIndex = (float) (i / (n - 1));
			regionList[i].uid = videoUids.get(i);
			regionList[i].width = viewWidth;
			regionList[i].height = viewHEdge;
			regionList[i].alpha = i + 1;
			regionList[i].renderMode = 0;
			if (i == 16) {
				regionList[i].x = (1 - viewWidth) * (1.f / 2) * 1.f;
				log.info("special layout for 17 x is:" + regionList[i].x);
			} else {
				regionList[i].x = 0.5f * viewWidth + viewWidth * xIndex;
			}
			regionList[i].y = (1.0 / n) * yIndex;
		}
	}

	private void adjustVideo5Layout(long maxResolutionUid, VideoMixingLayout.Region[] regionList,
			VideoMixingLayout layout, Vector<Long> videoUids) {
		boolean flag = false;

		float canvasWidth = (float) width;
		float canvasHeight = (float) height;

		float viewWidth = 0.235f;
		float viewHEdge = 0.012f;
		float viewHeight = viewWidth * (canvasWidth / canvasHeight);
		float viewVEdge = viewHEdge * (canvasWidth / canvasHeight);
		int number = 0;

		int i = 0;
		for (; i < videoUids.size(); i++) {
			regionList[i] = layout.new Region();
			if (maxResolutionUid == videoUids.get(i)) {
				log.info("adjustVideo5Layout equal with configured user uid:" + maxResolutionUid);
				flag = true;
				setMaxResolutionUid(number, maxResolutionUid, regionList, 0.8);
				number++;
				continue;
			}
			regionList[number].uid = videoUids.get(i);
			// float xIndex = ;
			float yIndex = flag ? ((float) (number - 1 % 4)) : ((float) (number % 4));
			regionList[number].x = 1.f * 0.8;
			regionList[number].y = (0.25) * yIndex;
			regionList[number].width = 1.f * (1 - 0.8);
			regionList[number].height = 1.f * (0.25);
			regionList[number].alpha = (double) number;
			regionList[number].renderMode = 0;
			number++;
			if (i == 4 && !flag) {
				changeToVideo7Layout(maxResolutionUid, regionList, layout, videoUids);
			}
		}
	}

	private void adjustVideo7Layout(long maxResolutionUid, VideoMixingLayout.Region[] regionList,
			VideoMixingLayout layout, Vector<Long> videoUids) {
		boolean flag = false;
		float canvasWidth = (float) width;
		float canvasHeight = (float) height;

		float viewWidth = 0.235f;
		float viewHEdge = 0.012f;
		float viewHeight = viewWidth * (canvasWidth / canvasHeight);
		float viewVEdge = viewHEdge * (canvasWidth / canvasHeight);
		int number = 0;

		int i = 0;
		for (; i < videoUids.size(); i++) {
			regionList[i] = layout.new Region();
			if (maxResolutionUid == videoUids.get(i)) {
				log.info("adjustVideo7Layout equal with configured user uid:" + maxResolutionUid);
				flag = true;
				setMaxResolutionUid(number, maxResolutionUid, regionList, 6.f / 7);
				number++;
				continue;
			}
			regionList[number].uid = videoUids.get(i);
			float yIndex = flag ? ((float) number - 1 % 6) : ((float) (number % 6));
			regionList[number].x = 6.f / 7;
			regionList[number].y = (1.f / 6) * yIndex;
			regionList[number].width = (1.f / 7);
			regionList[number].height = (1.f / 6);
			regionList[number].alpha = (double) number;
			regionList[number].renderMode = 0;
			number++;
			if (i == 6 && !flag) {
				changeToVideo9Layout(maxResolutionUid, regionList, layout, videoUids);
			}
		}

	}

	private void adjustVideo9Layout(long maxResolutionUid, VideoMixingLayout.Region[] regionList,
			VideoMixingLayout layout, Vector<Long> videoUids) {
		boolean flag = false;

		float canvasWidth = (float) width;
		float canvasHeight = (float) height;

		float viewWidth = 0.235f;
		float viewHEdge = 0.012f;
		float viewHeight = viewWidth * (canvasWidth / canvasHeight);
		float viewVEdge = viewHEdge * (canvasWidth / canvasHeight);
		int number = 0;

		int i = 0;
		for (; i < videoUids.size(); i++) {
			regionList[i] = layout.new Region();
			if (maxResolutionUid == videoUids.get(i)) {
				log.info("adjustVideo9Layout equal with configured user uid:" + maxResolutionUid);
				flag = true;
				setMaxResolutionUid(number, maxResolutionUid, regionList, 9.f / 5);
				number++;
				continue;
			}
			regionList[number].uid = videoUids.get(i);
			float yIndex = flag ? ((float) (number - 1 % 8)) : ((float) (number % 8));
			regionList[number].x = 8.f / 9;
			regionList[number].y = (1.f / 8) * yIndex;
			regionList[number].width = 1.f / 9;
			regionList[number].height = 1.f / 8;
			regionList[number].alpha = (double) number;
			regionList[number].renderMode = 0;
			number++;
			if (i == 8 && !flag) {
				changeToVideo17Layout(maxResolutionUid, regionList, layout, videoUids);
			}
		}
	}

	private void adjustVideo17Layout(long maxResolutionUid, VideoMixingLayout.Region[] regionList,
			VideoMixingLayout layout, Vector<Long> videoUids) {
		boolean flag = false;
		float canvasWidth = (float) width;
		float canvasHeight = (float) height;

		float viewWidth = 0.235f;
		float viewHEdge = 0.012f;
		float viewHeight = viewWidth * (canvasWidth / canvasHeight);
		float viewVEdge = viewHEdge * (canvasWidth / canvasHeight);
		int number = 0;
		log.info("adjustVideo17Layoutenter videoUids size is:" + videoUids.size() + ", maxResolutionUid:"
				+ maxResolutionUid);
		for (int i = 0; i < videoUids.size(); i++) {
			regionList[i] = layout.new Region();
			if (maxResolutionUid == videoUids.get(i)) {
				flag = true;
				setMaxResolutionUid(number, maxResolutionUid, regionList, 0.8);
				number++;
				continue;
			}
			if (!flag && i == 16) {
				log.info("Not the configured uid, and small regions is sixteen, so ignore this user:"
						+ videoUids.get(i));
				break;
			}

			regionList[number].uid = videoUids.get(i);
			// float xIndex = 0.833f;
			float yIndex = flag ? ((float) ((number - 1) % 8)) : ((float) (number % 8));
			regionList[number].x = ((flag && i > 8) || (!flag && i >= 8)) ? (9.f / 10) : (8.f / 10);
			regionList[number].y = (1.f / 8) * yIndex;
			regionList[number].width = 1.f / 10;
			regionList[number].height = 1.f / 8;
			regionList[number].alpha = (double) number;
			regionList[number].renderMode = 0;
			number++;
		}
	}

	private void WriteBytesToFileClassic(long uid, byte[] byteBuffer, long size, boolean isAudio) {
		if (byteBuffer == null) {
			log.info("WriteBytesToFileClassic but byte buffer is null!");
			return;
		}
		synchronized (this) {
			try {
				UserInfo info = isAudio ? audioChannels.get(Long.toString(uid)) : videoChannels.get(Long.toString(uid));
				if (info != null) {
					long curTs = System.currentTimeMillis();
					if (isAudio) {
						if (keepMediaTime > 0 && (curTs - lastKeepAudioTime) / 1000 >= keepMediaTime) {
							// System.out.printf("rewrite audio file:%s\n", info.fileName);
							info.channel.close();
							info.channel = new FileOutputStream(info.fileName, false);
							lastKeepAudioTime = curTs;
						}
					} else {
						if (keepMediaTime > 0 && (curTs - lastKeepVideoTime) / 1000 >= keepMediaTime) {
							// System.out.printf("rewrite video file:%s\n", info.fileName);
							info.channel.close();
							info.channel = new FileOutputStream(info.fileName, false);
							lastKeepVideoTime = curTs;
						}
					}
					info.channel.write(byteBuffer, 0, (int) size);
					info.channel.flush();
					info.last_receive_time = System.currentTimeMillis();
				} else {
					log.info("Channel is null");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean createChannel(String token, int uid) {
		if(Objects.isNull(token)) {
			token = "";
		}
		RecordingConfig config = new RecordingConfig();
		config.channelProfile = recordingProperties.getChannelProfile();
		config.idleLimitSec = recordingProperties.getIdleLimitSec();
		config.isVideoOnly = recordingProperties.isVideoOnly();
		config.isAudioOnly = recordingProperties.isAudioOnly();
		config.isMixingEnabled = recordingProperties.isMixingEnabled();
		config.mixResolution = recordingProperties.getMixResolution();
		config.mixedVideoAudio = recordingProperties.getMixedVideoAudio();
		config.appliteDir = recordingProperties.getAppliteDir();
		config.recordFileRootDir = recordingProperties.getRecordFileRootDir();
		config.cfgFilePath = recordingProperties.getCfgFilePath();
		config.secret = recordingProperties.getSecret();
		config.decryptionMode = recordingProperties.getDecryptionMode();
		config.lowUdpPort = recordingProperties.getLowUdpPort();
		config.highUdpPort = recordingProperties.getHighUdpPort();
		config.captureInterval = recordingProperties.getCaptureInterval();
		config.audioIndicationInterval = recordingProperties.getAudioIndicationInterval();
		config.decodeAudio = recordingProperties.getDecodeAudio();
		config.decodeVideo = recordingProperties.getDecodeVideo();
		config.streamType = recordingProperties.getStreamType();
		config.triggerMode = recordingProperties.getTriggerMode();
		config.proxyType = recordingProperties.getProxyType();
		config.proxyServer = recordingProperties.getProxyServer();
		config.audioProfile = recordingProperties.getAudioProfile();
		config.defaultVideoBgPath = recordingProperties.getDefaultVideoBgPath();
		config.defaultUserBgPath = recordingProperties.getDefaultUserBgPath();
		config.autoSubscribe = recordingProperties.isAutoSubscribe();
		config.enableCloudProxy = recordingProperties.isEnableCloudProxy();
		config.enableIntraRequest = recordingProperties.isEnableIntraRequest();
		config.subscribeVideoUids = recordingProperties.getSubscribeVideoUids();
		config.subscribeAudioUids = recordingProperties.getSubscribeAudioUids();
		config.enableH265Support = recordingProperties.isEnableH265Support();

		if (config.decodeVideo == VIDEO_FORMAT_TYPE.VIDEO_FORMAT_ENCODED_FRAME_TYPE) {
			config.decodeVideo = VIDEO_FORMAT_TYPE.VIDEO_FORMAT_H264_FRAME_TYPE;
		}

		this.config = config;

		/*
		 * change log_config Facility per your specific purpose like
		 * agora::base::LOCAL5_LOG_FCLT Default:USER_LOG_FCLT.
		 *
		 * ars.setFacility(LOCAL5_LOG_FCLT);
		 */

		int logLevel = recordingProperties.getLogLevel();
		if (logLevel < 1) {
			logLevel = 1;
		}
		if (logLevel > 6) {
			logLevel = 6;
		}

		this.profile_type = recordingProperties.getChannelProfile();
		if (recordingProperties.isMixingEnabled() && !recordingProperties.isAudioOnly()) {
			String[] sourceStrArray = recordingProperties.getMixResolution().split(",");
			if (sourceStrArray.length != 4) {
				log.info("Illegal resolution:" + recordingProperties.getMixResolution());
				return false;
			}
			this.width = Integer.valueOf(sourceStrArray[0]).intValue();
			this.height = Integer.valueOf(sourceStrArray[1]).intValue();
		}

		String tmpEnv = System.getenv("KEEPMEDIATIME");
		if (tmpEnv != null && !tmpEnv.isEmpty()) {
			keepMediaTime = Integer.parseInt(tmpEnv);
			System.out.printf("Get system env:KEEPMEDIATIME string:%s, int value:%d\n", tmpEnv, keepMediaTime);
		} else {
			log.info("No system env:KEEPMEDIATIME");
		}

		// run jni event loop , or start a new thread to do it
		cleanTimer = new Timer();
		boolean isSuccess = false;
		if (userAccount.length() > 0) {
			log.info("Agora Local Recording createChannelWithUserAccount , AppId : {}, token : {}, channelId : {}, userAccount : {}, config : {}, logLevel : {}",
					agoraProperties.getAppId(), token, channelId, userAccount, config, logLevel);
			isSuccess = recordingSDKInstance.createChannelWithUserAccount(agoraProperties.getAppId(), token, channelId, userAccount, config, logLevel);
		} else {
			log.info("Agora Local Recording createChannel , AppId : {}, token : {}, channelId : {}, uid : {}, config : {}, logLevel : {}",
					agoraProperties.getAppId(), token, channelId, uid, config, logLevel);
			isSuccess = recordingSDKInstance.createChannel(agoraProperties.getAppId(), token, channelId, uid, config, logLevel);
		}
		if(isSuccess){
			log.info("Agora Local Recording ...");
		} else {
			log.error("Agora Local Recording Start Fail .");
		}
		cleanTimer.cancel();
		log.info("jni layer has been exited...");
		return isSuccess;
	}

	public RecordingResult leaveChannel() {
		boolean leaveState = recordingSDKInstance.leaveChannel();
		return RecordingResult.builder()
				.channelId(this.channelId)
				.leaveState(leaveState)
				.fps(this.fps)
				.kbps(this.kbps)
				.height(this.height)
				.width(this.width)
				.firstReceiveVideoTime(this.firstReceiveVideoTime)
				.firstReceiveVideoElapsed(this.firstReceiveVideoElapsed)
				.firstReceiveAudioTime(this.firstReceiveAudioTime)
				.firstReceiveAudioElapsed(this.firstReceiveAudioElapsed)
				.storageDir(this.storageDir)
				.build();
	}

}
