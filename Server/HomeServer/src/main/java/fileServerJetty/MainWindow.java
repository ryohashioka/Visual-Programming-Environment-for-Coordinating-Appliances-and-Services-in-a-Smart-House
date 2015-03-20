package fileServerJetty;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;

/*
 * サーバのウィンドウ描画
 * サーバの開始、停止ボタンなど
 */

public class MainWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String TITLE = "WebSocketServer";
	private static final int WIDTH = 580;
	private static final int HEIGHT = 500;

	private static final String MESSAGE_START_SERVER = "Start Server";
	private static final String MESSAGE_STOP_SERVER = "Stop Server";

	private static final int SERVER_PORT = 8081;
	private static final String SERVER_DOCROOT = "./html";

	private static JTextArea logTextArea;
	private JToggleButton serverBootToggleButton;

	private WebSocketServer server;

	private Runtime runtime;

	// データベース関係
	java.sql.Connection sqlcon;
	Statement smt;
	String dataBaseName = "ksuihome.db";//"demo.db"; // "sample.db";

	public MainWindow() {
		try {
			Class.forName("org.sqlite.JDBC");
			sqlcon = DriverManager.getConnection("jdbc:sqlite:" + dataBaseName);
			smt = sqlcon.createStatement();

			runtime = new Runtime(this, smt);
			server = new WebSocketServer(SERVER_PORT, SERVER_DOCROOT, this, smt, sqlcon);

			Container container = getContentPane();

			// 以下サーバ開始、終了、コンソールっぽいもの用のGUI設置
			JPanel parentPanel = new JPanel();

			// テキストログ用のエリア
			logTextArea = new JTextArea("WebSocket Server App\n");
			JScrollPane scrollPane = new JScrollPane(logTextArea,
					JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			scrollPane.setPreferredSize(new Dimension(512, 250));

			// 開始ボタン
			serverBootToggleButton = new JToggleButton(MESSAGE_START_SERVER);
			serverBootToggleButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					if (serverBootToggleButton.isSelected()) {
						new Thread() {
							public void run() {
								try {
									logTextArea.append("Server Started.\n");
									logTextArea.setCaretPosition(logTextArea
											.getText().length());

									// プログラムのランタイムの生成
									// runtime = new Runtime();
									new Thread(runtime).start(); // ランタイムの実行もするよ
									// サーバスタート
									server.start();
									server.join();
								} catch (Exception e) {
									logTextArea.append("Something Wrong.\n");
									logTextArea.setCaretPosition(logTextArea
											.getText().length());

									e.printStackTrace();
								}
							};
						}.start();
						serverBootToggleButton.setText(MESSAGE_STOP_SERVER);
					} else {
						try {
							// サーバ停止
							server.stop();
							runtime.threadStop(); // ランタイムも停止させるよ

							logTextArea.append("Server Stoped.\n");
							logTextArea.setCaretPosition(logTextArea.getText()
									.length());
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						serverBootToggleButton.setText(MESSAGE_START_SERVER);
					}
				}
			});
			// メッセージのテスト送信用のボタン
			JButton messageButton = new JButton("SendMessage");
			messageButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					try {
						sendMessageToClient("Message : From Server To Client.");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			// GUI 描画
			parentPanel.add(scrollPane, BorderLayout.CENTER);
			parentPanel.add(serverBootToggleButton, BorderLayout.SOUTH);
			parentPanel.add(messageButton, BorderLayout.SOUTH);

			container.add(parentPanel);

			setTitle(TITLE);
			setSize(WIDTH, HEIGHT);
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setVisible(true);
		} catch (ClassNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	public static void loggingData(String msg) {
		logTextArea.append(msg + "\n");
		logTextArea.setCaretPosition(logTextArea.getText().length());
	}

	public void sendMessageToClient(String msg) {
		for (MyWebSocket ws : MyWebSocket.wsConnections) {
			try {
				// System.out.println("Get Message:" + msg);
				ws.connection.sendMessage(msg);
				MainWindow.loggingData("SendMessage! : " + ws.myID);
				MainWindow.loggingData(" - Message:" + msg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void changedPrograms() {
		runtime.updatePrograms();
	}

}