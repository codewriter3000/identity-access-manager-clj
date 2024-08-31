(ns permission.update-permission-description-test
  (:require [clojure.test :refer :all]
            [iam-clj-api.permission.controller :as controller]
            [iam-clj-api.permission.model :as model]))

(defn setup [f]
  (model/drop-permission-table)
  (model/create-permission-table)
  (model/insert-permission {:name "test-permission" :description "test description"})
  (f))

(use-fixtures :each setup)


(deftest test-update-permission-description
  (testing "update-permission-description with valid id and new description"
    (let [permission (model/get-permission-by-id 1)
          new-description "New description"]
      (is (= {:status 200 :body "Permission description updated"}
             (controller/update-permission-description 1 new-description)))))

  (testing "update-permission-description with invalid id"
    (let [permission (model/get-permission-by-id 1)
          new-description "New description"]
      (is (= {:status 400 :error "Failed to update permission description"}
             (controller/update-permission-description 100 new-description)))))

  (testing "update-permission-description with empty new description"
    (let [permission (model/get-permission-by-id 1)
          new-description ""]
      (is (= {:status 200 :body "Permission description updated"}
             (controller/update-permission-description 1 new-description))))))