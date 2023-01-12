package com.open.capacity.common.agora.recording;

import com.open.capacity.common.agora.recording.common.Common.AudioFrame;
import com.open.capacity.common.agora.recording.common.Common.VideoFrame;
import com.open.capacity.common.agora.recording.common.Common.AudioVolumeInfo;
import com.open.capacity.common.agora.recording.common.Common.RemoteAudioStats;
import com.open.capacity.common.agora.recording.common.Common.RemoteVideoStats;
import com.open.capacity.common.agora.recording.common.Common.RecordingStats;
import com.open.capacity.common.agora.recording.common.Common.REMOTE_STREAM_STATE;
import com.open.capacity.common.agora.recording.common.Common.REMOTE_STREAM_STATE_CHANGED_REASON;
import com.open.capacity.common.agora.recording.common.Common.CONNECTION_STATE_TYPE;
import com.open.capacity.common.agora.recording.common.Common.CONNECTION_CHANGED_REASON_TYPE;

import com.open.capacity.common.agora.recording.common.RecordingConfig;

/**
 * @author: <a href="https://github.com/hiwepy">hiwepy</a>
 */
public interface RecordingEventHandler {

	String getChannel();

	/**
	 * This callback is triggered when a user leaves the channel.
	 *
	 * @param reason The reasons why the recording server leaves the channel. See
	 *               {@link com.open.capacity.common.agora.recording.common.Common#LEAVE_PATH_CODE
	 *               LEAVE_PATH_CODE}.
	 */
	void onLeaveChannel(int reason);

	/**
	 * This callback is triggered when an error occurrs during SDK runtime.
	 *
	 * The SDK cannot fix the issue or resume running, which requires intervention
	 * from the application and informs the user on the issue.
	 *
	 * @param error     {@link com.open.capacity.common.agora.recording.common.Common#ERROR_CODE_TYPE
	 *                  Error codes}.
	 * @param stat_code {@link com.open.capacity.common.agora.recording.common.Common#STAT_CODE_TYPE
	 *                  State codes}.
	 */
	void onError(int error, int stat_code);

	/**
	 * This callback is triggered when a warning occurrs during SDK runtime.
	 *
	 * In most cases, the application can ignore the warnings reported by the SDK
	 * because the SDK can usually fix the issue and resume running.
	 *
	 * @param warn {@link com.open.capacity.common.agora.recording.common.Common#WARN_CODE_TYPE
	 *             Warning codes}.
	 */
	void onWarning(int warn);

	/**
	 * This callback is triggered when the recording server successfully joins the
	 * specified channel with an assigned Channel ID and user ID.
	 *
	 * @param channelId The Channel ID assigned based on the channel name specified
	 *                  in {@link RecordingSDK#createChannel() createChannel}.
	 * @param uid       User ID of the user.
	 */
	void onJoinChannelSuccess(String channelId, long uid);

	/**
	 * This callback is triggered when the state of a remote user's video stream
	 * changes.
	 *
	 * @param uid    The `uid` of the remote user.
	 * @param state  Indicates the current state of the remote user's video stream.
	 *               For details, see {@link REMOTE_STREAM_STATE
	 *               REMOTE_STREAM_STATE}.
	 * @param reason Indicates the reason causing the state change. For details, see
	 *               {@link REMOTE_STREAM_STATE_CHANGED_REASON
	 *               REMOTE_STREAM_STATE_CHANGED_REASON}.
	 */
	void onRemoteVideoStreamStateChanged(long uid, REMOTE_STREAM_STATE state,
			REMOTE_STREAM_STATE_CHANGED_REASON reason);

	/**
	 * This callback is triggered when the state of a remote user's audio stream
	 * changes.
	 *
	 * @param uid    The `uid` of the remote user.
	 * @param state  Indicates the current state of the remote user's audio stream.
	 *               For details, see {@link REMOTE_STREAM_STATE
	 *               REMOTE_STREAM_STATE}.
	 * @param reason Indicates the reason causing the state change. For details, see
	 *               {@link REMOTE_STREAM_STATE_CHANGED_REASON
	 *               REMOTE_STREAM_STATE_CHANGED_REASON}.
	 */
	void onRemoteAudioStreamStateChanged(long uid, REMOTE_STREAM_STATE state,
			REMOTE_STREAM_STATE_CHANGED_REASON reason);

	/**
	 * This callback is triggered when a user leaves the channel or goes offline.
	 *
	 * The SDK reads the timeout data to determine if a user leaves the channel (or
	 * goes offline). If no data package is received from the user within 15
	 * seconds, the SDK assumes the user is offline. A poor network connection may
	 * lead to false detections, so use signaling for reliable offline detection.
	 *
	 * @param uid    The `uid` of the user.
	 * @param reason The rerasons why the user leaves the channel or goes offline.
	 *               See
	 *               {@link com.open.capacity.common.agora.recording.common.Common#USER_OFFLINE_REASON_TYPE
	 *               USER_OFFLINE_REASON_TYPE}.
	 */
	void onUserOffline(long uid, int reason);

	/**
	 * This callback is triggered when a remote user/host joins the channel and
	 * reports the UID of the new user.
	 *
	 * <ul>
	 * <li>Communication profile: This callback notifies the recording server that a
	 * remote user joins the channel and reports the user's UID and
	 * information.</li>
	 * <li>Live broadcast profile: This callback notifies the recording server that
	 * a host joins the channel and reports the user's UID and information.</li>
	 * </ul>
	 *
	 * If there are users/hosts in the channel before the recording server joins the
	 * channel, the SDK also reports on the UIDs and information of the existing
	 * users/hosts. This callback is triggered as many times as the number of the
	 * users/hosts in the channel.
	 *
	 * @param uid          The `uid` of the new user/host.
	 * @param recordingDir The relative path of the recorded files and recording
	 *                     log.
	 */
	void onUserJoined(long uid, String recordingDir);

	/**
	 * This callback reports the user who speaks loudest.
	 *
	 * If you set the
	 * {@link io::agora::recording::common::RecordingConfig#audioIndicationInterval
	 * audioIndicationInterval} parameter in
	 * {@link io::agora::recording::common::RecordingConfig RecordingConfig} to be
	 * greater than 0, this callback returns the `uid` of the user with the highest
	 * voice volume over a certain time period.
	 *
	 * @param uid The `uid` of the user with the highest voice volume over a certain
	 *            time period.
	 */
	void onActiveSpeaker(long uid);

	/**
	 * Occurs when the raw audio data is received. If you set
	 * {@link io::agora::recording::common::RecordingConfig::decodeAudio
	 * decodeAudio} in {@link io::agora::recording::common::RecordingConfig
	 * RecordingConfig} as `1`, `2`, or `3`, you receive audio frames from this
	 * callback.
	 *
	 * @param uid   The `uid` of the user.
	 * @param frame Received raw audio data in PCM or AAC format. See
	 *              {@link AudioFrame AudioFrame}.
	 */
	void audioFrameReceived(long uid, AudioFrame frame);

	/**
	 * Occurs when the video frame is received.
	 *
	 * If you set {@link io::agora::recording::common::RecordingConfig::decodeVideo
	 * decodeVideo} in {@link io::agora::recording::common::RecordingConfig
	 * RecordingConfig} as `1`, `2`, or `3`, you receive raw video data from this
	 * callback.
	 *
	 * If you set {@link io::agora::recording::common::RecordingConfig::decodeVideo
	 * decodeVideo} in {@link io::agora::recording::common::RecordingConfig
	 * RecordingConfig} as `4` or `5`, you receive video frames in JPG file format
	 * from this callback.
	 *
	 * This callback is triggered for every received video frame and can be used to
	 * detect sexually explicit content, if necessary.
	 *
	 * Agora recommends capturing the I frame only and neglecting the others.
	 *
	 * @param uid      The `uid` of the remote user as specified in the
	 *                 createChannel() method. If no `uid` is previously assigned,
	 *                 the Agora server automatically assigns a uid.
	 * @param type     The format of the received video frame:
	 *                 <ul>
	 *                 <li>0: YUV</li>
	 *                 <li>1: H.264</li>
	 *                 <li>2: JPG</li>
	 *                 <li>3: H.265</li>
	 *                 <li>4: JPG file</li>
	 *                 </ul>
	 * @param frame    Received video frame. See {@link VideoFrame VideoFrame}.
	 * @param rotation Rotational angle: 0, 90, 180, or 270.
	 */
	void videoFrameReceived(long uid, int type, VideoFrame frame, int rotation);

	/**
	 * This callback reports the relative path of the recorded files.
	 *
	 * @param path The relative path of the recorded files.
	 */
	void recordingPathCallBack(String path);

	/**
	 * This callback reports the list of users who are speaking and their volumes.
	 *
	 * This callback works only when {@link RecordingConfig#audioIndicationInterval
	 * audioIndicationInterval} > 0.
	 *
	 * @param infos An array containing the user ID and volume information for each
	 *              speaker. For more information, see
	 *              {@link com.open.capacity.common.agora.recording.common.Common#AudioVolumeInfo
	 *              AudioVolumeInfo}.
	 */
	void onAudioVolumeIndication(AudioVolumeInfo[] infos);

	/**
	 * This callback is triggered when the first remote video frame is received and
	 * decoded.
	 *
	 * @param uid     The user ID.
	 * @param width   The width of the video frame.
	 * @param height  The height of the video frame.
	 * @param elapsed Time elapsed (ms) from the local user calling
	 *                {@link RecordingSDK#createChannel() createChannel} until this
	 *                callback is triggered.
	 */
	void onFirstRemoteVideoDecoded(long uid, int width, int height, int elapsed);

	/**
	 * This callback is triggered when the first remote audio frame is received.
	 *
	 * @param uid     The user ID.
	 * @param elapsed Time elapsed (ms) from the local user calling
	 *                {@link RecordingSDK#createChannel() createChannel} until this
	 *                callback is triggered.
	 */
	void onFirstRemoteAudioFrame(long uid, int elapsed);

	/**
	 * This callback is triggered when the status of receiving the audio or video
	 * stream changes.
	 *
	 * @param receivingAudio Whether or not the recording server is receiving the
	 *                       audio stream.
	 * @param receivingVideo Whether or not the recording server is receiving the
	 *                       video stream.
	 */
	void onReceivingStreamStatusChanged(boolean receivingAudio, boolean receivingVideo);

	/**
	 * This callback is triggered when the SDK cannot reconnect to Agora's edge
	 * server 10 seconds after its connection to the server is interrupted.
	 *
	 * The SDK triggers this callback when it cannot connect to the server 10
	 * seconds after calling {@link RecordingSDK#createChannel() createChannel}
	 * regardless of whether it is in the channel or not.
	 *
	 * This callback is different from
	 * {@link RecordingEventHandler#onConnectionInterrupted
	 * onConnectionInterrupted}:
	 * <ul>
	 * <li>The SDK triggers the {@link RecordingEventHandler#onConnectionInterrupted
	 * onConnectionInterrupted} callback when the SDK loses connection with the
	 * server for more than 4 seconds after it joins the channel.</li>
	 * <li>The SDK triggers the {@link RecordingEventHandler#onConnectionLost
	 * onConnectionLost} callback when the SDK loses connection with the server for
	 * more than 10 seconds, regardless of whether it joins the channel or not.</li>
	 * </ul>
	 * For both callbacks, the SDK tries to reconnect to the server until the
	 * application calls {@link RecordingSDK#leaveChannel() leaveChannel}.
	 */
	void onConnectionLost();

	/**
	 * This callback is triggered when the connection between the SDK and the server
	 * is interrupted.
	 *
	 * The SDK triggers this callback when it cannot connect to the server 10
	 * seconds after calling {@link RecordingSDK#createChannel() createChannel}
	 * regardless of whether it is in the channel or not.
	 *
	 * This callback is different from {@link RecordingEventHandler#onConnectionLost
	 * onConnectionLost}:
	 * <ul>
	 * <li>The SDK triggers the {@link RecordingEventHandler#onConnectionInterrupted
	 * onConnectionInterrupted} callback when the SDK loses connection with the
	 * server for more than 4 seconds after it joins the channel.</li>
	 * <li>The SDK triggers the {@link RecordingEventHandler#onConnectionLost
	 * onConnectionLost} callback when the SDK loses connection with the server for
	 * more than 10 seconds, regardless of whether it joins the channel or not.</li>
	 * </ul>
	 * For both callbacks, the SDK tries to reconnect to the server until the
	 * application calls {@link RecordingSDK#leaveChannel() leaveChannel}.
	 */
	void onConnectionInterrupted();

	/**
	 * This callback is triggered when the recording server rejoins the channel
	 * after being disconnected due to network problems.
	 *
	 * When the recording server loses connection with the server because of network
	 * problems, the SDK automatically tries to reconnect and triggers this callback
	 * upon reconnection.
	 *
	 * @param channelId The channel name.
	 *
	 * @param uid       The `uid` of the recording server.
	 */
	void onRejoinChannelSuccess(String channelId, long uid);

	/**
	 * This callback is triggered when the network connection state changes.
	 *
	 * @param state  The current network connection state. See
	 *               {@link CONNECTION_STATE_TYPE CONNECTION_STATE_TYPE}.
	 * @param reason The reason causing the change of the connection state. See
	 *               {@link CONNECTION_CHANGED_REASON_TYPE
	 *               CONNECTION_CHANGED_REASON_TYPE}.
	 */
	void onConnectionStateChanged(CONNECTION_STATE_TYPE state, CONNECTION_CHANGED_REASON_TYPE reason);

	/**
	 * This callback reports the statistics of the video stream from the remote user
	 * (communication profile)/host (live broadcast profile).
	 *
	 * The SDK triggers this callback once every two seconds for each remote user
	 * (communication profile)/host (live broadcast profile). If a channel includes
	 * multiple remote users/hosts, the SDK triggers this callback as many times.
	 *
	 * @param uid   The `uid` of the user sending the video stream.
	 *
	 * @param stats The statistics of the received remote video stream. See
	 *              {@link RemoteVideoStats RemoteVideoStats}.
	 */
	void onRemoteVideoStats(long uid, RemoteVideoStats stats);

	/**
	 * This callback reports the statistics of the audio stream from the remote user
	 * (communication profile)/host (live broadcast profile).
	 *
	 * The SDK triggers this callback once every two seconds for each remote user
	 * (communication profile)/host (live broadcast profile). If a channel includes
	 * multiple remote users, the SDK triggers this callback as many times.
	 *
	 * @param uid   The `uid` of the user sending the audio stream.
	 *
	 * @param stats The statistics of the received remote audio stream. See
	 *              {@link RemoteAudioStats RemoteAudioStats}.
	 */
	void onRemoteAudioStats(long uid, RemoteAudioStats stats);

	/**
	 * This callback reports the statistics of the recording once every two seconds.
	 *
	 * @param stats See {@link RecordingStats RecordingStats}.
	 */
	void onRecordingStats(RecordingStats stats);

	/**
	 * Occurs when the recording server successfully registers a user account by
	 * calling the {@link RecordingSDK.createChannelWithUserAccount
	 * createChannelWithUserAccount} method. This callback reports the user ID and
	 * user account of the recording server.
	 *
	 * @param uid         The `uid` of the recording server.
	 * @param userAccount The user account of the recording server.
	 */
	void onLocalUserRegistered(long uid, String userAccount);

	/**
	 * Occurs when the SDK gets the user ID and user account of a remote user.
	 *
	 * After a remote user joins the channel, the SDK gets the UID and user account
	 * of the remote user and triggers this callback on the local client.
	 *
	 * @param uid         The `uid` of the remote user.
	 * @param userAccount The user account of the remote user.
	 */
	void onUserInfoUpdated(long uid, String userAccount);
}
