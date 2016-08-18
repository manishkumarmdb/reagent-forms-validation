(ns reagent-forms-validation.core
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent-forms.core :refer [bind-fields]]
            [bouncer.core :as bouncer]
            [bouncer.validators :as validators]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [accountant.core :as accountant]))

;;--------------------------------------------------------------------
;; creating form template to represent our form
;; craete row
(defn row [label input]
  [:dev.row
   [:div.col-md-2 [:label label]]
   [:div.col-md-5 input]])

;; create form-template
(def form-template
  [:div
   (row "first name :"
        [:input.form-control {:field :text :id :first-name}])
   (row "last name :"
        [:input.form-control {:field :text :id :last-name}])
   (row "age :"
        [:input.form-control {:field :numeric :id :age}])
   (row "email :"
        [:input.form-control {:field :email :id :email}])
   (row "comments :"
        [:textarea.form-control {:field :textarea :id :comments}])
   ])

(defn form []
  (let [doc (atom {})]
    (fn []
      [:div
       [:div.page-header [:h4 "Reagent Form"]]
       [bind-fields form-template doc]
       [:label (str @doc)]])))

;;--------------------------------------------------------------------
;; Views

(defn home-page []
  [:div [:h2 "Welcome to reagent-forms-validation"]
   [:p "This is a "
    [:a {:href "https://github.com/reagent-project/reagent-forms"} "reagent-forms"]
    " workshop. In this workshop, we're doing validation using "
    [:a {:href "https://github.com/leonardoborges/bouncer"} "bouncer"]
    ". bouncer is a validation DSL for "
    [:a {:href "https://github.com/clojure/clojure"} "clojure"]
    " and "
    [:a {:href "https://github.com/clojure/clojurescript"} "clojurescript"]
    " application. please "
    [:a {:href "/reagent-forms"} "click here"] "."]])

(defn form-page []
  [:div
   [:div [:a {:href "/"} "home page"]]
   [:div [:h2 "validating using bouncer in reagent-forms"]
    [form]]])

(defn current-page []
  [:div [(session/get :current-page)]])

;; -------------------------
;; Routes

(secretary/defroute "/" []
  (session/put! :current-page #'home-page))

(secretary/defroute "/reagent-forms" []
  (session/put! :current-page #'form-page))

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (accountant/configure-navigation!
    {:nav-handler
     (fn [path]
       (secretary/dispatch! path))
     :path-exists?
     (fn [path]
       (secretary/locate-route path))})
  (accountant/dispatch-current!)
  (mount-root))
