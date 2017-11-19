(ns boot-isomorphic.start
  (:require
    [cljs.nodejs :as nodejs]
    [mount.core :as mount]
    [boot-isomorphic.core]
    #_[doo.runner :refer-macros [doo-tests]]
    #_[boot-isomorphic.core-test]))

#_(doo-tests 'boot-isomorphic.core-test)

(.install (nodejs/require "source-map-support") #js {"environment" "node"})

(nodejs/enable-util-print!)

(let [doc-root (.resolve (nodejs/require "path")
                         js/__dirname ".." ".." "client")]
  (set! *main-cli-fn* #(-> (mount/swap
                            {#'boot-isomorphic.env/env
                             {:document-root doc-root}})
                          mount/start)))
