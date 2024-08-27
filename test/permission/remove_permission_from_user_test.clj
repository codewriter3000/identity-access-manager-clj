(ns permission.remove-permission-from-user-test
  (:require [clojure.test :refer :all]
            [iam-clj-api.permission.model.core :as perm-model]
            [iam-clj-api.permission.controller.core :as controller]
            [iam-clj-api.user.model.core :as user-model]))

(defn setup [f]
  (perm-model/drop-permission-table)
  (perm-model/create-permission-table)
  (user-model/drop-user-table)
  (user-model/create-user-table)
  (perm-model/insert-permission {:name "test-permission" :description "test description"})
  (user-model/insert-user {:username "testuser1" :email "testuser1@example.com" :password "Password1!"})
  (f))

(use-fixtures :each setup)

(deftest test-remove-permission-from-user []
  (testing "Remove permission from user"
    (let [permission (perm-model/get-permission-by-name "test-permission")
          user (user-model/get-user-by-username "testuser1")]
      (is (= "test-permission" (get permission :name)))
      (is (= "testuser1" (get user :username)))
      (controller/add-permission-to-user (get permission :id) (get user :id))
      (let [users (get (controller/get-users-with-permission (get permission :id)) :body)]
        (is (= 1 (count users)))
        (is (= "testuser1" (get (first users) :username)))
        (controller/remove-permission-from-user (get permission :id) (get user :id))
        (let [users (get (controller/get-users-with-permission (get permission :id)) :body)]
          (is (= 0 (count users))))))))

(deftest test-remove-permission-from-user-with-invalid-permission-id []
  (testing "Remove permission from user with invalid permission id"
    (let [user (user-model/get-user-by-username "testuser1")]
      (is (= "testuser1" (get user :username)))
      (is (= {:status 400, :error "Failed to remove permission from user"} (controller/remove-permission-from-user 100 (get user :id)))))))

(deftest test-remove-permission-from-user-with-invalid-user-id []
  (testing "Remove permission from user with invalid user id"
    (let [permission (perm-model/get-permission-by-name "test-permission")]
      (is (= "test-permission" (get permission :name)))
      (is (= {:status 400, :error "Failed to remove permission from user"} (controller/remove-permission-from-user (get permission :id) 100))))))