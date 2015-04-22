/*
 * Copyright (c) 2015 Ryo Hashioka
 */
package inputDevices;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import fileServerJetty.Runtime;

/*******
 * 
 * 各センサとの通信を行う そのために様々なネットワークプロトコルに対応する予定
 * 
 * 基本的に機器から得た情報を持ち、聞かれた時に返すだけの処理をする。 どこで機器情報を管理する？ -> データベース
 * 
 * モジュールのインスタンス生成などは動的に行いたい。
 * 
 */

public class InputDevicesManager implements Runnable {

	Runtime runtime;

	final int INPUT_NUM = 5;
//	InputDevice[] inputDev = new InputDevice[INPUT_NUM];
//	OSC osc;

	// thread のループに使う
	boolean threadFlag = false;

	boolean changeFlag = false;

	// データベース関係
	Statement smt;

	public InputDevicesManager(Runtime _runtime, Statement smt) {
		runtime = _runtime;
		this.smt = smt;
		// 
//		osc = new OSC(this);
//		inputDev[0] = new Netatmo(this);
//		inputDev[1] = new Fplug(this);
//		inputDev[2] = new InputTwitter(this);
//		inputDev[3] = new Weather(this);
//		inputDev[4] = new YahooRain(this);
	}

	// 開始処理
	public void setup() {
		threadFlag = true;
//		for (int i = 0; i < inputDev.length; i++) {
//			inputDev[i].start();
//		}
	}

	@Override
	public void run() {
		// 初めだけ初期化処理
		setup();
		// TODO 自動生成されたメソッド・スタブ
		while (threadFlag) {
			try {
				Thread.sleep(1000); // １秒間に1回
			} catch (InterruptedException e) {
				System.out.println(e);
			}
		}
	}

	// 終了処理
	public void stop() {
		threadFlag = false;
//		osc.stop();
//		for (int i = 0; i < inputDev.length; i++) {
//			inputDev[i].stop();
//		}
	}

	// プログラミング環境に機器の状態を送る
	public void sendAction(String action) {
		runtime.sendMessageToClient(action);
	}

	// デバイスの情報が変更された時、呼ばれる
	public void setState(String name, String state) {
		try {

			String sql = "UPDATE input SET state = '" + state + "', time = '"
					+ timeToString() + "' WHERE name = '" + name + "';";
			smt.executeUpdate(sql);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		changeFlag = true;
		/***************** これはランタイムで行なう処理じゃよ */
		// sendAction(name + "," + state);
	}

	// 時間をString型でもらえるよ
	private String timeToString() {
		String updateTime;
		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		updateTime = sdf.format(c.getTime());
		// フォーマットパターン変更して表示する
		sdf.applyPattern("HHmmss");
		updateTime = updateTime + sdf.format(c.getTime());
		return updateTime;
	}

	// デバイスの情報が欲しい時、呼ばれる。
	// ただし、変更が無い場合は null が返ってくる。
	// データベースにその機器が登録されている前提で作っている
	public String getState(String deviceName) {
		// System.out.println("get " + deviceName + " State");
		String deviceState = "";
		if (deviceName.equals("dummy")) { // デバッグ用
			return " ";
		}
		// データベース検索
		ResultSet result;
		try {
			result = smt.executeQuery("SELECT * FROM input WHERE name = '"
					+ deviceName + "';");
			// 検索したのを読み込もう
			while (result.next()) {
				deviceState = result.getString("state");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return deviceState;
	}
}
