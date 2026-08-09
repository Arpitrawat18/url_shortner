import React from 'react'

export const SkeletonLine: React.FC<{ className?: string }> = ({ className = '' }) => (
  <div className={`skel skel--line ${className}`} />
)

export const SkeletonCard: React.FC = () => (
  <div className="skel skel--card stack--sm">
    <SkeletonLine className="skel--w-1-3" />
    <SkeletonLine className="skel--w-1-2" />
    <SkeletonLine className="skel--w-2-3" />
  </div>
)
