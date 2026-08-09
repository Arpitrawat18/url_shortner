import React, { useMemo, useState } from 'react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { deleteUrl, getMyUrls } from '../api/url'
import { Link } from 'react-router-dom'
import Card from '../components/Card'
import Header from '../components/Header'
import EmptyState from '../components/EmptyState'
import ConfirmDialog from '../components/ConfirmDialog'
import UrlStatusBadge from '../components/UrlStatusBadge'
import CopyButton from '../components/CopyButton'
import { SkeletonLine } from '../components/Skeleton'
import { useToast } from '../components/Toast'
import { useAuth } from '../hooks/useAuth'
import { BarChart2, Plus, Search, Trash2 } from 'lucide-react'
import { formatDate, getApiErrorMessage, getUrlStatus, truncateMiddle } from '../utils'
import { UrlResponse } from '../types'

const MyUrls: React.FC = () => {
  useAuth()
  const { showToast } = useToast()
  const queryClient = useQueryClient()
  const [query, setQuery] = useState('')
  const [pendingDelete, setPendingDelete] = useState<UrlResponse | null>(null)

  const { data, isPending, isError, error } = useQuery({
    queryKey: ['myurls'],
    queryFn: () => getMyUrls()
  })

  const urls = useMemo(() => {
    const q = query.trim().toLowerCase()
    const filtered = q
      ? (data ?? []).filter(
          (url) => url.originalUrl.toLowerCase().includes(q) || url.shortCode.toLowerCase().includes(q)
        )
      : (data ?? [])

    return [...filtered].sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
  }, [data, query])

  const del = useMutation({
    mutationFn: (shortCode: string) => deleteUrl(shortCode),
    onSuccess: () => {
      setPendingDelete(null)
      queryClient.invalidateQueries({ queryKey: ['myurls'] })
      showToast('URL deleted', 'success')
    },
    onError: (err) => {
      setPendingDelete(null)
      showToast(getApiErrorMessage(err, 'Failed to delete URL'), 'error')
    }
  })

  return (
    <div className="page">
      <Header
        title="My URLs"
        subtitle="Every link you've created, with its current status."
        actions={
          <Link to="/dashboard" className="btn btn--primary">
            <Plus size={16} />
            New URL
          </Link>
        }
      />

      <div className="search">
        <Search size={16} className="search__icon" />
        <input
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          placeholder="Search by short code or destination URL..."
          aria-label="Search URLs"
          className="field"
        />
      </div>

      {isPending && (
        <div className="stack">
          <SkeletonLine className="skel--row" />
          <SkeletonLine className="skel--row" />
          <SkeletonLine className="skel--row" />
          <SkeletonLine className="skel--row" />
          <SkeletonLine className="skel--row" />
        </div>
      )}

      {isError && (
        <Card>
          <p className="form-error">{getApiErrorMessage(error, 'Could not load your URLs')}</p>
        </Card>
      )}

      {!isPending && !isError && urls.length === 0 && (
        <EmptyState
          title={query ? 'No matching links' : 'No links yet'}
          subtitle={
            query
              ? 'Try a different search term.'
              : 'Your compressed links will appear here once you create your first link.'
          }
          action={
            !query && (
              <Link to="/dashboard" className="btn btn--primary">
                <Plus size={16} />
                Create your first link
              </Link>
            )
          }
        />
      )}

      {!isPending && !isError && urls.length > 0 && (
        <>
          <div className="tbl-wrap hidden--mobile">
            <table className="tbl tbl--spec">
              <tbody>
                {urls.map((u) => (
                  <tr key={u.shortCode}>
                    <td className="tbl-cell tbl-cell--short">
                      <span className="tbl-cell__label">Short URL</span>
                      <a
                        href={u.shortUrl}
                        target="_blank"
                        rel="noreferrer"
                        className="tbl-cell__link"
                        title={u.shortUrl}
                      >
                        {u.shortUrl}
                      </a>
                    </td>
                    <td className="tbl-cell tbl-cell--dest">
                      <span className="tbl-cell__label">Destination</span>
                      <span className="tbl-cell__dest" title={u.originalUrl}>
                        {truncateMiddle(u.originalUrl, 40)}
                      </span>
                    </td>
                    <td className="tbl-cell">
                      <span className="tbl-cell__label">Created</span>
                      <span className="tbl-cell__date">{formatDate(u.createdAt)}</span>
                    </td>
                    <td className="tbl-cell">
                      <span className="tbl-cell__label">Expires</span>
                      <span className={`tbl-cell__date${getUrlStatus(u).label === 'Expired' ? ' tbl-cell__date--err' : ''}`}>
                        {formatDate(u.expiresAt)}
                      </span>
                    </td>
                    <td className="tbl-cell">
                      <span className="tbl-cell__label">Status</span>
                      <UrlStatusBadge url={u} />
                    </td>
                    <td className="tbl-cell">
                      <span className="tbl-cell__label">Actions</span>
                      <div className="tbl__actions">
                        <CopyButton value={u.shortUrl} label="Copy" className="btn--sm" />
                        <Link to={`/analytics/${u.shortCode}`} className="btn btn--ghost btn--sm">
                          <BarChart2 size={14} />
                          Analytics
                        </Link>
                        <button
                          onClick={() => setPendingDelete(u)}
                          className="btn btn--danger btn--sm"
                          aria-label={`Delete ${u.shortCode}`}
                        >
                          <Trash2 size={14} />
                          Delete
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          <div className="url-cards hidden--desktop">
            {urls.map((u) => (
              <div key={u.shortCode} className="url-card url-card--spec">
                <div className="url-card__section">
                  <span className="url-card__label">Short link</span>
                  <a href={u.shortUrl} target="_blank" rel="noreferrer" className="url-card__code" title={u.shortUrl}>
                    {u.shortUrl}
                  </a>
                </div>
                <div className="url-card__section">
                  <span className="url-card__label">Destination</span>
                  <p className="url-card__dest url-card__dest--wrap" title={u.originalUrl}>
                    {truncateMiddle(u.originalUrl, 48)}
                  </p>
                </div>
                  <div className="url-card__section">
                    <span className="url-card__label">Metadata</span>
                    <div className="url-card__meta">
                      <span>{formatDate(u.createdAt)}</span>
                      <span>Expires {formatDate(u.expiresAt)}</span>
                    </div>
                  </div>
                <div className="url-card__section url-card__row">
                  <span className="url-card__label">Status</span>
                  <UrlStatusBadge url={u} />
                </div>
                <div className="url-card__section url-card__actions">
                  <CopyButton value={u.shortUrl} label="Copy" />
                  <Link to={`/analytics/${u.shortCode}`} className="btn btn--ghost">
                    <BarChart2 size={15} />
                    Analytics
                  </Link>
                  <button onClick={() => setPendingDelete(u)} className="btn btn--danger btn--wide">
                    <Trash2 size={15} />
                    Delete
                  </button>
                </div>
              </div>
            ))}
          </div>
        </>
      )}

      <ConfirmDialog
        open={pendingDelete !== null}
        title="Delete short URL?"
        description={
          pendingDelete
            ? `This will permanently remove "${pendingDelete.shortUrl}" and its analytics. This action cannot be undone.`
            : ''
        }
        confirmLabel="Delete"
        loading={del.isPending}
        onCancel={() => setPendingDelete(null)}
        onConfirm={() => pendingDelete && del.mutate(pendingDelete.shortCode)}
      />
    </div>
  )
}

export default MyUrls
