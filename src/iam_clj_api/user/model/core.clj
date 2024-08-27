(ns iam-clj-api.user.model.core
  (:require [next.jdbc :as jdbc]
            [lib.core :refer :all]))

(def ds (get-datasource))

(defn create-user-table []
  (jdbc/execute! ds
                 ["CREATE TABLE IF NOT EXISTS users (id SERIAL PRIMARY KEY,
      username VARCHAR(20) NOT NULL,
      email VARCHAR(256) NOT NULL,
      password VARCHAR(256) NOT NULL,
      created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
      );"]))

(defn drop-user-table []
  (jdbc/execute! ds ["DROP TABLE IF EXISTS users;"]))

(defn insert-user [user]
  (jdbc/execute! ds
                 ["INSERT INTO users (username, email, password, created_at)
         VALUES (?, ?, ?, DEFAULT);"
                  (get user :username) (get user :email) (get user :password)]))

(defn get-all-users []
  (let [result (jdbc/execute! ds
                              ["SELECT id, username, email FROM users;"])]
    (map remove-namespace (map #(into {} %) result))))

(defn get-user-by-id [id]
  (let [result (jdbc/execute! ds
                              ["SELECT id, username, email, created_at FROM users WHERE id = ?;" id])]
    (first result)))

(defn get-user-by-username [username]
  (let [result (jdbc/execute! ds
                              ["SELECT * FROM users WHERE username = ?;" username])]
    (remove-namespace (first result))))

(defn update-user-username [id new-username]
  (let [result (jdbc/execute! ds
                              ["UPDATE users SET username = ? WHERE id = ?;"
                               new-username id])]
    {:update-count (:next.jdbc/update-count (first result))}))

(defn update-user-email [id new-email]
  (let [result (jdbc/execute! ds
                              ["UPDATE users SET email = ? WHERE id = ?;"
                               new-email id])]
    {:update-count (:next.jdbc/update-count (first result))}))

(defn update-user-password [id new-password]
  (let [result (jdbc/execute! ds
                              ["UPDATE users SET password = ? WHERE id = ?;"
                               new-password id])]
    {:update-count (:next.jdbc/update-count (first result))}))

(defn delete-user [id]
  (let [result (jdbc/execute! ds
                              ["DELETE FROM users WHERE id = ?;" id])]
    {:delete-count (:next.jdbc/update-count (first result))}))
