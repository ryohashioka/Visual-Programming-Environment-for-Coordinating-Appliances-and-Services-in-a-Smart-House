/*
 * Copyright (c) 2015 Ryo Hashioka
 */
package controller;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.util.List;

import javax.swing.JFrame;

import twitter4j.DirectMessage;
import twitter4j.FilterQuery;
import twitter4j.PagableResponseList;
import twitter4j.Paging;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

public class DemoTwitter {

	/**
	 * コンシューマーキー
	 */
	private static final String CONSUMER_KEY = "";
	/**
	 * コンシューマーパスワード
	 */
	private static final String CONSUMER_SECRET = "";
	/**
	 * アクセストークン
	 */
	private static final String ACCESS_TOKEN = "";
	/**
	 * アクセストークンパスワード
	 */
	private static final String ACCESS_TOKEN_SECRET = "";
	/**
	 * TwitterStreamクラスのインスタンス
	 */
	private TwitterStream user;
	/**
	 * TwitterStreamクラスのインスタンス
	 */
	private TwitterStream hash;
	/**
	 * Twitterクラスのインスタンス
	 */
	private Twitter twitter;
	/**
	 * UserStreamクラスのインスタンス
	 * listener役割
	 */
	private UserStream uListener;
	/**
	 * HashStreamクラスのインスタンス
	 * listener役割
	 */
	private HashStream hListener;
	/**
	 * UserTwitterクラスのインスタンス
	 */
	private UserTwitter uPanel;


	/**
	 * コンストラクタ
	 * Proccesing画面起動、Twitter Streaming APIの起動を行う
	 */
	public DemoTwitter(String h){
		String HashTag = h;
		//下準備
		ConfigurationBuilder cb = new ConfigurationBuilder().setOAuthConsumerKey(CONSUMER_KEY)
				.setOAuthConsumerSecret(CONSUMER_SECRET)
				.setOAuthAccessToken(ACCESS_TOKEN)
				.setOAuthAccessTokenSecret(ACCESS_TOKEN_SECRET);
		this.user = new TwitterStreamFactory(cb.build()).getInstance();

		ConfigurationBuilder cb2 = new ConfigurationBuilder().setOAuthConsumerKey(CONSUMER_KEY)
				.setOAuthConsumerSecret(CONSUMER_SECRET)
				.setOAuthAccessToken(ACCESS_TOKEN)
				.setOAuthAccessTokenSecret(ACCESS_TOKEN_SECRET);
		this.twitter = new TwitterFactory(cb2.build()).getInstance();

		ConfigurationBuilder cb3 = new ConfigurationBuilder().setOAuthConsumerKey(CONSUMER_KEY)
				.setOAuthConsumerSecret(CONSUMER_SECRET)
				.setOAuthAccessToken(ACCESS_TOKEN)
				.setOAuthAccessTokenSecret(ACCESS_TOKEN_SECRET);
		this.hash = new TwitterStreamFactory(cb3.build()).getInstance();


		//ウィンドウ展開

		this.uPanel = new UserTwitter();
		JFrame myWindow = new JFrame("DEMO Display Application");
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
/*
		myWindow.setUndecorated(true);
		GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();*/
		//device.setFullScreenWindow(myWindow);

		myWindow.setBounds(0, 0, d.width, d.height);
		myWindow.setLayout(new BorderLayout());
		myWindow.add(this.uPanel, BorderLayout.CENTER);
		myWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		myWindow.setVisible(true);
		this.uPanel.init();
		/*
		JFrame hWindow = new JFrame("DEMO HASHTWITTER Application");
		hWindow.setLayout(new BorderLayout());
		hWindow.add(this.uPanel, BorderLayout.CENTER);
		hWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		hWindow.setSize(640, 480);
		hWindow.setBounds(0, 0, 640, 480);
		hWindow.setVisible(true);
		this.uPanel.init();*/

		/*
		//ツイートリスト初期化
		List<Status> st = null;
		Paging p = new Paging(1);
		p.setCount(50);
		try {
			st = twitter.getHomeTimeline(p);
		} catch (TwitterException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		for (Status status : st) {
			String[] ex = {status.getUser().getName(),status.getUser().getScreenName(),status.getUser().getProfileImageURL(), status.getText()};
			this.uPanel.tweetInit(ex);
		}

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		}
		this.uPanel.exShow();
		 */


		//Mentionリスト初期化
		List<Status> st = null;
		Paging p = new Paging(1);
		p.setCount(50);
		try {
			st = twitter.getMentionsTimeline(p);
		} catch (TwitterException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		for (Status status : st) {
			String[] ex = {status.getUser().getName(),status.getUser().getScreenName(),status.getUser().getProfileImageURL(), status.getText()};
			this.uPanel.mentionInit(ex);
		}



		/*
		//DMリスト初期化
		ResponseList<DirectMessage> dm = null;

		try {
			dm = this.twitter.getDirectMessages(p);
		} catch (TwitterException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		for (DirectMessage DM : dm) {
			//this.uPanel.dmInit(DM.getSender().getName() + "1359" + DM.getSenderScreenName() + "1359" + DM.getSender().getProfileImageURL() + "1359" + DM.getText());
			String[] ex = {DM.getSender().getName(),DM.getSenderScreenName(),DM.getSender().getProfileImageURL(),DM.getText()};
			this.uPanel.dmInit(ex);
		}



		//フォローリスト初期化
		PagableResponseList<User> fl = null;
		try {
			fl = this.twitter.getFriendsList(this.twitter.getId(), -1L);
		} catch (IllegalStateException | TwitterException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		}
		for(User us : fl){
			String[] ex = {us.getName(),us.getScreenName(),us.getProfileImageURL(),us.getDescription()};
			this.uPanel.followInit(ex);
		}

		//フォロワーリスト初期化
		fl = null;
		try {
			fl = this.twitter.getFollowersList(this.twitter.getId(), -1L);
		} catch (IllegalStateException | TwitterException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		}
		for(User us : fl){
			String[] ex = {us.getName(),us.getScreenName(),us.getProfileImageURL(),us.getDescription()};
			this.uPanel.followerInit(ex);
		}

		 */
		//ハッシュツイートリスト初期化
		Query query = new Query();
		query.setQuery("#" + HashTag);
		// 1度のリクエストで取得するTweetの数（100が最大）
		query.setCount(20);
		QueryResult result = null;
		try {
			result = twitter.search(query);
		} catch (TwitterException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		for (Status sta : result.getTweets()) {
			String r = sta.getText().replaceAll("\n", " ");
			String str = "";
			if(r.length() > 60){
				str += r.substring(0,30) + "\n" + r.substring(30,60);
			}else{
				str = r;
			}
			String[] ex = {sta.getUser().getName(),sta.getUser().getScreenName(),sta.getUser().getProfileImageURL(), str};
			this.uPanel.hashInit(ex);
		}




		//リスナーセット
		this.uListener = new UserStream(this.twitter,this.uPanel);
		this.user.addListener(this.uListener);
		this.user.user();

		this.hListener = new HashStream(this.twitter,this.uPanel);
		this.hash.addListener(this.hListener);
		String[] track = { HashTag };
		FilterQuery filter = new FilterQuery();
		filter.track( track );
		this.hash.filter( filter );


	}
	
	public void runStart(){
		this.uPanel.runStart();
	}

	
	public void runStop(){
		this.uPanel.runStop();
	}
	/**
	 * Proccesingで普通のツイート表示させるようにする命令メソッド
	 */
	public void updateTweet(){

	}

	/**
	 * processingでHashTag付きツイートを表示させるような命令メソッド
	 */
	public void updateHashTweet(int f){
		this.uPanel.setFlag(6,f);
	}

	/**
	 * ProcessindでDMを表示させるような命令メソッド
	 */
	public void updateDirectMessage(){
		//	this.uPanel.setFlag(3);
	}

	/**
	 * Proccesingで最新のフォロワーを表示させるような命令メソッド
	 */
	public void updateFollwer(){
		//this.uPanel.setFlag(5);
	}

	/**
	 * Processingで最新のフォローを表示させるような命令メソッド
	 */
	public void updateFollow(){
		//this.uPanel.setFlag(4);
	}

	/**
	 * Processingで最新のMentionsを表示させる命令メソッド
	 */
	public void updateMention(int f){
		this.uPanel.setFlag(2,f);
	}


	public void updateWeather(){
		this.uPanel.setWFlag();
	}

	public void updateRain(){
		this.uPanel.setRFlag();
	}
	/**
	 * 取得したいハッシュタグを変更するメソッド
	 * @param hash
	 */
	public void setHashTag(String HashTag){
		//this.hContents.init();
		//ハッシュツイートリスト初期化
		Query query = new Query();
		query.setQuery("#" + HashTag);
		// 1度のリクエストで取得するTweetの数（100が最大）
		query.setCount(20);
		QueryResult result = null;
		try {
			result = twitter.search(query);
		} catch (TwitterException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		for (Status sta : result.getTweets()) {
			String[] ex = {sta.getUser().getName(),sta.getUser().getScreenName(),sta.getUser().getProfileImageURL(), sta.getText()};
			this.uPanel.hashInit(ex);
		}

		//ストリームの再起動
		this.hash.shutdown();
		this.hash.addListener(this.hListener);
		String[] track = { HashTag };
		FilterQuery filter = new FilterQuery();
		filter.track( track );
		this.hash.filter( filter );
	}
}
