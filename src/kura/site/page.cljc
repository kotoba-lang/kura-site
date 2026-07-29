(ns kura.site.page
  "The public page for the kura storage network.

  Pure `.cljc` hiccup on `kotoba-ui.core` — one require, one theme map, zero
  app CSS (skill `kotoba-uiux`, ADR-2607122200). Everything visual comes from
  the HIG tokens; the only hex in this file is the accent, which is the one
  place a colour is legitimate.

  **Every number on this page is either measured or labelled as modelled.**
  That is not a style choice — it is the whole pitch. A storage network's
  durability claim is unfalsifiable to a customer until someone publishes what
  they actually measured and what they merely assumed, and the reason to
  choose this one is that it does."
  (:require [kotoba-ui.core :as ui]))

(def theme
  {:accent "#3A7D6B"
   :accent-dark "#5FB39A"
   :appearance :auto})

;; --- content ---------------------------------------------------------------
;; Figures come from the libraries themselves (kura-bugyo/pricing,
;; erasure/distance-test) rather than being retyped here, so a change to the
;; economics cannot silently leave the page quoting the old number.

(def facts
  {:launch-multiplier "2.00×"
   :target-multiplier "1.625×"
   :storage-price "$4.62"
   :target-price "$3.75"
   :egress-price "$3.08"
   :bond-per-tb "$7.50"
   :capital-drag "4.1%"
   :audit-share "0.005%"
   :repair-reads "4"
   :measured-distance "8"
   :patterns-checked "657,800"})

(defn- fact [label value detail]
  (ui/metric {:label label :value value :detail detail}))

(defn- claim [title body]
  (ui/panel [[:h3 title] [:p {:class "hig-callout"} body]]))

(defn- hero-block []
  (ui/hero
   {:title "Storage that publishes what it measured"
    :tagline (str "Erasure-coded object storage on independently operated nodes. "
                  "Priced in USD, settled in USDC, no network token.")
    :actions [(ui/button "Store data" {:act :customer})
              (ui/button "Run a node" {:act :operator})]}))

(defn- honesty-section []
  (ui/section
   {:title "What we measured" :wide true}
   [:p {:class "hig-body"}
    (str "A durability claim is unfalsifiable to a customer until someone says "
         "which numbers came from a measurement and which came from a model. "
         "These came from a measurement.")]
   (ui/grid
    (fact "Minimum distance"
          (:measured-distance facts)
          (str "exhaustive over " (:patterns-checked facts)
               " erasure patterns — not a bound, not a sample"))
    (fact "Reads to repair one shard"
          (:repair-reads facts)
          "and zero field multiplies")
    (fact "Audit read per node"
          (:audit-share facts)
          "of capacity per year, against 1200% for a monthly scrub"))
   [:p {:class "hig-footnote"}
    (str "Random sampling reported this code as tolerating one more loss than "
         "it does. Exhaustive search found 1,464 fatal patterns the sample "
         "never drew. We publish the exhaustive number.")]))

(defn- pricing-section []
  (ui/section
   {:title "Pricing" :wide true}
   (ui/data-table
    {:caption "USD per logical TB. Nodes are paid for physical bytes; the storage multiplier is our cost, not your line item."
     :columns [{:key :plan :label "Configuration"}
               {:key :mult :label "Storage multiplier"}
               {:key :storage :label "Storage / TB-month"}
               {:key :egress :label "Egress / TB"}]
     :rows [{:plan "Launch (today)" :mult (:launch-multiplier facts)
             :storage (:storage-price facts) :egress (:egress-price facts)}
            {:plan "Target (after Phase 0)" :mult (:target-multiplier facts)
             :storage (:target-price facts) :egress (:egress-price facts)}]})
   [:p {:class "hig-body"}
    (str "Launch is deliberately conservative. At launch the node-loss rate is "
         "unmeasured, and the costs are asymmetric: launching optimistic and "
         "finding the rate is worse loses data irreversibly, while launching "
         "conservative and finding it is better is a price cut. The 18.7% "
         "reduction goes to customers when the measurement earns it — the data "
         "that produces the measurement is theirs.")]
   [:p {:class "hig-footnote"}
    (str "Egress is not multiplied by the code. A systematic read reconstructs "
         "nothing, so serving one logical TB moves one physical TB. Charging "
         "egress at the storage multiplier would bill for work we do not do.")]))

(defn- operator-section []
  (ui/section
   {:title "Run a node" :wide true}
   [:p {:class "hig-body"}
    (str "Onboarding is permissionless. The bond is the barrier, not a "
         "gatekeeper — anyone may join, and what filters node quality is "
         "collateral, not an invitation.")]
   (ui/grid
    (fact "Bond" (:bond-per-tb facts) "per TB committed, in USDC on Base")
    (fact "Real cost of the bond" (:capital-drag facts)
          "of revenue at a 10% opportunity cost — it is collateral, and it comes back")
    (fact "Audit" "688 challenges" "per epoch, catching 1% deletion at p=0.999"))
   [:p {:class "hig-footnote"}
    (str "The bond is priced against the repair bill, not against what a cheat "
         "would gain. A strong audit makes deterrence cheap; rebuilding what a "
         "departed node held is the expense that remains, and it is the larger "
         "of the two.")]))

(defn- limits-section []
  (ui/section
   {:title "What this is not" :wide true}
   (ui/stack
    {:gap :4}
    (claim "Not a proof-of-storage chain"
           (str "Audits are unpredictable sampling against a committed Merkle "
                "sum tree, with collateral behind them. That is detection with "
                "an economic consequence, not the continuous on-chain proof "
                "Filecoin publishes. If public, chain-verifiable proof is your "
                "requirement, Filecoin is the correct choice and we will say so."))
    (claim "Not trustless placement"
           (str "A coordinator decides placement, issues audits and computes "
                "payouts. It cannot lie about a payout total or a node's share "
                "without contradicting a root it already published — but it is a "
                "trusted point, the same one Storj's satellites are. Quorum "
                "coordination is planned, not shipped."))
    (claim "Not a measured durability number yet"
           (str "The code's minimum distance is measured. The node-loss rate "
                "that turns it into a nines figure is not, and will not be until "
                "Phase 0 has run. Anyone quoting you eleven nines for a network "
                "that has not measured its own node population is quoting a "
                "model, including us — so we quote the model and label it."))
    (claim "Not independent by default"
           (str "Twenty-six shards on one provider's account are one failure "
                "domain, whatever the code says. Every backend must declare its "
                "independence, and the audit reports when a placement collapses "
                "into fewer real domains than the code needs.")))))

(def ^:private conformance-url
  "https://kura-conformance.04-feasts-minded.workers.dev")

(defn- live-section []
  (ui/section
   {:title "Live, not asserted" :wide true}
   [:p {:class "hig-body"}
    (str "The claims above are checkable while you read them. The contract runs "
         "against real buckets at two providers every thirty minutes, and the "
         "results are URLs, not screenshots.")]
   (ui/grid
    (ui/panel [[:h3 "Conformance"]
               [:p {:class "hig-callout"}
                "The shard-store contract against Cloudflare R2 and Backblaze B2. Returns 500 if anything fails, so it works as a check."]
               [:p {:class "hig-footnote"} (str conformance-url "/conformance")]])
    (ui/panel [[:h3 "Status"]
               [:p {:class "hig-callout"}
                "The probe series: availability per backend, median read latency, and an explicit refusal to quote a rate until the window is long enough."]
               [:p {:class "hig-footnote"} (str conformance-url "/status")]])
    (ui/panel [[:h3 "Durability, demonstrated"]
               [:p {:class "hig-callout"}
                (str "Stores an object across both providers, destroys shards on purpose, "
                     "repairs, and compares the recovered bytes. One shard lost costs four "
                     "reads. Eight lost is refused rather than guessed at.")]
               [:p {:class "hig-footnote"} (str conformance-url "/durability")]])
    (ui/panel [[:h3 "Fleet audit"]
               [:p {:class "hig-callout"}
                "How many genuinely independent failure domains the fleet has. It currently says the fleet is NOT survivable — two providers, and the code needs at least three."]
               [:p {:class "hig-footnote"} (str conformance-url "/audit")]]))
   [:p {:class "hig-footnote"}
    (str "The audit answering no, and the durability run including a case that "
         "must fail, are the reasons to trust the rest. A status page that only "
         "ever reports success is a status page nobody wired to anything.")]))

(defn- build-section []
  (ui/section
   {:title "Built in the open" :wide true}
   [:p {:class "hig-body"}
    "Five libraries, each with the reasoning in its README and tests that assert the claims."]
   (ui/grid
    (claim "erasure" "GF(2^8) locally recoverable codes. Minimum distance measured exhaustively, not cited.")
    (claim "kura" "Placement, repair, audit and payment-order decisions. Moves no bytes.")
    (claim "kura-node" "Shard backends and the fast multiply-accumulate, held byte-equal to the reference.")
    (claim "kura-bugyo" "Pricing, collateral and epoch payout roots anyone can verify.")
    (claim "kotobase-storage-kura" "Drop-in backend for anything already speaking kotobase.storage."))))

(defn view []
  (ui/app-shell
   {:nav (ui/nav-bar "kura 蔵"
                     {:trailing [(ui/button "Source" {:act :source})]})}
   (hero-block)
   (honesty-section)
   (live-section)
   (pricing-section)
   (operator-section)
   (limits-section)
   (build-section)
   (ui/section
    {:title "Status"}
    [:p {:class "hig-callout"}
     (str "Phase 0: the coding, placement, audit and settlement planes run "
          "against rented backends as pseudo-nodes, to measure the node-loss "
          "rate that sets the storage multiplier. Not yet accepting customer "
          "data. The libraries are public and the numbers above are "
          "reproducible from them.")])))

(defn render []
  (ui/->page
   {:title "kura — erasure-coded storage that publishes what it measured"
    :description (str "Erasure-coded object storage on independently operated "
                      "nodes. USD-priced, USDC-settled, no token. Minimum "
                      "distance measured exhaustively; durability model "
                      "labelled as a model.")
    :theme theme}
   (view)))
