==================
演算子のコンパイル
==================

このチュートリアルでは、バッチアプリケーションを構成する処理の単位となる **演算子** を作成する方法を説明していきます。

:doc:`dsl-operator-1` の続きで、ここでは演算子のコンパイルと演算子実装クラス、演算子ファクトリについて説明します。

演算子のコンパイル
==================

演算子クラスは通常のJavaクラスと同様に、Javaのコンパイラ ( :program:`javac` や IDEが提供するJavaコンパイラ ) によってコンパイルすることができます。

Eclipseを利用している場合、標準の設定では自動ビルドが有効になっているので、演算子クラスのソースファイルを保存するなどのタイミングで自動的に演算子クラスのコンパイルが実行されます。

コマンドライン上から演算子クラスをコンパイル場合は、Gradleの :program:`compileJava` タスクを実行します。

..  code-block:: sh

    ./gradlew compileJava

演算子クラスのコンパイルを実行すると通常のクラスファイルが生成される他に、Javaの注釈プロセッサを利用した **Operator DSLコンパイラ** が実行され、以下のクラスが生成されます。

* 演算子実装クラス
* 演算子ファクトリクラス

これらのクラス用のJavaソースファイルは、プロジェクトのソースフォルダ ``build/generated-sources/annotations`` に配置されます。
このフォルダ配下のソースファイルは演算子クラスがコンパイルされる都度更新されるため、直接編集しないよう注意してください。

.. _dsl-operator-impl-class:

演算子実装クラス
================

演算子実装クラスは、 演算子クラスを継承した実装クラスです。

演算子クラスは抽象クラス ( ``abstract class`` ) として宣言したクラスであるためインスタンスを生成できませんが、
演算子メソッドに対する単体テスト時には、対象クラスのインスタンスを生成したい場合があります。

このため演算子メソッドに対する単体テストを行う場合には、演算子実装クラスをインスタンス化してテストを実装します。
詳しくは、この後のチュートリアル :doc:`testing-operator` で説明します。

演算子実装クラスはもとの演算子クラスと同じJavaパッケージ配下に、クラス名の末尾に ``Impl`` をつけた名前で生成されます。
例えば、演算子クラス ``CategorySummaryOperator`` に対応する演算子実装クラスは ``CategorySummaryOperatorImpl`` となります。

.. _dsl-operator-factory-class:

演算子ファクトリ
================

演算子ファクトリは演算子クラスに宣言した演算子を、データフローを記述するFlow DSLから利用するためのクラスです。

演算子ファクトリの利用方法は、この後のチュートリアル :doc:`dsl-flowpart-1` でFlow DSLの記述方法と合わせて説明します。
ここでは演算子ファクトリの構成要素を説明します。

演算子ファクトリはもとの演算子クラスと同じJavaパッケージ配下に、クラス名の末尾に ``Factory`` をつけた名前で生成されます。
例えば演算子クラス ``CategorySummaryOperator`` に対応する演算子ファクトリは ``CategorySummaryOperatorFactory`` となります。

演算子ファクトリには以下の定義が含まれます。

* 演算子オブジェクトクラス
* 演算子ファクトリメソッド

.. _dsl-operator-object-class:

演算子オブジェクトクラス
------------------------

Flow DSLではデータフロー上で演算子を利用するために **演算子オブジェクト** を利用します。
演算子ファクトリは、この演算子オブジェクトの元となる **演算子オブジェクトクラス** の定義を持ちます。

演算子オブジェクトクラスは演算子ファクトリの内部クラスとして宣言され、演算子クラスに定義した演算子メソッド名をCamelCaseの形式に変換したクラス名で生成されます。

演算子オブジェクトクラスは演算子の種類や演算子の定義内容に対応した、演算子の出力を表す **ポート** をフィールドとして保持しています。
Flow DSLではこのポートを使って演算子間、または演算子と外部入出力との接続関係を定義していきます。

例えば演算子クラス ``CategorySummaryOperator`` からは、 ``CategorySummaryOperatorFactory`` の内部クラスとして以下の演算子オブジェクトクラスが生成されます。

..  list-table:: 演算子オブジェクトクラス ``CategorySummaryOperatorFactory``
    :widths: 2 2 2
    :header-rows: 1

    * - クラス名
      - ポート
      - 演算子の種類
    * - ``CheckStore``
      - ``found`` , ``missed``
      - マスタ確認演算子
    * - ``JoinItemInfo``
      - ``joined`` , ``missed``
      - マスタ結合演算子
    * - ``SummarizeByCategory``
      - ``out``
      - 単純集計演算子
    * - ``SetErrorMessage``
      - ``out``
      - 更新演算子

.. _dsl-operator-factory-method:

演算子ファクトリメソッド
------------------------

上記の `演算子オブジェクトクラス`_ を生成するファクトリメソッドです。

Flow DSLでは演算子オブジェクトを利用するために、演算子ファクトリクラスが持つ演算子ファクトリメソッドを呼び出して演算子オブジェクトを取得します。

演算子ファクトリメソッドは演算子ファクトリのメンバーとして宣言され、演算子メソッドと同じメソッド名を持ちます。

演算子ファクトリメソッドは、演算子への入力を表す **ポート** を引数にとります。
演算子間のデータフローを構築する際には、演算子オブジェクトがフィールドとして保持するポートを演算子ファクトリメソッドの引数として指定します。

戻り値型は、演算子メソッドに対応する演算子オブジェクトクラス型です。

例えば演算子クラス ``CategorySummaryOperator`` からは、 ``CategorySummaryOperatorFactory`` に対して以下の演算子ファクトリメソッドが生成されます。

..  list-table:: 演算子ファクトリメソッド ``CategorySummaryOperatorFactory``
    :widths: 2 3 2 3
    :header-rows: 1

    * - メソッド名
      - 引数 [#]_
      - 戻り値型
      - 演算子の種類
    * - ``checkStore``
      - ``Source<StoreInfo>`` , ``Source<SalesDetail>``
      - ``CheckStore``
      - マスタ確認演算子
    * - ``joinItemInfo``
      - ``Source<ItemInfo>`` , ``Source<SalesDetail>``
      - ``JoinItemInfo``
      - マスタ結合演算子
    * - ``summarizeByCategory``
      - ``Source<JoinedSalesInfo>``
      - ``SummarizeByCategory``
      - 単純集計演算子
    * - ``setErrorMessage``
      - ``Source<ErrorRecord>`` , ``String``
      - ``SetErrorMessage``
      - 更新演算子

..  [#] 演算子ファクトリメソッドの引数で使われる型 ``com.asakusafw.vocabulary.flow.Source`` はデータフローにおけるデータソースを表現する型です。詳しくは :doc:`dsl-flowpart-1` で説明します。

関連ドキュメント
================

* :asakusafw:`[Asakusa Framework documentation] - Asakusa DSLスタートガイド <dsl/start-guide.html>`
* :asakusafw:`[Asakusa Framework documentation] - Asakusa DSLユーザーガイド <dsl/user-guide.html>`
* :asakusafw:`[Asakusa Framework documentation] - 演算子リファレンス <dsl/operators.html>`
