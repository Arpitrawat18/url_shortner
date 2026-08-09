import React, { useMemo, useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { getAnalytics, getMyUrls } from '../api/url'
import { Link } from 'react-router-dom'
import Card from '../components/Card'
import Header from '../components/Header'
import EmptyState from '../components/EmptyState'
import { SkeletonLine } from '../components/Skeleton'
import { useAuth } from '../hooks/useAuth'
import { ArrowRight, BarChart3, ChevronDown, ChevronUp, Globe, LinkIcon, Users } from 'lucide-react'
import { getApiErrorMessage, truncateMiddle } from '../utils'
import { AnalyticsResponse, UrlResponse } from '../types'

type AnalyticsDetailProps = {
  shortCode: string
  expanded: boolean
}

const AnalyticsDetail: React.FC<AnalyticsDetailProps> = ({ shortCode, expanded }) => {
  const { data, isPending, isError } = useQuery<AnalyticsResponse>({
    queryKey: ['analytics', shortCode],
    queryFn: () => getAnalytics(shortCode),
    enabled: expanded
  })

  if (!expanded) return null
  if (isPending) return <span className="tbl__dim">Loading analytics…</span>
  if (isError || !data) return <span className="tbl__dim">Could not load analytics</span>

  return (
    <div className="row row--wrap" style={{ gap: 24 }}>
      <span className="row">
        <BarChart3 size={12} />
        {data.totalClicks} clicks
      </span>
      <span className="row">
        <Users size={12} />
        {data.uniqueVisitors} unique
      </span>
      <span className="row">
        <Globe size={12} />
        {data.topBrowser ?? '—'}
      </span>
      <Link to={`/analytics/${shortCode}`} className="btn btn--ghost btn--sm">
        View details
        <ArrowRight size={14} />
      </Link>
    </div>
  )
}

const AnalyticsOverview: React.FC = () => {
  useAuth()

  const { data, isPending, isError, error } = useQuery({
    queryKey: ['myurls'],
    queryFn: () => getMyUrls()
  })

  const [expandedCode, setExpandedCode] = useState<string | null>(null)

  const urls = useMemo<UrlResponse[]>(() => {
    const list = data ?? []
    return [...list].sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
  }, [data])

  const toggle = (shortCode: string) =>
    setExpandedCode((prev) => (prev === shortCode ? null : shortCode))

  const summary =
    !isPending && !isError
      ? `Select a link to view its click performance — ${urls.length} link${urls.length === 1 ? '' : 's'} in total.`
      : undefined

  return (
    <div className="page">
      <Header
        title="Analytics"
        subtitle={summary}
      />

      {isPending && (
        <div className="stack">
          <SkeletonLine className="skel--row" />
          <SkeletonLine className="skel--row" />
          <SkeletonLine className="skel--row" />
          <SkeletonLine className="skel--row" />
        </div>
      )}

      {isError && (
        <Card>
          <p className="form-error">{getApiErrorMessage(error, 'Could not load analytics')}</p>
        </Card>
      )}

      {!isPending && !isError && urls.length === 0 && (
        <EmptyState
          title="No analytics yet"
          subtitle="Create a link and watch it in action — click counts will appear here."
          action={
            <Link to="/dashboard" className="btn btn--primary">
              <BarChart3 size={16} />
              Shorten a URL
            </Link>
          }
        />
      )}

      {!isPending && !isError && urls.length > 0 && (
        <>
          <div className="tbl-wrap hidden--mobile">
            <table className="tbl">
              <thead>
                <tr>
                  <th>Short URL</th>
                  <th>Destination</th>
                  <th className="right">Analytics</th>
                </tr>
              </thead>
              <tbody>
                {urls.map((url) => {
                  const expanded = expandedCode === url.shortCode
                  return (
                    <React.Fragment key={url.shortCode}>
                      <tr onClick={() => toggle(url.shortCode)} style={{ cursor: 'pointer' }}>
                        <td>
                          <span className="tbl__code">{url.shortUrl}</span>
                        </td>
                        <td>
                          <span className="tbl__dest" title={url.originalUrl}>
                            {truncateMiddle(url.originalUrl, 44)}
                          </span>
                        </td>
                        <td className="right">
                          <span className="btn btn--ghost btn--sm">
                            {expanded ? <ChevronUp size={14} /> : <ChevronDown size={14} />}
                            {expanded ? 'Hide' : 'View'}
                          </span>
                        </td>
                      </tr>
                      {expanded && (
                        <tr>
                          <td colSpan={3} className="tbl__dim">
                            <AnalyticsDetail shortCode={url.shortCode} expanded={expanded} />
                          </td>
                        </tr>
                      )}
                    </React.Fragment>
                  )
                })}
              </tbody>
            </table>
          </div>

          <div className="url-cards hidden--desktop">
            {urls.map((url) => {
              const expanded = expandedCode === url.shortCode
              return (
                <div key={url.shortCode} className="url-card">
                  <div className="url-card__head">
                    <span className="url-card__icon">
                      <LinkIcon size={16} />
                    </span>
                    <button onClick={() => toggle(url.shortCode)} className="btn btn--ghost btn--sm">
                      {expanded ? 'Hide analytics' : 'View analytics'}
                      {expanded ? <ChevronUp size={14} /> : <ChevronDown size={14} />}
                    </button>
                  </div>
                  <span className="url-card__code" title={url.shortUrl}>
                    {url.shortUrl}
                  </span>
                  <p className="url-card__dest" title={url.originalUrl}>
                    {truncateMiddle(url.originalUrl, 48)}
                  </p>
                  {expanded && (
                    <div className="url-card__meta">
                      <AnalyticsDetail shortCode={url.shortCode} expanded={expanded} />
                    </div>
                  )}
                </div>
              )
            })}
          </div>
        </>
      )}
    </div>
  )
}

export default AnalyticsOverview
