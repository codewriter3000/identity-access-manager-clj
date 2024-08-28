(ns role.get-all-roles-test
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

(deftest test-get-all-roles
  (testing "Get all roles"
    (is (= {:status 200 :body [{:id 1 :name "role1" :description "description1"}
                               {:id 2 :name "role2" :description "description2"}
                               {:id 3 :name "role3" :description "description3"}
                               {:id 4 :name "role4" :description "description4"}
                               {:id 5 :name "role5" :description "description5"}]}
           (controller/get-all-roles)))))