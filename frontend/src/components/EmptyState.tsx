import React from 'react'
import { Inbox } from 'lucide-react'

const EmptyState: React.FC<{ title?: string; subtitle?: string; action?: React.ReactNode }> = ({ title = 'Nothing here', subtitle, action }) => (
  <div className="empty">
    <span className="empty__icon">
      <Inbox size={19} />
    </span>
    <h3 className="empty__title">{title}</h3>
    {subtitle && <p className="empty__sub">{subtitle}</p>}
    {action && <div className="empty__action">{action}</div>}
  </div>
)

export default EmptyState
