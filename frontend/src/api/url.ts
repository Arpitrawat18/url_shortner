import api from './axios'
import { CreateUrlRequest, UrlResponse, AnalyticsResponse } from '../types'

export const createShortUrl = async (payload: CreateUrlRequest): Promise<UrlResponse> => {
  const { data } = await api.post('/api/v1/url', payload)
  return data
}

export const getMyUrls = async (): Promise<UrlResponse[]> => {
  const { data } = await api.get('/api/v1/url/myurls')
  return data
}

export const deleteUrl = async (shortCode: string): Promise<void> => {
  await api.delete(`/api/v1/url/${shortCode}`)
}

export const getAnalytics = async (shortCode: string): Promise<AnalyticsResponse> => {
  const { data } = await api.get(`/api/v1/url/${shortCode}/analytics`)
  return data
}
