package controller;

import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.Twitter;

public class HashStream implements StatusListener{
	
	/**
	 * Twitterクラスのインスタンス
	 */
	private Twitter twitter;
	/**
	 * Contentsクラスのインスタンス
	 */
	private UserTwitter uPanel;
	
	public HashStream(Twitter tw,UserTwitter h){
		this.twitter = tw;
		this.uPanel = h;
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
	public void onStatus(Status sta) {
		// TODO 自動生成されたメソッド・スタブ
		//this.hPanel.add(st.getUser().getName() + "1359" + st.getUser().getScreenName() + "1359" + st.getUser().getProfileImageURL() + "1359" + st.getText());
		String[] ex = {sta.getUser().getName(),sta.getUser().getScreenName(),sta.getUser().getProfileImageURL(), sta.getText()};
		this.uPanel.setHashTweet(ex);
	}

	@Override
	public void onTrackLimitationNotice(int arg0) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	
}
