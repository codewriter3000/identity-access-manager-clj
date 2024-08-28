(ns iam-clj-api.role.model
  (:require [next.jdbc :as jdbc]
           [lib.core :refer :all]))

(def ds (get-datasource))

(defn create-role-table []
  (jdbc/execute! ds
                 ["CREATE TABLE IF NOT EXISTS roles (id SERIAL PRIMARY KEY,
      name VARCHAR(128) NOT NULL,
      description VARCHAR(1000)
      );

                   CREATE TABLE IF NOT EXISTS users_roles (user_id INT NOT NULL,
      role_id INT NOT NULL
      );

                   CREATE TABLE IF NOT EXISTS roles_permissions (role_id INT NOT NULL,
      permission_id INT NOT NULL
      );"]))

(defn drop-role-table []
  (jdbc/execute! ds ["DROP TABLE IF EXISTS roles;
                      DROP TABLE IF EXISTS users_roles;
                      DROP TABLE IF EXISTS roles_permissions;"]))

(defn insert-role [role]
    (jdbc/execute! ds
                     ["INSERT INTO roles (name, description)
            VALUES (?, ?);"
                    (get role :name) (get role :description)]))

(defn get-all-roles []
    (let [result (jdbc/execute! ds
                                ["SELECT id, name, description FROM roles;"])]
        (if (empty? result)
        nil
        (map remove-namespace (map #(into {} %) result)))))

(defn get-role-by-id [id]
    (let [result (jdbc/execute! ds
                                ["SELECT id, name, description FROM roles WHERE id = ?;" id])]
        (remove-namespace (first result))))

(defn get-role-by-name [name]
    (let [result (jdbc/execute! ds
                                ["SELECT id, name, description FROM roles WHERE name = ?;" name])]
        (remove-namespace (first result))))

(defn update-role-name [id new-name]
    (let [result (jdbc/execute! ds
                                ["UPDATE roles SET name = ? WHERE id = ?;"
                                 new-name id])]
        {:update-count (:next.jdbc/update-count (first result))}))

(defn update-role-description [id new-description]
    (let [result (jdbc/execute! ds
                                ["UPDATE roles SET description = ? WHERE id = ?;"
                                 new-description id])]
        {:update-count (:next.jdbc/update-count (first result))}))

(defn delete-role [id]
    (let [result (jdbc/execute! ds
                                ["DELETE FROM roles WHERE id = ?;" id])]
        {:update-count (:next.jdbc/update-count (first result))}))

(defn get-users-with-role [id]
  (let [result (jdbc/execute! ds
                              ["SELECT u.id, u.username, u.email
                                  FROM users u
                                  JOIN users_roles ur ON u.id = ur.user_id
                                  WHERE ur.role_id = ?;" id])]
    (if (not (some? result))
      nil
      (map remove-namespace (map #(into {} %) result)))))

(defn add-role-to-user [role-id user-id]
    (let [result (jdbc/execute! ds
                     ["INSERT INTO users_roles (role_id, user_id)
            VALUES (?, ?);"
                    role-id user-id])]
          {:update-count (:next.jdbc/update-count (first result))}))

(defn remove-role-from-user [id user-id]
    (let [result (jdbc/execute! ds
                     ["DELETE FROM users_roles WHERE user_id = ? AND role_id = ?;"
                      user-id id])]
      {:update-count (:next.jdbc/update-count (first result))}))

(defn get-permissions-for-role [id]
    (let [result (jdbc/execute! ds
                                ["SELECT p.id, p.name, p.description
                                FROM permissions p
                                JOIN roles_permissions rp ON p.id = rp.permission_id
                                WHERE rp.role_id = ?;" id])]
        (if (not (some? result))
          nil
          (map remove-namespace (map #(into {} %) result)))))

(defn add-permission-to-role [permission-id role-id]
  (let [result (jdbc/execute! ds
                              ["INSERT INTO roles_permissions (role_id, permission_id)
            VALUES (?, ?);"
                               role-id permission-id])]
    {:update-count (:next.jdbc/update-count (first result))}))

(defn remove-permission-from-role [id permission-id]
  (let [result (jdbc/execute! ds
                              ["DELETE FROM roles_permissions WHERE role_id = ? AND permission_id = ?;"
                               id permission-id])]
    {:update-count (:next.jdbc/update-count (first result))}))
