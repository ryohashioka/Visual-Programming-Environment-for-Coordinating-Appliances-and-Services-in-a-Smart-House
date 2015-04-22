/*
 * Copyright (c) 2015 Ryo Hashioka
 */
package controller;

import promidi.*;

public class Dimmer extends Thread {

	MidiIO io;
	MidiOut out;
	Note noteR = new Note(31, 0, 40);
	Note noteL = new Note(32, 0, 40);

	Note noteD = new Note(30, 0, 40); // dummy

	float lastValueR = 0, lastValueL = 0;
	float newValueR, newValueL;
	float rv, lv;
	float valueR, valueL;

	boolean loopFlag = true;

	Dimmer() {
		io = MidiIO.getInstance();
		out = io.getMidiOut(0, 0);

		io.printDevices();
		this.start();
	}

	public void run() {
		while (loopFlag) {
			valueR += rv;

			if (rv > 0 && valueR > newValueR) {
				valueR = newValueR;
				rv = 0;
			} else if (rv < 0 && valueR < newValueR) {
				valueR = newValueR;
				rv = 0;
			} else {
				valueR = newValueR;
				rv = 0;
			}

			// /

			valueL += lv;

			if (lv > 0 && valueL > newValueL) {
				valueL = newValueL;
				lv = 0;
			} else if (lv < 0 && valueL < newValueL) {
				valueL = newValueL;
				lv = 0;
			} else {
				valueR = newValueR;
				rv = 0;
			}

			out.sendNote(noteD); // sending dummy
			// out.sendNote(noteL);
			// out.sendNote(noteR);

			out.sendNote(new Note(32, (int) (127 * valueL), 40));
			out.sendNote(new Note(31, (int) (127 * valueR), 40));
			// println("slide r " + valueR);

			// setValueR(valueR);
			// setValueL(valueL);

			try {
				Thread.sleep(1000/45);
			} catch (InterruptedException e) {
			}
		}
	}

	void setR(float _r) {
		lastValueR = newValueR;
		newValueR = _r;
		rv = (newValueR - lastValueR) / 6;
	}

	void setL(float _r) {
		lastValueL = newValueL;
		newValueL = _r;
		lv = (newValueL - lastValueL) / 6;
	}

	void setValueR(float _val) {
		if (_val >= 0 || _val <= 1.0) {
			noteR = new Note(31, (int) (127 * _val), 40);
			// noteR.setVelocity(int(127*_val));
			// println("val : " + _val);

			out.sendNote(noteR);
			// lastValue = _val;
		}
	}

	void setValueL(float _val) {
		if (_val >= 0 || _val <= 1.0) {
			noteL = new Note(32, (int) (127 * _val), 40);
			// noteL.setVelocity(int(127*_val));
			// println("val : " + _val);

			out.sendNote(noteL);
			// lastValue = _val;
		}
	}

	float getR() {
		return valueR;
	}

	float getL() {
		return valueL;
	}
}
