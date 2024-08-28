(ns user.update-user-username-test
  (:require [clojure.test :refer :all]
            [iam-clj-api.user.controller.core :refer :all]
            [iam-clj-api.user.model.core :as model]))

(defn setup [f]
  (model/drop-user-table)
  (model/create-user-table)
  (model/insert-user {:username "test1" :email "test1@example.com" :password "Password1!"})
  (f))

(use-fixtures :each setup)

(deftest test-update-user-username
  (testing "Update user username"
    (let [result (update-user-username 1 "new-username")]
      (is (= {:status 200 :body "Username updated"} result)))))