# ベースイメージとして Node.js を使用
FROM node:22 AS build

# 作業ディレクトリを設定
WORKDIR /usr/src/app

# 依存関係をインストール
COPY package.json package-lock.json ./
RUN npm ci

# アプリケーションコードをコピー
COPY . .
COPY .env.example .env

# ビルドを実行
RUN npm run build

RUN apt-get update && apt-get install -y cpanminus

# cpanminus を動かして amazon 用のライブラリをインストールする
RUN cd amazon-batch/ && cpanm --notest -L extlib --installdeps .

# 実行用の軽量なイメージを作成
FROM node:22 AS release
WORKDIR /usr/src/app

# 必要なパッケージをインストール
RUN apt-get update && apt-get install -y openssl mariadb-client perl

# ビルド成果物と依存関係をコピー
COPY --from=build /usr/src/app/build ./build
COPY --from=build /usr/src/app/node_modules ./node_modules
COPY --from=build /usr/src/app/amazon-batch ./amazon-batch

# アプリケーションを起動
EXPOSE 3000
ENTRYPOINT ["node", "build/index.js"]
