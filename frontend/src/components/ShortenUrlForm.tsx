import React, { useState } from 'react'
import { useMutation } from '@tanstack/react-query'
import { createShortUrl } from '../api/url'
import { useToast } from './Toast'
import { getApiErrorMessage, isValidUrl } from '../utils'
import { CreateUrlRequest, UrlResponse } from '../types'
import ShortUrlResult from './ShortUrlResult'
import { Link as LinkIcon, Sparkles, X } from 'lucide-react'

const EXPIRY_OPTIONS = [
  { value: '1', label: '1 day' },
  { value: '7', label: '7 days' },
  { value: '30', label: '30 days' },
  { value: '90', label: '90 days' },
  { value: '365', label: '1 year' }
]

type ShortenUrlFormProps = {
  hero?: boolean
  onSuccess?: (result: UrlResponse) => void
}

const ShortenUrlForm: React.FC<ShortenUrlFormProps> = ({ hero = false, onSuccess }) => {
  const { showToast } = useToast()
  const [originalUrl, setOriginalUrl] = useState('')
  const [expiresAt, setExpiresAt] = useState('7')
  const [result, setResult] = useState<UrlResponse | null>(null)
  const [error, setError] = useState<string | null>(null)

  const mutation = useMutation({
    mutationFn: (payload: CreateUrlRequest) => createShortUrl(payload),
    onSuccess: (data) => {
      setResult(data)
      setOriginalUrl('')
      setError(null)
      showToast('Short link created', 'success')
      onSuccess?.(data)
    },
    onError: (err) => {
      const message = getApiErrorMessage(err, 'Failed to create short link')
      setError(message)
      showToast(message, 'error')
    }
  })

  const submit = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    const value = originalUrl.trim()

    if (!value) {
      setError('Please paste a URL to shorten')
      return
    }
    if (!isValidUrl(value)) {
      setError('Please enter a valid http(s) URL')
      return
    }

    const payload: CreateUrlRequest = {
      originalUrl: value,
      ...(expiresAt ? { expiresAt: Number(expiresAt) } : {})
    }

    setError(null)
    mutation.mutate(payload)
  }

  return (
    <div className="stack">
      <form onSubmit={submit} noValidate>
        <div className={`compressor${hero ? ' compressor--hero' : ''}`}>
          <div className="compressor__input-wrap">
            <LinkIcon size={17} className="compressor__icon" />
            <input
              type="text"
              value={originalUrl}
              onChange={(e) => setOriginalUrl(e.target.value)}
              placeholder="Paste your long URL here"
              aria-label="URL to shorten"
              autoComplete="off"
              spellCheck={false}
              className="compressor__input"
            />
          </div>
          <select
            value={expiresAt}
            onChange={(e) => setExpiresAt(e.target.value)}
            aria-label="Expiration"
            className="field compressor__select"
          >
            {EXPIRY_OPTIONS.map((option) => (
              <option key={option.value} value={option.value}>
                Expires in {option.label}
              </option>
            ))}
          </select>
          <button type="submit" disabled={mutation.isPending} className="btn btn--primary compressor__btn">
            {mutation.isPending ? (
              <span className="row">
                <span className="spinner spinner--sm" />
                Shortening...
              </span>
            ) : (
              <span className="row">
                <Sparkles size={15} />
                Shorten
              </span>
            )}
          </button>
        </div>

        {error && (
          <p className="form-error" role="alert">
            {error}
          </p>
        )}
      </form>

      {result && (
        <div className="stack--sm">
          <ShortUrlResult result={result} />
          <button
            type="button"
            onClick={() => setResult(null)}
            className="btn btn--ghost btn--sm"
          >
            <X size={13} />
            Shorten another link
          </button>
        </div>
      )}
    </div>
  )
}

export default ShortenUrlForm
