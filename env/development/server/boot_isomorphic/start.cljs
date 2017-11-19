(ns ^:figwheel-always boot-isomorphic.start
  (:require [cljs.nodejs :as nodejs]
            [mount.core :as mount]
            [boot-isomorphic.core]))

(nodejs/enable-util-print!)

(.on js/process "uncaughtException" #(js/console.error %))

(set! *main-cli-fn* #(-> (mount/swap
                          {#'boot-isomorphic.env/env {:document-root "target/dev/client"}})
                        mount/start))

