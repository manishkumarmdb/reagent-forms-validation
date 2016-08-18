(ns reagent-forms-validation.prod
  (:require [reagent-forms-validation.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
