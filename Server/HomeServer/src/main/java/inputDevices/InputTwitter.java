/*
 * Copyright (c) 2015 Ryo Hashioka
 */
package inputDevices;

import twitter4j.FilterQuery;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

/*
 * 橋岡さんのデモでは、twitterからmentionとハッシュタグ付きツイートの更新のみを用いるということなので
 * それ以外の値の更新に関してはコメントアウトしています。
 */

public class InputTwitter extends InputDevice {

	private boolean startFlag = false;
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
	 * TwitterStreamクラスのインスタンス ユーザのTimeLine用
	 */
	private TwitterStream userTwitterStream;
	/**
	 * TwitterStreamクラスのインスタンス 特定Hashタグ用
	 */
	private TwitterStream HashStream;
	/**
	 * Twitterクラスのインスタンス
	 */
	private Twitter twitter;
	/**
	 * InputStreamクラスのインスタンス
	 */
	private InputStream listener;
	/**
	 * HashStreamクラスのインスタンス
	 */
	private HashStream hashListener;

	/**
	 * コンストラクタ #抜きでハッシュタグを指定してください
	 */
	public InputTwitter(InputDevicesManager inputMana) {
		String hashtag = "インタラクション2015";
		ConfigurationBuilder cb = new ConfigurationBuilder()
				.setOAuthConsumerKey(CONSUMER_KEY)
				.setOAuthConsumerSecret(CONSUMER_SECRET)
				.setOAuthAccessToken(ACCESS_TOKEN)
				.setOAuthAccessTokenSecret(ACCESS_TOKEN_SECRET);
		this.userTwitterStream = new TwitterStreamFactory(cb.build())
				.getInstance();

		ConfigurationBuilder cb2 = new ConfigurationBuilder()
				.setOAuthConsumerKey(CONSUMER_KEY)
				.setOAuthConsumerSecret(CONSUMER_SECRET)
				.setOAuthAccessToken(ACCESS_TOKEN)
				.setOAuthAccessTokenSecret(ACCESS_TOKEN_SECRET);
		this.twitter = new TwitterFactory(cb2.build()).getInstance();

		ConfigurationBuilder cb3 = new ConfigurationBuilder()
				.setOAuthConsumerKey(CONSUMER_KEY)
				.setOAuthConsumerSecret(CONSUMER_SECRET)
				.setOAuthAccessToken(ACCESS_TOKEN)
				.setOAuthAccessTokenSecret(ACCESS_TOKEN_SECRET);
		this.HashStream = new TwitterStreamFactory(cb3.build()).getInstance();

		this.listener = new InputStream(this.twitter, this);
		this.userTwitterStream.addListener(this.listener);
		this.userTwitterStream.user();

		this.hashListener = new HashStream(this);
		this.HashStream.addListener(this.hashListener);
		String[] track = { hashtag };
		FilterQuery filter = new FilterQuery();
		filter.track(track);
		this.HashStream.filter(filter);
		super.inputMana = inputMana;

		class MyTwitterThread implements Runnable {
			public void run() {
				while (true) {
					while (startFlag) {
						tupdate("Twitter,NoUpdate");
						try {
							Thread.sleep(15000);
							System.out.println("twitte----noupdate----");
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					try {
						Thread.sleep(15000);
						System.out.println("twitte----hoge----");
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		new Thread(new MyTwitterThread()).start();
	}

	/**
	 * hashtagを変更する
	 * 
	 * @param tag
	 */
	public void setHashTag(String tag) {

		this.HashStream.shutdown();
		this.HashStream.addListener(this.hashListener);
		String[] track = { tag };
		FilterQuery filter = new FilterQuery();
		filter.track(track);
		this.HashStream.filter(filter);
	}

	public void start() {
		startFlag = true;
	}

	public void stop() {
		startFlag = false;
	}

	public void tupdate(String str) {
		if (startFlag) {
			super.update(str);
		}
	}

	public void tupdate(String[] str) {
		if (startFlag) {
			super.update(str);
		}
	}

}
