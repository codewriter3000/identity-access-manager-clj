(ns permission.insert-permission-test
  (:require [clojure.test :refer :all]
            [iam-clj-api.permission.model :as model]
            [iam-clj-api.permission.controller :as controller]
            [lib.core :refer :all]))

(defn setup [f]
  (model/drop-permission-table)
  (model/create-permission-table)
  (f))

(use-fixtures :each setup)

(deftest test-insert-permission
  (testing "Insert permission"
    (is (= {:status 201 :body "Permission created successfully"}
           (controller/insert-permission "permission1" "description1")))
    (is (= {:status 201 :body "Permission created successfully"}
           (controller/insert-permission "permission2" "description2")))
    (is (= {:status 201 :body "Permission created successfully"}
           (controller/insert-permission "permission3" "description3")))
    (is (= {:status 201 :body "Permission created successfully"}
           (controller/insert-permission "permission4" "description4"))))
  (testing "Insert permission with missing description"
    (is (= {:status 201 :body "Permission created successfully"}
           (controller/insert-permission "permission5" ""))))
  (testing "Inserting permission with duplicate name"
    (is (= {:status 400 :error "Permission with name permission1 already exists"}
           (controller/insert-permission "permission1" "")))))