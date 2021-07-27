(ns credit-card-alura.Card
  (:require [credit-card-alura.Client :as clients]))
(use 'java-time)

(def cards [])


(defn exist-client? [cpf] (clients/get-client cpf))

(defn new-card [number cpf cvv validate limit]
  (if (exist-client? cpf)
    (def cards (conj cards  { :cpf cpf, :number number, :cvv cvv, :validate validate :limit limit :actual-limit limit}))
    (println "Cliente não encontrado"))
  )

(defn get-client-cards [cpf]
  (filter #(= cpf (:cpf %)) cards))

(defn get-limit [number]
  (get (first (filter #(= number (:number %)) cards)) :limit 0))

(defn get-actual-limit [number]
  (get (first (filter #(= number (:number %)) cards)) :actual-limit 0))


(defn update-limit [number value]
  (update (first (filter #(= number (:number %)) cards)) :limit value))





