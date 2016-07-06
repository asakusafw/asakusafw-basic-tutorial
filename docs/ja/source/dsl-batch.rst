============
バッチの作成
============

このチュートリアルでは、バッチアプリケーションのワークフローを記述する **バッチ** を作成する方法を説明していきます。

ワークフローとBatch DSL
=======================

バッチはバッチアプリケーションを構成する最上位の単位で、運用時におけるアプリケーション実行の単位です。

**Batch DSL** はジョブフローを組み合わせてワークフローの構造を記述するDSLです。
現時点でのBatch DSLは、ジョブフロー間の実行順序の定義や分岐や合流の定義といった、ワークフローを記述する最小限の機能のみを提供しています。

アプリケーション開発の観点ではジョブフローがバッチアプリケーションの実行単位であるように思われるかもしれませんが、
アプリケーション運用の観点では複数のジョブフローの組みあわせが1つのアプリケーションと認識することが多くあります。
また運用時にはリソースの効率的な利用やターンアラウンドタイムの向上といった目的でジョブフローを並列に実行する、といったような性能上の観点も考慮されます。
このように、アプリケーション運用の観点でバッチアプリケーションを定義するレイヤーとしてBatch DSLが位置付けられています。

Batch DSLで複雑なワークフローを組むほかに、外部のワークフローエンジンを使ってワークフローを組むことも可能です。
その場合、Batch DSLではシンプルなワークフローを定義するとよいでしょう。

このチュートリアルでは、 :doc:`dsl-jobflow` で作成したジョブフローを実行するだけの最も単純なワークフロー定義を行います。

バッチクラスを作成する
======================

バッチを構築するためのクラスを **バッチクラス** と呼びます。

バッチクラスはプロジェクトのソースフォルダ ``src/main/java`` 配下に任意のJavaパッケージ名とクラス名を持つクラスとして作成できます。
ここでは、以下のようにバッチクラスを作成します。

..  list-table::
    :widths: 2 5

    * - パッケージ名
      - ``com.example.batch``
    * - クラス名
      - ``SummarizeBatch``

バッチクラスは、以下のように宣言します。

* ``public`` スコープを指定したクラスとして作成する
* Batch DSL用の親クラス ``BatchDescription`` [#]_ を継承する
* バッチクラスであることを示す注釈 ``Batch`` [#]_ を指定し、要素 ``name`` にこのバッチの名前を指定する

``BatchDescription`` を継承したクラスは抽象メソッド ``describe`` を実装する必要があります。
``describe`` メソッドの実装については後述の `バッチ記述メソッドを作成する`_ で説明しますが、ここではとりあえず実装が空のメソッドを定義しておきます。

作成したバッチクラスは、以下のようになります。

..  code-block:: java
    :caption: SummarizeBatch.java
    :name: SummarizeBatch.java-1

    package com.example.batch;

    import com.asakusafw.vocabulary.batch.Batch;
    import com.asakusafw.vocabulary.batch.BatchDescription;

    @Batch(name = "example.summarizeSales")
    public class SummarizeBatch extends BatchDescription {

        @Override
        protected void describe() {

        }

    }

注釈 ``Batch`` の要素 ``name`` には任意の名前を使用できますが、バッチ間でユニークな値を指定する必要があります。

この値は バッチ実行ツール **YAESS** のコマンドラインインターフェースを使ってバッチを実行する際に **バッチID** として指定します。
また、バッチアプリケーション実行時に ``batch_id`` という名前でログなどに出力されます。

..  [#] :javadoc:`com.asakusafw.vocabulary.batch.BatchDescription`
..  [#] :javadoc:`com.asakusafw.vocabulary.batch.Batch`

バッチ記述メソッドを作成する
============================

バッチで扱うワークフローの処理内容は、バッチ記述メソッドに記述します。
バッチ記述メソッドはバッチクラスの親クラスとして指定した ``BatchDescription`` クラスの ``describe`` メソッドをオーバーライドして記述します。

..  code-block:: java
    :caption: SummarizeBatch.java
    :name: SummarizeBatch.java-2

    ...
    import com.example.jobflow.CategorySummaryJob;

    @Batch(name = "example.summarizeSales")
    public class SummarizeBatch extends BatchDescription {

        @Override
        protected void describe() {
            run(CategorySummaryJob.class).soon();
        }

    }

バッチの内部で実行するジョブフローは、 ``BatchDescription`` クラスから継承した ``run`` メソッドで指定します。
同メソッドには対象のジョブフロークラスのクラスリテラルを指定し、そのままメソッドチェインで ``soon`` や ``after`` メソッドを起動します。

``soon`` メソッドはバッチの内部で最初に実行されるジョブフローを表します。
ここではバッチの中で単一のジョブフローのみを実行するよう、 ``soon`` メソッドを使って定義しています。

この例には登場しませんが、 ``after`` メソッドを使って依存関係にある処理が全て完了した後に実行されるジョブフローを指定することも可能です。

終わりに
========

このチュートリアル終了時点のバッチクラス :file:`SummarizeBatch.java` は、次のようになります。

..  literalinclude:: dsl-attachment/SummarizeBatch.java
    :language: java
    :linenos:
    :caption: SummarizeBatch.java
    :name: SummarizeBatch.java-all

関連ドキュメント
================

* :asakusafw:`[Asakusa Framework documentation] - Asakusa DSLスタートガイド <dsl/start-guide.html>`
* :asakusafw:`[Asakusa Framework documentation] - Asakusa DSLユーザーガイド <dsl/user-guide.html>`
