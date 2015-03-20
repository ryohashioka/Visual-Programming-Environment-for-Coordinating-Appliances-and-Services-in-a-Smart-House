package inputDevices;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import yahoorain.YahooRainModule;

/*
 * 今回の橋岡さんのでデモでは、取得時点での降水強度が更新された場合のみ更新したということを知らせるため、
 * Timer内部クラスではそのような実装を行っています。
 *
 *
 */

public class YahooRain extends InputDevice{
	
	private boolean startFlag = false;
	
	/**
	 * YahooRainModuleクラスのインスタンス
	 */
	private YahooRainModule module;
	/**
	 * Timerクラスのインスタンス
	 */
	private Timer timer;

	/**
	 * コンストラクタ
	 */
	public YahooRain(InputDevicesManager inputMana){
		this.module = new YahooRainModule();
		
		super.inputMana = inputMana;
		
		class MyWeatherThread implements Runnable {
			public void run() {
				while (true) {
					while (startFlag) {
						update("rainfall,NoUpdate");
						try {
							Thread.sleep(15000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}
		new Thread(new MyWeatherThread()).start();
	}
	
	
	
	/**
	 * startメソッド
	 */
	public void start(){
		this.module.runStart(3600000);
		this.timer = new Timer();
		this.timer.schedule(new TimeStar(this,this.module), 1000, 1000);
		startFlag = true;
	}
	
	/**
	 * stopメソッド
	 */
	public void stop(){
		this.timer.cancel();
		this.timer = null;
		this.module.runStop();
		startFlag = false;
	}
	
	
	/**
	 * Timerの内部クラス
	 * @author sumidatomoyuki
	 *
	 */
	class TimeStar extends TimerTask {
		
		/**
		 * 初期化用文字列
		 */
		public static final String NO_DATA = "No data";
		/**
		 * YahooRainクラスのインスタンス
		 */
		private YahooRain rain;
		/**
		 * WeatherModuleクラスのインスタンス
		 */
		private YahooRainModule module;
		/**
		 * 実測日と実測値の文字列
		 */
		private String SurveyData;
		/**
		 * 予測日と予測値の文字列
		 * 0 = 10分後 , 1 = 20分後, 2 = 30分後, 3 = 40分後, 4 = 50分後, 5 = 60分後
		 */
		private String PredictedData[] = new String[6];
		/**
		 * 更新されたデータを一時保管するリスト
		 */
		private ArrayList<String> list;
		
		/**
		 * コンストラクタ
		 * @param we
		 * @param md
		 */
		public TimeStar(YahooRain yr,YahooRainModule md){
			this.rain = yr;
			this.module = md;
			this.list = new ArrayList();
			this.SurveyData = NO_DATA;
			for(int i = 0; i < 6;i++){
				this.PredictedData[i] = NO_DATA;
			}
			
		}
		
		/**
		 * runメソッド
		 */
        public void run() {
        	if(!this.SurveyData.equals(this.module.getSurveyData() + " " + this.module.getSurveyRainfall())){
        		this.SurveyData = this.module.getSurveyData() + " " + this.module.getSurveyRainfall();
        		this.rain.update("rainfall,update rainfall report");
        	}
        }
    }
}
