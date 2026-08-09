import React from 'react'
import { AlertTriangle } from 'lucide-react'

type ConfirmDialogProps = {
  open: boolean
  title: string
  description: string
  confirmLabel?: string
  loading?: boolean
  onCancel: () => void
  onConfirm: () => void
}

const ConfirmDialog: React.FC<ConfirmDialogProps> = ({
  open,
  title,
  description,
  confirmLabel = 'Confirm',
  loading = false,
  onCancel,
  onConfirm
}) => {
  if (!open) return null

  return (
    <div className="dialog" role="dialog" aria-modal="true" aria-label={title}>
      <div className="dialog__backdrop" onClick={onCancel} />
      <div className="dialog__panel">
        <div className="dialog__head">
          <span className="dialog__icon">
            <AlertTriangle size={17} />
          </span>
          <div>
            <h2 className="dialog__title">{title}</h2>
            <p className="dialog__desc">{description}</p>
          </div>
        </div>
        <div className="dialog__actions">
          <button className="btn btn--ghost" onClick={onCancel} disabled={loading}>Cancel</button>
          <button className="btn btn--danger" onClick={onConfirm} disabled={loading}>
            {loading ? 'Deleting...' : confirmLabel}
          </button>
        </div>
      </div>
    </div>
  )
}

export default ConfirmDialog
