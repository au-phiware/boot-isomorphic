(ns boot-isomorphic.core
  (:require [mount.core :refer [defstate]]
            [boot-isomorphic.hello :refer [hello]]
            [pushy.core :as pushy]))

(defn hello-world [world] (js/alert (hello world)))

(defstate history
  :start (pushy/push-state! hello-world identity)
  :stop  (pushy/stop! @history))

