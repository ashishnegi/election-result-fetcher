(defproject electionresult "0.1.0-SNAPSHOT"
  :description "Fetches and Parses the Indian Election Results."
  :url ""
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[clj-http "0.9.1"]
                 [org.clojure/clojure "1.5.1"]
                 [enlive "1.1.5"]
                 ]
  :main electionresult.core
  :aot [electionresult.core]
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
