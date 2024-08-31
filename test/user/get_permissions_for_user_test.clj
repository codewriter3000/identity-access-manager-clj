(ns user.get-permissions-for-user-test
  (:require [clojure.test :refer :all]
            [iam-clj-api.user.model :as user-model]
            [iam-clj-api.permission.model :as permission-model]
            [iam-clj-api.user.controller :as controller]))

(defn setup [f]
  (permission-model/drop-permission-table)
  (permission-model/create-permission-table)
  (permission-model/insert-permission {:name "permission1" :description "description1"})
  (permission-model/insert-permission {:name "permission2" :description "description2"})
  (permission-model/insert-permission {:name "permission3" :description "description3"})
  (user-model/drop-user-table)
  (user-model/create-user-table)
  (user-model/insert-user {:username "user1" :email "user@example.com" :password "Password1!"})
  (f))

(use-fixtures :each setup)

(deftest test-get-permissions-for-user
  (testing "Get permissions for user"
    (permission-model/add-permission-to-user 1 1)
    (permission-model/add-permission-to-user 2 1)
    (permission-model/add-permission-to-user 3 1)
    (is (= {:status 200, :body [{:id 1, :name "permission1", :description "description1"}
                                {:id 2, :name "permission2", :description "description2"}
                                {:id 3, :name "permission3", :description "description3"}]}
           (controller/get-permissions-for-user 1))))

  (testing "Get permissions for user with invalid user id"
    (let [invalid-user (controller/get-permissions-for-user 100)]
      (is (= {:status 404, :error "User not found"} invalid-user))))

  (testing "Get permissions for user with no permissions"
    (user-model/insert-user {:username "user2" :email "user2@example.com" :password "Password1!"})
    (let [user (user-model/get-user-by-username "user2")]
      (let [permissions (get (controller/get-permissions-for-user (get user :id)) :body)]
        (is (= 0 (count permissions)))))))