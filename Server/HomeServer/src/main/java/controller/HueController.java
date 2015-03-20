package controller;

import hue.Hue;

public class HueController extends OutputDevice {
	private Hue hue;

	public HueController() {
		 this.hue = new Hue("133.101.59.49");
	}

	public void stop() {
		this.hue = null;
	}

	public void start() {
//		this.hue = new Hue("133.101.59.49");
	}

	public void control(String value, String place) {
		// System.out.println("hue , control : " + place.charAt(5) + " , " +
		// color);
		String id = "1";
		if (place.equals("lightStrips")) {
			id = "4";
		} else {
			System.out.println("place ----- " + place);
			if (place.equals("")) {
				id="1";
			} else {
				id = String.valueOf(place.charAt(5));
			}
		}
		if (value.equals("off")) {
			this.setID(id);
			this.hue.stop();
		} else {
			String color = value.split(";")[1];
			setID(id);
			setColor(color);
		}
	}

	private void setID(String id) {
		this.hue.setID(id);
	}

	/**
	 * #付きで色コードを入れてください
	 * 
	 */
	private void setColor(String color) { // on;#123456 off
		this.hue.setColor(color);
	}
}
