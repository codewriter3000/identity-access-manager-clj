(ns iam-clj-api.role.controller
  (:require [lib.core :refer :all]
            [iam-clj-api.role.model :as model]
            [iam-clj-api.permission.model.core :as perm-model]
            [iam-clj-api.user.model.core :as user-model]))

(defn get-all-roles []
    {:status 200 :body (model/get-all-roles)})

(defn get-role-by-id [id]
    (let [role (model/get-role-by-id id)]
        (if (get role :id)
        {:status 200 :body role}
        {:status 404 :error "Role not found"})))

(defn validate-input [name description]
    (cond
        (empty? name)
        {:status 400 :error "Missing name"}

        (> (count (model/get-role-by-name name)) 0)
        {:status 400 :error (str "Role with name " name " already exists")}

        :else
        {:name name :description description}))

(defn insert-role [name description]
    (let [validated-input (validate-input name description)]
        (if (= 400 (:status validated-input))
        validated-input
        (let [result (model/insert-role validated-input)]
            {:status 201 :body "Role created successfully"}))))

(defn update-role-name [id new-name]
    (if (empty? new-name)
        {:status 400 :error "Missing new name"}
        (let [result (model/update-role-name id new-name)]
        (if (= 1 (:update-count result))
            {:status 200 :body "Role name updated"}
            {:status 400 :error "Failed to update role name"}))))

(defn update-role-description [id new-description]
    (let [result (model/update-role-description id new-description)]
        (if (= 1 (:update-count result))
        {:status 200 :body "Role description updated"}
        {:status 400 :error "Failed to update role description"})))

(defn delete-role [id]
    (let [role (model/get-role-by-id id)]
        (if role
        (let [result (model/delete-role id)]
            (if (= 1 (:delete-count result))
            {:status 200 :body "Role deleted"}
            {:status 400 :error "Failed to delete role"}))
        {:status 404 :error "Role not found"})))

(defn get-users-with-role [id]
    (let [role (model/get-role-by-id id)
                users (model/get-users-with-role id)]
        (if (= (count role) 0)
        {:status 404 :error "Role not found"}
        {:status 200 :body users})))

(defn add-role-to-user [role-id user-id]
    (if (empty? (model/get-role-by-id role-id))
        {:status 404 :error "Role not found"}
        (if (empty? (user-model/get-user-by-id user-id))
        {:status 404 :error "User not found"}
        (let [result (model/add-role-to-user role-id user-id)]
            (if (= 1 (:update-count result))
            {:status 200 :body "Role added to user"}
            {:status 400 :error "Failed to add role to user"})))))

(defn remove-role-from-user [id role-id]
    (let [result (model/remove-role-from-user id role-id)]
        (if (= 1 (:update-count result))
        {:status 200 :body "Role removed from user"}
        {:status 400 :error "Failed to remove role from user"})))

(defn get-permissions-for-role [id]
    (let [role (model/get-role-by-id id)
                permissions (model/get-permissions-for-role id)]
        (if (= (count role) 0)
        {:status 404 :error "Role not found"}
        {:status 200 :body permissions})))

(defn add-permission-to-role [permission-id role-id]
  (if (= (count (model/get-role-by-id role-id)) 0)
    {:status 404 :error "Role not found"}
    (if (= (count (perm-model/get-permission-by-id permission-id)) 0)
      {:status 404 :error "Permission not found"}
      (if (> (count (model/get-permissions-for-role role-id)) 0)
        {:status 400 :error "Permission already added to role"}
        (let [result (model/add-permission-to-role permission-id role-id)]
          (if (= 1 (:update-count result))
            {:status 200 :body "Permission added to role"}
            {:status 400 :error "Failed to add permission to role"}))))))

(defn remove-permission-from-role [permission-id role-id]
    (if (= (count (model/get-role-by-id role-id)) 0)
      {:status 404 :error "Role not found"}
      (if (= (count (perm-model/get-permission-by-id permission-id)) 0)
        {:status 404 :error "Permission not found"}
        (if (= (count (model/get-permissions-for-role role-id)) 0)
          {:status 400 :error "Permission not added to role"}
          (let [result (model/remove-permission-from-role permission-id role-id)]
            (if (= 1 (:update-count result))
              {:status 200 :body "Permission removed from role"}
              {:status 400 :error "Failed to remove permission from role"}))))))