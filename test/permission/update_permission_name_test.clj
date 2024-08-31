(ns permission.update-permission-name-test
  (:require [clojure.test :refer :all]
            [iam-clj-api.permission.model.core :as model]
            [iam-clj-api.permission.controller :as controller]))

(defn setup [f]
  (model/drop-permission-table)
  (model/create-permission-table)
  (model/insert-permission {:name "test-permission" :description "test description"})
  (f))

(use-fixtures :each setup)

(deftest test-update-permission-name []
    (testing "Update permission name"
        (let [permission (model/get-permission-by-name "test-permission")]
        (is (= "test-permission" (get permission :name)))
        (controller/update-permission-name (get permission :id) "new-test-permission")
        (let [updated-permission (model/get-permission-by-name "new-test-permission")]
            (is (= "new-test-permission" (get updated-permission :name))))))
    )

(deftest test-update-permission-name-with-empty-name []
  (testing "Update permission name with empty name"
    (let [permission (model/get-permission-by-name "test-permission")]
      (is (= "test-permission" (get permission :name)))
      (is (= {:status 400 :error "Missing new name"} (controller/update-permission-name (get permission :id) ""))))))

(deftest test-update-permission-name-with-invalid-id []
  (testing "Update permission name with invalid id"
    (let [permission (model/get-permission-by-name "test-permission")]
      (is (= "test-permission" (get permission :name)))
      (is (= {:status 400 :error "Failed to update permission name"} (controller/update-permission-name 100 "new-test-permission"))))))