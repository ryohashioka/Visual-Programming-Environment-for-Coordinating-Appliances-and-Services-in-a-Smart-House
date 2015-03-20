このファイル構成について簡単に説明します。

ProgrammingEnvironmentフォルダ
-Androidフォルダ
・実際にAndroid端末上で動作するプロジェクトが入っています。
・./bin/HomeProgrammingEnvironment-debug.apk をAndroid端末にインストールしてください。
・動作を確認しているAndroid端末は Nexus10 Androidのバージョン 4.4.2 と 4.4.4 です。

-NetBeansProjectフォルダ
・上記のAndroidフォルダに入っているAndroidプロジェクトに変換する前の，JavaFXのコードがNetBeans 8.0のプロジェクトとして入っています。
・Javaのバージョンは 1.7.0_55-b13 です。
・JavaFXプロジェクトからAndroidプロジェクトの変換についてはNetBeansProjectフォルダ内の別テキストに書きます。

Serverフォルダ
・サーバ側のプログラムがEclipse KEPLERのプロジェクトとして入っています。
・Twitterなどのサーバモジュールで用いているサービスの認証コード等は公開できませんので、ご了承下さい。
・モジュール類の生成はコメントアウトしているので、公開しているコードそのままでは機器を制御することはできません。
・機器やサービス、プログラムを管理しているデータベース(ksuihome.db)もeclipseプロジェクトファイルの中にあります。

・通信はWebSocketを使っています。
・必要なライブラリはMavenを使ってインストールしています。Mavenをeclipseにインストールして、pom.xmlからインストールしてください。