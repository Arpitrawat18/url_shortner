import React from 'react'
import { Link, NavLink, useNavigate } from 'react-router-dom'
import { useAuth } from '../hooks/useAuth'
import { useToast } from './Toast'
import { LogOut } from 'lucide-react'

const PublicNavbar: React.FC = () => {
  const { token, clearToken } = useAuth()
  const { showToast } = useToast()
  const navigate = useNavigate()

  const logout = () => {
    clearToken()
    showToast('Signed out successfully', 'success')
    navigate('/login')
  }

  return (
    <header className="topbar">
      <div className="container topbar__inner">
        <Link to="/" className="topbar__brand">
          <span className="topbar__mark" />
          <span className="topbar__name">URL Shortener</span>
        </Link>

        <span className="topbar__dwg">Spec. SH-01 · Rev 2</span>

        <div className="row">
          {token ? (
            <>
              <NavLink to="/dashboard" className={({ isActive }) => `nav-link${isActive ? ' active' : ''}`}>
                Dashboard
              </NavLink>
              <button onClick={logout} className="btn btn--ghost btn--sm">
                <LogOut size={15} />
                Logout
              </button>
            </>
          ) : (
            <>
              <NavLink to="/login" className={({ isActive }) => `nav-link${isActive ? ' active' : ''}`}>
                Log in
              </NavLink>
              <NavLink to="/register" className="btn btn--primary btn--sm">
                Sign up free
              </NavLink>
            </>
          )}
        </div>
      </div>
    </header>
  )
}

export default PublicNavbar
