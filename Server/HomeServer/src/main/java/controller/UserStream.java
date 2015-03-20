package controller;

import twitter4j.DirectMessage;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
import twitter4j.UserList;
import twitter4j.UserStreamListener;

public class UserStream implements UserStreamListener{

	private Twitter twitter;
	private UserTwitter uPanel;
	/**
	 * コンストラクタ
	 * @param tw
	 * @param c
	 */
	public UserStream(Twitter tw,UserTwitter u){
		this.twitter = tw;
		this.uPanel = u;
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
	public void onStatus(Status status) {
		// TODO 自動生成されたメソッド・スタブ
		String[] data = status.getText().split(" ");
		if(data[0].indexOf("@T_S_kome") != -1){
			//this.uPanel.setMention(st.getUser().getName() + "1359" + st.getUser().getScreenName() + "1359" + st.getUser().getProfileImageURL() + "1359" + st.getText());
			String[] ex = {status.getUser().getName(),status.getUser().getScreenName(),status.getUser().getProfileImageURL(), status.getText()};
			this.uPanel.setMention(ex);
		}
		else{
			//this.uPanel.setTweet(st.getUser().getName() + "1359" + st.getUser().getScreenName() + "1359" + st.getUser().getProfileImageURL() + "1359" + st.getText());
			String[] ex = {status.getUser().getName(),status.getUser().getScreenName(),status.getUser().getProfileImageURL(), status.getText()};
			this.uPanel.setTweet(ex);
		}
	}

	@Override
	public void onTrackLimitationNotice(int arg0) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void onException(Exception arg0) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void onBlock(User arg0, User arg1) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void onDeletionNotice(long arg0, long arg1) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void onDirectMessage(DirectMessage DM) {
		// TODO 自動生成されたメソッド・スタブ

		try {
			if(!DM.getSenderScreenName().equals(this.twitter.getScreenName())){
				String[] ex = {DM.getSender().getName(),DM.getSenderScreenName(),DM.getSender().getProfileImageURL(),DM.getText()};
				this.uPanel.setDM(ex);
			}
		} catch (IllegalStateException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (TwitterException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}



	}

	@Override
	public void onFavorite(User arg0, User arg1, Status arg2) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void onFollow(User u1, User u2) {
		// TODO 自動生成されたメソッド・スタブ

		try {
			if(this.twitter.getScreenName().equals(u1.getScreenName())){
				//System.out.println(u2.getName() + "受信しとる");
				//this.uPanel.setFollow(u2.getName() + "1359" + u2.getScreenName() + "1359" + u2.getProfileImageURL() + "1359" + u2.getDescription());
				String[] ex = {u2.getName(),u2.getScreenName(),u2.getProfileImageURL(),u2.getDescription()};
				this.uPanel.setFollow(ex);
			}else{
				//this.contents.setFollower("update");
				//this.uPanel.setFollow(u1.getName() + "1359" + u1.getScreenName() + "1359" + u1.getProfileImageURL() + "1359" + u1.getDescription());
				String[] ex = {u1.getName(),u1.getScreenName(),u1.getProfileImageURL(),u1.getDescription()};
				this.uPanel.setFollower(ex);
			}
		} catch (IllegalStateException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (TwitterException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}


	}

	@Override
	public void onFriendList(long[] arg0) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void onUnblock(User arg0, User arg1) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void onUnfavorite(User arg0, User arg1, Status arg2) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void onUnfollow(User arg0, User arg1) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void onUserListCreation(User arg0, UserList arg1) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void onUserListDeletion(User arg0, UserList arg1) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void onUserListMemberAddition(User arg0, User arg1, UserList arg2) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void onUserListMemberDeletion(User arg0, User arg1, UserList arg2) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void onUserListSubscription(User arg0, User arg1, UserList arg2) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void onUserListUnsubscription(User arg0, User arg1, UserList arg2) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void onUserListUpdate(User arg0, UserList arg1) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void onUserProfileUpdate(User arg0) {
		// TODO 自動生成されたメソッド・スタブ

	}

}
