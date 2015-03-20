package inputDevices;

import twitter4j.DirectMessage;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
import twitter4j.UserList;
import twitter4j.UserStreamListener;

public class InputStream implements UserStreamListener{

	/**
	 * Twitterクラスのインスタンス
	 */
	private Twitter twitter;
	private InputTwitter input;



	/**
	 * コンストラクタ
	 * @param tw
	 * @param t
	 */
	public InputStream(Twitter tw,InputTwitter t){
		this.twitter = tw;
		this.input = t;

	}

	@Override
	public void onDeletionNotice(StatusDeletionNotice arg0) {
		// TODO 自動生成されたメソッド・スタブ
		System.out.println("---------------------------------1");

	}

	@Override
	public void onScrubGeo(long arg0, long arg1) {
		// TODO 自動生成されたメソッド・スタブ
		System.out.println("---------------------------------2");


	}

	@Override
	public void onStallWarning(StallWarning arg0) {
		// TODO 自動生成されたメソッド・スタブ
		System.out.println("---------------------------------3");


	}

	@Override
	public void onStatus(Status status) {
		// TODO 自動生成されたメソッド・スタブ
		System.out.println("4----------------------------------------------");
		String[] data = status.getText().split(" ");
		if(data[0].indexOf("@T_S_kome") != -1)
			this.input.tupdate("Twitter,receive mention");  
		/*else
			this.input.update("Twitter,HomeTimeLine;update");*/


	}

	@Override
	public void onTrackLimitationNotice(int arg0) {
		// TODO 自動生成されたメソッド・スタブ
		System.out.println("---------------------------------5");


	}

	@Override
	public void onException(Exception arg0) {
		// TODO 自動生成されたメソッド・スタブ
		System.out.println("---------------------------------6");
		System.out.println(arg0);


	}

	@Override
	public void onBlock(User arg0, User arg1) {
		// TODO 自動生成されたメソッド・スタブ
		System.out.println("---------------------------------7");


	}

	@Override
	public void onDeletionNotice(long arg0, long arg1) {
		// TODO 自動生成されたメソッド・スタブ
		System.out.println("---------------------------------8");


	}

	@Override
	public void onDirectMessage(DirectMessage message) {
		// TODO 自動生成されたメソッド・スタブ

//
//		try {
//			//if(!message.getSenderScreenName().equals(this.twitter.getScreenName()))
//				//this.input.update("Twitter,DirectMessage;update");
//		} catch (IllegalStateException e) {
//			// TODO 自動生成された catch ブロック
//			e.printStackTrace();
//		} catch (TwitterException e) {
//			// TODO 自動生成された catch ブロック
//			e.printStackTrace();
//		}



	}

	@Override
	public void onFavorite(User arg0, User arg1, Status arg2) {
		// TODO 自動生成されたメソッド・スタブ
		System.out.println("---------------------------------10");


	}

	@Override
	public void onFollow(User u1, User u2) {
		// TODO 自動生成されたメソッド・スタブ
		/*System.out.println("---------------------------------11");
		System.out.println(u1.getScreenName() + ":" + u1.getDescription());
		try {
			if(this.twitter.getScreenName().equals(u1.getScreenName()))
				this.input.update("Twitter,FollowList;update");
			else
				this.input.update("Twitter,FollowerList;update");
		} catch (IllegalStateException | TwitterException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}*/


	}

	@Override
	public void onFriendList(long[] arg0) {
		// TODO 自動生成されたメソッド・スタブ
		System.out.println("---------------------------------12");


	}

	@Override
	public void onUnblock(User arg0, User arg1) {
		// TODO 自動生成されたメソッド・スタブ
		System.out.println("---------------------------------13");


	}

	@Override
	public void onUnfavorite(User arg0, User arg1, Status arg2) {
		// TODO 自動生成されたメソッド・スタブ
		System.out.println("---------------------------------14");


	}

	@Override
	public void onUnfollow(User arg0, User arg1) {
		// TODO 自動生成されたメソッド・スタブ
		System.out.println("---------------------------------15");



	}

	@Override
	public void onUserListCreation(User arg0, UserList arg1) {
		// TODO 自動生成されたメソッド・スタブ
		System.out.println("---------------------------------16");
		//this.acquisition.setLists();

	}

	@Override
	public void onUserListDeletion(User arg0, UserList arg1) {
		// TODO 自動生成されたメソッド・スタブ
		System.out.println("---------------------------------17");


	}

	@Override
	public void onUserListMemberAddition(User arg0, User arg1, UserList arg2) {
		// TODO 自動生成されたメソッド・スタブ
		System.out.println("---------------------------------18");


	}

	@Override
	public void onUserListMemberDeletion(User arg0, User arg1, UserList arg2) {
		// TODO 自動生成されたメソッド・スタブ
		System.out.println("---------------------------------19");


	}

	@Override
	public void onUserListSubscription(User arg0, User arg1, UserList arg2) {
		// TODO 自動生成されたメソッド・スタブ
		System.out.println("---------------------------------20");


	}

	@Override
	public void onUserListUnsubscription(User arg0, User arg1, UserList arg2) {
		// TODO 自動生成されたメソッド・スタブ
		System.out.println("---------------------------------21");


	}

	@Override
	public void onUserListUpdate(User arg0, UserList arg1) {
		// TODO 自動生成されたメソッド・スタブ
		System.out.println("--------------------------------22");


	}

	@Override
	public void onUserProfileUpdate(User arg0) {
		// TODO 自動生成されたメソッド・スタブ
		System.out.println("---------------------------------23");


	}


}
