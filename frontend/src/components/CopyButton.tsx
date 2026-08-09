import React, { useCallback, useRef, useState } from 'react'
import { Check, Copy } from 'lucide-react'
import { useToast } from './Toast'

type CopyButtonProps = {
  value: string
  label?: string
  className?: string
}

const CopyButton: React.FC<CopyButtonProps> = ({ value, label = 'Copy', className = '' }) => {
  const { showToast } = useToast()
  const [copied, setCopied] = useState(false)
  const timer = useRef<number | undefined>(undefined)

  const handleCopy = useCallback(async () => {
    try {
      await navigator.clipboard.writeText(value)
      setCopied(true)
      showToast('Copied to clipboard', 'success')
      window.clearTimeout(timer.current)
      timer.current = window.setTimeout(() => setCopied(false), 1600)
    } catch {
      showToast('Could not copy to clipboard', 'error')
    }
  }, [value, showToast])

  return (
    <button
      type="button"
      onClick={handleCopy}
      className={`btn ${copied ? 'btn--ok' : ''} ${className}`}
    >
      {copied ? <Check size={15} /> : <Copy size={15} />}
      {copied ? 'Copied' : label}
    </button>
  )
}

export default CopyButton
