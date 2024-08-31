(ns role.remove-role-from-user-test
  (:require [clojure.test :refer :all]
            [iam-clj-api.role.model :as role-model]
            [iam-clj-api.user.model :as user-model]
            [iam-clj-api.role.controller :as controller]))

(defn setup [f]
  (role-model/drop-role-table)
  (role-model/create-role-table)
  (user-model/drop-user-table)
  (user-model/create-user-table)
  (role-model/insert-role {:name "admin" :description "admin role"})
  (user-model/insert-user {:username "admin" :email "adminuser@example.com" :password "Admin123!1"})
  (role-model/add-role-to-user 1 1)
  (f))

(use-fixtures :each setup)

(deftest test-remove-role-from-user []
  (testing "Remove role from user"
    (let [role (role-model/get-role-by-name "admin")
          user (user-model/get-user-by-username "admin")]
      (is (= "admin" (get role :name)))
      (is (= "admin" (get user :username)))
      (is (= 1 (count (user-model/get-roles-for-user (get user :id)))))
      (is (= {:status 200, :body "Role removed from user"}
             (controller/remove-role-from-user (get user :id) (get role :id))))
      (is (= 0 (count (user-model/get-roles-for-user (get user :id))))))))

(deftest test-remove-role-from-user-with-invalid-role-id []
    (testing "Remove role from user with invalid role id"
        (let [user (user-model/get-user-by-username "admin")]
        (is (= "admin" (get user :username)))
        (is (= {:status 400, :error "Failed to remove role from user"}
               (controller/remove-role-from-user (get user :id) 100))))))

(deftest test-remove-role-from-user-with-invalid-user-id []
    (testing "Remove role from user with invalid user id"
        (let [role (role-model/get-role-by-name "admin")]
        (is (= "admin" (get role :name)))
        (is (= {:status 400, :error "Failed to remove role from user"}
               (controller/remove-role-from-user 100 (get role :id)))))))