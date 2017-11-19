(ns boot-isomorphic.core
  (:require [cljs.nodejs :as nodejs]
            [boot-isomorphic.hello :refer [hello]]
            [hiccups.runtime]
            [boot-isomorphic.env :refer [env]]
            [mount.core :refer [defstate]])
  (:require-macros [hiccups.core :refer [defhtml]]))

(defhtml master [app]
  [:html
   [:head
    [:title "Hello World!"]]
   [:body
    app
    [:script {:src "/js/app.js"}]]])

(defn hello-world [req res]
  (.send res (master [:h1 (hello "World!")])))

(defn start-server [port]
  (-> ((nodejs/require "express"))
      (.use ((nodejs/require "serve-static") (:document-root @env)))
      (.get "/" hello-world)
      (.listen port #(println "Listening on port" port))))

(defstate server
  :start (start-server 3000)
  :stop  (.close @server))

