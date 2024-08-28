(ns role.delete-role-test
  (:require [clojure.test :refer :all]
            [iam-clj-api.role.controller :as controller]
            [iam-clj-api.role.model :as model]))

(defn setup [f]
    (model/drop-role-table)
    (model/create-role-table)
    (model/insert-role {:name "role1" :description "description1"})
    (f))

(use-fixtures :each setup)

(deftest test-delete-role
  (testing "Delete role"
    (let [role (model/get-role-by-id 1)]
      (is (= "role1" (get role :name)))
      (controller/delete-role (get role :id))
      (is (= {:status 404 :error "Role not found"} (controller/get-role-by-id (get role :id)))))))