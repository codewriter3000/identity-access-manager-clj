(ns lib.response)

(defn error [status msg]
  {:status status :error (str "{\"error\": \"" msg "\"}")})

;(defn error [status msg field]
 ; {:status status :error (str "{\"error\": \"" msg "\", \"field\": \"" field "\"}")})

(defn success [status msg]
  {:status status :body (str "{\"message\": \"" msg "\"}")})

(defn work [status data]
  {:status status :body data})