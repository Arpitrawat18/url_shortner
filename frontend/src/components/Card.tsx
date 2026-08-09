import React from 'react'

const Card: React.FC<React.PropsWithChildren<{ className?: string; style?: React.CSSProperties }>> = ({ children, className = '', style }) => (
  <div className={`panel ${className}`} style={style}>
    {children}
  </div>
)

export default Card
