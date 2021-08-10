(ns credit-card-alura.logic
  (:require [credit-card-alura.model :as m]
            [schema.core :as s]
            [credit-card-alura.database :as db]
            [clojure.pprint :as p]))

(use 'java-time)
(def conn (db/create-conn))
(db/create-schemas conn)


(s/defn create-client :- m/Client
  [name :- s/Str,
   cpf :- s/Str,
   email :- s/Str]
  (let [client {:consumer/cpf cpf, :consumer/name name, :consumer/email email}
        result @(db/add conn [client])]
    {:cpf cpf, :name name, :email email}))


(s/defn get-client
  [cpf :- s/Str]
  (first (db/get-client-by-cpf (db/get-db conn) cpf)))

(s/defn show-clients
  []
  (let [clients (db/get-clients (db/get-db conn))]
    (p/pprint clients))
  )

(s/defn get-clients
  ([]
   (db/get-clients (db/get-db conn))))


(s/defn new-card
  [id :- s/Num,
   number :- s/Str,
   cvv :- s/Num,
   validate :- s/Str,
   limit :- s/Num]
  (let [card {:card/consumer-id id
              :card/number      number
              :card/cvv         cvv
              :card/validate    validate
              :card/limit       limit}
        result @(db/add conn [card])]
    result))

(s/defn get-client-cards [cpf cards]
  (filter #(= cpf (:cpf %)) cards))
(defn get-cards []
  (db/get-cards (db/get-db conn)))

(s/defn select-cards
  [card-number :- s/Str]
  (first (first (db/get-card-by-number (db/get-db conn) card-number))))

(defn get-limit [card]
  (let [purchases-value (purchases-value (:db/id card))
        total-limit (:card/limit card)
        ]
    (- total-limit purchases-value)))


(defn get-purchases
  ([card]
   (map #(first %) (db/get-purchases-by-card (db/get-db conn) card)))
  ([]
   (map #(first %) (db/get-purchases (db/get-db conn)))))

(s/defn purchases-value :- m/Money
  ([card :- s/Num]
   (let [purchases (get-purchases card)
         purchase-map (map #(+ 0 (:purchase/value %)) purchases)
         ]
     (if (nil? purchases)
       (+ 0 0)
       (do
         (reduce + purchase-map)))
     )
   ))

(s/defn has-limit? :- s/Bool
  [
   card :- m/Map
   value :- m/Money
   ]
  (let [purchases-value (purchases-value (:db/id card))
        total-limit (:card/limit card)
        actual-limit (- total-limit (+ value purchases-value))]
    (>= actual-limit 0)))


(s/defn new-purchase :- m/Purchase
  [
   value :- m/Money
   date :- m/StrNotBlank
   establishment :- m/StrNotBlank
   category :- m/StrNotBlank
   card :- m/Map
   ]
  (let [total value
        purchase {
                  :purchase/uuid          (m/uuid)
                  :purchase/card          card,
                  :purchase/date          date,
                  :purchase/value         value,
                  :purchase/category      category,
                  :purchase/establishment establishment
                  :purchase/pay           false
                  }
        ]
    (if (> value 0)
      (if (has-limit? card value)
        (do @(db/add conn [purchase])
            {
             :purchase/card          card,
             :purchase/date          date,
             :purchase/value         value,
             :purchase/category      category,
             :purchase/establishment establishment
             :purchase/pay           false
             :purchase/uuid          (:purchase/uuid purchase)
             }
            )
        (throw (ex-info "" {:purchase-value value :limit total :card card :cause :card-not-valid-limit})))
      (throw (ex-info "" {:purchase-value value :limit total :cause :purchase-value-not-valid})))))

(s/defn group-by-category
  ([]
   (group-by :purchase/category (get-purchases)))
  ([
    category :- m/StrNotBlank
    ]
   (group-by :purchase/category (filter #(= category (:purchase/category %)) (get-purchases)))))


(s/defn map-total-by-category
  [
   [key itens]
   ]
  (let [values (map #(:purchase/value %) itens)]
    {:category key
     :total    (reduce + values)}))

(s/defn total-by-category
  []
  (let [group-by (group-by-category)]
    (map #(map-total-by-category %) group-by)))

(defn get-month [date]
  (as date :month-of-year))


(defn between [min max v]
  (and (<= v max) (>= v min)))


(s/defn filter-purchases :- [m/Purchase]
  ([value key]
   (let [purchases (get-purchases)]
     (filter #(= value (key %)) purchases)))
  ([min max key]
   (let [purchases (get-purchases)
         filter-purchases (filter #(between min max (key %)) purchases)]
     (map #(dissoc % :db/id) filter-purchases))))

(defn format-date
  [date-str]
  (let [date (local-date "dd/MM/yyyy" date-str)]
    date))

(defn purchases-by-month [purchases]
  (let [actual-month (get-month (local-date))]
    (filter #(= actual-month (get-month (format-date (:purchase/date %)))) purchases))
  )

(s/defn invoice :- m/Invoice
  ([
    card-number :- s/Num
    ]
   (let [
         purchases (get-purchases card-number)
         sales (purchases-by-month purchases)
         ]
     {:card-number   card-number
      :invoice-value (reduce + (map #(:purchase/value %) sales))})))


(defn date-to-str
  [date]
  (format "dd/MM/YYYY" date)
  )