package inputDevices;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import fplug.FplugModule;

public class Fplug extends InputDevice{

	/**
	 * FplugModuleのインスタンス
	 */
	private FplugModule module;
	/**
	 * Timerのインスタンス
	 */
	private Timer timer;

	/**
	 * コンストラクタ
	 */
	public Fplug(InputDevicesManager inputMana){
		this.module = new FplugModule("/dev/cu.F-PLUG-");//"/dev/cu.F-PLUG-");

		super.inputMana = inputMana;
	}

	/**
	 * startメソッド
	 */
	public void start() {
		this.module.runStart();
		this.timer = new Timer();
		this.timer.schedule(new TimeStar(this, this.module), 1000, 1000);
		System.out.println("Fplug : start");
	}

	/**
	 * stopメソッド
	 */
	public void stop() {
		System.out.println("Fplug : timer cancel");
		this.timer.cancel();
//		this.timer = null;
		this.module.runStop();

	}

	/**
	 * Timerの内部クラス
	 * 
	 * @author sumidatomoyuki
	 * 
	 */
	class TimeStar extends TimerTask {
		public static final String NO_DATA = "No data";
		/**
		 * 室温を保持する変数
		 */
		private String temp;

		/**
		 * 湿度を保持する変数
		 */
		private String hum;

		/**
		 * 照度保持する変数
		 */
		private String ill;

		/**
		 * 消費電力を保持する変数
		 */
		private String wat;
		/**
		 * Fplugのインスタンス
		 */
		private Fplug fplug;
		/**
		 * FplugModuleのインスタンス
		 */
		private FplugModule module;
		/**
		 * 更新されたデータを一時保管するリスト
		 */
		private ArrayList<String> list;

		/**
		 * コンストラクタ
		 * 
		 * @param fp
		 */
		public TimeStar(Fplug fp, FplugModule md) {
			this.fplug = fp;
			this.module = md;
			this.list = new ArrayList();
			this.hum = NO_DATA;
			this.ill = NO_DATA;
			this.temp = NO_DATA;
			this.wat = NO_DATA;
		}

		/**
		 * runメソッド
		 */
		public void run() {
			if (!this.module.getTemp().equals(this.temp)) {
				this.temp = this.module.getTemp();
				// this.fplug.update("FPLUG,Temperature;" + this.temp);
				this.list.add("F-PLUG,temperature;" + this.temp);
			}
			if (!this.module.getHum().equals(this.hum)) {
				this.hum = this.module.getHum();
				// this.fplug.update("FPLUG,Humid;" + this.hum);
				this.list.add("F-PLUG,humidity;" + this.hum);
			}
			if (!this.module.getIll().equals(this.ill)) {
				this.ill = this.module.getIll();
				// this.fplug.update("FPLUG,Ill;" + this.ill);
				this.list.add("F-PLUG,illumination;" + this.ill);
			}
			if (!this.module.getWatt().equals(this.wat)) {
				this.wat = this.module.getWatt();
				// this.fplug.update("FPLUG,RealtimeWatt;" + this.wat);
				this.list.add("F-PLUG,power;" + this.wat);
			}
			if (!this.list.isEmpty()) {
				String[] data = new String[this.list.size()];
				for (int j = 0; j < this.list.size(); j++) {
					data[j] = this.list.get(j);
				}
				this.fplug.update(data);
				this.list.clear();
			}
		}
	}

}
