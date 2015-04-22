/*
 * Copyright (c) 2015 Ryo Hashioka
 */
package inputDevices;

/*
 * 条件となるモジュールは基本的に、このクラスを継承します。
 * startとstopメソッドはそれぞれのモジュール内で開始・終了処理を書いてください。
 * updateメソッドはそのモジュールに変化があったときに呼んでください。
 */

public class InputDevice {

	InputDevicesManager inputMana;

	
	public void start() {
	}

	public void stop() {
	}

	// str は "名前,更新情報" にしてください。
	public void update(String str) {
		System.out.println("----- inputDevice is updated : " + str);
		String[] strs = str.split(",");
		inputMana.setState(strs[0], strs[1]);
	}

	// 更新情報が複数ある場合は配列で送ってください。
	public void update(String[] str) {
		String name = str[0].split(",")[0];
		String s = str[0].split(",")[1];
		for (int i = 1; i < str.length; i++) {
			s = s + "/" + str[i].split(",")[1];
		}
		System.out.println("----- inputDevice is updated : " + name + "," + s);
		inputMana.setState(name, s);
	}

	// InputDevide[] inputDev;
	// inputDev[0] = new Twitter(this);
	// inputDev[1] = new Netatmo(this);
	// inputDev[2] = new hogehoge(this);
}
