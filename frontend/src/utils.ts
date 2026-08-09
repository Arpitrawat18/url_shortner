import axios from 'axios'
import { UrlResponse } from './types'

const MONTHS = ['JAN', 'FEB', 'MAR', 'APR', 'MAY', 'JUN', 'JUL', 'AUG', 'SEP', 'OCT', 'NOV', 'DEC']

const pad = (value: number) => String(value).padStart(2, '0')

export const formatDate = (value?: string | null) => {
  if (!value) return 'No expiry'

  const date = new Date(value)
  const day = pad(date.getDate())
  const month = MONTHS[date.getMonth()]
  const year = date.getFullYear()
  const hours = pad(date.getHours())
  const minutes = pad(date.getMinutes())

  return `${day} ${month} ${year} · ${hours}:${minutes}`
}

export const getUrlStatus = (url: UrlResponse) => {
  if (!url.expiresAt) return { label: 'Active', tone: 'green' as const }

  return new Date(url.expiresAt).getTime() < Date.now()
    ? { label: 'Expired', tone: 'red' as const }
    : { label: 'Active', tone: 'green' as const }
}

export const getUrlStats = (urls: UrlResponse[] = []) => {
  const active = urls.filter((url) => getUrlStatus(url).label === 'Active').length
  const expired = urls.length - active

  return { total: urls.length, active, expired }
}

export const isValidUrl = (value: string) => {
  try {
    const url = new URL(value)
    return url.protocol === 'http:' || url.protocol === 'https:'
  } catch {
    return false
  }
}

export const getApiErrorMessage = (error: unknown, fallback: string) => {
  if (axios.isAxiosError(error)) {
    const data = error.response?.data as { message?: string; error?: string } | string | undefined

    if (typeof data === 'string') return data
    if (data?.message) return data.message
    if (data?.error) return data.error
  }

  return fallback
}

export const truncateMiddle = (value: string, max = 54) => {
  if (value.length <= max) return value

  const side = Math.floor((max - 3) / 2)
  return `${value.slice(0, side)}...${value.slice(-side)}`
}

export const decodeJwt = (token: string): Record<string, unknown> | null => {
  try {
    const payload = token.split('.')[1]
    const normalized = payload.replace(/-/g, '+').replace(/_/g, '/')
    const json = atob(decodeURIComponent(escape(normalized)))
    return JSON.parse(json) as Record<string, unknown>
  } catch {
    return null
  }
}
