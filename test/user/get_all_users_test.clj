(ns user.get-all-users-test
  (:require [clojure.test :refer :all]
            [cats.monad.either :as either]
            [iam-clj-api.user.controller.core :refer :all]
            [iam-clj-api.user.model.core :as model]))

(defn setup [f]
  (model/drop-user-table)
  (model/create-user-table)
  (model/insert-user {:username "test1" :email "test1@example.com" :password "Password1!"})
  (model/insert-user {:username "test2" :email "test2@example.com" :password "Password1!"})
  (f))

(use-fixtures :each setup)

(deftest test-get-all-users
  (testing "Get all users"
    (let [expected-users [{:id 1 :username "test1" :email "test1@example.com"}
                          {:id 2 :username "test2" :email "test2@example.com"}]
          result (get-all-users)]
      (is (= (either/right {:status 200 :body expected-users}) result)))))

(run-tests)