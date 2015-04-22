/*
 * Copyright (c) 2015 Ryo Hashioka
 */
/*
 * プログラムのランタイム
 * ランタイムはデータベースからプログラム一覧を取得し、順番に実行していく。
 * 実行はプログラムのオブジェクト情報と、別のデータベーステーブルにある機器情報を比較している。
 * 条件機器情報は InputDeviceクラスに、制御機器情報はControllerクラスに書かれている
 * InputDeviseManagerから条件情報を聞き出し、プログラムの条件がすべてマッチしたら、
 * Controllerに制御命令を送る。
 *
 */

package fileServerJetty;

import inputDevices.InputDevicesManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Stack;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import controller.Controller;

public class Runtime extends Thread {

	// 入出力機器や条件の処理をするクラス達
	InputDevicesManager inputDev;
	Controller controller;
//	Conditional conditional = new Conditional(this);

	// データベース関係
//	java.sql.Connection sqlcon;
	Statement smt;
	String dataBaseName = "demo.db"; // "sample.db";

	// プログラムのリスト
	ArrayList<String> programList;
	// ArrayList<Integer> boxList;

	// threadのループに使ってる
	boolean runFlag;

	boolean changeFlag;

	MainWindow mw;

	/*
	 * コンストラクタ
	 */
	public Runtime(MainWindow mw, Statement smt) {
		this.mw = mw;
		this.smt = smt;
		inputDev = new InputDevicesManager(this, smt);
		controller = new Controller();
	}

	/*
	 * 前処理
	 */
	private void setup() {
		// センササーバのスタート
		new Thread(inputDev).start(); // バックエンドでセンサ情報の取得が行われている。

		loadPrograms(); // プログラムIDだけ保持しておく
		// ループ開始
		runFlag = true;
		changeFlag = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		// 初めだけ前準備
		setup();
		try {
			sleep(5000);
		} catch (InterruptedException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		// 後は 1秒おきにループ
		while (runFlag) {
			// 実行処理
			System.out.println("-----------------------------------------");
			runtime();
			// もし変更があったら変更する。
			if (changeFlag) {
				changePrograms();
			}
			try {
				sleep(3000);
			} catch (InterruptedException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
		System.out.println("Runtime : runtime is end.");
	}

	/*
	 * ランタイム処理 プログラムIDリストは常に保持していて、データベースからそのプログラムIDを探してきて、プログラム内のオブジェクトを取得
	 * そして実行。
	 */
	private void runtime() {
		for (int i = 0; i < programList.size(); i++) {
			// データベースから読み込む
			try {
				// データベース検索
				// 各プログラムidとプログラムの先頭オブジェクトを取得
				ResultSet result = smt
						.executeQuery("SELECT * FROM programs WHERE programid = '"
								+ programList.get(i) + "' AND boxid='0';");
				// System.out.println("hoge" + result.);
				// 検索したのを読み込もう
				while (result.next()) {
					// 次の箱がある時にはスタックを使う。
					Stack<String> boxOrder = new Stack<String>();
					boxOrder.push("0");
					/************ ここから箱を順次見ていく処理 *************/
					while (true) { // ループで、次の箱が無い状態になると break
						// 箱処理のための準備
						if (boxOrder.empty()) { // スタックが空なら終了
							break;
						}
						// スタックの一番上を取り出して、検索結果を rs に代入
						ResultSet rs = smt
								.executeQuery("SELECT * FROM programs WHERE programid = '"
										+ programList.get(i)
										+ "' AND boxid='"
										+ boxOrder.pop() + "';");
						
						String bclass = rs.getString("boxclass");
						String bname = rs.getString("name");
						String bope = rs.getString("operation");
						String bplace = rs.getString("place");
						String runnable = rs.getString("runnable");
						String nextBox = rs.getString("nextbox");
						
						rs.close();

						// 実行可能状態だったら実行する
						if (runnable.equals("true")) {
							// オブジェクトの処理を行い、
							// もし、inputDeviceなどがプログラムとマッチしていなければ以降の処理は無くす

							System.out.println("Runtime : " + bname
									+ " is Run! --- " + bope + " , " + bplace);
							if (!objectProcessing(bclass, bname, bope, bplace)) {
								System.out.println("Runtime : Not Matching!");
//								rs.close();
								break;
							} else { // もし，output の実行ができたら、
								System.out.println("Runtime : Matching!");
//								rs.close();
								if (bclass.equals("output")) {
									System.out
											.println("----------------------- out is run = " + bname);
									if (boxOrder.empty()) {
										System.out
												.println("----------------------- end?");
										// データベースの runnable を false にする
										String sql = "UPDATE programs SET runnable = 'false' WHERE programid = '"
												+ programList.get(i) + "';";
										smt.executeUpdate(sql);
									}
								}
							}
						} else {
							// 実行不可のとき、プログラム内の各入力オブジェクトいずれかがトリガを発しない状態になれば実行可能にする。

							System.out.println("Runtime : " + bname
									+ " is no Run.");

							if (bclass.equals("output")) {
//								rs.close();
								break;
							}
							if (!objectProcessing(bclass, bname, bope, bplace)) {
								smt.executeUpdate("UPDATE programs SET runnable = 'true' WHERE programid = '"
										+ programList.get(i) + "';");
//								rs.close();
								break;
							} else {
//								rs.close();
								break;
							}
						}
						// 次の箱を見る
//						String nextBox = rs.getString("nextbox");
						// rs.close();

						// 次の箱の為の準備
						if (nextBox.equals("")) { // 次が無い場合
							// 何もしない。
//							break;
						} else if (nextBox.contains(",")) { // 複数ある場合
							// int nextBoxNum =
							// スタックに後ろから入れていく。
							String[] nextBoxStr = nextBox.split(",");
							for (int j = 0; j < nextBoxStr.length; j++) {
								boxOrder.push(nextBoxStr[nextBoxStr.length - j
										- 1]);
							}
						} else { // 次の箱が１つの時？
							// 次の箱を検索結果を結果変数に入れる
							boxOrder.push(nextBox);
						}
					}
				}
				System.out.println(" ----- Runtime : run next Program");
				// database検索の終了処理
				result.close();

			} catch (SQLException e) {
				System.out.println("sql exception");
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
	}

	/*
	 * 各オブジェクトの処理を行う boxClass = input : 入力オブジェクトで operation とデバイスがマッチしてたら true
	 * していなかったら false boxClass = output : 出力オブジェクトで operation を操作完了したら true 基本的に
	 * false は無い？ boxClass = 3 : 条件オブジェクトで operation とマッチしていたら true していなかったら
	 * false(3は今は使っていない。)
	 * input の place判定は未実装
	 */
	public boolean objectProcessing(String boxClass, String objectName,
			String operation, String place) {
		if (boxClass.equals("input")) { // 入力なら比較
			// オブジェクト名から現在の値を取得
			if (objectName.equals("Time")) {
				// time は range;12/00/15/00って形式で来る
				String parentVal = operation.split(";")[0];
				String childVal = operation.split(";")[1];
				String[] str = childVal.split("/");
				for (int i = 0; i < str.length; i++) {
					if (str[i].equals("")) {
						str[i] = "00";
					} else if (str[i].equals("0")) {
						str[i] = "00";
					}
				}

				// 現在の時間を取得
				String nTime;
				Calendar c = Calendar.getInstance();
				SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
				nTime = sdf.format(c.getTime());
				int time = Integer.valueOf(nTime);

				// 時間の比較
				if (parentVal.equals("range")) { // 範囲比較
					String fTime = str[0] + str[1];
					String bTime = str[2] + str[3];
					int time1 = Integer.valueOf(fTime);
					int time2 = Integer.valueOf(bTime);
					if (time1 < time && time < time2) {
						return true;
					} else {
						return false;
					}
				} else { // その時間かどうか
					String setTime = str[0] + str[1];
					if (setTime.equals(time)) {
						return true;
					} else {
						return false;
					}
				}
			} else if (objectName.equals("Timer")) { // 未実装
				return false;
			} else {
				String inputValue = inputDev.getState(objectName);
				// もし、更新情報が複数ある場合（/で区切られている）は全てと比較する。
				if (inputValue.indexOf("/") != -1) {
					String[] inputValues = inputValue.split("/");
					for (int i = 0; i < inputValues.length; i++) {
						// System.out.println("hoge : " +
						// operation.split("/")[0]);
						// 範囲判定がある時はoperationに ; が入っている．
						if (operation.indexOf("/") != -1) { // 範囲区切り文字が含まれていたら
							// 親と子が存在していたら
							String parentVal = "";
							String childVal = "";
							String inParent;
							String inChild;
							if (operation.indexOf(";") != -1) {
								parentVal = operation.split(";")[0];
								childVal = operation.split(";")[1];
								inParent = inputValues[i].split(";")[0];
								inChild = inputValues[i].split(";")[1];

								if (parentVal.equals(inParent)) { // 親が合っていたら子を見る
									if (childVal.indexOf("/") != -1) {
										// 範囲内にあったら true なかったら false
										String[] cvals = new String[2];
										if (childVal.endsWith("/")) {
											cvals[0] = childVal.split("/")[0];
											cvals[1] = "";
										} else {
											cvals = childVal.split("/");
										}
										boolean rmach = rangeMaching(inChild,
												cvals[0], cvals[1]);
										return rmach;
									} else { // 子供に区切り文字がなかったら
										if (inChild.equals(childVal)) {
											return true;
										} else {
											return false;
										}
									}
								}
							} else { // 親子が無い場合
								// 特に無いので未実装
							}
							// セミコロンで親と子を分ける。
							// 親に / が含まれていた場合は 範囲。含まれていない場合はマッチ
							// 親が合っていたら子を比較（親と同じ方法）

						} else { // そうでなければ、普通の判定
							// プログラムの設定と入力値の比較
							if (inputValues[i].equals(operation)) { // マッチしていたら
								// true
								return true;
							} else { // マッチしていない
							}
						}

					}
					// どれもマッチしなければ false
					return false;
				} else { // 更新項目が１つの場合
					// マッチの判定と範囲の判定
					// さらにタイムとタイマーは別物
					// timer と time オブジェクトは例外
					// 範囲判定がある時はoperationに ; が入っている．
					if (operation.indexOf("/") != -1) { // 範囲区切り文字が含まれていたら
						// 親と子が存在していたら
						String parentVal = "";
						String childVal = "";
						String inParent = "";
						String inChild = "";
						if (operation.indexOf(";") != -1) {
							parentVal = operation.split(";")[0];
							childVal = operation.split(";")[1];
							if (inputValue.equals(" ")) {
								return false;
							}
							inParent = inputValue.split(";")[0];
							inChild = inputValue.split(";")[1];

							if (parentVal.equals(inParent)) { // 親が合っていたら子を見る
								if (childVal.indexOf("/") != -1) {
									String[] cvals = new String[2];
									if (childVal.endsWith("/")) {
										cvals[0] = childVal.split("/")[0];
										cvals[1] = "";
									} else {
										cvals = childVal.split("/");
									}
									// 範囲内にあったら true なかったら false
									boolean rmach = rangeMaching(inChild,
											cvals[0], cvals[1]);
									return rmach;
								} else { // 子供に区切り文字がなかったら
									if (inChild.equals(childVal)) {
										return true;
									} else {
										return false;
									}
								}
							}
						} else { // 親子が無い場合
							// 特に無いので未実装
						}
						// セミコロンで親と子を分ける。
						// 親に / が含まれていた場合は 範囲。含まれていない場合はマッチ
						// 親が合っていたら子を比較（親と同じ方法）

					} else { // そうでなければ、普通の判定
						// プログラムの設定と入力値の比較
						if (inputValue.equals(operation)) { // マッチしていたら
							// true
							return true;
						} else { // マッチしていない
						}
					}
					// どれもマッチしなければ false
					return false;
				}
			}

		} else if (boxClass.equals("output")) { // 出力なら制御
			controller.control(objectName, operation, place);
			return true;

		} else { // それ以外ならエラー
			System.out.println("runtime error : box class is input or output");
			return false;
		}
	}

	// string型でもらったのを、intに変換して比較
	private boolean rangeMaching(String val, String val1, String val2) {
		if (val.equals(""))
			return false;
		int minVal = 0;
		int maxVal = 0;
		int n;
		if (val1.equals("") && !val2.equals("")) {
			minVal = Integer.parseInt(val) - 1;
			maxVal = Integer.parseInt(val2);
		} else if (!val1.equals("") && val2.equals("")) {
			minVal = Integer.parseInt(val1);
			maxVal = Integer.parseInt(val) + 1;
		} else {
			minVal = Integer.parseInt(val1);
			maxVal = Integer.parseInt(val2);
		}
		if (minVal > maxVal) {
			n = minVal;
			minVal = maxVal;
			maxVal = n;
		}
		if (val.equals("No data")) {
			System.out.println("Runtime : No data");
		} else {
			int value = Integer.parseInt(val);
			// System.out.println("Runtime : " + minVal + " , " + value + " , "
			// + maxVal);
			if (minVal < value && value < maxVal) {
				return true;
			}
		}
		return false;
	}

	/*
	 * スレッドを終了するときに呼ぶ（終了処理）
	 */
	public void threadStop() {
		// データベースの終了処理
//		try {
//			sqlcon.close();
//			smt.close();
//		} catch (SQLException e) {
//			// TODO 自動生成された catch ブロック
//			e.printStackTrace();
//		}
		// デバイス機器のサーバ？達も終了
		inputDev.stop();
		controller.stop();
//		conditional.stop();
		// ループの終了
		runFlag = false;
	}

	/*
	 * スレッド実行中にプログラムの変更があった場合に呼ばれる programの実行中に変更されたら困るので、 一通り終わってから変更適応させるために
	 * flagで管理
	 */
	public void updatePrograms() {
		changeFlag = true;
	}

	/*
	 * もしプログラムの変更点があったら呼ばれる。 更新しまーす。
	 */
	private void changePrograms() {
		// データベースの再読み込み（変更点だけを読み込む？）
		// 変更点だけを読み込むとすると、ソートはどうする？
		loadPrograms();
		changeFlag = false;
	}

	/*
	 * データベースからプログラムの読み出し
	 */
	private void loadPrograms() {
		programList = new ArrayList<String>();
		try {
			// データベース検索
			ResultSet rs = smt.executeQuery("SELECT * FROM PROGRAMS;");
			// 全ての要素をループで回していく。
			while (rs.next()) {
				// データベースからプログラムIDを取得
				String str = rs.getString("PROGRAMID");
				System.out.println("PROGRAMID = " + str);

				// 読み込んだ物を逐次リストに追加（もし、優先度とか設定するなら、その処理を書く）
				if (programList.contains(str)) {
					// もし、すでに追加されていたら何もしない
				} else {
					programList.add(str); // 追加されていなければ追加する。
				}
			}
			// database の終了処理
			rs.close();

			// for(int i=0;i<programList.size();i++){
			// System.out.println(programList.get(i));
			// }

		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	public void sendMessageToClient(String action) {
		mw.sendMessageToClient(action);
	}

}
