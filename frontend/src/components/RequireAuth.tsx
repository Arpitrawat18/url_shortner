import React from 'react'
import { Navigate, Outlet, useLocation } from 'react-router-dom'
import { useAuth } from '../hooks/useAuth'

const RequireAuth: React.FC = () => {
  const { token } = useAuth()
  const location = useLocation()

  if (!token) {
    return <Navigate to="/login" replace state={{ from: location.pathname }} />
  }

  return <Outlet />
}

export default RequireAuth
