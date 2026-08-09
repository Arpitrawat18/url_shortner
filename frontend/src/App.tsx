import React from 'react'
import { Routes, Route, Outlet } from 'react-router-dom'
import Navbar from './components/Navbar'
import RequireAuth from './components/RequireAuth'
import AppLayout from './components/layout/AppLayout'
import ScrollToTop from './components/ScrollToTop'
import Landing from './pages/Landing'
import Login from './pages/Login'
import Register from './pages/Register'
import Dashboard from './pages/Dashboard'
import MyUrls from './pages/MyUrls'
import AnalyticsOverview from './pages/AnalyticsOverview'
import AnalyticsPage from './pages/AnalyticsPage'
import Profile from './pages/Profile'
import NotFound from './pages/NotFound'

const PublicLayout: React.FC = () => (
  <>
    <Navbar />
    <Outlet />
  </>
)

const App: React.FC = () => {
  return (
    <div className="app">
      <ScrollToTop />
      <Routes>
        <Route element={<PublicLayout />}>
          <Route path="/" element={<Landing />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
        </Route>

        <Route element={<RequireAuth />}>
          <Route element={<AppLayout />}>
            <Route path="/dashboard" element={<Dashboard />} />
            <Route path="/myurls" element={<MyUrls />} />
            <Route path="/analytics" element={<AnalyticsOverview />} />
            <Route path="/analytics/:shortCode" element={<AnalyticsPage />} />
            <Route path="/profile" element={<Profile />} />
          </Route>
        </Route>

        <Route element={<PublicLayout />}>
          <Route path="*" element={<NotFound />} />
        </Route>
      </Routes>
    </div>
  )
}

export default App
