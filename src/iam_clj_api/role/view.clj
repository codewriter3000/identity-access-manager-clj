(ns iam-clj-api.role.view
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [iam-clj-api.role.controller :as controller]))

(defroutes role-view-routes
  (context "/role" []
    (GET "/" [] (controller/get-all-roles))
    (GET "/:id" [id] (controller/get-role-by-id id))
    (POST "/" [name description] (controller/insert-role name description))
    (PUT "/:id/name" [id new-name] (controller/update-role-name id new-name))
    (PUT "/:id/description" [id new-description] (controller/update-role-description id new-description))
    (DELETE "/:id" [id] (controller/delete-role id))
    ;; User - Roles
    (GET "/:id/user" [id] (controller/get-users-with-role id))
    (POST "/:id/user/:user-id" [id user-id] (controller/add-role-to-user id user-id))
    (DELETE "/:id/user/:user-id" [id user-id] (controller/remove-role-from-user id user-id))
    ;; Role - Permissions
    (GET "/:id/permission" [id] (controller/get-permissions-for-role id))
    (POST "/:id/permission/:permission-id" [id permission-id] (controller/add-permission-to-role id permission-id))
    (DELETE "/:id/permission/:permission-id" [id permission-id] (controller/remove-permission-from-role id permission-id))))