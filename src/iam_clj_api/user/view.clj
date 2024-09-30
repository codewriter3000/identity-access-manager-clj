(ns iam-clj-api.user.view
  (:require [compojure.core :refer :all]
            [iam-clj-api.user.controller :as controller]
            [ring.middleware.json :as json]
            [ring.util.request :as request]))

(defroutes user-view-routes
  (context "/user" []
    (GET "/" [] (controller/get-all-users))
    (GET "/:id" [id] (controller/get-user-by-id id))
    ;(POST "/" {:body user}
    ;  (controller/insert-user user))
    (POST "/" request
      (let [user (get-in request [:body])]
        (controller/insert-user user)))
    (POST "/login" [username password]
      (controller/login-user username password))
    (PUT "/:id" request
      (let [id (get-in request [:params :id])
            user (get-in request [:body])]
        (controller/update-user id user)
        {:status 200 :body "User updated"}))
    (PUT "/:id/username" [id new-username]
      (controller/update-user-username id new-username))
    (PUT "/:id/email" [id new-email]
      (controller/update-user-email id new-email))
    (PUT "/:id/password" [id new-password]
      (controller/update-user-password id new-password))
    (DELETE "/:id" [id] (controller/delete-user id))
    ;; User - Permission & User - Role
    (GET "/:id/permissions" [id] (controller/get-permissions-for-user id))
    (GET "/:id/roles" [id] (controller/get-roles-for-user id))))