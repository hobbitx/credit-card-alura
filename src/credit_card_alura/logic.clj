(ns credit-card-alura.logic
  (:require [credit-card-alura.model :as m]
            [schema.core :as s]))


(use 'java-time)
(s/defn create-client :- m/Client
        [name     :- s/Str,
         cpf      :- s/Str,
         email    :- s/Str]
 {:cpf cpf, :name name, :email email})

(s/defn get-client :- m/Client
  [cpf      :- s/Str
   clients  :- [m/Client]]
  (first (filter #(= cpf (:cpf %)) clients)))

(s/defn show-clients
  [clients  :- [m/Client]]
  (println clients))


(s/defn exist-client? :- m/Client
  [cpf      :- s/Str
   clients  :- [m/Client]
   ]
  (get-client cpf clients))

(s/defn new-card :- m/Card
  [cpf        :- s/Str,
   number     :- s/Str,
   cvv        :- s/Num,
   validate   :- s/Str,
   limit      :- s/Num]
  {:cpf cpf, :number number, :cvv cvv, :validate validate :limit limit :actual-limit limit})

(s/defn get-client-cards [cpf cards]
  (filter #(= cpf (:cpf %)) cards))

(s/defn select-cards :- m/Card
        [card-number  :- s/Str
         cards        :- [m/Card]
         ]
  (first (filter #(= card-number (:number %)) cards)))

(s/defn get-limit [card]
  (:limit card))

(s/defn get-purchases :- [m/Purchase]
  ([
    card-number :- s/Str
    purchases   :- [m/Purchase]]
   (filter #(= card-number (:card-number %)) purchases)))

(s/defn purchases-value :- m/ValorFinanceiro
  ([card-number purchases]
   (reduce + (map #(+ 0 (:value %)) (get-purchases card-number purchases)))))

(s/defn has-limit? :- s/Bool
  [
   card       :- m/Card
   value      :- m/ValorFinanceiro
   purchases  :- [m/Purchase]
   ]
  (let [purchases-value (purchases-value (:number card) purchases)
        total-limit (:limit card)]
    (<= 0 (- total-limit (+ value purchases-value)))))


(s/defn new-purchase :- m/Purchase
  [
   value            :- m/ValorFinanceiro,
   date             :- m/Date,
   establishment    :- s/Str,
   category         :- s/Str,
   card             :- m/Card,
   purchases        :- [m/Purchase]
   ]
  (let [total (- (:limit card) (purchases-value (:number card) purchases))]
    (if (and (has-limit? card value purchases) (> value 0))
      {
       :card-number   (str (:number card)),
       :date          date,
       :value         value,
       :category      category,
       :establishment establishment
       :pay           false
       }
      (if (> value 0)
        (throw (ex-info "" {:purchase-value value :limit total :cause :card-not-valid-limit }))
        (throw (ex-info "" {:purchase-value value :limit total :cause :purchase-value-not-valid}))
        )
      )))

(s/defn group-by-category
  ([
    collection :- [m/Purchase]
    ]
   (group-by :category collection))
  ([
    collection  :- [m/Purchase]
    category    :- s/Str
    ]
   (group-by :category (filter #(= category (:category %)) collection))))


(s/defn map-total-by-category
  [
   [category itens]
   card-number :- s/Str
   ]
  {:category category
   :total (purchases-value card-number itens)})

(s/defn total-by-category
  [card-number purchases]
  (map #(map-total-by-category % card-number)  (group-by-category (get-purchases card-number purchases))))

(defn get-month [date] (:month-of-year date))

(s/defn purchases-by-month :- [m/Purchase]
  ([month purchases]
   (filter #(= month (get-month (:date %))) purchases)))

(defn between [min max v]
  (and (<= v max) (>= v min )))

(s/defn filter-purchases :- [m/Purchase]
  ([value key purchases]
   (filter #(= value (key %)) purchases))
  ([min max key purchases]
   (filter #(between min max (key %)) purchases)))

(s/defn invoice :- m/Invoice
  ([
    card-number :- s/Str
    purchases   :- [m/Purchase]
    ]
   (let [actual-month (get-month (local-date))
         sales (purchases-by-month actual-month (get-purchases card-number purchases))]
     {:card-number   card-number
      :invoice-value (purchases-value card-number sales)}))
  ([
    card-number :- s/Str
    month       :- m/ValorFinanceiro
    purchases   :- [m/Purchase]]
   (let [sales (purchases-by-month month (get-purchases card-number purchases))]
     {:card-number   card-number
      :invoice-value (purchases-value card-number sales)})))