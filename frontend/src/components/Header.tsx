import React from 'react'

const Header: React.FC<{ title?: string; subtitle?: string; actions?: React.ReactNode }> = ({ title, subtitle, actions }) => (
  <div className="page-head">
    <div>
      <h1 className="page-head__title">{title}</h1>
      {subtitle && <p className="page-head__sub">{subtitle}</p>}
    </div>
    {actions && <div className="page-head__actions">{actions}</div>}
  </div>
)

export default Header
