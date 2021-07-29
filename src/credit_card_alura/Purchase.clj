(ns credit_card-alura.Purchase
  (:require [credit-card-alura.Card :as cards]))

(use 'java-time)

(def purchases [])

(defn get-purchases
  ([card] (filter #(and (= card (:card %)) (= false (:pay %))) purchases))
  ([] purchases))

(defn purchases-value
  ([card]
   (reduce + (map #(+ 0 (:value %)) (get-purchases card))))
  ([_ sales]
   (reduce + (map #(+ 0 (:value %)) sales))))

(defn has-limit? [card-number value]
  (let [purchases-value (purchases-value card-number)
        total-limit (cards/get-limit card-number)]
    (<= 0 (- total-limit (+ value purchases-value)))))

(defn new-purchase
  [card-number date value establishment category]
  (if (has-limit? card-number value)
    (def purchases (conj purchases
                         {:card     card-number, :date date, :value value,
                          :category category, :establishment establishment
                          :pay      false}))
    (println "Sem Limite ou cartão não encontrado")))

(defn group-by-category
  ([collection] (group-by :category collection))
  ([collection category] (group-by :category (filter #(= category (:category %)) collection))))


(defn map-total-by-category
  [[category itens]]
  {:category category
    :total (purchases-value "" itens)})

(defn total-by-category
  [card-numer]
  (map #(map-total-by-category %)  (group-by-category (get-purchases card-numer))))

(defn get-month [date] (as date :month-of-year))

(defn purchases-by-month
  ([month]
   (filter #(= month (get-month (:date %))) purchases))
  ([month sales]
   (filter #(= month (get-month (:date %))) sales)))

(defn between [min max v]
  (and (<= v max) (>= v min )))

(defn purchases-by-value
  ([value]
   (sort-by #(:value %) (filter #(= value (:value %)) purchases)))
  ([min max]
   (sort-by #(:value %) (filter #(between min max (:value %)) purchases))))

(defn purchases-by-establishment
  ([value]
   (filter #(= value (:establishment %)) purchases)))

(defn invoice
  ([card-number]
    (let [actual-month (get-month (local-date))
         sales (purchases-by-month actual-month (get-purchases card-number))]
     {:card-number   card-number
      :invoice-value (purchases-value "" sales)}))
  ([card-number month]
    (let [sales (purchases-by-month month (get-purchases card-number))]
     {:card-number   card-number
      :invoice-value (purchases-value "" sales)})))