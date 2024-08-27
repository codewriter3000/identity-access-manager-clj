(ns user.delete-user-test
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

(deftest test-delete-user
  (testing "Delete user"
    (let [result (delete-user 1)]
      (is (= (either/right {:status 200 :body "User deleted"}) result))))
    (testing "Delete user that does not exist"
        (let [result (delete-user 2)]
            (is (= (either/left {:status 404 :error "User not found"}) result)))))
(run-tests)