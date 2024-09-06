(ns iam-clj-api.user.controller
  (:require [lib.core :refer :all]
            [iam-clj-api.user.model :as model]
            [buddy.hashers :as hashers]))

(defn validate-input [username email password]
  (cond
    (or (empty? username) (empty? email) (empty? password))
    {:status 400 :error "All fields are required"}

    (not (re-matches #"(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}" password))
    {:status 400 :error "Password must be at least 8 characters long, contain an uppercase letter, a lowercase letter, a number, and a special character"}

    (or (< (count username) 3) (> (count username) 20))
    {:status 400 :error "Username must be between 3 and 20 characters"}

    (not (re-matches #".+@.+\..+" email))
    {:status 400 :error "Invalid email address"}

    (some? (model/get-user-by-username username))
    {:status 400 :error "Username already exists"}

    :else
    {:username username :email email :password password}))

(defn insert-user [username email password]
  (let [validated-input (validate-input username email password)]
          (let [{:keys [username email password]} validated-input
                password-hash (hashers/derive password)
                user {:username username :email email :password password-hash}]
            (model/insert-user user)
            {:status 201 :body "User created successfully"})))

(defn login-user [username password]
  (let [user (model/get-user-by-username username)]
    (if (and user (hashers/check password (get user :password)))
      {:status 200 :body "Login successful"}
      {:status 401 :error "Invalid username or password"})))

(defn get-all-users []
  (let [users (model/get-all-users)]
    {:status 200 :body (map remove-namespace users)}))

(defn get-user-by-id [id]
  (let [user (model/get-user-by-id id)]
    (if user
      {:status 200 :body user}
      {:status 404 :error "User not found"})))

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