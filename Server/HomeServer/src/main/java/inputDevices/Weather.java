package inputDevices;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import weather.WeatherModule;
/*
 * 今回の橋岡さんのでデモでは、取得時点での日付の天気が更新された場合のみ更新したということを知らせるため、
 * Timer内部クラスではそのような実装を行っています。
 *
 *
 */
public class Weather extends InputDevice{
	
	private boolean startFlag = false;

	/**
	 * WeatherModuleクラスのインスタンス
	 */
	private WeatherModule module;
	/**
	 * Timerクラスのインスタンス
	 */
	private Timer timer;

	/**
	 * コンストラクタ
	 */
	public Weather(InputDevicesManager inputMana){
		this.module = new WeatherModule();
		super.inputMana = inputMana;
		
		class MyWeatherThread implements Runnable {
			public void run() {
				while (true) {
					while (startFlag) {
						update("weather,NoUpdate");
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
	 * Timerを起動する
	 */
	public void start(){
		this.module.runstart(60000);; // 3,600,000
		this.timer = new Timer();
		this.timer.schedule(new TimeStar(this,this.module), 0, 1000);
		startFlag = true;
	}

	/**
	 * stopメソッド
	 * Timerを停止する
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
		 * 取得できる一週間分の天気予報
		 * 0 = 取得日, 1 = 1日後, 2 = 2日後, 3 = 3日後, 4 = 4日後, 5 = 5日後, 6 = 6日後, 7 = 7日後
		 */
		private String[] weath = new String[8];
		/**
		 * 取得できる一週間分の予想最高気温
		 * 0 = 取得日, 1 = 1日後, 2 = 2日後, 3 = 3日後, 4 = 4日後, 5 = 5日後, 6 = 6日後, 7 = 7日後
		 */
		private String[] maxTemp = new String[8];
		/**
		 * 取得できる一週間分の予想最低気温
		 * 0 = 取得日, 1 = 1日後, 2 = 2日後, 3 = 3日後, 4 = 4日後, 5 = 5日後, 6 = 6日後, 7 = 7日後
		 */
		private String[] minTemp = new String[8];
		/**
		 * 山城中部の注意報、または警報を保持する
		 */
		private String warning_alarm;
		/**
		 * Weatherクラスのインスタンス
		 */
		private Weather weather;
		/**
		 * WeatherModuleクラスのインスタンス
		 */
		private WeatherModule module;
		/**
		 * 更新されたデータを一時保管するリスト
		 */
		private ArrayList<String> list;

		/**
		 * コンストラクタ
		 * @param we
		 * @param md
		 */
		public TimeStar(Weather we,WeatherModule md){
			this.weather = we;
			this.module = md;
			this.list = new ArrayList();
			this.warning_alarm = NO_DATA;
			for(int i = 0; i < 8; i++){
				this.maxTemp[i] = NO_DATA;
				this.minTemp[i] = NO_DATA;
				this.weath[i] = NO_DATA;
			}
		}

		/**
		 * runメソッド
		 */
		public void run() {
			if(!this.weath[0].equals(this.module.getWeather(0))){
				this.weath[0] = this.module.getWeather(0);
				this.weather.update("weather,update weather report");
			}
			if(!this.warning_alarm.equals(this.module.getYahooWarningAlarm())){
				this.warning_alarm = this.module.getYahooWarningAlarm();
				if(this.warning_alarm.indexOf("あります") != -1){
					this.weather.update("weather,weather warning");
				}
			}
		}
	}
}
