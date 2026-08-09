import React, { useState } from 'react'
import { Link, NavLink, Outlet, useNavigate } from 'react-router-dom'
import { BarChart3, LayoutDashboard, Link2, LogOut, Menu, Plus, User } from 'lucide-react'
import { useAuth } from '../../hooks/useAuth'
import { useToast } from '../Toast'

const NAV_ITEMS = [
  { to: '/dashboard', label: 'Dashboard', icon: LayoutDashboard },
  { to: '/myurls', label: 'My URLs', icon: Link2 },
  { to: '/analytics', label: 'Analytics', icon: BarChart3 },
  { to: '/profile', label: 'Profile', icon: User }
]

const AppLayout: React.FC = () => {
  const [menuOpen, setMenuOpen] = useState(false)
  const { clearToken } = useAuth()
  const { showToast } = useToast()
  const navigate = useNavigate()

  const logout = () => {
    clearToken()
    showToast('Signed out successfully', 'success')
    navigate('/login')
  }

  const closeMenu = () => setMenuOpen(false)

  return (
    <div className="app">
      <header className="topbar">
        <div className="container topbar__inner">
          <Link to="/dashboard" className="topbar__brand" onClick={closeMenu}>
            <span className="topbar__mark" />
            <span className="topbar__name">URL Shortener</span>
          </Link>

          <span className="topbar__dwg">Control Panel · Sec 02</span>

          <button
            className="menu-btn"
            onClick={() => setMenuOpen((value) => !value)}
            aria-label="Toggle navigation menu"
            aria-expanded={menuOpen}
          >
            <Menu size={18} />
          </button>

          <nav className={`topbar__nav${menuOpen ? ' open' : ''}`}>
            {NAV_ITEMS.map(({ to, label, icon: Icon }) => (
              <NavLink
                key={to}
                to={to}
                onClick={closeMenu}
                className={({ isActive }) => `nav-link${isActive ? ' active' : ''}`}
              >
                <Icon size={15} />
                {label}
              </NavLink>
            ))}
            <Link to="/dashboard" onClick={closeMenu} className="btn btn--primary btn--sm">
              <Plus size={14} />
              New link
            </Link>
            <button onClick={logout} className="nav-link">
              <LogOut size={15} />
              Logout
            </button>
          </nav>
        </div>
      </header>

      <main className="app-main">
        <Outlet />
      </main>
    </div>
  )
}

export default AppLayout
