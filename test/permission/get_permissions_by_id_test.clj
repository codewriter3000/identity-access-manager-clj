(ns permission.get-permissions-by-id-test
  (:require [clojure.test :refer :all]
            [iam-clj-api.permission.controller.core :as controller]
            [iam-clj-api.permission.model.core :as model]
            [lib.core :refer :all]))

(defn setup [f]
    (model/drop-permission-table)
    (model/create-permission-table)
    (model/insert-permission {:name "permission1" :description "description1"})
    (model/insert-permission {:name "permission2" :description "description2"})
    (model/insert-permission {:name "permission3" :description "description3"})
    (model/insert-permission {:name "permission4" :description "description4"})
    (model/insert-permission {:name "permission5" :description "description5"})
    (model/insert-permission {:name "permission6" :description "description6"})
    (model/insert-permission {:name "permission7" :description "description7"})
    (model/insert-permission {:name "permission8" :description "description8"})
    (model/insert-permission {:name "permission9" :description "description9"})
    (model/insert-permission {:name "permission10" :description "description10"})
    (f))

(use-fixtures :each setup)

(deftest test-get-permission-by-id
  (testing "Get permission by id"
    (is (= {:status 200 :body {:id 1 :name "permission1" :description "description1"}}
           (controller/get-permission-by-id 1)))
    (is (= {:status 200 :body {:id 2 :name "permission2" :description "description2"}}
           (controller/get-permission-by-id 2)))
    (is (= {:status 200 :body {:id 3 :name "permission3" :description "description3"}}
           (controller/get-permission-by-id 3)))
    (is (= {:status 200 :body {:id 4 :name "permission4" :description "description4"}}
           (controller/get-permission-by-id 4)))
    (is (= {:status 200 :body {:id 5 :name "permission5" :description "description5"}}
           (controller/get-permission-by-id 5)))
    (is (= {:status 200 :body {:id 6 :name "permission6" :description "description6"}}
           (controller/get-permission-by-id 6)))
    (is (= {:status 200 :body {:id 7 :name "permission7" :description "description7"}}
           (controller/get-permission-by-id 7)))
    (is (= {:status 200 :body {:id 8 :name "permission8" :description "description8"}}
           (controller/get-permission-by-id 8)))
    (is (= {:status 200 :body {:id 9 :name "permission9" :description "description9"}}
           (controller/get-permission-by-id 9)))
    (is (= {:status 200 :body {:id 10 :name "permission10" :description "description10"}}
           (controller/get-permission-by-id 10))))

  (testing "Get permission by id with invalid id"
    (is (= {:status 404 :error "Permission not found"}
           (controller/get-permission-by-id 100)))))