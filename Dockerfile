# ベースイメージとして Bun を使用
FROM oven/bun:1 AS build

# 作業ディレクトリを設定
WORKDIR /usr/src/app

# 依存関係をインストール
COPY package.json bun.lockb ./
RUN bun install --frozen-lockfile

# アプリケーションコードをコピー
COPY . .
COPY .env.example .env

# ビルドを実行
RUN bun run build

# 実行用の軽量なイメージを作成
FROM oven/bun:1 AS release
WORKDIR /usr/src/app

# ビルド成果物と依存関係をコピー
COPY --from=build /usr/src/app/build ./build
COPY --from=build /usr/src/app/node_modules ./node_modules

# アプリケーションを起動
EXPOSE 3000
ENTRYPOINT ["bun", "build/index.js"]
