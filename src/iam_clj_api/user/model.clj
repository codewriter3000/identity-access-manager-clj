(ns iam-clj-api.user.model
  (:require [next.jdbc :as jdbc]
            [lib.core :refer :all]
            [clojure.string :as str]))

(def ds (get-datasource))

(defn create-user-table []
  (jdbc/execute! ds
                 ["CREATE TABLE IF NOT EXISTS users (id SERIAL PRIMARY KEY,
      username VARCHAR(20) NOT NULL,
      email VARCHAR(256) NOT NULL,
      first_name VARCHAR(32),
      last_name VARCHAR(32),
      password VARCHAR(256) NOT NULL,
      created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
      );"]))

(defn drop-user-table []
  (jdbc/execute! ds ["DROP TABLE IF EXISTS users;"]))

; INSERT INTO users (:username, :email, :password) VALUES (?, ?, ?);
(defn insert-user [user]
  (let [filtered-user (into {} (filter (comp some? val) user))
        keys (map name (keys filtered-user)) ; Convert keys to strings
        values (vals filtered-user)
        columns (str/join ", " keys)
        placeholders (str/join ", " (repeat (count values) "?"))
        query (str "INSERT INTO users (" columns ") VALUES (" placeholders ");")]
    (jdbc/execute! ds (into [query] values))))

(defn get-all-users []
  (let [result (jdbc/execute! ds
                              ["SELECT id, username, email, first_name, last_name FROM users;"])]
    (map remove-namespace (map #(into {} %) result))))

(defn get-user-by-id [id]
  (println "ID:" id)
  (println "ID type:" (type id))
  (let [result (jdbc/execute! ds
                              ["SELECT id, username, email, first_name, last_name, created_at FROM users WHERE id = ?;" (Integer/parseInt id)])]
    (first result)))

(defn get-user-by-username [username]
  (let [result (jdbc/execute! ds
                              ["SELECT * FROM users WHERE username = ?;" username])]
    (remove-namespace (first result))))

      (defn update-user [id user]
        (if (empty? user)
          {:update-count 0}
          (let [filtered-user (into {} (filter (comp some? val) user))
                set-clause (str/join ", " (map (fn [[k v]] (str (name k) " = ?")) filtered-user))
                values (concat (vals filtered-user) [(Integer/parseInt id)]) ; Cast id to integer
                query (str "UPDATE users SET " set-clause " WHERE id = ?;")]
            (println "Query:" (into [query] values))
            (jdbc/execute! ds (into [query] values)))))

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
                              ["DELETE FROM users WHERE id = ?;" (Integer/parseInt id)])]
    {:delete-count (:next.jdbc/update-count (first result))}))

(defn get-roles-for-user [id]
  (let [result (jdbc/execute! ds
                              ["SELECT roles.id, roles.name, roles.description FROM roles
        JOIN users_roles ON roles.id = users_roles.role_id
        WHERE users_roles.user_id = ?;" id])]
    (map remove-namespace (map #(into {} %) result))))

(defn get-permissions-for-user [id]
  (let [result (jdbc/execute! ds
                              ["SELECT permissions.id, permissions.name, permissions.description FROM permissions
                               JOIN users_permissions ON permissions.id = users_permissions.permission_id
                               WHERE users_permissions.user_id = ?;" id])]
    (map remove-namespace (map #(into {} %) result))))