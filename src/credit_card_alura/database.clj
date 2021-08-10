(ns credit-card-alura.database
  (:require [datomic.api :as d]))

(def db-url "datomic:dev://localhost:4334/credit-card-db")

(defn set-db-url
  [url]
  (def db-url url)
  )

(defn create-database []
  (d/create-database db-url))

(defn create-conn []
  (create-database)
  (d/connect db-url))

(defn get-db [conn]
  (d/db conn))

(def schemas [{:db/ident       :consumer/name
               :db/valueType   :db.type/string
               :db/cardinality :db.cardinality/one}
              {:db/ident       :consumer/cpf
               :db/valueType   :db.type/string
               :db/unique      :db.unique/identity
               :db/cardinality :db.cardinality/one}
              {:db/ident       :consumer/email
               :db/valueType   :db.type/string
               :db/cardinality :db.cardinality/one}
              {:db/ident       :card/number
               :db/valueType   :db.type/string
               :db/cardinality :db.cardinality/one}
              {:db/ident       :card/cvv
               :db/valueType   :db.type/long
               :db/cardinality :db.cardinality/one}
              {:db/ident       :card/limit
               :db/valueType   :db.type/double
               :db/cardinality :db.cardinality/one}
              {:db/ident       :card/validate
               :db/valueType   :db.type/string
               :db/cardinality :db.cardinality/one}
              {:db/ident       :card/consumer-id
               :db/valueType   :db.type/ref
               :db/cardinality :db.cardinality/one}
              {:db/ident       :purchase/uuid
               :db/valueType   :db.type/uuid
               :db/cardinality :db.cardinality/one
               :db/unique      :db.unique/identity}
              {:db/ident       :purchase/card
               :db/valueType   :db.type/ref
               :db/cardinality :db.cardinality/one}
              {:db/ident       :purchase/date
               :db/valueType   :db.type/string
               :db/cardinality :db.cardinality/one}
              {:db/ident       :purchase/value
               :db/valueType   :db.type/double
               :db/cardinality :db.cardinality/one}
              {:db/ident       :purchase/category
               :db/valueType   :db.type/string
               :db/cardinality :db.cardinality/one}
              {:db/ident       :purchase/establishment
               :db/valueType   :db.type/string
               :db/cardinality :db.cardinality/one}
              {:db/ident       :purchase/pay
               :db/valueType   :db.type/boolean
               :db/cardinality :db.cardinality/one}
              ])
(defn create-schemas
  [conn]
  (d/transact conn schemas))

(defn add
  [conn itens]
  (d/transact conn itens)
  )

(defn get-clients
  [db]
  (d/q '[:find (pull ?e [*])
         :where [?e :consumer/cpf ?cpf]] db))

(defn get-client-by-cpf
  [db cpf]
  (d/q '[:find [(pull ?e [*])]
         :in $ ?search-cpf
         :where [?e :consumer/cpf ?search-cpf]] db cpf)
  )
(defn get-cards [db]
  (d/q '[:find  (pull ?e [*])
         :where [?e :card/number]] db)
  )
(defn get-card-by-number [db number]
  (d/q '[:find  (pull ?e [*])
         :in $ ?search-number
         :where [?e :card/number ?search-number]] db number)
  )

(defn get-purchases-by-card
  [db card]
  (d/q '[:find  (pull ?e [*])
         :in $ ?ref-card
         :where [?e :purchase/card ?ref-card]] db card)
  )

(defn get-purchases
  [db]
  (d/q '[:find  (pull ?e [*])
         :where [?e :purchase/card]] db)
  )

(defn delete-database []
  (d/delete-database db-url))
