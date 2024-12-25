# blog3

tokuhirom の blog のソースコードです。
本番環境は https://blog.64p.org/ です。

閲覧用サイトはサーバーサイドレンダリングとなるようになっています。
今回は sveltekit で実装しています。

## ビルド方法

    npm install
    npm run dev

で実行できます。

    npm run build

で本番用ビルドができます。

    docker build -t blog3-app .

で Docker image ができます。

    docker run --init --rm -it -p 3000:3000 --env-file=.env blog3-app

などとして実行してください。

## Environment variables

以下の定義がすべて必要です。

# 環境変数一覧

| 環境変数名             | 必須/任意 | デフォルト値                        | 説明                                   |
| ---------------------- | --------- | ----------------------------------- | -------------------------------------- |
| `DATABASE_HOST`        | 必須      | なし                                | データベースのホスト名またはIPアドレス |
| `DATABASE_PORT`        | 必須      | なし                                | データベースのポート番号               |
| `DATABASE_USER`        | 必須      | なし                                | データベースのユーザー名               |
| `DATABASE_PASSWORD`    | 必須      | なし                                | データベースのパスワード               |
| `DATABASE_NAME`        | 必須      | なし                                | 使用するデータベースの名前             |
| `S3_REGION`            | 任意      | `jp-north-1`                        | S3 互換ストレージのリージョン          |
| `S3_BUCKET_NAME`       | 任意      | `blog3-attachments`                 | S3 互換ストレージのバケット名          |
| `S3_ACCESS_KEY_ID`     | 必須      | なし                                | S3 互換ストレージのアクセスキー        |
| `S3_SECRET_ACCESS_KEY` | 必須      | なし                                | S3 互換ストレージのシークレットキー    |
| `S3_ENDPOINT`          | 任意      | `https://s3.isk01.sakurastorage.jp` | S3 互換ストレージのエンドポイント      |
| `BASIC_AUTH_USERNAME`  | 必須      | なし                                | Basic 認証のユーザー名                 |
| `BASIC_AUTH_PASSWORD`  | 必須      | なし                                | Basic 認証のパスワード                 |

## Run locally

MySQL サーバーが必要です。

    docker run --name mysql-server -e MYSQL_ROOT_PASSWORD=yourpassword -p 3306:3306 -d mysql:8

schema.sql というファイルに書かれているテーブル定義をつかってテーブルを作れば動きます。

## LICENSE

See <https://tokuhirom.mit-license.org/>
