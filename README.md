nablarch-example-http-messaging
======================================

| master | develop |
|:-----------|:------------|
|[![Build Status](https://travis-ci.org/nablarch/nablarch-example-http-messaging.svg?branch=master)](https://travis-ci.org/nablarch/nablarch-example-http-messaging)|[![Build Status](https://travis-ci.org/nablarch/nablarch-example-http-messaging.svg?branch=develop)](https://travis-ci.org/nablarch/nablarch-example-http-messaging)|

Nablarchを使ったHTTPメッセージングウェブサービスのExampleです。

## 実行手順

### 1.動作環境
実行環境に以下のソフトウェアがインストールされている事を前提とします。
* Java Version : 8
* Maven 3.0.5以降

以下は、本手順では事前準備不要です。

|ソフトウェア|説明|
|:---|:---|
|APサーバ|このアプリケーションはTomcat8を使用しています。waitt-maven-pluginはTomcat8へのアプリケーションのデプロイ、起動を行います。（起動前にエンティティクラスの作成とアプリケーションのコンパイルを別途行う必要があります。）|
|DBサーバ|このアプリケーションはH2 Database Engine(以下H2)を組み込んであるため、別途インストールの必要はありません。|

### 2. プロジェクトリポジトリの取得
Gitを使用している場合、アプリケーションを配置したいディレクトリにて「git clone」コマンドを実行してください。
以下、コマンドの例です。

    $mkdir c:\example
    $cd c:\example
    $git clone https://github.com/nablarch/nablarch-example-http-messaging.git

Gitを使用しない場合、最新のタグからzipをダウンロードし、任意のディレクトリへ展開してください。

### 3. アプリケーションのビルド
#### 3.1. アプリケーションのビルド
アプリケーションのビルドを行います。
ビルドの際に、gspプラグインによりデータベースのセットアップが行われます。

    $mvn compile

### 4. アプリケーションの起動
最後にwaitt-maven-pluginを実行し、組み込みTomcatを起動させます。以下のコマンドを実行してください。

    $mvn waitt:run-headless

### 5. テスト用クライアントクラスからのアクセス

以下のテスト用クライアントクラスのmainメソッドを実行します。

* com.nablarch.example.client.ProjectClient

このクラスは、プロジェクト登録リクエストを二回発行します。
標準出力には、各リクエストに対するレスポンスコードが出力されます。

* リクエスト1:バリデーションエラーが発生する登録リクエスト(レスポンスコード：400)
* リクエスト2:正常に登録できる登録リクエスト(レスポンスコード：201)

アプリケーションが正常に稼働している場合、標準出力に以下の内容が出力されます。

   \####400####  
   \####201####

以降はコマンドプロンプトからクライアントクラスを実行する手順となります。

#### 5.1. クライアントクラスが依存するjarの出力

以下のコマンドを実行し、クライアントクラスが依存するjarをtarget/dependency配下に出力してください。

    $cd nablarch-example-http-messaging
    $mvn dependency:copy-dependencies -DoutputDirectory=target/dependency
    
#### 5.2. クライアントクラスの実行

以下のコマンドを実行し、クライアントクラスのmainメソッドを実行してください。

    $java -cp .\target\test-classes\;.\target\classes\;.\target\dependency\*; com.nablarch.example.client.ProjectClient

#### 5.3. 結果検証

標準出力に以下の内容が出力されていることを確認してください。

   \####400####  
   \####201####

DBに登録された値は、H2のコンソールから確認してください。

### 6. DBの確認方法

1. http://www.h2database.com/html/cheatSheet.html からH2をインストールしてください。

2. {インストールフォルダ}/bin/h2.bat を実行してください(コマンドプロンプトが開く)。  
  ※h2.bat実行中はExampleアプリケーションからDBへアクセスすることができないため、Exampleアプリケーションを停止しておいてください。

3. ブラウザから http://localhost:8082 を開き、以下の情報でH2コンソールにログインしてください。
   JDBC URLの{dbファイルのパス}には、`http_messaging_example.mv.db`ファイルの格納ディレクトリまでのパスを指定してください。
  JDBC URL：jdbc:h2:{dbファイルのパス}/http_messaging_example  
  ユーザ名：HTTP_MESSAGING_EXAMPLE  
  パスワード：HTTP_MESSAGING_EXAMPLE
