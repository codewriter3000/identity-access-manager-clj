(ns iam-clj-api.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.cors :refer [wrap-cors]]
            [iam-clj-api.user.view :as user-view]
            [env :as env]
            [clojure.tools.logging :as log]))

(defn wrap-no-anti-forgery [handler]
  (wrap-defaults handler (assoc-in site-defaults [:security :anti-forgery] false)))

(def app-routes
  (routes
   (wrap-no-anti-forgery user-view/user-view-routes)
  (if (= (get env/_ :ENV) "test")
    (do
      (log/info "Running in test mode, disabling anti-forgery middleware.")
      (wrap-no-anti-forgery user-view/user-view-routes))
    (do
      (log/info "Running in non-test mode, enabling anti-forgery middleware.")
      (wrap-defaults user-view/user-view-routes site-defaults)))
   ))

(def app
  (wrap-cors app-routes
             :access-control-allow-origin [#"http://localhost:3000"]
             :access-control-allow-methods [:get :put :post :delete]))