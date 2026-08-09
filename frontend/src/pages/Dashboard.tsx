import React, { useMemo } from 'react'
import { useQuery, useQueryClient } from '@tanstack/react-query'
import { getMyUrls } from '../api/url'
import Card from '../components/Card'
import StatCard from '../components/StatCard'
import ShortenUrlForm from '../components/ShortenUrlForm'
import EmptyState from '../components/EmptyState'
import UrlStatusBadge from '../components/UrlStatusBadge'
import CopyButton from '../components/CopyButton'
import { SkeletonCard, SkeletonLine } from '../components/Skeleton'
import { useAuth } from '../hooks/useAuth'
import { Link } from 'react-router-dom'
import { CheckCircle2, ExternalLink, Link as LinkIcon, TimerOff } from 'lucide-react'
import { UrlResponse } from '../types'
import { decodeJwt, formatDate, getApiErrorMessage, getUrlStats, truncateMiddle } from '../utils'

const Dashboard: React.FC = () => {
  const { token } = useAuth()
  const queryClient = useQueryClient()

  const claims = token ? decodeJwt(token) : null
  const rawName = typeof claims?.name === 'string' ? (claims.name as string) : undefined
  const firstName = rawName?.split(' ')[0]
  const email = (typeof claims?.email === 'string' ? claims.email : claims?.sub) as string | undefined

  const { data = [], isPending, isError, error } = useQuery({
    queryKey: ['myurls'],
    queryFn: () => getMyUrls()
  })

  const stats = getUrlStats(data)

  const recentUrls = useMemo(
    () => [...data].sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()).slice(0, 5),
    [data]
  )

  return (
    <div className="page">
      <div className="page-head">
        <div>
          <h1 className="page-head__title">Welcome back{firstName ? `, ${firstName}` : ''}</h1>
          <p className="page-head__sub">
            Shorten new links, then monitor their clicks and performance{email ? ` — signed in as ${email}` : ''}.
          </p>
        </div>
      </div>

      <div className="stat-grid">
        {isPending ? (
          <>
            <SkeletonCard />
            <SkeletonCard />
            <SkeletonCard />
            <SkeletonCard />
          </>
        ) : isError ? (
          <Card className="stat-grid__span">
            <p className="form-error">{getApiErrorMessage(error, 'Could not load dashboard data')}</p>
          </Card>
        ) : (
          <>
            <StatCard label="Total URLs" value={stats.total} icon={LinkIcon} hint="Links in this account" />
            <StatCard label="Active URLs" value={stats.active} icon={CheckCircle2} hint="Available for redirect" />
            <StatCard label="Expired URLs" value={stats.expired} icon={TimerOff} hint="Past expiration date" />
          </>
        )}
      </div>

      <div className="grid-2--uneven" style={{ marginTop: 16 }}>
        <Card>
          <div className="stack--sm">
            <div>
              <h2 className="section-head__title" style={{ margin: 0 }}>Create a new short link</h2>
              <p className="page-head__sub" style={{ marginTop: 6 }}>
                Paste a destination URL and choose how long the link stays active.
              </p>
            </div>
            <ShortenUrlForm onSuccess={() => queryClient.invalidateQueries({ queryKey: ['myurls'] })} />
          </div>
        </Card>

        <Card>
          <div className="row row--between">
            <div>
              <h2 className="section-head__title" style={{ margin: 0 }}>Recent URLs</h2>
              <p className="page-head__sub" style={{ marginTop: 6 }}>Your latest links.</p>
            </div>
            <Link to="/myurls" className="btn btn--ghost btn--sm">
              View all
            </Link>
          </div>

          <div className="stack" style={{ marginTop: 14 }}>
            {isPending && (
              <>
                <SkeletonLine className="skel--row" />
                <SkeletonLine className="skel--row" />
                <SkeletonLine className="skel--row" />
              </>
            )}
            {isError && (
              <p className="form-error">{getApiErrorMessage(error, 'Could not load URLs')}</p>
            )}
            {!isPending && !isError && recentUrls.length === 0 && (
              <EmptyState title="No links yet" subtitle="Shorten your first link above and it will appear here." />
            )}
            {recentUrls.map((u: UrlResponse) => (
              <div key={u.shortCode} className="panel">
                <div className="row row--between row--top">
                  <div className="min-w-0">
                    <a
                      href={u.shortUrl}
                      target="_blank"
                      rel="noreferrer"
                      className="tbl__code truncate"
                      title={u.shortUrl}
                    >
                      {u.shortUrl}
                    </a>
                    <p className="tbl__dest" style={{ maxWidth: 'none', marginTop: 4 }} title={u.originalUrl}>
                      {truncateMiddle(u.originalUrl, 46)}
                    </p>
                  </div>
                  <UrlStatusBadge url={u} />
                </div>
                <div className="row row--wrap" style={{ marginTop: 10, gap: 14 }}>
                  <span className="label--dim">{formatDate(u.createdAt)}</span>
                  <CopyButton value={u.shortUrl} label="Copy" className="btn--sm" />
                  <Link
                    to={`/analytics/${u.shortCode}`}
                    className="btn btn--ghost btn--sm"
                  >
                    <ExternalLink size={13} />
                    Analytics
                  </Link>
                </div>
              </div>
            ))}
          </div>
        </Card>
      </div>
    </div>
  )
}

export default Dashboard
