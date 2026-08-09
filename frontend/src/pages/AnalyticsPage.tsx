import React from 'react'
import { Link, useParams } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { getAnalytics } from '../api/url'
import { AnalyticsResponse } from '../types'
import Header from '../components/Header'
import Card from '../components/Card'
import StatCard from '../components/StatCard'
import CopyButton from '../components/CopyButton'
import { SkeletonCard } from '../components/Skeleton'
import { useAuth } from '../hooks/useAuth'
import { formatDate, getApiErrorMessage } from '../utils'
import { ArrowLeft, Globe, Laptop, Monitor, MousePointerClick, Users } from 'lucide-react'
import { ResponsiveContainer, BarChart, Bar, XAxis, YAxis, Tooltip, CartesianGrid } from 'recharts'

type ChartTooltipProps = {
  active?: boolean
  payload?: Array<{ name?: string; value?: number }>
  label?: string
}

const ChartTooltip: React.FC<ChartTooltipProps> = ({ active, payload, label }) => {
  if (!active || !payload || payload.length === 0) return null

  return (
    <div className="panel panel--padless" style={{ padding: '8px 12px', fontSize: 12 }}>
      <p className="mono">{label}</p>
      <p style={{ color: 'var(--accent-line)', marginTop: 2 }}>
        {payload[0].value} {payload[0].value === 1 ? 'count' : 'counts'}
      </p>
    </div>
  )
}

const AnalyticsPage: React.FC = () => {
  useAuth()
  const { shortCode } = useParams()

  const { data, isPending, isError, error } = useQuery<AnalyticsResponse>({
    queryKey: ['analytics', shortCode],
    queryFn: () => getAnalytics(shortCode!),
    enabled: Boolean(shortCode)
  })

  if (isPending) {
    return (
      <div className="page">
        <SkeletonCard />
        <div className="stat-grid" style={{ marginTop: 16 }}>
          <SkeletonCard />
          <SkeletonCard />
          <SkeletonCard />
          <SkeletonCard />
        </div>
      </div>
    )
  }

  if (isError || !data) {
    return (
      <div className="page">
        <Card className="center" style={{ maxWidth: 560, margin: '0 auto' }}>
          <p className="form-error">{getApiErrorMessage(error, 'Could not load analytics')}</p>
          <Link to="/myurls" className="btn btn--ghost" style={{ marginTop: 16 }}><ArrowLeft size={16} />Back to My URLs</Link>
        </Card>
      </div>
    )
  }

  const chartData = [
    { name: 'Total clicks', value: data.totalClicks },
    { name: 'Unique visitors', value: data.uniqueVisitors }
  ]

  return (
    <div className="page">
      <Link to="/myurls" className="btn btn--ghost btn--sm" style={{ marginBottom: 16 }}>
        <ArrowLeft size={14} />
        Back to My URLs
      </Link>

      <Header
        title={data.shortCode}
        subtitle={data.originalUrl}
        actions={<CopyButton value={data.originalUrl} label="Copy original URL" />}
      />

      <div className="stat-grid">
        <StatCard label="Total clicks" value={data.totalClicks} icon={MousePointerClick} hint="All-time redirects" />
        <StatCard label="Unique visitors" value={data.uniqueVisitors} icon={Users} hint="Distinct IP addresses" />
        <StatCard label="Top browser" value={data.topBrowser ?? '—'} icon={Globe} hint="Most common browser" />
        <StatCard label="Top device" value={data.topDevice ?? '—'} icon={Monitor} hint="Most common device" />
      </div>

      <div className="grid-2--uneven" style={{ marginTop: 16 }}>
        <div className="chart-panel">
          <h2 className="chart-panel__title">Click overview</h2>
          <p className="chart-panel__sub">Total clicks versus unique visitors.</p>
          <div className="chart-box">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={chartData} barSize={56}>
                <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#3E6488" />
                <XAxis
                  dataKey="name"
                  tickLine={false}
                  axisLine={false}
                  tick={{ fontSize: 12, fill: '#6E8CA6' }}
                />
                <YAxis
                  allowDecimals={false}
                  tickLine={false}
                  axisLine={false}
                  tick={{ fontSize: 12, fill: '#6E8CA6' }}
                />
                <Tooltip cursor={{ fill: 'rgba(62, 100, 136, 0.18)' }} content={<ChartTooltip />} />
                <Bar dataKey="value" name="Count" fill="#9FD8E8" radius={[3, 3, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>

        <div className="panel">
          <h2 className="chart-panel__title">Details</h2>
          <dl className="kv-list" style={{ marginTop: 10 }}>
            <div>
              <dt>Short code</dt>
              <dd>{data.shortCode}</dd>
            </div>
            <div>
              <dt>Created</dt>
              <dd>{formatDate(data.createdAt)}</dd>
            </div>
            <div>
              <dt>Expires</dt>
              <dd>{formatDate(data.expiresAt)}</dd>
            </div>
            <div>
              <dt>
                <Laptop size={13} className="kv-icon" />
                Operating system
              </dt>
              <dd>{data.topOperatingSystem ?? '—'}</dd>
            </div>
            <div>
              <dt>Top referrer</dt>
              <dd>{data.topReferrer ?? '—'}</dd>
            </div>
          </dl>
        </div>
      </div>
    </div>
  )
}

export default AnalyticsPage
