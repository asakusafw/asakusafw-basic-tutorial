=====================
Spark環境へのデプロイ
=====================

このチュートリアルでは、Spark環境に対してAsakusa Frameworkのバッチアプリケーション実行環境一式をデプロイする方法を説明していきます。

前提環境
========

SparkとHadoop環境
-----------------

以降の説明では、Asakusa Frameworkの実行環境となるSpark環境、及びHadoop環境が準備済みであることを前提とします。

Sparkのセットアップについては、Sparkのドキュメントや利用するHadoopディストリビューションのドキュメント等を参考にして下さい。
またAsakusa Frameworkが動作検証を行っている各プラットフォームのバージョン詳細などは `関連ドキュメント`_ を参照してください。

OSユーザ
--------

Spark環境に対して、Asakusa Frameworkのバッチアプリケーション実行環境一式をデプロイして実行するためのOSユーザを準備します。

ユーザの具体的な作成手順はここでは省略しますので、利用するOSが提供する方法に従ってユーザを準備してください。

このユーザは以下の条件を満たしているものとします。

* Sparkが提供する各コマンドが実行可能
* Hadoopが提供する各コマンドが実行可能
* Hadoopファイルシステム上に、このユーザに対応するホームディレクトリが作成されている

環境変数の設定
==============

Asakusa Frameworkのバッチアプリケーションを実行するには、以下の環境変数が設定されている必要があります。

..  list-table:: 環境変数の設定
    :widths: 2 3
    :header-rows: 1

    * - 環境変数
      - 説明
    * - ``JAVA_HOME``
      - JDKのインストールパス
    * - ``HADOOP_CMD``
      - :program:`hadoop` コマンドのパス [#]_
    * - ``SPARK_CMD``
      - :program:`spark-submit` コマンドのパス [#]_
    * - ``ASAKUSA_HOME``
      - Asakusa Frameworkのインストールパス

ここでは、OSユーザーのログインプロファイル( :file:`~/.bash_profile` など )に対して、以下の環境変数を設定します。

..  code-block:: sh
    :caption: ~/.bash_profile
    :name: .bash_profile

    export JAVA_HOME=/usr/lib/jvm/java-8-oracle
    export HADOOP_CMD=/usr/lib/hadoop/bin/hadoop
    export SPARK_CMD=/opt/spark/bin/spark-submit
    export ASAKUSA_HOME=$HOME/asakusa

以降の手順を実施するため、カレントシェルに対してプロファイルを適用しておきます。

..  code-block:: sh

    source ~/.bash_profile

..  [#] 環境変数 ``HADOOP_CMD`` を指定しない場合、環境変数 ``PATH`` に :program:`hadoop` コマンドのパスが含まれていればこれを利用します。

..  [#] 環境変数 ``SPARK_CMD`` を指定しない場合、環境変数 ``PATH`` に :program:`spark-submit` コマンドのパスが含まれていればこれを利用します。

デプロイメントアーカイブの配置
==============================

Asakusa Framework実行環境をデプロイするマシンに :doc:`assemble` で作成した開発環境上のデプロイメントアーカイブファイルを配置します。

ここでは ``$HOME/archive`` というディレクトリにデプロイメントアーカイブファイル ``asakusa-tutorial.tar.gz`` を配置したものとします。

デプロイメントアーカイブの展開
==============================

``$ASAKUSA_HOME`` 配下にデプロイメントアーカイブの内容を展開します。
展開後、 ``$ASAKUSA_HOME`` 配下の :file:`*.sh` に実行権限を追加します。

..  code-block:: sh

    mkdir -p "$ASAKUSA_HOME"
    cd "$ASAKUSA_HOME"
    tar -xf $HOME/archive/asakusa-tutorial.tar.gz
    find "$ASAKUSA_HOME" -name "*.sh" | xargs chmod u+x

デプロイが正常に実行された場合、 ``ASAKUSA_HOME`` 配下には以下のようなディレクトリが作成されています。

..  list-table:: デプロイメント構成 - ``ASAKUSA_HOME``
    :widths: 3 7
    :header-rows: 1

    * - ディレクトリ/ファイル
      - 説明
    * - :file:`VERSION`
      - バージョン情報を含むファイル
    * - :file:`batchapps`
      - バッチアプリケーションの配置ディレクトリ
    * - :file:`core`
      - Asakusa Framework実行ライブラリの配置ディレクトリ
    * - :file:`directio`
      - Direct I/Oの実行モジュール及びツールの配置ディレクトリ
    * - :file:`example-dataset`
      - テストデータの配置ディレクトリ
    * - :file:`ext`
      - 拡張モジュール用の配置ディレクトリ
    * - :file:`spark`
      - Asakusa on Sparkの実行モジュール及びツールの配置ディレクトリ
    * - :file:`tools`
      - 運用ツールの配置ディレクトリ
    * - :file:`windgate`
      - WindGateの実行モジュール配置ディレクトリ
    * - :file:`windgate-ssh`
      - WindGateの実行モジュール配置ディレクトリ
    * - :file:`yaess`
      - YAESSの実行モジュール配置ディレクトリ
    * - :file:`yaess-hadoop`
      - YAESSの実行モジュール配置ディレクトリ

テストデータの配置
==================

:doc:`assemble` の手順では、デプロイメント構成にバッチアプリケーション疎通確認用のテストデータを :file:`example-dataset` ディレクトリに含めました。
このテストデータをDirect I/Oの入力ディレクトリに配置します。

Direct I/Oのデフォルト設定では、入力ディレクトリはHadoopファイルシステム上のホームディレクトリ配下の ``target/testing/directio`` ディレクトリに設定されています。

ここではDirect I/Oのデフォルト設定に合わせてテストデータを配置します。

..  code-block:: sh

    hadoop fs -mkdir -p target/testing/directio
    hadoop fs -put $ASAKUSA_HOME/example-dataset/master target/testing/directio/master
    hadoop fs -put $ASAKUSA_HOME/example-dataset/sales target/testing/directio/sales

テストデータが正しく配置されているかを確認します。

..  code-block:: sh

    hadoop fs -text target/testing/directio/master/item_info.csv
    hadoop fs -text target/testing/directio/master/store_info.csv
    hadoop fs -text target/testing/directio/master/store_info.csv

正しく配置されている場合、それぞれ以下のように出力されます。

..  literalinclude:: config-attachment/example-dataset/master/item_info.csv
    :language: none
    :caption: master/item_info.csv

..  literalinclude:: config-attachment/example-dataset/master/store_info.csv
    :language: none
    :caption: master/store_info.csv

..  literalinclude:: config-attachment/example-dataset/sales/2011-04-01.csv
    :language: none
    :caption: sales/2011-04-01.csv

ここまでの手順でバッチアプリケーションを実行する準備ができました。
次のチュートリアルでデプロイしたバッチアプリケーションを実行していきます。

関連ドキュメント
================

* :asakusafw:`[Asakusa Framework documentation] - 対応プラットフォーム <product/target-platform.html>`
* :asakusafw:`[Asakusa Framework documentation] - Asakusa Framework デプロイメントガイド <administration/deployment-guide.html>`
* :asakusafw:`[Asakusa Framework documentation] - Asakusa on Spark ユーザガイド <spark/user-guide.html>`

