============================
結合モデルと集計モデルの定義
============================

このチュートリアルでは **DMDL** を利用してAsakusa Frameworkで利用する **データモデル** を定義する方法を説明していきます。

:doc:`dmdl-1` の続きで、ここでは **結合モデル** と **集計モデル** を作成していきます。

結合モデルを作成する
====================

結合モデルは、ある2つのデータモデルに対して「結合」の操作を行って生成するデータモデルを表します。

結合モデルを作成しておくことで、データフロー内で結合に関する処理を実装することなく、結合結果のデータモデルを取得することができるようになります。

結合モデルの定義
----------------

このチュートリアルでは、:doc:`example-app` で紹介した :ref:`example-app-data-schema-sales_detail` と :ref:`example-app-data-schema-item_info` を結合した結合モデルを作成していきます。

DMDLスクリプト ``models.dmdl`` に ``joined_sales_info`` というデータモデル名で結合モデルを追加します。
``models.dmdl`` 内に追加する場所はどこでもかまいません。
ここではデータモデル ``item_info`` の直後に追加します。

..  literalinclude:: dmdl-attachment/dmdl-2-finished-models.dmdl
    :language: dmdl
    :caption: models.dmdl
    :name: models.dmdl-2-1
    :lines: 67-87

結合モデルにはいくつかの記述方法がありますが、ここでは以下のような構造で作成しています。

..  code-block:: none

    joined <結合モデル名>
    = <対象データモデル1> -> {
        <結合前プロパティ名> -> <結合後プロパティ名>
        <結合前プロパティ名> -> <結合後プロパティ名>
        ...
    } % <結合キー1>
    + <対象データモデル2> -> {
        <結合前プロパティ名> -> <結合後プロパティ名>
        <結合前プロパティ名> -> <結合後プロパティ名>
        ...
    } % <結合キー2>;

上から順番に見ていきます。結合モデルはデータモデル名の前に ``joined`` キーワードを指定します。

..  literalinclude:: dmdl-attachment/dmdl-2-finished-models.dmdl
    :language: none
    :caption: models.dmdl
    :name: models.dmdl-2-2
    :lines: 67-68

データモデル名の定義に続けて、結合の対象となるデータモデルに関する情報を定義します。
まず、売上明細部分の定義を見ていきます。

..  literalinclude:: dmdl-attachment/dmdl-2-finished-models.dmdl
    :language: none
    :caption: models.dmdl
    :name: models.dmdl-2-3
    :lines: 69-79

対象データモデル名に続けて、``{...}`` で囲われたブロック内でプロパティのマッピング情報を記述します。

``item_code -> item_code;`` となっている行を例に説明すると、

* 結合前のデータモデル ``sales_detail`` が持つプロパティ ``item_code`` を
* 結合後のデータモデル ``joined_sales_info`` が持つプロパティ ``item_code`` にマッピングする

という意味になります。この例では、結合前のプロパティと結合後のプロパティが同じ名前ですが、
この仕組みによって結合前と後で異なるプロパティにマッピングすることができるようになっています。

なお ``sales_detail`` が持つプロパティのうち、ここに書かれていないプロパティ（例えば ``sales_date_time`` など）は結合モデルには引き継がれず、捨てられることになります。

最下行の ``% item_code`` では結合キーとなるプロパティを指定しています。
マッピング前後でプロパティ名を変更する場合、このプロパティ名は **結合後プロパティ名** を指定する必要があることに注意してください。

続けて商品マスタ側の定義を見てみます。

..  literalinclude:: dmdl-attachment/dmdl-2-finished-models.dmdl
    :language: none
    :caption: models.dmdl
    :name: models.dmdl-2-4
    :lines: 80-87

キーワード ``+`` に続けて ``sales_detail`` と同様に結合対象となるデータモデル名、プロパティのマッピング情報、結合キーを指定しています。

このデータモデルの定義の結果、結合モデル ``joined_sales_info`` は以下のような構造をもつデータモデルとして定義されます。

.. _example-app-data-schema-joined_sales_info:

..  list-table:: 結合モデル ( ``joined_sales_info`` )
    :widths: 3 4 4
    :header-rows: 1

    * - プロパティ名
      - 説明
      - 結合元データモデル
    * - ``item_code``
      - 商品コード (結合キー)
      - ``sales_detail`` = ``item_info``
    * - ``amount``
      - 数量
      - ``sales_detail``
    * - ``selling_price``
      - 販売金額
      - ``sales_detail``
    * - ``category_code``
      - カテゴリコード
      - ``item_info``

結合モデルの定義では、結合条件は同じ値をもつキー値で結合する等価結合条件のみを指定することができます。
これ以外の条件で結合する非等価結合条件を指定する場合は、演算子の実装側で結合条件を記述することになります。

非等価結合の実装方法は、この後のチュートリアル :doc:`dsl-operator-1` で説明します。

集計モデルを作成する
====================

集計モデルは、ある1つのデータモデルに対して「集計」の操作を行った結果として生成するデータモデルを表します。

集計モデルを作成しておくことで、データフロー内で集計に関する処理を実装することなく、集計結果のデータモデルを取得することができるようになります。

集計モデルの定義
----------------

このチュートリアルでは、`結合モデルを作成する`_ で作成した :ref:`example-app-data-schema-joined_sales_info` に対して集計を行い、
:ref:`example-app-data-schema-category_summary` を生成する集計モデルを作成していきます。

一つ前のチュートリアル :doc:`dmdl-1` では :ref:`example-app-data-schema-category_summary` はレコードモデルとして定義しました。

..  literalinclude:: dmdl-attachment/dmdl-1-finished-models.dmdl
    :language: dmdl
    :caption: models.dmdl
    :name: models.dmdl-2-5
    :lines: 67-78

このデータモデルを、以下のように集計モデルに変更します。

..  literalinclude:: dmdl-attachment/dmdl-2-finished-models.dmdl
    :language: dmdl
    :caption: models.dmdl
    :name: models.dmdl-2-6
    :lines: 89-100

集計モデルは以下のような構造で定義します。

..  code-block:: none

    summarized <集計モデル名> = <対象データモデル> => {
        <集約関数> <集計対象のプロパティ名> -> <集計結果のプロパティ名>;
        <集約関数> <集計対象のプロパティ名> -> <集計結果のプロパティ名>;
        ...
    } % <グループ化キー>;

上から順番に見ていきます。集計モデルはデータモデル名の前に ``summarized`` キーワードを指定します。
データモデル名の定義に続けて、集計の対象となるデータモデルを指定します。

..  literalinclude:: dmdl-attachment/dmdl-2-finished-models.dmdl
    :language: none
    :caption: models.dmdl
    :name: models.dmdl-2-7
    :lines: 90

データモデル名に続けて ``{...}`` で囲われたブロック内では、対象データモデルに対する集計方法を記述します。

集計対象のプロパティは「グループ化キー」で指定された値ごとにまとめられ、
行頭で指定する「集計関数」（ここの例では ``any`` や ``sum`` ）に従って集計されます。

..  literalinclude:: dmdl-attachment/dmdl-2-finished-models.dmdl
    :language: none
    :caption: models.dmdl
    :name: models.dmdl-2-8
    :lines: 92-99

``sum amount -> amount_total;`` となっている行を例に説明すると、

* 集計前のデータモデル ``joined_sales_info`` が持つプロパティ ``amount`` を
* 集計関数 ``sum`` によって合計して
* 集計後のデータモデル ``category_summary`` が持つプロパティ ``amount_total`` にマッピングする

という意味になります。

集計関数 ``sum`` は、グループ化した中の値の合計を算出します。
集計対象のプロパティは数値を表すデータ型を指定する必要があります。
また、値には ``NULL`` を含むことができないことに注意してください。

集計関数 ``any`` は、集計処理を行わずにデータモデルの値をそのまま利用します。
``any`` はグループ化キーなど、集計が不要な項目で、グルーピングした結果にすべて同じ値が入っているようなプロパティに使用します。
この例では ``any`` を指定している ``category_code`` はグループ化キーです。

最下行の ``% category_code`` ではグループ化キーとなるプロパティを指定しています。
マッピング前後でプロパティ名を変更する場合、このプロパティ名は **集計結果のプロパティ名** を指定する必要があることに注意してください。

..  literalinclude:: dmdl-attachment/dmdl-2-finished-models.dmdl
    :language: none
    :caption: models.dmdl
    :name: models.dmdl-2-9
    :lines: 100

終わりに
========

全てのデータモデルを定義した後にデータモデルクラスの生成を行い、6つのデータモデルの生成が成功していることをコンソールで確認してください。

このチュートリアル終了時点のDMDLスクリプト :file:`models.dmdl` は、次のようになります。

..  literalinclude:: dmdl-attachment/dmdl-2-finished-models.dmdl
    :language: dmdl
    :linenos:
    :caption: models.dmdl
    :name: models.dmdl-2-10

関連ドキュメント
================

* :asakusafw:`[Asakusa Framework documentation] - DMDLスタートガイド <dmdl/start-guide.html>`
* :asakusafw:`[Asakusa Framework documentation] - DMDLユーザーガイド <dmdl/user-guide.html>`

