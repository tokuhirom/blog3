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

# 実行用の軽量なイメージを作成
FROM node:22 AS release
WORKDIR /usr/src/app

# ビルド成果物と依存関係をコピー
COPY --from=build /usr/src/app/build ./build
COPY --from=build /usr/src/app/node_modules ./node_modules

# アプリケーションを起動
EXPOSE 3000
ENTRYPOINT ["node", "build/index.js"]
