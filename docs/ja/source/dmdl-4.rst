==================
データモデルクラス
==================

このチュートリアルでは **DMDL** を利用してAsakusa Frameworkで利用する **データモデル** を定義する方法を説明していきます。

:doc:`dmdl-3` の続きで、ここではDMDLから自動生成される **データモデルクラス** および **ジョブフロー入出力基底クラス** について説明します。

.. _dmdl-data-model-class:

データモデルクラスの生成
========================

:doc:`dmdl-1` - :ref:`dmdl-create-data-model-class` の手順などによって、DMDLスクリプトからデータモデルクラスが自動生成されます。

データモデルクラスはバッチアプリケーションを構成する演算子やデータフローの実装などの様々な箇所で利用します。

生成されるデータモデルクラス用のJavaソースファイルは、プロジェクトのソースフォルダ ``build/generated-sources/modelgen`` に配置されます。
このフォルダ配下のソースファイルは自動生成の都度更新されるため、直接編集しないよう注意してください。

:doc:`dmdl-2` が終了した状態でデータモデルクラスを生成した場合、
このソースフォルダ配下にはJavaパッケージ ``com.example.modelgen.dmdl.model`` を持つ次のデータモデルクラスが存在します。

..  list-table:: データモデルクラス一覧
    :widths: 3 7
    :header-rows: 1

    * - クラス名
      - 説明
    * - ``SalesDetail``
      - :ref:`example-app-data-schema-sales_detail` に対応するデータモデルクラス
    * - ``StoreInfo``
      - :ref:`example-app-data-schema-store_info` に対応するデータモデルクラス
    * - ``ItemInfo``
      - :ref:`example-app-data-schema-item_info` に対応するデータモデルクラス
    * - ``CategorySummary``
      - :ref:`example-app-data-schema-category_summary` に対応するデータモデルクラス
    * - ``ErrorRecord``
      - :ref:`example-app-data-schema-error_record` に対応するデータモデルクラス
    * - ``JoinedSalesInfo``
      - :ref:`example-app-data-schema-joined_sales_info` に対応するデータモデルクラス

各データモデルクラスは、DMDLスクリプトで定義したデータモデル名を、CamelCaseの形式に変換したクラス名で生成されます (例: ``sales_detail`` -> ``SalesDetail`` )。

データモデルクラスのアクセサメソッド
-------------------------------------

各データモデルクラスには、データモデルに定義したプロパティを操作するためのアクセサメソッドが作成されます。

* ``get <プロパティ名>``
* ``set <プロパティ名>``
* ``get <プロパティ名> Option``
* ``set <プロパティ名> Option``
* ``get <プロパティ名> AsString`` ( ``TEXT`` 型のプロパティのみ )
* ``set <プロパティ名> AsString`` ( ``TEXT`` 型のプロパティのみ )

各メソッドは、DMDLスクリプトで定義したプロパティ名を、CamelCaseの形式に変換したメソッド名で生成されます。
例えば、 ``sales_detail`` のプロパティ ``store_code`` に対しては、以下のメソッドが作成されます。

* ``public Text getStoreCode()``
* ``public void setStoreCode(Text)``
* ``public StringOption getStoreCodeOption()``
* ``public void setStoreCodeOption(StringOption)``
* ``public String getStoreCodeAsString()``
* ``public void setStoreCodeAsString(String)``

null値の扱いとOptionメソッド
----------------------------

メソッド名の末尾が ``...Option`` となっているメソッドは、 ``Option`` クラス型のオブジェクトに対して操作を行うためのメソッドです。これは主に ``null`` 値を扱うために利用します。

例えば、値が ``null`` のプロパティに対して ``get <プロパティ名>`` で値を取得しようとすると ``NullPointerException`` が発生します。
一方、 ``get <プロパティ名> Option`` を使うとデータ型に対応した ``Option`` クラス型のオブジェクトが返却されます。このオブジェクトに対して ``null`` に対するチェックなどの操作を行うことができます。

例えば ``sales_detail`` ( データモデルクラス ``SalesDetail`` ) のプロパティ ``store_code`` に対して、``StringOption`` を使った操作は以下のようになります。

..  code-block:: java

    private void example(SalesDetail salesDetail) {

        StringOption stringOption = salesDetail.getStoreCodeOption();
        // isNull メソッドを使って nullチェック
        boolean result = stringOption.isNull();

        // or メソッドを使って nullの場合に空文字を返す
        String storeCode = salesDetail.getStoreCodeOption().or("");
    }

DMDLスクリプトに定義したプロパティの型と、データモデルクラスで扱うJavaのデータ型は、以下のように対応します。

..  list-table:: DMDLとJavaのデータ型
    :widths: 3 5
    :header-rows: 1

    * - 型の名前
      - 対応する型 (Option)
    * - ``INT``
      - ``int (IntOption)``
    * - ``LONG``
      - ``long (LongOption)``
    * - ``FLOAT``
      - ``float (FloatOption)``
    * - ``DOUBLE``
      - ``double (DoubleOption)``
    * - ``TEXT``
      - ``Text (StringOption)``
    * - ``DECIMAL``
      - ``BigDecimal (DecimalOption)``
    * - ``DATE``
      - ``Date (DateOption)`` [#]_
    * - ``DATETIME``
      - ``DateTime (DateTimeOption)`` [#]_
    * - ``BOOLEAN``
      - ``boolean (BooleanOption)``
    * - ``BYTE``
      - ``byte (ByteOption)``
    * - ``SHORT``
      - ``short (ShortOption)``

..  [#] :javadoc:`com.asakusafw.runtime.value.Date`
..  [#] :javadoc:`com.asakusafw.runtime.value.DateTime`

文字列の扱いとAsStringメソッド
------------------------------

``TEXT`` 型のプロパティに対して、 通常の ``get <プロパティ名>`` や ``set <プロパティ名>`` で扱うJavaのデータ型はHadoopが提供する ``org.apache.hadoop.io.Text`` クラス型です。
Javaの ``String`` 型として扱う場合には、 ``get <プロパティ名> AsString`` や ``set <プロパティ名> AsString`` を使います。

..  code-block:: java

    private void example(SalesDetail salesDetail) {

        // 通常の getter は 内部で保持する Hadoopの org.apache.hadoop.io.Text をそのまま返す
        Text storeCodeAsText = salesDetail.getStoreCode();

        // getXXAsString は String型に変換して返す
        String storeCodeAsString = salesDetail.getStoreCodeAsString();

        // StringOptionでは get は Text型、 getAsString は String型を返す
        StringOption stringOption = salesDetail.getStoreCodeOption();
        if (stringOption.isNull() == false) {
            Text text = stringOption.get()
            String str = stringOption.getAsString();
        }
    }

.. _dmdl-jobflow-base-class:

ジョブフロー入出力基底クラス
============================

:doc:`dmdl-3` のようにしてデータモデルにCSVフォーマットファイルを読み書きする定義を行った場合は
:ref:`dmdl-create-data-model-class` の手順などによって、バッチアプリケーションの外部入出力情報を定義する「ジョブフロー入出力基底クラス」が合わせて生成されます。

ジョブフロー入出力基底クラスは後のチュートリアル :doc:`dsl-jobflow` で利用します。

生成されるジョブフロー入出力基底クラス用のJavaソースファイルは、データモデルクラスと同様にプロジェクトのソースフォルダ ``build/generated-sources/modelgen`` に配置されます。
このフォルダ配下のソースファイルは自動生成の都度更新されるため、直接編集しないよう注意してください。

:doc:`dmdl-3` が終了した状態でデータモデルクラスを生成した場合、
このソースフォルダ配下にはJavaパッケージ ``com.example.modelgen.dmdl.csv`` を持つ次のデータモデルクラスが存在します。

..  list-table:: ジョブフロー入出力基底クラス
    :widths: 3 7
    :header-rows: 1

    * - クラス名
      - 説明
    * - ``AbstractSalesDetailCsvInputDescription``
      - :ref:`example-app-data-schema-sales_detail` に対応するインポータ記述の基底クラス
    * - ``AbstractSalesDetailCsvOutputDescription``
      - :ref:`example-app-data-schema-sales_detail` に対応するエクスポータ記述の基底クラス
    * - ``SalesDetailCsvFormat``
      - :ref:`example-app-data-schema-sales_detail` に対応するCSVフォーマット実装クラス
    * - ``AbstractStoreInfoCsvInputDescription``
      - :ref:`example-app-data-schema-store_info` に対応するインポータ記述の基底クラス
    * - ``AbstractStoreInfoCsvOutputDescription``
      - :ref:`example-app-data-schema-store_info` に対応するエクスポータ記述の基底クラス
    * - ``StoreInfoCsvFormat``
      - :ref:`example-app-data-schema-store_info` に対応するCSVフォーマット実装クラス
    * - ``AbstractItemInfoCsvInputDescription``
      - :ref:`example-app-data-schema-item_info` に対応するインポータ記述の基底クラス
    * - ``AbstractItemInfoCsvOutputDescription``
      - :ref:`example-app-data-schema-item_info` に対応するエクスポータ記述の基底クラス
    * - ``ItemInfoCsvFormat``
      - :ref:`example-app-data-schema-item_info` に対応するCSVフォーマット実装クラス
    * - ``AbstractCategorySummaryCsvInputDescription``
      - :ref:`example-app-data-schema-category_summary` に対応するインポータ記述の基底クラス
    * - ``AbstractCategorySummaryCsvOutputDescription``
      - :ref:`example-app-data-schema-category_summary` に対応するエクスポータ記述の基底クラス
    * - ``CategorySummaryCsvFormat``
      - :ref:`example-app-data-schema-category_summary` に対応するCSVフォーマット実装クラス
    * - ``AbstractErrorRecordCsvInputDescription``
      - :ref:`example-app-data-schema-error_record` に対応するインポータ記述の基底クラス
    * - ``AbstractErrorRecordCsvOutputDescription``
      - :ref:`example-app-data-schema-error_record` に対応するエクスポータ記述の基底クラス
    * - ``ErrorRecordCsvFormat``
      - :ref:`example-app-data-schema-error_record` に対応するCSVフォーマット実装クラス

各ジョブフロー入出力基底クラスは、DMDLスクリプトで定義したデータモデル名からCamelCaseの形式に変換したクラス名にして、前後に各クラスの役割に応じた名前が付加されます。
(例: ``sales_detail`` -> ``AbstractSalesDetailCsvInputDescription`` )。

関連ドキュメント
================

* :asakusafw:`[Asakusa Framework documentation] - DMDLユーザーガイド <dmdl/user-guide.html>`
