(ns iam-clj-api.permission.controller.core
  (:require [lib.core :refer :all]
            [iam-clj-api.permission.model.core :as model]))

(defn get-all-permissions []
  {:status 200 :body (model/get-all-permissions)})

(defn get-permission-by-id [id]
    (let [permission (model/get-permission-by-id id)]
        (if (get permission :id)
        {:status 200 :body permission}
        {:status 404 :error "Permission not found"})))

(defn validate-input [name description]
  (cond
    (empty? name)
    {:status 400 :error "Missing name"}

    (> (count (model/get-permission-by-name name)) 0)
    {:status 400 :error (str "Permission with name " name " already exists")}

    :else
    {:name name :description description}))

(defn insert-permission [name description]
  (let [validated-input (validate-input name description)]
    (if (= 400 (:status validated-input))
    validated-input
    (let [result (model/insert-permission validated-input)]
        {:status 201 :body "Permission created successfully"}))))

(defn update-permission-name [id new-name]
  (if (empty? new-name)
    {:status 400 :error "Missing new name"}
      (let [result (model/update-permission-name id new-name)]
       (if (= 1 (:update-count result))
         {:status 200 :body "Permission name updated"}
         {:status 400 :error "Failed to update permission name"}))))

(defn update-permission-description [id new-description]
    (let [result (model/update-permission-description id new-description)]
        (if (= 1 (:update-count result))
        {:status 200 :body "Permission description updated"}
        {:status 400 :error "Failed to update permission description"})))

(defn delete-permission [id]
    (let [permission (model/get-permission-by-id id)]
        (if permission
        (let [result (model/delete-permission id)]
            (if (= 1 (:delete-count result))
            {:status 200 :body "Permission deleted"}
            {:status 400 :error "Failed to delete permission"}))
        {:status 404 :error "Permission not found"})))

(defn get-users-with-permission [id]
    (let [permission (model/get-permission-by-id id)
                users (model/get-users-with-permission id)]
      (if (= (count permission) 0)
        {:status 404 :error "Permission not found"}
        {:status 200 :body users})))

(defn add-permission-to-user [id user-id]
    (let [result (model/add-permission-to-user id user-id)]
        (if (= 1 (:insert-count result))
            {:status 200 :body "Permission added to user"}
            {:status 400 :error "Failed to add permission to user"})))

(defn remove-permission-from-user [id user-id]
    (let [result (model/remove-permission-from-user id user-id)]
        (if (= 1 (:delete-count result))
        {:status 200 :body "Permission removed from user"}
        {:status 400 :error "Failed to remove permission from user"})))