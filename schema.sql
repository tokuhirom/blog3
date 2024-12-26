CREATE TABLE "entry" (
  "path" varchar(255) CHARACTER SET ascii COLLATE ascii_general_ci NOT NULL,
  "title" varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  "body" text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  "visibility" enum('private','public') DEFAULT 'private' NOT NULL,
  "format" enum('html','mkdn') NOT NULL DEFAULT 'mkdn',
  "created_at" datetime DEFAULT CURRENT_TIMESTAMP,
  "updated_at" datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY ("path"),
  UNIQUE title ("title"),
  KEY "created_at" ("created_at"),
  KEY "updated_at" ("updated_at"),
  FULLTEXT KEY "idx_bigram" ("title","body") /*!50100 WITH PARSER "ngram" */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

create table entry_link (
  src_path varchar(255) CHARACTER SET ascii COLLATE ascii_general_ci NOT NULL,
  dst_title varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  PRIMARY KEY (src_path, dst_title),
  FOREIGN KEY (src_path) REFERENCES entry(path) ON DELETE CASCADE,
  INDEX (dst_title)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
