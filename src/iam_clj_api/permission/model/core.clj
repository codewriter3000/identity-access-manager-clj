(ns iam-clj-api.permission.model.core
  (:require [next.jdbc :as jdbc]
            [lib.core :refer :all]))

(def ds (get-datasource))

(defn create-permission-table []
  (jdbc/execute! ds
                 ["CREATE TABLE IF NOT EXISTS permissions (id SERIAL PRIMARY KEY,
      name VARCHAR(128) NOT NULL,
      description VARCHAR(1000)
      );

                   CREATE TABLE IF NOT EXISTS users_permissions (user_id INT NOT NULL,
      permission_id INT NOT NULL
      );"]))

(defn drop-permission-table []
  (jdbc/execute! ds ["DROP TABLE IF EXISTS permissions; DROP TABLE IF EXISTS users_permissions;"]))

(defn insert-permission [permission]
    (jdbc/execute! ds
                     ["INSERT INTO permissions (name, description)
            VALUES (?, ?);"
                    (get permission :name) (get permission :description)]))

(defn get-all-permissions []
  (let [result (jdbc/execute! ds
                              ["SELECT id, name, description FROM permissions;"])]
    (if (empty? result)
      nil
      (map remove-namespace (map #(into {} %) result)))))

(defn get-permission-by-id [id]
    (let [result (jdbc/execute! ds
                                ["SELECT id, name, description FROM permissions WHERE id = ?;" id])]
        (remove-namespace (first result))))

(defn get-permission-by-name [name]
    (let [result (jdbc/execute! ds
                                ["SELECT id, name, description FROM permissions WHERE name = ?;" name])]
        (remove-namespace (first result))))

(defn update-permission-name [id new-name]
    (let [result (jdbc/execute! ds
                                ["UPDATE permissions SET name = ? WHERE id = ?;"
                                 new-name id])]
        {:update-count (:next.jdbc/update-count (first result))}))

(defn update-permission-description [id new-description]
    (let [result (jdbc/execute! ds
                                ["UPDATE permissions SET description = ? WHERE id = ?;"
                                 new-description id])]
        {:update-count (:next.jdbc/update-count (first result))}))

(defn delete-permission [id]
    (let [result (jdbc/execute! ds
                                ["DELETE FROM permissions WHERE id = ?;" id])]
        {:update-count (:next.jdbc/update-count (first result))}))

(defn get-users-with-permission [id]
    (let [result (jdbc/execute! ds
                                ["SELECT u.id, u.username, u.email
            FROM users u
            JOIN users_permissions up ON u.id = up.user_id
            WHERE up.permission_id = ?;" id])]
      (if (not (some? result))
        nil
        (map remove-namespace (map #(into {} %) result)))))

(defn add-permission-to-user [id user-id]
    (jdbc/execute! ds
                     ["INSERT INTO users_permissions (user_id, permission_id)
            VALUES (?, ?);"
                    user-id id]))

(defn remove-permission-from-user [id user-id]
    (jdbc/execute! ds
                     ["DELETE FROM users_permissions WHERE user_id = ? AND permission_id = ?;"
                    user-id id]))