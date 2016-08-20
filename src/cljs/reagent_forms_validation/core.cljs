(ns reagent-forms-validation.core
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent-forms.core :refer [bind-fields]]
            [bouncer.core :as b]
            [bouncer.validators :as v]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [accountant.core :as accountant]))

;;--------------------------------------------------------------------
;; creating form template to represent our form
;; create row
(defn row [label input]
  [:div.row
   [:div.col-md-4 [:label label]]
   [:div.col-md-8 input]])

;; create input field
(defn input [label type id]
  (row label [:input.form-control {:field type :id id}]))

;; create form-template
(defn form-template [data]
  [:div.form-div
   [bind-fields (input "First Name :" :text :first-name) data]
   [:p.validation-error
    (first
     (first
      (b/validate @data
                  :first-name [[v/required :message "^*"]
                               [v/matches #"^[A-Za-z]+$"]])))]

   [bind-fields (input "Last Name :" :text :last-name) data]
   [:p.validation-error
    (first
     (first
      (b/validate @data
                  :last-name [[v/required :message "^*"]
                              [v/matches #"^[A-Za-z]+$"]])))]
   [bind-fields (input "Age :" :text :age) data]
   [:p.validation-error
    (first
     (first
      (b/validate @data
                  :age [[v/required :message "^*"]
                        [v/matches #"[0-9]+"]])))]
   [bind-fields (input "Email :" :email :email) data]
   [:p.validation-error
    (first
     (first
      (b/validate @data
                  :email [[v/required :message "^*"]
                          [v/email]])))]
   [bind-fields (input "Comments :" :textarea :comments) data]
   [:p.validation-error
    (first
     (first
      (b/validate @data
                  :comments [[v/required :message "^*"]
                             [v/string]])))]

   ;; (input "First Name :" :text :first-name)
   ;; (input "Last Name :" :text :last-name)
   ;; (input "Age :" :numeric :age)
   ;; (input "Email :" :email :email)
   ;; (input "Comments :" :textarea :comments)
   ])

(defn form []
  (let [doc (atom {})]
    (fn []
      [:div.form-group
       [:div.page-header [:h4 "Reagent Form"]]
       [form-template doc]
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

;;--------------------------------------------------------------------

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
