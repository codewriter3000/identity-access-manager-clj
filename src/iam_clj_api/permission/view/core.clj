(ns iam-clj-api.permission.view.core
(:require [compojure.core :refer :all]
          [compojure.route :as route]
          [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
          [iam-clj-api.permission.controller.core :as controller]))

(defroutes permission-view-routes
  (context "/permission" []
    (GET "/" [] (controller/get-all-permissions))
    (GET "/:id" [id] (controller/get-permission-by-id id))
    (POST "/" [name description] (controller/insert-permission name description))
    (PUT "/:id/name" [id new-name] (controller/update-permission-name id new-name))
    (PUT "/:id/description" [id new-description] (controller/update-permission-description id new-description))
    (DELETE "/:id" [id] (controller/delete-permission id))
    ;; User - Permissions
    (GET "/:id/user" [id] (controller/get-users-with-permission id))
    (POST "/:id/user/:user-id" [id user-id] (controller/add-permission-to-user id user-id))
    (DELETE "/:id/user/:user-id" [id user-id] (controller/remove-permission-from-user id user-id))))
