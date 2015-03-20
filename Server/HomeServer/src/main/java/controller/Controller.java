package controller;

/*
 * モジュールの制御を行っています。
 * 
 */

public class Controller {

	// Music music = new Music();

	//IRemocon iRemocon = new IRemocon();

	final int DEVNUM = 2;
	//OutputDevice[] outdev = new OutputDevice[DEVNUM];

	public Controller() {
		//outdev[0] = new HueController();
		//outdev[1] = new OSCSend();
	}

	public void start() {
		//for (int i = 0; i < outdev.length; i++) {
			//outdev[i].start();
		//}
	}

	public void stop() {
		//for (int i = 0; i < outdev.length; i++) {
			//outdev[i].stop();
		//}
	}

	// 名前と呼び出すオブジェクトを別の所で管理して、
	// 動的にオブジェクトを呼び出せるようにする必要あり？
	public void control(String name, String operation, String place) {
		System.out.println("control : " + name + " , " + operation);
		if (name.equals("speaker")) {
			if (operation.equals("power")) {
			//	iRemocon.sendSignal(64);
			} else if (operation.equals("up")) {
				//iRemocon.sendSignal(65);
			} else {
				//iRemocon.sendSignal(66);
			}
		} else if (name.equals("light")) {
			if (operation.equals("on")) {
				//iRemocon.sendSignal(42);
			} else if(operation.equals("off")){
				//iRemocon.sendSignal(43);
			} else if(operation.equals("original")){
				//iRemocon.sendSignal(45);
			}
		} else if (name.equals("DVD")) {
			if (operation.equals("play pause")) {
				//iRemocon.sendSignal(53);
			}  else if (operation.equals("stop")) {
				//iRemocon.sendSignal(54);
			}
		} else if (name.equals("display")) {
			if (operation.equals("on")) {
				 //iRemocon.sendSignal(1);
			} else if(operation.equals("off")) {
				 //iRemocon.sendSignal(2);
			} else if(operation.equals("input1")) {
				 //iRemocon.sendSignal(4);
			} else if(operation.equals("input2")) {
				 //iRemocon.sendSignal(5);
			} else if(operation.equals("input3")) {
				 //iRemocon.sendSignal(6);
			}
		} else if (name.equals("projector")) {
			if (operation.equals("on")) {
				 //iRemocon.sendSignal(34);
			} else if(operation.equals("off")) {
				 //iRemocon.sendSignal(35);
			}
		} else if (name.equals("screen")) {
			if (operation.equals("up")) {
				 //iRemocon.sendSignal(39);
			} else if(operation.equals("down")) {
				 //iRemocon.sendSignal(41);
			}
		} else if (name.equals("Blu-ray")) {
			if (operation.equals("power")) {
				 //iRemocon.sendSignal(21);
			} else if(operation.equals("play")) {
				 //iRemocon.sendSignal(25);
			} else if(operation.equals("pause")) {
				 //iRemocon.sendSignal(26);
			} else if(operation.equals("stop")) {
				 //iRemocon.sendSignal(27);
			}
		} else if (name.equals("AirConditioner")) {
			if (operation.equals("heater")) {
				 //iRemocon.sendSignal(67);
			} else if(operation.equals("cooler")) {
				 //iRemocon.sendSignal(69);
			} else if(operation.equals("off")) {
				 //iRemocon.sendSignal(70);
			}
		} else if (name.equals("AVamp")) {
			if (operation.equals("on")) {
				 //iRemocon.sendSignal(13);
			} else if(operation.equals("off")) {
				 //iRemocon.sendSignal(14);
			} else if(operation.equals("DVR")) {
				 //iRemocon.sendSignal(15);
			} else if(operation.equals("BD")) {
				 //iRemocon.sendSignal(16);
			} else if(operation.equals("HDMIOUT")) {
				 //iRemocon.sendSignal(20);
			}
		} else if (name.equals("Hue")) {
			//outdev[0].control(operation, place);
		} else if (name.equals("DEMO_Display")) {
			String str = "DEMO_Display," + operation;
			//outdev[1].control(str, place);
		} else {
			 System.out.println("該当するものはありません。");
		}
	}

}
