-- Current schema:
CREATE TABLE "entry" (
  "path" varchar(255) CHARACTER SET ascii COLLATE ascii_general_ci NOT NULL,
  "title" varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  "body" text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  "status" enum('draft','published') NOT NULL,
  "format" enum('html','mkdn') NOT NULL DEFAULT 'mkdn',
  "created_at" datetime DEFAULT CURRENT_TIMESTAMP,
  "updated_at" datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY ("path"),
  KEY "created_at" ("created_at"),
  KEY "updated_at" ("updated_at"),
  FULLTEXT KEY "idx_bigram" ("title","body") /*!50100 WITH PARSER "ngram" */
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- mysqldump -u root -p --databases blog > schema.sql

-- check the table:
select status, count(*) from entry group by status;

-- mysql> select status, count(*) from entry group by status;
-- +-----------+----------+
-- | status    | count(*) |
-- +-----------+----------+
-- | published |     1587 |
-- | draft     |       44 |
-- +-----------+----------+
-- 2 rows in set (0.09 sec)

-- add a new column to the table:
ALTER TABLE entry ADD COLUMN visibility ENUM('public', 'private') DEFAULT 'private' AFTER status;

-- Update the visibility column based on the status column:
UPDATE entry
SET visibility = CASE
    WHEN status = 'published' THEN 'public'
    ELSE 'private'
END;

-- Check the counts of each status and visibility:
select status, count(*) from entry group by status;
select visibility, count(*) from entry group by visibility;


-- mysql> select status, count(*) from entry group by status;
-- +-----------+----------+
-- | status    | count(*) |
-- +-----------+----------+
-- | published |     1587 |
-- | draft     |       44 |
-- +-----------+----------+
-- 2 rows in set (0.09 sec)

-- mysql> select visibility, count(*) from entry group by visibility;
-- +------------+----------+
-- | visibility | count(*) |
-- +------------+----------+
-- | public     |     1587 |
-- | private    |       44 |
-- +------------+----------+
-- 2 rows in set (0.09 sec)

-- Now we can make the visibility column NOT NULL:
ALTER TABLE entry MODIFY COLUMN visibility ENUM('public', 'private') NOT NULL DEFAULT 'private';

-- Use visibility instead of status in the PublicEntryRepository and AdminEntryRepository
-- Deploy to the production server

-- Now we can drop the status column:
ALTER TABLE entry DROP COLUMN status;

-- Finally, we can drop the status column:
CREATE TABLE entry (
    title VARCHAR(255),
    path VARCHAR(255),
    body LONGTEXT,
    visibility ENUM('public', 'private') NOT NULL DEFAULT 'private',
    format VARCHAR(255) DEFAULT 'mkdn',
    createdAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    updatedAt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (path)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
