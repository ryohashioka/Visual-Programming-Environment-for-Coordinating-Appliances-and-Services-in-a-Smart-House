package controller;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;
import weather.WeatherModule;
import yahoorain.YahooRainModule;


public class UserTwitter extends PApplet{


	/**
	 * MentionかHashTweetどっちを表示させるかを決めるフラグ
	 */
	private int tflag;
	/**
	 * 天気を表示させるかどうか
	 */
	private int wflag;
	/**
	 * 降水強度を表示させるかどうか
	 */
	private int rflag;
	/**
	 * 可変用最大表示数
	 */
	private int shownum;
	/**
	 * ツイートを最大20件保持する
	 * index = 0 が一番古く index = 19 が最新ツイート
	 * 中身の配列にかんしては
	 * [0] = 名前 [1] = @~~ [2] = プロフィール画像のURL [3] = 内容
	 */
	private ArrayList<String[]> tweetList;
	/**
	 * Mentionを最大20件保持する
	 * index = 0 が一番古く index = 19 が最新Mention
	 * 中身の配列にかんしては
	 * [0] = 名前 [1] = @~~ [2] = プロフィール画像のURL [3] = 内容
	 */
	private ArrayList<String[]> mentionList;
	/**
	 * DMを最大20件保持する
	 * index = 0 が一番古く index = 19 が最新DM
	 * 中身の配列にかんしては
	 * [0] = 名前 [1] = @~~ [2] = プロフィール画像のURL [3] = 内容
	 */
	private ArrayList<String[]> dmList;
	/**
	 * フォローリストを最大20件保持する
	 * index = 0 が一番古く index = 19 が最新フォローユーザー
	 * 中身の配列にかんしては
	 * [0] = 名前 [1] = @~~ [2] = プロフィール画像のURL [3] = 説明文
	 */
	private ArrayList<String[]> followList;
	/**
	 * フォロワーを最大20件保持する
	 * index = 0 が一番古く index = 19 が最新フォロワー
	 * 中身の配列にかんしては
	 * [0] = 名前 [1] = @~~ [2] = プロフィール画像のURL [3] = 説明
	 */
	private ArrayList<String[]> followerList;
	/**
	 * ハッシュツイートを最大20件保持する
	 * index = 0 が一番古く index = 19 が最新フォロワー
	 * 中身の配列にかんしては
	 * [0] = 名前 [1] = @~~ [2] = プロフィール画像のURL [3] = 説明
	 */
	private ArrayList<String[]> hashList;
	/**
	 * 波紋アニメーション用原点座標変数
	 */
	int x1,y1,x2,y2,x3,y3;
	/**
	 * 波紋アニメーション用半径変数
	 */
	int r1 = 0,r2 = 0,r3 = 0;
	/**
	 * 色変化のための変数
	 */
	int h = 0;

	/**
	 * サイズ
	 */
	int x,y;
	/**
	 * YahooRainModuleクラスのインスタンス
	 */
	private YahooRainModule rain;
	/**
	 * WeatherModuleクラスのインスタンス
	 */
	private WeatherModule weather;


	/**
	 * 初期化メソッド
	 */
	public void setup()
	{
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		this.x = d.width;
		this.y = d.height;
		size(x,y);
		frameRate(50);
		colorMode(HSB,360,100,100,100);
		smooth();
		noStroke();
		fill(200,80,80);
		background(197,42,92);
		stroke(0);
		this.tflag = 0;
		this.wflag = 0;
		this.rflag = 0;
		this.tweetList = new ArrayList<String[]>();
		this.mentionList = new ArrayList<String[]>();
		this.dmList = new ArrayList<String[]>();
		this.followList = new ArrayList<String[]>();
		this.followerList = new ArrayList<String[]>();
		this.hashList = new ArrayList<String[]>();
		this.rain = new YahooRainModule();
		this.weather = new WeatherModule();
	}

	/**
	 * 更新メソッド
	 */
	public void draw()
	{

		switch(this.tflag){
		case 0:
			background(0);
			if(r1 == 200)
				r1 = 0;
			if(r2 == 230)
				r2 = 0;
			if(r3 == 260)
				r3 = 0;
			if(r1 == 0){
				x1 = (int)(Math.random()*this.x);
				y1 = (int)(Math.random()*this.y);
			}
			if(r2 == 0){
				x2 = (int)(Math.random()*this.x);
				y2 = (int)(Math.random()*this.y);
			}
			if(r3 == 0){
				x3 = (int)(Math.random()*this.x);
				y3 = (int)(Math.random()*this.y);
			}

			fill(255);
			noFill();
			stroke(200);
			ellipse(x1,y1, r1, r1);
			ellipse(x2,y2, r2, r2);
			ellipse(x3,y3, r3, r3);


			fill(this.h%360,80,80);
			textSize(50);
			text("Kyoto Sangyo University",this.x/4,this.y/3);
			PFont font = createFont("MS Gothic",48,true);
			// フォントを設定
			textFont(font);
			//fill(0,250,250);
			//x += 280, y+= 80
			textSize(50);
			text("平#Lab",this.x/2 ,this.y/2);
			//変数の値を更新

			r1++;
			r2++;
			r3++;

			break;

		case 1:
			this.Tweet();
			break;

		case 2:
			this.Mention();
			break;

		case 3:
			this.DirectMessage();
			break;

		case 4:
			this.Follow();
			break;

		case 5:
			this.Follower();
			break;
		case 6:
			this.hashTweet();
			break;



		}

		if(this.wflag == 1){
			this.Weather();
		}
		if(this.rflag == 1){
			this.Rain();
		}


		this.h++;
		if(this.h == 361){
			this.h = 0;
		}

	}

	/**
	 * 天気を表示させる
	 */
	public void Weather(){
		String w = this.weather.getWeather(0);
		//System.out.println(w);
		PImage img = null;
		if(w.split(" ")[1].substring(0,1).equals("晴")){
			if(w.indexOf("後") != -1){
				if(w.indexOf("曇") != -1){
					img = loadImage("05.png");
				}else if(w.indexOf("雨") != -1){
					img = loadImage("06.png");
				}else if(w.indexOf("雪") != -1){
					img = loadImage("07.png");
				}
			}
			else if(w.indexOf("時々") != -1){
				if(w.indexOf("曇") != -1){
					img = loadImage("02.png");
				}else if(w.indexOf("雨") != -1){
					img = loadImage("03.png");
				}else if(w.indexOf("雪") != -1){
					img = loadImage("04.png");
				}
			}
			else{
				img = loadImage("01.png");
			}
		}
		
		else if(w.split(" ")[1].substring(0,1).equals("曇")){
			//System.out.println("雲値");
			if(w.indexOf("後") != -1){
				if(w.indexOf("晴") != -1){
					img = loadImage("12.png");
				}else if(w.indexOf("雨") != -1){
					img = loadImage("13.png");
				}else if(w.indexOf("雪") != -1){
					img = loadImage("14.png");
				}
			}
			else if(w.indexOf("時々") != -1){
				if(w.indexOf("晴") != -1){
					img = loadImage("09.png");
				}else if(w.indexOf("雨") != -1){
					img = loadImage("10.png");
				}else if(w.indexOf("雪") != -1){
					img = loadImage("11.png");
				}
			}
			else{
				img = loadImage("08.png");
			}
		}
		
		else if(w.split(" ")[1].substring(0,1).equals("雨")){
			if(w.indexOf("後") != -1){
				if(w.indexOf("曇") != -1){
					img = loadImage("20.png");
				}else if(w.indexOf("晴") != -1){
					img = loadImage("19.png");
				}else if(w.indexOf("雪") != -1){
					img = loadImage("21.png");
				}
			}
			else if(w.indexOf("時々") != -1){
				if(w.indexOf("曇") != -1){
					img = loadImage("17.png");
				}else if(w.indexOf("晴") != -1){
					img = loadImage("16.png");
				}else if(w.indexOf("雪") != -1){
					img = loadImage("18.png");
				}
			}
			else{
				img = loadImage("15.png");
			}
		}
		
		
		else if(w.split(" ")[1].substring(0,1).equals("雪")){
			if(w.indexOf("後") != -1){
				if(w.indexOf("曇") != -1){
					img = loadImage("27.png");
				}else if(w.indexOf("雨") != -1){
					img = loadImage("28.png");
				}else if(w.indexOf("晴") != -1){
					img = loadImage("26.png");
				}
			}
			else if(w.indexOf("時々") != -1){
				if(w.indexOf("曇") != -1){
					img = loadImage("24.png");
				}else if(w.indexOf("雨") != -1){
					img = loadImage("25.png");
				}else if(w.indexOf("晴") != -1){
					img = loadImage("23.png");
				}
			}
			else{
				img = loadImage("22.png");
			}
		}
		
		
		
		
		if(img != null)
			image(img,this.x*3/4,0,this.x/4,this.y/2);
	}

	
	
	
	/**
	 * 降水強度を表示させる
	 */
	public void Rain(){
		fill(197,42,92);
		rect(this.x*3/4,this.y/2,this.x/4,this.y/2);
		fill(0);
		PFont font = createFont("MS Gothic",48,true);
		textFont(font);
		textAlign(CENTER);
		textSize(this.x/55);
		text("ただいまの降水強度は",this.x*3/4 + this.x/8,this.y*3/4);
		textSize(this.x/30);
		text("\n" + this.rain.getSurveyRainfall() + "[mm/h]",this.x*3/4 + this.x/8,this.y*3/4);
		textAlign(LEFT);
	}
	
	
	

	/**
	 * ツイートを表示させる
	 */
	private void Tweet(){
		

	}

	
	
	
	/**
	 * Mentionを表示させる
	 */
	private void Mention(){
		int del = 0;
		int count = 0;
		PFont font = createFont("MS Gothic",48,true);
		switch(this.shownum){
		case 1:
			if(this.shownum == 1){
				del = this.y/2;
				background(197,42,92);
				stroke(0);
				//PFont font = createFont("MS Gothic",48,true);
				// フォントを設定
				textFont(font);
				fill(h%360,80,80);
				textSize(this.x/68);
				strokeWeight(1);
				String r = this.mentionList.get(19)[3].replaceAll("\n", " ");
				String str = "\n\n";
				if(r.length() > 124){
					str += r.substring(0,34) + "\n" + r.substring(34,64) + "\n" + r.substring(64,94) + "\n" + r.substring(94,124) + "\n" + r.substring(124);
				}else if(r.length() > 94 && r.length() <= 124){
					str += r.substring(0,34) + "\n" + r.substring(34,64) + "\n" + r.substring(64,94) + "\n" + r.substring(94);
				}
				else if(r.length() > 64 && r.length() <= 94){
					str += r.substring(0,34) + "\n" + r.substring(34,64) + "\n" + r.substring(64);
				}else if(r.length() > 34 && r.length() <= 64){
					str += r.substring(0,34) + "\n" + r.substring(34);
				}else{
					str += r;
				}

				fill(0);
				text("\n" + this.mentionList.get(19)[0] + "(@" + this.mentionList.get(19)[1] + ")",del + 10,(this.mentionList.size() -1 - 19)*del+ 20);
				text(str,del + 10,(this.mentionList.size() -1 - 19)*del + 40);
				PImage img = loadImage(this.mentionList.get(19)[2]);
				image(img,5,(this.mentionList.size() -1 - 19)*del+del/16,del*3/4,del*3/4);

				line(this.x*3/4,0,this.x*3/4,this.y);
			}
			break;

		case 2:
			del = this.y/2;
			background(197,10,92);
			stroke(0);
			//PFont font = createFont("MS Gothic",48,true);
			// フォントを設定
			textFont(font);
			fill(h%360,80,80);
			textSize(this.x/68);
			strokeWeight(1);
			count = 0;
			for(int i = this.mentionList.size()-1;i >= 18;i--){
				String r = this.mentionList.get(i)[3].replaceAll("\n", " ");
				String str = "\n\n";
				if(r.length() > 124){
					str += r.substring(0,34) + "\n" + r.substring(34,64) + "\n" + r.substring(64,94) + "\n" + r.substring(94,124) + "\n" + r.substring(124);
				}else if(r.length() > 94 && r.length() <= 124){
					str += r.substring(0,34) + "\n" + r.substring(34,64) + "\n" + r.substring(64,94) + "\n" + r.substring(94);
				}
				else if(r.length() > 64 && r.length() <= 94){
					str += r.substring(0,34) + "\n" + r.substring(34,64) + "\n" + r.substring(64);
				}else if(r.length() > 34 && r.length() <= 64){
					str += r.substring(0,34) + "\n" + r.substring(34);
				}else{
					str += r;
				}

				fill(0);
				text("\n" + this.mentionList.get(i)[0] + "(@" + this.mentionList.get(i)[1] + ")",del + 10,(this.mentionList.size() -1 - i)*del+ 20);
				text(str,del + 10,(this.mentionList.size() -1 - i)*del + 40);
				PImage img = loadImage(this.mentionList.get(i)[2]);
				image(img,5,(this.mentionList.size() -1 - i)*del+del/16,del*3/4,del*3/4);

				count++;
			}
			for(int j =0;j < 2;j++){
				line(0,del*j,this.x*3/4,del*j);
			}
			line(this.x*3/4,0,this.x*3/4,this.y);
			break;

		case 3:
			del = this.y/3;
			background(197,42,92);
			stroke(0);
			//PFont font = createFont("MS Gothic",48,true);
			// フォントを設定
			textFont(font);
			fill(h%360,80,80);
			textSize(this.x/68);
			strokeWeight(1);
			count = 0;
			for(int i = this.mentionList.size()-1;i >= 17;i--){
				String r = this.mentionList.get(i)[3].replaceAll("\n", " ");
				String str = "\n\n";
				if(r.length() > 110){
					str += r.substring(0,40) + "\n" + r.substring(40,75) + "\n" + r.substring(75,110) + "...";
				}
				else if(r.length() > 75 && r.length() <= 110){
					str += r.substring(0,40) + "\n" + r.substring(40,75) + "\n" + r.substring(75);
				}else if(r.length() > 40 && r.length() <= 75){
					str += r.substring(0,40) + "\n" + r.substring(40);
				}else{
					str += r;
				}

				fill(0);
				text("\n" + this.mentionList.get(i)[0] + "(@" + this.mentionList.get(i)[1] + ")",del + 10,(this.mentionList.size() -1 - i)*del+ 20);
				text(str,del + 10,(this.mentionList.size() -1 - i)*del + 40);
				PImage img = loadImage(this.mentionList.get(i)[2]);
				image(img,5,(this.mentionList.size() -1 - i)*del+del/16,del*3/4,del*3/4);

				count++;
			}
			for(int j =0;j < 3;j++){
				line(0,del*j,this.x*3/4,del*j);
			}
			line(this.x*3/4,0,this.x*3/4,this.y);
			break;

		case 4:
			del = this.y/4;
			background(197,42,92);
			stroke(0);
			//PFont font = createFont("MS Gothic",48,true);
			// フォントを設定
			textFont(font);
			fill(h%360,80,80);
			textSize(this.x/68);
			strokeWeight(1);
			count = 0;
			for(int i = this.mentionList.size()-1;i >= 16;i--){
				String r = this.mentionList.get(i)[3].replaceAll("\n", " ");
				String str = "\n";
				if(r.length() > 120){
					str += r.substring(0,43) + "\n" + r.substring(43,83) + "\n" + r.substring(83,120) + "...";
				}
				else if(r.length() > 83 && r.length() <= 120){
					str += r.substring(0,43) + "\n" + r.substring(43,83) + "\n" + r.substring(83);
				}else if(r.length() > 43 && r.length() <= 83){
					str += r.substring(0,43) + "\n" + r.substring(43);
				}else{
					str += r;
				}

				fill(0);
				text(this.mentionList.get(i)[0] + "(@" + this.mentionList.get(i)[1] + ")",del + 10,(this.mentionList.size() -1 - i)*del+ 20);
				text(str,del + 10,(this.mentionList.size() -1 - i)*del + 40);
				PImage img = loadImage(this.mentionList.get(i)[2]);
				image(img,5,(this.mentionList.size() -1 - i)*del,del,del);

				count++;
			}
			for(int j =0;j < 4;j++){
				line(0,del*j,this.x*3/4,del*j);
			}
			line(this.x*3/4,0,this.x*3/4,this.y);
			break;

		case 5:
			del = this.y/5;
			background(197,42,92);
			stroke(0);
			//PFont font = createFont("MS Gothic",48,true);
			// フォントを設定
			textFont(font);
			fill(h%360,80,80);
			textSize(this.x/68);
			strokeWeight(1);
			count = 0;
			for(int i = this.mentionList.size()-1;i >= 15;i--){
				String r = this.mentionList.get(i)[3].replaceAll("\n", " ");
				String str = "\n";
				if(r.length() > 120){
					str += r.substring(0,46) + "\n" + r.substring(46,83) + "\n" + r.substring(83,120) + "...";
				}
				else if(r.length() > 83 && r.length() <= 120){
					str += r.substring(0,46) + "\n" + r.substring(46,83) + "\n" + r.substring(83);
				}else if(r.length() > 46 && r.length() <= 83){
					str += r.substring(0,46) + "\n" + r.substring(46);
				}else{
					str += r;
				}

				fill(0);
				text(this.mentionList.get(i)[0] + "(@" + this.mentionList.get(i)[1] + ")",del + 10,(this.mentionList.size() -1 - i)*del+ 20);
				text(str,del + 10,(this.mentionList.size() -1 - i)*del + 40);
				PImage img = loadImage(this.mentionList.get(i)[2]);
				image(img,5,(this.mentionList.size() -1 - i)*del,del,del);

				count++;
			}
			for(int j =0;j < 5;j++){
				line(0,del*j,this.x*3/4,del*j);
			}
			line(this.x*3/4,0,this.x*3/4,this.y);
			break;

		case 6:
			del = this.y/6;
			background(197,42,92);
			stroke(0);
			//PFont font = createFont("MS Gothic",48,true);
			// フォントを設定
			textFont(font);
			fill(h%360,80,80);
			textSize(this.x/68);
			strokeWeight(1);
			count = 0;
			for(int i = this.mentionList.size()-1;i >= 14;i--){
				String r = this.mentionList.get(i)[3].replaceAll("\n", " ");
				String str = "\n";
				if(r.length() > 83){
					str += r.substring(0,46) + "\n" + r.substring(46,83) + "...";
				}else if(r.length() > 46 && r.length() <= 83){
					str += r.substring(0,46) + "\n" + r.substring(46);
				}else{
					str += r;
				}

				fill(0);
				text(this.mentionList.get(i)[0] + "(@" + this.mentionList.get(i)[1] + ")",del + 10,(this.mentionList.size() -1 - i)*del+ 20);
				text(str,del + 10,(this.mentionList.size() -1 - i)*del + 40);
				PImage img = loadImage(this.mentionList.get(i)[2]);
				image(img,5,(this.mentionList.size() -1 - i)*del,del,del);

				count++;
			}
			for(int j =0;j < 6;j++){
				line(0,del*j,this.x*3/4,del*j);
			}
			line(this.x*3/4,0,this.x*3/4,this.y);
			break;

		case 7:
			del = this.y/7;
			background(197,42,92);
			stroke(0);
			//PFont font = createFont("MS Gothic",48,true);
			// フォントを設定
			textFont(font);
			fill(h%360,80,80);
			textSize(this.x/68);
			strokeWeight(1);
			count = 0;
			for(int i = this.mentionList.size()-1;i >= 13;i--){
				String r = this.mentionList.get(i)[3].replaceAll("\n", " ");
				String str = "\n";
				if(r.length() > 83){
					str += r.substring(0,46) + "\n" + r.substring(46,83) + "...";
				}else if(r.length() > 46 && r.length() <= 83){
					str += r.substring(0,46) + "\n" + r.substring(46);
				}else{
					str += r;
				}

				fill(0);
				text(this.mentionList.get(i)[0] + "(@" + this.mentionList.get(i)[1] + ")",del + 10,(this.mentionList.size() -1 - i)*del+ 20);
				text(str,del + 10,(this.mentionList.size() -1 - i)*del + 40);
				PImage img = loadImage(this.mentionList.get(i)[2]);
				image(img,5,(this.mentionList.size() -1 - i)*del,del,del);

				count++;
			}
			for(int j =0;j < 7;j++){
				line(0,del*j,this.x*3/4,del*j);
			}
			line(this.x*3/4,0,this.x*3/4,this.y);
			break;

		case 8:
			del = this.y/8;
			background(197,42,92);
			stroke(0);
			//PFont font = createFont("MS Gothic",48,true);
			// フォントを設定
			textFont(font);
			fill(h%360,80,80);
			textSize(this.x/68);
			strokeWeight(1);
			count = 0;
			for(int i = this.mentionList.size()-1;i >= 12;i--){
				String r = this.mentionList.get(i)[3].replaceAll("\n", " ");
				String str = "";
				if(r.length() > 83){
					str += r.substring(0,46) + "\n" + r.substring(46,83) + "...";
				}else if(r.length() > 46 && r.length() <= 83){
					str += r.substring(0,46) + "\n" + r.substring(46);
				}else{
					str = r;
				}

				fill(0);
				text(this.mentionList.get(i)[0] + "(@" + this.mentionList.get(i)[1] + ")",del + 10,(this.mentionList.size() -1 - i)*del+ 20);
				text(str,del + 10,(this.mentionList.size() -1 - i)*del + 40);
				PImage img = loadImage(this.mentionList.get(i)[2]);
				image(img,5,(this.mentionList.size() -1 - i)*del,del,del);

				count++;
			}
			for(int j =0;j < 8;j++){
				line(0,del*j,this.x*3/4,del*j);
			}
			line(this.x*3/4,0,this.x*3/4,this.y);
			break;

		case 9:
			del = this.y/9;
			background(197,42,92);
			stroke(0);
			//PFont font = createFont("MS Gothic",48,true);
			// フォントを設定
			textFont(font);
			fill(h%360,80,80);
			textSize(this.x/68);
			strokeWeight(1);
			count = 0;
			for(int i = this.mentionList.size()-1;i >= 11;i--){
				String r = this.mentionList.get(i)[3].replaceAll("\n", " ");
				String str = "";
				if(r.length() > 83){
					str += r.substring(0,46) + "\n" + r.substring(46,83) + "...";
				}else if(r.length() > 46 && r.length() <= 83){
					str += r.substring(0,46) + "\n" + r.substring(46);
				}else{
					str = r;
				}

				fill(0);
				text(this.mentionList.get(i)[0] + "(@" + this.mentionList.get(i)[1] + ")",del + 10,(this.mentionList.size() -1 - i)*del+ 20);
				text(str,del + 10,(this.mentionList.size() -1 - i)*del + 40);
				PImage img = loadImage(this.mentionList.get(i)[2]);
				image(img,5,(this.mentionList.size() -1 - i)*del,del,del);

				count++;
			}
			for(int j =0;j < 9;j++){
				line(0,del*j,this.x*3/4,del*j);
			}
			line(this.x*3/4,0,this.x*3/4,this.y);
			break;

		case 10:
			del = this.y/10;
			background(197,42,92);
			stroke(0);
			//PFont font = createFont("MS Gothic",48,true);
			// フォントを設定
			textFont(font);
			fill(h%360,80,80);
			textSize(this.x/68);
			strokeWeight(1);
			count = 0;
			for(int i = this.mentionList.size()-1;i >= 10;i--){
				String r = this.mentionList.get(i)[3].replaceAll("\n", " ");
				String str = "";
				if(r.length() > 83){
					str += r.substring(0,46) + "\n" + r.substring(46,83) + "...";
				}else if(r.length() > 46 && r.length() <= 83){
					str += r.substring(0,46) + "\n" + r.substring(46);
				}else{
					str = r;
				}

				fill(0);
				text(this.mentionList.get(i)[0] + "(@" + this.mentionList.get(i)[1] + ")",del + 10,(this.mentionList.size() -1 - i)*del+ 20);
				text(str,del + 10,(this.mentionList.size() -1 - i)*del + 40);
				PImage img = loadImage(this.mentionList.get(i)[2]);
				image(img,5,(this.mentionList.size() -1 - i)*del,del,del);

				count++;
			}
			for(int j =0;j < 10;j++){
				line(0,del*j,this.x*3/4,del*j);
			}
			line(this.x*3/4,0,this.x*3/4,this.y);
			break;

		case 11:
			del = this.y/6;
			background(197,42,92);
			stroke(0);
			//PFont font = createFont("MS Gothic",48,true);
			// フォントを設定
			textFont(font);
			fill(h%360,80,80);
			textSize(this.x/68);
			strokeWeight(1);
			count = 0;
			for(int i = this.mentionList.size()-1;i >= 9;i--){
				String r = this.mentionList.get(i)[3].replaceAll("\n", " ");
				String str = "";
				if(r.length() > 59){
					str += r.substring(0,22) + "\n" + r.substring(22,40)+ "\n" + r.substring(40,56)+"...";	
				}else if(r.length() > 40 && r.length() <= 59){
					str += r.substring(0,22) + "\n" + r.substring(22,40) + "\n" + r.substring(40) ;
				}else if(r.length() <= 40 && r.length() > 22){
					str += r.substring(0,24) + "\n" + r.substring(24);
				}else{
					str = r;
				}
				if(count < 6){
					fill(0);
					text(this.mentionList.get(i)[0] + "(@" + this.mentionList.get(i)[1] + ")",del + 10,(this.mentionList.size() -1 - i)*del+ 20);
					text(str,del + 10,(this.mentionList.size() -1 - i)*del + 40);
					PImage img = loadImage(this.mentionList.get(i)[2]);
					image(img,5,(this.mentionList.size() -1 - i)*del,del,del);
				}else{
					text(this.mentionList.get(i)[0] + "(@" + this.mentionList.get(i)[1] + ")",this.x*3/8 + del + 15,(this.mentionList.size() -1 - i - 6)*del+20);
					text(str,this.x*3/8 + del + 15,(this.mentionList.size() -1 - i - 6)*del+40);
					PImage img = loadImage(this.mentionList.get(i)[2]);
					image(img,this.x*3/8+5,(this.mentionList.size() -1 - i - 6)*del,del,del);
				}

				count++;
			}
			for(int j =0;j < 9;j++){
				line(0,del*j,this.x*3/4,del*j);
			}
			strokeWeight(10); 
			line(this.x*3/8-5,0,this.x*3/8-5,this.y);
			strokeWeight(1);
			line(this.x*3/4,0,this.x*3/4,this.y);
			break;

		case 12:
			del = this.y/6;
			background(197,42,92);
			stroke(0);
			//PFont font = createFont("MS Gothic",48,true);
			// フォントを設定
			textFont(font);
			fill(h%360,80,80);
			textSize(this.x/68);
			strokeWeight(1);
			count = 0;
			for(int i = this.mentionList.size()-1;i >= 8;i--){
				String r = this.mentionList.get(i)[3].replaceAll("\n", " ");
				String str = "";
				if(r.length() > 59){
					str += r.substring(0,22) + "\n" + r.substring(22,40)+ "\n" + r.substring(40,56)+"...";	
				}else if(r.length() > 40 && r.length() <= 59){
					str += r.substring(0,22) + "\n" + r.substring(22,40) + "\n" + r.substring(40) ;
				}else if(r.length() <= 40 && r.length() > 22){
					str += r.substring(0,24) + "\n" + r.substring(24);
				}else{
					str = r;
				}
				if(count < 6){
					fill(0);
					text(this.mentionList.get(i)[0] + "(@" + this.mentionList.get(i)[1] + ")",del + 10,(this.mentionList.size() -1 - i)*del+ 20);
					text(str,del + 10,(this.mentionList.size() -1 - i)*del + 40);
					PImage img = loadImage(this.mentionList.get(i)[2]);
					image(img,5,(this.mentionList.size() -1 - i)*del,del,del);
				}else{
					text(this.mentionList.get(i)[0] + "(@" + this.mentionList.get(i)[1] + ")",this.x*3/8 + del + 15,(this.mentionList.size() -1 - i - 6)*del+20);
					text(str,this.x*3/8 + del + 15,(this.mentionList.size() -1 - i - 6)*del+40);
					PImage img = loadImage(this.mentionList.get(i)[2]);
					image(img,this.x*3/8+5,(this.mentionList.size() -1 - i - 6)*del,del,del);
				}

				////(count + this.mentionList.get(i)[3]);
				count++;
			}
			for(int j =0;j < 9;j++){
				line(0,del*j,this.x*3/4,del*j);
			}
			strokeWeight(10); 
			line(this.x*3/8-5,0,this.x*3/8-5,this.y);
			strokeWeight(1);
			line(this.x*3/4,0,this.x*3/4,this.y);
			break;

		case 13:
			del = this.y/7;
			background(197,42,92);
			stroke(0);
			//PFont font = createFont("MS Gothic",48,true);
			// フォントを設定
			textFont(font);
			fill(h%360,80,80);
			textSize(this.x/75);
			strokeWeight(1);
			count = 0;
			for(int i = this.mentionList.size()-1;i >= 7;i--){
				String r = this.mentionList.get(i)[3].replaceAll("\n", " ");
				String str = "";
				if(r.length() > 43){
					str += r.substring(0,25) + "\n" + r.substring(25,43)+ "...";
				}else if(r.length() <= 43 && r.length() > 25){
					str += r.substring(0,25) + "\n" + r.substring(25);
				}else{
					str = r;
				}
				if(count < 7){
					fill(0);
					text(this.mentionList.get(i)[0] + "(@" + this.mentionList.get(i)[1] + ")",del + 10,(this.mentionList.size() -1 - i)*del+ 20);
					text(str,del + 10,(this.mentionList.size() -1 - i)*del + 40);
					PImage img = loadImage(this.mentionList.get(i)[2]);
					image(img,5,(this.mentionList.size() -1 - i)*del,del,del);
				}else{
					text(this.mentionList.get(i)[0] + "(@" + this.mentionList.get(i)[1] + ")",this.x*3/8 + del + 15,(this.mentionList.size() -1 - i - 7)*del+20);
					text(str,this.x*3/8 + del + 15,(this.mentionList.size() -1 - i - 7)*del+40);
					PImage img = loadImage(this.mentionList.get(i)[2]);
					image(img,this.x*3/8+5,(this.mentionList.size() -1 - i - 7)*del,del,del);
				}

				//(count + this.mentionList.get(i)[3]);
				count++;
			}
			for(int j =0;j < 9;j++){
				line(0,del*j,this.x*3/4,del*j);
			}
			strokeWeight(10); 
			line(this.x*3/8-5,0,this.x*3/8-5,this.y);
			strokeWeight(1);
			line(this.x*3/4,0,this.x*3/4,this.y);
			break;

		case 14:
			del = this.y/7;
			background(197,42,92);
			stroke(0);
			//PFont font = createFont("MS Gothic",48,true);
			// フォントを設定
			textFont(font);
			fill(h%360,80,80);
			textSize(this.x/75);
			strokeWeight(1);
			count = 0;
			for(int i = this.mentionList.size()-1;i >= 6;i--){
				String r = this.mentionList.get(i)[3].replaceAll("\n", " ");
				String str = "";
				if(r.length() > 43){
					str += r.substring(0,25) + "\n" + r.substring(25,43)+ "...";
				}else if(r.length() <= 43 && r.length() > 25){
					str += r.substring(0,25) + "\n" + r.substring(25);
				}else{
					str = r;
				}
				if(count < 7){
					fill(0);
					text(this.mentionList.get(i)[0] + "(@" + this.mentionList.get(i)[1] + ")",del + 10,(this.mentionList.size() -1 - i)*del+ 20);
					text(str,del + 10,(this.mentionList.size() -1 - i)*del + 40);
					PImage img = loadImage(this.mentionList.get(i)[2]);
					image(img,5,(this.mentionList.size() -1 - i)*del,del,del);
				}else{
					text(this.mentionList.get(i)[0] + "(@" + this.mentionList.get(i)[1] + ")",this.x*3/8 + del + 15,(this.mentionList.size() -1 - i - 7)*del+20);
					text(str,this.x*3/8 + del + 15,(this.mentionList.size() -1 - i - 7)*del+40);
					PImage img = loadImage(this.mentionList.get(i)[2]);
					image(img,this.x*3/8+5,(this.mentionList.size() -1 - i - 7)*del,del,del);
				}

				//(count + this.mentionList.get(i)[3]);
				count++;
			}
			for(int j =0;j < 9;j++){
				line(0,del*j,this.x*3/4,del*j);
			}
			strokeWeight(10); 
			line(this.x*3/8-5,0,this.x*3/8-5,this.y);
			strokeWeight(1);
			line(this.x*3/4,0,this.x*3/4,this.y);
			break;
		case 15:
			del = this.y/8;
			background(197,42,92);
			stroke(0);
			//PFont font = createFont("MS Gothic",48,true);
			// フォントを設定
			textFont(font);
			fill(h%360,80,80);
			textSize(this.x/75);
			strokeWeight(1);
			count = 0;
			for(int i = this.mentionList.size()-1;i >= 5;i--){
				String r = this.mentionList.get(i)[3].replaceAll("\n", " ");
				String str = "";
				if(r.length() > 43){
					str += r.substring(0,25) + "\n" + r.substring(25,43)+ "...";
				}else if(r.length() <= 43 && r.length() > 25){
					str += r.substring(0,25) + "\n" + r.substring(25);
				}else{
					str = r;
				}
				if(count < 8){
					fill(0);
					text(this.mentionList.get(i)[0] + "(@" + this.mentionList.get(i)[1] + ")",del + 10,(this.mentionList.size() -1 - i)*del+ 20);
					text(str,del + 10,(this.mentionList.size() -1 - i)*del + 40);
					PImage img = loadImage(this.mentionList.get(i)[2]);
					image(img,5,(this.mentionList.size() -1 - i)*del,del,del);
				}else{
					text(this.mentionList.get(i)[0] + "(@" + this.mentionList.get(i)[1] + ")",this.x*3/8 + del + 15,(this.mentionList.size() -1 - i - 8)*del+20);
					text(str,this.x*3/8 + del + 15,(this.mentionList.size() -1 - i - 8)*del+40);
					PImage img = loadImage(this.mentionList.get(i)[2]);
					image(img,this.x*3/8+5,(this.mentionList.size() -1 - i - 8)*del,del,del);
				}

				//(count + this.mentionList.get(i)[3]);
				count++;
			}
			for(int j =0;j < 9;j++){
				line(0,del*j,this.x*3/4,del*j);
			}
			strokeWeight(10); 
			line(this.x*3/8-5,0,this.x*3/8-5,this.y);
			strokeWeight(1);
			line(this.x*3/4,0,this.x*3/4,this.y);
			break;

		case 16:
			del = this.y/8;
			background(197,42,92);
			stroke(0);
			//PFont font = createFont("MS Gothic",48,true);
			// フォントを設定
			textFont(font);
			fill(h%360,80,80);
			textSize(this.x/75);
			strokeWeight(1);
			count = 0;
			for(int i = this.mentionList.size()-1;i >= 4;i--){
				String r = this.mentionList.get(i)[3].replaceAll("\n", " ");
				String str = "";
				if(r.length() > 43){
					str += r.substring(0,25) + "\n" + r.substring(25,43)+ "...";
				}else if(r.length() <= 43 && r.length() > 25){
					str += r.substring(0,25) + "\n" + r.substring(25);
				}else{
					str = r;
				}
				if(count < 8){
					fill(0);
					text(this.mentionList.get(i)[0] + "(@" + this.mentionList.get(i)[1] + ")",del + 10,(this.mentionList.size() -1 - i)*del+ 20);
					text(str,del + 10,(this.mentionList.size() -1 - i)*del + 40);
					PImage img = loadImage(this.mentionList.get(i)[2]);
					image(img,5,(this.mentionList.size() -1 - i)*del,del,del);
				}else{
					text(this.mentionList.get(i)[0] + "(@" + this.mentionList.get(i)[1] + ")",this.x*3/8 + del + 15,(this.mentionList.size() -1 - i - 8)*del+20);
					text(str,this.x*3/8 + del + 15,(this.mentionList.size() -1 - i - 8)*del+40);
					PImage img = loadImage(this.mentionList.get(i)[2]);
					image(img,this.x*3/8+5,(this.mentionList.size() -1 - i - 8)*del,del,del);
				}

				//(count + this.mentionList.get(i)[3]);
				count++;
			}
			for(int j =0;j < 9;j++){
				line(0,del*j,this.x*3/4,del*j);
			}
			strokeWeight(10); 
			line(this.x*3/8-5,0,this.x*3/8-5,this.y);
			strokeWeight(1);
			line(this.x*3/4,0,this.x*3/4,this.y);
			break;

		case 17:
			del = this.y/9;
			background(197,42,92);
			stroke(0);
			//PFont font = createFont("MS Gothic",48,true);
			// フォントを設定
			textFont(font);
			fill(h%360,80,80);
			textSize(this.x/75);
			strokeWeight(1);
			count = 0;
			for(int i = this.mentionList.size()-1;i >= 3;i--){
				String r = this.mentionList.get(i)[3].replaceAll("\n", " ");
				String str = "";
				if(r.length() > 43){
					str += r.substring(0,25) + "\n" + r.substring(25,43)+ "...";
				}else if(r.length() <= 43 && r.length() > 25){
					str += r.substring(0,25) + "\n" + r.substring(25);
				}else{
					str = r;
				}
				if(count < 9){
					fill(0);
					text(this.mentionList.get(i)[0] + "(@" + this.mentionList.get(i)[1] + ")",del + 10,(this.mentionList.size() -1 - i)*del+ 20);
					text(str,del + 10,(this.mentionList.size() -1 - i)*del + 40);
					PImage img = loadImage(this.mentionList.get(i)[2]);
					image(img,5,(this.mentionList.size() -1 - i)*del,del,del);
				}else{
					text(this.mentionList.get(i)[0] + "(@" + this.mentionList.get(i)[1] + ")",this.x*3/8 + del + 15,(this.mentionList.size() -1 - i - 9)*del+20);
					text(str,this.x*3/8 + del + 15,(this.mentionList.size() -1 - i - 9)*del+40);
					PImage img = loadImage(this.mentionList.get(i)[2]);
					image(img,this.x*3/8+5,(this.mentionList.size() -1 - i - 9)*del,del,del);
				}

				//(count + this.mentionList.get(i)[3]);
				count++;
			}
			for(int j =0;j < 10;j++){
				line(0,del*j,this.x*3/4,del*j);
			}
			strokeWeight(10); 
			line(this.x*3/8-5,0,this.x*3/8-5,this.y);
			strokeWeight(1);
			line(this.x*3/4,0,this.x*3/4,this.y);
			break;
		case 18:
			del = this.y/9;
			background(197,42,92);
			stroke(0);
			//PFont font = createFont("MS Gothic",48,true);
			// フォントを設定
			textFont(font);
			fill(h%360,80,80);
			textSize(this.x/75);
			strokeWeight(1);
			count = 0;
			for(int i = this.mentionList.size()-1;i >= 2;i--){
				String r = this.mentionList.get(i)[3].replaceAll("\n", " ");
				String str = "";
				if(r.length() > 43){
					str += r.substring(0,25) + "\n" + r.substring(25,43)+ "...";
				}else if(r.length() <= 43 && r.length() > 25){
					str += r.substring(0,25) + "\n" + r.substring(25);
				}else{
					str = r;
				}
				if(count < 9){
					fill(0);
					text(this.mentionList.get(i)[0] + "(@" + this.mentionList.get(i)[1] + ")",del + 10,(this.mentionList.size() -1 - i)*del+ 20);
					text(str,del + 10,(this.mentionList.size() -1 - i)*del + 40);
					PImage img = loadImage(this.mentionList.get(i)[2]);
					image(img,5,(this.mentionList.size() -1 - i)*del,del,del);
				}else{
					text(this.mentionList.get(i)[0] + "(@" + this.mentionList.get(i)[1] + ")",this.x*3/8 + del + 15,(this.mentionList.size() -1 - i - 9)*del+20);
					text(str,this.x*3/8 + del + 15,(this.mentionList.size() -1 - i - 9)*del+40);
					PImage img = loadImage(this.mentionList.get(i)[2]);
					image(img,this.x*3/8+5,(this.mentionList.size() -1 - i - 9)*del,del,del);
				}

				//(count + this.mentionList.get(i)[3]);
				count++;
			}
			for(int j =0;j < 10;j++){
				line(0,del*j,this.x*3/4,del*j);
			}
			strokeWeight(10); 
			line(this.x*3/8-5,0,this.x*3/8-5,this.y);
			strokeWeight(1);
			line(this.x*3/4,0,this.x*3/4,this.y);
			break;

		case 19:
			del = this.y/10;
			background(197,42,92);
			stroke(0);
			//PFont font = createFont("MS Gothic",48,true);
			// フォントを設定
			textFont(font);
			//fill(h%360,80,80);
			textSize(this.x/68);
			strokeWeight(1);
			count = 0;
			for(int i = this.mentionList.size()-1;i >= 1;i--){
				String r = this.mentionList.get(i)[3].replaceAll("\n", " ");
				String str = "";
				if(r.length() > 43){
					str += r.substring(0,25) + "\n" + r.substring(25,43)+ "...";
				}else if(r.length() <= 43 && r.length() > 25){
					str += r.substring(0,25) + "\n" + r.substring(25);
				}else{
					str = r;
				}
				if(count < 10){
					fill(0);
					text(this.mentionList.get(i)[0] + "(@" + this.mentionList.get(i)[1] + ")",del + 10,(this.mentionList.size() -1 - i)*del+ 20);
					text(str,del + 10,(this.mentionList.size() -1 - i)*del + 40);
					PImage img = loadImage(this.mentionList.get(i)[2]);
					image(img,5,(this.mentionList.size() -1 - i)*del,del,del);
				}else{
					text(this.mentionList.get(i)[0] + "(@" + this.mentionList.get(i)[1] + ")",this.x*3/8 + del + 15,(this.mentionList.size() -1 - i - 10)*del+20);
					text(str,this.x*3/8 + del + 15,(this.mentionList.size() -1 - i - 10)*del+40);
					PImage img = loadImage(this.mentionList.get(i)[2]);
					image(img,this.x*3/8+5,(this.mentionList.size() -1 - i - 10)*del,del,del);
				}
				line(0,del*i,this.x*3/4,del*i);
				count++;
			}
			strokeWeight(10); 
			line(this.x*3/8-5,0,this.x*3/8-5,this.y);
			strokeWeight(1);
			line(this.x*3/4,0,this.x*3/4,this.y);
			break;
		case 20:
			del = this.y/10;
			background(197,42,92);
			stroke(0);
			//PFont font = createFont("MS Gothic",48,true);
			// フォントを設定
			textFont(font);
			//fill(h%360,80,80);
			textSize(this.x/68);
			strokeWeight(1);
			count = 0;
			for(int i = this.mentionList.size()-1;i >= 0;i--){
				String r = this.mentionList.get(i)[3].replaceAll("\n", " ");
				String str = "";
				if(r.length() > 43){
					str += r.substring(0,25) + "\n" + r.substring(25,43)+ "...";
				}else if(r.length() <= 43 && r.length() > 25){
					str += r.substring(0,25) + "\n" + r.substring(25);
				}else{
					str = r;
				}
				if(count < 10){
					fill(0);
					text(this.mentionList.get(i)[0] + "(" + this.mentionList.get(i)[1] + ")",del + 10,(this.mentionList.size() -1 - i)*del+ 20);
					text(str,del + 10,(this.mentionList.size() -1 - i)*del + 40);
					PImage img = loadImage(this.mentionList.get(i)[2]);
					image(img,5,(this.mentionList.size() -1 - i)*del,del,del);
				}else{

					fill(0);
					text(this.mentionList.get(i)[0] + "(" + this.mentionList.get(i)[1] + ")",this.x*3/8 + del + 15,(this.mentionList.size() -1 - i - 10)*del+20);
					text(str,this.x*3/8 + del + 15,(this.mentionList.size() -1 - i - 10)*del+40);
					PImage img = loadImage(this.mentionList.get(i)[2]);
					image(img,this.x*3/8+5,(this.mentionList.size() -1 - i - 10)*del,del,del);
				}
				line(0,del*i,this.x*3/4,del*i);
				count++;
			}
			strokeWeight(10); 
			line(this.x*3/8-5,0,this.x*3/8-5,this.y);
			strokeWeight(1);
			line(this.x*3/4,0,this.x*3/4,this.y);
			//System.out.println("デバッグ1");
			break;

		}
		//System.out.println(this.x + ";" + this.y);
		fill(0, 0, 0);
		rect(this.x*3/4,0,this.x/4,this.y);
		if(this.wflag == 0){
			fill(this.h%360,80,80);
			//System.out.println("h = " + this.h);
			textSize(24);
			fill(this.h%360,80,80);
			text("Weather",this.x*3/4 + this.x/8,this.y/4);
			this.h++;
		}
		if(this.rflag == 0){
			fill(this.h%360,80,80);
			//System.out.println("h = " + this.h);
			textSize(24);
			fill(this.h%360,80,80);
			text("Rain",this.x*3/4 + this.x/8,this.y*3/4);
			this.h++;
		}


	}

	
	
	
	/**
	 * dmを表示させる
	 */
	private void DirectMessage(){

	}

	
	
	
	/**
	 * followlistを表示する
	 */
	private void Follow(){

	}

	
	
	
	/**
	 * followerListを表示する
	 */
	private void Follower(){
	}

	
	
	/**
	 * ハッシュツイートを表示する
	 */
	private void hashTweet(){

		int del = 0;
		int count = 0;
		PFont font = createFont("MS Gothic",48,true);
		switch(this.shownum){
		case 1:
			if(this.shownum == 1){
				del = this.y/2;
				background(197,42,92);
				stroke(0);
				//PFont font = createFont("MS Gothic",48,true);
				// フォントを設定
				textFont(font);
				fill(h%360,80,80);
				textSize(20);
				strokeWeight(1);
				String r = this.hashList.get(19)[3].replaceAll("\n", " ");
				String str = "\n\n";
				if(r.length() > 124){
					str += r.substring(0,34) + "\n" + r.substring(34,64) + "\n" + r.substring(64,94) + "\n" + r.substring(94,124) + "\n" + r.substring(124);
				}else if(r.length() > 94 && r.length() <= 124){
					str += r.substring(0,34) + "\n" + r.substring(34,64) + "\n" + r.substring(64,94) + "\n" + r.substring(94);
				}
				else if(r.length() > 64 && r.length() <= 94){
					str += r.substring(0,34) + "\n" + r.substring(34,64) + "\n" + r.substring(64);
				}else if(r.length() > 34 && r.length() <= 64){
					str += r.substring(0,34) + "\n" + r.substring(34);
				}else{
					str += r;
				}

				fill(0);
				text("\n" + this.hashList.get(19)[0] + "(@" + this.hashList.get(19)[1] + ")",del + 10,(this.hashList.size() -1 - 19)*del+ 20);
				text(str,del + 10,(this.hashList.size() -1 - 19)*del + 40);
				PImage img = loadImage(this.hashList.get(19)[2]);
				image(img,5,(this.hashList.size() -1 - 19)*del+del/16,del*3/4,del*3/4);

				line(this.x*3/4,0,this.x*3/4,this.y);
			}
			break;

		case 2:
			del = this.y/2;
			background(197,42,92);
			stroke(0);
			//PFont font = createFont("MS Gothic",48,true);
			// フォントを設定
			textFont(font);
			fill(h%360,80,80);
			textSize(20);
			strokeWeight(1);
			count = 0;
			for(int i = this.hashList.size()-1;i >= 18;i--){
				String r = this.hashList.get(i)[3].replaceAll("\n", " ");
				String str = "\n\n";
				if(r.length() > 124){
					str += r.substring(0,34) + "\n" + r.substring(34,64) + "\n" + r.substring(64,94) + "\n" + r.substring(94,124) + "\n" + r.substring(124);
				}else if(r.length() > 94 && r.length() <= 124){
					str += r.substring(0,34) + "\n" + r.substring(34,64) + "\n" + r.substring(64,94) + "\n" + r.substring(94);
				}
				else if(r.length() > 64 && r.length() <= 94){
					str += r.substring(0,34) + "\n" + r.substring(34,64) + "\n" + r.substring(64);
				}else if(r.length() > 34 && r.length() <= 64){
					str += r.substring(0,34) + "\n" + r.substring(34);
				}else{
					str += r;
				}

				fill(0);
				text("\n" + this.hashList.get(i)[0] + "(@" + this.hashList.get(i)[1] + ")",del + 10,(this.hashList.size() -1 - i)*del+ 20);
				text(str,del + 10,(this.hashList.size() -1 - i)*del + 40);
				PImage img = loadImage(this.hashList.get(i)[2]);
				image(img,5,(this.hashList.size() -1 - i)*del+del/16,del*3/4,del*3/4);

				count++;
			}
			for(int j =0;j < 2;j++){
				line(0,del*j,this.x*3/4,del*j);
			}
			line(this.x*3/4,0,this.x*3/4,this.y);
			break;

		case 3:
			del = this.y/3;
			background(197,42,92);
			stroke(0);
			//PFont font = createFont("MS Gothic",48,true);
			// フォントを設定
			textFont(font);
			fill(h%360,80,80);
			textSize(20);
			strokeWeight(1);
			count = 0;
			for(int i = this.hashList.size()-1;i >= 17;i--){
				String r = this.hashList.get(i)[3].replaceAll("\n", " ");
				String str = "\n\n";
				if(r.length() > 110){
					str += r.substring(0,40) + "\n" + r.substring(40,75) + "\n" + r.substring(75,110) + "...";
				}
				else if(r.length() > 75 && r.length() <= 110){
					str += r.substring(0,40) + "\n" + r.substring(40,75) + "\n" + r.substring(75);
				}else if(r.length() > 40 && r.length() <= 75){
					str += r.substring(0,40) + "\n" + r.substring(40);
				}else{
					str += r;
				}

				fill(0);
				text("\n" + this.hashList.get(i)[0] + "(@" + this.hashList.get(i)[1] + ")",del + 10,(this.hashList.size() -1 - i)*del+ 20);
				text(str,del + 10,(this.hashList.size() -1 - i)*del + 40);
				PImage img = loadImage(this.hashList.get(i)[2]);
				image(img,5,(this.hashList.size() -1 - i)*del+del/16,del*3/4,del*3/4);

				count++;
			}
			for(int j =0;j < 3;j++){
				line(0,del*j,this.x*3/4,del*j);
			}
			line(this.x*3/4,0,this.x*3/4,this.y);
			break;

		case 4:
			del = this.y/4;
			background(197,42,92);
			stroke(0);
			//PFont font = createFont("MS Gothic",48,true);
			// フォントを設定
			textFont(font);
			fill(h%360,80,80);
			textSize(20);
			strokeWeight(1);
			count = 0;
			for(int i = this.hashList.size()-1;i >= 16;i--){
				String r = this.hashList.get(i)[3].replaceAll("\n", " ");
				String str = "\n";
				if(r.length() > 120){
					str += r.substring(0,43) + "\n" + r.substring(43,83) + "\n" + r.substring(83,120) + "...";
				}
				else if(r.length() > 83 && r.length() <= 120){
					str += r.substring(0,43) + "\n" + r.substring(43,83) + "\n" + r.substring(83);
				}else if(r.length() > 43 && r.length() <= 83){
					str += r.substring(0,43) + "\n" + r.substring(43);
				}else{
					str += r;
				}

				fill(0);
				text(this.hashList.get(i)[0] + "(@" + this.hashList.get(i)[1] + ")",del + 10,(this.hashList.size() -1 - i)*del+ 20);
				text(str,del + 10,(this.hashList.size() -1 - i)*del + 40);
				PImage img = loadImage(this.hashList.get(i)[2]);
				image(img,5,(this.hashList.size() -1 - i)*del,del,del);

				count++;
			}
			for(int j =0;j < 4;j++){
				line(0,del*j,this.x*3/4,del*j);
			}
			line(this.x*3/4,0,this.x*3/4,this.y);
			break;

		case 5:
			del = this.y/5;
			background(197,42,92);
			stroke(0);
			//PFont font = createFont("MS Gothic",48,true);
			// フォントを設定
			textFont(font);
			fill(h%360,80,80);
			textSize(20);
			strokeWeight(1);
			count = 0;
			for(int i = this.hashList.size()-1;i >= 15;i--){
				String r = this.hashList.get(i)[3].replaceAll("\n", " ");
				String str = "\n";
				if(r.length() > 120){
					str += r.substring(0,46) + "\n" + r.substring(46,83) + "\n" + r.substring(83,120) + "...";
				}
				else if(r.length() > 83 && r.length() <= 120){
					str += r.substring(0,46) + "\n" + r.substring(46,83) + "\n" + r.substring(83);
				}else if(r.length() > 46 && r.length() <= 83){
					str += r.substring(0,46) + "\n" + r.substring(46);
				}else{
					str += r;
				}

				fill(0);
				text(this.hashList.get(i)[0] + "(@" + this.hashList.get(i)[1] + ")",del + 10,(this.hashList.size() -1 - i)*del+ 20);
				text(str,del + 10,(this.hashList.size() -1 - i)*del + 40);
				PImage img = loadImage(this.hashList.get(i)[2]);
				image(img,5,(this.hashList.size() -1 - i)*del,del,del);

				count++;
			}
			for(int j =0;j < 5;j++){
				line(0,del*j,this.x*3/4,del*j);
			}
			line(this.x*3/4,0,this.x*3/4,this.y);
			break;

		case 6:
			del = this.y/6;
			background(197,42,92);
			stroke(0);
			//PFont font = createFont("MS Gothic",48,true);
			// フォントを設定
			textFont(font);
			fill(h%360,80,80);
			textSize(20);
			strokeWeight(1);
			count = 0;
			for(int i = this.hashList.size()-1;i >= 14;i--){
				String r = this.hashList.get(i)[3].replaceAll("\n", " ");
				String str = "\n";
				if(r.length() > 83){
					str += r.substring(0,46) + "\n" + r.substring(46,83) + "...";
				}else if(r.length() > 46 && r.length() <= 83){
					str += r.substring(0,46) + "\n" + r.substring(46);
				}else{
					str += r;
				}

				fill(0);
				text(this.hashList.get(i)[0] + "(@" + this.hashList.get(i)[1] + ")",del + 10,(this.hashList.size() -1 - i)*del+ 20);
				text(str,del + 10,(this.hashList.size() -1 - i)*del + 40);
				PImage img = loadImage(this.hashList.get(i)[2]);
				image(img,5,(this.hashList.size() -1 - i)*del,del,del);

				count++;
			}
			for(int j =0;j < 6;j++){
				line(0,del*j,this.x*3/4,del*j);
			}
			line(this.x*3/4,0,this.x*3/4,this.y);
			break;

		case 7:
			del = this.y/7;
			background(197,42,92);
			stroke(0);
			//PFont font = createFont("MS Gothic",48,true);
			// フォントを設定
			textFont(font);
			fill(h%360,80,80);
			textSize(20);
			strokeWeight(1);
			count = 0;
			for(int i = this.hashList.size()-1;i >= 13;i--){
				String r = this.hashList.get(i)[3].replaceAll("\n", " ");
				String str = "\n";
				if(r.length() > 83){
					str += r.substring(0,46) + "\n" + r.substring(46,83) + "...";
				}else if(r.length() > 46 && r.length() <= 83){
					str += r.substring(0,46) + "\n" + r.substring(46);
				}else{
					str += r;
				}

				fill(0);
				text(this.hashList.get(i)[0] + "(@" + this.hashList.get(i)[1] + ")",del + 10,(this.hashList.size() -1 - i)*del+ 20);
				text(str,del + 10,(this.hashList.size() -1 - i)*del + 40);
				PImage img = loadImage(this.hashList.get(i)[2]);
				image(img,5,(this.hashList.size() -1 - i)*del,del,del);

				count++;
			}
			for(int j =0;j < 7;j++){
				line(0,del*j,this.x*3/4,del*j);
			}
			line(this.x*3/4,0,this.x*3/4,this.y);
			break;

		case 8:
			del = this.y/8;
			background(197,42,92);
			stroke(0);
			//PFont font = createFont("MS Gothic",48,true);
			// フォントを設定
			textFont(font);
			fill(h%360,80,80);
			textSize(20);
			strokeWeight(1);
			count = 0;
			for(int i = this.hashList.size()-1;i >= 12;i--){
				String r = this.hashList.get(i)[3].replaceAll("\n", " ");
				String str = "";
				if(r.length() > 83){
					str += r.substring(0,46) + "\n" + r.substring(46,83) + "...";
				}else if(r.length() > 46 && r.length() <= 83){
					str += r.substring(0,46) + "\n" + r.substring(46);
				}else{
					str = r;
				}

				fill(0);
				text(this.hashList.get(i)[0] + "(@" + this.hashList.get(i)[1] + ")",del + 10,(this.hashList.size() -1 - i)*del+ 20);
				text(str,del + 10,(this.hashList.size() -1 - i)*del + 40);
				PImage img = loadImage(this.hashList.get(i)[2]);
				image(img,5,(this.hashList.size() -1 - i)*del,del,del);

				count++;
			}
			for(int j =0;j < 8;j++){
				line(0,del*j,this.x*3/4,del*j);
			}
			line(this.x*3/4,0,this.x*3/4,this.y);
			break;

		case 9:
			del = this.y/9;
			background(197,42,92);
			stroke(0);
			//PFont font = createFont("MS Gothic",48,true);
			// フォントを設定
			textFont(font);
			fill(h%360,80,80);
			textSize(20);
			strokeWeight(1);
			count = 0;
			for(int i = this.hashList.size()-1;i >= 11;i--){
				String r = this.hashList.get(i)[3].replaceAll("\n", " ");
				String str = "";
				if(r.length() > 83){
					str += r.substring(0,46) + "\n" + r.substring(46,83) + "...";
				}else if(r.length() > 46 && r.length() <= 83){
					str += r.substring(0,46) + "\n" + r.substring(46);
				}else{
					str = r;
				}

				fill(0);
				text(this.hashList.get(i)[0] + "(@" + this.hashList.get(i)[1] + ")",del + 10,(this.hashList.size() -1 - i)*del+ 20);
				text(str,del + 10,(this.hashList.size() -1 - i)*del + 40);
				PImage img = loadImage(this.hashList.get(i)[2]);
				image(img,5,(this.hashList.size() -1 - i)*del,del,del);

				count++;
			}
			for(int j =0;j < 9;j++){
				line(0,del*j,this.x*3/4,del*j);
			}
			line(this.x*3/4,0,this.x*3/4,this.y);
			break;

		case 10:
			del = this.y/10;
			background(197,42,92);
			stroke(0);
			//PFont font = createFont("MS Gothic",48,true);
			// フォントを設定
			textFont(font);
			fill(h%360,80,80);
			textSize(20);
			strokeWeight(1);
			count = 0;
			for(int i = this.hashList.size()-1;i >= 10;i--){
				String r = this.hashList.get(i)[3].replaceAll("\n", " ");
				String str = "";
				if(r.length() > 83){
					str += r.substring(0,46) + "\n" + r.substring(46,83) + "...";
				}else if(r.length() > 46 && r.length() <= 83){
					str += r.substring(0,46) + "\n" + r.substring(46);
				}else{
					str = r;
				}

				fill(0);
				text(this.hashList.get(i)[0] + "(@" + this.hashList.get(i)[1] + ")",del + 10,(this.hashList.size() -1 - i)*del+ 20);
				text(str,del + 10,(this.hashList.size() -1 - i)*del + 40);
				PImage img = loadImage(this.hashList.get(i)[2]);
				image(img,5,(this.hashList.size() -1 - i)*del,del,del);

				count++;
			}
			for(int j =0;j < 10;j++){
				line(0,del*j,this.x*3/4,del*j);
			}
			line(this.x*3/4,0,this.x*3/4,this.y);
			break;

		case 11:
			del = this.y/6;
			background(197,42,92);
			stroke(0);
			//PFont font = createFont("MS Gothic",48,true);
			// フォントを設定
			textFont(font);
			fill(h%360,80,80);
			textSize(20);
			strokeWeight(1);
			count = 0;
			for(int i = this.hashList.size()-1;i >= 9;i--){
				String r = this.hashList.get(i)[3].replaceAll("\n", " ");
				String str = "";
				if(r.length() > 59){
					str += r.substring(0,22) + "\n" + r.substring(22,40)+ "\n" + r.substring(40,56)+"...";	
				}else if(r.length() > 40 && r.length() <= 59){
					str += r.substring(0,22) + "\n" + r.substring(22,40) + "\n" + r.substring(40) ;
				}else if(r.length() <= 40 && r.length() > 22){
					str += r.substring(0,24) + "\n" + r.substring(24);
				}else{
					str = r;
				}
				if(count < 6){
					fill(0);
					text(this.hashList.get(i)[0] + "(@" + this.hashList.get(i)[1] + ")",del + 10,(this.hashList.size() -1 - i)*del+ 20);
					text(str,del + 10,(this.hashList.size() -1 - i)*del + 40);
					PImage img = loadImage(this.hashList.get(i)[2]);
					image(img,5,(this.hashList.size() -1 - i)*del,del,del);
				}else{
					text(this.hashList.get(i)[0] + "(@" + this.hashList.get(i)[1] + ")",this.x*3/8 + del + 15,(this.hashList.size() -1 - i - 6)*del+20);
					text(str,this.x*3/8 + del + 15,(this.hashList.size() -1 - i - 6)*del+40);
					PImage img = loadImage(this.hashList.get(i)[2]);
					image(img,this.x*3/8+5,(this.hashList.size() -1 - i - 6)*del,del,del);
				}

				count++;
			}
			for(int j =0;j < 9;j++){
				line(0,del*j,this.x*3/4,del*j);
			}
			strokeWeight(10); 
			line(this.x*3/8-5,0,this.x*3/8-5,this.y);
			strokeWeight(1);
			line(this.x*3/4,0,this.x*3/4,this.y);
			break;

		case 12:
			del = this.y/6;
			background(197,42,92);
			stroke(0);
			//PFont font = createFont("MS Gothic",48,true);
			// フォントを設定
			textFont(font);
			fill(h%360,80,80);
			textSize(20);
			strokeWeight(1);
			count = 0;
			for(int i = this.hashList.size()-1;i >= 8;i--){
				String r = this.hashList.get(i)[3].replaceAll("\n", " ");
				String str = "";
				if(r.length() > 59){
					str += r.substring(0,22) + "\n" + r.substring(22,40)+ "\n" + r.substring(40,56)+"...";	
				}else if(r.length() > 40 && r.length() <= 59){
					str += r.substring(0,22) + "\n" + r.substring(22,40) + "\n" + r.substring(40) ;
				}else if(r.length() <= 40 && r.length() > 22){
					str += r.substring(0,24) + "\n" + r.substring(24);
				}else{
					str = r;
				}
				if(count < 6){
					fill(0);
					text(this.hashList.get(i)[0] + "(@" + this.hashList.get(i)[1] + ")",del + 10,(this.hashList.size() -1 - i)*del+ 20);
					text(str,del + 10,(this.hashList.size() -1 - i)*del + 40);
					PImage img = loadImage(this.hashList.get(i)[2]);
					image(img,5,(this.hashList.size() -1 - i)*del,del,del);
				}else{
					text(this.hashList.get(i)[0] + "(@" + this.hashList.get(i)[1] + ")",this.x*3/8 + del + 15,(this.hashList.size() -1 - i - 6)*del+20);
					text(str,this.x*3/8 + del + 15,(this.hashList.size() -1 - i - 6)*del+40);
					PImage img = loadImage(this.hashList.get(i)[2]);
					image(img,this.x*3/8+5,(this.hashList.size() -1 - i - 6)*del,del,del);
				}

				//(count + this.hashList.get(i)[3]);
				count++;
			}
			for(int j =0;j < 9;j++){
				line(0,del*j,this.x*3/4,del*j);
			}
			strokeWeight(10); 
			line(this.x*3/8-5,0,this.x*3/8-5,this.y);
			strokeWeight(1);
			line(this.x*3/4,0,this.x*3/4,this.y);
			break;

		case 13:
			del = this.y/7;
			background(197,42,92);
			stroke(0);
			//PFont font = createFont("MS Gothic",48,true);
			// フォントを設定
			textFont(font);
			fill(h%360,80,80);
			textSize(18);
			strokeWeight(1);
			count = 0;
			for(int i = this.hashList.size()-1;i >= 7;i--){
				String r = this.hashList.get(i)[3].replaceAll("\n", " ");
				String str = "";
				if(r.length() > 43){
					str += r.substring(0,25) + "\n" + r.substring(25,43)+ "...";
				}else if(r.length() <= 43 && r.length() > 25){
					str += r.substring(0,25) + "\n" + r.substring(25);
				}else{
					str = r;
				}
				if(count < 7){
					fill(0);
					text(this.hashList.get(i)[0] + "(@" + this.hashList.get(i)[1] + ")",del + 10,(this.hashList.size() -1 - i)*del+ 20);
					text(str,del + 10,(this.hashList.size() -1 - i)*del + 40);
					PImage img = loadImage(this.hashList.get(i)[2]);
					image(img,5,(this.hashList.size() -1 - i)*del,del,del);
				}else{
					text(this.hashList.get(i)[0] + "(@" + this.hashList.get(i)[1] + ")",this.x*3/8 + del + 15,(this.hashList.size() -1 - i - 7)*del+20);
					text(str,this.x*3/8 + del + 15,(this.hashList.size() -1 - i - 7)*del+40);
					PImage img = loadImage(this.hashList.get(i)[2]);
					image(img,this.x*3/8+5,(this.hashList.size() -1 - i - 7)*del,del,del);
				}

				//(count + this.hashList.get(i)[3]);
				count++;
			}
			for(int j =0;j < 9;j++){
				line(0,del*j,this.x*3/4,del*j);
			}
			strokeWeight(10); 
			line(this.x*3/8-5,0,this.x*3/8-5,this.y);
			strokeWeight(1);
			line(this.x*3/4,0,this.x*3/4,this.y);
			break;

		case 14:
			del = this.y/7;
			background(197,42,92);
			stroke(0);
			//PFont font = createFont("MS Gothic",48,true);
			// フォントを設定
			textFont(font);
			fill(h%360,80,80);
			textSize(18);
			strokeWeight(1);
			count = 0;
			for(int i = this.hashList.size()-1;i >= 6;i--){
				String r = this.hashList.get(i)[3].replaceAll("\n", " ");
				String str = "";
				if(r.length() > 43){
					str += r.substring(0,25) + "\n" + r.substring(25,43)+ "...";
				}else if(r.length() <= 43 && r.length() > 25){
					str += r.substring(0,25) + "\n" + r.substring(25);
				}else{
					str = r;
				}
				if(count < 7){
					fill(0);
					text(this.hashList.get(i)[0] + "(@" + this.hashList.get(i)[1] + ")",del + 10,(this.hashList.size() -1 - i)*del+ 20);
					text(str,del + 10,(this.hashList.size() -1 - i)*del + 40);
					PImage img = loadImage(this.hashList.get(i)[2]);
					image(img,5,(this.hashList.size() -1 - i)*del,del,del);
				}else{
					text(this.hashList.get(i)[0] + "(@" + this.hashList.get(i)[1] + ")",this.x*3/8 + del + 15,(this.hashList.size() -1 - i - 7)*del+20);
					text(str,this.x*3/8 + del + 15,(this.hashList.size() -1 - i - 7)*del+40);
					PImage img = loadImage(this.hashList.get(i)[2]);
					image(img,this.x*3/8+5,(this.hashList.size() -1 - i - 7)*del,del,del);
				}

				//(count + this.hashList.get(i)[3]);
				count++;
			}
			for(int j =0;j < 9;j++){
				line(0,del*j,this.x*3/4,del*j);
			}
			strokeWeight(10); 
			line(this.x*3/8-5,0,this.x*3/8-5,this.y);
			strokeWeight(1);
			line(this.x*3/4,0,this.x*3/4,this.y);
			break;
		case 15:
			del = this.y/8;
			background(197,42,92);
			stroke(0);
			//PFont font = createFont("MS Gothic",48,true);
			// フォントを設定
			textFont(font);
			fill(h%360,80,80);
			textSize(18);
			strokeWeight(1);
			count = 0;
			for(int i = this.hashList.size()-1;i >= 5;i--){
				String r = this.hashList.get(i)[3].replaceAll("\n", " ");
				String str = "";
				if(r.length() > 43){
					str += r.substring(0,25) + "\n" + r.substring(25,43)+ "...";
				}else if(r.length() <= 43 && r.length() > 25){
					str += r.substring(0,25) + "\n" + r.substring(25);
				}else{
					str = r;
				}
				if(count < 8){
					fill(0);
					text(this.hashList.get(i)[0] + "(@" + this.hashList.get(i)[1] + ")",del + 10,(this.hashList.size() -1 - i)*del+ 20);
					text(str,del + 10,(this.hashList.size() -1 - i)*del + 40);
					PImage img = loadImage(this.hashList.get(i)[2]);
					image(img,5,(this.hashList.size() -1 - i)*del,del,del);
				}else{
					text(this.hashList.get(i)[0] + "(@" + this.hashList.get(i)[1] + ")",this.x*3/8 + del + 15,(this.hashList.size() -1 - i - 8)*del+20);
					text(str,this.x*3/8 + del + 15,(this.hashList.size() -1 - i - 8)*del+40);
					PImage img = loadImage(this.hashList.get(i)[2]);
					image(img,this.x*3/8+5,(this.hashList.size() -1 - i - 8)*del,del,del);
				}

				//(count + this.hashList.get(i)[3]);
				count++;
			}
			for(int j =0;j < 9;j++){
				line(0,del*j,this.x*3/4,del*j);
			}
			strokeWeight(10); 
			line(this.x*3/8-5,0,this.x*3/8-5,this.y);
			strokeWeight(1);
			line(this.x*3/4,0,this.x*3/4,this.y);
			break;

		case 16:
			del = this.y/8;
			background(197,42,92);
			stroke(0);
			//PFont font = createFont("MS Gothic",48,true);
			// フォントを設定
			textFont(font);
			fill(h%360,80,80);
			textSize(18);
			strokeWeight(1);
			count = 0;
			for(int i = this.hashList.size()-1;i >= 4;i--){
				String r = this.hashList.get(i)[3].replaceAll("\n", " ");
				String str = "";
				if(r.length() > 43){
					str += r.substring(0,25) + "\n" + r.substring(25,43)+ "...";
				}else if(r.length() <= 43 && r.length() > 25){
					str += r.substring(0,25) + "\n" + r.substring(25);
				}else{
					str = r;
				}
				if(count < 8){
					fill(0);
					text(this.hashList.get(i)[0] + "(@" + this.hashList.get(i)[1] + ")",del + 10,(this.hashList.size() -1 - i)*del+ 20);
					text(str,del + 10,(this.hashList.size() -1 - i)*del + 40);
					PImage img = loadImage(this.hashList.get(i)[2]);
					image(img,5,(this.hashList.size() -1 - i)*del,del,del);
				}else{
					text(this.hashList.get(i)[0] + "(@" + this.hashList.get(i)[1] + ")",this.x*3/8 + del + 15,(this.hashList.size() -1 - i - 8)*del+20);
					text(str,this.x*3/8 + del + 15,(this.hashList.size() -1 - i - 8)*del+40);
					PImage img = loadImage(this.hashList.get(i)[2]);
					image(img,this.x*3/8+5,(this.hashList.size() -1 - i - 8)*del,del,del);
				}

				//(count + this.hashList.get(i)[3]);
				count++;
			}
			for(int j =0;j < 9;j++){
				line(0,del*j,this.x*3/4,del*j);
			}
			strokeWeight(10); 
			line(this.x*3/8-5,0,this.x*3/8-5,this.y);
			strokeWeight(1);
			line(this.x*3/4,0,this.x*3/4,this.y);
			break;

		case 17:
			del = this.y/9;
			background(197,42,92);
			stroke(0);
			//PFont font = createFont("MS Gothic",48,true);
			// フォントを設定
			textFont(font);
			fill(h%360,80,80);
			textSize(18);
			strokeWeight(1);
			count = 0;
			for(int i = this.hashList.size()-1;i >= 3;i--){
				String r = this.hashList.get(i)[3].replaceAll("\n", " ");
				String str = "";
				if(r.length() > 43){
					str += r.substring(0,25) + "\n" + r.substring(25,43)+ "...";
				}else if(r.length() <= 43 && r.length() > 25){
					str += r.substring(0,25) + "\n" + r.substring(25);
				}else{
					str = r;
				}
				if(count < 9){
					fill(0);
					text(this.hashList.get(i)[0] + "(@" + this.hashList.get(i)[1] + ")",del + 10,(this.hashList.size() -1 - i)*del+ 20);
					text(str,del + 10,(this.hashList.size() -1 - i)*del + 40);
					PImage img = loadImage(this.hashList.get(i)[2]);
					image(img,5,(this.hashList.size() -1 - i)*del,del,del);
				}else{
					text(this.hashList.get(i)[0] + "(@" + this.hashList.get(i)[1] + ")",this.x*3/8 + del + 15,(this.hashList.size() -1 - i - 9)*del+20);
					text(str,this.x*3/8 + del + 15,(this.hashList.size() -1 - i - 9)*del+40);
					PImage img = loadImage(this.hashList.get(i)[2]);
					image(img,this.x*3/8+5,(this.hashList.size() -1 - i - 9)*del,del,del);
				}

				//(count + this.hashList.get(i)[3]);
				count++;
			}
			for(int j =0;j < 10;j++){
				line(0,del*j,this.x*3/4,del*j);
			}
			strokeWeight(10); 
			line(this.x*3/8-5,0,this.x*3/8-5,this.y);
			strokeWeight(1);
			line(this.x*3/4,0,this.x*3/4,this.y);
			break;
		case 18:
			del = this.y/9;
			background(197,42,92);
			stroke(0);
			//PFont font = createFont("MS Gothic",48,true);
			// フォントを設定
			textFont(font);
			fill(h%360,80,80);
			textSize(18);
			strokeWeight(1);
			count = 0;
			for(int i = this.hashList.size()-1;i >= 2;i--){
				String r = this.hashList.get(i)[3].replaceAll("\n", " ");
				String str = "";
				if(r.length() > 43){
					str += r.substring(0,25) + "\n" + r.substring(25,43)+ "...";
				}else if(r.length() <= 43 && r.length() > 25){
					str += r.substring(0,25) + "\n" + r.substring(25);
				}else{
					str = r;
				}
				if(count < 9){
					fill(0);
					text(this.hashList.get(i)[0] + "(@" + this.hashList.get(i)[1] + ")",del + 10,(this.hashList.size() -1 - i)*del+ 20);
					text(str,del + 10,(this.hashList.size() -1 - i)*del + 40);
					PImage img = loadImage(this.hashList.get(i)[2]);
					image(img,5,(this.hashList.size() -1 - i)*del,del,del);
				}else{
					text(this.hashList.get(i)[0] + "(@" + this.hashList.get(i)[1] + ")",this.x*3/8 + del + 15,(this.hashList.size() -1 - i - 9)*del+20);
					text(str,this.x*3/8 + del + 15,(this.hashList.size() -1 - i - 9)*del+40);
					PImage img = loadImage(this.hashList.get(i)[2]);
					image(img,this.x*3/8+5,(this.hashList.size() -1 - i - 9)*del,del,del);
				}

				//(count + this.hashList.get(i)[3]);
				count++;
			}
			for(int j =0;j < 10;j++){
				line(0,del*j,this.x*3/4,del*j);
			}
			strokeWeight(10); 
			line(this.x*3/8-5,0,this.x*3/8-5,this.y);
			strokeWeight(1);
			line(this.x*3/4,0,this.x*3/4,this.y);
			break;

		case 19:
			del = this.y/10;
			background(197,42,92);
			stroke(0);
			//PFont font = createFont("MS Gothic",48,true);
			// フォントを設定
			textFont(font);
			fill(h%360,80,80);
			textSize(20);
			strokeWeight(1);
			count = 0;
			for(int i = this.hashList.size()-1;i >= 1;i--){
				String r = this.hashList.get(i)[3].replaceAll("\n", " ");
				String str = "";
				if(r.length() > 43){
					str += r.substring(0,25) + "\n" + r.substring(25,43)+ "...";
				}else if(r.length() <= 43 && r.length() > 25){
					str += r.substring(0,25) + "\n" + r.substring(25);
				}else{
					str = r;
				}
				if(count < 10){
					fill(0);
					text(this.hashList.get(i)[0] + "(@" + this.hashList.get(i)[1] + ")",del + 10,(this.hashList.size() -1 - i)*del+ 20);
					text(str,del + 10,(this.hashList.size() -1 - i)*del + 40);
					PImage img = loadImage(this.hashList.get(i)[2]);
					image(img,5,(this.hashList.size() -1 - i)*del,del,del);
				}else{
					text(this.hashList.get(i)[0] + "(@" + this.hashList.get(i)[1] + ")",this.x*3/8 + del + 15,(this.hashList.size() -1 - i - 10)*del+20);
					text(str,this.x*3/8 + del + 15,(this.hashList.size() -1 - i - 10)*del+40);
					PImage img = loadImage(this.hashList.get(i)[2]);
					image(img,this.x*3/8+5,(this.hashList.size() -1 - i - 10)*del,del,del);
				}
				line(0,del*i,this.x*3/4,del*i);
				count++;
			}
			strokeWeight(10); 
			line(this.x*3/8-5,0,this.x*3/8-5,this.y);
			strokeWeight(1);
			line(this.x*3/4,0,this.x*3/4,this.y);
			break;
		case 20:
			del = this.y/10;
			background(197,42,92);
			stroke(0);
			//PFont font = createFont("MS Gothic",48,true);
			// フォントを設定
			textFont(font);
			fill(h%360,80,80);
			textSize(20);
			strokeWeight(1);
			count = 0;
			for(int i = this.hashList.size()-1;i >= 0;i--){
				String r = this.hashList.get(i)[3].replaceAll("\n", " ");
				String str = "";
				if(r.length() > 43){
					str += r.substring(0,25) + "\n" + r.substring(25,43)+ "...";
				}else if(r.length() <= 43 && r.length() > 25){
					str += r.substring(0,25) + "\n" + r.substring(25);
				}else{
					str = r;
				}
				if(count < 10){
					fill(0);
					text(this.hashList.get(i)[0] + "(" + this.hashList.get(i)[1] + ")",del + 10,(this.hashList.size() -1 - i)*del+ 20);
					text(str,del + 10,(this.hashList.size() -1 - i)*del + 40);
					PImage img = loadImage(this.hashList.get(i)[2]);
					image(img,5,(this.hashList.size() -1 - i)*del,del,del);
				}else{
					text(this.hashList.get(i)[0] + "(" + this.hashList.get(i)[1] + ")",this.x*3/8 + del + 15,(this.hashList.size() -1 - i - 10)*del+20);
					text(str,this.x*3/8 + del + 15,(this.hashList.size() -1 - i - 10)*del+40);
					PImage img = loadImage(this.hashList.get(i)[2]);
					image(img,this.x*3/8+5,(this.hashList.size() -1 - i - 10)*del,del,del);
				}
				line(0,del*i,this.x*3/4,del*i);
				count++;
			}
			strokeWeight(10); 
			line(this.x*3/8-5,0,this.x*3/8-5,this.y);
			strokeWeight(1);
			line(this.x*3/4,0,this.x*3/4,this.y);
			break;

		}
		
		fill(0, 0, 0);
		rect(this.x*3/4,0,this.x/4,this.y);
		if(this.wflag == 0){
			fill(this.h%360,80,80);
			//System.out.println("h = " + this.h);
			textSize(24);
			fill(this.h%360,80,80);
			text("Weather",this.x*3/4 + this.x/8,this.y/4);
			this.h++;
		}
		if(this.rflag == 0){
			fill(this.h%360,80,80);
			//System.out.println("h = " + this.h);
			textSize(24);
			fill(this.h%360,80,80);
			text("Rain",this.x*3/4 + this.x/8,this.y*3/4);
			this.h++;
		}

	}

	
	
	
	/**
	 * ツイートリスト初期化メソッド
	 * @param data
	 */
	public void tweetInit(String[] data){
		this.tweetList.add(0,data);
		if(this.tweetList.size() > 20)
			this.tweetList.remove(0);
	}

	
	
	
	/**
	 * メンションリスト初期化メソッド
	 * @param data
	 */
	public void mentionInit(String[] data){
		this.mentionList.add(0,data);
		if(this.mentionList.size() > 20)
			this.mentionList.remove(0);
	}

	
	
	
	/**
	 * ダイレクトメッセージリスト初期化メソッド
	 * @param data
	 */
	public void dmInit(String[] data){
		this.dmList.add(0,data);
		if(this.dmList.size() > 20)
			this.dmList.remove(0);
	}

	
	
	/**
	 * フォローリスト初期化メソッド
	 * @param data
	 */
	public void followInit(String[] data){
		this.followList.add(0,data);
	}

	
	
	/**
	 * フォロワー初期化メソッド
	 * @param data
	 */
	public void followerInit(String[] data){
		this.followerList.add(0,data);
	}

	
	
	/**
	 * ハッシュツイート初期化メソッド
	 * @param data
	 */
	public void hashInit(String[] data){
		this.hashList.add(0,data);
	}

	
	
	/**
	 * ツイートリスト更新メソッド
	 * @param data
	 */
	public void setTweet(String data[]){
		//("更新");
		this.tweetList.add(data);
		this.tweetList.remove(0);
		for(int i=0;i < this.tweetList.size();i++)
			for(int j = 0;j < 4; j++){
				//(i + ":" + this.tweetList.get(i)[j]);
			}
	}

	
	
	/**
	 * Mentionリスト更新メソッド
	 * @param data
	 */
	public void setMention(String[] data){
		this.mentionList.add(data);
		this.mentionList.remove(0);
	}

	
	
	/**
	 * DirectMessageリスト更新メソッド
	 * @param data
	 */
	public void setDM(String[] data){
		this.dmList.add(data);
		this.dmList.remove(0);
	}

	
	
	/**
	 * Followリスト更新メソッド
	 * @param data
	 */
	public void setFollow(String[] data){
		this.followList.add(data);
		this.followList.remove(0);
	}

	
	
	/**
	 * Followerリスト更新メソッド
	 * @param data
	 */
	public void setFollower(String[] data){
		this.followerList.add(data);
		this.followerList.remove(0);
	}

	
	
	/**
	 * ハッシュツイートリスト更新メソッド
	 * @param data
	 */
	public void setHashTweet(String[] data){
		this.hashList.add(data);
		this.hashList.remove(0);
	}

	
	

	/**
	 * this.tflagのセッター
	 * ツイート表示開始
	 * @param f
	 */
	public void setFlag(int f,int num){
		this.tflag = f;
		this.shownum = num;
		new Timer().schedule(new TweetTimer(this),15000);
	}

	
	
	/**
	 * tflagのセッター
	 * 天気表示開始
	 */
	public void setWFlag(){
		this.wflag = 1;
		new Timer().schedule(new WeatherTimer(this),15000);
	}

	
	
	/**
	 * rflagのセッター
	 * 降水強度表
	 */
	public void setRFlag(){
		this.rflag = 1;
		new Timer().schedule(new RainTimer(this),15000);
	}

	
	

	
	
	/**
	 * タイマー終了後にtflagを初期化する￥
	 */
	public void setTweetInit(){
		this.tflag = 0;
	}

	
	
	/**
	 * タイマー終了後にwflagを初期化する
	 */
	public void setWeatherInit(){
		this.wflag = 0;
	}

	
	
	/**
	 * タイマー終了後にrflagを初期化する
	 */
	public void setRainInit(){
		this.rflag = 0;
	}

	
	
	/**
	 * WeatherModuleとRainModuleのタイマーを開始する
	 */
	public void runStart(){
		this.weather.runstart(3500000);;
		this.rain.runStart(3500000);;
	}
	
	
	
	/**
	 * WeatherModuleとRainModuleのタイマーを停止する
	 */
	public void runStop(){
		this.weather.runStop();
		this.rain.runStop();
	}




	/**
	 * 
	 * @author sumidatomoyuki
	 *
	 */
	class TweetTimer extends TimerTask {
		private UserTwitter user;
		public TweetTimer(UserTwitter u){
			this.user = u;
		}
		public void run() {
			this.user.setTweetInit();
		}
	}

	
	
	
	/**
	 * 
	 * @author sumidatomoyuki
	 *
	 */
	class WeatherTimer extends TimerTask {
		private UserTwitter user;
		public WeatherTimer(UserTwitter u){
			this.user = u;
		}
		public void run() {
			this.user.setWeatherInit();
		}
	}

	
	
	
	/**
	 * 
	 * @author sumidatomoyuki
	 *
	 */
	class RainTimer extends TimerTask {
		private UserTwitter user;
		public RainTimer(UserTwitter u){
			this.user = u;
		}
		public void run() {
			this.user.setRainInit();
		}
	}
}
