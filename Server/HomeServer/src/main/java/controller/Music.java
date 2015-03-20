package controller;

import processing.core.*;
import ddf.minim.*;
import ddf.minim.signals.*;
import ddf.minim.analysis.*;
import ddf.minim.effects.*;

public class Music extends PApplet {

	Minim minim;
	AudioPlayer player;
	// AudioPlayer[] player = new AudioPlayer[3];

	boolean playFlag = false;

	Music() {
		minim = new Minim(this);
		player = minim.loadFile("./music/メルト.mp3");
		// player[0] = minim.loadFile("./music/メルト.mp3");
		// player[1] = minim.loadFile("./music/crossing field.mp3");
		// player[2] = minim.loadFile("./music/君がいるから.mp3");
	}

	public void control(String operation) {
		if (operation.equals("PLAY")) {
			play();
		} else if (operation.equals("STOP")) {
			pause();
		} else if (operation.equals("NEXT")) {
			next();
		} else if (operation.equals("PREV")) {
			prev();
		}
	}

	public void play() {
		if (!playFlag) { // 再生されていなければ
			// player.play();
			player.loop();
			playFlag = true;
		}
	}

	public void pause() {
		if (playFlag) {
			player.pause();
			playFlag = false;
		}
	}

	public void next() {
		if (playFlag) {
			// 次の楽曲に行く処理
		}
	}

	public void prev() {
		if (playFlag) {
			// 次の楽曲に行く処理
		}
	}

	public void volume(int vol) {
		if (playFlag) {
			float f = (float) vol / (float) 10;
			player.setVolume(f);
		}
	}

	public void stop() {
		player.close();
		minim.stop();
	}
	//
	// public static void main(String[] args) {
	// Music music = new Music();
	// music.play();
	// }

}
