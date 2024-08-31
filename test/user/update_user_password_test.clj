(ns user.update-user-password-test
  (:require [clojure.test :refer :all]
            [iam-clj-api.user.controller :refer :all]
            [iam-clj-api.user.model :as model]
            [buddy.hashers :as hashers]))

(defn setup [f]
  (model/drop-user-table)
  (model/create-user-table)
  (model/insert-user {:username "test1" :email "test1@example.com" :password "Password1!"})
  (f))

(use-fixtures :each setup)

(deftest test-update-user-password
  (testing "Update user password"
    (let [result (update-user-password 1 "Password2!")]
      (is (= {:status 200 :body "Password updated"} result)))))