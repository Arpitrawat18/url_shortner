import React, { createContext, useCallback, useContext, useMemo, useState } from 'react'
import { AlertCircle, CheckCircle2, X } from 'lucide-react'

type ToastTone = 'success' | 'error' | 'info'

type ToastItem = {
  id: number
  tone: ToastTone
  message: string
}

type ToastContextValue = {
  showToast: (message: string, tone?: ToastTone) => void
}

const ToastContext = createContext<ToastContextValue | null>(null)

export const ToastProvider: React.FC<React.PropsWithChildren> = ({ children }) => {
  const [toasts, setToasts] = useState<ToastItem[]>([])

  const dismiss = useCallback((id: number) => {
    setToasts((items) => items.filter((item) => item.id !== id))
  }, [])

  const showToast = useCallback((message: string, tone: ToastTone = 'info') => {
    const id = Date.now()
    setToasts((items) => [...items, { id, message, tone }])
    window.setTimeout(() => dismiss(id), 3200)
  }, [dismiss])

  const value = useMemo(() => ({ showToast }), [showToast])

  return (
    <ToastContext.Provider value={value}>
      {children}
      <div className="toast-stack">
        {toasts.map((toast) => (
          <div
            key={toast.id}
            className={`toast toast--${toast.tone === 'success' ? 'ok' : toast.tone}`}
          >
            <span className="toast__icon">
              {toast.tone === 'success' ? (
                <CheckCircle2 size={17} />
              ) : (
                <AlertCircle size={17} />
              )}
            </span>
            <p className="toast__msg">{toast.message}</p>
            <button className="toast__close" onClick={() => dismiss(toast.id)} aria-label="Dismiss notification">
              <X size={15} />
            </button>
          </div>
        ))}
      </div>
    </ToastContext.Provider>
  )
}

export const useToast = () => {
  const context = useContext(ToastContext)

  if (!context) {
    throw new Error('useToast must be used within a ToastProvider')
  }

  return context
}
