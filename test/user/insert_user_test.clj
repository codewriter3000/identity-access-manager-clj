(ns user.insert-user-test
  (:require [clojure.test :refer :all]
            [iam-clj-api.user.controller.core :refer :all]
            [cats.monad.either :as either]
            [iam-clj-api.user.model.core :as model]))

(defn mock-get-user-by-username [username]
  (if (= username "existinguser")
    {:username "existinguser"}
    nil))

(defn setup [f]
  (model/drop-user-table)
  (model/create-user-table)
  (with-redefs [model/get-user-by-username mock-get-user-by-username]
    (f)))

(use-fixtures :each setup)

(deftest test-insert-user
  (testing "Insertion of user"
    (is (= (either/right {:status 201 :body "User created successfully"})
           (insert-user "newuser" "newuser@example.com" "Password1!")))))

(deftest test-validate-input
  (testing "Validation of input"
    (is (= (either/left {:status 400 :error "All fields are required"})
           (validate-input "" "test@example.com" "Password1!")))
    (is (= (either/left {:status 400 :error "Password must be at least 8 characters long, contain an uppercase letter, a lowercase letter, a number, and a special character"})
           (validate-input "username" "test@example.com" "password")))
    (is (= (either/left {:status 400 :error "Username must be between 3 and 20 characters"})
           (validate-input "ab" "test@example.com" "Password1!")))
    (is (= (either/left {:status 400 :error "Invalid email address"})
           (validate-input "username" "invalid-email" "Password1!")))
    (is (= (either/left {:status 400 :error "Username already exists"})
           (validate-input "existinguser" "test@example.com" "Password1!")))))

(run-tests)