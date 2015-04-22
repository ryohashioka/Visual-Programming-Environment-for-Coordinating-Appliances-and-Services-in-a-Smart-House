/*
 * Copyright (c) 2015 Ryo Hashioka
 */
package inputDevices;

import oscP5.*;
import netP5.*;

/*
 * これもモジュールの１つだが、InputDeviceを継承していない。
 * （今後する予定）
 */

public class OSC {

	OscP5 oscP5;
	NetAddress myRemoteLocation;

	InputDevicesManager sensor;

	OSC(InputDevicesManager sensorMain) {
		oscP5 = new OscP5(this, 12005);
		sensor = sensorMain;
	}
	
	public void stop(){
		oscP5.stop();
	}

	void oscEvent(OscMessage theOscMessage) {
		System.out.println("osc Message " + theOscMessage.addrPattern()
				+ " , " + theOscMessage.get(0).stringValue());
		if (theOscMessage.addrPattern().equals("/button")) {
			sensor.sendAction("button," + theOscMessage.get(0).stringValue());
			sensor.setState("button",theOscMessage.get(0).stringValue());
		} else if (theOscMessage.addrPattern().startsWith("/door")) {
			sensor.sendAction("door," + theOscMessage.get(0).stringValue());
			sensor.setState("door",theOscMessage.get(0).stringValue());
		} else {
			sensor.sendAction(theOscMessage.addrPattern() + "," + theOscMessage.get(0).stringValue());
		}
	}

}
