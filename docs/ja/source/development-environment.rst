==============
開発環境の準備
==============

このチュートリアル実施するにあたって必要となる **開発環境** を準備します。

前提環境
========

このチュートリアルに沿ってアプリケーションを開発するために必要となる環境は次の通りです。

..  list-table:: 前提環境
    :widths: 2 3 5
    :header-rows: 1

    * - ソフトウェア
      - バージョン
      - 備考
    * - OS
      - Windows, MacOSX, Linux
      -
    * - `JDK <http://www.oracle.com/technetwork/java/javase/downloads/index.html>`_
      - JDK 8
      - JREは利用不可 [#]_
    * - `Eclipse <http://www.eclipse.org/downloads/>`_
      - バージョン 4.4.2 以上
      -
    * - Excel
      - Excel 2007以上を推奨 [#]_
      - (オプション) テストデータの編集に使用 [#]_

このチュートリアルでは、コマンドラインインターフェース（以降「コマンドライン」）上での手順は主にUnix系OSの例を記載しています。
Windowsを利用している場合は、チュートリアルの説明を適宜読みかえて進めてください。

また、このチュートリアルは統合開発環境(IDE)にEclipseを使った手順を説明します。
他のIDE（IntelliJ IDEAなど）を利用する場合は、チュートリアルの説明を適宜読みかえて進めてください。

Asakusa Frameworkが動作検証を行っている開発環境上のソフトウェアバージョンについて、詳しくは `関連ドキュメント`_ を参照してください。

..  [#] Asakusa Frameworkのいくつかの機能はJDKの機能を利用するため、JREのみがインストールされている環境では正しく動作しません。
..  [#] Excelが利用できない環境の場合に、 `LibreOffice Calc <https://ja.libreoffice.org/discover/calc/>`_ での動作報告もありますが、編集するExcelシートのスタイルが崩れることがあるなどの問題も報告されています。
..  [#] このチュートリアルではExcelを使ったテストデータの作成方法を紹介します。Excelが利用できない環境では、このチュートリアルに添付している作成済みのテストデータファイルを利用してテストを実行してください。

環境変数の設定
==============

Asakusa Frameworkを利用する開発環境には、以下の環境変数を設定してください。

..  list-table:: 開発環境の設定
    :widths: 3 7
    :header-rows: 1

    * - 変数名
      - 値
    * - ``ASAKUSA_HOME``
      - Asakusa Frameworkのインストールディレクトリを指定
    * - ``JAVA_HOME``
      - JDKのインストールディレクトリを指定
    * - ``PATH``
      - JDKのコマンドパスを追加

環境変数 ``ASAKUSA_HOME`` はAsakusa Framework実行環境のインストール先ディレクトリを絶対パスで指定します。

環境変数 ``JAVA_HOME`` にはJDKのインストール先ディレクトリを絶対パスで指定します。
Asakusa Frameworkのいくつかの機能はJDKの機能を利用するため、JREではなくJDKのパスを設定してください。

環境変数 ``PATH`` にはJDKのコマンドパスを指定します。
通常、Unix系OSの場合は ``${JAVA_HOME}/bin`` ､ Windows系OSの場合は ``%JAVA_HOME%¥bin`` のように指定します。

以降の手順では、コマンドライン環境やIDE環境上で上記の環境変数が有効になっている必要があるため、
環境変数を設定したら、必要に応じてデスクトップ環境に対してログインセッションの再起動などを実施してください。

.. todo:: OSごとの環境変数の設定手順の例があったほうがよいか

関連ドキュメント
================

* :asakusafw:`[Asakusa Framework documentation] - 対応プラットフォーム <product/target-platform.html>`
