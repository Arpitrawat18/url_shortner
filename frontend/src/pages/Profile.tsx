import React, { useMemo } from 'react'
import { useNavigate } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { getMyUrls } from '../api/url'
import { useAuth } from '../hooks/useAuth'
import { useToast } from '../components/Toast'
import Card from '../components/Card'
import Header from '../components/Header'
import StatCard from '../components/StatCard'
import { SkeletonCard } from '../components/Skeleton'
import { decodeJwt, getUrlStats } from '../utils'
import { KeyRound, Link as LinkIcon, LogOut, Mail, Pencil, ShieldCheck, User as UserIcon } from 'lucide-react'

const Profile: React.FC = () => {
  const { token, clearToken } = useAuth()
  const { showToast } = useToast()
  const navigate = useNavigate()

  const claims = token ? decodeJwt(token) : null
  const name = typeof claims?.name === 'string' ? (claims.name as string) : null
  const email = (typeof claims?.email === 'string' ? claims.email : claims?.sub) as string | undefined

  const initials = useMemo(() => {
    if (name) {
      return name
        .split(/\s+/)
        .filter(Boolean)
        .slice(0, 2)
        .map((part) => part[0]!.toUpperCase())
        .join('')
    }
    return (email?.charAt(0) ?? 'U').toUpperCase()
  }, [name, email])

  const { data = [], isPending, isError } = useQuery({
    queryKey: ['myurls'],
    queryFn: () => getMyUrls()
  })

  const stats = getUrlStats(data)

  const logout = () => {
    clearToken()
    showToast('Signed out successfully', 'success')
    navigate('/login')
  }

  const unavailable = () => {
    showToast('This feature requires backend support that is not available yet', 'info')
  }

  return (
    <div className="page" style={{ maxWidth: 760, margin: '0 auto' }}>
      <Header title="Profile" subtitle="Manage your account and URL Shortener activity" />

      <div className="profile-head">
        <span className="profile-head__mark">{initials}</span>
        <div className="min-w-0">
          <h2 className="profile-head__name">{name ?? 'Member'}</h2>
          <p className="profile-head__meta">{email}</p>
          <p className="profile-head__meta">Member · URL Shortener</p>
        </div>
      </div>

      <div className="section-head">
        <h2 className="section-head__title">Account</h2>
      </div>
      <Card>
        <dl className="kv-list">
          <div>
            <dt>
              <UserIcon size={14} className="kv-icon" />
              Name
            </dt>
            <dd>{name ?? '—'}</dd>
          </div>
          <div>
            <dt>
              <Mail size={14} className="kv-icon" />
              Email
            </dt>
            <dd>{email ?? '—'}</dd>
          </div>
        </dl>
        <div className="row row--end" style={{ marginTop: 14, borderTop: '1px solid var(--border)', paddingTop: 14 }}>
          <button onClick={unavailable} className="btn btn--ghost btn--sm">
            <Pencil size={14} />
            Edit Profile
          </button>
        </div>
      </Card>

      <div className="section-head">
        <h2 className="section-head__title">URL Shortener</h2>
      </div>
      <div className="stat-grid stat-grid--3">
        {isPending ? (
          <>
            <SkeletonCard />
            <SkeletonCard />
            <SkeletonCard />
          </>
        ) : (
          <>
            <StatCard label="Total URLs" value={stats.total} icon={LinkIcon} hint="Links created" />
            <StatCard label="Active URLs" value={stats.active} icon={ShieldCheck} hint="Available for redirect" />
          </>
        )}
        {isError && (
          <Card className="stat-grid__span">
            <p className="form-error">Could not load your URL statistics.</p>
          </Card>
        )}
      </div>

      <div className="section-head">
        <h2 className="section-head__title">Security</h2>
      </div>
      <Card>
        <div className="row row--between row--wrap">
          <div className="row">
            <span className="url-card__icon">
              <KeyRound size={17} />
            </span>
            <div>
              <p style={{ fontFamily: 'var(--font-mono)', fontSize: 12.5 }}>Password</p>
              <p className="tbl__dim">Keep your account secure</p>
            </div>
          </div>
          <button onClick={unavailable} className="btn btn--ghost btn--sm">
            <ShieldCheck size={14} />
            Change Password
          </button>
        </div>
      </Card>

      <div className="row row--end" style={{ marginTop: 24 }}>
        <button onClick={logout} className="btn btn--danger">
          <LogOut size={15} />
          Logout
        </button>
      </div>
    </div>
  )
}

export default Profile
