import React from 'react'
import { Link } from 'react-router-dom'
import ShortenUrlForm from '../components/ShortenUrlForm'
import { useAuth } from '../hooks/useAuth'
import { BarChart3, CheckCircle2, Sparkles, Zap } from 'lucide-react'

const FEATURES = [
  {
    icon: Zap,
    title: 'Instant short links',
    text: 'Paste a long URL and get a short, shareable link in milliseconds.'
  },
  {
    icon: Sparkles,
    title: 'Free, no account needed',
    text: 'Shorten your first link without signing up. No credit card, no limits.'
  },
  {
    icon: BarChart3,
    title: 'Click analytics',
    text: 'Create a free account to track clicks, unique visitors and devices per link.'
  }
]

const STEPS = [
  { step: '1', text: 'Paste your long URL into the box above.' },
  { step: '2', text: 'Choose when your link expires.' },
  { step: '3', text: 'Copy your short link and share it anywhere.' }
]

const Landing: React.FC = () => {
  const { token } = useAuth()

  return (
    <div>
      <section className="hero">
        <span className="hero__kicker">
          <Sparkles size={12} />
          Free forever · No sign-up to shorten
        </span>

        <h1 className="hero__title">
          Shorten links.
          <br />
          <span className="accent">Share them anywhere.</span>
        </h1>

        <p className="hero__sub">
          Paste any long URL and get a short, shareable link in seconds. Free, fast and simple for everyone.
        </p>

        <div className="hero__form">
          <ShortenUrlForm hero />
        </div>

        <p className="hero__note">
          Already have an account?{' '}
          <Link to="/login">Log in</Link>
          {' '}·{' '}
          <Link to="/register">Sign up free</Link>
        </p>
      </section>

      {token && (
        <div className="container" style={{ maxWidth: 780 }}>
          <div className="promo">
            <p className="promo__msg">
              You&apos;re signed in — manage and <strong>analyze</strong> all of your links from one place.
            </p>
            <div className="promo__actions">
              <Link to="/dashboard" className="btn btn--primary btn--sm">Open dashboard</Link>
            </div>
          </div>
        </div>
      )}

      <section className="container" style={{ paddingTop: 16 }}>
        <div className="feature-grid">
          {FEATURES.map(({ icon: Icon, title, text }) => (
            <div key={title} className="feature">
              <span className="feature__icon">
                <Icon size={17} />
              </span>
              <h3 className="feature__title">{title}</h3>
              <p className="feature__text">{text}</p>
            </div>
          ))}
        </div>
      </section>

      <section className="container" style={{ paddingTop: 28, paddingBottom: 28 }}>
        <div className="section-head">
          <h2 className="section-head__title">How it works</h2>
        </div>
        <div className="panel">
          <div className="step-list">
            {STEPS.map(({ step, text }) => (
              <div key={step} className="step">
                <span className="step__num">{step}</span>
                <p className="step__text">{text}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {!token && (
        <section className="container" style={{ paddingTop: 8, paddingBottom: 40 }}>
          <div className="panel">
            <h2 className="center auth-panel__title">Ready to track your links?</h2>
            <p className="center auth-panel__sub" style={{ maxWidth: 460, margin: '8px auto 0' }}>
              Create a free account to get click analytics, device breakdowns and full link management.
            </p>
            <div className="row center" style={{ justifyContent: 'center', marginTop: 20, flexWrap: 'wrap' }}>
              <Link to="/register" className="btn btn--primary">
                <CheckCircle2 size={15} />
                Create free account
              </Link>
              <Link to="/login" className="btn btn--ghost">Log in</Link>
            </div>
          </div>
        </section>
      )}
    </div>
  )
}

export default Landing
