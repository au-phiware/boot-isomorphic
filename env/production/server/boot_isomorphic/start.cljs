(ns boot-new.start
  (:require [cljs.nodejs :as nodejs]
            [mount.core :as mount]
            [boot-new.core]))

(nodejs/enable-util-print!)

(let [doc-root (.resolve (nodejs/require "path")
                         js/__dirname ".." ".." "client")]
  (set! *main-cli-fn* #(-> (mount/swap
                            {#'boot-new.env/env
                             {:document-root doc-root}})
                          mount/start)))

