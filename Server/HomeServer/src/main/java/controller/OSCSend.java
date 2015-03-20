package controller;

import netP5.NetAddress;
import oscP5.OscMessage;
import oscP5.OscP5;

public class OSCSend extends OutputDevice{
	
	OscP5 oscP5;
	NetAddress myRemoteLocation;

	OSCSend() {
		oscP5 = new OscP5(this, 12010);
		myRemoteLocation = new NetAddress("192.168.11.5", 12003);
	}

	void oscSend(String str1, String str2) {
		OscMessage myMessage = new OscMessage("/" + str1);

		myMessage.add(str2); /* add an int to the osc message */

		/* send the message */
		oscP5.send(myMessage, myRemoteLocation);
	}

	void oscEvent(OscMessage theOscMessage) {

	}

	
	public void start(){}
	public void stop(){}
		
	public void control(String value, String place){
		String str1 = value.split(",")[0];
		String str2 = value.split(",")[1];
		this.oscSend(str1, str2);
	}

}
