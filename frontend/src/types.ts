export interface AuthResponse { token: string }

export interface LoginRequest { email: string; password: string }
export interface RegisterRequest { name: string; email: string; password: string }

export interface UrlResponse {
  id: number
  originalUrl: string
  shortCode: string
  shortUrl: string
  createdAt: string
  expiresAt?: string | null
}

export interface CreateUrlRequest { originalUrl: string; expiresAt?: number }

export interface AnalyticsResponse {
  originalUrl: string
  shortCode: string
  totalClicks: number
  createdAt: string
  expiresAt?: string | null
  uniqueVisitors: number
  topBrowser?: string
  topDevice?: string
  topOperatingSystem?: string
  topReferrer?: string
}
