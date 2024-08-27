(ns iam-clj-api.user.controller.core
  (:require [lib.core :refer :all]
            [iam-clj-api.user.model.core :as model]
            [buddy.hashers :as hashers]
            [cats.monad.either :as either]
            [cats.core :as m]))

(defn validate-input [username email password]
  (cond
    (or (empty? username) (empty? email) (empty? password))
    (either/left {:status 400 :error "All fields are required"})

    (not (re-matches #"(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}" password))
    (either/left {:status 400 :error "Password must be at least 8 characters long, contain an uppercase letter, a lowercase letter, a number, and a special character"})

    (or (< (count username) 3) (> (count username) 20))
    (either/left {:status 400 :error "Username must be between 3 and 20 characters"})

    (not (re-matches #".+@.+\..+" email))
    (either/left {:status 400 :error "Invalid email address"})

    (some? (model/get-user-by-username username))
    (either/left {:status 400 :error "Username already exists"})

    :else
    (either/right {:username username :email email :password password})))

(defn insert-user [username email password]
  (m/mlet [validated-input (validate-input username email password)]
          (let [{:keys [username email password]} validated-input
                password-hash (hashers/derive password)
                user {:username username :email email :password password-hash}]
            (model/insert-user user)
            (either/right {:status 201 :body "User created successfully"}))))

(defn login-user [username password]
  (let [user (model/get-user-by-username username)]
    (if (and user (hashers/check password (get user :password)))
      (either/right {:status 200 :body "Login successful"})
      (either/left {:status 401 :error "Invalid username or password"}))))

(defn get-all-users []
  (let [users (model/get-all-users)]
    (either/right {:status 200 :body (map remove-namespace users)})))

(defn get-user-by-id [id]
  (let [user (model/get-user-by-id id)]
    (if user
      (either/right {:status 200 :body user})
      (either/left {:status 404 :error "User not found"}))))

(defn update-user-username [id new-username]
  (let [user (model/get-user-by-id id)]
    (if user
      (let [result (model/update-user-username id new-username)]
        (if (= 1 (:update-count result))
          (either/right {:status 200 :body "Username updated"})
          (either/left {:status 400 :error "Failed to update username"})))
      (either/left {:status 404 :error "User not found"}))))

(defn update-user-email [id new-email]
  (let [user (model/get-user-by-id id)]
    (if user
      (let [result (model/update-user-email id new-email)]
        (if (= 1 (:update-count result))
          (either/right {:status 200 :body "Email updated"})
          (either/left {:status 400 :error "Failed to update email"})))
      (either/left {:status 404 :error "User not found"}))))

(defn update-user-password [id new-password]
  (let [user (model/get-user-by-id id)]
    (if user
      (let [result (model/update-user-password id (hashers/derive new-password))]
        (either/right {:status 200 :body "Password updated"}))
      (either/left {:status 404 :error "User not found"}))))

(defn delete-user [id]
  (let [user (model/get-user-by-id id)]
    (if user
      (let [result (model/delete-user id)]
        (if (= 1 (:delete-count result))
          (either/right {:status 200 :body "User deleted"})
          (either/left {:status 400 :error "Failed to delete user"}))
        )
      (either/left {:status 404 :error "User not found"}))))