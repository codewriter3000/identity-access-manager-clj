(defproject iam-clj-api "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [compojure "1.6.1"]
                 [ring/ring-defaults "0.3.2"]
                 [ring-cors/ring-cors "0.1.9"]
                 [com.github.seancorfield/next.jdbc "1.3.939"]
                 [org.postgresql/postgresql "42.7.4"]
                 [buddy/buddy-hashers "1.4.0"]
                 [environ "1.2.0"]
                 [org.clojure/tools.logging "1.3.0"]
                 [funcool/cats "2.3.0"]
                 [environ "1.2.0"]]
  :plugins [[lein-ring "0.12.5"]
            [lein-environ "1.2.0"]]
  :ring {:handler iam-clj-api.handler/app}
  :profiles {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                                  [ring/ring-mock "0.3.2"]]}})
