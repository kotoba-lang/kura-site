# kura-site

The public surface for the [kura](https://github.com/kotoba-lang/kura) storage
network. **Live: https://kura-site.04-feasts-minded.workers.dev**

## The page's thesis is its construction

Every number is either **measured** or **labelled as modelled**. That is not a
tone choice — it is the pitch. A storage network's durability claim is
unfalsifiable to a customer until someone publishes which figures came from a
measurement and which came from an assumption, and the reason to choose this
one is that it does. So the page has a "What we measured" section, a "What this
is not" section, and a footnote admitting that random sampling reported the code
as one loss better than it is.

## Built on the paved road

Pure `.cljc` hiccup on `kotoba-ui.core` — one require, one theme map, **zero
app CSS** (skill `kotoba-uiux`, ADR-2607122200). The only hex in the source is
the accent, which is the one place a colour is legitimate.

Scored with the deterministic HIG/WCAG audit, which is the gate the skill
prescribes because an unmeasured page is theater:

```
design-quality audit — 1 page(s)
  100.00  public/index.html
findings (headroom-first): (none — converged)
gate: aggregate 100.00 >= min 95.00 -> PASS
```

## Build and deploy

```bash
nbb --classpath "src:$KL/css/src:$KL/html/src:$KL/shitsuke/src:$KL/liquid-glass-ui/src:$KL/kotoba-ui/src" generate.cljk
npx wrangler deploy
```

Static assets only — no Worker script, because the page is server-rendered at
build time and has nothing to compute per request.

**workers.dev on purpose, not for lack of a domain.** ADR-2607177500 records a
concurrent session taking a `*.kotobase.net` hostname out from under another
Worker — last `wrangler deploy` wins on Cloudflare custom domains. Claiming a
hostname is a decision with a contention cost; a workers.dev URL is a real
public address with none. Moving to a custom domain is one line in
`wrangler.jsonc` once someone decides which name kura should own.

## `/llms.txt`

The agent-readable version, same discipline: what is measured, what is
modelled, what this is not, and an explicit note that **there is no paid
endpoint and no x402 catalog entry**. Registering one before the network
accepts data would be exactly the overclaiming the rest of the document exists
to avoid.

## License

MIT.
