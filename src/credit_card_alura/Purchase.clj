(ns credit_card-alura.Purchase
  (:require [credit-card-alura.Card :as cards]))

(use 'java-time)


(defn get-purchases
  ([card-number purchases] (filter #(= card-number (:card-number %)) purchases))
  ([purchases] purchases))

(defn purchases-value
  ([card-number purchases]
   (reduce + (map #(+ 0 (:value %)) (get-purchases card-number purchases)))))

(defn has-limit? [card value purchases]
  (let [purchases-value (purchases-value (:number card) purchases)
        total-limit (:limit card)]
    (<= 0 (- total-limit (+ value purchases-value)))))

(defn new-purchase
  [date value establishment category card purchases]
  (if (has-limit? card value purchases)
    (conj purchases
          {:card-number     (:number card), :date date, :value value,
           :category category, :establishment establishment
           :pay      false})
    (println "Sem Limite ou cartão não encontrado")))

(defn group-by-category
  ([collection purchases] (group-by :category collection))
  ([collection category purchases] (group-by :category (filter #(= category (:category %)) collection))))


(defn map-total-by-category
  [[category itens]]
  {:category category
    :total (purchases-value "" itens)})

(defn total-by-category
  [card purchases]
  (map #(map-total-by-category %)  (group-by-category (get-purchases (:number card) purchases) purchases)))

(defn get-month [date] (as date :month-of-year))

(defn purchases-by-month
  ([month purchases]
   (filter #(= month (get-month (:date %))) purchases)))

(defn between [min max v]
  (and (<= v max) (>= v min )))

(defn purchases-by-value
  ([value purchases]
   (sort-by #(:value %) (filter #(= value (:value %)) purchases)))
  ([min max purchases]
   (sort-by #(:value %) (filter #(between min max (:value %)) purchases))))

(defn purchases-by-establishment
  ([value purchases]
   (filter #(= value (:establishment %)) purchases)))

(defn invoice
  ([card-number purchases]
    (let [actual-month (get-month (local-date))
         sales (purchases-by-month actual-month (get-purchases card-number purchases))]
     {:card-number   card-number
      :invoice-value (purchases-value card-number sales)}))
  ([card-number month purchases]
    (let [sales (purchases-by-month month (get-purchases card-number purchases))]
     {:card-number   card-number
      :invoice-value (purchases-value card-number sales)})))