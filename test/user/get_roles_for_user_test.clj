(ns user.get-roles-for-user-test
  (:require [clojure.test :refer :all]
            [iam-clj-api.user.model :as user-model]
            [iam-clj-api.role.model :as role-model]
            [iam-clj-api.user.controller :as controller]))

(defn setup [f]
  (role-model/drop-role-table)
  (role-model/create-role-table)
  (role-model/insert-role {:name "role1" :description "description1"})
  (role-model/insert-role {:name "role2" :description "description2"})
  (role-model/insert-role {:name "role3" :description "description3"})
  (user-model/drop-user-table)
  (user-model/create-user-table)
  (user-model/insert-user {:username "user1" :email "user@example.com" :password "Password1!"})
  (f))

(use-fixtures :each setup)

(deftest test-get-roles-for-user
  (testing "Get roles for user"
    (let [user (user-model/get-user-by-username "user1")]
      (role-model/add-role-to-user 1 1)
      (role-model/add-role-to-user 2 1)
      (role-model/add-role-to-user 3 1)
      (is (= {:status 200, :body [{:id 1, :name "role1", :description "description1"}
                                  {:id 2, :name "role2", :description "description2"}
                                  {:id 3, :name "role3", :description "description3"}]}
             (controller/get-roles-for-user (get user :id))))))

  (testing "Get roles for user with invalid user id"
    (let [invalid-user (controller/get-roles-for-user 100)]
      (is (= {:status 404, :error "User not found"} invalid-user))))

  (testing "Get roles for user with no roles"
    (user-model/insert-user {:username "user2" :email "user2@example.com" :password "Password1!"})
    (is (= {:status 200, :body []}
           (controller/get-roles-for-user 2)))))