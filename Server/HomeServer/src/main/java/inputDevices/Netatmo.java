/*
 * Copyright (c) 2015 Ryo Hashioka
 */
package inputDevices;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import atmo.NetatmoModule;

public class Netatmo extends InputDevice{
	
	/**
	 * NetatmoModuleのインスタンス
	 */
	private NetatmoModule module;
	/**
	 * Timerのインスタンス
	 */
	private Timer timer;
	
	/**
	 * コンストラクタ
	 */
	public Netatmo(InputDevicesManager inputMana){
		module = new NetatmoModule();
		//start();
		super.inputMana = inputMana;
	}
	
	/**
	 * 更新メソッド
	 * @param data
	 */
//	public void update(String data) {
//		// TODO 自動生成されたメソッド・スタブ
//		System.out.println(data);
//	}
	
	/**
	 * 更新メソッド
	 * @param data
	 */
//	public void update(String[] data) {
//		// TODO 自動生成されたメソッド・スタブ
//		for(int i = 0;i < data.length; i++){
//		System.out.println(data[i]);
//		}
//	}
	
	/**
	 * スタートメソッド
	 */
	public void start(){
		module.runStart(15000);
		this.timer = new Timer();
		this.timer.schedule(new TimeStar(this,this.module),0, 1000);
	}
	
	/**
	 * ストップメソッド
	 */
	public void stop(){
		this.timer.cancel();
//		this.timer = null;
		module.runStop();
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
		private static final String NO_DATA = "nodata";
		/**
		 * SCALE
		 */
		private String[] SCALE_TIME = {"Thirty_Minute", "One_Hour", "Three_Hours", "One_Day" , "One_Week", "One_Month"};
		/**
		 * Netatmoクラスのインスタンス
		 */
		private Netatmo atmo;
		/**
		 * NetatmoModuleクラスのインスタンス
		 */
		private NetatmoModule module;
		/**
		 * SCALE_MAX時の室温を保持する変数
	 	 */
		private String InTemperature;
		/**
		 * SCALE_MAX時のCO2を保持する変数
		 */
		private String InCO2;
		/**
		 * SCALE_MAX時の湿度を保持する変数
		 */
		private String InHumidity;
		/**
		 * SCALE_MAX時の気圧を保持する変数
		 */
		private String InPressure;
		/**
		 * SCALE_MAX時の騒音を保持する変数
		 */
		private String InNoise;
		/**
		 * 0 = SCALE_THIRTY_MINUTES , 1 = SCALE_ONE_HOUR, 2 = SCALE_THREE_HOURS, 3 = SCALE_ONE_DAY , 4 = SCALE_ONE_WEEK, 5 = SCALE_ONE_MONTH
		 * 各期間の平均室温を保持する配列変数
		 */
		private String[] aveTemp = new String[6];
		/**
		 * 0 = SCALE_THIRTY_MINUTES , 1 = SCALE_ONE_HOUR, 2 = SCALE_THREE_HOURS, 3 = SCALE_ONE_DAY , 4 = SCALE_ONE_WEEK, 5 = SCALE_ONE_MONTH
		 * 各期間の平均CO2を保持する配列変数
		 */
		private String[] aveCO2 = new String[6];
		/**
		 * 0 = SCALE_THIRTY_MINUTES , 1 = SCALE_ONE_HOUR, 2 = SCALE_THREE_HOURS, 3 = SCALE_ONE_DAY , 4 = SCALE_ONE_WEEK, 5 = SCALE_ONE_MONTH
		 * 各期間の平均湿度を保持する配列変数
		 */
		private String[] aveHum = new String[6];
		/**
		 * 0 = SCALE_THIRTY_MINUTES , 1 = SCALE_ONE_HOUR, 2 = SCALE_THREE_HOURS, 3 = SCALE_ONE_DAY , 4 = SCALE_ONE_WEEK, 5 = SCALE_ONE_MONTH
		 * 各期間の平均気圧を保持する配列変数
		 */
		private String[] avePres = new String[6];
		/**
		 * 0 = SCALE_THIRTY_MINUTES , 1 = SCALE_ONE_HOUR, 2 = SCALE_THREE_HOURS, 3 = SCALE_ONE_DAY , 4 = SCALE_ONE_WEEK, 5 = SCALE_ONE_MONTH
		 * 各期間の平均騒音を保持する配列変数
		 */
		private String[] aveNoi = new String[6];
		/**
		 * 0 = SCALE_THIRTY_MINUTES , 1 = SCALE_ONE_HOUR, 2 = SCALE_THREE_HOURS, 3 = SCALE_ONE_DAY , 4 = SCALE_ONE_WEEK, 5 = SCALE_ONE_MONTH
		 * 各期間の最低室温を保持する配列変数
		 */
		private String[] InMinTemp = new String[6];
		/**
		 * 0 = SCALE_THIRTY_MINUTES , 1 = SCALE_ONE_HOUR, 2 = SCALE_THREE_HOURS, 3 = SCALE_ONE_DAY , 4 = SCALE_ONE_WEEK, 5 = SCALE_ONE_MONTH
		 * 各期間の最高室温を保持する配列変数
		 */
		private String[] InMaxTemp = new String[6]; 
		/**
		 * 0 = SCALE_THIRTY_MINUTES , 1 = SCALE_ONE_HOUR, 2 = SCALE_THREE_HOURS, 3 = SCALE_ONE_DAY , 4 = SCALE_ONE_WEEK, 5 = SCALE_ONE_MONTH
		 * 各期間の最低湿度を保持する配列変数
		 */
		private String[] InMinHum = new String[6];
		/**
		 * 0 = SCALE_THIRTY_MINUTES , 1 = SCALE_ONE_HOUR, 2 = SCALE_THREE_HOURS, 3 = SCALE_ONE_DAY , 4 = SCALE_ONE_WEEK, 5 = SCALE_ONE_MONTH
		 * 各期間の最高湿度を保持する配列変数
		 */
		private String[] InMaxHum = new String[6];
		/**
		 * 0 = SCALE_THIRTY_MINUTES , 1 = SCALE_ONE_HOUR, 2 = SCALE_THREE_HOURS, 3 = SCALE_ONE_DAY , 4 = SCALE_ONE_WEEK, 5 = SCALE_ONE_MONTH
		 * 各期間の最低気圧を保持する配列変数
		 */
		private String[] InMinPres = new String[6];
		/**
		 * 0 = SCALE_THIRTY_MINUTES , 1 = SCALE_ONE_HOUR, 2 = SCALE_THREE_HOURS, 3 = SCALE_ONE_DAY , 4 = SCALE_ONE_WEEK, 5 = SCALE_ONE_MONTH
		 * 各期間の最高気圧を保持する配列変数
		 */
		private String[] InMaxPres = new String[6];
		/**
		 * 0 = SCALE_THIRTY_MINUTES , 1 = SCALE_ONE_HOUR, 2 = SCALE_THREE_HOURS, 3 = SCALE_ONE_DAY , 4 = SCALE_ONE_WEEK, 5 = SCALE_ONE_MONTH
		 * 各期間の最低騒音を保持する配列変数
		 */
		private String[] InMinNoise = new String[6];
		/**
		 * 0 = SCALE_THIRTY_MINUTES , 1 = SCALE_ONE_HOUR, 2 = SCALE_THREE_HOURS, 3 = SCALE_ONE_DAY , 4 = SCALE_ONE_WEEK, 5 = SCALE_ONE_MONTH
		 * 各期間の最高騒音を保持する配列変数
		 */
		private String[] InMaxNoise = new String[6];
		/**
		 * 雨量計の測定値
		 */
		private String rain;
		/**
		 * リスト
		 */
		private ArrayList<String> list;
		
		
		/**
		 * コンストラクタ
		 * 全フィールドを初期化する
		 * @param na
		 * @param md
		 */
		public TimeStar(Netatmo na,NetatmoModule md){
			this.atmo = na;
			this.module = md;
			this.InTemperature = NO_DATA;
			this.InCO2 = NO_DATA;
			this.InHumidity = NO_DATA;
			this.InPressure = NO_DATA;
			this.InNoise = NO_DATA;
			this.rain = NO_DATA;
			this.list = new ArrayList();
			/*for(int i = 0;i < 6; i++){
				this.aveTemp[i] = NO_DATA;
				this.aveCO2[i] = NO_DATA;
				this.aveHum[i] = NO_DATA;
				this.avePres[i] = NO_DATA;
				this.aveNoi[i] = NO_DATA;
				this.InMinTemp[i] = NO_DATA;
				this.InMaxTemp[i] = NO_DATA; 
				this.InMinHum[i] = NO_DATA;
				this.InMaxHum[i] = NO_DATA;
				this.InMinPres[i] = NO_DATA;
				this.InMaxPres[i] = NO_DATA;
				this.InMinNoise[i] = NO_DATA;
				this.InMaxNoise[i] = NO_DATA;
			}*/
		}
	

		@Override
		public void run() {
			// TODO 自動生成されたメソッド・スタブ
			if(!this.InTemperature.equals(this.module.getIndoorTemperature())){
				this.InTemperature = this.module.getIndoorTemperature();
				//this.atmo.update("Netatmo,Temperature Now;" + this.InTemperature);
				this.list.add("netatmo,temperature;" + this.InTemperature);
			}
			if(!this.InHumidity.equals(this.module.getIndoorHumidity())){
				this.InHumidity = this.module.getIndoorHumidity();
				//this.atmo.update("Netatmo,Humidity Now;" + this.InHumidity);
				this.list.add("netatmo,humidity;" + this.InHumidity);
			}
			if(!this.InCO2.equals(this.module.getIndoorCO2())){
				this.InCO2 = this.module.getIndoorCO2();
				//this.atmo.update("Netatmo,CO2 Now;" + this.InCO2);
				this.list.add("netatmo,CO2;" + this.InCO2);
			}
			if(!this.InNoise.equals(this.module.getIndoorNoise())){
				this.InNoise = this.module.getIndoorNoise();
				//this.atmo.update("Netatmo,Noise Now;" + this.InNoise);
				this.list.add("netatmo,noise;" + this.InNoise);
			}
			if(!this.InPressure.equals(this.module.getIndoorPressure())){
				this.InPressure = this.module.getIndoorPressure();
				//this.atmo.update("Netatmo,Pressure Now;" + this.InPressure);
				this.list.add("netatmo,pressure;" + this.InPressure);
			}
			if(!this.rain.equals(this.module.getRain())){
				this.rain = this.module.getRain();
				//this.atmo.update("Netatmo,RainGauge Now;" + this.rain);
				this.list.add("netatmo,Rain Gauge;" + this.rain);
			}
			if(!this.list.isEmpty()){
        		String[] data = new String[this.list.size()];
        		for(int j = 0;j < this.list.size();j++){
        			data[j] = this.list.get(j);
        		}
        		System.out.println("更新");
        		this.atmo.update(data);
        		this.list.clear();
        	}
			/*for(int i = 0; i< 6;i++){
				if(!this.aveTemp[i].equals(this.module.getIndoor_AveTemp(i))){
					this.aveTemp[i] = this.module.getIndoor_AveTemp(i);
					this.atmo.update("Netatmo,Temperature Average" + this.SCALE_TIME[i] + ";" + this.aveTemp[i]);
				}
				if(!this.aveHum[i].equals(this.module.getIndoor_AveHum(i))){
					this.aveHum[i] = this.module.getIndoor_AveHum(i);
					this.atmo.update("Netatmo,Humidity Average" + this.SCALE_TIME[i] + ";" + this.aveHum[i]);
				}
				if(!this.aveCO2[i].equals(this.module.getIndoor_AveCO2(i))){
					this.aveCO2[i] = this.module.getIndoor_AveCO2(i);
					this.atmo.update("Netatmo,CO2 Average" + this.SCALE_TIME[i] + ";" + this.aveCO2[i]);
				}
				if(!this.aveNoi[i].equals(this.module.getIndoor_AveNoise(i))){
					this.aveNoi[i] = this.module.getIndoor_AveNoise(i);
					this.atmo.update("Netatmo,Noise Average" + this.SCALE_TIME[i] + ";" + this.aveNoi[i]);
				}
				if(!this.avePres[i].equals(this.module.getIndoor_AvePres(i))){
					this.avePres[i] = this.module.getIndoor_AvePres(i);
					this.atmo.update("Netatmo,Pressure Average" + this.SCALE_TIME[i] + ";" + this.avePres[i]);
				}
				
				if(!this.InMaxTemp[i].equals(this.module.getIndoor_MaxTemp(i))){
					this.InMaxTemp[i] = this.module.getIndoor_MaxTemp(i);
					this.atmo.update("Netatmo,Temperature Max" + this.SCALE_TIME[i] + ";" + this.InMaxTemp[i]);
				}
				if(!this.InMaxHum[i].equals(this.module.getIndoor_MaxHum(i))){
					this.InMaxHum[i] = this.module.getIndoor_MaxHum(i);
					this.atmo.update("Netatmo,Humidity Max" + this.SCALE_TIME[i] + ";" + this.InMaxHum[i]);
				}
				if(!this.InMaxNoise[i].equals(this.module.getIndoor_MaxNoise(i))){
					this.InMaxNoise[i] = this.module.getIndoor_MaxNoise(i);
					this.atmo.update("Netatmo,Noise Max" + this.SCALE_TIME[i] + ";" + this.InMaxNoise[i]);
				}
				if(!this.InMaxPres[i].equals(this.module.getIndoor_MaxPres(i))){
					this.InMaxPres[i] = this.module.getIndoor_MaxPres(i);
					this.atmo.update("Netatmo,Pressure Max" + this.SCALE_TIME[i] + ";" + this.InMaxPres[i]);
				}
				
				if(!this.InMinTemp[i].equals(this.module.getIndoor_MinTemp(i))){
					this.InMinTemp[i] = this.module.getIndoor_MinTemp(i);
					this.atmo.update("Netatmo,Temperature Min" + this.SCALE_TIME[i] + ";" + this.InMinTemp[i]);
				}
				if(!this.InMinHum[i].equals(this.module.getIndoor_MinHum(i))){
					this.InMinHum[i] = this.module.getIndoor_MinHum(i);
					this.atmo.update("Netatmo,Humidity Min" + this.SCALE_TIME[i] + ";" + this.InMinHum[i]);
				}
				if(!this.InMinNoise[i].equals(this.module.getIndoor_MinNoise(i))){
					this.InMinNoise[i] = this.module.getIndoor_MinNoise(i);
					this.atmo.update("Netatmo,Noise Min" + this.SCALE_TIME[i] + ";" + this.InMinNoise[i]);
				}
				if(!this.InMinPres[i].equals(this.module.getIndoor_MinPres(i))){
					this.InMinPres[i] = this.module.getIndoor_MinPres(i);
					this.atmo.update("Netatmo,Pressure Min" + this.SCALE_TIME[i] + ";" + this.InMinPres[i]);
				}
			}*/
		}
	
	}



	
}