/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blackcat.coach.easemob.listener;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;


import com.blackcat.coach.R;
import com.blackcat.coach.activities.ChatActivity;
import com.blackcat.coach.easemob.basefunction.HXSDKHelper;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.VoiceMessageBody;

public class VoicePlayClickListener implements View.OnClickListener {
	private static final String TAG = "VoicePlayClickListener";
	EMMessage mMessage;
	VoiceMessageBody mVoiceBody;
	ImageView mVoiceIconView;
	ImageView mIvReadStaus;
	private AnimationDrawable mVoiceAnimation = null;
	MediaPlayer mMediaPlayer = null;
	private ChatType mChatType;
	private BaseAdapter mAdapter;
	private Context mContext;
	public static boolean mIsPlaying = false;
	public static VoicePlayClickListener mCurrentPlayListener = null;


	public VoicePlayClickListener(EMMessage message, ImageView v, ImageView iv_read_staus, BaseAdapter adapter, Context context) {
		mMessage = message;
		mVoiceBody = (VoiceMessageBody) message.getBody();
		mAdapter = adapter;
		mVoiceIconView = v;
		mChatType = message.getChatType();
		mContext = context;
		mIvReadStaus = iv_read_staus;
	}

	public void stopPlayVoice() {
		mVoiceAnimation.stop();
		if (mMessage.direct == EMMessage.Direct.RECEIVE) {
			mVoiceIconView.setImageResource(R.mipmap.chatfrom_voice_playing);
		} else {
			mVoiceIconView.setImageResource(R.mipmap.chatto_voice_playing);
		}
		// stop play voice
		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
			mMediaPlayer.release();
		}
		mIsPlaying = false;
		ChatActivity.playMsgId = null;
		mAdapter.notifyDataSetChanged();
	}

	public void playVoice(String filePath) {
		if (!(new File(filePath).exists())) {
			return;
		}
		ChatActivity.playMsgId = mMessage.getMsgId();
		AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);

		mMediaPlayer = new MediaPlayer();
		if (HXSDKHelper.getInstance().getModel().getSettingMsgSpeaker()) {
			audioManager.setMode(AudioManager.MODE_NORMAL);
			audioManager.setSpeakerphoneOn(true);
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
		} else {
			audioManager.setSpeakerphoneOn(false);// 关闭扬声器
			// 把声音设定成Earpiece（听筒）出来，设定为正在通话中
			audioManager.setMode(AudioManager.MODE_IN_CALL);
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
		}
		try {
			mMediaPlayer.setDataSource(filePath);
			mMediaPlayer.prepare();
			mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					// TODO Auto-generated method stub
					mMediaPlayer.release();
					mMediaPlayer = null;
					stopPlayVoice(); // stop animation
				}

			});
			mIsPlaying = true;
			mCurrentPlayListener = this;
			mMediaPlayer.start();
			showAnimation();

			// 如果是接收的消息
			if (mMessage.direct == EMMessage.Direct.RECEIVE) {
				try {
					if (!mMessage.isAcked) {
						mMessage.isAcked = true;
						// 告知对方已读这条消息
						if (mChatType != ChatType.GroupChat && mChatType != ChatType.ChatRoom)
							EMChatManager.getInstance().ackMessageRead(mMessage.getFrom(), mMessage.getMsgId());
					}
				} catch (Exception e) {
					mMessage.isAcked = false;
				}
				if (!mMessage.isListened() && mIvReadStaus != null &&
						mIvReadStaus.getVisibility() == View.VISIBLE) {
					mIvReadStaus.setVisibility(View.INVISIBLE);
					EMChatManager.getInstance().setMessageListened(mMessage);
				}


			}

		} catch (Exception e) {
		}
	}

	// show the voice playing animation
	private void showAnimation() {
		if (mMessage.direct == EMMessage.Direct.RECEIVE) {
			mVoiceIconView.setImageResource(R.anim.voice_from_icon);
		} else {
			mVoiceIconView.setImageResource(R.anim.voice_to_icon);
		}
		mVoiceAnimation = (AnimationDrawable) mVoiceIconView.getDrawable();
		mVoiceAnimation.start();
	}

	@Override
	public void onClick(View v) {
		//是否有语音正在播放
		if (mIsPlaying) {
			if (ChatActivity.playMsgId != null && ChatActivity.playMsgId.equals(mMessage.getMsgId())) {
				mCurrentPlayListener.stopPlayVoice();
				return;
			}
			mCurrentPlayListener.stopPlayVoice();
		}

		if (mMessage.direct == EMMessage.Direct.SEND) {
			// for sent msg, we will try to play the voice file directly
			playVoice(mVoiceBody.getLocalUrl());
		} else {
			if (mMessage.status == EMMessage.Status.SUCCESS) {
				File file = new File(mVoiceBody.getLocalUrl());
				if (file.exists() && file.isFile()) {
					playVoice(mVoiceBody.getLocalUrl());
				}

			} else if (mMessage.status == EMMessage.Status.INPROGRESS) {

			} else if (mMessage.status == EMMessage.Status.FAIL) {

				new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {
						EMChatManager.getInstance().asyncFetchMessage(mMessage);
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						super.onPostExecute(result);
						mAdapter.notifyDataSetChanged();
					}

				}.execute();

			}

		}
	}
}