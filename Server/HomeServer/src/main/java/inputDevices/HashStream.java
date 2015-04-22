/*
 * Copyright (c) 2015 Ryo Hashioka
 */
package inputDevices;


import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;

public class HashStream implements StatusListener{

	private InputTwitter input;
	
	/**
	 * コンストラクタ
	 * @param t
	 */
	public HashStream(InputTwitter t){
		this.input = t;
	}
	
	@Override
	public void onException(Exception arg0) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public void onDeletionNotice(StatusDeletionNotice arg0) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public void onScrubGeo(long arg0, long arg1) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public void onStallWarning(StallWarning arg0) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public void onStatus(Status st) {
		// TODO 自動生成されたメソッド・スタブ
		this.input.tupdate("Twitter,HashTimeLine;update");
		
	}

	@Override
	public void onTrackLimitationNotice(int arg0) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

}
