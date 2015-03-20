package controller;




public class DemoDisplayController extends OutputDevice{
	
	/**
	 * DemoTwitterクラスのインスタンス
	 */
	private DemoTwitter demo;
	
	/**
	 * コンストラクタ
	 */
	public DemoDisplayController(){
		this.demo = new DemoTwitter("ponponpain");
	}
	
	/**
	 * startメソッド
	 */
	public void start(){
//		this.demo = new DemoTwitter("ponponpain");
		this.demo.runStart();
	}
	
	/**
	 * stopメソッド
	 */
	public void stop(){
		this.demo.runStop();
		this.demo = null;
	}
	
	/**
	 * controlメソッド
	 * @param value
	 * @param place
	 */
	public void control(String value,String place){
		if(value.indexOf("twitter") != -1){
			System.out.println("DemoDisplay - twitter");
			this.showHashTimeLine();
		}
		if(value.indexOf("weather") != -1){
			System.out.println("DemoDisplay - weather");
			this.showWeather();
		}
		if(value.indexOf("rainfall") != -1){
			System.out.println("DemoDisplay - rainfall");
			this.showRain();
		}
		
	}
	
	/**
	 * Mentionを表示させるメソッド
	 */
	private void showMention(){
		this.demo.updateMention(20);
	}
	
	/**
	 * HashTimeLineを表示させるメソッド
	 */
	private void showHashTimeLine(){
		this.demo.updateHashTweet(20);
	}
	
	/**
	 * Weatherを表示させるメソッド
	 */
	private void showWeather(){
		this.demo.updateWeather();
	}
	
	/**
	 * Rainfallを表示させるメソッド
	 */
	private void showRain(){
		this.demo.updateRain();
	}
}
