====================
ジョブフローのテスト
====================

このチュートリアルでは、ジョブフローのテストを作成する方法を説明していきます。

ジョブフローのテスト
====================

ジョブフローのテストは基本的に :doc:`testing-flowpart` で説明した方法と同様にテストドライバーを利用してテストを作成していきます。
フロー部品とジョブフローでは一部テストドライバーで利用するAPIが異なるので、ここではその違いを中心に説明します。

ジョブフローのテストメソッドを作成する
======================================

以下は、ジョブフローのテストをテストドライバーで実行するテストメソッドの例です。

..  code-block:: java
    :caption: CategorySummaryJobTest.java
    :name: CategorySummaryJobTest.java-1

    ...
    import com.asakusafw.testdriver.JobFlowTester;
    ...

    private void run(String dataSet) {
        JobFlowTester tester = new JobFlowTester(getClass());
        tester.setBatchArg("date", "testing");

        tester.input("storeInfo", StoreInfo.class)
            .prepare("masters.xls#store_info");
        tester.input("itemInfo", ItemInfo.class)
            .prepare("masters.xls#item_info");

        tester.input("salesDetail", SalesDetail.class)
            .prepare(dataSet + "#sales_detail");
        tester.output("categorySummary", CategorySummary.class)
            .verify(dataSet + "#result", dataSet + "#result_rule");
        tester.output("errorRecord", ErrorRecord.class)
            .dumpActual("build/dump/error_" + dataSet);

        tester.runTest(CategorySummaryJob.class);
    }

ジョブフローをテストするには、テストドライバー用のAPIクラス ``JobFlowTester`` [#]_ をインスタンス化します。

利用方法は :doc:`testing-flowpart` とほぼ同様ですが、以下の点が異なります。

* 入出力の名前には、ジョブフローの注釈 ``Import`` や ``Export`` の ``name`` に指定した値を利用する
* ``runTest`` メソッドにはジョブフロークラス( ``.class`` )のみを指定する

  * ``In`` や ``Out`` の指定は不要のため、各 ``input`` や ``output`` で戻り値を保持する必要はない。

``JobFlowTester`` のメソッド ``setBatchArg`` はバッチ引数（バッチ実行時の引数）を設定します。
ジョブフローのインポータ記述やエクスポータ記述、データフロー内の演算子などでバッチ引数を利用している場合は、このメソッドを使ってバッチ引数を設定してください。

..  [#] :javadoc:`com.asakusafw.testdriver.JobFlowTester`

関連ドキュメント
================

* :asakusafw:`[Asakusa Framework documentation] - テストドライバーユーザーガイド <testing/user-guide.html>`
