package controller;

import java.util.Timer;
import java.util.TimerTask;

public class IRemocon {

	int port = 51013;
	String ipAddress = "192.168.11.3";

	 boolean sendFlag = true;
	 
//	Client client = new Client(ipAddress, port);

	public IRemocon() {

	}

	boolean connectConfirmation() {
		return false;
	}

	public void sendFlagTrue(){
		this.sendFlag = true;
	}
	
	class MyTimerTask extends TimerTask{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			sendFlagTrue();
		}
		
	}
	
	// 信号を送信する。
	void sendSignal(int num) {
		String command = "*is;" + num + ";\r\n";
		System.out.println(" ---------- iRemocon : send command is - "
				+ command);

		while(!sendFlag){
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Client client = new Client(ipAddress, port);
		client.write(command);
		client.clear();
		client.stop();
		sendFlag = false;
		Timer timer = new Timer();
		timer.schedule(new MyTimerTask(), 2000);
//		client = new Client(this, ipAddress, port);
//		client.write(command);

		// new Client(this, ipAddress, port).write(command);

		// returnSignal();
	}

	// 学習モード
	void startLearn(int num) {
		String command = "*ic;" + num + ";\r\n";
		System.out.println("command : " + command);
//		client.write(command);
		// new Client(this, ipAddress, port).write(command);

		// returnSignal();
	}

	// 学習中断
	void stopLearn() {
		String command = "*cc\r\n";
//		client.write(command);
		// new Client(this, ipAddress, port).write(command);
	}

	void setTimer() {
	}

	void getTimer() {
	}

	void cancelTimer() {
	}

	void setTime() {
	}

	void getTime() {
	}

	// バージョンを得る
	void getVersion() {
		String command = null;
		// Client client = new Client(this, ipAddress, port);
//		client.write(command);

//		if (client.available() > 0) {
//			println(client.readString());
//		}
	}

	void returnSignal(Client client) {
		String s = "";
		while (true) {
			if (client.available() > 0) {
				String ss = client.readString();
				s = s + ss;
				System.out.println(s);
				if (s.indexOf("ok") != -1 || s.indexOf("err") != -1) {
					System.out.println("break");
					break;
				}
			}
		}
	}
}
