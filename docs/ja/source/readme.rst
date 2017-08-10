========
はじめに
========

チュートリアルの構成
====================

このチュートリアルは、 :asakusafw:`Asakusa Framework <index.html>` の基本的な使い方や開発の流れをサンプルアプリケーションを作成しながら解説していきます。
このチュートリアルは開発の流れに沿って構成されています。はじめてアプリケーションを開発する場合は、チュートリアルの先頭から順番に進めていくとよいでしょう。

このチュートリアル内で作成するサンプルアプリケーションを開発する限りにおいて、
このチュートリアルは独立して完結しており、他のドキュメントを参照する必要がないように構成されています。
ただし、Asakusa Frameworkの全体像や基本的な概念については本チュートリアルでは触れていませんので、次のドキュメントを未読の方は目を通しておくことを推奨します。

* :asakusafw:`[Asakusa Framework documentation] - Asakusa Framework概観 <introduction/overview.html>`

各チュートリアルページの最後には、そのチュートリアルの説明を補完する「関連ドキュメント」を挙げています。
各機能の網羅的な説明や詳細を把握したい場合は、関連ドキュメントのページを確認してください。

対象読者
========

このチュートリアルでは、次のような開発者を主に想定しています。

* Javaによるプログラミング経験がある、もしくは基本的な文法について理解がある
* Eclipse や IntelliJ IDEA などJava開発向けIDEの利用経験がある
* UnixシェルやWindowsコマンドなど、利用するOSのコマンドラインインターフェースについて基本的な知識を持っている

このチュートリアルの後半では `Apache Spark`_ 環境上でバッチアプリケーションを実行する手順を説明しています。
Spark や関連する `Apache Hadoop`_ については本チュートリアルでは解説しませんので、
この部分についてはSparkやHadoopの基本的な知識があることを前提としています。

..  _`Apache Spark`: http://spark.apache.org/
..  _`Apache Hadoop`: http://hadoop.apache.org/

サンプルアプリケーション
========================

このチュートリアルで扱うサンプルアプリケーションはGitHubで公開しているサンプルアプリケーションプロジェクト
`example-basic-spark <https://github.com/asakusafw/asakusafw-examples/tree/0.10.0/example-basic-spark>`_ をベースにしています。
完成したソースコードやプロジェクト構成を確認したい場合はこちらも参考にしてください。

ドキュメントについて
====================

リポジトリ
----------

このチュートリアルのソースコードは以下のリポジトリで公開しています。

* https://github.com/asakusafw/asakusafw-basic-tutorial

改定履歴
--------

このチュートリアルの改定履歴は上記リポジトリの `CHANGELOG.md <https://github.com/asakusafw/asakusafw-basic-tutorial/blob/master/CHANGELOG.md>`_ を参照してください。

ライセンス
----------

Asakusa Framework チュートリアルは `Apache License, Version 2.0`_ の元で公開しています。

..  _`Apache License, Version 2.0`: http://www.apache.org/licenses/
