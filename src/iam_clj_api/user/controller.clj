(ns iam-clj-api.user.controller
  (:require [lib.core :refer :all]
            [iam-clj-api.user.model :as model]
            [buddy.hashers :as hashers]
            [lib.response :refer [error success work]]))

(defn validate-input [user]
  (let [username (get user :username)
        email (get user :email)
        password (get user :password)]
    (cond
      (not (re-matches #"(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}" password))
      (error 400 "Password must be at least 8 characters long, contain an uppercase letter, a lowercase letter, a number, and a special character")

      (or (< (count username) 3) (> (count username) 20))
      (error 400 "Username must be between 3 and 20 characters")

      (not (re-matches #".+@.+\..+" email))
      (error 400 "Email is invalid")

      (not (empty? (model/get-user-by-username username)))
      (error 400 "Username already exists")

      :else
      (assoc user :password (hashers/derive (get user :password))))))

(defn insert-user [user]
  (println "Received user data:" user)
  (let [validated-user (validate-input user)]
    (if (not= 400 (:status validated-user))
      (do
        (model/insert-user validated-user)
        (success 201 "User created successfully"))
        validated-user)))

(defn login-user [username password]
  (let [user (model/get-user-by-username username)]
    (if (and user (hashers/check password (get user :password)))
      {:status 200 :body "Login successful"}
      {:status 401 :error "Invalid username or password"})))

(defn get-all-users []
  (let [users (model/get-all-users)]
    (work 200 (map remove-namespace users))))

(defn get-user-by-id [id]
  (let [user (model/get-user-by-id id)]
    (if user
      (work 207 user)
      (error 404 "User not found"))))

(defn update-user [id user]
  (println "Received user data into controller:" user)
  (let [existing-user (model/get-user-by-id id)]
    (if existing-user
      (let [result (model/update-user id user)]
        (if (= 1 (:update-count result))
          (success 200 "User updated")
          (error 400 "Failed to update user")))
      (error 404 "User not found"))))

(defn update-user-username [id new-username]
  (let [user (model/get-user-by-id id)]
    (if user
      (let [result (model/update-user id {:username new-username})]
        (if (= 1 (:update-count result))
          {:status 200 :body "Username updated"}
          {:status 400 :error "Failed to update username"}))
      {:status 404 :error "User not found"})))

(defn update-user-email [id new-email]
  (let [user (model/get-user-by-id id)]
    (if user
      (let [result (model/update-user id {:email new-email})]
        (if (= 1 (:update-count result))
          {:status 200 :body "Email updated"}
          {:status 400 :error "Failed to update email"}))
      {:status 404 :error "User not found"})))

(defn update-user-password [id new-password]
  (let [user (model/get-user-by-id id)]
    (if user
      (let [result (model/update-user-password id (hashers/derive new-password))]
        {:status 200 :body "Password updated"})
      {:status 404 :error "User not found"})))

(defn delete-user [id]
  (let [user (model/get-user-by-id id)]
    (if user
      (let [result (model/delete-user id)]
        (if (= 1 (:delete-count result))
          {:status 200 :body "User deleted"}
          {:status 400 :error "Failed to delete user"})
        )
      {:status 404 :error "User not found"})))

(defn get-roles-for-user [id]
  (let [user (model/get-user-by-id id)]
    (if user
      (let [roles (model/get-roles-for-user id)]
        {:status 200 :body roles})
      {:status 404 :error "User not found"})))

(defn get-permissions-for-user [id]
  (let [user (model/get-user-by-id id)]
    (if user
      (let [permissions (model/get-permissions-for-user id)]
        {:status 200 :body permissions})
      {:status 404 :error "User not found"})))