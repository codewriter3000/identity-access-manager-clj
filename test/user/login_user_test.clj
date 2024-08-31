(ns user.login-user-test
  (:require [clojure.test :refer :all]
            [iam-clj-api.user.controller :refer :all]
            [iam-clj-api.user.model :as model]
            [buddy.hashers :as hashers]))

(defn setup [f]
  (model/drop-user-table)
  (model/create-user-table)
  (let [password-hash (hashers/derive "Password1!")
        user {:username "test" :email "test@example.com" :password password-hash}]
    (model/insert-user user)
  (f)))

(use-fixtures :each setup)

(deftest test-login-user
  (testing "Login of user"
    (let [result (login-user "test" "Password1!")]
      (is (=  {:status 200 :body "Login successful"} result)))))

(run-tests)