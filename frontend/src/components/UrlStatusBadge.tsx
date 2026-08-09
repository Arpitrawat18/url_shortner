import React from 'react'
import Badge from './Badge'
import { getUrlStatus } from '../utils'
import { UrlResponse } from '../types'

const UrlStatusBadge: React.FC<{ url: UrlResponse; className?: string }> = ({ url, className = '' }) => {
  const status = getUrlStatus(url)

  return (
    <Badge tone={status.tone} className={className}>
      {status.label}
    </Badge>
  )
}

export default UrlStatusBadge
