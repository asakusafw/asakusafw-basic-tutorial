==================
ジョブフローの作成
==================

このチュートリアルでは、バッチアプリケーションのデータフローを定義する **ジョブフロー** を作成する方法を説明していきます。

ジョブフロー
============

ジョブフローは外部システムからデータを読み出し、データを加工して、外部システムにデータを書き戻す、という一連のデータフローを記述します。

:doc:`dsl-flowpart-1` の繰り返しになりますが、ジョブフローでは以下の3つの定義を行います。

外部入力の定義
  バッチアプリケーションの入力データをどのようにして外部システムから取り込むかを定義します。
  データソースの種類（ファイルやデータベース）、データモデルの種類や想定されるデータサイズ、データの配置場所やフィルタルールなどの指定を行います。

外部出力の定義
  バッチアプリケーションの出力データをどのようにして外部システムに書き戻すかを定義します。
  データソースの種類（ファイルやデータベース）、データモデルの種類、データの配置場所や配置形式（ファイル名やファイル分割のルール）などの指定を行います。

データフローの定義
  演算子を組み合わせて一連のデータ処理の流れを定義します。
  ジョブフローではこれに加えて、外部入力と演算子の流れ、および演算子と外部出力の流れも記述します。

これらの実装のために、ジョブフローでは以下のコンポーネントを作成します。

* 外部入力の定義 - `インポータ記述を作成する`_
* 外部出力の定義 - `エクスポータ記述を作成する`_
* データフローの定義 - `ジョブフロークラスを作成する`_

なお、外部入出力については :doc:`example-app` - :ref:`example-app-fileio-deployment` の仕様に従って定義していきます。

インポータ記述を作成する
========================

**インポータ記述** は外部システムから入力データをどのように取り込むかをデータモデルごとに指定します。
インポータ記述はJavaのインターフェース ``ImporterDescription`` [#]_ を実装したクラスとして作成します。

インポータ記述は :doc:`dmdl-4` で生成した :ref:`dmdl-jobflow-base-class` に含まれる、インポータ記述の基底クラスを継承して作成すると便利です。
このクラスは ``ImporterDescription`` を実装した抽象クラスで、各データモデルを扱うための標準的な実装を提供します。

ここではジョブフローの入力となる売上明細、店舗マスタ、商品マスタに対してそれぞれインポータ記述を定義します。

..  [#] :javadoc:`com.asakusafw.vocabulary.external.ImporterDescription`

売上明細のインポータ記述
------------------------

ではまず、売上明細のインポータ記述を作成します。

インポータ記述はプロジェクトのソースフォールダ ``src/main/java`` 配下に任意のJavaパッケージ名とクラス名を持つクラスとして作成できます。
ここでは、以下のようにインポータ記述を作成します。

..  list-table::
    :widths: 2 5

    * - パッケージ名
      - ``com.example.jobflow``
    * - クラス名
      - ``SalesDetailFromCsv``
    * - 基底クラス
      - ``com.example.modelgen.dmdl.csv.AbstractSalesDetailCsvInputDescription``

基底クラス ``AbstractSalesDetailCsvInputDescription`` は :doc:`dmdl-4` によって生成されたインポータ記述用の基底クラスです。
この基底クラスにはいくつかの抽象メソッドが含まれるので、このクラスで実装していきます。
抽象メソッドを仮にメソッド定義のみを行った状態は、以下のようになります。

..  code-block:: java
    :caption: SalesDetailFromCsv.java
    :name: SalesDetailFromCsv.java-1

    package com.example.jobflow;

    import com.example.modelgen.dmdl.csv.AbstractSalesDetailCsvInputDescription;

    public class SalesDetailFromCsv extends AbstractSalesDetailCsvInputDescription {

        @Override
        public String getBasePath() {

        }

        @Override
        public String getResourcePattern() {

        }

    }

入力ファイルのベースパス
~~~~~~~~~~~~~~~~~~~~~~~~

インポータ記述のメソッド ``getBasePath`` には、このインポータ記述が取り扱う入力ファイルの基底となる論理的なパスを戻り値として指定します。

..  code-block:: java
    :caption: SalesDetailFromCsv.java
    :name: SalesDetailFromCsv.java-2

    @Override
    public String getBasePath() {
        return "sales";
    }

このパスはあくまで論理的なもので、バッチアプリケーション実行時にファイルを読み込む際には、
Direct I/Oの設定に基づいてベースパスからファイルシステム上のパスに解決されます。

たとえば、 ベースパス ``sales`` はバッチアプリケーションの実行時にHadoopファイルシステム上のパス ``/user/asakusa/target/testing/directio/sales`` にマッピングされ、
このディレクトリ配下のファイルを読み込む、といったように動作します。

入力ファイル名のパターン
~~~~~~~~~~~~~~~~~~~~~~~~

インポータ記述のメソッド ``getResourcePattern`` には、このインポータ記述が取り扱う入力ファイル名のパターンを戻り値として指定します。

..  code-block:: java
    :caption: SalesDetailFromCsv.java
    :name: SalesDetailFromCsv.java-3

    @Override
    public String getResourcePattern() {
        return "**/${date}.csv";
    }

ここにはファイル名を表す文字列のほか、バッチ実行時の引数 ``${arg}`` やワイルドカード ``*`` などのパターン用の文字列も利用できます。

ここでの設定 ``**/{date}.csv`` は、ベースパスに対してすべてのサブディレクトリ配下の、バッチ引数 ``${date}`` で指定された日付文字列を含む、拡張子 ``.csv`` のファイルにマッチします。
例えば バッチ引数に ``date=2011-04-01`` と指定した場合、 ``2011-04-01.csv`` , ``2011/2011-04-01.csv`` などのファイルにマッチします。

入力ファイルの推定データサイズ
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

オプションの設定項目として、インポータ記述が扱うデータに対する推定データサイズを指定することで、データサイズに応じた最適化が行われます。

インポータ記述のメソッド ``getDataSize`` には、入力データの推定データサイズを列挙型 ``DataSize`` [#]_ を使って指定します。

..  code-block:: java
    :caption: SalesDetailFromCsv.java
    :name: SalesDetailFromCsv.java-4

    @Override
    public DataSize getDataSize() {
        return DataSize.LARGE;
    }

ここでは売上明細を大きめな入力データと推定するため ``DataSize.LARGE`` を指定します。

適切なデータサイズを指定することで、特に結合処理におけるバッチアプリケーションのパフォーマンスが向上する可能性があるため、
この項目はなるべく設定するとよいでしょう。

..  [#] :javadoc:`com.asakusafw.vocabulary.external.ImporterDescription.DataSize`

店舗マスタのインポータ記述
--------------------------

`売上明細のインポータ記述`_ と同様の手順で、店舗マスタのインポータ記述を作成しましょう。

ここでは、以下の値を参考に、店舗マスタのインポータ記述を作成してみてください。

..  list-table::
    :widths: 2 5

    * - クラス名
      - ``StoreInfoFromCsv``
    * - 基底クラス
      - ``com.example.modelgen.dmdl.csv.AbstractStoreInfoCsvInputDescription``
    * - ベースパス
      - ``master``
    * - リソースパターン
      - ``store_info.csv``
    * - データサイズ
      - ``DataSize.TINY`` （とても小さい）

商品マスタのインポータ記述
--------------------------

同様にして、以下の値を参考に商品マスタのインポータ記述を作成してみてください。

..  list-table::
    :widths: 2 5

    * - クラス名
      - ``ItemInfoFromCsv``
    * - 基底クラス
      - ``com.example.modelgen.dmdl.csv.AbstractItemInfoCsvInputDescription``
    * - ベースパス
      - ``master``
    * - リソースパターン
      - ``store_info.csv``
    * - データサイズ
      - ``DataSize.LARGE`` （大きい）

エクスポータ記述を作成する
==========================

**エクスポータ記述** は外部システムに対して出力データをどのように書き戻すかをデータモデルごとに指定します。
エクスポータ記述はJavaのインターフェース ``ExporterDescription`` [#]_ を実装したクラスとして作成します。

エクスポータ記述は :doc:`dmdl-4` で生成した :ref:`dmdl-jobflow-base-class` に含まれる、エクスポータ記述の基底クラスを継承して作成すると便利です。
このクラスは ``ExporterDescription`` を実装した抽象クラスで、各データモデルを扱うための標準的な実装を提供します。

ここではジョブフローの出力となるカテゴリ別売上集計、エラー情報に対してそれぞれエクスポータ記述を定義します。

..  [#] :javadoc:`com.asakusafw.vocabulary.external.ExporterDescription`

カテゴリ別売上集計のエクスポータ記述
------------------------------------

カテゴリ別売上集計のエクスポータ記述を作成します。

エクスポータ記述はプロジェクトのソースフォルダ ``src/main/java`` 配下に任意のJavaパッケージ名とクラス名を持つクラスとして作成できます。
ここでは、以下のようにエクスポータ記述を作成します。

..  list-table::
    :widths: 2 5

    * - パッケージ名
      - ``com.example.jobflow``
    * - クラス名
      - ``CategorySummaryToCsv``
    * - 基底クラス
      - ``com.example.modelgen.dmdl.csv.AbstractCategorySummaryCsvOutputDescription``

基底クラス ``AbstractCategorySummaryCsvOutputDescription`` は :doc:`dmdl-4` によって生成されたエクスポータ記述用の基底クラスです。
この基底クラスにはいくつかの抽象メソッドが含まれるので、このクラスで実装していきます。
抽象メソッドを仮にメソッド定義のみを行った状態は、以下のようになります。

..  code-block:: java
    :caption: CategorySummaryToCsv.java
    :name: CategorySummaryToCsv.java-1

    package com.example.jobflow;

    import com.example.modelgen.dmdl.csv.AbstractCategorySummaryCsvOutputDescription;

    public class CategorySummaryToCsv extends AbstractCategorySummaryCsvOutputDescription {

        @Override
        public String getBasePath() {

        }

        @Override
        public String getResourcePattern() {

        }

    }

出力ファイルのベースパス
~~~~~~~~~~~~~~~~~~~~~~~~

エクスポータ記述のメソッド ``getBasePath`` には、このエクスポータ記述が取り扱う出力ファイルの基底となる論理的なパスを戻り値として指定します。

ベースパスの仕組みは `入力ファイルのベースパス`_ と同様です。

..  code-block:: java
    :caption: CategorySummaryToCsv.java
    :name: CategorySummaryToCsv.java-2

    @Override
    public String getBasePath() {
        return "result/category";
    }

出力ファイル名のパターン
~~~~~~~~~~~~~~~~~~~~~~~~

エクスポータ記述のメソッド ``getResourcePattern`` には、このエクスポータ記述が取り扱う出力ファイル名のパターンを戻り値として指定します。

..  code-block:: java
    :caption: CategorySummaryToCsv.java
    :name: CategorySummaryToCsv.java-3

    @Override
    public String getResourcePattern() {
        return "result.csv";
    }

ここにはファイル名を表す文字列のほか、バッチ実行時の引数 ``${arg}`` やワイルドカード ``*`` などのパターン用の文字列も利用できます。

..  hint::
    出力ファイル名にパターンを利用することは、パフォーマンスの観点でも重要になることがあります。
    例えば大容量のファイルを出力する際には、1つのファイルとして出力するよりもワイルドカードやその他のパターンを利用して適切にファイルを分割することで、処理時間が短縮できることが多いでしょう。

出力データのソート
~~~~~~~~~~~~~~~~~~

オプションの設定項目として、出力データのソートに関する指定を行うことができます。

エクスポータ記述のメソッド ``getOrder`` では、出力ファイルをソートするためのキーとなるプロパティ名を戻り値として指定します。

..  code-block:: java
    :caption: CategorySummaryToCsv.java
    :name: CategorySummaryToCsv.java-4

    @Override
    public List<String> getOrder() {
        return Arrays.asList("-selling_price_total");
    }

それぞれのプロパティには接頭辞 ``+`` ( ``+property_name`` ) を付与することで昇順、接頭辞 ``-`` ( ``-property_name`` ) を指定することで降順を表します。

戻り値はリスト形式で、複数のソートキーを指定することもできます。

ここでは、売上合計プロパティ ``selling_price_total`` の降順でソートしています。

出力前の削除ファイルのパターン
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

エクスポータ記述のメソッド ``getDeletePatterns`` では、出力を行う前に削除するファイル名パターンの一覧を戻り値に指定します。
`出力ファイルのベースパス`_ で指定したパスを起点に、これらのパターンが表すパスに含まれるファイルを消去した後に、実行結果の出力を行います。

パターンには ``*`` (ワイルドカード) なども利用することができます。

..  code-block:: java
    :caption: CategorySummaryToCsv.java
    :name: CategorySummaryToCsv.java-5

    @Override
    public List<String> getDeletePatterns() {
        return Arrays.asList("*");
    }

この設定はバッチを複数回実行する際に、前回の出力結果が混ざらないようにするために利用することを意図しています。
特に `出力ファイル名のパターン`_ を利用する場合、定義の内容によってはファイル名が出力の都度変わるため、
前回実行したバッチの出力をクリーニングしないと意図せずに前回実行したバッチの出力と結果が混ざってしまう場合があります。

この項目はオプションの設定項目ですが、特別な理由がない限りは設定しておくことを推奨します。

エラー情報のエクスポータ記述
----------------------------

`カテゴリ別売上集計のエクスポータ記述`_ と同様の手順で、エラー情報のエクスポータ記述を作成しましょう。

ここでは、以下の値を参考に、エラー情報のエクスポータ記述を作成してみてください。

..  list-table::
    :widths: 2 5

    * - クラス名
      - ``ErrorRecordToCsv``
    * - 基底クラス
      - ``com.example.modelgen.dmdl.csv.AbstractErrorRecordCsvOutputDescription``
    * - ベースパス
      - ``result/error``
    * - リソースパターン
      - ``${date}.csv``
    * - ソート順
      - ファイル名の昇順
    * - 削除パターン
      - ベースパス配下のすべてのファイル

ジョブフロークラスを作成する
============================

ジョブフローを構築するためのクラスを **ジョブフロークラス** と呼びます。

ジョブフロークラスはプロジェクトのソースフォルダ ``src/main/java`` 配下に任意のJavaパッケージ名とクラス名を持つクラスとして作成できます。
ここでは、以下のようにジョブフロークラスを作成します。

..  list-table::
    :widths: 2 5

    * - パッケージ名
      - ``com.example.jobflow``
    * - クラス名
      - ``CategorySummaryJob``

ジョブフロークラスは、以下のように宣言します。

* ``public`` スコープを指定したクラスとして作成する
* Flow DSL用の親クラス ``FlowDescription`` [#]_ を継承する
* ジョブフロークラスであることを示す注釈 ``JobFlow`` [#]_ を指定し、要素 ``name`` にこのジョブフローの名前を指定する

ジョブフロークラスはフロー部品クラスと似たような構成ですが、
ジョブフロークラスであることを示す注釈 ``JobFlow`` を指定し、要素 ``name`` にこのジョブフローの名前を指定する点が異なります。

要素 ``name`` は任意の名前を使用できますが、バッチアプリケーションごとにユニークな値を指定する必要があります。
この値は、テストドライバーによるジョブフローのテスト時に利用するほか、
バッチアプリケーション実行時に **フローID** ( ``flow_id`` )という名前でログなどに出力されます。

``FlowDescription`` を継承したクラスは抽象メソッド ``describe`` を実装する必要があります。
``describe`` メソッドの実装については後述の `フロー記述メソッドを作成する`_ で説明しますが、ここではとりあえず空のメソッドを定義しておきます。

作成したジョブフロークラスは、以下のようになります。

..  code-block:: java
    :caption: CategorySummaryJob.java
    :name: CategorySummaryJob.java-1

    package com.example.jobflow;

    import com.asakusafw.vocabulary.flow.FlowDescription;
    import com.asakusafw.vocabulary.flow.JobFlow;

    @JobFlow(name = "byCategory")
    public class CategorySummaryJob extends FlowDescription {

        @Override
        protected void describe() {

        }

    }

..  [#] :javadoc:`com.asakusafw.vocabulary.flow.FlowDescription`
..  [#] :javadoc:`com.asakusafw.vocabulary.flow.JobFlow`

ジョブフローコンスタクタを作成する
==================================

ジョブフロークラスのコンストラクタには、このジョブフローの入出力を表すインポータ記述とエクスポータ記述の指定を行います。

このジョブフローでは、 このチュートリアルで作成した以下のインポータ記述とエクスポータ記述を指定します。

* インポータ記述

  * `売上明細のインポータ記述`_
  * `店舗マスタのインポータ記述`_
  * `商品マスタのインポータ記述`_
* エクスポータ記述

  * `カテゴリ別売上集計のエクスポータ記述`_
  * `エラー情報のエクスポータ記述`_

これに基づいて作成したジョブフローコンストラクタは、以下のようになります。

..  code-block:: java
    :caption: CategorySummaryJob.java
    :name: CategorySummaryJob.java-2

    ...
    import com.asakusafw.vocabulary.flow.Export;
    import com.asakusafw.vocabulary.flow.Import;
    import com.asakusafw.vocabulary.flow.In;
    import com.asakusafw.vocabulary.flow.Out;
    import com.example.modelgen.dmdl.model.CategorySummary;
    import com.example.modelgen.dmdl.model.ErrorRecord;
    import com.example.modelgen.dmdl.model.ItemInfo;
    import com.example.modelgen.dmdl.model.SalesDetail;
    import com.example.modelgen.dmdl.model.StoreInfo;

    @JobFlow(name = "byCategory")
    public class CategorySummaryJob extends FlowDescription {

        final In<SalesDetail> salesDetail;
        final In<StoreInfo> storeInfo;
        final In<ItemInfo> itemInfo;
        final Out<CategorySummary> categorySummary;
        final Out<ErrorRecord> errorRecord;

        public CategorySummaryJob(
                @Import(name = "salesDetail", description = SalesDetailFromCsv.class)
                In<SalesDetail> salesDetail,
                @Import(name = "storeInfo", description = StoreInfoFromCsv.class)
                In<StoreInfo> storeInfo,
                @Import(name = "itemInfo", description = ItemInfoFromCsv.class)
                In<ItemInfo> itemInfo,
                @Export(name = "categorySummary", description = CategorySummaryToCsv.class)
                Out<CategorySummary> categorySummary,
                @Export(name = "errorRecord", description = ErrorRecordToCsv.class)
                Out<ErrorRecord> errorRecord) {
            this.salesDetail = salesDetail;
            this.storeInfo = storeInfo;
            this.itemInfo = itemInfo;
            this.categorySummary = categorySummary;
            this.errorRecord = errorRecord;
        }

まず、フロー部品と同様にジョブフロークラスにデータフローの入出力を保持するインスタンスフィールドを作成します。


..  code-block:: java
    :caption: CategorySummaryJob.java
    :name: CategorySummaryJob.java-3

    final In<SalesDetail> salesDetail;
    final In<StoreInfo> storeInfo;
    final In<ItemInfo> itemInfo;
    final Out<CategorySummary> categorySummary;
    final Out<ErrorRecord> errorRecord;

コンストラクタでは、仮引数にこのフロー部品が受け取る入力と出力を宣言します。
ここまではフロー部品と同様です。

..  code-block:: java
    :caption: CategorySummaryJob.java
    :name: CategorySummaryJob.java-4

    public CategorySummaryJob(
            In<SalesDetail> salesDetail,
            In<StoreInfo> storeInfo,
            In<ItemInfo> itemInfo,
            Out<CategorySummary> categorySummary,
            Out<ErrorRecord> errorRecord) {
    }

そして、引数の入力に対して注釈 ``Import`` [#]_ を付与し、要素 ``name`` に入力に対する任意の名前を、要素 ``description`` に インポータ記述のクラスリテラルを指定します。
ここで指定したインポート処理の結果が、この入力を通して利用できます。

..  code-block:: java
    :caption: CategorySummaryJob.java
    :name: CategorySummaryJob.java-5
    :emphasize-lines: 2,4,6

    public CategorySummaryJob(
            @Import(name = "salesDetail", description = SalesDetailFromCsv.class)
            In<SalesDetail> salesDetail,
            @Import(name = "storeInfo", description = StoreInfoFromCsv.class)
            In<StoreInfo> storeInfo,
            @Import(name = "itemInfo", description = ItemInfoFromCsv.class)
            In<ItemInfo> itemInfo,
            Out<CategorySummary> categorySummary,
            Out<ErrorRecord> errorRecord) {
    }

同様に、引数の出力に対して注釈 ``Export`` [#]_ を付与し、要素 ``name`` に出力に対する任意の名前を、要素 ``description`` に エクスポータ記述のクラスリテラルを指定します。
この出力に対するジョブフローの実行結果が、エクスポート処理によって外部システムに書き出されるようになります。

..  code-block:: java
    :caption: CategorySummaryJob.java
    :name: CategorySummaryJob.java-6
    :emphasize-lines: 8,10

    public CategorySummaryJob(
            @Import(name = "salesDetail", description = SalesDetailFromCsv.class)
            In<SalesDetail> salesDetail,
            @Import(name = "storeInfo", description = StoreInfoFromCsv.class)
            In<StoreInfo> storeInfo,
            @Import(name = "itemInfo", description = ItemInfoFromCsv.class)
            In<ItemInfo> itemInfo,
            @Export(name = "categorySummary", description = CategorySummaryToCsv.class)
            Out<CategorySummary> categorySummary,
            @Export(name = "errorRecord", description = ErrorRecordToCsv.class)
            Out<ErrorRecord> errorRecord) {
    }

コンストラクタの本体では、引数で受け取った入力と出力をインスタンスフィールドに代入します。

..  code-block:: java
    :caption: CategorySummaryJob.java
    :name: CategorySummaryJob.java-7
    :emphasize-lines: 12-16

    public CategorySummaryJob(
            @Import(name = "salesDetail", description = SalesDetailFromCsv.class)
            In<SalesDetail> salesDetail,
            @Import(name = "storeInfo", description = StoreInfoFromCsv.class)
            In<StoreInfo> storeInfo,
            @Import(name = "itemInfo", description = ItemInfoFromCsv.class)
            In<ItemInfo> itemInfo,
            @Export(name = "categorySummary", description = CategorySummaryToCsv.class)
            Out<CategorySummary> categorySummary,
            @Export(name = "errorRecord", description = ErrorRecordToCsv.class)
            Out<ErrorRecord> errorRecord) {
        this.salesDetail = salesDetail;
        this.storeInfo = storeInfo;
        this.itemInfo = itemInfo;
        this.categorySummary = categorySummary;
        this.errorRecord = errorRecord;
    }

..  [#] :javadoc:`com.asakusafw.vocabulary.flow.Import`
..  [#] :javadoc:`com.asakusafw.vocabulary.flow.Export`

フロー記述メソッドを作成する
============================

ジョブフローで扱うデータフローの処理内容は、フロー部品と同様にフロー記述メソッド ``describe`` に記述します。

..  code-block:: java
    :caption: CategorySummaryJob.java
    :name: CategorySummaryJob.java-8

    @Override
    protected void describe() {

    }

このフロー記述メソッド  ``describe`` はフロー部品と記述方法は全く同じですが、
ジョブフローのフロー記述メソッドで扱う入力はインポータ記述の定義内容に従って外部入力となること、
同様に出力はエクスポータ記述の定義内容に従った外部出力になる点がフロー部品と異なります。

このチュートリアルではデータフローの処理は :doc:`dsl-flowpart-1` でフロー部品として作成してあるため、
ここでのフロー記述メソッドはフロー部品から生成されたフロー演算子を呼び出す処理のみを記述します。

演算子ファクトリを用意する
--------------------------

ここでは :doc:`dsl-flowpart-2` で作成したフロー部品用の演算子ファクトリクラス ``CategorySummaryFlowPartFactory`` を生成します。

..  code-block:: java
    :caption: CategorySummaryJob.java
    :name: CategorySummaryJob.java-9

    ...
    import com.example.flowpart.CategorySummaryFlowPartFactory;
    ...

    @Override
    protected void describe() {
        CategorySummaryFlowPartFactory flowPartFactory = new CategorySummaryFlowPartFactory();
    }

入力と演算子を接続する
----------------------

このデータフローの入力である売上明細、店舗マスタ、商品マスタを指定してフロー部品を実行します。
フロー部品の演算子ファクトリクラスに対しては ``create`` メソッドを呼び出すことで該当のフロー部品を実行するデータフローを記述することができます。

..  code-block:: java
    :caption: CategorySummaryJob.java
    :name: CategorySummaryJob.java-10
    :emphasize-lines: 9

    ...
    import com.example.flowpart.CategorySummaryFlowPartFactory.CategorySummaryFlowPart;
    ...

    @Override
    protected void describe() {
        CategorySummaryFlowPartFactory flowPartFactory = new CategorySummaryFlowPartFactory();

        CategorySummaryFlowPart flowPart = flowPartFactory.create(salesDetail, storeInfo, itemInfo);
    }

演算子と出力を接続する
----------------------

このデータフローの出力であるカテゴリ別売上明細とエラー情報に対して、それぞれフロー部品の出力ポートを指定してデータフローを構築します。

..  code-block:: java
    :caption: CategorySummaryJob.java
    :name: CategorySummaryJob.java-11
    :emphasize-lines: 6-7

    @Override
    protected void describe() {
        CategorySummaryFlowPartFactory flowPartFactory = new CategorySummaryFlowPartFactory();

        CategorySummaryFlowPart flowPart = flowPartFactory.create(salesDetail, storeInfo, itemInfo);
        categorySummary.add(flowPart.categorySummary);
        errorRecord.add(flowPart.errorRecord);
    }

これでジョブフローからフロー部品を呼び出すデータフローが完成しました。

終わりに
========

このチュートリアル終了時点のジョブフロー関連クラスは、次のようになります。

インポータ記述
--------------

..  literalinclude:: dsl-attachment/SalesDetailFromCsv.java
    :language: java
    :linenos:
    :caption: SalesDetailFromCsv.java
    :name: SalesDetailFromCsv.java-all

..  literalinclude:: dsl-attachment/StoreInfoFromCsv.java
    :language: java
    :linenos:
    :caption: StoreInfoFromCsv.java
    :name: StoreInfoFromCsv.java-all

..  literalinclude:: dsl-attachment/ItemInfoFromCsv.java
    :language: java
    :linenos:
    :caption: ItemInfoFromCsv.java
    :name: ItemInfoFromCsv.java-all

エクスポータ記述
----------------

..  literalinclude:: dsl-attachment/CategorySummaryToCsv.java
    :language: java
    :linenos:
    :caption: CategorySummaryToCsv.java
    :name: CategorySummaryToCsv.java-all

..  literalinclude:: dsl-attachment/ErrorRecordToCsv.java
    :language: java
    :linenos:
    :caption: ErroRecordToCsv.java
    :name: ErroRecordToCsv.java-all

ジョブフロークラス
------------------

..  literalinclude:: dsl-attachment/CategorySummaryJob.java
    :language: java
    :linenos:
    :caption: CategorySummaryJob.java
    :name: CategorySummaryJob.java-all

関連ドキュメント
================

* :asakusafw:`[Asakusa Framework documentation] - Asakusa DSLスタートガイド <dsl/start-guide.html>`
* :asakusafw:`[Asakusa Framework documentation] - Asakusa DSLユーザーガイド <dsl/user-guide.html>`
* :asakusafw:`[Asakusa Framework documentation] - Direct I/O スタートガイド <directio/start-guide.html>`
* :asakusafw:`[Asakusa Framework documentation] - Direct I/O ユーザーガイド <directio/user-guide.html>`
