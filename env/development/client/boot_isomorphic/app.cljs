(ns boot-isomorphic.app
  (:require [mount.core :as mount]
            [boot-isomorphic.core]))

(enable-console-print!)

(.addEventListener js/window "error" #(js/console.error %))

(mount/start)

