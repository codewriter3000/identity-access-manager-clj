(ns iam-clj-api.user.view
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [iam-clj-api.user.controller :as controller]))

(defroutes user-view-routes
  (context "/user" []
    (GET "/" [] (controller/get-all-users))
    (GET "/:id" [id] (controller/get-user-by-id id))
    (POST "/" [username email password]
      (controller/insert-user username email password))
    (POST "/login" [username password]
      (controller/login-user username password))
    (PUT "/:id/username" [id new-username]
      (controller/update-user-username id new-username))
    (PUT "/:id/email" [id new-email]
      (controller/update-user-email id new-email))
    (PUT "/:id/password" [id new-password]
      (controller/update-user-password id new-password))
    (DELETE "/:id" [id] (controller/delete-user id))
    ;; User - Permission & User - Role
    (GET "/:id/permissions" [id] (controller/get-permissions-for-user id))
    (GET "/:id/roles" [id] (controller/get-roles-for-user id))
    ))