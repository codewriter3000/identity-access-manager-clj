(ns user.update-user-email-test
  (:require [clojure.test :refer :all]
            [iam-clj-api.user.controller.core :refer :all]
            [cats.monad.either :as either]
            [iam-clj-api.user.model.core :as model]))

(defn setup [f]
    (model/drop-user-table)
    (model/create-user-table)
    (model/insert-user {:username "test1" :email "test1@example.com" :password "Password1!"})
    (f))

(use-fixtures :each setup)

(deftest test-update-user-email
  (testing "Update user email"
    (let [result (update-user-email 1 "newemail@example.com")]
      (is (= (either/right {:status 200 :body "Email updated"}) result)))))