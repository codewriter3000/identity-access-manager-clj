(ns role.get-role-by-id-test
  (:require [clojure.test :refer :all]
            [iam-clj-api.role.controller :as controller]
            [iam-clj-api.role.model :as model]
            [lib.core :refer :all]))

(defn setup [f]
    (model/drop-role-table)
    (model/create-role-table)
    (model/insert-role {:name "role1" :description "description1"})
    (model/insert-role {:name "role2" :description "description2"})
    (model/insert-role {:name "role3" :description "description3"})
    (model/insert-role {:name "role4" :description "description4"})
    (model/insert-role {:name "role5" :description "description5"})
    (f))

(use-fixtures :each setup)

(deftest test-get-role-by-id
    (testing "Get role by id"
        (is (= {:status 200 :body {:id 1 :name "role1" :description "description1"}}
               (controller/get-role-by-id 1)))
        (is (= {:status 200 :body {:id 2 :name "role2" :description "description2"}}
               (controller/get-role-by-id 2)))
        (is (= {:status 200 :body {:id 3 :name "role3" :description "description3"}}
               (controller/get-role-by-id 3)))
        (is (= {:status 200 :body {:id 4 :name "role4" :description "description4"}}
               (controller/get-role-by-id 4)))
        (is (= {:status 200 :body {:id 5 :name "role5" :description "description5"}}
               (controller/get-role-by-id 5)))
    )

    (testing "Get role by id with invalid id"
        (is (= {:status 404 :error "Role not found"}
               (controller/get-role-by-id 100)))))