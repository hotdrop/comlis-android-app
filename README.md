[![Kotlin 1.1.50](https://img.shields.io/badge/Kotlin-1.1.50-blue.svg)](http://kotlinlang.org)

# コムリス
空いた時間でチマチマ作った会社情報を保持するAndroidアプリケーションです。  
会社情報は自分で手動登録する必要がありますが、私のリポジトリにある  
`comlis-web-scraping`と`comlis-data-store`  
を導入することでリモートからデータを取得することも可能になります。  
ただしスクレイピング部分は自分でプログラミングする必要があります。  

This application contains deliverables distributed under the Apache License 2.0  
[Apache License Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)

# SDK version
  - min_sdk_version: 23
  - target_sdk_version: 26

# 環境
  - Kotlin 1.1.50
  - gradle 2.2.3

# 使用ライブラリ
  - RxKotlin 2.1.0
  - RxAndroid 2.0.1
  - RxJava 2.1.3
  - RxBinding-kotlin 2.0.0
  - Flexbox-layout 0.3.0-alpha4(RecyclerView対応版)
  - databinding
  - Dagger 2.11
  - Lottie 1.5.3
  - Android-Orma 4.2.5
  - deployGate
  - Retrofit 2.3.0
  - OkHttp 3.8.1

# 使い方
主要な画面の簡単な説明と使い方を示します。  
会社情報登録画面やタグ付与画面、ダイアログ等もありますが、詳細な説明は不要と思いますので省略します。
（説明が必要な画面だったらUIを見直す。）

# 会社一覧画面
アプリ起動時に表示される画面です。戻るボタンでの末端画面でもあります。  
登録した会社情報を分類ごとに一覧表示します。  
新規登録した会社情報はリストの先頭に追加されます。タブの並び順は、分類画面のアイテム順になります。  
各会社情報に表示するタグは、先頭から最大5つとなります。  

## 各操作
1. 登録:
2. 編集:
3. 削除:
4. 検索:
5. リモート接続:

## 画面イメージ
<img src="images/01_main.png" width="200" />  

# 分類画面
会社情報の分類を登録、編集する画面です。  
ここで登録した分類は、会社一覧画面のタブになります。  
識別しやすいよう、分類にはテーマカラーを指定できます。  
テーマカラーは会社一覧画面や会社詳細画面に反映されます。

## 各操作
1. 登録: 右下の丸い+ボタンをタップし、分類登録ダイアログから登録します。
2. 編集: 分類一覧から、名前またはカラーを変更したい分類をタップし、ダイアログから編集します。
3. 削除: 分類一覧から、削除したい分類をタップし、ダイアログから削除します。  
なお、誤操作を防ぐため分類に1つ以上会社情報が登録されている場合は削除できません。

## 画面イメージ
<img src="images/02_category.png" width="200" />

# タグ画面
会社情報のタグを表示する画面です。

## 各操作
1. 登録:
2. 編集:
3. 削除:

## 画面イメージ
<img src="images/03_tag.png" width="200" />

# 会社情報詳細画面

# リモート接続
