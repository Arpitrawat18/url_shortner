import React from 'react'

type BadgeTone = 'green' | 'red' | 'blue' | 'slate' | 'amber'

const tones: Record<BadgeTone, string> = {
  green: 'tag--ok',
  red: 'tag--err',
  blue: 'tag--info',
  slate: 'tag--idle',
  amber: 'tag--warn'
}

const Badge: React.FC<React.PropsWithChildren<{ tone?: BadgeTone; className?: string }>> = ({ children, tone = 'slate', className = '' }) => (
  <span className={`tag ${tones[tone]} ${className}`}>
    {children}
  </span>
)

export default Badge
