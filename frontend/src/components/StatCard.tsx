import React from 'react'
import type { LucideIcon } from 'lucide-react'

type StatCardProps = {
  label: string
  value: React.ReactNode
  icon: LucideIcon
  tone?: string
  hint?: string
}

const StatCard: React.FC<StatCardProps> = ({ label, value, icon: Icon, hint }) => (
  <div className="stat">
    <div className="stat__head">
      <span className="stat__label">{label}</span>
      <Icon size={16} className="stat__icon" />
    </div>
    <span className="stat__value">{value}</span>
    {hint && <span className="stat__hint">{hint}</span>}
  </div>
)

export default StatCard
