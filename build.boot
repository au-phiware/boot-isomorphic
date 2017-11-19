(set-env!
  :dependencies '[[org.clojure/clojure "1.8.0"]
                  [org.clojure/clojurescript "1.9.946" :scope "test"]
                  [boot/core "2.7.2"]
                  [adzerk/boot-cljs "2.1.3" :scope "test"]

                  ; Common
                  [mount "0.1.11"]

                  ; Client
                  [kibu/pushy "0.3.8"]

                  ; Server
                  [hiccups "0.3.0"]

                  ; Test
                  [doo "0.1.7"]])

(require
  '[boot.pod :refer [make-pod with-eval-in]]
  '[adzerk.boot-cljs :refer [cljs]]
  '[clojure.set :as set]
  '[clojure.java.io :as io])

(def common-env
  {:source-paths #{"src/common"}})

(def common-compiler-options
  {:install-deps true
   :npm-deps     {:ws "3.3.1"
                  :express "4.15.4"
                  :serve-static "1.12.4"
                  :source-map-support "0.5.0"
                  }})

(defn source-paths
  "Creates source paths for the given target and configuration"
  [paths target config]
  (set/union #{(str "src/" (name target)) (str "env/" (name config) "/" (name target))} paths))

(defn resource-paths
  "Creates resource paths for the given target and configuration"
  [paths target config]
  (set/union #{(str "resources/" (name target))} paths))

(defn compiler-options
  "Creates cljs compiler options for the given configuration."
  ([config] (compiler-options common-compiler-options config))
  ([opts config]
   (case config
     :development
     (conj {:optimizations        :none
            :pretty-print         true
            :source-map           true
            :source-map-timestamp true}
           opts)
     :testing
     (conj {;:source-paths         "TODO"
            }
           (compiler-options :production opts))
     :production
     (conj {:optimizations        :advanced}
           opts)
     opts)))

(defn- new-env
  ([target config] (new-env (get-env) target config))
  ([env target config]
   (-> env
       (update :source-paths   source-paths   target config)
       (update :resource-paths resource-paths target config))))

(def target-to-id {:server "private/js/start" :client "public/js/app"})

(defn configure!
  ([config & targets]
   (let [config  (or config :development)
         targets (or targets [:server :client])
         ids     (into #{} (map target-to-id targets))
         env     (reduce #(new-env %1 %2 config)
                         common-env
                         targets)]
     (apply set-env! (flatten (into [] env)))
     (task-options!
       cljs {:ids ids :compiler-options (compiler-options config)}
       sift {:invert true :include #{#"\.cljs\.edn$"}}
       target {:dir #{(str "dist/" (name config))}}))))

(deftask configure
  "Sets boot env for the given configuration."
  [c config VAL kw "One of :production, :testing or :development"
   t target VAL [kw] "Any of :client :server"]
  (with-pass-thru _ (configure! config target)))

(deftask build
  "Compiles target for the given configuration."
  [t target VAL kw "One of :server, :client, :devcards"
   c config VAL kw "One of :production, :testing or :development"
   p pod      bool "Enable pods."]
  (let [config (or config :development)
        env    (new-env common-env target config)
        env    (conj (get-env) env)
        worker (if pod (make-pod env))
        opts   (compiler-options config)
        ids    #{(target-to-id target)}
        dirs   #{(str "dist/" (name config))}]
    (if worker
      (with-eval-in worker
        (require '[boot.core :refer :all]
                 '[boot.util :refer :all]
                 '[boot.task.built-in :refer :all]
                 '[adzerk.boot-cljs :refer [cljs]])
        (info "Compiling with %s%n" (get-env))
        (boot (cljs :ids ~ids :compiler-options ~opts)
              (sift :invert true :include #{#"\.cljs\.edn$"})
              (target :dir ~dirs)))
      (do
        (configure! config target)
        (info "Compiling with %s%n" (get-env))
        (comp
          (cljs)
          (sift)
          )))))

(deftask once
  "Build target for the given configuration."
  [t target VAL [kw] "Target, one of :client or :server"
   c config VAL kw
   "Configuration, one of :production, :testing or :development (default)"]
  (let [config (or config :development)]
    (comp
      (build :config config :target :client)
      (build :config config :target :server))))
