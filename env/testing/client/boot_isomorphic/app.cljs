(ns boot-isomorphic.app
  (:require
    [mount.core :as mount]
    [boot-isomorphic.core]
    #_[doo.runner :refer-macros [doo-tests]]
    #_[boot-isomorphic.core-test]))

#_(doo-tests 'boot-isomorphic.core-test)

(enable-console-print!)

(mount/start)

