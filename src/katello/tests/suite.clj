(ns katello.tests.suite
  (:refer-clojure :exclude [fn])
  (:require (katello.tests organizations providers promotions
                           sync_management login environments
                           systems users permissions templates e2e)
            
            [test.tree.jenkins :as jenkins]
            [katello.tests.setup :as setup]
            serializable.fn)
  (:use test.tree.script))

(defgroup all-katello-tests
      :test-setup login/navigate-toplevel

      katello.tests.login/all-login-tests
      katello.tests.organizations/all-org-tests
      katello.tests.environments/all-environment-tests
      katello.tests.providers/all-provider-tests
      katello.tests.systems/all-system-tests
      katello.tests.sync_management/all-sync-tests
      katello.tests.users/all-user-tests
      katello.tests.permissions/all-permission-tests
      katello.tests.templates/all-template-tests
      katello.tests.e2e/all-end-to-end-tests)

(defn suite
  ([] (suite nil))
  ([group]
     (with-meta (-> group (or "katello.tests.suite/all-katello-tests")
                   symbol resolve deref)
       (merge {:threads (let [user-choice (try (-> (System/getProperty "test.tree.threads")
                                                  (Integer.))
                                               (catch Exception e 3))]
                          (Math/min user-choice 5))} ;
              setup/runner-config))))

;;list of namespaces and fns we want to trace 
(def to-trace 
  '[katello.tasks
    katello.api-tasks
    katello.client
    katello.setup/start-selenium
    katello.setup/stop-selenium
    katello.setup/switch-new-admin-user
    com.redhat.qe.verify/check
    com.redhat.qe.auto.selenium.selenium/call-sel
    com.redhat.qe.config/property-map])

;;set of fns to exclude from tracing
(def do-not-trace 
  #{'katello.tasks/notification 
    'katello.tasks/success?
    'katello.tasks/uniqueify
    'katello.tasks/unique-names
    'katello.tasks/timestamps})

(defn -main [ & args]
  (jenkins/run-suite (suite (first args)) {:to-trace to-trace
                              :do-not-trace do-not-trace}))


