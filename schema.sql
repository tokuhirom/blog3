CREATE TABLE "entry" (
  "path" varchar(255) CHARACTER SET ascii COLLATE ascii_general_ci NOT NULL,
  "title" varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  "body" text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  "visibility" enum('private','public') DEFAULT 'private' NOT NULL,
  "format" enum('html','mkdn') NOT NULL DEFAULT 'mkdn',
  "created_at" datetime DEFAULT CURRENT_TIMESTAMP,
  "updated_at" datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY ("path"),
  KEY "title" ("title"),
  KEY "created_at" ("created_at"),
  KEY "updated_at" ("updated_at"),
  FULLTEXT KEY "idx_bigram" ("title","body") /*!50100 WITH PARSER "ngram" */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
