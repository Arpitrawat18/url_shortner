import React from 'react'
import { Link } from 'react-router-dom'
import { CalendarClock, ExternalLink, Sparkles } from 'lucide-react'
import { formatDate, truncateMiddle } from '../utils'
import { UrlResponse } from '../types'
import CopyButton from './CopyButton'
import { useAuth } from '../hooks/useAuth'

const ShortUrlResult: React.FC<{ result: UrlResponse }> = ({ result }) => {
  const { token } = useAuth()

  return (
    <div className="result">
      <div className="result__head">
        <div>
          <span className="row" style={{ gap: 8 }}>
            <Sparkles size={14} className="label" />
            <span className="label">Short link ready</span>
          </span>
        </div>
        <CopyButton value={result.shortUrl} />
      </div>

      <a href={result.shortUrl} target="_blank" rel="noreferrer" className="result__code">
        {result.shortUrl}
      </a>
      <span className="result__link">{result.originalUrl}</span>

      <dl className="result__meta">
        <div className="result__meta-row">
          <dt>Original</dt>
          <dd title={result.originalUrl}>{truncateMiddle(result.originalUrl, 90)}</dd>
        </div>
        <div className="result__meta-row">
          <dt>Expires</dt>
          <dd>
            <span className="row" style={{ gap: 6 }}>
              <CalendarClock size={13} className="label--dim" />
              {formatDate(result.expiresAt)}
            </span>
          </dd>
        </div>
      </dl>

      <div className="dim">
        <div className="dim__labels">
          <span className="dim__label">Source length</span>
          <span className="dim__label">Compressed length</span>
        </div>
        <div className="dim__rule">
          <span className="dim__tick dim__tick--l" />
          <span className="dim__tick dim__tick--r" />
        </div>
        <div className="dim__counts">
          <span className="dim__count">{result.originalUrl.length} chars</span>
          <span className="dim__count">{result.shortUrl.length} chars</span>
        </div>
      </div>

      {token ? (
        <div className="promo">
          <p className="promo__msg">
            Link saved to <strong>your workspace</strong>. Track clicks, visitors and devices.
          </p>
          <div className="promo__actions">
            <Link to="/dashboard" className="btn btn--primary btn--sm">
              <ExternalLink size={14} />
              Open dashboard
            </Link>
          </div>
        </div>
      ) : (
        <div className="promo">
          <p className="promo__msg">
            Create a free account to <strong>track clicks</strong>, visitors and manage your links.
          </p>
          <div className="promo__actions">
            <Link to="/register" className="btn btn--primary btn--sm">
              Sign up free
            </Link>
            <Link to="/login" className="btn btn--ghost btn--sm">
              Log in
            </Link>
          </div>
        </div>
      )}
    </div>
  )
}

export default ShortUrlResult
