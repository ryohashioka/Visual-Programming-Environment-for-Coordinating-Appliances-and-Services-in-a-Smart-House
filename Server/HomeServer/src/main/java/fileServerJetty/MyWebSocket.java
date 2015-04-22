/*
 * Copyright (c) 2015 Ryo Hashioka
 */
package fileServerJetty;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.jetty.websocket.WebSocket;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/*
 * クライアント端末との文字列、ファイルの送受信
 * プログラムや危機・サービス情報のxmlファイルの
 * 保存や読み込み、データベースへの書き込みを行っている。
 */

public class MyWebSocket implements WebSocket.OnTextMessage {

	MainWindow m;

	public int myID;
	public Connection connection;

	boolean fileFlag = false;
	boolean learnFlag = false;
	boolean controlFlag = false;
	boolean timeFlag = false;

	static Set<MyWebSocket> wsConnections = new CopyOnWriteArraySet<MyWebSocket>();

	String fileNamePath;
	String databaseName = "ksuihome.db";
	final String programXMLFileName = "program_Test.xml";
	final String deviceXMLFileName = "ApplianceAndServiceList_Demo.xml";
	
	java.sql.Connection sqlcon;
	Statement smt;

	public MyWebSocket(int id, MainWindow m, Statement smt, java.sql.Connection sqlcon) {
		String s = System.getProperty("java.class.path");
		s = s.substring(0, s.length() - 15);
		fileNamePath = s;
		myID = id;
		this.m = m;
		this.smt = smt;
		this.sqlcon = sqlcon;
	}

	@Override
	// クライアントが接続してきたら呼ばれるコールバック関数
	public void onOpen(Connection conn) {
		connection = conn;
		connection.setMaxIdleTime(Integer.MAX_VALUE);

		System.out.println("Connection Added:" + myID);
		System.out.println(" - MaxIdleTime:" + connection.getMaxIdleTime());
		System.out.println(" - MaxTextMessageSize:"
				+ connection.getMaxTextMessageSize());

		MainWindow.loggingData("Connection Added:" + myID);
		MainWindow.loggingData(" - MaxIdleTime:" + connection.getMaxIdleTime());
		MainWindow.loggingData(" - MaxTextMessageSize:"
				+ connection.getMaxTextMessageSize());

		synchronized (connection) {
			wsConnections.add(this);
		}
	}

	int nowConnectID = 0;
	
	@Override
	// クライアントからメッセージがあったら呼ばれるコールバック関数
	public void onMessage(String msg) {
		for (MyWebSocket ws : wsConnections) {
			try {
				System.out.println("Get Message:" + msg);
				ws.connection.sendMessage("success");
				MainWindow.loggingData(ws.myID + " - Message:" + msg);
				// ファイルを保存する時にー
				if (msg.equals("fileSend")) {
					fileFlag = true;
					nowConnectID = ws.myID;
				} else if (msg.equals("learn")) { // 学習モード
					learnFlag = true;
				} else if (msg.equals("programFilePlease")) {
					// プログラムファイルを生成して，ファイルをクライアントに送信
					// このとき、すべてのクライアントに送信される
					createProgramFile();
					sendFile(this.programXMLFileName);
				} else if (msg.equals("deviceFilePlease")) {
					// オブジェクト一覧ファイルを生成して、ファイルをクライアントに送信
					// すべてのクライアントに送信される。
					createDeviceFile();
					sendFile(this.deviceXMLFileName);
				}

				if (fileFlag && nowConnectID==ws.myID) {
					// 受け取ったxmlをもとにdatabeseに保存するよー
					saveFile(msg);
					m.changedPrograms(); // プログラムを変更したことを伝える
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	// クライアントが閉じたら呼ばれるコールバック関数
	public void onClose(int parameter, String msg) {
		System.out.println(parameter + " disconnect. -> " + msg);
		MainWindow.loggingData("Connection Close:" + myID);
		synchronized (connection) {
			wsConnections.remove(this);
		}
	}

	// log ファイルを保存する
	public void saveFile(String text) {
		// System.out.println(getDate() + "," + getTime());
		String fileName = "programLog.xml";
		String saveText = text;
		try {
			File file = new File(fileName);

			if (text.equals("start")) {
				MainWindow.loggingData("create New File");
				if (file.exists()) {
					file.delete();
				}
				file.createNewFile();
			} else if (text.equals("end")) {
				fileFlag = false;
				writtenDataBase();
			} else if (text.equals("fileSend")) {

			} else {
				FileWriter filewriter = new FileWriter(file, true);
				filewriter.write(saveText);
				filewriter.write("\n");
				filewriter.close();
			}

		} catch (IOException e) {
			System.out.println(e);
		}
	}

	// データベースに書き込む
	// ハードコーディングすぎるから、リストとテーブル名をもらってそれからデータベースを作成するようにする
	public void writtenDataBase() {

		// 時間は取得出来てる
		// System.out.println(timeToString());

		// データベース名
		// String fileName = "sample.db";
//		String fileName = databaseName;
		// まずは xml の読み込み
		DocumentBuilderFactory factory;
		DocumentBuilder builder;
		Node root;
		Node child;
		NodeList children;
		try {
			// データベースに書き込む為の前準備
//			Class.forName("org.sqlite.JDBC");
//			java.sql.Connection sqlcon = DriverManager
//					.getConnection("jdbc:sqlite:" + fileName);
//			Statement smt = sqlcon.createStatement();
			// プログラムテーブル
			// // もしテーブルがあれば消去
			// smt.executeUpdate("DROP TABLE IF EXISTS PROGRAMS");
			// // // テーブルの作成
			// smt.executeUpdate("CREATE TABLE IF NOT EXISTS PROGRAMS (PROGRAMID TEXT, BOXID INTEGER, BOXCLASS INTEGER, "
			// +
			// "NAME TEXT, NEXTBOX TEXT, OPERATION TEXT, X INTEGER, Y INTEGER);");
			PreparedStatement prep = sqlcon
					.prepareStatement("INSERT INTO programs VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");

			// 更新履歴テーブル（プログラムの履歴と機器・サービス管理の履歴）
			// smt.executeUpdate("DROP TABLE IF EXISTS HISTORY");
			// テーブルの作成
			// which -> program or 機器・サービス
			// what -> which で選んだものの中の何か
			// how -> add したのか remove したのか
			// smt.executeUpdate("CREATE TABLE IF NOT EXISTS HISTORY (DATE TEXT, WHICH TEXT, WHAT TEXT, HOW TEXT);");
			PreparedStatement historyPrep = sqlcon
					.prepareStatement("INSERT INTO HISTORY VALUES (?, ?, ?, ?, ?);");

			// xml を読み込む為の前準備
			factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
			factory.setIgnoringElementContentWhitespace(true);
			factory.setIgnoringComments(true);
			factory.setValidating(true);
			// クライアントから貰ったデータの読み込み
			root = builder.parse("programLog.xml");
			children = root.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				child = children.item(i);
				NodeList grandChildren = child.getChildNodes();
				String time = null;
				// まずは更新時間の取得
				for (int j = 0; j < grandChildren.getLength(); j++) {
					if (grandChildren.item(j).getNodeName().equals("other")) {
						NamedNodeMap attrs = grandChildren.item(j)
								.getAttributes();
						time = attrs.getNamedItem("time").getNodeValue();
					}
				}
				// 変更内容を取得してデータベースを更新
				for (int j = 0; j < grandChildren.getLength(); j++) {
					// System.out.println(grandChildren.item(j).getNodeName());
					NamedNodeMap attrs = grandChildren.item(j).getAttributes();
					String programID = "";
					String programSwitch = "";

					// もし add するなら新しいプログラムをデータベースに書き込む
					if (grandChildren.item(j).getNodeName().equals("add")) {
						// まずは id の取得
						programID = attrs.getNamedItem("id").getNodeValue();
						// 実行するかしないかの情報を取得
						programSwitch = attrs.getNamedItem("switch")
								.getNodeValue();
						System.out.println("add databese : " + programID
								+ " is " + programSwitch);

						// より詳しい情報の取得
						Node grandChild = grandChildren.item(j);
						NodeList addList = grandChild.getChildNodes();
						// プログラム内のオブジェクト情報をデータベースに更新
						for (int k = 0; k < addList.getLength(); k++) {
							if (addList.item(k).getNodeName().equals("object")) {
								NamedNodeMap addElements = addList.item(k)
										.getAttributes();
								System.out.println(addElements.getNamedItem(
										"name").getNodeValue());
								// databaseに書き込み
								prep.setString(1, programID);
								prep.setString(2,
										addElements.getNamedItem("boxID")
												.getNodeValue());
								prep.setString(3,
										addElements.getNamedItem("boxClass")
												.getNodeValue());
								prep.setString(4,
										addElements.getNamedItem("name")
												.getNodeValue());
								prep.setString(5,
										addElements.getNamedItem("nextBox")
												.getNodeValue());
								prep.setString(6,
										addElements.getNamedItem("operation")
												.getNodeValue());
								prep.setString(7, addElements.getNamedItem("x")
										.getNodeValue());
								prep.setString(8, addElements.getNamedItem("y")
										.getNodeValue());
								prep.setString(9,
										addElements.getNamedItem("gui")
												.getNodeValue());
								prep.setString(10,
										addElements.getNamedItem("icon")
												.getNodeValue());
								prep.setString(11,
										addElements.getNamedItem("set")
												.getNodeValue());
								prep.setString(12, programSwitch);
								prep.setString(13,
										addElements.getNamedItem("option")
												.getNodeValue());
								prep.setString(14,
										addElements.getNamedItem("place")
												.getNodeValue());
								prep.setString(15,
										addElements.getNamedItem("places")
												.getNodeValue());
								prep.setString(16,"true");
								prep.addBatch();

								// 履歴を書き込む
								historyPrep.setString(1, time); // 日時
								historyPrep.setString(2, "0"); // ユーザID（未実装）
								historyPrep.setString(3, "program"); // program
																		// or
																		// device（ここではプログラムの保存なのでprogram）
								historyPrep.setString(4, programID); // programID
								historyPrep.setString(5, "add"); // add or
																	// remove（今はaddの処理）
								historyPrep.addBatch();
							}
						}
						// もし change なら データベースのアップデートをする
					} else if (grandChildren.item(j).getNodeName()
							.equals("change")) {
						// まずは id の取得
						programID = attrs.getNamedItem("id").getNodeValue();
						System.out.println("change databese : " + programID);

						// より詳しい情報の取得
						Node grandChild = grandChildren.item(j);
						NodeList addList = grandChild.getChildNodes();
						// プログラム内のオブジェクト情報をデータベースに更新
						for (int k = 0; k < addList.getLength(); k++) {
							if (addList.item(k).getNodeName().equals("object")) {
								NamedNodeMap addElements = addList.item(k)
										.getAttributes();
								System.out.println(addElements.getNamedItem(
										"name").getNodeValue());
								// databaseに更新
								try {
//									System.out.println(addElements.getNamedItem("operation").getNodeValue());
//									System.out.println(addElements.getNamedItem("x").getNodeValue());
//									System.out.println(addElements.getNamedItem("y").getNodeValue());
//									System.out.println(addElements.getNamedItem("place").getNodeValue());
//									System.out.println(programID);
//									System.out.println(addElements.getNamedItem("boxID").getNodeValue());
									String sql = "UPDATE programs SET operation = '"
											+ addElements.getNamedItem(
													"operation").getNodeValue()
											+ "', x = '"
											+ addElements.getNamedItem("x")
													.getNodeValue()
											+ "', y = '"
											+ addElements.getNamedItem("y")
													.getNodeValue()
											+ "', place = '"
											+ addElements.getNamedItem("place")
													.getNodeValue()
											+ "' WHERE programid = '"
											+ programID
											+ "' and "
											+ "boxid = '"
											+ addElements.getNamedItem("boxID")
													.getNodeValue() + "';";
									smt.executeUpdate(sql);
								} catch (SQLException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								// 履歴を書き込む
								historyPrep.setString(1, time);
								historyPrep.setString(2, "0");
								historyPrep.setString(3, "program");
								historyPrep.setString(4, programID);
								historyPrep.setString(5, "change");
								historyPrep.addBatch();
							}
						}
					} else if (grandChildren.item(j).getNodeName()
							.equals("remove")) {
						// まずは id の取得
						programID = attrs.getNamedItem("id").getNodeValue();
						System.out.println("remove databese" + programID);
						// データベースから削除
						smt.executeUpdate("DELETE FROM PROGRAMS WHERE PROGRAMID=\""
								+ programID + "\"");

						// 履歴を書き込む
						historyPrep.setString(1, time);
						historyPrep.setString(2, "0");
						historyPrep.setString(3, "program");
						historyPrep.setString(4, programID);
						historyPrep.setString(5, "remove");
						historyPrep.addBatch();
					}
				}
			}
			// 
//			smt.execute("vacuum;");
			// databaseの後処理
			sqlcon.setAutoCommit(false);
			prep.executeBatch();
			historyPrep.executeBatch();
			sqlcon.commit();

			ResultSet rs = smt.executeQuery("SELECT * FROM PROGRAMS;");
			// while (rs.next()) {
			// System.out.println("PROGRAMID = " + rs.getString("PROGRAMID"));
			// System.out.println("BOXID = " + rs.getString("BOXID"));
			// System.out.println("NAME = " + rs.getString("NAME"));
			// System.out.println("OPERATION = " + rs.getString("OPERATION"));
			// System.out.println("ORDERNUM = " + rs.getString("ORDERNUM"));
			// }
			// database の終了処理
//			sqlcon.close();
//			smt.close();
			rs.close();

		} catch (ParserConfigurationException e0) {
			System.out.println(e0.getMessage());
		} catch (SAXException e1) {
			System.out.println(e1.getMessage());
		} catch (IOException e2) {
			System.out.println(e2.getMessage());
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	// 文字列を送信
	public void sendMessage(String msg) {
		try {
			connection.sendMessage(msg);
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	// 指定したファイルを1行ずつ送信
	public void sendFile(String filePath) {
		System.out.println("send file : " + filePath);
		FileReader fr = null;
		BufferedReader br = null;

		try {
			connection.sendMessage("fileSend");
			connection.sendMessage("start");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// connection.sendMessage("fileName,program.xml");
		try {
			fr = new FileReader(filePath);
			br = new BufferedReader(fr);

			String line;
			while ((line = br.readLine()) != null) {
				connection.sendMessage(line);
			}
			// 更新時間の保存
			// new FileAdmin().saveTime();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
				fr.close();
				connection.sendMessage("end");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return;
	}

	// データベースからプログラムXMLファイルを生成
	private void createProgramFile() {
		System.out.println("create program file");
		// データベースからプログラム一覧の取得
		java.sql.Connection sqlcon;
		Statement smt;
		try {
			Class.forName("org.sqlite.JDBC");
			sqlcon = DriverManager.getConnection("jdbc:sqlite:" + databaseName);
			smt = sqlcon.createStatement();
			try { // プログラムID一覧を取得
				ResultSet rs = smt
						.executeQuery("select programid from programs;");
				ArrayList<String> programIDList = new ArrayList<String>();
				while (rs.next()) {
					boolean flag = true;
					String pID = rs.getString("programid");
					// System.out.println(rs.getString("programid"));
					for (int i = 0; i < programIDList.size(); i++) {
						if (pID.equals(programIDList.get(i))) {
							flag = false;
							break;
						}
					}
					if (flag) {
						programIDList.add(rs.getString("programid"));
					}
				}

				// xmlの準備
				DocumentBuilder documentBuilder = null;
				try {
					documentBuilder = DocumentBuilderFactory.newInstance()
							.newDocumentBuilder();
				} catch (ParserConfigurationException e) {
					e.printStackTrace();
					return;
				}
				Document document = documentBuilder.newDocument();

				// XML文書の作成
				Element root = document.createElement("system");

				// 各プログラムIDを再度データベースに検索をかけてXMLを生成
				for (int i = 0; i < programIDList.size(); i++) {
					System.out.println("sendFile : programID - "
							+ programIDList.get(i));
					Element program = document.createElement("program");
					program.setAttribute("id", programIDList.get(i));
					// id から各値の取得
					rs = smt.executeQuery("select * from programs where programid='"
							+ programIDList.get(i) + "';");
					while (rs.next()) {
						System.out.println(rs.getString("name"));
						Element object = document.createElement("object");
						object.setAttribute("boxClass",
								rs.getString("boxclass"));
						object.setAttribute("boxID", rs.getString("boxid"));
						object.setAttribute("name", rs.getString("name"));
						object.setAttribute("nextBox", rs.getString("nextbox"));
						object.setAttribute("operation",
								rs.getString("operation"));
						object.setAttribute("x", rs.getString("x"));
						object.setAttribute("y", rs.getString("y"));
						object.setAttribute("icon", rs.getString("icon"));
						object.setAttribute("gui", rs.getString("gui"));
						object.setAttribute("set", rs.getString("setup"));
						object.setAttribute("option", rs.getString("option"));
						object.setAttribute("place", rs.getString("place"));
						object.setAttribute("places", rs.getString("places"));
						program.appendChild(object);
					}
					root.appendChild(program);
				}
				document.appendChild(root);

				// XMLファイルの作成
				File file = new File(programXMLFileName);
				TransformerFactory transformerFactory = TransformerFactory
						.newInstance();
				Transformer transformer = null;
				try {
					transformer = transformerFactory.newTransformer();
					transformer.setOutputProperty("indent", "yes"); // 改行指定
					// transformer.setOutputProperty("encoding", "Shift_JIS");
					// // エンコーディングはしていない
					transformer.transform(new DOMSource(document),
							new StreamResult(file));
				} catch (TransformerConfigurationException e) {
					e.printStackTrace();
					return;
				} catch (TransformerException e) {
					e.printStackTrace();
					return;
				}
				// database の終了処理
				rs.close();
			} catch (SQLException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}

			sqlcon.close();
			smt.close();

			// ファイル生成が終了したのであとはファイル送信
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	// データベースからクライアント端末に表示するデバイスXMLファイルを生成
	private void createDeviceFile() {
		System.out.println("create device file");
		// データベースからプログラム一覧の取得
		java.sql.Connection sqlcon;
		Statement smt;
		try {
			Class.forName("org.sqlite.JDBC");
			sqlcon = DriverManager.getConnection("jdbc:sqlite:" + databaseName);
			smt = sqlcon.createStatement();
			try {

				// xmlの準備
				DocumentBuilder documentBuilder = null;
				try {
					documentBuilder = DocumentBuilderFactory.newInstance()
							.newDocumentBuilder();
				} catch (ParserConfigurationException e) {
					e.printStackTrace();
					return;
				}
				Document document = documentBuilder.newDocument();

				// XML文書の作成
				Element root = document
						.createElement("ApplianceAndServiceList");

				// 入力機器と出力機器の一覧を取得
				ResultSet inrs = smt.executeQuery("select * from input;");

				// 順番にXMLに書き込むだけ
				while (inrs.next()) { // まずは入力から
					Element input = document.createElement("input");
					input.setAttribute("category", inrs.getString("category"));
					input.setAttribute("name", inrs.getString("name"));
					input.setAttribute("icon", inrs.getString("icon"));
					input.setAttribute("gui", inrs.getString("gui"));
					input.setAttribute("set", inrs.getString("setup"));
					input.setAttribute("option", inrs.getString("option"));
					input.setAttribute("place", inrs.getString("place"));
					root.appendChild(input);
				}
				inrs.close();
				ResultSet outrs = smt.executeQuery("select * from output;");
				while (outrs.next()) { // 次に出力
					Element output = document.createElement("output");
					output.setAttribute("category", inrs.getString("category"));
					output.setAttribute("name", inrs.getString("name"));
					output.setAttribute("icon", inrs.getString("icon"));
					output.setAttribute("gui", inrs.getString("gui"));
					output.setAttribute("set", inrs.getString("setup"));
					output.setAttribute("option", inrs.getString("option"));
					output.setAttribute("place", inrs.getString("place"));
					root.appendChild(output);
				}
				outrs.close();

				document.appendChild(root);

				// XMLファイルの作成
				File file = new File(deviceXMLFileName);
				TransformerFactory transformerFactory = TransformerFactory
						.newInstance();
				Transformer transformer = null;
				try {
					transformer = transformerFactory.newTransformer();
					transformer.setOutputProperty("indent", "yes"); // 改行指定
					// transformer.setOutputProperty("encoding", "Shift_JIS");
					// // エンコーディングはしていない
					transformer.transform(new DOMSource(document),
							new StreamResult(file));
				} catch (TransformerConfigurationException e) {
					e.printStackTrace();
					return;
				} catch (TransformerException e) {
					e.printStackTrace();
					return;
				}
				// database の終了処理
			} catch (SQLException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}

			sqlcon.close();
			smt.close();

			// ファイル生成が終了したのであとはファイル送信
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}
}