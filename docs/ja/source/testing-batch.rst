==============
バッチのテスト
==============

このチュートリアルでは、バッチのテストを作成する方法を説明していきます。

バッチのテスト
==============

バッチのテストも基本的に :doc:`testing-flowpart` や :doc:`testing-jobflow` で説明した方法と同様にテストドライバーを利用してテストを作成していきますが、
一部テストドライバで利用するAPIが異なるので、ここではその違いを中心に説明します。

バッチのテストメソッドを作成する
================================

以下は、バッチのテストをテストドライバで実行するテストメソッドの例です。

..  code-block:: java
    :caption: SummarizeBatchTest.java
    :name: SummarizeBatchTest.java-1

    ...
    import com.asakusafw.testdriver.BatchTester;
    ...

    private void run(String dataSet) {
        BatchTester tester = new BatchTester(getClass());
        tester.setBatchArg("date", "testing");

        tester.jobflow("byCategory").input("storeInfo", StoreInfo.class)
            .prepare("masters.xls#store_info");
        tester.jobflow("byCategory").input("itemInfo", ItemInfo.class)
            .prepare("masters.xls#item_info");

        tester.jobflow("byCategory").input("salesDetail", SalesDetail.class)
            .prepare(dataSet + "#sales_detail");
        tester.jobflow("byCategory").output("categorySummary", CategorySummary.class)
            .verify(dataSet + "#result", dataSet + "#result_rule");
        tester.jobflow("byCategory").output("errorRecord", ErrorRecord.class)
            .dumpActual("build/dump/error_" + dataSet);

        tester.runTest(SummarizeBatch.class);
    }

バッチをテストするには、テストドライバ用のAPIクラス ``BatchTester`` [#]_ をインスタンス化します。

利用方法は :doc:`testing-flowpart` や :doc:`testing-jobflow` とほぼ同様ですが、以下の点が異なります。

* 入出力を指定する前に、どのジョブフローに対するテストデータなのかを指定する

  * ``jobflow`` メソッドを経由して入出力を利用するジョブフローのID（注釈 ``@JobFlow`` の ``name`` に指定した文字列）を指定する
* ``runTest`` メソッドにはバッチクラス( ``.class`` )を指定する

  * ``In`` や ``Out`` の指定は不要のため、各 ``input`` や ``output`` で戻り値を保持する必要はない。

..  [#] :javadoc:`com.asakusafw.testdriver.BatchTester`

関連ドキュメント
================

* :asakusafw:`[Asakusa Framework documentation] - テストドライバーユーザーガイド <testing/user-guide.html>`
