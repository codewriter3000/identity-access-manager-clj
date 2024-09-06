(ns user.get-all-users-test
  (:require [clojure.test :refer :all]
            [iam-clj-api.user.controller :refer :all]
            [iam-clj-api.user.model :as model]))

(defn setup [f]
  (model/drop-user-table)
  (model/create-user-table)
  (model/insert-user {:username "test1" :email "test1@example.com" :password "Password1!"})
  (model/insert-user {:username "test2" :email "test2@example.com" :password "Password1!" :first_name "Test" :last_name "User"})
  (f))

(use-fixtures :each setup)

(deftest test-get-all-users
  (testing "Get all users"
    (let [expected-users [{:id 1 :username "test1" :email "test1@example.com" :first_name nil :last_name nil}
                          {:id 2 :username "test2" :email "test2@example.com" :first_name "Test" :last_name "User"}]
          result (get-all-users)]
      (is (= {:status 200 :body expected-users} result)))))